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
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

public class ImportASWDataDocument
{
  /**
   * helper that can ask the user a question
   *
   */
  public static interface QuestionHelper
  {
    String getTrackName(String title, String message);
  }

  public static class TestImportWord extends TestCase
  {
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

    @SuppressWarnings("unused")
    public void testParseFixes()
    {
      final String d1 =
          "TMPOS/261200ZAPR/IN/12.23.34N-121.12.1E/057T/04KTS/05.1M//";
      final String d2 =
          "TMPOS/262327ZAPR/INS/13.14.15S-011.22.33W/180T/13KTS/000FT";
      final String d3 = "TMPOS/220000ZAPR/GPS/1230N/01215W";
      final String d4 =
          "TMPOS/230001ZAPR/GPS/22.33.44N/020.11.22W/006T/10KTS//";
      final String d5 = "TMPOS/290000ZAPR/GPS/04 .02. 01N/111.22. 11W/89T/9KTS";
      // repeat of last line, but with spaces removed
      final String d6 = "TMPOS/290000ZAPR/GPS/04.02.01N/111.22.11W/89T/9KTS";
      
      final FixWrapper f1 = fixFor(d1);
      assertNotNull(f1);
      assertEquals(57d, f1.getCourseDegs());
      assertEquals(4d, f1.getSpeed());
      assertEquals(5.1d, f1.getDepth());
      
      assertEquals("190426 120000", DebriefFormatDateTime.toStringHiRes(f1.getDateTimeGroup()));
    }

    public void testParseLocations()
    {
      assertNull("fails for empty", locationFor(null));

      final String d1 = "12.23.34N-121.12.1E";
      final String d2 = "13.14.15S-011.22.33W";
      final String d3 = "1230N/01215W";
      final String d4 = "22.33.44N/020.11.22W";
      final String d5 = "04 .02. 01N/111.22. 11W";
      // repeat of last line, but with spaces removed
      final String d6 = "04.02.01N/111.22.11W";

      assertEquals(" 12°23'34.00\"N 121°12'01.00\"E " , locationFor(d1).toString());
      assertEquals(" 13°14'15.00\"S 011°22'33.00\"W " , locationFor(d2).toString());
      assertEquals(" 12°30'00.00\"N 012°15'00.00\"W " , locationFor(d3).toString());
      assertEquals(" 22°33'44.00\"N 020°11'22.00\"W " , locationFor(d4).toString());
      assertEquals(" 04°02'01.00\"N 111°22'11.00\"W " , locationFor(d5).toString());
      assertEquals(" 04°02'01.00\"N 111°22'11.00\"W " , locationFor(d6).toString());
      assertEquals(" 22°33'00.00\"N 020°11'00.00\"W " , locationFor("22.33N-020.11W").toString());
      
      // and some mangled one
      try {
        locationFor("22.33.44J/020.11.22W");
        fail("should have tripped");
      }
      catch(IllegalArgumentException ie)
      {
        assertEquals("Not a valid hemisphere:J", ie.getMessage());
      }
      
      // and some mangled one
      try {
        locationFor("22.33.44N=020.11.22W");
        fail("should have tripped");
      }
      catch(IllegalArgumentException ie)
      {
        assertEquals("Location separator not found", ie.getMessage());
      }

      // and some mangled one
      try {
        locationFor("22.33.44N/020.11.22");
        fail("should have tripped");
      }
      catch(IllegalArgumentException ie)
      {
        assertEquals("Not a valid hemisphere:2", ie.getMessage());
      }
    }

    public void testDates()
    {
      final String d1 = "261200ZAPR";
      final String d2 = "262327ZAPR";
      final String d3 = "220300ZJAN";
      final String d4 = "012345ZAPR";
      final String d5 = "292258ZNOV";

      assertEquals("aaa", "190426 120000", DebriefFormatDateTime.toString(dateFor(d1, 0).getDate().getTime()));
      assertEquals("aaa", "190426 232700", DebriefFormatDateTime.toString(dateFor(d2, 0).getDate().getTime()));
      assertEquals("aaa", "190122 030000", DebriefFormatDateTime.toString(dateFor(d3, 0).getDate().getTime()));
      assertEquals("aaa", "190401 234500", DebriefFormatDateTime.toString(dateFor(d4, 0).getDate().getTime()));
      assertEquals("aaa", "191229 225800", DebriefFormatDateTime.toString(dateFor(d5, 0).getDate().getTime()));
    }

    @SuppressWarnings("unused")
    private static WorldLocation loc(final double lat, final double lon,
        final double dep)
    {
      return new WorldLocation(lat, lon, dep);
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

      assertEquals(12.5125d, getValueFor(d1));
      assertEquals(11.5125d, getValueFor(d2));
      assertEquals(115.75, getValueFor(d3a));
      assertEquals(15.75, getValueFor(d3b));
      assertEquals(22.5125, getValueFor(d4));
      assertEquals(4.5125, getValueFor(d5));
      assertEquals(104.5125, getValueFor(d6));
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

    public void testIsValid()
    {
      List<String> input = new ArrayList<String>();
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
  }

  /**
   * helper class that can ask the user a question populated via Dependency Injection
   */
  private static QuestionHelper questionHelper = null;

  /**
   * match a 6 figure DTG
   *
   */
  static final String DATE_MATCH_SIX = "(\\d{6})";

  static final String DATE_MATCH_FOUR = "(\\d{4})";

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

  public static void setQuestionHelper(final QuestionHelper helper)
  {
    questionHelper = helper;
  }

  /**
   * check if the layers contains a single track object
   *
   * @param layers
   * @return
   */
  private static boolean singleTrackIn(final Layers layers)
  {
    int ctr = 0;
    final int len = layers.size();
    for (int i = 0; i < len; i++)
    {
      final Layer next = layers.elementAt(i);
      if (next instanceof TrackWrapper)
      {
        ctr++;

        if (ctr > 1)
        {
          break;
        }
      }
    }
    return ctr == 1;
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
   * repeatably find a color for the specified track id The color will be RED if it's a master track
   * ("M01"). Shades of gray nor ownship blue are returned.
   *
   * @param trackId
   * @return
   */
  private static Color colorFor(final String trackId)
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

  public static void logError(final int status, final String msg,
      final Exception e)
  {
    logThisError(status, msg, e);
  }

  /**
   * parse a list of strings
   *
   * @param strings
   */
  @SuppressWarnings("unused")
  public void processThese(final ArrayList<String> strings)
  {

    if (strings.isEmpty())
    {
      return;
    }
    boolean proceed = true;

    // keep track of if we've added anything
    boolean dataAdded = false;

    // ok, now we can loop through the strings
    if (proceed)
    {
      int ctr = 0;
      TrackWrapper track = null;
      for (final String raw_text : strings)
      {
        // increment counter, for num lines processed
        ctr++;

        // also remove any other control chars that may throw MS Word
        final String text = removeBadChars(raw_text);

        // process it
      }

      if (dataAdded)
      {
        _layers.fireModified(track);
      }
    }
  }

  final private static String marker = "TMPOS";

  private static HiResDate dateFor(final String str, int year)
  {
    DateFormat df = new GMTDateFormat("ddHHmm'Z'MMM");
    HiResDate res = null;
    try
    {
      Date date = df.parse(str);
      
      if(year > 100)
      {
        date.setYear(year);
      }
      else
      {
        date.setYear(new Date().getYear());
      }
      
      res = new HiResDate(date);
    }
    catch (ParseException e)
    {
      logError(ToolParent.ERROR, "While Parsing:" + str, e);
    }
    return res;
  }

  private static double getHemiFor(String str)
  {
    final String lastLetter =  str.substring(str.length() - 1);
    final double res;
    switch(lastLetter.toUpperCase())
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
        throw new IllegalArgumentException("Not a valid hemisphere:" + lastLetter);
    }
    
    return res;
  }

  private static WorldLocation locationFor(final String str)
  {
    // 00.00.0N-000.00.0W
    // 00.00.00N-000.00.00W
    // ddmmN/dddmmW
    // 00.00.00N/000.00.00W
    // 00 .00. 00N/000.00. 00W

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

    String latStr = components[0];
    double latHemi = getHemiFor(latStr);
    double latVal = getValueFor(latStr.substring(0, latStr.length()-1));

    String longStr = components[1];
    double longHemi = getHemiFor(longStr);
    double longVal = getValueFor(longStr.substring(0, longStr.length()-1));
    
    return new WorldLocation(latHemi * latVal, longHemi * longVal, 0d);
  }

  private static double getValueFor(final String str)
  {
//    final String d1 = "12.23.34";
//    final String d2 = "011.22.33";
//    final String d3 = "01215";
//    final String d4 = "22.33.44";
//    final String d5 = "04 .02. 01";
//    // repeat of last line, but with spaces removed
//    final String d6 = "04.02.01";
    
    final double res;
    if(str.contains("."))
    {
      String[] comps = str.split("\\.");
      if(comps.length == 3)
      {
        final double degs = Double.valueOf(comps[0].trim());
        final double mins = Double.valueOf(comps[1].trim());
        final double secs = Double.valueOf(comps[2].trim());
        res = degs + mins / 60 + secs / (60 * 60);
      }
      else if(comps.length == 2)
      {
        final double degs = Double.valueOf(comps[0].trim());
        final double mins = Double.valueOf(comps[1].trim());
        res = degs + mins / 60;
      }
      else
      {
        throw new IllegalArgumentException("Badly formatted location:" + str);
      }
    }
    else
    {
      final int len = str.length();
      final double degs = Double.parseDouble(str.substring(0, len - 2));
      final double mins = Double.parseDouble(str.substring(len - 2));
      res = degs + mins / 60;
    }
    
    return res;
  }
  
  @SuppressWarnings(
  {"unused"})
  private static FixWrapper fixFor(final String str)
  {
//    final String d1 =
//        "TMPOS/261200ZAPR/IN/12.23.34N-121.12.1E/057T/04KTS/00.0M//";
//    final String d2 =
//        "TMPOS/262327ZAPR/INS/13.14.15S-011.22.33W/180T/13KTS/000FT";
//    final String d3 = "TMPOS/220000ZAPR/GPS/1230N/01215W";
//    final String d4 =
//        "TMPOS/230001ZAPR/GPS/22.33.44N/020.11.22W/006T/10KTS//";
//    final String d5 = "TMPOS/290000ZAPR/GPS/04 .02. 01N/111.22. 11W/89T/9KTS";
//    // repeat of last line, but with spaces removed
//    final String d6 = "TMPOS/290000ZAPR/GPS/04.02.01N/111.22.11W/89T/9KTS";

    
    if (str == null)
    {
      return null;
    }
    else if (!str.startsWith("TMPOS"))
    {
      throw new IllegalArgumentException("Not a position line");
    }
    else
    {
      String[] tokens = str.split("/");
      final int numTokens = tokens.length;
      
      if(numTokens < 5)
      {
        throw new IllegalArgumentException("Insufficient token in string (expected 5):" + str);
      }

      int ctr = 0;
      String title = tokens[ctr++];
      HiResDate date = dateFor(tokens[ctr++], 0);
      String source = tokens[ctr++];
      String pos1 = tokens[ctr++];
      if(!pos1.contains("-"))
      {
        // ok, it's split across two tokens
        String pos2 = tokens[ctr++];
        pos1 = pos1 + "/" + pos2;
      }
      WorldLocation loc = locationFor(pos1);
      
      String courseStr = ctr < numTokens ? tokens[ctr++] : null;
      String speedStr = ctr < numTokens ? tokens[ctr++] : null;
      String depthStr = ctr < numTokens ? tokens[ctr++] : null;
     
      final double course = courseFor(courseStr);
      final double speed = speedFor(speedStr);
      final double depth = depthFor(depthStr);
      
      loc.setDepth(depth);
      Fix fix = new Fix(date, loc, course, speed);
      return new FixWrapper(fix);
    }
  }

  private static double speedFor(String speed)
  {
    if(speed == null)
      return 0d;
    
    // regex came from here:
    // https://codereview.stackexchange.com/a/2349
    String[] components = speed.split("[^A-Z0-9]+|(?<=[A-Z])(?=[0-9])|(?<=[0-9])(?=[A-Z])");
    if(components.length != 2)
    {
      throw new IllegalArgumentException("Speed not formatted correctly (expected 23KTS):" + speed);
    }
    
    final double value = Double.valueOf(components[0]);
        final String units = components[1];
    
    final WorldSpeed res;
    switch(units.toUpperCase())
    {
      case "KTS":
        res = new WorldSpeed(value, WorldSpeed.Kts);
        break;
      case "MS":
        res = new WorldSpeed(value, WorldSpeed.M_sec);
        break;
      default:
        throw new IllegalArgumentException("Speed units not recognised:" + speed);
    }
    
    return res.getValueIn(WorldSpeed.ft_sec) / 3d;
  }

  private static double depthFor(String depth)
  {
    if(depth == null)
      return 0d;
    
    final String units = depth.substring(depth.length()-1);
    final double value = Double.valueOf(depth.substring(0,  depth.length()-1));
    final WorldDistance res;
    switch(units.toUpperCase())
    {
      case "M":
        res = new WorldDistance(value, WorldDistance.METRES);
        break;
      case "FT":
        res = new WorldDistance(value, WorldDistance.FT);
        break;
      default:
        throw new IllegalArgumentException("Depth units not recognised:" + depth);
    }
    
    return res.getValueIn(WorldDistance.METRES);
  }
  
  private static double courseFor(String courseStr)
  {
    if(courseStr == null)
      return 0d;
    
    int tIndex = courseStr.indexOf("T");
    if(tIndex == -1)
    {
      throw new IllegalArgumentException("Course not formatted correctly (expect 123T):" + courseStr);
    }
    
    double courseDegs = Double.parseDouble(courseStr.substring(0, tIndex));
    return MWC.Algorithms.Conversions.Degs2Rads(courseDegs);
  }

  private static Integer yearFor(String str)
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
      String[] tokens = str.split("/");
      for (final String s : tokens)
      {
        if (s.length() == 4 && s.matches("^-?\\d+$"))
        {
          final int year = Integer.parseInt(s);
          if (year > 1000 && year < 3000)
          {
            return year;
          }
        }
      }
      return null;
    }
  }

  public static boolean canImport(List<String> strings)
  {
    // run through strings, see if we have TMPOS in more than 5 lines in the first 50
    final int numLines = 100;
    final int requiredLines = 5;
    int matches = 0;
    int lines = 0;
    for (final String l : strings)
    {
      lines++;

      if (l.startsWith(marker))
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

}
