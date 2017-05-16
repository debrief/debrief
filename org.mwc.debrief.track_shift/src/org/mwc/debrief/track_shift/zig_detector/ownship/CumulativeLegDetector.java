package org.mwc.debrief.track_shift.zig_detector.ownship;

import java.util.ArrayList;
import java.util.List;

import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;

public class CumulativeLegDetector implements IOwnshipLegDetector
{

  /**
   * slice this data into ownship legs, where the course and speed are relatively steady
   * 
   * @param course_degs
   * @param speed
   * @param bearings
   * @param elapsedTimess
   * @return
   */
  public List<LegOfData> identifyOwnshipLegs(final long[] times,
      final double[] rawSpeeds, final double[] rawCourses,
      final int minsOfAverage, Precision precision)
  {

    double lastCourse = 0;
    long lastTime = 0;

    final double areaUnderTurn = 20000; // deg millis

    final List<LegOfData> legs = new ArrayList<LegOfData>();
    legs.add(new LegOfData("Leg-1"));
    Integer lastSign = null;
    double leftArea = 0;
    double rightArea = 0;

    // 1. make continuous dataset
    double[] newCourses =
        PeakTrackingOwnshipLegDetector.makeContinuous(rawCourses);
    
    final long startTime = times[0];

    for (int i = 0; i < times.length; i++)
    {
      final long thisTime = times[i];
      final double thisCourse = newCourses[i];

      if (i > 0)
      {
        // ok, check out the course change rate
        final double timeStep = thisTime - lastTime;
        final double courseDelta = thisCourse - lastCourse;
        // final double courseRate = courseDelta / timeStepSecs;
        final double courseArea = courseDelta * timeStep;

        // ok, find the sign of the course
        final int thisSign = (int) Math.signum(courseDelta);

        leftArea += courseArea;
        rightArea += courseArea;

        if (lastSign != null && thisSign != lastSign && thisSign != 0)
        {
          // ok, have we turned to left or right?
          if (thisSign == 1)
          {
            // turn to right

            // 1. the left turn is complete. does it count?
            if (Math.abs(leftArea) > areaUnderTurn)
            {
              System.out.println("left turn end at" +  (thisTime - startTime)/1000);
            }
            else
            {
//              System.out.println("skipping turn to right at " + (thisTime - startTime)/1000 + " area:" + leftArea + " crse" + thisCourse);
            }
            leftArea = 0;
          }
          else
          {
            // turn to left

            // 1. the right turn is complete. does it count?
            if (Math.abs(rightArea) > areaUnderTurn)
            {
              System.out.println("right turn end at" +  (thisTime - startTime)/1000);
            }
            else
            {
//              System.out.println("skipping turn to left at " +  (thisTime - startTime)/1000 + " area:" + rightArea + " crse" + thisCourse);
            }
            rightArea = 0;
          }
        }

        // ok, store the values
        if (thisSign != 0)
        {
          lastSign = thisSign;
        }
      }

      final long soFar = (thisTime - startTime)/1000;
      if(soFar > 600 && soFar < 1000)
      {
      System.out.println(soFar + "," + thisCourse + "," + lastSign + ","
          + (int) leftArea + "," + (int) rightArea);
      }
      if(i % 450 == 0)
      {
 //       System.out.println("here");
      }
      
      lastTime = thisTime;
      lastCourse = thisCourse;
      // lastSpeed = thisSpeed;

    }

    return legs;
  }

  /**
   * create a moving average over the set of dat measurements
   * 
   * @param measurements
   * @param period
   * @return
   */
  // private double[] movingAverage(final double[] measurements, final int period)
  // {
  // final double[] res = new double[measurements.length];
  // final CenteredMovingAverage ma = new CenteredMovingAverage(period);
  // for (int j = 0; j < measurements.length; j++)
  // {
  // res[j] = ma.average(j, measurements);
  // }
  // return res;
  // }

}
