package com.planetmayo.debrief.satc.zigdetector.TimeWindow.average;

/**
 * Created by deft on 03/03/2015.
 */
public class CenteredMovingAverage {

    private final int period;

    public CenteredMovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
    }

    public double average(int n, double[] data) {
        assert n > 0 : "N must be a positive integer.";
        assert data.length > 0 : "Data array must not be empty.";
        assert n < data.length : "N should be less than the data array length " + data.length;

        int nIdx = n;

        // Get N index in data array.
        if (nIdx > data.length) {
            nIdx = data.length - period;
        }

        double sum = data[nIdx];

        int lastBackwardIndex = lastBackwardIndex(period, n);
        if (lastBackwardIndex < 0) {
            lastBackwardIndex = 0;
        }

        int lastForwardIndex = lastForwardIndex(period, n);
        if (lastForwardIndex > data.length-1) {
            lastForwardIndex = data.length-1;
        }

        for (int idx = nIdx + 1; idx <= lastForwardIndex; idx++) {
            sum += data[idx];
        }

        for (int idx = nIdx - 1; idx >= lastBackwardIndex; idx--) {
            sum += data[idx];
        }

        return sum / (lastForwardIndex - lastBackwardIndex +1);
    }

    private int lastBackwardIndex(int period, int n) {
        int distance = (period-1) / 2;
        return n - distance;
    }

    private int lastForwardIndex(int period, int n) {
        int mod = (period-1) % 2;
        int distance = (period-1) / 2;
        return n + distance + mod;
    }

    public int getPeriod() {
        return period;
    }
}
