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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.NarrativeViewer;

import java.util.LinkedHashSet;

import org.eclipse.jface.preference.IPreferenceStore;

import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;

abstract class AbstractColumn implements Column {
	private static final String PREFERENCE_PREFIX = "com.borlander.ianmayo.nviewer.preferences.isHidden.";

	private final int myIndex;
	private final String myColumnName;
	private final int myInitialWidth;
	private final IPreferenceStore myStore;

	private KTableCellRenderer myRenderer;
	private boolean myRendererCreated;

	private boolean myIsVisible = true;
	private ColumnFilter myFilter;
	
	private final LinkedHashSet<VisibilityListener> myVisibilityListeners = new LinkedHashSet<VisibilityListener>(); 

	protected abstract KTableCellRenderer createRenderer();

	public AbstractColumn(final int index, final String columnName, final IPreferenceStore store) {
		this(index, columnName, 100, store);
	}

	public AbstractColumn(final int index, final String columnName, final int initialWidth, final IPreferenceStore store) {
		myIndex = index;
		myColumnName = columnName;
		myInitialWidth = initialWidth;
		myStore = store;

		myIsVisible = myStore != null && !myStore.getBoolean(getIsHiddenPreferenceName());
	}
	
	public void addVisibilityListener(final VisibilityListener visibilityListener) {
		myVisibilityListeners.add(visibilityListener);
	}
	
	public void setFilter(final ColumnFilter filter){
		myFilter = filter;
	}
	
	public ColumnFilter getFilter() {
		return myFilter;
	}

	public int getIndex() {
		return myIndex;
	}

	public final int getColumnWidth() {
		return myInitialWidth;
	}

	public final String getColumnName() {
		return myColumnName;
	}

	public KTableCellRenderer getCellRenderer() {
		if (!myRendererCreated) {
			myRenderer = createRenderer();
			myRendererCreated = true;
		}
		return myRenderer;
	}

	public KTableCellEditor getCellEditor() {
		// by default -- read only
		return null;
	}

	public boolean isVisible() {
		return myIsVisible;
	}

	public void setVisible(final boolean isVisible) {
		final boolean oldValue = myIsVisible;
		myIsVisible = isVisible;
		if (myStore != null) {
			myStore.setValue(getIsHiddenPreferenceName(), !isVisible);
		}
		
		if (myIsVisible != oldValue){
			for (final Column.VisibilityListener next : myVisibilityListeners){
				next.columnVisibilityChanged(this, myIsVisible);	
			}
		}
	}

	private String getIsHiddenPreferenceName() {
		return PREFERENCE_PREFIX + getColumnName();
	}
	
	
	

}