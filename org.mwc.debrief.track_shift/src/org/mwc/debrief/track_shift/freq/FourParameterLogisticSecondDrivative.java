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

import org.apache.commons.math3.analysis.UnivariateFunction;

public class FourParameterLogisticSecondDrivative implements UnivariateFunction {

	final public double[] coeff;

	public FourParameterLogisticSecondDrivative(final double[] modelParameters) {
		coeff = modelParameters;
	}

	@Override
	public double value(final double x) {
		final double a = coeff[0];
		final double b = coeff[1];
		final double c = coeff[2];
		final double d = coeff[3];
		final double cb = Math.pow(c, b);
		final double xb = Math.pow(x, b);
		return -(b * cb * Math.pow(x, b - 2) * (b * cb - cb - b * xb - xb) * (a - d)) / Math.pow(cb + xb, 3);

	}

}
