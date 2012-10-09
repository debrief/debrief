package com.planetmayo.debrief.satc.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;

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
	
	public static double kts2MSec(double kts)
	{
		return kts * 0.514444444;
	}
	
	public static double MSec2kts(double m_sec)
	{
		return m_sec / 0.514444444;
	}
	
	public static void writeGeometry(String title, Geometry geo)
	{
		if(geo == null)
			return;
		
		if(geo instanceof LineString)
		{
			LineString ring = (LineString) geo;
			Coordinate[] coords = ring.getCoordinates();
			writeGeometry(title, coords);
		}
		else if(geo instanceof Polygon)
		{
			Polygon poly = (Polygon) geo;
			writeGeometry(title + " boundary " ,poly.getBoundary());
		}
		else if(geo instanceof MultiLineString)
		{
			MultiLineString lineS = (MultiLineString) geo;
			int n = lineS.getNumGeometries();
			for(int i=0;i<n;i++)
			{
				Geometry thisGeo = lineS.getGeometryN(i);
				writeGeometry(title + " geo:" + i, thisGeo);
			}
		}
	}

	private static void writeGeometry(String title, Coordinate[] coords)
	{
		System.out.println("== " + title + " ==");
		for (int i = 0; i < coords.length; i++)
		{
			Coordinate coordinate = coords[i];
			System.out.println(coordinate.x + ", " + coordinate.y);
		}
	}
}
