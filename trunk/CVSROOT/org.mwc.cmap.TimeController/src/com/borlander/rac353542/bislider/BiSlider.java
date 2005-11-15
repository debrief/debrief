package com.borlander.rac353542.bislider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class BiSlider extends Composite implements Disposable, BiSliderDataModel.Listener {
    private final BiSliderDataModel.Writable myDataModel;
    private final BiSliderUIModel myUiModel;
    private BiSliderLabelProvider myLabelProvider;
    private final CoordinateMapper myMapper;
    private BiSliderPointer myMinPointer;
    private BiSliderPointer myMaxPointer;
    private BiSliderContents myContents;
    private BiSliderOutline myOutline;
    private Color myForeground; 
    
    public BiSlider(Composite parent, int style){
        this(parent, style | SWT.DOUBLE_BUFFERED, new BiSliderDataModelImpl(), new BiSliderUIModelImpl());
    }
     
    public BiSlider(Composite parent, int style, BiSliderDataModel.Writable dataModel, BiSliderUIModel uiModel){
        super(parent, style);
        setFont(parent.getFont());
        myDataModel = dataModel;
        myUiModel = uiModel;
        myLabelProvider = BiSliderLabelProvider.TO_STRING;
        myForeground = ColorManager.getInstance().getColor(255, 0, 0);
        myMapper = new CoordinateMapperImpl(myUiModel.isVertical());
        myMaxPointer = new BiSliderPointer(this, true);
        myMinPointer = new BiSliderPointer(this, false);
        myContents = new BiSliderContents(this);
        myOutline = new BiSliderOutline(this);
        
        dataModel.addListener(this);
        
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                freeResources();
            }
        });
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                BiSlider.this.paintBiSlider(e.gc);
            }
        });
    }
    
    public BiSliderDataModel getDataModel(){
        return getWritableDataModel();
    }
    
    public BiSliderUIModel getUIModel(){
        return myUiModel;
    }
    
    public void freeResources() {
        myDataModel.removeListener(this);
        if (myMaxPointer != null){
            myMaxPointer.freeResources();
            myMaxPointer = null;
        }
        if (myMinPointer != null){
            myMinPointer.freeResources();
            myMinPointer = null;
        }
        if (myForeground != null){
            ColorManager.getInstance().releaseColor(myForeground);
            myForeground = null;
        }
    }
    
    public void dataModelChanged(BiSliderDataModel dataModel) {
        if (!isDisposed()){
            redraw();
        }
    }
    
    /**
     * intentionally package local
     */
    CoordinateMapper getCoordinateMapper(){
        return myMapper;
    }
    
    /**
     * intentionally package local
     */
    BiSliderDataModel.Writable getWritableDataModel() {
        return myDataModel;
    }
    
    /**
     * intentionally package local
     */
    public BiSliderLabelProvider getLabelProvider() {
        return myLabelProvider;
    }

    private void paintBiSlider(GC gc){
        Rectangle drawArea = myUiModel.getDrawArea(getClientArea());
        myMapper.setContext(myDataModel, drawArea);
        
        myContents.paintContents(gc);
        myMinPointer.paintPointer(gc);
        myMaxPointer.paintPointer(gc);
        myOutline.paintOutline(gc);
    }
    
}
