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

import org.eclipse.swt.widgets.Composite;

public abstract class BiSlider extends Composite {
	public BiSlider(final Composite parent, final int style) {
		super(parent, style);
	}

	public abstract BiSliderDataModel getDataModel();

	public abstract BiSliderUIModel getUIModel();

	public abstract BiSliderDataModel.Writable getWritableDataModel();

	public abstract void resetMinMaxPointers();

	public abstract void setShowLabels(boolean showLabels);

}
