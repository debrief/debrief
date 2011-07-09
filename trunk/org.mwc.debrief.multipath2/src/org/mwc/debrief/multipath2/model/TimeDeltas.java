package org.mwc.debrief.multipath2.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;

import MWC.GenericData.HiResDate;

/**
 * class that loads a series of time-delta observations
 * 
 * @author ian
 * 
 */
public class TimeDeltas
{
	public static class Observation
	{
		private HiResDate time;
		private double interval;

		public Observation(HiResDate timeVal, double intervalVal)
		{
			time = timeVal;
			interval = intervalVal;
		}

		public HiResDate getDate()
		{
			return time;
		}

		public double getInterval()
		{
			return interval;
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

		// ditch the first line
		line = bufRdr.readLine();

		// read each line of text file
		while ((line = bufRdr.readLine()) != null)
		{
			// here's the format:
			// YYYY,MM,DD,HH,MM,SS,mmm,TIME_DELAY(msec),POWER(dB)
			// 2009,04,42,18,37,00,254,6.797688,22


			StringTokenizer st = new StringTokenizer(line, ",");
			int year = Integer.valueOf(st.nextToken());
			int month = Integer.valueOf(st.nextToken());
			int day = Integer.valueOf(st.nextToken());
			int hour = Integer.valueOf(st.nextToken());
			int min = Integer.valueOf(st.nextToken());
			int sec = Integer.valueOf(st.nextToken());
			int milli = Integer.valueOf(st.nextToken());

			double delay = Double.valueOf(st.nextToken());
			@SuppressWarnings("unused")
			double strength = Double.valueOf(st.nextToken());

			// do the time
			
			GregorianCalendar cal2 = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
			cal2.clear();
			cal2.set(Calendar.YEAR,year);
			cal2.set(Calendar.MONTH, month-1);
			cal2.set(Calendar.DAY_OF_MONTH,day);
			cal2.set(Calendar.HOUR,hour);
			cal2.set(Calendar.MINUTE, min);
			cal2.set(Calendar.SECOND, sec);
			cal2.set(Calendar.MILLISECOND, milli);
			
			
			long tMillis = cal2.getTimeInMillis();
			tMillis += milli;
			double tSecs = tMillis / 1000;
			times.add((double) tSecs);
			
			// to the delay
			values.add(delay / 1000);
			
		}
		if (values.size() > 0)
		{
			// just check the values are of the correct order
			double sampleVal = values.elementAt(1);
			if (sampleVal > 500)
				throw new MultiPathModel.DataFormatException(
						"Doesn't look like time interval data");

			final int numEntries = values.size();
			_myData = new Vector<Observation>();
			for (int i = 0; i < numEntries; i++)
			{
				Double thisTime = times.elementAt(i);
				HiResDate thisD = new HiResDate((long) (thisTime * 1000d));
				Double thisInterval = values.elementAt(i);
				Observation obs = new Observation(thisD, thisInterval);
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
		public static final String TEST_TIMES_FILE = "src/org/mwc/debrief/multipath2/model/test_times.csv";

		public void testMe()
		{
			TimeDeltas times = new TimeDeltas();

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

			assertEquals("got correct num entries", 379, times._myData.size());

			assertNotNull("return iterator", times.iterator());

		}

	}
}
