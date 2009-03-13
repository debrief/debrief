package Debrief.Tools.Tote.Calculations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: bearingRateCalc.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: bearingRateCalc.java,v $
// Revision 1.5  2006/03/16 16:01:06  Ian.Mayo
// Override not-applicable statement
//
// Revision 1.4  2005/12/13 09:04:54  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2005/09/01 14:12:22  Ian.Mayo
// Correct the sign of bearing rate calculations
//
// Revision 1.2  2004/11/25 10:24:36  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:12  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.8  2003-07-04 10:59:31+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.7  2003-03-19 15:37:20+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.6  2003-03-14 08:35:35+00  ian_mayo
// use optimised subtract method
//
// Revision 1.5  2003-02-10 16:27:50+00  ian_mayo
// Reflect name change of get wrappable data
//
// Revision 1.4  2003-02-07 15:36:12+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.3  2002-06-12 13:58:05+01  ian_mayo
// Change left/right identifiers
//
// Revision 1.2  2002-05-28 09:25:11+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:31+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:11+01  administrator
// Initial revision
//
// Revision 1.4  2001-01-10 09:35:31+00  novatech
// keep test class up to speed with Watchable class
//
// Revision 1.3  2001-01-09 11:16:43+00  novatech
// implement Watchable signature in testing code
//
// Revision 1.2  2001-01-09 10:30:27+00  novatech
// reflect new Watchable signature
//
// Revision 1.1  2001-01-03 13:40:24+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:04  ianmayo
// initial import of files
//
// Revision 1.2  2000-11-17 09:33:08+00  ian_mayo
// removed System.out
//
// Revision 1.1  2000-09-14 10:25:00+01  ian_mayo
// Initial revision
//
// Revision 1.1  2000-09-14 08:43:04+01  ian_mayo
// Initial revision
//

import java.text.DecimalFormat;

import Debrief.Tools.Tote.Watchable;
import MWC.GenericData.*;

/** Calculate the bearing from the primary vessel to the secondary (for use in the tote)
 */
public final class bearingRateCalc extends plainCalc
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /** our own copy of a world vector, to reduce object creation
   *
   */
  private static final WorldVector _myWorldVector = new WorldVector(0,0,0);

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
/** constructor, initialise formatter
 */
  public bearingRateCalc()
  {
    super(new DecimalFormat("0.000"), "Brg Rate", "deg/min");
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
    if(((primary != null) && (secondary != null))
       && (primary != secondary))
    {
      double bDot = calculate(primary, secondary, time);

      // make a copy of the original val absolute, we rely on the sign of the 
      double formattedbDot = Math.abs(bDot);
      res = _myPattern.format(formattedbDot);

      if(bDot < 0)
        res += "L";
      else
        res += "R";


    }
    else
      res = NOT_APPLICABLE;

    return res;
  }

  /** does this calculation require special bearing handling (prevent wrapping through 360 degs)
   *
   */
  public final boolean isWrappableData() {
    return false;
  }

  public final double calculate(Watchable primary, Watchable secondary, HiResDate thisTime)
  {
    double res = 0.0;
    if((primary != null) && (secondary != null) && (primary != secondary))
    {
      WorldVector wv = primary.getLocation().subtract(secondary.getLocation(), _myWorldVector);

      double brg = wv.getBearing();
      double rng = wv.getRange();

      // convert to yards
      rng = MWC.Algorithms.Conversions.Degs2Yds (rng);

      // get the vessel course and speeds (in knots and radians)
      double oCrse = secondary.getCourse();
      double oSpd = secondary.getSpeed();
      double tCrse = primary.getCourse();
      double tSpd = primary.getSpeed();

      double relBrg = brg - oCrse;
      double ATB = brg - Math.PI - tCrse;
      double TSA = tSpd * Math.sin(ATB);
      double OSA = oSpd * Math.sin(relBrg);
      double RSA = TSA + OSA;

    //  RSA = Conversions.clipRadians(RSA);

      double bDot = 6080 / Math.PI * RSA / rng;

      res = bDot;

    }
    return res;
  }


  public final String toString() {
    return "Bearing rate calculation";
  }


}
