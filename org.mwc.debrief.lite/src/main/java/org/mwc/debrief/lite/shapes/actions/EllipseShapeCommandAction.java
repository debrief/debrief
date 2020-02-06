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
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class EllipseShapeCommandAction extends CreateShape implements
    CommandAction
{

  public EllipseShapeCommandAction(ToolParent theParent,
      PropertiesPanel thePanel, Layers theData, String theName, String theImage,
      BoundsProvider bounds)
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
    return new ShapeWrapper("new ellipse", new EllipseShape(centre, 30,
        new WorldDistance(5, WorldDistance.KM), new WorldDistance(3,
            WorldDistance.KM)), DebriefColors.RED, null);
  }

}
