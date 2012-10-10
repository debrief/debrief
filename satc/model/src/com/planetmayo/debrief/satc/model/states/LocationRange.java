package com.planetmayo.debrief.satc.model.states;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

	public LocationRange(Polygon area)
	{
		_myArea = area;
	}

	/**
	 * copy constructor
	 * 
	 * @param range
	 */
	public LocationRange(LocationRange range)
	{
		// TODO: this should do a deep copy, not a shallow copy
		this(range._myArea);
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
				_myArea = (Polygon) _myArea.intersection(geo);
			}
		}

		else if (collection instanceof GeometryCollection)
		{
			throw new RuntimeException("Not expecting geometry colletion!");
//			GeometryCollection geo = (GeometryCollection) collection;
//			if (geo.getLength() == 1)
//			{
//				_myArea = (Polygon) geo.getGeometryN(0);
//			}
//			else if (geo.getLength() == 0)
//			{
//				throw new IncompatibleStateException("Polygons do not overlap", this,
//						sTwo);
//			}
		}
	}

	public Polygon getPolygon()
	{
		return _myArea;
	}

	@Override
	public String getConstraintSummary()
	{
		String res = "N/A";
		if (_myArea != null)
		{
		//	NumberFormat df = new DecimalFormat("0.0000");
			Geometry theBoundary = _myArea.convexHull();
			;
			double theArea = theBoundary.getArea();
			
			// TODO: the next line should have better formatting
			res = _myArea.getCoordinates().length + "pts " + (int)(theArea * 100000);
		}
		return res;
	}

}
