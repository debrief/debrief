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
package Debrief.Tools.Tote.Calculations;


import java.text.DecimalFormat;

import MWC.Algorithms.FrequencyCalcs;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public class dopplerCalc extends plainCalc
{

	private double _fNought = 150;

	private double _C = 2000;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public dopplerCalc()
	{
		super(new DecimalFormat("0.000"), "Doppler", "Hz");
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public double calculate(final Watchable primary, final Watchable secondary,
			final HiResDate thisTime)
	{
		double freq = 0.0;
		if ((primary != null) && (secondary != null) && (primary != secondary))
		{
	
			double rxSpeedKts = primary.getSpeed();
			double txSpeedKts = secondary.getSpeed();;
			
			double rxCourseDegs = Math.toDegrees(primary.getCourse());
			double txCourseDegs = Math.toDegrees(secondary.getCourse());
			
			double bearingDegs = Math.toDegrees(primary.getLocation().bearingFrom(secondary.getLocation()));

			// what's the observed freq?
			freq = FrequencyCalcs.getObservedFreq(_fNought, _C, rxSpeedKts, rxCourseDegs, 
					txSpeedKts, txCourseDegs, bearingDegs);
		}
		return freq;
	}
	
	/**
	 * does this calculation require special bearing handling (prevent wrapping
	 * through 360 degs)
	 */
	public final boolean isWrappableData()
	{
		return false;
	}

	public String update(final Watchable primary, final Watchable secondary,
			final HiResDate time)
	{
		// check we have data
		if((primary == null) || (secondary == null))
			return NOT_APPLICABLE;
		
    return _myPattern.format(
       calculate(primary, secondary, time));
	}

	public void setFNought(double value)
	{
		_fNought = value;
	}
	public double getFNought()
	{
		return _fNought;
	}

	public void setSpeedOfSound(double value)
	{
		_C = value;
	}
	
	public double getSpeedOfSound()
	{
		return _C;
	}
	

}
