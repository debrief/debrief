/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.TimeController.controls.DTGBiSlider.DoFineControl;

import com.borlander.rac353542.bislider.*;
import com.borlander.rac353542.bislider.BiSliderDataModel.Writable;

/**
 * The gate allowing to separate API and implementation of BiSlider. It should
 * be the only externally visible class in the implementation package.
 */
public class BiSliderFactoryImpl extends BiSliderFactory {

		@Override
		public BiSlider createBiSlider(Composite parent, Writable dataModel,
				BiSliderUIModel uiConfig, DoFineControl handler)
		{
      return new BiSliderImpl(parent, SWT.NONE, dataModel, uiConfig, handler);
		}
}
