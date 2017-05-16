package org.mwc.debrief.track_shift.zig_detector;

import java.util.ArrayList;
import java.util.List;

import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.mwc.debrief.track_shift.zig_detector.ownship.PeakTrackingOwnshipLegDetector;

/**
 * special case of leg detector - for artificially generated legs that contain no wiggle
 * 
 * @author Ian
 * 
 */
public class ArtificalLegDetector implements IOwnshipLegDetector
{

  @Override
  public List<LegOfData> identifyOwnshipLegs(long[] times, double[] rawSpeeds,
      double[] rawCourses, int minsOfAverage, Precision precision)
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
    final double[] newCourses =
        PeakTrackingOwnshipLegDetector.makeContinuous(rawCourses);

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
    if (artificialLegStart != -1)
    {
      legs.add(new LegOfData("L" + legs.size() + 1, artificialLegStart,
          lastTime));
    }
    else if (thisTroughCourse == Double.MIN_VALUE
        || thisPeakCourse == Double.MIN_VALUE)
    {
      // no, we're turning
    }
    else
    {
      // we're still in a leg. is it an artificial one?
      legs.add(new LegOfData("L" + legs.size() + 1, thisLegStart, lastTime));
    }

    return legs;
  }

}
