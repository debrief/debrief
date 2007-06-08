package com.borlander.rac353542.bislider;

import org.eclipse.swt.widgets.Composite;

public abstract class BiSlider extends Composite {
    public BiSlider(Composite parent, int style){
        super(parent, style);
    }
    
    public abstract BiSliderDataModel getDataModel();
    public abstract BiSliderDataModel.Writable getWritableDataModel();
    public abstract BiSliderUIModel getUIModel();
}
