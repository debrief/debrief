package org.mwc.cmap.grideditor.interpolation.location;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.WorldLocation;

import flanagan.interpolation.CubicSpline;

public class CubicLocationInterpolator extends AbstractLocationInterpolator {

	private CubicSpline myLatitudeWorker;

	private CubicSpline myLongitudeWorker;

	public CubicLocationInterpolator(GriddableItemDescriptor descriptor, TimeStampedDataItem... dataItems) {
		super(descriptor, dataItems);

		double[] millisecs = new double[dataItems.length];
		double[] latitudes = new double[dataItems.length];
		double[] longitudes = new double[dataItems.length];

		for (int i = 0; i < dataItems.length; i++) {
			TimeStampedDataItem nextItem = dataItems[i];
			millisecs[i] = extractMillis(nextItem);
			latitudes[i] = getLatitude().getDimensionValue(nextItem);
			longitudes[i] = getLongitude().getDimensionValue(nextItem);
		}

		myLatitudeWorker = new CubicSpline(millisecs.clone(), latitudes);
		myLongitudeWorker = new CubicSpline(millisecs.clone(), longitudes);
	}

	public boolean canInterpolate(TimeStampedDataItem item) {
		return myLatitudeWorker != null && myLongitudeWorker != null;
	}

	public Object getInterpolatedValue(TimeStampedDataItem item) {
		double millis = extractMillis(item);
		double latitude = myLatitudeWorker.interpolate(millis);
		double longitude = myLongitudeWorker.interpolate(millis);
		return new WorldLocation(latitude, longitude, 0);
	}

}
