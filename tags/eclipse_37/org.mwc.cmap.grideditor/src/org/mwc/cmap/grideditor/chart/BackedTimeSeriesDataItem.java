package org.mwc.cmap.grideditor.chart;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeriesDataItem;

import MWC.GUI.TimeStampedDataItem;

public class BackedTimeSeriesDataItem extends TimeSeriesDataItem implements BackedChartItem {

	private static final long serialVersionUID = 7733271755942292350L;

	private final TimeStampedDataItem myDomainItem;

	public BackedTimeSeriesDataItem(FixedMillisecond period, double value, TimeStampedDataItem domainItem) {
		super(period, value);
		myDomainItem = domainItem;
	}

	public TimeStampedDataItem getDomainItem() {
		return myDomainItem;
	}

	@Override
	public FixedMillisecond getPeriod() {
		return (FixedMillisecond) super.getPeriod();
	}

	@Override
	public Object clone() {
		//yes, we OK with that
		return super.clone();
	}
	
	public double getXValue() {
		return getPeriod().getFirstMillisecond(); //or any 
	}
}