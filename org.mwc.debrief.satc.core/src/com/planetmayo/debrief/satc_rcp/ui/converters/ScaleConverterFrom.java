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

import org.eclipse.core.databinding.conversion.IConverter;

import com.planetmayo.debrief.satc_rcp.ui.converters.units.YdsToMeter;

public class ScaleConverterFrom implements IConverter {

	public static void main(final String[] args) {
		final ScaleConverterFrom converter = new ScaleConverterFrom(new int[] { 50, 100, 200, 500, 1000 },
				new int[] { 100, 1000, 3000, 7000, 17000, 40000 });
		final ScaleConverterTo converter1 = new ScaleConverterTo(new int[] { 50, 100, 200, 500, 1000 },
				new int[] { 100, 1000, 3000, 7000, 17000, 40000 });
		for (int i = 1; i < 300; i++) {
			System.out.println(i + " - " + converter.convert(i) + " - " + converter1.convert(converter.convert(i)));
		}
	}

	protected int[] increments;
	protected int[] values;

	protected int startValue;

	public ScaleConverterFrom(final int[] increments, final int[] borders) {
		this.increments = increments;
		this.values = new int[increments.length];
		for (int i = 0; i < borders.length - 1; i++) {
			values[i] = (borders[i + 1] - borders[i]) / increments[i];
		}
		startValue = borders[0];
	}

	@Override
	public Object convert(final Object value) {
		if (!(value instanceof Integer)) {
			return null;
		}
		// int val = (Integer) value;
		final Double v = ((Integer) value).doubleValue();
		final Double d = new YdsToMeter().safeConvert(v);
		final int val = d.intValue();

		int current = 0;
		int result = startValue;
		for (int i = 0; i < values.length; i++) {
			final int delta = Math.min(values[i], val - current);
			result += delta * increments[i];
			if (delta < values[i]) {
				break;
			}
			current += delta;
		}
		return (double) result;
	}

	@Override
	public Object getFromType() {
		return Integer.class;
	}

	@Override
	public Object getToType() {
		return Double.class;
	}
}
