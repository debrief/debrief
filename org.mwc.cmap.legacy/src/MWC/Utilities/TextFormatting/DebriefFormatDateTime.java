/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// @Author : Ian Mayo
// @Project: Debrief 3
// @File   : FormatRNDateTime.java

package MWC.Utilities.TextFormatting;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import MWC.GenericData.HiResDate;

public class DebriefFormatDateTime
{
  static public class DebriefFormatTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "CONV";

    public DebriefFormatTest(final String val)
    {
      super(val);
    }
    
    /** todo - fix this. it fails on the CI server, since the server is in a 
     * different time-zone or daylight savings mode
     * @throws ParseException 
     */
    public void testMalformedDate() 
    {
      String message = null;
      try
      {
        parseThis("700001", "010000");
      }
      catch (ParseException e)
      {
        message = e.getMessage();
      }
      assertNotNull("exception not thrown", message);
      
      message = null;
      
      try
      {
        parseThis("700100", "010000");
      }
      catch (ParseException e)
      {
        message = e.getMessage();
      }
      assertNotNull("exception not thrown", message);

      message = null;
    }

    /** todo - fix this. it fails on the CI server, since the server is in a 
     * different time-zone or daylight savings mode
     * @throws ParseException 
     */
    @SuppressWarnings("deprecation")
    public void notTestPadding() throws ParseException
    {
      HiResDate val = parseThis("700101", "010000");
      assertEquals("correct date", new Date(70, 00, 01, 02, 00, 00), val
          .getDate());

      val = parseThis("080101", "010000");
      assertEquals("correct date", new Date(108, 00, 01, 01, 00, 00), val
          .getDate());

      val = parseThis("080101", "010000.005");
      // System.out.println("1:" + val.getDate().getTime() + " 2:" + new
      // Date(70,00,01,01,00,00).getTime());
      Date thisDate = new Date(108, 00, 01, 01, 00, 00);
      thisDate = new Date(thisDate.getTime() + 5);
      assertEquals("correct date", thisDate, val.getDate());

      val = parseThis("080101", "010000.5");
      // System.out.println("1:" + val.getDate().getTime() + " 2:" + new
      // Date(70,00,01,01,00,00).getTime());
      thisDate = new Date(108, 00, 01, 01, 00, 00);
      thisDate = new Date(thisDate.getTime() + 500);
      assertEquals("correct date", thisDate, val.getDate());

      val = parseThis("80101", "10000");
      // System.out.println("1:" + val.getDate().getTime() + " 2:" + new
      // Date(70,00,01,01,00,00).getTime());
      assertEquals("correct date", new Date(108, 00, 01, 01, 00, 00), val
          .getDate());

      val = parseThis("20080101", "10000");
      // System.out.println("1:" + val.getDate().getTime() + " 2:" + new
      // Date(70,00,01,01,00,00).getTime());
      assertEquals("correct date", new Date(108, 00, 01, 01, 00, 00), val
          .getDate());

      val = parseThis("20080101", "200");
      // System.out.println("1:" + val.getDate().getTime() + " 2:" + new
      // Date(70,00,01,01,00,00).getTime());
      assertEquals("correct date", new Date(108, 00, 01, 00, 02, 00), val
          .getDate());
    }

    public void testPadding2()
    {
      assertEquals("000001", padToken("1"));
      assertEquals("001001", padToken("1001"));
      assertEquals("101001", padToken("101001"));
    }

    public void testValues()
    {
      Date newDTG = new Date(1000);
      String res = DebriefFormatDateTime.toString(newDTG.getTime());
      assertEquals("matches", "700101 000001", res);

      newDTG = new Date(1);
      res = DebriefFormatDateTime.toString(newDTG.getTime());
      assertEquals("matches", "700101 000000.001", res);

      HiResDate hi = new HiResDate(1000);
      res = DebriefFormatDateTime.toStringHiRes(hi);
      assertEquals("matches", "700101 000001", res);

      hi = new HiResDate(1);
      res = DebriefFormatDateTime.toStringHiRes(hi);
      assertEquals("matches", "700101 000000.001", res);

      hi = new HiResDate(0, 1000);
      res = DebriefFormatDateTime.toStringHiRes(hi);
      assertEquals("matches", "700101 000000.001", res);

      hi = new HiResDate(2, 1000);
      res = DebriefFormatDateTime.toStringHiRes(hi);
      assertEquals("matches", "700101 000000.003", res);

      hi = new HiResDate(0, 11);
      res = DebriefFormatDateTime.toStringHiRes(hi);
      assertEquals("matches", "700101 000000.000011", res);

    }
  }

  private static DateFormat _dfMillis = null;
  private static DateFormat _df = null;
  private static NumberFormat _micros = null;
  private static NumberFormat _millis = null;
  private static final DateFormat FOUR_DIGIT_YEAR_FORMAT =
      new GMTDateFormat("yyyyMMdd HHmmss");

  private static final DateFormat TWO_DIGIT_YEAR_FORMAT = new GMTDateFormat(
      "yyMMdd HHmmss");

  /**
   * there are also some instances where invalid dates have crept in, possibly related to Debrief
   * storing 0 and trying to write this to disk. Problem probably occured during Hi-Res times
   * transition.
   */
  // private static final String INVALID_DATE_STRING = "700101 000000";

  /**
   * the string which in the past could appear, when the intention of the software was to store a
   * null string
   */
  private static final String NULL_DATE_STRING = "691231 235959.999";

  /**
   * formatting method which just exports the micro-seconds within a DTG
   * 
   * @param dtg
   * @return
   */
  public static String formatMicros(final HiResDate dtg)
  {
    // check our declarations
    initialisePatterns();
    return _micros.format(dtg.getMicros() % 1000000);
  }

  /**
   * we use static instances of patterns. just initialise them once
   * 
   */
  private synchronized static void initialisePatterns()
  {
    if (_dfMillis == null)
    {
      _dfMillis = new GMTDateFormat("yyMMdd HHmmss.SSS");
      _df = new GMTDateFormat("yyMMdd HHmmss");

      // and the microsecond bits
      _micros = new DecimalFormat("000000");
      _millis = new DecimalFormat("000");
    }
  }

  private static String padToken(final String token)
  {
    final String res;
    if (token.length() == 6)
    {
      res = token;
    }
    else
    {
      final int numMissing = 6 - token.length();
      final StringBuffer buffer = new StringBuffer(6);
      for (int i = 0; i < numMissing; i++)
      {
        buffer.append("0");
      }
      buffer.append(token);
      res = buffer.toString();
    }
    return res;
  }

  /**
   * parse a date string using our format
   * @throws ParseException on malformed date
   */
  public synchronized static HiResDate parseThis(final String rawText) throws ParseException
  {
    // make sure our two and four-digit date bits are initialised
    initialisePatterns();

    Date date = null;
    HiResDate res = null;

    // right, start off by trimming spaces off the date
    final String theRawText = rawText.trim();

    // right. Special check to see if this is an incorrectly represented null
    // date (-1)
    // if (theRawText.equals(NULL_DATE_STRING) || theRawText.equals(INVALID_DATE_STRING))
    if (theRawText.equals(NULL_DATE_STRING))
    {
      System.err.println("Invalid date read from xml file: " + theRawText);
      res = null;
    }
    else
    {

      String secondPart = theRawText;
      String subSecondPart = null;

      // start off by seeing if we have sub-millisecond date
      final int subSecondIndex = theRawText.indexOf('.');
      if (subSecondIndex > 0)
      {
        // so, there is a separator - extract the text before the separator
        secondPart = theRawText.substring(0, subSecondIndex);

        // just check that the '.' isn't the last character
        if (subSecondIndex < theRawText.length() - 1)
        {
          // yes, we do have digits after the separator
          subSecondPart = theRawText.substring(subSecondIndex + 1);
        }
      }

      // next determine if we have a 4-figure year value (in which case the
      // space will be in column 9
      final int spaceIndex = secondPart.indexOf(" ");
      
      if (spaceIndex > 6)
      {
        date = FOUR_DIGIT_YEAR_FORMAT.parse(secondPart);
      }
      else
      {
        date = TWO_DIGIT_YEAR_FORMAT.parse(secondPart);
      }

      int micros = 0;

      // do we have a sub-second part?
      if (subSecondPart != null)
      {
        // get the value
        micros = Integer.parseInt(subSecondPart);

        final int subSecLen = subSecondPart.length();

        // are we within the acceptable data resolution?
        if (subSecLen <= 6)
        {
          micros = micros * (int) (Math.pow(10, 6 - subSecLen));
        }
        else
        {
          System.err
              .println("Debrief is only capable of reading data to microsecond resolution (dtg:"
                  + theRawText + ")");
          micros = -1;
        }
      }

      if (micros != -1)
      {
        if (date != null)
        {
          res = new HiResDate(date.getTime(), micros);
        }
      }
    }

    return res;
  }

  /**
   * parse a date string using our format
   * @throws ParseException  on malformed date
   */
  public static HiResDate parseThis(final String dateToken,
      final String timeToken) throws ParseException
  {
    // do we have millis?
    final int decPoint = timeToken.indexOf(".");
    String milliStr, timeStr;
    if (decPoint > 0)
    {
      milliStr = timeToken.substring(decPoint, timeToken.length());
      timeStr = timeToken.substring(0, decPoint);
    }
    else
    {
      milliStr = "";
      timeStr = timeToken;
    }

    // sort out if we have to padd
    // check the date for missing leading zeros
    final String theDateToken = padToken(dateToken);
    timeStr = padToken(timeStr);

    final String composite = theDateToken + " " + timeStr + milliStr;

    return parseThis(composite);
  }

  static public String toString(final long theVal)
  {
    initialisePatterns();

    final java.util.Date theTime = new java.util.Date(theVal);
    String res;

    // first determine which pattern to use.
    DateFormat selectedFormat;
    if (theVal % 1000 > 0)
    {
      // ok, it contains milliseconds - include them in the output
      selectedFormat = _dfMillis;
    }
    else
    {
      selectedFormat = _df;
    }

    res = selectedFormat.format(theTime);

    return res;
  }

  /**
   * output the hi-res date as a formatted string, supplying micro-second and milli-second decimal
   * places as required.
   * 
   * @param time
   *          - can't imagine. What-ever could this parameter be called for?
   * @return formatted string
   */
  public static String toStringHiRes(final HiResDate time)
  {
    // check our declarations
    initialisePatterns();

    // so, have a look at the data
    long micros = time.getMicros();

    final long wholeSeconds = micros / 1000000;

    final StringBuffer res = new StringBuffer();
    res.append(toString(wholeSeconds * 1000));

    // do we have micros?
    if (micros % 1000 > 0)
    {
      // yes
      res.append(".");
      res.append(_micros.format(micros % 1000000));
    }
    else
    {
      // do we have millis?
      if (micros % 1000000 > 0)
      {
        // yes, convert the value to millis

        final long millis = micros = (micros % 1000000) / 1000;

        res.append(".");
        res.append(_millis.format(millis));
      }
      else
      {
        // just use the normal output
      }
    }

    return res.toString();

  }

  /**
   * output the hi-res date as a formatted string, supplying micro-second and milli-second decimal
   * places as required.
   * 
   * @param time
   *          - can't imagine. What-ever could this parameter be called for?
   * @return formatted string
   */
  public static String toStringHiRes(final HiResDate time,
      final String formatStr)
  {
    String res;

    // hmm, see if we are actually working in micros
    final long micros = time.getMicros();
    if (micros % 1000 > 0)
    {
      res = toStringHiRes(time);
    }
    else
    {
      final DateFormat myDF = new GMTDateFormat(formatStr);
      res = myDF.format(time.getDate());
    }

    // cool, all finished
    return res;
  }

}
