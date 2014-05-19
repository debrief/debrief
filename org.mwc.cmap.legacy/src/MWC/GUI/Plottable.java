// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Plottable.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: Plottable.java,v $
// Revision 1.3  2006/05/25 14:10:41  Ian.Mayo
// Make plottables comparable
//
// Revision 1.2  2004/05/25 15:45:44  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:04  Ian.Mayo
// Initial import
//
// Revision 1.4  2002-11-25 14:39:05+00  ian_mayo
// Minor tidying
//
// Revision 1.3  2002-07-12 15:46:57+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:35+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:13+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:30+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-11-22 07:54:07+00  administrator
// make correction to method documentation
//
// Revision 1.0  2001-07-17 08:46:39+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-16 15:00:16+01  novatech
// add setVisible method
//
// Revision 1.1  2001-01-03 13:43:08+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:57  ianmayo
// initial version
//
// Revision 1.2  2000-01-20 10:17:36+00  ian_mayo
// white-space only
//
// Revision 1.1  1999-10-12 15:37:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:51+01  administrator
// Initial revision
//
// Revision 1.4  1999-07-27 09:28:07+01  administrator
// tidying up
//
// Revision 1.3  1999-07-19 12:40:33+01  administrator
// added storage of sub-second time data (Switched to storing as Long rather than java.utils.Date)
//
// Revision 1.2  1999-07-12 08:09:19+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:09+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:01+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 14:25:04+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:33:15+00  sm11td
// Initial revision
//


package MWC.GUI;

/** an object which may be plotted to a canvas
 */
public interface Plottable extends Editable, Comparable<Plottable> {

  public final static int INVALID_RANGE = -1;

  /** paint this object to the specified canvas
   */
  public void paint(CanvasType dest);

  /** find the data area occupied by this item
   */
  public MWC.GenericData.WorldArea getBounds();

  /** it this item currently visible?
   */
  public boolean getVisible();

  /** set the visibility of this item
   *
   */
  public void setVisible(boolean val);

  /** how far away are we from this point?
   * or return INVALID_RANGE if it can't be calculated
   */
  public double rangeFrom(MWC.GenericData.WorldLocation other);

}



