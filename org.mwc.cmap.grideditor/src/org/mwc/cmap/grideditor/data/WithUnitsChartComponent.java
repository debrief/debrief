/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
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
import org.mwc.cmap.gridharness.data.UnitsSet;
import org.mwc.cmap.gridharness.data.ValueInUnits;

import MWC.GUI.TimeStampedDataItem;


class WithUnitsChartComponent implements GriddableItemChartComponent {

	private final GriddableItemDescriptor myDescriptor;

	private final Class<? extends ValueInUnits> myUnitsClass;

	public WithUnitsChartComponent(final Class<? extends ValueInUnits> unitsClass, final GriddableItemDescriptor descriptor) {
		myUnitsClass = unitsClass;
		myDescriptor = descriptor;
	}

	public double getDoubleValue(final TimeStampedDataItem dataItem) {
		final ValueInUnits valueInUnits = BeanUtil.getItemValue(dataItem, myDescriptor, myUnitsClass);
		final UnitsSet unitsSet = valueInUnits.getUnitsSet();
		return valueInUnits.getValueIn(unitsSet.getMainUnit());
	}

}
