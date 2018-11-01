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
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
public final class ImportSensor2 extends AbstractPlainLineImporter
{
  /**
   * the type for this string
   */
  private final String _myType = ";SENSOR2:";

  /**
   * read in this string and return a Label
   * @throws ParseException 
   */
  public final Object readThisLine(final String theLine) throws ParseException
  {

    // ;SENSOR2: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B CCC.C FFF.F RRRR
    // yy..yy xx..xx
    // ;; date, ownship name, symbology, sensor lat/long (or the single word NULL), bearing (degs),
    // ambigous bearing (degs)
    // [or the single word NULL], frequency(Hz), range(yds), sensor name, label (to end of
    // line)</markup>

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String theTrack;
    String sensorName;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    WorldLocation origin = null;
    HiResDate theDtg = null;
    Double brg = null;
    WorldDistance rng = null;
    Double brg2 = null, freq = null;
    java.awt.Color theColor;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // get the (possibly multi-word) track name
    theTrack = ImportFix.checkForQuotedName(st);

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
        latDeg = MWCXMLReader.readThisDouble(next);

        // ok, this is valid data, persevere with it
        latMin = MWCXMLReader.readThisDouble(st.nextToken());
        latSec = MWCXMLReader.readThisDouble(st.nextToken());

        /**
         * now, we may have trouble here, since there may not be a space between the hemisphere
         * character and a 3-digit latitude value - so BE CAREFUL
         */
        final String vDiff = st.nextToken();
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

        longMin = MWCXMLReader.readThisDouble(st.nextToken());
        longSec = MWCXMLReader.readThisDouble(st.nextToken());
        longHem = st.nextToken().charAt(0);

        // create the origin
        origin =
            new WorldLocation(latDeg, latMin, latSec, latHem, longDeg, longMin,
                longSec, longHem, 0);
      } // whether the duff origin data was entered

      // get the bearing
      final String brgStr = st.nextToken();
      if (!brgStr.startsWith("N"))
      {
        // cool, we have data
        brg = new Double(MWCXMLReader.readThisDouble(brgStr));
      }

      // and now get the ambiguous bearing
      String tmp = st.nextToken();
      if (!tmp.startsWith("N"))
      {
        // cool, we have data
        brg2 = new Double(MWCXMLReader.readThisDouble(tmp));
      }

      // and the frequency
      tmp = st.nextToken();
      if (!tmp.startsWith("N"))
      {
        // cool, we have data
        freq = new Double(MWCXMLReader.readThisDouble(tmp));
      }

      // and the range
      tmp = st.nextToken();
      if (!tmp.startsWith("N"))
      {
        // cool, we have data
        rng =
            new WorldDistance(new Double(MWCXMLReader.readThisDouble(tmp)),
                WorldDistance.YARDS);
      }

      // get the (possibly multi-word) track name
      sensorName = ImportFix.checkForQuotedName(st);

      // trim the sensor name
      sensorName = sensorName.trim();

      // and lastly read in the message (to the end of the line)
      final String labelTxt = st.nextToken("\r");

      theColor = ImportReplay.replayColorFor(symbology);

      final int theStyle = ImportReplay.replayLineStyleFor(symbology);

      // create the contact object
      final SensorContactWrapper data =
          new SensorContactWrapper(theTrack, theDtg, rng, brg, brg2, freq,
              origin, theColor, null, theStyle, sensorName);

      // sort out the (optional) comment
      data.setLabel(ImportReplay.getLabel(labelTxt));
      data.setComment(ImportReplay.getComment(labelTxt));

      return data;
    }
    catch (final ParseException pe)
    {
      MWC.Utilities.Errors.Trace.trace(pe, "Whilst import sensor2");
      return null;
    }
  }

  /**
   * determine the identifier returning this type of annotation
   */
  public final String getYourType()
  {
    return _myType;
  }

  /**
   * export the specified shape as a string
   * 
   * @param theWrapper
   *          the thing we are going to export
   * @return the shape in String form
   */
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    // result value
    final String line = ";; Export of sensor data not implemented";
    return line;

  }

  /**
   * indicate if you can export this type of object
   * 
   * @param val
   *          the object to test
   * @return boolean saying whether you can do it
   */
  public final boolean canExportThis(final Object val)
  {
    boolean res = false;

    if (val instanceof SensorWrapper)
    {
      res = true;
    }

    return res;

  }

  // ;SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL Contact_bearings 0414

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testMe(final String val)
    {
      super(val);
    }

    public final void testInvalidImport() 
    {
      final String lineA =
          ";SENSOR2: 20090022 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL Contact_bearings 0414";

      final ImportSensor2 is2 = new ImportSensor2();
      SensorContactWrapper resA = null;
      try
      {
        resA = (SensorContactWrapper) is2.readThisLine(lineA);
      }
      catch (ParseException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      assertNull("should have failed", resA);
    }

    
    public final void testImport() throws ParseException
    {
      final String lineA =
          ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL Contact_bearings 0414";
      final String lineB =
          ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL \"Contact bearings\" 0414";
      final String lineTabs =
          ";SENSOR2:	20090722	041434.000	NONSUCH	@B	NULL	59.3	300.8	49.96	NULL	\"Contact bearings\"	0414";
      final String lineD =
          ";SENSOR2: 20090722 041434.000 \"NON SUCH\" @B NULL 59.3 NULL NULL NULL \"Contact bearings\" 0414";

      final ImportSensor2 is2 = new ImportSensor2();
      final SensorContactWrapper resA =
          (SensorContactWrapper) is2.readThisLine(lineA);
      assertEquals("lineA failed", "Contact_bearings", resA
          .getSensorName());
      assertEquals("lineA failed", "0414", resA.getLabel());
      final SensorContactWrapper resB =
          (SensorContactWrapper) is2.readThisLine(lineB);
      assertEquals("lineB failed", "0414", resB.getLabel());
      final SensorContactWrapper resC =
          (SensorContactWrapper) is2.readThisLine(lineTabs);
      assertEquals("lineTabs failed", "0414", resC.getLabel());
      final SensorContactWrapper resD =
          (SensorContactWrapper) is2.readThisLine(lineD);
      assertEquals("lineD failed", "0414", resD.getLabel());
    }
  }

}
