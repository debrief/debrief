package Debrief.ReaderWriter.XML.Shapes;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.XML.Shapes.LabelHandler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.ReaderWriter.XML.Shapes.LabelHandler.*;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.TimeRangeHandler;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class LabelHandlerTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testConversion() {
		final String start = "here\\nand there\\nalpha";

		String other = MWCXMLReader.fromXML(start);
		assertEquals("here\nand there\nalpha", other, "strings match on way to XML");

		final String andBack = MWCXMLReader.toXML(other);

		assertEquals(start, andBack, "strings match on way back to text");

		if (!andBack.equals(start)) {
			System.out.println("second one failed");
		}

		// check they handle null strings
		other = MWCXMLReader.toXML(null);
		assertEquals("", other, "handled null string going to XML");

		other = MWCXMLReader.fromXML(null);
		assertEquals("", other, "handled null string going from XML");

		// check they handle short strings
		other = MWCXMLReader.toXML("");
		assertEquals("", other, "handled empty string going to XML");

		other = MWCXMLReader.fromXML("");
		assertEquals("", other, "handled empty string going from XML");
	}
}
