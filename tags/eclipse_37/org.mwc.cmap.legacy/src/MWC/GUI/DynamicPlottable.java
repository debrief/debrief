// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DynamicPlottable.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: DynamicPlottable.java,v $
// Revision 1.3  2004/11/24 16:05:31  Ian.Mayo
// Switch to hi-res timers
//
// Revision 1.2  2004/05/25 15:45:27  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:03  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:36+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:25+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:34+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:06+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:45  ianmayo
// initial version
//
// Revision 1.1  2000-03-07 13:42:40+00  ian_mayo
// Initial revision
//

package MWC.GUI;

import MWC.GenericData.HiResDate;

/**
 * class implemented by plottable object with a dynamic
 * nature, that is time-dependent plottables which
 * are happen at an instant in time, or for a specified period
 */
public interface DynamicPlottable extends Plottable
{
  /**
   * test to see if this plottable is visible between the specified time periods
   */
  public boolean visibleBetween(HiResDate start,
                                HiResDate end);

}
