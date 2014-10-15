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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.multipath2.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import flanagan.interpolation.LinearInterpolation;

/**
 * class that loads a series of time-delta observations
 * 
 * @author ian
 * 
 */
public class RangeValues
{

	double[] _times;
	double[] _ranges;
	private LinearInterpolation _interp;

	/**
	 * load an data file from the specified path
	 * 
	 * @param path
	 *          where to get it from
	 * @throws NumberFormatException
	 *           if the numbers aren't legible
	 * @throws IOException
	 *           if the file can't be found
	 * @throws ParseException 
	 */
	public void load(final String path) throws ParseException, IOException,
			MultiPathModel.DataFormatException
	{
		final Vector<Double> times = new Vector<Double>();
		final Vector<Double> values = new Vector<Double>();

		final BufferedReader bufRdr = new BufferedReader(new FileReader(path));
		String line = null;

		// read each line of text file
		while ((line = bufRdr.readLine()) != null)
		{
			final StringTokenizer st = new StringTokenizer(line, ",");
			double thisTime = 0, thisRange = 0;
			if (st.hasMoreTokens())
			{
				thisTime = MWCXMLReader.readThisDouble(st.nextToken());
			}
			if (st.hasMoreTokens())
			{
				thisRange = MWCXMLReader.readThisDouble(st.nextToken());
			}

			if (thisTime < 2000000)
			{
				times.add(thisTime);
				values.add(thisRange);
			}
		}
		
		bufRdr.close();
		
		if (values.size() > 0)
		{
			_ranges = new double[values.size()];
			_times = new double[values.size()];
			for (int i = 0; i < values.size(); i++)
			{
				_ranges[i] = values.elementAt(i);
				_times[i] = times.elementAt(i);
			}

			// and generate the interpolation algorithm
			_interp = new LinearInterpolation(_times, _ranges);

		}
	}

	/**
	 * produce an interpolated range
	 * 
	 * @param millis
	 * @return
	 */
	public double valueAt(final long millis)
	{
		final double secs = millis / 1000d;
		return _interp.interpolate(secs);
	}
	

	/**
	 * see if we have data at the specified time
	 * 
	 * @param timeVal
	 * @return
	 */
	public boolean hasValueAt(final long timeVal)
	{
		final double secs = timeVal / 1000d;
		return ((_times[0] <= secs) && (_times[_times.length - 1] >= secs));
	}


	// /////////////////////////////////////////////////
	// and the testing goes here
	// /////////////////////////////////////////////////
	public static class RangesTest extends junit.framework.TestCase
	{
		public static final String TEST_RANGES_FILE = "src/org/mwc/debrief/multipath2/model/TestRangeValues.csv";

		public void testDummy() {
			
		}
		
		// TODO FIX-TEST
		public void NtestMe()
		{
			final RangeValues times = new RangeValues();

			assertEquals("not got data", null, times._times);
			assertEquals("not got data", null, times._ranges);

			// and missing file
			try
			{
				times.load("src/org/mwc/debrief/multipath/model2/test_times_bad.csv");
				fail("should not have found file");
			}
			catch (final ParseException e)
			{
				fail("wrong number format");
			}
			catch (final IOException e)
			{
				// ok - this should have been thrown
			}
			catch (final DataFormatException e)
			{
				fail("bad data");
			}

			assertEquals("still not got data", null, times._ranges);

			try
			{
				times.load(TEST_RANGES_FILE);
			}
			catch (final ParseException e)
			{
				fail("wrong number format");
			}
			catch (final IOException e)
			{
				fail("unable to read lines");
			}
			catch (final DataFormatException e)
			{
				fail("bad data");
			}

			assertEquals("got correct num entries", 801, times._times.length);

			assertEquals("got correct range", 2530.96, times.valueAt(10000), 0.0001);
			assertEquals("got correct range", 2529.144, times.valueAt(11000), 0.0001);
			assertTrue("correct time checking", times.hasValueAt(0));
			assertTrue("correct time checking", times.hasValueAt(2000));
			assertTrue("correct time checking", !times.hasValueAt(36000000));

		}

	}

}
