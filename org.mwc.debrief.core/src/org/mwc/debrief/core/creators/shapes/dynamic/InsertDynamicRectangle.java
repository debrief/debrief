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
package org.mwc.debrief.core.creators.shapes.dynamic;

import java.util.Date;

import org.mwc.debrief.core.wizards.dynshapes.CoreDynamicShapeWizard;
import org.mwc.debrief.core.wizards.dynshapes.DynamicRectangleBoundsPage;

import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class InsertDynamicRectangle extends InsertDynamicShape<CoreDynamicShapeWizard<?>>{
  
  @Override
  protected CoreDynamicShapeWizard<DynamicRectangleBoundsPage> getWizard(
      final Date startDate, final Date endDate, final WorldLocation center)
  {
    return new CoreDynamicShapeWizard<DynamicRectangleBoundsPage>("Circle",
        startDate, endDate)
    {
      @Override
      protected DynamicRectangleBoundsPage getBoundsPage()
      {
        return new DynamicRectangleBoundsPage("Bounds", center);
      }

      @Override
      protected PlainShape getShape(DynamicRectangleBoundsPage page)
      {
        WorldLocation topLeft = page.getTopLeftLocation();
        WorldLocation bottomRight = page.getBottomRightLocation();
        return new RectangleShape(topLeft, bottomRight);
      }
    };
  }
}
