/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.contributions;

import java.awt.Color;
import java.util.Date;

import com.planetmayo.debrief.satc.model.GeoPoint;

/**
 * utility class for storing a measurement
 * 
 * @author ian
 * 
 */
public class FrequencyMeasurement
{
	private double freq;
	private final Date time;
	private Color color;
	private boolean isActive;
	private final GeoPoint origin;

	public FrequencyMeasurement(Date time, GeoPoint theLoc, double freq)
	{
		this.time = time;
		this.color = java.awt.Color.red;
		this.isActive = true;
		this.origin = theLoc;
		this.freq = freq;
	}

	public GeoPoint getLocation()
	{
		return origin;
	}
	
	public java.awt.Color getColor()
	{
		return color;
	}

	public void setColor(java.awt.Color color)
	{
		this.color = color;
	}

	public Date getDate()
	{
		return time;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void setActive(boolean active)
	{
		this.isActive = active;
	}

	public double getFrequency()
	{
		return freq;
	}
	
}