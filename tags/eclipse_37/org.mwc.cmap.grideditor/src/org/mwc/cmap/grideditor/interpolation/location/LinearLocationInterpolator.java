package org.mwc.cmap.grideditor.interpolation.location;

import org.mwc.cmap.grideditor.interpolation.LinearInterpolator;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.WorldLocation;


public class LinearLocationInterpolator extends AbstractLocationInterpolator {

	private LinearInterpolator myLatitudeWorker;

	private LinearInterpolator myLongitudeWorker;

	public LinearLocationInterpolator(GriddableItemDescriptor descriptor, TimeStampedDataItem... dataItems) {
		super(descriptor, dataItems);

		TimeStampedDataItem startItem = dataItems[0];
		TimeStampedDataItem endItem = dataItems[1];

		double startMillis = extractMillis(dataItems[0]);
		double endMillis = extractMillis(dataItems[1]);
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

	public boolean canInterpolate(TimeStampedDataItem item) {
		return myLatitudeWorker != null && myLongitudeWorker != null;
	}

	public Object getInterpolatedValue(TimeStampedDataItem item) {
		double millis = extractMillis(item);
		double latitude = myLatitudeWorker.interp(millis);
		double longitude = myLongitudeWorker.interp(millis);
		return new WorldLocation(latitude, longitude,0);
	}

}
