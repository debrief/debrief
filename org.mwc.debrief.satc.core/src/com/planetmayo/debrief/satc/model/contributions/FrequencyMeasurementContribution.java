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
package com.planetmayo.debrief.satc.model.contributions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.ObjectUtils;

public class FrequencyMeasurementContribution extends CoreMeasurementContribution<FrequencyMeasurementContribution.FMeasurement>
{
	private static final long serialVersionUID = 1L;


	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// hmm, can't think of anything clever to do here
	}

	
	
	@Override
	protected double calcError(State thisState)
	{
		double res = 0;

		Date date = thisState.getTime();
		
		FMeasurement meas = measurementAt(date);
		
		if(meas != null)
		{
			// ok, we can do a calculation
			
		}
		
		return res;
	}
		
	private FMeasurement measurementAt(Date date)
	{
		Iterator<FMeasurement> iter = this.measurements.iterator();
		while (iter.hasNext())
		{
			FMeasurement measurement = (FMeasurement) iter
					.next();
			if(measurement.getDate().equals(date))
			{
				return measurement;
			}
		}
		
		return null;
	}



	public void loadFrom(List<String> lines)
	{
		// load from this source
		// ;;IGNORE YYMMDD HHMMSS IGNORE IGNORE LAT_DEG LAT_MIN LAT_SEC LAT_HEM
		// LONG_DEG LONG_MIN LONG_SEC LONG_HEM BEARING MAX_RNG
		// ;SENSOR: 100112 121329 SENSOR @A 0 3 57.38 S 30 0 8.65 W 1.5 15000

		// Read File Line By Line
		for (String strLine : lines)
		{
			// hey, is this a comment line?
			if (strLine.startsWith(";;"))
			{
				continue;
			}
			// ok, get parseing it
			String[] elements = strLine.split("\\s+");

			// now the date
			String date = elements[1];

			// and the time
			String time = elements[2];

			String latDegs = elements[5];
			String latMins = elements[6];
			String latSecs = elements[7];
			String latHemi = elements[8];

			String lonDegs = elements[9];
			String lonMins = elements[10];
			String lonSecs = elements[11];
			String lonHemi = elements[12];

			// and the beraing

			// and the range
			String range = elements[14];

			// ok,now construct the date=time
			Date theDate = ObjectUtils.safeParseDate(new SimpleDateFormat("yyMMdd hhmmss"),
					date + " " + time);

			// and the location
			double lat = Double.valueOf(latDegs) + Double.valueOf(latMins) / 60d
					+ Double.valueOf(latSecs) / 60d / 60d;
			if (latHemi.toUpperCase().equals("S"))
				lat = -lat;
			double lon = Double.valueOf(lonDegs) + Double.valueOf(lonMins) / 60d
					+ Double.valueOf(lonSecs) / 60d / 60d;
			if (lonHemi.toUpperCase().equals("W"))
				lon = -lon;

			FMeasurement measure = new FMeasurement(theDate, Double.valueOf(range));

			addMeasurement(measure);

		}
	}

	
	/**
	 * utility class for storing a measurement
	 * 
	 * @author ian
	 * 
	 */
	public static class FMeasurement extends CoreMeasurementContribution.CoreMeasurement
	{
		/**
		 * the (optional) maximum range for this measurement
		 * 
		 */
		@SuppressWarnings("unused")
		private final Double frequency;		
		@SuppressWarnings("unused")
		private Double osCourse = null;
		@SuppressWarnings("unused")
		private Double osSpeed = null;

		public FMeasurement(Date time, Double frequency)
		{
			super(time);
			this.frequency = frequency;
		}
		public void setState(double crseRads, double spdMs)
		{
			osCourse = crseRads;
			osSpeed = spdMs;
		}
	}


}
