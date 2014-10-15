/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
