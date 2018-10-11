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
 */package org.mwc.debrief.core.wizards.dynshapes;

import java.util.Date;

import MWC.GUI.Shapes.PlainShape;

/**
 * @author Ayesha
 *
 */
public class DynamicPolygonWizard extends CoreDynamicShapeWizard 
{


  public DynamicPolygonWizard(Date startDate,Date endDate)
  {
    super("Polygon", startDate, endDate);
  }

  @Override
  DynamicShapeBaseWizardPage getBoundsPage()
  {
    return new DynamicPolygonBoundsPage(DynamicShapeBaseWizardPage.BOUNDS_PAGE);
  }

  @Override
  protected PlainShape getShape()
  {    
    DynamicPolygonBoundsPage page = (DynamicPolygonBoundsPage) _boundsPage;
    return page.getPolygonShape();
  }

}
