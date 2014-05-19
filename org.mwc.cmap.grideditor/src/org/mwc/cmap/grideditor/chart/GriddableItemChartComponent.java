package org.mwc.cmap.grideditor.chart;

import MWC.GUI.TimeStampedDataItem;

/**
 * Encapsulates a different ways to extract a chartable value from a given
 * {@link TimeStampedDataItem}.
 */
public interface GriddableItemChartComponent {

	public double getDoubleValue(TimeStampedDataItem dataItem);
}
