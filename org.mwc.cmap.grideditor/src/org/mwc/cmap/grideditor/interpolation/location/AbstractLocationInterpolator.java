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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.interpolation.location;

import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.grideditor.interpolation.ItemsInterpolator;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.WorldLocation;


public abstract class AbstractLocationInterpolator implements ItemsInterpolator {

	private final DimensionAccess myLatitude;

	private final DimensionAccess myLongitude;

	public AbstractLocationInterpolator(final GriddableItemDescriptor descriptor, final TimeStampedDataItem... dataItems) {
		myLatitude = new LatitudeAccess(descriptor);
		myLongitude = new LongitudeAccess(descriptor);
		if (dataItems.length < 2) {
			throw new IllegalArgumentException("I need at least 2 points to interpolate, actually " + dataItems.length);
		}
	}

	protected DimensionAccess getLatitude() {
		return myLatitude;
	}

	protected DimensionAccess getLongitude() {
		return myLongitude;
	}

	protected static final double extractMillis(final TimeStampedDataItem item) {
		return item.getDTG().getDate().getTime();
	}

	//	private static abstract class WorkerWrapper {
	//
	//		private final DimensionAccess myDimension;
	//
	//		protected abstract double doInterpolate(double x);
	//
	//		public WorkerWrapper(DimensionAccess dimension) {
	//			myDimension = dimension;
	//		}
	//
	//		protected double getValueFor(TimeStampedDataItem item) {
	//			return myDimension.getDimensionValue(item);
	//		}
	//
	//		public double interpolate(TimeStampedDataItem item) {
	//			return doInterpolate(extractMillis(item));
	//		}
	//	}
	//
	//	private static class LinearWrapper implements WorkerWrapper {
	//
	//		private final LinearInterpolator myWorker;
	//
	//		public LinearWrapper(LinearInterpolator worker, DimensionAccess dimension) {
	//			super(dimension)
	//			myWorker = worker;
	//		}
	//
	//		@Override
	//		public double doInterpolate(double x) {
	//			return myWorker.interp(x);
	//		}
	//	}
	//
	//	private static class CubicWrapper implements WorkerWrapper {
	//
	//		private final CubicSpline myWorker;
	//
	//		public CubicWrapper(CubicSpline worker) {
	//			myWorker = worker;
	//		}
	//
	//		@Override
	//		public double doInterpolate(double x) {
	//			return myWorker.interpolate(x);
	//		}
	//	}

	/*	private static LinearInterpolator createWorker(TimeStampedDataItem startPoint, TimeStampedDataItem endPoint, GriddableItemDescriptor descriptor) {
			if (startMillis == endMillis) {
				return null;
			}

			double startValue = getDoubleValue(startPoint, descriptor);
			double endValue = getDoubleValue(endPoint, descriptor);

			return 
		}

	*/
	protected static abstract class DimensionAccess {

		private final GriddableItemDescriptor myDescriptor;

		public DimensionAccess(final GriddableItemDescriptor descriptor) {
			myDescriptor = descriptor;
		}

		public double getDimensionValue(final TimeStampedDataItem item) {
			final WorldLocation location = BeanUtil.getItemValue(item, myDescriptor, WorldLocation.class);
			return location == null ? 0 : getDimension(location);
		}

		protected abstract double getDimension(WorldLocation location);
	}

	private static class LatitudeAccess extends DimensionAccess {

		public LatitudeAccess(final GriddableItemDescriptor descriptor) {
			super(descriptor);
		}

		@Override
		protected double getDimension(final WorldLocation location) {
			return location.getLat();
		}
	}

	private static class LongitudeAccess extends DimensionAccess {

		public LongitudeAccess(final GriddableItemDescriptor descriptor) {
			super(descriptor);
		}

		@Override
		protected double getDimension(final WorldLocation location) {
			return location.getLong();
		}
	}

}
