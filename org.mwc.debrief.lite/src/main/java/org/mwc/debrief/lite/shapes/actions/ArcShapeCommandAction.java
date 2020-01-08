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
package org.mwc.debrief.lite.shapes.actions;

import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Shapes.ArcShape;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class ArcShapeCommandAction extends CreateShape implements CommandAction
{
  public ArcShapeCommandAction(ToolParent theParent, PropertiesPanel thePanel,
      Layers theData, String theName, String theImage, BoundsProvider bounds)
  {
    super(theParent, thePanel, theData, theName, theImage, bounds);
  }


  protected ShapeWrapper getShape(final WorldLocation centre)
  {
    return new ShapeWrapper("new arc", new ArcShape(centre,
        new WorldDistance(4000, WorldDistance.YARDS), 135, 90, true,
        false), DebriefColors.RED, null);
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
    actionPerformed(e);
    
  }

}
