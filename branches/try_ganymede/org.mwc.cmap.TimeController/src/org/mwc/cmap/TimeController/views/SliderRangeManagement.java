/**
 * 
 */
package org.mwc.cmap.TimeController.views;

import MWC.GenericData.HiResDate;

abstract class SliderRangeManagement
{

	/**
	 * the start time - used as an offset
	 */
	private HiResDate _startTime;

	// only let slider work in micros if there is under a second of data
	private final int TIME_SPAN_TO_USE_MICROS = 1000000;

	private boolean _useMicros = false;

	public abstract void setMinVal(int min);

	public abstract void setMaxVal(int max);

	public abstract void setTickSize(int small, int large, int drag);

	public abstract void setEnabled(boolean val);

	public void resetRange(HiResDate min, HiResDate max)
	{
		if ((min != null) && (max != null))
		{
			// ok - store the start time
			_startTime = min;

			// yes - initialise the ranges
			long range = max.getMicros() - min.getMicros();

			int maxVal = 100;

			if (range > 0)
			{
				// double-check the min value
				setMinVal(0);

				if (range < TIME_SPAN_TO_USE_MICROS)
				{
					maxVal = (int) range;
					setMaxVal(maxVal);
					setEnabled(true);
					_useMicros = true;
				}
				else
				{
					long rangeMillis = range / 1000;
					long rangeSecs = rangeMillis / 1000;
					if (rangeMillis < Integer.MAX_VALUE)
					{
						// ok, we're going to run in millisecond resolution
						maxVal = (int) rangeSecs;
						setMaxVal(maxVal);
						_useMicros = false;
						setEnabled(true);
					}
					else
					{
						// hey, we must be running in units which are too large.
						setEnabled(false);
					}
				}

				// ok. just sort out the step size when the user clicks on the slider
				int smallTick;
				int largeTick;
				int dragSize;
				int NUM_MILLIS_FOR_STEP;
				if (_useMicros)
				{
					dragSize = 500; // 500 microseconds
					NUM_MILLIS_FOR_STEP = dragSize * 20; // 10 millis
				}
				else
				{
					dragSize = 1; // one second
					NUM_MILLIS_FOR_STEP = dragSize * 60; // one minute
				}
				smallTick = NUM_MILLIS_FOR_STEP;
				largeTick = smallTick * 10;

				// hmm, try to trim down the size of the large ticks, we don't want too
				// many do we?
				while (largeTick < (maxVal / 50))
				{
					largeTick *= 10;
				}

				setTickSize(smallTick, largeTick, dragSize);

				// ok, we've finished updating the form. back to normal processing
				// _updatingForm = false;
			}
		}
	}

	public int toSliderUnits(HiResDate now)
	{
		int res = -1;
		
		// do we know our start time?
		if (_startTime != null)
		{
			long offset = now.getMicros() - _startTime.getMicros();

			if (!_useMicros)
			{
				offset /= 1000000;
			}

			res = (int) offset;
		}
		
		return res;

	}

	public HiResDate fromSliderUnits(int value, long sliderResolution)
	{
		long newValue = value;

		// convert the resolution to micros
		sliderResolution *= 1000;

		if (_useMicros)
		{
		}
		else
		{
			// convert reading to microseconds
			newValue *= 1000000;
		}

		// re-apply the offset
		long newDate = _startTime.getMicros() + newValue;

		// and trim the resulting value
		newDate = (newDate / sliderResolution) * sliderResolution;

		return new HiResDate(0, newDate);
	}
}