/*
 * MWCXMLReader.java
 *
 * Created on 04 October 2000, 11:34
 */

package MWC.Utilities.ReaderWriter.XML;

import java.io.CharArrayWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import MWC.GUI.Shapes.TextLabel;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * @author IAN MAYO
 */
public class MWCXMLReader extends DefaultHandler {

	private static final String FALSE = "false";
	private static final String TRUE = "true";
	private final Vector<MWCXMLReader> _myHandlers;
	private final String _myType;
	private XMLReader _theParser;
	private ContentHandler _theParent;

	private final Vector<HandleAttribute> _myAttributeHandlers;

	// Buffer for collecting data from
	// the "characters" SAX event.
	private final CharArrayWriter contents = new CharArrayWriter();

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
	static private final java.text.DecimalFormat shortFormat = new java.text.DecimalFormat(
			"0.000");
	static private final java.text.DecimalFormat longFormat = new java.text.DecimalFormat(
			"0.0000000");

	/**
	 * whether we report "Handler not found for.." errors
	 */
	private boolean _reportNotHandled = true;

	private static final String HANDLER_NOT_FOUND_MESSAGE = " handler not found";

	// ////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////
	public MWCXMLReader(final String myType) {
		_myType = myType;
		_myHandlers = new Vector<MWCXMLReader>(0, 1);
		_myAttributeHandlers = new Vector<HandleAttribute>(0, 1);
	}

	public final void reportNotHandledErrors(final boolean val) {
		this._reportNotHandled = val;
	}

	/**
	 * the actual data for this type of object
	 */
	protected void handleOurselves(final String name,
			final Attributes attributes) {

		// go through our list of handlers
		final Enumeration<HandleAttribute> enumer = _myAttributeHandlers
				.elements();
		while (enumer.hasMoreElements()) {
			final HandleAttribute ha = (HandleAttribute) enumer.nextElement();
			final String val = attributes.getValue(ha.myName);
			if (val != null) {
				// handle this next call, since it does occasionally fail
				try {
					// //
					ha.setValue(ha.myName, val);
					// //
				} catch (Exception e) {
					MWC.Utilities.Errors.Trace.trace(e,
							"Trouble handling attribute: " + ha.myName
									+ " for:" + _myType);
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
	 * remove this type of handler from our list
	 */
	public final void removeHandler(final MWCXMLReader handler) {
		_myHandlers.remove(handler);
	}

	/**
	 * see if we can handle this type of data
	 */
	public boolean canHandleThis(final String element) {
		return element.equals(_myType);
	}

	/**
	 * take over the parsing "stack"
	 */
	public final void handleThis(final XMLReader parser,
			final ContentHandler parent) {
		_theParent = parent;
		_theParser = parser;
		parser.setContentHandler(this);
	}

	/**
	 * process this stream of characters
	 */
	@Override
	public final void characters(final char[] ch, final int start,
			final int length) throws SAXException {
		// accumulate the contents into a buffer.
		contents.write(ch, start, length);

	}

	/**
	 * updated!
	 */

	@Override
	public void startElement(final String nameSpace, String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		boolean handled = false;

		localName = qName;
		// check we are handling a session
		if (canHandleThis(localName)) {
			// hooray it's one of ours!
			handleOurselves(localName, attributes);
			handled = true;
		} else
		// see if we have a handler for this object
		if (_myHandlers != null) {
			final Enumeration<MWCXMLReader> enumer = _myHandlers.elements();
			while (enumer.hasMoreElements()) {
				final MWCXMLReader hand = (MWCXMLReader) enumer.nextElement();
				if (hand.canHandleThis(localName)) {
					hand.startElement(nameSpace, localName, qName, attributes);

					// //////////////
					// wrap this, it's hard to diagnose errors which appear here
					// //////////////

					try {
						hand.handleThis(_theParser, this);
					} catch (Exception e) {
						MWC.Utilities.Errors.Trace.trace(e,
								"Trouble handling attribute:" + localName);
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
						"MWCXMLReader failed to find handler for:" + localName
								+ " when handling:" + _myType, false);
				throw new java.lang.RuntimeException("\"" + localName + "\""
						+ HANDLER_NOT_FOUND_MESSAGE);
			}
		}
	}

	/**
	 * we have reached the end of an element. See if it is our element - so we
	 * should drop out, else let's continue
	 */
	@Override
	public final void endElement(final java.lang.String namespaceURI,
			java.lang.String localName, final java.lang.String qName)
			throws SAXException {
		localName = qName;
		// check if it is us which have finished, if so, drop back to our parent
		if (localName.equals(this._myType)) {

			try {
				elementClosed();
			} catch (NullPointerException se) {
				// output a hopefully useful message
				final String msg = "Trouble parsing element: " + localName;
				MWC.Utilities.Errors.Trace.trace(se, msg);

				// and continue back up the stack
				throw se;
			}

			// element has finished, drop back
			_theParser.setContentHandler(_theParent);
		}
	}

	public void elementClosed() {
		// don't bother
	}

	// ////////////////////////////////////////////////////////
	// date formatter access
	// ///////////////////////////////////////////////////////
	public static DateFormat getRNDateFormatter() {
		if (RNdateFormat == null) {
			RNdateFormat = new SimpleDateFormat("yyMMdd HHmmss.SSS");
			RNdateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		return RNdateFormat;
	}

	static DateFormat getXMLDateFormatter() {
		if (_XMLDateFormat == null) {
			_XMLDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			_XMLDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}

		return _XMLDateFormat;
	}

	// ////////////////////////////////////////////////////////
	// handle the different types of attribute
	// ///////////////////////////////////////////////////////
	abstract static public class HandleAttribute {
		public final String myName;

		public HandleAttribute(final String name) {
			myName = name;
		}

		abstract public void setValue(String name, String value);
	}

	abstract static public class HandleDoubleAttribute extends HandleAttribute {
		public HandleDoubleAttribute(final String name) {
			super(name);
		}

		@Override
		public final void setValue(final String name, final String value) {
			try {
				final double val = Double.parseDouble(value);// longFormat.parse(value).doubleValue();
				setValue(name, val);
			} catch (java.lang.NumberFormatException pe) {
				MWC.Utilities.Errors.Trace.trace(pe,
						"Reader: Whilst reading in " + name + " value of :"
								+ value);
			}
		}

		abstract public void setValue(String name, double value);

	}

	abstract static public class HandleLongAttribute extends HandleAttribute {
		public HandleLongAttribute(final String name) {
			super(name);
		}

		@Override
		public final void setValue(final String name, final String value) {
			final long val = Long.parseLong(value);
			setValue(name, val);
		}

		abstract public void setValue(String name, long value);
	}

	abstract static public class HandleIntegerAttribute extends HandleAttribute {
		public HandleIntegerAttribute(final String name) {
			super(name);
		}

		@Override
		public final void setValue(final String name, final String value) {
			final int val = Integer.parseInt(value);
			setValue(name, val);
		}

		abstract public void setValue(String name, int value);
	}

	abstract static public class HandleBooleanAttribute extends HandleAttribute {
		public HandleBooleanAttribute(final String name) {
			super(name);
		}

		@Override
		public final void setValue(final String name, final String value) {
			final boolean val = Boolean.valueOf(value).booleanValue();
			setValue(name, val);
		}

		abstract public void setValue(String name, boolean value);
	}

	abstract static public class HandleDateTimeAttribute extends
			HandleAttribute {
		public HandleDateTimeAttribute(final String name) {
			super(name);
		}

		@Override
		public final void setValue(final String name, final String value) {
			try {
				final long time = getXMLDateFormatter().parse(value).getTime();
				setValue(name, time);
			} catch (ParseException e) {
				MWC.Utilities.Errors.Trace.trace(e,
						"Failed to parse Date value of:" + value + " for:"
								+ name);
			}
		}

		abstract public void setValue(String name, long time);
	}

	// ////////////////////////////////////////////////////////////////
	// number formatting used in XML export
	// ////////////////////////////////////////////////////////////////

	static public String writeThis(final boolean val) {
		if (val)
			return TRUE;
		else
			return FALSE;
	}

	static public String writeThis(final Boolean val) {
		return writeThis(val.booleanValue());
	}

	static public String writeThis(final int val) {
		return Integer.toString(val);
	}

	static public String writeThis(final long val) {
		return Long.toString(val);
	}

	static public String writeThis(final double val) {
		final double outVal;
		
		// check for NaN
		if(Double.isNaN(val))
			outVal = 0;
		else
			outVal = val;
		return shortFormat.format(outVal);
	}

	static public String writeThis(final Date val) {
		return getRNDateFormatter().format(val);
	}

	static public String writeThis(final HiResDate val) {
		String res = DebriefFormatDateTime.toStringHiRes(val);
		return res;
	}

	static public String writeThis(final Duration val) {
		String res = val.toString();
		return res;
	}

	static public String writeThisInXML(final Date val) {
		return getXMLDateFormatter().format(val);
	}

	static public String writeThisLong(final double val) {
		return longFormat.format(val);
	}

	static public double readThisDouble(final String val)
			throws java.text.ParseException {
		// 
		double res;

		// SPECIAL CASE: An external system is producing Debrief datafiles. It
		// puts NaN in for course, and it's making us trip over.
		if (val.toUpperCase().equals("NAN"))
			res = 0;
		else
			res = shortFormat.parse(val).doubleValue();

		return res;
	}

	static public HiResDate parseThisDate(final String val) {
		return DebriefFormatDateTime.parseThis(val);
	}

	static public Duration parseThisDuration(final String val) {
		return Duration.fromString(val);
	}

	/**
	 * replace newline characters with the long equivalent
	 * 
	 * @param val
	 *            the text as a normal Java string
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

	public static String fromXML(final String val) {
		String res = new String();

		if (val != null) {
			int start = 0;
			int newlineAt;

			final String XML_MARKER = "\\n";
			final int XML_LEN = XML_MARKER.length();

			while ((newlineAt = val.indexOf(XML_MARKER, start)) > 0) {
				res += val.substring(start, newlineAt)
						+ TextLabel.NEWLINE_MARKER;
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

}
