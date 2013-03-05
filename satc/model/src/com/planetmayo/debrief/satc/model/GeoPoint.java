package com.planetmayo.debrief.satc.model;

import java.text.DecimalFormat;

import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class GeoPoint extends ModelObject
{

	private static final long serialVersionUID = 1L;

	public static final String LAT = "lat";
	public static final String LON = "lon";

	private double _lat;
	private double _lon;

	public GeoPoint(final double lat, final double lon)
	{
		setLat(lat);
		setLon(lon);
	}

	public Point asPoint()
	{
		Coordinate coord = new Coordinate(_lon, _lat);
		return GeoSupport.getFactory().createPoint(coord);
	}

	public double getLat()
	{
		return _lat;
	}

	public double getLon()
	{
		return _lon;
	}

	public void setLat(double lat)
	{
		double oldLat = _lat;
		this._lat = lat;
		firePropertyChange(LAT, oldLat, lat);
	}

	public void setLon(double lon)
	{
		double oldLon = _lon;
		this._lon = lon;
		firePropertyChange(LON, oldLon, lon);
	}

	@Override
	public String toString()
	{
		DecimalFormat format = new DecimalFormat("0.00");
		String latitudeStr = format.format(Math.abs(_lat)) + (_lat < 0 ? "S" : "N");
		String longitudeStr = format.format(Math.abs(_lon))
				+ (_lon < 0 ? "W" : "E");
		return latitudeStr + " " + longitudeStr;
	}

	public double bearingTo(Point loc)
	{
		double deltaX = loc.getX() - _lon;
		double deltaY = loc.getY() - _lat;
		return Math.PI/2 - Math.atan2(deltaY, deltaX);
	}
}
