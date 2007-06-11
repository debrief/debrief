// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: StatusBar.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: StatusBar.java,v $
// Revision 1.2  2004/05/25 15:45:50  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:04  Ian.Mayo
// Initial import
//
// Revision 1.4  2002-12-16 15:38:04+00  ian_mayo
// Remove units labels (refactored out)
//
// Revision 1.3  2002-10-28 09:24:22+00  ian_mayo
// minor tidying (from IntelliJ Idea)
//
// Revision 1.2  2002-05-28 09:25:34+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:32+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:38+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-14 11:58:17+01  novatech
// include method to show range and bearing, and text values of units/property name
//
// Revision 1.1  2001-01-03 13:43:09+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:00  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:37:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:52+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-23 14:04:01+01  administrator
// Initial revision
//

package MWC.GUI;

public interface StatusBar
{


  public void setText(String theVal);
  /** set range and bearing data in this text panel
   *  @param range the range in degrees
   *  @param bearing the bearing in radians
   */
  public void setRngBearing(double range, double bearing);

}
