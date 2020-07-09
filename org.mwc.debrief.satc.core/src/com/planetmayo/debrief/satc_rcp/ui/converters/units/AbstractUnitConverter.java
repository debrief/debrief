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

package com.planetmayo.debrief.satc_rcp.ui.converters.units;

import org.eclipse.core.databinding.conversion.IConverter;

public abstract class AbstractUnitConverter implements IConverter {
	protected boolean isModelToUI;

	public AbstractUnitConverter(final boolean isModelToUI) {
		this.isModelToUI = isModelToUI;
	}

	@Override
	public Object convert(final Object obj) {
		if (obj == null) {
			return null;
		}
		final Double result = safeConvert((Number) obj);
		return result;
	}

	@Override
	public Object getFromType() {
		return Number.class;
	}

	@Override
	public Object getToType() {
		return Double.class;
	}

	protected abstract Double safeConvert(Number obj);
}
