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

public class FourParameterLogistic implements ParametricUnivariateFunction {

	@Override
	public double[] gradient(double x, double... params) {		
		double a = params[0];		
		double b = params[1];		
		double c = params[2];		
		double d = params[3];		
		return new double[] {
			1.0/(Math.pow(x/c, b)+1.0),
			-(Math.pow(c*x, b)*Math.log(x/c)*(a-d)) / Math.pow(Math.pow(c, b)+Math.pow(x, b),2),
			(b*Math.pow(c, b-1)*Math.pow(x, b)*(a-d))/(Math.pow(Math.pow(c, b)+Math.pow(x, b),2)),
			1.0-(1.0/(1.0+(Math.pow(x/c, b))))
		};

	}

	@Override
	public double value(double x, double... params) {
		double a = params[0];
		double b = params[1];
		double c = params[2];
		double d = params[3];
		return ((a-d)/(1.0+Math.pow(x/c, b)))+d;
	}

}
