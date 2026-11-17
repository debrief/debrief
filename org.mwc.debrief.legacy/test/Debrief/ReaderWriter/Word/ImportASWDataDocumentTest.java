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

import static Debrief.ReaderWriter.Word.ImportASWDataDocument.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Word.ImportASWDataDocument.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class ImportASWDataDocumentTest {
	private class MyHelper implements ImportNarrativeDocument.QuestionHelper {

		private final String _answer;

		public MyHelper(final String answer) {
			_answer = answer;
		}

		@Override
		public String askQuestion(final String title, final String message, final String defaultStr) {
			questionStore = defaultStr;
			return _answer;
		}

		@Override
		public boolean askYes(final String title, final String message) {
			return false;
		}

		@Override
		public void showMessage(final String title, final String message) {
			answerStore = message;
		}

		@Override
		public void showMessageWithLogButton(final String title, final String message) {
			answerStore = message;
		}

	}

	private static final String NORWICH = "NORWICH";

	private static List<String> incrementTimes(final List<String> lines) {
		final List<String> res = new ArrayList<String>();
		for (final String t : lines) {
			if (isValid(t)) {
				res.add(t.replace("APR", "JUN"));
			} else {
				res.add(t);
			}
		}
		return res;
	}

	@SuppressWarnings("unused")
	private static WorldLocation loc(final double lat, final double lon, final double dep) {
		return new WorldLocation(lat, lon, dep);
	}

	private String answerStore = null;

	private String questionStore = null;

	@Test
	public void testCleanString() {
		assertEquals("0000", clean("00O0"));
		assertEquals("0000", clean("00o0"));
		assertEquals("0000", clean("00o0 "));
		assertEquals("0000", clean(" 00o0"));
		assertEquals("0000", clean(" 0Oo0"));
		assertEquals("0A00", clean("0ao0 "));
		assertEquals("1.234", clean("1,234"));
	}

	@Test
	public void testCourse() {
		assertEquals(MWC.Algorithms.Conversions.Degs2Rads(123.0), courseFor("123T"));
		assertEquals(MWC.Algorithms.Conversions.Degs2Rads(0.0), courseFor("000T"));
	}

	@Test
	public void testDates() {
		final String d1 = "261200ZAPR";
		final String d2 = "262327ZAPR";
		final String d3 = "220300ZJAN";
		final String d4 = "012345ZAPR";
		final String d5 = "292258ZNOV";

		// special test handling. In the absence of a year value, we use the
		// current year. This will change as time moves forward. So,
		// we need to calculate the year value

		final int year = LocalDateTime.now().getYear();
		final String yearStr = "" + (year - 2000);

		assertEquals(yearStr + "0426 120000", DebriefFormatDateTime.toString(dateFor(d1, 0).getDate().getTime()),
				"aaa");
		assertEquals(yearStr + "0426 232700", DebriefFormatDateTime.toString(dateFor(d2, 0).getDate().getTime()),
				"aaa");
		assertEquals(yearStr + "0122 030000", DebriefFormatDateTime.toString(dateFor(d3, 0).getDate().getTime()),
				"aaa");
		assertEquals(yearStr + "0401 234500", DebriefFormatDateTime.toString(dateFor(d4, 0).getDate().getTime()),
				"aaa");
		assertEquals(yearStr + "1129 225800", DebriefFormatDateTime.toString(dateFor(d5, 0).getDate().getTime()),
				"aaa");
	}

	@Test
	public void testFewFailures() {
		final List<String> lines = new ArrayList<String>();

		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("TMPOS/261200ZAPR/IN/00.01.0N-000.21.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261202ZAPR/IN/00.02.0N-000.22.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261204ZAPR/IN/00.03.0N-000.23.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261205ZAPR/IN/00.04.0N-000.24.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261a206ZAPR/IN/00.05.0N-000.25.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261207ZAPR/IN/00.06.0N-000.27.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261208ZAPR/IN/00.07.0N-000.28.0W/05b7T/04KTS/01.0M//");
		lines.add("TMPOS/261209ZAPR/IN/00.08.0N-000.31.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261210ZAPR/IN/00.09.0N-000.33.0W/057T/04KbTS/01.0M//");
		lines.add("TMPOS/261211ZAPR/IN/00.10.0N-000.35.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261212ZAPR/INS/00.10.30N-000.34.30W/180T/13KTS/000FT");
		lines.add("TMPOS/261214ZAPR/INS/00.10.40N-000.34.00W/189T/12KTS/004FT");

		final Layers layers = new Layers();
		final ImportASWDataDocument iw = new ImportASWDataDocument(layers);
		final String trackName = "track";

		// start off with user cancelling import
		ImportASWDataDocument.setQuestionHelper(new MyHelper(trackName));

		answerStore = null;
		iw.processThese(lines);

		assertTrue(layers.size() == 1, "layers should not be empty");
		final TrackWrapper track = (TrackWrapper) layers.findLayer(trackName);

		assertNotNull(track, "found track");

		assertEquals(9, track.numFixes(), "correct length");

		// check the message
		assertEquals("Import completed, with errors. Errors at lines 9, 11, 13.\nSee Error Log (below) for more.",
				answerStore,
				"Good string");
	}

	@Test
	public void testGetHemi() {
		final String d1 = "00.00.0N";
		final String d2 = "000.00.00S";
		final String d3 = "dddmmW";
		final String d4 = "00.00.00E";
		final String d5 = "000.00. 00W";

		assertEquals(1d, getHemiFor(d1));
		assertEquals(-1d, getHemiFor(d2));
		assertEquals(-1d, getHemiFor(d3));
		assertEquals(1d, getHemiFor(d4));
		assertEquals(-1d, getHemiFor(d5));
	}

	@Test
	public void testGetValue() {
		final String d1 = "12.30.45";
		final String d2 = "011.30.45";
		final String d3a = "11545";
		final String d3b = "1545";
		final String d4 = "22.30.45";
		final String d5 = "04 .30. 45";
		// repeat of last line, but with spaces removed
		final String d6 = "104.30.45";
		final String d7 = "O11.3o.45";

		assertEquals(12.5125d, getDegreesFor(d1, false));
		assertEquals(11.5125d, getDegreesFor(d2, false));
		assertEquals(115.75, getDegreesFor(d3a, false));
		assertEquals(15.75, getDegreesFor(d3b, false));
		assertEquals(22.5125, getDegreesFor(d4, false));
		assertEquals(4.5125, getDegreesFor(d5, false));
		assertEquals(104.5125, getDegreesFor(d6, false));
		assertEquals(11.5125d, getDegreesFor(d7, false));
	}

	@Test
	public void testGetValue_DecimalMinutes() {
		final String d1 = "12.30.50";
		final String d1a = "12.30.30";

		final String d2 = "011.30.75";
		final String d2a = "011.30.45";

		final boolean MMmm = true;
		final boolean MMSS = false;

		assertEquals(getDegreesFor(d1, MMmm), getDegreesFor(d1a, MMSS));
		assertEquals(getDegreesFor(d2, MMmm), getDegreesFor(d2a, MMSS));

		try {
			final String d3 = "011.30.75";
			getDegreesFor(d3, MMSS);
			fail("should have thrown exception");
		} catch (final IllegalArgumentException ie) {
			assertEquals(LOCATION_FORMAT_WRONG + "011.30.75", ie.getMessage());
		}
	}

	@Test
	public void testIsValid() {
		final List<String> input = new ArrayList<String>();
		for (int i = 0; i < 20; i++) {
			input.add("Some old duff information");
		}
		assertFalse(canImport(input), "not suitable for import");
		for (int i = 0; i < 10; i++) {
			input.add("TMPOS/Here we go!");
		}
		assertTrue(canImport(input), "now suitable for import");

	}

	@Test
	public void testManyFailures() {
		final List<String> lines = new ArrayList<String>();

		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("TMPOS/261200ZAPR/IN/00.01.0N-000.21.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261a202ZAPR/IN/00.02.0N-000.22.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261a204ZAPR/IN/00.03.0N-000.23.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261a205ZAPR/IN/00.04.0N-000.24.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261a206ZAPR/IN/00.05.0N-000.25.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261207ZAPR/IN/00.06.0N-000.27.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261208ZAPR/IN/00.07.0N-000.28.0W/05b7T/04KTS/01.0M//");
		lines.add("TMPOS/261209ZAPR/IN/00.08.0N-000.31.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261210ZAPR/IN/00.09.0N-000.33.0W/057T/04KbTS/01.0M//");
		lines.add("TMPOS/261d211ZAPR/IN/00.10.0N-000.35.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261d212ZAPR/INS/00.10.30N-000.34.30W/180T/13KTS/000FT");
		lines.add("TMPOS/261214ZAPR/INS/00.10.40N-000.34.00W/189T/12KTS/004FT");

		final Layers layers = new Layers();
		final ImportASWDataDocument iw = new ImportASWDataDocument(layers);
		final String trackName = "track";

		// start off with user cancelling import
		ImportASWDataDocument.setQuestionHelper(new MyHelper(trackName));

		answerStore = null;
		iw.processThese(lines);

		assertTrue(layers.size() == 1, "layers should not be empty");
		final TrackWrapper track = (TrackWrapper) layers.findLayer(trackName);

		assertNotNull(track, "found track");

		assertEquals(4, track.numFixes(), "correct length");

		// check the message
		assertEquals("Import completed, with errors. Errors on 8 lines.\nSee Error Log (below) for more.",
				answerStore, "Good string");
	}

	@Test
	public void testParseDates() {
		final String d1 = "TIMPD/290000Z/300200Z/APR/2016/APR2016";
		final String d2 = "TIMPD//230001Z//232359Z//APR//2017//APR//2017//";
		final String d3 = "TIMPD/220000Z/222400Z/APR/2018";
		final String d4 = "TIMPD/261200ZAPR/IN/262359ZAPR/IN/APR/2019/APR/2019/";

		// start off with good ones
		assertEquals(2016, yearFor(d1).intValue(), "date 1");
		assertEquals(2017, yearFor(d2).intValue(), "date 2");
		assertEquals(2018, yearFor(d3).intValue(), "date 3");
		assertEquals(2019, yearFor(d4).intValue(), "date 4");

		assertNull(yearFor("TMPOS/290000Z/300200Z/APR/2016/APR2016"), "wrong format");
		assertNull(yearFor("TMPOS/290000Z/300200Z/APR/201A6/APR2016"), "no year");
		assertNull(yearFor("TMPOS/290000Z/300200Z/APR/16/APR2016"), "too early");
		assertNull(yearFor("TMPOS/290000Z/300200Z/APR/124316/APR2016"), "too late");
	}

	@Test
	public void testParseDocument() {
		final List<String> lines = new ArrayList<String>();

		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("TMPOS/261200ZAPR/IN/00.01.0N-000.21.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261202ZAPR/IN/00.02.0N-000.22.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261204ZAPR/IN/00.03.0N-000.23.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261205ZAPR/IN/00.04.0N-000.24.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261206ZAPR/IN/00.05.0N-000.25.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261207ZAPR/IN/00.06.0N-000.27.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261208ZAPR/IN/00.07.0N-000.28.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261209ZAPR/IN/00.08.0N-000.31.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261210ZAPR/IN/00.09.0N-000.33.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261211ZAPR/IN/00.10.0N-000.35.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261212ZAPR/INS/00.10.30N-000.34.30W/180T/13KTS/000FT");
		lines.add("TMPOS/261214ZAPR/INS/00.10.40N-000.34.00W/189T/12KTS/004FT");

		final Layers layers = new Layers();
		final ImportASWDataDocument iw = new ImportASWDataDocument(layers);

		// start off with user cancelling import
		ImportASWDataDocument.setQuestionHelper(new MyHelper(null));

		iw.processThese(lines);

		assertTrue(layers.size() == 0, "layers should be empty");

		questionStore = null;
		_lastTrackName = null;

		ImportASWDataDocument.setQuestionHelper(new MyHelper(NORWICH));

		iw.processThese(lines);

		assertEquals(DEFAULT_TRACK_MESSAGE, questionStore, "we should have stored the default str");

		assertTrue(layers.size() == 1, "We should have a track");
		final TrackWrapper track = (TrackWrapper) layers.findLayer(NORWICH);

		// check it has some data
		final TrackSegment segment = (TrackSegment) track.getSegments().elements().nextElement();
		assertEquals(12, segment.size(), "has fixes");
		assertEquals(NORWICH, track.getName());

		// add another track

		ImportASWDataDocument.setQuestionHelper(new MyHelper("DULWICH"));

		iw.processThese(lines);
		assertTrue(layers.size() == 2, "We should have multiple tracks");

		assertEquals("NORWICH", questionStore, "we should been offered the new default");

		final List<String> lines2 = incrementTimes(lines);
		ImportASWDataDocument.setQuestionHelper(new MyHelper(NORWICH));

		iw.processThese(lines2);
		final TrackSegment segment2 = (TrackSegment) track.getSegments().elements().nextElement();
		assertEquals(24, segment2.size(), "has fixes");
	}

	@Test
	public void testParseDocument_mm_mm() {
		final List<String> lines = new ArrayList<String>();

		// NOTE: the third element of the posits are all under 59,
		// so we presume seconds

		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("TMPOS/261200ZAPR/IN/00.01.00N-000.21.00W/057T/04KTS/01.0FT//");
		lines.add("TMPOS/261202ZAPR/IN/00.02.00N-000.22.00W/057T/04KTS/02.0FT//");
		lines.add("TMPOS/261204ZAPR/IN/00.03.00N-000.23.00W/057T/04KTS/02.0FT//");
		lines.add("TMPOS/261205ZAPR/IN/00.04.00N-000.24.00W/057T/04KTS/02.0FT//");
		lines.add("TMPOS/261206ZAPR/IN/00.05.00N-000.25.00W/057T/04KTS/01.0FT//");
		lines.add("TMPOS/261207ZAPR/IN/00.06.00N-000.27.00W/057T/04KTS/01.0FT//");
		lines.add("TMPOS/261208ZAPR/IN/00.07.00N-000.28.00W/057T/04KTS/01.0FT//");
		lines.add("TMPOS/261209ZAPR/IN/00.08.00N-000.31.00W/057T/04KTS/02.0FT//");
		lines.add("TMPOS/261210ZAPR/IN/00.09.00N-000.33.00W/057T/04KTS/01.0FT//");
		lines.add("TMPOS/261211ZAPR/IN/00.10.00N-000.32.00W/057T/04KTS/02.0FT//");
		lines.add("TMPOS/261212ZAPR/INS/00.10.30N-000.34.30W/180T/13KTS/000FT");
		lines.add("TMPOS/261214ZAPR/INS/00.10.75N-000.34.75W/189T/12KTS/004FT");

		final Layers layers = new Layers();
		final ImportASWDataDocument iw = new ImportASWDataDocument(layers);

		// start off with user cancelling import
		ImportASWDataDocument.setQuestionHelper(new MyHelper(null));

		iw.processThese(lines);

		assertTrue(layers.size() == 0, "layers should be empty");

		questionStore = null;
		_lastTrackName = null;

		ImportASWDataDocument.setQuestionHelper(new MyHelper(NORWICH));

		iw.processThese(lines);

		assertEquals(DEFAULT_TRACK_MESSAGE, questionStore, "we should have stored the default str");

		assertTrue(layers.size() == 1, "We should have a track");
		final TrackWrapper track = (TrackWrapper) layers.findLayer(NORWICH);

		// check it has some data
		final TrackSegment segment = (TrackSegment) track.getSegments().elements().nextElement();
		assertEquals(12, segment.size(), "has fixes");
		assertEquals(NORWICH, track.getName());

		final WorldArea area = track.getBounds();
		// System.out.println(area);
		assertEquals(" Area TL: 00\u00B010'45.00\"N 000\u00B034'45.00\"W  BR: 00\u00B001'00.00\"N 000\u00B021'00.00\"W ",
				area.toString(),
				"correct area");
	}

	@Test
	public void testParseDocument_mm_ss() {
		final List<String> lines = new ArrayList<String>();

		// NOTE: the third element of the posits are all under 59,
		// so we presume seconds

		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("HEADER LINE");
		lines.add("TMPOS/261200ZAPR/IN/00.01.0N-000.21.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261202ZAPR/IN/00.02.0N-000.22.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261204ZAPR/IN/00.03.0N-000.23.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261205ZAPR/IN/00.04.0N-000.24.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261206ZAPR/IN/00.05.0N-000.25.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261207ZAPR/IN/00.06.0N-000.27.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261208ZAPR/IN/00.07.0N-000.28.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261209ZAPR/IN/00.08.0N-000.31.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261210ZAPR/IN/00.09.0N-000.33.0W/057T/04KTS/01.0M//");
		lines.add("TMPOS/261211ZAPR/IN/00.10.0N-000.35.0W/057T/04KTS/02.0M//");
		lines.add("TMPOS/261212ZAPR/INS/00.10.30N-000.34.30W/180T/13KTS/000M");
		lines.add("TMPOS/261214ZAPR/INS/00.10.40N-000.34.00W/189T/12KTS/004M");

		final Layers layers = new Layers();
		final ImportASWDataDocument iw = new ImportASWDataDocument(layers);

		// start off with user cancelling import
		ImportASWDataDocument.setQuestionHelper(new MyHelper(null));

		iw.processThese(lines);

		assertTrue(layers.size() == 0, "layers should be empty");

		questionStore = null;
		_lastTrackName = null;

		ImportASWDataDocument.setQuestionHelper(new MyHelper(NORWICH));

		iw.processThese(lines);

		assertEquals(DEFAULT_TRACK_MESSAGE, questionStore, "we should have stored the default str");

		assertTrue(layers.size() == 1, "We should have a track");
		final TrackWrapper track = (TrackWrapper) layers.findLayer(NORWICH);

		// check it has some data
		final TrackSegment segment = (TrackSegment) track.getSegments().elements().nextElement();
		assertEquals(12, segment.size(), "has fixes");
		assertEquals(NORWICH, track.getName());

		final WorldArea area = track.getBounds();
		// System.out.println(area);
		assertEquals(" Area TL: 00\u00B010'40.00\"N 000\u00B035'00.00\"W  BR: 00\u00B001'00.00\"N 000\u00B021'00.00\"W ",
				area.toString(),
				"correct area");
	}

	@SuppressWarnings("unused")
	@Test
	public void testParseFixes() {
		final String d1 = "TMPOS/261200ZAPR/IN/12.23.34N-121.12.1E/o57T/04KTS/05.1M//";
		final String d2 = "TIMPOS/262327ZAPR/INS/13.14.15S-011.22.33W/000T/13KTS/0o1FT";
		final String d3 = "TMPOS/220000ZAPR/GPS/1230N/01215W";
		final String d4 = "TMPOS/230001ZAPR/GPS/22.33.44N/020.11.22W/006T/10KTS//";
		final String d5 = "TMPOS/290000ZAPR/GPS/04 .02. 01N/111.22. 11W/89T/9KTS";
		// repeat of last line, but with spaces removed
		final String d6 = "TMPOS/290000ZAPR/GPS/04.02.01N/111.22.11W/89T/9KTS";

		final FixWrapper f1 = fixFor(d1, 2019);
		assertNotNull(f1);
		assertEquals(57d, f1.getCourseDegs());
		assertEquals(4d, f1.getSpeed());
		assertEquals(5.1d, f1.getDepth());
		assertEquals("190426 120000", DebriefFormatDateTime.toStringHiRes(f1.getDateTimeGroup()));

		final FixWrapper f2 = fixFor(d2, 2016);
		assertNotNull(f2);
		assertEquals(0d, f2.getCourseDegs());
		assertEquals(13d, f2.getSpeed(), 0.001);
		assertEquals(new WorldDistance(1, WorldDistance.FT).getValueIn(WorldDistance.METRES), f2.getDepth());
		assertEquals("160426 232700", DebriefFormatDateTime.toStringHiRes(f2.getDateTimeGroup()));

		final FixWrapper f3 = fixFor(d3, 1998);
		assertNotNull(f3);
		assertEquals(0d, f3.getCourseDegs());
		assertEquals(0d, f3.getSpeed(), 0.001);
		assertEquals(0d, f3.getDepth());
		assertEquals("980422 000000", DebriefFormatDateTime.toStringHiRes(f3.getDateTimeGroup()));
	}

	@Test
	public void testParseLocations() {
		assertNull(locationFor(null, null), "fails for empty");

		final String d1 = "12.23.34N-121.12.1E";
		final String d2 = "13.14.15S-011.22.33W";
		final String d3 = "1230N/01215W";
		final String d4 = "22.33.44N/020.11.22W";
		final String d5 = "04 .02. 01N/111.22. 11W";
		// repeat of last line, but with spaces removed
		final String d6 = "04.02.01N/111.22.11W";
		final String d7 = "04.02.01N/211.22.11W";
		final String d8 = "94.02.01N/211.22.11W";

		assertEquals(" 12\u00B023'34.00\"N 121\u00B012'01.00\"E ", locationFor(d1, "").toString());
		assertEquals(" 13\u00B014'15.00\"S 011\u00B022'33.00\"W ", locationFor(d2, "").toString());
		assertEquals(" 12\u00B030'00.00\"N 012\u00B015'00.00\"W ", locationFor(d3, "").toString());
		assertEquals(" 22\u00B033'44.00\"N 020\u00B011'22.00\"W ", locationFor(d4, "").toString());
		assertEquals(" 04\u00B002'01.00\"N 111\u00B022'11.00\"W ", locationFor(d5, "").toString());
		assertEquals(" 04\u00B002'01.00\"N 111\u00B022'11.00\"W ", locationFor(d6, "").toString());
		assertEquals(" 22\u00B033'00.00\"N 020\u00B011'00.00\"W ", locationFor("22.33N-020.11W", "").toString());

		// and some mangled one
		try {
			locationFor("22.33.44J/020.11.22W", "");
			fail("should have tripped");
		} catch (final IllegalArgumentException ie) {
			assertEquals("Not a valid hemisphere:J from:22.33.44J", ie.getMessage());
		}

		// and some mangled one
		try {
			locationFor("22.33.44N=020.11.22W", "");
			fail("should have tripped");
		} catch (final IllegalArgumentException ie) {
			assertEquals("Location separator not found", ie.getMessage());
		}

		// and some mangled one
		try {
			locationFor("22.33.44N/020.11.22", "");
			fail("should have tripped");
		} catch (final IllegalArgumentException ie) {
			assertEquals("Not a valid hemisphere:2 from:020.11.22", ie.getMessage());
		}

		// and some mangled one
		try {
			locationFor(d7, "");
			fail("should have tripped");
		} catch (final IllegalArgumentException ie) {
			assertEquals("Longitude out of limits:211.22.11W", ie.getMessage());
		}

		// and some mangled one
		try {
			locationFor(d8, "");
			fail("should have tripped");
		} catch (final IllegalArgumentException ie) {
			assertEquals("Latitude out of limits:94.02.01N", ie.getMessage());
		}

	}

	@Test
	public void testReadRealDocument() throws IOException {
		// get our sample data-file
		final Layers theLayers = new Layers();
		final String fName = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/ASW Data Format.docx";
		final File inFile = new File(fName);
		assertTrue(inFile.exists(), "input file exists");
		final FileInputStream is = new FileInputStream(fName);
		final ImportASWDataDocument iw = new ImportASWDataDocument(theLayers);
		final String trackName = "Wibble";
		ImportASWDataDocument.setQuestionHelper(new MyHelper(trackName));

		final XWPFDocument doc = new XWPFDocument(is);
		final ArrayList<String> strings = ImportNarrativeDocument.importFromWordX(doc);
		assertTrue(ImportASWDataDocument.canImport(strings));
		iw.processThese(strings);
		assertEquals(1, theLayers.size());
		final TrackWrapper track = (TrackWrapper) theLayers.findLayer(trackName);
		final TrackSegment segment = (TrackSegment) track.getSegments().elements().nextElement();
		assertEquals(28, segment.size(), "loaded all");
	}
}
