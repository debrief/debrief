package org.mwc.cmap.grideditor.data;

import org.mwc.cmap.grideditor.chart.GriddableItemChartComponent;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.ValueInUnits;


public class ChartComponentFactory {

	/**
	 * @return <code>true</code> if given descriptor can be wrapped into this
	 * 	{@link ChartComponentImpl}, in particular if it represents a bean
	 * 	property of primitive type or or a type that extends {@link Number}
	 * 	class. In this case its guaranteed that constructor won't throw
	 * 	exception's
	 */
	public static boolean isChartable(GriddableItemDescriptor descriptor) {
		return isDirectlyChartable(descriptor) || isDescriptorWithUnits(descriptor);
	}

	private static boolean isDirectlyChartable(GriddableItemDescriptor descriptor) {
		Class<?> descriptorType = descriptor.getType();
		return descriptorType.isPrimitive() || Number.class.isAssignableFrom(descriptorType);
	}

	private static boolean isDescriptorWithUnits(GriddableItemDescriptor descriptor) {
		Class<?> descriptorType = descriptor.getType();
		return ValueInUnits.class.isAssignableFrom(descriptorType);
	}

	/**
	 * Wraps given descriptor.
	 * 
	 * @throws IllegalArgumentException
	 * 		if descriptor is not chartable
	 * @see ChartComponentFactory#isChartable(GriddableItemDescriptor)
	 */
	public static GriddableItemChartComponent newChartComponent(GriddableItemDescriptor descriptor) {
		if (isDirectlyChartable(descriptor)) {
			return new ChartComponentImpl(descriptor);
		}
		if (isDescriptorWithUnits(descriptor)) {
			@SuppressWarnings("unchecked")
			Class<? extends ValueInUnits> typeImpl = (Class<? extends ValueInUnits>) descriptor.getType();
			return new WithUnitsChartComponent(typeImpl, descriptor);
		}
		throw new IllegalArgumentException(//
				"The descriptor " + descriptor.getTitle() + //
						" of type " + descriptor.getType() + " is not chartable");
	}
}
