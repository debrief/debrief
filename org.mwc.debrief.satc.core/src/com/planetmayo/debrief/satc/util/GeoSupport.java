/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.util;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.calculator.GeoCalculatorType;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * utility class providing geospatial support
 * 
 * @author ian
 * 
 */
public class GeoSupport
{
	private static volatile GeoCalculatorType _calculatorType = GeoCalculatorType.FAST;
	
	private static final GeometryFactory _factory = new GeometryFactory();

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
	
	public static void setCalculatorType(GeoCalculatorType type)
	{
		_calculatorType = type;
	}

	/**
	 * get our geometry factory
	 * 
	 * @return
	 */
	public static GeometryFactory getFactory()
	{
		return _factory;
	}
	
	public static Point createPoint(double lon, double lat) 
	{
		return getFactory().createPoint(new Coordinate(lon, lat));
	}

	/**
	 * creates a ring with center in specified point (lon, lat) and specified radius in meters (range) 
	 * @param center
	 * @param range
	 * @param polygon
	 * @return
	 */
	public static LinearRing geoRing(Point center, double range) 
	{
		return (LinearRing) geoRingOrPolygon(center, range, false);
	}
	
	/**
	 * creates a circle with center in specified point (lon, lat) and specified radius in meters (range) 
	 * @param center
	 * @param range
	 * @param polygon
	 * @return
	 */
	public static Polygon geoCircle(Point center, double range) 
	{
		return (Polygon) geoRingOrPolygon(center, range, true);
	}	
	
	public static Geometry geoRingOrPolygon(Point center, double range, boolean polygon)
	{
		GeodeticCalculator calculator = createCalculator();
		calculator.setStartingGeographicPoint(center.getX(), center.getY());
		calculator.setDirection(0, range);
		double yRadius = Math.abs(calculator.getDestinationGeographicPoint().getY() - center.getY());
		calculator.setDirection(90, range);
		double xRadius = Math.abs(calculator.getDestinationGeographicPoint().getX() - center.getX());
		Coordinate[] coords = new Coordinate[37];
		
		double current = 0;
		double delta = Math.PI / 18.0;
		for (int i = 0; i < 36; i++, current += delta)
		{
			coords[i] = new Coordinate(
					center.getX() + Math.cos(current) * xRadius,
					center.getY() + Math.sin(current) * yRadius
			);
		}
		coords[36] = coords[0];
		return polygon ? _factory.createPolygon(coords) : _factory.createLinearRing(coords);		
	}


	public static double kts2MSec(double kts)
	{
		return kts * 0.514444444;
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
	
	public static double yds2m(double yds)
	{
		return yds * 0.91444;
	}
	
	public static double convertToCompassAngle(double angle)
	{
		return MathUtils.normalizeAngle(Math.PI / 2 - angle);
	}
	
	public static GeodeticCalculator createCalculator() 
	{
		return _calculatorType.create();
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
