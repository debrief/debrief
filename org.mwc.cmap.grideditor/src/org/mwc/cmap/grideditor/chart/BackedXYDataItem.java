/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.chart;

import org.jfree.data.xy.XYDataItem;

import MWC.GUI.TimeStampedDataItem;

public class BackedXYDataItem extends XYDataItem implements BackedChartItem {

	private static final long serialVersionUID = 3331357479111378526L;

	private final TimeStampedDataItem myDomainItem;

	public BackedXYDataItem(final double x, final double y, final TimeStampedDataItem domainItem) {
		super(x, y);
		myDomainItem = domainItem;
	}

	public TimeStampedDataItem getDomainItem() {
		return myDomainItem;
	}
	
	@Override
	public Object clone() {
		//sic
		return super.clone();
	}

}
