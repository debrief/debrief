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

package Debrief.ReaderWriter.Word;

import Debrief.ReaderWriter.Replay.ImportReplayTest;

import static Debrief.ReaderWriter.Word.ImportRiderNarrativeDocument.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Word.ImportRiderNarrativeDocument.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.TacticalData.TrackDataProvider;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportRiderNarrativeDocumentTest {
	private class DummyTrackDataProvider implements TrackDataProvider {
		final private TrackWrapper _primary;

		private DummyTrackDataProvider(final TrackWrapper primary) {
			_primary = primary;
		}

		@Override
		public void addSecondary(final WatchableList secondary) {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public void addTrackDataListener(final TrackDataListener listener) {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public void addTrackShiftListener(final TrackShiftListener listener) {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public void fireTracksChanged() {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public void fireTrackShift(final WatchableList watchableList) {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public WatchableList getPrimaryTrack() {
			return _primary;
		}

		@Override
		public WatchableList[] getSecondaryTracks() {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public void removeTrackDataListener(final TrackDataListener listener) {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public void removeTrackShiftListener(final TrackShiftListener listener) {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public void setPrimary(final WatchableList primary) {
			throw new IllegalArgumentException("Not implemented");
		}

		@Override
		public void setSecondary(final WatchableList secondary) {
			throw new IllegalArgumentException("Not implemented");
		}

	}

	private final static String valid_doc_path = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/RiderNarrative.docx";

	private final static String valid_doc_path_bad_date = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/RiderNarrative2.docx";

	private final static String test_doc_root = "../org.mwc.debrief.legacy/src/Debrief/ReaderWriter/Word/test_docs";
	private final static String bad_date_1 = test_doc_root + "/" + "BadDate1.docx";

	private final static String old_doc_format = test_doc_root + "/" + "RiderNarrative.doc";

	private final static String wrong_type_path = test_doc_root + "/" + "RiderNarrative_WrongType.doc";

	private final static String ownship_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/nonsuch.rep";

	public static int countLines(final String str) {
		if (str == null || str.isEmpty()) {
			return 0;
		}
		int lines = 1;
		int pos = 0;
		while ((pos = str.indexOf("\n", pos) + 1) != 0) {
			lines++;
		}
		return lines;
	}

	@SuppressWarnings("unused")
	private String messageStr = null;

	@BeforeEach
	public void setUp() {
		// System.out.println("setting up message provider ");

		// clear the message string
		messageStr = null;

		// initialise the message provider
		MessageProvider.Base.setProvider(new MessageProvider() {

			@Override
			public void show(final String title, final String message, final int status) {
				messageStr = message;
			}
		});
	}

	@Test
	public void testBadDate() throws InterruptedException, IOException {
		final String testFile = bad_date_1;
		final File testI = new File(testFile);
		assertTrue(testI.exists());
		final InputStream is = new FileInputStream(testI);
		final XWPFDocument doc = new XWPFDocument(is);
		final List<XWPFTable> tables = doc.getTables();
		assertTrue(canImport(new DocXHelper(tables.get(0))), "can import");
		doc.close();
	}

	@Test
	public void testCanImport() throws InterruptedException, IOException {
		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());
		final InputStream is = new FileInputStream(testI);
		final XWPFDocument doc = new XWPFDocument(is);
		final List<XWPFTable> tables = doc.getTables();
		assertTrue(canImport(new DocXHelper(tables.get(0))), "can import");
		doc.close();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testCombineTime() throws ParseException {
		final ImportRiderNarrativeDocument im = new ImportRiderNarrativeDocument(null, null);

		final Date date = im.DATE_FORMAT.parse("090000Z JUL 17");
		final Date time = im.TIME_FORMAT.parse("02:01:34");

		final long millis = dateFor(date, time);

		final Date res = new Date(millis);
		assertEquals("9 Jul 2017 02:01:34 GMT", res.toGMTString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDateImport() throws ParseException {
		final String string2 = "090000Z JUL 17";

		final DateFormat df = new GMTDateFormat(DATE_FORMAT_STR);
		assertEquals("9 Jul 2017 00:00:00 GMT", df.parse(string2).toGMTString(), "correct date");
	}

	@Test
	public void testImportRiderNarrative() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");
		final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
		assertNotNull(parent, "found parent track");

		// fix the track name
		parent.setName("NONSUCH");

		final String testFile = old_doc_format;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);
		assertEquals(1, tLayers.size(), "layers empty");

		final ImportRiderNarrativeDocument importer = new ImportRiderNarrativeDocument(tLayers, null);
		final HWPFDocument doc = new HWPFDocument(is);

		final Range range = doc.getRange();
		final Table table = new TableIterator(range).next();
		assertTrue(canImport(new DocHelper(table)));

		final TableBreakdown data = importer.importFromWord(doc);
		testThisData(tLayers, parent, importer, data);

	}

	/**
	 * test we don't fall over when origin date isn't at midnight
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testImportRiderNarrativeOffsetDate() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		assertEquals(1, tLayers.size(), "read in track");
		final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
		assertNotNull(parent, "found parent track");

		// fix the track name
		parent.setName("NONSUCH");

		final String testFile = valid_doc_path_bad_date;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportRiderNarrativeDocument importer = new ImportRiderNarrativeDocument(tLayers, null);
		assertEquals(1, tLayers.size(), "layers empty");

		final XWPFDocument doc = new XWPFDocument(is);
		final TableBreakdown data = importer.importFromWordX(doc);

		testThisData(tLayers, parent, importer, data);
	}

	@Test
	public void testImportRiderNarrativeX() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		assertEquals(1, tLayers.size(), "read in track");
		final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
		assertNotNull(parent, "found parent track");

		// fix the track name
		parent.setName("NONSUCH");

		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportRiderNarrativeDocument importer = new ImportRiderNarrativeDocument(tLayers, null);
		assertEquals(1, tLayers.size(), "layers empty");

		final XWPFDocument doc = new XWPFDocument(is);
		final TableBreakdown data = importer.importFromWordX(doc);

		testThisData(tLayers, parent, importer, data);
	}

	@Test
	public void testImportRiderNarrativeXMissingPlatformNoPrimaryTrack() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");
		final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
		assertNotNull(parent, "found parent track");

		// add in a second track
		final TrackWrapper secondTrack = new TrackWrapper();
		secondTrack.setName("some name");
		tLayers.addThisLayer(secondTrack);

		parent.setName("NELSON");

		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportRiderNarrativeDocument importer = new ImportRiderNarrativeDocument(tLayers,
				new DummyTrackDataProvider(null));
		assertEquals(2, tLayers.size(), "tracks present");

		final XWPFDocument doc = new XWPFDocument(is);
		final TableBreakdown data = importer.importFromWordX(doc);

		importer.processThese(data);

		// ok, check the sensors got added
		final BaseLayer sensors = parent.getSensors();
		assertEquals(0, sensors.size(), "No Sensors created");

		// also check no narrative entries added
		final NarrativeWrapper narr = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNull(narr);
	}

	@Test
	public void testImportRiderNarrativeXMissingPlatformPrimaryTrack() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");
		final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
		assertNotNull(parent, "found parent track");

		// add in a second track
		final TrackWrapper secondTrack = new TrackWrapper();
		secondTrack.setName("some name");
		tLayers.addThisLayer(secondTrack);

		parent.setName("NELSON");

		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportRiderNarrativeDocument importer = new ImportRiderNarrativeDocument(tLayers,
				new DummyTrackDataProvider(parent));
		assertEquals(2, tLayers.size(), "tracks present");

		final XWPFDocument doc = new XWPFDocument(is);
		final TableBreakdown data = importer.importFromWordX(doc);

		importer.processThese(data);

		// ok, check the sensors got added
		final BaseLayer sensors = parent.getSensors();
		assertEquals(1, sensors.size(), "Sensor created");
		final SensorWrapper theS = (SensorWrapper) sensors.elements().nextElement();
		assertEquals(7, theS.size());
	}

	@Test
	public void testImportRiderNarrativeXMissingPlatformSingleTrack() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");
		final TrackWrapper parent = (TrackWrapper) tLayers.findLayer("NONSUCH");
		assertNotNull(parent, "found parent track");

		parent.setName("NELSON");

		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportRiderNarrativeDocument importer = new ImportRiderNarrativeDocument(tLayers, null);
		assertEquals(1, tLayers.size(), "layers empty");

		final XWPFDocument doc = new XWPFDocument(is);
		final TableBreakdown data = importer.importFromWordX(doc);

		importer.processThese(data);

		// ok, check the sensors got added
		final BaseLayer sensors = parent.getSensors();
		assertEquals(1, sensors.size(), "Sensor created");
		final SensorWrapper theS = (SensorWrapper) sensors.elements().nextElement();
		assertEquals(7, theS.size());
	}

	private void testThisData(final Layers tLayers, final TrackWrapper parent,
			final ImportRiderNarrativeDocument importer, final TableBreakdown data) {
		final SimpleDateFormat dateF = new GMTDateFormat("yyyy/MM/dd HH:mm:ss");

		assertNotNull(data);
		assertNotNull(data.header);
		assertNotNull(data.header.startDate);
		assertNotNull(data.entries);
		assertEquals(15, data.entries.size());
		assertEquals("HMS Nonsuch", data.header.platform);
		assertEquals("2009/07/22 00:00:00", dateF.format(data.header.startDate));

		final RiderEntry entry4 = data.entries.get(4);
		assertEquals("2009/07/22 04:14:16", dateF.format(entry4.date));
		assertEquals(null, entry4.bearing);
		assertEquals("Lorem ipsum 5", entry4.text);
		assertEquals("21", entry4.beam);
		assertEquals("[Beam:21] Lorem ipsum 5", entry4.toString());

		final RiderEntry entry5 = data.entries.get(5);
		assertEquals("2009/07/22 04:15:17", dateF.format(entry5.date));
		assertEquals(274, entry5.bearing.intValue());
		assertEquals("Lorem ipsum 6", entry5.text);
		assertEquals(null, entry5.beam);
		assertEquals("[Brg:274] Lorem ipsum 6", entry5.toString());

		final RiderEntry entry8 = data.entries.get(7);
		assertEquals("2009/07/22 04:17:19", dateF.format(entry8.date));
		assertEquals(null, entry8.bearing);
		assertTrue(entry8.text.contains("\n"), "Contains newline");
		assertEquals(null, entry8.beam);

		// the two different importers handle \r newlines differently.
		// so we allow for both permutations here
		final boolean matchesText = entry8.toString().equals("Lorem ipsum 8And more \nAnd more")
				|| entry8.toString().equals("Lorem ipsum 8\rAnd more \nAnd more");
		assertTrue(matchesText, "correct text");

		final RiderEntry entry9 = data.entries.get(9);
		assertEquals("2009/07/22 04:19:21", dateF.format(entry9.date));
		assertEquals(92, entry9.bearing.intValue());
		assertEquals(112, entry9.ambig.intValue());
		assertEquals("12/13", entry9.beam);
		assertEquals("[Brg:92/112, Beam:12/13] Lorem ipsum 10", entry9.toString());

		final RiderEntry entry10 = data.entries.get(10);
		assertEquals("2009/07/22 04:20:22", dateF.format(entry10.date));
		assertEquals(94, entry10.bearing.intValue());
		assertEquals(123, entry10.ambig.intValue());
		assertEquals(null, entry10.beam);

		final RiderEntry entry11 = data.entries.get(11);
		assertEquals("2009/07/22 04:21:23", dateF.format(entry11.date));
		assertEquals(null, entry11.bearing);
		assertEquals(null, entry11.ambig);
		assertEquals(null, entry11.beam);
		assertEquals("[Bearing represents arc (096-112). Not imported.]Lorem ipsum 12", entry11.toString());

		final RiderEntry entry12 = data.entries.get(12);
		assertEquals("2009/07/22 04:21:23", dateF.format(entry12.date));
		assertEquals(null, entry12.bearing);
		assertEquals(null, entry12.ambig);
		assertEquals(null, entry12.beam);
		assertEquals(DTG_MISSING_STR + "Lorem ipsum 13", entry12.toString());

		final RiderEntry entry13 = data.entries.get(13);
		assertEquals("2009/07/22 04:21:23", dateF.format(entry13.date));
		assertEquals(null, entry13.bearing);
		assertEquals(null, entry13.ambig);
		assertEquals(null, entry13.beam);
		assertEquals(DTG_MISSING_STR + "[Bearing represents arc (119-143). Not imported.]Lorem ipsum 14",
				entry13.toString());

		// ok, now store them
		importer.processThese(data);

		// hmmm, how many tracks
		assertEquals(2, tLayers.size(), "got new tracks");

		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.elementAt(1);
		// correct final count
		assertEquals(15, narrLayer.size(), "Got num lines");

		// check ownship received cuts
		assertNotNull(parent.getSensors(), "got a sensor");
		final SensorWrapper sensor = findOurSensor(NARRATIVE_CUTS_SENSOR, parent, false);

		assertNotNull(sensor, "got our sensor");
		assertEquals(7, sensor.size(), "got cuts");

		final Enumeration<Editable> cuts = sensor.elements();
		SensorContactWrapper lastCut = null;
		SensorContactWrapper firstCut = null;
		while (cuts.hasMoreElements()) {
			lastCut = (SensorContactWrapper) cuts.nextElement();
			if (firstCut == null) {
				firstCut = lastCut;
			}
		}

		assertNotNull(firstCut);
		assertEquals("090722 041012", firstCut.toString());
		assertNull(firstCut.getRange());
		assertEquals(NARRATIVE_CUTS_SENSOR, firstCut.getSensor().getName());
		assertFalse(firstCut.getHasAmbiguousBearing());
		assertEquals(273d, firstCut.getBearing(), 0.001);
		assertEquals(Double.NaN, firstCut.getAmbiguousBearing(), 0.001);

		assertNotNull(lastCut);
		assertTrue(lastCut.getHasAmbiguousBearing());
		assertEquals(094d, lastCut.getBearing(), 0.001);
		assertEquals(123d, lastCut.getAmbiguousBearing(), 0.001);
		assertEquals("090722 042022", lastCut.toString());
		assertNull(lastCut.getRange());
		assertEquals(NARRATIVE_CUTS_SENSOR, lastCut.getSensor().getName());

	}

	@Test
	public void testTidyDate() throws ParseException {
		final String GOOD_STR = "220000Z JUL 09";
		final String BAD_STR = "220001Z JUL 09";
		final DateFormat format = new GMTDateFormat(DATE_FORMAT_STR);
		final Date goodDate = format.parse(GOOD_STR);
		final Date fixedDate = tidyDate(BAD_STR, format);
		assertEquals(goodDate, fixedDate, "correct date");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTimeImport() throws ParseException {
		final String string2 = "02:01:34";
		final DateFormat df = new GMTDateFormat("HH:mm:ss");
		assertEquals("1 Jan 1970 02:01:34 GMT", df.parse(string2).toGMTString(), "correct time");
	}

	@Test
	public void testWronglyNamedVersion() throws FileNotFoundException {
		final String testFile = wrong_type_path;
		final Layers layers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, layers);

		final File testI = new File(testFile);
		assertTrue(testI.exists());
		final InputStream is = new FileInputStream(testI);
		final ImportRiderNarrativeDocument importer = new ImportRiderNarrativeDocument(layers, null);
		importer.handleImport(testFile, is);
		assertEquals(2, layers.size(), "have loaded track and narrative");
	}

}
