// Copyright MWC 1999
// $RCSfile: SwingToolbarButton.java,v $
// $Author: Ian.Mayo $
// $Log: SwingToolbarButton.java,v $
// Revision 1.2  2004/05/25 15:44:39  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:47  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-04-25 12:26:50+01  ian_mayo
// Improve error message
//
// Revision 1.2  2002-05-28 09:26:02+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:03+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:35+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-02-18 09:19:46+00  administrator
// Set the name of the GUI component (largely so that we can access it from JFCUnit)
//
// Revision 1.2  2002-01-29 07:56:50+00  administrator
// Use MWC.Trace instead of System.out
//
// Revision 1.1  2002-01-15 09:15:55+00  administrator
// Report errors to trace, not to command line
//
// Revision 1.0  2001-07-17 08:42:51+01  administrator
// Initial revision
//
// Revision 1.3  2001-06-14 15:44:16+01  novatech
// the tools should return their minimum size as the preferred size
//
// Revision 1.2  2001-01-10 09:33:39+00  novatech
// remove debug code
//
// Revision 1.1  2001-01-03 13:41:38+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:03  ianmayo
// initial version
//
// Revision 1.7  2000-08-16 14:13:04+01  ian_mayo
// make minimum size by default
//
// Revision 1.6  2000-04-12 10:49:14+01  ian_mayo
// put try/catch around execute method, so we are not left in unstable condition
//
// Revision 1.5  2000-03-14 14:59:46+00  ian_mayo
// Handle missing icon
//
// Revision 1.4  2000-03-14 09:54:17+00  ian_mayo
// allow use of icons for buttons
//
// Revision 1.3  2000-03-08 16:25:23+00  ian_mayo
// add flag to try to prevent repeating key clicks
//
// Revision 1.2  1999-11-25 13:26:54+00  ian_mayo
// added toggle button functionality
//
// Revision 1.1  1999-11-18 11:13:39+00  ian_mayo
// new Swing versions
//

package MWC.GUI.Tools.Swing;



import javax.swing.*;
import java.awt.event.*;
import MWC.GUI.*;

/** extension of Swing button, to create one which implements one
 * of our Debrief tools
 */
public class SwingToolbarButton extends JButton implements ActionListener
{

  /////////////////////////////////////////////////////////
  // member variables
  /////////////////////////////////////////////////////////

  protected Tool _theTool;

	/** flag to indicate if we are currently processing this button or not
	 */
	protected boolean _running = false;

  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
  /** convenience constructor, calls normal one
   */
  public SwingToolbarButton(Tool theTool){
    this(theTool.getLabel(), theTool);
  }

	/** constructor for if we don't have an image (don't show label)
	 */
	public SwingToolbarButton(Tool theTool, ImageIcon icon)
	{
 	  super(icon);
		formatMe(theTool);
    setPreferredSize(getMinimumSize());
  }

  public java.awt.Dimension getPreferredSize()
  {
    return getMinimumSize();
  }

  public SwingToolbarButton(String theLabel, Tool theTool){
    super(theLabel);
		formatMe(theTool);
    setPreferredSize(getMinimumSize());
  }

	private void formatMe(Tool theTool)
	{
    _theTool = theTool;
    this.addActionListener(this);
		this.setToolTipText(theTool.getLabel());
    this.setName(theTool.getLabel());
		this.setBorderPainted(false);
		this.setRolloverEnabled(true);
		this.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e)
			{
				setBorderPainted(true);
			}
			public void mouseExited(MouseEvent e)
			{
				setBorderPainted(false);
			}
			});

		// try to handle the icon not being found
		Icon ic = this.getIcon();
		if(ic != null)
		{
			// check if icon available
			ImageIcon ii = (ImageIcon)ic;
			if(ii.getImageLoadStatus() == java.awt.MediaTracker.ERRORED)
			{
				this.setIcon(null);
				this.setText(theTool.getLabel());
			}
		}
	}


  /////////////////////////////////////////////////////////
  // member functions
  /////////////////////////////////////////////////////////

  /** callback function for a button being pressed
   */
	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		if(!_running)
		{
			_running = true;


			// catch any exceptions, so that we are
			// not left in the "running" state
			try
			{
				_theTool.execute();
			}
			catch(Throwable ex)
			{
                MWC.Utilities.Errors.Trace.trace(ex, "Problem with button press: " + this.getText());
			}

			_running = false;
		}
		else
		{
//			System.out.println("Skipping event");
		}
  }


  /** return the tool for this button
   */
  protected Tool getTool(){
    return _theTool;
  }

}
