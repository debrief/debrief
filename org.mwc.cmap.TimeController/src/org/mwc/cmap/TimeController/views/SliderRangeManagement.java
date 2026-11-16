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

package org.mwc.cmap.TimeController.views;


import MWC.GenericData.HiResDate;

abstract class SliderRangeManagement {

	private static final long DAY_STEP = 24 * 60 * 60000000L;

	private static final long HOUR_STEP = 60 * 60000000L;

	static final long MINUTE_STEP = 60000000;

	private static final int SECOND_STEP = 1000000;

	private static final int MICRO_STEP = 1;

	/**
	 * the start time - used as an offset
	 */
	private HiResDate _startTime;

	// only let slider work in micros if there is under a second of data
	private final int TIME_SPAN_TO_USE_MICROS = 1000000;

	/**
	 * how we convert a screen drag pixel to a time jump
	 *
	 */
	long _timeScalar = 1;

	public HiResDate fromSliderUnits(final int value, final long sliderResolution) {
		long newValue = value;

		// convert the resolution to micros
		final long sliderResolutionMicros = sliderResolution * 1000;

		// convert reading to real time units
		newValue *= _timeScalar;

		// re-apply the offset
		long newDate = _startTime.getMicros() + newValue;

		// and trim the resulting value
		newDate = (newDate / sliderResolutionMicros) * sliderResolutionMicros;

		return new HiResDate(0, newDate);
	}

	public void resetRange(final HiResDate min, final HiResDate max) {
		if ((min != null) && (max != null)) {
			// ok - store the start time
			_startTime = min;

			// yes - initialise the ranges
			final long range = max.getMicros() - min.getMicros();

			final int maxVal;

			if (range > 0) {
				// double-check the min value
				setMinVal(0);

				if (range < TIME_SPAN_TO_USE_MICROS) {
					maxVal = (int) range;
					setMaxVal(maxVal);
					setEnabled(true);
					_timeScalar = MICRO_STEP;
				} else {
					final long rangeMillis = range / 1000;
					final long rangeSecs = rangeMillis / 1000;
					final int maxSliderSteps = 100000;
					if (rangeSecs < maxSliderSteps) {
						// ok, we're going to run in second resolution
						maxVal = (int) rangeSecs;
						setMaxVal(maxVal);
						_timeScalar = SECOND_STEP;
						setEnabled(true);
					} else {
						// let's run in minute resolution
						final long rangeMins = rangeSecs / 60;
						if (rangeMins < maxSliderSteps) {
							// ok, we're going to run in minute resolution
							maxVal = (int) rangeMins;
							setMaxVal(maxVal);
							_timeScalar = MINUTE_STEP;
							setEnabled(true);
						} else {
							// let's run in hour resolution
							final long rangeHours = rangeMins / 60;
							if (rangeHours < maxSliderSteps) {
								// ok, we're going to run in minute resolution
								maxVal = (int) rangeHours;
								setMaxVal(maxVal);
								_timeScalar = HOUR_STEP;
								setEnabled(true);
							} else {
								// let's run in day resolution
								final long rangDays = rangeHours / 24;
								if (rangDays < maxSliderSteps) {
									// ok, we're going to run in minute resolution
									maxVal = (int) rangDays;
									setMaxVal(maxVal);
									_timeScalar = DAY_STEP;
									setEnabled(true);
								} else {
									// hey, we must be running in units which are too large.
									System.err.println("Can't show this time period - too long");
									setEnabled(false);
									return;
								}
							}
						}
					}
				}

				// ok. just sort out the step size when the user clicks on the slider
				int smallTick = 5;
				int largeTick = 5;
				final int dragSize = 0;

				if (_timeScalar == MICRO_STEP) {
					smallTick = 1;
					largeTick = 1000;
				} else if (_timeScalar == SECOND_STEP) {
					smallTick = 1; // one second
					largeTick = 60; // one minute
				} else if (_timeScalar == MINUTE_STEP) {
					smallTick = 1; // one minute
					largeTick = 60; // one hour
				} else if (_timeScalar == HOUR_STEP) {
					smallTick = 1; // one hour
					largeTick = 24; // one day
				} else if (_timeScalar == DAY_STEP) {
					smallTick = 1; // one day
					largeTick = 7; // one week
				} else
					throw new RuntimeException("Failed to determine correct step size");

				// hmm, try to trim down the size of the large ticks, we don't want too
				// many do we?
				while (largeTick < (maxVal / 50)) {
					largeTick *= 10;
				}

				setTickSize(smallTick, largeTick, dragSize);

				// ok, we've finished updating the form. back to normal processing
				// _updatingForm = false;
			}
		}
	}

	public abstract void setEnabled(boolean val);

	public abstract void setMaxVal(int max);

	public abstract void setMinVal(int min);

	public abstract void setTickSize(int small, int large, int drag);

	public int toSliderUnits(final HiResDate now) {
		int res = -1;

		// do we know our start time?
		if (_startTime != null) {
			long offset = now.getMicros() - _startTime.getMicros();

			offset /= _timeScalar;

			res = (int) offset;
		}

		return res;

	}
}