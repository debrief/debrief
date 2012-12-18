package com.planetmayo.debrief.satc.util;

import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Polygon;

/**
 * utility class providing geospatial support
 * 
 * @author ian
 * 
 */
public class GeoSupport
{
	public static interface GeoPlotter
	{

		/**
		 * clear the plot
		 * 
		 */
		void clear(String title);

		/**
		 * plot the indicated line
		 * 
		 * @param title
		 *          title of the line
		 * @param coords
		 *          coords to plot
		 */
		void showGeometry(String seriesName, Coordinate[] coords);

	}

	private static GeometryFactory _factory;

	private static GeoPlotter _plotter;

	private static boolean _writeToConsole = false;

	public static void clearOutput(String title)
	{
		if (_plotter != null)
			_plotter.clear(title);

	}

	public static double deg2m(double degs)
	{
		return degs * 111200d;
	}

	public static double[][] getCoordsFor(LocationRange loc)
	{
		Polygon poly = loc.getPolygon();
		Coordinate[] coords = poly.getCoordinates();
		double[][] res = new double[coords.length][2];
		for (int i = 0; i < coords.length; i++)
		{
			Coordinate thisC = coords[i];
			res[i][0] = thisC.x;
			res[i][1] = thisC.y;
		}

		return res;
	}

	/**
	 * get our geometry factory
	 * 
	 * @return
	 */
	public static GeometryFactory getFactory()
	{
		if (_factory == null)
			_factory = new GeometryFactory();

		return _factory;
	}

	public static double kts2MSec(double kts)
	{
		return kts * 0.514444444;
	}

	public static double m2deg(double metres)
	{
		return metres / 111200d;
	}
	
	/** convert a turn rate of degrees per second to radians per millisecond
	 * 
	 * @param rads_milli
	 * @return
	 */
	public static double degsSec2radsMilli(double degs_sec)
	{
		// first convert to millis
		double res = degs_sec / 1000;
		
		// and now to rads
		return Math.toRadians(res);
	}
	
	/** convert a turn rate of  radians per millisecond to degrees per second to radians per millisecond
	 * 
	 * @param rads_milli
	 * @return
	 */
	public static double radsMilli2degSec(double rads_milli)
	{
		// first convert to seconds
		double res = rads_milli * 1000d;
		
		// now to degrees
		return Math.toDegrees(res);
	}
	
	public static double MSec2kts(double m_sec)
	{
		return m_sec / 0.514444444;
	}

	public static void setPlotter(GeoPlotter plotter)
	{
		_plotter = plotter;
	}

	public static void setToConsole(boolean writeToConsole)
	{
		_writeToConsole = writeToConsole;
	}

	private static void showGeometry(String title, Coordinate[] coords)
	{
		if (_plotter != null)
		{
			_plotter.showGeometry(title, coords);
		}
	}

	private static void writeGeometry(String title, Coordinate[] coords)
	{
		if (_writeToConsole)
		{
			System.out.println("== " + title + " ==");
			for (int i = 0; i < coords.length; i++)
			{
				Coordinate coordinate = coords[i];
				System.out.println(coordinate.x + ", " + coordinate.y);
			}
		}

		// and try to show it
		showGeometry(title, coords);
	}

	public static void writeGeometry(String title, Geometry geo)
	{
		if (geo == null)
			return;

		if (geo instanceof LineString)
		{
			LineString ring = (LineString) geo;
			Coordinate[] coords = ring.getCoordinates();
			writeGeometry(title, coords);
		}
		else if (geo instanceof Polygon)
		{
			Polygon poly = (Polygon) geo;
			writeGeometry(title + " edge ", poly.getBoundary());
		}
		else if (geo instanceof MultiLineString)
		{
			MultiLineString lineS = (MultiLineString) geo;
			int n = lineS.getNumGeometries();
			for (int i = 0; i < n; i++)
			{
				Geometry thisGeo = lineS.getGeometryN(i);
				writeGeometry(title + " geo:" + i, thisGeo);
			}
		}
		else if (geo instanceof MultiPoint)
		{
			MultiPoint mp = (MultiPoint) geo;
			Coordinate[] coords = mp.getCoordinates();
			writeGeometry(title, coords);
		}

	}
}
