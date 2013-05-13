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
