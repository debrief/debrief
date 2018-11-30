package org.mwc.debrief.track_shift.zig_detector;

import java.util.List;

import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;

public interface IOwnshipLegDetector
{
  /** produce a list of sliced ownship legs
   * 
   * @param times timestamps of measurements
   * @param rawSpeeds speed measurements
   * @param rawCourses course measurements
   * @param minsOfAverage factor for lenght of moving average
   * @param precision degree of precision required
   * @return list of legs
   */
  List<LegOfData> identifyOwnshipLegs(long[] times, double[] rawSpeeds,
      double[] rawCourses, int minsOfAverage, Precision precision);
}
