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