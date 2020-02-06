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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

import com.borlander.rac353542.bislider.BiSliderDataModel;

class UserRangePanner implements DragSupport.DragListener, Disposable {
	protected class OutsidePointersUserSelectedArea implements AreaGate {
		@Override
		public boolean isInsideArea(final int x, final int y) {
			final CoordinateMapper mapper = myBiSlider.getCoordinateMapper();
			if (!mapper.getDrawArea().contains(x, y)) {
				return false;
			}

			final AreaGate minPointerArea = myMinPointer.getPointerAreaGate();
			final AreaGate maxPointerArea = myMaxPointer.getPointerAreaGate();

			if (minPointerArea != null && minPointerArea.isInsideArea(x, y)) {
				return false;
			}
			if (maxPointerArea != null && maxPointerArea.isInsideArea(x, y)) {
				return false;
			}

			final BiSliderDataModel dataModel = myBiSlider.getDataModel();
			final double value = mapper.pixel2value(x, y);
			return dataModel.getUserMinimum() <= value && value <= dataModel.getUserMaximum();
		}
	}

	final BiSliderImpl myBiSlider;
	final BiSliderPointer myMinPointer;
	final BiSliderPointer myMaxPointer;
	private final Point myCachedStartPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
	private final Point myCachedLastSeenAtPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
	private DragSupport myDragSupport;
	private final Segmenter mySegmenter;
	private double myLastRequestedMinimum;

	private double myLastRequestedMaximum;

	public UserRangePanner(final BiSliderImpl biSlider, final BiSliderPointer minPointer,
			final BiSliderPointer maxPointer, final Segmenter segmenter) {
		myBiSlider = biSlider;
		myMinPointer = minPointer;
		myMaxPointer = maxPointer;
		setShowValueLabels(true);
		mySegmenter = segmenter;
		myDragSupport = new DragSupport(myBiSlider, new OutsidePointersUserSelectedArea(), this);
	}

	private void cacheLastSeenAtPoint(final Point startPoint, final MouseEvent e, final double lastRequestedMinimum,
			final double lastRequestedMaximum) {
		myCachedStartPoint.x = startPoint.x;
		myCachedStartPoint.y = startPoint.y;
		myCachedLastSeenAtPoint.x = e.x;
		myCachedLastSeenAtPoint.y = e.y;
		myLastRequestedMinimum = lastRequestedMinimum;
		myLastRequestedMaximum = lastRequestedMaximum;
	}

	@Override
	public void dragFinished() {
		myBiSlider.getWritableDataModel().finishCompositeUpdate();
		myBiSlider.redraw();
	}

	@Override
	public void dragStarted() {
		myBiSlider.getWritableDataModel().startCompositeUpdate();
	}

	@Override
	public void freeResources() {
		setShowValueLabels(false);
		myBiSlider.redraw();
		if (myDragSupport != null) {
			myDragSupport.releaseControl();
			myDragSupport = null;
		}
	}

	private double getClosestSegmentEnd(final ColoredSegment segment, final double value) {
		final double segmentMax = segment.getMaxValue();
		final double segmentMin = segment.getMinValue();
		if (value >= (segmentMax + segmentMin) / 2) {
			return segmentMax;
		} else {
			return segmentMin;
		}
	}

	private double getLastRequestedMaximum(final Point startPoint) {
		return startPoint.equals(myCachedStartPoint) ? myLastRequestedMaximum
				: myBiSlider.getDataModel().getUserMaximum();
	}

	private double getLastRequestedMinimum(final Point startPoint) {
		return startPoint.equals(myCachedStartPoint) ? myLastRequestedMinimum
				: myBiSlider.getDataModel().getUserMinimum();
	}

	private Point getLastSeenAtPoint(final Point startPoint) {
		return startPoint.equals(myCachedStartPoint) ? myCachedLastSeenAtPoint : startPoint;
	}

	@Override
	public void mouseDragged(final MouseEvent e, final Point startPoint) {
		myBiSlider.redraw();

		final Point lastSeenAt = getLastSeenAtPoint(startPoint);
		if (lastSeenAt.x == e.x && lastSeenAt.y == e.y) {
			return;
		}

		final CoordinateMapper mapper = myBiSlider.getCoordinateMapper();
		final double lastAppliedValue = mapper.pixel2value(lastSeenAt);
		final double requestedValue = mapper.pixel2value(e.x, e.y);

		final BiSliderDataModel.Writable dataModel = myBiSlider.getWritableDataModel();
		final double currentSpan = dataModel.getUserDelta();
		final double requestedDelta = requestedValue - lastAppliedValue;
		double requestedMin = getLastRequestedMinimum(startPoint) + requestedDelta;
		double requestedMax = getLastRequestedMaximum(startPoint) + requestedDelta;

		cacheLastSeenAtPoint(startPoint, e, requestedMin, requestedMax);

		if ((e.stateMask & SWT.SHIFT) > 0) {
			final boolean dragToLeft = requestedDelta < 0;
			if (dragToLeft) {
				requestedMin = getClosestSegmentEnd(mySegmenter.getSegment(requestedMin), requestedMin);
				requestedMax = requestedMin + currentSpan;
			} else {
				requestedMax = getClosestSegmentEnd(mySegmenter.getSegment(requestedMax), requestedMax);
				requestedMin = requestedMax - currentSpan;
			}
		}

		final double totalMin = dataModel.getTotalMinimum();
		final double totalMax = dataModel.getTotalMaximum();

		if (requestedMin < totalMin) {
			requestedMin = totalMin;
			requestedMax = totalMin + currentSpan;
			cacheLastSeenAtPoint(startPoint, e, requestedMin, requestedMax);
		}

		if (requestedMax > totalMax) {
			requestedMax = totalMax;
			requestedMin = totalMax - currentSpan;
			cacheLastSeenAtPoint(startPoint, e, requestedMin, requestedMax);
		}

		dataModel.setUserRange(requestedMin, requestedMax);
	}

	public void setShowValueLabels(final boolean showValueLabels) {
		if (myMaxPointer != null) {
			myMaxPointer.setShowValueLabel(showValueLabels);
		}
		if (myMaxPointer != null) {
			myMinPointer.setShowValueLabel(showValueLabels);
		}
	}
}
