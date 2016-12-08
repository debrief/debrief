package org.mwc.debrief.track_shift.zig_detector;

import java.util.ArrayList;
import java.util.List;


public class CumulativeLegDetector
{

	//private static final int MIN_OWNSHIP_LENGTH = 180000; // 3 minutes

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
	public List<LegOfData> identifyOwnshipLegs(final long[] times,
			final double[] rawSpeeds, final double[] rawCourses,
			final int minsOfAverage, Precision precision)
	{

		// ok, see if we can find the precision
//		final double COURSE_TOLERANCE;
//		final double SPEED_TOLERANCE;
//		
//    switch (precision)
//    {
//    case HIGH:
//      COURSE_TOLERANCE = 0.05; // degs / sec (just a guess!!)
//      SPEED_TOLERANCE = 0.0005; // ms / sec (just a guess!!)
//      break;
//    case MEDIUM:
//      COURSE_TOLERANCE = 0.08; // degs / sec (just a guess!!)
//      SPEED_TOLERANCE = 0.001; // ms / sec (just a guess!!)
//      break;
//    case LOW:
//    default:
//      COURSE_TOLERANCE = 0.2; // degs / sec (just a guess!!)
//      SPEED_TOLERANCE = 0.04; // ms / sec (just a guess!!)
//      break;
//    }		

		double lastCourse = 0;
//		double lastSpeed = 0;
		long lastTime = 0;
	//	int avgPeriod = 0;
		
		final double areaUnderTurn = 40;  // deg secs
		

		// NOTE: we have to handle high and low data-rate ownship tracks, from one second
		// to one minute time intervals.
		// So, we determine how many time intervals are necessary to provide the 
		// supplied minsOfAverage data.  But, we also trim this a little,
		// so that we can still recognise turns.
		
		final List<LegOfData> legs = new ArrayList<LegOfData>();
		legs.add(new LegOfData("Leg-1"));

		// switch the courses to an n-term moving average
//		final double[] courses = movingAverage(rawCourses, avgPeriod);
//		final double[] speeds = movingAverage(rawSpeeds, avgPeriod);
//		
	//	TimeBasedMovingAverage tbm5 = new TimeBasedMovingAverage(5 * 60 * 1000L);
		
		Integer lastSign = null;
		double runningArea = 0;
		long lastFlip = -1;
		
		for (int i = 0; i < times.length; i++)
		{
			final long thisTime = times[i];

//			final double thisSpeed = speeds[i];
			double thisCourse = rawCourses[i];

			if (i > 0)
			{
				// here is our time-based averageing algorithm
			//	final double newCourseAvg5 = tbm5.average(thisTime, times, rawCourses);

				// decide which value to use as average
	//			thisCourse = newCourseAvg5;
				
				// ok, check out the course change rate
				final double timeStepSecs = (thisTime - lastTime) / 1000d;
				final double courseRate = (thisCourse - lastCourse)
						/ timeStepSecs;
	//			final double speedRate = Math.abs(thisSpeed - lastSpeed) / timeStepSecs;

				// ok, find the sign of the course
				final int thisSign = (int) Math.signum(courseRate);
				
				if(lastSign != null && thisSign != lastSign)
				{
				  System.out.println("turning,area was:" + (int)runningArea);
				  
				  // ok, course change
				  runningArea = 0;
				  
				  // remember this time
				  lastFlip = thisTime;
				  
				  // is the current leg initialised?
				  if(legs.get(legs.size() - 1).initialised())
				  {
				    // ok.
				  }
				  else
				  {
				    // not initialised. this is the new leg then :-)
				    legs.get(legs.size() - 1).add(thisTime);
				  }
				}
				else
				{
				  // carry on builing up the area under the graph
				  runningArea += timeStepSecs * Math.abs(courseRate);
				  
				  if(courseRate > 0) 
				  {
				    System.out.println("motion");
				  }
				  
				  // have we built up enough to signify a course change?
				  // and, do we have a properly initialised leg?
				  if(runningArea > areaUnderTurn && legs.get(legs.size()-1).initialised())
				  {
				    // ok, we've formally entered a zig. the last leg is complete
				    
				    // the last leg finished at the last flip
	          legs.get(legs.size() - 1).add(lastFlip);
	          
	          // and start the new leg
            legs.add(new LegOfData("Leg-" + (legs.size() + 1)));
				  }
				  
				}
				
	      // ok, store the values
	      lastSign = thisSign;
			}

      lastTime = thisTime;
      lastCourse = thisCourse;
		//	lastSpeed = thisSpeed;

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
//	private double[] movingAverage(final double[] measurements, final int period)
//	{
//		final double[] res = new double[measurements.length];
//		final CenteredMovingAverage ma = new CenteredMovingAverage(period);
//		for (int j = 0; j < measurements.length; j++)
//		{
//			res[j] = ma.average(j, measurements);
//		}
//		return res;
//	}

}
