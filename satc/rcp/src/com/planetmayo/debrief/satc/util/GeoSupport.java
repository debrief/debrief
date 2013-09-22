package com.planetmayo.debrief.satc.util;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * utility class providing geospatial support
 * 
 * @author ian
 * 
 */
public class GeoSupport
{
	private static GeometryFactory _factory;

	public static double deg2m(double degs)
	{
		return degs * 111200d;
	}

	public static double[][] getCoordsFor(LocationRange loc)
	{
		Geometry geometry = loc.getGeometry();
		Coordinate[] coords = geometry.getCoordinates();
		double[][] res = new double[coords.length][2];
		for (int i = 0; i < coords.length; i++)
		{
			Coordinate thisC = coords[i];
			res[i][0] = thisC.x;
			res[i][1] = thisC.y;
		}

		return res;
	}

	/**
	 * get our geometry factory
	 * 
	 * @return
	 */
	public static GeometryFactory getFactory()
	{
		if (_factory == null)
			_factory = new GeometryFactory();

		return _factory;
	}
	
	public static Point createPoint(double lon, double lat) 
	{
		return getFactory().createPoint(new Coordinate(lon, lat));
	}
	
	public static Geometry doBuffer(Geometry geom, double distance)
	{
		return geom.buffer(distance, 3);
	}

	public static Distance computeDistance(Geometry geo1, Geometry geo2)
	{
		return new DistanceHelper(geo1, geo2).calculate();
	}

	public static double kts2MSec(double kts)
	{
		return kts * 0.514444444;
	}

	public static double m2deg(double metres)
	{
		return metres / 111200d;
	}

	// /** convert a turn rate of degrees per second to radians per millisecond
	// *
	// * @param rads_milli
	// * @return
	// */
	// public static double degsSec2radsMilli(double degs_sec)
	// {
	// // first convert to millis
	// double res = degs_sec / 1000;
	//
	// // and now to rads
	// return Math.toRadians(res);
	// }
	//
	// /** convert a turn rate of radians per millisecond to degrees per second to
	// radians per millisecond
	// *
	// * @param rads_milli
	// * @return
	// */
	// public static double radsMilli2degSec(double rads_milli)
	// {
	// // first convert to seconds
	// double res = rads_milli * 1000d;
	//
	// // now to degrees
	// return Math.toDegrees(res);
	// }

	public static double MSec2kts(double m_sec)
	{
		return m_sec / 0.514444444;
	}
	
	public static double convertToCompassAngle(double angle)
	{
		return MathUtils.normalizeAngle(Math.PI / 2 - angle);
	}

	public static String formatGeoPoint(GeoPoint geoPoint)
	{
		double _lat = geoPoint.getLat();
		double _lon = geoPoint.getLon();

		String latitudeStr = decimalToDMS(Math.abs(_lat)) + (_lat < 0 ? "S" : "N");
		String longitudeStr = decimalToDMS(Math.abs(_lon)) + (_lon < 0 ? "W" : "E");
		return latitudeStr + "\n" + longitudeStr;
	}

	public static String decimalToDMS(double coord)
	{

		String output, degrees, minutes, seconds;

		double mod = coord % 1;
		int intPart = (int) coord;

		degrees = String.valueOf((int) intPart);

		coord = mod * 60;
		mod = coord % 1;
		intPart = (int) coord;

		minutes = String.valueOf((int) intPart);

		coord = mod * 60;
		intPart = (int) coord;

		seconds = String.valueOf(Math.round(coord * 100.0) / 100.0);

		output = degrees + "\u00B0 " + minutes + "' " + seconds + "\" ";
		return output;

	}

	public static GeoPoint getGeoPointFromString(String latlong)
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

	private static double parseDMSString(String lat)
	{
		double deg = Double.parseDouble(lat.substring(0, lat.indexOf("\u00B0 ")));
		double min = Double.parseDouble(lat.substring(lat.indexOf("\u00B0 ") + 1,
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

	/**
	 * generate a grid of points across the polygon (see implementation for more
	 * detail)
	 */
	public static ArrayList<Point> ST_Tile(final Geometry p_geom,
			final int numPoints, final int p_precision)
	{
		return MakeGrid.ST_Tile(p_geom, numPoints, p_precision);
	}

}
