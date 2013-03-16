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

	protected Double _minSpeed = 0d;
	protected Double _maxSpeed = MAX_SPEED_VALUE_MS;
	protected Double _estimate;

	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{
		// create a bounded state representing our values
		final SpeedRange speedRange = new SpeedRange(getMinSpeed(), getMaxSpeed());
		for (BoundedState state : space.getBoundedStatesBetween(_startDate,
				_finishDate))
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
		return _estimate;
	}

	public Double getMaxSpeed()
	{
		return _maxSpeed;
	}

	public Double getMinSpeed()
	{
		return _minSpeed;
	}

	public void setEstimate(Double estimate)
	{
		Double oldEstimate = _estimate;
		this._estimate = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxSpeed(Double maxSpeed)
	{
		Double oldMaxSpeed = _maxSpeed;
		this._maxSpeed = maxSpeed;
		firePropertyChange(MAX_SPEED, oldMaxSpeed, maxSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldMaxSpeed, maxSpeed);
	}

	public void setMinSpeed(Double minSpeed)
	{
		Double oldMinSpeed = _minSpeed;
		this._minSpeed = minSpeed;
		firePropertyChange(MIN_SPEED, oldMinSpeed, minSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldMinSpeed, minSpeed);
	}

}
