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

/*
 *	    Class MinimisationExample
 *
 *       An example of the use of the class Minimisation
 *       and the interface MinimisationFunction
 *
 *       Finds the minimum of the function
 *           z = a + x^2 + 3y^4
 *       where a is constant
 *       (an easily solved function has been chosen
 *       for clarity and easy checking)
 *
 * 	    WRITTEN BY: Michael Thomas Flanagan
 *
 *	    DATE:   April 2004
 *       UPDATE: June  2005
 *
 *       PERMISSION TO COPY:
 * 	    Permission to use, copy and modify this software and its documentation
 *	    for NON-COMMERCIAL purposes and without fee is hereby granted provided
 *	    that an acknowledgement to the author, Michael Thomas Flanagan, and the
 *	    disclaimer below, appears in all copies.
 *
 * 	    The author makes no representations or warranties about the suitability
 *       or fitness of the software for any or for a particular purpose.
 *       The author shall not be liable for any damages suffered as a result of
 *       using, modifying or distributing this software or its derivatives.
 *
 **********************************************************/
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

// Class to demonstrate minimisation method, Minimisation nelderMead
public class MinimisationExample {

	public static void main(final String[] args) {

		// Create instance of Minimisation
		final Minimisation min = new Minimisation();

		// Create instace of class holding function to be minimised
		final MinimFunct funct = new MinimFunct();

		// Set value of the constant a to 5
		funct.setA(5.0D);

		// initial estimates
		final double[] start = { 1.0D, 3.0D };

		// initial step sizes
		final double[] step = { 0.2D, 0.6D };

		// convergence tolerance
		final double ftol = 1e-15;

		// Nelder and Mead minimisation procedure
		min.nelderMead(funct, start, step, ftol);

		// get values of y and z at minimum
		final double[] param = min.getParamValues();

		// Output the results to screen
		System.out.println("Minimum = " + min.getMinimum());
		System.out.println("Value of x at the minimum = " + param[0]);
		System.out.println("Value of y at the minimum = " + param[1]);

	}
}
