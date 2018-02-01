package org.mwc.debrief.track_shift.freq;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;

public class ScalableSigmoid implements ParametricUnivariateFunction {

	@Override
	public double[] gradient(double x, double... params) {
		double a = params[0];
		double b = params[1];
		double c = params[2];
		//double d = params[3];
		double eaxb = Math.exp(a*x+b); 
		return new double[] {
			-(eaxb*c*x)/Math.pow(eaxb+1, 2),
			-(c*eaxb)/Math.pow(eaxb+1, 2),
			1.0/(eaxb+1),
			1.0
		};
	}

	@Override
	public double value(double x, double... params) {
		double a = params[0];
		double b = params[1];
		double c = params[2];
		double d = params[3];
		return (c/(1.0+Math.exp(a*x+b)))+d;
	}

}
