package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class SpeedAnalysisContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	
	
	public SpeedAnalysisContribution() {
		super();
		setName("Speed Analysis");
	}

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// TODO implement this
		System.err.println("Speed Analysis Contribution Not Implemented!");
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}
}
