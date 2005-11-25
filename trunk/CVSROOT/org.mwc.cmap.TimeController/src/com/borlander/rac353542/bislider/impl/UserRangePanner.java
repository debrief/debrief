package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import com.borlander.rac353542.bislider.BiSliderDataModel;



class UserRangePanner implements DragSupport.DragListener, Disposable {
    private final BiSliderImpl myBiSlider;
    private final BiSliderPointer myMinPointer;
    private final BiSliderPointer myMaxPointer;
    private final Point myCachedStartPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
    private double myCachedAppliedDelta;
    private DragSupport myDragSupport;

    public UserRangePanner(BiSliderImpl biSlider, BiSliderPointer minPointer, BiSliderPointer maxPointer){
        myBiSlider = biSlider;
        myMinPointer = minPointer;
        myMaxPointer = maxPointer;
        myDragSupport = new DragSupport(myBiSlider, new OutsidePointersUserSelectedArea(), this);
    }
    
    public void dragFinished() {
        setShowValueLabels(false);
        myBiSlider.redraw();
    }
    
    public void freeResources() {
        if (myDragSupport != null){
            myDragSupport.releaseControl();
            myDragSupport = null;
        }
    }
    
    public void mouseDragged(MouseEvent e, Point startPoint) {
        setShowValueLabels(true);
        myBiSlider.redraw();
        
        CoordinateMapper mapper = myBiSlider.getCoordinateMapper();
        double requestedValue = mapper.pixel2value(e.x, e.y);
        double initialValue = mapper.pixel2value(startPoint);
        double requestedDelta = requestedValue - initialValue;
        double alreadyAppliedDelta = getAlreadyAppliedDelta(startPoint);
        double additionalDelta = requestedDelta - alreadyAppliedDelta;
        
        BiSliderDataModel.Writable dataModel = myBiSlider.getWritableDataModel();
        double newUserMin = dataModel.getUserMinimum() + additionalDelta;
        double newUserMax = dataModel.getUserMaximum() + additionalDelta;
        
        double totalMin = dataModel.getTotalMinimum();
        double totalMax = dataModel.getTotalMaximum();
        
        if (newUserMin < totalMin){
            additionalDelta += totalMin - newUserMin;
            newUserMax += totalMin - newUserMin;
            newUserMin = totalMin;
        }
        
        if (newUserMax > totalMax){
            additionalDelta += totalMax - newUserMax;
            newUserMin += totalMax - newUserMax;
            newUserMax = totalMax;
        }
        
        setAlreadyAppliedDelta(startPoint, requestedDelta);
        if (additionalDelta != 0){
            dataModel.setUserRange(newUserMin, newUserMax);
            
        }
    }
    
    private void setShowValueLabels(boolean showValueLabels){
        myMaxPointer.setShowValueLabel(showValueLabels);
        myMinPointer.setShowValueLabel(showValueLabels);
    }
    
    private double getAlreadyAppliedDelta(Point startPoint) {
        return startPoint.equals(myCachedStartPoint) ? myCachedAppliedDelta : 0; 
    }

    private void setAlreadyAppliedDelta(Point startPoint, double appliedDelta) {
        myCachedStartPoint.x = startPoint.x;
        myCachedStartPoint.y = startPoint.y;
        myCachedAppliedDelta = appliedDelta;
    }

    private class OutsidePointersUserSelectedArea implements AreaGate {
        public boolean isInsideArea(int x, int y) {
            CoordinateMapper mapper = myBiSlider.getCoordinateMapper();
            if (!mapper.getDrawArea().contains(x, y)){
                return false;
            }
            
            AreaGate minPointerArea = myMinPointer.getPointerAreaGate();
            AreaGate maxPointerArea = myMaxPointer.getPointerAreaGate();
            
            if (minPointerArea != null && minPointerArea.isInsideArea(x, y)){
                return false;
            }
            if (maxPointerArea != null && maxPointerArea.isInsideArea(x, y)){
                return false;
            }
            
            BiSliderDataModel dataModel = myBiSlider.getDataModel();
            double value = mapper.pixel2value(x, y);
            return dataModel.getUserMinimum() <= value && value <= dataModel.getUserMaximum();
        }
    }
}
