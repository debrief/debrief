package ASSET.Util.XML.Decisions.Responses;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Util.XML.Decisions.Responses.ManoeuvreToCourseHandler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.Util.XML.Decisions.Responses.ManoeuvreToCourseHandler.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import ASSET.Models.Decision.Responses.ManoeuvreToCourse;
import ASSET.Models.Decision.Responses.Response;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class ManoeuvreToCourseHandlerTest {
	abstract class MyReaderWriter extends ASSET.Util.XML.ASSETReaderWriter {

		/**
		 * exporting the session
		 */
		public void exportThis(final ManoeuvreToCourse scenario, final java.io.OutputStream os) {
			try {
				// output the XML header stuff
				// output the plot
				final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				final org.w3c.dom.Element scen = doc.createElement("Test");
				ManoeuvreToCourseHandler.exportThis(scenario, scen, doc);
				doc.appendChild(scen);

				// ok, we should be done now

			} catch (final ParserConfigurationException e) {
				e.printStackTrace();
			}
		}

		/**
		 * handle the import of XML data into an existing session
		 */
		@Override
		public void importThis(final String fName, final java.io.InputStream is) {
			final MWC.Utilities.ReaderWriter.XML.MWCXMLReader handler = new ManoeuvreToCourseHandler() {
				@Override
				public void setResponse(final Response dec) {
					responseIs(dec);
				}
			};
			handler.reportNotHandledErrors(false);

			// import the datafile into this set of layers
			doImport(new org.xml.sax.InputSource(is), handler);
		}

		abstract public void responseIs(Response rec);
	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	ManoeuvreToCourse resp = null;


	@Disabled("Cannot read its input file; ASSET's tests were in no build before the JUnit 6 migration (org.mwc.asset.test was not in the reactor), so this never ran")
	@Test
	public void testTheTrail() {
		// ok, let's go for it
		final MyReaderWriter mr = new MyReaderWriter() {
			@Override
			public void responseIs(final Response rec) {
				resp = (ManoeuvreToCourse) rec;
			}
		};

		ManoeuvreToCourse ml = new ManoeuvreToCourse(new WorldSpeed(12, WorldSpeed.M_sec), false, new Float(12),
				true, new WorldDistance(14, WorldDistance.METRES));
		ml.setName("bingop");
		java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
		mr.exportThis(ml, os);

		// great, now try to read it in!
		java.io.InputStream is = new java.io.ByteArrayInputStream(os.toByteArray());
		mr.importThis("", is);

		// check we got it
		assertEquals(resp.getName(), "bingop", "it has the correct name");
		assertEquals(resp.getSpeed().getValueIn(WorldSpeed.M_sec), 12, 0.001, "it has the correct speed");
		assertEquals(resp.getCourse().floatValue(), 12f, 0.001, "correct course");

		// second try, with missing speed
		ml = new ManoeuvreToCourse(null, false, new Float(12), true, null);
		ml.setName("bingop");
		os = new java.io.ByteArrayOutputStream();
		mr.exportThis(ml, os);

		// great, now try to read it in!
		is = new java.io.ByteArrayInputStream(os.toByteArray());
		mr.importThis("", is);

		// check we got it
		assertEquals(resp.getName(), "bingop", "it has the correct name");
		assertEquals(resp.getSpeed(), null, "it has the correct (null) speed");
		assertEquals(resp.getCourse().floatValue(), 12f, 0.001, "correct course");

	}

}
