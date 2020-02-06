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

package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.SpeedRange;

public class SpeedAnalysisContribution extends BaseAnalysisContribution<SpeedRange> {
	private static final long serialVersionUID = 1L;

	public SpeedAnalysisContribution() {
		super();
		setName("Speed Analysis");
	}

	@Override
	protected void applyThis(final BoundedState state, final SpeedRange thisState) throws IncompatibleStateException {
		state.constrainTo(thisState);
	}

	@Override
	protected SpeedRange calcRelaxedRange(final BoundedState lastStateWithRange, final VehicleType vType, long millis) {
		final double maxAccel;
		final double maxDecel;

		// see if we are running fwd or bwds
		if (millis < 0) {
			maxAccel = vType.getMaxDecelRate();
			maxDecel = vType.getMaxAccelRate();
		} else {
			maxDecel = vType.getMaxDecelRate();
			maxAccel = vType.getMaxAccelRate();
		}

		// just in case we're doing a reverse pass, use the abs millis
		millis = Math.abs(millis);

		final double diffSeconds = millis / 1000.0d;

		double minSpeed = lastStateWithRange.getSpeed().getMin() - maxDecel * diffSeconds;
		final double maxSpeed = lastStateWithRange.getSpeed().getMax() + maxAccel * diffSeconds;
		if (minSpeed < 0) {
			minSpeed = 0;
		}
		return new SpeedRange(minSpeed, maxSpeed);
	}

	@Override
	protected SpeedRange cloneRange(final SpeedRange thisRange) {
		return new SpeedRange(thisRange);
	}

	@Override
	protected void furtherConstrain(final SpeedRange currentLegState, final SpeedRange thisRange)
			throws IncompatibleStateException {
		currentLegState.constrainTo(thisRange);
	}

	@Override
	public ContributionDataType getDataType() {
		return ContributionDataType.ANALYSIS;
	}

	@Override
	protected SpeedRange getRangeFor(final BoundedState lastStateWithRange) {
		return lastStateWithRange.getSpeed();
	}
}
