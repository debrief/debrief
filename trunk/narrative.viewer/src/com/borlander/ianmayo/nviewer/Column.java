package com.borlander.ianmayo.nviewer;

import com.borlander.ianmayo.nviewer.model.IEntry;

import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;

public interface Column {
	public String getColumnName();

	public int getColumnWidth();

	public KTableCellRenderer getCellRenderer();

	public KTableCellEditor getCellEditor();

	public boolean isVisible();

	public void setVisible(boolean isVisible);

	public int getIndex();

	public Object getProperty(IEntry entry);
	
	public ColumnFilter getFilter();
	
	public static interface VisibilityListener {
		public void columnVisibilityChanged(Column column, boolean actualIsVisible);
	}
	
}