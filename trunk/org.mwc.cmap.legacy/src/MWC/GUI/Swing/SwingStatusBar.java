package MWC.GUI.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingStatusBar.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingStatusBar.java,v $
// Revision 1.2  2004/05/25 15:37:06  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:24  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:38  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-10-28 09:24:37+00  ian_mayo
// minor tidying (from IntelliJ Idea)
//
// Revision 1.2  2002-05-28 09:25:57+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:15+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:46+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-01-24 14:22:31+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.1  2002-01-17 20:40:45+00  administrator
// Insert space character before text string (To give left-hand border)
//
// Revision 1.0  2001-07-17 08:43:05+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-14 11:57:43+01  novatech
// add comments, & include formatting/units conversion of range data
//
// Revision 1.1  2001-01-03 13:42:06+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:46  ianmayo
// initial version
//
// Revision 1.4  2000-03-17 13:39:51+00  ian_mayo
// Removed unnecessary method
//
// Revision 1.3  1999-11-25 16:54:04+00  ian_mayo
// tidied up locations
//
// Revision 1.2  1999-11-23 11:15:17+00  ian_mayo
// moved directory
//
// Revision 1.1  1999-11-16 17:05:21+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-10-12 15:37:04+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:49+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-23 14:04:01+01  administrator
// Initial revision
//

import javax.swing.JLabel;
import javax.swing.JPanel;

import MWC.GUI.StatusBar;

/**
 * Class providing Swing implementation of StatusBar.
 */
public class SwingStatusBar extends JPanel implements StatusBar
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
  // member variables
  /**
 * ////////////////////////////////////////////////////////////
 * the text label we are managing
 */
  protected JLabel theText;

  /**
   * support class to help us format the text & set the correct unites
   */
  protected MWC.GUI.AWT.AWTStatusBar.StatusBarSupport _support;

  /** the property editor
   *
   */
  protected MWC.GUI.Properties.PropertiesPanel _theEditor;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public SwingStatusBar(MWC.GUI.Properties.PropertiesPanel editor, MWC.GUI.ToolParent parent)
  {
    theText = new JLabel("             ");
    theText.setAlignmentX(JLabel.CENTER);
    theText.setToolTipText("Double-click to change units");
    java.awt.BorderLayout lm = new java.awt.BorderLayout();
    setLayout(lm);
    add("Center", theText);

    _support = new MWC.GUI.AWT.AWTStatusBar.StatusBarSupport();
    _support.setParent(parent);

    // handle a double-click on the status bar (used to set units)
    theText.addMouseListener(new java.awt.event.MouseAdapter(){
      public void mouseClicked(java.awt.event.MouseEvent e)
      {
        if(e.getClickCount() == 2)
        {
          doubleClicked();
        }

      }
    });

    _theEditor = editor;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public void doubleClicked()
  {
    // go for it, using the property editor
    _theEditor.addEditor(_support.getInfo(), null);

  }

  public void setText(String theVal)
  {
    theText.setText(" " + theVal);
  }

  /** set range and bearing data in this text panel
   *  @param range the range in degrees
   *  @param bearing the bearing in radians
   */
  public void setRngBearing(double range, double bearing)
  {
    String rngStr = _support.formatRange(range);
    String brgStr = _support.formatBearing(bearing);

    setText(rngStr + " " + brgStr);
  }


  public void paint(java.awt.Graphics p1)
  {
    super.paint(p1);

    java.awt.Rectangle rt = super.getBounds();
    p1.setColor(java.awt.Color.lightGray);
    p1.draw3DRect(1, 1, rt.width-3, rt.height-3, false);
  }



}
