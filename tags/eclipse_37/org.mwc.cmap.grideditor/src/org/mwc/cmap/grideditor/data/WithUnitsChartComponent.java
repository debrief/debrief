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

	public WithUnitsChartComponent(Class<? extends ValueInUnits> unitsClass, GriddableItemDescriptor descriptor) {
		myUnitsClass = unitsClass;
		myDescriptor = descriptor;
	}

	public double getDoubleValue(TimeStampedDataItem dataItem) {
		ValueInUnits valueInUnits = BeanUtil.getItemValue(dataItem, myDescriptor, myUnitsClass);
		UnitsSet unitsSet = valueInUnits.getUnitsSet();
		return valueInUnits.getValueIn(unitsSet.getMainUnit());
	}

}
