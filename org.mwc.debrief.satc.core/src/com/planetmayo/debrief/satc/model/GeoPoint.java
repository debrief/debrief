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

	private volatile double lat;
	private volatile double lon;

	public GeoPoint(final double lat, final double lon)
	{
		setLat(lat);
		setLon(lon);
	}

	public Point asPoint()
	{
		Coordinate coord = new Coordinate(lon, lat);
		return GeoSupport.getFactory().createPoint(coord);
	}

	public double getLat()
	{
		return lat;
	}

	public double getLon()
	{
		return lon;
	}

	public void setLat(double newLat)
	{
		double oldLat = lat;
		this.lat = newLat;
		firePropertyChange(LAT, oldLat, newLat);
	}

	public void setLon(double newLon)
	{
		double oldLon = lon;
		this.lon = newLon;
		firePropertyChange(LON, oldLon, newLon);
	}

	@Override
	public String toString()
	{
		DecimalFormat format = new DecimalFormat("0.00");
		String latitudeStr = format.format(Math.abs(lat)) + (lat < 0 ? "S" : "N");
		String longitudeStr = format.format(Math.abs(lon))
				+ (lon < 0 ? "W" : "E");
		return latitudeStr + " " + longitudeStr;
	}

	public double bearingTo(Point loc)
	{
		double deltaX = loc.getX() - lon;
		double deltaY = loc.getY() - lat;
		return Math.PI/2 - Math.atan2(deltaY, deltaX);
	}
}
