package org.mwc.debrief.track_shift.zig_detector.target;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.mwc.debrief.track_shift.zig_detector.moving_average.TimeRestrictedMovingAverage;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.osgi.framework.Bundle;

import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;
import junit.framework.TestCase;

public class ZigDetector
{
  private static class ArrayWalker
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
      int otherEnd = -1;

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
          System.out.println("Leg:" + _thisLeg + " opp:" + lastOpposite);
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
     * @param period
     */
    void doIt(final TPeriod innerPeriod);
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

  public static class TestMe extends TestCase
  {
    @SuppressWarnings("unused")
    private ILegStorer getLegStorer()
    {
      final ILegStorer legStorer = new ILegStorer()
      {

        @Override
        public void storeLeg(final String scenarioName, final long tStart,
            final long tEnd, final double rms)
        {
          // System.out.println("store it: " + new Date(tStart) + ", "
          // + new Date(tEnd));
        }
      };
      return legStorer;
    }

    @SuppressWarnings("unused")
    private ILog getLogger()
    {
      final ILog logger = new ILog()
      {

        @Override
        public void addLogListener(final ILogListener listener)
        {
          // not required, class just for testing
        }

        @Override
        public Bundle getBundle()
        {
          return null;
        }

        @Override
        public void log(final IStatus status)
        {
          // not required, class just for testing
        }

        @Override
        public void removeLogListener(final ILogListener listener)
        {
          // not required, class just for testing
        }
      };
      return logger;
    }

    @SuppressWarnings("unused")
    private IZigStorer getZigStorer()
    {
      final IZigStorer zigStorer = new IZigStorer()
      {

        @Override
        public void finish()
        {
          // TODO Auto-generated method stub

        }

        @Override
        public void storeZig(final String scenarioName, final long tStart,
            final long tEnd, final double rms)
        {
          // TODO Auto-generated method stub

        }
      };
      return zigStorer;
    }

    public void testCalcStart()
    {
      final List<Long> list = new ArrayList<Long>(Arrays.asList(new Long[]
      {0L, 40L, 100L, 140L, 180L, 220L, 260L, 280L}));
      assertEquals("correct time", 3, calculateNewStart(list, 1, 100));
      assertEquals("correct time", 5, calculateNewStart(list, 2, 100));
      assertEquals("correct time", 1, calculateNewStart(list, 0, 20));

      // and a reverse list
      Collections.reverse(list);
      assertEquals("correct time", 4, calculateNewStart(list, 1, 100));
      assertEquals("correct time", 5, calculateNewStart(list, 2, 100));
      assertEquals("correct time", 2, calculateNewStart(list, 0, 30));

    }

    public void testCleanValues()
    {
      List<Double> list = new ArrayList<Double>();
      list.add(355d);
      list.add(4d);
      list.add(2d);
      list.add(349d);
      list.add(2d);
      list = prepareBearings(list);

      assertEquals("correct length", 5, list.size());
      assertEquals("fixed bearings", 364d, list.get(1), 0.001);
      assertEquals("fixed bearings", 362d, list.get(2), 0.001);
      assertEquals("fixed bearings", 349d, list.get(3), 0.001);
      assertEquals("fixed bearings", 362d, list.get(4), 0.001);

      // and the other way around
      list.clear();

      list.add(55d);
      list.add(354d);
      list.add(339d);
      list.add(9d);
      list.add(2d);
      list = prepareBearings(list);

      assertEquals("correct length", 5, list.size());
      assertEquals("fixed bearings", -6d, list.get(1), 0.001);
      assertEquals("fixed bearings", -21d, list.get(2), 0.001);
      assertEquals("fixed bearings", 9d, list.get(3), 0.001);
      assertEquals("fixed bearings", 2d, list.get(4), 0.001);

    }

    public void testContinuousSlicing()
    {
      final long sepSecs = 600;
      final Long[] times = new Long[]
      {0l, 50l, 100l, 150l, 300l, 500l, 800l, 1500l, 1700l, 2100l, 2800l, 3000l,
          3200l, 3900l};
      final Double[] bearings = new Double[]
      {0d, 5d, 10d, 15d, 30d, 50d, 80d, 150d, 170d, 210d, 280d, 300d, 320d,
          380d};
      final List<Long> tList = new ArrayList<Long>();
      tList.addAll(Arrays.asList(times));
      final List<Double> bList = new ArrayList<Double>();
      bList.addAll(Arrays.asList(bearings));

      final List<List<Long>> slicedT = new ArrayList<List<Long>>();
      final List<List<Double>> slicedB = new ArrayList<List<Double>>();
      sliceIntoBlocks(tList, bList, slicedT, slicedB, sepSecs);

      assertTrue("has times", slicedT.size() > 0);
      assertTrue("has bearings", slicedB.size() > 0);
      assertEquals("same len", slicedT.size(), slicedB.size());

      assertEquals("correct length", 4, slicedT.size());
      for (final List<Long> t : slicedT)
      {
        System.out.println("===");
        for (final Long l : t)
        {
          System.out.print(l + " ");
        }
        System.out.println("");
      }
    }

    public void testMinBearingRate()
    {

      final double[] times = new double[]
      {0, 17.99999981, 33.99999978, 47.99999991, 62.00000003, 82.00000031,
          98.00000028, 109.9999999, 125.9999999, 140, 157.9999998, 172,
          186.0000001, 200.0000002, 210.0000001, 227.9999999, 243.9999998, 258,
          272.0000001, 294.0000002, 312, 332.0000003, 358.0000001, 383.9999999,
          410.0000003, 436.0000001, 472.0000003, 504.0000003, 531.9999999,
          563.9999998, 592.0000001, 624, 657.9999998, 692.0000002, 727.9999998,
          764.0000001, 810.0000001, 840.0000002, 875.9999998, 907.9999998, 944,
          982.0000001, 1020, 1056, 1092, 1126, 1172, 1214, 1258, 1296, 1334,
          1372, 1404, 1426, 1456, 1486, 1514, 1540, 1562, 1590, 1624, 1652,
          1678, 1710, 1744, 1774, 1808, 1846, 1880, 1912, 1942, 1982, 2014,
          2050, 2082, 2118, 2154, 2186, 2220, 2246, 2276, 2298, 2324, 2354,
          2386, 2414, 2446, 2476, 2510, 2548, 2580, 2618, 2650, 2686, 2720,
          2758, 2790, 2822, 2852, 2886, 2922, 2952, 2980, 3012, 3038, 3066,
          3090, 3114, 3140, 3172, 3202, 3234, 3274, 3310, 3344, 3382, 3418,
          3448, 3478, 3508, 3542, 3570, 3606, 3642, 3676, 3710, 3746, 3778,
          3808, 3846, 3884, 3922, 3954, 3990, 4026, 4060, 4098, 4140, 4174,
          4214, 4254, 4288, 4324, 4360, 4398, 4440, 4484, 4512, 4542, 4574,
          4610, 4644, 4668, 4690, 4716, 4738, 4760, 4782, 4802, 4822, 4842,
          4852, 4864, 4880, 4900, 4916, 4932, 4952, 4972, 4992, 5014, 5040,
          5068, 5090, 5112, 5138, 5158, 5188, 5210, 5238, 5264, 5294, 5324,
          5358, 5390, 5416, 5444, 5476, 5514, 5556, 5592, 5634, 5680, 5720,
          5756, 5800, 5842, 5882, 5918, 5958, 6000, 6046, 6090, 6128, 6186,
          6222, 6264, 6300, 6334, 6370, 6408, 6448, 6488, 6534, 6576, 6620,
          6664, 6712};
      final Double[] bearings = new Double[]
      {-147d, -143.6, -140.3, -137.3, -134d, -130d, -125.9, -122.6, -117.9,
          -114.9, -111.3, -107.3, -103.6, -99.6, -95.6, -91.3, -87.9, -84.2,
          -80.6, -76.9, -72.6, -68.9, -65.6, -60.6, -56.6, -52.9, -50.2, -47.2,
          -45.5, -43.9, -42.2, -40.2, -38.9, -37.9, -36.9, -36.5, -36.2, -35.5,
          -34.9, -34.2, -33.5, -32.9, -32.2, -31.9, -31.2, -30.9, -29.9, -28.5,
          -27.9, -27.2, -25.5, -24.9, -23.9, -23.9, -26.2, -28.5, -29.5, -31.5,
          -32.5, -31.5, -31.5, -31.2, -31.2, -30.9, -30.5, -29.9, -28.9, -28.2,
          -27.5, -27.9, -26.5, -25.5, -24.9, -24.2, -23.5, -22.9, -21.9, -20.9,
          -19.9, -19.5, -18.9, -18.2, -17.5, -16.8, -15.5, -15.2, -14.5, -13.8,
          -13.2, -12.2, -11.5, -10.2, -9.8, -8.5, -7.5, -6.5, -5.5, -4.8, -3.5,
          -3.2, -2.2, -1.5, -0.8, -0.2, 1.2, 1.5, 2.2, 4.2, 4.8, 5.8, 6.8, 7.8,
          8.8, 10.2, 10.5, 11.5, 12.5, 13.2, 14.5, 14.8, 16.5, 17.5, 18.5, 19.9,
          21.2, 21.9, 22.9, 23.2, 24.5, 25.5, 26.5, 26.9, 27.5, 28.2, 28.5,
          29.2, 29.5, 29.9, 30.2, 30.9, 31.5, 31.9, 32.5, 32.5, 32.9, 32.9,
          32.9, 32.9, 33.2, 33.2, 32.9, 31.9, 30.2, 27.5, 23.9, 20.2, 14.8, 8.8,
          3.2, -3.8, -9.5, -14.8, -19.5, -24.9, -31.2, -36.2, -40.2, -46.5,
          -51.5, -55.6, -60.6, -66.6, -72.9, -77.9, -82.9, -87.9, -91.9, -96.9,
          -101.6, -104.9, -107.9, -111.6, -114.9, -117.6, -120.3, -122.3,
          -123.3, -123.3, -123.3, -123.3, -122.3, -120.9, -118.9, -118.3,
          -116.9, -116.3, -115.3, -114.3, -113.3, -111.9, -110.9, -110.3,
          -109.3, -108.6, -107.6, -106.6, -105.6, -104.9, -104.3, -103.6,
          -102.3, -100.9, -99.9, -97.9, -96.6, -95.3, -92.9, -91.9};

      final List<Long> timeList = new ArrayList<Long>();
      for (final double d : times)
      {
        timeList.add((long) d);
      }
      final List<Double> bearingList = new ArrayList<Double>();
      bearingList.addAll(Arrays.asList(bearings));

      final TPeriod minRate = findLowestRateIn(timeList, bearingList, 300L);
      System.out.println("flattest:" + minRate.toString(timeList));
      assertEquals("correct start", 200, minRate.start);
      assertEquals("correct end", 207, minRate.end);
    }

    public void testNonContinuousMultiSlice() throws ParseException
    {

      final double[] times = new double[]
      {0, 17.99999981, 33.99999978, 47.99999991, 62.00000003, 82.00000031,
          98.00000028, 109.9999999, 125.9999999, 140, 157.9999998, 172,
          186.0000001, 200.0000002, 210.0000001, 227.9999999, 243.9999998, 258,
          272.0000001, 294.0000002, 312, 332.0000003, 358.0000001, 383.9999999,
          410.0000003, 436.0000001, 472.0000003, 504.0000003, 531.9999999,
          563.9999998, 592.0000001, 624, 657.9999998, 692.0000002, 727.9999998,
          764.0000001, 810.0000001, 840.0000002, 875.9999998, 907.9999998, 944,
          982.0000001, 1020, 1056, 1092, 1126, 1172, 1214, 1258, 1296, 1334,
          1372, 1404, 1426, 1456, 1486, 1514, 1540, 1562, 1590, 1624, 1652,
          1678, 1710, 1744, 1774, 1808, 1846, 1880, 1912, 1942, 1982, 2014,
          2758, 2790, 2822, 2852, 2886, 2922, 2952, 2980, 3012, 3038, 3066,
          3090, 3114, 3140, 3172, 3202, 3234, 3274, 3310, 3344, 3382, 3418,
          3448, 3478, 3508, 3542, 3570, 3606, 3642, 3676, 3710, 3746, 3778,
          3808, 3846, 3884, 3922, 3954, 3990, 4026, 4060, 4098, 4140, 4174,
          4214, 4254, 4288, 4324, 4360, 4398, 4440, 4484, 4512, 4542, 4574,
          4610, 4644, 4668, 4690, 4716, 4738, 4760, 4782, 4802, 4822, 4842,
          4852, 4864, 4880, 4900, 4916, 4932, 4952, 4972, 4992, 5014, 5040,
          5068, 5090, 5112, 5138, 5158, 5188, 5210, 5238, 5264, 5294, 5324,
          5358, 5390, 5416, 5444, 5476, 5514, 5556, 5592, 5634, 5680, 5720,
          5756, 5800, 5842, 5882, 5918, 5958, 6000, 6046, 6090, 6128, 6186,
          6222, 6264, 6300, 6334, 6370, 6408, 6448, 6488, 6534, 6576, 6620,
          6664, 6712};
      final Double[] bearings = new Double[]
      {-147d, -143.6, -140.3, -137.3, -134d, -130d, -125.9, -122.6, -117.9,
          -114.9, -111.3, -107.3, -103.6, -99.6, -95.6, -91.3, -87.9, -84.2,
          -80.6, -76.9, -72.6, -68.9, -65.6, -60.6, -56.6, -52.9, -50.2, -47.2,
          -45.5, -43.9, -42.2, -40.2, -38.9, -37.9, -36.9, -36.5, -36.2, -35.5,
          -34.9, -34.2, -33.5, -32.9, -32.2, -31.9, -31.2, -30.9, -29.9, -28.5,
          -27.9, -27.2, -25.5, -24.9, -23.9, -23.9, -26.2, -28.5, -29.5, -31.5,
          -32.5, -31.5, -31.5, -31.2, -31.2, -30.9, -30.5, -29.9, -28.9, -28.2,
          -27.5, -27.9, -26.5, -25.5, -24.9, -24.2, -23.5, -22.9, -21.9, -20.9,
          -19.9, -19.5, -18.9, -18.2, -17.5, -16.8, -15.5, -15.2, -14.5, -13.8,
          -13.2, -12.2, -11.5, -10.2, -9.8, -8.5, -7.5, -6.5, -5.5, -4.8, -3.5,
          -3.2, -2.2, -1.5, -0.8, -0.2, 1.2, 1.5, 2.2, 4.2, 4.8, 5.8, 6.8, 7.8,
          8.8, 10.2, 10.5, 11.5, 12.5, 13.2, 14.5, 14.8, 16.5, 17.5, 18.5, 19.9,
          32.9, 32.9, 33.2, 33.2, 32.9, 31.9, 30.2, 27.5, 23.9, 20.2, 14.8, 8.8,
          3.2, -3.8, -9.5, -14.8, -19.5, -24.9, -31.2, -36.2, -40.2, -46.5,
          -51.5, -55.6, -60.6, -66.6, -72.9, -77.9, -82.9, -87.9, -91.9, -96.9,
          -101.6, -104.9, -107.9, -111.6, -114.9, -117.6, -120.3, -122.3,
          -123.3, -123.3, -123.3, -123.3, -122.3, -120.9, -118.9, -118.3,
          -116.9, -116.3, -115.3, -114.3, -113.3, -111.9, -110.9, -110.3,
          -109.3, -108.6, -107.6, -106.6, -105.6, -104.9, -104.3, -103.6,
          -102.3, -100.9, -99.9, -97.9, -96.6, -95.3, -92.9, -91.9};

      final Long[] normalTimes = new Long[times.length];
      final Long[] rawTimes = new Long[times.length];
      // final java.text.DateFormat sdf = new SimpleDateFormat("HHmmss");
      long startTime = 0;
      for (int i = 0; i < times.length; i++)
      {
        final long thisTime = (long) times[i];

        rawTimes[i] = thisTime;

        if (i == 0)
        {
          startTime = thisTime;
        }

        normalTimes[i] = (thisTime - startTime);
      }

      // start to collate the adta
      final List<Long> rawList1 = Arrays.asList(rawTimes);
      final List<Double> tBearings = Arrays.asList(bearings);

      final ZigDetector detector = new ZigDetector();
      double zigRatio = 1000d;
      final double optimiseTolerance = 0.0000000004;
      //
      final long timeWindow = 240;
      zigRatio = 15d;
      final EventHappened happened = new EventHappened()
      {
        @Override
        public void eventAt(final long time, final double score,
            final double threshold)
        {
          // System.out.println("event at " + new Date(time) + " score:" + score);
        }
      };

      final List<TPeriod> legs = new ArrayList<TPeriod>();

      detector.runThrough2(optimiseTolerance, rawList1, tBearings, happened,
          zigRatio, timeWindow, legs);

      listSlices(legs, rawList1);

      listSlicesForPlotting(legs, rawList1);
    }

    public void testMultiSlice() throws ParseException
    {

      final double[] times = new double[]
      {0, 17.99999981, 33.99999978, 47.99999991, 62.00000003, 82.00000031,
          98.00000028, 109.9999999, 125.9999999, 140, 157.9999998, 172,
          186.0000001, 200.0000002, 210.0000001, 227.9999999, 243.9999998, 258,
          272.0000001, 294.0000002, 312, 332.0000003, 358.0000001, 383.9999999,
          410.0000003, 436.0000001, 472.0000003, 504.0000003, 531.9999999,
          563.9999998, 592.0000001, 624, 657.9999998, 692.0000002, 727.9999998,
          764.0000001, 810.0000001, 840.0000002, 875.9999998, 907.9999998, 944,
          982.0000001, 1020, 1056, 1092, 1126, 1172, 1214, 1258, 1296, 1334,
          1372, 1404, 1426, 1456, 1486, 1514, 1540, 1562, 1590, 1624, 1652,
          1678, 1710, 1744, 1774, 1808, 1846, 1880, 1912, 1942, 1982, 2014,
          2050, 2082, 2118, 2154, 2186, 2220, 2246, 2276, 2298, 2324, 2354,
          2386, 2414, 2446, 2476, 2510, 2548, 2580, 2618, 2650, 2686, 2720,
          2758, 2790, 2822, 2852, 2886, 2922, 2952, 2980, 3012, 3038, 3066,
          3090, 3114, 3140, 3172, 3202, 3234, 3274, 3310, 3344, 3382, 3418,
          3448, 3478, 3508, 3542, 3570, 3606, 3642, 3676, 3710, 3746, 3778,
          3808, 3846, 3884, 3922, 3954, 3990, 4026, 4060, 4098, 4140, 4174,
          4214, 4254, 4288, 4324, 4360, 4398, 4440, 4484, 4512, 4542, 4574,
          4610, 4644, 4668, 4690, 4716, 4738, 4760, 4782, 4802, 4822, 4842,
          4852, 4864, 4880, 4900, 4916, 4932, 4952, 4972, 4992, 5014, 5040,
          5068, 5090, 5112, 5138, 5158, 5188, 5210, 5238, 5264, 5294, 5324,
          5358, 5390, 5416, 5444, 5476, 5514, 5556, 5592, 5634, 5680, 5720,
          5756, 5800, 5842, 5882, 5918, 5958, 6000, 6046, 6090, 6128, 6186,
          6222, 6264, 6300, 6334, 6370, 6408, 6448, 6488, 6534, 6576, 6620,
          6664, 6712};
      final Double[] bearings = new Double[]
      {-147d, -143.6, -140.3, -137.3, -134d, -130d, -125.9, -122.6, -117.9,
          -114.9, -111.3, -107.3, -103.6, -99.6, -95.6, -91.3, -87.9, -84.2,
          -80.6, -76.9, -72.6, -68.9, -65.6, -60.6, -56.6, -52.9, -50.2, -47.2,
          -45.5, -43.9, -42.2, -40.2, -38.9, -37.9, -36.9, -36.5, -36.2, -35.5,
          -34.9, -34.2, -33.5, -32.9, -32.2, -31.9, -31.2, -30.9, -29.9, -28.5,
          -27.9, -27.2, -25.5, -24.9, -23.9, -23.9, -26.2, -28.5, -29.5, -31.5,
          -32.5, -31.5, -31.5, -31.2, -31.2, -30.9, -30.5, -29.9, -28.9, -28.2,
          -27.5, -27.9, -26.5, -25.5, -24.9, -24.2, -23.5, -22.9, -21.9, -20.9,
          -19.9, -19.5, -18.9, -18.2, -17.5, -16.8, -15.5, -15.2, -14.5, -13.8,
          -13.2, -12.2, -11.5, -10.2, -9.8, -8.5, -7.5, -6.5, -5.5, -4.8, -3.5,
          -3.2, -2.2, -1.5, -0.8, -0.2, 1.2, 1.5, 2.2, 4.2, 4.8, 5.8, 6.8, 7.8,
          8.8, 10.2, 10.5, 11.5, 12.5, 13.2, 14.5, 14.8, 16.5, 17.5, 18.5, 19.9,
          21.2, 21.9, 22.9, 23.2, 24.5, 25.5, 26.5, 26.9, 27.5, 28.2, 28.5,
          29.2, 29.5, 29.9, 30.2, 30.9, 31.5, 31.9, 32.5, 32.5, 32.9, 32.9,
          32.9, 32.9, 33.2, 33.2, 32.9, 31.9, 30.2, 27.5, 23.9, 20.2, 14.8, 8.8,
          3.2, -3.8, -9.5, -14.8, -19.5, -24.9, -31.2, -36.2, -40.2, -46.5,
          -51.5, -55.6, -60.6, -66.6, -72.9, -77.9, -82.9, -87.9, -91.9, -96.9,
          -101.6, -104.9, -107.9, -111.6, -114.9, -117.6, -120.3, -122.3,
          -123.3, -123.3, -123.3, -123.3, -122.3, -120.9, -118.9, -118.3,
          -116.9, -116.3, -115.3, -114.3, -113.3, -111.9, -110.9, -110.3,
          -109.3, -108.6, -107.6, -106.6, -105.6, -104.9, -104.3, -103.6,
          -102.3, -100.9, -99.9, -97.9, -96.6, -95.3, -92.9, -91.9};

      final Long[] normalTimes = new Long[times.length];
      final Long[] rawTimes = new Long[times.length];
      // final java.text.DateFormat sdf = new SimpleDateFormat("HHmmss");
      long startTime = 0;
      for (int i = 0; i < times.length; i++)
      {
        final long thisTime = (long) times[i];

        rawTimes[i] = thisTime;

        if (i == 0)
        {
          startTime = thisTime;
        }

        normalTimes[i] = (thisTime - startTime);
      }

      // start to collate the adta
      final List<Long> rawList1 = Arrays.asList(rawTimes);

      final List<Double> tBearings1 = Arrays.asList(bearings);

      final List<Double> tBearings = tBearings1;

      final ZigDetector detector = new ZigDetector();
      double zigRatio = 1000d;
      final double optimiseTolerance = 0.0000000004;

      final long timeWindow = 240;
      zigRatio = 15d;
      final EventHappened happened = new EventHappened()
      {
        @Override
        public void eventAt(final long time, final double score,
            final double threshold)
        {
          // System.out.println("event at " + new Date(time) + " score:" + score);
        }
      };

      final List<TPeriod> legs = new ArrayList<TPeriod>();

      detector.runThrough2(optimiseTolerance, rawList1, tBearings, happened,
          zigRatio, timeWindow, legs);

      listSlices(legs, rawList1);

      listSlicesForPlotting(legs, rawList1);

      // final Double[] bearings =
      // new Double[]
      // {180d, 180.3, 180.7, 181d, 181.4, 181.7, 182.1, 182.5, 182.8, 183.2,
      // 183.6, 184.1, 184.5, 184.9, 185.3, 185.8, 186.3, 186.7, 187.2,
      // 187.7, 188.2, 188.8, 189.3, 189.8, 190.4, 191d, 191.6, 192.2,
      // 192.8, 193.4, 194.1, 194.8, 195.5, 196.2, 196.9, 197.6, 198.4,
      // 199.2, 200d, 200.8, 201.7, 202.6, 203.4, 204.4, 205.3, 206.1,
      // 206.7, 207.3, 207.9, 208.5, 209.2, 209.9, 210.6, 211.4, 212.2,
      // 213.1, 214d, 214.9, 215.9, 216.9, 218d, 219.1, 220.3, 221.6,
      // 223d, 224.4, 225.9, 227.4, 229.1, 230.8, 232.7, 234.6, 236.6,
      // 238.8, 241d, 243.4, 245.9, 248.2, 250.3, 252.3, 254.3, 256.1,
      // 257.9, 259.6, 261.2, 262.8, 264.3, 265.7, 267.1, 268.4, 269.7,
      // 270.9, 272d, 273.1, 274.2, 275.2, 276.2, 277.1, 278d, 278.8,
      // 279.7, 280.4, 281.2};
      //
      // final int[] timeStr =
      // new int[]
      // {120000, 120050, 120140, 120230, 120320, 120410, 120500, 120550,
      // 120640, 120730, 120820, 120910, 121000, 121050, 121140, 121230,
      // 121320, 121410, 121500, 121550, 121640, 121730, 121820, 121910,
      // 122000, 122050, 122140, 122230, 122320, 122410, 122500, 122550,
      // 122640, 122730, 122820, 122910, 123000, 123050, 123140, 123230,
      // 123320, 123410, 123500, 123550, 123640, 123730, 123820, 123910,
      // 124000, 124050, 124140, 124230, 124320, 124410, 124500, 124550,
      // 124640, 124730, 124820, 124910, 125000, 125050, 125140, 125230,
      // 125320, 125410, 125500, 125550, 125640, 125730, 125820, 125910,
      // 130000, 130050, 130140, 130230, 130320, 130410, 130500, 130550,
      // 130640, 130730, 130820, 130910, 131000, 131050, 131140, 131230,
      // 131320, 131410, 131500, 131550, 131640, 131730, 131820, 131910,
      // 132000, 132050, 132140, 132230, 132320, 132410, 132500};

      // Long[] times =
      // new Long[]
      // {1248237792000L, 1248237799000L, 1248237896000L, 1248237944000L,
      // 1248237990000L, 1248238098000L, 1248238177000L, 1248238249000L,
      // 1248238321000L, 1248238393000L, 1248238484000L, 1248238556000L,
      // 1248238624000L, 1248238695000L, 1248238759000L, 1248238843000L,
      // 1248238931000L, 1248239006000L, 1248239074000L, 1248239162000L,
      // 1248239277000L, 1248239353000L, 1248239444000L, 1248239520000L,
      // 1248239600000L, 1248239644000L, 1248239735000L, 1248239799000L,
      // 1248239891000L, 1248239951000L, 1248240030000L, 1248240090000L,
      // 1248240142000L, 1248240198000L, 1248240257000L, 1248240305000L};
      // Double[] bearings =
      // new Double[]
      // {295.8, 295.5, 293.5, 293.0, 292.8, 290.3, 289.0, 288.3, 288.0,
      // 288.0, 288.8, 288.8, 288.8, 289.8, 289.8, 291.0, 291.5, 292.3,
      // 292.3, 293.0, 293.5, 294.0, 294.3, 294.8, 294.8, 294.8, 295.8,
      // 295.8, 295.8, 296.5, 296.5, 297.5, 297.8, 298.3, 299.0, 299.5};

      // Long[] times2 =
      // new Long[]
      // {946697610000L, 946697640000L, 946697670000L, 946697700000L,
      // 946697730000L, 946697760000L, 946697790000L, 946697820000L,
      // 946697850000L, 946697880000L, 946697910000L, 946697940000L,
      // 946697970000L, 946698000000L, 946698030000L, 946698060000L,
      // 946698090000L, 946698120000L, 946698150000L, 946698180000L,
      // 946698210000L, 946698240000L, 946698270000L, 946698300000L,
      // 946698330000L, 946698360000L, 946698390000L, 946698420000L,
      // 946698450000L, 946698480000L, 946698510000L, 946698540000L,
      // 946698570000L, 946698600000L, 946698630000L, 946698660000L,
      // 946698690000L, 946698720000L, 946698750000L, 946698780000L,
      // 946698810000L, 946698840000L, 946698870000L, 946698900000L,
      // 946698930000L, 946698960000L, 946698990000L, 946699020000L,
      // 946699050000L, 946699080000L, 946699110000L, 946699140000L,
      // 946699170000L, 946699200000L, 946699230000L, 946699260000L,
      // 946699290000L, 946699320000L, 946699350000L, 946699380000L,
      // 946699410000L, 946699440000L, 946699470000L, 946699500000L,
      // 946699530000L, 946699560000L, 946699590000L, 946699620000L,
      // 946699650000L, 946699680000L, 946699710000L, 946699740000L,
      // 946699770000L, 946699800000L, 946699830000L, 946699860000L,
      // 946699890000L, 946699920000L, 946699950000L, 946699980000L,
      // 946700010000L, 946700040000L, 946700070000L, 946700100000L,
      // 946700130000L, 946700160000L, 946700190000L, 946700220000L,
      // 946700250000L, 946700280000L, 946700310000L, 946700340000L,
      // 946700370000L, 946700400000L, 946700430000L, 946700460000L,
      // 946700490000L, 946700520000L, 946700550000L, 946700580000L,
      // 946700610000L, 946700640000L, 946700670000L, 946700700000L,
      // 946700730000L, 946700760000L, 946700790000L, 946700820000L,
      // 946700850000L, 946700880000L, 946700910000L, 946700940000L,
      // 946700970000L};
      //
      // Double[] bearings2 =
      // new Double[]
      // {170.095, 170.566, 171.404, 172.021, 172.757, 173.25, 173.767,
      // 174.391, 174.958, 175.839, 176.485, 177.282, 177.66, 178.444,
      // 179.09, 179.671, -179.482, -178.846, -178.363, -177.853,
      // -177.173, -175.994, -175.115, -174.628, -174.019, -173.208,
      // -172.378, -171.79, -170.932, -170.251, -169.526, -168.751,
      // -168.123, -167.354, -166.331, -165.639, -164.767, -164.272,
      // -163.407, -162.441, -161.783, -161.074, -159.886, -158.873,
      // -158.367, -157.495, -156.606, -155.92, -154.829, -153.856,
      // -152.983, -152.355, -151.561, -151.01, -149.65, -149.143,
      // -148.211, -147.211, -146.283, -145.55, -145.102, -144.119,
      // -143.22, -143.185, -141.704, -140.562, -139.975, -139.124,
      // -138.346, -137.36, -137.276, -135.746, -135.333, -134.338,
      // -133.295, -132.577, -131.86, -131.143, -130.278, -129.278,
      // -128.344, -127.83199999999998, -127.107, -126.345,
      // -125.40799999999999, -124.49999999999999, -123.88299999999998,
      // -123.195, -122.52800000000002, -122.17599999999999, -121.21,
      // -120.267, -120.11499999999998, -119.31799999999998, -118.507,
      // -117.99500000000002, -117.689, -117.71000000000001, -117.36,
      // -117.09399999999998, -117.23799999999999, -117.01, -116.633,
      // -116.74, -116.40300000000002, -116.296, -116.158,
      // -115.85400000000001, -115.82, -115.777, -115.56000000000002,
      // -115.071, -114.71999999999998};
      //

      // Long[] times =
      // new Long[]
      // {946699110000L, 946699140000L, 946699170000L, 946699200000L,
      // 946699230000L, 946699260000L, 946699290000L, 946699320000L,
      // 946699350000L, 946699380000L, 946699410000L, 946699440000L,
      // 946699470000L, 946699500000L, 946699530000L, 946699560000L,
      // 946699590000L, 946699620000L, 946699650000L, 946699680000L,
      // 946699710000L, 946699740000L, 946699770000L, 946699800000L,
      // 946699830000L, 946699860000L, 946699890000L, 946699920000L,
      // 946699950000L, 946699980000L, 946700010000L, 946700040000L,
      // 946700070000L, 946700100000L, 946700130000L, 946700160000L,
      // 946700190000L, 946700220000L, 946700250000L, 946700280000L,
      // 946700310000L, 946700340000L, 946700370000L, 946700400000L,
      // 946700430000L, 946700460000L, 946700490000L, 946700520000L,
      // 946700550000L, 946700580000L, 946700610000L, 946700640000L,
      // 946700670000L, 946700700000L, 946700730000L, 946700760000L,
      // 946700790000L, 946700820000L, 946700850000L, 946700880000L,
      // 946700910000L, 946700940000L, 946700970000L};
      // Double[] bearings =
      // new Double[]
      // {207.017, 207.645, 208.439, 208.99, 210.35, 210.857, 211.789,
      // 212.789, 213.717, 214.45, 214.898, 215.881, 216.78, 216.815,
      // 218.296, 219.438, 220.025, 220.876, 221.654, 222.64, 222.724,
      // 224.254, 224.667, 225.662, 226.705, 227.423, 228.14, 228.857,
      // 229.722, 230.722, 231.656, 232.168, 232.893, 233.655, 234.592,
      // 235.5, 236.11700000000002, 236.805, 237.47199999999998, 237.824,
      // 238.79000000000002, 239.733, 239.88500000000002,
      // 240.68200000000002, 241.493, 242.005, 242.311, 242.29, 242.64,
      // 242.906, 242.762, 242.99, 243.36700000000002, 243.26,
      // 243.59699999999998, 243.704, 243.84199999999998, 244.146, 244.18,
      // 244.223, 244.44, 244.929, 245.28000000000003};

      // Long[] times = new Long[]{946699110000L, 946699140000L, 946699170000L, 946699200000L,
      // 946699230000L, 946699260000L, 946699290000L, 946699320000L, 946699350000L, 946699380000L,
      // 946699410000L, 946699440000L, 946699470000L, 946699500000L, 946699530000L, 946699560000L,
      // 946699590000L, 946699620000L, 946699650000L, 946699680000L, 946699710000L, 946699740000L,
      // 946699770000L, 946699800000L, 946699830000L, 946699860000L, 946699890000L, 946699920000L,
      // 946699950000L, 946699980000L, 946700010000L, 946700040000L, 946700070000L, 946700100000L,
      // 946700130000L, 946700160000L, 946700190000L, 946700220000L, 946700250000L, 946700280000L,
      // 946700310000L, 946700340000L, 946700370000L, 946700400000L, 946700430000L, 946700460000L,
      // 946700490000L, 946700520000L, 946700550000L, 946700580000L};
      // Double[] bearings = new Double[]{-152.983, -152.355, -151.561, -151.01, -149.65, -149.143,
      // -148.211, -147.211, -146.283, -145.55, -145.102, -144.119, -143.22, -143.185, -141.704,
      // -140.562, -139.975, -139.124, -138.346, -137.36, -137.276, -135.746, -135.333, -134.338,
      // -133.295, -132.577, -131.86, -131.143, -130.278, -129.278, -128.344, -127.83199999999998,
      // -127.107, -126.345, -125.40799999999999, -124.49999999999999, -123.88299999999998,
      // -123.195, -122.52800000000002, -122.17599999999999, -121.21, -120.267, -120.11499999999998,
      // -119.31799999999998, -118.507, -117.99500000000002, -117.689, -117.71000000000001, -117.36,
      // -117.09399999999998};

    }

    public void testSlicing()
    {
      final List<TPeriod> q = new ArrayList<TPeriod>();
      final List<TPeriod> legs = new ArrayList<TPeriod>();
      final TPeriod outer = new TPeriod(0, 100);
      q.add(outer);
      TPeriod leg = new TPeriod(20, 30);

      assertEquals("one item", 1, q.size());
      assertTrue("correct item", q.contains(outer));

      handleNewSlices(q, legs, outer, leg, 5, true);

      assertEquals("two items", 2, q.size());
      assertEquals("first item", q.get(0), new TPeriod(0, 19));
      assertEquals("second item", q.get(1), new TPeriod(31, 100));
      assertEquals("correct legs", 1, legs.size());

      leg = new TPeriod(30, 40);
      handleNewSlices(q, legs, q.get(1), leg, 5, true);

      assertEquals("three items", 3, q.size());

      assertEquals("first item", new TPeriod(0, 19), q.get(0));
      assertEquals("second item", new TPeriod(31, 60), q.get(1));
      assertEquals("third item", new TPeriod(72, 100), q.get(2));
      assertEquals("correct legs", 2, legs.size());

      // ok, leave a short one
      leg = new TPeriod(2, 10);

      handleNewSlices(q, legs, q.get(2), leg, 5, true);
      assertEquals("three items", 3, q.size());

      assertEquals("first item", new TPeriod(0, 19), q.get(0));
      assertEquals("second item", new TPeriod(31, 60), q.get(1));
      assertEquals("third item", new TPeriod(83, 100), q.get(2));
      assertEquals("correct legs", 3, legs.size());

      // ok, leave a short one
      leg = new TPeriod(13, 16);

      handleNewSlices(q, legs, q.get(0), leg, 5, true);
      assertEquals("three items", 3, q.size());

      assertEquals("second item", new TPeriod(0, 12), q.get(0));
      assertEquals("second item", new TPeriod(31, 60), q.get(1));
      assertEquals("third item", new TPeriod(83, 100), q.get(2));
      assertEquals("correct legs", 4, legs.size());

      // ok, leave a short one
      leg = new TPeriod(4, 14);

      handleNewSlices(q, legs, q.get(2), leg, 5, true);
      assertEquals("two items", 2, q.size());

      assertEquals("second item", new TPeriod(0, 12), q.get(0));
      assertEquals("second item", new TPeriod(31, 60), q.get(1));
      assertEquals("correct legs", 5, legs.size());

      // ok, leave a short one
      leg = new TPeriod(2, 6);
      handleNewSlices(q, legs, q.get(0), leg, 5, true);
      assertEquals("two items", 2, q.size());

      assertEquals("second item", new TPeriod(7, 12), q.get(0));
      assertEquals("second item", new TPeriod(31, 60), q.get(1));
      assertEquals("correct legs", 6, legs.size());

      // ok, leave a short one
      leg = new TPeriod(2, 6);
      handleNewSlices(q, legs, q.get(0), leg, 5, true);
      assertEquals("two items", 1, q.size());

      assertEquals("second item", new TPeriod(31, 60), q.get(0));
      assertEquals("correct legs", 7, legs.size());

      listSlices(legs, null);

    }

    public void testSorting()
    {
      final SortedMap<Double, TPeriod> scores = new TreeMap<Double, TPeriod>();
      scores.put(12d, new TPeriod(12, 2));
      scores.put(13d, new TPeriod(13, 2));
      scores.put(9d, new TPeriod(9, 2));
      scores.put(6d, new TPeriod(6, 2));

      final Iterator<TPeriod> iter = scores.values().iterator();
      assertEquals("lowest first", 6, iter.next().start);
      assertEquals("lowest first", 9, iter.next().start);

      for (final TPeriod t : scores.values())
      {
        System.out.println(t.start);
      }

    }

    public void testStartTimes()
    {
      assertEquals(2, 5 / 2);

      final List<Long> times = new ArrayList<Long>(Arrays.asList(new Long[]
      {1000L, 1200L, 1500L, 1800L, 2100L, 2400L, 2700L, 3000L, 3300L, 3600L,
          3900L}));

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

    public void testWalker()
    {
      final ArrayWalker walker1 = new ArrayWalker(0, 2, "up");
      assertTrue(walker1.hasNext());
      assertEquals((Integer) 0, walker1.next());
      assertTrue(walker1.hasNext());
      assertEquals((Integer) 1, walker1.next());
      assertTrue(walker1.hasNext());
      assertEquals((Integer) 2, walker1.next());
      assertFalse(walker1.hasNext());

      final ArrayWalker walker2 = new ArrayWalker(2, 0, "down");
      assertTrue(walker2.hasNext());
      assertEquals((Integer) 2, walker2.next());
      assertTrue(walker2.hasNext());
      assertEquals((Integer) 1, walker2.next());
      assertTrue(walker2.hasNext());
      assertEquals((Integer) 0, walker2.next());
      assertFalse(walker2.hasNext());

    }
  }

  private static class TPeriod implements Comparable<TPeriod>
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
      return "Period:" + start + "-" + end + ", " + thisTimes.get(start) + "-"
          + thisTimes.get(end) + " secs";
    }

  }

  static int ctr = 0;

  private static int calculateNewStart(final List<Long> legTimes,
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
  private static TPeriod findLowestRateIn(final List<Long> legTimes,
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

      // work through all the window start times
      for (int i = 0; i < legTimes.size(); i++)
      {
        // ok, build up a list of values from this index to the window size
        double runningSum = 0;
        int ctr = 0;
        int lastProcessed = -1;
        Double lastBDelta = null;
        final long startTime = legTimes.get(i);
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
              final double tDelta = legTimes.get(j) - legTimes.get(j - 2);
              final double bDelta2Rate = bDelta2 / tDelta;

              runningSum += (bDelta2Rate);
              ctr++;
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
          double startT = legTimes.get(thisP.start);
          double endT = legTimes.get(thisP.end);

          if (endT - startT >= periodSecs)
          {
            scores.put(meanRate, thisP);

            // DecimalFormat df = new DecimalFormat("0.0000000");
            // System.out.println(df.format(meanRate) + " from:" + ctr + " items, "
            // + thisP.toString(legTimes));
            //
            // double time = startT + (endT - startT) / 2;
            // System.out.println(time + ", " + meanRate * 100);
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
      final List<Long> thisTimes, final List<Double> thisBearings,
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
    final Helpers.WalkHelper upHelper = new Helpers.UpHelper(thisLeg, thisTimes
        .size());

    // remember the value at hte other end
    downHelper.rememberOtherStart();
    upHelper.rememberOtherStart();

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

      final boolean me = thisLeg.toString().equals("Period:127-149");

      ctr++;

      // fit the curve
      final List<Long> times = thisTimes.subList(thisLeg.start, thisLeg.end);
      final List<Double> bearings = thisBearings.subList(thisLeg.start,
          thisLeg.end);

      final Minimisation optimiser = optimiseThis(times, bearings,
          optimiseTolerance);

      // check it worked
      if (!optimiser.getConvStatus())
      {
        System.out.println("can't converge. Max iterations:" + optimiser
            .getNiter());
        System.out.println("Ctr:" + ctr);
        // failed to converge. skip to the next one - see if more data will help
        continue;
      }

      final double[] coeff = optimiser.getParamValues();

      // if it's more than a few minutes, let's ditch it.
      final long timeSecs = times.get(times.size() - 1) - times.get(0);

      System.out.println(thisLeg.toString(thisTimes) + " Error score:"
          + optimiser.getMinimum() + " secs:" + timeSecs + " error/item:"
          + (optimiser.getMinimum() / times.size()));
      // System.out.println("Growing:" + thisLeg);

      if (me)
      {
        System.out.println("==="  + thisLeg.toString());
        for (int j = 0; j < times.size(); j++)
        {
          final long t = times.get(j);

          double thisB = bearings.get(j);
          if (thisB < 0)
          {
            thisB += 360d;
          }

           System.out.println(t + ", " + thisB + ", " + FlanaganArctan
           .calcForecast(coeff, t));
        }
      }

      if (optimiser.getMinimum() > validFit)
      {

        // ok. we;ve got a low quality fit. This probably
        // isn't an ArcTan period

        if (true)
        {
          for (int j = 0; j < times.size(); j++)
          {
            final long t = times.get(j);

            double thisB = bearings.get(j);
            if (thisB < 0)
            {
              thisB += 360d;
            }

             System.out.println(t + ", " + thisB + ", " + FlanaganArctan
             .calcForecast(coeff, t));
          }
        }

        if (timeSecs > 180)
        {
          // ok, let's ditch this period, and move on.
          if (deleteIfCantFit)
          {
            deleter.doIt(thisLeg);
            return null;
          }
          else
          {
            return originalLeg;
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

      walkThisEnd(upHelper, times, bearings, coeff, thisLeg, downHelper);
      walkThisEnd(downHelper, times, bearings, coeff, thisLeg, upHelper);
    }

    return thisLeg;
  }

  private static void handleNewSlices(final List<TPeriod> sliceQueue,
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

  private static void listSlices(final List<TPeriod> sliceQueue,
      final List<Long> legTimes)
  {
    for (final TPeriod p : sliceQueue)
    {
      if (legTimes != null)
      {
        System.out.println(legTimes.get(p.start) + "-" + legTimes.get(p.end));
      }
      else
      {
        System.out.println(p);
      }
    }
  }

  private static void listSlicesForPlotting(final List<TPeriod> legs,
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
  private static List<Double> prepareBearings(final List<Double> raw)
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

  private static void sliceIntoBlocks(final List<Long> legTimes,
      final List<Double> legBearings, final List<List<Long>> slicedTimes,
      final List<List<Double>> slicedBearings, final long sepSecs)
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
        if (thisT - lastT > sepSecs)
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

        if (measuredB < 0 && predictedB > 0)
        {
          measuredB += 360d;
        }

        final double error = predictedB - measuredB;

        final boolean thisAbove = error > 0;

        states.add(thisT + ", " + measuredB + ", " + predictedB + ", "
            + (thisAbove));

        final double absError = Math.abs(error);

        if (walker.justStarted())
        {
          // ok, initialise the oppositve value
          walker.setLastOpposite(i);
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
          final double minDiff = 2d;

          if (absError < lastScore + minDiff)
          {
            walker.setSameSideCount(0);
          }
          else
          {
            // error increasing
            final int curSameSide = walker.getSameSideCount();
            walker.setSameSideCount(curSameSide + 1);

            if (walker.getSameSideCount() == 4)
            {
              System.out.println(walker + ": stopping growing at:" + i + "("
                  + times.get(i) + ")" + " leg:" + thisLeg);
              helper.stopGrowing();
              helper.storeEnd(walker.getLastOpposite());
              System.out.println("End value updated to" + thisLeg);

              // ok, have a look.
              for (final String s : states)
              {
                System.out.println(s);
              }

              // is the other end still walking? if it is, restart it.
              if (otherHelper.isGrowing())
              {
                helper.restoreOtherEnd();
                System.out.println("Leg restored to:" + thisLeg);
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
  private List<Long> prepareTimes(final List<Long> times)
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
   * @param legTimes
   * @param legBearings
   * @param listener
   * @param zigThreshold
   * @param timeWindowSecs
   * @param legs
   */
  private void runThrough2(final double optimiseTolerance,
      final List<Long> fullTimes, final List<Double> fullBearings,
      final EventHappened listener, final double zigThreshold,
      final long timeWindowSecs, final List<TPeriod> legs)
  {
    final List<TPeriod> sliceQueue = new ArrayList<TPeriod>();

    // slice the data into contiguous blocks
    final List<List<Long>> slicedTimes = new ArrayList<List<Long>>();
    final List<List<Double>> slicedBearings = new ArrayList<List<Double>>();
    final long sepSecs = 600;
    sliceIntoBlocks(fullTimes, fullBearings, slicedTimes, slicedBearings,
        sepSecs);

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
          public void doIt(final TPeriod innerPeriod)
          {
            System.out.println("deleting:" + innerPeriod.toString(fullTimes) + " from "
                + outerPeriod.toString(fullTimes));
            // get rid of this period of data, create legs either side.
            handleNewSlices(sliceQueue, legs, outerPeriod, innerPeriod, 5,
                false);
          }
        };

        System.out.println("=======");
        System.out.println("Analysing:" + outerPeriod.toString(zeroTimes));

        // slice the data
        final List<Long> thisTimes = zeroTimes.subList(outerPeriod.start,
            outerPeriod.end);
        final List<Double> thisBearings = legBearings1.subList(
            outerPeriod.start, outerPeriod.end);

        final long minPeriod = 60 * 10;
        // ok, find the period with the lowest bearing rate
        TPeriod thisLeg = findLowestRateIn(thisTimes, thisBearings, minPeriod);

        // check we can find a flat section
        if (thisLeg == null)
        {
          // ok, ditch this leg
          sliceQueue.remove(outerPeriod);
          continue;
        }

        showLeg("flattest:", thisTimes, thisLeg);

        final double validFit = 4d;

        // grow right first, since turns normally start more sharply
        // than they finish
        thisLeg = growLeg(optimiseTolerance, thisTimes, thisBearings, thisLeg,
            validFit, deleter, true);
        
        // have we finished growing?
        if (thisLeg != null)
        {
          if (thisLeg.toString().equals("Period:30-35"))
          {
            System.out.println("bad");
          }
          showLeg("STORING:", thisTimes, thisLeg);
          handleNewSlices(sliceQueue, legs, outerPeriod, thisLeg, 5, true);
        }
        
//        return;
      }
    }
  }

  private void showLeg(final String msg, final List<Long> thisTimes,
      final TPeriod thisLeg)
  {
    if (thisLeg != null)
    {
      System.out.println(msg + thisLeg.toString(thisTimes));
    }
    else
    {
      System.out.println(msg + "NULL");
    }
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
