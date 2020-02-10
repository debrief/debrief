/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.cmap.grideditor.chart;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;

public interface ChartDataManager {

	public void attach(JFreeChartComposite chartPanel);

	public ValueAxis createXAxis();

	public ValueAxis createYAxis();

	public void detach(JFreeChartComposite chartPanel);

	public String getChartTitle();

	public GriddableItemDescriptor getDescriptor();

	public XYDataset getXYDataSet();

	public void handleItemAdded(int index, TimeStampedDataItem addedItem);

	public void handleItemChanged(TimeStampedDataItem changedItem);

	public void handleItemDeleted(TimeStampedDataItem deletedItem);

	public void setInput(GriddableSeries input);

}
