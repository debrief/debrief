/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.borlander.rac353542.bislider.BiSliderDataModel;

class CoordinateMapperImpl implements CoordinateMapper {
	private final Point myTempPoint;
	private BiSliderDataModel myDataModel;
	private Rectangle myDrawArea;
	private Rectangle myFullBounds;
	private Axis myAxis;
	private int myMinPixel;
	private int myMaxPixel;

	public CoordinateMapperImpl(final boolean isVertical) {
		myTempPoint = new Point(0, 0);
		setVertical(isVertical);
	}

	private void checkRange() {
		if (myMinPixel == myMaxPixel) {
			throw new IllegalStateException("I can not work with zero size");
		}
	}

	@Override
	public Axis getAxis() {
		return myAxis;
	}

	@Override
	public Rectangle getDrawArea() {
		return myDrawArea;
	}

	@Override
	public Rectangle getFullBounds() {
		return myFullBounds;
	}

	@Override
	public double getOnePixelValueDelta(final double value) {
		return myDataModel.getTotalDelta() / (myMaxPixel - myMinPixel);
	}

	@Override
	public double pixel2value(final int pixelX, final int pixelY) {
		myTempPoint.x = pixelX;
		myTempPoint.y = pixelY;
		return pixel2value(myTempPoint);
	}

	@Override
	public double pixel2value(final Point pixel) {
		checkRange();
		return myDataModel.getTotalMinimum()
				+ myDataModel.getTotalDelta() * (myAxis.getAsDouble(pixel) - myMinPixel) / (myMaxPixel - myMinPixel);
	}

	@Override
	public Rectangle segment2rectangle(final double min, final double max, final double normalRate,
			final boolean shrink) {
		final Point leftBottom = value2pixel(Math.min(min, max), false);
		final Point rightBottom = value2pixel(Math.max(min, max), false);
		int normal = (int) Math.round(myAxis.getNormalDelta(myDrawArea) * normalRate);
		if (shrink) {
			// Workaround for GC filling algorithm which otherwise extends the
			// borlders
			normal--;
		}
		return myAxis.createRectangle(leftBottom, myAxis.get(rightBottom) - myAxis.get(leftBottom), -normal);
	}

	@Override
	public void setContext(final BiSliderDataModel dataModel, final Rectangle drawArea, final Rectangle fullBounds) {
		if (dataModel.equals(myDataModel) && drawArea.equals(myDrawArea)) {
			return;
		}
		myDataModel = dataModel;
		myDrawArea = Util.cloneRectangle(drawArea);
		myFullBounds = Util.cloneRectangle(fullBounds);
		updateMinMax();
	}

	public void setVertical(final boolean verticalNotHorizontal) {
		myAxis = Axis.getAxis(verticalNotHorizontal);
		updateMinMax();
	}

	private void updateMinMax() {
		if (myDrawArea != null) {
			myMinPixel = myAxis.getMin(myDrawArea);
			myMaxPixel = myAxis.getMax(myDrawArea);
		}
	}

	public int value2delta(final double value) {
		checkRange();
		if (value <= myDataModel.getTotalMinimum()) {
			return 0;
		}
		if (value >= myDataModel.getTotalMaximum()) {
			return myMaxPixel - myMinPixel;
		}
		final double dataDelta = (value - myDataModel.getTotalMinimum()) / myDataModel.getTotalDelta();
		return (int) Math.ceil(dataDelta * (myMaxPixel - myMinPixel));
	}

	@Override
	public Point value2pixel(final double value, final boolean anchorAtMinimumEdge) {
		final Point result = new Point(myDrawArea.x, myDrawArea.y);
		if (!anchorAtMinimumEdge) {
			myAxis.advanceNormal(result, myAxis.getNormalDelta(myDrawArea));
		}
		myAxis.advance(result, value2delta(value));
		return result;
	}
}
