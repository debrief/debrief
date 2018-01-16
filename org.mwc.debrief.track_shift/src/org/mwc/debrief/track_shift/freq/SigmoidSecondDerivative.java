package org.mwc.debrief.track_shift.freq;
import org.apache.commons.math3.analysis.UnivariateFunction;

public class SigmoidSecondDerivative implements UnivariateFunction {
	
	public double[] coeff;

	@Override
	public double value(double x) {		
		double a = coeff[0];
		double b = coeff[1];
		double c = coeff[2];
		
		double eaxb = Math.exp(a*x+b);
		return (a*a*c*eaxb*(eaxb-1))/Math.pow(eaxb+1,3);
	}


}
