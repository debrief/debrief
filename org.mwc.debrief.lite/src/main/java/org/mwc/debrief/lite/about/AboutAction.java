/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.about;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class AboutAction extends AbstractAction
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JFrame parent;
  public AboutAction(String title, JFrame parent) {
    super(title);
    this.parent = parent;
  }

  /* (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e)
  {
    Debrief.GUI.Frames.Swing.AboutDialog.showIt(parent, "About", "Debrief Lite v1.0. \nDeveloped by Deep Blue C Ltd.");

  }

}
