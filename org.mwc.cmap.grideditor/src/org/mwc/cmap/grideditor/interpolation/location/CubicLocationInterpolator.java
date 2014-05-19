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
