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
