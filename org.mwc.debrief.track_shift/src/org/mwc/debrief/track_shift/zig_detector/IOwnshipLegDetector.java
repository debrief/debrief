/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
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
