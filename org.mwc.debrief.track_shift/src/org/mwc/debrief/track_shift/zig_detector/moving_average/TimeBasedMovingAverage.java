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

/**
 * Created by Romain on 09/05/2015.
 */
public class TimeBasedMovingAverage {

	public static Boolean inTimeFrame(final Long duration, final long refMillis, final long candidateMillis) {
		final Long distance = refMillis - candidateMillis;
		return Math.abs(distance) <= Math.abs(duration);
	}

	/**
	 * how far either side of the specified time we include data values
	 *
	 */
	private final Long duration;

	/**
	 * @param duration in milliseconds
	 */
	public TimeBasedMovingAverage(final Long duration) {
		this.duration = duration / 2;
	}

	/**
	 * @param dataPoint the reference point to compute the moving average from
	 * @param data      SortedSet by Timestamp of dataPoints
	 * @return the moving average value
	 */
	public Double average(final long dataMillis, final long[] times, final double[] values) {

		int nbPts = 0;
		double sum = 0;

		for (int i = 0; i < times.length; i++) {
			if (inTimeFrame(duration, dataMillis, times[i])) {
				nbPts++;
				sum += values[i];
			}
		}

		return sum / nbPts;
	}

	public Long getDuration() {
		return duration;
	}
}
