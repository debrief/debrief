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

package org.mwc.debrief.satc_interface.utilities;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.vividsolutions.jts.geom.Coordinate;

import MWC.GenericData.WorldLocation;

/**
 * convenience methods to move between SATC and Debrief location models
 *
 * @author ian
 *
 */
public class conversions {
	public static WorldLocation toLocation(final Coordinate coord) {
		return new WorldLocation(coord.y, coord.x, 0);
	}

	public static WorldLocation toLocation(final GeoPoint point) {
		return new WorldLocation(point.getLat(), point.getLon(), 0);
	}

	public static GeoPoint toPoint(final WorldLocation loc) {
		final GeoPoint res = new GeoPoint(loc.getLat(), loc.getLong());
		return res;
	}

	/**
	 * declare private constructor, to prevent accidental declaration
	 *
	 */
	private conversions() {
	}
}
