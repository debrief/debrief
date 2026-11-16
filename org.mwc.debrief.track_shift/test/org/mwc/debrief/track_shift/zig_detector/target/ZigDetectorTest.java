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

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.track_shift.zig_detector.target.ZigDetector.*;
import org.mwc.debrief.track_shift.zig_detector.target.ZigDetector.*;
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
import org.junit.jupiter.api.Test;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.zig_detector.moving_average.TimeRestrictedMovingAverage;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.osgi.framework.Bundle;
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

public class ZigDetectorTest
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

	@Test
	public void testCalcStart()
  {
    final List<Long> list = new ArrayList<Long>(Arrays.asList(new Long[]
    {0L, 40L, 100L, 140L, 180L, 220L, 260L, 280L}));
    assertEquals(3, calculateNewStart(list, 1, 100), "correct time");
    assertEquals(5, calculateNewStart(list, 2, 100), "correct time");
    assertEquals(1, calculateNewStart(list, 0, 20), "correct time");

    // and a reverse list
    Collections.reverse(list);
    assertEquals(4, calculateNewStart(list, 1, 100), "correct time");
    assertEquals(5, calculateNewStart(list, 2, 100), "correct time");
    assertEquals(2, calculateNewStart(list, 0, 30), "correct time");

  }

	@Test
	public void testCleanValues()
  {
    List<Double> list = new ArrayList<Double>();
    list.add(355d);
    list.add(4d);
    list.add(2d);
    list.add(349d);
    list.add(2d);
    list = prepareBearings(list);

    assertEquals(5, list.size(), "correct length");
    assertEquals(364d, list.get(1), 0.001, "fixed bearings");
    assertEquals(362d, list.get(2), 0.001, "fixed bearings");
    assertEquals(349d, list.get(3), 0.001, "fixed bearings");
    assertEquals(362d, list.get(4), 0.001, "fixed bearings");

    // and the other way around
    list.clear();

    list.add(55d);
    list.add(354d);
    list.add(339d);
    list.add(9d);
    list.add(2d);
    list = prepareBearings(list);

    assertEquals(5, list.size(), "correct length");
    assertEquals(-6d, list.get(1), 0.001, "fixed bearings");
    assertEquals(-21d, list.get(2), 0.001, "fixed bearings");
    assertEquals(9d, list.get(3), 0.001, "fixed bearings");
    assertEquals(2d, list.get(4), 0.001, "fixed bearings");

  }

	@Test
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

    assertTrue(slicedT.size() > 0, "has times");
    assertTrue(slicedB.size() > 0, "has bearings");
    assertEquals(slicedT.size(), slicedB.size(), "same len");

    assertEquals(4, slicedT.size(), "correct length");
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

	@Test
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
      timeList.add((long) d * 1000L);
    }
    final List<Double> bearingList = new ArrayList<Double>();
    bearingList.addAll(Arrays.asList(bearings));

    final TPeriod minRate = findLowestRateIn(timeList, bearingList, 300000L);
    System.out.println("flattest:" + minRate.toString(timeList));
    assertEquals(202, minRate.start, "correct start");
    assertEquals(209, minRate.end, "correct end");
  }

	@Test
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
    // final long timeWindow = 240;
    zigRatio = 15d;

    final List<TPeriod> legs = new ArrayList<TPeriod>();

    detector.runThrough2(optimiseTolerance, rawList1, tBearings, zigRatio,
        legs);

    listSlices(legs, rawList1);

    listSlicesForPlotting(legs, rawList1);
  }

	@Test
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

    // final long timeWindow = 240;
    zigRatio = 15d;

    final List<TPeriod> legs = new ArrayList<TPeriod>();

    detector.runThrough2(optimiseTolerance, rawList1, tBearings, zigRatio,
         legs);

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

	@Test
	public void testSlicing()
  {
    final List<TPeriod> q = new ArrayList<TPeriod>();
    final List<TPeriod> legs = new ArrayList<TPeriod>();
    final TPeriod outer = new TPeriod(0, 100);
    q.add(outer);
    TPeriod leg = new TPeriod(20, 30);

    assertEquals(1, q.size(), "one item");
    assertTrue(q.contains(outer), "correct item");

    handleNewSlices(q, legs, outer, leg, 5, true);

    assertEquals(2, q.size(), "two items");
    assertEquals(q.get(0), new TPeriod(0, 19), "first item");
    assertEquals(q.get(1), new TPeriod(31, 100), "second item");
    assertEquals(1, legs.size(), "correct legs");

    leg = new TPeriod(30, 40);
    handleNewSlices(q, legs, q.get(1), leg, 5, true);

    assertEquals(3, q.size(), "three items");

    assertEquals(new TPeriod(0, 19), q.get(0), "first item");
    assertEquals(new TPeriod(31, 60), q.get(1), "second item");
    assertEquals(new TPeriod(72, 100), q.get(2), "third item");
    assertEquals(2, legs.size(), "correct legs");

    // ok, leave a short one
    leg = new TPeriod(2, 10);

    handleNewSlices(q, legs, q.get(2), leg, 5, true);
    assertEquals(3, q.size(), "three items");

    assertEquals(new TPeriod(0, 19), q.get(0), "first item");
    assertEquals(new TPeriod(31, 60), q.get(1), "second item");
    assertEquals(new TPeriod(83, 100), q.get(2), "third item");
    assertEquals(3, legs.size(), "correct legs");

    // ok, leave a short one
    leg = new TPeriod(13, 16);

    handleNewSlices(q, legs, q.get(0), leg, 5, true);
    assertEquals(3, q.size(), "three items");

    assertEquals(new TPeriod(0, 12), q.get(0), "second item");
    assertEquals(new TPeriod(31, 60), q.get(1), "second item");
    assertEquals(new TPeriod(83, 100), q.get(2), "third item");
    assertEquals(4, legs.size(), "correct legs");

    // ok, leave a short one
    leg = new TPeriod(4, 14);

    handleNewSlices(q, legs, q.get(2), leg, 5, true);
    assertEquals(2, q.size(), "two items");

    assertEquals(new TPeriod(0, 12), q.get(0), "second item");
    assertEquals(new TPeriod(31, 60), q.get(1), "second item");
    assertEquals(5, legs.size(), "correct legs");

    // ok, leave a short one
    leg = new TPeriod(2, 6);
    handleNewSlices(q, legs, q.get(0), leg, 5, true);
    assertEquals(2, q.size(), "two items");

    assertEquals(new TPeriod(7, 12), q.get(0), "second item");
    assertEquals(new TPeriod(31, 60), q.get(1), "second item");
    assertEquals(6, legs.size(), "correct legs");

    // ok, leave a short one
    leg = new TPeriod(2, 6);
    handleNewSlices(q, legs, q.get(0), leg, 5, true);
    assertEquals(1, q.size(), "two items");

    assertEquals(new TPeriod(31, 60), q.get(0), "second item");
    assertEquals(7, legs.size(), "correct legs");

    listSlices(legs, null);

  }

	@Test
	public void testSorting()
  {
    final SortedMap<Double, TPeriod> scores = new TreeMap<Double, TPeriod>();
    scores.put(12d, new TPeriod(12, 2));
    scores.put(13d, new TPeriod(13, 2));
    scores.put(9d, new TPeriod(9, 2));
    scores.put(6d, new TPeriod(6, 2));

    final Iterator<TPeriod> iter = scores.values().iterator();
    assertEquals(6, iter.next().start, "lowest first");
    assertEquals(9, iter.next().start, "lowest first");

    for (final TPeriod t : scores.values())
    {
      System.out.println(t.start);
    }

  }

	@Test
	public void testStartTimes()
  {
    assertEquals(2, 5 / 2);

    final List<Long> times = new ArrayList<Long>(Arrays.asList(new Long[]
    {1000L, 1200L, 1500L, 1800L, 2100L, 2400L, 2700L, 3000L, 3300L, 3600L,
        3900L}));

    assertEquals(-1, getEnd(0, times, 200, 0), "correct");
    assertEquals(-1, getEnd(0, times, 200, 1), "correct");
    assertEquals(-1, getEnd(0, times, 200, 2), "correct");
    assertEquals(2, getEnd(0, times, 200, 3), "correct");
    assertEquals(3, getEnd(0, times, 200, 4), "correct");
    assertEquals(4, getEnd(0, times, 200, 5), "correct");
    assertEquals(5, getEnd(0, times, 200, 6), "correct");
    assertEquals(6, getEnd(0, times, 200, 7), "correct");
    assertEquals(7, getEnd(0, times, 200, 8), "correct");
    assertEquals(8, getEnd(0, times, 200, 9), "correct");
    assertEquals(9, getEnd(0, times, 200, 10), "correct");

    assertEquals(2, getStart(0, times, 400, 0), "correct");
    assertEquals(2, getStart(0, times, 400, 1), "correct");
    assertEquals(3, getStart(0, times, 400, 2), "correct");
    assertEquals(4, getStart(0, times, 400, 3), "correct");
    assertEquals(5, getStart(0, times, 400, 4), "correct");
    assertEquals(6, getStart(0, times, 400, 5), "correct");
    assertEquals(7, getStart(0, times, 400, 6), "correct");
    assertEquals(8, getStart(0, times, 400, 7), "correct");
    assertEquals(-1, getStart(0, times, 400, 8), "correct");
    assertEquals(-1, getStart(0, times, 400, 9), "correct");
    assertEquals(-1, getStart(0, times, 400, 10), "correct");
  }

	@Test
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
