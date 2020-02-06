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

// $RCSfile: ImportSensor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: ImportSensor.java,v $
// Revision 1.7  2006/02/13 16:19:06  Ian.Mayo
// Sort out problem with creating sensor data
//
// Revision 1.6  2006/01/06 10:36:40  Ian.Mayo
// Reflect tidying of sensor wrapper naming
//
// Revision 1.5  2005/12/13 09:04:38  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2004/11/25 10:24:18  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/08/19 14:12:48  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.2  2004/07/06 13:35:22  Ian.Mayo
// Correct class naming typo
//
// Revision 1.1.1.2  2003/07/21 14:47:52  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-05-06 09:09:16+01  ian_mayo
// Corrected javadoc
//
// Revision 1.3  2003-03-19 15:37:28+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:16+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:09+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:52+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:40+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-02-26 16:36:29+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.3  2001-08-24 16:35:11+01  administrator
// Keep the strings tidy
//
// Revision 1.2  2001-08-24 09:53:48+01  administrator
// Modified to reflect new way of representing null data in Sensor line
//
// Revision 1.1  2001-08-23 11:41:30+01  administrator
// first attempt at handling null position values
//
// Revision 1.0  2001-08-13 12:50:12+01  administrator
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:29+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 13:23:45+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.1  2001-01-03 13:40:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:47:04  ianmayo
// initial import of files
//
// Revision 1.2  2000-10-09 13:37:41+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.1  2000-09-26 10:57:40+01  ian_mayo
// Initial revision
//
//

package Debrief.ReaderWriter.Replay;

import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import junit.framework.TestCase;

/**
 * class to parse a label from a line of text
 */
final class ImportSensor extends AbstractPlainLineImporter
{
  public static class TestImport extends TestCase
  {

    static public final String TEST_ALL_TEST_TYPE = "CONV";

    public void testValues1() throws ParseException
    {
      // ;SENSOR: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B RRR XXX YYY.....YYY

      final String iLine =
          ";SENSOR: 100112 121314 T23 @A NULL 90.0 40503.2 \"Plain Cookie\" FisherOne held on Plain Cookie";
      final AbstractPlainLineImporter iff = new ImportSensor();
      final SensorContactWrapper res = (SensorContactWrapper) iff.readThisLine(
          iLine);

      assertNotNull("Read it in", res);

      // and check the result
      assertNotNull("read it in", res);
      assertEquals("right track", "FisherOne held on Plain Cookie", res
          .getLabel());
      assertEquals("right color", new java.awt.Color(0, 100, 189), res
          .getColor());
      assertEquals("right sensor", "Plain Cookie", res.getSensorName());
      assertEquals("correct date", "1213", FormatRNDateTime.toShortString(res
          .getDTG().getDate().getTime()));
      assertEquals("correct date", "121213.14", FormatRNDateTime.toString(res
          .getDTG().getDate().getTime()));
      assertEquals("correct bearing", 90d, res.getBearing(), 0.01);
      assertEquals("correct range", 40503.2, res.getRange().getValueIn(
          WorldDistance.YARDS), 0.001);
      assertEquals("correct track", "T23", res.getTrackName());
      assertEquals("right name", "100112 121314", res.getName());
      assertEquals("correct color", DebriefColors.BLUE, res.getColor());
    }

    public void testValuesNANBearing() throws ParseException
    {
      // ;SENSOR: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B RRR XXX YYY.....YYY

      final String iLine =
          ";SENSOR: 100112 121314 T23 @A NULL NAN 40503.2 \"Plain Cookie\" FisherOne held on Plain Cookie";
      final AbstractPlainLineImporter iff = new ImportSensor();
      final SensorContactWrapper res = (SensorContactWrapper) iff.readThisLine(
          iLine);

      assertNotNull("Read it in", res);

      // and check the result
      assertNotNull("read it in", res);
      assertEquals("right track", "FisherOne held on Plain Cookie", res
          .getLabel());
      assertEquals("right color", new java.awt.Color(0, 100, 189), res
          .getColor());
      assertEquals("right name", "100112 121314", res.getName());
    }

    public void testValuesNullBearing() throws ParseException
    {
      // ;SENSOR: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B RRR XXX YYY.....YYY

      final String iLine =
          ";SENSOR: 100112 121314 T23 @A NULL NULL 40503.2 \"Plain Cookie\" FisherOne held on Plain Cookie";
      final AbstractPlainLineImporter iff = new ImportSensor();
      final SensorContactWrapper res = (SensorContactWrapper) iff.readThisLine(
          iLine);

      assertNotNull("Read it in", res);

      // and check the result
      assertNotNull("read it in", res);
      assertEquals("right track", "FisherOne held on Plain Cookie", res
          .getLabel());
      assertEquals("right color", new java.awt.Color(0, 100, 189), res
          .getColor());
      assertEquals("right name", "100112 121314", res.getName());
    }

  }

  /**
   * the type for this string
   */
  private final String _myType = ";SENSOR:";

  /**
   * indicate if you can export this type of object
   *
   * @param val
   *          the object to test
   * @return boolean saying whether you can do it
   */
  @Override
  public final boolean canExportThis(final Object val)
  {
    boolean res = false;

    if (val instanceof SensorWrapper)
    {
      res = true;
    }

    return res;

  }

  /**
   * export the specified shape as a string
   *
   * @param theWrapper
   *          the thing we are going to export
   * @return the shape in String form
   */
  @Override
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    // result value
    final String line = ";; Export of sensor data not implemented";
    return line;

  }

  /**
   * determine the identifier returning this type of annotation
   */
  @Override
  public final String getYourType()
  {
    return _myType;
  }

  /**
   * read in this string and return a Label
   *
   * @throws ParseException
   */
  @Override
  public final Object readThisLine(final String theLine) throws ParseException
  {

    // ;SENSOR: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B RRR XXX YYY.....YYY

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String theTrack;
    String sensorName;
    WorldLocation origin = null;
    HiResDate theDtg = null;
    final Double brg;
    final double rng;
    java.awt.Color theColor;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // get the (possibly multi-word) track name
    theTrack = AbstractPlainLineImporter.checkForQuotedName(st);

    // start with the symbology
    symbology = st.nextToken(normalDelimiters);

    // now the sensor offsets
    final String next = st.nextToken().trim();

    try
    {
      // find out if it's our null value
      if (!next.startsWith("N"))
      {
        // get the deg out of this value
        final double latDeg = MWCXMLReader.readThisDouble(next);

        // ok, this is valid data, persevere with it
        final double latMin = MWCXMLReader.readThisDouble(st.nextToken());
        final double latSec = MWCXMLReader.readThisDouble(st.nextToken());

        /**
         * now, we may have trouble here, since there may not be a space between the hemisphere
         * character and a 3-digit latitude value - so BE CAREFUL
         */
        final String vDiff = st.nextToken();
        final double longDeg;
        final char latHem;
        if (vDiff.length() > 3)
        {
          // hmm, they are combined
          latHem = vDiff.charAt(0);
          final String secondPart = vDiff.substring(1, vDiff.length());
          longDeg = MWCXMLReader.readThisDouble(secondPart);
        }
        else
        {
          // they are separate, so only the hem is in this one
          latHem = vDiff.charAt(0);
          longDeg = MWCXMLReader.readThisDouble(st.nextToken());
        }

        final double longMin = MWCXMLReader.readThisDouble(st.nextToken());
        final double longSec = MWCXMLReader.readThisDouble(st.nextToken());
        final char longHem = st.nextToken().charAt(0);

        // create the origin
        origin = new WorldLocation(latDeg, latMin, latSec, latHem, longDeg,
            longMin, longSec, longHem, 0);
      } // whether the duff origin data was entered

      final String brgStr = st.nextToken();
      if (brgStr.startsWith("N"))
      {
        brg = null;
      }
      else
      {
        brg = MWCXMLReader.readThisDouble(brgStr);
      }

      final String rangeStr = st.nextToken();
      if (rangeStr.startsWith("N"))
      {
        rng = 0;
      }
      else
      {
        rng = MWCXMLReader.readThisDouble(rangeStr);
      }

      // only store a sensor range if a legitimate one was passed in
      final WorldDistance sensorRng;
      if (rng != 0)
        sensorRng = new WorldDistance(rng, WorldDistance.YARDS);
      else
        sensorRng = null;

      // get the (possibly multi-word) track name
      sensorName = AbstractPlainLineImporter.checkForQuotedName(st);

      // and ditch some whitespace
      sensorName = sensorName.trim();

      // and lastly read in the message (to the end of the line)
      final String labelTxt;
      if (st.hasMoreTokens())
      {
        labelTxt = st.nextToken("\r");
      }
      else
      {
        labelTxt = null;
      }

      theColor = ImportReplay.replayColorFor(symbology);

      final int theStyle = ImportReplay.replayLineStyleFor(symbology);

      // create the contact object
      final SensorContactWrapper data = new SensorContactWrapper(theTrack,
          theDtg, sensorRng, brg, origin, theColor, null, theStyle, sensorName);

      // sort out the (optional) comment
      data.setLabel(ImportReplay.getLabel(labelTxt));
      data.setComment(ImportReplay.getComment(labelTxt));

      return data;
    }
    catch (final ParseException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe, "Whilst import sensor");
      return null;
    }
  }

}
