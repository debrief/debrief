/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2019, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package Debrief.ReaderWriter.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

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
import junit.framework.TestCase;

public class ImportASWDataDocument
{
  private static final String LOCATION_FORMAT_WRONG = "Presumed location format was dd.mm.ss, but it looks like dd.mm.mm: ";

  public static class TestImportWord extends TestCase
  {
    private class MyHelper implements ImportNarrativeDocument.QuestionHelper
    {

      private final String _answer;

      public MyHelper(final String answer)
      {
        _answer = answer;
      }

      @Override
      public String askQuestion(final String title, final String message,
          final String defaultStr)
      {
        questionStore = defaultStr;
        return _answer;
      }

      @Override
      public boolean askYes(final String title, final String message)
      {
        return false;
      }

      @Override
      public void showMessage(final String title, final String message)
      {
        answerStore = message;
      }

      @Override
      public void showMessageWithLogButton(final String title,
          final String message)
      {
        answerStore = message;
      }

    }

    private static final String NORWICH = "NORWICH";

    private static List<String> incrementTimes(final List<String> lines)
    {
      final List<String> res = new ArrayList<String>();
      for (final String t : lines)
      {
        if (isValid(t))
        {
          res.add(t.replace("APR", "JUN"));
        }
        else
        {
          res.add(t);
        }
      }
      return res;
    }

    @SuppressWarnings("unused")
    private static WorldLocation loc(final double lat, final double lon,
        final double dep)
    {
      return new WorldLocation(lat, lon, dep);
    }

    private String answerStore = null;

    private String questionStore = null;

    public void testCleanString()
    {
      assertEquals("0000", clean("00O0"));
      assertEquals("0000", clean("00o0"));
      assertEquals("0000", clean("00o0 "));
      assertEquals("0000", clean(" 00o0"));
      assertEquals("0000", clean(" 0Oo0"));
      assertEquals("0A00", clean("0ao0 "));
      assertEquals("1.234", clean("1,234"));
    }

    public void testCourse()
    {
      assertEquals(MWC.Algorithms.Conversions.Degs2Rads(123.0), courseFor(
          "123T"));
      assertEquals(MWC.Algorithms.Conversions.Degs2Rads(0.0), courseFor(
          "000T"));
    }

    public void testDates()
    {
      final String d1 = "261200ZAPR";
      final String d2 = "262327ZAPR";
      final String d3 = "220300ZJAN";
      final String d4 = "012345ZAPR";
      final String d5 = "292258ZNOV";

      // special test handling. In the absence of a year value, we use the
      // current year. This will change as time moves forward.

      // So, we don't compare the first two chars. The string used for the
      // first test result _was_ "190426 120000"

      assertEquals("aaa", "0426 120000", DebriefFormatDateTime.toString(
          dateFor(d1, 0).getDate().getTime()).substring(2));
      assertEquals("aaa", "0426 232700", DebriefFormatDateTime.toString(
          dateFor(d2, 0).getDate().getTime()).substring(2));
      assertEquals("aaa", "0122 030000", DebriefFormatDateTime.toString(
          dateFor(d3, 0).getDate().getTime()).substring(2));
      assertEquals("aaa", "0401 234500", DebriefFormatDateTime.toString(
          dateFor(d4, 0).getDate().getTime()).substring(2));
      assertEquals("aaa", "1129 225800", DebriefFormatDateTime.toString(
          dateFor(d5, 0).getDate().getTime()).substring(2));
    }

    public void testFewFailures()
    {
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

      assertTrue("layers should not be empty", layers.size() == 1);
      final TrackWrapper track = (TrackWrapper) layers.findLayer(trackName);

      assertNotNull("found track", track);

      assertEquals("correct length", 9, track.numFixes());

      // check the message
      assertEquals("Good string",
          "Import completed, with errors. Errors at lines 9, 11, 13.\nSee Error Log (below) for more.",
          answerStore);
    }

    public void testGetHemi()
    {
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

    public void testGetValue()
    {
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
    
    public void testGetValue_DecimalMinutes()
    {
      final String d1 = "12.30.50";
      final String d1a = "12.30.30";
      
      final String d2 = "011.30.75";
      final String d2a = "011.30.45";
      
      final boolean MMmm = true;
      final boolean MMSS = false;

      assertEquals(getDegreesFor(d1, MMmm), getDegreesFor(d1a, MMSS));
      assertEquals(getDegreesFor(d2, MMmm), getDegreesFor(d2a, MMSS));

      try
      {
        final String d3 = "011.30.75";
        getDegreesFor(d3, MMSS);
        fail("should have thrown exception");
      }
      catch(IllegalArgumentException ie)
      {
        assertEquals(LOCATION_FORMAT_WRONG + "011.30.75", ie.getMessage());
      }
    }

    public void testIsValid()
    {
      final List<String> input = new ArrayList<String>();
      for (int i = 0; i < 20; i++)
      {
        input.add("Some old duff information");
      }
      assertFalse("not suitable for import", canImport(input));
      for (int i = 0; i < 10; i++)
      {
        input.add("TMPOS/Here we go!");
      }
      assertTrue("now suitable for import", canImport(input));

    }

    public void testManyFailures()
    {
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

      assertTrue("layers should not be empty", layers.size() == 1);
      final TrackWrapper track = (TrackWrapper) layers.findLayer(trackName);

      assertNotNull("found track", track);

      assertEquals("correct length", 4, track.numFixes());

      // check the message
      assertEquals("Good string",
          "Import completed, with errors. Errors on 8 lines.\nSee Error Log (below) for more.",
          answerStore);
    }

    public void testParseDates()
    {
      final String d1 = "TIMPD/290000Z/300200Z/APR/2016/APR2016";
      final String d2 = "TIMPD//230001Z//232359Z//APR//2017//APR//2017//";
      final String d3 = "TIMPD/220000Z/222400Z/APR/2018";
      final String d4 = "TIMPD/261200ZAPR/IN/262359ZAPR/IN/APR/2019/APR/2019/";

      // start off with good ones
      assertEquals("date 1", 2016, yearFor(d1).intValue());
      assertEquals("date 2", 2017, yearFor(d2).intValue());
      assertEquals("date 3", 2018, yearFor(d3).intValue());
      assertEquals("date 4", 2019, yearFor(d4).intValue());

      assertNull("wrong format", yearFor(
          "TMPOS/290000Z/300200Z/APR/2016/APR2016"));
      assertNull("no year", yearFor("TMPOS/290000Z/300200Z/APR/201A6/APR2016"));
      assertNull("too early", yearFor("TMPOS/290000Z/300200Z/APR/16/APR2016"));
      assertNull("too late", yearFor(
          "TMPOS/290000Z/300200Z/APR/124316/APR2016"));
    }
    
    
    public void testParseDocument_mm_ss()
    {
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

      assertTrue("layers should be empty", layers.size() == 0);

      questionStore = null;
      _lastTrackName = null;

      ImportASWDataDocument.setQuestionHelper(new MyHelper(NORWICH));

      iw.processThese(lines);

      assertEquals("we should have stored the default str",
          DEFAULT_TRACK_MESSAGE, questionStore);

      assertTrue("We should have a track", layers.size() == 1);
      final TrackWrapper track = (TrackWrapper) layers.findLayer(NORWICH);

      // check it has some data
      final TrackSegment segment = (TrackSegment) track.getSegments().elements()
          .nextElement();
      assertEquals("has fixes", 12, segment.size());
      assertEquals(NORWICH, track.getName());
      
      WorldArea area = track.getBounds();
      System.out.println(area);
      assertEquals("correct area", " Area TL: 00\u00B010'40.00\"N 000\u00B035'00.00\"W  BR: 00\u00B001'00.00\"N 000\u00B021'00.00\"W ",area.toString());
    }

    
    public void testParseDocument_mm_mm()
    {
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

      assertTrue("layers should be empty", layers.size() == 0);

      questionStore = null;
      _lastTrackName = null;

      ImportASWDataDocument.setQuestionHelper(new MyHelper(NORWICH));

      iw.processThese(lines);

      assertEquals("we should have stored the default str",
          DEFAULT_TRACK_MESSAGE, questionStore);

      assertTrue("We should have a track", layers.size() == 1);
      final TrackWrapper track = (TrackWrapper) layers.findLayer(NORWICH);

      // check it has some data
      final TrackSegment segment = (TrackSegment) track.getSegments().elements()
          .nextElement();
      assertEquals("has fixes", 12, segment.size());
      assertEquals(NORWICH, track.getName());
      
      WorldArea area = track.getBounds();
      System.out.println(area);
      assertEquals("correct area", " Area TL: 00\u00B010'45.00\"N 000\u00B034'45.00\"W  BR: 00\u00B001'00.00\"N 000\u00B021'00.00\"W ",area.toString());
    }
    
    public void testParseDocument()
    {
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

      assertTrue("layers should be empty", layers.size() == 0);

      questionStore = null;
      _lastTrackName = null;

      ImportASWDataDocument.setQuestionHelper(new MyHelper(NORWICH));

      iw.processThese(lines);

      assertEquals("we should have stored the default str",
          DEFAULT_TRACK_MESSAGE, questionStore);

      assertTrue("We should have a track", layers.size() == 1);
      final TrackWrapper track = (TrackWrapper) layers.findLayer(NORWICH);

      // check it has some data
      final TrackSegment segment = (TrackSegment) track.getSegments().elements()
          .nextElement();
      assertEquals("has fixes", 12, segment.size());
      assertEquals(NORWICH, track.getName());

      // add another track

      ImportASWDataDocument.setQuestionHelper(new MyHelper("DULWICH"));

      iw.processThese(lines);
      assertTrue("We should have multiple tracks", layers.size() == 2);

      assertEquals("we should been offered the new default", "NORWICH",
          questionStore);

      final List<String> lines2 = incrementTimes(lines);
      ImportASWDataDocument.setQuestionHelper(new MyHelper(NORWICH));

      iw.processThese(lines2);
      final TrackSegment segment2 = (TrackSegment) track.getSegments()
          .elements().nextElement();
      assertEquals("has fixes", 24, segment2.size());
    }

    @SuppressWarnings("unused")
    public void testParseFixes()
    {
      final String d1 =
          "TMPOS/261200ZAPR/IN/12.23.34N-121.12.1E/o57T/04KTS/05.1M//";
      final String d2 =
          "TIMPOS/262327ZAPR/INS/13.14.15S-011.22.33W/000T/13KTS/0o1FT";
      final String d3 = "TMPOS/220000ZAPR/GPS/1230N/01215W";
      final String d4 =
          "TMPOS/230001ZAPR/GPS/22.33.44N/020.11.22W/006T/10KTS//";
      final String d5 = "TMPOS/290000ZAPR/GPS/04 .02. 01N/111.22. 11W/89T/9KTS";
      // repeat of last line, but with spaces removed
      final String d6 = "TMPOS/290000ZAPR/GPS/04.02.01N/111.22.11W/89T/9KTS";

      final FixWrapper f1 = fixFor(d1, 2019);
      assertNotNull(f1);
      assertEquals(57d, f1.getCourseDegs());
      assertEquals(4d, f1.getSpeed());
      assertEquals(5.1d, f1.getDepth());
      assertEquals("190426 120000", DebriefFormatDateTime.toStringHiRes(f1
          .getDateTimeGroup()));

      final FixWrapper f2 = fixFor(d2, 2016);
      assertNotNull(f2);
      assertEquals(0d, f2.getCourseDegs());
      assertEquals(13d, f2.getSpeed(), 0.001);
      assertEquals(new WorldDistance(1, WorldDistance.FT).getValueIn(
          WorldDistance.METRES), f2.getDepth());
      assertEquals("160426 232700", DebriefFormatDateTime.toStringHiRes(f2
          .getDateTimeGroup()));

      final FixWrapper f3 = fixFor(d3, 1998);
      assertNotNull(f3);
      assertEquals(0d, f3.getCourseDegs());
      assertEquals(0d, f3.getSpeed(), 0.001);
      assertEquals(0d, f3.getDepth());
      assertEquals("980422 000000", DebriefFormatDateTime.toStringHiRes(f3
          .getDateTimeGroup()));
    }

    public void testParseLocations()
    {
      assertNull("fails for empty", locationFor(null, null));

      final String d1 = "12.23.34N-121.12.1E";
      final String d2 = "13.14.15S-011.22.33W";
      final String d3 = "1230N/01215W";
      final String d4 = "22.33.44N/020.11.22W";
      final String d5 = "04 .02. 01N/111.22. 11W";
      // repeat of last line, but with spaces removed
      final String d6 = "04.02.01N/111.22.11W";
      final String d7 = "04.02.01N/211.22.11W";
      final String d8 = "94.02.01N/211.22.11W";

      assertEquals(" 12\u00B023'34.00\"N 121\u00B012'01.00\"E ", locationFor(d1, "")
          .toString());
      assertEquals(" 13\u00B014'15.00\"S 011\u00B022'33.00\"W ", locationFor(d2, "")
          .toString());
      assertEquals(" 12\u00B030'00.00\"N 012\u00B015'00.00\"W ", locationFor(d3, "")
          .toString());
      assertEquals(" 22\u00B033'44.00\"N 020\u00B011'22.00\"W ", locationFor(d4, "")
          .toString());
      assertEquals(" 04\u00B002'01.00\"N 111\u00B022'11.00\"W ", locationFor(d5, "")
          .toString());
      assertEquals(" 04\u00B002'01.00\"N 111\u00B022'11.00\"W ", locationFor(d6, "")
          .toString());
      assertEquals(" 22\u00B033'00.00\"N 020\u00B011'00.00\"W ", locationFor(
          "22.33N-020.11W", "").toString());

      // and some mangled one
      try
      {
        locationFor("22.33.44J/020.11.22W", "");
        fail("should have tripped");
      }
      catch (final IllegalArgumentException ie)
      {
        assertEquals("Not a valid hemisphere:J from:22.33.44J", ie
            .getMessage());
      }

      // and some mangled one
      try
      {
        locationFor("22.33.44N=020.11.22W", "");
        fail("should have tripped");
      }
      catch (final IllegalArgumentException ie)
      {
        assertEquals("Location separator not found", ie.getMessage());
      }

      // and some mangled one
      try
      {
        locationFor("22.33.44N/020.11.22", "");
        fail("should have tripped");
      }
      catch (final IllegalArgumentException ie)
      {
        assertEquals("Not a valid hemisphere:2 from:020.11.22", ie
            .getMessage());
      }
      
      // and some mangled one
      try
      {
        locationFor(d7, "");
        fail("should have tripped");
      }
      catch (final IllegalArgumentException ie)
      {
        assertEquals("Longitude out of limits:211.22.11W", ie
            .getMessage());
      }

      // and some mangled one
      try
      {
        locationFor(d8, "");
        fail("should have tripped");
      }
      catch (final IllegalArgumentException ie)
      {
        assertEquals("Latitude out of limits:94.02.01N", ie
            .getMessage());
      }

    }

    public void testReadRealDocument() throws IOException
    {
      // get our sample data-file
      final Layers theLayers = new Layers();
      final String fName =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/ASW Data Format.docx";
      final File inFile = new File(fName);
      assertTrue("input file exists", inFile.exists());
      final FileInputStream is = new FileInputStream(fName);
      final ImportASWDataDocument iw = new ImportASWDataDocument(theLayers);
      final String trackName = "Wibble";
      ImportASWDataDocument.setQuestionHelper(new MyHelper(trackName));

      final XWPFDocument doc = new XWPFDocument(is);
      final ArrayList<String> strings = ImportNarrativeDocument.importFromWordX(
          doc);
      assertTrue(ImportASWDataDocument.canImport(strings));
      iw.processThese(strings);
      assertEquals(1, theLayers.size());
      final TrackWrapper track = (TrackWrapper) theLayers.findLayer(trackName);
      final TrackSegment segment = (TrackSegment) track.getSegments().elements()
          .nextElement();
      assertEquals("loaded all", 28, segment.size());
    }
  }

  private static final String DEFAULT_TRACK_MESSAGE = "track name";

  /**
   * helper class that can ask the user a question populated via Dependency Injection
   */
  private static ImportNarrativeDocument.QuestionHelper questionHelper = null;

  /**
   * the message line indicator we're looking for
   *
   */
  final private static String marker = "TMPOS";

  /**
   * we also occasionally encounter this mangled version of the marker
   *
   */
  final private static String dodgyMarker = "TIMPOS";

  /**
   * remember the name of the last track imported, so we can offer it next time
   */
  static private String _lastTrackName = null;

  public static boolean canImport(final List<String> strings)
  {
    // run through strings, see if we have TMPOS in more than 5 lines in the first 50
    final int numLines = 100;
    final int requiredLines = 5;
    int matches = 0;
    int lines = 0;
    for (final String l : strings)
    {
      lines++;

      if (isValid(l))
      {
        matches++;
      }

      if (matches >= requiredLines)
      {
        return true;
      }

      if (lines > numLines)
      {
        break;
      }

    }
    return false;
  }

  private static String clean(final String str)
  {
    final String noOs = str.replace("O", "0");
    final String noos = noOs.replace("o", "0");
    final String noCommas = noos.replace(",", ".");
    final String trimmed = noCommas.trim();
    final String upper = trimmed.toUpperCase();
    return upper;
  }

  private static double courseFor(final String courseStr)
  {
    if (courseStr == null || courseStr.length() == 0)
      return 0d;

    final int tIndex = courseStr.indexOf("T");
    if (tIndex == -1)
    {
      throw new IllegalArgumentException(
          "Course not formatted correctly (expect 123T):" + courseStr);
    }

    final double courseDegs = Double.parseDouble(clean(courseStr.substring(0,
        tIndex)));
    return MWC.Algorithms.Conversions.Degs2Rads(courseDegs);
  }

  private static HiResDate dateFor(final String str, final int year)
  {
    if (!str.contains("Z"))
    {
      throw new IllegalArgumentException(
          "Wrongly formatted date (expected 123245ZJAN):" + str);
    }
    final DateFormat df = new GMTDateFormat("yyyy ddHHmm'Z'MMM");
    HiResDate res = null;
    try
    {
      // prepend the year
      final int yearVal;
      if (year > 1000)
      {
        yearVal = year;
      }
      else
      {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        yearVal = cal.get(Calendar.YEAR);
      }

      final Date date = df.parse(yearVal + " " + str);

      res = new HiResDate(date);
    }
    catch (final ParseException e)
    {
      throw new IllegalArgumentException("Badly formatted date:" + str);
    }
    return res;
  }

  private static double depthFor(final String depth)
  {
    if (depth == null || depth.length() == 0)
      return 0d;

    final String[] components = splitIntoValueAndUnits(depth);
    if (components.length != 2)
    {
      throw new IllegalArgumentException(
          "Depth not formatted correctly (expected 23M or 12FT):" + depth);
    }

    final double value = Double.valueOf(clean(components[0]));
    final String units = components[1];
    final WorldDistance res;
    switch (units.toUpperCase())
    {
      case "M":
        res = new WorldDistance(value, WorldDistance.METRES);
        break;
      case "FT":
        res = new WorldDistance(value, WorldDistance.FT);
        break;
      default:
        throw new IllegalArgumentException("Depth units not recognised:"
            + depth);
    }

    return res.getValueIn(WorldDistance.METRES);
  }

  @SuppressWarnings(
  {"unused"})
  private static FixWrapper fixFor(final String str, final int year)
  {
    // final String d1 =
    // "TMPOS/261200ZAPR/IN/12.23.34N-121.12.1E/057T/04KTS/00.0M//";
    // final String d2 =
    // "TMPOS/262327ZAPR/INS/13.14.15S-011.22.33W/180T/13KTS/000FT";
    // final String d3 = "TMPOS/220000ZAPR/GPS/1230N/01215W";
    // final String d4 =
    // "TMPOS/230001ZAPR/GPS/22.33.44N/020.11.22W/006T/10KTS//";
    // final String d5 = "TMPOS/290000ZAPR/GPS/04 .02. 01N/111.22. 11W/89T/9KTS";
    // // repeat of last line, but with spaces removed
    // final String d6 = "TMPOS/290000ZAPR/GPS/04.02.01N/111.22.11W/89T/9KTS";

    if (str == null)
    {
      return null;
    }
    else if (!isValid(str))
    {
      throw new IllegalArgumentException("Not a position line");
    }
    else
    {
      final String[] tokens = str.split("/");
      final int numTokens = tokens.length;

      if (numTokens < 5)
      {
        throw new IllegalArgumentException(
            "Insufficient token in string (expected 5):" + str);
      }

      int ctr = 0;
      final String title = tokens[ctr++];
      final HiResDate date = dateFor(tokens[ctr++], year);
      final String source = tokens[ctr++];
      String pos1 = tokens[ctr++];
      if (!pos1.contains("-"))
      {
        // ok, it's split across two tokens
        final String pos2 = tokens[ctr++];
        pos1 = pos1 + "/" + pos2;
      }

      final String courseStr = ctr < numTokens ? clean(tokens[ctr++]) : null;
      final String speedStr = ctr < numTokens ? clean(tokens[ctr++]) : null;
      final String depthStr = ctr < numTokens ? clean(tokens[ctr++]) : null;

      final WorldLocation loc = locationFor(pos1, depthStr);

      final double course = courseFor(courseStr);
      final double speed = speedFor(speedStr);
      final double depth = depthFor(depthStr);

      loc.setDepth(depth);
      final Fix fix = new Fix(date, loc, course, speed);
      return new FixWrapper(fix);
    }
  }

  private static double getDegreesFor(final String str, boolean isDecimalMinutes)
  {
    // final String d1 = "12.23.34";
    // final String d2 = "011.22.33";
    // final String d3 = "01215";
    // final String d4 = "22.33.44";
    // final String d5 = "04 .02. 01";
    // // repeat of last line, but with spaces removed
    // final String d6 = "04.02.01";

    try
    {
      final double res;
      if (str.contains("."))
      {
        final String[] comps = str.split("\\.");
        if (comps.length == 3)
        {
          if(isDecimalMinutes)
          {
            final double degs = Double.valueOf(clean(comps[0].trim()));
            final double mins = Double.valueOf(clean(comps[1].trim())+ "." + clean(comps[2].trim()));
            res = degs + mins / 60;
          }
          else
          {
            final double degs = Double.valueOf(clean(comps[0].trim()));
            final double mins = Double.valueOf(clean(comps[1].trim()));
            final double secs = Double.valueOf(clean(comps[2].trim()));
            
            // sanity check. We're presuming decimal seconds. But, if the value is more than
            // 59, we must actually be receiving decimal minutes
            if(secs >= 60d)
            {
              throw new IllegalArgumentException(
                  LOCATION_FORMAT_WRONG
                      + str);
            }
            
            res = degs + mins / 60 + secs / (60 * 60);
          }
        }
        else if (comps.length == 2)
        {
          final double degs = Double.valueOf(clean(comps[0].trim()));
          final double mins = Double.valueOf(clean(comps[1].trim()));
          res = degs + mins / 60;
        }
        else
        {
          throw new IllegalArgumentException("Badly formatted location:" + str);
        }
      }
      else
      {
        // we may have a trailing hemisphere
        final String[] tokens = splitIntoValueAndUnits(str);
        final String digits = tokens[0];
        final int len = digits.length();
        final double degVal = Double.parseDouble(digits.substring(0, len - 2));
        final double minVal = Double.parseDouble(digits.substring(len - 2,
            len));
        res = degVal + minVal / 60;
      }
      return res;
    }
    catch (final NumberFormatException ne)
    {
      throw new IllegalArgumentException("Couldn't extract location form:"
          + str);
    }
  }

  private static double getHemiFor(final String str)
  {
    final String cleaned = clean(str);
    final String lastLetter = cleaned.substring(cleaned.length() - 1);
    final double res;
    switch (lastLetter.toUpperCase())
    {
      case "N":
      case "E":
        res = 1;
        break;
      case "S":
      case "W":
        res = -1;
        break;
      default:
        throw new IllegalArgumentException("Not a valid hemisphere:"
            + lastLetter + " from:" + cleaned);
    }

    return res;
  }

  private static boolean isValid(final String line)
  {
    return line.startsWith(marker) || line.startsWith(dodgyMarker);
  }

  private static WorldLocation locationFor(final String str, final String depthStr)
  {
    // 00.00.0N-000.00.0W
    // 00.00.00N-000.00.00W
    // ddmmN/dddmmW
    // 00.00.00N/000.00.00W
    // 00 .00. 00N/000.00. 00W

    // Note: if the depth Str contains "ft", we will interpret the last two blocks as
    // mm.mm, rather than mm.ss
    final boolean IsDecimalMinutes = depthStr != null && depthStr.toUpperCase().contains("FT");
    
    if (str == null)
    {
      return null;
    }

    final String[] components;
    if (str.contains("-"))
    {
      components = str.split("-");
    }
    else if (str.contains("/"))
    {
      components = str.split("/");
    }
    else
    {
      throw new IllegalArgumentException("Location separator not found");
    }

    final String latStr = components[0];
    final double latHemi = getHemiFor(clean(latStr));
    final double latVal = getDegreesFor(clean(latStr).substring(0, latStr
        .length() - 1), IsDecimalMinutes);
    
    if(latVal > 90)
    {
      throw new IllegalArgumentException("Latitude out of limits:" + latStr);
    }

    final String longStr = components[1];
    final double longHemi = getHemiFor(clean(longStr));
    final double longVal = getDegreesFor(clean(longStr).substring(0, longStr
        .length() - 1), IsDecimalMinutes);

    if(longVal > 180)
    {
      throw new IllegalArgumentException("Longitude out of limits:" + longStr);
    }

    return new WorldLocation(latHemi * latVal, longHemi * longVal, 0d);
  }

  public static void logError(final int status, final String msg,
      final Exception e)
  {
    logThisError(status, msg, e);
  }

  public static void logThisError(final int status, final String msg,
      final Exception e)
  {
    Application.logError3(status, msg, e, true);
  }

  /**
   * do some pre-processing of text, to protect robustness of data written to file
   *
   * @param raw_text
   * @return text with some control chars removed
   */
  public static String removeBadChars(final String raw_text)
  {
    // swap soft returns for hard ones
    String res = raw_text.replace('\u000B', '\n');

    // we learned that whilst MS Word includes the following
    // control chars, and we can persist them via XML, we
    // can't restore them via SAX. So, swap them for
    // spaces
    res = res.replace((char) 1, (char) 32);
    res = res.replace((char) 19, (char) 32);
    res = res.replace((char) 8, (char) 32); // backspace char, occurred in Oct 17, near an inserted
                                            // picture
    res = res.replace((char) 20, (char) 32);
    res = res.replace((char) 21, (char) 32);
    res = res.replace((char) 5, (char) 32); // MS Word comment marker
    res = res.replace((char) 31, (char) 32); // described as units marker, but we had it prior to
                                             // subscript "2"

    // done.
    return res;
  }

  public static void setQuestionHelper(
      final ImportNarrativeDocument.QuestionHelper helper)
  {
    questionHelper = helper;
  }

  private static double speedFor(final String speed)
  {
    if (speed == null || speed.length() == 0)
      return 0d;

    final String[] components = splitIntoValueAndUnits(speed);
    if (components.length != 2)
    {
      throw new IllegalArgumentException(
          "Speed not formatted correctly (expected 23KTS):" + speed);
    }

    final double value = Double.valueOf(components[0]);
    final String units = components[1];

    final WorldSpeed res;
    switch (units.toUpperCase())
    {
      case "KTS":
        res = new WorldSpeed(value, WorldSpeed.Kts);
        break;
      case "MS":
        res = new WorldSpeed(value, WorldSpeed.M_sec);
        break;
      default:
        throw new IllegalArgumentException("Speed units not recognised:"
            + speed);
    }

    return res.getValueIn(WorldSpeed.ft_sec) / 3d;
  }

  private static String[] splitIntoValueAndUnits(final String string)
  {
    // clean it first
    final String cleaned = clean(string);

    // regex came from here:
    // https://codereview.stackexchange.com/a/2349
    final String[] components = cleaned.split(
        "[^A-Z.0-9]+|(?<=[A-Z])(?=[0-9])|(?<=[0-9])(?=[A-Z])");

    return components;
  }

  private static Integer yearFor(final String str)
  {
    // formats encountered in the wild
    // "TIMPD/290000Z/300200Z/APR/2016/APR2019";
    // "TIMPD//230001Z//232359Z//APR//2017//APR//2019//";
    // "TIMPD/220000Z/222400Z/APR/2017";
    // "TIMPD/261200ZAPR/IN/262359ZAPR/IN/APR/2019/APR/2019/";

    // double-check it's one of ours
    if (!str.startsWith("TIMPD"))
    {
      return null;
    }
    else
    {
      // ok, tokenise
      final String[] tokens = str.split("/");
      for (final String t : tokens)
      {
        // drop any whitespace
        final String s = t.trim();
        if (s.length() == 4 && s.matches("^-?\\d+$"))
        {
          final int year = Integer.parseInt(s);
          if (year > 2015 && year < 2030)
          {
            return year;
          }
        }
      }
      throw new IllegalArgumentException("Failed to extract year from TIMPD:"
          + str);
    }
  }

  /**
   * where we write our data
   *
   */
  private final Layers _layers;

  public ImportASWDataDocument(final Layers destination)
  {
    _layers = destination;
  }

  /**
   * parse a list of strings
   *
   * @param strings
   */
  public void processThese(final List<String> strings)
  {

    if (strings.isEmpty())
    {
      return;
    }

    // get the track name
    if (questionHelper == null)
    {
      throw new RuntimeException(
          "ASW Data importer has not had a Question Helper assigned");
    }

    final String defaultTrack = _lastTrackName != null ? _lastTrackName
        : DEFAULT_TRACK_MESSAGE;

    final String trackName = questionHelper.askQuestion("Load ASW Track",
        "Name for this track:", defaultTrack);
    if (trackName == null)
    {
      return;
    }
    else
    {
      // ok, remember the track name
      _lastTrackName = trackName;
    }

    // does this track already exist?
    TrackWrapper track = (TrackWrapper) _layers.findLayer(trackName, true);

    // ok, now we can loop through the strings
    int ctr = 1;
    int year = Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(
        Calendar.YEAR);
    final List<Integer> badLines = new ArrayList<Integer>();
    for (final String line : strings)
    {
      try
      {
        if (line == null)
        {
          // this shouldn't happen, but let's be tidy about it
          break;
        }
        if (line.startsWith("TIMPD"))
        {
          // get the year
          year = yearFor(line);
        }
        else if (isValid(line))
        {
          final FixWrapper fix = fixFor(line, year);
          fix.resetName();

          // ok, we've got something, check we have a track to put it into
          if (track == null)
          {
            track = new TrackWrapper();
            track.setName(trackName);
            track.setColor(DebriefColors.YELLOW);
            _layers.addThisLayer(track);
          }

          // now add the fix
          track.addFix(fix);
        }
      }
      catch (final IllegalArgumentException ie)
      {
        final String msg = "Formatting error at line:" + ctr + "\n" + ie
            .getMessage();
        Application.logError2(ToolParent.ERROR, msg, ie);
        badLines.add(ctr);
      }
      catch (final Exception ex)
      {
        final String msg = "Unknown error at line:" + ctr + "\n" + ex
            .getMessage();
        Application.logError2(ToolParent.ERROR, msg, ex);
        badLines.add(ctr);
      }

      ctr++;
    }

    if (!badLines.isEmpty())
    {
      reportBadImportLines(badLines);
    }

    // fire modified event
    _layers.fireModified(track);
  }

  public void reportBadImportLines(final List<Integer> badLines)
  {
    final StringBuffer msg = new StringBuffer();
    final int len = badLines.size();
    if (len < 5)
    {
      msg.append("Errors at lines ");
      boolean first = true;
      for (final Integer t : badLines)
      {
        if (first)
        {
          first = false;
        }
        else
        {
          msg.append(", ");
        }
        msg.append(t);
      }
      msg.append(".");
    }
    else
    {
      msg.append("Errors on " + len + " lines.");
    }
    final String message = "Import completed, with errors. " + msg.toString()
        + "\nSee Error Log (below) for more.";
    questionHelper.showMessageWithLogButton("Import error", message);
  }

}
