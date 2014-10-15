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
package org.mwc.cmap.grideditor.chart;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;

import MWC.GUI.TimeStampedDataItem;

public interface ChartDataManager {

	public GriddableItemDescriptor getDescriptor();

	public String getChartTitle();

	public void setInput(GriddableSeries input);

	public ValueAxis createXAxis();

	public ValueAxis createYAxis();
	
	public XYDataset getXYDataSet();

	public void handleItemAdded(int index, TimeStampedDataItem addedItem);

	public void handleItemChanged(TimeStampedDataItem changedItem);

	public void handleItemDeleted(int oldIndex, TimeStampedDataItem deletedItem);
	
	public void attach(JFreeChartComposite chartPanel);
	
	public void detach(JFreeChartComposite chartPanel);
	
}
