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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// @Author : Ian Mayo
// @Project: Debrief 3
// @File   : FormatRelativeBearing.java

package MWC.Utilities.TextFormatting;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/** Format this bearing in relative (reg/green) form
 */
public class FormatRelativeBearing 
{
/** format this value (in degrees) as a relative bearing
 * @param theBrg the bearing (in degrees)
 * @return the relative bearing, as text
 */  
  static public String toString(final double theBrg)
  {
    
    String res;
    double brg = theBrg;
    
    // convert the bearing to +/- 180
    while(brg > 180)
    {
      brg -= 360;
    }

    while(brg < -180)
    {
      brg += 360;
    }
    
    if(brg < 0)
      res = "R";
    else
      res = "G";
    
    brg = Math.abs(brg);
    
    final NumberFormat df = new DecimalFormat("0.0");
    res += df.format(brg);
    
    return res;
  }

}


