/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
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
// @File   : BriefFormatLocation.java

package MWC.Utilities.TextFormatting;

import java.text.DecimalFormat;

import MWC.GenericData.WorldLocation;

public class DebriefFormatLocation implements PlainFormatLocation
  {

  static DecimalFormat df = new DecimalFormat("00.00", new java.text.DecimalFormatSymbols(java.util.Locale.UK));
  static DecimalFormat df2 = new DecimalFormat("00", new java.text.DecimalFormatSymbols(java.util.Locale.UK));
  static DecimalFormat df3 = new DecimalFormat("000", new java.text.DecimalFormatSymbols(java.util.Locale.UK));

  static public String toString(final WorldLocation loc)
  {

    final double _lat = loc.getLat();
    final double _long = loc.getLong();

    final brokenDown latD = new brokenDown(_lat, true);
    final brokenDown longD = new brokenDown(_long, false);

    final StringBuffer res = new StringBuffer();
    res.append(df2.format(latD.deg));
    res.append(" ");
    res.append(df2.format(latD.min));
    res.append(" ");
    res.append(df.format(latD.sec));
    res.append(" ");
    res.append(latD.hem);
    res.append(" ");
    res.append(df3.format(longD.deg));
    res.append(" ");
    res.append(df2.format(longD.min));
    res.append(" ");
    res.append(df.format(longD.sec));
    res.append(" ");
    res.append(longD.hem);

    return res.toString();
  }

  static public class brokenDown
  {
    public int deg;
    public int min;
    public double sec;
    public char hem;

    public brokenDown(final double val, final boolean isLat)
    {
      hem = doHem(val, isLat);
      final double theVal = Math.abs(val);
      deg = (int) (theVal);
      min = (int) ((theVal - deg) * 60.0);
      sec = ((theVal - deg) - ((double) min / 60.0)) * 3600;
      
      // just catch rounding problem
      if(sec > 59.99999)
      {
      	sec = 0;
      	min++;
      }
    }

    protected char doHem(final double val, final boolean isLat)
    {
      char res;
      if (val > 0)
      {
        if (isLat)
          res = 'N';
        else
          res = 'E';
      }
      else
      {
        if (isLat)
          res = 'S';
        else
          res = 'W';
      }

      return res;
    }

  }

	public String convertToString(final WorldLocation theLocation)
	{
		return toString(theLocation);
	}

	public String getExampleString()
	{
		return "Debrief format";
	}
}
