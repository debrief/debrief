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

	public ColumnsResizer(Table table, int fixedColumnsWidth,
			final boolean fitOnResize)
	{
		myTable = table;
		myFixedColumnsWidth = fixedColumnsWidth;
		myTable.addListener(SWT.Resize, new Listener()
		{

			public void handleEvent(Event event)
			{
				if (mySizeDatas != null)
				{
					doResize(true, fitOnResize);
				}
			}
		});
	}

	private void doResize(boolean checkWidth, boolean fitTable)
	{
		if (checkWidth && myTableWidth == myTable.getClientArea().width)
		{
			return;
		}

		boolean updateWeights = false;
		for (ColumnSizeData sizeData : mySizeDatas)
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
			for (ColumnSizeData sizeData : mySizeDatas)
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
		for (ColumnSizeData sizeData : mySizeDatas)
		{
			totalWeight += sizeData.getWeight();
		}
		for (ColumnSizeData sizeData : mySizeDatas)
		{
			sizeData.setWidth(totalWidth * sizeData.getWeight() / totalWeight);
		}
	}

	public void fitTableWidth()
	{
		doResize(false, true);
	}

	public void setSizeDatas(Iterable<ColumnSizeData> sizeDatas)
	{
		mySizeDatas = sizeDatas;
	}
}
