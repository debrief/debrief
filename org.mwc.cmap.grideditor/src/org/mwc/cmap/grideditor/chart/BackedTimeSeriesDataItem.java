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
package org.mwc.cmap.grideditor.chart;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeriesDataItem;

import MWC.GUI.TimeStampedDataItem;

public class BackedTimeSeriesDataItem extends TimeSeriesDataItem implements BackedChartItem {

	private static final long serialVersionUID = 7733271755942292350L;

	private final TimeStampedDataItem myDomainItem;

	public BackedTimeSeriesDataItem(final FixedMillisecond period, final double value, final TimeStampedDataItem domainItem) {
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