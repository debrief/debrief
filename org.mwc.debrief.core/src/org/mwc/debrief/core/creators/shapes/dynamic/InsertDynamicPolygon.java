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

import org.mwc.debrief.core.wizards.dynshapes.DynamicPolygonWizard;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeWizard;

import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class InsertDynamicPolygon extends InsertDynamicShape
{
  @Override
  protected DynamicShapeWizard getWizard(Date startDate, Date endDate,WorldLocation center)
  {
    return new DynamicPolygonWizard(startDate,endDate);
  }

}
