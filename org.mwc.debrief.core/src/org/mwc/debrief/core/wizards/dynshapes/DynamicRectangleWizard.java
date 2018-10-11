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
package org.mwc.debrief.core.wizards.dynshapes;

import java.util.Date;

import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class DynamicRectangleWizard extends CoreDynamicShapeWizard
{
  private WorldLocation _centre;
  public DynamicRectangleWizard(Date startDate,Date endDate,WorldLocation centre)
  {
    super("Rectangle", startDate, endDate);
    _centre = centre;
  }
  
  @Override
  DynamicShapeBaseWizardPage getBoundsPage()
  {
    return new DynamicRectangleBoundsPage("Bounds",_centre);
  }

  @Override
  protected PlainShape getShape()
  {    
    DynamicRectangleBoundsPage page = (DynamicRectangleBoundsPage) _boundsPage;
    WorldLocation topLeft = page.getTopLeftLocation();
    WorldLocation bottomRight = page.getBottomRightLocation();
    return new RectangleShape(topLeft, bottomRight);
  }
}
