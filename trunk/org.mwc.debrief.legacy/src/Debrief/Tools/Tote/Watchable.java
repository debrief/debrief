package Debrief.Tools.Tote;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Watchable.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: Watchable.java,v $
// Revision 1.3  2005/12/13 09:04:56  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:42  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:09  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.2  2002-05-28 09:25:11+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:45+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:39+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:13+01  administrator
// Initial revision
//
// Revision 1.4  2001-01-10 09:34:46+00  novatech
// provide getColor() accessor
//
// Revision 1.3  2001-01-09 11:16:18+00  novatech
// return the name of this item
//
// Revision 1.2  2001-01-09 10:29:38+00  novatech
// add extra parameters to allow WatchableLists to be used instead of TrackWrappers
//
// Revision 1.1  2001-01-03 13:40:27+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:04  ianmayo
// initial import of files
//
// Revision 1.3  2000-04-03 10:20:00+01  ian_mayo
// add set/get Visible methods
//
// Revision 1.2  2000-03-07 13:42:26+00  ian_mayo
// corrected units
//
// Revision 1.1  1999-10-12 15:34:05+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:03:14+01  administrator
// Initial revision
//

import MWC.GenericData.*;

public interface Watchable
{
  /** get the current location of the watchable
   * @return the location
   */
  public WorldLocation getLocation();
  /** get the current course of the watchable (rads)
   * @return course in radians
   */
  public double getCourse();
  /** get the current speed of the watchable (kts)
   * @return speed in knots
   */
  public double getSpeed();

  /** get the current depth of the watchable (m)
   * @return depth in metres
   */
  public double getDepth();

  /** get the bounds of the object (used when we are painting it)
   */
  public WorldArea getBounds();

	/** specify if this Watchable is visible or not
	 * @param val whether it's visible
	 */
	public void setVisible(boolean val);

	/** determine if this Watchable is visible or not
	 * @return boolean whether it's visible
	 */
	public boolean getVisible();

  /** find out the time of this watchable
   */
  public HiResDate getTime();

  /** find out the name of this watchable
   */
  public String getName();

  /** find out the colour of this watchable
   */
  public java.awt.Color getColor();

}
