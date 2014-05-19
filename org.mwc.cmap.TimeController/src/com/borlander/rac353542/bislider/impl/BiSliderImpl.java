package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.TimeController.controls.DTGBiSlider.DoFineControl;

import com.borlander.rac353542.bislider.*;

/**
 * Actual BiLsider implementation.
 * <p>
 * Intentionally package local
 */
class BiSliderImpl extends BiSlider implements Disposable, BiSliderDataModel.Listener, BiSliderUIModel.Listener{
    private final static int MIN_NORMAL_SIZE = 20;
	private final BiSliderDataModel.Writable myDataModel;
    private final BiSliderUIModel myUiModel;
    private final CoordinateMapper myMapper;
    BiSliderPointer myMinPointer;
    BiSliderPointer myMaxPointer;
    private BiSliderContents myContents;
    private BiSliderOutline myOutline;
    private Rectangle myCachedClientArea;
    private Rectangle myCachedDrawArea;
    private Segmenter mySegmenter;
    private LabelSupport myLabelSupport;
    private UserRangePanner myUserRangePanner;

    public BiSliderImpl(Composite parent, int style, BiSliderDataModel.Writable dataModel, 
    		BiSliderUIModel uiModel, DoFineControl handler) {
        super(parent, style | SWT.DOUBLE_BUFFERED);
        setFont(parent.getFont());
        myDataModel = dataModel;
        myUiModel = uiModel;
        myLabelSupport = new LabelSupport(this);
        myMapper = new CoordinateMapperImpl(myUiModel.isVertical());
        mySegmenter = new Segmenter(myDataModel, myUiModel);
        myMaxPointer = new BiSliderPointer(this, true, mySegmenter, handler);
        myMinPointer = new BiSliderPointer(this, false, mySegmenter, handler);
        
        myContents = new BiSliderContents(this, mySegmenter);
        myOutline = new BiSliderOutline(this, myLabelSupport);
        myUserRangePanner = new UserRangePanner(this, myMinPointer, myMaxPointer, mySegmenter);
        dataModel.addListener(this);
        uiModel.addListener(this);
        addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {
                freeResources();
            }
        });
        addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {
                BiSliderImpl.this.paintBiSlider(e.gc);
            }
        });
        addMouseListener(new SegmentSelector(this, mySegmenter));
        addMouseListener(new MouseAdapter() {

            public void mouseDown(MouseEvent e) {
                boolean isFocusControl = isFocusControl();
                if (!isFocusControl) {
                    if (myMaxPointer != null) {
                        myMaxPointer.disposeFineTuneAdjuster(true);
                    }
                    if (myMinPointer != null) {
                        myMinPointer.disposeFineTuneAdjuster(true);
                    }
                    setFocus();
                }
            }
        });
    }

    public BiSliderDataModel getDataModel() {
        return getWritableDataModel();
    }

    public BiSliderUIModel getUIModel() {
        return myUiModel;
    }

    public void freeResources() {
        myDataModel.removeListener(this);
        if (myMaxPointer != null) {
            myMaxPointer.freeResources();
            myMaxPointer = null;
        }
        if (myMinPointer != null) {
            myMinPointer.freeResources();
            myMinPointer = null;
        }
        if (mySegmenter != null) {
            mySegmenter.freeResources();
            mySegmenter = null;
        }
        if (myUserRangePanner != null) {
            myUserRangePanner.freeResources();
            myUserRangePanner = null;
        }
        if (myLabelSupport != null){
        	myLabelSupport.freeResources();
        	myLabelSupport = null;
        }
    }
    
    public void dataModelChanged(BiSliderDataModel dataModel, boolean moreChangesExpectedInNearFuture) {
        // reload in any case to provide visual feedback immediately
        reloadChanges();
        
    }

    public void uiModelChanged(BiSliderUIModel uiModel) {
        reloadChanges();
    }

    private void reloadChanges() {
        if (!isDisposed()) {
            redraw();
        }
    }

    /**
     * intentionally package local
     */
    CoordinateMapper getCoordinateMapper() {
        return myMapper;
    }

    /**
     * @return <code>true</code> if given point is inside one of pointers.
     */
    boolean isInsidePointer(int pointX, int pointY) {
        return (myMaxPointer != null && myMaxPointer.isInsideArea(pointX, pointY)) || 
                (myMinPointer != null && myMinPointer.isInsideArea(pointX, pointY));
    }

    public BiSliderDataModel.Writable getWritableDataModel() {
        return myDataModel;
    }

    void paintBiSlider(GC gc) {
        Rectangle drawArea = getDrawArea(gc);
        myMapper.setContext(myDataModel, drawArea, getClientArea());
        myContents.paintContents(gc);
        myMinPointer.paintPointer(gc);
        myMaxPointer.paintPointer(gc);
        myOutline.paintOutline(gc);
    }

    private Rectangle getDrawArea(GC gc) {
        Rectangle clientArea = getClientArea();
        if (!clientArea.equals(myCachedClientArea)) {
            myCachedClientArea = Util.cloneRectangle(clientArea);
            myCachedDrawArea = computeDrawArea(myCachedClientArea, gc);
        }
        return myCachedDrawArea;
    }

    private Rectangle computeDrawArea(Rectangle clientArea, GC gc) {
        int x = clientArea.x;
        int y = clientArea.y;
        int width = clientArea.width;
        int height = clientArea.height;
        int labelInsets = getLabelInsets(gc);
        int nonLabelInsets = myUiModel.getNonLabelInsets();
        // allocate space for labels but only if there is enough space
        if (myUiModel.isVertical()) {
            if (myUiModel.hasLabelsAboveOrLeft() && width > labelInsets + MIN_NORMAL_SIZE) {
                width -= labelInsets;
                x += labelInsets;
            }
            if (myUiModel.hasLabelsBelowOrRight() && width > labelInsets + MIN_NORMAL_SIZE) {
                width -= labelInsets;
            }
            if (height > nonLabelInsets) {
                height -= nonLabelInsets;
                y += nonLabelInsets;
            }
            if (height > nonLabelInsets) {
                height -= nonLabelInsets;
            }
        } else {
            if (myUiModel.hasLabelsAboveOrLeft() && height > labelInsets + MIN_NORMAL_SIZE) {
                height -= labelInsets;
                y += labelInsets;
            }
            if (myUiModel.hasLabelsBelowOrRight() && height > labelInsets + MIN_NORMAL_SIZE) {
                height -= labelInsets;
            }
            if (width > nonLabelInsets) {
                width -= nonLabelInsets;
                x += nonLabelInsets;
            }
            if (width > nonLabelInsets) {
                width -= nonLabelInsets;
            }
        }
        return new Rectangle(x, y, width, height);
    }
    
    private int getLabelInsets(GC gc){
    	int result = myUiModel.getLabelInsets();
    	if (result == SWT.DEFAULT){
    		result = myLabelSupport.getPrefferedLabelInsets(gc);
    	}
    	return result;
    }
    
}
