package com.planetmayo.debrief.satc.model.states;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Polygon;

/**
 * class representing a bounded set of locations, stored as a polygon
 * 
 * @author ian
 * 
 */
public class LocationRange extends BaseRange<LocationRange>
{
	/**
	 * the range of locations we allow
	 * 
	 */
	private Polygon _myArea;

	/**
	 * copy constructor
	 * 
	 * @param range
	 */
	public LocationRange(LocationRange range)
	{
		this((Polygon) range._myArea.clone());
	}

	public LocationRange(Polygon area)
	{
		_myArea = area;
	}

	/**
	 * find out the number of points in the shape (if we have one)
	 * 
	 * @return
	 */
	public int numPoints()
	{
		final int res;
		if (_myArea != null)
			res = _myArea.getNumPoints();
		else
			res = 0;
		return res;
	}

	/**
	 * trim my area to the area provided
	 * 
	 * @param sTwo
	 */
	@Override
	public void constrainTo(LocationRange sTwo) throws IncompatibleStateException
	{
		Geometry collection = _myArea.intersection(sTwo._myArea);
		if (collection instanceof Polygon)
		{
			Polygon geo = (Polygon) collection;
			// do we already have an area?
			if (_myArea == null)
			{
				// take a copy of it
				_myArea = (Polygon) geo.clone();
			}
			else
			{
				// ok, constrain myself
				Geometry intersect = _myArea.intersection(geo);

				// is this just a geometry collection?
				if (intersect instanceof GeometryCollection)
				{
					GeometryCollection gc = (GeometryCollection) intersect;

					// TODO: the intersect above isn't working as expected - see below
					// on occasion it returns a linestring followed by a polygon. The
					// LineString is contained inside the polygon, and shouldn't be there.
					if (gc.getNumGeometries() == 2)
					{
						Geometry outer = gc.getGeometryN(1);
						if (outer instanceof Polygon)
							intersect = outer;
					}
				}

				_myArea = (Polygon) intersect;
			}
		}

		else if (collection instanceof GeometryCollection)
		{
			GeometryCollection geo = (GeometryCollection) collection;
			if (geo.getLength() == 1)
			{
				_myArea = (Polygon) geo.getGeometryN(0);
			}
			else if (geo.getLength() == 0)
			{
				throw new IncompatibleStateException("Polygons do not overlap", this,
						sTwo);
			}
		}
	}

	public Polygon getPolygon()
	{
		return _myArea;
	}

}
