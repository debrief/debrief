package org.mwc.debrief.track_shift.zig_detector.ownship.alternate;


public class SCConstants {
    // if all the values in interval do not differ more, we can consider it the steady course/speed interval
    static final double COURSE_STEADY_RANGE = 0.5;
    static final double SPEED_STEADY_RANGE = 0.1;

    // if standard deviation is less then the predefined minimum, we can consider it the steady course/speed interval
    static final double COURSE_STEADY_STDEV = 0.05;
    static final double SPEED_STEADY_STDEV = 0.01;
}
