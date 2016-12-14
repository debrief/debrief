package org.mwc.debrief.track_shift.zig_detector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class PeakTrackingOwnshipLegDetector implements IOwnshipLegDetector
{

  /**
   * slice this data into ownship legs, where the course and speed are relatively steady
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
    final long minLegLength = 120000;

    switch (precision)
    {
    case HIGH:
    case MEDIUM:
    case LOW:
    default:
      COURSE_TOLERANCE = 6; // degs
      break;
    }

    // 1. make continuous dataset
    double[] newCourses = makeContinuous(rawCourses);

    // 2. find max & min

    long thisTroughTime = 0, thisPeakTime = 0;
    long thisLegStart = -1;
    double lastTroughCourse = Double.MAX_VALUE, lastPeakCourse =
        Double.MIN_VALUE, thisTroughCourse = Double.MAX_VALUE, thisPeakCourse =
        Double.MIN_VALUE;
    double lastCourse = 0;
    int lastDir = 0;
    final long startTime = times[0];
    long lastTime = Long.MIN_VALUE;

    for (int i = 0; i < newCourses.length; i++)
    {
      // handle this step
      final long thisTime = times[i];
      final double thisCourse = newCourses[i];
      
//      if(Math.abs(thisCourse - 341.94) < 0.01)
//      {        
//        System.out.println("here at:" + new Date(thisTime));
//      }

      // do we need a leg start?
      if (thisLegStart == -1)
      {
        thisLegStart = thisTime;
      }

      if (i == 0)
      {
      }
      else
      {
        // sort out the direction of change
        final int thisDir;
        if (thisCourse > lastCourse)
        {
          thisDir = 1;
        }
        else if (thisCourse < lastCourse)
        {
          thisDir = -1;
        }
        else
        {
          thisDir = 0;
        }

        // have we changed?
        if (thisDir != lastDir && thisDir != 0)
        {
          lastDir = thisDir;

          final double delta;
          final long legEnd;

          if (thisDir == -1)
          {
            // ok, just passed peak
            lastPeakCourse = thisPeakCourse;

            thisPeakTime = lastTime;
            thisPeakCourse = lastCourse;
            legEnd = thisTroughTime;
            
            // do we already have a peak?
            if (lastPeakCourse != Double.MIN_VALUE)
            {
              // is it more than threshold?
              delta = Math.abs(thisPeakCourse - lastPeakCourse);
            }
            else
            {
              delta = 0;
            }
          }
          else
          {
            // ok, just passed peak
            lastTroughCourse = thisTroughCourse;

            thisTroughTime = lastTime;
            thisTroughCourse = lastCourse;
            legEnd = thisPeakTime;
            
            // do we already have a peak?
            if (lastTroughCourse != Double.MIN_VALUE)
            {
              // is it more than threshold?
              delta = Math.abs(thisTroughCourse - lastTroughCourse);
            }
            else
            {
              delta = 0;
            }
          }
          
          // ok, are we in a turn?
          if (delta > COURSE_TOLERANCE)
          {
            // just check the leg is long enough
            final long legLength = legEnd - thisLegStart;
            
            if(legLength >= minLegLength)
            {  
              // ok, leg ended.
              legs.add(new LegOfData("L" + legs.size() + 1, thisLegStart, legEnd));
              
//              System.out.println("Leg:" + (thisLegStart - startTime)/1000 + " (" + new Date(thisLegStart) + ") to:" + (legEnd - startTime)/1000 + "(" + new Date(legEnd) + ")");
              System.out.println("Leg:" + (thisLegStart - startTime)/1000 + " to:" + (legEnd - startTime)/1000);
            }
            
            // clear the leg marker
            thisLegStart = lastTime;

            // and a bit of clearing out
            if (thisDir == -1)
            {
              // clear the last min value, it will be wrong - since it's from the last leg
              thisTroughCourse = Double.MIN_VALUE;
            }
            else
            {
              // clear the last max value, it will be wrong - since it's from the last leg
              thisPeakCourse = Double.MIN_VALUE;
            }
          }
        }
      }
      lastCourse = thisCourse;
      lastTime = thisTime;
    }
    
    return legs;
  }

  private double[] makeContinuous(final double[] raw)
  {
    final double[] res = new double[raw.length];

    for (int i = 0; i < raw.length; i++)
    {
      final double thisCourse = raw[i];

      if (i == 0)
      {
        res[i] = thisCourse;
      }
      else
      {
        final double lastCourse = res[i - 1];
        double thisDiff = thisCourse - lastCourse;
        if (Math.abs(thisDiff) > 180d)
        {
          // ok, we've flippped
          if (thisDiff > 180)
          {
            // ok, deduct 360
            res[i] = thisCourse - 360d;
          }
          else
          {
            // ok, add 360
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
      PeakTrackingOwnshipLegDetector tester =
          new PeakTrackingOwnshipLegDetector();
      double[] test =
          new double[]
          {160, 170, 175, 180, -175, -200, -175, 180, 170, 150, 130, 110, 70,
              20, 5, 355, 335};
      double[] res = tester.makeContinuous(test);
      assertEquals("correct side", 185d, res[4]);
      assertEquals("correct side", -5d, res[15]);
      System.out.println(res);
    }
  }
}
