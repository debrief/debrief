package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import com.borlander.rac353542.bislider.BiSlider;
import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderUIModel;
import com.borlander.rac353542.bislider.DefaultBiSliderDataModel;
import com.borlander.rac353542.bislider.DefaultBiSliderUIModel;

/**
 * Actual BiLsider implementation.
 * <p>
 * Intentionally package local
 */
class BiSliderImpl extends BiSlider implements Disposable, BiSliderDataModel.Listener, BiSliderUIModel.Listener {
    private final BiSliderDataModel.Writable myDataModel;
    private final BiSliderUIModel myUiModel;
    private final CoordinateMapper myMapper;
    private BiSliderPointer myMinPointer;
    private BiSliderPointer myMaxPointer;
    private BiSliderContents myContents;
    private BiSliderOutline myOutline;
    private Rectangle myCachedClientArea;
    private Rectangle myCachedDrawArea;

    public BiSliderImpl(Composite parent, int style) {
        this(parent, style, new DefaultBiSliderDataModel(), new DefaultBiSliderUIModel());
    }

    public BiSliderImpl(Composite parent, int style, BiSliderDataModel.Writable dataModel, BiSliderUIModel uiModel) {
        super(parent, style | SWT.DOUBLE_BUFFERED);
        setFont(parent.getFont());
        myDataModel = dataModel;
        myUiModel = uiModel;
        myMapper = new CoordinateMapperImpl(myUiModel.isVertical());
        myMaxPointer = new BiSliderPointer(this, true);
        myMinPointer = new BiSliderPointer(this, false);
        myContents = new BiSliderContents(this);
        myOutline = new BiSliderOutline(this);
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
    }

    public void dataModelChanged(BiSliderDataModel dataModel) {
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

    public BiSliderDataModel.Writable getWritableDataModel() {
        return myDataModel;
    }

    private void paintBiSlider(GC gc) {
        Rectangle drawArea = getDrawArea();
        myMapper.setContext(myDataModel, drawArea);
        myContents.paintContents(gc);
        myMinPointer.paintPointer(gc);
        myMaxPointer.paintPointer(gc);
        myOutline.paintOutline(gc);
    }

    private Rectangle getDrawArea() {
        Rectangle clientArea = getClientArea();
        if (!clientArea.equals(myCachedClientArea)) {
            myCachedClientArea = Util.cloneRectangle(clientArea);
            myCachedDrawArea = computeDrawArea(myCachedClientArea);
        }
        return myCachedDrawArea;
    }

    private Rectangle computeDrawArea(Rectangle clientArea) {
        int x = clientArea.x;
        int y = clientArea.y;
        int width = clientArea.width;
        int height = clientArea.height;
        int labelInsets = myUiModel.getLabelInsets();
        int nonLabelInsets = myUiModel.getNonLabelInsets();
        
        // allocate space for labels but only if there is enough space
        if (myUiModel.isVertical()) {
            if (myUiModel.hasLabelsAboveOrLeft() && width > labelInsets) {
                width -= labelInsets;
                x += labelInsets;
            }
            if (myUiModel.hasLabelsBelowOrRight() && width > labelInsets) {
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
            if (myUiModel.hasLabelsAboveOrLeft() && height > labelInsets) {
                height -= labelInsets;
                y += labelInsets;
            }
            if (myUiModel.hasLabelsBelowOrRight() && height > labelInsets) {
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
}
