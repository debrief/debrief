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
package Debrief.Tools.Tote.Calculations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: tidyTimeCalc.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: tidyTimeCalc.java,v $
// Revision 1.5  2006/07/25 14:48:52  Ian.Mayo
// Handle when this pt has no time
//
// Revision 1.4  2006/03/16 16:01:09  Ian.Mayo
// Override not-applicable statement
//
// Revision 1.3  2005/12/13 09:04:56  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2005/01/24 11:03:35  Ian.Mayo
// Include text field pattern
//
// Revision 1.1  2005/01/24 10:58:09  Ian.Mayo
// First version
//

import java.text.DecimalFormat;

import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

/** class to provide nice and tidy text-output, in a form which is readable by Excel
 *
 */
public final class tidyTimeCalc extends plainCalc
{

  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public tidyTimeCalc()
  {  
    super(new DecimalFormat("000.0"), "Time", "dd/MMM/yy HH:mm:ss");
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public final double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime)
  {
    double res = 0.0;
    res = thisTime.getMicros();
    return res;
  }
  public final String update(final Watchable primary, final Watchable secondary, final HiResDate time)
  {
		// check we have data
		if(secondary == null)
			return NOT_APPLICABLE;

		if(time == null)
			return NOT_APPLICABLE;

		final String res = MWC.Utilities.TextFormatting.FullFormatDateTime.toString(time.getDate().getTime());

  	return res;
  }


  /** does this calculation require special bearing handling (prevent wrapping through 360 degs)
   *
   */
  public final boolean isWrappableData() {
    return false;
  }
}
