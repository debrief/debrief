package org.mwc.debrief.multipath2.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;

import MWC.GenericData.HiResDate;

/**
 * class that loads a series of time-delta observations
 * 
 * @author ian
 * 
 */
public class RangeValues
{
	public static class Observation
	{
		private HiResDate time;
		private double range;

		public Observation(HiResDate timeVal, double rangeVal)
		{
			time = timeVal;
			range = rangeVal;
		}

		public HiResDate getDate()
		{
			return time;
		}

		public double getRange()
		{
			return range;
		}
	}

	private Vector<Observation> _myData;

	public HiResDate getStartTime()
	{
		return _myData.firstElement().getDate();
	}

	public HiResDate getEndTime()
	{
		return _myData.lastElement().getDate();
	}

	/**
	 * load an data file from the specified path
	 * 
	 * @param path
	 *          where to get it from
	 * @throws NumberFormatException
	 *           if the numbers aren't legible
	 * @throws IOException
	 *           if the file can't be found
	 */
	public void load(String path) throws NumberFormatException, IOException,
			MultiPathModel.DataFormatException
	{
		Vector<Double> times = new Vector<Double>();
		Vector<Double> values = new Vector<Double>();

		BufferedReader bufRdr = new BufferedReader(new FileReader(path));
		String line = null;

		// read each line of text file
		while ((line = bufRdr.readLine()) != null)
		{
			StringTokenizer st = new StringTokenizer(line, ",");
			if (st.hasMoreTokens())
			{
				times.add(Double.valueOf(st.nextToken()));
			}
			if (st.hasMoreTokens())
			{
				values.add(Double.valueOf(st.nextToken()));
			}
		}
		if (values.size() > 0)
		{
			final int numEntries = values.size();
			_myData = new Vector<Observation>();
			for (int i = 0; i < numEntries; i++)
			{
				double thisTime = times.elementAt(i) * 1000;
				HiResDate thisD = new HiResDate((long)thisTime);
				Double thisRange = values.elementAt(i);
				Observation obs = new Observation(thisD, thisRange);
				_myData.add(obs);
			}
		}
	}

	/**
	 * provide support for cycling through the observations
	 * 
	 * @return
	 */
	public Iterator<Observation> iterator()
	{
		return _myData.iterator();
	}

	// /////////////////////////////////////////////////
	// and the testing goes here
	// /////////////////////////////////////////////////
	public static class IntervalTest extends junit.framework.TestCase
	{
		public static final String TEST_TIMES_FILE = "src/org/mwc/debrief/multipath2/model/TestRangeValues.csv";

		public void testMe()
		{
			RangeValues times = new RangeValues();

			assertEquals("not got data", null, times._myData);

			// and missing file
			try
			{
				times.load("src/org/mwc/debrief/multipath/model2/test_times_bad.csv");
				fail("should not have found file");
			}
			catch (NumberFormatException e)
			{
				fail("wrong number format");
			}
			catch (IOException e)
			{
				// ok - this should have been thrown
			}
			catch (DataFormatException e)
			{
				fail("bad data");
			}

			assertEquals("still not got data", null, times._myData);

			try
			{
				times.load(TEST_TIMES_FILE);
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

			assertEquals("got correct num entries", 801, times._myData.size());

			assertNotNull("return iterator", times.iterator());

		}

	}
}
