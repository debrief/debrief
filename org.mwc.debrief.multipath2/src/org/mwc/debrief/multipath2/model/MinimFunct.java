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

package org.mwc.debrief.multipath2.model;

import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

// Class to evaluate the function z = a + x^2 + 3y^4
// where a is fixed and the values of x and y
// (x[0] and x[1] in this method) are the
// current values in the minimisation method.
class MinimFunct implements MinimisationFunction {

	private double a = 0.0D;

	// evaluation function
	@Override
	public double function(final double[] x) {
		final double z = a + x[0] * x[0] + 3.0D * Math.pow(x[1], 4);
		return z;
	}

	// Method to set a
	public void setA(final double a) {
		this.a = a;
	}
}
