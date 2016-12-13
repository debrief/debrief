package org.mwc.debrief.track_shift.zig_detector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class PeakTrackingOwnshipLegDetector implements IOwnshipLegDetector
{

  private static final int MIN_OWNSHIP_LENGTH = 180000; // 3 minutes

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

    long thisMinT = 0, thisMaxT = 0;
    long thisLegStart = -1;
    double lastMinC = Double.MAX_VALUE, lastMaxC = Double.MIN_VALUE, thisMinC =
        Double.MAX_VALUE, thisMaxC = Double.MIN_VALUE;
    double lastC = 0;
    int lastDir = 0;

    for (int i = 0; i < newCourses.length; i++)
    {
      // handle this step
      final long thisT = times[i];
      final double thisC = newCourses[i];

      // do we need a leg start?
      if (thisLegStart == -1)
      {
        thisLegStart = thisT;
      }

      if (i == 0)
      {
        // special handling, initialise some stuff
        thisMinT = thisT;
        thisMinC = thisC;
      }
      else
      {
        final int thisDir;

        if (thisC > lastC)
        {
          thisDir = 1;
        }
        else if (thisC < lastC)
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
          
          if (thisDir == -1)
          {
            // ok, just passed peak
            lastMaxC = thisMaxC;

            thisMaxT = thisT;
            thisMaxC = thisC;

            // do we already have a peak?
            if (lastMaxC != Double.MIN_VALUE)
            {
              // is it more than threshold?
              final double delta = Math.abs(thisMaxC - lastMaxC);

              if (delta > COURSE_TOLERANCE)
              {
                // ok, leg ended.

                legs.add(new LegOfData("L" + legs.size() + 1, thisLegStart,
                    thisMinT));

                // clear the leg marker
                thisLegStart = -1;

                // clear the last min value, it will be wrong - since it's from the last leg
                thisMinC = Double.MIN_VALUE;
              }
            }
          }
          else if (thisDir == 1)
          {
            // ok, just passed peak
            lastMinC = thisMinC;

            thisMinT = thisT;
            thisMinC = thisC;

            // do we already have a peak?
            if (lastMinC != Double.MIN_VALUE)
            {
              // is it more than threshold?
              final double delta = Math.abs(thisMinC - lastMinC);

              if (delta > COURSE_TOLERANCE)
              {

                legs.add(new LegOfData("L" + legs.size() + 1, thisLegStart,
                    thisMaxT));
                // clear the leg marker
                thisLegStart = -1;

                // clear the last max value, it will be wrong - since it's from the last leg
                thisMaxC = Double.MIN_VALUE;
                // ok, leg ended.
              }
            }
          }
        }
      }

      lastC = thisC;
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
