package org.mwc.cmap.grideditor.interpolation;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;

import flanagan.interpolation.CubicSpline;

class CubicItemsInterpolator extends AbstractItemsInterpolator {

	private final CubicSpline myWorker;

	public CubicItemsInterpolator(final GriddableItemDescriptor descriptor, final TimeStampedDataItem... baseItems) {
		super(descriptor);
		if (baseItems.length < 3) {
			throw new IllegalArgumentException("You have to provide at least 3 base points");
		}

		final double[] basePoints = new double[baseItems.length];
		final double[] values = new double[baseItems.length];
		for (int i = 0; i < basePoints.length; i++) {
			final TimeStampedDataItem nextItem = baseItems[i];
			basePoints[i] = extractBaseValue(nextItem);
			values[i] = getDoubleValue(nextItem, descriptor);
		}
		myWorker = new CubicSpline(basePoints, values);
	}

	@Override
	public boolean canInterpolate(final TimeStampedDataItem item) {
		//well, i guess there should be some constraints but I don't know them
		return true;
	}

	public Object getInterpolatedValue(final TimeStampedDataItem item) {
		if (!canInterpolate(item)) {
			throw new IllegalStateException("I told you I can't interpolate item: " + item);
		}
		final double millis = extractBaseValue(item);
		final double workerResult = Double.valueOf(myWorker.interpolate(millis));
		return getSafeInterpolatedValue(item, getDescriptor(), workerResult);
	}

}
