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
package Debrief.ReaderWriter.Replay.extensions;

import java.text.ParseException;
import java.util.StringTokenizer;

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;


public class TA_COG_ABS_DataHandler extends Core_TA_Handler 
{

  public TA_COG_ABS_DataHandler()
  {
    super("TA_COG_ABS");
  }

  @Override
  public Object readThisLine(String theLine) throws ParseException
  {
    // should look like:
    // ;TA_COG_ABS: 100112 120230 SENSOR TA_ARRAY 60.187961627128395 0.2243122833234192 19.02

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    final HiResDate theDate;
    final String platform_name;
    final String sensor_name;

    // skip the comment identifier
    st.nextToken();
    
    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // trouble - the track name may have been quoted, in which case we will
    // pull
    // in the remaining fields aswell
    platform_name = AbstractPlainLineImporter.checkForQuotedName(st).trim();
    sensor_name = AbstractPlainLineImporter.checkForQuotedName(st).trim();

    // extract the measuremetns
    final String dLatStr = st.nextToken();
    final String dLongStr = st.nextToken();
    final String dDepthStr = st.nextToken();
    
    if(isNull(dLatStr) || isNull(dLongStr) || isNull(dDepthStr))
    {
      // ok, skip this one. We haven't got the data
    }
    else
    {
      final double dLat = Double.valueOf(dLatStr);
      final double dLong = Double.valueOf(dLongStr);
      final double depth = Double.valueOf(dDepthStr);

      // ok, try to store the measurement
      storeMeasurement2D(platform_name, sensor_name, CENTRE_OF_GRAVITY,
          "LatLong", "\u00b0", theDate, "Lat", "Long", dLat, dLong);
      storeMeasurement(platform_name, sensor_name, CENTRE_OF_GRAVITY, "Depth",
          "m", theDate, depth);
    }

    return null;
  } 
}
