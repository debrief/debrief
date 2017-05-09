package org.mwc.debrief.track_shift.zig_detector;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;

import junit.framework.TestCase;

public class PeakTrackingOwnshipLegDetector implements IOwnshipLegDetector
{

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
    double[] courses = makeContinuous(rawCourses);
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
    double[] courseDeltas = getDeltas(courses);
    double[] courseChanges = getChanges(courseDeltas);
    double[] peaks = getPeaks(courseChanges);

    double[] downMin = getDownMin(peaks, courses, minCourse);
    double[] downMax = getDownMax(peaks, courses, maxCourse);
    double[] upMin = getUpMin(peaks, courses, minCourse);
    double[] upMax = getUpMax(peaks, courses, maxCourse);
    
    // right, now diff them
    double[] diffMin = getDiff(downMin, upMin);
    double[] diffMax = getDiff(downMax, upMax);
    
    boolean[] overThreshold = getOverThreshold(diffMin, diffMax, COURSE_TOLERANCE);
    
    double[] legId = getLegIds(overThreshold);
    
    // ok, convert them into legs
    LegOfData thisL = null;
    for(int i=0;i<len;i++)
    {
      if(i > 0)
      {
        double lastLeg = legId[i-1];
        double thisLeg = legId[i];
        
        if(thisLeg != lastLeg)
        {
          if(thisLeg % 1 == 0)
          {
            if(thisL != null)
            {
              // hey, we shouldn't get here
              throw new RuntimeException("Ownship leg slicing failed: Should not have pending open leg");
            }
            
            thisL = new LegOfData("" + thisLeg);
            thisL.tStart = times[i];
          }
          else
          {
            if(thisL == null)
            {
              // hey, we shouldn't get here
              throw new RuntimeException("Ownship leg slicing failed: Should have an open leg ready");
            }
            thisL.tEnd = times[i];
            legs.add(thisL);
            thisL = null;
          }
        }
      }
    }
    
    // ok, do we have a trailing leg?
    if(thisL != null)
    {
      thisL.tEnd = times[len-1];
      legs.add(thisL);
    }

    return legs;
  }

  private double[] getLegIds(boolean[] overThreshold)
  {
    final int len = overThreshold.length;
    double[] overT = new double[len];
    for (int i = 0;i<len;i++)      
    {
      final double thisLeg;
      if(i==0)
      {
        thisLeg = 0.5;
      }
      else
      {
        final double lastLeg = overT[i-1];
        if(overThreshold[i] != overThreshold[i-1])
        {
          thisLeg = lastLeg + 0.5;
        }
        else
        {
          thisLeg  = lastLeg;
        }
      }
        
      overT[i] = thisLeg;
    }
    return overT;
  }

  private boolean[] getOverThreshold(double[] diffMin, double[] diffMax, double tolerance)
  {
    final int len = diffMin.length;
    boolean[] overT = new boolean[len];
    for (int i = 0;i<len;i++)      
    {
      overT[i] = diffMin[i] > tolerance && diffMax[i] > tolerance;
    }
    return overT;
  }

  private double[] getDiff(double[] downValues, double[] upValues)
  {
    final int len = downValues.length;
    double[] diff = new double[len];
    for (int i = 0;i<len;i++)      
    {
      diff[i] = Math.abs(downValues[i] - upValues[i]);
    }
    return diff;
  }

  private double[] getUpMin(double[] peaks, double[] courses, double minCourse)
  {
    final int len = peaks.length;
    double[] upMin = new double[len];
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

  private double[] getUpMax(double[] peaks, double[] courses, double maxCourse)
  {
    final int len = peaks.length;
    double[] upMax = new double[len];
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
  
  private double[]
      getDownMin(double[] peaks, double[] courses, double minCourse)
  {
    final int len = peaks.length;
    double[] downMin = new double[len];
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

  private double[]
      getDownMax(double[] peaks, double[] courses, double maxCourse)
  {
    final int len = peaks.length;
    double[] downMax = new double[len];
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

  private double[] getPeaks(double[] courseDeltas)
  {
    final int len = courseDeltas.length;
    double[] peaks = new double[len];
    for (int i = 0; i < len; i++)
    {
      final double thisPeak;
      if (i == 0)
      {
        thisPeak = 0;
      }
      else
      {
        double thisDelta = courseDeltas[i];
        double lastDelta = courseDeltas[i - 1];
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

  private double[] getChanges(double[] courseDeltas)
  {
    final int len = courseDeltas.length;
    double[] courseChanges = new double[len];
    for (int i = 0; i < len; i++)
    {
      // look forward
      double thisDelta = courseDeltas[i];
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

  private double[] getDeltas(double[] courses)
  {
    final int len = courses.length;
    double[] courseDeltas = new double[len];
    for (int i = 0; i < len; i++)
    {
      // look forward
      if (i < len - 1)
      {
        double thisCourse = courses[i];
        double nextCourse = courses[i + 1];
        courseDeltas[i] = nextCourse - thisCourse;
      }
      else
      {
        courseDeltas[i] = 0;
      }
    }
    return courseDeltas;
  }

  /**
   * slice this data into ownship legs, where the course and speed are relatively steady
   * 
   * @param course_degs
   * @param speed
   * @param bearings
   * @param elapsedTimess
   * @return
   */
  public List<LegOfData> identifyOwnshipLegs2(final long[] times,
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
    long lastTime = Long.MIN_VALUE;
    long artificialLegStart = -1;

    for (int i = 0; i < newCourses.length; i++)
    {
      // handle this step
      final long thisTime = times[i];
      final double thisCourse = newCourses[i];

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
        if (thisDir == 0)
        {
          // ok, we may be on a computer generated dataset,
          // with perfect course keeping
          if (artificialLegStart == -1)
          {
            artificialLegStart = lastTime;
          }
        }
        else
        {
          // ok, we're turning. just check if we were on a computer generated straight leg
          if (artificialLegStart != -1)
          {
            // ok, have we elapsed very long
            if (thisTime - artificialLegStart > minLegLength)
            {
              // ok, generate a leg
              legs.add(new LegOfData("L" + legs.size() + 1, artificialLegStart,
                  lastTime));
            }
          }

          // make sure we clear the artificial straight leg start time
          artificialLegStart = -1;
        }

        if (thisDir != lastDir && thisDir != 0)
        {
          // ok, back to peak tracking algorithm

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
              thisLegStart = lastTime;
            }
          }
          else
          {
            // ok, just passed trough
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
              thisLegStart = lastTime;
            }
          }

          // ok, are we in a turn?
          if (delta > COURSE_TOLERANCE)
          {
            // just check the leg is long enough
            final long legLength = legEnd - thisLegStart;

            if (legLength >= minLegLength)
            {
              // ok, leg ended.
              legs.add(new LegOfData("L" + legs.size() + 1, thisLegStart,
                  legEnd));
            }

            // clear the leg marker
            thisLegStart = lastTime;

            // and a bit of clearing out
            if (thisDir == -1)
            {
              // clear the last min value, it will be wrong - since it's from the last leg
              thisTroughCourse = Double.MIN_VALUE;
              lastTroughCourse = Double.MIN_VALUE;
            }
            else
            {
              // clear the last max value, it will be wrong - since it's from the last leg
              thisPeakCourse = Double.MIN_VALUE;
              lastPeakCourse = Double.MIN_VALUE;
            }
          }
        }
      }
      lastCourse = thisCourse;
      lastTime = thisTime;
    }

    // do we have a trailing leg?
    if (thisTroughCourse == Double.MIN_VALUE
        || thisPeakCourse == Double.MIN_VALUE)
    {
      // no, we're turning
    }
    else
    {
      // we're still in a leg. is it an artificial one?
      if (artificialLegStart != -1)
      {
        legs.add(new LegOfData("L" + legs.size() + 1, artificialLegStart,
            lastTime));
      }
      else
      {
        legs.add(new LegOfData("L" + legs.size() + 1, thisLegStart, lastTime));
      }
    }

    return legs;
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
      double[] test =
          new double[]
          {160, 170, 175, 180, -175, -200, -175, 180, 170, 150, 130, 110, 70,
              20, 5, 355, 335};
      double[] res = PeakTrackingOwnshipLegDetector.makeContinuous(test);
      assertEquals("correct side", 185d, res[4]);
      assertEquals("correct side", -5d, res[15]);
      System.out.println(res);
    }

    private TimeSeries getOSValues()
    {
      TimeSeries tmpSeries = new TimeSeries("Set piece");
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

    public void testSetPiece()
        {
          TimeSeries tmpSeries = getOSValues();
    
          PeakTrackingOwnshipLegDetector detector =
              new PeakTrackingOwnshipLegDetector();
          int len = tmpSeries.getItemCount();
          long[] times = new long[len];
          double[] speeds = new double[len];
          double[] courses = new double[len];
    
    //      long start = tmpSeries.getDataItem(0).getPeriod().getFirstMillisecond();
    
          for (int i = 0; i < len; i++)
          {
            times[i] = tmpSeries.getDataItem(i).getPeriod().getFirstMillisecond();
            speeds[i] = 0d;
            courses[i] = (Double) tmpSeries.getDataItem(i).getValue();
          }
    
          List<LegOfData> legs =
              detector
                  .identifyOwnshipLegs(times, speeds, courses, 5, Precision.LOW);
          
          

          assertEquals("got right num legs:", 2, legs.size());
          assertEquals("got leg 1 start time right", 946697580000L, legs.get(0).tStart);
          assertEquals("got leg 1 end time right", 946699020000L, legs.get(0).tEnd);
          assertEquals("got leg 2 start time right", 946699110000L, legs.get(1).tStart);
          assertEquals("got leg 2 end time right", 946700970000L, legs.get(1).tEnd);
        }
  }
}
