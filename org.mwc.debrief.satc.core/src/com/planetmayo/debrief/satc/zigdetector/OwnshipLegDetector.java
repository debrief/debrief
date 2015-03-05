package com.planetmayo.debrief.satc.zigdetector;

import java.util.ArrayList;
import java.util.List;

public class OwnshipLegDetector
{

	/**
	 * slice this data into ownship legs, where the course and speed are
	 * relatively steady
	 * 
	 * @param course_degs
	 * @param speed
	 * @param bearings
	 * @param elapsedTimess
	 * @return
	 */
	public List<LegOfData> identifyOwnshipLegs(final long[] times, final double[] rawSpeeds, final double[] rawCourses,
			final int avgPeriod)
	{
	
		final double COURSE_TOLERANCE = 0.1; // degs / sec (just a guess!!)
		final double SPEED_TOLERANCE = 0.01; // knots / sec (just a guess!!)
	
		double lastCourse = 0;
		double lastSpeed = 0;
		long lastTime = 0;
	
		final List<LegOfData> legs = new ArrayList<LegOfData>();
		legs.add(new LegOfData("Leg-1"));
	
		// switch the courses to an n-term moving average
		final double[] courses = movingAverage(rawCourses, avgPeriod);
//		track.averageCourses = courses;
	
		final double[] speeds = movingAverage(rawSpeeds, avgPeriod);
//		track.averageSpeeds = speeds;
	
		for (int i = 0; i < times.length; i++)
		{
			final long thisTime = times[i];
	
			final double thisSpeed = speeds[i];
			final double thisCourse = courses[i];
	
			if (i > 0)
			{
				// ok, check out the course change rate
				final double timeStepSecs = (thisTime - lastTime) / 1000;
				final double courseRate = Math.abs(thisCourse - lastCourse)
						/ timeStepSecs;
				final double speedRate = Math.abs(thisSpeed - lastSpeed) / timeStepSecs;
	
				// are they out of range
				if ((courseRate < COURSE_TOLERANCE) && (speedRate < SPEED_TOLERANCE))
				{
					// ok, we're on a new leg - drop the current one
					legs.get(legs.size() - 1).add(thisTime);
				}
				else
				{
					// we may be in a turn. create a new leg, if we haven't done
					// so already
					if (legs.get(legs.size() - 1).initialised())
					{
						legs.add(new LegOfData("Leg-" + (legs.size() + 1)));
					}
				}
			}
	
			// ok, store the values
			lastTime = thisTime;
			lastCourse = thisCourse;
			lastSpeed = thisSpeed;
	
		}
	
		return legs;
	}
	
	/**
	 * create a moving average over the set of dat measurements
	 * 
	 * @param measurements
	 * @param period
	 * @return
	 */
	private double[] movingAverage(final double[] measurements,
			final int period)
	{
		final double[] res = new double[measurements.length];
		final CenteredMovingAverage ma = new CenteredMovingAverage(period);
		for (int j = 0; j < measurements.length; j++)
		{
			res[j] = ma.average(j, measurements);
		}
		return res;
	}

}
