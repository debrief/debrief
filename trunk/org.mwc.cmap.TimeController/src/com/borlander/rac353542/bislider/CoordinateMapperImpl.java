package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class CoordinateMapperImpl implements CoordinateMapper {
    private final Point myTempPoint;
    private BiSliderDataModel myDataModel;
    private Rectangle myScreenBounds;
    private Axis myAxis;
    private int myMinPixel;
    private int myMaxPixel;

    public CoordinateMapperImpl(boolean isVertical) {
        myTempPoint = new Point(0, 0);
        setVertical(isVertical);
    }

    public void setVertical(boolean verticalNotHorizontal) {
        myAxis = Axis.getAxis(verticalNotHorizontal);
        updateMinMax();
    }
    
    public Axis getAxis() {
        return myAxis;
    }

    public void setContext(BiSliderDataModel dataModel, Rectangle screenBounds) {
        if (dataModel.equals(myDataModel) && screenBounds.equals(myScreenBounds)) {
            return;
        }
        myDataModel = dataModel;
        myScreenBounds = Util.cloneRectangle(screenBounds);
        updateMinMax();
    }
    
    public Rectangle getScreenBounds() {
        return myScreenBounds;
    }

    public double pixel2value(int pixelX, int pixelY) {
        myTempPoint.x = pixelX;
        myTempPoint.y = pixelY;
        return pixel2value(myTempPoint);
    }

    public double pixel2value(Point pixel) {
        checkRange();
        return myDataModel.getTotalMinimum() + myDataModel.getTotalDelta() * (myAxis.getAsDouble(pixel) - myMinPixel) / (myMaxPixel - myMinPixel);
    }

    public Point value2pixel(double value, boolean anchorAtMinimumEdge) {
        Point result = new Point(myScreenBounds.x, myScreenBounds.y);
        if (!anchorAtMinimumEdge) {
            myAxis.advanceNormal(result, myAxis.getNormalDelta(myScreenBounds));
        }
        myAxis.advance(result, value2delta(value));
        return result;
    }

    public Rectangle segment2rectangle(double min, double max, double normalRate, boolean shrink) {
        Point leftBottom = value2pixel(Math.min(min, max), false);
        Point rightBottom = value2pixel(Math.max(min, max), false);
        int normal = (int) Math.round(myAxis.getNormalDelta(myScreenBounds) * normalRate);
        if (shrink) {
            // Workaround for GC filling algorithm which otherwise extends the
            // borlders
            normal--;
        }
        return myAxis.createRectangle(leftBottom, myAxis.get(rightBottom) - myAxis.get(leftBottom), -normal);
    }

    public int value2delta(double value) {
        checkRange();
        if (value <= myDataModel.getTotalMinimum()) {
            return 0;
        }
        if (value >= myDataModel.getTotalMaximum()) {
            return myMaxPixel - myMinPixel;
        }
        double dataDelta = (value - myDataModel.getTotalMinimum()) / myDataModel.getTotalDelta();
        return (int) Math.ceil(dataDelta * (myMaxPixel - myMinPixel));
    }

    private void updateMinMax() {
        if (myScreenBounds != null) {
            myMinPixel = myAxis.getMin(myScreenBounds);
            myMaxPixel = myAxis.getMax(myScreenBounds);
        }
    }

    private void checkRange() {
        if (myMinPixel == myMaxPixel) {
            throw new IllegalStateException("I can not work with zero size");
        }
    }
}
