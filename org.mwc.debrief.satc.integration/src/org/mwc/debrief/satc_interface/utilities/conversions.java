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
package org.mwc.debrief.satc_interface.utilities;

import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.GeoPoint;
import org.locationtech.jts.geom.Coordinate;

/** convenience methods to move between SATC and Debrief location models
 * 
 * @author ian
 *
 */
public class conversions
{
	/** declare private constructor, to prevent accidental declaration
	 * 
	 */
	private conversions(){		
	}
	
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
