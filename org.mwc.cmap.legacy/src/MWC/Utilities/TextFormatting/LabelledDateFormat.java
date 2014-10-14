/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// @Author : Ian Mayo
// @Project: Debrief 3
// @File   : FormatRNDateTime.java

package MWC.Utilities.TextFormatting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class LabelledDateFormat 
{
  static public String toString(final long theVal)
  {
    
    final java.util.Date theTime = new java.util.Date(theVal);
    String res;
    final DateFormat df = new SimpleDateFormat("dd'd HH'h mm'm ss's");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    res = df.format(theTime);

    return res;
  }

}


