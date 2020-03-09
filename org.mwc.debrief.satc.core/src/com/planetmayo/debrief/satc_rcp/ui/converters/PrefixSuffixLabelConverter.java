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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.eclipse.core.databinding.conversion.IConverter;

import com.planetmayo.debrief.satc_rcp.ui.converters.units.AbstractUnitConverter;

public class PrefixSuffixLabelConverter implements IConverter, Cloneable {
	private AbstractUnitConverter nestedUnitConverter;

	private final NumberFormat numberFormat;
	private final Class<?> fromType;
	private final String prefix;
	private final String suffix;

	public PrefixSuffixLabelConverter(final Class<?> fromType) {
		this(fromType, "", "");
	}

	public PrefixSuffixLabelConverter(final Class<?> fromType, final String suffix) {
		this(fromType, "", suffix);
	}

	public PrefixSuffixLabelConverter(final Class<?> fromType, final String prefix, final String suffix) {
		this(fromType, prefix, suffix, new DecimalFormat("0"));
	}

	public PrefixSuffixLabelConverter(final Class<?> fromType, final String prefix, final String suffix,
			final NumberFormat numberFormat) {
		this.fromType = fromType;
		this.prefix = prefix;
		this.suffix = suffix;
		this.numberFormat = numberFormat;
	}

	@Override
	public PrefixSuffixLabelConverter clone() throws CloneNotSupportedException {
		return (PrefixSuffixLabelConverter) super.clone();
	}

	@Override
	public Object convert(Object from) {
		if (from == null) {
			return "";
		}
		if (from instanceof Number) {
			if (getNestedUnitConverter() != null) {
				from = getNestedUnitConverter().convert(from);
			}
			from = numberFormat.format(from);
		}
		return prefix + from + suffix;
	}

	@Override
	public Object getFromType() {
		return fromType;
	}

	public AbstractUnitConverter getNestedUnitConverter() {
		return nestedUnitConverter;
	}

	@Override
	public Object getToType() {
		return String.class;
	}

	public void setNestedUnitConverter(final AbstractUnitConverter nestedUnitConverter) {
		this.nestedUnitConverter = nestedUnitConverter;
	}
}
