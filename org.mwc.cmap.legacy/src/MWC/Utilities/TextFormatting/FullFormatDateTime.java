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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class FullFormatDateTime 
{

  
  public static final String FULL_FORMAT = "dd/MMM/yy HH:mm:ss";
  public static final String ISO_FORMAT =  "dd/MM/yyyy hh:mm:ss";

	static public String toString(final long theVal)
  {
		return toStringLikeThis(theVal, 
														FULL_FORMAT);
  }

	static public String toISOString(final long theVal)
  {
		return toStringLikeThis(theVal, 
														ISO_FORMAT);
  }

	static public long fromString(final String theStr) throws ParseException
	{
		return fromStringLikeThis(theStr, FULL_FORMAT);
	}
	
	static public String toShortString(final long theVal)
	{
		return toStringLikeThis(theVal, 
														"HHmm");
	}

  static public String toDetailedShortString(final long theVal)
	{
		return toStringLikeThis(theVal,
														"HHmm:ss");
	}

	static public String toStringLikeThis(final long theVal,
																				final String thePattern)
	{
    final java.util.Date theTime = new java.util.Date(theVal);
    String res;

  
    final DateFormat df = new SimpleDateFormat(thePattern);    
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    res = df.format(theTime);

    return res;
	}


	static protected long fromStringLikeThis(final String theTxt,
																				final String thePattern) throws ParseException
	{
    final DateFormat df = new SimpleDateFormat(thePattern);    
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    final long res = df.parse(theTxt).getTime();

    return res;
	}

  static public String getExample(){
    return FULL_FORMAT;
  }
}


