package org.mwc.debrief.track_shift.zig_detector;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


public class OwnshipLegDetector2 implements IOwnshipLegDetector
{

	private static final int MIN_OWNSHIP_LENGTH = 180000; // 3 minutes

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
	@Override
	public List<LegOfData> identifyOwnshipLegs(final long[] times,
			final double[] rawSpeeds, final double[] rawCourses,
			final int minsOfAverage, Precision precision)
	{
    final ArrayList<LegOfData> legs = new ArrayList<LegOfData>();

		// ok, see if we can find the precision
		final double COURSE_TOLERANCE;
		final double SPEED_TOLERANCE;
		
    switch (precision)
    {
    case HIGH:
      COURSE_TOLERANCE = 0.05; // degs / sec (just a guess!!)
      SPEED_TOLERANCE = 0.0005; // ms / sec (just a guess!!)
      break;
    case MEDIUM:
      COURSE_TOLERANCE = 0.08; // degs / sec (just a guess!!)
      SPEED_TOLERANCE = 0.001; // ms / sec (just a guess!!)
      break;
    case LOW:
    default:
      COURSE_TOLERANCE = 0.2; // degs / sec (just a guess!!)
      SPEED_TOLERANCE = 0.04; // ms / sec (just a guess!!)
      break;
    }		

    // 1. make continuous dataset
    double[] newCourses = new double[rawCourses.length];
    for(int i=0;i<rawCourses.length;i++)
    {
      final double thisCourse = rawCourses[i];
      
      if(i == 0)
      {
        newCourses[i] = thisCourse;
      }
      else
      {
        final double lastCourse = rawCourses[i-1];
        double thisDiff = thisCourse - lastCourse;
        if (Math.abs(thisDiff) > 180d)
        {
          // ok, we've flippped
          if(thisDiff > 180)
          {
            // ok, deduct 360
            newCourses[i] = thisCourse - 360d;
          }
          else
          {
            // ok, deduct 360
            newCourses[i] = thisCourse + 360d;
          }
        }
      }
    }
    
		return legs;
	}
	
	private double[] makeContinuous(final double[] raw)
	{
	  final double[] res = new double[raw.length];
	  
	  for(int i=0;i<raw.length;i++)
    {
      final double thisCourse = raw[i];
      
      if(i == 0)
      {
        res[i] = thisCourse;
      }
      else
      {
        final double lastCourse = res[i-1];
        double thisDiff = thisCourse - lastCourse;
        if (Math.abs(thisDiff) > 180d)
        {
          // ok, we've flippped
          if(thisDiff > 180)
          {
            // ok, deduct 360
            res[i] = thisCourse - 360d;
          }
          else
          {
            // ok, deduct 360
            res[i] = thisCourse + 360d;
          }
        }
        else
        {
          res[i] = thisCourse;
        }
      }
    }
	  
	  return res;
	}

  public static class TestCalcs extends TestCase
  {
    public void testContinuous()
    {
      OwnshipLegDetector2 tester = new OwnshipLegDetector2();
      double[] test = new double[]{160, 170,175, 180, -175, -200, -175, 180, 170, 150, 130, 110, 70, 20, 5, 355, 335};
      double[] res = tester.makeContinuous(test);
      assertEquals("correct side", 185d, res[4]);
      assertEquals("correct side", -5d, res[15]);
      System.out.println(res);
    }
  }
}
