package org.mwc.cmap.grideditor.table;

import org.eclipse.jface.viewers.ColumnViewer;

/**
 * Represents the cell of the editor (may be not exist at the time of
 * construction) on which in-place editor should be activated
 */
public interface EditableTarget {

	public ColumnViewer getColumnViewer();

	public Object getElementToEdit(Object actuallyEdited);

	public int getColumnIndex();
}
