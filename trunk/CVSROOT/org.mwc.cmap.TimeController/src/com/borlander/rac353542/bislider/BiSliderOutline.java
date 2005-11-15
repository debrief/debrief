package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class BiSliderOutline extends BiSliderComponentBase {

    private static final int LABEL_GAP_NORMAL = 5;
    private ColorDescriptor myForeground;
    private Font myBoldFont;
    private final Point myTempPoint = new Point(0, 0);

    public BiSliderOutline(BiSlider biSlider) {
        super(biSlider);
    }

    public void paintOutline(GC gc) {
        BiSliderUIModel uiModel = getUIModel();
        int arcRadius = uiModel.getArcRadius();
        gc.setForeground(getForeground());
        Rectangle drawArea = getDrawArea();
        gc.drawRoundRectangle(drawArea.x, drawArea.y, drawArea.width, drawArea.height, arcRadius, arcRadius);
        if (getUIModel().hasLabelsAboveOrLeft()) {
            drawLabels(gc, false);
        }
        if (getUIModel().hasLabelsBelowOrRight()) {
            drawLabels(gc, true);
        }
    }

    private void drawLabels(GC gc, boolean anchorAtMinEdge) {
        int ticksCount = getDataModel().getSegmentsCount();
        if (ticksCount < 2) {
            // at least should be start and first label
            return;
        }
        BiSliderDataModel dataModel = getDataModel();
        double totalMin = dataModel.getTotalMinimum();
        double totalMax = dataModel.getTotalMaximum();
        double totalDelta = dataModel.getTotalDelta();
        // always draw the first and last tick
        // draw other ticks only if they are not overlapped
        Font oldFont = gc.getFont();
        gc.setFont(getLabelFont(gc));
        Rectangle previousLabelBounds = drawLabelAndUpdateConstraints(gc, 0.0, totalMin, anchorAtMinEdge, null, null);
        Rectangle lastTextBounds = drawLabelAndUpdateConstraints(gc, 1.0, totalMax, anchorAtMinEdge,
                // WRONG! WILL CHANGE BOUNDS: previousLabelBounds
                null, null);
        
        for (int i = 1; i < ticksCount; i++) {
            double nextLabelRate = ((double) i) / ticksCount;
            double nextLabelValue = totalMin + totalDelta * nextLabelRate;
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
        Point textSize = gc.stringExtent(label);
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
        gc.drawText(label, adjustedX, adjustedY, true);
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
        if (getUIModel().isVertical()) {
            if (atMinimumEdge) {
                myTempPoint.x = -textSize.x - LABEL_GAP_NORMAL;
            } else {
                myTempPoint.x = LABEL_GAP_NORMAL;
            }
            // 0 at the minimum side, 1 at the maximum size
            myTempPoint.y = -(int) (valueRate * textSize.y);
        } else {
            if (atMinimumEdge) {
                myTempPoint.y = -textSize.y - LABEL_GAP_NORMAL;
            } else {
                myTempPoint.y = LABEL_GAP_NORMAL;
            }
            myTempPoint.x = -(int) (valueRate * textSize.x);
        }
        return myTempPoint;
    }

    private Color getForeground() {
        if (myForeground == null) {
            myForeground = new ColorDescriptor(getUIModel().getBiSliderForegroundRGB());
        }
        return myForeground.getColor();
    }

    private Font getLabelFont(GC gc) {
        if (myBoldFont == null) {
            myBoldFont = Util.deriveBold(gc.getFont());
        }
        return myBoldFont;
    }

    public void freeResources() {
        if (myForeground != null) {
            myForeground.freeResources();
            myForeground = null;
        }
        if (myBoldFont != null) {
            myBoldFont.dispose();
            myBoldFont = null;
        }
    }
}
