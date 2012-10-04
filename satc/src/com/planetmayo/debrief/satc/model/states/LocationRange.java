package com.planetmayo.debrief.satc.model.states;

import com.vividsolutions.jts.geom.Polygon;

/**
 * class representing a bounded set of locations, stored as a polygon
 * 
 * @author ian
 * 
 */
public class LocationRange  extends BaseRange<LocationRange>
{
	/** the range of locations we allow
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

	/** trim my area to the area provided
	 * 
	 * @param sTwo
	 */
	@Override
	public void constrainTo(LocationRange sTwo) throws IncompatibleStateException
	{
		_myArea = (Polygon) _myArea.intersection(sTwo._myArea);
	}
}
