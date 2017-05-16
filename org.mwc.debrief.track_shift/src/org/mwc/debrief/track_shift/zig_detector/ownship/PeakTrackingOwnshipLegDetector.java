package org.mwc.debrief.track_shift.zig_detector.ownship;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.mwc.debrief.track_shift.Activator;
import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;

public class PeakTrackingOwnshipLegDetector implements IOwnshipLegDetector
{

  public static class TestCalcs extends TestCase
  {
    private TimeSeries getOSValues()
    {
      final TimeSeries tmpSeries = new TimeSeries("Set piece");
      tmpSeries.add(new FixedMillisecond(946697490000L), 60.20000000000001);
      tmpSeries.add(new FixedMillisecond(946697520000L), 64.4);
      tmpSeries.add(new FixedMillisecond(946697550000L), 97.2);
      tmpSeries.add(new FixedMillisecond(946697580000L), 106.1);
      tmpSeries.add(new FixedMillisecond(946697610000L), 105.2);
      tmpSeries.add(new FixedMillisecond(946697640000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946697670000L), 104.9);
      tmpSeries.add(new FixedMillisecond(946697700000L), 105.2);
      tmpSeries.add(new FixedMillisecond(946697730000L), 105.4);
      tmpSeries.add(new FixedMillisecond(946697760000L), 105.2);
      tmpSeries.add(new FixedMillisecond(946697790000L), 105.2);
      tmpSeries.add(new FixedMillisecond(946697820000L), 105.0);
      tmpSeries.add(new FixedMillisecond(946697850000L), 105.3);
      tmpSeries.add(new FixedMillisecond(946697880000L), 105.3);
      tmpSeries.add(new FixedMillisecond(946697910000L), 105.2);
      tmpSeries.add(new FixedMillisecond(946697940000L), 105.3);
      tmpSeries.add(new FixedMillisecond(946697970000L), 105.2);
      tmpSeries.add(new FixedMillisecond(946698000000L), 105.4);
      tmpSeries.add(new FixedMillisecond(946698030000L), 105.4);
      tmpSeries.add(new FixedMillisecond(946698060000L), 104.7);
      tmpSeries.add(new FixedMillisecond(946698090000L), 105.5);
      tmpSeries.add(new FixedMillisecond(946698120000L), 105.4);
      tmpSeries.add(new FixedMillisecond(946698150000L), 104.9);
      tmpSeries.add(new FixedMillisecond(946698180000L), 105.4);
      tmpSeries.add(new FixedMillisecond(946698210000L), 105.4);
      tmpSeries.add(new FixedMillisecond(946698240000L), 104.8);
      tmpSeries.add(new FixedMillisecond(946698270000L), 105.7);
      tmpSeries.add(new FixedMillisecond(946698300000L), 105.2);
      tmpSeries.add(new FixedMillisecond(946698330000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946698360000L), 105.3);
      tmpSeries.add(new FixedMillisecond(946698390000L), 105.5);
      tmpSeries.add(new FixedMillisecond(946698420000L), 104.8);
      tmpSeries.add(new FixedMillisecond(946698450000L), 105.3);
      tmpSeries.add(new FixedMillisecond(946698480000L), 105.3);
      tmpSeries.add(new FixedMillisecond(946698510000L), 105.2);
      tmpSeries.add(new FixedMillisecond(946698540000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946698570000L), 105.5);
      tmpSeries.add(new FixedMillisecond(946698600000L), 105.6);
      tmpSeries.add(new FixedMillisecond(946698630000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946698660000L), 105.4);
      tmpSeries.add(new FixedMillisecond(946698690000L), 105.0);
      tmpSeries.add(new FixedMillisecond(946698720000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946698750000L), 105.4);
      tmpSeries.add(new FixedMillisecond(946698780000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946698810000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946698840000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946698870000L), 105.3);
      tmpSeries.add(new FixedMillisecond(946698900000L), 105.1);
      tmpSeries.add(new FixedMillisecond(946698930000L), 105.0);
      tmpSeries.add(new FixedMillisecond(946698960000L), 105.3);
      tmpSeries.add(new FixedMillisecond(946698990000L), 105.0);
      tmpSeries.add(new FixedMillisecond(946699020000L), 107.6);
      tmpSeries.add(new FixedMillisecond(946699050000L), 128.9);
      tmpSeries.add(new FixedMillisecond(946699080000L), 133.6);
      tmpSeries.add(new FixedMillisecond(946699110000L), 135.6);
      tmpSeries.add(new FixedMillisecond(946699140000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946699170000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946699200000L), 135.6);
      tmpSeries.add(new FixedMillisecond(946699230000L), 134.8);
      tmpSeries.add(new FixedMillisecond(946699260000L), 135.6);
      tmpSeries.add(new FixedMillisecond(946699290000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946699320000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946699350000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946699380000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946699410000L), 135.0);
      tmpSeries.add(new FixedMillisecond(946699440000L), 135.1);
      tmpSeries.add(new FixedMillisecond(946699470000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946699500000L), 135.0);
      tmpSeries.add(new FixedMillisecond(946699530000L), 135.0);
      tmpSeries.add(new FixedMillisecond(946699560000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946699590000L), 134.9);
      tmpSeries.add(new FixedMillisecond(946699620000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946699650000L), 135.7);
      tmpSeries.add(new FixedMillisecond(946699680000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946699710000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946699740000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946699770000L), 135.5);
      tmpSeries.add(new FixedMillisecond(946699800000L), 135.1);
      tmpSeries.add(new FixedMillisecond(946699830000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946699860000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946699890000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946699920000L), 135.7);
      tmpSeries.add(new FixedMillisecond(946699950000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946699980000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946700010000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946700040000L), 135.0);
      tmpSeries.add(new FixedMillisecond(946700070000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946700100000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946700130000L), 135.5);
      tmpSeries.add(new FixedMillisecond(946700160000L), 135.0);
      tmpSeries.add(new FixedMillisecond(946700190000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946700220000L), 135.6);
      tmpSeries.add(new FixedMillisecond(946700250000L), 134.9);
      tmpSeries.add(new FixedMillisecond(946700280000L), 135.1);
      tmpSeries.add(new FixedMillisecond(946700310000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946700340000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946700370000L), 135.6);
      tmpSeries.add(new FixedMillisecond(946700400000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946700430000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946700460000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946700490000L), 135.1);
      tmpSeries.add(new FixedMillisecond(946700520000L), 135.1);
      tmpSeries.add(new FixedMillisecond(946700550000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946700580000L), 135.1);
      tmpSeries.add(new FixedMillisecond(946700610000L), 134.8);
      tmpSeries.add(new FixedMillisecond(946700640000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946700670000L), 135.5);
      tmpSeries.add(new FixedMillisecond(946700700000L), 135.3);
      tmpSeries.add(new FixedMillisecond(946700730000L), 135.5);
      tmpSeries.add(new FixedMillisecond(946700760000L), 135.4);
      tmpSeries.add(new FixedMillisecond(946700790000L), 135.1);
      tmpSeries.add(new FixedMillisecond(946700820000L), 135.5);
      tmpSeries.add(new FixedMillisecond(946700850000L), 135.2);
      tmpSeries.add(new FixedMillisecond(946700880000L), 135.1);
      tmpSeries.add(new FixedMillisecond(946700910000L), 135.0);
      tmpSeries.add(new FixedMillisecond(946700940000L), 136.0);
      tmpSeries.add(new FixedMillisecond(946700970000L), 136.7);
      return tmpSeries;
    }

    public void testContinuous()
    {
      final double[] test =
          new double[]
          {160, 170, 175, 180, -175, -200, -175, 180, 170, 150, 130, 110, 70,
              20, 5, 355, 335};
      final double[] res = PeakTrackingOwnshipLegDetector.makeContinuous(test);
      assertEquals("correct side", 185d, res[4]);
      assertEquals("correct side", -5d, res[15]);
      System.out.println(res);
    }

    public void testIsHalfLeg()
    {
      final double[] legIds1 = new double[]
      {0.5, 0.5, 0.5, 1d, 1d, 1d, 1.5, 1.5};
      final double[] legIds2 = new double[]
      {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
      final double[] courseDeltas1 = new double[]
      {6, 6, 6, 1, 1, 1, 5, 5, 5};
      final double[] courseDeltas2 = new double[]
      {1, 1, 1, 5, 5, 5, 1, 1, 1};

      System.out.println("started");

      assertFalse("right answer", PeakTrackingOwnshipLegDetector.isHalfLeg(
          legIds1, courseDeltas1));
      assertTrue("right answer", PeakTrackingOwnshipLegDetector.isHalfLeg(
          legIds1, courseDeltas2));
      assertTrue("right answer", PeakTrackingOwnshipLegDetector.isHalfLeg(
          legIds2, courseDeltas2));
    }

    public void testSetPiece()
    {
      final TimeSeries tmpSeries = getOSValues();

      final PeakTrackingOwnshipLegDetector detector =
          new PeakTrackingOwnshipLegDetector();
      final int len = tmpSeries.getItemCount();
      final long[] times = new long[len];
      final double[] speeds = new double[len];
      final double[] courses = new double[len];

      // long start = tmpSeries.getDataItem(0).getPeriod().getFirstMillisecond();

      for (int i = 0; i < len; i++)
      {
        times[i] = tmpSeries.getDataItem(i).getPeriod().getFirstMillisecond();
        speeds[i] = 0d;
        courses[i] = (Double) tmpSeries.getDataItem(i).getValue();
      }

      final List<LegOfData> legs =
          detector
              .identifyOwnshipLegs(times, speeds, courses, 5, Precision.LOW);

      assertEquals("got right num legs:", 2, legs.size());
      assertEquals("got leg 1 start time right", 946697580000L,
          legs.get(0).tStart);
      assertEquals("got leg 1 end time right", 946699020000L, legs.get(0).tEnd);
      assertEquals("got leg 2 start time right", 946699110000L,
          legs.get(1).tStart);
      assertEquals("got leg 2 end time right", 946700970000L, legs.get(1).tEnd);
    }
  }

  private static final long MIN_LEG_LENGTH = 120000L;

  /**
   * see if 0.5 represents straight legs or zigs
   * 
   * @param legId
   * @param courseDeltas
   * @return
   */
  private static boolean isHalfLeg(final double[] legId,
      final double[] courseDeltas)
  {
    double halfTotal = 0;
    int halfCount = 0;
    double wholeTotal = 0;
    int wholeCount = 0;
    for (int i = 0; i < legId.length; i++)
    {
      final double thisDelta = Math.abs(courseDeltas[i]);
      if (legId[i] % 1 == 0)
      {
        wholeTotal += thisDelta;
        wholeCount++;
      }
      else
      {
        halfTotal += thisDelta;
        halfCount++;
      }
    }

    final double meanWhole = wholeTotal / wholeCount;
    final double meanHalf = halfTotal / halfCount;

    if (Double.isNaN(meanWhole) || Double.isNaN(meanHalf))
    {
      final Activator def = Activator.getDefault();
      if (def != null)
      {
        def.getLog()
            .log(
                new Status(
                    IStatus.WARNING,
                    "Unable to accurately determine if OS Leg is on 0.5 or 1.0 marker",
                    null));
      }
      else
      {
        System.err
            .println("Unable to accurately determine if OS Leg is on 0.5 or 1.0 marker");
      }
    }

    final boolean res;
    if (meanWhole < meanHalf)
    {
      res = false;
    }
    else
    {
      res = true;
    }

    return res;
  }

  public static double[] makeContinuous(final double[] raw)
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
        final double thisDiff = thisCourse - lastCourse;
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

  private double[] getChanges(final double[] courseDeltas)
  {
    final int len = courseDeltas.length;
    final double[] courseChanges = new double[len];
    for (int i = 0; i < len; i++)
    {
      // look forward
      final double thisDelta = courseDeltas[i];
      if (thisDelta > 0)
      {
        courseChanges[i] = 1;
      }
      else
      {
        courseChanges[i] = -1;
      }
    }
    return courseChanges;
  }

  private double[] getDeltas(final double[] courses)
  {
    final int len = courses.length;
    final double[] courseDeltas = new double[len];
    for (int i = 0; i < len; i++)
    {
      // look forward
      if (i < len - 1)
      {
        final double thisCourse = courses[i];
        final double nextCourse = courses[i + 1];
        courseDeltas[i] = nextCourse - thisCourse;
      }
      else
      {
        courseDeltas[i] = 0;
      }
    }
    return courseDeltas;
  }

  private double[] getDiff(final double[] downValues, final double[] upValues)
  {
    final int len = downValues.length;
    final double[] diff = new double[len];
    for (int i = 0; i < len; i++)
    {
      diff[i] = Math.abs(downValues[i] - upValues[i]);
    }
    return diff;
  }

  private double[] getDownMax(final double[] peaks, final double[] courses,
      final double maxCourse)
  {
    final int len = peaks.length;
    final double[] downMax = new double[len];
    for (int i = 0; i < len; i++)
    {
      if (i < len - 1)
      {
        if (peaks[i] == 1)
        {
          // ok, min point, take the course
          downMax[i] = courses[i];
        }
        else
        {
          if (i > 0)
          {
            // nope, use the previous
            downMax[i] = downMax[i - 1];
          }
          else
          {
            downMax[i] = maxCourse;
          }
        }
      }
      else
      {
        // populate with min/max values
        downMax[i] = maxCourse;
      }
    }
    return downMax;
  }

  private double[] getDownMin(final double[] peaks, final double[] courses,
      final double minCourse)
  {
    final int len = peaks.length;
    final double[] downMin = new double[len];
    for (int i = 0; i < len; i++)
    {
      if (peaks[i] == -1)
      {
        // ok, min point, take the course
        downMin[i] = courses[i];
      }
      else
      {
        if (i > 0)
        {
          // nope, use the previous
          downMin[i] = downMin[i - 1];
        }
        else
        {
          // use the minimum
          downMin[i] = minCourse;
        }
      }
    }
    return downMin;
  }

  private double[] getLegIds(final boolean[] overThreshold)
  {
    final int len = overThreshold.length;
    final double[] overT = new double[len];
    for (int i = 0; i < len; i++)
    {
      final double thisLeg;
      if (i == 0)
      {
        thisLeg = 0.5;
      }
      else
      {
        final double lastLeg = overT[i - 1];
        if (overThreshold[i] != overThreshold[i - 1])
        {
          thisLeg = lastLeg + 0.5;
        }
        else
        {
          thisLeg = lastLeg;
        }
      }

      overT[i] = thisLeg;
    }
    return overT;
  }

  private boolean[] getOverThreshold(final double[] diffMin,
      final double[] diffMax, final double tolerance)
  {
    final int len = diffMin.length;
    final boolean[] overT = new boolean[len];
    for (int i = 0; i < len; i++)
    {
      overT[i] = diffMin[i] > tolerance && diffMax[i] > tolerance;
    }
    return overT;
  }

  private double[] getPeaks(final double[] courseDeltas)
  {
    final int len = courseDeltas.length;
    final double[] peaks = new double[len];
    for (int i = 0; i < len; i++)
    {
      final double thisPeak;
      if (i == 0)
      {
        thisPeak = 0;
      }
      else
      {
        final double thisDelta = courseDeltas[i];
        final double lastDelta = courseDeltas[i - 1];
        if (thisDelta != lastDelta)
        {
          if (thisDelta > 0 && lastDelta < 0)
          {
            thisPeak = -1;
          }
          else
          {
            thisPeak = 1;
          }
        }
        else
        {
          thisPeak = 0;
        }
      }
      peaks[i] = thisPeak;
    }
    return peaks;
  }

  private double[] getUpMax(final double[] peaks, final double[] courses,
      final double maxCourse)
  {
    final int len = peaks.length;
    final double[] upMax = new double[len];
    for (int i = len - 1; i >= 0; i--)
    {
      if (i == len - 1)
      {
        upMax[i] = maxCourse;
      }
      else
      {
        if (peaks[i] == 1)
        {
          // ok, min point, take the course
          upMax[i] = courses[i + 1];
        }
        else
        {
          // nope, use the next
          upMax[i] = upMax[i + 1];
        }
      }
    }
    return upMax;
  }

  private double[] getUpMin(final double[] peaks, final double[] courses,
      final double minCourse)
  {
    final int len = peaks.length;
    final double[] upMin = new double[len];
    for (int i = len - 1; i >= 0; i--)
    {
      if (i == len - 1)
      {
        upMin[i] = minCourse;
      }
      else
      {
        if (peaks[i] == -1)
        {
          // ok, min point, take the course
          upMin[i] = courses[i + 1];
        }
        else
        {
          // nope, use the next
          upMin[i] = upMin[i + 1];
        }
      }
    }
    return upMin;
  }

  public List<LegOfData> identifyOwnshipLegs(final long[] times,
      final double[] rawSpeeds, final double[] rawCourses,
      final int minsOfAverage, final Precision precision)
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
    final double[] courses = makeContinuous(rawCourses);
    final int len = courses.length;

    // collate data
    double minCourse = Double.MAX_VALUE;
    double maxCourse = Double.MIN_VALUE;

    // down first
    for (int i = 0; i < len; i++)
    {
      final double thisC = courses[i];
      if (thisC < minCourse)
      {
        minCourse = thisC;
      }
      if (thisC > maxCourse)
      {
        maxCourse = thisC;
      }
    }

    // collate the course changes
    final double[] courseDeltas = getDeltas(courses);
    final double[] courseChanges = getChanges(courseDeltas);
    final double[] peaks = getPeaks(courseChanges);

    final double[] downMin = getDownMin(peaks, courses, minCourse);
    final double[] downMax = getDownMax(peaks, courses, maxCourse);
    final double[] upMin = getUpMin(peaks, courses, minCourse);
    final double[] upMax = getUpMax(peaks, courses, maxCourse);

    // right, now diff them
    final double[] diffMin = getDiff(downMin, upMin);
    final double[] diffMax = getDiff(downMax, upMax);

    final boolean[] overThreshold =
        getOverThreshold(diffMin, diffMax, COURSE_TOLERANCE);

    final double[] legId = getLegIds(overThreshold);

    // find out if 0.5 marks straight legs or zigs
    final boolean halfIsLeg = isHalfLeg(legId, courseDeltas);

    // ok, convert them into legs
    LegOfData thisL = null;
    for (int i = 0; i < len; i++)
    {
      if (i > 0)
      {
        final double lastLeg = legId[i - 1];
        final double thisLeg = legId[i];

        if (thisLeg != lastLeg)
        {
          final boolean isWhole = thisLeg % 1 == 0;
          if (halfIsLeg == isWhole)
          {
            // end of leg

            // do we know the leg?
            if (thisL == null)
            {
              if (legs.size() == 0)
              {
                // ok, first leg. don't worry. Start the leg back at the first entry
                thisL = new LegOfData("" + thisLeg);
                thisL.tStart = times[0];
              }
              else
              {

                // hey, we shouldn't get here
                throw new RuntimeException(
                    "Ownship leg slicing failed: Should have an open leg ready");
              }
            }

            // finish the leg
            thisL.tEnd = times[i];

            // ok, is the leg long enough?
            if (thisL.tEnd - thisL.tStart > MIN_LEG_LENGTH)
            {
              legs.add(thisL);
            }
            thisL = null;
          }
          else
          {
            // start of next leg
            if (thisL != null)
            {
              // hey, we shouldn't get here
              throw new RuntimeException(
                  "Ownship leg slicing failed: Should not have pending open leg");
            }
            thisL = new LegOfData("" + thisLeg);
            thisL.tStart = times[i];
          }
        }
      }
    }

    // ok, do we have a trailing leg?
    if (thisL != null)
    {
      thisL.tEnd = times[len - 1];
      legs.add(thisL);
    }

    return legs;
  }
}
