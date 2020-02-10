/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model;

import java.text.DecimalFormat;

import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class GeoPoint extends ModelObject {

	private static final long serialVersionUID = 1L;

	public static final String LAT = "lat";
	public static final String LON = "lon";

	private volatile double lat;
	private volatile double lon;

	public GeoPoint(final double lat, final double lon) {
		setLat(lat);
		setLon(lon);
	}

	public Point asPoint() {
		final Coordinate coord = new Coordinate(lon, lat);
		return GeoSupport.getFactory().createPoint(coord);
	}

	public double bearingTo(final Point loc) {
		final double deltaX = loc.getX() - lon;
		final double deltaY = loc.getY() - lat;
		return Math.PI / 2 - Math.atan2(deltaY, deltaX);
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLat(final double newLat) {
		final double oldLat = lat;
		this.lat = newLat;
		firePropertyChange(LAT, oldLat, newLat);
	}

	public void setLon(final double newLon) {
		final double oldLon = lon;
		this.lon = newLon;
		firePropertyChange(LON, oldLon, newLon);
	}

	@Override
	public String toString() {
		final DecimalFormat format = new DecimalFormat("0.00");
		final String latitudeStr = format.format(Math.abs(lat)) + (lat < 0 ? "S" : "N");
		final String longitudeStr = format.format(Math.abs(lon)) + (lon < 0 ? "W" : "E");
		return latitudeStr + " " + longitudeStr;
	}
}
