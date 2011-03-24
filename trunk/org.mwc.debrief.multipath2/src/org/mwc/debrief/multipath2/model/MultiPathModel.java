package org.mwc.debrief.multipath2.model;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.mwc.debrief.multipath2.model.TimeDeltas.Observation;

import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;
import flanagan.math.MinimizationFunction;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public class MultiPathModel
{

	public static class CalculationException extends RuntimeException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CalculationException(String msg)
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

		public DataFormatException(String msg)
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
	public TimeSeries getMeasuredProfileFor(TimeDeltas deltas)
	{
		TimeSeries res = new TimeSeries("Measured delay");

		// ok, loop through the times
		Iterator<Observation> iter = deltas.iterator();
		while (iter.hasNext())
		{
			Observation thisO = iter.next();

			// what's this time?
			HiResDate tNow = thisO.getDate();
			double delay = thisO.getInterval();

			// and add it
			res.add(new FixedMillisecond(tNow.getDate().getTime()), delay);
		}

		return res;
	}

	public static class MiracleFunction implements MinimisationFunction
	{
		private WatchableList _primary;
		private WatchableList _secondary;
		private SVP _svp;
		private TimeDeltas _times;
		private MultiPathModel _model;
		private TimeSeries _measuredTimes;

		public MiracleFunction(WatchableList primary, WatchableList secondary,
				SVP svp, TimeDeltas times)
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
		public double function(double[] param)
		{
			// ok, sort out the calculated times
			TimeSeries calcTimes = _model.getCalculatedProfileFor(_primary,
					_secondary, _svp, _times, param[0]);

			int lenA = _measuredTimes.getItemCount();
			int lenB = calcTimes.getItemCount();

			if (lenA != lenB)
			{
				throw new RuntimeException(
						"Measured and calculated datasets should be of equal length");
			}

			double runningError = 0;

			for (int i = 0; i < lenA; i++)
			{
				double valA = _measuredTimes.getDataItem(i).getValue().doubleValue();
				double valB = calcTimes.getDataItem(i).getValue().doubleValue();
				double thisError = Math.pow(valB - valA, 2);
				runningError += thisError;
			}

			// done
			System.out.println("returned " + runningError + " from:" + param[0]);

			return runningError;
		}
	}


	public static class RangedMiracleFunction implements MinimisationFunction
	{
		private SVP _svp;
		private TimeDeltas _times;
		private MultiPathModel _model;
		private TimeSeries _measuredTimes;
		private RangeValues _ranges;
		private double _ownDepth;

		public RangedMiracleFunction(RangeValues ranges,
				SVP svp, TimeDeltas times, double ownDepth)
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
		public double function(double[] param)
		{
			// ok, sort out the calculated times
			TimeSeries calcTimes = _model.getCalculatedProfileFor(_ranges, _svp, _times, param[0], _ownDepth);

			int lenA = _measuredTimes.getItemCount();
			int lenB = calcTimes.getItemCount();

			if (lenA != lenB)
			{
				throw new RuntimeException(
						"Measured and calculated datasets should be of equal length");
			}

			double runningError = 0;

			for (int i = 0; i < lenA; i++)
			{
				double valA = _measuredTimes.getDataItem(i).getValue().doubleValue();
				double valB = calcTimes.getDataItem(i).getValue().doubleValue();
				double thisError = Math.pow(valB - valA, 2);
				runningError += thisError;
			}

			// done
			System.out.println("returned " + runningError + " from:" + param[0]);

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
	public TimeSeries getCalculatedProfileFor(WatchableList primary,
			WatchableList secondary, SVP svp, TimeDeltas deltas, double targetDepth)
	{
		Boolean oldPrimaryInterp = null;
		Boolean oldSecondaryInterp = null;

		TimeSeries res = new TimeSeries("Calculated delay");

		if (primary instanceof TrackWrapper)
		{
			TrackWrapper trk = (TrackWrapper) primary;
			oldPrimaryInterp = trk.getInterpolatePoints();
			trk.setInterpolatePoints(true);
		}
		if (secondary instanceof TrackWrapper)
		{
			TrackWrapper trk = (TrackWrapper) secondary;
			oldSecondaryInterp = trk.getInterpolatePoints();
			trk.setInterpolatePoints(true);
		}

		// ok, loop through the times
		Iterator<Observation> iter = deltas.iterator();
		while (iter.hasNext())
		{
			Observation thisO = iter.next();

			// what's this time?
			HiResDate tNow = thisO.getDate();
			long timeVal = tNow.getDate().getTime();

			// find the locations
			Watchable[] priLocs = primary.getNearestTo(tNow);
			Watchable[] secLocs = secondary.getNearestTo(tNow);

			// do we have data
			if (priLocs.length == 0)
				throw new CalculationException("Insufficient primary data");
			if (secLocs.length == 0)
				throw new CalculationException("Insufficient secondary data");

			WorldLocation priLoc = priLocs[0].getLocation();
			WorldLocation secLoc = secLocs[0].getLocation();

			// create a key for this range separation calculation
			String thisKey = "" + priLoc.getLat() + priLoc.getLong()
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
			double sepM = MWC.Algorithms.Conversions.Degs2m(sep.getRange());

			double time_delay = calculateDelayFor(svp, targetDepth,
					priLoc.getDepth(), sepM);

			res.add(new FixedMillisecond(timeVal), time_delay);
		}

		// restore the interpolation settings
		if (oldPrimaryInterp != null)
		{
			TrackWrapper trk = (TrackWrapper) primary;
			trk.setInterpolatePoints(oldPrimaryInterp.booleanValue());
		}
		if (oldSecondaryInterp != null)
		{
			TrackWrapper trk = (TrackWrapper) secondary;
			trk.setInterpolatePoints(oldSecondaryInterp.booleanValue());
		}

		return res;
	}

	private double calculateDelayFor(SVP svp, double targetDepth,
			double hostDepth, double sepM)
	{
		double zR = hostDepth;
		double zS = targetDepth;

		// now sort out the sound speeds
		double cD = svp.getMeanSpeedBetween(zS, zR);
		double cS = svp.getMeanSpeedBetween(0, zS);
		double cR = svp.getMeanSpeedBetween(0, zR);

		// do the actual calculation
		double time_delay = calculateDelay(sepM, zR, zS, cD, cS, cR);
		return time_delay;
	}

	private double calculateDelay(double sepM, double zR, double zS, double cD,
			double cS, double cR)
	{
		double hD = sepM;
		double lDirect = Math.sqrt(hD * hD + Math.pow(zS - zR, 2));

		double lsHoriz = (hD * zS) / (zS + zR);
		double lS = Math.sqrt(lsHoriz * lsHoriz + zS * zS);
		double lR = Math.sqrt(Math.pow(hD - lsHoriz, 2) + zR * zR);

		double timeSurface = lS / cS;
		double timeReceiver = lR / cR;
		double timeDirect = lDirect / cD;
		double time_delay = (timeSurface + timeReceiver) - timeDirect;
		return time_delay;
	}

	// /////////////////////////////////////////////////
	// and the testing goes here
	// /////////////////////////////////////////////////
	public static class IntervalTest extends junit.framework.TestCase
	{

		public void testMe()
		{
			WorldLocation loc = new WorldLocation(2, 2, 30);
			LabelWrapper primary = new LabelWrapper("Sensor", loc, Color.red);

			TrackWrapper secondary = new TrackWrapper();

			TimeDeltas deltas = getDeltas();

			HiResDate start = deltas.getStartTime();
			HiResDate end = deltas.getEndTime();
			long interval = 10000;

			WorldLocation startLoc = loc.add(new WorldVector(Math.PI, 0.02, 0));

			for (long thisT = start.getDate().getTime(); thisT <= end.getDate()
					.getTime() + interval; thisT += interval)
			{
				HiResDate thisD = new HiResDate(thisT);
				WorldLocation newLoc = new WorldLocation(startLoc.add(new WorldVector(
						Math.PI * 1.5, 0.005, 0)));
				Fix newF = new Fix(thisD, newLoc, 0, 0);
				FixWrapper newFw = new FixWrapper(newF);
				secondary.addFix(newFw);

				startLoc = newLoc;
			}

			SVP svp = getSVP();

			// ok, go for the calc
			MultiPathModel model = new MultiPathModel();
			TimeSeries calc = model.getCalculatedProfileFor(primary, secondary, svp,
					deltas, 44);

			assertNotNull("got some results", calc);
		}

		public static class MyFunc implements MinimisationFunction
		{

			private WatchableList _primary;
			private WatchableList _secondary;
			private SVP _svp;
			private TimeDeltas _times;
			private MultiPathModel _model;
			private TimeSeries _measuredTimes;

			public MyFunc(WatchableList primary, WatchableList secondary, SVP svp,
					TimeDeltas times)
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
			public double function(double[] param)
			{
				// ok, sort out the calculated times
				// TimeSeries calcTimes = _model.getCalculatedProfileFor(_primary,
				// _secondary, _svp, _times, param[0]);

				// now the error function

				double res = Math.abs(100d - param[0]);

				// done
				System.out.println("returned " + res + " from:" + param[0]);

				return res;
			}

		};

		public void testMagic()
		{
			// load the data
			final SVP svp = new SVP();
			final TimeDeltas times = new TimeDeltas();

			try
			{
				svp.load(SVP.SVP_Test.SVP_FILE);
				times.load(TimeDeltas.IntervalTest.TEST_TIMES_FILE);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				fail();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				fail();
			}
			catch (DataFormatException e)
			{
				e.printStackTrace();
				fail();
			}

			// ok, what else do we need?

			// Create instance of Minimisation
			Minimisation min = new Minimisation();

			// Create instace of class holding function to be minimised
			WatchableList primary = null;
			WatchableList secondary = null;
			MinimisationFunction funct = new MyFunc(primary, secondary, svp, times);

			// initial estimates
			double[] start =
			{ 30 };

			// initial step sizes
			double[] step =
			{ 5 };

			// convergence tolerance
			double ftol = 1e-2;

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 100);

			// get the minimum value
			double minimum = min.getMinimum();

			// get values of y and z at minimum
			double[] param = min.getParamValues();

			// Output the results to screen
			System.out.println("Minimum = " + min.getMinimum());
			System.out.println("Value of x at the minimum = " + param[0]);

		}

		public void testCalc()
		{
			// ok, go for the calc
			MultiPathModel model = new MultiPathModel();

			double sepM = 2549.11;
			double zR = 30;
			double zS = 50;
			double cD = 1500.760;
			double cS = 1501.628;
			double cR = 1502.207;
			double delay = model.calculateDelay(sepM, zR, zS, cD, cS, cR);

			assertEquals("wrong calc", -0.00044, delay, 0.001);

			sepM = 2072.33;
			assertEquals("wrong calc", -0.000034, delay, 0.001);

		}

		public void testCalc2()
		{
			SVP svp = getSVP2();

			MultiPathModel model = new MultiPathModel();

			for (int i = 1140; i < 1348; i++)
			{

				double zS = 1340;
				double zR = i;

				// now sort out the sound speeds
				double cD = svp.getMeanSpeedBetween(zS, zR);
				double cS = svp.getMeanSpeedBetween(0, zS);
				double cR = svp.getMeanSpeedBetween(0, zR);

				// do the actual calculation
				double time_delay = model.calculateDelay(1200, zR, zS, cD, cS, cR);

				System.out.println("cd:" + (int) cD + " cs:" + (int) cS + " cr:"
						+ (int) cR + " delay at " + i + " is:" + time_delay);

			}

		}

		private SVP getSVP()
		{
			SVP svp = new SVP();
			try
			{
				svp.load(SVP.SVP_Test.SVP_FILE);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				fail("number format");
			}
			catch (IOException e)
			{
				e.printStackTrace();
				fail("file read problem");
			}
			catch (DataFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("bad data problem");
			}
			return svp;
		}

		private SVP getSVP2()
		{
			SVP svp = new SVP();
			try
			{
				svp.load(SVP.SVP_Test.SVP_FILE2);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				fail("number format");
			}
			catch (IOException e)
			{
				e.printStackTrace();
				fail("file read problem");
			}
			catch (DataFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("bad data problem");
			}
			return svp;
		}

		private static TimeDeltas getDeltas()
		{
			TimeDeltas deltas = new TimeDeltas();
			try
			{
				deltas.load(TimeDeltas.IntervalTest.TEST_TIMES_FILE);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				fail("number format problem");
			}
			catch (IOException e)
			{
				e.printStackTrace();
				fail("file read problem");
			}
			catch (DataFormatException e)
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
	public TimeSeries getCalculatedProfileFor(RangeValues ranges, SVP svp,
			TimeDeltas times, double targetDepth, double hostDepth)
	{
		TimeSeries res = new TimeSeries("Calculated delay (TEST)");

		// ok, loop through the times
		Iterator<Observation> iter = times.iterator();
		while (iter.hasNext())
		{
			Observation thisO = iter.next();

			// what's this time?
			HiResDate tNow = thisO.getDate();
			long timeVal = tNow.getDate().getTime();

			// do we have a range at this time?
			if (ranges.hasValueAt(timeVal))
			{

				// what's the range separation at this time
				double sepM = ranges.valueAt(timeVal);

				// and the delay
				double time_delay = calculateDelayFor(svp, targetDepth, hostDepth, sepM);

				res.add(new FixedMillisecond(timeVal), time_delay);
			}
		}

		return res;
	}
}
