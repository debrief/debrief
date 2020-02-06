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
package org.mwc.debrief.track_shift.zig_detector.ownship.alternate;


public class SCConstants {
    // if all the values in interval do not differ more, we can consider it the steady course/speed interval
    static protected final double COURSE_STEADY_RANGE = 0.5;
    static protected final double SPEED_STEADY_RANGE = 0.1;

    // if standard deviation is less then the predefined minimum, we can consider it the steady course/speed interval
    static protected final double COURSE_STEADY_STDEV = 0.05;
    static protected final double SPEED_STEADY_STDEV = 0.01;
}
