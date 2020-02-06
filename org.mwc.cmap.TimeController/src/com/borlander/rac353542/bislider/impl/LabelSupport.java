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

import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderLabelProvider;
import com.borlander.rac353542.bislider.BiSliderUIModel;

public class LabelSupport {
	public static final int LABEL_GAP_NORMAL = 5;
	private final BiSliderImpl myBiSlider;
	private Font myBoldFont;

	public LabelSupport(final BiSliderImpl biSlider) {
		myBiSlider = biSlider;
	}

	public void freeResources() {
		if (myBoldFont != null) {
			myBoldFont.dispose();
			myBoldFont = null;
		}
	}

	private BiSliderDataModel getDataModel() {
		return myBiSlider.getDataModel();
	}

	public Font getLabelFont(final GC gc) {
		if (myBoldFont == null) {
			myBoldFont = Util.deriveBold(gc.getFont());
		}
		return myBoldFont;
	}

	public int getPrefferedLabelInsets(final GC gc) {
		final BiSliderLabelProvider labelProvider = getUIModel().getLabelProvider();
		final String minLabel = labelProvider.getLabel(getDataModel().getTotalMinimum());
		final String maxLabel = labelProvider.getLabel(getDataModel().getTotalMaximum());

		final Point minLabelSize = getTextSize(gc, minLabel);
		final Point maxLabelSize = getTextSize(gc, maxLabel);

		int size;
		if (getUIModel().isVertical()) {
			size = Math.max(minLabelSize.x, maxLabelSize.x);
		} else {
			size = Math.max(minLabelSize.y, maxLabelSize.y);
		}
		return size + LABEL_GAP_NORMAL;
	}

	public Point getTextSize(final GC gc, final String label) {
		final Font oldFont = gc.getFont();
		gc.setFont(getLabelFont(gc));
		final Point result = gc.stringExtent(label);
		if (getUIModel().isVerticalLabels()) {
			final int temp = result.x;
			result.x = result.y;
			result.y = temp;
		}
		gc.setFont(oldFont);
		return result;
	}

	private BiSliderUIModel getUIModel() {
		return myBiSlider.getUIModel();
	}

}
