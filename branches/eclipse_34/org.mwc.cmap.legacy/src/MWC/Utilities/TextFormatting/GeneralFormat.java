package MWC.Utilities.TextFormatting;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: GeneralFormat.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: GeneralFormat.java,v $
// Revision 1.3  2004/08/12 13:52:21  Ian.Mayo
// Add a line-separator convenience constant
//
// Revision 1.2  2004/05/24 16:26:09  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:28  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:55  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-06-25 15:41:12+01  ian_mayo
// Only output when we have data
//
// Revision 1.3  2003-06-25 08:49:42+01  ian_mayo
// Introduce vessel status formatting
//
// Revision 1.2  2002-05-28 09:26:06+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:51+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-09-25 16:34:24+01  administrator
// Specify Locale for text output
//
// Revision 1.0  2001-07-17 08:42:45+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:33+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:15  ianmayo
// initial version
//
// Revision 1.2  2000-01-13 15:33:26+00  ian_mayo
// added one-decimal place formatter
//
// Revision 1.1  1999-10-12 15:36:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:30+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-08 13:08:48+01  administrator
// Initial revision
//

import java.text.*;

public class GeneralFormat
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private static NumberFormat _oneDPFormat = new DecimalFormat("0.0", new java.text.DecimalFormatSymbols(java.util.Locale.UK));
  private static NumberFormat _bearingFormat = new DecimalFormat("000.00", new java.text.DecimalFormatSymbols(java.util.Locale.UK));
  private static NumberFormat _rangeFormat = new DecimalFormat("#0", new java.text.DecimalFormatSymbols(java.util.Locale.UK));


  /** just declare the line separator once
   * 
   */
  public final static String LINE_SEPARATOR = System.getProperty("line.separator");


  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public static String formatBearing(double val){
    // format bearing to give a 3 figure leading part, followed
    // by a 2 digit decimal
    return _bearingFormat.format(val) + "degs";
  }

  public static String formatRange(double val){
    return _rangeFormat.format(val) + "yds";
  }

  public static String formatOneDecimalPlace(double val){
    return _oneDPFormat.format(val);
  }

  public static String formatStatus(double courseDegs, double speedKts, double depthMetres)
  {
    String res = "n/a";

    //
    if((courseDegs == 0d) && (speedKts == 0d) && (depthMetres == 0d))
    {
      // we don't have vessel status details
      res = "Status unavailable";
    }
    else
    {
      res = "Crse:" + formatOneDecimalPlace(courseDegs) +
         " Spd:" + formatOneDecimalPlace(speedKts) +
         " Depth:" + formatOneDecimalPlace(depthMetres);
    }
    return res;
  }

}
