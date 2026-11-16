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

import static Debrief.ReaderWriter.Word.ImportNarrativeDocument.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Word.ImportNarrativeDocument.*;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.NMEA.ImportNMEA;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportNarrativeDocumentTest {
	private final static String dummy_doc_path = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/test_narrative.doc";
	private final static String valid_doc_path = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/FCS_narrative.doc";
	private final static String no_metadata_path = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/FCS_narrative_no_metadata.doc";

	private final static String ownship_track = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";
	private final static String ownship_track_test = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boattest.rep";

	private final static TrimNarrativeHelper only_in_period = new TrimNarrativeHelper() {

		@Override
		public NarrativeHelperRetVal findWhatToImport(final Map<String, Integer> narrativeTypes) {
			final NarrativeHelperRetVal retVal = new NarrativeHelperRetVal();
			retVal.narrativeEnum = ImportNarrativeEnum.TRIMMED_DATA;
			// retVal.selectedNarrativeTypes =
			// (List<String>)Arrays.asList(narrativeTypes.keySet().toArray(new
			// String[narrativeTypes.size()]));
			return retVal;
		}
	};

	private final static TrimNarrativeHelper only_selected_types = new TrimNarrativeHelper() {

		@Override
		public NarrativeHelperRetVal findWhatToImport(final Map<String, Integer> narrativeTypes) {
			final NarrativeHelperRetVal retVal = new NarrativeHelperRetVal();
			retVal.narrativeEnum = ImportNarrativeEnum.ALL_DATA;
			retVal.selectedNarrativeTypes = new ArrayList<>();
			retVal.selectedNarrativeTypes.add("CAT COMMENT");
			return retVal;
		}
	};
	private final static TrimNarrativeHelper only_selected_trimmed_data_types = new TrimNarrativeHelper() {

		@Override
		public NarrativeHelperRetVal findWhatToImport(final Map<String, Integer> narrativeTypes) {
			final NarrativeHelperRetVal retVal = new NarrativeHelperRetVal();
			retVal.narrativeEnum = ImportNarrativeEnum.TRIMMED_DATA;
			retVal.selectedNarrativeTypes = new ArrayList<>();
			retVal.selectedNarrativeTypes.add("OOW COMMENT");
			return retVal;
		}
	};

	private final static TrimNarrativeHelper allow_all = new TrimNarrativeHelper() {

		@Override
		public NarrativeHelperRetVal findWhatToImport(final Map<String, Integer> narrativeTypes) {
			final NarrativeHelperRetVal retVal = new NarrativeHelperRetVal();
			retVal.narrativeEnum = ImportNarrativeEnum.ALL_DATA;
			// retVal.selectedNarrativeTypes =
			// (List<String>)Arrays.asList(narrativeTypes.keySet().toArray(new
			// String[narrativeTypes.size()]));
			return retVal;
		}
	};
	private final static TrimNarrativeHelper cancelled_import = new TrimNarrativeHelper() {

		@Override
		public NarrativeHelperRetVal findWhatToImport(final Map<String, Integer> narrativeTypes) {
			final NarrativeHelperRetVal retVal = new NarrativeHelperRetVal();
			retVal.narrativeEnum = ImportNarrativeEnum.CANCEL;
			return retVal;
		}
	};

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

	private static ArrayList<String> getNarrativeStringsNoMetadata() {
		final ArrayList<String> res = new ArrayList<String>();

		// start with some track data
		res.add("irrelevant preamble 1");
		res.add("irrelevant preamble 2");
		res.add("31 Dec 1995");
		res.add("310504 SR023 SOURCE_A FCS B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.");
		res.add("irrelevant preamble 3");
		res.add("irrelevant preamble 4");
		res.add("01 Jan 1996");
		res.add("010505 SR023 SOURCE_A FCS B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.");
		res.add("010506 irrelevant content 1");
		res.add("010507 SR023 SOURCE_B FCS (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.");
		res.add("010508 irrelevant content 2");
		res.add("010509 SR023 SOURCE_B FCS (AAAA) B-123 R-800yds C-321 S-6kts AAAAAAA. Classified AAAAAA \r BBBBBB AAAAAA.");
		res.add("010608");
		res.add("irrelevant postamble 3");
		res.add("irrelevant postamble 4");

		return res;
	}

	private static String[] getTrackStrings() {
		final ArrayList<String> res = new ArrayList<String>();
		res.add("960101 050000.000 NELSON  @C 22 11 10.63 N 21 41 52.37 W 269.7 2.0 0\n");
		res.add("960101 050100.000 NELSON  @C 22 11 10.58 N 21 42  2.98 W 269.7 2.0 0\n");
		res.add("960101 050200.000 NELSON  @C 22 11 10.51 N 21 42 14.81 W 269.9 2.0 0\n");
		res.add("960101 050300.000 NELSON  @C 22 11 10.51 N 21 42 27.27 W 268.7 2.0 0\n");
		res.add("960101 050400.000 NELSON  @C 22 11 10.28 N 21 42 40.33 W 270.6 2.0 0\n");
		res.add("960101 053500.000 NELSON  @C 22 11 10.39 N 21 42 53.47 W 269.4 2.0 0 \n");
		res.add("960101 053600.000 NELSON  @C 22 11 10.26 N 21 43  6.79 W 269.0 2.0 0 \n");
		res.add("960101 053700.000 NELSON  @C 22 11 10.08 N 21 43 20.34 W 270.5 2.0 0 \n");
		res.add("960101 054800.000 NELSON  @C 22 11 10.18 N 21 43 33.68 W 269.9 2.0 0 \n");
		res.add("960101 055900.000 NELSON  @C 22 11 10.19 N 21 43 47.26 W 268.6 2.0 0\n");

		return res.toArray(new String[] {});
	}

	private static StringBuilder strArrayToStream(final String[] track) {
		final StringBuilder sb = new StringBuilder();
		for (final String s : track) {
			sb.append(s);
		}
		return sb;
	}

	@Test
	public static void testAddFCSToHiddenTrack() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		// ok, now filter it to a time period
		final TrackWrapper track = (TrackWrapper) tLayers.elementAt(0);

		// filter the list to a period of data after the narrative cuts
		track.filterListTo(new HiResDate(818749200000L), new HiResDate(818766600000L));

		// now load the FCS data

		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);

		// hmmm, how many tracks
		assertEquals(3, tLayers.size(), "got new tracks");

		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
		// correct final count
		assertEquals(364, narrLayer.size(), "Got num lines");

		final BaseLayer fcsLayer = (BaseLayer) tLayers.findLayer(NARR_LAYER);

		final Object[] solutions = fcsLayer.getData().toArray();

		// hey, let's have a look them
		LightweightTrackWrapper tw = (LightweightTrackWrapper) solutions[5];
		assertEquals("M01_AAAA AAAA AAA (BBBB)", tw.getName(), "correct name");
		assertEquals(3, tw.numFixes(), "got fixes");

		// hey, let's have a look them
		tw = (LightweightTrackWrapper) solutions[2];
		assertEquals("025_AAAA AAAA AAA (AAAA)", tw.getName(), "correct name");
		assertEquals(5, tw.numFixes(), "got fixes");

		// we need to introduce a 500ms delay, so we don't use
		// the cahced visible period
		Thread.sleep(550);

		final TimePeriod bounds = tw.getVisiblePeriod();
		// in our sample data we have several FCSs at the same time,
		// so we have to increment the DTG (seconds) on successive points.
		// so,the dataset should end at 08:11:01 - since the last point
		// had a second added.
		assertEquals("Period:951212 080800 to 951212 081400", bounds.toString(), "correct bounds:");

		// hey, let's have a look tthem

		final BaseLayer fcsNarr = (BaseLayer) tLayers.findLayer(NARR_LAYER);
		final Object[] data = fcsNarr.getData().toArray();

		tw = (LightweightTrackWrapper) data[3];
		assertEquals("027_AAAA AAAA AAA (AAAA)", tw.getName(), "correct name");
		assertEquals(3, tw.numFixes(), "got fixes");

	}

	@Test
	public static void testAddFCSToTrack() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);

		// hmmm, how many tracks
		assertEquals(3, tLayers.size(), "got new tracks");

		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);

		// correct final count
		assertEquals(364, narrLayer.size(), "Got num lines");

		final BaseLayer sols = (BaseLayer) tLayers.findLayer(NARR_LAYER);
		final Object[] data = sols.getData().toArray();

		// hey, let's have a look them
		LightweightTrackWrapper tw = (LightweightTrackWrapper) data[5];
		assertEquals("M01_AAAA AAAA AAA (BBBB)", tw.getName(), "correct name");
		assertEquals(3, tw.numFixes(), "got fixes");

		// hey, let's have a look them
		tw = (LightweightTrackWrapper) data[2];
		assertEquals("025_AAAA AAAA AAA (AAAA)", tw.getName(), "correct name");
		assertEquals(5, tw.numFixes(), "got fixes");

		// we need to introduce a 500ms delay, so we don't use
		// the cahced visible period
		Thread.sleep(550);

		final TimePeriod bounds = tw.getVisiblePeriod();
		// in our sample data we have several FCSs at the same time,
		// so we have to increment the DTG (seconds) on successive points.
		// so,the dataset should end at 08:11:01 - since the last point
		// had a second added.
		assertEquals("Period:951212 080800 to 951212 081400", bounds.toString(), "correct bounds:");

		// hey, let's have a look tthem
		tw = (LightweightTrackWrapper) data[3];
		assertEquals("027_AAAA AAAA AAA (AAAA)", tw.getName(), "correct name");
		assertEquals(3, tw.numFixes(), "got fixes");

	}

	@Test
	public static void testAdvancedParseBulkFCS() throws ParseException {
		final String str1 = "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_A FCS B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
		final String str1a = "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_B FCS (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
		final String str2 = "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_B FCS (AAAA) B-123 R-800yds C-321 S-6kts AAAAAAA. Classified AAAAAA \r BBBBBB AAAAAA.";
		final String str3 = "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_A FCS B-123 R-800 m C-321 S-6kts AAAAAAA. Classified AAAAAA \nBBBBBB AAAAAA.";
		final String str4 = "160504,16,08,2016,NONSUCH,FCS,  SV023 SOURCE_A FCS B-311\u00b0 R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA.";

		// create mock importer
		final String[] strings = new String[] { str1, str1a, str2, str3, str4 };
		final ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strings));

		final Layers target = new Layers();

		// create the ownship track
		final TrackWrapper nonsuch = new TrackWrapper();
		nonsuch.setName("NONSUCH");

		// we also need fixes covering this period
		final SimpleDateFormat df = new GMTDateFormat("MM/dd/yyyy HH:mm:ss");
		final HiResDate hd1 = new HiResDate(df.parse("08/16/2016 03:00:00"));
		final HiResDate hd2 = new HiResDate(df.parse("08/16/2016 08:00:00"));
		final WorldLocation loc1 = new WorldLocation(1, 1, 0);
		final WorldLocation loc2 = new WorldLocation(2, 2, 0);
		final Fix fx1 = new Fix(hd1, loc1, 12d, 5);
		final Fix fx2 = new Fix(hd2, loc2, 12d, 5);

		nonsuch.addFix(new FixWrapper(fx1));
		nonsuch.addFix(new FixWrapper(fx2));

		target.addThisLayer(nonsuch);

		final ImportNarrativeDocument importer = new ImportNarrativeDocument(target);

		assertEquals(1, target.size(), "one track");

		importer.processThese(strList);

		// check we have two tracks
		assertEquals(3, target.size(), "all tracks");

		// check the size
		final Layer t2 = target.elementAt(2);

		// check t2 is narratives
		assertEquals(NARR_LAYER, t2.getName(), "correct name");
		final BaseLayer layer = (BaseLayer) t2;
		final Editable sol1 = layer.first();
		assertEquals("023_SOURCE_A FCS", sol1.getName(), "correct name");
		final Editable sol2 = layer.last();
		assertEquals("023_SOURCE_A FCS", sol1.getName(), "correct name");
		assertEquals("023_SOURCE_B FCS (AAAA)", sol2.getName(), "correct name");

		// check zero depth in target track
		final LightweightTrackWrapper light = (LightweightTrackWrapper) sol2;
		final FixWrapper first = (FixWrapper) light.getPositionIterator().nextElement();
		assertEquals(0d, first.getDepth(), 0.0001, "fix has zero depth");
	}

	@Test
	public static void testAdvancedParseFCS() throws ParseException {

		final String str1 = "   SR023 SOURCE_A FCS B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
		final String str2 = "SR023 1936 GAINED FCS (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
		final String str3 = "M01 AAAA AAAA AAA (AAAA) B-173 R-3.7kyds C-271 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";

		// high level test of extracting source
		final String match1 = FCSEntry.parseSource(str1);
		assertEquals("SOURCE_A FCS", match1, "got source");

		// check we do our special pattern matching
		final String match2 = FCSEntry.parseSource(str2);
		assertEquals("1936", match2, "got source");

		final String match3 = FCSEntry.parseSource(str3);
		assertEquals("AAAA AAAA AAA (AAAA)", match3, "got source");
	}

	/**
	 * pin down how each style of narrative line is parsed, so the parser can be
	 * restructured safely
	 */
	@Test
	public void testNarrEntryParsing() {
		NarrEntry.reset();
		final String[] lines = new String[] {
				// hidden six-figure DTG, full metadata
				"121005,12,03,20,NONSUCH,SOURCE,Some text here",
				// four-figure DTG, four digit year
				"1106,12,03,2020,NONSUCH,COMMS,Next message",
				// entry at 23xx, hidden day one ahead of the DTG
				"122345,13,03,20,NONSUCH,SOURCE,Late entry",
				// day has gone backwards, without month changing
				"110900,11,03,20,NONSUCH,SOURCE,Mangled date",
				// two-digit year from the 1980s, month increasing
				"010800,01,04,85,OTHER,TYPE,Old year",
				// entry type that is missing its closing comma
				"020900,02,04,85,OTHER,VERYLONGTYPEWITHOUT COMMA and more text,trailing",
				// time-only line, following on from previous entry
				"1015 follow on text for the entry",
				// six digit time-only line, day moving backwards (so next month)
				"011030 C0101 Tgt B-123 continued FCS text",
				// continuation text
				"just some more words for the previous entry",
				// a new-day marker
				"12 Mar 20",
				// time-only line after the day marker
				"1200 after the day marker",
				// too short to be anything
				"1234", };

		final StringBuilder res = new StringBuilder();
		final DateFormat df = new GMTDateFormat("yyyy-MM-dd HH:mm");
		int ctr = 0;
		for (final String line : lines) {
			final NarrEntry entry = NarrEntry.create(line, ++ctr);
			if (entry == null) {
				res.append("null\n");
			} else {
				res.append(entry.dtg == null ? "no-dtg" : df.format(entry.dtg.getDate())).append("|")
						.append(entry.platform).append("|").append(entry.type).append("|").append(entry.text)
						.append("|").append(entry.appendedToPrevious).append("\n");
			}
		}
		NarrEntry.reset();

		assertEquals("2020-03-12 10:05|NONSUCH|SOURCE|Some text here|false\n"
				+ "2020-03-12 11:06|NONSUCH|COMMS|Next message|false\n"
				+ "2020-03-12 23:45|NONSUCH|SOURCE|Late entry|false\n"
				+ "2020-03-12 09:00|NONSUCH|SOURCE|Mangled date|false\n"
				+ "1985-04-01 08:00|OTHER|TYPE|Old year|false\n"
				+ "1985-04-02 09:00|OTHER|VERYLONGTYPEWITHOU|COMMA and more text,trailing|false\n"
				+ "1985-04-02 10:15|OTHER|N/A|follow on text for the entry|false\n"
				+ "1985-05-01 10:30|OTHER|FCS|C0101 Tgt B-123 continued FCS text|false\n"
				+ "no-dtg|null|null|just some more words for the previous entry|true\n" + "null\n"
				+ "2020-03-12 12:00|OTHER|N/A|after the day marker|false\n" + "no-dtg|null|null|1234|true\n",
				res.toString());
	}

	@Test
	public static void testDTGZ_in_preamble() throws UnsupportedEncodingException {
		final Layers layers = new Layers();
		final ImportReplay importer = new ImportReplay();
		final String[] track = getTrackStrings();

		final StringBuilder sb = strArrayToStream(track);

		final ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

		importer.importThis(null, stream, layers);

		assertEquals(1, layers.size(), "have data");

		final ArrayList<String> narr = getNarrativeStringsNoMetadata();

		// inject the DTG (with Z) number
		narr.set(2, "121321Z Some pre-amble text");

		final int index = indexOfStart(narr);
		assertEquals(0, index, "not found start");

		final ImportNarrativeDocument nd = new ImportNarrativeDocument(layers);
		setNarrativeHelper(allow_all);
		nd.processThese(narr);
		setNarrativeHelper(null);

		final NarrativeWrapper narrLayer = (NarrativeWrapper) layers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(5, narrLayer.size(), "have items"); // fewer than 5 - which we get without end-of marker
	}

	@Test
	public static void testFirstWhiteSpace() {
		assertEquals(4, firstWhiteSpace("1234 sdf"));
		assertEquals(4, firstWhiteSpace("1234\tsdf"));
	}

	@Test
	public static void testImportEmptyLayers() throws IOException {
		final String testFile = dummy_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final Layers tLayers = new Layers();

		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);

		// hmmm, how many tracks
		assertEquals(1, tLayers.size(), "got new tracks");

		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.elementAt(0);
		// System.out.println("processed:" + narrLayer.size());

		// hey, let's have a look tthem
		final AbstractCollection<Editable> items = narrLayer.getData();
		final Object[] arr = items.toArray();

		// check array item
		final NarrativeEntry multiLine = (NarrativeEntry) arr[9];
		final String contents = multiLine.getEntry();
		assertEquals(3, countLines(contents), "multi-line entry");

		// correct final count
		assertEquals(13, narrLayer.size(), "Got num lines");
	}

	@Test
	public static void testMissingDateMarkerInNoMetadata() throws UnsupportedEncodingException {
		final Layers layers = new Layers();
		final ImportReplay importer = new ImportReplay();
		final String[] track = getTrackStrings();

		final StringBuilder sb = strArrayToStream(track);

		final ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

		importer.importThis(null, stream, layers);

		assertEquals(1, layers.size(), "have 1 data item");

		final ArrayList<String> narr = getNarrativeStringsNoMetadata();

		// inject the DTG (with Z) number
		narr.remove(6);

		final int index = indexOfStart(narr);
		assertEquals(0, index, "not found start value");

		final ImportNarrativeDocument nd = new ImportNarrativeDocument(layers);
		setNarrativeHelper(only_in_period);
		nd.processThese(narr);
		setNarrativeHelper(null);

		final NarrativeWrapper narrLayer = (NarrativeWrapper) layers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(5, narrLayer.size(), "have 5 items"); // fewer than 5 - which we get without end-of marker
	}

	@Test
	public static void testMMSI_in_preamble() throws UnsupportedEncodingException {
		final Layers layers = new Layers();
		final ImportReplay importer = new ImportReplay();
		final String[] track = getTrackStrings();

		final StringBuilder sb = strArrayToStream(track);

		final ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

		importer.importThis(null, stream, layers);

		assertEquals(1, layers.size(), "have data");

		final ArrayList<String> narr = getNarrativeStringsNoMetadata();

		// inject the MMSI numbers
		narr.set(2, "12632144 Some pre-able text");
		narr.set(9, "12632144 Some pre-able text");

		final int index = indexOfStart(narr);
		assertEquals(0, index, "not found start");

		final ImportNarrativeDocument nd = new ImportNarrativeDocument(layers);
		setNarrativeHelper(allow_all);
		nd.processThese(narr);
		setNarrativeHelper(null);

		final NarrativeWrapper narrLayer = (NarrativeWrapper) layers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(4, narrLayer.size(), "have items"); // fewer than 5 - which we get without end-of marker
	}

	@Test
	public static void testNameHandler() {
		final Layers layers = new Layers();
		final TrackWrapper track = new TrackWrapper();
		track.setName("Nelson");
		layers.addThisLayer(track);
		final TrackWrapper track2 = new TrackWrapper();
		track2.setName("Iron Duck");
		layers.addThisLayer(track2);
		final ImportNarrativeDocument iw = new ImportNarrativeDocument(layers);
		String match = iw.trackFor("HMS Boat", "HMS Boat");
		assertNull(match, "not found match");
		match = iw.trackFor("HMS Nelson", "HMS Nelson");
		assertNotNull(match, "found match");
		match = iw.trackFor("Hms Nelson", "Hms Nelson");
		assertNotNull(match, "found match");
		match = iw.trackFor("RNAS Nelson", "RNAS Nelson");
		assertNotNull(match, "found match");

		// check we've created new entries
		assertEquals(3, iw.nameMatches.size(), "name matches");

		// and the two word name
		match = iw.trackFor("Hms Iron Duck", "Hms Iron Duck");
		assertNotNull(match, "found match");

		// check we've created new entries
		assertEquals(4, iw.nameMatches.size(), "name matches");

	}

	@Test
	public static void testNoMetadata() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		final String testFile = no_metadata_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);

		// hmmm, how many tracks
		assertEquals(3, tLayers.size(), "got new tracks");

		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);

		// correct final count
		assertEquals(350, narrLayer.size(), "Got num lines");

		final BaseLayer sols = (BaseLayer) tLayers.findLayer(NARR_LAYER);
		final Object[] data = sols.getData().toArray();

		// hey, let's have a look them
		LightweightTrackWrapper tw = (LightweightTrackWrapper) data[5];
		assertEquals("M01_AAAA AAAA AAA (BBBB)", tw.getName(), "correct name");
		assertEquals(3, tw.numFixes(), "got fixes");

		// hey, let's have a look them
		tw = (LightweightTrackWrapper) data[2];
		assertEquals("025_AAAA AAAA AAA (AAAA)", tw.getName(), "correct name");
		assertEquals(5, tw.numFixes(), "got fixes");

		// we need to introduce a 500ms delay, so we don't use
		// the cahced visible period
		Thread.sleep(550);

		final TimePeriod bounds = tw.getVisiblePeriod();
		// in our sample data we have several FCSs at the same time,
		// so we have to increment the DTG (seconds) on successive points.
		// so,the dataset should end at 08:11:01 - since the last point
		// had a second added.
		assertEquals("Period:951212 080800 to 951212 081400", bounds.toString(), "correct bounds:");

		// hey, let's have a look tthem
		tw = (LightweightTrackWrapper) data[3];
		assertEquals("027_AAAA AAAA AAA (AAAA)", tw.getName(), "correct name");
		assertEquals(3, tw.numFixes(), "got fixes");

	}

	@Test
	public static void testNoMetadata1() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		final String testFile = no_metadata_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

	}

	@Test
	public static void testNoMetadata2() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		final String testFile = no_metadata_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);

		// hmmm, how many tracks
		assertEquals(3, tLayers.size(), "got new tracks");

		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);

		// correct final count
		assertEquals(350, narrLayer.size(), "Got num lines");

	}

	@Test
	public static void testParseDate() {

		final String goodDate = "000000";
		assertTrue(goodDate.matches(DATE_MATCH_SIX), "date");
		assertFalse("Notes:".matches(DATE_MATCH_SIX), "not date");

		final String testDate1 = "160909,16,09,2016,HMS NONSUCH, CAT COMMENT, SOME COMMENT ";

		// ok, get the narrative type
		final NarrEntry thisN1 = NarrEntry.create(testDate1, 1);
		final Date date = thisN1.dtg.getDate();
		final Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		instance.setTime(date);
		assertEquals(2016, instance.get(Calendar.YEAR), "year");
		assertEquals(8, instance.get(Calendar.MONTH), "month");
		assertEquals(16, instance.get(Calendar.DAY_OF_MONTH), "day");
		// removed the next line - it's failing on the CI build,
		// because of a timezone difference

		assertEquals(9, instance.get(Calendar.HOUR_OF_DAY), "hour"); // not 9, since
		// we're BST
		assertEquals(9, instance.get(Calendar.MINUTE), "min");
		assertEquals(0, instance.get(Calendar.SECOND), "sec");
		assertEquals("HMS NONSUCH", thisN1.platform, "platform");
		assertEquals("SOME COMMENT", thisN1.text, "content");

		// ok, now one with mangled (missing) date fields
		final String testDate2 = "161006\tSOME COMMENT 2 ";
		// ok, get the narrative type
		final NarrEntry thisN2 = NarrEntry.create(testDate2, 1);
		final Date date2 = thisN2.dtg.getDate();
		final Calendar instance2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		instance2.setTime(date2);
		assertEquals(2016, instance2.get(Calendar.YEAR), "year");
		assertEquals(8, instance2.get(Calendar.MONTH), "month");
		assertEquals(16, instance2.get(Calendar.DAY_OF_MONTH), "day");
		// removed the next line - it's failing on the CI build,
		// because of a timezone difference
		assertEquals(10, instance2.get(Calendar.HOUR_OF_DAY), "hour"); // not 10, we're
		// BST
		assertEquals(6, instance2.get(Calendar.MINUTE), "min");
		assertEquals(0, instance2.get(Calendar.SECOND), "sec");
		assertEquals("HMS NONSUCH", thisN2.platform, "platform");
		assertEquals("SOME COMMENT 2", thisN2.text, "content");
		assertFalse(thisN2.appendedToPrevious, "flag");

		// hey, what if it's just text?
		final String testDate3 = "SOME COMMENT ";
		// ok, get the narrative type
		final NarrEntry thisN3 = NarrEntry.create(testDate3, 1);

		// ok, should just be that text
		assertNull(thisN3.dtg, "year");
		assertNull(thisN3.platform, "platform");
		assertNotNull(thisN3.text, "content");
		assertTrue(thisN3.appendedToPrevious, "flag");

	}

	@Test
	public static void testParseDateFCS() throws ParseException {
		final String date = "12 Nov 2018";
		final NarrEntry dateLine = new NarrEntry(date);
		assertNotNull(dateLine);
		assertNull(dateLine.dtg);
		assertNull(dateLine.text);
		final String line = "120506 irrelevant content 1";
		final NarrEntry ne = new NarrEntry(line);
		System.out.println(ne.dtg.getDate());
		assertNotNull(ne);
		assertNotNull(ne.dtg);
		assertNotNull(ne.text);
	}

	@Test
	public static void testParseFCS() throws ParseException {
		final String str1 = "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";

		final String str2 = "160403,16,09,2016,NONSUCH,FCS, M01 1234 Rge B-311\u00b0 R-12600 yds. Classified AAAAAA CCCCCC AAAAAA.";

		final String str3 = "160403,16,09,2016,NONSUCH,FCS, M02 1234 Rge B-311\u00b0 R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA. Source from S333.";

		final String str4 = "160403,16,09,2016,NONSUCH,FCS, M02 1234 Rge R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA. Source from S333.";

		// try our special identifier
		assertEquals(123d, FCSEntry.getElement("B-", str1), "first bearing");
		assertEquals(321d, FCSEntry.getElement("C-", str1), "first course");
		assertEquals(5d, FCSEntry.getElement("R-", str1), "first range");
		assertEquals(6d, FCSEntry.getElement("S-", str1), "first speed");

		assertEquals(311d, FCSEntry.getElement("B-", str2), "second bearing");
		assertEquals(12600d, FCSEntry.getElement("R-", str2), "second range");

		assertEquals("AAAAAA BBBBBB AAAAAA.", FCSEntry.getClassified(str1), "correct classified");

		NarrEntry ne = new NarrEntry(str1);
		final FCSEntry fe1 = new FCSEntry(ne.text);
		assertEquals(5000d, fe1.rangYds, 0.001, "got range:");
		assertEquals(123d, fe1.brgDegs, "got brg:");
		assertEquals("023", fe1.contact, "got contact:");
		assertEquals(321d, fe1.crseDegs, "got course:");
		assertEquals(6d, fe1.spdKts, "got speed:");
		assertEquals("AAAAAA BBBBBB AAAAAA.", fe1.tgtType, "got name:");

		ne = new NarrEntry(str2);
		final FCSEntry fe2 = new FCSEntry(ne.text);
		assertEquals(12600d, fe2.rangYds, 0.001, "got range:");
		assertEquals(311d, fe2.brgDegs, "got brg:");
		assertEquals("M01", fe2.contact, "got contact:");
		assertEquals(0d, fe2.crseDegs, "got course:");
		assertEquals(0d, fe2.spdKts, "got speed:");
		assertEquals("AAAAAA CCCCCC AAAAAA.", fe2.tgtType, "got name:");

		ne = new NarrEntry(str3);
		final FCSEntry fe3 = new FCSEntry(ne.text);
		assertEquals("M02", fe3.contact, "processed master id before other id:");

		ne = new NarrEntry(str4);
		final FCSEntry fe4 = new FCSEntry(ne.text);
		assertNull(fe4.brgDegs, "empty bearing");

	}

	@Test
	public static void testParseFCSRange() throws ParseException {
		final String str1 = "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
		final String str1a = "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
		final String str2 = "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-800yds C-321 S-6kts AAAAAAA. Classified AAAAAA \r BBBBBB AAAAAA.";
		final String str3 = "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-800 m C-321 S-6kts AAAAAAA. Classified AAAAAA \nBBBBBB AAAAAA.";
		final String str4 = "160403,16,09,2016,NONSUCH,FCS, M01 1234 Rge B-311\u00b0 R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA.";

		assertEquals(5.1, FCSEntry.getRange(str1).getValueIn(WorldDistance.KYDS), 0.1, "got kyds");
		assertEquals(5, FCSEntry.getRange(str1a).getValueIn(WorldDistance.KYDS), 0.1, "got kyds");
		assertEquals(800, FCSEntry.getRange(str2).getValueIn(WorldDistance.YARDS), 0.1, "got yds");
		assertEquals(800, FCSEntry.getRange(str3).getValueIn(WorldDistance.METRES), 0.1, "got m");
		assertEquals(12.4, FCSEntry.getRange(str4).getValueIn(WorldDistance.KYDS), 0.1, "got kyds");
	}

	@Test
	public static void testParseFCSWithSameTime() throws ParseException {
		final String str1 = "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_A FCS B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
		final String str1a = "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_B FCS (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
		final String str2 = "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_B FCS (AAAA) B-123 R-800yds C-321 S-6kts AAAAAAA. Classified AAAAAA \r BBBBBB AAAAAA.";
		final String str3 = "160505,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_A FCS B-123 R-800 m C-321 S-6kts AAAAAAA. Classified AAAAAA \nBBBBBB AAAAAA.";
		final String str4 = "160505,16,08,2016,NONSUCH,FCS,  SV023 SOURCE_B FCS (AAAA) B-311\u00b0 R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA.";

		// create mock importer
		final String[] strings = new String[] { str1, str1a, str2, str3, str4 };
		final ArrayList<String> strList = new ArrayList<String>(Arrays.asList(strings));

		final Layers target = new Layers();

		// create the ownship track
		final TrackWrapper nonsuch = new TrackWrapper();
		nonsuch.setName("NONSUCH");

		// we also need fixes covering this period
		final SimpleDateFormat df = new GMTDateFormat("MM/dd/yyyy HH:mm:ss");
		final HiResDate hd1 = new HiResDate(df.parse("08/16/2016 04:00:00"));
		final HiResDate hd2 = new HiResDate(df.parse("08/16/2016 06:00:00"));
		final WorldLocation loc1 = new WorldLocation(1, 1, 0);
		final WorldLocation loc2 = new WorldLocation(2, 2, 0);
		final Fix fx1 = new Fix(hd1, loc1, 12d, 5);
		final Fix fx2 = new Fix(hd2, loc2, 12d, 5);

		nonsuch.add(new FixWrapper(fx1));
		nonsuch.add(new FixWrapper(fx2));

		target.addThisLayer(nonsuch);

		final ImportNarrativeDocument importer = new ImportNarrativeDocument(target);

		assertEquals(1, target.size(), "one track");

		importer.processThese(strList);

		// check we have two tracks
		assertEquals(3, target.size(), "all tracks");

		final BaseLayer narrs = (BaseLayer) target.findLayer(NARR_LAYER);

		// check the size
		final LightweightTrackWrapper t1 = (LightweightTrackWrapper) narrs.first();
		final LightweightTrackWrapper t2 = (LightweightTrackWrapper) narrs.last();

		assertEquals("023_SOURCE_A FCS", t1.getName(), "correct name");
		assertEquals("023_SOURCE_B FCS (AAAA)", t2.getName(), "correct name");

		assertEquals(2, t1.numFixes(), "correct length");
		assertEquals(3, t2.numFixes(), "correct length");
	}

	@Test
	public static void testParseTrackNumber() {
		final String str1 = "asdfads S000 adf ag a";
		final String str1a = "asdfads S000 adf ag a";
		final String str2 = "asdfads SV000 adf ag a";
		final String str2a = "asdfads M00 adf ag a";
		final String str3 = "asdfads adf ag a";
		final String str5 = "M00 0000";

		assertEquals("000", FCSEntry.parseTrack(str1), "right id");
		assertEquals("000", FCSEntry.parseTrack(str1a), "right id");
		assertEquals("000", FCSEntry.parseTrack(str2), "right id");
		assertEquals("M00", FCSEntry.parseTrack(str2a), "right id");
		assertEquals("M00", FCSEntry.parseTrack(str5), "right id");
		assertNull(FCSEntry.parseTrack(str3), "right id");
	}

	@Test
	public static void testSingleTrack() {
		final Layers layers = new Layers();
		assertFalse(singleTrackIn(layers), "no tracks");

		final BaseLayer theLayer = new BaseLayer();
		theLayer.setName("a");
		layers.addThisLayer(theLayer);
		assertFalse(singleTrackIn(layers), "no tracks");

		final TrackWrapper theTrack = new TrackWrapper();
		theTrack.setName("t1");
		layers.addThisLayer(theTrack);
		assertTrue(singleTrackIn(layers), "one track");

		final BaseLayer theLayer2 = new BaseLayer();
		theLayer2.setName("b");
		layers.addThisLayer(theLayer2);
		assertTrue(singleTrackIn(layers), "one track");

		final TrackWrapper theTrack2 = new TrackWrapper();
		theTrack2.setName("t2");
		layers.addThisLayer(theTrack2);
		assertFalse(singleTrackIn(layers), "two track");
	}

	@Test
	public static void testSpanningYear() throws InterruptedException, IOException {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");

		// we've also got to change the date of the very last entry on the track,
		// so that we can load narrative entries that go past the end of the track
		// (into the next year).
		final TrackWrapper trk = (TrackWrapper) tLayers.elementAt(0);
		final FixWrapper lastFix = (FixWrapper) trk.getNearestTo(trk.getEndDTG())[0];
		lastFix.setDateTimeGroup(new HiResDate(new Date()));

		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);

		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);

		final NarrativeWrapper narr = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertEquals(371, narr.size(), "Got num lines");
	}

	@Test
	public static void testStartOfAndEndOfPresent() throws UnsupportedEncodingException {
		final Layers layers = new Layers();
		final ImportReplay importer = new ImportReplay();
		final String[] track = getTrackStrings();

		final StringBuilder sb = strArrayToStream(track);

		final ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

		importer.importThis(null, stream, layers);

		assertEquals(1, layers.size(), "have data");

		final ArrayList<String> narr = getNarrativeStringsNoMetadata();

		// inject the start of records line
		narr.set(5, START_OF_RECORDS_1 + " FOR SOME EXERCISE");
		narr.set(9, END_OF_RECORDS_2 + " FOR SOME EXERCISE");

		final int index = indexOfStart(narr);
		assertEquals(5, index, "found start");

		final ImportNarrativeDocument nd = new ImportNarrativeDocument(layers);
		setNarrativeHelper(allow_all);
		nd.processThese(narr);
		setNarrativeHelper(null);

		final NarrativeWrapper narrLayer = (NarrativeWrapper) layers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(2, narrLayer.size(), "have items"); // fewer than 5 - which we get without end-of marker
	}

	@Test
	public static void testStartOfNotPresent() throws UnsupportedEncodingException {
		final Layers layers = new Layers();
		final ImportReplay importer = new ImportReplay();
		final String[] track = getTrackStrings();

		final StringBuilder sb = strArrayToStream(track);

		final ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

		importer.importThis(null, stream, layers);

		assertEquals(1, layers.size(), "have data");

		final ArrayList<String> narr = getNarrativeStringsNoMetadata();

		final ImportNarrativeDocument nd = new ImportNarrativeDocument(layers);
		setNarrativeHelper(allow_all);
		nd.processThese(narr);
		setNarrativeHelper(null);

		final NarrativeWrapper narrLayer = (NarrativeWrapper) layers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(6, narrLayer.size(), "have items");
	}

	@Test
	public static void testStartOfPresent() throws UnsupportedEncodingException {
		final Layers layers = new Layers();
		final ImportReplay importer = new ImportReplay();
		final String[] track = getTrackStrings();

		final StringBuilder sb = strArrayToStream(track);

		final ByteArrayInputStream stream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

		importer.importThis(null, stream, layers);

		assertEquals(1, layers.size(), "have data");

		final ArrayList<String> narr = getNarrativeStringsNoMetadata();

		// inject the start of records line
		narr.set(5, START_OF_RECORDS_2 + " FOR SOME EXERCISE");

		final int index = indexOfStart(narr);
		assertEquals(5, index, "found start");

		final ImportNarrativeDocument nd = new ImportNarrativeDocument(layers);
		setNarrativeHelper(allow_all);
		nd.processThese(narr);
		setNarrativeHelper(null);

		final NarrativeWrapper narrLayer = (NarrativeWrapper) layers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(5, narrLayer.size(), "have items");
	}

	private final TrimNarrativeHelper only_fcs_types = new TrimNarrativeHelper() {

		@Override
		public NarrativeHelperRetVal findWhatToImport(final Map<String, Integer> narrativeTypes) {
			final NarrativeHelperRetVal retVal = new NarrativeHelperRetVal();
			retVal.narrativeEnum = ImportNarrativeEnum.ALL_DATA;
			retVal.selectedNarrativeTypes = new ArrayList<>();
			retVal.selectedNarrativeTypes.add("FCS");
			return retVal;
		}
	};

	@SuppressWarnings("unused")
	private String messageStr = null;

	@BeforeEach
	public void setUp() {
		// clear the message string
		messageStr = null;

		// clear the static variables in NarrativeEntry
		NarrEntry.reset();

		// initialise the message provider
		MessageProvider.Base.setProvider(new MessageProvider() {

			@Override
			public void show(final String title, final String message, final int status) {
				messageStr = message;
			}
		});
		setNarrativeHelper(null);
	}

	@Test
	public void testCancelImportNarrativeTypes() throws Exception {
		final String testFile = dummy_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);
		final Layers tLayers = new Layers();
		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		ImportNarrativeDocument.setNarrativeHelper(cancelled_import);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);
		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNull(narrLayer);
	}

	/**
	 * A page tree whose /Kids contains the /Pages node itself. PDFBox 2.0.3 recursed
	 * until StackOverflowError here (CR-022); current 2.0.x detects the loop.
	 */
	@Test
	public void testImportFromPdfSelfReferencingPageTree() throws Exception {
		final String[] objs = { "<< /Type /Catalog /Pages 2 0 R >>",
				"<< /Type /Pages /Kids [2 0 R 3 0 R] /Count 2 >>",
				"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] >>" };
		final StringBuilder sb = new StringBuilder("%PDF-1.4\n");
		final int[] offsets = new int[objs.length];
		for (int i = 0; i < objs.length; i++) {
			offsets[i] = sb.length();
			sb.append(i + 1).append(" 0 obj\n").append(objs[i]).append("\nendobj\n");
		}
		final int xref = sb.length();
		sb.append("xref\n0 ").append(objs.length + 1).append("\n0000000000 65535 f \n");
		for (final int offset : offsets) {
			sb.append(String.format("%010d 00000 n \n", offset));
		}
		sb.append("trailer\n<< /Size ").append(objs.length + 1).append(" /Root 1 0 R >>\nstartxref\n")
				.append(xref).append("\n%%EOF\n");
		final InputStream is = new ByteArrayInputStream(sb.toString().getBytes("ISO-8859-1"));

		// must return rather than overflow the stack
		final ArrayList<String> strings = importFromPdf("loop.pdf", is);
		assertNotNull(strings);
	}

	@Test
	public void testImportAllNarrativeTypes() throws Exception {
		final String testFile = dummy_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);
		final Layers tLayers = new Layers();
		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);
		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(narrLayer.size(), 13);
	}

	@Test
	public void testImportSelectedNarrativeFCSTypes() throws Exception {
		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);
		final Layers tLayers = new Layers();
		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		ImportNarrativeDocument.setNarrativeHelper(only_fcs_types);
		final List<String> selectedTypes = new ArrayList<>();
		selectedTypes.add("FCS");
		importer.setSelectedNarrativeTypes(selectedTypes);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);
		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(narrLayer.size(), 18);

	}

	@Test
	public void testImportSelectedNarrativeNonFCSTypes() throws Exception {
		final String testFile = dummy_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);
		final Layers tLayers = new Layers();
		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		ImportNarrativeDocument.setNarrativeHelper(only_selected_types);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);
		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(narrLayer.size(), 5);
	}

	@Test
	public void testImportSelectedTypesWithOuterPeriod() throws Exception {
		final Layers tLayers = new Layers();

		// start off with the ownship track
		final File boatFile = new File(ownship_track_test);
		assertTrue(boatFile.exists());
		final InputStream bs = new FileInputStream(boatFile);

		final ImportReplay trackImporter = new ImportReplay();
		ImportReplay.initialise(new ImportReplayTest.TestParent(ImportReplay.IMPORT_AS_OTG, 0L));
		trackImporter.importThis(ownship_track_test, bs, tLayers);

		assertEquals(1, tLayers.size(), "read in track");
		final String testFile = valid_doc_path;
		final File testI = new File(testFile);
		assertTrue(testI.exists());

		final InputStream is = new FileInputStream(testI);
		final ImportNarrativeDocument importer = new ImportNarrativeDocument(tLayers);
		ImportNarrativeDocument.setNarrativeHelper(only_selected_trimmed_data_types);
		final HWPFDocument doc = new HWPFDocument(is);
		final ArrayList<String> strings = importFromWord(doc);
		importer.processThese(strings);
		final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
		assertNotNull(narrLayer);
		assertEquals(narrLayer.size(), 285);

	}
}
