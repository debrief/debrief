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
import junit.framework.TestCase;

public class ImportKML {

	public static class ImportKMLTest extends TestCase {

		private static final String RADAR_PLACEMARK = "<Placemark><name>%s</name><MultiGeometry/>"
				+ "<TimeStamp><when>2009-09-12T20:01:12Z</when></TimeStamp>"
				+ "<Point><coordinates>7.9633,5.9696,0</coordinates></Point>"
				+ "<description><![CDATA[<b>RADAR PLOT</b><br><hr>Lat: 05.9696<br>Lon: 07.9633"
				+ "<br>Course: 253.0<br>Speed: 7.1 knots<br>Date: September 12, 2009]]></description></Placemark>";

		private static InputStream asStream(final String str) {
			return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		}

		@Override
		protected void setUp() throws Exception {
			DialogFactory.setRunHeadless(true);
		}

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
				assertNull("external entity must not be resolved into a track name", layers.findLayer("radar-42"));
				assertEquals("nothing imported from a file with a DOCTYPE", 0, layers.size());
			} finally {
				secret.delete();
			}
		}

		public void testRadarPlotStillImports() throws Exception {
			final String kml = "<?xml version=\"1.0\"?>\n<kml><Document>" + String.format(RADAR_PLACEMARK, "12 - radar")
					+ "</Document></kml>";
			final Layers layers = new Layers();
			doImport(layers, asStream(kml), "radar.kml");
			final TrackWrapper track = (TrackWrapper) layers.findLayer("radar-12");
			assertNotNull("track created", track);
			assertEquals(1, track.numFixes());
		}

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
			assertNotNull("valid placemark loaded", layers.findLayer("radar-12"));
			assertNotNull("2D coords accepted", layers.findLayer("radar-13"));
			assertNotNull("missing description accepted", layers.findLayer("radar-14"));
			assertNull("missing time skipped", layers.findLayer("radar-15"));
			final List<String> msgs = problems.getProblems();
			assertEquals("problems:" + msgs, 4, problems.size());
			assertTrue(msgs.toString(), msgs.get(0).contains("Ship A"));
			assertTrue(msgs.toString(), msgs.get(1).contains("15 - radar"));
			assertTrue(msgs.toString(), msgs.get(2).contains("7.4,x"));
			assertTrue(msgs.toString(), msgs.get(3).contains("pin"));

			// and the line string points that could be read
			int lineFixes = 0;
			final Enumeration<Editable> iter = layers.elements();
			while (iter.hasMoreElements()) {
				final Editable next = iter.nextElement();
				if (next instanceof TrackWrapper && !next.getName().startsWith("radar")) {
					lineFixes += ((TrackWrapper) next).numFixes();
				}
			}
			assertEquals("3 good points in the line string", 3, lineFixes);
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

		public void testKmzImports() throws Exception {
			final byte[] data = kmz("doc.kml", "<?xml version=\"1.0\"?>\n<kml><Document>"
					+ String.format(RADAR_PLACEMARK, "12 - radar") + "<!--", 1000, "--></Document></kml>");
			final Layers layers = new Layers();
			doZipImport(layers, new ByteArrayInputStream(data), "radar.kmz", 100 * 1024);
			assertNotNull("track created", layers.findLayer("doc-12"));
		}

		public void testKmzBombRejected() throws Exception {
			// a valid KML with a comment that inflates past the limit
			final int limit = 1024 * 1024;
			final byte[] data = kmz("doc.kml", "<?xml version=\"1.0\"?>\n<kml><Document>"
					+ String.format(RADAR_PLACEMARK, "12 - radar") + "<!--", 2 * limit, "--></Document></kml>");
			assertTrue("highly compressed", data.length < limit / 50);
			final Layers layers = new Layers();
			doZipImport(layers, new ByteArrayInputStream(data), "radar.kmz", limit);
			assertEquals("oversized entry not imported", 0, layers.size());
		}
	}

	/**
	 * largest permitted total uncompressed size of the KML inside a KMZ. The KML
	 * is held in memory several times over while it's parsed, so this protects
	 * against zip bombs. There are no KMZ samples in the repo; this is a generous
	 * allowance for radar-plot / GPS tracker exports.
	 */
	public static final long MAX_KMZ_BYTES = 64L * 1024 * 1024;

	/**
	 * most entries we'll look through in a KMZ
	 */
	public static final int MAX_KMZ_ENTRIES = 1000;

	// cache the last layer - for speed
	private static TrackWrapper lastLayer = null;

	/**
	 * keep track of how many tracks we've created, so we can generate unique colors
	 */
	private static int colorCounter = 0;

	/**
	 * the time-stamp presumed for LineString data that may not contain time data
	 *
	 */
	private static int DEFAULT_TIME_STEP = 1000;

	/**
	 * create a fix using the supplied data
	 *
	 * @param target
	 * @param trackName
	 * @param theDate
	 * @param theLoc
	 */
	private static void addFix(final Layers target, final String trackName, final HiResDate theDate,
			final WorldLocation theLoc, final double courseDegs, final double speedKts) {
		// is this our current layer?
		if (lastLayer != null) {
			if (lastLayer.getName().equals(trackName)) {
				// sorted
			} else {
				lastLayer = null;
			}
		}

		if (lastLayer == null) {
			lastLayer = (TrackWrapper) target.findLayer(trackName);
			if (lastLayer == null) {
				createTrack(target, trackName);
			}
		}

		final double courseRads = MWC.Algorithms.Conversions.Degs2Rads(courseDegs);
		final double speedYPS = new WorldSpeed(speedKts, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3d;

		final FixWrapper newFix = new FixWrapper(new Fix(theDate, theLoc, courseRads, speedYPS));

		// and reset the time value
		newFix.resetName();

		lastLayer.addFix(newFix);
	}

	/**
	 * extract the course element from the supplied string
	 *
	 * @param descriptionTxt
	 * @return
	 * @throws ParseException
	 */
	private static double courseFrom(final String descriptionTxt) throws ParseException {
		double res = 0;
		// STRING LOOKS LIKE
		// <![CDATA[<b>RADAR PLOT 20:01:12 (GMT)</b><br><hr>Lat:
		// 05.9696<br>Lon: 07.9633<br>Course: 253.0<br>Speed: 7.1
		// knots<br>Date: September 12, 2009]]>
		final int startI = descriptionTxt.indexOf("Course");
		final int endI = descriptionTxt.indexOf("<br>Speed");
		if ((startI >= 0) && (endI > startI + 7)) {
			final String subStr = descriptionTxt.substring(startI + 7, endI);
			res = MWCXMLReader.readThisDouble(subStr.trim());
		}
		return res;
	}

	/**
	 * create a track using the supplied name
	 *
	 * @param target
	 * @param trackName
	 */
	private static void createTrack(final Layers target, final String trackName) {
		lastLayer = new TrackWrapper();
		lastLayer.setName(trackName);

		// sort out a color
		// sort out a color
		final Color theCol = DebriefColors.RandomColorProvider.getRandomColor(colorCounter++);
		lastLayer.setColor(theCol);

		target.addThisLayer(lastLayer);
	}

	public static ImportProblems doImport(final Layers theLayers, final InputStream inputStream, final String fileName) {
		final ImportProblems problems = new ImportProblems();
		try {

			// get the main part of the file - we use it for the track name
			final String prefix = tidyFileName(fileName);

			// read the file into a string
			final String theStr = inputStreamAsString(inputStream);// readFileAsString(thePath);

			// now ditch any non-compatible chars (such as the degree symbol)
			final String tidyStr = stripNonValidXMLCharacters(theStr);

			// wrap the string in a source
			final InputSource s = new InputSource(new StringReader(tidyStr));

			// get the document loader. It's hardened against external entities
			// (XXE), since KML files may come from anywhere
			final DocumentBuilder loader = SafeXMLFactory.newDocumentBuilder();

			// get parsing
			final Document doc = loader.parse(s);

			// normalise the DOM - to make it a little more stable/predictable
			doc.getDocumentElement().normalize();

			// get our XML date parser ready
			final SimpleDateFormat parser = new GMTDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");

			// find the placemarks
			final NodeList nodeList = doc.getElementsByTagName("Placemark");

			// right, work through them. A placemark we can't read is skipped (and
			// reported), the rest are still loaded
			for (int i = 0; i < nodeList.getLength(); i++) {
				// ok - we have a placemark, see if it's got useful data
				final Element thisP = (Element) nodeList.item(i);
				final String placemarkName = textOf(thisP, "name");
				final String description = "Placemark " + (i + 1) + (placemarkName == null ? "" : " (" + placemarkName + ")");
				try {
					if (thisP.getElementsByTagName("MultiGeometry").getLength() > 0) {
						// yup, it's one of ours (radar plot).
						readRadarPlot(theLayers, prefix, parser, thisP, placemarkName);
					} else if (thisP.getElementsByTagName("LineString").getLength() > 0) {
						// see if it's from a GPS tracker
						readLineString(theLayers, fileName, thisP, description, problems);
					} else {
						problems.add(description + ": no MultiGeometry or LineString, skipped");
					}
				} catch (final ParseException | RuntimeException e) {
					problems.add(description + ": " + e.getMessage() + ", skipped");
				}
			}

		} catch (final ParserConfigurationException | SAXException | IOException e) {
			problems.add("Unable to read KML: " + e.getMessage());
		}

		// tell the user about anything we had to skip
		problems.report("Import KML", fileName);

		// lastly, clear the 'last layer' object
		lastLayer = null;

		return problems;
	}

	/**
	 * read a radar-plot style placemark: name "[track id] - ...", a time stamp,
	 * a point, and course/speed in the description
	 */
	private static void readRadarPlot(final Layers theLayers, final String prefix, final SimpleDateFormat parser,
			final Element thisP, final String theName) throws ParseException {
		if (theName == null) {
			throw new ParseException("missing name", 0);
		}

		// get the first part of the track id
		final String trackIdTxt = theName.split("-")[0].trim();
		final int trackID;
		try {
			trackID = Integer.parseInt(trackIdTxt);
		} catch (final NumberFormatException e) {
			throw new ParseException("name doesn't start with a track number", 0);
		}

		// now for the time
		final String timeTxt = textOf(thisP, "when");
		if (timeTxt == null) {
			throw new ParseException("missing time", 0);
		}
		final Date theD = parser.parse(timeTxt.trim());

		// and the location
		final String coordsTxt = textOf(thisP, "coordinates");
		if (coordsTxt == null) {
			throw new ParseException("missing coordinates", 0);
		}
		final WorldLocation loc = parseTuple(coordsTxt.trim());

		// lastly, the course/speed (if present)
		final String descriptionTxt = textOf(thisP, "description");
		final double courseDegs = descriptionTxt == null ? 0 : courseFrom(descriptionTxt);
		final double speedKts = descriptionTxt == null ? 0 : speedFrom(descriptionTxt);

		addFix(theLayers, prefix + "-" + trackID, new HiResDate(theD.getTime()), loc, courseDegs, speedKts);
	}

	/**
	 * read a GPS tracker style placemark (e.g. NokiaSportsTracker), where the name
	 * of the file gives the start time
	 */
	private static void readLineString(final Layers theLayers, final String fileName, final Element thisP,
			final String description, final ImportProblems problems) {
		// get our XML date parser ready
		final SimpleDateFormat nokiaDateFormat = new GMTDateFormat("yyyyMMddHHmms");
		Date theDate = new Date();
		try {
			String trimmedFile = fileName.substring(1, fileName.length() - 1);
			trimmedFile = trimmedFile.substring(0, trimmedFile.length() - 4);
			theDate = nokiaDateFormat.parse(trimmedFile);
		} catch (final ParseException | RuntimeException e) {
			// file name doesn't give the time, stick with the current time
		}

		final Element theString = (Element) thisP.getElementsByTagName("LineString").item(0);
		final String contents = textOf(theString, "coordinates");
		if (contents == null) {
			problems.add(description + ": LineString has no coordinates, skipped");
		} else {
			parseTheseCoords(contents, theLayers, theDate, description, problems);
		}
	}

	/**
	 * @return the text of the first child element with this name, or null if there
	 *         isn't one
	 */
	private static String textOf(final Element parent, final String tag) {
		final Node node = parent.getElementsByTagName(tag).item(0);
		return node == null ? null : node.getTextContent();
	}

	/**
	 * parse a KML coordinate tuple: longitude,latitude[,altitude]
	 */
	private static WorldLocation parseTuple(final String tuple) throws ParseException {
		final String[] coords = tuple.split(",");
		if (coords.length < 2 || coords.length > 3) {
			throw new ParseException("coordinates should be longitude,latitude[,altitude]: \"" + tuple + "\"", 0);
		}
		final double longVal = MWCXMLReader.readThisDouble(coords[0]);
		final double latVal = MWCXMLReader.readThisDouble(coords[1]);
		final double altitudeVal = coords.length == 3 ? MWCXMLReader.readThisDouble(coords[2]) : 0;
		return new WorldLocation(latVal, longVal, -altitudeVal);
	}

	public static void doZipImport(final Layers theLayers, final InputStream inputStream, final String fileName) {
		doZipImport(theLayers, inputStream, fileName, MAX_KMZ_BYTES);
	}

	static void doZipImport(final Layers theLayers, final InputStream inputStream, final String fileName,
			final long maxBytes) {
		ZipEntry entry;
		long remaining = maxBytes;
		int numEntries = 0;
		try (ZipInputStream zis = new ZipInputStream(inputStream)) {
			while ((entry = zis.getNextEntry()) != null) {
				if (++numEntries > MAX_KMZ_ENTRIES) {
					throw new SafeArchive.ArchiveException(
							fileName + " has more than the permitted " + MAX_KMZ_ENTRIES + " entries");
				}

				// is this one of ours?
				final String theName = entry.getName();

				if (theName.endsWith(".kml")) {
					// cool, here it is - process it

					// extract the data, with a cap on the uncompressed size (zip bomb)
					final ByteArrayOutputStream bos = new ByteArrayOutputStream();
					remaining -= SafeArchive.copy(zis, bos, remaining, fileName);
					zis.closeEntry();

					// now create a byte input stream from the byte output stream
					final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

					// and create it
					doImport(theLayers, bis, theName);
				}
			}
		} catch (final IOException e) {
			MWC.Utilities.Errors.Trace.trace(e, "Failed to read KMZ file:" + fileName);
			DialogFactory.showMessage("Import KMZ", "Failed to read " + fileName + ": " + e.getMessage());
		}

	}

	private static String inputStreamAsString(final InputStream stream) throws IOException {
		final InputStreamReader isr = new InputStreamReader(stream);
		final BufferedReader br = new BufferedReader(isr);
		final StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}

		br.close();
		return sb.toString();
	}

	/**
	 * utility to run through the contents of a LineString item: whitespace
	 * separated longitude,latitude[,altitude] tuples. Tuples that can't be read
	 * are skipped and reported.
	 *
	 * @param contents  the inside of the linestring construct
	 * @param theLayers where we're going to stick the data
	 * @param startDate the start date for the track
	 */
	private static void parseTheseCoords(final String contents, final Layers theLayers, final Date startDate,
			final String description, final ImportProblems problems) {
		Date newDate = new Date(startDate.getTime());

		for (final String tuple : contents.trim().split("\\s+")) {
			if (tuple.isEmpty()) {
				continue;
			}
			try {
				addFix(theLayers, startDate.toString(), new HiResDate(newDate.getTime()), parseTuple(tuple), 0, 0);
			} catch (final ParseException e) {
				problems.add(description + ": couldn't read point \"" + tuple + "\" (" + e.getMessage() + "), skipped");
			}

			// add a second incremenet to the date, to create the new date
			newDate = new Date(newDate.getTime() + DEFAULT_TIME_STEP);
		}

	}

	/**
	 * extract the course element from the supplied string
	 *
	 * @param descriptionTxt
	 * @return
	 * @throws ParseException
	 */
	private static double speedFrom(final String descriptionTxt) throws ParseException {
		double res = 0;
		// STRING LOOKS LIKE
		// <![CDATA[<b>RADAR PLOT 20:01:12 (GMT)</b><br><hr>Lat:
		// 05.9696<br>Lon: 07.9633<br>Course: 253.0<br>Speed: 7.1
		// knots<br>Date: September 12, 2009]]>
		final int startI = descriptionTxt.indexOf("Speed");
		final int endI = descriptionTxt.indexOf("knots");
		if ((startI >= 0) && (endI > startI + 6)) {
			final String subStr = descriptionTxt.substring(startI + 6, endI);
			res = MWCXMLReader.readThisDouble(subStr.trim());
		}
		return res;
	}

	/**
	 * This method ensures that the output String has only valid XML unicode
	 * characters as specified by the XML 1.0 standard. For reference, please see
	 * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
	 * standard</a>. This method will return an empty String if the input is null or
	 * empty.
	 *
	 * @param in The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	private static String stripNonValidXMLCharacters(final String in) {
		final StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || ("".equals(in))) {
			return ""; // vacancy test.
		}
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here;
			// it should not happen.
			if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF))) {
				out.append(current);
			}
		}
		return out.toString();
	}

	/**
	 * Remove the suffix from the passed file name, together with any leading path.
	 *
	 * @param fileName File name to remove suffix from.
	 *
	 * @return <TT>fileName</TT> without a suffix.
	 *
	 * @throws IllegalArgumentException if <TT>null</TT> file name passed.
	 */
	private static String tidyFileName(final String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException("file name == null");
		}

		// start off by ditching the path
		final File holder = new File(fileName);
		String res = holder.getName();

		// now ditch the file suffix
		final int pos = res.lastIndexOf('.');
		if (pos > 0 && pos < res.length() - 1) {
			res = res.substring(0, pos);
		}
		return res;
	}

	// private static String readFileAsString(String filePath)
	// throws java.io.IOException
	// {
	// StringBuffer fileData = new StringBuffer(1000);
	// BufferedReader reader = new BufferedReader(new FileReader(filePath));
	// char[] buf = new char[1024];
	// int numRead = 0;
	// while ((numRead = reader.read(buf)) != -1)
	// {
	// String readData = String.valueOf(buf, 0, numRead);
	// fileData.append(readData);
	// buf = new char[1024];
	// }
	// reader.close();
	// return fileData.toString();
	// }
	//
	// public static void main(String[] args)
	// {
	// doImport(null, null, null);
	// }

}
