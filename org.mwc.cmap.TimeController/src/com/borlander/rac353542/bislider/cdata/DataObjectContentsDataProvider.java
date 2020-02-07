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

package com.borlander.rac353542.bislider.cdata;

import com.borlander.rac353542.bislider.BiSliderContentsDataProvider;

public abstract class DataObjectContentsDataProvider implements BiSliderContentsDataProvider {

	private final DataObjectMapper myObjectMapper;

	public DataObjectContentsDataProvider(final DataObjectMapper objectMapper) {
		myObjectMapper = objectMapper;
	}

	@Override
	public double getNormalValueAt(final double totalMin, final double totalMax, final double segmentMin,
			final double segmentMax) {
		final Object totalMinObject = myObjectMapper.double2object(totalMin);
		final Object totalMaxObject = myObjectMapper.double2object(totalMax);
		final Object segmentMinObject = myObjectMapper.double2object(segmentMin);
		final Object segmentMaxObject = myObjectMapper.double2object(segmentMax);
		return getNormalValueAt(totalMinObject, totalMaxObject, segmentMinObject, segmentMaxObject);
	}

	public abstract double getNormalValueAt(Object totalMinObject, Object totalMaxObject, Object segmentMinObject,
			Object segmentMaxObject);
}
