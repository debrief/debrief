// Copyright MWC 1999
// $RCSfile: AWTToolbarButton.java,v $
// $Author: Ian.Mayo $
// $Log: AWTToolbarButton.java,v $
// Revision 1.2  2004/05/25 15:41:11  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:25  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:42  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:16+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:58+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:11+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:47+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:59+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:55+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:12  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:25+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:32+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:02+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:56+01  sm11td
// Initial revision
//
// Revision 1.3  1999-06-01 16:49:23+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.2  1999-02-01 16:08:50+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-02-01 14:25:08+00  sm11td
// Initial revision
//
package MWC.GUI.Tools.AWT;



import java.awt.*;
import java.awt.event.*;
import MWC.GUI.*;

/** extension of AWT button, to create one which implements one
 * of our Debrief tools
 */
public class AWTToolbarButton extends Button implements ActionListener
{
  
  /////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Tool _theTool;
  

  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
  /** convenience constructor, calls normal one
   */
  public AWTToolbarButton(Tool theTool){
    this(theTool.getLabel(), theTool);
    
  }
  
  public AWTToolbarButton(String theLabel, Tool theTool){
    super(theLabel);
    _theTool = theTool;
    this.addActionListener(this);
  }

  /////////////////////////////////////////////////////////
  // member functions
  /////////////////////////////////////////////////////////
  
  /** callback function for a button being pressed
   */
  public void actionPerformed(java.awt.event.ActionEvent e){
    /** check that we have a tool declared
     */
    if(_theTool != null)
      _theTool.execute();
  }
  
  

  public Dimension getMaximumSize()
  {
    return super.getMinimumSize();
  }
  
  /** return the tool for this button
   */
  protected Tool getTool(){
    return _theTool;
  }
  
}
