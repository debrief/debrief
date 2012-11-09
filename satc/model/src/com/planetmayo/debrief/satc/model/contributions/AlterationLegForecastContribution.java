package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class AlterationLegForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;
	
	public static final String MAX_COURSE_CHANGE = "maxCourseChange";
	public static final String MAX_SPEED_CHANGE = "maxSpeedChange";
	
	private Integer maxCourseChange = 0;
	private Double maxSpeedChange = 0.0;

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
		return "n/a";
	}
	
	public String getEstimate() 
	{
		return "n/a";
	}

	public Integer getMaxCourseChange()
	{
		return maxCourseChange;
	}

	public void setMaxCourseChange(Integer maxCourseChange)
	{
		Integer old = this.maxCourseChange;
		this.maxCourseChange = maxCourseChange;
		firePropertyChange(MAX_COURSE_CHANGE, old, maxCourseChange);
	}

	public Double getMaxSpeedChange()
	{
		return maxSpeedChange;
	}

	public void setMaxSpeedChange(Double maxSpeedChange)
	{
		Double old = this.maxSpeedChange;
		this.maxSpeedChange = maxSpeedChange;
		firePropertyChange(MAX_SPEED_CHANGE, old, maxSpeedChange);
	}
}
