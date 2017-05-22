package org.mwc.debrief.track_shift.zig_detector.target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.mwc.debrief.track_shift.zig_detector.moving_average.TimeRestrictedMovingAverage;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.osgi.framework.Bundle;

import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

public class ZigDetector
{
  protected static class ScoredTime implements Comparable<ScoredTime>
  {
    private final long _time;
    private final double _score;

    public ScoredTime(long time, double score)
    {
      _time = time;
      _score = score;
    }

    @Override
    public int compareTo(ScoredTime o)
    {
      Long myTime = _time;
      Long hisTime = o._time;
      return myTime.compareTo(hisTime);
    }
  }

  static class FlanaganArctan implements MinimisationFunction
  {
    private static double calcForecast(final double B, final double P,
        final double Q, final double elapsedSecs)
    {
      final double dX = Math.cos(Math.toRadians(B)) + Q * elapsedSecs;
      final double dY = Math.sin(Math.toRadians(B)) + P * elapsedSecs;
      return Math.toDegrees(Math.atan2(dY, dX));
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
    public double function(final double[] point)
    {
      final double B = point[0];
      final double P = point[1];
      final double Q = point[2];

      double runningSum = 0;
      final Long firstTime = _times.get(0);

      // ok, loop through the data
      for (int i = 0; i < _times.size(); i++)
      {
        final long elapsedMillis = Math.abs(_times.get(i) - firstTime);
        final double elapsedSecs = elapsedMillis / 1000d;
        final double thisForecast = calcForecast(B, P, Q, elapsedSecs);
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

    public void testCalcStart()
    {
      List<Long> list = new ArrayList<Long>(Arrays.asList(new Long[]
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

    public void testMultiSlice() throws ParseException
    {
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
      Double[] bearings =
          new Double[]
          {180d, 180.3, 180.7, 181d, 181.4, 181.7, 182.1, 182.5, 182.8, 183.2,
              183.6, 184.1, 184.5, 184.9, 185.3, 185.8, 186.3, 186.7, 187.2,
              187.7, 188.2, 188.8, 189.3, 189.8, 190.4, 191d, 191.6, 192.2,
              192.8, 193.4, 194.1, 194.8, 195.5, 196.2, 196.9, 197.6, 198.4,
              199.2, 200d, 200.8, 201.7, 202.6, 203.4, 204.4, 205.3, 206.1,
              206.7, 207.3, 207.9, 208.5, 209.2, 209.9, 210.6, 211.4, 212.2,
              213.1, 214d, 214.9, 215.9, 216.9, 218d, 219.1, 220.3, 221.6,
              223d, 224.4, 225.9, 227.4, 229.1, 230.8, 232.7, 234.6, 236.6,
              238.8, 241d, 243.4, 245.9, 248.2, 250.3, 252.3, 254.3, 256.1,
              257.9, 259.6, 261.2, 262.8, 264.3, 265.7, 267.1, 268.4, 269.7,
              270.9, 272d, 273.1, 274.2, 275.2, 276.2, 277.1, 278d, 278.8,
              279.7, 280.4, 281.2};

      int[] timeStr =
          new int[]
          {120000, 120050, 120140, 120230, 120320, 120410, 120500, 120550,
              120640, 120730, 120820, 120910, 121000, 121050, 121140, 121230,
              121320, 121410, 121500, 121550, 121640, 121730, 121820, 121910,
              122000, 122050, 122140, 122230, 122320, 122410, 122500, 122550,
              122640, 122730, 122820, 122910, 123000, 123050, 123140, 123230,
              123320, 123410, 123500, 123550, 123640, 123730, 123820, 123910,
              124000, 124050, 124140, 124230, 124320, 124410, 124500, 124550,
              124640, 124730, 124820, 124910, 125000, 125050, 125140, 125230,
              125320, 125410, 125500, 125550, 125640, 125730, 125820, 125910,
              130000, 130050, 130140, 130230, 130320, 130410, 130500, 130550,
              130640, 130730, 130820, 130910, 131000, 131050, 131140, 131230,
              131320, 131410, 131500, 131550, 131640, 131730, 131820, 131910,
              132000, 132050, 132140, 132230, 132320, 132410, 132500};

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

      Long[] times = new Long[timeStr.length];
      java.text.DateFormat sdf = new SimpleDateFormat("HHmmss");
      for (int i = 0; i < timeStr.length; i++)
      {
        String thisVal = "" + timeStr[i];

        times[i] = sdf.parse(thisVal).getTime();
      }

      // start to collate the adta
      List<Long> tList1 = Arrays.asList(times);
      List<Double> tBearings1 = Arrays.asList(bearings);

      // System.out
      // .println("last time:" + new Date(tList1.get(tList1.size() - 1)));

      // get the last 40 elements
      // final int start = tList1.size() - 50;
      // final int end = tList1.size() - 1;
      //
      // List<Long> tList = tList1.subList(start, end);
      // List<Double> tBearings = tBearings1.subList(start, end);

      List<Long> tList = tList1;
      List<Double> tBearings = tBearings1;

      // System.out.println("from:" + new Date(times[0]) + " // to:"
      // + new Date(times[times.length - 1]) + " // " + times.length + " entries");

      final ZigDetector detector = new ZigDetector();
      double zigRatio = 1000d;
      double optimiseTolerance = 0.0000000004;

      // ILog logger = getLogger();
      // ILegStorer legStorer = getLegStorer();
      // IZigStorer zigStorer = getZigStorer();
      // detector.sliceThis(logger, "some name", "scenario", times[0],
      // times[times.length - 1], legStorer, zigStorer, zigRatio,
      // optimiseTolerance, tList, tBearings);

      // reverse the arrays
      // Collections.reverse(tList);
      // Collections.reverse(tBearings);
      //
      long timeWindow = 240000;
      zigRatio = 15d;
      EventHappened happened = new EventHappened()
      {
        public void eventAt(long time, double score, double threshold)
        {
          // System.out.println("event at " + new Date(time) + " score:" + score);
        }
      };
      detector.runThrough(optimiseTolerance, tList, tBearings, happened,
          zigRatio, timeWindow);

    }

    @SuppressWarnings("unused")
    private ILegStorer getLegStorer()
    {
      ILegStorer legStorer = new ILegStorer()
      {

        @Override
        public void storeLeg(String scenarioName, long tStart, long tEnd,
            double rms)
        {
          // System.out.println("store it: " + new Date(tStart) + ", "
          // + new Date(tEnd));
        }
      };
      return legStorer;
    }

    @SuppressWarnings("unused")
    private IZigStorer getZigStorer()
    {
      IZigStorer zigStorer = new IZigStorer()
      {

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

        }
      };
      return zigStorer;
    }

    @SuppressWarnings("unused")
    private ILog getLogger()
    {
      ILog logger = new ILog()
      {

        @Override
        public void addLogListener(ILogListener listener)
        {
          //  not required, class just for testing
        }

        @Override
        public Bundle getBundle()
        {
          return null;
        }

        @Override
        public void log(IStatus status)
        {
          //  not required, class just for testing
        }

        @Override
        public void removeLogListener(ILogListener listener)
        {
          //  not required, class just for testing
        }
      };
      return logger;
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
          optimiseThis_Legacy(beforeTimes, beforeBearings, beforeBearings
              .get(0), optimiserTolerance);
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
          optimiseThis_Legacy(afterTimes, afterBearings, afterBearings.get(0),
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
//
//  /**
//   * 
//   * @param log
//   *          the logger
//   * @param PLUGIN_ID
//   *          the id of the plugin that is runnign this
//   * @param scenario
//   *          the name of this scenario
//   * @param wholeStart
//   *          overall start time
//   * @param wholeEnd
//   *          overall end time
//   * @param legStorer
//   *          someone interested in legs
//   * @param zigStorer
//   *          someone interested in zigs
//   * @param RMS_ZIG_RATIO
//   *          how much better the slice has to be
//   * @param optimiseTolerance
//   *          when the ARC_TAN fit is good enough
//   * @param thisLegTimes
//   *          bearing times
//   * @param thisLegBearings
//   *          bearing values
//   */
//  public void sliceThis_Original(final ILog log, final String PLUGIN_ID,
//      final String scenario, final long wholeStart, final long wholeEnd,
//      final ILegStorer legStorer, IZigStorer zigStorer,
//      final double RMS_ZIG_RATIO, final double optimiseTolerance,
//      final List<Long> thisLegTimes, final List<Double> thisLegBearings)
//  {
//    // ok, find the best slice
//    // prepare the data
//
//    // final int len = thisLegTimes.size();
//    // for (int i = 0; i < len; i++)
//    // {
//    // System.out.print(thisLegTimes.get(i) + "L, ");
//    // }
//    // System.out.println("===");
//    // for (int i = 0; i < len; i++)
//    // {
//    // System.out.print(thisLegBearings.get(i) + ", ");
//    // }
//
//    if (thisLegBearings.size() == 0)
//    {
//      return;
//    }
//
//    final Minimisation wholeLeg =
//        optimiseThis_Legacy(thisLegTimes, thisLegBearings, thisLegBearings
//            .get(0), optimiseTolerance);
//    final double wholeLegScore = wholeLeg.getMinimum();
//
//    // ok, now have to slice it
//    double bestScore = Double.MAX_VALUE;
//    // int bestSlice = -1;
//    long sliceTime = -1;
//    long bestLegOneEnd = -1;
//    long bestLegTwoStart = -1;
//
//    /**
//     * how long we allow for a turn (millis)
//     * 
//     */
//    final long BUFFER_SIZE = 300 * 1000;
//
//    // TODO - drop this object, it's just for debugging
//    // DateFormat ds = new SimpleDateFormat("hh:mm:ss");
//
//    // find the optimal first slice
//    for (int index = 0; index < thisLegTimes.size(); index++)
//    {
//      final int legOneEnd = getEnd(0, thisLegTimes, BUFFER_SIZE, index);
//      final int legTwoStart = getStart(0, thisLegTimes, BUFFER_SIZE, index);
//
//      // check we have two legitimate legs
//      if (legOneEnd != -1 && legTwoStart != -1)
//      {
//        // what's the total score for slicing at this index?
//        final double sum =
//            sliceLeg(index, thisLegBearings, thisLegTimes, legOneEnd,
//                legTwoStart, optimiseTolerance);
//
//        // is this better?
//        if ((sum != Double.MAX_VALUE) && (sum < bestScore))
//        {
//          // yes - store it.
//          bestScore = sum;
//          // bestSlice = index;
//          sliceTime = thisLegTimes.get(index);
//          bestLegOneEnd = thisLegTimes.get(legOneEnd);
//          bestLegTwoStart = thisLegTimes.get(legTwoStart);
//        }
//      }
//    }
//
//    // right, how did we get on?
//    if (sliceTime != -1)
//    {
//      // is this slice acceptable?
//      if (bestScore < wholeLegScore * RMS_ZIG_RATIO)
//      {
//        legStorer.storeLeg(scenario, wholeStart, bestLegOneEnd, bestScore
//            / wholeLegScore * 100);
//        legStorer.storeLeg(scenario, bestLegTwoStart, wholeEnd, bestScore
//            / wholeLegScore * 100);
//        if (zigStorer != null)
//        {
//          zigStorer.storeZig(scenario, bestLegOneEnd, bestLegTwoStart,
//              bestScore / wholeLegScore * 100);
//        }
//      }
//      else
//      {
//        // right - we couldn't get a good slice. see what the whole score is
//        // SATC_Activator.log(Status.INFO, "Couldn't slice: whole leg score:"
//        // + wholeLegScore + " best slice:" + bestScore, null);
//
//        // just store the whole slice
//        legStorer.storeLeg(scenario, wholeStart, wholeEnd, wholeLegScore
//            / wholeLegScore * 100);
//      }
//    }
//    else
//    {
//      log.log(new Status(Status.INFO, PLUGIN_ID,
//          "slicing complete, can't slice", null));
//    }
//
//    // and tell the storer that we're done.
//    zigStorer.finish();
//
//  }

  private static interface EventHappened
  {
    public void eventAt(long time, double score, double threshold);
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
      final ILegStorer legStorer, IZigStorer zigStorer,
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

    // final int len = legTimes.size();
    // for (int i = 0; i < len; i++)
    // {
    // System.out.print(legTimes.get(i) + "L, ");
    // }
    // System.out.println("");
    // System.out.println("");
    // for (int i = 0; i < len; i++)
    // {
    // System.out.print(legBearings.get(i) + ", ");
    // }

    // System.out.println("## ZigDetector.sliceThis Slicing from:" + new Date(legTimes.get(0)) +
    // " to:"
    // + new Date(legTimes.get(legTimes.size() - 1)));

    if (legBearings.size() == 0)
    {
      return;
    }

    final Set<ScoredTime> zigStarts = new TreeSet<ScoredTime>();
    final Set<ScoredTime> zigEnds = new TreeSet<ScoredTime>();

    // include our start/end values
    // zigEnds.add(legTimes.get(0));
    // zigStarts.add(legTimes.get(legTimes.size() - 1));

    EventHappened fwdListener = new EventHappened()
    {
      @Override
      public void eventAt(long time, double score, double threshold)
      {
        // System.out
        // .println("zig start at:" + new Date(time) + " score:" + score);
        zigStarts.add(new ScoredTime(time, score));
      }
    };

    // double threshold = 0.002;
    long timeWindow = 120000;

    runThrough(optimiseTolerance, legTimes, legBearings, fwdListener,
        RMS_ZIG_RATIO, timeWindow);

    // ok, now reverse the steps
    EventHappened backListener = new EventHappened()
    {
      @Override
      public void eventAt(long time, double score, double threshold)
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
    List<LegOfData> legs = new ArrayList<LegOfData>();
    Long lastZig = null;
    for (ScoredTime legStart : zigEnds)
    {
      if (lastZig == null || legStart._time > lastZig)
      {
        // ok, we have start time. find the next leg end time
        for (ScoredTime legEnd : zigStarts)
        {
          if (legEnd._time > legStart._time)
          {
            LegOfData newLeg =
                new LegOfData("Leg:" + (legs.size() + 1), legStart._time,
                    legEnd._time);
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
    for (LegOfData leg : legs)
    {
      if (legStorer != null)
      {
        legStorer.storeLeg(leg.getName(), leg.getStart(), leg.getEnd(), 2d);
      }
    }

  }

  private void storeZigs(final long wholeEnd, IZigStorer zigStorer,
      final Set<ScoredTime> zigStarts, final Set<ScoredTime> zigEnds)
  {
    // ok, try to broadcast the zigs
    if (zigStorer != null)
    {
      // LegOfData lastLeg = null;
      for (ScoredTime legEnd : zigStarts)
      {
        boolean matched = false;
        // ok, find the zig end that appears after thie
        for (ScoredTime legStart : zigEnds)
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
          System.err.println("MANUALLY CLOSING ZIG time:" + new Date(legEnd._time) + " new end:" + new Date(newEnd));
          
          // ok, we didn't find a zig end. make one up, with a 3 min period
          zigStorer.storeZig("Scenario", legEnd._time, newEnd,
              legEnd._score);
        }
      }
    }
  }

  private void runThrough(final double optimiseTolerance,
      final List<Long> legTimes, final List<Double> legBearings,
      EventHappened listener, final double zigThreshold, final long timeWindow)
  {

    final int len = legTimes.size();

//    java.text.DateFormat df = new SimpleDateFormat("HH:mm:ss");

    TimeRestrictedMovingAverage avgScore =
        new TimeRestrictedMovingAverage(timeWindow, 3);

    /** experimental regression analysis of data, it will let
     * us forecast the next value, rather than using the average
     */
    SimpleRegression regression = new SimpleRegression();

//    final long firstT = legTimes.get(0);

    int start = 0;
    for (int end = 0; end < len; end++)
    {
      final long thisTime = legTimes.get(end);
      
//       Date legEnd = new Date(thisTime);       
//       if(legEnd.toString().contains("13:24:10"))
//       {
//         float dd = 45 * 34;
//         float ee = dd + 33;
//       }

      // we need at least 4 cuts
      if (end >= start + 4)
      {
        // ok, if we've got more than entries, just use the most recent onces
        // start = Math.max(start, end - 20);
        
        // aah, sub-list end point is exclusive, so we have to add one,
        // if we can
        final int increment;
        if(end < legTimes.size() - 2)
        {
          increment = 1;
        }
        else
        {
          increment = 0;
        }

        final List<Long> times = legTimes.subList(start, end + increment);
        final List<Double> bearings = legBearings.subList(start, end + increment);

        Minimisation optimiser =
            optimiseThis(times, bearings, optimiseTolerance);
        double score = optimiser.getMinimum();

        // double[] values = optimiser.getParamValues();
        // System.out.println("scores: B:" + values[0] + " P:" + values[1] + " Q:" + values[2]);

        // FlanaganArctan func = new FlanaganArctan(times, bearings);
        // double[] permutation = new double[]{179.7654684, -0.000123539292961681,
        // 0.000189892808700104};
        // double[] permutation = new double[]{179.766017, -0.000123519863656172,
        // 0.0001899251729596};
        // double score = func.function(permutation);

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
        double forecast = regression.predict(thisTime);
        
        // contribute this score
        avgScore.add(thisTime, score);
        
        // now add a value to the forecast
        regression.addData(thisTime, score);
        
        // final double thisProportion = scoreDelta / variance;
        final double thisProportion = scoreDelta / variance;

//        final long elapsed = (thisTime - firstT) / 1000;
//        NumberFormat nf = new DecimalFormat(" 0.000000;-0.000000");
//        System.out.println(df.format(new Date(thisTime)) + " " + elapsed + " "
//            + nf.format(avg) + " " + nf.format(score) + " "
//            + nf.format(scoreDelta) + " " + nf.format(variance) + " "
//            + nf.format(thisProportion) + " " + nf.format(forecast) + " " + nf.format (score / forecast));

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
          // still building up our initial population
          // mAverage.add(thisTime, score);

          // System.out.println(df.format(new Date(thisTime)) + ", " + avg + ", "
          // + score + ", " + scoreDelta + ", " + variance + ", " + scoreDelta
          // / variance + ", " + legBearings.get(end) + ", "
          // + (legBearings.get(end - 1) - legBearings.get(end)));

        }
      }
    }
  }

  private static int calculateNewStart(List<Long> legTimes, int startPoint,
      long interval)
  {
    final long startValue = legTimes.get(startPoint);
    for (int i = startPoint; i < legTimes.size(); i++)
    {
      long thisValue = legTimes.get(i);
      if (Math.abs(thisValue - startValue) >= interval)
      {
        return i;
      }
    }
    return legTimes.size() - 1;
  }

  /**
   * put the bearings in the same domain, so we don't jump across 360
   * 
   * @param raw
   *          set of raw bearings
   * @return processed bearings
   */
  private static List<Double> prepareBearings(List<Double> raw)
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
        double thisDiff = thisCourse - lastCourse;
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

  final private Minimisation optimiseThis_Legacy(final List<Long> times,
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

}
