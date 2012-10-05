package com.planetmayo.debrief.satc.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class GeoPoint
{
	private double lat;
	private double lon;
	public GeoPoint(final double lat, final double lon)
	{
		setLat(lat);
		setLon(lon);
	}
	
	public double getLat()
	{
		return lat;
	}

	public double getLon()
	{
		return lon;
	}

	public void setLat(double lat)
	{
		this.lat = lat;
	}

	public void setLon(double lon)
	{
		this.lon = lon;
	}

	public Point asPoint()
	{
		Coordinate coord = new Coordinate(lon, lat);
		return GeoSupport.getFactory().createPoint(coord);
	}
}
