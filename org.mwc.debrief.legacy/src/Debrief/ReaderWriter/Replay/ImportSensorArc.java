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
