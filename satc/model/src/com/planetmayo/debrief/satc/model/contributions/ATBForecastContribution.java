package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class ATBForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;
	
	public static final String MIN_ANGLE = "minAngle";	
	public static final String MAX_ANGLE = "maxAngle";
	
	private int _minAngle;
	private int _maxAngle = 360;
	private int _estimate;

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

	@Override
	public String getHardConstraints()
	{
		return "" + _minAngle + (_minAngle == _maxAngle ? "" : " - " + _maxAngle);
	}

	public int getMinAngle()
	{
		return _minAngle;
	}

	public void setMinAngle(int minAngle)
	{
		String oldHardConstraints = getHardConstraints();
		int old = _minAngle;
		_minAngle = minAngle;
		firePropertyChange(MIN_ANGLE, old, minAngle);
		firePropertyChange(HARD_CONSTRAINTS, oldHardConstraints, getHardConstraints());
	}

	public int getMaxAngle()
	{
		return _maxAngle;
	}

	public void setMaxAngle(int maxAngle)
	{
		String oldHardConstraints = getHardConstraints();
		int old = _maxAngle;		
		_maxAngle = maxAngle;
		firePropertyChange(MAX_ANGLE, old, maxAngle);
		firePropertyChange(HARD_CONSTRAINTS, oldHardConstraints, getHardConstraints());
	}

	public int getEstimate()
	{
		return _estimate;
	}

	public void setEstimate(int estimate)
	{
		int old = _estimate;
		_estimate = estimate;
		firePropertyChange(ESTIMATE, old, estimate);
	}
}
