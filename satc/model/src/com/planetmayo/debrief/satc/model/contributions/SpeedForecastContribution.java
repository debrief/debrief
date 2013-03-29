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
		// create a bounded state representing our values
		final SpeedRange speedRange = new SpeedRange(getMinSpeed(), getMaxSpeed());
		for (BoundedState state : space.getBoundedStatesBetween(startDate,
				finishDate))
		{
			state.constrainTo(speedRange);
		}
	}

	protected double calcError(State thisState)
	{
		double delta = 0;

		// do we have an estimate?
		if (getEstimate() != null)
			delta = Math.abs(this.getEstimate() - thisState.getSpeed());

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
