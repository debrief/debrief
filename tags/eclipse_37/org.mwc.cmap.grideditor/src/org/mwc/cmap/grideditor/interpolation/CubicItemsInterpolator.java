package org.mwc.cmap.grideditor.interpolation;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;

import flanagan.interpolation.CubicSpline;

class CubicItemsInterpolator extends AbstractItemsInterpolator {

	private CubicSpline myWorker;

	public CubicItemsInterpolator(GriddableItemDescriptor descriptor, TimeStampedDataItem... baseItems) {
		super(descriptor);
		if (baseItems.length < 3) {
			throw new IllegalArgumentException("You have to provide at least 3 base points");
		}

		double[] basePoints = new double[baseItems.length];
		double[] values = new double[baseItems.length];
		for (int i = 0; i < basePoints.length; i++) {
			TimeStampedDataItem nextItem = baseItems[i];
			basePoints[i] = extractBaseValue(nextItem);
			values[i] = getDoubleValue(nextItem, descriptor);
		}
		myWorker = new CubicSpline(basePoints, values);
	}

	@Override
	public boolean canInterpolate(TimeStampedDataItem item) {
		//well, i guess there should be some constraints but I don't know them
		return true;
	}

	public Object getInterpolatedValue(TimeStampedDataItem item) {
		if (!canInterpolate(item)) {
			throw new IllegalStateException("I told you I can't interpolate item: " + item);
		}
		double millis = extractBaseValue(item);
		double workerResult = Double.valueOf(myWorker.interpolate(millis));
		return getSafeInterpolatedValue(item, getDescriptor(), workerResult);
	}

}
