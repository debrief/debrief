/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model.states;

import com.vividsolutions.jts.geom.Geometry;

/**
 * class representing a bounded set of locations, stored as a polygon
 *
 * @author ian
 *
 */
public class LocationRange extends BaseRange<LocationRange> {
	/**
	 * the range of locations we allow
	 *
	 */
	private volatile Geometry _myArea;

	public LocationRange(final Geometry area) {
		if (area == null) {
			throw new IllegalArgumentException("Location range must have area");
		}
		_myArea = area;
	}

	/**
	 * copy constructor
	 *
	 * @param range
	 */
	public LocationRange(final LocationRange range) {
		this((Geometry) range._myArea.clone());
	}

	/**
	 * trim my area to the area provided
	 *
	 * @param sTwo
	 */
	@Override
	public void constrainTo(final LocationRange sTwo) throws IncompatibleStateException {
		if (_myArea != null) {
			// check he hasn't got multiple geometries, because we can't do an intersection
			// of that
			if (sTwo != null && sTwo._myArea.getNumGeometries() == 1) {
				final Geometry intersection = _myArea.intersection(sTwo._myArea);
				if (intersection.isEmpty()) {
					throw new IncompatibleStateException("location ranges don't intersect", this, sTwo);
				}
				_myArea = intersection;
			}
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final LocationRange other = (LocationRange) obj;

		if (!_myArea.equals(other._myArea))
			return false;
		return true;
	}

	public Geometry getGeometry() {
		return _myArea;
	}

	@Override
	public int hashCode() {
		return _myArea.hashCode();
	}

	/**
	 * find out the number of points in the shape (if we have one)
	 *
	 * @return
	 */
	public int numPoints() {
		return _myArea.getNumPoints();
	}
}
