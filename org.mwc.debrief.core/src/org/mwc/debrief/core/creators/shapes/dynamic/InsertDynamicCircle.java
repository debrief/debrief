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
import org.mwc.debrief.core.wizards.dynshapes.DynamicCircleBoundsPage;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeBaseWizardPage;

import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class InsertDynamicCircle extends
    InsertDynamicShape<CoreDynamicShapeWizard<?>>
{
  @Override
  protected CoreDynamicShapeWizard<DynamicCircleBoundsPage> getWizard(
      final Date startDate, final Date endDate, final WorldLocation center)
  {
    return new CoreDynamicShapeWizard<DynamicCircleBoundsPage>("Circle",
        startDate, endDate)
    {
      @Override
      protected DynamicCircleBoundsPage getBoundsPage()
      {
        return new DynamicCircleBoundsPage(
            DynamicShapeBaseWizardPage.BOUNDS_PAGE, center);
      }

      @Override
      protected PlainShape getShape(DynamicCircleBoundsPage page)
      {
        return new CircleShape(page.getCenter(), page.getRadius());
      }
    };
  }
}
