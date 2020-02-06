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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.mwc.cmap.TimeController.controls.DTGBiSlider.DoFineControl;

import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.impl.DragSupport.DragListener;

class BiSliderPointer extends BiSliderComponentBase implements DragListener, AreaGate {
	final boolean myMinNotMax;
	final PointerDrawer myDrawer;
	private final Segmenter mySegmenter;
	private final DragSupport myDragSupport;
	private boolean myShowValueLabel;
	private MouseListener myFineTunePopupShower;
	private final FineTuneValueAdjuster myFineTunePopup;

	public BiSliderPointer(final BiSliderImpl biSlider, final boolean minNotMax, final Segmenter segmenter,
			final DoFineControl handler) {
		super(biSlider);
		myMinNotMax = minNotMax;
		mySegmenter = segmenter;
		myDrawer = new DefaultSliderPointer(!minNotMax, !minNotMax);
		myDragSupport = new DragSupport(getBiSlider(), myDrawer.getAreaGate(), this);
		myFineTunePopup = new FineTuneValueAdjuster(getBiSlider(), myMinNotMax);
		myFineTunePopupShower = new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				if (myDrawer.getAreaGate().isInsideArea(e.x, e.y)) {
					// ok, is CTRL button down
					if ((e.stateMask & SWT.CTRL) != 0) {
						// show our fancy little window
						myFineTunePopup.showAdjustmentControl();
					} else {
						// put the time in the properties window
						handler.adjust(myMinNotMax);
					}
				}
			}
		};
		getBiSlider().addMouseListener(myFineTunePopupShower);
	}

	public void disposeFineTuneAdjuster(final boolean acceptValues) {
		myFineTunePopup.disposeAdjustmentControl(acceptValues);
	}

	@Override
	public void dragFinished() {
		getWritableDataModel().finishCompositeUpdate();
		getBiSlider().redraw();
	}

	@Override
	public void dragStarted() {
		getWritableDataModel().startCompositeUpdate();
	}

	@Override
	public void freeResources() {
		if (myFineTunePopupShower != null) {
			getBiSlider().removeMouseListener(myFineTunePopupShower);
			myFineTunePopupShower = null;
		}
		myDragSupport.releaseControl();
		myDrawer.freeResources();
	}

	private double getDataModelUserValue() {
		return myMinNotMax ? getDataModel().getUserMinimum() : getDataModel().getUserMaximum();
	}

	public AreaGate getPointerAreaGate() {
		return myDrawer == null ? null : myDrawer.getAreaGate();
	}

	public Point getPointerBasePoint() {
		return getMapper().value2pixel(getDataModelUserValue(), myMinNotMax);
	}

	private BiSliderDataModel.Writable getWritableDataModel() {
		return getBiSlider().getWritableDataModel();
	}

	@Override
	public boolean isInsideArea(final int x, final int y) {
		return myDrawer.getAreaGate().isInsideArea(x, y);
	}

	@Override
	public void mouseDragged(final MouseEvent e, final Point startPoint) {
		final CoordinateMapper mapper = getMapper();
		double currentValue = mapper.pixel2value(e.x, e.y);
		if ((e.stateMask & SWT.SHIFT) > 0) {
			final ColoredSegment segment = mySegmenter.getSegment(currentValue);
			final double startValue = mapper.pixel2value(startPoint.x, startPoint.y);
			if (startValue < currentValue) {
				currentValue = segment.getMaxValue();
			} else {
				currentValue = segment.getMinValue();
			}
		}
		setDataModelUserValue(currentValue);
	}

	public void paintPointer(final GC gc) {
		final double dataValue = getDataModelUserValue();
		final Point pointerAt = getPointerBasePoint();
		if (myShowValueLabel) {
			final String label = getLabel(dataValue);
			myDrawer.paintPointer(gc, pointerAt, label);
		} else {
			myDrawer.paintPointer(gc, pointerAt);
		}
	}

	public void reset() {
		if (!getBiSlider().isDisposed()) {
			getWritableDataModel().startCompositeUpdate();
			if (myMinNotMax) {
				setDataModelUserValue(getWritableDataModel().getTotalMinimum());
			} else {
				setDataModelUserValue(getWritableDataModel().getTotalMaximum());
			}
			getWritableDataModel().finishCompositeUpdate();
			getBiSlider().redraw();
		}
	}

	private double setDataModelUserValue(final double value) {
		if (myMinNotMax) {
			getWritableDataModel().setUserMinimum(value);
		} else {
			getWritableDataModel().setUserMaximum(value);
		}
		return getDataModelUserValue();
	}

	public void setShowValueLabel(final boolean showValueLabel) {
		myShowValueLabel = showValueLabel;
	}

}
