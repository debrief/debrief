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
package org.mwc.debrief.track_shift.zig_detector.target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.zig_detector.moving_average.TimeRestrictedMovingAverage;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;

import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

public class ZigDetector
{
  static class ArrayWalker
  {
    private final int _start;
    private final int _end;
    private int _current;
    private final int _step;

    private Boolean _lastAbove = null;
    private int _lastOpposite;
    private int _sameSideCount;
    private final String _name;
    private double _lastError = 0d;

    public ArrayWalker(final int start, final int end, final String name)
    {
      _start = start;
      _end = end;
      _name = name;

      _current = start;
      _step = _end > _start ? 1 : -1;
    }

    final public boolean getLastAbove()
    {
      return _lastAbove;
    }

    public double getLastError()
    {
      return _lastError;
    }

    final public int getLastOpposite()
    {
      return _lastOpposite;
    }

    final public int getSameSideCount()
    {
      return _sameSideCount;
    }

    public boolean hasNext()
    {
      return _current != _end + _step;
    }

    final public boolean justStarted()
    {
      return _lastAbove == null;
    }

    public Integer next()
    {
      // check we haven't passed the end
      if (!hasNext())
      {
        throw new IllegalArgumentException(
            "Can't call next() once we've passed the end");
      }

      final Integer res = _current;
      _current += _step;
      return res;
    }

    final public void setLastAbove(final boolean thisAbove)
    {
      _lastAbove = thisAbove;
    }

    public void setLastError(final double error2)
    {
      _lastError = error2;
    }

    final public void setLastOpposite(final int i)
    {
      _lastOpposite = i;
      _lastError = 0d;
    }

    final public void setSameSideCount(final int i)
    {
      _sameSideCount = i;
    }

    @Override
    public String toString()
    {
      return _name;
    }
  }

  private static interface EventHappened
  {
    public void eventAt(long time, double score, double threshold);
  }

  static class FlanaganArctan implements MinimisationFunction
  {
    private static double calcForecast(final double[] params,
        final double elapsedSecs)
    {
      final double B = params[0];
      final double P = params[1];
      final double Q = params[2];
      final double dX = Math.cos(Math.toRadians(B)) + Q * elapsedSecs;
      final double dY = Math.sin(Math.toRadians(B)) + P * elapsedSecs;
      double res = Math.toDegrees(Math.atan2(dY, dX));
      if (res < 0)
      {
        res += 360;
      }
      return res;
    }

    final private List<Long> _times;

    final private List<Double> _bearings;

    public FlanaganArctan(final List<Long> beforeTimes,
        final List<Double> beforeBearings)
    {
      _times = beforeTimes;
      _bearings = beforeBearings;
    }

    // evaluation function
    @Override
    public double function(final double[] params)
    {
      double runningSum = 0;

      // ok, loop through the data
      for (int i = 0; i < _times.size(); i++)
      {
        final double thisForecast = calcForecast(params, _times.get(i));
        final double thisMeasured = _bearings.get(i);
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
      // normalise by dividing by the number cuts - 3
      // (the -3 is relates to the number of variables being considered)
      final double mean = runningSum / (_times.size() - 3);

      return mean;

      // final double rms = Math.sqrt(mean);
      // return rms;
    }

  }

  static class FlanaganArctan_Legacy implements MinimisationFunction
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

    public FlanaganArctan_Legacy(final List<Long> beforeTimes,
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

  final static class Helpers
  {

    abstract static class BaseHelper implements WalkHelper
    {
      protected final TPeriod _thisLeg;

      private boolean _isGrowing = true;

      private final String _name;

      private BaseHelper(final TPeriod thisLeg, final String name)
      {
        _thisLeg = thisLeg;
        _name = name;
      }

      protected int getWalkerSize()
      {
        final int size = _thisLeg.end - _thisLeg.start;
        final int res = size / 2;
        return res;
      }

      @Override
      final public boolean isGrowing()
      {
        return _isGrowing;
      }

      @Override
      final public void stopGrowing()
      {
        _isGrowing = false;
      }

      @Override
      public String toString()
      {
        return _name;
      }
    }

    final static class DownHelper extends BaseHelper
    {
      private  int otherEnd = -1;

      public DownHelper(final TPeriod thisLeg)
      {
        super(thisLeg, "Down");
      }

      @Override
      public ArrayWalker getWalker(final List<Long> times)
      {
        return new ArrayWalker(getWalkerSize(), 0, "Down");
      }

      @Override
      public void grow()
      {
        _thisLeg.start = _thisLeg.start - 1;
      }

      @Override
      public void rememberOtherStart()
      {
        otherEnd = _thisLeg.end;
      }

      @Override
      public void restoreOtherEnd()
      {
        _thisLeg.end = otherEnd;
      }

      @Override
      public boolean smallEnough()
      {
        return _thisLeg.start > 0;
      }

      @Override
      public void storeEnd(final int lastOpposite)
      {
        _thisLeg.start = _thisLeg.start + lastOpposite;
      }

      @Override
      public int trimmed(final int i)
      {
        return i + 1;
      }
    }

    final static class UpHelper extends BaseHelper
    {
      private final int _outerLen;
      private int otherEnd;

      public UpHelper(final TPeriod thisLeg, final int outerLen)
      {
        super(thisLeg, "Up");
        _outerLen = outerLen;
      }

      @Override
      public ArrayWalker getWalker(final List<Long> times)
      {
        return new ArrayWalker(times.size() - getWalkerSize(), times.size() - 1,
            "Up");
      }

      @Override
      public void grow()
      {
        _thisLeg.end = _thisLeg.end + 1;
      }

      @Override
      public void rememberOtherStart()
      {
        otherEnd = _thisLeg.start;
      }

      @Override
      public void restoreOtherEnd()
      {
        _thisLeg.start = otherEnd;
      }

      @Override
      public boolean smallEnough()
      {
        return _thisLeg.end < _outerLen - 1;
      }

      @Override
      public void storeEnd(final int lastOpposite)
      {
        if (_thisLeg.start + lastOpposite == 52)
        {
          log("Leg:" + _thisLeg + " opp:" + lastOpposite);
        }

        _thisLeg.end = _thisLeg.start + lastOpposite;

      }

      @Override
      public int trimmed(final int i)
      {
        return i - 1;
      }
    }

    private static interface WalkHelper
    {

      ArrayWalker getWalker(List<Long> times);

      void grow();

      boolean isGrowing();

      void rememberOtherStart();

      void restoreOtherEnd();

      boolean smallEnough();

      void stopGrowing();

      void storeEnd(int lastOpposite);

      int trimmed(int i);

    }

  }

  private static interface PeriodHandler
  {
    /**
     * do something using this time period
     * 
     * @param innerPeriod
     * @param score
     *
     */
    void doIt(final TPeriod innerPeriod, double score);
  }

  protected static class ScoredTime implements Comparable<ScoredTime>
  {
    private final long _time;
    private final double _score;

    public ScoredTime(final long time, final double score)
    {
      _time = time;
      _score = score;
    }

    @Override
    public int compareTo(final ScoredTime o)
    {
      final Long myTime = _time;
      final Long hisTime = o._time;
      return myTime.compareTo(hisTime);
    }
  }

  static class TPeriod implements Comparable<TPeriod>
  {
    public int start;
    public int end;

    public TPeriod(final int start, final int end)
    {
      this.start = start;
      this.end = end;
    }

    @Override
    public int compareTo(final TPeriod other)
    {
      return new Integer(start).compareTo(other.start);
    }

    @Override
    public boolean equals(final Object arg0)
    {
      if (arg0 instanceof TPeriod)
      {
        final TPeriod other = (TPeriod) arg0;
        return other.start == start && other.end == end;
      }
      else
      {
        return false;
      }
    }

    @Override
    public int hashCode()
    {
      return start * 10000 + end;
    }

    @Override
    public String toString()
    {
      return "Period:" + start + "-" + end;
    }

    public String toString(final List<Long> thisTimes)
    {

      if (start >= thisTimes.size() || end >= thisTimes.size() || start == -1
          || end == -1)
      {
        System.out.println("Trouble");
        return "Period:" + start + "-" + end + ", " + thisTimes.size();// + thisTimes.get(start) +
                                                                       // "secs- " +
                                                                       // thisTimes.get(end) +
                                                                       // "secs";
      }
      else
      {
        String dates;
        if(thisTimes.get(start) < 10000000)
        {
          dates = thisTimes.get(start) + "-"
              + thisTimes.get(end) + " secs" ;
        }
        else
        {
          dates  = new Date(thisTimes.get(start)) + "-" + new Date(thisTimes
              .get(end));
        }
        return "Period:" + start + "-" + end + ", " + dates;
      }

    }

  }

  private static int ctr = 0;

  static int calculateNewStart(final List<Long> legTimes,
      final int startPoint, final long interval)
  {
    final long startValue = legTimes.get(startPoint);
    for (int i = startPoint; i < legTimes.size(); i++)
    {
      final long thisValue = legTimes.get(i);
      if (Math.abs(thisValue - startValue) >= interval)
      {
        return i;
      }
    }
    return legTimes.size() - 1;
  }

  /**
   * go through the data, and find the window (of specified min period) with the lowest mean bearing
   * rate. So, it doens't have to be steady bearing, but steady bearing rate.
   *
   * @param legTimes
   *          times of measurements
   * @param legBearings
   *          measured bearings
   * @param periodMillis
   *          period we're inspecting
   * @return
   */
  static TPeriod findLowestRateIn(final List<Long> legTimes,
      final List<Double> legBearings, final long periodSecs)
  {
    // check the period is long enough
    final long dataPeriod = legTimes.get(legTimes.size() - 1) - legTimes.get(0);
    final TPeriod res;
    if (dataPeriod < periodSecs)
    {
      res = null;
    }
    else
    {
      final SortedMap<Double, TPeriod> scores = new TreeMap<Double, TPeriod>();

      final boolean examine = legTimes.get(0) == 4611;

      // work through all the window start times
      for (int i = 0; i < legTimes.size(); i++)
      {
        // ok, build up a list of values from this index to the window size
        double runningSum = 0;
        int ctr = 0;
        int lastProcessed = -1;
        Double lastBDelta = null;
        final long startTime = legTimes.get(i);

        if (examine)
          System.out.println("== " + startTime);

        // check why we're not getting the flattest period in the second run of this method

        for (int j = i + 1; j < legTimes.size(); j++)
        {
          final long thisTime = legTimes.get(j);

          if (thisTime <= startTime + periodSecs)
          {
            lastProcessed = j;

            double bDelta = Math.abs(legBearings.get(j) - legBearings.get(j
                - 1));

            // check for passing through 360
            if (bDelta > 180)
            {
              bDelta = Math.abs(bDelta - 360d);
            }

            // do know the previous delta bearing?
            if (lastBDelta != null)
            {
              // yes, we can compare them
              final double bDelta2 = Math.abs(lastBDelta - bDelta);
              final double tDelta = legTimes.get(j) - legTimes.get(j - 1);
              final double bDelta2Rate = bDelta2 / tDelta;

              runningSum += bDelta2Rate;
              ctr++;

              if (examine)
              {
                java.text.DecimalFormat df = new java.text.DecimalFormat(
                    " #0.0000000;-#0.0000000");
                System.out.println(thisTime + ", " + df.format(legBearings.get(
                    j)) + ", " + df.format(bDelta) + ", " + df.format(bDelta2)
                    + ", " + df.format(runningSum));
              }

            }
            lastBDelta = bDelta;
          }
          else
          {
            break;
          }
        }

        if (ctr > 1)
        {
          // ok, store the score
          final TPeriod thisP = new TPeriod(i, lastProcessed);
          final double meanRate = runningSum / ctr;
          final double rootMeanRate = meanRate;
          double startT = legTimes.get(thisP.start);
          double endT = legTimes.get(thisP.end);

          // we may end up finishing slightly before the required period.
          // let's stay relaxed about it
          final double relaxedFinish = periodSecs * 0.8;

          if (endT - startT >= relaxedFinish)
          {
            scores.put(rootMeanRate, thisP);

            if (examine)
            {
              java.text.DecimalFormat df = new java.text.DecimalFormat(
                  "0.0000000");
              System.out.println(df.format(rootMeanRate) + " from:" + ctr
                  + " items, total:" + df.format(runningSum) + ", " + thisP
                      .toString(legTimes));
            }

            // double time = startT + (endT - startT) / 2;
            // System.out.println(time + ", " + rootMeanRate * 100);
          }
        }
      }

      // get the lowest score, if we have any
      res = scores.size() > 1 ? scores.values().iterator().next() : null;
    }
    return res;
  }

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
  static int getEnd(final int start, final List<Long> thisLegTimes,
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
      final long thisTime = thisLegTimes.get(ctr);

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
  static int getStart(final int start, final List<Long> thisLegTimes,
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
      final long thisTime = thisLegTimes.get(ctr);

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

  private static TPeriod growLeg(final double optimiseTolerance,
      final List<Long> outerTimes, final List<Double> outerBearings,
      final TPeriod thisLeg, final double validFit, final PeriodHandler deleter,
      final boolean deleteIfCantFit)
  {
    // handle receiving null leg, just return
    if (thisLeg == null)
    {
      return null;
    }

    // take safe copy of leg
    final TPeriod originalLeg = new TPeriod(thisLeg.start, thisLeg.end);

    final Helpers.WalkHelper downHelper = new Helpers.DownHelper(thisLeg);
    final Helpers.WalkHelper upHelper = new Helpers.UpHelper(thisLeg, outerTimes
        .size());

    // remember the value at hte other end
    downHelper.rememberOtherStart();
    upHelper.rememberOtherStart();

    TPeriod lastLeg = null;

    while (upHelper.isGrowing() || downHelper.isGrowing())
    {
      if (upHelper.isGrowing() && upHelper.smallEnough())
      {
        // ok - work it, girl
        upHelper.grow();
      }
      else
      {
        // ok, we can't grow any more. Stop growing
        upHelper.stopGrowing();
      }

      if (downHelper.isGrowing() && downHelper.smallEnough())
      {
        // ok - work it, girl
        downHelper.grow();
      }
      else
      {
        // ok, we can't grow any more. Stop growing
        downHelper.stopGrowing();
      }

      final boolean me = thisLeg.toString().equals("aPeriod:31-48");

      ctr++;

      // fit the curve
      final List<Long> times = outerTimes.subList(thisLeg.start, thisLeg.end);
      final List<Double> bearings = outerBearings.subList(thisLeg.start,
          thisLeg.end);
      final List<Long> zeroTimes = prepareTimes(times);
      
      // adjust the times to start at zero
      final Minimisation optimiser = optimiseThis(zeroTimes, bearings,
          optimiseTolerance);

      // check it worked
      if (!optimiser.getConvStatus())
      {
        log("can't converge. Max iterations:" + optimiser
            .getNiter());
        System.out.println("Ctr:" + ctr);
        // failed to converge. skip to the next one - see if more data will help
        continue;
      }

      final double thisScore = optimiser.getMinimum();

      final double[] coeff = optimiser.getParamValues();

      // if it's more than a few minutes, let's ditch it.
      final long elapsedTimeSecs = (zeroTimes.get(zeroTimes.size() - 1) - zeroTimes.get(0))
          / 1000L;

      // System.out.println(thisLeg.toString(thisTimes) + " Error score:"
      // + thisScore + " secs:" + elapsedTimeSecs + " error/item:" + (thisScore
      // / times.size()) + " growth:" + (thisScore / lastScore));

      if (me)
      {
        System.out.println("===" + thisLeg.toString());
        for (int j = 0; j < zeroTimes.size(); j++)
        {
          final long t = zeroTimes.get(j);

          double thisB = bearings.get(j);
          if (thisB < 0)
          {
            thisB += 360d;
          }

          System.out.println(t + ", " + thisB + ", " + FlanaganArctan
              .calcForecast(coeff, t));
        }
        System.out.println("===");
      }

      if (thisScore > validFit)
      {

        // ok. we;ve got a low quality fit. This probably
        // isn't an ArcTan period
        final boolean showIt = true;
        if (showIt)
        {
          for (int j = 0; j < times.size(); j++)
          {
            @SuppressWarnings("unused")
            final long t = times.get(j);

            double thisB = bearings.get(j);
            if (thisB < 0)
            {
              thisB += 360d;
            }

            // System.out.println(t + ", " + thisB + ", " + FlanaganArctan
            // .calcForecast(coeff, t));
          }
        }

        if (elapsedTimeSecs > 180)
        {
          if (lastLeg == null)
          {

            // ok, let's ditch this period, and move on.
            if (deleteIfCantFit)
            {
              deleter.doIt(thisLeg, thisScore);
              return null;
            }
            else
            {
              return originalLeg;
            }
          }
          else
          {
            // ok, we've grown. Let's just use the previous dimensions
            return lastLeg;
          }
        }
        else
        {
          // still short. let it grow a little more
          continue;
        }

      }
      // System.out.print("B:" + bearings.get(0).intValue() + ", " + " func:"
      // + optimiser.getMinimum() + ", ");
      // for (int i = 0; i < coeff.length; i++)
      // {
      // System.out.print(coeff[i] + ", ");
      // }
      // System.out.println();

      // to walk up, give it a few more values at the end
      final int extraLeg = 2;
      final int newStart = Math.max(thisLeg.start - extraLeg, 0);
      final int newEnd = Math.min(thisLeg.end + extraLeg, outerTimes.size() - 1);
      List<Long> tmpTimes = outerTimes.subList(thisLeg.start, newEnd);
      List<Double> tmpBearings = outerBearings.subList(thisLeg.start, newEnd);
      walkThisEnd(upHelper, tmpTimes, tmpBearings, coeff, thisLeg, downHelper);

      tmpTimes = outerTimes.subList(newStart, thisLeg.end);
      tmpBearings = outerBearings.subList(newStart, thisLeg.end);
      walkThisEnd(downHelper, tmpTimes, tmpBearings, coeff, thisLeg, upHelper);

      lastLeg = new TPeriod(thisLeg.start, thisLeg.end);
    }

    return thisLeg;
  }

  static void handleNewSlices(final List<TPeriod> sliceQueue,
      final List<TPeriod> legs, final TPeriod outerPeriod, final TPeriod newLeg,
      final int minLength, final boolean storeNewLeg)
  {

    // represent the new slice in overall values
    final TPeriod relative = new TPeriod(outerPeriod.start + newLeg.start,
        outerPeriod.start + newLeg.end);

    // remove this period
    sliceQueue.remove(outerPeriod);

    // add the bits either side to the queue
    if (relative.start - outerPeriod.start > minLength)
    {
      sliceQueue.add(new TPeriod(outerPeriod.start, relative.start - 1));
    }
    if (outerPeriod.end - relative.end > minLength)
    {
      sliceQueue.add(new TPeriod(relative.end + 1, outerPeriod.end));
    }

    // do we want to store the new leg?
    if (storeNewLeg)
    {
      legs.add(relative);
    }

    // and re-store the legs
    Collections.sort(legs);

    // ok, re-sort the queue
    Collections.sort(sliceQueue);
  }

  static void listSlices(final List<TPeriod> sliceQueue,
      final List<Long> legTimes)
  {
    for (final TPeriod p : sliceQueue)
    {
      if (legTimes != null)
      {
        log(legTimes.get(p.start) + "-" + legTimes.get(p.end));
      }
      else
      {
        log("" + p);
      }
    }
  }

  static void listSlicesForPlotting(final List<TPeriod> legs,
      final List<Long> legTimes)
  {
    int headCtr = 0;
    // output headers
    for (@SuppressWarnings("unused")
    final TPeriod t : legs)
    {
      System.out.print("Leg " + ++headCtr + ", ");
    }
    System.out.println();

    int colCtr = 0;
    final int numLegs = legs.size();

    if (numLegs == 0)
      return;

    for (int i = 0; i < legTimes.size(); i++)
    {
      boolean matched = false;
      for (final TPeriod t : legs)
      {

        if (t.start <= i && t.end >= i)
        {
          matched = true;

          // input blank rows
          for (int j = 0; j < colCtr; j++)
          {
            System.out.print(" #N/A, ");
          }
          // now the value
          System.out.print((1 + (colCtr % 2)) + ",");

          // now the remaining markers
          for (int j = 0; j < numLegs - colCtr - 1; j++)
          {
            System.out.print(" #N/A, ");
          }

          // is this the last one?
          if (t.end == i)
          {
            // ok, inc ctr
            colCtr++;
          }
        }
      }

      if (!matched)
      {
        // not covered by legs
        for (int j = 0; j < numLegs; j++)
        {
          System.out.print(" #N/A, ");
        }
      }

      System.out.println();
    }
  }

  final static private Minimisation optimiseThis(final List<Long> times,
      final List<Double> bearings, final double optimiserTolerance)
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

    // set the max number of iterations
    min.setNmax(116400);

    // convergence tolerance
    final double ftol = optimiserTolerance;

    // Nelder and Mead minimisation procedure
    min.nelderMead(funct, start, step, ftol);

    return min;
  }

  /**
   * put the bearings in the same domain, so we don't jump across 360
   *
   * @param raw
   *          set of raw bearings
   * @return processed bearings
   */
  static List<Double> prepareBearings(final List<Double> raw)
  {
    final List<Double> res = new ArrayList<Double>();
    for (int i = 0; i < raw.size(); i++)
    {
      final double thisCourse = raw.get(i);

      final double cleanValue;

      if (i == 0)
      {
        cleanValue = thisCourse;
      }
      else
      {
        final double lastCourse = res.get(i - 1);
        final double thisDiff = thisCourse - lastCourse;
        if (Math.abs(thisDiff) > 180d)
        {
          // ok, we've flippped
          if (thisDiff > 180)
          {
            // ok, deduct 360
            cleanValue = thisCourse - 360d;
          }
          else
          {
            // ok, add 360
            cleanValue = thisCourse + 360d;
          }
        }
        else
        {
          cleanValue = thisCourse;
        }
      }

      res.add(cleanValue);
    }

    return res;

  }

  /**
   * 
   * @param legTimes
   *          the list of times
   * @param legBearings
   *          the list of bearings
   * @param slicedTimes
   *          the outgoing list of blocks of time
   * @param slicedBearings
   *          the outgoing list of blocks of bearings
   * @param sepMillis
   *          the size of gap that causes a split
   */
  static void sliceIntoBlocks(final List<Long> legTimes,
      final List<Double> legBearings, final List<List<Long>> slicedTimes,
      final List<List<Double>> slicedBearings, final long sepMillis)
  {

    List<Long> thisTList = new ArrayList<Long>();
    List<Double> thisBList = new ArrayList<Double>();

    slicedTimes.add(thisTList);
    slicedBearings.add(thisBList);

    final int pCount = legTimes.size();
    for (int i = 0; i < pCount; i++)
    {
      final long thisT = legTimes.get(i);
      final double thisB = legBearings.get(i);

      if (thisTList.size() > 0)
      {
        // ok, we've got a previous entry. get it
        final long lastT = thisTList.get(thisTList.size() - 1);

        // how's the gap?
        if (thisT - lastT > sepMillis)
        {
          // ok. we're on a new leg
          thisTList = new ArrayList<Long>();
          thisBList = new ArrayList<Double>();
          slicedTimes.add(thisTList);
          slicedBearings.add(thisBList);
        }
      }
      thisTList.add(thisT);
      thisBList.add(thisB);
    }
  }

  private static void walkThisEnd(final Helpers.WalkHelper helper,
      final List<Long> times, final List<Double> bearings, final double[] coeff,
      final TPeriod thisLeg, final Helpers.WalkHelper otherHelper)
  {

    // System.out.println("Walking " + helper.toString() + " leg:" + thisLeg);
    final List<String> states = new ArrayList<String>();
    if (helper.isGrowing())
    {
      final ArrayWalker walker = helper.getWalker(times);
      // System.out.println("Walking: " + walker.toString());
      while (walker.hasNext() && helper.isGrowing())
      {
        final int i = walker.next();
        final long thisT = times.get(i);
        double measuredB = bearings.get(i);
        final double predictedB = FlanaganArctan.calcForecast(coeff, thisT);

        final boolean largeError = Math.abs(measuredB - predictedB) > 100;
        final boolean oppositeSides = measuredB < 0 && predictedB > 0;
        if (oppositeSides && largeError)
        {
          measuredB += 360d;
        }

        final double error = predictedB - measuredB;

        final boolean thisAbove = error > 0;

        String thisState = thisT + ", " + measuredB + ", " + predictedB + ", "
            + (thisAbove) + ", ";
        // System.out.println(thisState);
        states.add(thisState);

        final double absError = Math.abs(error);

        if (walker.justStarted())
        {
          // ok, initialise the oppositve value
          walker.setLastOpposite(helper.trimmed(i));
        }
        else if (thisAbove != walker.getLastAbove())
        {
          walker.setSameSideCount(0);
          walker.setLastOpposite(helper.trimmed(i));
        }
        else
        {
          final double lastScore = walker.getLastError();

          // how far the error needs to be from the last one, to
          // represent a divergence
          final double minDiff = 0.5d;

          // System.out.println("error at:" + thisT + " " + absError + ", "
          // + lastScore + ", " + (absError < lastScore + minDiff));

          if (absError < lastScore + minDiff)
          {
            walker.setSameSideCount(0);
            walker.setLastOpposite(helper.trimmed(i));
          }
          else
          {
            // error increasing
            final int curSameSide = walker.getSameSideCount();
            walker.setSameSideCount(curSameSide + 1);

            if (walker.getSameSideCount() == 3)
            {
              log(walker + ": stopping growing at:" + i + "("
                  + times.get(i) + ")" + " leg:" + thisLeg);
              helper.stopGrowing();
              helper.storeEnd(walker.getLastOpposite());
              log("End value updated to" + thisLeg);

              // ok, have a look.
              // for (final String s : states)
              // {
              // // System.out.println(s);
              // }

              // is the other end still walking? if it is, restart it.
              if (otherHelper.isGrowing())
              {
                helper.restoreOtherEnd();
                log("Leg restored to:" + thisLeg);
                continue;
              }
            }
          }
        }
        walker.setLastAbove(thisAbove);
        walker.setLastError(absError);
      }
    }
  }

  /**
   * make this dataset zero-based
   *
   * @param times
   *          regular time values
   * @return zero-based version of time dataset
   */
  private static List<Long> prepareTimes(final List<Long> times)
  {
    final long first = times.get(0);
    final List<Long> res = new ArrayList<Long>();
    for (final Long t : times)
    {
      res.add(t - first);
    }

    return res;
  }

  private void runThrough(final double optimiseTolerance,
      final List<Long> legTimes, final List<Double> legBearings,
      final EventHappened listener, final double zigThreshold,
      final long timeWindow)
  {

    final int len = legTimes.size();

    // java.text.DateFormat df = new SimpleDateFormat("HH:mm:ss");

    final TimeRestrictedMovingAverage avgScore =
        new TimeRestrictedMovingAverage(timeWindow, 3);

    /**
     * experimental regression analysis of data, it will let us forecast the next value, rather than
     * using the average
     */
    final SimpleRegression regression = new SimpleRegression();

    int start = 0;
    for (int end = 0; end < len; end++)
    {
      final long thisTime = legTimes.get(end);

      // we need at least 4 cuts
      if (end >= start + 4)
      {
        // ok, if we've got more than entries, just use the most recent onces
        // start = Math.max(start, end - 20);

        // aah, sub-list end point is exclusive, so we have to add one,
        // if we can
        final int increment;
        if (end < legTimes.size() - 2)
        {
          increment = 1;
        }
        else
        {
          increment = 0;
        }

        final List<Long> times = legTimes.subList(start, end + increment);
        final List<Double> bearings = legBearings.subList(start, end
            + increment);

        final Minimisation optimiser = optimiseThis(times, bearings,
            optimiseTolerance);
        final double score = optimiser.getMinimum();
        final double[] coeff = optimiser.getParamValues();
        for (int i = 0; i < times.size(); i++)
        {
          final long t = times.get(i);

          System.out.println(t + ", " + FlanaganArctan.calcForecast(coeff, t)
              + ", " + bearings.get(i));
        }

        final double[] values = optimiser.getParamValues();
        System.out.println("scores: B:" + values[0] + " P:" + values[1] + " Q:"
            + values[2]);

        @SuppressWarnings("unused")
        final double lastScore;
        if (avgScore.isEmpty())
        {
          lastScore = score;
        }
        else
        {
          lastScore = avgScore.lastValue();
        }

        // ok, see how things are going
        final double avg = avgScore.getAverage();

        // ok, is it increasing by more than double the variance?
        final double variance = avgScore.getVariance();

        // how far have we travelled from the last score?
        final double scoreDelta;
        if (avgScore.isEmpty())
        {
          scoreDelta = Double.NaN;
        }
        else
        {
          // scoreDelta = score - lastScore;
          scoreDelta = score - avg;
        }

        // what's the forecast
        @SuppressWarnings("unused")
        final double forecast = regression.predict(thisTime);

        // contribute this score
        avgScore.add(thisTime, score);

        // now add a value to the forecast
        regression.addData(thisTime, score);

        // final double thisProportion = scoreDelta / variance;
        final double thisProportion = scoreDelta / variance;

        // do we have enough data?
        if (avgScore.isPopulated())
        {

          // are we twice the variance?
          if (thisProportion > zigThreshold)
          {
            // System.out.println("this proportion:" + thisProportion);

            listener.eventAt(thisTime, thisProportion, zigThreshold);

            // System.out.println("diverging. delta:" + scoreDelta + ", variance:"
            // + variance + ", proportion:" + (scoreDelta / variance)
            // + " threshold:" + zigThreshold);

            // // ok, move the start past the turn
            start = calculateNewStart(legTimes, end, 120000);

            // and clear the moving average
            avgScore.clear();

            // and clear the regression
            regression.clear();
          }

        }
        else
        {
        }
      }
    }
  }

  /**
   *
   * @param optimiseTolerance
   * @param zigThreshold
   * @param timeWindowSecs
   * @param legs
   * @param legTimes
   * @param legBearings
   */
  void runThrough2(final double optimiseTolerance,
      final List<Long> fullTimes, final List<Double> fullBearings,
      final double zigThreshold, final List<TPeriod> legs)
  {
    final List<TPeriod> sliceQueue = new ArrayList<TPeriod>();

    // slice the data into contiguous blocks
    final List<List<Long>> slicedTimes = new ArrayList<List<Long>>();
    final List<List<Double>> slicedBearings = new ArrayList<List<Double>>();
    final long sepMillis = 600000;
    sliceIntoBlocks(fullTimes, fullBearings, slicedTimes, slicedBearings,
        sepMillis);

    // ok, now loop through them
    final int len = slicedTimes.size();
    for (int thisSlice = 0; thisSlice < len; thisSlice++)
    {
      final List<Long> legTimes = slicedTimes.get(thisSlice);
      final List<Double> legBearings = slicedBearings.get(thisSlice);

      // sort out the beraings
      final List<Double> legBearings1 = prepareBearings(legBearings);

      // give the times a zero offset
      final List<Long> zeroTimes = prepareTimes(legTimes);

      // initialise the list
      sliceQueue.add(new TPeriod(0, zeroTimes.size() - 1));

      while (!sliceQueue.isEmpty())
      {
        final TPeriod outerPeriod = sliceQueue.get(0);

        // create helper, to ditch data we can't use
        final PeriodHandler deleter = new PeriodHandler()
        {
          @Override
          public void doIt(final TPeriod innerPeriod, double score)
          {
            log("deleting:" + innerPeriod.toString(fullTimes)
                + " from " + outerPeriod.toString(fullTimes) + " score:"
                + score);
            // get rid of this period of data, create legs either side.
            handleNewSlices(sliceQueue, legs, outerPeriod, innerPeriod, 5,
                false);
          }
        };

        log("====");
        log("Analysing:" + outerPeriod.toString(fullTimes));

        // slice the data
        final List<Long> thisTimes = zeroTimes.subList(outerPeriod.start,
            outerPeriod.end);
        final List<Double> thisBearings = legBearings1.subList(
            outerPeriod.start, outerPeriod.end);

        final long minPeriod = 1000 * 60 * 7;
        // ok, find the period with the lowest bearing rate
        TPeriod thisLeg = findLowestRateIn(thisTimes, thisBearings, minPeriod);

        // check we can find a flat section
        if (thisLeg == null)
        {
          // ok, ditch this leg
          sliceQueue.remove(outerPeriod);
          continue;
        }

        showLeg("flattest:", fullTimes, thisLeg);

        final double validFit = 2.5d;

        // grow right first, since turns normally start more sharply
        // than they finish
        thisLeg = growLeg(optimiseTolerance, thisTimes, thisBearings, thisLeg,
            validFit, deleter, true);

        // have we finished growing?
        if (thisLeg != null)
        {
          if (thisLeg.toString().equals("aPeriod:30-35"))
          {
            System.out.println("bad");
          }
          showLeg("STORING:", fullTimes, thisLeg);
          handleNewSlices(sliceQueue, legs, outerPeriod, thisLeg, 5, true);
        }

        // return;
      }
    }
  }

  private void showLeg(final String msg, final List<Long> thisTimes,
      final TPeriod thisLeg)
  {
    if (thisLeg != null)
    {
      log(msg + thisLeg.toString(thisTimes));
    }
    else
    {
      log(msg + "NULL");
    }
  }
  
  private static void log(final String msg)
  {
    CorePlugin.logError(Status.INFO, msg, null);
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
   * @param legTimes
   *          bearing times
   * @param legBearings
   *          bearing values
   */
  public void sliceThis(final ILog log, final String PLUGIN_ID,
      final String scenario, final long wholeStart, final long wholeEnd,
      final ILegStorer legStorer, final IZigStorer zigStorer,
      final double RMS_ZIG_RATIO, final double optimiseTolerance,
      final List<Long> legTimes, final List<Double> rawLegBearings)
  {
    // check we have some
    if (legTimes.isEmpty())
    {
      return;
    }

    // ok, find the best slice
    // prepare the data
    final List<Double> legBearings = prepareBearings(rawLegBearings);

    if (legBearings.size() == 0)
    {
      return;
    }

    final Set<ScoredTime> zigStarts = new TreeSet<ScoredTime>();
    final Set<ScoredTime> zigEnds = new TreeSet<ScoredTime>();

    final EventHappened fwdListener = new EventHappened()
    {
      @Override
      public void eventAt(final long time, final double score,
          final double threshold)
      {
        // System.out
        // .println("zig start at:" + new Date(time) + " score:" + score);
        zigStarts.add(new ScoredTime(time, score));
      }
    };

    // double threshold = 0.002;
    final long timeWindow = 120000;

    runThrough(optimiseTolerance, legTimes, legBearings, fwdListener,
        RMS_ZIG_RATIO, timeWindow);

    // ok, now reverse the steps
    final EventHappened backListener = new EventHappened()
    {
      @Override
      public void eventAt(final long time, final double score,
          final double threshold)
      {
        // System.out.println("zig end at:" + new Date(time) + " score:" + score);
        zigEnds.add(new ScoredTime(time, score));
      }
    };

    Collections.reverse(legTimes);
    Collections.reverse(legBearings);

    // ok, now run through it
    final double reverseZigRation = RMS_ZIG_RATIO * 0.5;
    runThrough(optimiseTolerance, legTimes, legBearings, backListener,
        reverseZigRation, timeWindow);

    // note: we should share the zigs, not the ends.
    // the parent algorithm may be working through blocks
    // of sensor data that equate to ownship legs. We
    // don't wish to slice the data according to legs we've
    // identified (presumably starting at the first cut),
    // byt by legs of data
    final List<LegOfData> legs = new ArrayList<LegOfData>();
    Long lastZig = null;
    for (final ScoredTime legStart : zigEnds)
    {
      if (lastZig == null || legStart._time > lastZig)
      {
        // ok, we have start time. find the next leg end time
        for (final ScoredTime legEnd : zigStarts)
        {
          if (legEnd._time > legStart._time)
          {
            final LegOfData newLeg = new LegOfData("Leg:" + (legs.size() + 1),
                legStart._time, legEnd._time);
            // System.out.println("adding leg:" + newLeg);
            legs.add(newLeg);
            lastZig = legEnd._time;
            break;
          }
        }
      }
    }

    // refactor out storing zigs, to simplify method
    storeZigs(wholeEnd, zigStorer, zigStarts, zigEnds);

    // ok, share the good news
    for (final LegOfData leg : legs)
    {
      if (legStorer != null)
      {
        legStorer.storeLeg(leg.getName(), leg.getStart(), leg.getEnd(), 2d);
      }
    }

  }

  public void sliceThis2(final ILog log, final String PLUGIN_ID,
      final String scenario, final ILegStorer legStorer,
      final double RMS_ZIG_RATIO, final double optimiseTolerance,
      final List<Long> legTimes, final List<Double> rawLegBearings)
  {
    // check we have some
    if (legTimes.isEmpty())
    {
      return;
    }

    // ok, find the best slice
    // prepare the data
    final List<Double> legBearings = prepareBearings(rawLegBearings);

    if (legBearings.size() == 0)
    {
      return;
    }

    // double threshold = 0.002;
    // final long timeWindow = 120000;

    final List<TPeriod> legs = new ArrayList<TPeriod>();

    runThrough2(optimiseTolerance, legTimes, legBearings, RMS_ZIG_RATIO,
        legs);

    // ok, share the good news
    int ctr = 1;
    for (final TPeriod leg : legs)
    {
      if (legStorer != null)
      {
        legStorer.storeLeg("leg_" + ctr++, legTimes.get(leg.start), legTimes
            .get(leg.end), 2d);
      }
    }
  }

  private void storeZigs(final long wholeEnd, final IZigStorer zigStorer,
      final Set<ScoredTime> zigStarts, final Set<ScoredTime> zigEnds)
  {
    // ok, try to broadcast the zigs
    if (zigStorer != null)
    {
      // LegOfData lastLeg = null;
      for (final ScoredTime legEnd : zigStarts)
      {
        boolean matched = false;
        // ok, find the zig end that appears after thie
        for (final ScoredTime legStart : zigEnds)
        {
          if (legStart._time > legEnd._time)
          {
            // ok, this will do
            zigStorer.storeZig("Scenario", legEnd._time, legStart._time,
                legEnd._score);
            matched = true;
            break;
          }
        }

        if (!matched)
        {
          long newEnd = legEnd._time + 180000;

          // check the finish point is still in the scenario period
          // (put it 5 secs before the end, if necessary
          newEnd = Math.min(newEnd, wholeEnd - 5000);
          System.err.println("MANUALLY CLOSING ZIG time:" + new Date(
              legEnd._time) + " new end:" + new Date(newEnd));

          // ok, we didn't find a zig end. make one up, with a 3 min period
          zigStorer.storeZig("Scenario", legEnd._time, newEnd, legEnd._score);
        }
      }
    }
  }

}
