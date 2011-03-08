package org.mwc.debrief.multipath2.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;

import flanagan.interpolation.LinearInterpolation;

/**
 * class that loads & store an SVP, providing a mean average calculator
 * 
 * @author ian
 * 
 */
public class SVP
{

	public static final String DEEP_FAIL = "SVP doesn't go deep enough";
	public static final String SHALLOW_FAIL = "SVP doesn't go shallow enough";
	double _depths[];
	double _speeds[];

	/**
	 * load an SVP from teh specific path
	 * 
	 * @param path
	 *          where to get it from
	 * @throws NumberFormatException
	 *           if the numbers aren't legible
	 * @throws IOException
	 *           if the file can't be found
	 */
	public void load(String path) throws NumberFormatException, IOException,
			DataFormatException
	{
		Vector<Double> values = new Vector<Double>();

		BufferedReader bufRdr = new BufferedReader(new FileReader(path));
		String line = null;

		// read each line of text file
		while ((line = bufRdr.readLine()) != null)
		{
			StringTokenizer st = new StringTokenizer(line, ",");
			while (st.hasMoreTokens())
			{
				String thisE = st.nextToken();
				if (thisE.length() > 0)
					values.add(Double.valueOf(thisE));
			}
		}

		if (values.size() > 0)
		{
			// just check the values are of the correct order
			double sampleVal = values.elementAt(1);
			if (sampleVal < 500)
				throw new MultiPathModel.DataFormatException(
						"Doesn't look like sound speed data");

			// ok, now move the values into our two arrays
			final int numEntries = values.size();
			_depths = new double[numEntries / 2];
			_speeds = new double[numEntries / 2];
			for (int i = 0; i < numEntries; i += 2)
			{
				_depths[i / 2] = values.elementAt(i);
				_speeds[i / 2] = values.elementAt(i + 1);
			}

			// SPECIAL CASE: if the first depth is under 5 metres, extrapolate a
			// point at zero metres
			double minDepth = _depths[0];
			if (minDepth > 0)
			{
				// aah, none-zero. do we have one nearby
				if (minDepth < 5)
				{
					// do we have enough to extrapolate?
					if (_depths.length >= 2)
					{
							double zeroSpd = extrapolateZero(_depths[0], _speeds[0], _depths[1], _speeds[1]);
							
							double[] tmpDepths = new double[_depths.length + 1];
							double[] tmpSpeeds = new double[_depths.length + 1];
							
							// now shove the arrays along
							System.arraycopy(_depths, 0, tmpDepths, 1, _depths.length);
							System.arraycopy(_speeds, 0, tmpSpeeds, 1, _speeds.length);
							
							_depths = tmpDepths;
							_speeds = tmpSpeeds;
							
							_depths[0] = 0;
							_speeds[0] = zeroSpd;
					}

				}
			}

		}
	}

	private static double extrapolateZero(double depthOne, double speedOne,
			double depthTwo, double speedTwo)
	{
		double spdDelta = speedTwo - speedOne;
		double depthDelta = depthTwo - depthOne;
		double gradient = spdDelta / depthDelta;
		double zeroSpeed = speedOne - (depthOne * gradient);

		return zeroSpeed;
	}

	/**
	 * calculate the weighted average speed between the two depths
	 * 
	 * @param depthOne
	 *          first depth
	 * @param depthTwo
	 *          second depth
	 * @return
	 */
	public double getMeanSpeedBetween(double depthOne, double depthTwo)
	{
		double shallowDepth = Math.min(depthOne, depthTwo);
		double deepDepth = Math.max(depthOne, depthTwo);

		LinearInterpolation interp = new LinearInterpolation(_depths, _speeds);

		// ok, first find the point before the shallow depth
		int before = pointBefore(shallowDepth);

		// did we find one?
		if (before == -1)
			throw new RuntimeException(SHALLOW_FAIL);

		// now find the point after the deep depth
		int after = pointAfter(deepDepth);

		if (after == -1)
			throw new RuntimeException(DEEP_FAIL);

		double runningMean = -1;
		double lastDepth = -1;
		double lastSpeed = -1;

		// sort out the gaps
		for (int i = before; i <= after; i++)
		{
			double thisDepth = _depths[i];
			double thisSpeed = _speeds[i];

			// have we passed our first loop?
			if (lastDepth != -1)
			{
				if (runningMean == -1)
				{
					double travelInThisSeg = thisDepth - shallowDepth;
					double lowerSpeed = interp.interpolate(shallowDepth);
					double upperSpeed = thisSpeed;
					double meanSpeed = (lowerSpeed + upperSpeed) / 2;
					runningMean = meanSpeed * travelInThisSeg;
				}
				else
				{
					// ok, are we in a mid-section?
					if (thisDepth < deepDepth)
					{
						// yes, consume the whole of this section
						double travelInThisSeg = thisDepth - lastDepth;
						double meanSpeed = (thisSpeed + lastSpeed) / 2;
						runningMean += meanSpeed * travelInThisSeg;
					}
					else
					{
						// we must be in the last leg, just consume the portion we need
						double travelInThisSeg = deepDepth - lastDepth;
						double lowerSpeed = lastSpeed;
						double upperSpeed = interp.interpolate(deepDepth);
						double meanSpeed = (lowerSpeed + upperSpeed) / 2;
						runningMean += meanSpeed * travelInThisSeg;
					}
				}
			}
			lastDepth = thisDepth;
			lastSpeed = thisSpeed;
		}

		// ok, now just divide by the total depth travelled
		runningMean = runningMean / (deepDepth - shallowDepth);

		return runningMean;
	}

	/**
	 * determine the index of the observation before the specified depth
	 * 
	 * @param depth
	 * @return
	 */
	private int pointBefore(double depth)
	{
		int res = -1;
		int len = _depths.length;
		for (int i = 0; i < len; i++)
		{
			double thisD = _depths[i];
			if (thisD > depth)
			{
				// ok, passed our data value
				break;
			}
			res = i;
		}

		return res;
	}

	/**
	 * determine the index of the data point after the supplied depth
	 * 
	 * @param depth
	 * @return
	 */
	private int pointAfter(double depth)
	{
		int res = -1;
		int len = _depths.length;
		for (int i = 0; i < len; i++)
		{
			double thisD = _depths[i];
			if (thisD >= depth)
			{
				res = i;
				break;
			}
		}

		return res;
	}

	// /////////////////////////////////////////////////
	// and the testing goes here
	// /////////////////////////////////////////////////
	public static class SVP_Test extends junit.framework.TestCase
	{
		public static final String SVP_FILE = "src/org/mwc/debrief/multipath2/model/test_svp.csv";
		public static final String SVP_FILE2 = "src/org/mwc/debrief/multipath2/model/test_svp2.csv";
		public static final String SVP_FILE_NO_ZERO = "src/org/mwc/debrief/multipath2/model/test_svp_noZero.csv";

		public void testMean()
		{
			SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try
			{
				svp.load(SVP_FILE);
			}
			catch (NumberFormatException e)
			{
				fail("wrong number format");
			}
			catch (IOException e)
			{
				fail("unable to read lines");
			}
			catch (DataFormatException e)
			{
				fail("bad data");
			}


			double mean = svp.getMeanSpeedBetween(30, 55);
			assertEquals("correct mean", 1510.125, mean);

			// now for a calc that spans a SVP step
			mean = svp.getMeanSpeedBetween(20, 55);
			assertEquals("correct mean", 1508.42, mean, 0.01);

			// now for a calc that goes from surface
			mean = svp.getMeanSpeedBetween(0, 35);
			assertEquals("correct mean", 1503.03, mean, 0.01);

			// now for a calc with depths reversed
			mean = svp.getMeanSpeedBetween(55, 20);
			assertEquals("correct mean", 1508.42, mean, 0.01);
		}

		public void testMean2()
		{
			SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try
			{
				svp.load(SVP_FILE2);
			}
			catch (NumberFormatException e)
			{
				fail("wrong number format");
			}
			catch (IOException e)
			{
				fail("unable to read lines");
			}
			catch (DataFormatException e)
			{
				fail("bad data");
			}


			double mean;

			// now for a calc that spans a SVP step
			int surface = 0;
			int transmitter = 50;
			int receiver = 30;
			mean = svp.getMeanSpeedBetween(surface, transmitter);
			assertEquals("correct mean", 1501.628, mean, 0.01);

			// now for a calc that goes from surface
			mean = svp.getMeanSpeedBetween(surface, receiver);
			assertEquals("correct mean", 1502.207, mean, 0.01);

			// and the direct path
			mean = svp.getMeanSpeedBetween(receiver, transmitter);
			assertEquals("correct mean", 1500.760, mean, 0.01);

			// try some other depths
			transmitter = 20;
			receiver = 75;

			mean = svp.getMeanSpeedBetween(surface, transmitter);
			assertEquals("correct mean", 1502.298, mean, 0.01);

			// now for a calc that goes from surface
			mean = svp.getMeanSpeedBetween(surface, receiver);
			assertEquals("correct mean", 1500.305, mean, 0.01);

			// and the direct path
			mean = svp.getMeanSpeedBetween(receiver, transmitter);
			assertEquals("correct mean", 1499.581, mean, 0.01);
		}

		public void testMissingData()
		{
			SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try
			{
				svp.load(SVP_FILE);
			}
			catch (NumberFormatException e)
			{
				fail("wrong number format");
			}
			catch (IOException e)
			{
				fail("unable to read lines");
			}
			catch (DataFormatException e)
			{
				fail("bad data");
			}


			// change the first point so we don't have data at zero
			svp._depths[0] = 4;

			try
			{
				svp.getMeanSpeedBetween(0, 22);
				fail("should not have found index");
			}
			catch (RuntimeException e)
			{
				assertEquals("wrong message provided", SHALLOW_FAIL, e.getMessage());
			}

			try
			{
				svp.getMeanSpeedBetween(33, 222);
				fail("should not have found index");
			}
			catch (RuntimeException e)
			{
				assertEquals("wrong message provided", DEEP_FAIL, e.getMessage());
			}

		}

		public void testExtrapolate()
		{
			double res = SVP.extrapolateZero(3, 4, 6, 5);
			assertEquals("wrong extrapolated value", 3d, res);

			res = SVP.extrapolateZero(3, 4, 6, 6);
			assertEquals("wrong extrapolated value", 2d, res);

			res = SVP.extrapolateZero(3, 6, 6, 4);
			assertEquals("wrong extrapolated value", 8d, res);
			
			
			// now try loading it
			SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try
			{
				svp.load(SVP_FILE_NO_ZERO);
			}
			catch (NumberFormatException e)
			{
				fail("wrong number format");
			}
			catch (IOException e)
			{
				fail("unable to read lines");
			}
			catch (DataFormatException e)
			{
				fail("bad data");
			}
			
			assertEquals("not created extra point", 4, svp._depths.length);
			assertEquals("not created extra point", 4, svp._speeds.length);
			
			assertEquals("not got zero depth", 0d, svp._depths[0]);
			assertEquals("not got surface speed", 1503d, svp._speeds[0]);
			
		}

		public void testIndex()
		{
			SVP svp = new SVP();

			assertEquals("not got data", null, svp._depths);

			try
			{
				svp.load(SVP_FILE);
			}
			catch (NumberFormatException e)
			{
				fail("wrong number format");
			}
			catch (IOException e)
			{
				fail("unable to read lines");
			}
			catch (DataFormatException e)
			{
				fail("bad data");
			}


			assertEquals("got data", 4, svp._depths.length);

			int t1 = svp.pointBefore(0);
			assertEquals("correct depth", 0d, svp._depths[t1]);

			t1 = svp.pointBefore(5);
			assertEquals("correct depth", 0d, svp._depths[t1]);

			t1 = svp.pointBefore(15);
			assertEquals("correct depth", 00d, svp._depths[t1]);

			t1 = svp.pointBefore(35);
			assertEquals("correct depth", 30d, svp._depths[t1]);

			t1 = svp.pointBefore(50);
			assertEquals("correct depth", 40d, svp._depths[t1]);

			t1 = svp.pointAfter(0);
			assertEquals("correct depth", 0d, svp._depths[t1]);

			t1 = svp.pointAfter(5);
			assertEquals("correct depth", 30d, svp._depths[t1]);

			t1 = svp.pointAfter(15);
			assertEquals("correct depth", 30d, svp._depths[t1]);

			t1 = svp.pointAfter(35);
			assertEquals("correct depth", 40d, svp._depths[t1]);

			t1 = svp.pointAfter(50);
			assertEquals("correct depth", 60d, svp._depths[t1]);

			t1 = svp.pointAfter(150);
			assertEquals("correct depth", -1, t1);

		}
	}
}
