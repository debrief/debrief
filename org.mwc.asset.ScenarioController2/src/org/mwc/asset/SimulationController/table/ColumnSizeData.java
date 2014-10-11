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
package org.mwc.asset.SimulationController.table;

import org.eclipse.swt.widgets.TableColumn;

public class ColumnSizeData
{

	private final TableColumn myTableColumn;

	private int myWeight;

	private int myWidth;

	public ColumnSizeData(final TableColumn tableColumn, final int weight)
	{
		myTableColumn = tableColumn;
		myWeight = weight;
		myWidth = myTableColumn.getWidth();
	}

	public TableColumn getTableColumn()
	{
		return myTableColumn;
	}

	public int getWeight()
	{
		return myWeight;
	}

	public boolean isWidthChanged()
	{
		return myTableColumn.getWidth() != myWidth;
	}

	public void setWidth(final int width)
	{
		myTableColumn.setWidth(width);
		myWidth = width;
	}

	public void updateWeight()
	{
		myWeight = myTableColumn.getWidth();
	}
}
