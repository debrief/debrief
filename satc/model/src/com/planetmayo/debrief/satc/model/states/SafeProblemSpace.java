package com.planetmayo.debrief.satc.model.states;

import java.util.Collection;
import java.util.Date;

import com.planetmayo.debrief.satc.model.VehicleType;

public class SafeProblemSpace
{
	private final ProblemSpace problemSpace;

	public SafeProblemSpace(ProblemSpace problemSpace)
	{
		this.problemSpace = problemSpace;
	}
	
	public VehicleType getVehicleType() 
	{
		return problemSpace.getVehicleType();
	}
	
	public BoundedState getBoundedStateAt(Date theTime) 
	{
		return problemSpace.getBoundedStateAt(theTime);
	}
	
	public Collection<BoundedState> getBoundedStatesBetween(Date startDate,	Date finishDate) 
	{
		return problemSpace.getBoundedStatesBetween(startDate, finishDate);
	}
	
	public Date getFinishDate() 
	{
		return problemSpace.getFinishDate();
	}
	
	public Date getStartDate() 
	{
		return problemSpace.getStartDate();
	}
	
	public int size() 
	{
		return problemSpace.size();
	}
	
	public Collection<BoundedState> states() 
	{
		return problemSpace.states();
	}
	
	public Collection<BoundedState> times() 
	{
		return problemSpace.states();
	}
	
	public Collection<BoundedState> statesAfter(BoundedState state) 
	{
		return problemSpace.statesAfter(state);
	}
}
