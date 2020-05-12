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

public final class courseDeltaRateRateCalc extends courseDeltaAverageCalc {

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public courseDeltaRateRateCalc() {
		super(new DecimalFormat("000.0"), "Absolute Course Delta Rate (abs)", "degs/sec/sec");
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	@Override
	public double[] calculate(final Watchable[] primary, final HiResDate[] thisTime, final long windowSizeMillis) {
		final double[] measure = createMeasures(primary);
		final double[] deltaRate = super.calculate(primary, thisTime, windowSizeMillis); // Average
		return DeltaRateToteCalcImplementation.calculateDeltaRateRate(measure, thisTime, windowSizeMillis, deltaRate);
	}
}
