/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.grideditor.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mwc.cmap.gridharness.data.GriddableSeries;


public class SeriesContentProvider implements IStructuredContentProvider {

	private static final Object[] NOTHING = new Object[0];

	private GriddableSeries myInput;

	private TableRefresher myTableRefresher;

	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		final ColumnViewer columnViewer = (ColumnViewer) viewer;
		if (newInput == myInput) {
			return;
		}
		if (myInput != null) {
			disposeRefresher();
		}
		myInput = null;
		if (newInput instanceof GriddableSeries) {
			final GriddableSeries newInputImpl = (GriddableSeries) newInput;
			installRefresher(columnViewer, newInputImpl);
			myInput = newInputImpl;
		}
	}

	public void dispose() {
		disposeRefresher();
		myInput = null;
	}

	public Object[] getElements(final Object inputElement) {
		if (inputElement != myInput) {
			return NOTHING;
		}
		return myInput.getItems().toArray();
	}

	private void installRefresher(final ColumnViewer viewer, final GriddableSeries input) {
		disposeRefresher();
		myTableRefresher = new TableRefresher(viewer, input);
	}

	private void disposeRefresher() {
		if (myTableRefresher != null) {
			myTableRefresher.dispose();
			myTableRefresher = null;
		}
	}

	private static class TableRefresher implements PropertyChangeListener {

		private final ColumnViewer myColumnViewer;

		private GriddableSeries myData;

		public TableRefresher(final ColumnViewer viewer, final GriddableSeries data) {
			myColumnViewer = viewer;
			myData = data;
			myData.addPropertyChangeListener(this);
		}

		public void propertyChange(final PropertyChangeEvent evt) {
			if (myColumnViewer.getControl().isDisposed()) {
				dispose();
				return;
			}
			if (myData == null) {
				return;
			}

			if (GriddableSeries.PROPERTY_CHANGED.equals(evt.getPropertyName())) {
				myColumnViewer.refresh(evt.getNewValue(), true);
			} else if (GriddableSeries.PROPERTY_DELETED.equals(evt.getPropertyName()) || //
					GriddableSeries.PROPERTY_ADDED.equals(evt.getPropertyName())) {
				myColumnViewer.refresh(true);
			}
		}

		public void dispose() {
			if (myData != null) {
				myData.removePropertyChangeListener(this);
				myData = null;
			}
		}
	}
}
