package com.planetmayo.debrief.satc.util;

import com.vividsolutions.jts.geom.GeometryFactory;

/** utility class providing geospatial support
 * 
 * @author ian
 *
 */
public class GeoSupport
{
	private static GeometryFactory _factory;

	/** get our geometry factory
	 * 
	 * @return
	 */
	public static GeometryFactory getFactory()
	{
		if(_factory == null)
			_factory = new GeometryFactory();
		
		return _factory;
	}

	public static double m2deg(double metres)
	{
		return metres / 111200d;
	}
	
	public static double deg2m(double degs)
	{
		return degs * 111200d;
	}
	
}
