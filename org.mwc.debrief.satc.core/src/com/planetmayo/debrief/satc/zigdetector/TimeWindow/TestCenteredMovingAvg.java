package com.planetmayo.debrief.satc.zigdetector.TimeWindow;

import com.planetmayo.debrief.satc.zigdetector.TimeWindow.average.CenteredMovingAverage;


public class TestCenteredMovingAvg {

    public static void main(String[] args) {

        double[] testData = {1,2,3,4,5,5,4,3,2,1};

        int[] windowSizes = {3,5};

        for (int windSize : windowSizes) {

            final CenteredMovingAverage cma = new CenteredMovingAverage(windSize);

            for (int n = 0; n<testData.length; n++) {
                double avg = cma.average(n, testData);
                String msg = String.format("The centered moving average with period %d and n %d is %f", cma.getPeriod(), n, avg);
                System.out.println(msg);
            }
            System.out.println();
        }
    }
}
