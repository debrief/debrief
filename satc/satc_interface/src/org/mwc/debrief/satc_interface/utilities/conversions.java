package org.mwc.debrief.satc_interface.utilities;

import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.GeoPoint;

public class conversions
{
	public static GeoPoint toPoint(WorldLocation loc)
	{
		GeoPoint res = new GeoPoint(loc.getLat(), loc.getLong());
		return res;
	}
}
