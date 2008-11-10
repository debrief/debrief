package org.mwc.cmap.NarrativeViewer;

import MWC.TacticalData.NarrativeEntry;
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

	public Object getProperty(NarrativeEntry entry);
	
	public ColumnFilter getFilter();
	
	public static interface VisibilityListener {
		public void columnVisibilityChanged(Column column, boolean actualIsVisible);
	}
	
}