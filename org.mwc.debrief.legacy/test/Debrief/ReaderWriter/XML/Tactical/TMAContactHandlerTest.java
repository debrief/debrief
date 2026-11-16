package Debrief.ReaderWriter.XML.Tactical;

import static Debrief.ReaderWriter.XML.Tactical.TMAContactHandler.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.XML.Tactical.TMAContactHandler.*;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Enumeration;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class TMAContactHandlerTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	// TODO FIX-TEST
	public final void NtestRead() {
		final DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(null);
		final Layers res = new Layers();
		final String fName = "../org.mwc.debrief.legacy/src/test_tma_read_write.xml";

		final java.io.File fileTest = new File(fName);
		assertTrue(fileTest.exists(), "Test file not found:" + fName);

		try {
			final java.io.FileInputStream fis = new java.io.FileInputStream(fName);
			reader.importThis(fName, fis, res);

			// right, now check it contains our data
			final Layer layer = res.findLayer("TOMATO");
			assertNotNull("found tomato track");

			final TrackWrapper tw = (TrackWrapper) layer;
			final Enumeration<Editable> solutions = tw.getSolutions().elements();
			assertNotNull(solutions, "found solutions");

			// find our solution track
			while (solutions.hasMoreElements()) {
				final TMAWrapper wrapper = (TMAWrapper) solutions.nextElement();
				assertEquals("TRACK_060", wrapper.getName(), "found our solution");

				final Enumeration<Editable> contacts = wrapper.elements();
				while (contacts.hasMoreElements()) {
					final TMAContactWrapper contactWrapper = (TMAContactWrapper) contacts.nextElement();
					assertEquals("Trial label", contactWrapper.getLabel(), "found first contact");
					assertEquals("Submarine", contactWrapper.getSymbol(), "correct symbol set");
					assertEquals(true, contactWrapper.getVisible(), "correct vis set");
					assertEquals(true, contactWrapper.getLabelVisible(), "correct label vis");
					assertEquals(new Color(230, 200, 20), contactWrapper.getColor(), "correct colour set");
					assertEquals(true, contactWrapper.getEllipseVisible(), "correct ellipse vis");
					assertEquals(true, contactWrapper.getSymbolVisible(), "correct symbol vis");
					assertEquals(true, contactWrapper.getLineVisible(), "correct line vis");
					assertEquals(new Integer(MWC.GUI.Properties.LocationPropertyEditor.RIGHT), contactWrapper.getLabelLocation(),
							"correct label loc");
					assertEquals(50, contactWrapper.getTargetCourse(), 0d, "correct line course");
					assertEquals(12.4, contactWrapper.getSpeed(), 0d, "correct line speed");
					assertEquals(100, contactWrapper.getDepth(), 0d, "correct line depth");
					final EllipseShape es = contactWrapper.buildGetEllipse();
					assertEquals(45, es.getOrientation(), 0d, "correct orientation");
					assertEquals(4000, es.getMaxima().getValueIn(WorldDistance.YARDS), 0.00001d, "correct maxima");
					assertEquals(2000, es.getMinima().getValueIn(WorldDistance.YARDS), 0.0001d, "correct minima");
				}

			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		}
	}

	@Test
	public void testDummy() {

	}
}
