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

import static MWC.Utilities.ReaderWriter.XML.MWCXMLReader.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader.*;
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
import org.junit.jupiter.api.Test;
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

public class MWCXMLReaderTest {
	private static void assertRejected(final String val) {
		try {
			final double res = readThisDouble(val);
			fail("should have rejected '" + val + "', got " + res);
		} catch (final ParseException pe) {
			// expected
		}
	}

	@Test
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
		assertEquals(90d, course[0], 0.0001, "exponent notation read in full");
		assertEquals(-1d, speed[0], 0.0001, "bad value not applied");
		final ImportProblems problems = rw.getImportProblems();
		assertEquals(1, problems.size());
		final String problem = problems.getProblems().get(0);
		assertTrue(problem.contains("Speed") && problem.contains("12kts") && problem.startsWith("Line 3"), problem);
	}

	@Test
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
		// could be a thousands separator or a decimal comma, so don't guess
		assertRejected("1,234");
		assertRejected("-12,500");
		assertRejected("1,234,567");
		assertEquals(1.2345, readThisDouble("1,2345"), 1e-9);
		assertEquals(1234.5, readThisDouble("1234,5"), 1e-9);
	}
}
