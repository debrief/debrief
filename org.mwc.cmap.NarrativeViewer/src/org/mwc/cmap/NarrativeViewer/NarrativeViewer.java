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
package org.mwc.cmap.NarrativeViewer;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.NarrativeViewer.actions.NarrativeViewerActions;
import org.mwc.cmap.NarrativeViewer.filter.ui.FilterDialog;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellDoubleClickAdapter;
import de.kupzog.ktable.KTableCellResizeAdapter;
import de.kupzog.ktable.SWTX;

public class NarrativeViewer extends KTable
{

	final NarrativeViewerModel myModel;
	private NarrativeViewerActions myActions;

	public NarrativeViewer(final Composite parent, final IPreferenceStore preferenceStore)
	{
		super(parent, SWTX.FILL_WITH_LASTCOL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		myModel = new NarrativeViewerModel(preferenceStore,
				new ColumnSizeCalculator()
				{
					@SuppressWarnings("synthetic-access")
					public int getColumnWidth(final int col)
					{
						return getColumnRight(col) - getColumnLeft(col);
					}
				});

		myModel.addColumnVisibilityListener(new Column.VisibilityListener()
		{
			public void columnVisibilityChanged(final Column column, final boolean actualIsVisible)
			{
				refresh();
			}
		});
		setModel(myModel);

		addControlListener(new ControlAdapter()
		{
			public void controlResized(final ControlEvent e)
			{
				onColumnsResized(false);
			}
		});

		addCellResizeListener(new KTableCellResizeAdapter()
		{
			public void columnResized(final int col, final int newWidth)
			{
				onColumnsResized(false);
			}
		});

		addCellDoubleClickListener(new KTableCellDoubleClickAdapter()
		{
			public void fixedCellDoubleClicked(final int col, final int row, final int statemask)
			{
				final Column column = myModel.getVisibleColumn(col);
				showFilterDialog(column);
				final ColumnFilter filter = column.getFilter();
				if (filter != null)
				{

				}
			}
		});
	}

	public NarrativeViewerActions getViewerActions()
	{
		if (myActions == null)
		{
			myActions = new NarrativeViewerActions(this);
		}
		return myActions;
	}

	public NarrativeViewerModel getModel()
	{
		return (NarrativeViewerModel) super.getModel();
	}

	public void showFilterDialog(final Column column)
	{
		if (!myModel.hasInput())
		{
			return;
		}

		final ColumnFilter filter = column.getFilter();
		if (filter == null)
		{
			return;
		}

		final FilterDialog dialog = new FilterDialog(getShell(), myModel.getInput(),
				column);

		if (Dialog.OK == dialog.open())
		{
			dialog.commitFilterChanges();
			refresh();
		}
	}

	void onColumnsResized(final boolean force)
	{
		final GC gc = new GC(this);
		myModel.onColumnsResized(gc, force);
		gc.dispose();
	}

	public void setInput(final IRollingNarrativeProvider entryWrapper)
	{
		myModel.setInput(entryWrapper);
		refresh();
	}

	public void setTimeFormatter(final TimeFormatter timeFormatter)
	{
		myModel.setTimeFormatter(timeFormatter);
		redraw();
	}

	public void refresh()
	{
		onColumnsResized(true);
		redraw();
	}

	public boolean isWrappingEntries()
	{
		return myModel.isWrappingEntries();
	}

	public void setWrappingEntries(final boolean shouldWrap)
	{
		if (myModel.setWrappingEntries(shouldWrap))
		{
			refresh();
		}
	}

	/**
	 * the controlling time has updated
	 * 
	 * @param dtg
	 *          the selected dtg
	 */
	public void setDTG(final HiResDate dtg)
	{
		// find the table entry immediately after or on this DTG
		int theIndex = -1;
		int thisIndex = 0;

		// retrieve the list of visible rows
		final LinkedList<NarrativeEntry> visEntries = myModel.myVisibleRows;

		// step through them
		for (final Iterator<NarrativeEntry> entryIterator = visEntries.iterator(); entryIterator
				.hasNext();)
		{
			final NarrativeEntry narrativeEntry = (NarrativeEntry) entryIterator.next();

			// get the date
			final HiResDate dt = narrativeEntry.getDTG();

			// is this what we're looking for?
			if (dt.greaterThanOrEqualTo(dtg))
			{
				// yup, remember the index
				theIndex = thisIndex;
				break;
			}

			// increment the counter
			thisIndex++;
		}

		// ok, try to select this entry
		if (theIndex > -1)
		{
			// just check it's not already selected
			final int[] currentRows = super.getRowSelection();
			if (currentRows.length == 1)
			{
				// don't bother, we've already selected it
				if (currentRows[0] == theIndex + 1)
					return;
			}

			// to make sure the desired entry is fully visible (even if it's a
			// multi-line one),
			// select the entry after our target one, then our target entry.
			super.setSelection(1, theIndex + 2, true);

			// right, it's currently looking at the entry after our one. Now select
			// our one.
			super.setSelection(1, theIndex + 1, true);
		}

	}

	/**
	 * the controlling time has updated
	 * 
	 * @param dtg
	 *          the selected dtg
	 */
	public void setEntry(final NarrativeEntry entry)
	{
		// find the table entry immediately after or on this DTG
		int theIndex = -1;
		int thisIndex = 0;

		// retrieve the list of visible rows
		final LinkedList<NarrativeEntry> visEntries = myModel.myVisibleRows;

		// step through them
		for (final Iterator<NarrativeEntry> entryIterator = visEntries.iterator(); entryIterator
				.hasNext();)
		{
			final NarrativeEntry narrativeEntry = (NarrativeEntry) entryIterator.next();

			if (narrativeEntry == entry)
			{
				// yup, remember the index
				theIndex = thisIndex;
				break;
			}

			// increment the counter
			thisIndex++;
		}

		// ok, try to select this entry
		if (theIndex > -1)
		{
			// to make sure the desired entry is fully visible (even if it's a
			// multi-line one),
			// select the entry after our target one, then our target entry.
			super.setSelection(1, theIndex + 2, true);

			// right, it's currently looking at the entry after our one. Now select
			// our one.
			super.setSelection(1, theIndex + 1, true);
		}

	}
}
