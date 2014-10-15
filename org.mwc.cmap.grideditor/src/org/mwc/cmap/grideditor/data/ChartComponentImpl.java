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
package org.mwc.cmap.grideditor.data;

import org.mwc.cmap.grideditor.chart.GriddableItemChartComponent;
import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;

/**
 * Implemented {@link GriddableItemChartComponent} by reflective calling double
 * getters of appropriate {@link GriddableItemDescriptor}.
 */
class ChartComponentImpl implements GriddableItemChartComponent
{

	private final GriddableItemDescriptor myDescriptor;

	/**
	 * Wraps given descriptor.
	 * <p>
	 * Intentionally package local, only {@link ChartComponentFactory} is allowed
	 * to create new instance.
	 */
	ChartComponentImpl(final GriddableItemDescriptor descriptor)
	{
		myDescriptor = descriptor;
	}

	public GriddableItemDescriptor getDescriptor()
	{
		return myDescriptor;
	}

	public double getDoubleValue(final TimeStampedDataItem dataItem)
	{
		Number value;
		if (myDescriptor.getType().isPrimitive())
		{
			final Object boxedPrimitive = BeanUtil.getItemValue(dataItem, myDescriptor);
			// should be safe -- any boxed primitive is a Number
			value = (Number) boxedPrimitive;
		}
		else
		{
				value = BeanUtil.getItemValue(dataItem, myDescriptor, Number.class);
		}
		return value.doubleValue();
	}
}
