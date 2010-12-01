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

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		ColumnViewer columnViewer = (ColumnViewer) viewer;
		if (newInput == myInput) {
			return;
		}
		if (myInput != null) {
			disposeRefresher();
		}
		myInput = null;
		if (newInput instanceof GriddableSeries) {
			GriddableSeries newInputImpl = (GriddableSeries) newInput;
			installRefresher(columnViewer, newInputImpl);
			myInput = newInputImpl;
		}
	}

	public void dispose() {
		disposeRefresher();
		myInput = null;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement != myInput) {
			return NOTHING;
		}
		return myInput.getItems().toArray();
	}

	private void installRefresher(ColumnViewer viewer, GriddableSeries input) {
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

		public TableRefresher(ColumnViewer viewer, GriddableSeries data) {
			myColumnViewer = viewer;
			myData = data;
			myData.addPropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent evt) {
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
