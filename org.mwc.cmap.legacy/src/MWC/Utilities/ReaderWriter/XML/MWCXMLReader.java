/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.Utilities.ReaderWriter.XML;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Shapes.TextLabel;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.ImportProblems;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

/**
 * @author IAN MAYO
 */
public class MWCXMLReader extends DefaultHandler {

	// ////////////////////////////////////////////////////////
	// handle the different types of attribute
	// ///////////////////////////////////////////////////////
	abstract static public class HandleAttribute {
		public final String myName;

		public HandleAttribute(final String name) {
			myName = name;
		}

		abstract public void setValue(final String name, final String value);
	}

	abstract static public class HandleBooleanAttribute extends HandleAttribute {
		public HandleBooleanAttribute(final String name) {
			super(name);
		}

		abstract public void setValue(String name, boolean value);

		@Override
		public final void setValue(final String name, final String value) {
			final boolean val = Boolean.valueOf(value).booleanValue();
			setValue(name, val);
		}
	}

	abstract static public class HandleDateTimeAttribute extends HandleAttribute {
		public HandleDateTimeAttribute(final String name) {
			super(name);
		}

		abstract public void setValue(String name, long time);

		@Override
		public final void setValue(final String name, final String value) {
			try {
				final long time = getXMLDateFormatter().parse(value).getTime();
				setValue(name, time);
			} catch (final ParseException e) {
				reportProblem("Couldn't read date for " + name + ": \"" + value + "\"", e);
			}
		}
	}

	abstract static public class HandleDoubleAttribute extends HandleAttribute {
		public HandleDoubleAttribute(final String name) {
			super(name);
		}

		abstract public void setValue(String name, double value);

		@Override
		public final void setValue(final String name, final String value) {
			try {
				final double val = readThisDouble(value);
				setValue(name, val);
			} catch (final ParseException pe) {
				reportProblem("Couldn't read number for " + name + ": \"" + value + "\"", pe);
			}
		}

	}

	abstract static public class HandleIntegerAttribute extends HandleAttribute {
		public HandleIntegerAttribute(final String name) {
			super(name);
		}

		abstract public void setValue(String name, int value);

		@Override
		public final void setValue(final String name, final String value) {
			final int val = Integer.parseInt(value);
			setValue(name, val);
		}
	}

	abstract static public class HandleLongAttribute extends HandleAttribute {
		public HandleLongAttribute(final String name) {
			super(name);
		}

		abstract public void setValue(String name, long value);

		@Override
		public final void setValue(final String name, final String value) {
			final long val = Long.parseLong(value);
			setValue(name, val);
		}
	}

	public static class ReadDoubleTest extends TestCase {
		private static void assertRejected(final String val) {
			try {
				final double res = readThisDouble(val);
				fail("should have rejected '" + val + "', got " + res);
			} catch (final ParseException pe) {
				// expected
			}
		}

		public void testAttributeProblemsReported() throws Exception {
			DialogFactory.setRunHeadless(true);
			final double[] course = { -1 };
			final double[] speed = { -1 };
			final MWCXMLReader handler = new MWCXMLReader("fix") {
				{
					addAttributeHandler(new HandleDoubleAttribute("Course") {
						@Override
						public void setValue(final String name, final double value) {
							course[0] = value;
						}
					});
					addAttributeHandler(new HandleDoubleAttribute("Speed") {
						@Override
						public void setValue(final String name, final double value) {
							speed[0] = value;
						}
					});
				}
			};
			final String doc = "<?xml version=\"1.0\"?>\n<fix\n Course=\"9.0E1\" Speed=\"12kts\"/>";
			final MWCXMLReaderWriter rw = new MWCXMLReaderWriter();
			rw.importThis("test.dpf", new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8)), handler);
			assertEquals("exponent notation read in full", 90d, course[0], 0.0001);
			assertEquals("bad value not applied", -1d, speed[0], 0.0001);
			final ImportProblems problems = rw.getImportProblems();
			assertEquals(1, problems.size());
			final String problem = problems.getProblems().get(0);
			assertTrue(problem, problem.contains("Speed") && problem.contains("12kts") && problem.startsWith("Line 3"));
		}

		public void testReadThisDouble() throws ParseException {
			assertEquals(12.5, readThisDouble("12.5"), 0);
			assertEquals(12.5, readThisDouble(" 12.5 "), 0);
			assertEquals(-0.5, readThisDouble("-0.5"), 0);
			assertEquals(12, readThisDouble("12"), 0);
			assertEquals(0.5, readThisDouble(".5"), 0);
			// exponent notation
			assertEquals(12, readThisDouble("1.2E1"), 0);
			assertEquals(90, readThisDouble("9.0E1"), 0);
			assertEquals(100000, readThisDouble("1e5"), 0);
			assertEquals(0.00012, readThisDouble("1.2e-4"), 1e-12);
			// comma decimal separator
			assertEquals(22.5, readThisDouble("22,5"), 0);
			assertEquals(239.9, readThisDouble("239,9"), 1e-9);
			assertEquals(53.54, readThisDouble("53,54"), 1e-9);
			// trailing garbage / partial numbers
			assertRejected("12abc");
			assertRejected("12.5abc");
			assertRejected("22 10");
			assertRejected("1.2.3");
			assertRejected("");
			assertRejected("   ");
			assertRejected("x");
			// not-a-number is not a valid measurement
			assertRejected("NaN");
			assertRejected("nan");
			assertRejected("Infinity");
		}
	}

	private static final String FALSE = "false";

	private static final String TRUE = "true";

	/**
	 * RN date formatter to be used by child classes
	 */
	static private java.text.DateFormat RNdateFormat = null;

	/**
	 * XML date formatter to be used by child classes
	 */
	static private DateFormat _XMLDateFormat = null;

	/**
	 * number formatter used by our "writeThis" methods
	 */
	static private final java.text.DecimalFormat shortFormat = new java.text.DecimalFormat("0.000",
			new DecimalFormatSymbols(Locale.UK));
	static private final java.text.DecimalFormat longFormat = new java.text.DecimalFormat("0.0000000",
			new DecimalFormatSymbols(Locale.UK));

	/**
	 * a complete number, as accepted by readThisDouble: optional sign, digits with
	 * an optional '.' or ',' decimal separator, optional exponent
	 */
	static private final Pattern NUMBER_PATTERN = Pattern.compile("[+-]?(\\d+([.,]\\d*)?|[.,]\\d+)([eE][+-]?\\d+)?");

	private static final String HANDLER_NOT_FOUND_MESSAGE = " handler not found.\n\nMaybe it's not a Debrief file.";

	/**
	 * problems found during the import running on this thread (null if we're not
	 * collecting them)
	 */
	private static final ThreadLocal<ImportProblems> _currentProblems = new ThreadLocal<ImportProblems>();

	/**
	 * where the parser has got to in the file, for problem reports
	 */
	private static final ThreadLocal<Locator> _currentLocator = new ThreadLocal<Locator>();

	public static String fromXML(final String val) {
		String res = new String();

		if (val != null) {
			int start = 0;
			int newlineAt;

			final String XML_MARKER = "\\n";
			final int XML_LEN = XML_MARKER.length();

			while ((newlineAt = val.indexOf(XML_MARKER, start)) > 0) {
				res += val.substring(start, newlineAt) + TextLabel.NEWLINE_MARKER;
				start = newlineAt + XML_LEN;
			}

			// did we find any?
			// if we did, we have to append the last line
			if (res.length() > 0) {
				// yes, we've found some - append the last line
				res += val.substring(start);
			} else {
				// no - we've not found anything, just take a copy of the line
				res = val;
			}
		}

		return res;
	}

	// ////////////////////////////////////////////////////////
	// date formatter access
	// ///////////////////////////////////////////////////////
	public synchronized static DateFormat getRNDateFormatter() {
		if (RNdateFormat == null) {
			RNdateFormat = DebriefFormatDateTime.applyTwoDigitYearWindow(new GMTDateFormat("yyMMdd HHmmss.SSS"));
		}
		return RNdateFormat;
	}

	synchronized static DateFormat getXMLDateFormatter() {
		if (_XMLDateFormat == null) {
			_XMLDateFormat = new GMTDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}

		return _XMLDateFormat;
	}

	/**
	 * record a problem with the data being imported (e.g. a malformed attribute
	 * that has been skipped). During an import by {@link MWCXMLReaderWriter} it is
	 * added (with the line number) to the summary shown to the analyst at the end
	 * of the import; otherwise it just goes to the trace log.
	 *
	 * @param reason what was wrong
	 * @param e      the exception, if there was one
	 */
	public static void reportProblem(final String reason, final Exception e) {
		final ImportProblems problems = _currentProblems.get();
		if (problems != null) {
			final Locator locator = _currentLocator.get();
			problems.add(locator == null ? -1 : locator.getLineNumber(), reason, null);
		} else if (e != null) {
			MWC.Utilities.Errors.Trace.trace(e, reason);
		} else {
			MWC.Utilities.Errors.Trace.trace(reason, false);
		}
	}

	/**
	 * start collecting problems found by the handlers on this thread
	 */
	static void startCollectingProblems(final ImportProblems problems) {
		_currentProblems.set(problems);
	}

	/**
	 * stop collecting problems on this thread
	 */
	static void stopCollectingProblems() {
		_currentProblems.remove();
		_currentLocator.remove();
	}

	static public HiResDate parseThisDate(final String val) throws ParseException {
		return DebriefFormatDateTime.parseThis(val);
	}

	static public Duration parseThisDuration(final String val) throws ParseException {
		return Duration.fromString(val);
	}

	/**
	 * parse a number from a data file. The whole (trimmed) value must be a number:
	 * either '.' or ',' may be used as the decimal separator (no thousands
	 * separators), and exponent notation (1.2E1, 1e5) is accepted. Anything else,
	 * including trailing text, NaN and infinity, is rejected rather than silently
	 * truncated.
	 *
	 * @param value the text to parse
	 * @return the number
	 * @throws ParseException if the value isn't a valid number
	 */
	static public double readThisDouble(final String value) throws ParseException {
		if (value == null) {
			throw new ParseException("Missing number", 0);
		}

		// do some trimming, just in case
		final String trimmed = value.trim();

		if (!NUMBER_PATTERN.matcher(trimmed).matches()) {
			throw new ParseException("Unparseable number: \"" + value + "\"", 0);
		}

		// only one decimal separator can be present, so we can normalise it
		return Double.parseDouble(trimmed.replace(',', '.'));
	}

	/**
	 * replace newline characters with the long equivalent
	 *
	 * @param val the text as a normal Java string
	 * @return the text in XML form
	 */
	public static String toXML(final String val) {
		String res = new String();

		if (val != null) {
			int start = 0;
			int newlineAt;

			final String XML_MARKER = "\\n";

			while ((newlineAt = val.indexOf(TextLabel.NEWLINE_MARKER, start)) > 0) {
				res += val.substring(start, newlineAt) + XML_MARKER;
				start = newlineAt + TextLabel.NEWLINE_MARKER.length();
			}

			// did we find any?
			// if we did, we have to append the last line
			if (res.length() > 0) {
				// yes, we've found some - append the last line
				res += val.substring(start);
			} else {
				// no - we've not found anything, just take a copy of the line
				res = val;
			}
		}

		return res;

	}

	static public String writeThis(final boolean val) {
		if (val)
			return TRUE;
		else
			return FALSE;
	}

	static public String writeThis(final Boolean val) {
		return writeThis(val.booleanValue());
	}

	static public String writeThis(final Date val) {
		return getRNDateFormatter().format(val);
	}

	static public String writeThis(final double val) {
		final double outVal;

		// check for NaN
		if (Double.isNaN(val))
			outVal = 0;
		else
			outVal = val;
		return shortFormat.format(outVal);
	}

	static public String writeThis(final Duration val) {
		final String res = val.toString();
		return res;
	}

	static public String writeThis(final HiResDate val) {
		final String res = DebriefFormatDateTime.toStringHiResXML(val);
		return res;
	}

	static public String writeThis(final int val) {
		return Integer.toString(val);
	}

	static public String writeThis(final long val) {
		return Long.toString(val);
	}

	static public String writeThisInXML(final Date val) {
		return getXMLDateFormatter().format(val);
	}

	static public String writeThisLong(final double val) {
		return longFormat.format(val);
	}

	private final Vector<MWCXMLReader> _myHandlers;

	private final String _myType;

	private XMLReader _theParser;

	private ContentHandler _theParent;

	private final Vector<HandleAttribute> _myAttributeHandlers;

	// ////////////////////////////////////////////////////////////////
	// number formatting used in XML export
	// ////////////////////////////////////////////////////////////////

	// Buffer for collecting data from
	// the "characters" SAX event.
	private final CharArrayWriter contents = new CharArrayWriter();

	/**
	 * whether we report "Handler not found for.." errors
	 */
	private boolean _reportNotHandled = true;

	// ////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////
	public MWCXMLReader(final String myType) {
		_myType = myType;
		_myHandlers = new Vector<MWCXMLReader>(0, 1);
		_myAttributeHandlers = new Vector<HandleAttribute>(0, 1);
	}

	public final void addAttributeHandler(final HandleAttribute val) {
		_myAttributeHandlers.addElement(val);
	}

	/**
	 * remember that we also have this type of handler
	 */
	public final void addHandler(final MWCXMLReader handler) {
		_myHandlers.addElement(handler);
	}

	/**
	 * see if we can handle this type of data
	 */
	public boolean canHandleThis(final String element) {
		return element.equals(_myType);
	}

	/**
	 * process this stream of characters
	 */
	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		// accumulate the contents into a buffer.
		contents.write(ch, start, length);

	}

	public void elementClosed() {
		// don't bother
	}

	/**
	 * we have reached the end of an element. See if it is our element - so we
	 * should drop out, else let's continue
	 */
	@Override
	public final void endElement(final java.lang.String namespaceURI, final java.lang.String localName,
			final java.lang.String qName) throws SAXException {
		final java.lang.String theLocalName = qName;
		// check if it is us which have finished, if so, drop back to our parent
		if (theLocalName.equals(this._myType)) {

			try {
				elementClosed();
			} catch (final NullPointerException se) {
				// output a hopefully useful message
				final String msg = "Trouble parsing element: " + theLocalName;
				MWC.Utilities.Errors.Trace.trace(se, msg);

				// and continue back up the stack
				throw se;
			}

			// element has finished, drop back
			_theParser.setContentHandler(_theParent);
		}
	}

	public Vector<MWCXMLReader> getHandlers() {
		return _myHandlers;
	}

	/**
	 * the actual data for this type of object
	 */
	protected void handleOurselves(final String name, final Attributes attributes) {

		// go through our list of handlers
		final Enumeration<HandleAttribute> enumer = _myAttributeHandlers.elements();
		while (enumer.hasMoreElements()) {
			final HandleAttribute ha = enumer.nextElement();
			final String val = attributes.getValue(ha.myName);
			if (val != null) {
				// handle this next call, since it does occasionally fail
				try {
					// //
					ha.setValue(ha.myName, val);
					// //
				} catch (final Exception e) {
					reportProblem("Couldn't read " + ha.myName + " of " + _myType + ": \"" + val + "\"", e);
				}
			} else {
				// let's not bother about parameters not being found, they're
				// mostly
				// optional anyway
				// NO, KEEP IT IN! to give us some hints if somebody's
				// application isn't
				// working too well.
				// MWC.Utilities.Errors.Trace.trace("parameter not found for:" +
				// ha.myName + " in element " + name);
			}
		}
	}

	/**
	 * take over the parsing "stack"
	 */
	public final void handleThis(final XMLReader parser, final ContentHandler parent) {
		_theParent = parent;
		_theParser = parser;
		parser.setContentHandler(this);
	}

	/**
	 * remove this type of handler from our list
	 */
	public final void removeHandler(final MWCXMLReader handler) {
		_myHandlers.remove(handler);
	}

	/**
	 * never resolve external entities or DTDs referenced from a data file (XXE).
	 * The parser from {@link SafeXMLFactory} already rejects DOCTYPEs, this is a
	 * second line of defence since the SAX handler is also the entity resolver.
	 */
	@Override
	public InputSource resolveEntity(final String publicId, final String systemId)
			throws IOException, SAXException {
		return SafeXMLFactory.REJECT_EXTERNAL_ENTITIES.resolveEntity(publicId, systemId);
	}

	/**
	 * remember the locator, so problems can be reported with a line number
	 */
	@Override
	public void setDocumentLocator(final Locator locator) {
		_currentLocator.set(locator);
	}

	public final void reportNotHandledErrors(final boolean val) {
		this._reportNotHandled = val;
	}

	/**
	 * updated!
	 */

	@Override
	public void startElement(final String nameSpace, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		boolean handled = false;
		final String theLocalName = qName;
		// check we are handling a session
		if (canHandleThis(theLocalName)) {
			// hooray it's one of ours!
			handleOurselves(theLocalName, attributes);
			handled = true;
		} else
		// see if we have a handler for this object
		if (_myHandlers != null) {
			final Enumeration<MWCXMLReader> enumer = _myHandlers.elements();
			while (enumer.hasMoreElements()) {
				final MWCXMLReader hand = enumer.nextElement();
				if (hand.canHandleThis(theLocalName)) {
					hand.startElement(nameSpace, theLocalName, qName, attributes);

					// //////////////
					// wrap this, it's hard to diagnose errors which appear here
					// //////////////

					try {
						hand.handleThis(_theParser, this);
					} catch (final Exception e) {
						MWC.Utilities.Errors.Trace.trace(e, "Trouble handling attribute:" + theLocalName);
					}

					// //////////////
					// ok, continue
					// //////////////
					handled = true;
					break;
				}
			}
		}

		if (!handled) {
			// are we reporting not-handled errors?
			if (_reportNotHandled) {
				MWC.Utilities.Errors.Trace.trace(
						"MWCXMLReader failed to find handler for:" + theLocalName + " when handling:" + _myType, false);
				throw new java.lang.RuntimeException("\"" + theLocalName + "\"" + HANDLER_NOT_FOUND_MESSAGE);
			}
		}
	}

}
