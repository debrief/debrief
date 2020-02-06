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

package com.borlander.rac353542.bislider;

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class DefaultBiSliderUIModel implements BiSliderUIModel {
	private static final RGB DEFAULT_FOREGROUND = new RGB(0, 0, 0);
	private static final RGB DEFAULT_MIN_RGB = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)
			.getRGB();
	private static final RGB DEFAULT_MAX_RGB = DEFAULT_MIN_RGB;
	private static final RGB DEFAULT_BLANK_RGB = new RGB(255, 255, 255);
	private static final int DEFAULT_LABEL_INSETS = 40;
	private static final int DEFAULT_NON_LABEL_INSETS = 20;

	private final LinkedList<Listener> myListeners = new LinkedList<Listener>();
	private Listener[] myListenersArray;
	private int myArcRadius;
	private boolean myIsVertical;
	private boolean myIsVerticalLabels;
	private int myNonLabelInsets;
	private int myLabelInsets;
	private BiSliderContentsDataProvider myContentsDataProvider;
	private boolean myHasLabelsAboveOrLeft;
	private boolean myHasLabelsBelowOrRight;
	private RGB myForegroundRGB;

	private ColorInterpolation myColorInterpolation;
	private BiSliderLabelProvider myLabelProvider;
	private RGB myMaximumRGB;
	private RGB myMinimumRGB;
	private RGB myNotColoredSegmentRGB;

	public DefaultBiSliderUIModel() {
		setHasLabelsAboveOrLeft(true);
		setContentsDataProvider(BiSliderContentsDataProvider.FILL);
		setColorInterpolation(new ColorInterpolation.INTERPOLATE_HSB());
		setLabelProvider(BiSliderLabelProvider.TO_STRING);

		setBiSliderForegroundRGB(null);
		setMaximumRGB(null);
		setMinimumRGB(null);
		setNotColoredSegmentRGB(null);

		setLabelInsets(DEFAULT_LABEL_INSETS);
		setNonLabelInsets(DEFAULT_NON_LABEL_INSETS);
	}

	@Override
	public void addListener(final Listener listener) {
		if (listener != null) {
			myListeners.add(listener);
		}
	}

	/**
	 * Creates separate copy of listeners. It allows listeners to be unregistered
	 * during notification.
	 * <p>
	 * An array instance is cached to avoid unnecessary creation.
	 */
	private Listener[] copyListeners() {
		if (myListenersArray == null) {
			myListenersArray = new Listener[myListeners.size()];
		}
		myListenersArray = myListeners.toArray(myListenersArray);
		return myListenersArray;
	}

	protected void fireChanged() {
		if (!myListeners.isEmpty()) {
			final Listener[] listenersCopy = copyListeners();
			for (int i = 0; i < listenersCopy.length; i++) {
				final Listener next = listenersCopy[i];
				if (next == null) {
					break;
				}
				next.uiModelChanged(this);
			}
		}
	}

	@Override
	public int getArcRadius() {
		return myArcRadius;
	}

	@Override
	public RGB getBiSliderForegroundRGB() {
		return myForegroundRGB;
	}

	@Override
	public ColorInterpolation getColorInterpolation() {
		return myColorInterpolation;
	}

	@Override
	public BiSliderContentsDataProvider getContentsDataProvider() {
		return myContentsDataProvider;
	}

	@Override
	public int getLabelInsets() {
		return myLabelInsets;
	}

	@Override
	public BiSliderLabelProvider getLabelProvider() {
		return myLabelProvider;
	}

	@Override
	public RGB getMaximumRGB() {
		return myMaximumRGB;
	}

	@Override
	public RGB getMinimumRGB() {
		return myMinimumRGB;
	}

	@Override
	public int getNonLabelInsets() {
		return myNonLabelInsets;
	}

	@Override
	public RGB getNotColoredSegmentRGB() {
		return myNotColoredSegmentRGB;
	}

	@Override
	public boolean hasLabelsAboveOrLeft() {
		return myHasLabelsAboveOrLeft;
	}

	@Override
	public boolean hasLabelsBelowOrRight() {
		return myHasLabelsBelowOrRight;
	}

	@Override
	public boolean isVertical() {
		return myIsVertical;
	}

	@Override
	public boolean isVerticalLabels() {
		return myIsVerticalLabels;
	}

	@Override
	public void removeListener(final Listener listener) {
		if (listener != null) {
			myListeners.add(listener);
		}
	}

	public void setArcRadius(final int arcRadius) {
		if (myArcRadius != arcRadius) {
			myArcRadius = arcRadius;
			fireChanged();
		}
	}

	public void setBiSliderForegroundRGB(RGB foregroundRGB) {
		if (foregroundRGB == null) {
			foregroundRGB = DEFAULT_FOREGROUND;
		}
		if (!foregroundRGB.equals(myForegroundRGB)) {
			myForegroundRGB = foregroundRGB;
			fireChanged();
		}
	}

	public void setColorInterpolation(ColorInterpolation colorInterpolation) {
		if (colorInterpolation == null) {
			colorInterpolation = new ColorInterpolation.INTERPOLATE_RGB();
		}
		if (!colorInterpolation.isSameInterpolationMode(myColorInterpolation)) {
			myColorInterpolation = colorInterpolation;
			fireChanged();
		}
	}

	public void setContentsDataProvider(BiSliderContentsDataProvider contentsDataProvider) {
		if (contentsDataProvider == null) {
			contentsDataProvider = BiSliderContentsDataProvider.FILL;
		}
		if (!contentsDataProvider.equals(myContentsDataProvider)) {
			myContentsDataProvider = contentsDataProvider;
			fireChanged();
		}
	}

	public void setHasLabelsAboveOrLeft(final boolean hasLabelsAboveOrLeft) {
		if (myHasLabelsAboveOrLeft != hasLabelsAboveOrLeft) {
			myHasLabelsAboveOrLeft = hasLabelsAboveOrLeft;
			fireChanged();
		}
	}

	public void setHasLabelsBelowOrRight(final boolean hasLabelsBelowOrRight) {
		if (myHasLabelsBelowOrRight != hasLabelsBelowOrRight) {
			myHasLabelsBelowOrRight = hasLabelsBelowOrRight;
			fireChanged();
		}
	}

	public void setLabelInsets(int labelInsets) {
		labelInsets = Math.max(labelInsets, SWT.DEFAULT);
		if (myLabelInsets != labelInsets) {
			myLabelInsets = labelInsets;
			fireChanged();
		}
	}

	public void setLabelProvider(BiSliderLabelProvider labelProvider) {
		if (labelProvider == null) {
			labelProvider = BiSliderLabelProvider.DUMMY;
		}
		if (!labelProvider.equals(myLabelProvider)) {
			myLabelProvider = labelProvider;
			fireChanged();
		}
	}

	public void setMaximumRGB(RGB maximumRGB) {
		if (maximumRGB == null) {
			maximumRGB = DEFAULT_MAX_RGB;
		}
		if (!maximumRGB.equals(myMaximumRGB)) {
			myMaximumRGB = maximumRGB;
			fireChanged();
		}
	}

	public void setMinimumRGB(RGB minimumRGB) {
		if (minimumRGB == null) {
			minimumRGB = DEFAULT_MIN_RGB;
		}
		if (!minimumRGB.equals(myMinimumRGB)) {
			myMinimumRGB = minimumRGB;
			fireChanged();
		}
	}

	public void setNonLabelInsets(int nonLabelInsets) {
		nonLabelInsets = Math.max(nonLabelInsets, 0);
		if (myNonLabelInsets != nonLabelInsets) {
			myNonLabelInsets = nonLabelInsets;
			fireChanged();
		}
	}

	public void setNotColoredSegmentRGB(RGB notColoredSegmentRGB) {
		if (notColoredSegmentRGB == null) {
			notColoredSegmentRGB = DEFAULT_BLANK_RGB;
		}
		if (!notColoredSegmentRGB.equals(myNotColoredSegmentRGB)) {
			myNotColoredSegmentRGB = notColoredSegmentRGB;
			fireChanged();
		}
	}

	public void setVertical(final boolean isVertical) {
		if (myIsVertical != isVertical) {
			myIsVertical = isVertical;
			fireChanged();
		}
	}

	public void setVerticalLabels(final boolean isVerticalLabels) {
		if (myIsVerticalLabels != isVerticalLabels) {
			myIsVerticalLabels = isVerticalLabels;
			fireChanged();
		}
	}
}
