package com.borlander.rac353542.bislider;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;


public abstract class BiSliderComponentBase implements Disposable {
    private final BiSlider myBiSlider;

    protected BiSliderComponentBase(BiSlider biSlider){
        myBiSlider = biSlider;
        myBiSlider.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                freeResources();
            }
        });
    }
    
    public abstract void freeResources();
    
    protected final BiSlider getBiSlider() {
        return myBiSlider;
    }
    
    protected final BiSliderDataModel getDataModel(){
        return myBiSlider.getDataModel();
    }

    protected final CoordinateMapper getMapper() {
        return myBiSlider.getCoordinateMapper();
    }

    protected final BiSliderUIModel getUIModel() {
        return myBiSlider.getUIModel();
    }
    
    protected String getLabel(double value){
        String result = getLabelProvider().getLabel(value);
        if (result != null){
            result = result.trim();
            if (result.length() == 0){
                result = null;
            }
        }
        return result;
    }

    protected final Rectangle getDrawArea(){
        return getMapper().getScreenBounds();
    }

    private BiSliderLabelProvider getLabelProvider(){
        return myBiSlider.getLabelProvider();
    }
    
}
