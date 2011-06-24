package org.mwc.cmap.grideditor.chart;

import MWC.GUI.TimeStampedDataItem;


public interface BackedChartItem {

	public TimeStampedDataItem getDomainItem();
	
	public double getXValue();
}
