package com.planetmayo.debrief.satc.zigdetector.TimeWindow.average;


/**
 * Created by Romain on 09/05/2015.
 */
public class TimeBasedMovingAverage
{

	/**
	 * how far either side of the specified time we include data values
	 * 
	 */
	private final Long duration;

	/**
	 * @param duration
	 *          in milliseconds
	 */
	public TimeBasedMovingAverage(Long duration)
	{
		this.duration = duration / 2;
	}

	public Long getDuration()
	{
		return duration;
	}

	/**
	 * @param dataPoint
	 *          the reference point to compute the moving average from
	 * @param data
	 *          SortedSet by Timestamp of dataPoints
	 * @return the moving average value
	 */
	public Double average(final long dataMillis, long[] times, double[] values)
	{

		int nbPts = 0;
		double sum = 0;

		for(int i=0;i<times.length;i++)
		{
			if (inTimeFrame(duration, dataMillis, times[i]))
			{
				nbPts++;
				sum += values[i];
			}
		}

		return sum / nbPts;
	}

	public static Boolean inTimeFrame(Long duration, long refMillis, long candidateMillis)
	{
		final Long distance = refMillis
				- candidateMillis;
		return Math.abs(distance) <= Math.abs(duration);
	}
}
