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

package org.mwc.debrief.lite.shapes.actions;

import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Palette.CreateGrid;

/**
 * @author Ayesha
 *
 */
public class CreateGridCommandAction extends CreateGrid implements CommandAction
{

  public CreateGridCommandAction(ToolParent theParent, PropertiesPanel thePanel,
      Layers theData, BoundsProvider theBounds)
  {
    super(theParent, thePanel, theData, theBounds);
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
    super.execute();

  }

}
