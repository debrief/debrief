package org.mwc.debrief.track_shift.zig_detector;

import java.util.List;

public interface IOwnshipLegDetector
{
  List<LegOfData> identifyOwnshipLegs(long[] times, double[] rawSpeeds,
      double[] rawCourses, int minsOfAverage, Precision precision);
}
