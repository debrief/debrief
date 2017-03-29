/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import MWC.GUI.Tool;

/**
 * extension of normal Swing menu to create a menu item containing a Debrief tool item
 */
public class SwingMenuItem extends JMenuItem implements
    java.awt.event.ActionListener
{
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  // ///////////////////////////////////////////////////////////
  // member variables
  // //////////////////////////////////////////////////////////
  /**
   * the Debrief tool we are calling
   */
  final protected Tool _theTool;

  // ///////////////////////////////////////////////////////////
  // constructor
  // //////////////////////////////////////////////////////////
  public SwingMenuItem(final String theLabel, final Tool theTool)
  {
    super(theLabel);
    _theTool = theTool;
    this.addActionListener(this);
  }

  // ///////////////////////////////////////////////////////////
  // member functions
  // //////////////////////////////////////////////////////////

  @Override
  public Icon getIcon()
  {
    ImageIcon im = null;

    // do we have an icon?
    final String iconPath = _theTool.getImage();
    if (iconPath != null)
    {
      final java.lang.ClassLoader loader = getClass().getClassLoader();
      if (loader != null)
      {
        final java.net.URL imLoc = loader.getResource(iconPath);
        if(imLoc != null)
        {
          im = new ImageIcon(imLoc);
        }
        else
        {
          System.out.println("Can't find icon for:" + iconPath);
        }
      }
    }
    return im;
  }

  protected Tool getTool()
  {
    return _theTool;
  }

  /**
   * callback function for when menu item if selected
   */
  public void actionPerformed(final java.awt.event.ActionEvent e)
  {
    /**
     * check that we have a tool declared
     */
    if (_theTool != null)
      _theTool.execute();
  }
}
