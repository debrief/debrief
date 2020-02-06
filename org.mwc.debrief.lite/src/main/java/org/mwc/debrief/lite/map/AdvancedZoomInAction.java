/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.debrief.lite.map;

import java.awt.event.ActionEvent;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.ZoomInAction;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

/**
 * use our advanced zoom in tool, that also support zoom out
 *
 * @author ian
 *
 */
public class AdvancedZoomInAction extends ZoomInAction implements CommandAction
{

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public AdvancedZoomInAction(final MapPane mapPane)
  {
    super(mapPane);
  }

  @Override
  public void actionPerformed(final ActionEvent ev)
  {
    getMapPane().setCursorTool(new AdvancedZoomInTool());
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
    actionPerformed(e);
    
  }
}
