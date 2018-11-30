package org.mwc.debrief.track_shift.zig_detector.ownship.alternate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.mwc.debrief.track_shift.zig_detector.ownship.alternate.SCAlgorithms.SpanPair;

public class AlternateLegWrapper implements IOwnshipLegDetector
{

  @Override
  public List<LegOfData> identifyOwnshipLegs(long[] times, double[] rawSpeeds,
      double[] rawCourses, int minsOfAverage, Precision precision)
  {
    // collate the input data
    List<Tote> totes = collateData(times, rawSpeeds, rawCourses);

    double mintime = 300.0; // 5min = 300sec

    // Finding out steady-course periods
    List<SCAlgorithms.SpanPair> course0_intervals = SCAlgorithms
        .extractSteadyHeadings(totes, mintime);
    System.out.println("\nSteady course intervals:");
    printIntervals(course0_intervals, totes);

    // Finding out steady-speed periods
    List<SCAlgorithms.SpanPair> speed0_intervals = SCAlgorithms
        .extractSteadySpeeds(totes, mintime);
    System.out.println("\nSteady speed intervals:");
    printIntervals(speed0_intervals, totes);

    // Combine them
    List<SCAlgorithms.SpanPair> steady_CourseAndSpeed_intervals = SCAlgorithms
        .intersectLists(course0_intervals, speed0_intervals);
    System.out.println("\nSteady course-speed combined intervals:");
    printIntervals(steady_CourseAndSpeed_intervals, totes);

    // wrap the results
    List<LegOfData> res = wrapResults(steady_CourseAndSpeed_intervals, totes);
    return res;
  }

  private List<LegOfData> wrapResults(
      List<SpanPair> steady_CourseAndSpeed_intervals, List<Tote> totes)
  {
    List<LegOfData> res = new ArrayList<LegOfData>();
    
    for(SpanPair leg: steady_CourseAndSpeed_intervals)
    {
      long legStart = (long) (totes.get(leg.first).dabsolute_time * 1000);
      long legEnd = (long) (totes.get(leg.second - 1).dabsolute_time * 1000);
      LegOfData newL = new LegOfData("Leg:" + res.size() + 1, legStart, legEnd);
      res.add(newL);
    }
    
    return res;
  }

  private List<Tote> collateData(long[] times, double[] rawSpeeds,
      double[] rawCourses)
  {
    List<Tote> res = new ArrayList<Tote>();
    int len = times.length;
    Double previousHeading = null;
    for (int i = 0; i < len; i++)
    {
      Tote it = new Tote();
      long time = times[i];
      it.dabsolute_time = time / 1000d;
      it.dspeed = rawSpeeds[i];
      double heading = rawCourses[i];

      if (previousHeading != null)
      {
        if (previousHeading - heading > 180.0)
          heading += 360.0;
        else if (heading - heading > 180.0)
          heading -= 360.0;
      }
      it.dheading = heading;

      previousHeading = it.dheading;

      res.add(it);
    }
    return res;
  }

  // -------------------------------------------------------------------------
  static void printIntervals(List<SCAlgorithms.SpanPair> intervals,
      List<Tote> totes)
  /* throws IOException */ {
    Iterator<SCAlgorithms.SpanPair> iter = intervals.iterator();
    while (iter.hasNext())
    {
      SCAlgorithms.SpanPair item = iter.next();

      // times in hhmm.ss format
      double dstart_time = totes.get(
          item.first).dabsolute_time;
      double dend_time = 0.01 * totes.get(item.second
          - 1).dabsolute_time;
      String sstart_time = String.format("%07.2f", dstart_time);
      String send_time = String.format("%07.2f", dend_time);

      System.out.println(String.format("%5d", item.first) + " " + String.format(
          "%5d", item.second) + "  (" + sstart_time + " - " + send_time + ")");
    }
  }
}
