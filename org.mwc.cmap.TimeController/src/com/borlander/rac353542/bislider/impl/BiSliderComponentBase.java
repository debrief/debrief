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

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.borlander.rac353542.bislider.BiSliderDataModel;
import com.borlander.rac353542.bislider.BiSliderLabelProvider;
import com.borlander.rac353542.bislider.BiSliderUIModel;

abstract class BiSliderComponentBase implements Disposable {
	private final BiSliderImpl myBiSlider;

	protected BiSliderComponentBase(final BiSliderImpl biSlider) {
		myBiSlider = biSlider;
		myBiSlider.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				freeResources();
			}
		});
	}

	@Override
	public abstract void freeResources();

	protected final BiSliderImpl getBiSlider() {
		return myBiSlider;
	}

	protected final BiSliderDataModel getDataModel() {
		return myBiSlider.getDataModel();
	}

	protected final Rectangle getDrawArea() {
		return getMapper().getDrawArea();
	}

	protected String getLabel(final double value) {
		String result = getLabelProvider().getLabel(value);
		if (result != null) {
			result = result.trim();
			if (result.length() == 0) {
				result = null;
			}
		}
		return result;
	}

	private BiSliderLabelProvider getLabelProvider() {
		return getUIModel().getLabelProvider();
	}

	protected final CoordinateMapper getMapper() {
		return myBiSlider.getCoordinateMapper();
	}

	protected final BiSliderUIModel getUIModel() {
		return myBiSlider.getUIModel();
	}

	protected final ColorDescriptor updateColorDescriptor(ColorDescriptor oldDescriptor, final RGB newRGB) {
		if (oldDescriptor == null || !oldDescriptor.getRGB().equals(newRGB)) {
			if (oldDescriptor != null) {
				oldDescriptor.freeResources();
			}
			oldDescriptor = new ColorDescriptor(newRGB);
		}
		return oldDescriptor;
	}

}
