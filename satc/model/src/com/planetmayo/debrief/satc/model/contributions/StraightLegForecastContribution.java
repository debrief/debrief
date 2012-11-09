package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class StraightLegForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// do something...
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
}
