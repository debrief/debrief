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

package com.planetmayo.debrief.satc_rcp.ui.converters;

import java.util.concurrent.Callable;

import org.eclipse.core.databinding.conversion.IConverter;

public class BooleanToNullConverter<T> implements IConverter {
	private T defaultValue;
	private Class<T> computatorClass;
	private Callable<T> defaultValueComputator;

	public BooleanToNullConverter(final Class<T> computatorClass, final Callable<T> defaultValueComputator) {
		this.computatorClass = computatorClass;
		this.defaultValueComputator = defaultValueComputator;
	}

	public BooleanToNullConverter(final T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public Object convert(final Object from) {
		final Boolean data = (Boolean) from;
		if (data == Boolean.TRUE) {
			if (defaultValue != null) {
				return defaultValue;
			} else {
				try {
					return defaultValueComputator.call();
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	@Override
	public Object getFromType() {
		return Boolean.class;
	}

	@Override
	public Object getToType() {
		if (defaultValue != null) {
			return defaultValue.getClass();
		}
		return computatorClass;
	}
}
