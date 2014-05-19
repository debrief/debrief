package org.mwc.cmap.grideditor.interpolation;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;

class LinearItemsInterpolator extends AbstractItemsInterpolator {

	private final LinearInterpolator myWorker;

	/**
	 * Creates linear interpolator for given bean property and start and end
	 * points.
	 * 
	 * @throws IllegalArgumentException
	 * 		if descriptor is not interpolate-able,
	 * @see LinearItemsInterpolator#canInterpolate(GriddableItemDescriptor)
	 */
	public LinearItemsInterpolator(final TimeStampedDataItem startPoint, final TimeStampedDataItem endPoint, final GriddableItemDescriptor descriptor) {
		super(descriptor);
		myWorker = createWorker(startPoint, endPoint, descriptor);
	}

	/**
	 * @return <code>false</code> if and only if the start and end points had
	 * 	the same time-stamp
	 */
	public boolean canInterpolate(final TimeStampedDataItem item) {
		return myWorker != null;
	}

	public Object getInterpolatedValue(final TimeStampedDataItem item) {
		if (!canInterpolate(item)) {
			throw new IllegalStateException("I told you I can't interpolate item: " + item);
		}

		final double millis = extractBaseValue(item);
		final double interpolatedResult = Double.valueOf(myWorker.interp(millis));
		return getSafeInterpolatedValue(item, getDescriptor(), interpolatedResult);
	}

	private static LinearInterpolator createWorker(final TimeStampedDataItem startPoint, final TimeStampedDataItem endPoint, final GriddableItemDescriptor descriptor) {
		final double startMillis = extractBaseValue(startPoint);
		final double endMillis = extractBaseValue(endPoint);
		if (startMillis == endMillis) {
			return null;
		}

		final double startValue = getDoubleValue(startPoint, descriptor);
		final double endValue = getDoubleValue(endPoint, descriptor);

		return new LinearInterpolator(endValue, startValue, endMillis, startMillis);
	}

}
