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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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
	public static boolean isChartable(final GriddableItemDescriptor descriptor) {
		return isDirectlyChartable(descriptor) || isDescriptorWithUnits(descriptor);
	}

	private static boolean isDirectlyChartable(final GriddableItemDescriptor descriptor) {
		final Class<?> descriptorType = descriptor.getType();
		return descriptorType.isPrimitive() || Number.class.isAssignableFrom(descriptorType);
	}

	private static boolean isDescriptorWithUnits(final GriddableItemDescriptor descriptor) {
		final Class<?> descriptorType = descriptor.getType();
		return ValueInUnits.class.isAssignableFrom(descriptorType);
	}

	/**
	 * Wraps given descriptor.
	 * 
	 * @throws IllegalArgumentException
	 * 		if descriptor is not chartable
	 * @see ChartComponentFactory#isChartable(GriddableItemDescriptor)
	 */
	public static GriddableItemChartComponent newChartComponent(final GriddableItemDescriptor descriptor) {
		if (isDirectlyChartable(descriptor)) {
			return new ChartComponentImpl(descriptor);
		}
		if (isDescriptorWithUnits(descriptor)) {
			@SuppressWarnings("unchecked")
			final
			Class<? extends ValueInUnits> typeImpl = (Class<? extends ValueInUnits>) descriptor.getType();
			return new WithUnitsChartComponent(typeImpl, descriptor);
		}
		throw new IllegalArgumentException(//
				"The descriptor " + descriptor.getTitle() + //
						" of type " + descriptor.getType() + " is not chartable");
	}
}
