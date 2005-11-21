package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.impl.DragSupport.DragListener;


class BiSliderPointer extends BiSliderComponentBase implements DragListener, Disposable {
    private final boolean myMinNotMax;
    private final PointerDrawer myDrawer;
    private DragSupport myDragSupport;
    private boolean myIsDragging;

    public BiSliderPointer(BiSliderImpl biSlider, boolean minNotMax){
        super(biSlider);
        myMinNotMax = minNotMax;
        myDrawer = new DefaultSliderPointer(!minNotMax, minNotMax);
        myDragSupport = new DragSupport(getBiSlider(), myDrawer.getAreaGate(), this);
    }
    
    public void mouseDragged(MouseEvent e, Point startPoint) {
        myIsDragging = true;
        CoordinateMapper mapper = getMapper();
        double currentValue = mapper.pixel2value(e.x, e.y);
        setDataModelUserValue(currentValue);
    }
    
    public void dragFinished() {
        myIsDragging = false;
        getBiSlider().redraw();
    }
    
    public void freeResources() {
        myDragSupport.releaseControl();
        myDrawer.freeResources();
    }

    public void paintPointer(GC gc) {
        double dataValue = getDataModelUserValue(); 
        Point pointerAt = getMapper().value2pixel(dataValue, !myMinNotMax);
        if (myIsDragging){
            String label = getLabel(dataValue);
            myDrawer.paintPointer(gc, pointerAt, label);
        } else {
            myDrawer.paintPointer(gc, pointerAt);
        }
    }

    private double getDataModelUserValue() {
        return myMinNotMax ? getDataModel().getUserMinimum() : getDataModel().getUserMaximum();
    }
    
    private double setDataModelUserValue(double value){
        if (myMinNotMax) {
            getWritableDataModel().setUserMinimum(value);
        } else {
            getWritableDataModel().setUserMaximum(value);
        }
        return getDataModelUserValue();
    }
    
    private BiSliderDataModel.Writable getWritableDataModel(){
        return getBiSlider().getWritableDataModel();
    }

}
