
package org.mwc.debrief.satc_interface.utilities;

import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.vividsolutions.jts.geom.Coordinate;

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
