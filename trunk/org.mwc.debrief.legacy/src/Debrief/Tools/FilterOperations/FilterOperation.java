package Debrief.Tools.FilterOperations;

import Debrief.Tools.Tote.WatchableList;
import MWC.GenericData.HiResDate;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: FilterOperation.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: FilterOperation.java,v $
// Revision 1.2  2004/11/25 10:24:27  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:48:23  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-25 15:54:15+00  ian_mayo
// Implement "Reset me" buttons
//
// Revision 1.2  2002-05-28 12:28:22+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:58+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:08+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:22+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:34+00  novatech
// Initial revision
//
// Revision 1.2  2000-08-07 14:22:23+01  ian_mayo
// added VCS headers
//


/** interface defining behaviour triggered from TimeFilter tote panel
 */
public interface FilterOperation extends MWC.GUI.Tool
{
	/** return the description for this operation
	 */
	public String getDescription();
	
	/** specify the time period selected by the user
	 */
	public void setPeriod(HiResDate startDTG, HiResDate finishDTG);
	
	/** specify the tracks (or watchable lists) selected by the user
	 */
	public void setTracks(java.util.Vector<WatchableList> selectedTracks);

  /** the user has pressed RESET whilst this button is pressed
   *
   * @param startTime the new start time
   * @param endTime the new end time
   */
  public void resetMe(HiResDate startTime, HiResDate endTime);

}
