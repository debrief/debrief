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
package com.planetmayo.debrief.satc.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.ObjectUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class TestSupport
{
	private ArrayList<State> _targetSolution;

	public List<State> loadSolutionTrack()
	{
		if (_targetSolution != null)
			return _targetSolution;

		ArrayList<String> lines = getSolution();

		_targetSolution = new ArrayList<State>();
		// load from this source
		// ;;IGNORE YYMMDD HHMMSS IGNORE IGNORE LAT_DEG LAT_MIN LAT_SEC LAT_HEM
		// LONG_DEG LONG_MIN LONG_SEC LONG_HEM CRSE SPED DEPTH
		// 100112 121329 SENSOR @A 0 3 57.38 S 30 0 8.65 W 1.5 15000

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
			String date = elements[0];

			// and the time
			String time = elements[1];

			String latDegs = elements[4];
			String latMins = elements[5];
			String latSecs = elements[6];
			String latHemi = elements[7];

			String lonDegs = elements[8];
			String lonMins = elements[9];
			String lonSecs = elements[10];
			String lonHemi = elements[11];

			// and the course
			String course = elements[13];

			// and the speed
			String speed = elements[14];

			// ok,now construct the date=time
			Date theDate = ObjectUtils.safeParseDate(new SimpleDateFormat(
					"yyMMdd HHmmss"), date + " " + time);

			// and the location
			double lat = Double.valueOf(latDegs) + Double.valueOf(latMins) / 60d
					+ Double.valueOf(latSecs) / 60d / 60d;
			if (latHemi.toUpperCase().equals("S"))
				lat = -lat;
			double lon = Double.valueOf(lonDegs) + Double.valueOf(lonMins) / 60d
					+ Double.valueOf(lonSecs) / 60d / 60d;
			if (lonHemi.toUpperCase().equals("W"))
				lon = -lon;

			Point pt = GeoSupport.getFactory().createPoint(new Coordinate(lat, lon));
			State newS = new State(theDate, pt,
					Math.toRadians(Double.valueOf(course)), GeoSupport.kts2MSec(Double
							.valueOf(speed)));
			_targetSolution.add(newS);

		}

		return _targetSolution;
	}

	private static ArrayList<String> getSolution()
	{
		final ArrayList<String> rows = new ArrayList<String>();

		rows.add("100112 121314 SUBJECT @C 00 00 00.00 S 030 00 00.00 W 270.00  9.00  0.00");
		rows.add("100112 121314 SUBJECT @C 00 00 00.00 S 030 00 00.00 W 270.00  9.00  0.00");
		rows.add("100112 121329 SUBJECT @C 00 00 00.00 S 030 00 02.25 W 270.00  9.00  0.00");
		rows.add("100112 121344 SUBJECT @C 00 00 00.00 S 030 00 04.50 W 270.00  9.00  0.00");
		rows.add("100112 121359 SUBJECT @C 00 00 00.00 S 030 00 06.75 W 270.00  9.00  0.00");
		rows.add("100112 121414 SUBJECT @C 00 00 00.00 S 030 00 09.00 W 270.00  9.00  0.00");
		rows.add("100112 121429 SUBJECT @C 00 00 00.00 S 030 00 11.25 W 270.00  9.00  0.00");
		rows.add("100112 121444 SUBJECT @C 00 00 00.00 S 030 00 13.50 W 270.00  9.00  0.00");
		rows.add("100112 121459 SUBJECT @C 00 00 00.00 S 030 00 15.75 W 270.00  9.00  0.00");
		rows.add("100112 121514 SUBJECT @C 00 00 00.00 S 030 00 18.00 W 270.00  9.00  0.00");
		rows.add("100112 121529 SUBJECT @C 00 00 00.00 S 030 00 20.25 W 270.00  9.00  0.00");
		rows.add("100112 121544 SUBJECT @C 00 00 00.00 S 030 00 22.50 W 270.00  9.00  0.00");
		rows.add("100112 121559 SUBJECT @C 00 00 00.00 S 030 00 24.75 W 270.00  9.00  0.00");
		rows.add("100112 121614 SUBJECT @C 00 00 00.00 S 030 00 27.00 W 270.00  9.00  0.00");
		rows.add("100112 121629 SUBJECT @C 00 00 00.00 S 030 00 29.25 W 270.00  9.00  0.00");
		rows.add("100112 121644 SUBJECT @C 00 00 00.00 S 030 00 31.50 W 270.00  9.00  0.00");
		rows.add("100112 121659 SUBJECT @C 00 00 00.00 S 030 00 33.75 W 270.00  9.00  0.00");
		rows.add("100112 121714 SUBJECT @C 00 00 00.00 S 030 00 36.00 W 270.00  9.00  0.00");
		rows.add("100112 121729 SUBJECT @C 00 00 00.00 S 030 00 38.25 W 270.00  9.00  0.00");
		rows.add("100112 121744 SUBJECT @C 00 00 00.00 S 030 00 40.50 W 270.00  9.00  0.00");
		rows.add("100112 121759 SUBJECT @C 00 00 00.00 S 030 00 42.75 W 270.00  9.00  0.00");
		rows.add("100112 121814 SUBJECT @C 00 00 00.00 S 030 00 45.00 W 270.00  9.00  0.00");
		rows.add("100112 121829 SUBJECT @C 00 00 00.00 S 030 00 47.25 W 270.00  9.00  0.00");
		rows.add("100112 121844 SUBJECT @C 00 00 00.00 S 030 00 49.50 W 270.00  9.00  0.00");
		rows.add("100112 121859 SUBJECT @C 00 00 00.00 S 030 00 51.75 W 270.00  9.00  0.00");
		rows.add("100112 121914 SUBJECT @C 00 00 00.00 S 030 00 54.00 W 270.00  9.00  0.00");
		rows.add("100112 121929 SUBJECT @C 00 00 00.00 S 030 00 56.25 W 270.00  9.00  0.00");
		rows.add("100112 121944 SUBJECT @C 00 00 00.00 S 030 00 58.50 W 270.00  9.00  0.00");
		rows.add("100112 121959 SUBJECT @C 00 00 00.00 S 030 01 00.75 W 270.00  9.00  0.00");
		rows.add("100112 122014 SUBJECT @C 00 00 00.00 S 030 01 03.00 W 270.00  9.00  0.00");
		rows.add("100112 122029 SUBJECT @C 00 00 00.00 S 030 01 05.25 W 270.00  9.00  0.00");
		rows.add("100112 122044 SUBJECT @C 00 00 00.00 S 030 01 07.50 W 270.00  9.00  0.00");
		rows.add("100112 122059 SUBJECT @C 00 00 00.00 S 030 01 09.75 W 270.00  9.00  0.00");
		rows.add("100112 122114 SUBJECT @C 00 00 00.00 S 030 01 12.00 W 270.00  9.00  0.00");
		rows.add("100112 122129 SUBJECT @C 00 00 00.00 S 030 01 14.25 W 270.00  9.00  0.00");
		rows.add("100112 122144 SUBJECT @C 00 00 00.00 S 030 01 16.50 W 270.00  9.00  0.00");
		rows.add("100112 122159 SUBJECT @C 00 00 00.00 S 030 01 18.75 W 270.00  9.00  0.00");
		rows.add("100112 122214 SUBJECT @C 00 00 00.00 S 030 01 21.00 W 270.00  9.00  0.00");
		rows.add("100112 122229 SUBJECT @C 00 00 00.00 S 030 01 23.25 W 270.00  9.00  0.00");
		rows.add("100112 122244 SUBJECT @C 00 00 00.00 S 030 01 25.50 W 270.00  9.00  0.00");
		rows.add("100112 122259 SUBJECT @C 00 00 00.00 S 030 01 27.75 W 270.00  9.00  0.00");
		rows.add("100112 122314 SUBJECT @C 00 00 00.00 S 030 01 30.00 W 270.00  9.00  0.00");
		rows.add("100112 122329 SUBJECT @C 00 00 00.00 S 030 01 32.25 W 270.00  9.00  0.00");
		rows.add("100112 122344 SUBJECT @C 00 00 00.00 S 030 01 34.50 W 270.00  9.00  0.00");
		rows.add("100112 122359 SUBJECT @C 00 00 00.00 S 030 01 36.75 W 270.00  9.00  0.00");
		rows.add("100112 122414 SUBJECT @C 00 00 00.00 S 030 01 39.00 W 270.00  9.00  0.00");
		rows.add("100112 122429 SUBJECT @C 00 00 00.00 S 030 01 41.25 W 270.00  9.00  0.00");
		rows.add("100112 122444 SUBJECT @C 00 00 00.00 S 030 01 43.50 W 270.00  9.00  0.00");
		rows.add("100112 122459 SUBJECT @C 00 00 00.00 S 030 01 45.75 W 270.00  9.00  0.00");
		rows.add("100112 122514 SUBJECT @C 00 00 00.00 S 030 01 48.00 W 270.00  9.00  0.00");
		rows.add("100112 122529 SUBJECT @C 00 00 00.00 S 030 01 50.25 W 270.00  9.00  0.00");
		rows.add("100112 122544 SUBJECT @C 00 00 00.00 S 030 01 52.50 W 270.00  9.00  0.00");
		rows.add("100112 122559 SUBJECT @C 00 00 00.00 S 030 01 54.75 W 270.00  9.00  0.00");
		rows.add("100112 122614 SUBJECT @C 00 00 00.00 S 030 01 57.00 W 270.00  9.00  0.00");
		rows.add("100112 122629 SUBJECT @C 00 00 00.00 S 030 01 59.25 W 270.00  9.00  0.00");
		rows.add("100112 122644 SUBJECT @C 00 00 00.00 S 030 02 01.50 W 270.00  9.00  0.00");
		rows.add("100112 122659 SUBJECT @C 00 00 00.00 S 030 02 03.75 W 270.00  9.00  0.00");
		rows.add("100112 122714 SUBJECT @C 00 00 00.00 S 030 02 06.00 W 270.00  9.00  0.00");
		rows.add("100112 122729 SUBJECT @C 00 00 00.00 S 030 02 08.25 W 270.00  9.00  0.00");
		rows.add("100112 122744 SUBJECT @C 00 00 00.00 S 030 02 10.50 W 270.00  9.00  0.00");
		rows.add("100112 122759 SUBJECT @C 00 00 00.00 S 030 02 12.75 W 270.00  9.00  0.00");
		rows.add("100112 122814 SUBJECT @C 00 00 00.00 S 030 02 15.00 W 270.00  9.00  0.00");
		rows.add("100112 122829 SUBJECT @C 00 00 00.00 S 030 02 17.25 W 270.00  9.00  0.00");
		rows.add("100112 122844 SUBJECT @C 00 00 00.00 S 030 02 19.50 W 270.00  9.00  0.00");
		rows.add("100112 122859 SUBJECT @C 00 00 00.00 S 030 02 21.75 W 270.00  9.00  0.00");
		rows.add("100112 122914 SUBJECT @C 00 00 00.00 S 030 02 24.00 W 270.00  9.00  0.00");
		rows.add("100112 122929 SUBJECT @C 00 00 00.00 S 030 02 26.25 W 270.00  9.00  0.00");
		rows.add("100112 122944 SUBJECT @C 00 00 00.00 S 030 02 28.50 W 270.00  9.00  0.00");
		rows.add("100112 122959 SUBJECT @C 00 00 00.00 S 030 02 30.75 W 270.00  9.00  0.00");
		rows.add("100112 123014 SUBJECT @C 00 00 00.00 S 030 02 33.00 W 270.00  9.00  0.00");
		rows.add("100112 123029 SUBJECT @C 00 00 00.00 S 030 02 35.25 W 270.00  9.00  0.00");
		rows.add("100112 123044 SUBJECT @C 00 00 00.00 S 030 02 37.50 W 270.00  9.00  0.00");
		rows.add("100112 123059 SUBJECT @C 00 00 00.00 S 030 02 39.75 W 270.00  9.00  0.00");
		rows.add("100112 123114 SUBJECT @C 00 00 00.00 S 030 02 42.00 W 270.00  9.00  0.00");
		rows.add("100112 123129 SUBJECT @C 00 00 00.00 S 030 02 44.25 W 270.00  9.00  0.00");
		rows.add("100112 123144 SUBJECT @C 00 00 00.00 S 030 02 46.50 W 270.00  9.00  0.00");
		rows.add("100112 123159 SUBJECT @C 00 00 00.00 S 030 02 48.75 W 270.00  9.00  0.00");
		rows.add("100112 123214 SUBJECT @C 00 00 00.00 S 030 02 51.00 W 270.00  9.00  0.00");
		rows.add("100112 123229 SUBJECT @C 00 00 00.19 S 030 02 53.24 W 260.05  9.00  0.00");
		rows.add("100112 123244 SUBJECT @C 00 00 00.77 S 030 02 55.41 W 250.10  9.00  0.00");
		rows.add("100112 123259 SUBJECT @C 00 00 01.72 S 030 02 57.45 W 240.16  9.00  0.00");
		rows.add("100112 123314 SUBJECT @C 00 00 03.00 S 030 02 59.29 W 230.21  9.00  0.00");
		rows.add("100112 123329 SUBJECT @C 00 00 04.56 S 030 03 00.91 W 224.39  9.00  0.00");
		rows.add("100112 123344 SUBJECT @C 00 00 06.17 S 030 03 02.49 W 224.38  9.00  0.00");
		rows.add("100112 123359 SUBJECT @C 00 00 07.78 S 030 03 04.06 W 224.38  9.00  0.00");
		rows.add("100112 123414 SUBJECT @C 00 00 09.39 S 030 03 05.63 W 224.38  9.00  0.00");
		rows.add("100112 123429 SUBJECT @C 00 00 10.99 S 030 03 07.21 W 224.38  9.00  0.00");
		rows.add("100112 123444 SUBJECT @C 00 00 12.60 S 030 03 08.78 W 224.38  9.00  0.00");
		rows.add("100112 123459 SUBJECT @C 00 00 14.21 S 030 03 10.36 W 224.38  9.00  0.00");
		rows.add("100112 123514 SUBJECT @C 00 00 15.82 S 030 03 11.93 W 224.38  9.00  0.00");
		rows.add("100112 123529 SUBJECT @C 00 00 17.43 S 030 03 13.50 W 224.38  9.00  0.00");
		rows.add("100112 123544 SUBJECT @C 00 00 19.03 S 030 03 15.08 W 224.38  9.00  0.00");
		rows.add("100112 123559 SUBJECT @C 00 00 20.64 S 030 03 16.65 W 224.38  9.00  0.00");
		rows.add("100112 123614 SUBJECT @C 00 00 22.25 S 030 03 18.22 W 224.38  9.00  0.00");
		rows.add("100112 123629 SUBJECT @C 00 00 23.86 S 030 03 19.80 W 224.38  9.00  0.00");
		rows.add("100112 123644 SUBJECT @C 00 00 25.47 S 030 03 21.37 W 224.38  9.00  0.00");
		rows.add("100112 123659 SUBJECT @C 00 00 27.07 S 030 03 22.95 W 224.38  9.00  0.00");
		rows.add("100112 123714 SUBJECT @C 00 00 28.68 S 030 03 24.52 W 224.38  9.00  0.00");
		rows.add("100112 123729 SUBJECT @C 00 00 30.29 S 030 03 26.09 W 224.38  9.00  0.00");
		rows.add("100112 123744 SUBJECT @C 00 00 31.90 S 030 03 27.67 W 224.38  9.00  0.00");
		rows.add("100112 123759 SUBJECT @C 00 00 33.51 S 030 03 29.24 W 224.38  9.00  0.00");
		rows.add("100112 123814 SUBJECT @C 00 00 35.12 S 030 03 30.81 W 224.38  9.00  0.00");
		rows.add("100112 123829 SUBJECT @C 00 00 36.72 S 030 03 32.39 W 224.38  9.00  0.00");
		rows.add("100112 123844 SUBJECT @C 00 00 38.33 S 030 03 33.96 W 224.38  9.00  0.00");
		rows.add("100112 123859 SUBJECT @C 00 00 39.94 S 030 03 35.53 W 224.38  9.00  0.00");
		rows.add("100112 123914 SUBJECT @C 00 00 41.55 S 030 03 37.11 W 224.38  9.00  0.00");
		rows.add("100112 123929 SUBJECT @C 00 00 43.16 S 030 03 38.68 W 224.38  9.00  0.00");
		rows.add("100112 123944 SUBJECT @C 00 00 44.76 S 030 03 40.26 W 224.38  9.00  0.00");
		rows.add("100112 123959 SUBJECT @C 00 00 46.37 S 030 03 41.83 W 224.38  9.00  0.00");
		rows.add("100112 124014 SUBJECT @C 00 00 47.98 S 030 03 43.40 W 224.38  9.00  0.00");
		rows.add("100112 124029 SUBJECT @C 00 00 49.59 S 030 03 44.98 W 224.38  9.00  0.00");
		rows.add("100112 124044 SUBJECT @C 00 00 51.20 S 030 03 46.55 W 224.38  9.00  0.00");
		rows.add("100112 124059 SUBJECT @C 00 00 52.80 S 030 03 48.12 W 224.38  9.00  0.00");
		rows.add("100112 124114 SUBJECT @C 00 00 54.41 S 030 03 49.70 W 224.38  9.00  0.00");
		rows.add("100112 124129 SUBJECT @C 00 00 56.02 S 030 03 51.27 W 224.38  9.00  0.00");
		rows.add("100112 124144 SUBJECT @C 00 00 57.63 S 030 03 52.85 W 224.38  9.00  0.00");
		rows.add("100112 124159 SUBJECT @C 00 00 59.24 S 030 03 54.42 W 224.38  9.00  0.00");
		rows.add("100112 124214 SUBJECT @C 00 01 00.85 S 030 03 55.99 W 224.38  9.00  0.00");
		rows.add("100112 124229 SUBJECT @C 00 01 02.45 S 030 03 57.57 W 224.38  9.00  0.00");
		rows.add("100112 124244 SUBJECT @C 00 01 04.06 S 030 03 59.14 W 224.38  9.00  0.00");
		rows.add("100112 124259 SUBJECT @C 00 01 05.67 S 030 04 00.71 W 224.38  9.00  0.00");
		rows.add("100112 124314 SUBJECT @C 00 01 07.28 S 030 04 02.29 W 224.38  9.00  0.00");
		rows.add("100112 124329 SUBJECT @C 00 01 08.89 S 030 04 03.86 W 224.38  9.00  0.00");
		rows.add("100112 124344 SUBJECT @C 00 01 10.49 S 030 04 05.43 W 224.38  9.00  0.00");
		rows.add("100112 124359 SUBJECT @C 00 01 12.10 S 030 04 07.01 W 224.38  9.00  0.00");
		rows.add("100112 124414 SUBJECT @C 00 01 13.71 S 030 04 08.58 W 224.38  9.00  0.00");
		rows.add("100112 124429 SUBJECT @C 00 01 15.32 S 030 04 10.16 W 224.38  9.00  0.00");
		rows.add("100112 124444 SUBJECT @C 00 01 16.93 S 030 04 11.73 W 224.38  9.00  0.00");
		rows.add("100112 124459 SUBJECT @C 00 01 18.53 S 030 04 13.30 W 224.38  9.00  0.00");
		rows.add("100112 124514 SUBJECT @C 00 01 20.14 S 030 04 14.88 W 224.38  9.00  0.00");
		rows.add("100112 124529 SUBJECT @C 00 01 21.75 S 030 04 16.45 W 224.38  9.00  0.00");
		rows.add("100112 124544 SUBJECT @C 00 01 23.36 S 030 04 18.02 W 224.38  9.00  0.00");
		rows.add("100112 124559 SUBJECT @C 00 01 24.97 S 030 04 19.60 W 224.38  9.00  0.00");
		rows.add("100112 124614 SUBJECT @C 00 01 26.57 S 030 04 21.17 W 224.38  9.00  0.00");
		rows.add("100112 124629 SUBJECT @C 00 01 28.18 S 030 04 22.75 W 224.38  9.00  0.00");
		rows.add("100112 124644 SUBJECT @C 00 01 29.79 S 030 04 24.32 W 224.38  9.00  0.00");
		rows.add("100112 124659 SUBJECT @C 00 01 31.40 S 030 04 25.89 W 224.38  9.00  0.00");
		rows.add("100112 124714 SUBJECT @C 00 01 33.01 S 030 04 27.47 W 224.38  9.00  0.00");
		rows.add("100112 124729 SUBJECT @C 00 01 34.62 S 030 04 29.04 W 224.38  9.00  0.00");
		rows.add("100112 124744 SUBJECT @C 00 01 36.22 S 030 04 30.61 W 224.38  9.00  0.00");
		rows.add("100112 124759 SUBJECT @C 00 01 37.83 S 030 04 32.19 W 224.38  9.00  0.00");
		rows.add("100112 124814 SUBJECT @C 00 01 39.44 S 030 04 33.76 W 224.38  9.00  0.00");
		rows.add("100112 124829 SUBJECT @C 00 01 41.05 S 030 04 35.33 W 224.38  9.00  0.00");
		rows.add("100112 124844 SUBJECT @C 00 01 42.66 S 030 04 36.91 W 224.38  9.00  0.00");
		rows.add("100112 124859 SUBJECT @C 00 01 44.26 S 030 04 38.48 W 224.38  9.00  0.00");
		rows.add("100112 124914 SUBJECT @C 00 01 45.87 S 030 04 40.06 W 224.38  9.00  0.00");
		rows.add("100112 124929 SUBJECT @C 00 01 47.48 S 030 04 41.63 W 224.38  9.00  0.00");
		rows.add("100112 124944 SUBJECT @C 00 01 49.09 S 030 04 43.20 W 224.38  9.00  0.00");
		rows.add("100112 124959 SUBJECT @C 00 01 50.70 S 030 04 44.78 W 224.38  9.00  0.00");
		rows.add("100112 125014 SUBJECT @C 00 01 52.30 S 030 04 46.35 W 224.38  9.00  0.00");
		rows.add("100112 125029 SUBJECT @C 00 01 53.91 S 030 04 47.92 W 224.38  9.00  0.00");
		rows.add("100112 125044 SUBJECT @C 00 01 55.52 S 030 04 49.50 W 224.38  9.00  0.00");
		rows.add("100112 125059 SUBJECT @C 00 01 57.13 S 030 04 51.07 W 224.38  9.00  0.00");
		rows.add("100112 125114 SUBJECT @C 00 01 58.74 S 030 04 52.65 W 224.38  9.00  0.00");
		rows.add("100112 125129 SUBJECT @C 00 02 00.35 S 030 04 54.22 W 224.38  9.00  0.00");
		rows.add("100112 125144 SUBJECT @C 00 02 01.95 S 030 04 55.79 W 224.38  9.00  0.00");
		rows.add("100112 125159 SUBJECT @C 00 02 03.56 S 030 04 57.37 W 224.38  9.00  0.00");
		rows.add("100112 125214 SUBJECT @C 00 02 05.17 S 030 04 58.94 W 224.38  9.00  0.00");
		rows.add("100112 125229 SUBJECT @C 00 02 06.78 S 030 05 00.51 W 224.38  9.00  0.00");
		rows.add("100112 125244 SUBJECT @C 00 02 08.39 S 030 05 02.09 W 224.38  9.00  0.00");
		rows.add("100112 125259 SUBJECT @C 00 02 09.99 S 030 05 03.66 W 224.38  9.00  0.00");
		rows.add("100112 125314 SUBJECT @C 00 02 11.60 S 030 05 05.23 W 224.38  9.00  0.00");
		rows.add("100112 125329 SUBJECT @C 00 02 13.21 S 030 05 06.81 W 224.38  9.00  0.00");
		rows.add("100112 125344 SUBJECT @C 00 02 14.82 S 030 05 08.38 W 224.38  9.00  0.00");
		rows.add("100112 125359 SUBJECT @C 00 02 16.43 S 030 05 09.96 W 224.38  9.00  0.00");
		rows.add("100112 125414 SUBJECT @C 00 02 18.03 S 030 05 11.53 W 224.38  9.00  0.00");
		rows.add("100112 125429 SUBJECT @C 00 02 19.64 S 030 05 13.10 W 224.38  9.00  0.00");
		rows.add("100112 125444 SUBJECT @C 00 02 21.25 S 030 05 14.68 W 224.38  9.00  0.00");
		rows.add("100112 125459 SUBJECT @C 00 02 22.86 S 030 05 16.25 W 224.38  9.00  0.00");
		rows.add("100112 125514 SUBJECT @C 00 02 24.47 S 030 05 17.82 W 224.38  9.00  0.00");
		rows.add("100112 125529 SUBJECT @C 00 02 26.08 S 030 05 19.40 W 224.38  9.00  0.00");
		rows.add("100112 125544 SUBJECT @C 00 02 27.68 S 030 05 20.97 W 224.38  9.00  0.00");
		rows.add("100112 125559 SUBJECT @C 00 02 29.29 S 030 05 22.55 W 224.38  9.00  0.00");
		rows.add("100112 125614 SUBJECT @C 00 02 30.90 S 030 05 24.12 W 224.38  9.00  0.00");
		rows.add("100112 125629 SUBJECT @C 00 02 32.51 S 030 05 25.69 W 224.38  9.00  0.00");
		rows.add("100112 125644 SUBJECT @C 00 02 34.12 S 030 05 27.27 W 224.38  9.00  0.00");
		rows.add("100112 125659 SUBJECT @C 00 02 35.72 S 030 05 28.84 W 224.38  9.00  0.00");
		rows.add("100112 125714 SUBJECT @C 00 02 37.33 S 030 05 30.41 W 224.38  9.00  0.00");
		rows.add("100112 125729 SUBJECT @C 00 02 38.94 S 030 05 31.99 W 224.38  9.00  0.00");
		rows.add("100112 125744 SUBJECT @C 00 02 40.55 S 030 05 33.56 W 224.38  9.00  0.00");
		rows.add("100112 125759 SUBJECT @C 00 02 42.16 S 030 05 35.13 W 224.38  9.00  0.00");
		rows.add("100112 125814 SUBJECT @C 00 02 43.76 S 030 05 36.71 W 224.38  9.00  0.00");
		rows.add("100112 125829 SUBJECT @C 00 02 45.37 S 030 05 38.28 W 224.38  9.00  0.00");
		rows.add("100112 125844 SUBJECT @C 00 02 46.98 S 030 05 39.86 W 224.38  9.00  0.00");
		rows.add("100112 125859 SUBJECT @C 00 02 48.59 S 030 05 41.43 W 224.38  9.00  0.00");
		rows.add("100112 125914 SUBJECT @C 00 02 50.20 S 030 05 43.00 W 224.38  9.00  0.00");
		rows.add("100112 125929 SUBJECT @C 00 02 51.80 S 030 05 44.58 W 224.38  9.00  0.00");
		rows.add("100112 125944 SUBJECT @C 00 02 53.41 S 030 05 46.15 W 224.38  9.00  0.00");
		rows.add("100112 125959 SUBJECT @C 00 02 55.02 S 030 05 47.72 W 224.38  9.00  0.00");
		rows.add("100112 130014 SUBJECT @C 00 02 56.63 S 030 05 49.30 W 224.38  9.00  0.00");
		rows.add("100112 130029 SUBJECT @C 00 02 58.24 S 030 05 50.87 W 224.38  9.00  0.00");
		rows.add("100112 130044 SUBJECT @C 00 02 59.85 S 030 05 52.45 W 224.38  9.00  0.00");
		rows.add("100112 130059 SUBJECT @C 00 03 01.45 S 030 05 54.02 W 224.38  9.00  0.00");
		rows.add("100112 130114 SUBJECT @C 00 03 03.06 S 030 05 55.59 W 224.38  9.00  0.00");
		rows.add("100112 130129 SUBJECT @C 00 03 04.67 S 030 05 57.17 W 224.38  9.00  0.00");
		rows.add("100112 130144 SUBJECT @C 00 03 06.28 S 030 05 58.74 W 224.38  9.00  0.00");
		rows.add("100112 130159 SUBJECT @C 00 03 07.89 S 030 06 00.31 W 224.38  9.00  0.00");
		rows.add("100112 130214 SUBJECT @C 00 03 09.49 S 030 06 01.89 W 224.38  9.00  0.00");
		rows.add("100112 130229 SUBJECT @C 00 03 11.10 S 030 06 03.46 W 224.38  9.00  0.00");
		rows.add("100112 130244 SUBJECT @C 00 03 12.71 S 030 06 05.03 W 224.38  9.00  0.00");
		rows.add("100112 130259 SUBJECT @C 00 03 14.32 S 030 06 06.61 W 224.38  9.00  0.00");
		rows.add("100112 130314 SUBJECT @C 00 03 15.93 S 030 06 08.18 W 224.38  9.00  0.00");
		rows.add("100112 130329 SUBJECT @C 00 03 17.53 S 030 06 09.76 W 224.38  9.00  0.00");
		rows.add("100112 130344 SUBJECT @C 00 03 19.14 S 030 06 11.33 W 224.38  9.00  0.00");
		rows.add("100112 130359 SUBJECT @C 00 03 20.75 S 030 06 12.90 W 224.38  9.00  0.00");
		rows.add("100112 130414 SUBJECT @C 00 03 22.36 S 030 06 14.48 W 224.38  9.00  0.00");
		rows.add("100112 130429 SUBJECT @C 00 03 23.97 S 030 06 16.05 W 224.38  9.00  0.00");
		rows.add("100112 130444 SUBJECT @C 00 03 25.58 S 030 06 17.62 W 224.38  9.00  0.00");
		rows.add("100112 130459 SUBJECT @C 00 03 27.18 S 030 06 19.20 W 224.38  9.00  0.00");
		rows.add("100112 130514 SUBJECT @C 00 03 28.79 S 030 06 20.77 W 224.38  9.00  0.00");
		rows.add("100112 130529 SUBJECT @C 00 03 30.40 S 030 06 22.35 W 224.38  9.00  0.00");
		rows.add("100112 130544 SUBJECT @C 00 03 32.01 S 030 06 23.92 W 224.38  9.00  0.00");
		rows.add("100112 130559 SUBJECT @C 00 03 33.62 S 030 06 25.49 W 224.38  9.00  0.00");
		rows.add("100112 130614 SUBJECT @C 00 03 35.22 S 030 06 27.07 W 224.38  9.00  0.00");
		rows.add("100112 130629 SUBJECT @C 00 03 36.83 S 030 06 28.64 W 224.38  9.00  0.00");

		return rows;

	}

	public static ArrayList<String> getLongData()
	{
		final ArrayList<String> rows = new ArrayList<String>();

		rows.add(";;IGNORE	YYMMDD	HHMMSS	IGNORE	IGNORE	LAT_DEG	LAT_MIN	LAT_SEC	LAT_HEM	LONG_DEG	LONG_MIN	LONG_SEC	LONG_HEM	BEARING	MAX_RNG");
		rows.add(";SENSOR:	100112	121329	SENSOR	@A	0	3	57.38	S	30	0	8.65	W	1.5	15000");
		rows.add(";SENSOR:	100112	121359	SENSOR	@A	0	3	52.31	S	30	0	11.09	W	1.1	15000");
		rows.add(";SENSOR:	100112	121429	SENSOR	@A	0	3	51.7	S	30	0	16.99	W	1.4	15000");
		rows.add(";SENSOR:	100112	121459	SENSOR	@A	0	3	51.7	S	30	0	22.99	W	1.8	15000");
		rows.add(";SENSOR:	100112	121529	SENSOR	@A	0	3	51.7	S	30	0	28.99	W	2.2	15000");
		rows.add(";SENSOR:	100112	121559	SENSOR	@A	0	3	51.7	S	30	0	34.99	W	2.5	15000");
		rows.add(";SENSOR:	100112	121629	SENSOR	@A	0	3	51.7	S	30	0	40.99	W	2.9	15000");
		rows.add(";SENSOR:	100112	121644	SENSOR	@A	0	3	51.7	S	30	0	43.99	W	3.1	15000");
		rows.add(";SENSOR:	100112	121744	SENSOR	@A	0	3	51.7	S	30	0	55.99	W	3.8	15000");
		rows.add(";SENSOR:	100112	121814	SENSOR	@A	0	3	51.7	S	30	1	1.99	W	4.2	15000");
		rows.add(";SENSOR:	100112	121929	SENSOR	@A	0	3	42.55	S	30	1	10.68	W	3.7	15000");
		rows.add(";SENSOR:	100112	122029	SENSOR	@A	0	3	30.74	S	30	1	12.76	W	2	15000");
		rows.add(";SENSOR:	100112	122129	SENSOR	@A	0	3	16.96	S	30	1	15.19	W	0.3	15000");
		rows.add(";SENSOR:	100112	122229	SENSOR	@A	0	3	3.18	S	30	1	17.62	W	-1.8	15000");
		rows.add(";SENSOR:	100112	122329	SENSOR	@A	0	2	49.39	S	30	1	20.05	W	-4.1	15000");
		rows.add(";SENSOR:	100112	122429	SENSOR	@A	0	2	35.6	S	30	1	22.48	W	-6.9	15000");
		rows.add(";SENSOR:	100112	122529	SENSOR	@A	0	2	21.82	S	30	1	24.91	W	-10.1	15000");
		rows.add(";SENSOR:	100112	122629	SENSOR	@A	0	2	8.03	S	30	1	27.34	W	-14	15000");
		rows.add(";SENSOR:	100112	122729	SENSOR	@A	0	1	54.24	S	30	1	29.78	W	-18.6	15000");
		rows.add(";SENSOR:	100112	122829	SENSOR	@A	0	1	40.45	S	30	1	32.21	W	-24.2	15000");
		rows.add(";SENSOR:	100112	122929	SENSOR	@A	0	1	26.46	S	30	1	32.28	W	-32	15000");
		rows.add(";SENSOR:	100112	123029	SENSOR	@A	0	1	12.46	S	30	1	32.28	W	-41	15000");
		rows.add(";SENSOR:	100112	123129	SENSOR	@A	0	1	0.29	S	30	1	36.37	W	-48.4	15000");
		rows.add(";SENSOR:	100112	123229	SENSOR	@A	0	0	53.29	S	30	1	48.49	W	-50.6	15000");
		rows.add(";SENSOR:	100112	123329	SENSOR	@A	0	0	46.29	S	30	2	0.61	W	-55.3	15000");
		rows.add(";SENSOR:	100112	123429	SENSOR	@A	0	0	39.29	S	30	2	12.74	W	-62.5	15000");
		rows.add(";SENSOR:	100112	123529	SENSOR	@A	0	0	32.29	S	30	2	24.86	W	-73	15000");
		rows.add(";SENSOR:	100112	123629	SENSOR	@A	0	0	25.29	S	30	2	36.99	W	-88.1	15000");
		rows.add(";SENSOR:	100112	123729	SENSOR	@A	0	0	18.29	S	30	2	49.11	W	-108	15000");
		rows.add(";SENSOR:	100112	123829	SENSOR	@A	0	0	11.29	S	30	3	1.24	W	-129.2	15000");
		rows.add(";SENSOR:	100112	123929	SENSOR	@A	0	0	5.39	S	30	3	13.71	W	-146.5	15000");
		rows.add(";SENSOR:	100112	124029	SENSOR	@A	0	0	5.39	S	30	3	27.71	W	-158.7	15000");
		rows.add(";SENSOR:	100112	124129	SENSOR	@A	0	0	5.39	S	30	3	41.71	W	-169.3	15000");
		rows.add(";SENSOR:	100112	124229	SENSOR	@A	0	0	5.39	S	30	3	49.87	W	-172.3	15000");
		rows.add(";SENSOR:	100112	124329	SENSOR	@A	0	0	5.39	S	30	3	57.87	W	-174.6	15000");
		rows.add(";SENSOR:	100112	124429	SENSOR	@A	0	0	8.65	S	30	4	4.46	W	-175.1	15000");
		rows.add(";SENSOR:	100112	124529	SENSOR	@A	0	0	16.62	S	30	4	4.73	W	-169.8	15000");
		rows.add(";SENSOR:	100112	124629	SENSOR	@A	0	0	24.62	S	30	4	4.73	W	-164.2	15000");
		rows.add(";SENSOR:	100112	124729	SENSOR	@A	0	0	36.96	S	30	4	4.73	W	-157.1	15000");
		rows.add(";SENSOR:	100112	124829	SENSOR	@A	0	0	50.96	S	30	4	4.73	W	-148.6	15000");
		rows.add(";SENSOR:	100112	124929	SENSOR	@A	0	1	4.96	S	30	4	4.73	W	-139	15000");
		rows.add(";SENSOR:	100112	125029	SENSOR	@A	0	1	18.96	S	30	4	4.73	W	-129	15000");
		rows.add(";SENSOR:	100112	125129	SENSOR	@A	0	1	32.96	S	30	4	4.73	W	-119	15000");
		rows.add(";SENSOR:	100112	125229	SENSOR	@A	0	1	46.96	S	30	4	4.73	W	-109.6	15000");
		rows.add(";SENSOR:	100112	125329	SENSOR	@A	0	2	0.96	S	30	4	4.73	W	-101.2	15000");
		rows.add(";SENSOR:	100112	125429	SENSOR	@A	0	2	12.21	S	30	4	12.68	W	-97	15000");
		rows.add(";SENSOR:	100112	125529	SENSOR	@A	0	2	22.94	S	30	4	21.68	W	-93.1	15000");
		rows.add(";SENSOR:	100112	125629	SENSOR	@A	0	2	33.66	S	30	4	30.68	W	-88.8	15000");
		rows.add(";SENSOR:	100112	125744	SENSOR	@A	0	2	49.98	S	30	4	36.93	W	-80.5	15000");
		rows.add(";SENSOR:	100112	125829	SENSOR	@A	0	2	59.84	S	30	4	40.52	W	-75.9	15000");
		rows.add(";SENSOR:	100112	125914	SENSOR	@A	0	3	9.71	S	30	4	44.11	W	-71.7	15000");
		rows.add(";SENSOR:	100112	130014	SENSOR	@A	0	3	22.42	S	30	4	49.71	W	-66.6	15000");
		rows.add(";SENSOR:	100112	130129	SENSOR	@A	0	3	35.82	S	30	5	0.96	W	-61	15000");
		rows.add(";SENSOR:	100112	130229	SENSOR	@A	0	3	46.55	S	30	5	9.96	W	-56.5	15000");
		rows.add(";SENSOR:	100112	130329	SENSOR	@A	0	3	57.27	S	30	5	18.95	W	-52	15000");
		rows.add(";SENSOR:	100112	130429	SENSOR	@A	0	4	8	S	30	5	27.95	W	-47.5	15000");
		rows.add(";SENSOR:	100112	130529	SENSOR	@A	0	4	18.72	S	30	5	36.95	W	-43.2	15000");

		return rows;
	}

	public static ArrayList<String> getShortData()
	{
		// and put them into an array list
		final ArrayList<String> rows = new ArrayList<String>();
		rows.add(";;IGNORE	YYMMDD	HHMMSS	IGNORE	IGNORE	LAT_DEG	LAT_MIN	LAT_SEC	LAT_HEM	LONG_DEG	LONG_MIN	LONG_SEC	LONG_HEM	BEARING	MAX_RNG");
		rows.add(";SENSOR:	100112	121329	SENSOR	@A	0	3	57.38	S	30	0	8.65	W	1.5	15000");
		// rows.add(";SENSOR:	100112	121459	SENSOR	@A	0	3	51.7	S	30	0	22.99	W	1.8	15000");
		rows.add(";SENSOR:	100112	121529	SENSOR	@A	0	3	51.7	S	30	0	28.99	W	2.2	15000");
		// rows.add(";SENSOR:	100112	121644	SENSOR	@A	0	3	51.7	S	30	0	43.99	W	3.1	15000");
		rows.add(";SENSOR:	100112	121744	SENSOR	@A	0	3	51.7	S	30	0	55.99	W	3.8	15000");
		// rows.add(";SENSOR:	100112	122029	SENSOR	@A	0	3	30.74	S	30	1	12.76	W	2	15000");
		rows.add(";SENSOR:	100112	122129	SENSOR	@A	0	3	16.96	S	30	1	15.19	W	0.3	15000");
		rows.add(";SENSOR:	100112	122429	SENSOR	@A	0	2	35.6	S	30	1	22.48	W	-6.9	15000");

		return rows;
	}
	
	public static ArrayList<String> getFreqDataOne()
	{
		// and put them into an array list
		final ArrayList<String> rows = new ArrayList<String>();
		rows.add(";SENSOR2: 100112 131000 SENSOR @A 60 09 44.84 N 000 01 27.17 W NULL NULL 150.069 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131050 SENSOR @A 60 09 43.01 N 000 01 29.15 W NULL NULL 150.048 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131140 SENSOR @A 60 09 41.17 N 000 01 31.13 W NULL NULL 150.027 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131230 SENSOR @A 60 09 39.33 N 000 01 33.11 W NULL NULL 150.005 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131320 SENSOR @A 60 09 37.50 N 000 01 35.09 W NULL NULL 149.984 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131410 SENSOR @A 60 09 35.66 N 000 01 37.06 W NULL NULL 149.963 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131500 SENSOR @A 60 09 33.83 N 000 01 39.04 W NULL NULL 149.941 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131550 SENSOR @A 60 09 31.99 N 000 01 41.02 W NULL NULL 149.921 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131640 SENSOR @A 60 09 30.15 N 000 01 43.00 W NULL NULL 149.900 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131730 SENSOR @A 60 09 28.32 N 000 01 44.98 W NULL NULL 149.880 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131820 SENSOR @A 60 09 26.48 N 000 01 46.96 W NULL NULL 149.861 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 131910 SENSOR @A 60 09 24.64 N 000 01 48.93 W NULL NULL 149.842 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 132000 SENSOR @A 60 09 22.81 N 000 01 50.91 W NULL NULL 149.824 0000 NB_FREQ SUBJECT held on NB_FREQ");

		return rows;
	}
	public static ArrayList<String> getFreqDataTwo()
	{
		// and put them into an array list
		final ArrayList<String> rows = new ArrayList<String>();
		rows.add(";SENSOR2: 100112 133500 SENSOR @A 60 09 32.08 N 000 02 40.66 W NULL NULL 149.759 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 133550 SENSOR @A 60 09 34.15 N 000 02 41.08 W NULL NULL 149.757 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 133640 SENSOR @A 60 09 36.22 N 000 02 41.50 W NULL NULL 149.755 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 133730 SENSOR @A 60 09 38.30 N 000 02 41.91 W NULL NULL 149.753 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 133820 SENSOR @A 60 09 40.37 N 000 02 42.33 W NULL NULL 149.752 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 133910 SENSOR @A 60 09 42.44 N 000 02 42.74 W NULL NULL 149.750 0000 NB_FREQ SUBJECT held on NB_FREQ");
		rows.add(";SENSOR2: 100112 134000 SENSOR @A 60 09 44.52 N 000 02 43.16 W NULL NULL 149.748 0000 NB_FREQ SUBJECT held on NB_FREQ");
		

		return rows;
	}

	public void loadGoodData(ISolver generator)
	{
		IContributions contributions = generator.getContributions();
		// clear the geneartor first
		boolean live = generator.isLiveRunning();
		generator.setLiveRunning(false);
		generator.clear();

		// now load some data
		BearingMeasurementContribution bmc = new BearingMeasurementContribution();
		bmc.setName("Measured bearing");
		bmc.setAutoDetect(false);
		RangeForecastContribution rangeF = new RangeForecastContribution();
		rangeF.setName("Measured range");
		ArrayList<String> rows = getLongData();

		try
		{
			// populate the bearing data
			bmc.loadFrom(rows);
			contributions.addContribution(bmc);

			// and populate the range data
			rangeF.loadFrom(rows);
			contributions.addContribution(rangeF);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// sort out the legs
		StraightLegForecastContribution st1 = new StraightLegForecastContribution();
		st1.setStartDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 121329"));
		st1.setFinishDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 123029"));
		st1.setName("Straight leg one");
		contributions.addContribution(st1);

		StraightLegForecastContribution st2 = new StraightLegForecastContribution();
		st2.setStartDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 123329"));
		st2.setFinishDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 124829"));
		st2.setName("Straight leg two");
		contributions.addContribution(st2);

		StraightLegForecastContribution st3 = new StraightLegForecastContribution();
		st3.setStartDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 125100"));
		st3.setFinishDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 130429"));
		st3.setName("Straight leg three");
		contributions.addContribution(st3);

		CourseForecastContribution course = new CourseForecastContribution();
		course.setStartDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 121329"));
		course.setFinishDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 130429"));
		course.setMinCourse(Math.toRadians(190));
		course.setMaxCourse(Math.toRadians(315));
		course.setEstimate(Math.toRadians(225));
		course.setName("Initial course forecast");
		contributions.addContribution(course);

		SpeedForecastContribution speed = new SpeedForecastContribution();
		speed.setStartDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 121329"));
		speed.setFinishDate(ObjectUtils.safeParseDate(new SimpleDateFormat(
				"yyMMdd HHmmss"), "100112 130429"));
		speed.setMinSpeed(GeoSupport.kts2MSec(4d));
		speed.setMaxSpeed(GeoSupport.kts2MSec(14d));
		speed.setEstimate(GeoSupport.kts2MSec(8d));
		speed.setName("Initial speed forecast");
		contributions.addContribution(speed);

		//
		// // try a location forecast
		// LocationForecastContribution locF = new LocationForecastContribution();
		// locF.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss",
		// "100112 121300"));
		// locF.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 121700"));
		// locF.setLocation(new GeoPoint(0.03, -30.0));
		// locF.setLimit(3000d);
		// locF.setName("Last known location");
		// locF.setActive(false);
		// getGenerator().addContribution(locF);
		//
		// // hey, how about a time-bounded course constraint?
		// CourseForecastContribution course = new CourseForecastContribution();
		// course.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss",
		// "100112 121231"));
		// course.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122525"));
		// course.setMinCourse(Math.toRadians(225));
		// course.setMaxCourse(Math.toRadians(315));
		// course.setName("Last known course");
		// getGenerator().addContribution(course);
		//
		// // hey, how about a time-bounded course constraint?
		// SpeedForecastContribution speed2 = new SpeedForecastContribution();
		// speed2.setStartDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 122500"));
		// speed2.setFinishDate(SupportServices.INSTANCE.parseDate("yyMMdd HHmmss","100112 123100"));
		// speed2.setMinSpeed(GeoSupport.kts2MSec(8d));
		// speed2.setMaxSpeed(GeoSupport.kts2MSec(27d));
		// speed2.setName("Later speed obs");
		// getGenerator().addContribution(speed2);

		// and our analysis contributions
		SpeedAnalysisContribution speedA = new SpeedAnalysisContribution();
		speedA.setActive(true);
		contributions.addContribution(speedA);
		CourseAnalysisContribution courseA = new CourseAnalysisContribution();
		courseA.setActive(true);
		contributions.addContribution(courseA);
		contributions.addContribution(new LocationAnalysisContribution());

		// ok, and get it to go for it
		generator.setLiveRunning(live);
		generator.run(true, false);

	}

	public static String asGeoJSON(String title, Coordinate[] pts)
	{
		String res = "";
		final String newLine = System.getProperty("line.separator");

		// sort out the feature
		String coordsStr = "";
		for (int i = 0; i < pts.length; i++)
		{
			Coordinate coordinate = pts[i];

			if (i > 0) 
			{
				coordsStr += ", ";
			}
			coordsStr += "[" + newLine;
			coordsStr += coordinate.x + "," + newLine;
			coordsStr += coordinate.y + newLine;
			coordsStr += "]" + newLine;
		}

		res = "{   " + " \"type\": \"FeatureCollection\"," + newLine
				+ " \"features\": [{" + newLine + "\"type\": \"Feature\"," + newLine
				+ " \"geometry\": {" + newLine + "   \"type\": \"LineString\","
				+ newLine + "   \"coordinates\": [" + newLine + coordsStr + newLine
				+ "  ]" + newLine + " }," + newLine + " \"properties\": {" + newLine
				+ "  \"name\": \"" + title + "\"," + newLine + " }" + newLine + "}]"
				+ newLine + "}";

		return res;
	}
}