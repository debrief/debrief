package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class VehiclePerformanceReferenceContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	private VehicleType vType;
	
	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// store the current value of vehicle type
	}

	public void setRepository(VehicleType vType)
	{
		this.vType = vType;
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
