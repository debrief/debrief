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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderLabelProvider;

public class FineTuneValueAdjuster {

	private static class AdjustmentControl extends Composite {

		private final BiSliderLabelProvider myLabelProvider;
		private final Scale2ValueConverter myConverter;
		private final boolean myIsVertical;
		private Scale myScale;
		private Label myLabel;

		public AdjustmentControl(final Composite parent, final boolean isVertical, final Scale2ValueConverter converter,
				final BiSliderLabelProvider labelProvider) {
			super(parent, SWT.BORDER);
			myLabelProvider = labelProvider;
			myIsVertical = isVertical;
			myConverter = converter;
			createContents();
			myScale.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					updateLabel();
				}

				@Override
				public void widgetSelected(final SelectionEvent e) {
					updateLabel();
				}
			});
			updateLabel();
		}

		private void createContents() {
			setLayout(new GridLayout(1, true));
			myScale = new Scale(this, myIsVertical ? SWT.VERTICAL : SWT.HORIZONTAL);
			myScale.setMinimum(0);
			myScale.setMaximum(myConverter.getTotalSteps());
			myScale.setIncrement(1);
			myScale.setPageIncrement(Math.max(1, myConverter.getTotalSteps() / 4));
			myScale.setSelection(myConverter.getStepForFixedValue());
			myScale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			myLabel = new Label(this, SWT.CENTER);
			myLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}

		public Point getPreferredSize() {
			return this.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		}

		public Scale getScale() {
			return myScale;
		}

		public double getSelectedValue() {
			return myConverter.scale2value(myScale.getSelection());
		}

		void updateLabel() {
			final int selectedScale = myScale.getSelection();
			final double selectedValue = myConverter.scale2value(selectedScale);
			String label = myLabelProvider.getLabel(selectedValue);
			if (label == null) {
				label = "";
			}
			myLabel.setText(label);
		}
	}

	private static class Scale2ValueConverter {

		private final double myFixedValue;
		private final int myStepForFixedValue;
		private final int myTotalSteps;
		private final double myStepIncrement;

		/**
		 * Creates converter which will map double range into [0, totalSteps] integer
		 * range suitable for Scale control. In the context of this mapping the given
		 * <code>fixedValue</code> should be mapped into <code>stepForFixedValue</code>.
		 */
		public Scale2ValueConverter(final double fixedValue, final int stepForFixedValue, final double totalDelta,
				final int maxStep) {
			myFixedValue = fixedValue;
			myStepForFixedValue = stepForFixedValue;
			myTotalSteps = maxStep;
			myStepIncrement = totalDelta / maxStep;
		}

		public int getStepForFixedValue() {
			return myStepForFixedValue;
		}

		public int getTotalSteps() {
			return myTotalSteps;
		}

		public double scale2value(final int step) {
			final int delta = step - myStepForFixedValue;
			return myFixedValue + delta * myStepIncrement;
		}
	}

	private static final int MAX_ADJUSTMENT_STEPS = 100;
	private static final int MIN_ADJUSTMENT_STEPS = 3;

	private static double ensureInRange(double value, final double min, final double max) {
		if (min > max) {
			throw new IllegalArgumentException("Requested min: " + min + ", requested max: " + max);
		}
		value = Math.min(value, max);
		value = Math.max(value, min);
		return value;
	}

	private final BiSliderImpl myBiSlider;

	private final BiSliderDataModel myDataModel;

	private final boolean myIsForMinimumPointer;

	private AdjustmentControl myAdjustmentControl;

	public FineTuneValueAdjuster(final BiSliderImpl biSlider, final boolean isForMinimumPointer) {
		myBiSlider = biSlider;
		myDataModel = biSlider.getDataModel();
		myIsForMinimumPointer = isForMinimumPointer;
	}

	private AdjustmentControl createAdjustmentComposite(final Composite parent, final Scale2ValueConverter converter) {
		return new AdjustmentControl(parent, myBiSlider.getUIModel().isVertical(), converter,
				myBiSlider.getUIModel().getLabelProvider());
	}

	private Scale2ValueConverter createScale2ValueConverter() {
		final CoordinateMapper mapper = myBiSlider.getCoordinateMapper();
		final double roughValue = getRoughValue();
		final double onePixelDelta = mapper.getOnePixelValueDelta(roughValue);
		// allow to adjust values for +/- 2 screen pixels
		final double scaleDelta = onePixelDelta * 2;
		final double safeMin = makeSafe(roughValue - scaleDelta);
		final double safeMax = makeSafe(roughValue + scaleDelta);
		final double precision = myDataModel.getPrecision();
		if (safeMax - safeMin <= precision * MIN_ADJUSTMENT_STEPS) {
			return null;
		}
		final int adjustmentSteps = (precision == 0) ? MAX_ADJUSTMENT_STEPS
				: Math.min(MAX_ADJUSTMENT_STEPS, (int) ((safeMax - safeMin) / precision));
		if (adjustmentSteps < MIN_ADJUSTMENT_STEPS) {
			return null;
		}
		final int stepForRoughValue = (int) Math.round(adjustmentSteps * (roughValue - safeMin) / (safeMax - safeMin));
		return new Scale2ValueConverter(roughValue, stepForRoughValue, safeMax - safeMin, adjustmentSteps);
	}

	public void disposeAdjustmentControl(final boolean acceptValue) {
		if (myAdjustmentControl != null && !myAdjustmentControl.isDisposed()) {
			if (acceptValue) {
				final double adjustedValue = myAdjustmentControl.getSelectedValue();
				if (myIsForMinimumPointer) {
					myBiSlider.getWritableDataModel().setUserMinimum(adjustedValue);
				} else {
					myBiSlider.getWritableDataModel().setUserMaximum(adjustedValue);
				}
			}
			myAdjustmentControl.dispose();
			myAdjustmentControl = null;
		}
	}

	private double getRoughValue() {
		return myIsForMinimumPointer ? myDataModel.getUserMinimum() : myDataModel.getUserMaximum();
	}

	private double makeSafe(final double someDouble) {
		return (myIsForMinimumPointer)
				? ensureInRange(someDouble, myDataModel.getTotalMinimum(), myDataModel.getUserMaximum())
				: ensureInRange(someDouble, myDataModel.getUserMinimum(), myDataModel.getTotalMaximum());
	}

	/**
	 * NOTE: Inside tis method, "width" and "X" are used to denote the size and
	 * value for <b>tangential</b> component of coordinate sets defined by axis, and
	 * "height" and "Y" used to denote the size and value for <b>normal</b>
	 * component. That is, if Bislider iyself is Vertical, than "width" means the
	 * screen height, and vise versa
	 */
	private void positionAdjustmentControl(final Control control) {
		/*
		 * NOTE: Inside tis method, "width" and "X" are used to denote the size and
		 * value for <b>tangential</b> component of coordinate sets defined by axis, and
		 * "height" and "Y" used to denote the size and value for <b>normal</b>
		 * component. That is, if Bislider iyself is Vertical, than "width" means the
		 * screen height, and vise versa.
		 */
		final Point prefSize = myAdjustmentControl.getPreferredSize();
		final CoordinateMapper coordinateMapper = myBiSlider.getCoordinateMapper();
		final Axis axis = coordinateMapper.getAxis();
		final Rectangle drawArea = coordinateMapper.getDrawArea();
		final boolean atMinumumEdge = !myIsForMinimumPointer;
		final Point basePoint = coordinateMapper.value2pixel(getRoughValue(), atMinumumEdge);

		int controlCenterX = axis.get(basePoint);
		final int controlWidth = axis.get(prefSize);
		final int drawAreaMinX = axis.getMin(drawArea);
		final int drawAreaMaxX = axis.getMax(drawArea);

		final int controlHalfWidth = controlWidth / 2;
		if (controlCenterX - controlHalfWidth < drawAreaMinX) {
			controlCenterX = drawAreaMinX + controlHalfWidth;
		}
		if (controlCenterX + controlHalfWidth > drawAreaMaxX) {
			controlCenterX = drawAreaMaxX - controlHalfWidth;
		}

		axis.set(basePoint, controlCenterX - controlHalfWidth);

		// nornal component
		final Rectangle fullControlBounds = coordinateMapper.getFullBounds();
		final int baseY = axis.getNormal(basePoint);
		final int controlHeight = axis.getNormal(prefSize);
		final int minVisibleY = axis.getNormalMin(fullControlBounds);
		final int maxVisibleY = axis.getNormalMax(fullControlBounds);
		if (atMinumumEdge) {
			int pointToSeeAtBottom = maxVisibleY - controlHeight;
			if (pointToSeeAtBottom < minVisibleY) {
				pointToSeeAtBottom = minVisibleY;
			}
			axis.setNormal(basePoint, Math.min(baseY + 10, pointToSeeAtBottom));
		} else {
			final int pointToSeeAtTop = minVisibleY;
			axis.setNormal(basePoint, Math.max(baseY - 10 - controlHeight, pointToSeeAtTop));
		}
		control.setBounds(basePoint.x, basePoint.y, prefSize.x, prefSize.y);
	}

	public void showAdjustmentControl() {
		if (myAdjustmentControl != null) {
			return;
		}
		final Scale2ValueConverter converter = createScale2ValueConverter();
		if (converter == null) {
			return;
		}
		myAdjustmentControl = createAdjustmentComposite(myBiSlider, converter);
		if (myAdjustmentControl == null) {
			return;
		}
		positionAdjustmentControl(myAdjustmentControl);
		myAdjustmentControl.setVisible(true);
		myAdjustmentControl.getScale().setFocus();
		myAdjustmentControl.getScale().addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				disposeAdjustmentControl(false);
			}
		});
		myAdjustmentControl.getScale().addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(final KeyEvent e) {
				//
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.character == SWT.ESC || e.character == SWT.CR) {
					disposeAdjustmentControl(e.character == SWT.CR);
				}
			}
		});
	}
}
