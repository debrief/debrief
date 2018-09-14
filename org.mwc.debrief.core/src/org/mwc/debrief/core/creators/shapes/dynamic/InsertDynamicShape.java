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

import org.mwc.debrief.core.creators.shapes.CoreInsertShape;

import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public abstract class InsertDynamicShape extends CoreInsertShape
{
  @Override
  protected Plottable getPlottable(PlainChart theChart)
  {
    final Layers theLayers = theChart.getLayers();
    final Date startDate = getTimeControllerDate(theLayers,true);
    final Date endDate = getTimeControllerDate(theLayers,false);
    return getDynamicShape(startDate,endDate);
    
  }
  
  protected abstract DynamicShapeWrapper getDynamicShape(final Date startDate,final Date endDate);

  @Override
  protected PlainShape getShape(WorldLocation centre)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getShapeName()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
