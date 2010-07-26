// Copyright MWC 1999, Debrief 3 Project
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

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.PlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import java.util.StringTokenizer;

import junit.framework.Assert;

/**
 * class to parse a label from a line of text
 */
final class ImportSensor2 implements PlainLineImporter {
  /**
   * the type for this string
   */
  private final String _myType = ";SENSOR2:";

  /**
   * read in this string and return a Label
   */
  public final Object readThisLine(String theLine) {

    //;SENSOR2: YYMMDD HHMMSS.SSS AAAAAA @@ DD MM SS.SS H DDD MM SS.SS H BBB.B CCC.C FFF.F RRRR yy..yy xx..xx
    //;; date, ownship name, symbology, sensor lat/long (or the single word NULL), bearing (degs), ambigous bearing (degs) 
  	 //[or the single word NULL], frequency(Hz),  range(yds), sensor name, label (to end of line)</markup>

    // get a stream from the string
    StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String theText;
    String theSymbology;
    String theTrack;
    String sensorName;
    String dateStr;
    double latDeg, longDeg, latMin, longMin;
    char latHem, longHem;
    double latSec, longSec;
    WorldLocation origin = null;
    HiResDate theDtg = null;
    double brg;
    WorldDistance rng=null;
    Double brg2=null, freq=null;
    java.awt.Color theColor;

    // skip the comment identifier
    st.nextToken();

    // now the dtg
    dateStr = st.nextToken();

    // append the time
    dateStr = dateStr + " " + st.nextToken();

    // and extract the date
    theDtg = DebriefFormatDateTime.parseThis(dateStr);

    // get the (possibly multi-word) track name
    theTrack = ImportFix.checkForQuotedTrackName(st);

    // start with the symbology
    theSymbology = st.nextToken(normalDelimiters);

    // now the sensor offsets
    String next = st.nextToken();

    // let's trim this string aswell, just so we're sure N is the first letter
    // if that's its destiny
    next.trim();

    // find out if it's our null value
    if (next.startsWith("N")) {
      // ditch it,
    } else {

      // get the deg out of this value
      latDeg = Double.valueOf(next);

      // ok, this is valid data, persevere with it
      latMin = Double.valueOf(st.nextToken());
      latSec = Double.valueOf(st.nextToken()).doubleValue();

      /** now, we may have trouble here, since there may not be
       * a space between the hemisphere character and a 3-digit
       * latitude value - so BE CAREFUL
       */
      String vDiff = st.nextToken();
      if (vDiff.length() > 3) {
        // hmm, they are combined
        latHem = vDiff.charAt(0);
        String secondPart = vDiff.substring(1, vDiff.length());
        longDeg = Double.valueOf(secondPart);
      } else {
        // they are separate, so only the hem is in this one
        latHem = vDiff.charAt(0);
        longDeg = Double.valueOf(st.nextToken());
      }

      longMin = Double.valueOf(st.nextToken());
      longSec = Double.valueOf(st.nextToken()).doubleValue();
      longHem = st.nextToken().charAt(0);

      // create the origin
      origin = new WorldLocation(latDeg, latMin, latSec, latHem,
          longDeg, longMin, longSec, longHem,
          0);
    } // whether the duff origin data was entered

    brg = Double.valueOf(st.nextToken()).doubleValue();
    
    // get the ambiguous bearing
    String tmp = st.nextToken();
    if(!tmp.startsWith("N"))
    {
    	// cool, we have data
    	brg2 = new Double(Double.valueOf(tmp));    	
    }

    // and the frequency
    tmp = st.nextToken();
    if(!tmp.startsWith("N"))
    {
    	// cool, we have data
    	freq = new Double(Double.valueOf(tmp));    	
    }

    // and the range
    tmp = st.nextToken();
    if(!tmp.startsWith("N"))
    {
    	// cool, we have data
    	rng = new WorldDistance(new Double(Double.valueOf(tmp)), WorldDistance.YARDS);    	
    }

    // get the (possibly multi-word) track name
    sensorName = ImportFix.checkForQuotedTrackName(st);

    // trim the sensor name
    sensorName = sensorName.trim();

    // and lastly read in the message
    theText = st.nextToken("\r").trim();

    theColor = ImportReplay.replayColorFor(theSymbology);

    int theStyle = ImportReplay.replayLineStyleFor(theSymbology);


    // create the contact object
    SensorContactWrapper data =
        new SensorContactWrapper(theTrack, theDtg, rng, brg, brg2, freq, origin, theColor, theText, theStyle, sensorName);

    return data;
  }

  /**
   * determine the identifier returning this type of annotation
   */
  public final String getYourType() {
    return _myType;
  }

  /**
   * export the specified shape as a string
   *
   * @param theWrapper the thing we are going to export
   * @return the shape in String form
   */
  public final String exportThis(MWC.GUI.Plottable theWrapper) {
    // result value
    String line = ";; Export of sensor data not implemented";
    return line;

  }

  /**
   * indicate if you can export this type of object
   *
   * @param val the object to test
   * @return boolean saying whether you can do it
   */
  public final boolean canExportThis(Object val) {
    boolean res = false;

    if (val instanceof SensorWrapper) {
      res = true;
    }

    return res;

  }
  
//  ;SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL Contact_bearings 0414

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

		public final void testImport()
		{
			String lineA = ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL Contact_bearings 0414";
			String lineB = ";SENSOR2: 20090722 041434.000 NONSUCH @B NULL 59.3 300.8 49.96 NULL \"Contact bearings\" 0414";
			
			ImportSensor2 is2 = new ImportSensor2();
			SensorContactWrapper resA = (SensorContactWrapper) is2.readThisLine(lineA);
			Assert.assertEquals("lineA failed", "Contact_bearings", resA.getSensorName());
			Assert.assertEquals("lineA failed", "0414", resA.getLabel());
			SensorContactWrapper resB = (SensorContactWrapper) is2.readThisLine(lineB);
			Assert.assertEquals("lineB failed", "0414", resB.getLabel());
		}
	}

  
}
