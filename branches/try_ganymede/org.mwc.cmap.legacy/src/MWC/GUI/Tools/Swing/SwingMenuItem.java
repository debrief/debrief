// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingMenuItem.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingMenuItem.java,v $
// Revision 1.2  2004/05/25 15:44:34  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:47  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:03+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:02+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:33+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:51+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:37+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:58  ianmayo
// initial version
//
// Revision 1.1  1999-11-18 11:13:38+00  ian_mayo
// new Swing versions
//

//
package MWC.GUI.Tools.Swing;

import javax.swing.JMenuItem;

import MWC.GUI.Tool;


/** extension of normal Swing menu to create a menu item containing
 * a Debrief tool item
 */
public class SwingMenuItem extends JMenuItem implements java.awt.event.ActionListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /** the Debrief tool we are calling
   */
  protected Tool _theTool;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  
  public SwingMenuItem(Tool theTool){
    _theTool = theTool;
  }
  
  public SwingMenuItem(String theLabel, Tool theTool){
    super(theLabel);
    _theTool = theTool;
    this.addActionListener(this);
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  
  protected Tool getTool(){
    return _theTool;
  }
  
  /** callback function for when menu item if selected
   */
  public void actionPerformed(java.awt.event.ActionEvent e){
    /** check that we have a tool declared
     */
    if(_theTool != null)
      _theTool.execute();
  }
}
