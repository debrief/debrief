/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.interpolation;

import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.UnitsSet;
import org.mwc.cmap.gridharness.data.ValueInUnits;

import MWC.GUI.TimeStampedDataItem;


public abstract class AbstractItemsInterpolator implements ItemsInterpolator {

	private final GriddableItemDescriptor myDescriptor;

	/**
	 * Creates linear interpolator for given bean property and start and end
	 * points.
	 * 
	 * @throws IllegalArgumentException
	 * 		if descriptor is not interpolate-able,
	 * @see LinearItemsInterpolator#canInterpolate(GriddableItemDescriptor)
	 */
	public AbstractItemsInterpolator(final GriddableItemDescriptor descriptor) {
		if (!canInterpolate(descriptor)) {
			throw new IllegalArgumentException("I can only interpolate double or float values, not applicable to type: " + descriptor.getType());
		}
		myDescriptor = descriptor;
	}

	public GriddableItemDescriptor getDescriptor() {
		return myDescriptor;
	}

	/**
	 * @return <code>true</code> if given descriptor represents a bean property
	 * 	of either double or float primitive type, or their "boxed" counterparts
	 */
	public static final boolean canInterpolate(final GriddableItemDescriptor descriptor) {
		final Class<?> type = descriptor.getType();
		if (ValueInUnits.class.isAssignableFrom(type)) {
			return true;
		}
		return double.class.equals(type) || //
				float.class.equals(type) || //
				Double.class.equals(type) || //
				Float.class.equals(type);
	}

	protected static final Object getSafeInterpolatedValue(final TimeStampedDataItem item, final GriddableItemDescriptor descriptor, final Double interpolatedValue) {
		final Class<?> descriptorType = descriptor.getType();
		if (ValueInUnits.class.isAssignableFrom(descriptorType)) {
			final ValueInUnits actualValue = BeanUtil.getItemValue(item, descriptor, ValueInUnits.class);
			final UnitsSet unitsSet = actualValue.getUnitsSet();
			final UnitsSet.Unit mainUnit = unitsSet.getMainUnit();
			final ValueInUnits result = actualValue.makeCopy();
			result.setValues(interpolatedValue, mainUnit);
			return result;
		} else if (double.class.equals(descriptorType) || Double.class.equals(descriptorType)) {
			return interpolatedValue;
		} else if (float.class.equals(descriptorType) || Float.class.equals(descriptorType)) {
			return interpolatedValue.floatValue();
		}
		throw new IllegalArgumentException("I can only interpolate double or float values, not applicable to type: " + descriptor.getType());
	}

	protected static final double getDoubleValue(final TimeStampedDataItem dataItem, final GriddableItemDescriptor descriptor) {
		Number value;
		if (descriptor.getType().isPrimitive()) {
			final Object boxedPrimitive = BeanUtil.getItemValue(dataItem, descriptor);
			//should be safe -- any boxed primitive is a Number
			value = (Number) boxedPrimitive;
		} else if (ValueInUnits.class.isAssignableFrom(descriptor.getType())) {
			final ValueInUnits actualValue = BeanUtil.getItemValue(dataItem, descriptor, ValueInUnits.class);
			final UnitsSet unitsSet = actualValue.getUnitsSet();
			final UnitsSet.Unit mainUnit = unitsSet.getMainUnit();
			value = actualValue.getValueIn(mainUnit);
		} else {
			value = BeanUtil.getItemValue(dataItem, descriptor, Number.class);
		}
		return value.doubleValue();
	}

	protected static final double extractBaseValue(final TimeStampedDataItem dataItem) {
		return dataItem.getDTG().getDate().getTime();
	}

	public boolean canInterpolate(final TimeStampedDataItem item)
	{
		return false;
	}

}
