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
package MWC.GUI.Properties.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTEditFrame.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTEditFrame.java,v $
// Revision 1.2  2004/05/25 15:29:22  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:25  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:45+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:32+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:44+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:43+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:24  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:46+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:06:03+01  administrator
// Initial revision
//

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditor;

/////////////////////////////////////////////////////
// frame to pop up, and allow editing
///////////////////////////////////////////////////
    
public class AWTEditFrame extends Dialog implements ActionListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected PropertyEditor _pe;
  public AWTEditFrame(final Frame parent, final PropertyEditor pe)
  {
    super(parent);
    _pe = pe;
    initForm();
    setBackground(SystemColor.control);
    final Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
    final Rectangle dme = getBounds();
    setLocation( (di.width - dme.width)/2,
                 (di.height - dme.height)/2);
    this.addWindowListener(new WindowAdapter(){
      public void windowClosing(final WindowEvent e)
      {
        dispose();
      }
      });
        
  }
  public void initForm()
  {
    setLayout(new BorderLayout());
    final Component cp = _pe.getCustomEditor();
    add("Center", cp);
    final Button fin = new Button("Done");
    fin.addActionListener(this);
    add("South", fin);
    final Dimension sz = cp.getPreferredSize();
    setSize(sz.width + 50,
            sz.height + fin.getPreferredSize().height + 50);
  }
      
  public void actionPerformed(final ActionEvent e)
  {
    dispose();
  }
}
