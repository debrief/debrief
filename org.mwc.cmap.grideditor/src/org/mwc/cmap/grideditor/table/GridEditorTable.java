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
package org.mwc.cmap.grideditor.table;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.mwc.cmap.grideditor.GridEditorActionContext;
import org.mwc.cmap.grideditor.table.actons.GridEditorActionGroup;
import org.mwc.cmap.grideditor.table.actons.TablePopupMenuBuilder;
import org.mwc.cmap.gridharness.data.GriddableSeries;

public class GridEditorTable extends Composite
{

	private final TableViewer myTableViewer;

	private final TableModel myTableModel;

	private final SeriesLabelProvider mySeriesLabelProvider;

	private final GridEditorActionContext myActionContext;

	private SelectionListener myColumnHeaderSelectionListener;

	private boolean myIsTrackingSelection = true;

	private boolean myOnlyShowVisiblePoints = true;

	public GridEditorTable(final Composite parent, final GridEditorActionGroup actionGroup)
	{
		super(parent, SWT.NONE);
		setTrackingSelection(true);
		setLayout(new FillLayout());
		myActionContext = actionGroup.getContext();

		myTableViewer = new TableViewer(this, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		myTableViewer.getTable().setHeaderVisible(true);
		myTableViewer.getTable().setLinesVisible(true);
		myTableViewer.setContentProvider(new SeriesContentProvider());
		myTableModel = new TableModel(myTableViewer, myActionContext
				.getUndoSupport());
		mySeriesLabelProvider = new SeriesLabelProvider(myTableModel);
		myTableViewer.setLabelProvider(mySeriesLabelProvider);
		myTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(final SelectionChangedEvent event)
			{
				myActionContext.setSelection(myTableViewer.getSelection());
			}
		});

		new TablePopupMenuBuilder(this, actionGroup);

		myTableViewer.getTable().addListener(SWT.MeasureItem, new Listener()
		{

			private int myComputedHeight = -1;

			public void handleEvent(final Event event)
			{
				if (myComputedHeight < 0)
				{
					final DateTime shouldFit = new DateTime(myTableViewer.getTable(), SWT.DATE
							| SWT.MEDIUM);
					final Point sizeToFit = shouldFit.computeSize(SWT.DEFAULT, SWT.DEFAULT,
							true);
					shouldFit.dispose();
					myComputedHeight = sizeToFit.y;
				}
				event.height = myComputedHeight;
			}
		});
	}

	public void setTrackingSelection(final boolean isTrackingSelection)
	{
		myIsTrackingSelection = isTrackingSelection;
	}

	public boolean isTrackingSelection()
	{
		return myIsTrackingSelection;
	}

	public boolean isOnlyShowVisible()
	{
		return myOnlyShowVisiblePoints;
	}

	public void setOnlyShowVisible(final boolean val)
	{
		myOnlyShowVisiblePoints = val;

		// ok, do an update of the data
		setInput((GriddableSeries) myTableViewer.getInput());
	}

	public void setColumnHeaderSelectionListener(final SelectionListener listener)
	{
		myColumnHeaderSelectionListener = listener;
	}

	public void setInput(final GriddableSeries series)
	{

		// have we received real data?
		if (series != null)
		{
			series.setOnlyShowVisibleItems(myOnlyShowVisiblePoints);
		}

		myTableModel.setInputSeries(series);
		// the set of columns was probably changed
		refreshTableColumns();
		myTableViewer.setInput(series);
		myActionContext.setInput(series);
	}

	public TableViewer getTableViewer()
	{
		return myTableViewer;
	}

	public GridEditorActionContext getActionContext()
	{
		return myActionContext;
	}

	public TableModel getTableModel()
	{
		return myTableModel;
	}

	private void refreshTableColumns()
	{
		// this sets up the cell label providers for all columns at once
		myTableViewer.setLabelProvider(mySeriesLabelProvider);

		if (myColumnHeaderSelectionListener != null)
		{
			for (final TableColumn next : myTableViewer.getTable().getColumns())
			{
				next.addSelectionListener(myColumnHeaderSelectionListener);
			}
		}
	}
}
