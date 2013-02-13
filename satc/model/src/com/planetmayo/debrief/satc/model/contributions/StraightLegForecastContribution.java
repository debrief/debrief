package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class StraightLegForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		
		for (BoundedState state : space.getBoundedStatesBetween(_startDate, _finishDate))
		{
			// NOTE: just double-check that this doesn't already have a leg - we can't let them overlap
			String existing = state.getMemberOf();
			if(existing != null)
				throw new RuntimeException("We don't support overlapping legs");
			
			// ok, now just store the leg id
			state.setMemberOf(this.getName());
		}
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

}
