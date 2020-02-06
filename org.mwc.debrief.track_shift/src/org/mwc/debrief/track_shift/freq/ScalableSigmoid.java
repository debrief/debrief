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
package org.mwc.debrief.track_shift.freq;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;

public class ScalableSigmoid implements ParametricUnivariateFunction {

	@Override
	public double[] gradient(final double x, final double... params) {
		final double a = params[0];
		final double b = params[1];
		final double c = params[2];
		// double d = params[3];
		final double eaxb = Math.exp(a * x + b);
		return new double[] { -(eaxb * c * x) / Math.pow(eaxb + 1, 2), -(c * eaxb) / Math.pow(eaxb + 1, 2),
				1.0 / (eaxb + 1), 1.0 };
	}

	@Override
	public double value(final double x, final double... params) {
		final double a = params[0];
		final double b = params[1];
		final double c = params[2];
		final double d = params[3];
		return (c / (1.0 + Math.exp(a * x + b))) + d;
	}

}
