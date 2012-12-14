package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class CourseAnalysisContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// TODO implement this
		System.err.println("Course Analysis Contribution Not Implemented!");
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}
}
