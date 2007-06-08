package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.borlander.rac353542.bislider.*;

/**
 * The gate allowing to separate API and implementation of BiSlider. It should
 * be the only externally visible class in the implementation package.
 */
public class BiSliderFactoryImpl extends BiSliderFactory {

    public BiSlider createBiSlider(Composite parent, BiSliderDataModel.Writable dataModel, BiSliderUIModel uiConfig) {
        return new BiSliderImpl(parent, SWT.NONE, dataModel, uiConfig);
    }
}
