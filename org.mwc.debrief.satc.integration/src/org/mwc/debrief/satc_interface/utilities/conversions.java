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
package org.mwc.debrief.satc_interface.utilities;

import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.vividsolutions.jts.geom.Coordinate;

public class conversions
{
	public static GeoPoint toPoint(WorldLocation loc)
	{
		GeoPoint res = new GeoPoint(loc.getLat(), loc.getLong());
		return res;
	}
	public static WorldLocation toLocation(GeoPoint point)
	{
		return new WorldLocation(point.getLat(), point.getLon(), 0);
	}
	public static WorldLocation toLocation(Coordinate coord)
	{
		return new WorldLocation(coord.y, coord.x, 0);
	}
}
