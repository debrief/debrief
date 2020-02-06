
package org.mwc.cmap.NarrativeViewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.nebula.widgets.grid.Grid;

import MWC.TacticalData.NarrativeEntry;

public interface Column {
	public String getColumnName();

	public int getColumnWidth();
	public boolean isColumnWidthExpand();

	public CellLabelProvider getCellRenderer(ColumnViewer viewer);

	public CellEditor getCellEditor(Grid table);

	public boolean isVisible();

	public void setVisible(boolean isVisible);

	public int getIndex();

	public Object getProperty(NarrativeEntry entry);
	public void setProperty(NarrativeEntry entry,Object obj);
	
	public ColumnFilter getFilter();
	
	public static interface VisibilityListener {
		public void columnVisibilityChanged(Column column, boolean actualIsVisible);
	}
	
}