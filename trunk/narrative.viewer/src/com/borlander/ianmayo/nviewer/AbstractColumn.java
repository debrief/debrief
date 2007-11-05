package com.borlander.ianmayo.nviewer;

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

	public AbstractColumn(int index, String columnName, IPreferenceStore store) {
		this(index, columnName, 100, store);
	}

	public AbstractColumn(int index, String columnName, int initialWidth, IPreferenceStore store) {
		myIndex = index;
		myColumnName = columnName;
		myInitialWidth = initialWidth;
		myStore = store;

		myIsVisible = myStore != null && !myStore.getBoolean(getIsHiddenPreferenceName());
	}
	
	public void addVisibilityListener(VisibilityListener visibilityListener) {
		myVisibilityListeners.add(visibilityListener);
	}
	
	public void setFilter(ColumnFilter filter){
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

	public void setVisible(boolean isVisible) {
		boolean oldValue = myIsVisible;
		myIsVisible = isVisible;
		if (myStore != null) {
			myStore.setValue(getIsHiddenPreferenceName(), !isVisible);
		}
		
		if (myIsVisible != oldValue){
			for (Column.VisibilityListener next : myVisibilityListeners){
				next.columnVisibilityChanged(this, myIsVisible);	
			}
		}
	}

	private String getIsHiddenPreferenceName() {
		return PREFERENCE_PREFIX + getColumnName();
	}
	
	
	

}