/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
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

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import junit.framework.TestCase;

public class ImportNarrativeDocument
{
  /**
   * collection of fields for an FCS entry
   *
   * @author ian
   *
   */
  private static class FCSEntry
  {
    private static String getClassified(final String input)
    {
      String res = null;

      final String regexp = "Classified (.*$)";
      final Pattern pattern = Pattern.compile(regexp);

      final Matcher matcher = pattern.matcher(input);
      if (matcher.find())
      {
        res = matcher.group(1);
      }

      return res;
    }

    /**
     * get the element that starts with the provided identifier
     *
     * @param identifier
     * @param input
     * @return
     */
    private static Double getElement(final String identifier,
        final String input)
    {
      Double res = null;

      final String regexp = identifier + "-*(\\d+\\.?\\d*)";
      final Pattern pattern = Pattern.compile(regexp);

      final Matcher matcher = pattern.matcher(input);
      if (matcher.find())
      {
        final String found = matcher.group(1);
        try
        {
          res = Double.parseDouble(found);
        }
        catch (final NumberFormatException fe)
        {
          // ok, we failed :-(
        }
      }

      return res;
    }

    /**
     * special element handler that can accommodate a range of types of units
     *
     * @param tidied
     * @return
     */
    private static WorldDistance getRange(final String input)
    {
      final WorldDistance res;

      // replace newline control characters
      String tidied = input.replace("\n", "");
      tidied = tidied.replace("\r", "");

      final String regexp =
          ".*R-(?<RANGE>\\d+\\.?\\d?)(?:\\s*)(?<UNITS>\\w*?)(?:\\.|\\s).*";
      final Matcher m = Pattern.compile(regexp).matcher(tidied);
      if (m.matches())
      {
        final double range = Double.valueOf(m.group("RANGE"));
        final String units = m.group("UNITS");

        // ok, create the relevant object
        if (units.toUpperCase().equals("KYDS"))
        {
          res = new WorldDistance(range, WorldDistance.KYDS);
        }
        else if (units.toUpperCase().equals("YDS"))
        {
          res = new WorldDistance(range, WorldDistance.YARDS);
        }
        else if (units.toUpperCase().equals("M"))
        {
          res = new WorldDistance(range, WorldDistance.METRES);
        }
        else
        {
          res = null;
        }
      }
      else
      {
        res = null;
      }

      return res;
    }

    private static String parseSource(final String str)
    {
      // replace newline control characters
      String tidied = str.replace("\n", "");
      tidied = tidied.replace("\r", "");
      tidied = tidied.trim();

      final String regexp = ".*([A-Z]{1,4}\\d{3}|M\\d{2})(?<SOURCE>.*)B-.*";
      final Matcher m = Pattern.compile(regexp).matcher(tidied);
      final String res;
      if (m.matches())
      {
        final String source = m.group("SOURCE").trim();

        // ok, special processing. We're getting unpredicable extra text
        // in the source field (between FCS and "B-". So
        // do some inspection to decide what to show
        if (source.contains("LOP"))
        {
          res = "LOP";
        }
        else if (source.contains("SMCS"))
        {
          res = "SMCS";
        }
        else if (source.contains("WECDIS"))
        {
          res = "WECDIS";
        }
        else if (source.contains("1936"))
        {
          res = "1936";
        }
        else if (source.contains("1959"))
        {
          res = "1959";
        }
        else if (source.contains("CMD"))
        {
          res = "CMD";
        }
        else if (source.contains("WECDIS"))
        {
          res = "WECDIS";
        }
        else if (source.toUpperCase().contains("TRIANGULATION"))
        {
          res = "Triangulation";
        }
        else if (source.contains("HDPR"))
        {
          res = "HDPR";
        }
        else
        {
          res = source;
        }
      }
      else
      {
        res = null;
      }

      return res;
    }

    /**
     * extract the track number from the provided string
     *
     * @param str
     * @return
     */
    private static String parseTrack(final String str)
    {
      // note: we try to match the master track first, since sometimes
      // both are referred to in the FCS entry

      // NOTE: if we continue to get "thrown" by multiple references in the line,
      // then we should use a more prescriptive regexp, that starts with the
      // FCS marker

      final String shortTrackId = "(M\\d{2})";
      final Pattern shortPattern = Pattern.compile(shortTrackId);

      final Matcher matcher = shortPattern.matcher(str);
      final String res;
      if (matcher.find())
      {
        res = matcher.group(1);
      }
      else
      {
        final String longTrackId = "[A-Z]{1,4}(\\d{3})";
        final Pattern longPattern = Pattern.compile(longTrackId);

        final Matcher matcher1 = longPattern.matcher(str);
        if (matcher1.find())
        {
          res = matcher1.group(1);
        }
        else
        {
          res = null;
        }
      }

      return res;
    }

    /**
     * have brg/rng as objects, so they can be null
     *
     */
    final Double brgDegs;
    final Double rangYds;

    final String tgtType;

    final String contact;

    final double crseDegs;

    final double spdKts;

    final String source;

    public FCSEntry(final String msg)
    {
      // pull out the matching strings
      final Double bVal = getElement("B-", msg);
      final WorldDistance rVal = getRange(msg);
      final Double cVal = getElement("C-", msg);
      final Double sVal = getElement("S-", msg);

      // extract the classification
      final String classStr = getClassified(msg);

      // try to extract the track id
      final String trackId = parseTrack(msg);
      final String source = parseSource(msg);

      this.crseDegs = cVal != null ? cVal : 0d;
      this.brgDegs = bVal != null ? bVal : null;
      this.rangYds = rVal != null ? rVal.getValueIn(WorldDistance.YARDS) : null;
      this.spdKts = sVal != null ? sVal : 0d;
      this.tgtType = classStr != null ? classStr : "N/A";
      this.contact = trackId != null ? trackId : "N/A";
      this.source = source != null ? source : "";
    }

  }

  private static class NarrEntry
  {
    /**
     * what is the last valid time we have. if time fields are missing we will extend from the last
     * DTG
     */
    private static Date lastDtg;
    /**
     * what was the last platform we read in, in case the platform is missing
     *
     */
    private static String lastPlatform;
    /**
     * what was the last entry? We remember it, so we can append ourselves to it
     *
     */
    private static NarrEntry lastEntry;
    /**
     * we've encountered circumstances where copy/paste has ended up with the day being earlier than
     * the current one When we can detect this, we'll use the previous day.
     */
    private static String lastDay;

    /**
     * don#t assume a decreasing day is wrong if the month has incremented
     */
    private static String lastMonth;

    // ///////////////////
    // static variables to help handle corrupt/incomplete data.
    // NOTE: any new ones should be included in the "reset() processing
    // ///////////////////

    /**
     * don#t assume a decreasing day is wrong if the year has incremented
     */
    private static String lastYear;

    static public NarrEntry create(final String msg, final int lineNum)
    {
      NarrEntry res = null;
      try
      {
        res = new NarrEntry(msg);

        if (res.appendedToPrevious && res.text != null)
        {
          // that's ok - we'll let the parent handle it
        }
        else
        {
          // just check it's valid
          final boolean valid = (res.dtg != null) && (res.type != null)
              && (res.platform != null) && (res.text != null);
          if (!valid)
          {
            res = null;
          }
        }
      }
      catch (final ParseException e)
      {
        logThisError(ToolParent.WARNING,
            "Failed whilst parsing Word Document, at line:" + lineNum, e);
      }

      return res;
    }

    /**
     * reset the static variables we use to handle missing, or mangled data
     *
     */
    public static void reset()
    {
      lastDtg = null;
      lastPlatform = null;
      lastEntry = null;
      lastDay = null;
      lastMonth = null;
      lastYear = null;
    }

    HiResDate dtg;

    String type;

    String platform;

    String text;

    boolean appendedToPrevious = false;

    @SuppressWarnings("deprecation")
    public NarrEntry(final String entry) throws ParseException
    {
      final String trimmed = entry.trim();
      final String[] parts = trimmed.split(",");
      int ctr = 0;

      // if(entry.contains("SEARCH STRING"))
      // {
      // System.out.println("here");
      // }

      // sort out our date formats
      final DateFormat fourBlock = new GMTDateFormat("HHmm");

      // final DateFormat sixBlock = new SimpleDateFormat("ddHHmm");
      // sixBlock.setTimeZone(TimeZone.getTimeZone("UTC"));

      final boolean correctLength = parts.length > 5;
      final boolean sixFigDTG = correctLength && parts[0].length() == 6
          && parts[0].matches(DATE_MATCH_SIX);
      final boolean fourFigDTG = correctLength && parts[0].length() == 4
          && parts[0].matches(DATE_MATCH_FOUR);
      final boolean hasDTG = sixFigDTG || fourFigDTG;

      if (hasDTG)
      {
        final String dtgStr;
        if (fourFigDTG)
        {
          dtgStr = parts[ctr++];
        }
        else
        {
          dtgStr = parts[ctr++].substring(2, 6);
        }

        // ok, sort out the time first
        String dayStr = parts[ctr++];
        final String monStr = parts[ctr++];
        final String yrStr = parts[ctr++];
        platform = parts[ctr++].trim();
        type = parts[ctr++].trim();

        /**
         * special processing, to overcome problem with entries being pulled back from the next day.
         * The problem has occurred when something that happened at, say 2345 only gets entered at
         * 0005, so the user moves the entry back to the real time
         */
        if (sixFigDTG)
        {
          final int dtgDate = Integer.valueOf(parts[0].substring(0, 2));
          final int hours = Integer.valueOf(parts[0].substring(2, 4));

          // is this entry after 2300? (that's the usual destination)
          if (hours == 23)
          {
            final int hiddenDay = Integer.parseInt(dayStr);
            if (hiddenDay == dtgDate + 1)
            {
              // ok, the date in the hidden text is one day after
              // that in 6-fix DTG. correct the date
              dayStr = "" + dtgDate;
            }
          }
        }

        /**
         * special processing, to overcome the previous day being used
         *
         */
        final boolean dayDecreased = lastDay != null && Integer.parseInt(
            dayStr) < Integer.parseInt(lastDay);
        final boolean monthIncreased = lastMonth != null && Integer.parseInt(
            monStr) > Integer.parseInt(lastMonth);
        final boolean yearIncreased = lastYear != null && Integer.parseInt(
            yrStr) > Integer.parseInt(lastYear);

        if (dayDecreased && !monthIncreased && !yearIncreased)
        {
          // ok, the day has dropped, but the month hasn't increased
          dayStr = lastDay;

          // insert warning, since this may be a mangled DTG
          final String msg = "Day decreased, but month didn't increase: "
              + dtgStr + ". The previous entry may be a mangled cut/paste";
          logThisError(ToolParent.ERROR, msg, null);
        }
        else
        {
          // it's valid, update the last day
          lastDay = dayStr;
          lastMonth = monStr;
          lastYear = yrStr;
        }

        // hmm, on occasion we don't get the closing comma on the entry type
        if (type.length() > 20)
        {
          final int firstSpace = type.indexOf(" ");
          // note: should actually be looking for non-alphanumeric, since it may be a tab
          type = type.substring(0, firstSpace - 1);
        }

        final int year;
        if (yrStr.length() == 2)
        {
          final int theYear = Integer.parseInt(yrStr);

          // is this from the late 80's onwards?
          if (theYear > 80)
          {
            year = 1900 + theYear;
          }
          else
          {
            year = 2000 + theYear;
          }
        }
        else
        {
          year = Integer.parseInt(yrStr);
        }

        final Date datePart = new Date(year - 1900, Integer.parseInt(monStr)
            - 1, Integer.parseInt(dayStr));

        final Date timePart = fourBlock.parse(dtgStr);

        dtg = new HiResDate(new Date(datePart.getTime() + timePart.getTime()));

        // ok, and the message part
        final int ind = entry.indexOf(type);

        text = entry.substring(ind + type.length() + 1).trim();

        // remember what's happening, so we can refer back to previous entries
        lastDtg = new Date(dtg.getDate().getTime());
        lastPlatform = platform;
        lastEntry = this;
      }
      else
      {

        final int firstTab = trimmed.indexOf("\t");
        int blockToUse = 6;
        if (firstTab != -1 && firstTab <= 7)
        {
          blockToUse = firstTab;
        }

        // see if the first few characters are date
        final String dateStr = trimmed.substring(0, Math.min(trimmed.length(),
            blockToUse));

        // is this all numeric
        boolean probIsDate = false;

        try
        {
          if (dateStr.length() == 6 || dateStr.length() == 4)
          {
            @SuppressWarnings("unused")
            final int testInt = Integer.parseInt(dateStr);
            probIsDate = true;
          }
        }
        catch (final NumberFormatException e)
        {
        }

        final boolean probHasContent = entry.length() > 8;

        if (probIsDate && probHasContent)
        {
          // yes, go for it.

          // ooh, do we have some stored data?
          if (lastDtg != null && lastPlatform != null)
          {
            final String parseStr;
            if (dateStr.length() == 6)
            {
              // reduce to four charts
              parseStr = dateStr.substring(2, 6);
            }
            else
            {
              parseStr = dateStr;
            }

            // first try to parse it
            final Date timePart = fourBlock.parse(parseStr);

            // ok, we can go for it
            final Date newDate = new Date(lastDtg.getYear(), lastDtg.getMonth(),
                lastDtg.getDate());

            // ok, we're ready for the DTG
            dtg = new HiResDate(newDate.getTime() + timePart.getTime());

            // stash the platform
            platform = lastPlatform;

            // and catch the rest of the text
            text = trimmed.substring(dateStr.length()).trim();

            // see if we can recognise the first word as a track number
            // if (text.length() == 0)
            // {
            // System.out.println("here");
            // }

            final String startOfLine = text.substring(0, Math.min(20, text
                .length() - 1));
            final String trackNum = FCSEntry.parseTrack(startOfLine);
            if (trackNum != null)
            {
              type = "FCS";
            }
            else
            {
              // explain we don't know what type of comment this is
              type = "N/A";
            }

            // try to replace soft returns with hard returns
            text = text.replace("\r", "\n");
          }
        }
        else
        {
          // hmm, see if it's just text. If it is, stick it on the end of the previous one

          // ooh, it may be a next day marker. have a check
          final DateFormat dtgBlock = new GMTDateFormat("dd MMM yy");

          boolean hasDate = false;
          try
          {
            @SuppressWarnings("unused")
            final Date scrapDate = dtgBlock.parse(trimmed);
            hasDate = true;
          }
          catch (final ParseException e)
          {
            // it's ok, we can silently fail
          }

          if (hasDate)
          {
            // ok. skip it. it's just a date
          }
          else
          {
            // ooh, do we have a previous one?
            if (lastEntry != null)
            {
              text = trimmed;

              // now flag that we've just added ourselves to the previous one
              appendedToPrevious = true;
            }
          }
        }
      }
    }
  }

  /**
   * helper that can ask the user a question
   *
   */
  public static interface QuestionHelper
  {
    boolean askYes(String title, String message);
  }
  
  public static interface TrimNarrativeHelper
  {
    ImportNarrativeEnum findWhatToImport();
  }
  
  public static enum ImportNarrativeEnum{
    TRIMMED_DATA("trimmed-data"), 
    ALL_DATA("all-data"),
    CANCEL("cancel");
    private String name;
    ImportNarrativeEnum(String string){
      this.name = string;
    }
    public String getName() {
      return this.name;
    }
    public static ImportNarrativeEnum getByName(String name) {
      switch(name) {
        case "trimmed-data":return TRIMMED_DATA;
        case "all-data":return ALL_DATA;
        default:return CANCEL;
      }
    }
  };
  

  public static class TestImportWord extends TestCase
  {
    private final static String dummy_doc_path =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/test_narrative.doc";
    private final static String valid_doc_path =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/FCS_narrative.doc";

    private final static String ownship_track =
        "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";

    public static int countLines(final String str)
    {
      if (str == null || str.isEmpty())
      {
        return 0;
      }
      int lines = 1;
      int pos = 0;
      while ((pos = str.indexOf("\n", pos) + 1) != 0)
      {
        lines++;
      }
      return lines;
    }

    @SuppressWarnings("unused")
    private String messageStr = null;

    @Override
    public void setUp()
    {

      System.out.println("setting up message provider ");

      // clear the message string
      messageStr = null;

      // initialise the message provider
      MessageProvider.Base.setProvider(new MessageProvider()
      {

        @Override
        public void show(final String title, final String message,
            final int status)
        {
          messageStr = message;
        }
      });
    }

    public void testAddFCSToHiddenTrack() throws InterruptedException,
        IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());

      // ok, now filter it to a time period
      final TrackWrapper track = (TrackWrapper) tLayers.elementAt(0);

      // filter the list to a period of data after the narrative cuts
      track.filterListTo(new HiResDate(818749200000L), new HiResDate(
          818766600000L));

      // now load the FCS data

      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportNarrativeDocument importer = new ImportNarrativeDocument(
          tLayers);
      final HWPFDocument doc = new HWPFDocument(is);
      final ArrayList<String> strings = importer.importFromWord(doc);
      importer.processThese(strings);

      // hmmm, how many tracks
      assertEquals("got new tracks", 3, tLayers.size());

      final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(
          LayerHandler.NARRATIVE_LAYER);
      // correct final count
      assertEquals("Got num lines", 364, narrLayer.size());
      
      BaseLayer fcsLayer = (BaseLayer) tLayers.findLayer(NARR_LAYER);

      final Object[] solutions = fcsLayer.getData().toArray();

      // hey, let's have a look them
      LightweightTrackWrapper tw = (LightweightTrackWrapper) solutions[5];
      assertEquals("correct name", "M01_AAAA AAAA AAA (BBBB)", tw.getName());
      assertEquals("got fixes", 3, tw.numFixes());

      // hey, let's have a look them
      tw = (LightweightTrackWrapper) solutions[2];
      assertEquals("correct name", "025_AAAA AAAA AAA (AAAA)", tw.getName());
      assertEquals("got fixes", 5, tw.numFixes());

      // we need to introduce a 500ms delay, so we don't use
      // the cahced visible period
      Thread.sleep(550);

      final TimePeriod bounds = tw.getVisiblePeriod();
      // in our sample data we have several FCSs at the same time,
      // so we have to increment the DTG (seconds) on successive points.
      // so,the dataset should end at 08:11:01 - since the last point
      // had a second added.
      assertEquals("correct bounds:", "Period:951212 080800 to 951212 081400",
          bounds.toString());

      // hey, let's have a look tthem
      
      BaseLayer fcsNarr = (BaseLayer) tLayers.findLayer(NARR_LAYER);
      Object[] data = fcsNarr.getData().toArray();
      
      tw = (LightweightTrackWrapper) data[3];
      assertEquals("correct name", "027_AAAA AAAA AAA (AAAA)", tw.getName());
      assertEquals("got fixes", 3, tw.numFixes());

    }

    public void testAddFCSToTrack() throws InterruptedException, IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());

      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportNarrativeDocument importer = new ImportNarrativeDocument(
          tLayers);
      final HWPFDocument doc = new HWPFDocument(is);
      final ArrayList<String> strings = importer.importFromWord(doc);
      importer.processThese(strings);

      // hmmm, how many tracks
      assertEquals("got new tracks", 3, tLayers.size());
      
      

      final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.findLayer(LayerHandler.NARRATIVE_LAYER);
      
      // correct final count
      assertEquals("Got num lines", 364, narrLayer.size());
      
      BaseLayer sols = (BaseLayer) tLayers.findLayer(NARR_LAYER);
      Object[] data = sols.getData().toArray();

      // hey, let's have a look them
      LightweightTrackWrapper tw = (LightweightTrackWrapper) data[5];
      assertEquals("correct name", "M01_AAAA AAAA AAA (BBBB)", tw.getName());
      assertEquals("got fixes", 3, tw.numFixes());

      // hey, let's have a look them
      tw = (LightweightTrackWrapper) data[2];
      assertEquals("correct name", "025_AAAA AAAA AAA (AAAA)", tw.getName());
      assertEquals("got fixes", 5, tw.numFixes());

      // we need to introduce a 500ms delay, so we don't use
      // the cahced visible period
      Thread.sleep(550);

      final TimePeriod bounds = tw.getVisiblePeriod();
      // in our sample data we have several FCSs at the same time,
      // so we have to increment the DTG (seconds) on successive points.
      // so,the dataset should end at 08:11:01 - since the last point
      // had a second added.
      assertEquals("correct bounds:", "Period:951212 080800 to 951212 081400",
          bounds.toString());

      // hey, let's have a look tthem
      tw = (LightweightTrackWrapper) data[3];
      assertEquals("correct name", "027_AAAA AAAA AAA (AAAA)", tw.getName());
      assertEquals("got fixes", 3, tw.numFixes());

    }

    public void testAdvancedParseBulkFCS() throws ParseException
    {
      final String str1 =
          "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_A FCS B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
      final String str1a =
          "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_B FCS (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
      final String str2 =
          "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_B FCS (AAAA) B-123 R-800yds C-321 S-6kts AAAAAAA. Classified AAAAAA \r BBBBBB AAAAAA.";
      final String str3 =
          "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_A FCS B-123 R-800 m C-321 S-6kts AAAAAAA. Classified AAAAAA \nBBBBBB AAAAAA.";
      final String str4 =
          "160504,16,08,2016,NONSUCH,FCS,  SV023 SOURCE_A FCS B-311� R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA.";

      // create mock importer
      final String[] strings = new String[]
      {str1, str1a, str2, str3, str4};
      final ArrayList<String> strList = new ArrayList<String>(Arrays.asList(
          strings));

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

      final ImportNarrativeDocument importer = new ImportNarrativeDocument(
          target);

      assertEquals("one track", 1, target.size());

      importer.processThese(strList);

      // check we have two tracks
      assertEquals("all tracks", 3, target.size());

      // check the size
      final Layer t2 = target.elementAt(2);
      
      // check t2 is narratives
      assertEquals("correct name", NARR_LAYER, t2.getName());
      BaseLayer layer = (BaseLayer) t2;
      Editable sol1 = layer.first();
      assertEquals("correct name", "023_SOURCE_A FCS", sol1.getName());
      Editable sol2 = layer.last();
      assertEquals("correct name", "023_SOURCE_A FCS", sol1.getName());
      assertEquals("correct name", "023_SOURCE_B FCS (AAAA)", sol2.getName());
      
      // check zero depth in target track
      LightweightTrackWrapper light = (LightweightTrackWrapper) sol2;
      FixWrapper first = (FixWrapper) light.getPositionIterator().nextElement();
      assertEquals("fix has zero depth", 0d, first.getDepth(), 0.0001);
    }

    public void testAdvancedParseFCS() throws ParseException
    {

      final String str1 =
          "   SR023 SOURCE_A FCS B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
      final String str2 =
          "SR023 1936 GAINED FCS (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
      final String str3 =
          "M01 AAAA AAAA AAA (AAAA) B-173 R-3.7kyds C-271 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";

      // high level test of extracting source
      final String match1 = FCSEntry.parseSource(str1);
      assertEquals("got source", "SOURCE_A FCS", match1);

      // check we do our special pattern matching
      final String match2 = FCSEntry.parseSource(str2);
      assertEquals("got source", "1936", match2);

      final String match3 = FCSEntry.parseSource(str3);
      assertEquals("got source", "AAAA AAAA AAA (AAAA)", match3);
    }

    public void testImportEmptyLayers() throws IOException
    {
      final String testFile = dummy_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final Layers tLayers = new Layers();

      final ImportNarrativeDocument importer = new ImportNarrativeDocument(
          tLayers);
      final HWPFDocument doc = new HWPFDocument(is);
      final ArrayList<String> strings = importer.importFromWord(doc);
      importer.processThese(strings);

      // hmmm, how many tracks
      assertEquals("got new tracks", 1, tLayers.size());

      final NarrativeWrapper narrLayer = (NarrativeWrapper) tLayers.elementAt(
          0);
      System.out.println("processed:" + narrLayer.size());

      // hey, let's have a look tthem
      final AbstractCollection<Editable> items = narrLayer.getData();
      final Object[] arr = items.toArray();

      // check array item
      final NarrativeEntry multiLine = (NarrativeEntry) arr[9];
      final String contents = multiLine.getEntry();
      assertEquals("multi-line entry", 3, countLines(contents));

      // correct final count
      assertEquals("Got num lines", 13, narrLayer.size());
    }

    public void testNameHandler()
    {
      final Layers layers = new Layers();
      final TrackWrapper track = new TrackWrapper();
      track.setName("Nelson");
      layers.addThisLayer(track);
      final TrackWrapper track2 = new TrackWrapper();
      track2.setName("Iron Duck");
      layers.addThisLayer(track2);
      final ImportNarrativeDocument iw = new ImportNarrativeDocument(layers);
      String match = iw.trackFor("HMS Boat", "HMS Boat");
      assertNull("not found match", match);
      match = iw.trackFor("HMS Nelson", "HMS Nelson");
      assertNotNull("found match", match);
      match = iw.trackFor("Hms Nelson", "Hms Nelson");
      assertNotNull("found match", match);
      match = iw.trackFor("RNAS Nelson", "RNAS Nelson");
      assertNotNull("found match", match);

      // check we've created new entries
      assertEquals("name matches", 3, iw.nameMatches.size());

      // and the two word name
      match = iw.trackFor("Hms Iron Duck", "Hms Iron Duck");
      assertNotNull("found match", match);

      // check we've created new entries
      assertEquals("name matches", 4, iw.nameMatches.size());

    }

    @SuppressWarnings("deprecation")
    public void testParseDate()
    {

      final String goodDate = "000000";
      assertTrue("date", goodDate.matches(DATE_MATCH_SIX));
      assertFalse("not date", "Notes:".matches(DATE_MATCH_SIX));

      final String testDate1 =
          "160909,16,09,2016,HMS NONSUCH, CAT COMMENT, SOME COMMENT ";

      // ok, get the narrative type
      final NarrEntry thisN1 = NarrEntry.create(testDate1, 1);
      assertEquals("year", 116, thisN1.dtg.getDate().getYear());
      assertEquals("month", 8, thisN1.dtg.getDate().getMonth());
      assertEquals("day", 16, thisN1.dtg.getDate().getDate());
      assertEquals("hour", 9, thisN1.dtg.getDate().getHours());
      assertEquals("min", 9, thisN1.dtg.getDate().getMinutes());
      assertEquals("sec", 0, thisN1.dtg.getDate().getSeconds());
      assertEquals("platform", "HMS NONSUCH", thisN1.platform);
      assertEquals("content", "SOME COMMENT", thisN1.text);

      // ok, now one with mangled (missing) date fields
      final String testDate2 = "161006\tSOME COMMENT 2 ";
      // ok, get the narrative type
      final NarrEntry thisN2 = NarrEntry.create(testDate2, 1);
      assertEquals("year", 116, thisN2.dtg.getDate().getYear());
      assertEquals("month", 8, thisN2.dtg.getDate().getMonth());
      assertEquals("day", 16, thisN2.dtg.getDate().getDate());
      assertEquals("hour", 10, thisN2.dtg.getDate().getHours());
      assertEquals("min", 6, thisN2.dtg.getDate().getMinutes());
      assertEquals("sec", 0, thisN2.dtg.getDate().getSeconds());
      assertEquals("platform", "HMS NONSUCH", thisN2.platform);
      assertEquals("content", "SOME COMMENT 2", thisN2.text);
      assertFalse("flag", thisN2.appendedToPrevious);

      // hey, what if it's just text?
      final String testDate3 = "SOME COMMENT ";
      // ok, get the narrative type
      final NarrEntry thisN3 = NarrEntry.create(testDate3, 1);

      // ok, should just be that text
      assertNull("year", thisN3.dtg);
      assertNull("platform", thisN3.platform);
      assertNotNull("content", thisN3.text);
      assertTrue("flag", thisN3.appendedToPrevious);

    }

    public void testParseFCS() throws ParseException
    {
      final String str1 =
          "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";

      final String str2 =
          "160403,16,09,2016,NONSUCH,FCS, M01 1234 Rge B-311� R-12600 yds. Classified AAAAAA CCCCCC AAAAAA.";

      final String str3 =
          "160403,16,09,2016,NONSUCH,FCS, M02 1234 Rge B-311� R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA. Source from S333.";

      final String str4 =
          "160403,16,09,2016,NONSUCH,FCS, M02 1234 Rge R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA. Source from S333.";

      // try our special identifier
      assertEquals("first bearing", 123d, FCSEntry.getElement("B-", str1));
      assertEquals("first course", 321d, FCSEntry.getElement("C-", str1));
      assertEquals("first range", 5d, FCSEntry.getElement("R-", str1));
      assertEquals("first speed", 6d, FCSEntry.getElement("S-", str1));

      assertEquals("second bearing", 311d, FCSEntry.getElement("B-", str2));
      assertEquals("second range", 12600d, FCSEntry.getElement("R-", str2));

      assertEquals("correct classified", "AAAAAA BBBBBB AAAAAA.", FCSEntry
          .getClassified(str1));

      NarrEntry ne = new NarrEntry(str1);
      final FCSEntry fe1 = new FCSEntry(ne.text);
      assertEquals("got range:", 5000d, fe1.rangYds, 0.001);
      assertEquals("got brg:", 123d, fe1.brgDegs);
      assertEquals("got contact:", "023", fe1.contact);
      assertEquals("got course:", 321d, fe1.crseDegs);
      assertEquals("got speed:", 6d, fe1.spdKts);
      assertEquals("got name:", "AAAAAA BBBBBB AAAAAA.", fe1.tgtType);

      ne = new NarrEntry(str2);
      final FCSEntry fe2 = new FCSEntry(ne.text);
      assertEquals("got range:", 12600d, fe2.rangYds, 0.001);
      assertEquals("got brg:", 311d, fe2.brgDegs);
      assertEquals("got contact:", "M01", fe2.contact);
      assertEquals("got course:", 0d, fe2.crseDegs);
      assertEquals("got speed:", 0d, fe2.spdKts);
      assertEquals("got name:", "AAAAAA CCCCCC AAAAAA.", fe2.tgtType);

      ne = new NarrEntry(str3);
      final FCSEntry fe3 = new FCSEntry(ne.text);
      assertEquals("processed master id before other id:", "M02", fe3.contact);

      ne = new NarrEntry(str4);
      final FCSEntry fe4 = new FCSEntry(ne.text);
      assertNull("empty bearing", fe4.brgDegs);

    }

    public void testParseFCSRange() throws ParseException
    {
      final String str1 =
          "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
      final String str1a =
          "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
      final String str2 =
          "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-800yds C-321 S-6kts AAAAAAA. Classified AAAAAA \r BBBBBB AAAAAA.";
      final String str3 =
          "160504,16,08,2016,NONSUCH,FCS,   SR023 AAAA AAAA AAA (AAAA) B-123 R-800 m C-321 S-6kts AAAAAAA. Classified AAAAAA \nBBBBBB AAAAAA.";
      final String str4 =
          "160403,16,09,2016,NONSUCH,FCS, M01 1234 Rge B-311� R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA.";

      assertEquals("got kyds", 5.1, FCSEntry.getRange(str1).getValueIn(
          WorldDistance.KYDS), 0.1);
      assertEquals("got kyds", 5, FCSEntry.getRange(str1a).getValueIn(
          WorldDistance.KYDS), 0.1);
      assertEquals("got yds", 800, FCSEntry.getRange(str2).getValueIn(
          WorldDistance.YARDS), 0.1);
      assertEquals("got m", 800, FCSEntry.getRange(str3).getValueIn(
          WorldDistance.METRES), 0.1);
      assertEquals("got kyds", 12.4, FCSEntry.getRange(str4).getValueIn(
          WorldDistance.KYDS), 0.1);
    }

    public void testParseFCSWithSameTime() throws ParseException
    {
      final String str1 =
          "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_A FCS B-123 R-5.1kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
      final String str1a =
          "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_B FCS (AAAA) B-123 R-5kyds C-321 S-6kts AAAAAAA. Classified AAAAAA BBBBBB AAAAAA.";
      final String str2 =
          "160504,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_B FCS (AAAA) B-123 R-800yds C-321 S-6kts AAAAAAA. Classified AAAAAA \r BBBBBB AAAAAA.";
      final String str3 =
          "160505,16,08,2016,NONSUCH,FCS,  SR023 SOURCE_A FCS B-123 R-800 m C-321 S-6kts AAAAAAA. Classified AAAAAA \nBBBBBB AAAAAA.";
      final String str4 =
          "160505,16,08,2016,NONSUCH,FCS,  SV023 SOURCE_B FCS (AAAA) B-311� R-12.4kyds. Classified AAAAAA CCCCCC AAAAAA.";

      // create mock importer
      final String[] strings = new String[]
      {str1, str1a, str2, str3, str4};
      final ArrayList<String> strList = new ArrayList<String>(Arrays.asList(
          strings));

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

      final ImportNarrativeDocument importer = new ImportNarrativeDocument(
          target);

      assertEquals("one track", 1, target.size());

      importer.processThese(strList);

      // check we have two tracks
      assertEquals("all tracks", 3, target.size());

      BaseLayer narrs = (BaseLayer) target.findLayer(NARR_LAYER);
      
      
      // check the size
      final LightweightTrackWrapper t1 = (LightweightTrackWrapper)narrs.first();
      final LightweightTrackWrapper t2 = (LightweightTrackWrapper)narrs.last();

      assertEquals("correct name", "023_SOURCE_A FCS", t1.getName());
      assertEquals("correct name", "023_SOURCE_B FCS (AAAA)", t2.getName());

      assertEquals("correct length", 2, t1.numFixes());
      assertEquals("correct length", 3, t2.numFixes());
    }

    public void testParseTrackNumber()
    {
      final String str1 = "asdfads S000 adf ag a";
      final String str1a = "asdfads S000 adf ag a";
      final String str2 = "asdfads SV000 adf ag a";
      final String str2a = "asdfads M00 adf ag a";
      final String str3 = "asdfads adf ag a";
      final String str5 = "M00 0000";

      assertEquals("right id", "000", FCSEntry.parseTrack(str1));
      assertEquals("right id", "000", FCSEntry.parseTrack(str1a));
      assertEquals("right id", "000", FCSEntry.parseTrack(str2));
      assertEquals("right id", "M00", FCSEntry.parseTrack(str2a));
      assertEquals("right id", "M00", FCSEntry.parseTrack(str5));
      assertNull("right id", FCSEntry.parseTrack(str3));
    }

    public void testSpanningYear() throws InterruptedException, IOException
    {
      final Layers tLayers = new Layers();

      // start off with the ownship track
      final File boatFile = new File(ownship_track);
      assertTrue(boatFile.exists());
      final InputStream bs = new FileInputStream(boatFile);

      final ImportReplay trackImporter = new ImportReplay();
      ImportReplay.initialise(new ImportReplay.testImport.TestParent(
          ImportReplay.IMPORT_AS_OTG, 0L));
      trackImporter.importThis(ownship_track, bs, tLayers);

      assertEquals("read in track", 1, tLayers.size());

      // we've also got to change the date of the very last entry on the track,
      // so that we can load narrative entries that go past the end of the track
      // (into the next year).
      final TrackWrapper trk = (TrackWrapper) tLayers.elementAt(0);
      final FixWrapper lastFix = (FixWrapper) trk.getNearestTo(trk
          .getEndDTG())[0];
      lastFix.setDateTimeGroup(new HiResDate(new Date()));

      final String testFile = valid_doc_path;
      final File testI = new File(testFile);
      assertTrue(testI.exists());

      final InputStream is = new FileInputStream(testI);

      final ImportNarrativeDocument importer = new ImportNarrativeDocument(
          tLayers);
      final HWPFDocument doc = new HWPFDocument(is);
      final ArrayList<String> strings = importer.importFromWord(doc);
      importer.processThese(strings);

      final NarrativeWrapper narr = (NarrativeWrapper) tLayers.findLayer(
          LayerHandler.NARRATIVE_LAYER);
      assertEquals("Got num lines", 371, narr.size());
    }
  }

  /**
   * helper class that can ask the user a question populated via Dependency Injection
   */
  private static QuestionHelper questionHelper = null;

  private static List<String> SkipNames = null;

  private static TrimNarrativeHelper trimNarrativeHelper = null;

  /**
   * match a 6 figure DTG
   *
   */
  static final String DATE_MATCH_SIX = "(\\d{6})";

  static final String DATE_MATCH_FOUR = "(\\d{4})";
  
  private static final String NARR_LAYER = "Narrative FCSs";

  private static String existingWECDISTrack(final Layers layers,
      final String dataName)
  {
    String res = null;

    // loop through the layers, see if one matches the WECDIS header text
    final int ctr = layers.size();
    for (int i = 0; i < ctr; i++)
    {
      final Layer thisL = layers.elementAt(i);
      if (thisL.getVisible() && thisL.getName().startsWith(
          ImportNMEA.WECDIS_OWNSHIP_PREFIX))
      {
        final String existingName = thisL.getName();

        // ok, change the track name to the provided name
        thisL.setName(dataName);

        // and we'll now return it, as confirmation that it worked
        res = thisL.getName();

        // and politely tell the user
        MessageProvider.Base.Provider.show("Import Narrative",
            "Since it looks like a WECDIS track, we've renamed " + existingName
                + " to " + res + ", so we can add create FCSs.",
            MessageProvider.INFO);

        // done
        break;
      }
    }

    return res;
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

    // done.
    return res;
  }

  public static void setQuestionHelper(final QuestionHelper helper)
  {
    questionHelper = helper;
  }

  public static void setNarrativeHelper(final TrimNarrativeHelper helper)
  {
    trimNarrativeHelper = helper;
  }
  /**
   * keep track of which track-source combinations we've asked about
   *
   */
  private final List<String> askedAbout = new ArrayList<String>();

  /**
   * flag for if we've informed user that we couldn't find host track
   *
   */
  private boolean _declaredNoHostFound = false;

  /**
   * where we write our data
   *
   */
  private final Layers _layers;

  /**
   * keep track of the last successfully imported narrative entry if we've just received a plain
   * text block, we'll add it to the previous one *
   */
  private NarrativeEntry _lastEntry;

  /**
   * keep track of track names that we have matched
   *
   */
  Map<String, String> nameMatches = new HashMap<String, String>();

  public ImportNarrativeDocument(final Layers target)
  {
    _layers = target;

    if (SkipNames == null)
    {
      SkipNames = new ArrayList<String>();
      SkipNames.add("HMS");
      SkipNames.add("Hms");
      SkipNames.add("USS");
      SkipNames.add("RNAS");
      SkipNames.add("HNLMS");
    }
  }

  private void addEntry(final NarrEntry thisN)
  {
    final NarrativeWrapper nw = getNarrativeLayer();
    String hisTrack = trackFor(thisN.platform, thisN.platform);

    // did we find a track? Don't worry if we didn't just use the raw text
    if (hisTrack == null)
    {
      hisTrack = thisN.platform;
    }

    final NarrativeEntry ne = new NarrativeEntry(hisTrack, thisN.type,
        new HiResDate(thisN.dtg), thisN.text);

    // remember that entry, in case we get incomplete text inthe future
    _lastEntry = ne;

    // try to color the entry
    final Layer host = _layers.findLayer(trackFor(thisN.platform));
    if (host instanceof TrackWrapper)
    {
      final TrackWrapper tw = (TrackWrapper) host;
      ne.setColor(tw.getColor());
    }

    // and store it
    nw.add(ne);
  }

  private void addFCS(final NarrEntry thisN)
  {
    // ok, parse the message
    final FCSEntry fe = new FCSEntry(thisN.text);

    // do we have enough data to create a solution?
    if (fe.brgDegs == null || fe.rangYds == null)
    {
      return;
    }

    // find the host
    final TrackWrapper host = (TrackWrapper) _layers.findLayer(trackFor(
        thisN.platform));
    if (host != null)
    {
      // find the fix nearest this time
      final Watchable[] nearest = host.getNearestTo(thisN.dtg, false);
      if (nearest != null && nearest.length > 0)
      {
        final Watchable fix = nearest[0];
        // apply the offset
        final WorldVector vec = new WorldVector(Math.toRadians(fe.brgDegs),
            new WorldDistance(fe.rangYds, WorldDistance.YARDS),
            new WorldDistance(0, WorldDistance.METRES));
        final WorldLocation loc = fix.getLocation().add(vec);
        
        // overwrite the depth, to put contact at surface (since we really don't know depth)
        loc.setDepth(0);

        // build the track name
        final String trackName;
        if (fe.source != null)
        {
          trackName = fe.contact + "_" + fe.source;
        }
        else
        {
          trackName = fe.contact;
        }

        // find the track for this solution
        LightweightTrackWrapper hisTrack = (LightweightTrackWrapper) _layers.findLayer(trackName, true);
        if (hisTrack == null)
        {
          hisTrack = new LightweightTrackWrapper();
          hisTrack.setName(trackName);
          
          // get a custom color for this contact number (tracks from different
          // will share the same color if they're from the same contact number)
          final Color customColor = colorFor(fe.contact);
          hisTrack.setColor(customColor);

          // other formatting
          hisTrack.setLineThickness(3);

          // do we have narratives folder?
          Layer narrLayer = _layers.findLayer(NARR_LAYER);
          if(narrLayer == null)
          {
            narrLayer = new BaseLayer();
            narrLayer.setName(NARR_LAYER);
            _layers.addThisLayer(narrLayer);
          }
          
          // store this new track
          narrLayer.add(hisTrack);
        }

        // ok, now create the fix
        final WorldSpeed ws = new WorldSpeed(fe.spdKts, WorldSpeed.Kts);
        final double yds_per_sec = ws.getValueIn(WorldSpeed.ft_sec / 3);
        final Fix newF = new Fix(thisN.dtg, loc, Math.toRadians(fe.crseDegs),
            yds_per_sec);
        final FixWrapper newFw = new FixWrapper(newF);

        // lastly, reset the label, so it's legible
        newFw.resetName();

        // oh, and do some more formatting
        newFw.setSymbolShowing(false);
        newFw.setArrowShowing(true);
        newFw.setLabelShowing(true);

        // ok, we may have multiple fixes at the same time
        final Watchable[] hisNearest = hisTrack.getNearestTo(thisN.dtg);
        if (hisNearest != null && hisNearest.length > 0 && hisNearest[0] != null)
        {
          // ok, have a look at it.
          final Watchable nearestW = hisNearest[0];
          System.out.println("nearest:" + nearestW);
          System.out.println("newF:" + newF);
          while (nearestW.getTime().equals(newF.getTime()))
          {
            newF.setTime(new HiResDate(newF.getTime().getDate().getTime()
                + 1000));
          }
        }

        // and store it
        hisTrack.add(newFw);
      }
      else
      {
        logError(ToolParent.WARNING, "Host fix not present for FCS at:"
            + thisN.dtg.getDate(), null);
      }
    }
  }

  /**
   * repeatably find a color for the specified track id The color will be RED if it's a master track
   * ("M01"). Shades of gray nor ownship blue are returned.
   *
   * @param trackId
   * @return
   */
  private Color colorFor(final String trackId)
  {
    final Color res;

    // ok, is it a master track?
    if (trackId.startsWith("M"))
    {
      res = DebriefColors.RED;
    }
    else
    {
      // ok, get the hash code
      final int hash = trackId.hashCode();
      res = DebriefColors.RandomColorProvider.getRandomColor(hash);
    }
    return res;
  }

  private NarrativeWrapper getNarrativeLayer()
  {
    NarrativeWrapper nw = (NarrativeWrapper) _layers.findLayer(
        LayerHandler.NARRATIVE_LAYER);

    if (nw == null)
    {
      nw = new NarrativeWrapper(LayerHandler.NARRATIVE_LAYER);
      _layers.addThisLayer(nw);
    }

    return nw;
  }

  public ArrayList<String> importFromPdf(final String fileName,
      final InputStream inputStream)
  {
    final ArrayList<String> strings = new ArrayList<String>();

    try
    {
      final PDDocument document = PDDocument.load(inputStream);

      // clear the stored data in the importer
      NarrEntry.reset();
      final PDFTextStripper textStripper = new PDFTextStripper();
      final PDPageTree pages = document.getPages();
      for (int i = 1; i <= pages.getCount(); i++)
      {
        textStripper.setStartPage(i);
        textStripper.setEndPage(i);
        final String pageText = textStripper.getText(document);
        final String[] split = pageText.split(textStripper.getLineSeparator());
        strings.addAll(Arrays.asList(split));

      }
      document.close();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

    return strings;
  }

  public ArrayList<String> importFromWord(final HWPFDocument doc)
  {
    final ArrayList<String> strings = new ArrayList<String>();

    final Range r = doc.getRange();

    // clear the stored data in the MS Word importer
    NarrEntry.reset();

    final int lenParagraph = r.numParagraphs();
    for (int x = 0; x < lenParagraph; x++)
    {
      final Paragraph p = r.getParagraph(x);
      strings.add(p.text());
    }

    return strings;
  }

  public ArrayList<String> importFromWordX(final XWPFDocument doc)
  {
    final ArrayList<String> strings = new ArrayList<String>();

    try
    {

      final List<XWPFParagraph> paragraphs = doc.getParagraphs();

      // clear the stored data in the MS Word importer
      NarrEntry.reset();

      for (final XWPFParagraph xwpfParagraph : paragraphs)
      {
        strings.add(xwpfParagraph.getText());
      }
      doc.close();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

    return strings;
  }

  public void logError(final int status, final String msg, final Exception e)
  {
    logThisError(status, msg, e);
  }

  /**
   * parse a list of strings
   *
   * @param strings
   */
  public void processThese(final ArrayList<String> strings)
  {
    
    if (strings.isEmpty())
    {
      return;
    }
    boolean proceed = true;
    ImportNarrativeEnum whatToImport = trimNarrativeHelper.findWhatToImport();
    // keep track of if we've added anything
    boolean dataAdded = false;
    
    TimePeriod outerPeriod = null;

    // find the outer time period - we only load data into the current time period
    if(whatToImport == ImportNarrativeEnum.CANCEL) {
      proceed=false;
      //if cancelled then do nothing.
    }
    else if(whatToImport == ImportNarrativeEnum.ALL_DATA) {
      outerPeriod = null;
    }
    else {
      outerPeriod = outerPeriodFor(_layers);
    }
    
    // ok, now we can loop through the strings
    if(proceed) {
      int ctr = 0;
      for (final String raw_text : strings)
      {
        ctr++;
  
        if (raw_text.trim().length() == 0)
        {
          continue;
        }
  
        // also remove any other control chars that may throw MS Word
        final String text = removeBadChars(raw_text);
  
        // ok, get the narrative type
        final NarrEntry thisN = NarrEntry.create(text, ctr);
  
        if (thisN == null)
        {
          // logError("Unable to parse line:" + text, null);
          continue;
        }
  
        // do we know the outer time period?
        if (outerPeriod != null && thisN.dtg != null)
        {
          // check it's in the currently loaded time period
          if (!outerPeriod.contains(thisN.dtg))
          {
            // System.out.println(thisN.dtg.getDate() + " is not between " +
            // outerPeriod.getStartDTG().getDate() + " and " + outerPeriod.getEndDTG().getDate());
  
            // ok, it's not in our period
            continue;
          }
        }
  
        // is it just text, that we will appned
        if (thisN.appendedToPrevious)
        {
          // hmm, just check if this is an FCS
  
          // do we have a previous one?
          if (_lastEntry != null)
          {
            final String newText = thisN.text;
  
            _lastEntry.setEntry(_lastEntry.getEntry() + "\n" + newText);
          }
  
          // ok, we can't do any more. carry on
          continue;
        }
  
        switch (thisN.type)
        {
          case "FCS":
          {
            // add a narrative entry
            addEntry(thisN);
  
            // create track for this
            try
            {
              addFCS(thisN);
            }
            catch (final StringIndexOutOfBoundsException e)
            {
              // don't worry about panicking, it may not be an FCS after all
            }
            catch (final NumberFormatException e)
            {
              // don't worry about panicking, it may not be an FCS after all
            }
  
            // ok, take note that we've added something
            dataAdded = true;
  
            break;
          }
          default:
          {
            // ok, just add a narrative entry for anything not recognised
  
            // add a narrative entry
            addEntry(thisN);
  
            // ok, take note that we've added something
            dataAdded = true;
  
            break;
  
          }
        }
      }
  
      if (dataAdded)
      {
        _layers.fireModified(getNarrativeLayer());
      }
    }
  }

  private TimePeriod outerPeriodFor(Layers theLayers)
  {
    TimePeriod outerPeriod = null;
    final Enumeration<Editable> layers = theLayers.elements();
    while (layers.hasMoreElements())
    {
      final Layer thisL = (Layer) layers.nextElement();
      if (thisL instanceof WatchableList)
      {
        final WatchableList wl = (WatchableList) thisL;
        if (wl.getStartDTG() != null && wl.getEndDTG() != null)
        {
          final TimePeriod thisP = new TimePeriod.BaseTimePeriod(wl
              .getStartDTG(), wl.getEndDTG());
          if (outerPeriod == null)
          {
            outerPeriod = thisP;
          }
          else
          {
            outerPeriod.extend(wl.getStartDTG());
            outerPeriod.extend(wl.getEndDTG());
          }
        }
      }
    }
    return outerPeriod;
  }

  /**
   * is there a single visible track present?
   *
   * @param layers
   * @param narrativeName
   * @return
   */
  private TrackWrapper singleTrackPresent(final Layers layers,
      final String narrativeName)
  {
    final TrackWrapper res;
    TrackWrapper candidate = null;
    boolean singleCandidate = false;

    // loop through the layers, see if there is a single track present
    final int ctr = layers.size();
    for (int i = 0; i < ctr; i++)
    {
      final Layer thisL = layers.elementAt(i);
      if (thisL.getVisible() && thisL instanceof TrackWrapper)
      {
        // have we already asked about this platform
        final String thisPerm = thisL.getName() + narrativeName;
        if (!askedAbout.contains(thisPerm))
        {
          // nope, go for it

          // ok, have we found one already?
          if (candidate != null)
          {
            // bugger, more than one track. don't bother
            singleCandidate = false;
            break;
          }
          else
          {
            // hey, it's a maybe
            candidate = (TrackWrapper) thisL;

            // remember we've found one
            singleCandidate = true;
          }
        }
      }
    }

    if (singleCandidate)
    {
      res = candidate;
    }
    else
    {
      res = null;
    }

    return res;
  }

  private String trackFor(final String originalName)
  {
    return trackFor(originalName, null);
  }

  private String trackFor(final String originalName, String name)
  {
    if (name == null)
    {
      name = originalName;
    }

    final String platform = name.trim();
    String match = nameMatches.get(platform);
    if (match == null)
    {
      // search the layers
      final Layer theL = _layers.findLayer(platform);
      if (theL != null)
      {
        match = theL.getName();
        nameMatches.put(originalName, match);
      }
      else
      {
        // try skipping then names
        final Iterator<String> nameIter = SkipNames.iterator();
        while (nameIter.hasNext() && match == null)
        {
          final String thisSkip = nameIter.next();
          if (platform.startsWith(thisSkip))
          {
            final String subStr = platform.substring(thisSkip.length()).trim();
            match = trackFor(originalName, subStr);
          }
        }

        // did it work?
        if (match == null)
        {
          // ok, fallback processing.

          // do we have a track that has come straight from WECDIS?
          match = existingWECDISTrack(_layers, name);
        }

        if (match == null)
        {
          // ok, if there is just one track present, invite the user to use that
          final TrackWrapper singleTrack = singleTrackPresent(_layers, name);

          // did we find one?
          if (singleTrack != null)
          {
            // ok, ask the user if he wants to change the subject track to this track's name
            if (questionHelper != null)
            {

              final boolean wantsTo = questionHelper.askYes("Change track name",
                  "Host platform not found for narrative entries.\nDo you want to rename track ["
                      + singleTrack.getName() + "] to [" + name + "]");

              // remember that we've asked about it
              askedAbout.add(singleTrack.getName() + name);

              if (wantsTo)
              {
                singleTrack.setName(name);
                match = name;
              }
            }
          }
          else
          {
            // we can't find a host track.

            // have we already told the user?
            if (!_declaredNoHostFound)
            {
              // ok, stop it appearing again
              _declaredNoHostFound = true;

              // tell the user
              MessageProvider.Base.Provider.show("Import Narrative",
                  "Narrative entries will be imported, but we won't be creating FCSs "
                      + "since we couldn't determine the host track for: "
                      + originalName + ".", MessageProvider.WARNING);

            }
          }
        }
      }
    }

    return match;
  }

}
