/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.multipath2.model;

import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.multipath2.model.TimeDeltas.Observation;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

public class MultiPathModel
{

	public static class CalculationException extends RuntimeException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CalculationException(final String msg)
		{
			super(msg);
		}
	}

	public static class DataFormatException extends Exception
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DataFormatException(final String msg)
		{
			super(msg);
		}

	}

	/**
	 * keep a cache of calculate ranges, we'll keep on using them
	 * 
	 */
	private transient HashMap<String, WorldVector> _rangeCache;

	public MultiPathModel()
	{
		_rangeCache = new HashMap<String, WorldVector>();
	}

	/**
	 * get the measured profile
	 * 
	 * @param deltas
	 * @return a time series of measured time delays
	 */
	public TimeSeries getMeasuredProfileFor(final TimeDeltas deltas)
	{
		final TimeSeries res = new TimeSeries("Measured delay");

		if (deltas != null)
		{
			// ok, loop through the times
			final Iterator<Observation> iter = deltas.iterator();
			while (iter.hasNext())
			{
				final Observation thisO = iter.next();

				// what's this time?
				final HiResDate tNow = thisO.getDate();
				final double delay = thisO.getInterval();

				// and add it
				res.add(new FixedMillisecond(tNow.getDate().getTime()), delay);
			}
		}

		return res;
	}

	public static class MiracleFunction implements MinimisationFunction
	{
		private final WatchableList _primary;
		private final WatchableList _secondary;
		private final SVP _svp;
		private final TimeDeltas _times;
		private final MultiPathModel _model;
		private final TimeSeries _measuredTimes;

		public MiracleFunction(final WatchableList primary, final WatchableList secondary,
				final SVP svp, final TimeDeltas times)
		{
			_primary = primary;
			_secondary = secondary;
			_svp = svp;
			_times = times;

			// sort out the measured times
			_model = new MultiPathModel();
			_measuredTimes = _model.getMeasuredProfileFor(times);
		}

		@Override
		public double function(final double[] param)
		{
			// ok, sort out the calculated times
			final TimeSeries calcTimes = _model.getCalculatedProfileFor(_primary,
					_secondary, _svp, _times, param[0]);

			final int lenA = _measuredTimes.getItemCount();
			final int lenB = calcTimes.getItemCount();

			if (lenA != lenB)
			{
				throw new RuntimeException(
						"Measured and calculated datasets should be of equal length");
			}

			double runningError = 0;

			for (int i = 0; i < lenA; i++)
			{
				final double valA = _measuredTimes.getDataItem(i).getValue().doubleValue();
				final double valB = calcTimes.getDataItem(i).getValue().doubleValue();
				final double thisError = Math.pow(valB - valA, 2);
				runningError += thisError;
			}

			// log the calculation
			CorePlugin.logError(Status.INFO, "error:"
					+ (int) (runningError * 10000000d) + " for " + param[0], null);

			// done
			return runningError;
		}
	}

	public static class RangedMiracleFunction implements MinimisationFunction
	{
		private final SVP _svp;
		private final TimeDeltas _times;
		private final MultiPathModel _model;
		private final TimeSeries _measuredTimes;
		private final RangeValues _ranges;
		private final double _ownDepth;

		public RangedMiracleFunction(final RangeValues ranges, final SVP svp, final TimeDeltas times,
				final double ownDepth)
		{
			_ownDepth = ownDepth;
			_ranges = ranges;
			_svp = svp;
			_times = times;

			// sort out the measured times
			_model = new MultiPathModel();
			_measuredTimes = _model.getMeasuredProfileFor(times);
		}

		@Override
		public double function(final double[] param)
		{
			// ok, sort out the calculated times
			final TimeSeries calcTimes = _model.getCalculatedProfileFor(_ranges, _svp,
					_times, param[0], _ownDepth);

			final int lenA = _measuredTimes.getItemCount();
			final int lenB = calcTimes.getItemCount();

			if (lenA != lenB)
			{
				throw new RuntimeException(
						"Measured and calculated datasets should be of equal length");
			}

			double runningError = 0;

			for (int i = 0; i < lenA; i++)
			{
				final TimeSeriesDataItem measObs = _measuredTimes.getDataItem(i);
				final TimeSeriesDataItem calcObs = calcTimes.getDataItem(i);
				final double valA = measObs.getValue().doubleValue();
				final double valB = calcObs.getValue().doubleValue();
				final double thisError = Math.pow(valB - valA, 2);
				runningError += thisError;
			}

			// log the calculation
			CorePlugin.logError(Status.INFO, "error:"
					+ (int) (runningError * 10000000d) + " for " + param[0], null);

			// done
			return runningError;
		}
	}

	/**
	 * get the calculated profile
	 * 
	 * @param primary
	 *          the primary track
	 * @param secondary
	 *          the secondary track
	 * @param svp
	 *          the sound speed profile
	 * @param deltas
	 *          the series of time-delta observations
	 * @param targetDepth
	 *          target depth to experiment with
	 * @return a time series of calculated time delays
	 */
	public TimeSeries getCalculatedProfileFor(final WatchableList primary,
			final WatchableList secondary, final SVP svp, final TimeDeltas deltas, final double targetDepth)
	{
		Boolean oldPrimaryInterp = null;
		Boolean oldSecondaryInterp = null;

		final TimeSeries res = new TimeSeries("Calculated delay");

		if (primary instanceof TrackWrapper)
		{
			final TrackWrapper trk = (TrackWrapper) primary;
			oldPrimaryInterp = trk.getInterpolatePoints();
			trk.setInterpolatePoints(true);
		}
		if (secondary instanceof TrackWrapper)
		{
			final TrackWrapper trk = (TrackWrapper) secondary;
			oldSecondaryInterp = trk.getInterpolatePoints();
			trk.setInterpolatePoints(true);
		}

		// ok, loop through the times
		final Iterator<Observation> iter = deltas.iterator();
		while (iter.hasNext())
		{
			final Observation thisO = iter.next();

			// what's this time?
			final HiResDate tNow = thisO.getDate();
			final long timeVal = tNow.getDate().getTime();

			// find the locations
			final Watchable[] priLocs = primary.getNearestTo(tNow);
			final Watchable[] secLocs = secondary.getNearestTo(tNow);

			// do we have data
			if ((priLocs.length > 0) && (secLocs.length > 0))
			{
				final WorldLocation priLoc = priLocs[0].getLocation();
				final WorldLocation secLoc = secLocs[0].getLocation();

				// create a key for this range separation calculation
				final String thisKey = "" + priLoc.getLat() + priLoc.getLong()
						+ secLoc.getLat() + secLoc.getLong();

				// do we have this key
				WorldVector sep = _rangeCache.get(thisKey);
				if (sep == null)
				{
					// nope, better create it then
					sep = priLoc.subtract(secLoc);

					// and store it
					_rangeCache.put(thisKey, sep);
				}
				final double sepM = MWC.Algorithms.Conversions.Degs2m(sep.getRange());

				final double time_delay = calculateDelayFor(svp, targetDepth,
						priLoc.getDepth(), sepM);

				res.add(new FixedMillisecond(timeVal), time_delay);
			}
		}

		// restore the interpolation settings
		if (oldPrimaryInterp != null)
		{
			final TrackWrapper trk = (TrackWrapper) primary;
			trk.setInterpolatePoints(oldPrimaryInterp.booleanValue());
		}
		if (oldSecondaryInterp != null)
		{
			final TrackWrapper trk = (TrackWrapper) secondary;
			trk.setInterpolatePoints(oldSecondaryInterp.booleanValue());
		}

		return res;
	}

	private double calculateDelayFor(final SVP svp, final double targetDepth,
			final double hostDepth, final double sepM)
	{
		final double zR = hostDepth;
		final double zS = targetDepth;

		// now sort out the sound speeds
		final double cD = svp.getMeanSpeedBetween(zS, zR);
		final double cS = svp.getMeanSpeedBetween(0, zS);
		final double cR = svp.getMeanSpeedBetween(0, zR);

		// do the actual calculation
		final double time_delay = calculateDelay(sepM, zR, zS, cD, cS, cR);
		return time_delay;
	}

	private double calculateDelay(final double sepM, final double zR, final double zS, final double cD,
			final double cS, final double cR)
	{
		final double hD = sepM;
		final double lDirect = Math.sqrt(hD * hD + Math.pow(zS - zR, 2));

		final double lsHoriz = (hD * zS) / (zS + zR);
		final double lS = Math.sqrt(lsHoriz * lsHoriz + zS * zS);
		final double lR = Math.sqrt(Math.pow(hD - lsHoriz, 2) + zR * zR);

		final double timeSurface = lS / cS;
		final double timeReceiver = lR / cR;
		final double timeDirect = lDirect / cD;
		final double time_delay = (timeSurface + timeReceiver) - timeDirect;
		return time_delay;
	}

	// /////////////////////////////////////////////////
	// and the testing goes here
	// /////////////////////////////////////////////////
	public static class IntervalTest extends junit.framework.TestCase
	{
		// TODO FIX-TEST
		public void NtestNonMatchTimes()
		{
			final WorldLocation loc = new WorldLocation(2, 2, 30);
			final LabelWrapper primary = new LabelWrapper("Sensor", loc, Color.red);

			final TrackWrapper secondary = new TrackWrapper();

			final TimeDeltas deltas = getDeltas();

			final HiResDate start = deltas.getStartTime();
			final HiResDate end = deltas.getEndTime();
			final long interval = 10000;

			WorldLocation startLoc = loc.add(new WorldVector(Math.PI, 0.02, 0));

			for (long thisT = start.getDate().getTime() + 92000; thisT <= end
					.getDate().getTime() + interval; thisT += interval)
			{
				final HiResDate thisD = new HiResDate(thisT);
				final WorldLocation newLoc = new WorldLocation(startLoc.add(new WorldVector(
						Math.PI * 1.5, 0.005, 0)));
				final Fix newF = new Fix(thisD, newLoc, 0, 0);
				final FixWrapper newFw = new FixWrapper(newF);
				secondary.addFix(newFw);

				startLoc = newLoc;
			}

			final SVP svp = getSVP();

			// ok, go for the calc
			final MultiPathModel model = new MultiPathModel();
			final TimeSeries calc = model.getCalculatedProfileFor(primary, secondary, svp,
					deltas, 44);

			assertNotNull("got some results", calc);
			assertEquals("correct num return", 369, calc.getItemCount());

		}

		// TODO FIX-TEST
		public void NtestMe()
		{
			final WorldLocation loc = new WorldLocation(2, 2, 30);
			final LabelWrapper primary = new LabelWrapper("Sensor", loc, Color.red);

			final TrackWrapper secondary = new TrackWrapper();

			final TimeDeltas deltas = getDeltas();

			final HiResDate start = deltas.getStartTime();
			final HiResDate end = deltas.getEndTime();
			final long interval = 10000;

			WorldLocation startLoc = loc.add(new WorldVector(Math.PI, 0.02, 0));

			for (long thisT = start.getDate().getTime(); thisT <= end.getDate()
					.getTime() + interval; thisT += interval)
			{
				final HiResDate thisD = new HiResDate(thisT);
				final WorldLocation newLoc = new WorldLocation(startLoc.add(new WorldVector(
						Math.PI * 1.5, 0.005, 0)));
				final Fix newF = new Fix(thisD, newLoc, 0, 0);
				final FixWrapper newFw = new FixWrapper(newF);
				secondary.addFix(newFw);

				startLoc = newLoc;
			}

			final SVP svp = getSVP();

			// ok, go for the calc
			final MultiPathModel model = new MultiPathModel();
			final TimeSeries calc = model.getCalculatedProfileFor(primary, secondary, svp,
					deltas, 44);

			assertNotNull("got some results", calc);
			assertEquals("correct num return", 379, calc.getItemCount());
		}

		public static class MyFunc implements MinimisationFunction
		{

			public MyFunc(final WatchableList primary, final WatchableList secondary, final SVP svp,
					final TimeDeltas times)
			{

			}

			@Override
			public double function(final double[] param)
			{
				// now the error function
				final double res = Math.abs(100d - param[0]);

				// done
				System.out.println("returned " + res + " from:" + param[0]);

				return res;
			}

		};

		// TODO FIX-TEST
		public void NtestMagic()
		{
			// load the data
			final SVP svp = new SVP();
			final TimeDeltas times = new TimeDeltas();

			try
			{
				svp.load(SVP.SVP_Test.SVP_FILE);
				times.load(TimeDeltas.IntervalTest.TEST_TIMES_FILE);
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
				fail();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				fail();
			}
			catch (final DataFormatException e)
			{
				e.printStackTrace();
				fail();
			}

			// ok, what else do we need?

			// Create instance of Minimisation
			final Minimisation min = new Minimisation();

			// Create instace of class holding function to be minimised
			final WatchableList primary = null;
			final WatchableList secondary = null;
			final MinimisationFunction funct = new MyFunc(primary, secondary, svp, times);

			// initial estimates
			final double[] start =
			{ 30 };

			// initial step sizes
			final double[] step =
			{ 5 };

			// convergence tolerance
			final double ftol = 1e-2;

			// specify the maximum depth
			double maxDepth = svp.getMaxDepth();
			min.addConstraint(0, +1, maxDepth);

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 100);

			// get values of y and z at minimum
			double[] param = min.getParamValues();

			// Output the results to screen
			System.out.println("Minimum = " + min.getMinimum());
			System.out.println("Value of x at the minimum = " + param[0]);
			assertEquals("valid depth", 59, param[0], 1);

			// specify the shallower maximum depth
			min.removeConstraints();
			maxDepth = 40;
			min.addConstraint(0, +1, maxDepth);

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 100);

			// get values of y and z at minimum
			param = min.getParamValues();

			// Output the results to screen
			System.out.println("Minimum = " + min.getMinimum());
			System.out.println("Value of x at the minimum = " + param[0]);

			assertEquals("valid depth", 40, param[0], 1);

		}

		public void testCalc()
		{
			// ok, go for the calc
			final MultiPathModel model = new MultiPathModel();

			double sepM = 2549.11;
			final double zR = 30;
			final double zS = 50;
			final double cD = 1500.760;
			final double cS = 1501.628;
			final double cR = 1502.207;
			final double delay = model.calculateDelay(sepM, zR, zS, cD, cS, cR);

			assertEquals("wrong calc", -0.00044, delay, 0.001);

			sepM = 2072.33;
			assertEquals("wrong calc", -0.000034, delay, 0.001);

		}

		// TODO FIX-TEST
		public void NtestCalc2()
		{
			final SVP svp = getSVP2();

			final MultiPathModel model = new MultiPathModel();

			for (int i = 1140; i < 1348; i++)
			{

				final double zS = 1340;
				final double zR = i;

				// now sort out the sound speeds
				final double cD = svp.getMeanSpeedBetween(zS, zR);
				final double cS = svp.getMeanSpeedBetween(0, zS);
				final double cR = svp.getMeanSpeedBetween(0, zR);

				// do the actual calculation
				final double time_delay = model.calculateDelay(1200, zR, zS, cD, cS, cR);

				System.out.println("cd:" + (int) cD + " cs:" + (int) cS + " cr:"
						+ (int) cR + " delay at " + i + " is:" + time_delay);

			}

		}

		private SVP getSVP()
		{
			final SVP svp = new SVP();
			try
			{
				svp.load(SVP.SVP_Test.SVP_FILE);
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
				fail("number format");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				fail("file read problem");
			}
			catch (final DataFormatException e)
			{
				e.printStackTrace();
				fail("bad data problem");
			}
			return svp;
		}

		private SVP getSVP2()
		{
			final SVP svp = new SVP();
			try
			{
				svp.load(SVP.SVP_Test.SVP_FILE2);
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
				fail("number format");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				fail("file read problem");
			}
			catch (final DataFormatException e)
			{
				e.printStackTrace();
				fail("bad data problem");
			}
			return svp;
		}

		private static TimeDeltas getDeltas()
		{
			final TimeDeltas deltas = new TimeDeltas();
			try
			{
				deltas.load(TimeDeltas.IntervalTest.TEST_TIMES_FILE);
			}
			catch (final ParseException e)
			{
				e.printStackTrace();
				fail("number format problem");
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				fail("file read problem");
			}
			catch (final DataFormatException e)
			{
				e.printStackTrace();
				fail("bad data problem");
			}
			return deltas;
		}
	}

	/**
	 * helper method, produces calculated profile using pre-coded set of ranges
	 * 
	 * @param ranges
	 * @param svp
	 * @param times
	 * @param val
	 * @return
	 */
	public TimeSeries getCalculatedProfileFor(final RangeValues ranges, final SVP svp,
			final TimeDeltas times, final double targetDepth, final double hostDepth)
	{
		final TimeSeries res = new TimeSeries("Calculated delay (TEST)");

		if (times != null)
		{
			// ok, loop through the times
			final Iterator<Observation> iter = times.iterator();
			while (iter.hasNext())
			{
				final Observation thisO = iter.next();

				// what's this time?
				final HiResDate tNow = thisO.getDate();
				final long timeMillis = tNow.getDate().getTime();

				// do we have a range at this time?
				if (ranges.hasValueAt(timeMillis))
				{

					// what's the range separation at this time
					final double sepM = ranges.valueAt(timeMillis);

					// and the delay
					final double time_delay = calculateDelayFor(svp, targetDepth, hostDepth,
							sepM);

					res.add(new FixedMillisecond(timeMillis), time_delay);
				}
			}
		}

		return res;
	}
}
