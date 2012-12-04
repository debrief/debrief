package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class SpeedForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;
	
	public static final double MAX_SPEED_VALUE_KTS = 40.0;
	public static final double MAX_SPEED_VALUE_MS = GeoSupport.kts2MSec(MAX_SPEED_VALUE_KTS);	

	public static final String MIN_SPEED = "minSpeed";

	public static final String MAX_SPEED = "maxSpeed";

	protected Double _minSpeedKts = 0d;
	protected Double _maxSpeedKts = MAX_SPEED_VALUE_MS;
	protected Double _estimateKts;
	
	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{
		// create a bounded state representing our values
		final SpeedRange speedRange = new SpeedRange(getMinSpeed(), getMaxSpeed());
		for (BoundedState state : space.getBoundedStatesBetween(_startDate, _finishDate))
		{
			state.constrainTo(speedRange);
		}
		if (_startDate != null && space.getBoundedStateAt(_startDate) == null)
		{
			final BoundedState startState = new BoundedState(this.getStartDate());
			startState.constrainTo(speedRange);
			space.add(startState);
		}
		if (_finishDate != null && space.getBoundedStateAt(_finishDate) == null)
		{
			final BoundedState endState = new BoundedState(this.getFinishDate());
			endState.constrainTo(speedRange);
			space.add(endState);
		}
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public Double getEstimate()
	{
		return _estimateKts;
	}

	public Double getMaxSpeed()
	{
		return _maxSpeedKts;
	}

	public Double getMinSpeed()
	{
		return _minSpeedKts;
	}

	public void setEstimate(Double estimate)
	{
		Double oldEstimate = _estimateKts;
		this._estimateKts = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxSpeed(Double maxSpeed)
	{
		Double oldMaxSpeed = _maxSpeedKts;
		this._maxSpeedKts = maxSpeed;
		firePropertyChange(MAX_SPEED, oldMaxSpeed, maxSpeed);
	}

	public void setMinSpeed(Double minSpeed)
	{
		Double oldMinSpeed = _minSpeedKts;
		this._minSpeedKts = minSpeed;
		firePropertyChange(MIN_SPEED, oldMinSpeed, minSpeed);
	}

}
