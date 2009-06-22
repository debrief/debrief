package org.mwc.cmap.grideditor.data;

import org.mwc.cmap.grideditor.chart.GriddableItemChartComponent;
import org.mwc.cmap.grideditor.command.BeanUtil;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;



/**
 * Implemented {@link GriddableItemChartComponent} by reflective calling double
 * getters of appropriate {@link GriddableItemDescriptor}.
 */
class ChartComponentImpl implements GriddableItemChartComponent {

	private final GriddableItemDescriptor myDescriptor;

	/**
	 * Wraps given descriptor.
	 * <p>
	 * Intentionally package local, only {@link ChartComponentFactory} is
	 * allowed to create new instance.
	 */
	ChartComponentImpl(GriddableItemDescriptor descriptor) {
		myDescriptor = descriptor;
	}

	public GriddableItemDescriptor getDescriptor() {
		return myDescriptor;
	}

	@Override
	public double getDoubleValue(TimeStampedDataItem dataItem) {
		Number value;
		if (myDescriptor.getType().isPrimitive()) {
			Object boxedPrimitive = BeanUtil.getItemValue(dataItem, myDescriptor);
			//should be safe -- any boxed primitive is a Number
			value = (Number) boxedPrimitive;
		} else {
			value = BeanUtil.getItemValue(dataItem, myDescriptor, Number.class);
		}
		return value.doubleValue();
	}
}
