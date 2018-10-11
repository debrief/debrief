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

import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldLocation;

/**
 * Wizard for inputting parameters required for
 * creating a dynamic circle
 * @author Ayesha
 *
 */
public class DynamicCircleWizard extends CoreDynamicShapeWizard 
{
  
  public static final String SHAPE_NAME = "Circle";
  private WorldLocation _centre;

  public DynamicCircleWizard(Date startDate,Date endDate,WorldLocation centre)
  {
    super("Circle", startDate, endDate);
    _centre = centre;
  }
  
  @Override
  protected DynamicShapeBaseWizardPage getBoundsPage()
  {
    return new DynamicCircleBoundsPage(DynamicShapeBaseWizardPage.BOUNDS_PAGE,_centre);
  }

  @Override
  protected PlainShape getShape()
  {
    DynamicCircleBoundsPage page = (DynamicCircleBoundsPage) _boundsPage;
    return new CircleShape(page.getCenter(), page.getRadius());
  }

}
