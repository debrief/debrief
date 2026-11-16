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

package Debrief.ReaderWriter.XML.csv_gz;

import static Debrief.ReaderWriter.XML.csv_gz.Import_CSV_GZ.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.XML.csv_gz.Import_CSV_GZ.*;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.LoggingService;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.SafeArchive;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class Import_CSV_GZTest {
	static class Logger implements ErrorLogger {
		protected final List<String> messages = new ArrayList<String>();

		private final boolean console = true;

		public List<String> getMessages() {
			return messages;
		}

		public boolean isEmpty() {
			return messages.isEmpty();
		}

		@Override
		public void logError(final int status, final String text, final Exception e) {
			output(text, e);
		}

		@Override
		public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
			output(text, e);
		}

		@Override
		public void logStack(final int status, final String text) {
			output(text, null);
		}

		public void output(final String text, final Exception e) {
			messages.add(text);
			if (console) {
				System.out.println(text);
				if (e != null) {
					e.printStackTrace();
				}
			}
		}
	}

	@BeforeEach
	public void setUp() {
		DialogFactory.setRunHeadless(true);
	}

	@Test
	public void test_gzip_bomb_rejected() throws IOException {
		final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
		final String filename = "BARTON_xxxx_SystemTrack_xxxx.csv.gz";
		final byte[] realData;
		try (InputStream in = new GZIPInputStream(new FileInputStream(root + filename))) {
			realData = in.readAllBytes();
		}

		// real data, followed by a long run of highly compressible padding
		final int limit = 1024 * 1024;
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (GZIPOutputStream gz = new GZIPOutputStream(bos)) {
			gz.write(realData);
			final byte[] spaces = new byte[64 * 1024];
			Arrays.fill(spaces, (byte) ' ');
			for (int i = 0; i < 2 * limit / spaces.length; i++) {
				gz.write(spaces);
			}
		}
		assertTrue(bos.size() < limit / 50, "highly compressed");

		final Layers theLayers = new Layers();
		final Logger logger = new Logger();
		try {
			new Import_CSV_GZ().doZipImport(theLayers, new ByteArrayInputStream(bos.toByteArray()), filename,
					logger, limit);
		} catch (final RuntimeException re) {
			// the loader reports these to the user
			logger.messages.add(re.getMessage());
		}
		boolean found = false;
		for (final String msg : logger.getMessages()) {
			found |= msg != null && msg.contains("larger than");
		}
		assertTrue(found, "size limit reported:" + logger.getMessages());
	}

	@Test
	public void test_parse_bad_OSD_File() throws IOException {
		final String root = "../org.mwc.debrief.legacy/test_data/CSV_GZ_Import/";
		final String filename = "BARTON_xxxx_OSD_xxxx.csv.gz";

		// start off with the ownship track
		final File zipFile = new File(root + filename);
		assertTrue(zipFile.exists());

		assertTrue(GzipUtils.isCompressedFilename(root + filename), "is gzip");
		assertEquals("BARTON_xxxx_OSD_xxxx.csv", GzipUtils.getUncompressedFilename(filename), "name");

		final InputStream bs = new FileInputStream(zipFile);

		final Layers theLayers = new Layers();

		// check empty
		assertEquals(0, theLayers.size(), "empty");

		final Import_CSV_GZ importer = new Import_CSV_GZ();
		final Logger logger = new Logger();
		importer.doZipImport(theLayers, bs, filename, logger);

		// check 3 errors thrown
		assertEquals(3, logger.getMessages().size(), "3 messages");
		final List<String> strings = logger.getMessages();
		assertTrue(strings
				.contains("Problem at line:3 While parsing attr_latitude_For input string: \"a1.585351162\""));
		assertTrue(strings.contains("Problem at line:5 Missing token for attr_latitude"));
		assertTrue(strings.contains("Problem at line:11 Missing token for attr_longitude"));
	}

	@Test
	public void test_parse_Big_Tracks_File() throws IOException {
		final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
		final String filename = "BARTON_xxxx_SystemTrack_xxxx.csv.gz";

		// start off with the ownship track
		final File zipFile = new File(root + filename);
		assertTrue(zipFile.exists());

		assertTrue(GzipUtils.isCompressedFilename(root + filename), "is gzip");
		assertEquals("BARTON_xxxx_SystemTrack_xxxx.csv", GzipUtils.getUncompressedFilename(filename), "name");

		final InputStream bs = new FileInputStream(zipFile);

		final Layers theLayers = new Layers();

		// check empty
		assertEquals(0, theLayers.size(), "empty");
		final Logger logger = new Logger();

		new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);

		// check empty
		assertEquals(3, theLayers.size(), "has track");
		final Layer bTrack = theLayers.findLayer("BARTON");
		assertNotNull(bTrack, "found track");
		final TrackWrapper track = (TrackWrapper) bTrack;

		assertEquals(10, track.numFixes(), "has fixes");
		final Enumeration<Editable> pIter = track.getPositionIterator();
		// move along a bit
		pIter.nextElement();
		pIter.nextElement();
		pIter.nextElement();
		pIter.nextElement();
		final FixWrapper fix5 = (FixWrapper) pIter.nextElement();
		final Date dtg = fix5.getDTG().getDate();
		final SimpleDateFormat dfDate = new GMTDateFormat("dd/MMM/yyyy HH:mm:ss");
		assertEquals("12/Nov/2019 12:47:40", dfDate.format(dtg), "correct date");
		final WorldLocation loc = fix5.getLocation();
		assertEquals(-0.8071912410000001, Math.toRadians(loc.getLat()), 0.000001);
		assertEquals(0.788539756051038, Math.toRadians(loc.getLong()), 0.000001);
		assertEquals(0d, loc.getDepth(), 0.000001);
		assertEquals(0.847944870877505, fix5.getCourse(), 0.0001);
		assertEquals(6.8, new WorldSpeed(fix5.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec), 0.0001);

		// also check out the bulk trcks
		final BaseLayer bulk = (BaseLayer) theLayers.findLayer(BULK_TRACKS);
		assertNotNull(bulk);
		assertEquals(2, bulk.size(), "2 tracks");
	}

	@Test
	public void test_parse_OSD_File() throws IOException {
		final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
		final String filename = "BARTON_xxxx_OSD_xxxx.csv.gz";

		// start off with the ownship track
		final File zipFile = new File(root + filename);
		assertTrue(zipFile.exists());

		assertTrue(GzipUtils.isCompressedFilename(root + filename), "is gzip");
		assertEquals("BARTON_xxxx_OSD_xxxx.csv", GzipUtils.getUncompressedFilename(filename), "name");

		final InputStream bs = new FileInputStream(zipFile);

		final Layers theLayers = new Layers();

		// check empty
		assertEquals(0, theLayers.size(), "empty");
		final Logger logger = new Logger();

		new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);

		// check empty
		assertEquals(1, theLayers.size(), "has track");
		final Layer bTrack = theLayers.findLayer("BARTON");
		assertNotNull(bTrack, "found track");
		final TrackWrapper track = (TrackWrapper) bTrack;
		assertEquals(27, track.numFixes(), "has fixes");
		final Enumeration<Editable> pIter = track.getPositionIterator();
		// move along a bit
		pIter.nextElement();
		pIter.nextElement();
		pIter.nextElement();
		pIter.nextElement();
		final FixWrapper fix5 = (FixWrapper) pIter.nextElement();
		final Date dtg = fix5.getDTG().getDate();
		final SimpleDateFormat dfDate = new GMTDateFormat("dd/MMM/yyyy HH:mm:ss");
		assertEquals("12/Nov/2019 12:47:00", dfDate.format(dtg), "correct date");
		final WorldLocation loc = fix5.getLocation();
		assertEquals(-0.789600782, Math.toRadians(loc.getLat()), 0.000001);
		assertEquals(0.765952588191503, Math.toRadians(loc.getLong()), 0.000001);
		assertEquals(37d, loc.getDepth(), 0.000001);
		assertEquals(0.523598776, fix5.getCourse());
		assertEquals(9d, new WorldSpeed(fix5.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec));

	}

	@Test
	public void test_parse_Sensor_File() throws IOException {

		final String root1 = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
		final String filename1 = "BARTON_xxxx_OSD_xxxx.csv.gz";

		// start off with the ownship track
		final File zipFile1 = new File(root1 + filename1);
		assertTrue(zipFile1.exists());

		assertTrue(GzipUtils.isCompressedFilename(root1 + filename1), "is gzip");
		assertEquals("BARTON_xxxx_OSD_xxxx.csv", GzipUtils.getUncompressedFilename(filename1), "name");

		final InputStream bs1 = new FileInputStream(zipFile1);

		final Layers theLayers = new Layers();

		// check empty
		assertEquals(0, theLayers.size(), "empty");
		final Logger logger = new Logger();

		new Import_CSV_GZ().doZipImport(theLayers, bs1, filename1, logger);

		final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
		final String filename = "BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv.gz";

		// start off with the ownship track
		final File zipFile = new File(root + filename);
		assertTrue(zipFile.exists());

		assertTrue(GzipUtils.isCompressedFilename(root + filename), "is gzip");
		assertEquals("BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv", GzipUtils.getUncompressedFilename(filename),
				"name");

		final InputStream bs = new FileInputStream(zipFile);

		// check empty
		assertEquals(1, theLayers.size(), "has ownship");

		new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);

		// check empty
		assertEquals(1, theLayers.size(), "has track");
		final Layer bTrack = theLayers.findLayer("BARTON");
		assertNotNull(bTrack, "found track");
		final TrackWrapper track = (TrackWrapper) bTrack;
		assertEquals(27, track.numFixes(), "has fixes");

		// get that sensor data
		final BaseLayer sensors = track.getSensors();
		assertEquals(3, sensors.size());

		final SensorWrapper sensor = (SensorWrapper) sensors.elements().nextElement();
		assertEquals(10, sensor.size(), "correct cuts");

		final Enumeration<Editable> pIter = sensor.elements();
		// move along a bit
		pIter.nextElement();
		pIter.nextElement();
		pIter.nextElement();
		pIter.nextElement();
		final SensorContactWrapper fix5 = (SensorContactWrapper) pIter.nextElement();
		final Date dtg = fix5.getDTG().getDate();
		final SimpleDateFormat dfDate = new GMTDateFormat("dd/MMM/yyyy HH:mm:ss");
		assertEquals("12/Nov/2019 12:47:55", dfDate.format(dtg), "correct date");
		assertEquals(Math.toDegrees(1.056688125), fix5.getBearing(), "correct bearing");
		assertEquals("78902_3000", fix5.getSensorName(), "correct sensor name");

	}

	@Test
	public void test_parse_Sensor_File_Host_Missing() throws IOException {
		final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
		final String filename = "BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv.gz";

		// start off with the ownship track
		final File zipFile = new File(root + filename);
		assertTrue(zipFile.exists());

		assertTrue(GzipUtils.isCompressedFilename(root + filename), "is gzip");
		assertEquals("BARTON_Tracks_xxxx_BSensorTrack_xxxx.csv", GzipUtils.getUncompressedFilename(filename),
				"name");

		final InputStream bs = new FileInputStream(zipFile);

		final Layers theLayers = new Layers();

		// check empty
		assertEquals(0, theLayers.size(), "empty");
		final Logger logger = new Logger();

		try {
			new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);
			fail("Should not get here");
		} catch (final RuntimeException re) {
			assertEquals("Host track not found:BARTON", re.getMessage(), "correct error message");
		}

	}

	@Test
	public void test_parse_Tracks_File() throws IOException {
		final String root = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
		final String filename = "BARTON_xxxx_SystemTrack_xxxx.csv.gz";

		// start off with the ownship track
		final File zipFile = new File(root + filename);
		assertTrue(zipFile.exists());

		assertTrue(GzipUtils.isCompressedFilename(root + filename), "is gzip");
		assertEquals("BARTON_xxxx_SystemTrack_xxxx.csv", GzipUtils.getUncompressedFilename(filename), "name");

		final InputStream bs = new FileInputStream(zipFile);

		final Layers theLayers = new Layers();

		// check empty
		assertEquals(0, theLayers.size(), "empty");
		final Logger logger = new Logger();

		new Import_CSV_GZ().doZipImport(theLayers, bs, filename, logger);

		// check empty
		assertEquals(3, theLayers.size(), "has track");
		final Layer bTrack = theLayers.findLayer("BARTON");
		assertNotNull(bTrack, "found track");
		final TrackWrapper track = (TrackWrapper) bTrack;
		assertEquals(10, track.numFixes(), "has fixes");
		final Enumeration<Editable> pIter = track.getPositionIterator();
		// move along a bit
		pIter.nextElement();
		pIter.nextElement();
		pIter.nextElement();
		pIter.nextElement();
		final FixWrapper fix5 = (FixWrapper) pIter.nextElement();
		final Date dtg = fix5.getDTG().getDate();
		final SimpleDateFormat dfDate = new GMTDateFormat("dd/MMM/yyyy HH:mm:ss");
		assertEquals("12/Nov/2019 12:47:40", dfDate.format(dtg), "correct date");
		final WorldLocation loc = fix5.getLocation();
		assertEquals(-0.8071912410000001, Math.toRadians(loc.getLat()), 0.000001);
		assertEquals(0.788539756051038, Math.toRadians(loc.getLong()), 0.000001);
		assertEquals(0d, loc.getDepth(), 0.000001);
		assertEquals(0.847944870877505, fix5.getCourse(), 0.0001);
		assertEquals(6.8, new WorldSpeed(fix5.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec), 0.0001);
	}

	@Test
	public void testDateParse() throws ParseException {
		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final OSD_Importer importer = pImporter.new OSD_Importer();
		final String test_date = "20 Nov 2019 - 11:22:33.000";
		final Date date = importer.getDate(test_date);

		final DateFormat df = new GMTDateFormat(CSV_DATE_FORMAT);
		assertEquals(test_date, df.format(date), "matching date");
	}

	@Test
	public void testOSD() throws ParseException {
		final ErrorLogger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("attr_courseOverTheGround");
		tokens.add("" + Math.PI);
		tokens.add("attr_longitude");
		tokens.add("" + Math.PI / 2);
		tokens.add("attr_latitude");
		tokens.add("" + Math.PI / 4);
		tokens.add("attr_depth");
		tokens.add("" + 22d);
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final OSD_Importer importer = pImporter.new OSD_Importer();
		final FixWrapper res = importer.process(tokens.iterator(), logger);
		assertNotNull(res, "should have fix");
		assertEquals(45d, res.getFixLocation().getLat(), "lat");
		assertEquals(90d, res.getFixLocation().getLong(), "long");
		assertEquals(22d, res.getFixLocation().getDepth(), "dep");
		assertEquals(180d, res.getCourseDegs(), "crse");
		assertEquals(1.5d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec), 0.0001,
				"speed");
	}

	@Test
	public void testOSD_bad_parse() throws ParseException {
		final ErrorLogger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("attr_courseOverTheGround");
		tokens.add("" + Math.PI);
		tokens.add("attr_longitude");
		tokens.add("a" + Math.PI / 2);
		tokens.add("attr_latitude");
		tokens.add("" + Math.PI / 4);
		tokens.add("attr_depth");
		tokens.add("" + 22d);
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final OSD_Importer importer = pImporter.new OSD_Importer();
		try {
			importer.process(tokens.iterator(), logger);
			fail("should have thrown exception");
		} catch (final NumberFormatException nf) {
			assertNotNull(nf);
		}
	}

	@Test
	public void testOSD_Short() throws ParseException {
		final Logger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("attr_courseOverTheGround");
		tokens.add("" + Math.PI);
		tokens.add("attr_depth");
		tokens.add("" + 22d);
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final OSD_Importer importer = pImporter.new OSD_Importer();
		final FixWrapper res = importer.process(tokens.iterator(), logger);
		assertNull(res, "should not have created fix");
		assertFalse(logger.isEmpty(), "should have thrown warning");
		assertEquals("Missing fields:attr_longitude, attr_latitude", logger.messages.get(0), "valid message");
	}

	@Test
	public void testParseFilename() {
		assertEquals("my1", getTrackPrefix("/C:/users/ronaldo/my1_ball1.csv"));
		assertEquals("my2", getTrackPrefix("//users/ronaldo/my2_ball2.csv"));
		assertEquals("my3", getTrackPrefix("my3_ball3.csv"));
	}

	@Test
	public void testSensor() throws ParseException {
		final ErrorLogger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("blah");
		tokens.add("123456789012345");
		tokens.add("attr_bearing");
		tokens.add("" + Math.PI);
		tokens.add("attr_longitude");
		tokens.add("" + Math.PI / 2);
		tokens.add("attr_trackNumber");
		tokens.add("2000");
		tokens.add("attr_depth");
		tokens.add("" + 22d);
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final Sensor_Importer importer = pImporter.new Sensor_Importer();

		final SensorContactWrapper res = importer.process(tokens.iterator(), logger);
		assertNotNull(res, "should have fix");
		// note: note including timezone in test, since Travis server
		// was running UTC not GMT
		assertTrue(res.getDTG().getDate().toString().startsWith("Wed Nov 20 11:22:33"), "DTG");
		assertEquals(180d, res.getBearing(), "bearing");
		assertEquals("789012345_2000", res.getSensorName(), "sensor");
	}

	@Test
	public void testSensor_Short() throws ParseException {
		final Logger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("blah");
		tokens.add("123456789012345");
		tokens.add("attr_longitude");
		tokens.add("" + Math.PI / 2);
		tokens.add("attr_trackNumber");
		tokens.add("2000");
		tokens.add("attr_depth");
		tokens.add("" + 22d);
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final Sensor_Importer importer = pImporter.new Sensor_Importer();
		final SensorContactWrapper res = importer.process(tokens.iterator(), logger);
		assertNull(res, "should not have created fix");
		assertFalse(logger.isEmpty(), "should have thrown warning");
		assertEquals("Missing fields:attr_bearing", logger.messages.get(0), "valid message");
	}

	@Test
	public void testSystem() throws ParseException {
		final ErrorLogger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("blah");
		tokens.add("123456789012345");

		tokens.add("attr_bearing");
		tokens.add("" + Math.PI);
		tokens.add("attr_longitude");
		tokens.add("" + Math.PI / 2);
		tokens.add("attr_latitude");
		tokens.add("" + Math.PI / 4);
		tokens.add("attr_countryAbbreviation");
		tokens.add("GBR");
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");
		tokens.add("attr_course");
		tokens.add("" + Math.PI / 8);
		tokens.add("attr_speed");
		tokens.add("33");
		tokens.add("attr_trackNumber");
		tokens.add("1234");

		final String hostName = "DINGO";

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final State_Importer importer = pImporter.new State_Importer();
		final FixWrapper res = importer.process(tokens.iterator(), logger, hostName);
		assertNotNull(res, "should have fix");
		assertEquals(45d, res.getFixLocation().getLat(), "lat");
		assertEquals(90d, res.getFixLocation().getLong(), "long");
		assertEquals(0d, res.getFixLocation().getDepth(), "dep");
		assertEquals(22.5d, res.getCourseDegs(), "crse");
		assertEquals(33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec), 0.0001,
				"speed");
		assertEquals("1234_GBR", res.getComment(), "country");
	}

	@Test
	public void testSystem_no_country() throws ParseException {
		final ErrorLogger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("blah");
		tokens.add("123456789012345");
		tokens.add("attr_bearing");
		tokens.add("" + Math.PI);
		tokens.add("attr_longitude");
		tokens.add("" + Math.PI / 2);
		tokens.add("attr_latitude");
		tokens.add("" + Math.PI / 4);
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");
		tokens.add("attr_course");
		tokens.add("" + Math.PI / 8);
		tokens.add("attr_speed");
		tokens.add("33");
		tokens.add("attr_trackNumber");
		tokens.add("3550");

		final String hostName = "DINGO";

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final State_Importer importer = pImporter.new State_Importer();
		final FixWrapper res = importer.process(tokens.iterator(), logger, hostName);
		assertNotNull(res, "should have fix");
		assertEquals(45d, res.getFixLocation().getLat(), "lat");
		assertEquals(90d, res.getFixLocation().getLong(), "long");
		assertEquals(0d, res.getFixLocation().getDepth(), "dep");
		assertEquals(22.5d, res.getCourseDegs(), "crse");
		assertEquals(33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec), 0.0001,
				"speed");
		assertEquals("3550", res.getComment(), "country");
	}

	@Test
	public void testSystem_short() throws ParseException {
		final Logger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");
		tokens.add("attr_course");
		tokens.add("" + Math.PI / 8);
		tokens.add("attr_speed");
		tokens.add("33");
		tokens.add("attr_trackNumber");
		tokens.add("11000323223550");

		final String hostName = "DINGO";

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final State_Importer importer = pImporter.new State_Importer();
		final FixWrapper res = importer.process(tokens.iterator(), logger, hostName);
		assertNull(res, "should not have created fix");
		assertFalse(logger.isEmpty(), "should have thrown warning");
		assertEquals("Missing fields:attr_bearing, attr_countryAbbreviation, attr_latitude, attr_longitude",
				logger.messages.get(0),
				"valid message");
	}

	@Test
	public void testSystemOwnship() throws ParseException {
		final ErrorLogger logger = new Logger();
		final List<String> tokens = new ArrayList<String>();
		tokens.add("20 Nov 2019 - 11:22:33.000");
		tokens.add("blah");
		tokens.add("rhah");
		tokens.add("blah");
		tokens.add("123456789012345");

		tokens.add("attr_bearing");
		tokens.add("" + Math.PI);
		tokens.add("attr_longitude");
		tokens.add("" + Math.PI / 2);
		tokens.add("attr_latitude");
		tokens.add("" + Math.PI / 4);
		tokens.add("attr_countryAbbreviation");
		tokens.add("GBR");
		tokens.add("attr_speedOverTheGround");
		tokens.add("1.5");
		tokens.add("bahh");
		tokens.add("attr_course");
		tokens.add("" + Math.PI / 8);
		tokens.add("attr_speed");
		tokens.add("33");
		tokens.add("attr_trackNumber");
		tokens.add("1");
		final String hostName = "DINGO";

		final Import_CSV_GZ pImporter = new Import_CSV_GZ();
		final State_Importer importer = pImporter.new State_Importer();
		final FixWrapper res = importer.process(tokens.iterator(), logger, hostName);
		assertNotNull(res, "should have fix");
		assertEquals(45d, res.getFixLocation().getLat(), "lat");
		assertEquals(90d, res.getFixLocation().getLong(), "long");
		assertEquals(0d, res.getFixLocation().getDepth(), "dep");
		assertEquals(22.5d, res.getCourseDegs(), "crse");
		assertEquals(33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts).getValueIn(WorldSpeed.M_sec), 0.0001,
				"speed");
		assertEquals(hostName, res.getComment(), "country");
	}

	public void unTest_Logger() {
		final State_Importer importer = new Import_CSV_GZ().new State_Importer();
		for (int i = 0; i < 2000; i++) {
			importer.perfLog(i);
		}
	}
}
