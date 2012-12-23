package com.planetmayo.debrief.satc.support;

import java.util.Date;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.GeoPoint;

public abstract class UtilsService
{

	public abstract String formatDate(String pattern, Date date);

	public abstract Date parseDate(String pattern, String text);

	public abstract <T> T higherElement(TreeSet<T> set, T currentElement);

	public String formatGeoPoint(GeoPoint geoPoint)
	{
		double _lat = geoPoint.getLat();
		double _lon = geoPoint.getLon();

		String latitudeStr = decimalToDMS(Math.abs(_lat)) + (_lat < 0 ? "S" : "N");
		String longitudeStr = decimalToDMS(Math.abs(_lon)) + (_lon < 0 ? "W" : "E");
		return latitudeStr + " " + longitudeStr;
	}

	public String decimalToDMS(double coord)
	{
		String output, degrees, minutes, seconds;

		double mod = coord % 1;
		int intPart = (int) coord;

		degrees = String.valueOf(intPart);

		coord = mod * 60;
		mod = coord % 1;
		intPart = (int) coord;

		minutes = String.valueOf(intPart);

		coord = mod * 60;
		intPart = (int) coord;

		seconds = String.valueOf(intPart);

		output = degrees + getDegreeSymbol() + " " + minutes + "' " + seconds
				+ "\" ";
		return output;
	}

	public abstract String getDegreeSymbol();

	public GeoPoint getGeoPointFromString(String latlong)
	{

		String[] _latlong = latlong.split("[NEWS]");

		String lat = _latlong[0];
		String lon = _latlong[1];

		double _lat = parseDMSString(lat);
		double _lon = parseDMSString(lon);

		if (latlong.indexOf("S") > 0)
		{
			_lat *= -1;
		}

		if (latlong.indexOf("W") > 0)
		{
			_lon *= -1;
		}

		return new GeoPoint(_lat, _lon);
	}

	private double parseDMSString(String lat)
	{
		double deg = Double.parseDouble(lat.substring(0, lat.indexOf("° ")));
		double min = Double.parseDouble(lat.substring(lat.indexOf("° ") + 1,
				lat.indexOf("' ")));
		double sec = Double.parseDouble(lat.substring(lat.indexOf("' ") + 1,
				lat.indexOf("\" ")));
		return dmsToDecimal(deg, min, sec);
	}

	public static double dmsToDecimal(double degree, double minutes,
			double seconds)
	{
		return degree + ((seconds / 60) + minutes) / 60;
	}
}
