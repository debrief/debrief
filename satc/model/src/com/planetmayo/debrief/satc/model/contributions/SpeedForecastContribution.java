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

	public static final String MIN_SPEED = "minSpeed";

	public static final String MAX_SPEED = "maxSpeed";

	protected Double _minSpeedKts = 0d;
	protected Double _maxSpeedKts = MAX_SPEED_VALUE_KTS;
	protected Double _estimateKts;
	
	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{
		// create a bounded state representing our values
		final SpeedRange speedRange = new SpeedRange(GeoSupport.kts2MSec(getMinSpeed()),
				GeoSupport.kts2MSec(getMaxSpeed()));
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

	@Override
	public String getEstimateStr()
	{
		return _estimateKts == null ? "" : "" + _estimateKts.intValue();
	}

	@Override
	public String getHardConstraints()
	{
		String min = _minSpeedKts == null ? "-\u221E" : "" + _minSpeedKts.intValue();
		String max = _maxSpeedKts == null ? "+\u221E" : "" + _maxSpeedKts.intValue();
		return min + (min.equals(max) ? "" : " - " + max);
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
		String oldConstraints = getHardConstraints();
		Double oldMaxSpeed = _maxSpeedKts;
		this._maxSpeedKts = maxSpeed;
		firePropertyChange(MAX_SPEED, oldMaxSpeed, maxSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	public void setMinSpeed(Double minSpeed)
	{
		Double oldMinSpeed = _minSpeedKts;
		String oldConstraints = getHardConstraints();
		this._minSpeedKts = minSpeed;
		firePropertyChange(MIN_SPEED, oldMinSpeed, minSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

}
