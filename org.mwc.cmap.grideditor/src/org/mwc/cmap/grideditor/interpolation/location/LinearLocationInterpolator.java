/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.interpolation.location;

import org.mwc.cmap.grideditor.interpolation.LinearInterpolator;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.WorldLocation;


public class LinearLocationInterpolator extends AbstractLocationInterpolator {

	private LinearInterpolator myLatitudeWorker;

	private LinearInterpolator myLongitudeWorker;

	public LinearLocationInterpolator(final GriddableItemDescriptor descriptor, final TimeStampedDataItem... dataItems) {
		super(descriptor, dataItems);

		final TimeStampedDataItem startItem = dataItems[0];
		final TimeStampedDataItem endItem = dataItems[1];

		final double startMillis = extractMillis(dataItems[0]);
		final double endMillis = extractMillis(dataItems[1]);
		if (startMillis != endMillis) {
			myLatitudeWorker = new LinearInterpolator(//
					getLatitude().getDimensionValue(endItem), //
					getLatitude().getDimensionValue(startItem), //
					endMillis, startMillis);
			myLongitudeWorker = new LinearInterpolator(//
					getLongitude().getDimensionValue(endItem), //
					getLongitude().getDimensionValue(startItem), //
					endMillis, startMillis);
		}
	}

	public boolean canInterpolate(final TimeStampedDataItem item) {
		return myLatitudeWorker != null && myLongitudeWorker != null;
	}

	public Object getInterpolatedValue(final TimeStampedDataItem item) {
		final double millis = extractMillis(item);
		final double latitude = myLatitudeWorker.interp(millis);
		final double longitude = myLongitudeWorker.interp(millis);
		return new WorldLocation(latitude, longitude,0);
	}

}
