package com.planetmayo.debrief.satc.model.contributions;

import java.awt.Color;
import java.util.Date;

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

	public FrequencyMeasurement(Date time, double freq)
	{
		_time = time;
		_color = java.awt.Color.red;
		_isActive = true;
		_freq = freq;
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