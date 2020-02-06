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

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;

import com.borlander.rac353542.bislider.BiSliderDataModel;

/**
 * Selects a single segment on double click
 */
class SegmentSelector extends MouseAdapter {

	private final BiSliderImpl myBiSlider;
	private final Segmenter mySegmenter;

	public SegmentSelector(final BiSliderImpl biSlider, final Segmenter segmenter) {
		myBiSlider = biSlider;
		mySegmenter = segmenter;
	}

	@Override
	public void mouseDoubleClick(final MouseEvent e) {
		if (myBiSlider.isInsidePointer(e.x, e.y)) {
			return;
		}
		final CoordinateMapper mapper = myBiSlider.getCoordinateMapper();
		final Rectangle drawArea = mapper.getDrawArea();
		if (drawArea.contains(e.x, e.y)) {
			final double value = mapper.pixel2value(e.x, e.y);
			final ColoredSegment segment = mySegmenter.getSegment(value);
			if (segment != null) {
				final BiSliderDataModel.Writable dataModel = myBiSlider.getWritableDataModel();
				dataModel.setUserRange(segment.getMinValue(), segment.getMaxValue());
			}
		}
	}
}
