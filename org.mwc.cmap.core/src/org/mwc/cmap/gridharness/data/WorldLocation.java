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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.gridharness.data;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.gridharness.data.base60.Sexagesimal;
import org.mwc.cmap.gridharness.data.base60.SexagesimalFormat;


public class WorldLocation {

	private final double myLatitude;

	private final double myLongitude;

	public WorldLocation() {
		this(0, 0);
	}

	public WorldLocation(final double latitude, final double longitude) {
		myLatitude = latitude;
		myLongitude = longitude;
	}

	public double getLatitude() {
		return myLatitude;
	}

	public double getLongitude() {
		return myLongitude;
	}

	@Override
	public String toString() {
		final SexagesimalFormat format = CorePlugin.getDefault().getLocationFormat();
		final Sexagesimal latitude = format.parseDouble(getLatitude());
		final Sexagesimal longitude = format.parseDouble(getLongitude());
		return format.format(latitude, false) + " " + format.format(longitude, true);
	}

	public static final WorldLocation NULL = new WorldLocation(0, 0);
}
