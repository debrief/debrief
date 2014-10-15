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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.states;

import com.vividsolutions.jts.geom.Geometry;

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
	private volatile Geometry _myArea;

	/**
	 * copy constructor
	 * 
	 * @param range
	 */
	public LocationRange(LocationRange range)
	{
		this((Geometry) range._myArea.clone());
	}

	public LocationRange(Geometry area)
	{
		if (area == null) {
			throw new IllegalArgumentException("Location range must have area");
		}
		_myArea = area;
	}

	/**
	 * find out the number of points in the shape (if we have one)
	 * 
	 * @return
	 */
	public int numPoints()
	{
		return _myArea.getNumPoints();
	}

	/**
	 * trim my area to the area provided
	 * 
	 * @param sTwo
	 */
	@Override
	public void constrainTo(LocationRange sTwo) throws IncompatibleStateException
	{
		Geometry intersection = _myArea.intersection(sTwo._myArea);
		if (intersection.isEmpty()) 
		{
			throw new IncompatibleStateException("location ranges don't intersect", this, sTwo);
		}
		_myArea = intersection;
	}

	public Geometry getGeometry()
	{
		return _myArea;
	}
	
	@Override
	public int hashCode()
	{
		return _myArea.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}
		LocationRange other = (LocationRange) obj;		
		
		if (! _myArea.equals(other._myArea)) return false;
		return true;
	}			
}
