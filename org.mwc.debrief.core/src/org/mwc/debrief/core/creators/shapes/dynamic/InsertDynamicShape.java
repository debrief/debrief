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

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.mwc.debrief.core.creators.shapes.CoreInsertShape;
import org.mwc.debrief.core.wizards.dynshapes.DynamicShapeWizard;

import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public abstract class InsertDynamicShape extends CoreInsertShape
{
  @Override
  protected Plottable getPlottable(final PlainChart theChart)
  {
    final Layers theLayers = theChart.getLayers();
    final Date startDate = getTimeControllerDate(theLayers,true);
    final Date endDate = getTimeControllerDate(theLayers,false);
    final WorldArea wa = theChart.getCanvas().getProjection().getVisibleDataArea();
    
    // get centre of area (at zero depth)
    final WorldLocation centre = wa.getCentreAtSurface();
    return getDynamicShape(startDate,endDate,centre);
    
  }
  
  protected DynamicShapeWrapper getDynamicShape(final Date startDate, final Date endDate,final WorldLocation center)
  {
    final DynamicShapeWizard wizard = getWizard(startDate, endDate,center);
    final WizardDialog wd = new WizardDialog(getShell(), wizard);
    final DynamicShapeWrapper thisShape;
    if(wd.open()==Window.OK) {
      
      //get all param details from the wizard now.
      thisShape = wizard.getDynamicShapeWrapper();
    }
    else {
      thisShape = null;
    }
    return thisShape;
  }
  
  abstract protected DynamicShapeWizard getWizard(final Date startDate, final Date endDate,final WorldLocation center);

  @Override
  protected PlainShape getShape(WorldLocation centre)
  {
    // not implemented, since we use our own getPlottable() method
    throw new IllegalArgumentException(
        "getShape() should not be called for this class. Not implemented");
  }

  @Override
  protected String getShapeName()
  {
    // not implemented, since we use our own getPlottable() method
    throw new IllegalArgumentException(
        "getShapeName() should not be called for this class. Not implemented");
  }

}
