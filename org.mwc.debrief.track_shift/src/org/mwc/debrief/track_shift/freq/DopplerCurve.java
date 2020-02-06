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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;

public class DopplerCurve implements IDopplerCurve {

	/**
	 * our function definition for a doppler curve
	 *
	 * @author Ian
	 *
	 */
	@SuppressWarnings("unused")
	private static class DopplerCurveFitter extends AbstractCurveFitter {

		@Override
		protected LeastSquaresProblem getProblem(final Collection<WeightedObservedPoint> points) {
			final int len = points.size();
			final double[] target = new double[len];
			final double[] weights = new double[len];

			int i = 0;
			for (final WeightedObservedPoint point : points) {
				target[i] = point.getY();
				weights[i] = point.getWeight();
				i += 1;
			}

			final AbstractCurveFitter.TheoreticalValuesFunction model = new AbstractCurveFitter.TheoreticalValuesFunction(
					new ScalableSigmoid(), points);

			final LeastSquaresBuilder lsb = new LeastSquaresBuilder();
			lsb.maxEvaluations(1000000);
			lsb.maxIterations(1000000);
			lsb.start(new double[] { 1.0, -0.5, 1.0, 1.0 });
			lsb.target(target);
			lsb.weight(new DiagonalMatrix(weights));
			lsb.model(model.getModelFunction(), model.getModelFunctionJacobian());
			return lsb.build();
		}

	}

	private static class FourPLCurveFitter extends AbstractCurveFitter {
		@Override
		protected LeastSquaresProblem getProblem(final Collection<WeightedObservedPoint> points) {
			final int len = points.size();
			final double[] target = new double[len];
			final double[] weights = new double[len];

			int i = 0;
			for (final WeightedObservedPoint point : points) {
				target[i] = point.getY();
				weights[i] = point.getWeight();
				i += 1;
			}

			final AbstractCurveFitter.TheoreticalValuesFunction model = new AbstractCurveFitter.TheoreticalValuesFunction(
					new FourParameterLogistic(), points);

			final LeastSquaresBuilder lsb = new LeastSquaresBuilder();
			lsb.maxEvaluations(1000000);
			lsb.maxIterations(1000000);
			lsb.start(new double[] { 1.0, -0.5, 1.0, 1.0 });
			lsb.target(target);
			lsb.weight(new DiagonalMatrix(weights));
			lsb.model(model.getModelFunction(), model.getModelFunctionJacobian());
			return lsb.build();
		}

	}

	/**
	 * time stamp at inflection point
	 */
	private final long _inflectionTime;

	/**
	 * frequency at inflection point
	 */
	private final double _inflectionFreq;

	/**
	 * double[4] -> [a,b,c,d] for the sigmoid model: d + (c/(1+e^(a*x+b)))
	 */
	private final double[] _modelParameters;

	private final Normaliser _timeNormaliser;

	public DopplerCurve(final ArrayList<Long> times, final ArrayList<Double> freqs) {
		// do some data testing
		if (times == null || freqs == null) {
			throw new IllegalArgumentException("The input datasets cannot be null");
		}

		if (times.size() == 0 || freqs.size() == 0) {
			throw new IllegalArgumentException("The input datasets cannot be empty");
		}

		// convert the times to doubles
		final ArrayList<Double> dTimes = new ArrayList<Double>();
		for (final Long t : times) {
			dTimes.add((double) t);
		}
		_timeNormaliser = new Normaliser(dTimes, false);
		final Normaliser freqNormaliser = new Normaliser(freqs, true);

		final int sampleCount = times.size();

		// ok, collate the data
		final WeightedObservedPoints obs = new WeightedObservedPoints();

		for (int i = 0; i < sampleCount; i++) {
			final double time = _timeNormaliser.normalise(dTimes.get(i));
			final double freq = freqNormaliser.normalise(freqs.get(i));
			obs.add(time, freq);
			System.out.println(time + ", " + freq);
		}

		// now Instantiate a parametric sigmoid fitter.
		// final AbstractCurveFitter fitter = new DopplerCurveFitter(); // ***
		final AbstractCurveFitter fitter = new FourPLCurveFitter(); // ***

		// Retrieve fitted parameters (a,b,c,d) for the sigmoid model: d +
		// (c/(1+e^(a*x+b)))
		final double[] coeff = fitter.fit(obs.toList());

		// --- checking for inflection point ---
		// construct the second order derivative of the sigmoid with this parameters
		// SigmoidSecondDerivative derivativeFunc = new SigmoidSecondDerivative(); //
		// ***
		final FourParameterLogisticSecondDrivative derivativeFunc = new FourParameterLogisticSecondDrivative(coeff); // ***

		// use bisection solver to find the zero crossing point of derivative
		final BisectionSolver bs = new BisectionSolver(1.0e-12, 1.0e-8);
		final double root = bs.solve(1000000, derivativeFunc, 0, 1, 0.5);

		// and store the equation parameters
		_modelParameters = coeff;

		_inflectionTime = (long) _timeNormaliser.deNormalise(root); // taking into account
																	// that time is reversed
		_inflectionFreq = valueAt(_inflectionTime);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see IDopplerCurve#inflectionFreq()
	 */
	@Override
	public double inflectionFreq() {
		return _inflectionFreq;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see IDopplerCurve#inflectionTime()
	 */
	@Override
	public long inflectionTime() {
		return _inflectionTime;
	}

	@Override
	public void printCoords() {
		final double[] coords = _modelParameters;
		for (int i = 0; i < coords.length; i++) {
			System.out.print(coords[i] + " , ");
		}
		System.out.println();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see IDopplerCurve#valueAt(long)
	 */
	@Override
	public double valueAt(final long t) {
		final double normalised = _timeNormaliser.normalise(t);
		return new ScalableSigmoid().value(normalised, _modelParameters);
	}

}