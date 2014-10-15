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
package org.mwc.cmap.gridharness.data.base60;

import java.text.NumberFormat;
import java.text.ParseException;

import MWC.GenericData.WorldLocation;

public class SexagesimalSupport
{

	public static final double MINUTES_IN_DEGREE = 60;

	public static final double SECONDS_IN_MINUTE = 60;

	public static final double MAX_LATITUDE_ABS = 90;

	public static final double MAX_LONGITUDE_ABS = 180;

	public static boolean isValid(final double minutes, final double seconds)
	{
		return isValidMinutes(minutes) && isValidSeconds(seconds);
	}

	private static boolean isValidSeconds(final double seconds)
	{
		return (seconds >= 0 && seconds < SECONDS_IN_MINUTE);
	}

	private static boolean isValidMinutes(final double minutes)
	{
		return (minutes >= 0 && minutes < MINUTES_IN_DEGREE);
	}

	public static boolean isValidLongitude(final double degree, final double minutes,
			final double seconds)
	{
		return isValid(minutes, seconds) && Math.abs(degree) <= MAX_LONGITUDE_ABS;
	}

	public static boolean isValidLatitude(final double degree, final double minutes,
			final double seconds)
	{
		return isValid(minutes, seconds) && Math.abs(degree) <= MAX_LATITUDE_ABS;
	}

	public static double combineToDegrees(final double degree, final double minutes,
			final double seconds, final int hemi)
	{
		if (!isValidMinutes(minutes))
		{
			throw new IllegalArgumentException("Illegal value of minutes: " + minutes);
		}
		if (!isValidSeconds(seconds))
		{
			throw new IllegalArgumentException("Illegal value of seconds: " + seconds);
		}
		return hemi
				* (degree + minutes / MINUTES_IN_DEGREE + seconds / MINUTES_IN_DEGREE
						/ SECONDS_IN_MINUTE);
	}

	public static final SexagesimalFormat _DD_MM_MMM = new SexagesimalFormat()
	{

		@Override
		public String format(final Sexagesimal value, final boolean forLongitudeNotLatitude)
		{
			final StringBuffer result = new StringBuffer();
			final NumberFormat degreesFormat = forLongitudeNotLatitude ? DDD : XX;
			result.append(degreesFormat.format(Math.abs(value.getDegrees())));
			result.append('\u00B0');
			result.append(XX_XXX.format(value.getMinutes()));
			result.append('\u2032');
			appendHemisphere(value, forLongitudeNotLatitude, result);
			return result.toString();
		}

		@Override
		public String getNebulaPattern(final boolean forLongitudeNotLatitude)
		{
			final String forLatitude = "##\u00B0##.###\u2032U";
			// longitude may have 3 digits for degree
			return forLongitudeNotLatitude ? "#" + forLatitude : forLatitude;
		}

		@Override
		public Sexagesimal parse(final String text, final boolean forLongitudeNotLatitude)
				throws ParseException
		{
			String theText = text;
			theText = theText.trim();
			final int hemi = getHemisphereSignum(theText, forLongitudeNotLatitude);
			theText = theText.substring(0, theText.length() - 1).trim();

			final String[] subdivisions = theText.trim().split("[\u2032\u2033\u00B0]");
			if (subdivisions.length != 2)
			{
				throw new ParseException("2 parts expected, actually: "
						+ subdivisions.length + " for: " + theText, -1);
			}
			final int degrees = DDD.parse(subdivisions[0]).intValue();
			final double minutes = XX_XXX.parse(subdivisions[1]).doubleValue();
			final double seconds = 0;
			return new Sexagesimal(degrees, minutes, seconds, hemi);
		}

		@Override
		public Sexagesimal parseDouble(final double combinedDegrees)
		{
			double theCombinedDegrees = combinedDegrees;
			final int hemi = theCombinedDegrees < 0 ? -1 : 1;
			theCombinedDegrees = Math.abs(theCombinedDegrees);
			final int degrees = (int) Math.floor(theCombinedDegrees);
			final double minutes = (theCombinedDegrees - degrees) * MINUTES_IN_DEGREE;
			final double seconds = 0;
			return new Sexagesimal(degrees, minutes, seconds, hemi);
		}

		public String getExampleString()
		{
			return "_DD_MM_MMM";
		}
	};

	public static final SexagesimalFormat _DD_MM_SS_SSS = new SexagesimalFormat()
	{

		@Override
		public String format(final Sexagesimal value, final boolean forLongitudeNotLatitude)
		{
			final StringBuffer result = new StringBuffer();
			final NumberFormat degreesFormat = forLongitudeNotLatitude ? DDD : XX;
			result.append(degreesFormat.format(Math.abs(value.getDegrees())));
			result.append('\u00B0');
			result.append(XX.format(value.getMinutes()));
			result.append('\u2032');
			result.append(XX_XXX.format(value.getSeconds()));
			result.append('\u2033');
			appendHemisphere(value, forLongitudeNotLatitude, result);
			return result.toString();
		}

		@Override
		public String getNebulaPattern(final boolean forLongitudeNotLatitude)
		{
			final String forLatitude = "##\u00B0##\u2032##.###\u2033U";
			// longitude may have 3 digits for degree
			return forLongitudeNotLatitude ? "#" + forLatitude : forLatitude;
		}

		@Override
		public Sexagesimal parse(final String text, final boolean forLongitudeNotLatitude)
				throws ParseException
		{
			String theText = text;
			theText = theText.trim();
			final int hemi = getHemisphereSignum(theText, forLongitudeNotLatitude);
			theText = theText.substring(0, theText.length() - 1).trim();

			final String[] subdivisions = theText.trim().split("(\\u2032|\\u2033|\\u00B0)");
			if (subdivisions.length != 3)
			{
				throw new ParseException("3 parts expected, actually: "
						+ subdivisions.length + " for: " + theText, -1);
			}
			final int degrees = DDD.parse(subdivisions[0]).intValue();
			final int minutes = XX.parse(subdivisions[1]).intValue();
			final double seconds = XX_XXX.parse(subdivisions[2]).doubleValue();
			return new Sexagesimal(degrees, minutes, seconds, hemi);
		}

		@Override
		public Sexagesimal parseDouble(final double combinedDegrees)
		{
			double theCombinedDegrees = combinedDegrees;
			final int hemi = theCombinedDegrees < 0 ? -1 : 1;
			theCombinedDegrees = Math.abs(theCombinedDegrees);
			final int degrees = (int) Math.floor(theCombinedDegrees);
			final double notRoundedMinutes = (theCombinedDegrees - degrees)
					* MINUTES_IN_DEGREE;
			final double minutes = Math.floor(notRoundedMinutes);
			final double seconds = (notRoundedMinutes - minutes) * SECONDS_IN_MINUTE;
			return new Sexagesimal(degrees, minutes, seconds, hemi);
		}

		public String getExampleString()
		{
			return "_DD_MM_SS_SSS";
		}

	};

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class FormatTest extends junit.framework.TestCase
	{
		public void testNearGreenwich()
		{
			WorldLocation loc = new WorldLocation(1, 0, 0, 'N', 0, 0, 30, 'E', 0);
			String res = _DD_MM_MMM.convertToString(loc);
			assertEquals(
					"Result was not 1 degree, 0 minutes 0 seconds North, 0 degrees 0 minutes 30 seconds East",
					"01\u00B000.000\u2032N 000\u00B000.500\u2032E", res);
			loc = new WorldLocation(1, 0, 0, 'N', 1, 0, 30, 'W', 0);
			res = _DD_MM_MMM.convertToString(loc);
			assertEquals(
					"Result was not 1 degree, 0 minutes 0 seconds North, 0 degrees 0 minutes 30 seconds West",
					"01\u00B000.000\u2032N 001\u00B000.500\u2032W", res);
			loc = new WorldLocation(1, 0, 0, 'N', 0, 0, 30, 'W', 0);
			res = _DD_MM_MMM.convertToString(loc);
			assertEquals(
					"Result was not 1 degree, 0 minutes 0 seconds North, 0 degrees 0 minutes 30 seconds West",
					"01\u00B000.000\u2032N 000\u00B000.500\u2032W", res);
			loc = new WorldLocation(0, 0, 30, 'N', 1, 0, 00, 'W', 0);
			res = _DD_MM_MMM.convertToString(loc);
			assertEquals(
					"Result was not 0 degree, 0 minutes 30 seconds North, 1 degrees 0 minutes 0 seconds West",
					"00\u00B000.500\u2032N 001\u00B000.000\u2032W", res);
			loc = new WorldLocation(0, 0, 30, 'N', 0, 0, 00, 'E', 0);
			res = _DD_MM_MMM.convertToString(loc);
			assertEquals(
					"Result was not 0 degrees, 0 minutes 30 seconds North, 0 degrees 0 minutes 0 seconds East",
					"00\u00B000.500\u2032N 000\u00B000.000\u2032E", res);
			loc = new WorldLocation(0.5, -0.5, 0);
			res = _DD_MM_MMM.convertToString(loc);
			assertEquals("correct conversion",
					"00\u00B030.000\u2032N 000\u00B030.000\u2032W", res);
			loc = new WorldLocation(0.5, -0.5, 0);
			res = _DD_MM_SS_SSS.convertToString(loc);
			assertEquals("correct conversion",
					"00\u00B030\u203200.000\u2033N 000\u00B030\u203200.000\u2033W", res);
			loc = new WorldLocation(0.5, 0.5, 0);
			res = _DD_MM_SS_SSS.convertToString(loc);
			assertEquals("correct conversion",
					"00\u00B030\u203200.000\u2033N 000\u00B030\u203200.000\u2033E", res);

		}
	}

}
