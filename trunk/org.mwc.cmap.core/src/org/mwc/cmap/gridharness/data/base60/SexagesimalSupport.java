package org.mwc.cmap.gridharness.data.base60;

import java.text.NumberFormat;
import java.text.ParseException;

import MWC.GenericData.WorldLocation;

public class SexagesimalSupport {

	public static final double MINUTES_IN_DEGREE = 60;

	public static final double SECONDS_IN_MINUTE = 60;

	public static final double MAX_LATITUDE_ABS = 90;

	public static final double MAX_LONGITUDE_ABS = 180;

	public static boolean isValid(double minutes, double seconds) {
		return isValidMinutes(minutes) && isValidSeconds(seconds);
	}

	private static boolean isValidSeconds(double seconds) {
		return (seconds >= 0 && seconds < SECONDS_IN_MINUTE);
	}

	private static boolean isValidMinutes(double minutes) {
		return (minutes >= 0 && minutes < MINUTES_IN_DEGREE);
	}

	public static boolean isValidLongitude(double degree, double minutes, double seconds) {
		return isValid(minutes, seconds) && Math.abs(degree) <= MAX_LONGITUDE_ABS;
	}

	public static boolean isValidLatitude(double degree, double minutes, double seconds) {
		return isValid(minutes, seconds) && Math.abs(degree) <= MAX_LATITUDE_ABS;
	}

	public static double combineToDegrees(double degree, double minutes, double seconds, int hemi) {
		if (!isValidMinutes(minutes)) {
			throw new IllegalArgumentException("Illegal value of minutes: " + minutes);
		}
		if (!isValidSeconds(seconds)) {
			throw new IllegalArgumentException("Illegal value of seconds: " + seconds);
		}
		return degree + hemi * minutes / MINUTES_IN_DEGREE + hemi * seconds / MINUTES_IN_DEGREE / SECONDS_IN_MINUTE;
	}

	public static final SexagesimalFormat _DD_MM_MMM = new SexagesimalFormat() {

		@Override
		public String format(Sexagesimal value, boolean forLongitudeNotLatitude) {
			StringBuffer result = new StringBuffer();
			NumberFormat degreesFormat = forLongitudeNotLatitude ? DDD : XX;
			result.append(degreesFormat.format(Math.abs(value.getDegrees())));
			result.append('\u00B0');
			result.append(XX_XXX.format(value.getMinutes()));
			result.append('\u2032');
			appendHemisphere(value, forLongitudeNotLatitude, result);
			return result.toString();
		}

		@Override
		public String getNebulaPattern(boolean forLongitudeNotLatitude) {
			String forLatitude = "##\u00B0##.###\u2032U";
			//longitude may have 3 digits for degree
			return forLongitudeNotLatitude ? "#" + forLatitude : forLatitude;
		}

		@Override
		public Sexagesimal parse(String text, boolean forLongitudeNotLatitude) throws ParseException {
			text = text.trim();
			int hemi = getHemisphereSignum(text, forLongitudeNotLatitude);
			text = text.substring(0, text.length() - 1).trim();

			String[] subdivisions = text.trim().split("[\u2032\u2033\u00B0]");
			if (subdivisions.length != 2) {
				throw new ParseException("2 parts expected, actually: " + subdivisions.length + " for: " + text, -1);
			}
			int degrees = DDD.parse(subdivisions[0]).intValue();
			double minutes = XX_XXX.parse(subdivisions[1]).doubleValue();
			double seconds = 0;
			return new Sexagesimal(degrees, minutes, seconds, hemi);
		}

		@Override
		public Sexagesimal parseDouble(double combinedDegrees) {
			int hemi = combinedDegrees < 0 ? -1 : 1;
			combinedDegrees = Math.abs(combinedDegrees);
			int degrees = (int) Math.floor(combinedDegrees);
			double minutes = (combinedDegrees - degrees) * MINUTES_IN_DEGREE;
			double seconds = 0;
			return new Sexagesimal(degrees, minutes, seconds, hemi);
		}

		@Override
		public String getExampleString()
		{
			return "_DD_MM_MMM";
		}
	};

	public static final SexagesimalFormat _DD_MM_SS_SSS = new SexagesimalFormat() {

		@Override
		public String format(Sexagesimal value, boolean forLongitudeNotLatitude) {
			StringBuffer result = new StringBuffer();
			NumberFormat degreesFormat = forLongitudeNotLatitude ? DDD : XX;
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
		public String getNebulaPattern(boolean forLongitudeNotLatitude) {
			String forLatitude = "##\u00B0##\u2032##.###\u2033U";
			//longitude may have 3 digits for degree
			return forLongitudeNotLatitude ? "#" + forLatitude : forLatitude;
		}

		@Override
		public Sexagesimal parse(String text, boolean forLongitudeNotLatitude) throws ParseException {
			text = text.trim();
			int hemi = getHemisphereSignum(text, forLongitudeNotLatitude);
			text = text.substring(0, text.length() - 1).trim();

			String[] subdivisions = text.trim().split("(\\u2032|\\u2033|\\u00B0)");
			if (subdivisions.length != 3) {
				throw new ParseException("3 parts expected, actually: " + subdivisions.length + " for: " + text, -1);
			}
			int degrees = DDD.parse(subdivisions[0]).intValue();
			int minutes = XX.parse(subdivisions[1]).intValue();
			double seconds = XX_XXX.parse(subdivisions[2]).doubleValue();
			return new Sexagesimal(degrees, minutes, seconds, hemi);
		}

		@Override
		public Sexagesimal parseDouble(double combinedDegrees) {
			int hemi = combinedDegrees < 0 ? -1 : 1;
			combinedDegrees = Math.abs(combinedDegrees);
			int degrees = (int) Math.floor(combinedDegrees);
			double notRoundedMinutes = (combinedDegrees - degrees) * MINUTES_IN_DEGREE;
			double minutes = Math.floor(notRoundedMinutes);
			double seconds = (notRoundedMinutes - minutes) * SECONDS_IN_MINUTE;
			return new Sexagesimal(degrees, minutes, seconds, hemi);
		}

		@Override
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
			WorldLocation loc  = new WorldLocation(1,0,0,'N', 0,0,30, 'E', 0);
			String res =_DD_MM_MMM.convertToString(loc);
			assertEquals("correct conversion", "01\u00B000.000\u2032N 000\u00B000.500\u2032E", res);
			 loc  = new WorldLocation(1,0,0,'N', 1,0,30, 'W', 0);
			 res =_DD_MM_MMM.convertToString(loc);
			assertEquals("correct conversion", "01\u00B000.000\u2032N 001\u00B000.500\u2032W", res);
			 loc  = new WorldLocation(1,0,0,'N', 0,0,30, 'W', 0);
			 res =_DD_MM_MMM.convertToString(loc);
			assertEquals("correct conversion", "01\u00B000.000\u2032N 000\u00B000.500\u2032W", res);
			 loc  = new WorldLocation(0,0,30,'N', 1,0,00, 'W', 0);
			 res =_DD_MM_MMM.convertToString(loc);
			assertEquals("correct conversion", "00\u00B000.500\u2032N 001\u00B000.000\u2032W", res);
			 loc  = new WorldLocation(0,0,30,'N', 0,0,00, 'E', 0);
			 res =_DD_MM_MMM.convertToString(loc);
			assertEquals("correct conversion", "00\u00B000.500\u2032N 000\u00B000.000\u2032E", res);
			 loc  = new WorldLocation(0.5, -0.5, 0);
			 res =_DD_MM_MMM.convertToString(loc);
			assertEquals("correct conversion", "00\u00B030.000\u2032N 000\u00B030.000\u2032W", res);
			 loc  = new WorldLocation(0.5, -0.5, 0);
			 res =_DD_MM_SS_SSS.convertToString(loc);
			assertEquals("correct conversion", "00\u00B030\u203200.000\u2033N 000\u00B030\u203200.000\u2033W", res);
			 loc  = new WorldLocation(0.5, 0.5, 0);
			 res =_DD_MM_SS_SSS.convertToString(loc);
			assertEquals("correct conversion", "00\u00B030\u203200.000\u2033N 000\u00B030\u203200.000\u2033E", res);

		}
	}

	
}
