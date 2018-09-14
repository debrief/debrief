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
import org.mwc.debrief.core.wizards.dynshapes.DynamicRectangleWizard;

import Debrief.Wrappers.DynamicShapeWrapper;

/**
 * @author Ayesha
 *
 */
public class InsertDynamicRectangle extends InsertDynamicShape{

  @Override
  protected DynamicShapeWrapper getDynamicShape(Date startDate, Date endDate)
  {
    DynamicRectangleWizard wizard = new DynamicRectangleWizard(startDate,endDate);
    WizardDialog wd = new WizardDialog(getShell(), wizard);
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



}
