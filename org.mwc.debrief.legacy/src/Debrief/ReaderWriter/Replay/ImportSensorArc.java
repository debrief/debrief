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

import java.awt.Color;
import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.SensorArcContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
final class ImportSensorArc extends AbstractPlainLineImporter {
  /**
   * the type for this string
   */
  private final String _myType = ";SENSORARC";

  /**
   * read in this string and return a Label
   */
  public final Object readThisLine(final String theLine) {

  	// ;SENSORARC 951212 062800 951212 063000 NELSON @@ -75 35 0 1000 "fwd dynamic"
  	// ;SENSORARC YYMMDD HHMMSS.SSS YYMMDD HHMMSS.SSS TRACKNAME SYMBOLOGY LEFT RIGHT INNER OUTER [LEFT RIGHT INNER OUTER] LABEL
    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    String theTrack;
    String sensorName;
    //double latDeg, longDeg, latMin, longMin;
    //char latHem, longHem;
    //double latSec, longSec;
    //WorldLocation origin = null;
    HiResDate startDtg = null;
    HiResDate endDtg = null;
    int left, right, inner, outer, left1=0, right1=0, inner1=0, outer1=0;
    boolean isBeam;
    
    java.awt.Color theColor;

    // skip the comment identifier
    st.nextToken();

		// combine the date, a space, and the time
		String dateToken = st.nextToken();
		if (dateToken.startsWith("N"))
		{
			// startDtg is null
		}
		else
		{
			final String timeToken = st.nextToken();
			startDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);
		}
		dateToken = st.nextToken();
		if (dateToken.startsWith("N"))
		{
			// endDtg is null
		}
		else
		{
			final String timeToken = st.nextToken();
			endDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);
		}

    // get the (possibly multi-word) track name
    theTrack = ImportFix.checkForQuotedName(st);

    // start with the symbology
    symbology = st.nextToken(normalDelimiters);
    
    try
    {
    	
    	 left = (int)MWCXMLReader.readThisDouble(st.nextToken());
    	 right = (int)MWCXMLReader.readThisDouble(st.nextToken());
    	 inner = (int)MWCXMLReader.readThisDouble(st.nextToken());
    	 outer = (int)MWCXMLReader.readThisDouble(st.nextToken());

	    // get the (possibly multi-word) track name
			String theName = st.nextToken();
			final int quoteIndex = theName.indexOf("\"");
			if (quoteIndex >= 0)
			{
				sensorName = ImportFix.checkForQuotedName(st, theName);
				isBeam = false;
			}
			else
			{
				//left1 = (int) MWCXMLReader.readThisDouble(st.nextToken());
				left1 = new Integer(theName).intValue(); 
				right1 = (int) MWCXMLReader.readThisDouble(st.nextToken());
				inner1 = (int) MWCXMLReader.readThisDouble(st.nextToken());
				outer1 = (int) MWCXMLReader.readThisDouble(st.nextToken());
				sensorName = ImportFix.checkForQuotedName(st);
				isBeam = true;
			}
	    
	    // and ditch some whitespace
	    sensorName = sensorName.trim();
	 
	    theColor = ImportReplay.replayColorFor(symbology);
	
	    final int theStyle = ImportReplay.replayLineStyleFor(symbology);
	
	    // create the contact object		
	    final SensorArcContactWrapper data =
	        new SensorArcContactWrapper(theTrack, startDtg, 
	        		endDtg, 
	        		left, right, inner, outer,
	        		left1, right1, inner1, outer1,
	        		isBeam,
	        		theColor,
	        		theStyle, sensorName);
	        		
	
	    return data;
    }
    catch(final ParseException pe)
    {
    	MWC.Utilities.Errors.Trace.trace(pe,
				"Whilst import sensor");
    	return null;
    }
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
  public final String exportThis(final MWC.GUI.Plottable theWrapper) {
    // result value
    final String line = ";; Export of sensor data not implemented";
    return line;

  }

  /**
   * indicate if you can export this type of object
   *
   * @param val the object to test
   * @return boolean saying whether you can do it
   */
  public final boolean canExportThis(final Object val) {
    boolean res = false;

    if (val instanceof SensorWrapper) {
      res = true;
    }

    return res;

  }

}
