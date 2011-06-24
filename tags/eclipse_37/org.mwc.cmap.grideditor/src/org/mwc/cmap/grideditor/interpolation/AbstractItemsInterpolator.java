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
	public AbstractItemsInterpolator(GriddableItemDescriptor descriptor) {
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
	public static final boolean canInterpolate(GriddableItemDescriptor descriptor) {
		Class<?> type = descriptor.getType();
		if (ValueInUnits.class.isAssignableFrom(type)) {
			return true;
		}
		return double.class.equals(type) || //
				float.class.equals(type) || //
				Double.class.equals(type) || //
				Float.class.equals(type);
	}

	protected static final Object getSafeInterpolatedValue(TimeStampedDataItem item, GriddableItemDescriptor descriptor, Double interpolatedValue) {
		Class<?> descriptorType = descriptor.getType();
		if (ValueInUnits.class.isAssignableFrom(descriptorType)) {
			ValueInUnits actualValue = BeanUtil.getItemValue(item, descriptor, ValueInUnits.class);
			UnitsSet unitsSet = actualValue.getUnitsSet();
			UnitsSet.Unit mainUnit = unitsSet.getMainUnit();
			ValueInUnits result = actualValue.makeCopy();
			result.setValues(interpolatedValue, mainUnit);
			return result;
		} else if (double.class.equals(descriptorType) || Double.class.equals(descriptorType)) {
			return interpolatedValue;
		} else if (float.class.equals(descriptorType) || Float.class.equals(descriptorType)) {
			return interpolatedValue.floatValue();
		}
		throw new IllegalArgumentException("I can only interpolate double or float values, not applicable to type: " + descriptor.getType());
	}

	protected static final double getDoubleValue(TimeStampedDataItem dataItem, GriddableItemDescriptor descriptor) {
		Number value;
		if (descriptor.getType().isPrimitive()) {
			Object boxedPrimitive = BeanUtil.getItemValue(dataItem, descriptor);
			//should be safe -- any boxed primitive is a Number
			value = (Number) boxedPrimitive;
		} else if (ValueInUnits.class.isAssignableFrom(descriptor.getType())) {
			ValueInUnits actualValue = BeanUtil.getItemValue(dataItem, descriptor, ValueInUnits.class);
			UnitsSet unitsSet = actualValue.getUnitsSet();
			UnitsSet.Unit mainUnit = unitsSet.getMainUnit();
			value = actualValue.getValueIn(mainUnit);
		} else {
			value = BeanUtil.getItemValue(dataItem, descriptor, Number.class);
		}
		return value.doubleValue();
	}

	protected static final double extractBaseValue(TimeStampedDataItem dataItem) {
		return dataItem.getDTG().getDate().getTime();
	}

	public boolean canInterpolate(TimeStampedDataItem item)
	{
		return false;
	}

}
