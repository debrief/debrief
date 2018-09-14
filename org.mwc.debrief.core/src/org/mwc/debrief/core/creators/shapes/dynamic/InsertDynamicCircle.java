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

import org.mwc.debrief.core.wizards.dynshapes.DynamicCircleWizard;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeWizard;

/**
 * @author Ayesha
 *
 */
public class InsertDynamicCircle extends InsertDynamicShape
{
  @Override
  protected DynamicShapeWizard getWizard(final Date startDate, final Date endDate)
  {
    return new DynamicCircleWizard(startDate, endDate);
  }
}
