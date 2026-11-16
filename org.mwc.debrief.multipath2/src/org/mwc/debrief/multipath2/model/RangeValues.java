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
public class RangeValues {

	double[] _times;
	double[] _ranges;

	private LinearInterpolation _interp;

	/**
	 * see if we have data at the specified time
	 *
	 * @param timeVal
	 * @return
	 */
	public boolean hasValueAt(final long timeVal) {
		final double secs = timeVal / 1000d;
		return ((_times[0] <= secs) && (_times[_times.length - 1] >= secs));
	}

	/**
	 * load an data file from the specified path
	 *
	 * @param path where to get it from
	 * @throws NumberFormatException if the numbers aren't legible
	 * @throws IOException           if the file can't be found
	 * @throws ParseException
	 */
	public void load(final String path) throws ParseException, IOException, MultiPathModel.DataFormatException {
		final Vector<Double> times = new Vector<Double>();
		final Vector<Double> values = new Vector<Double>();

		final BufferedReader bufRdr = new BufferedReader(new FileReader(path));
		String line = null;

		// read each line of text file
		while ((line = bufRdr.readLine()) != null) {
			final StringTokenizer st = new StringTokenizer(line, ",");
			double thisTime = 0, thisRange = 0;
			if (st.hasMoreTokens()) {
				thisTime = MWCXMLReader.readThisDouble(st.nextToken());
			}
			if (st.hasMoreTokens()) {
				thisRange = MWCXMLReader.readThisDouble(st.nextToken());
			}

			if (thisTime < 2000000) {
				times.add(thisTime);
				values.add(thisRange);
			}
		}

		bufRdr.close();

		if (!values.isEmpty()) {
			_ranges = new double[values.size()];
			_times = new double[values.size()];
			for (int i = 0; i < values.size(); i++) {
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
	public double valueAt(final long millis) {
		final double secs = millis / 1000d;
		return _interp.interpolate(secs);
	}

}
