package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.mwc.cmap.TimeController.controls.DTGBiSlider.DoFineControl;

import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.impl.DragSupport.DragListener;


class BiSliderPointer extends BiSliderComponentBase implements DragListener, AreaGate  {
    final boolean myMinNotMax;
    final PointerDrawer myDrawer;
    private final Segmenter mySegmenter;
    private DragSupport myDragSupport;
    private boolean myShowValueLabel;
    private MouseListener myFineTunePopupShower;
    private FineTuneValueAdjuster myFineTunePopup;

    public BiSliderPointer(BiSliderImpl biSlider, boolean minNotMax, 
    		Segmenter segmenter, final DoFineControl handler){
        super(biSlider);
        myMinNotMax = minNotMax;
        mySegmenter = segmenter;
        myDrawer = new DefaultSliderPointer(!minNotMax, minNotMax);
        myDragSupport = new DragSupport(getBiSlider(), myDrawer.getAreaGate(), this);
        myFineTunePopup = new FineTuneValueAdjuster(getBiSlider(), myMinNotMax);        
        myFineTunePopupShower = new MouseAdapter(){
					public void mouseDoubleClick(final MouseEvent e)
					{
            if (myDrawer.getAreaGate().isInsideArea(e.x, e.y)){
  						handler.adjust(myMinNotMax);
          }
					}};               
        getBiSlider().addMouseListener(myFineTunePopupShower);
    }
    
    public void mouseDragged(MouseEvent e, Point startPoint) {
        setShowValueLabel(true);
        CoordinateMapper mapper = getMapper();
        double currentValue = mapper.pixel2value(e.x, e.y);
        if ((e.stateMask & SWT.SHIFT) > 0){
            ColoredSegment segment = mySegmenter.getSegment(currentValue);
            double startValue = mapper.pixel2value(startPoint.x, startPoint.y);
            if (startValue < currentValue){
                currentValue = segment.getMaxValue();
            } else {
                currentValue = segment.getMinValue();
            }
        }
        setDataModelUserValue(currentValue);
    }
    
    public void setShowValueLabel(boolean showValueLabel){
        myShowValueLabel = showValueLabel;
    }
    
    public AreaGate getPointerAreaGate(){
        return myDrawer == null ? null : myDrawer.getAreaGate();
    }
    
    public void dragStarted() {
        getWritableDataModel().startCompositeUpdate();
    }
    
    public void dragFinished() {
        setShowValueLabel(false);
        getWritableDataModel().finishCompositeUpdate();
        getBiSlider().redraw();
    }
    
    public void freeResources() {
        if (myFineTunePopupShower != null){
            getBiSlider().removeMouseListener(myFineTunePopupShower);
            myFineTunePopupShower = null;
        }
        myDragSupport.releaseControl();
        myDrawer.freeResources();
    }
    
    public Point getPointerBasePoint(){
        return getMapper().value2pixel(getDataModelUserValue(), myMinNotMax);        
    }

    public void paintPointer(GC gc) {
        double dataValue = getDataModelUserValue(); 
        Point pointerAt = getPointerBasePoint();
        if (myShowValueLabel){
            String label = getLabel(dataValue);
            myDrawer.paintPointer(gc, pointerAt, label);
        } else {
            myDrawer.paintPointer(gc, pointerAt);
        }
    }
    
    public void disposeFineTuneAdjuster(boolean acceptValues){
        myFineTunePopup.disposeAdjustmentControl(acceptValues);
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
    
    public boolean isInsideArea(int x, int y) {
        return myDrawer.getAreaGate().isInsideArea(x, y);
    }

}
