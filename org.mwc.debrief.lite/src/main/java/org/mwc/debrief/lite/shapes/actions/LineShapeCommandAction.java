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

import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * @author Ayesha
 *
 */
public class LineShapeCommandAction extends CreateShape implements CommandAction
{
  

  public LineShapeCommandAction(ToolParent theParent, PropertiesPanel thePanel,
      Layers theData, String theName, String theImage, BoundsProvider bounds)
  {
    super(theParent, thePanel, theData, theName, theImage, bounds);
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
   super.execute();
    
  }

  @Override
  protected ShapeWrapper getShape(final WorldLocation centre)
  {
    return new ShapeWrapper("new line", new LineShape(centre, centre
        .add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45.0),
            0.05, 0))), DebriefColors.RED, null);
  }

  

}
