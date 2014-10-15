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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class ATBForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;
	
	public static final String MIN_ANGLE = "minAngle";	
	public static final String MAX_ANGLE = "maxAngle";
	
	private Double minAngle = 0d;
	private Double maxAngle = 2 * Math.PI;
	private Double estimate;

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

	public Double getMinAngle()
	{
		return minAngle;
	}

	public void setMinAngle(Double newMinAngle)
	{
		Double old = minAngle;
		minAngle = newMinAngle;
		firePropertyChange(MIN_ANGLE, old, newMinAngle);
		firePropertyChange(HARD_CONSTRAINTS, old, newMinAngle);		
	}

	public Double getMaxAngle()
	{
		return maxAngle;
	}

	public void setMaxAngle(Double newMaxAngle)
	{
		Double old = maxAngle;		
		maxAngle = newMaxAngle;
		firePropertyChange(MAX_ANGLE, old, newMaxAngle);
		firePropertyChange(HARD_CONSTRAINTS, old, newMaxAngle);
	}

	public Double getEstimate()
	{
		return estimate;
	}

	public void setEstimate(Double newEstimate)
	{
		Double old = estimate;
		estimate = newEstimate;
		firePropertyChange(ESTIMATE, old, newEstimate);
	}
}
