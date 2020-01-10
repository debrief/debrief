/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.lite.view.actions;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.PanAction;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

/**
 * @author Ayesha
 *
 */
public class PanCommandAction extends PanAction implements CommandAction
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public PanCommandAction(MapPane mapPane)
  {
    super(mapPane);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
   super.actionPerformed(e);

  }

}
