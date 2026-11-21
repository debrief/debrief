package ASSET.Util.XML.Decisions.Responses;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Util.XML.Decisions.Responses.ManoeuvreToLocationHandler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.Util.XML.Decisions.Responses.ManoeuvreToLocationHandler.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import ASSET.Models.Decision.Responses.ManoeuvreToLocation;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Util.XML.Utils.ASSETLocationHandler;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class ManoeuvreToLocationHandlerTest {
	abstract protected class MyReaderWriter extends ASSET.Util.XML.ASSETReaderWriter {

		/**
		 * exporting the session
		 */
		public void exportThis(final ManoeuvreToLocation scenario, final java.io.OutputStream os) {
			try {
				final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				final org.w3c.dom.Element scen = doc.createElement("Test");
				ManoeuvreToLocationHandler.exportThis(scenario, scen, doc);
				doc.appendChild(scen);

				// write out the element the handler produced (this never wrote
				// anything, so the import read an empty stream)
				javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(
						new javax.xml.transform.dom.DOMSource(scen.getFirstChild()),
						new javax.xml.transform.stream.StreamResult(os));
			} catch (final ParserConfigurationException | javax.xml.transform.TransformerException e) {
				throw new IllegalStateException(e);
			}
		}

		/**
		 * handle the import of XML data into an existing session
		 */
		@Override
		public void importThis(final String fName, final java.io.InputStream is) {
			final MWC.Utilities.ReaderWriter.XML.MWCXMLReader handler = new ManoeuvreToLocationHandler() {
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

	ManoeuvreToLocation resp = null;


	@Test
	public void testTheTrail() {
		// ok, let's go for it
		final MyReaderWriter mr = new MyReaderWriter() {
			@Override
			public void responseIs(final Response rec) {
				resp = (ManoeuvreToLocation) rec;
			}
		};

		ManoeuvreToLocation ml = new ManoeuvreToLocation(new WorldLocation(1, 1, 1),
				new WorldSpeed(12, WorldSpeed.M_sec));
		ml.setName("bingop");
		java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
		mr.exportThis(ml, os);

		// great, now try to read it in!
		java.io.InputStream is = new java.io.ByteArrayInputStream(os.toByteArray());
		mr.importThis("", is);

		// check we got it
		assertEquals(resp.getName(), "bingop", "it has the correct name");
		assertEquals(resp.getSpeed().getValueIn(WorldSpeed.M_sec), 12, 0.001, "it has the correct speed");
		assertEquals(resp.getLocation(), new WorldLocation(1, 1, 1), "correct location");

		// second try, with missing speed
		ml = new ManoeuvreToLocation(new WorldLocation(1, 1, 1), null);
		ml.setName("bingop");
		os = new java.io.ByteArrayOutputStream();
		mr.exportThis(ml, os);

		// great, now try to read it in!
		is = new java.io.ByteArrayInputStream(os.toByteArray());
		mr.importThis("", is);

		// check we got it
		assertEquals(resp.getName(), "bingop", "it has the correct name");
		assertEquals(resp.getSpeed(), null, "it has the correct (null) speed");
		assertEquals(resp.getLocation(), new WorldLocation(1, 1, 1), "correct location");

	}

}
