/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.states;

/**
 * class representing a set of speed bounds
 * 
 * @author ian
 * 
 */
public class SpeedRange extends BaseRange<SpeedRange>
{
	private volatile double _minSpeed;
	private volatile double _maxSpeed;

	public SpeedRange(double minSpd, double maxSpd)
	{
		_minSpeed = minSpd;
		_maxSpeed = maxSpd;
	}

	/**
	 * copy constructor
	 * 
	 * @param range
	 */
	public SpeedRange(SpeedRange range)
	{
		this(range.getMin(), range.getMax());
	}

	@Override
	public void constrainTo(SpeedRange sTwo) throws IncompatibleStateException
	{
		_minSpeed = Math.max(getMin(), sTwo.getMin());
		_maxSpeed = Math.min(getMax(), sTwo.getMax());
	}

	public double getMax()
	{
		return _maxSpeed;
	}

	public double getMin()
	{
		return _minSpeed;
	}	

	@Override
	public int hashCode()
	{
    int result = (int) _minSpeed;
    result = 31 * result + (int) _maxSpeed;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}
		SpeedRange other = (SpeedRange) obj;		
		
		if (_maxSpeed != other._maxSpeed)	return false;
		if (_minSpeed != other._minSpeed)	return false;

		return true;
	}

	/** does the supplied speed fit in my range?
	 * 
	 * @param speed the value to test
	 * @return  yes/no
	 */
	public boolean allows(double speed)
	{
		return (speed >= _minSpeed) && (speed <= _maxSpeed);
	}		
}
