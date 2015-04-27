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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import Debrief.Wrappers.SensorArcContactWrapper;
import Debrief.Wrappers.SensorArcValue;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
final class ImportSensorArc extends AbstractPlainLineImporter {
  /**
   * the type for this string
   */
  private final String _myType = ";SENSORARC:";

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
    List<SensorArcValue> values = new ArrayList<SensorArcValue>();
    java.awt.Color theColor;

    List<String> myTokens = new ArrayList<String>();
		while (st.hasMoreTokens())
		{
			String next = ImportFix.checkForQuotedName(st);
			myTokens.add(next);
		}
		if (myTokens.size() < 10)
		{
			MWC.Utilities.Errors.Trace.trace("Whilst import sensor arc: " + theLine);
			return null;
		}
    int index = 1;
    // skip the comment identifier
    //st.nextToken();
    
		// combine the date, a space, and the time
		String dateToken = myTokens.get(index++);
		if (dateToken.startsWith("N"))
		{
			// startDtg is null
		}
		else
		{
			final String timeToken = myTokens.get(index++);
			startDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);
		}
		dateToken = myTokens.get(index++);
		if (dateToken.startsWith("N"))
		{
			// endDtg is null
		}
		else
		{
			final String timeToken = myTokens.get(index++);
			endDtg = DebriefFormatDateTime.parseThis(dateToken, timeToken);
		}

    // get the (possibly multi-word) track name
    theTrack = myTokens.get(index++);
    
    // start with the symbology
    //symbology = st.nextToken(normalDelimiters);
    symbology = myTokens.get(index++);
    
    int tokens = myTokens.size() - index;
    try
    {
    	while (tokens > 4)
			{
				index = addValue(myTokens, values, index);
				tokens -= 4;
			}
	    sensorName = myTokens.get(index);
	    
	    // and ditch some whitespace
	    sensorName = sensorName.trim();
	 
	    theColor = ImportReplay.replayColorFor(symbology);
	
	    final int theStyle = ImportReplay.replayLineStyleFor(symbology);
	
	    // create the contact object		
	    final SensorArcContactWrapper data =
	        new SensorArcContactWrapper(theTrack, startDtg, 
	        		endDtg, 
	        		values,
	        		theColor,
	        		theStyle, sensorName);
	        		
	
	    return data;
    }
    catch(final ParseException pe)
    {
    	MWC.Utilities.Errors.Trace.trace(pe,
				"Whilst import sensor arc: " + theLine);
    	return null;
    }
  }

	private int addValue(final List<String> myTokens, List<SensorArcValue> values, int index)
			throws ParseException
	{
		SensorArcValue value = new SensorArcValue();
		value.left = new Integer(myTokens.get(index++)).intValue();
		value.right = new Integer(myTokens.get(index++)).intValue();
		value.inner = new Integer(myTokens.get(index++)).intValue();
		value.outer = new Integer(myTokens.get(index++)).intValue();
		values.add(value);
		return index;
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
  	// ;SENSORARC 951212 062800 951212 063000 NELSON @@ -75 35 0 1000 "fwd dynamic"
  	
  	SensorArcContactWrapper sacw = (SensorArcContactWrapper) theWrapper;
  	StringBuilder builder = new StringBuilder();
    builder.append("\n");
    builder.append(_myType);
    builder.append(" ");
    if (sacw.getDTG() == null)
    {
    	builder.append("NULL");
    }
    else
    {
    	builder.append(MWC.Utilities.TextFormatting.DebriefFormatDateTime.toStringHiRes(sacw.getStartDTG()));
    }
    builder.append(" ");
    if (sacw.getEndDTG() == null)
    {
    	builder.append("NULL");
    }
    else
    {
    	builder.append(MWC.Utilities.TextFormatting.DebriefFormatDateTime.toStringHiRes(sacw.getEndDTG()));
    }
    builder.append(" ");
    String trackName = sacw.getTrackName();
    if (trackName.indexOf(" ") > -1)
    {
    	builder.append("\"");
    	builder.append(trackName);
    	builder.append("\"");
    }
    else 
    {
    	builder.append(trackName);
    }
    builder.append(" ");
    // symbology
    builder.append(ImportReplay.replaySymbolFor(sacw.getColor(), null));
    // FIXME - issue #1186 - Additional symbology codes    
    // builder.append(ImportReplay.replaySymbolForLineStyle(sacw.getLineStyle()));
    
    builder.append(" ");
    for (SensorArcValue value:sacw.getValues())
    {
    	builder.append(value.left);
    	builder.append(" ");
    	builder.append(value.right);
    	builder.append(" ");
    	builder.append(value.inner);
    	builder.append(" ");
    	builder.append(value.outer);
    	builder.append(" ");
    }
		
    String name = sacw.getSensorName();
    if (name.indexOf(" ") > -1)
    {
    	builder.append("\"");
    	builder.append(name);
    	builder.append("\"");
    }
    else 
    {
    	builder.append(name);
    }
    builder.append(" ");
    
    return builder.toString();

  }

  /**
   * indicate if you can export this type of object
   *
   * @param val the object to test
   * @return boolean saying whether you can do it
   */
  public final boolean canExportThis(final Object val) {
    boolean res = false;

    if (val instanceof SensorArcContactWrapper) {
      res = true;
    }

    return res;

  }

}
