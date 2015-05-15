package com.planetmayo.debrief.satc.zigdetector.TimeWindow.average;

import java.util.Set;

/**
 * Created by Romain on 09/05/2015.
 */
public class TimeBasedMovingAverage {

    private final Long duration;

    /**
     * @param duration in milliseconds
     */
    public TimeBasedMovingAverage(Long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        return duration;
    }

    /**
     * @param dataPoint the reference point to compute the moving average from
     * @param data SortedSet by Timestamp of dataPoints
     * @return the moving average value
     */
    public Double average(final DataPoint dataPoint, Set<DataPoint> data) {

        int nbPts = 1;
        double sum = dataPoint.getValue();

        for (DataPoint pt : data) {

            if (inTimeFrame(duration, dataPoint, pt)) {
                nbPts++;
                sum += pt.getValue();
            }
        }

        return sum / nbPts;
    }

    public static Boolean inTimeFrame(Long duration, DataPoint referencePoint, DataPoint candidate) {
        final Long distance = referencePoint.getTimestamp().getTimeInMillis() - candidate.getTimestamp().getTimeInMillis();
        return Math.abs(distance) <= Math.abs(duration);
    }
}
