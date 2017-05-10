package org.mwc.debrief.track_shift.zig_detector.target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

public class ZigDetector
{
  static class FlanaganArctan implements MinimisationFunction
  {
    private static double calcForecast(final double B, final double P,
        final double Q, final double elapsedSecs)
    {
      final double dX = Math.cos(Math.toRadians(B)) + Q * elapsedSecs;
      final double dY = Math.sin(Math.toRadians(B)) + P * elapsedSecs;
      return Math.toDegrees(Math.atan2(dY, dX));
    }

    final private Long[] _times;

    final private Double[] _bearings;

    public FlanaganArctan(final List<Long> beforeTimes,
        final List<Double> beforeBearings)
    {
      _times = beforeTimes.toArray(new Long[]
      {});
      _bearings = beforeBearings.toArray(new Double[]
      {});
    }

    // evaluation function
    @Override
    public double function(final double[] point)
    {
      final double B = point[0];
      final double P = point[1];
      final double Q = point[2];

      double runningSum = 0;
      final Long firstTime = _times[0];

      // ok, loop through the data
      for (int i = 0; i < _times.length; i++)
      {
        final long elapsedMillis = _times[i] - firstTime;
        final double elapsedSecs = elapsedMillis / 1000d;
        final double thisForecast = calcForecast(B, P, Q, elapsedSecs);
        final double thisMeasured = _bearings[i];
        double thisError = thisForecast - thisMeasured;
        if (thisError > 180)
        {
          thisError -= 360;
        }
        else if (thisError < -180)
        {
          thisError += 360;
        }
        final double sqError = Math.pow(thisError, 2);
        runningSum += sqError;
      }
      final double mean = runningSum / _times.length;

      final double rms = Math.sqrt(mean);
      return rms;
    }

  }

  public static class TestMe extends TestCase
  {
    public void testStartTimes()
    {
      assertEquals(2, 5 / 2);

      List<Long> times =
          new ArrayList<Long>(Arrays.asList(new Long[]
          {1000L, 1200L, 1500L, 1800L, 2100L, 2400L, 2700L, 3000L, 3300L,
              3600L, 3900L}));

      assertEquals("correct", -1, getEnd(0, times, 200, 0));
      assertEquals("correct", -1, getEnd(0, times, 200, 1));
      assertEquals("correct", -1, getEnd(0, times, 200, 2));
      assertEquals("correct", 2, getEnd(0, times, 200, 3));
      assertEquals("correct", 3, getEnd(0, times, 200, 4));
      assertEquals("correct", 4, getEnd(0, times, 200, 5));
      assertEquals("correct", 5, getEnd(0, times, 200, 6));
      assertEquals("correct", 6, getEnd(0, times, 200, 7));
      assertEquals("correct", 7, getEnd(0, times, 200, 8));
      assertEquals("correct", 8, getEnd(0, times, 200, 9));
      assertEquals("correct", 9, getEnd(0, times, 200, 10));

      assertEquals("correct", 2, getStart(0, times, 400, 0));
      assertEquals("correct", 2, getStart(0, times, 400, 1));
      assertEquals("correct", 3, getStart(0, times, 400, 2));
      assertEquals("correct", 4, getStart(0, times, 400, 3));
      assertEquals("correct", 5, getStart(0, times, 400, 4));
      assertEquals("correct", 6, getStart(0, times, 400, 5));
      assertEquals("correct", 7, getStart(0, times, 400, 6));
      assertEquals("correct", 8, getStart(0, times, 400, 7));
      assertEquals("correct", -1, getStart(0, times, 400, 8));
      assertEquals("correct", -1, getStart(0, times, 400, 9));
      assertEquals("correct", -1, getStart(0, times, 400, 10));
    }

    public void testMultiSlice()
    {
      Long[] times =
          new Long[]
          {1248237792000L, 1248237799000L, 1248237896000L, 1248237944000L,
              1248237990000L, 1248238098000L, 1248238177000L, 1248238249000L,
              1248238321000L, 1248238393000L, 1248238484000L, 1248238556000L,
              1248238624000L, 1248238695000L, 1248238759000L, 1248238843000L,
              1248238931000L, 1248239006000L, 1248239074000L, 1248239162000L,
              1248239277000L, 1248239353000L, 1248239444000L, 1248239520000L,
              1248239600000L, 1248239644000L, 1248239735000L, 1248239799000L,
              1248239891000L, 1248239951000L, 1248240030000L, 1248240090000L,
              1248240142000L, 1248240198000L, 1248240257000L, 1248240305000L};
      Double[] bearings =
          new Double[]
          {295.8, 295.5, 293.5, 293.0, 292.8, 290.3, 289.0, 288.3, 288.0,
              288.0, 288.8, 288.8, 288.8, 289.8, 289.8, 291.0, 291.5, 292.3,
              292.3, 293.0, 293.5, 294.0, 294.3, 294.8, 294.8, 294.8, 295.8,
              295.8, 295.8, 296.5, 296.5, 297.5, 297.8, 298.3, 299.0, 299.5};

      // start to collate the adta
      List<Long> tList = Arrays.asList(times);
      List<Double> tBearings = Arrays.asList(bearings);

      final ZigDetector detector = new ZigDetector();
      ILog logger = getLogger();
      ILegStorer legStorer = new ILegStorer()
      {

        @Override
        public void storeLeg(String scenarioName, long tStart, long tEnd,
            double rms)
        {
          System.out.println("store it: " + tStart + ", " + tEnd);
        }
      };
      IZigStorer zigStorer = new IZigStorer(){

        @Override
        public void storeZig(String scenarioName, long tStart, long tEnd,
            double rms)
        {
          // TODO Auto-generated method stub
          
        }

        @Override
        public void finish()
        {
          // TODO Auto-generated method stub
          
        }};
      detector.sliceThis(logger, "some name", "scenario", times[0],
          times[times.length - 1], legStorer, zigStorer, 0.1, 0.2, tList, tBearings);

    }

    private ILog getLogger()
    {
      ILog logger = new ILog()
      {

        @Override
        public void addLogListener(ILogListener listener)
        {
          // TODO Auto-generated method stub

        }

        @Override
        public Bundle getBundle()
        {
          // TODO Auto-generated method stub
          return null;
        }

        @Override
        public void log(IStatus status)
        {
          // TODO Auto-generated method stub

        }

        @Override
        public void removeLogListener(ILogListener listener)
        {
          // TODO Auto-generated method stub

        }
      };
      return logger;
    }
  }

  final SimpleDateFormat dateF = new SimpleDateFormat("HH:mm:ss");

  /**
   * if we slice these times in two, with a buffer, what is the index of the last item in the first
   * leg?
   * 
   * @param start
   * @param thisLegTimes
   * @param buffer
   * @param index
   * @return
   */
  private static int getEnd(final int start, final List<Long> thisLegTimes,
      final long buffer, final int index)
  {
    int res = -1;
    final int MIN_SIZE = 3;
    final long halfWid = buffer / 2l;

    // find the half-way mark
    final long sliceAt = thisLegTimes.get(index);

    // and the time of the last item in the first leg
    final long endTime = sliceAt - halfWid;

    // ok, loop through
    for (int ctr = 0; ctr < thisLegTimes.size(); ctr++)
    {
      // what's this time?
      long thisTime = thisLegTimes.get(ctr);

      // have we passed the end time?
      if (thisTime > endTime)
      {
        // yes, it must have been the previous time
        if (ctr >= MIN_SIZE)
        {
          res = ctr - 1;
        }
        break;
      }
    }

    return res;
  }

  /**
   * if we slice these times in two, with a buffer, what is the index of the first item in the
   * second leg?
   * 
   * @param start
   * @param thisLegTimes
   * @param buffer
   * @param index
   * @return
   */
  private static int getStart(final int start, final List<Long> thisLegTimes,
      final long buffer, final int index)
  {
    int res = -1;
    final int MIN_SIZE = 3;
    final long halfWid = buffer / 2l;

    // find the half-way mark
    final long halfWay = thisLegTimes.get(index);

    // and the time of the first item in the second leg
    final long startTime = halfWay + halfWid;

    // ok, loop through
    for (int ctr = 0; ctr < thisLegTimes.size(); ctr++)
    {
      // what's this time?
      long thisTime = thisLegTimes.get(ctr);

      // have we passed the end time?
      if (thisTime > startTime)
      {
        // have we passed the min size?
        if (ctr <= thisLegTimes.size() - MIN_SIZE)
        {
          // ok, we still have the min number of points

          // yes, it must have been the previous time
          res = ctr;
          break;
        }
      }
    }

    return res;
  }

  final private Minimisation optimiseThis(final List<Long> times,
      final List<Double> bearings, final double initialBearing,
      final double optimiserTolerance)
  {
    // Create instance of Minimisation
    final Minimisation min = new Minimisation();

    // Create instace of class holding function to be minimised
    final FlanaganArctan funct = new FlanaganArctan(times, bearings);

    // initial estimates
    final double firstBearing = bearings.get(0);
    final double[] start =
    {firstBearing, 0.0D, 0.0D};

    // initial step sizes
    final double[] step =
    {0.2D, 0.3D, 0.3D};

    // convergence tolerance
    final double ftol = optimiserTolerance;

    // Nelder and Mead minimisation procedure
    min.nelderMead(funct, start, step, ftol);

    return min;
  }

  /**
   * @param trialIndex
   * @param bearings
   * @param times
   * @param legOneEnd
   * @param legTwoStart
   * @param optimiserTolerance
   * @param fittedQ
   * @param fittedP
   * @param overallScore
   *          the overall score for this leg
   * @param BUFFER_REGION
   * @param straightBar
   * @param thisSeries
   * @return
   */
  private double sliceLeg(final int trialIndex, final List<Double> bearings,
      final List<Long> times, final int legOneEnd, final int legTwoStart,
      final double optimiserTolerance)
  {

    final List<Long> theseTimes = times;
    final List<Double> theseBearings = bearings;

    final Date thisD = new Date(times.get(trialIndex));

    // if((legOneEnd == -1) || (legTwoStart == -1))
    // return Double.MAX_VALUE;

    double beforeScore = Double.MAX_VALUE;
    double afterScore = Double.MAX_VALUE;

    @SuppressWarnings("unused")
    String msg = dateF.format(thisD);

    Minimisation beforeOptimiser = null;
    Minimisation afterOptimiser = null;

    if (legOneEnd != -1)
    {
      final List<Long> beforeTimes = theseTimes.subList(0, legOneEnd);
      final List<Double> beforeBearings = theseBearings.subList(0, legOneEnd);
      beforeOptimiser =
          optimiseThis(beforeTimes, beforeBearings, beforeBearings.get(0),
              optimiserTolerance);
      beforeScore = beforeOptimiser.getMinimum();
      // System.out.println(" before:" + _outDates(beforeTimes));

    }

    if (legTwoStart != -1)
    {
      final List<Long> afterTimes =
          theseTimes.subList(legTwoStart, theseTimes.size() - 1);
      final List<Double> afterBearings =
          theseBearings.subList(legTwoStart, theseTimes.size() - 1);
      afterOptimiser =
          optimiseThis(afterTimes, afterBearings, afterBearings.get(0),
              optimiserTolerance);
      afterScore = afterOptimiser.getMinimum();
      // System.out.println(" after:" + _outDates(afterTimes));
    }

    // find the total error sum
    double sum = Double.MAX_VALUE;

    // do we have both legs?
    if ((legOneEnd != -1) && (legTwoStart != -1))
    {
      final int beforeLen = theseTimes.subList(0, legOneEnd).size();
      final int afterLen =
          theseTimes.subList(legTwoStart, theseTimes.size() - 1).size();

      final int totalCuts = beforeLen + afterLen;

      final double beforeNormal = beforeScore * beforeLen / totalCuts;
      final double afterNormal = afterScore * afterLen / totalCuts;
      sum = beforeNormal + afterNormal;

      // double[] bValues = beforeOptimiser.getParamValues();
      // msg +=
      // " ,BEFORE," + dateF.format(times.get(0)) + ","
      // + dateF.format(times.get(legOneEnd)) + "," + beforeScore;
      // msg += ",B," + bValues[0] + ",P," + bValues[1] + ",Q," + bValues[2] + ",score," +
      // beforeNormal;
      // double[] aValues = afterOptimiser.getParamValues();
      // msg +=
      // " ,AFTER," + dateF.format(times.get(legTwoStart)) + ","
      // + dateF.format(times.get(times.size() - 1)) + "," + afterScore;
      // msg += ",B," + aValues[0] + ",P," + aValues[1] + ",Q," + aValues[2] + ",score," +
      // afterNormal;
      // System.out.println(msg + ",sum," + sum);

    }

    return sum;
  }

  /**
   * 
   * @param log
   *          the logger
   * @param PLUGIN_ID
   *          the id of the plugin that is runnign this
   * @param scenario
   *          the name of this scenario
   * @param wholeStart
   *          overall start time
   * @param wholeEnd
   *          overall end time
   * @param legStorer
   *          someone interested in legs
   * @param zigStorer
   *          someone interested in zigs
   * @param RMS_ZIG_RATIO
   *          how much better the slice has to be
   * @param optimiseTolerance
   *          when the ARC_TAN fit is good enough
   * @param thisLegTimes
   *          bearing times
   * @param thisLegBearings
   *          bearing values
   */
  public void sliceThis(final ILog log, final String PLUGIN_ID,
      final String scenario, final long wholeStart, final long wholeEnd,
      final ILegStorer legStorer, IZigStorer zigStorer,
      final double RMS_ZIG_RATIO, final double optimiseTolerance,
      final List<Long> thisLegTimes, final List<Double> thisLegBearings)
  {
    // ok, find the best slice
    // prepare the data

    final int len = thisLegTimes.size();
    for (int i = 0; i < len; i++)
    {
      System.out.print(thisLegTimes.get(i) + "L, ");
    }
    System.out.println("");

    for (int i = 0; i < len; i++)
    {
      System.out.print(thisLegBearings.get(i) + ", ");
    }

    if (thisLegBearings.size() == 0)
    {
      return;
    }

    final Minimisation wholeLeg =
        optimiseThis(thisLegTimes, thisLegBearings, thisLegBearings.get(0),
            optimiseTolerance);
    final double wholeLegScore = wholeLeg.getMinimum();

    // System.out.println("Whole leg score is:" + wholeLegScore);

    // ok, now have to slice it
    double bestScore = Double.MAX_VALUE;
    // int bestSlice = -1;
    long sliceTime = -1;
    long bestLegOneEnd = -1;
    long bestLegTwoStart = -1;

    /**
     * how long we allow for a turn (millis)
     * 
     */
    final long BUFFER_SIZE = 300 * 1000;

    // TODO - drop this object, it's just for debugging
    // DateFormat ds = new SimpleDateFormat("hh:mm:ss");

    // find the optimal first slice
    for (int index = 0; index < thisLegTimes.size(); index++)
    {
      final int legOneEnd = getEnd(0, thisLegTimes, BUFFER_SIZE, index);
      final int legTwoStart = getStart(0, thisLegTimes, BUFFER_SIZE, index);

      // check we have two legitimate legs
      if (legOneEnd != -1 && legTwoStart != -1)
      {
        // what's the total score for slicing at this index?
        final double sum =
            sliceLeg(index, thisLegBearings, thisLegTimes, legOneEnd,
                legTwoStart, optimiseTolerance);

        // System.out.println(ds.format(new Date(thisLegTimes.get(index))) + ", " + sum);

        // is this better?
        if ((sum != Double.MAX_VALUE) && (sum < bestScore))
        {
          // yes - store it.
          bestScore = sum;
          // bestSlice = index;
          sliceTime = thisLegTimes.get(index);
          bestLegOneEnd = thisLegTimes.get(legOneEnd);
          bestLegTwoStart = thisLegTimes.get(legTwoStart);
        }
      }
    }

    // right, how did we get on?
    if (sliceTime != -1)
    {
      // System.out.println(ds.format(new Date(sliceTime)));
      // System.out.println("Best score:" + bestScore + " whole score:" + wholeLegScore + " ratio:"
      // + (bestScore / wholeLegScore));

      // is this slice acceptable?
      if (bestScore < wholeLegScore * RMS_ZIG_RATIO)
      {
        legStorer.storeLeg(scenario, wholeStart, bestLegOneEnd, bestScore
            / wholeLegScore * 100);
        legStorer.storeLeg(scenario, bestLegTwoStart, wholeEnd, bestScore
            / wholeLegScore * 100);
        if (zigStorer != null)
        {
          zigStorer.storeZig(scenario, bestLegOneEnd, bestLegTwoStart,
              bestScore / wholeLegScore * 100);
        }
      }
      else
      {
        // right - we couldn't get a good slice. see what the whole score is
        // SATC_Activator.log(Status.INFO, "Couldn't slice: whole leg score:"
        // + wholeLegScore + " best slice:" + bestScore, null);

        // just store the whole slice
        legStorer.storeLeg(scenario, wholeStart, wholeEnd, wholeLegScore
            / wholeLegScore * 100);
      }
    }
    else
    {
      log.log(new Status(Status.INFO, PLUGIN_ID,
          "slicing complete, can't slice", null));
    }

    // and tell the storer that we're done.
    zigStorer.finish();

  }

}
