/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.borlander.rac353542.bislider.*;

abstract class BiSliderComponentBase implements Disposable {
    private final BiSliderImpl myBiSlider;

    protected BiSliderComponentBase(BiSliderImpl biSlider){
        myBiSlider = biSlider;
        myBiSlider.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                freeResources();
            }
        });
    }
    
    public abstract void freeResources();
    
    protected final BiSliderImpl getBiSlider() {
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
    
    protected final ColorDescriptor updateColorDescriptor(ColorDescriptor oldDescriptor, RGB newRGB){
        if (oldDescriptor == null || !oldDescriptor.getRGB().equals(newRGB)){
            if (oldDescriptor != null){
                oldDescriptor.freeResources();
            }
            oldDescriptor = new ColorDescriptor(newRGB);
        }
        return oldDescriptor;
    }

    protected final Rectangle getDrawArea(){
        return getMapper().getDrawArea();
    }

    private BiSliderLabelProvider getLabelProvider(){
        return getUIModel().getLabelProvider();
    }
    
}
