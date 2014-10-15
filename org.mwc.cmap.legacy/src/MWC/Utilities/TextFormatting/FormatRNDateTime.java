/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class FormatRNDateTime 
{
  static private SimpleDateFormat _df = null;

  static public String toString(final long theVal)
  {
		return toStringLikeThis(theVal, 
														"ddHHmm.ss");
  }
  
  
  
	static public String toMediumString(final long theVal)
	{
		return toStringLikeThis(theVal, 
														"ddHHmm");
	}
  
	static public String toShortString(final long theVal)
	{
		return toStringLikeThis(theVal, 
														"HHmm");
	}
	
	static public String toStringLikeThis(final long theVal,
																				final String thePattern)
	{
    final java.util.Date theTime = new java.util.Date(theVal);
    String res;

    if(_df == null)
    {
      _df = new SimpleDateFormat(thePattern);
      _df.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    // do we need to change the pattern?
    if(_df.toPattern().equals(thePattern))
    {
      // hey, don't bother, we're ok
    }
    else
    {
      // and update the pattern
      _df.applyPattern(thePattern);
    }

    res = _df.format(theTime);

    return res;
	}
	
  static public String getExample(){
    return "ddHHmm.ss";
  }
}


