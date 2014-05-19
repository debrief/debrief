// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: Toolbar.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: Toolbar.java,v $
// Revision 1.2  2004/05/25 15:45:53  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:05  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:34+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:15+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:33+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:38+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:10+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:03  ianmayo
// initial version
//
// Revision 1.5  2000-04-12 10:45:20+01  ian_mayo
// provide better support for garbage collection (close method)
//
// Revision 1.4  2000-01-12 15:38:28+00  ian_mayo
// support for toggled tools
//
// Revision 1.3  1999-12-03 14:32:12+00  ian_mayo
// added accelerator and mnemonic commands
//
// Revision 1.2  1999-11-25 13:27:38+00  ian_mayo
// added toggling toolbars
//
// Revision 1.1  1999-10-12 15:37:13+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:52+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:10+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:01+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 16:08:52+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:14+00  sm11td
// Initial revision
//


package MWC.GUI;

/** interface to be implemented by classes which produce a toolbar for our use
 */
public interface Toolbar {

  /** which direction is the toolbar laid out?
   */
  public static final int HORIZONTAL = 1;
  public static final int VERTICAL = 2;

  /** create a UI control in the toolbar which calls the specified command
   */
  public void addTool(Tool theTool);
	
	/** create a toggling control, which is part of the named group
	 */
	public void addToggleTool(String group, Tool theTool);

	
  /** create a UI control in the toolbar which calls the specified command
   */
  public void addTool(Tool theTool, 
											java.awt.MenuShortcut theShortcut,
											char theMnemonic);
	/** create a toggling control, which is part of the named group
	 */
	public void addToggleTool(String group, 
														Tool theTool, 
														java.awt.MenuShortcut theShortcut,
														char theMnemonic);
	
	/** provide method to clear toolbar (removing references to tools)
	 */
	public void close();
}




