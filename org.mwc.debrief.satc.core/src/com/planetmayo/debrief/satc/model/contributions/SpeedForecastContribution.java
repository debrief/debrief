/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class SpeedForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final double MAX_SPEED_VALUE_KTS = 40.0;
	public static final double MAX_SPEED_VALUE_MS = GeoSupport
			.kts2MSec(MAX_SPEED_VALUE_KTS);

	public static final String MIN_SPEED = "minSpeed";

	public static final String MAX_SPEED = "maxSpeed";

	protected Double minSpeed = 0d;
	protected Double maxSpeed = MAX_SPEED_VALUE_MS;
	protected Double estimate;

	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{

		// check that speed values are present
		if ((minSpeed != null) && (maxSpeed != null))
		{
			// create a bounded state representing our values
			final SpeedRange speedRange = new SpeedRange(getMinSpeed(), getMaxSpeed());
			for (BoundedState state : space.getBoundedStatesBetween(startDate,
					finishDate))
			{
				state.constrainTo(speedRange);
			}
		}
	}

	protected double calcError(State thisState)
	{
		double delta = 0;

		// do we have an estimate?
		if (getEstimate() != null)
		{
			delta = this.getEstimate() - thisState.getSpeed();

			// ok - we 'normalise' this speed according to the max/min
			Double limit;

			// is the state lower than the estimate?
			if (delta > 0)
			{
				// ok, do we have a min value
				limit = getMinSpeed();
			}
			else
			{
				// higher, use max
				limit = getMaxSpeed();
			}

			// do we have a relevant limit?
			if (limit != null)
			{
				// what's the range from the estimate to the limit
				double allowable = getEstimate() - limit;

				// ok, and how far through this are we
				delta = delta / allowable;
			}

			// ok, make it absolute
			delta = Math.abs(delta);
		}

		return delta;
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public Double getEstimate()
	{
		return estimate;
	}

	public Double getMaxSpeed()
	{
		return maxSpeed;
	}

	public Double getMinSpeed()
	{
		return minSpeed;
	}

	public void setEstimate(Double newEstimate)
	{
		Double oldEstimate = estimate;
		this.estimate = newEstimate;
		firePropertyChange(ESTIMATE, oldEstimate, newEstimate);
	}

	public void setMaxSpeed(Double newMaxSpeed)
	{
		Double oldMaxSpeed = maxSpeed;
		this.maxSpeed = newMaxSpeed;
		firePropertyChange(MAX_SPEED, oldMaxSpeed, newMaxSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldMaxSpeed, newMaxSpeed);
	}

	public void setMinSpeed(Double newMinSpeed)
	{
		Double oldMinSpeed = minSpeed;
		this.minSpeed = newMinSpeed;
		firePropertyChange(MIN_SPEED, oldMinSpeed, newMinSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldMinSpeed, newMinSpeed);
	}

}
