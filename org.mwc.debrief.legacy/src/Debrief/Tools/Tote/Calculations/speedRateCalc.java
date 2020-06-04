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

import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.Tools.Tote.DeltaRateToteCalculation;

public class speedRateCalc extends plainCalc implements DeltaRateToteCalculation {

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	protected static double[] calculateMeasure(final Watchable[] primary) {
		final double[] measure = new double[primary.length];
		for (int i = 0; i < primary.length; i++) {
			measure[i] = primary[i].getSpeed();
		}
		return measure;
	}

	public speedRateCalc() {
		super(new DecimalFormat("00.00"), "Speed Rate (abs)", "Knots/sec");
	}
	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	public speedRateCalc(final DecimalFormat decimalFormat, final String description, final String units) {
		super(decimalFormat, description, units);
	}

	@Override
	public double calculate(final Watchable primary, final Watchable secondary, final HiResDate thisTime) {
		double res = 0.0;
		if (primary != null) {
			res = primary.getSpeed();
		}
		return res;
	}

	@Override
	public double[] calculate(final Watchable[] primary, final HiResDate[] thisTime, final long windowSizeMillis) {
		final double[] measure = calculateMeasure(primary);

		return DeltaRateToteCalcImplementation.calculateRate(measure, thisTime);
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
