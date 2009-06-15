// Copyright MWC 1999
// $RCSfile: SwingToggleButton.java,v $
// $Author: Ian.Mayo $
// $Log: SwingToggleButton.java,v $
// Revision 1.2  2004/05/25 15:44:36  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:47  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-06-05 14:31:11+01  ian_mayo
// Minor tidying, improve border around toggle buttons
//
// Revision 1.2  2002-05-28 09:26:03+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:02+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:34+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:51+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-10 09:33:47+00  novatech
// white space
//
// Revision 1.1  2001-01-03 13:41:38+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:01  ianmayo
// initial version
//
// Revision 1.7  2000-08-16 14:12:49+01  ian_mayo
// make minimum size by default
//
// Revision 1.6  2000-08-14 10:45:07+01  ian_mayo
// correctly centred button
//
// Revision 1.5  2000-08-07 14:07:20+01  ian_mayo
// switch to class-loader for images
//
// Revision 1.4  2000-03-14 14:59:53+00  ian_mayo
// handle missing icon
//
// Revision 1.3  2000-03-14 09:54:17+00  ian_mayo
// allow use of icons for buttons
//
// Revision 1.2  1999-11-25 13:26:53+00  ian_mayo
// added toggle button functionality
//
// Revision 1.1  1999-11-18 11:13:39+00  ian_mayo
// new Swing versions
//

package MWC.GUI.Tools.Swing;


import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.border.CompoundBorder;

import MWC.GUI.Tool;

/** extension of Swing button, to create one which implements one
 * of our Debrief tools
 */
public class SwingToggleButton extends JCheckBox implements ActionListener
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
  public SwingToggleButton(Tool theTool)
  {
    this(theTool.getLabel(), theTool);
  }

  public SwingToggleButton(String theLabel, Tool theTool)
  {
    super(theLabel);
    formatMe(theTool);

  }

  /** constructor for if we don't have an image (don't show label)
   */
  public SwingToggleButton(Tool theTool, ImageIcon icon)
  {
    super(icon);
    formatMe(theTool);
    setPreferredSize(getMinimumSize());
  }


  private void formatMe(Tool theTool)
  {
    _theTool = theTool;
    this.addActionListener(this);
    this.setToolTipText(theTool.getLabel());
    this.setBorderPainted(false);
    this.setRolloverEnabled(true);
    this.setHorizontalAlignment(JCheckBox.CENTER);

    setBorder(new CompoundBorder(BorderFactory.createEtchedBorder(),
                                 BorderFactory.createEmptyBorder(1,1,1,1)));

    this.addMouseListener(new MouseAdapter()
    {
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
    if (ic != null)
    {
      // check if icon available
      ImageIcon ii = (ImageIcon) ic;
      if (ii.getImageLoadStatus() == java.awt.MediaTracker.ERRORED)
      {
        this.setIcon(null);
        this.setText(theTool.getLabel());
      }
      else
      {
        // try to set the other icon, if we can
        String iName = theTool.getImage();
        if (iName != null)
        {
          // try to extend the filename
          String selectedIconName = iName.substring(0, iName.indexOf("."));
          selectedIconName += "_.gif";
          super.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource(selectedIconName)));
        }
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
    /** check that we have a tool declared
     */
    if (_theTool != null)
      _theTool.execute();
  }

  /** return the tool for this button
   */
  protected Tool getTool()
  {
    return _theTool;
  }
}
