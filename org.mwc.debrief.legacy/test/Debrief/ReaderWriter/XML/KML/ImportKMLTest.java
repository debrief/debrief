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

package Debrief.ReaderWriter.XML.KML;

import static Debrief.ReaderWriter.XML.KML.ImportKML.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.XML.KML.ImportKML.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.ImportProblems;
import MWC.Utilities.ReaderWriter.SafeArchive;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.SafeXMLFactory;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportKMLTest {

	private static final String RADAR_PLACEMARK = "<Placemark><name>%s</name><MultiGeometry/>"
			+ "<TimeStamp><when>2009-09-12T20:01:12Z</when></TimeStamp>"
			+ "<Point><coordinates>7.9633,5.9696,0</coordinates></Point>"
			+ "<description><![CDATA[<b>RADAR PLOT</b><br><hr>Lat: 05.9696<br>Lon: 07.9633"
			+ "<br>Course: 253.0<br>Speed: 7.1 knots<br>Date: September 12, 2009]]></description></Placemark>";

	private static InputStream asStream(final String str) {
		return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
	}

	@BeforeEach
	public void setUp() throws Exception {
		DialogFactory.setRunHeadless(true);
	}

	@Test
	public void testExternalEntityNotResolved() throws Exception {
		final File secret = File.createTempFile("kml_secret", ".txt");
		try {
			try (FileWriter fw = new FileWriter(secret)) {
				// the track id is taken from the leading number of the name
				fw.write("42");
			}
			final String kml = "<?xml version=\"1.0\"?>\n<!DOCTYPE kml [<!ENTITY xxe SYSTEM \"" + secret.toURI()
					+ "\">]>\n<kml><Document>" + String.format(RADAR_PLACEMARK, "&xxe;") + "</Document></kml>";
			final Layers layers = new Layers();
			doImport(layers, asStream(kml), "radar.kml");
			assertNull(layers.findLayer("radar-42"), "external entity must not be resolved into a track name");
			assertEquals(0, layers.size(), "nothing imported from a file with a DOCTYPE");
		} finally {
			secret.delete();
		}
	}

	@Test
	public void testRadarPlotStillImports() throws Exception {
		final String kml = "<?xml version=\"1.0\"?>\n<kml><Document>" + String.format(RADAR_PLACEMARK, "12 - radar")
				+ "</Document></kml>";
		final Layers layers = new Layers();
		doImport(layers, asStream(kml), "radar.kml");
		final TrackWrapper track = (TrackWrapper) layers.findLayer("radar-12");
		assertNotNull(track, "track created");
		assertEquals(1, track.numFixes());
	}

	@Test
	public void testUnexpectedLayoutReported() throws Exception {
		final String twoCoordPlacemark = RADAR_PLACEMARK.replace("7.9633,5.9696,0", "7.9633,5.9696");
		final String noDescription = RADAR_PLACEMARK.replaceAll("<description>.*</description>", "");
		final String kml = "<?xml version=\"1.0\"?>\n<kml><Document>"
				// name isn't a numeric track id
				+ String.format(RADAR_PLACEMARK, "Ship A")
				// no altitude
				+ String.format(twoCoordPlacemark, "13 - radar")
				// no course/speed description
				+ String.format(noDescription, "14 - radar")
				// missing time
				+ String.format(RADAR_PLACEMARK, "15 - radar").replaceAll("<TimeStamp>.*</TimeStamp>", "")
				// valid
				+ String.format(RADAR_PLACEMARK, "12 - radar")
				// GPS style line string, whitespace separated tuples, some without altitude
				+ "<Placemark><name>route</name><LineString><coordinates>7.1,5.1,0 7.2,5.2\n7.3,5.3,0 7.4,x</coordinates>"
				+ "</LineString></Placemark>"
				// a point we don't know how to handle
				+ "<Placemark><name>pin</name><Point><coordinates>7.1,5.1,0</coordinates></Point></Placemark>"
				+ "</Document></kml>";
		final Layers layers = new Layers();
		final ImportProblems problems = doImport(layers, asStream(kml), "radar.kml");
		assertNotNull(layers.findLayer("radar-12"), "valid placemark loaded");
		assertNotNull(layers.findLayer("radar-13"), "2D coords accepted");
		assertNotNull(layers.findLayer("radar-14"), "missing description accepted");
		assertNull(layers.findLayer("radar-15"), "missing time skipped");
		final List<String> msgs = problems.getProblems();
		assertEquals(4, problems.size(), "problems:" + msgs);
		assertTrue(msgs.get(0).contains("Ship A"), msgs.toString());
		assertTrue(msgs.get(1).contains("15 - radar"), msgs.toString());
		assertTrue(msgs.get(2).contains("7.4,x"), msgs.toString());
		assertTrue(msgs.get(3).contains("pin"), msgs.toString());

		// and the line string points that could be read
		int lineFixes = 0;
		final Enumeration<Editable> iter = layers.elements();
		while (iter.hasMoreElements()) {
			final Editable next = iter.nextElement();
			if (next instanceof TrackWrapper && !next.getName().startsWith("radar")) {
				lineFixes += ((TrackWrapper) next).numFixes();
			}
		}
		assertEquals(3, lineFixes, "3 good points in the line string");
	}

	private static byte[] kmz(final String entryName, final String before, final int padding, final String after)
			throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(bos)) {
			zos.putNextEntry(new ZipEntry(entryName));
			zos.write(before.getBytes(StandardCharsets.UTF_8));
			final byte[] spaces = new byte[64 * 1024];
			Arrays.fill(spaces, (byte) ' ');
			int remaining = padding;
			while (remaining > 0) {
				final int n = Math.min(spaces.length, remaining);
				zos.write(spaces, 0, n);
				remaining -= n;
			}
			zos.write(after.getBytes(StandardCharsets.UTF_8));
			zos.closeEntry();
		}
		return bos.toByteArray();
	}

	@Test
	public void testKmzImports() throws Exception {
		final byte[] data = kmz("doc.kml", "<?xml version=\"1.0\"?>\n<kml><Document>"
				+ String.format(RADAR_PLACEMARK, "12 - radar") + "<!--", 1000, "--></Document></kml>");
		final Layers layers = new Layers();
		doZipImport(layers, new ByteArrayInputStream(data), "radar.kmz", 100 * 1024);
		assertNotNull(layers.findLayer("doc-12"), "track created");
	}

	@Test
	public void testKmzBombRejected() throws Exception {
		// a valid KML with a comment that inflates past the limit
		final int limit = 1024 * 1024;
		final byte[] data = kmz("doc.kml", "<?xml version=\"1.0\"?>\n<kml><Document>"
				+ String.format(RADAR_PLACEMARK, "12 - radar") + "<!--", 2 * limit, "--></Document></kml>");
		assertTrue(data.length < limit / 50, "highly compressed");
		final Layers layers = new Layers();
		doZipImport(layers, new ByteArrayInputStream(data), "radar.kmz", limit);
		assertEquals(0, layers.size(), "oversized entry not imported");
	}
}
