// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTToolbar.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTToolbar.java,v $
// Revision 1.2  2004/05/25 15:41:10  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:58+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:46+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:59+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:54+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:12  ianmayo
// initial version
//
// Revision 1.6  2000-08-30 14:43:59+01  ian_mayo
// tidy up
//
// Revision 1.5  2000-04-12 10:45:19+01  ian_mayo
// provide better support for garbage collection (close method)
//
// Revision 1.4  2000-01-12 15:38:12+00  ian_mayo
// support for toggling tools
//
// Revision 1.3  1999-12-03 14:32:11+00  ian_mayo
// added accelerator and mnemonic commands
//
// Revision 1.2  1999-11-25 13:27:38+00  ian_mayo
// added toggling toolbars
//
// Revision 1.1  1999-10-12 15:36:24+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:31+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-23 14:03:45+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.1  1999-07-07 11:10:02+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:55+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 16:08:51+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:12+00  sm11td
// Initial revision
//


package MWC.GUI.Tools.AWT;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;

import MWC.GUI.Tool;
import MWC.GUI.Toolbar;

/** implementation of toolbar using AWT controls 
 * @stereotype AWT*/
public class AWTToolbar extends Panel implements Toolbar 
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////
	
  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
  public AWTToolbar(int theDirection){
    // set the layout manager, & align the buttons
    
    if(theDirection == HORIZONTAL)
      this.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 3));
    else
      this.setLayout(new GridLayout(0, 2));
    
    // set the background colour
    setBackground(Color.lightGray);
		
  }
  
  /////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

	/** method to remove tools from toolbar
	 */
	public void close()
	{
		//
		this.removeAll();
	}
	
	
	
	public void addTool(Tool theTool)
	{
    // cast the tool back to the correct type
    AWTToolbarButton theBtn = new AWTToolbarButton(theTool);
    this.add(theBtn);
    this.doLayout();
 //   this.getParent().doLayout();
	}
	
  public void addTool(Tool theTool,
											java.awt.MenuShortcut theShortcut,
											char theMnemonic)
	{
		addTool(theTool);
  }

	public void addToggleTool(String group, 
														Tool theTool)
	{
		// hmm, we can't really do this, since AWT doesn't have toggle buttons
		addTool(theTool);
	}
	
	/** create a toggling control, which is part of the named group
	 */
	public void addToggleTool(String group, 
														Tool theTool,
														java.awt.MenuShortcut theShortcut,
														char theMnemonic)
	{
		// hmm, we can't really do this, since AWT doesn't have toggle buttons
		addTool(theTool, theShortcut, theMnemonic);
	}
	
  
}










