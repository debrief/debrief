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
