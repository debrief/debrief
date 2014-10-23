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
package MWC.Algorithms.LiveData;

/**
 * object that stores time-stamped data observation
 * 
 * @author ianmayo
 * 
 */
public class DataDoublet
{
	/**
	 * time of this observation
	 * 
	 */
	private final long _time;

	/**
	 * value of this observation
	 * 
	 */
	private final Object _value;

	/**
	 * constructor for a data observation
	 * 
	 * @param time
	 * @param value
	 */
	public DataDoublet(final long time, final Object value)
	{
		_time = time;
		_value = value;
	}

	/**
	 * retrieve time of observation
	 * 
	 * @return
	 */
	public long getTime()
	{
		return _time;
	}

	/**
	 * retrieve observation
	 * 
	 * @return
	 */
	public Object getValue()
	{
		return _value;
	}

}
