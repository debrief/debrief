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
	private double _freq;
	private final Date _time;
	private Color _color;
	private boolean _isActive;
	private final GeoPoint _theLoc;

	public FrequencyMeasurement(Date time, GeoPoint theLoc, double freq)
	{
		_time = time;
		_color = java.awt.Color.red;
		_isActive = true;
		_theLoc = theLoc;
		_freq = freq;
	}

	public GeoPoint getLocation()
	{
		return _theLoc;
	}
	
	public java.awt.Color getColor()
	{
		return _color;
	}

	public void setColor(java.awt.Color color)
	{
		_color = color;
	}

	public Date getDate()
	{
		return _time;
	}

	public boolean isActive()
	{
		return _isActive;
	}

	public void setActive(boolean active)
	{
		_isActive = active;
	}

	public double getFrequency()
	{
		return _freq;
	}
	
	public Date getTime()
	{
		return _time;
	}

}