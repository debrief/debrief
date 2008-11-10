package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import com.borlander.rac353542.bislider.*;

class BiSliderOutline extends BiSliderComponentBase {
    private static final int LABEL_GAP_NORMAL = LabelSupport.LABEL_GAP_NORMAL;
    private ColorDescriptor myForeground;
    private final Point myTempPoint = new Point(0, 0);
    private BiSliderUIModel.Listener myConfigListener;
    private Transform myCachedOriginalTransform;
    private Transform myCachedRotatedTransform;
    private final float[] myTempTransformMatrix;
	private final LabelSupport myLabelSupport;

    public BiSliderOutline(BiSliderImpl biSlider, LabelSupport labelSupport) {
        super(biSlider);
		myLabelSupport = labelSupport;
        reloadConfig();
        myConfigListener = new BiSliderUIModel.Listener(){
            public void uiModelChanged(BiSliderUIModel uiModel) {
                reloadConfig();
            }
        };
        getUIModel().addListener(myConfigListener);
        myCachedOriginalTransform = new Transform(Display.getCurrent());
        myCachedRotatedTransform = new Transform(Display.getCurrent());
        myTempTransformMatrix = new float[6];
    }
    
    private void reloadConfig(){
        myForeground = updateColorDescriptor(myForeground, getUIModel().getBiSliderForegroundRGB());
    }

    public void paintOutline(GC gc) {
        BiSliderUIModel uiModel = getUIModel();
        int arcRadius = uiModel.getArcRadius();
        gc.setForeground(myForeground.getColor());
        Rectangle drawArea = getDrawArea();
        gc.drawRoundRectangle(drawArea.x, drawArea.y, drawArea.width, drawArea.height, arcRadius, arcRadius);
        if (getUIModel().hasLabelsAboveOrLeft()) {
            drawLabels(gc, true);
        }
        if (getUIModel().hasLabelsBelowOrRight()) {
            drawLabels(gc, false);
        }
    }

    private void drawLabels(GC gc, boolean anchorAtMinEdge) {
    	double segmentSize = getDataModel().getSegmentLength();
        BiSliderDataModel dataModel = getDataModel();
        double totalMin = dataModel.getTotalMinimum();
        double totalMax = dataModel.getTotalMaximum();
        double totalDelta = dataModel.getTotalDelta();
        // always draw the first and last tick
        // draw other ticks only if they are not overlapped
        Font oldFont = gc.getFont();
        gc.setFont(myLabelSupport.getLabelFont(gc));
        Rectangle previousLabelBounds = drawLabelAndUpdateConstraints(gc, 0.0, totalMin, anchorAtMinEdge, null, null);
        Rectangle lastTextBounds = drawLabelAndUpdateConstraints(gc, 1.0, totalMax, anchorAtMinEdge,
                // WRONG! WILL CHANGE BOUNDS: previousLabelBounds
                null, null);
        
        for (double nextLabelValue = totalMin + segmentSize; nextLabelValue < totalMax; nextLabelValue += segmentSize){
            double nextLabelRate = (nextLabelValue - totalMin) / totalDelta;
            drawLabelAndUpdateConstraints(gc, nextLabelRate, nextLabelValue, anchorAtMinEdge, previousLabelBounds, lastTextBounds);
        }
        gc.setFont(oldFont);
    }

    /**
     * Draws the label if and only if it does not intersects with both left and
     * rights constraint rectangles. On success updates the left rectangle
     * position, so the next invocation will be checked against new, just drawn
     * bounds.
     * <p>
     * NOTE: algorithm tries to avoid unnecessary object creation. Labels
     * REQUIRES to be iterated from left to right.
     * 
     * @return the left constraint
     */
    private Rectangle drawLabelAndUpdateConstraints(GC gc, double rate, double value, boolean anchorAtMinEdge, Rectangle leftConstraint, Rectangle rightConstraint) {
        String label = getLabel(value);
        if (label == null) {
            return null;
        }
        Point textSize = myLabelSupport.getTextSize(gc, label);
        Point basePoint = getMapper().value2pixel(value, anchorAtMinEdge);
        Point adjustment = getLabelAdjustmentX(anchorAtMinEdge, textSize, rate);
        int adjustedX = basePoint.x + adjustment.x;
        int adjustedY = basePoint.y + adjustment.y;
        if (leftConstraint != null && leftConstraint.intersects(adjustedX, adjustedY, textSize.x, textSize.y)) {
            return null;
        }
        if (rightConstraint != null && rightConstraint.intersects(adjustedX, adjustedY, textSize.x, textSize.y)) {
            return null;
        }
        
        drawText(gc, label, adjustedX, adjustedY, anchorAtMinEdge);
        
        if (leftConstraint == null) {
            leftConstraint = new Rectangle(0, 0, 0, 0);
        }
        leftConstraint.x = adjustedX;
        leftConstraint.y = adjustedY;
        leftConstraint.width = textSize.x + 5;
        leftConstraint.height = textSize.y + 5;
        return leftConstraint;
    }

    private Point getLabelAdjustmentX(boolean atMinimumEdge, Point textSize, double valueRate) {
        boolean verticalLabels = getUIModel().isVerticalLabels();
        if (getUIModel().isVertical()) {
            myTempPoint.x = atMinimumEdge ? - LABEL_GAP_NORMAL : LABEL_GAP_NORMAL; 
            if (verticalLabels) {
                myTempPoint.x += atMinimumEdge ? -textSize.y : textSize.y;  
            } else {
                myTempPoint.x += atMinimumEdge ? -textSize.x : 0;
            }
            if (verticalLabels ){
                myTempPoint.y = atMinimumEdge ? (int)(valueRate * textSize.x) : -(int)((1 - valueRate) * textSize.x);
            } else {
                myTempPoint.y = textSize.y * 2 / 3;
            }
        } else {
            myTempPoint.y = atMinimumEdge ? -LABEL_GAP_NORMAL : LABEL_GAP_NORMAL;
            if (!verticalLabels && atMinimumEdge){
                myTempPoint.y -= textSize.y;
            }
            if (verticalLabels){
                int leftOrRight = (atMinimumEdge) ? -1 : 1;
                myTempPoint.x = leftOrRight * textSize.x * 2 / 3;
            } else {
                myTempPoint.x = -(int) (valueRate * textSize.x);
            }
        }
        return myTempPoint;
    }
    
    private void drawText(GC gc, String text, int x, int y, boolean anchorAtMinEdge){
        if (getUIModel().isVerticalLabels()){
            /**/myCachedOriginalTransform = new Transform(Display.getCurrent());
            gc.getTransform(myCachedOriginalTransform);
            myCachedRotatedTransform = setT2equalsToT1(myCachedRotatedTransform, myCachedOriginalTransform);
            myCachedRotatedTransform.translate(x, y);
            myCachedRotatedTransform.rotate(getRotationAngle(anchorAtMinEdge));
            gc.setTransform(myCachedRotatedTransform);
            gc.drawText(text, 0, 0, true);
            gc.setTransform(myCachedOriginalTransform);
        } else {
            gc.drawText(text, x, y, true);
        }
    }
    
    private float getRotationAngle(boolean anchorAtMinEdge) {
        return anchorAtMinEdge ? -90 : 90;
    }

    public void freeResources() {
        if (myConfigListener != null){
            getUIModel().removeListener(myConfigListener);
            myConfigListener = null;
        }
        if (myForeground != null) {
            myForeground.freeResources();
            myForeground = null;
        }
        if (myCachedOriginalTransform != null){
            safeDispose(myCachedOriginalTransform);
            myCachedOriginalTransform = null;
        }
        if (myCachedRotatedTransform != null){
            safeDispose(myCachedRotatedTransform);
            myCachedRotatedTransform = null;
        }
    }
    
    private void safeDispose(Transform transform){
        if (!transform.isDisposed()){
            transform.dispose();
        }
    }
    
    private Transform setT2equalsToT1(Transform t1, Transform t2){
        float[] matrix = myTempTransformMatrix;
        t2.getElements(matrix);
        t1.setElements(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
        return t1;
    }
}
