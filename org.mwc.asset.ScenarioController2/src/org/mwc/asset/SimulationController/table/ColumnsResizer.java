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
package org.mwc.asset.SimulationController.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

public class ColumnsResizer
{

	private final Table myTable;

	private final int myFixedColumnsWidth;

	private Iterable<ColumnSizeData> mySizeDatas;

	private int myTableWidth;

	private int myUserTableWidth;

	private int myUserTotalWidth;

	public ColumnsResizer(final Table table, final int fixedColumnsWidth,
			final boolean fitOnResize)
	{
		myTable = table;
		myFixedColumnsWidth = fixedColumnsWidth;
		myTable.addListener(SWT.Resize, new Listener()
		{

			public void handleEvent(final Event event)
			{
				if (mySizeDatas != null)
				{
					doResize(true, fitOnResize);
				}
			}
		});
	}

	private void doResize(final boolean checkWidth, final boolean fitTable)
	{
		if (checkWidth && myTableWidth == myTable.getClientArea().width)
		{
			return;
		}

		boolean updateWeights = false;
		for (final ColumnSizeData sizeData : mySizeDatas)
		{
			if (sizeData.isWidthChanged())
			{
				updateWeights = true;
				break;
			}
		}
		if (updateWeights)
		{
			myUserTableWidth = myTableWidth;
			myUserTotalWidth = myFixedColumnsWidth;
			for (final ColumnSizeData sizeData : mySizeDatas)
			{
				sizeData.updateWeight();
				myUserTotalWidth += sizeData.getTableColumn().getWidth();
			}
		}


		myTableWidth = myTable.getClientArea().width;

		int totalWidth;
		if (fitTable)
		{
			totalWidth = myTableWidth;
			myUserTableWidth = myTableWidth;
			myUserTotalWidth = totalWidth;
		}
		else
		{
			// just check we haven't been collapsed
			if (myUserTableWidth == 0)
				return;
			totalWidth = myUserTotalWidth * myTableWidth / myUserTableWidth;
		}
		totalWidth -= myFixedColumnsWidth;

		int totalWeight = 0;
		for (final ColumnSizeData sizeData : mySizeDatas)
		{
			totalWeight += sizeData.getWeight();
		}
		for (final ColumnSizeData sizeData : mySizeDatas)
		{
			sizeData.setWidth(totalWidth * sizeData.getWeight() / totalWeight);
		}
	}

	public void fitTableWidth()
	{
		doResize(false, true);
	}

	public void setSizeDatas(final Iterable<ColumnSizeData> sizeDatas)
	{
		mySizeDatas = sizeDatas;
	}
}
