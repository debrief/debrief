/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.lite.custom;

import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;
import org.pushingpixels.flamingo.api.ribbon.synapse.projection.ComponentProjection;

/**
 * @author Ayesha
 *
 */
public class RibbonLabelProjection extends ComponentProjection<JRibbonLabel, LabelComponentContentModel>
{

  public RibbonLabelProjection(LabelComponentContentModel contentModel,
      ComponentPresentationModel presentationModel,
      ComponentSupplier<JRibbonLabel, LabelComponentContentModel, ComponentPresentationModel> componentSupplier)
  {
    super(contentModel, presentationModel, componentSupplier);
  }

  @Override
  protected void configureComponent(JRibbonLabel component)
  {
    // TODO Auto-generated method stub
    
  }

}
