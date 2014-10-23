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
package org.mwc.cmap.grideditor.interpolation.location;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.WorldLocation;

import flanagan.interpolation.CubicSpline;

public class CubicLocationInterpolator extends AbstractLocationInterpolator {

	private final CubicSpline myLatitudeWorker;

	private final CubicSpline myLongitudeWorker;

	public CubicLocationInterpolator(final GriddableItemDescriptor descriptor, final TimeStampedDataItem... dataItems) {
		super(descriptor, dataItems);

		final double[] millisecs = new double[dataItems.length];
		final double[] latitudes = new double[dataItems.length];
		final double[] longitudes = new double[dataItems.length];

		for (int i = 0; i < dataItems.length; i++) {
			final TimeStampedDataItem nextItem = dataItems[i];
			millisecs[i] = extractMillis(nextItem);
			latitudes[i] = getLatitude().getDimensionValue(nextItem);
			longitudes[i] = getLongitude().getDimensionValue(nextItem);
		}

		myLatitudeWorker = new CubicSpline(millisecs.clone(), latitudes);
		myLongitudeWorker = new CubicSpline(millisecs.clone(), longitudes);
	}

	public boolean canInterpolate(final TimeStampedDataItem item) {
		return myLatitudeWorker != null && myLongitudeWorker != null;
	}

	public Object getInterpolatedValue(final TimeStampedDataItem item) {
		final double millis = extractMillis(item);
		final double latitude = myLatitudeWorker.interpolate(millis);
		final double longitude = myLongitudeWorker.interpolate(millis);
		return new WorldLocation(latitude, longitude, 0);
	}

}
