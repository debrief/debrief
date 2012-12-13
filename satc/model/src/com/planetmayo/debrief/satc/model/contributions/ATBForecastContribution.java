package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class ATBForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;
	
	public static final String MIN_ANGLE = "minAngle";	
	public static final String MAX_ANGLE = "maxAngle";
	
	private Double _minAngle = 0d;
	private Double _maxAngle = 2 * Math.PI;
	private Double _estimate;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// do something		
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public Double getMinAngle()
	{
		return _minAngle;
	}

	public void setMinAngle(Double minAngle)
	{
		Double old = _minAngle;
		_minAngle = minAngle;
		firePropertyChange(MIN_ANGLE, old, minAngle);
		firePropertyChange(HARD_CONSTRAINTS, old, minAngle);		
	}

	public Double getMaxAngle()
	{
		return _maxAngle;
	}

	public void setMaxAngle(Double maxAngle)
	{
		Double old = _maxAngle;		
		_maxAngle = maxAngle;
		firePropertyChange(MAX_ANGLE, old, maxAngle);
		firePropertyChange(HARD_CONSTRAINTS, old, maxAngle);
	}

	public Double getEstimate()
	{
		return _estimate;
	}

	public void setEstimate(Double estimate)
	{
		Double old = _estimate;
		_estimate = estimate;
		firePropertyChange(ESTIMATE, old, estimate);
	}
}
