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

public class YdsToMeter extends AbstractUnitConverter {
	final static private double FT_M_CONV = 0.3048;

	public YdsToMeter() {
		super(false);
	}

	@Override
	public Double safeConvert(final Number obj) {
		return (obj.doubleValue() * 3) * FT_M_CONV;
	}
}
