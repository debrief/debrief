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

package Debrief.Tools.Tote.Calculations;

import java.text.DecimalFormat;

import MWC.Algorithms.Conversions;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Tools.Tote.DeltaRateToteCalculation;

public class courseRateCalc extends plainCalc implements DeltaRateToteCalculation {
	
	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public courseRateCalc() {
		super(new DecimalFormat("000.0"), "Course Rate (abs)", "degs/sec");
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	public courseRateCalc(DecimalFormat decimalFormat, String name, String unit) {
		super(decimalFormat, name, unit);
	}

	@Override
	public final double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime) {
		double res = Conversions.Rads2Degs(primary.getCourse());
		if (res < 0)
			res += 360;

		return res;
	}

	@Override
	public double[] calculate(final Watchable[] primary, final HiResDate[] thisTime, final long windowSizeMillis) {
		final double[] measure = calculateMeasure(primary);
		return DeltaRateToteCalcImplementation.calculateRate(measure, thisTime, windowSizeMillis);
	}

	protected static double[] calculateMeasure(final Watchable[] primary) {
		final double[] measure = new double[primary.length];
		for (int i = 0; i < primary.length; i++) {
			measure[i] = Conversions.Rads2Degs(primary[i].getCourse());
		}
		return measure;
	}

	/**
	 * does this calculation require special bearing handling (prevent wrapping
	 * through 360 degs)
	 *
	 */
	@Override
	public final boolean isWrappableData() {
		return false;
	}

	@Override
	public final String update(final Watchable primary, final Watchable secondary, final HiResDate time) {
		// check we have data
		if (primary == null)
			return NOT_APPLICABLE;

		return _myPattern.format(calculate(primary, secondary, time));
	}
}
