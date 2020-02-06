/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.cmap.NarrativeViewer;

import java.util.LinkedHashSet;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.nebula.widgets.grid.Grid;

import MWC.TacticalData.NarrativeEntry;

abstract class AbstractColumn implements Column {
	private static final String PREFERENCE_PREFIX = "com.borlander.ianmayo.nviewer.preferences.isHidden.";

	private final int myIndex;
	private final String myColumnName;
	private final IPreferenceStore myStore;

	private CellLabelProvider myRenderer;

	private boolean myIsVisible = true;
	private ColumnFilter myFilter;

	private final LinkedHashSet<VisibilityListener> myVisibilityListeners = new LinkedHashSet<VisibilityListener>();

	public AbstractColumn(final int index, final String columnName, final IPreferenceStore store) {
		myIndex = index;
		myColumnName = columnName;
		myStore = store;

		myIsVisible = myStore != null && !myStore.getBoolean(getIsHiddenPreferenceName());
	}

	public void addVisibilityListener(final VisibilityListener visibilityListener) {
		myVisibilityListeners.add(visibilityListener);
	}

	protected void columnSelection(final NarrativeViewer viewer) {

	}

	protected abstract CellLabelProvider createRenderer(ColumnViewer viewer);

	@Override
	public CellEditor getCellEditor(final Grid table) {
		// by default -- read only
		return null;
	}

	@Override
	public CellLabelProvider getCellRenderer(final ColumnViewer viewer) {
		if (myRenderer == null) {
			myRenderer = createRenderer(viewer);
		}
		return myRenderer;
	}

	@Override
	public final String getColumnName() {
		return myColumnName;
	}

	@Override
	public ColumnFilter getFilter() {
		return myFilter;
	}

	@Override
	public int getIndex() {
		return myIndex;
	}

	private String getIsHiddenPreferenceName() {
		return PREFERENCE_PREFIX + getColumnName();
	}

	@Override
	public boolean isColumnWidthExpand() {
		return false;
	}

	@Override
	public boolean isVisible() {
		return myIsVisible;
	}

	public boolean isWrap() {
		return false;
	}

	public boolean isWrapSupport() {
		return false;
	}

	public void setFilter(final ColumnFilter filter) {
		myFilter = filter;
	}

	@Override
	public void setProperty(final NarrativeEntry entry, final Object obj) {
		// do nothing

	}

	@Override
	public void setVisible(final boolean isVisible) {
		final boolean oldValue = myIsVisible;
		myIsVisible = isVisible;
		if (myStore != null) {
			myStore.setValue(getIsHiddenPreferenceName(), !isVisible);
		}

		if (myIsVisible != oldValue) {
			for (final Column.VisibilityListener next : myVisibilityListeners) {
				next.columnVisibilityChanged(this, myIsVisible);
			}
		}
	}

}