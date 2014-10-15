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
package org.mwc.cmap.grideditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.actions.ActionContext;
import org.mwc.cmap.grideditor.chart.ChartDataManager;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;


public class GridEditorActionContext extends ActionContext {

	private ChartDataManager myChartInput;

	private Listener myListener = Listener.NULL;

	private final GridEditorUndoSupport myUndoSupport;

	public GridEditorActionContext(final GridEditorUndoSupport undoSupport) {
		super(null);
		myUndoSupport = undoSupport;
	}

	public GridEditorUndoSupport getUndoSupport() {
		return myUndoSupport;
	}

	public void setChartInput(final ChartDataManager chartInput) {
		myChartInput = chartInput;
		myListener.chartInputChanged();
	}

	public ChartDataManager getChartInput() {
		return myChartInput;
	}

	public GriddableItemDescriptor getChartInputDescriptor() {
		return myChartInput == null ? null : myChartInput.getDescriptor();
	}

	public void setListener(final Listener listener) {
		myListener = listener;
		if (myListener == null) {
			myListener = Listener.NULL;
		}
	}

	@Override
	public void setSelection(final ISelection selection) {
		if (selection != null && false == selection instanceof IStructuredSelection) {
			throw new IllegalStateException("Selection of table viewers is always structured : " + selection);
		}
		super.setSelection(selection);
		//damn, this is called from super class constructor, before myListener initialized
		if (myListener != null) {
			myListener.selectionChanged();
		}
	}

	public IStructuredSelection getStructuredSelection() {
		if (getSelection() == null) {
			return StructuredSelection.EMPTY;
		}
		return (IStructuredSelection) getSelection();
	}

	@Override
	public void setInput(final Object input) {
		if (input != null && false == input instanceof GriddableSeries) {
			throw new IllegalStateException("We are expecting that table input is always GriddableSeries : " + input);
		}
		super.setInput(input);
		myUndoSupport.setTableInput((GriddableSeries) input);
		myListener.tableInputChanged();
	}

	public GriddableSeries getTableInput() {
		return (GriddableSeries) getInput();
	}

	public static interface Listener {

		public void tableInputChanged();

		public void chartInputChanged();

		public void selectionChanged();

		public static final Listener NULL = new Listener() {

			public void tableInputChanged() {
				//
			}

			public void selectionChanged() {
				//
			}

			public void chartInputChanged() {
				//
			}
		};
	}

}
