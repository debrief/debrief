package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class ATBForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;
	
	public static final String MIN_ANGLE = "minAngle";	
	public static final String MAX_ANGLE = "maxAngle";
	
	private Integer _minAngle = 0;
	private Integer _maxAngle = 360;
	private Integer _estimate;

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
		String min = _minAngle == null ? "0" : "" + _minAngle.intValue();
		String max = _maxAngle == null ? "360" : "" + _maxAngle.intValue();
		return min + (min.equals(max) ? "" : " - " + max);
	}

	public Integer getMinAngle()
	{
		return _minAngle;
	}

	public void setMinAngle(Integer minAngle)
	{
		String oldHardConstraints = getHardConstraints();
		Integer old = _minAngle;
		_minAngle = minAngle;
		firePropertyChange(MIN_ANGLE, old, minAngle);
		firePropertyChange(HARD_CONSTRAINTS, oldHardConstraints, getHardConstraints());
	}

	public Integer getMaxAngle()
	{
		return _maxAngle;
	}

	public void setMaxAngle(Integer maxAngle)
	{
		String oldHardConstraints = getHardConstraints();
		Integer old = _maxAngle;		
		_maxAngle = maxAngle;
		firePropertyChange(MAX_ANGLE, old, maxAngle);
		firePropertyChange(HARD_CONSTRAINTS, oldHardConstraints, getHardConstraints());
	}

	public Integer getEstimate()
	{
		return _estimate;
	}

	public void setEstimate(Integer estimate)
	{
		Integer old = _estimate;
		_estimate = estimate;
		firePropertyChange(ESTIMATE, old, estimate);
	}
}
