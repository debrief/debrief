/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2017, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package Debrief.ReaderWriter.Replay;

import java.util.StringTokenizer;

import junit.framework.Assert;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
public final class ImportSensor3 extends AbstractPlainLineImporter
{
  /**
   * the type for this string
   */
  private final String _myType = ";SENSOR3:";

  /**
   * read in this string and return a Label
   */
  public final Object readThisLine(final String theLine)
  {

    // ;SENSOR3: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B CCC.C
    // FFF.F GGG.G RRRR yy..yy xx..xx
    // ;; date, ownship name, symbology, sensor lat/long (or the single word NULL),
    // bearing (degs) [or the single word NULL], bearing accuracy (degs), frequency(Hz)
    // [or the single word NULL], frequency accuracy (Hz), range(yds)
    // [or the single word NULL], sensor name, label (to end of line)

    Trace
        .trace(
            "Note: Full import of Sensor3 data not implemented yet, accuracy ignored",
            false);

    // TODO:
    // 1. produce some sample lines in unit tests, check they import corectly
    // 2. extend the SensorDataWrapper element to include bearing accuracy & frequency accuracy data
    // 3. extend unit tests to verify that the data has been stored correctly.

    // get a stream from the string
    StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String theText;
    String theSymbology;
    String theTrack;
    String sensorName;
    double latDeg;
    double longDeg;
    double latMin; 
    double longMin;
    char latHem;
    char longHem;
    double latSec; 
    double longSec;
    WorldLocation origin = null;
    HiResDate theDtg = null;
    Double brg = null;
    WorldDistance rng = null;
    Double brg2 = null;
    Double freq = null;
    java.awt.Color theColor;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    String dateToken = st.nextToken();
    String timeToken = st.nextToken();

    // and extract the date
    theDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // get the (possibly multi-word) track name
    theTrack = ImportFix.checkForQuotedName(st);

    // start with the symbology
    theSymbology = st.nextToken(normalDelimiters);

    // now the sensor offsets
    String next = st.nextToken();

    // let's trim this string aswell, just so we're sure N is the first letter
    // if that's its destiny
    next = next.trim();

    // find out if it's our null value
    if (next.startsWith("N"))
    {
      // ditch it,
    }
    else
    {

      // get the deg out of this value
      latDeg = Double.valueOf(next);

      // ok, this is valid data, persevere with it
      latMin = Double.valueOf(st.nextToken());
      latSec = Double.valueOf(st.nextToken()).doubleValue();

      /**
       * now, we may have trouble here, since there may not be a space between the hemisphere
       * character and a 3-digit latitude value - so BE CAREFUL
       */
      String vDiff = st.nextToken();
      if (vDiff.length() > 3)
      {
        // hmm, they are combined
        latHem = vDiff.charAt(0);
        String secondPart = vDiff.substring(1, vDiff.length());
        longDeg = Double.valueOf(secondPart);
      }
      else
      {
        // they are separate, so only the hem is in this one
        latHem = vDiff.charAt(0);
        longDeg = Double.valueOf(st.nextToken());
      }

      longMin = Double.valueOf(st.nextToken());
      longSec = Double.valueOf(st.nextToken()).doubleValue();
      longHem = st.nextToken().charAt(0);

      // create the origin
      origin =
          new WorldLocation(latDeg, latMin, latSec, latHem, longDeg, longMin,
              longSec, longHem, 0);
    } // whether the duff origin data was entered

    // get the bearing
    String brgStr = st.nextToken();
    if (!brgStr.startsWith("N"))
    {
      // cool, we have data
      brg = new Double(Double.valueOf(brgStr));
    }

    @SuppressWarnings("unused")
    String brgAccStr = st.nextToken();

    // and the frequency
    String tmp = st.nextToken();
    if (!tmp.startsWith("N"))
    {
      // cool, we have data
      freq = new Double(Double.valueOf(tmp));
    }

    @SuppressWarnings("unused")
    String freqAccStr = st.nextToken();

    // and the range
    tmp = st.nextToken();
    if (!tmp.startsWith("N"))
    {
      // cool, we have data
      rng =
          new WorldDistance(new Double(Double.valueOf(tmp)),
              WorldDistance.YARDS);
    }

    // get the (possibly multi-word) track name
    sensorName = ImportFix.checkForQuotedName(st);

    // trim the sensor name
    sensorName = sensorName.trim();

    // and lastly read in the message
    theText = st.nextToken("\r").trim();

    theColor = ImportReplay.replayColorFor(theSymbology);

    int theStyle = ImportReplay.replayLineStyleFor(theSymbology);

    // special case: if the sensor name is "UNKNOWN" and we have freq data, and brg is zero,
    // then cancel the bearings
    if (sensorName.equals("UNKNOWN") && freq != null & brg != null && brg == 0d)
    {
      // ok, cancel the bearing
      brg = null;
    }

    // create the contact object
    SensorContactWrapper data =
        new SensorContactWrapper(theTrack, theDtg, rng, brg, brg2, freq,
            origin, theColor, theText, theStyle, sensorName);

    return data;
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

    public void testDummy()
    {

    }

    public final void testImport()
    {
      // final String lineA =
      // ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL Contact_bearings 0414";
      // final String lineB =
      // ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL \"Contact bearings\" 0414";
      // final String lineTabs =
      // ";SENSOR2:	20090722	041434.000	NONSUCH	@B	NULL	59.3	300.8	49.96	NULL	\"Contact bearings\"	0414";
      // final String lineD =
      // ";SENSOR2: 20090722 041434.000 \"NON SUCH\" @B NULL 59.3 NULL NULL NULL \"Contact bearings\" 0414";

      // NOTE: This test just uses the mangled form of SENSOR3 output that we get from system G.
      final String lineA =
          ";SENSOR3: 20090722 041434.000 NULL @@ NULL 0.0 NULL 155.55 0.001 NULL \"UNKNOWN\" \"UNKNOWN\"";

      final ImportSensor3 is2 = new ImportSensor3();
      final SensorContactWrapper resA =
          (SensorContactWrapper) is2.readThisLine(lineA);
      Assert.assertEquals("lineA failed", "NULL", resA.getTrackName());
      Assert.assertEquals("lineA failed", "UNKNOWN", resA.getSensorName());
      Assert.assertEquals("lineA failed", "\"UNKNOWN\"", resA.getLabel());
      assertEquals("correct freq", 155.55, resA.getFrequency(), 0.001);
    }
  }

}
