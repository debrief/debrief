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
