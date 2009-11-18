package Debrief.Tools.Tote.Calculations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: bearingCalc.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: bearingCalc.java,v $
// Revision 1.3  2006/03/16 16:01:06  Ian.Mayo
// Override not-applicable statement
//
// Revision 1.2  2004/11/25 10:24:35  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:11  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-03-19 15:37:04+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-10 16:27:51+00  ian_mayo
// Reflect name change of get wrappable data
//
// Revision 1.3  2003-02-07 15:36:13+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.2  2002-05-28 09:25:11+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:30+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:11+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:24+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:04  ianmayo
// initial import of files
//
// Revision 1.1  2000-09-14 10:25:05+01  ian_mayo
// Initial revision
//
// Revision 1.5  2000-08-14 10:59:51+01  ian_mayo
// add comments
//
// Revision 1.4  2000-05-19 11:23:44+01  ian_mayo
// provided n/a result string when secondary watchable not present
//
// Revision 1.3  2000-04-03 10:18:26+01  ian_mayo
// shorten units label
//
// Revision 1.2  2000-03-07 14:48:11+00  ian_mayo
// optimised algorithms
//
// Revision 1.1  1999-10-12 15:34:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 09:47:45+01  administrator
// Initial revision
//

import java.text.DecimalFormat;

import MWC.Algorithms.Conversions;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

/** Calculate the bearing from the primary vessel to the secondary (for use in the tote)
 */
public final class bearingCalc extends plainCalc
{
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
/** constructor, initialise formatter
 */  
  public bearingCalc()
  {  
    super(new DecimalFormat("000.0"), "Bearing", "degs");
  }
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

/** produce our calculation from the Watchables
 * @param primary primary watchable
 * @param secondary secondary watchable
 * @return string representation of calculated value
 */  
  public final String update(Watchable primary, Watchable secondary, HiResDate time)
  {
    String res = null;
    if((primary != null) && (secondary != null) && (primary != secondary))
    {
			double brg = calculate(primary, secondary, time);
      res = _myPattern.format(brg);
    }
    else
      res = NOT_APPLICABLE;
    
    return res;
  }

  /** does this calculation require special bearing handling (prevent wrapping through 360 degs)
   *
   */
  public final boolean isWrappableData() {
    return true;
  }

  public final double calculate(Watchable primary, Watchable secondary, HiResDate thisTime)
  {
    double brg = 0.0;
    if((primary != null) && (secondary != null) && (primary != secondary))
    {
			brg = primary.getLocation().bearingFrom(secondary.getLocation());
      brg = Conversions.clipRadians(brg);
      brg = Conversions.Rads2Degs(brg);
    }
    
    return brg;
  }

}
