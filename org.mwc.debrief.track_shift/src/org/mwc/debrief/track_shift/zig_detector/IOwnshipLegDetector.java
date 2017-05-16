package org.mwc.debrief.track_shift.zig_detector;

import java.util.List;

import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;

public interface IOwnshipLegDetector
{
  List<LegOfData> identifyOwnshipLegs(long[] times, double[] rawSpeeds,
      double[] rawCourses, int minsOfAverage, Precision precision);
}
