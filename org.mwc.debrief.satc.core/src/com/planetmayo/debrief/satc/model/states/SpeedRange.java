/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model.states;

/**
 * class representing a set of speed bounds
 *
 * @author ian
 *
 */
public class SpeedRange extends BaseRange<SpeedRange> {
	private volatile double _minSpeed;
	private volatile double _maxSpeed;

	public SpeedRange(final double minSpd, final double maxSpd) {
		_minSpeed = minSpd;
		_maxSpeed = maxSpd;
	}

	/**
	 * copy constructor
	 *
	 * @param range
	 */
	public SpeedRange(final SpeedRange range) {
		this(range.getMin(), range.getMax());
	}

	/**
	 * does the supplied speed fit in my range?
	 *
	 * @param speed the value to test
	 * @return yes/no
	 */
	public boolean allows(final double speed) {
		return (speed >= _minSpeed) && (speed <= _maxSpeed);
	}

	@Override
	public void constrainTo(final SpeedRange sTwo) throws IncompatibleStateException {
		_minSpeed = Math.max(getMin(), sTwo.getMin());
		_maxSpeed = Math.min(getMax(), sTwo.getMax());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final SpeedRange other = (SpeedRange) obj;

		if (_maxSpeed != other._maxSpeed)
			return false;
		if (_minSpeed != other._minSpeed)
			return false;

		return true;
	}

	public double getMax() {
		return _maxSpeed;
	}

	public double getMin() {
		return _minSpeed;
	}

	@Override
	public int hashCode() {
		int result = (int) _minSpeed;
		result = 31 * result + (int) _maxSpeed;
		return result;
	}
}
