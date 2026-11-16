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
package org.mwc.debrief.track_shift.zig_detector.moving_average;

import java.util.List;
import java.util.Vector;

/**
 * windowed moving average
 *
 * @author Ian
 *
 */
public class TimeRestrictedMovingAverage {

	final List<Long> _times;
	final List<Double> _values;

	final long _millis;

	/**
	 * whether we've got enough entries to fill the window
	 *
	 */
	boolean _populated = false;

	/**
	 * the minimum number of measurements we require, before a moving average has
	 * stabilised
	 */
	private final int _minSample;

	public TimeRestrictedMovingAverage(final long millis, final int minSample) {
		_millis = millis;
		_minSample = minSample;
		_times = new Vector<Long>();
		_values = new Vector<Double>();
	}

	public void add(final long time, final double value) {

		// ok, we've moved forwards. Do some deleting
		final List<Long> removeTimes = new Vector<Long>();
		final List<Double> removeValues = new Vector<Double>();

		for (int i = 0; i < _times.size(); i++) {
			final Long thisTime = _times.get(i);
			// note: we're using ABS here, since the series
			// of values may be running backwards in time
			if (Math.abs(time - thisTime) > _millis) {
				removeTimes.add(thisTime);
				removeValues.add(_values.get(i));
			}
		}

		// and remove them
		if (!removeTimes.isEmpty()) {
			if (!_populated) {
				_populated = true;
			}

			_times.removeAll(removeTimes);
			_values.removeAll(removeValues);
		}

		// ok, now add the new items
		_times.add(time);
		_values.add(value);
	}

	public void clear() {
		_times.clear();
		_values.clear();
		_populated = false;
	}

	public double getAverage() {
		if (_values.size() > 0) {
			double sum = 0;
			for (final Double value : _values) {
				sum += value;
			}
			return sum / _values.size();
		} else {
			return Double.NaN;
		}
	}

	public double getVariance() {
		if (_values.size() > 0) {
			final double mean = getAverage();

			double sum = 0;
			for (final Double value : _values) {
				sum += Math.pow(mean - value, 2);
			}
			final double variance = sum / _values.size();
			return Math.sqrt(variance);
		} else {
			return 0d;
		}
	}

	public boolean isEmpty() {
		return _values.isEmpty();
	}

	public boolean isPopulated() {
		return _times.size() >= _minSample;
	}

	public double lastValue() {
		return _values.get(_values.size() - 1);
	}

	public long period() {
		if (_times.isEmpty()) {
			return 0;
		} else {
			return _times.get(_times.size() - 1) - _times.get(0);
		}
	}

	public int size() {
		return _times.size();
	}
}
