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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderUIModel;

class BiSliderOutline extends BiSliderComponentBase {
	private static final int LABEL_GAP_NORMAL = LabelSupport.LABEL_GAP_NORMAL;
	private ColorDescriptor myForeground;
	private final Point myTempPoint = new Point(0, 0);
	private BiSliderUIModel.Listener myConfigListener;
	private final float[] myTempTransformMatrix;
	private final LabelSupport myLabelSupport;

	public BiSliderOutline(final BiSliderImpl biSlider, final LabelSupport labelSupport) {
		super(biSlider);
		myLabelSupport = labelSupport;
		reloadConfig();
		myConfigListener = new BiSliderUIModel.Listener() {
			@Override
			public void uiModelChanged(final BiSliderUIModel uiModel) {
				reloadConfig();
			}
		};
		getUIModel().addListener(myConfigListener);
		myTempTransformMatrix = new float[6];
	}

	/**
	 * Draws the label if and only if it does not intersects with both left and
	 * rights constraint rectangles. On success updates the left rectangle position,
	 * so the next invocation will be checked against new, just drawn bounds.
	 * <p>
	 * NOTE: algorithm tries to avoid unnecessary object creation. Labels REQUIRES
	 * to be iterated from left to right.
	 *
	 * @return the left constraint
	 */
	private Rectangle drawLabelAndUpdateConstraints(final GC gc, final double rate, final double value,
			final boolean anchorAtMinEdge, Rectangle leftConstraint, final Rectangle rightConstraint) {
		final String label = getLabel(value);
		if (label == null) {
			return null;
		}
		final Point textSize = myLabelSupport.getTextSize(gc, label);
		final Point basePoint = getMapper().value2pixel(value, anchorAtMinEdge);
		final Point adjustment = getLabelAdjustmentX(anchorAtMinEdge, textSize, rate);
		final int adjustedX = basePoint.x + adjustment.x;
		final int adjustedY = basePoint.y + adjustment.y;
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

	private void drawLabels(final GC gc, final boolean anchorAtMinEdge) {
		final double segmentSize = getDataModel().getSegmentLength();
		final BiSliderDataModel dataModel = getDataModel();
		final double totalMin = dataModel.getTotalMinimum();
		final double totalMax = dataModel.getTotalMaximum();
		final double totalDelta = dataModel.getTotalDelta();
		// always draw the first and last tick
		// draw other ticks only if they are not overlapped
		final Font oldFont = gc.getFont();
		gc.setFont(myLabelSupport.getLabelFont(gc));
		final Rectangle previousLabelBounds = drawLabelAndUpdateConstraints(gc, 0.0, totalMin, anchorAtMinEdge, null,
				null);
		final Rectangle lastTextBounds = drawLabelAndUpdateConstraints(gc, 1.0, totalMax, anchorAtMinEdge,
				// WRONG! WILL CHANGE BOUNDS: previousLabelBounds
				null, null);

		for (double nextLabelValue = totalMin + segmentSize; nextLabelValue < totalMax; nextLabelValue += segmentSize) {
			final double nextLabelRate = (nextLabelValue - totalMin) / totalDelta;
			drawLabelAndUpdateConstraints(gc, nextLabelRate, nextLabelValue, anchorAtMinEdge, previousLabelBounds,
					lastTextBounds);
		}
		gc.setFont(oldFont);
	}

	private void drawText(final GC gc, final String text, final int x, final int y, final boolean anchorAtMinEdge) {
		if (getUIModel().isVerticalLabels()) {
			Transform myCachedOriginalTransform = new Transform(Display.getCurrent());
			Transform myCachedRotatedTransform = new Transform(Display.getCurrent());

			// do we need to ditch an old one?
			if (myCachedOriginalTransform != null)
				if (!myCachedOriginalTransform.isDisposed())
					myCachedOriginalTransform.dispose();

			myCachedOriginalTransform = new Transform(Display.getCurrent());
			gc.getTransform(myCachedOriginalTransform);

			myCachedRotatedTransform = setT2equalsToT1(myCachedRotatedTransform, myCachedOriginalTransform);
			myCachedRotatedTransform.translate(x, y);
			myCachedRotatedTransform.rotate(getRotationAngle(anchorAtMinEdge));
			gc.setTransform(myCachedRotatedTransform);
			gc.drawText(text, 0, 0, true);
			gc.setTransform(myCachedOriginalTransform);

			if (myCachedOriginalTransform != null) {
				safeDispose(myCachedOriginalTransform);
				myCachedOriginalTransform = null;
			}
			if (myCachedRotatedTransform != null) {
				safeDispose(myCachedRotatedTransform);
				myCachedRotatedTransform = null;
			}

		} else {
			gc.drawText(text, x, y, true);
		}
	}

	@Override
	public void freeResources() {
		if (myConfigListener != null) {
			getUIModel().removeListener(myConfigListener);
			myConfigListener = null;
		}
		if (myForeground != null) {
			myForeground.freeResources();
			myForeground = null;
		}
	}

	private Point getLabelAdjustmentX(final boolean atMinimumEdge, final Point textSize, final double valueRate) {
		final boolean verticalLabels = getUIModel().isVerticalLabels();
		if (getUIModel().isVertical()) {
			myTempPoint.x = atMinimumEdge ? -LABEL_GAP_NORMAL : LABEL_GAP_NORMAL;
			if (verticalLabels) {
				myTempPoint.x += atMinimumEdge ? -textSize.y : textSize.y;
			} else {
				myTempPoint.x += atMinimumEdge ? -textSize.x : 0;
			}
			if (verticalLabels) {
				myTempPoint.y = atMinimumEdge ? (int) (valueRate * textSize.x) : -(int) ((1 - valueRate) * textSize.x);
			} else {
				myTempPoint.y = textSize.y * 2 / 3;
			}
		} else {
			myTempPoint.y = atMinimumEdge ? -LABEL_GAP_NORMAL : LABEL_GAP_NORMAL;
			if (!verticalLabels && atMinimumEdge) {
				myTempPoint.y -= textSize.y;
			}
			if (verticalLabels) {
				final int leftOrRight = (atMinimumEdge) ? -1 : 1;
				myTempPoint.x = leftOrRight * textSize.x * 2 / 3;
			} else {
				myTempPoint.x = -(int) (valueRate * textSize.x);
			}
		}
		return myTempPoint;
	}

	private float getRotationAngle(final boolean anchorAtMinEdge) {
		return anchorAtMinEdge ? -90 : 90;
	}

	public void paintOutline(final GC gc) {
		final BiSliderUIModel uiModel = getUIModel();
		final int arcRadius = uiModel.getArcRadius();
		gc.setForeground(myForeground.getColor());
		final Rectangle drawArea = getDrawArea();
		gc.drawRoundRectangle(drawArea.x, drawArea.y, drawArea.width, drawArea.height, arcRadius, arcRadius);
		if (getUIModel().hasLabelsAboveOrLeft()) {
			drawLabels(gc, true);
		}
		if (getUIModel().hasLabelsBelowOrRight()) {
			drawLabels(gc, false);
		}
	}

	void reloadConfig() {
		myForeground = updateColorDescriptor(myForeground, getUIModel().getBiSliderForegroundRGB());
	}

	private void safeDispose(final Transform transform) {
		if (!transform.isDisposed()) {
			transform.dispose();
		}
	}

	private Transform setT2equalsToT1(final Transform t1, final Transform t2) {
		final float[] matrix = myTempTransformMatrix;
		t2.getElements(matrix);
		t1.setElements(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
		return t1;
	}
}
