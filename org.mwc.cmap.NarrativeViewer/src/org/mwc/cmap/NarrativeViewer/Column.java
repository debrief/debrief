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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.nebula.widgets.grid.Grid;

import MWC.TacticalData.NarrativeEntry;

public interface Column {
	public static interface VisibilityListener {
		public void columnVisibilityChanged(Column column, boolean actualIsVisible);
	}

	public CellEditor getCellEditor(Grid table);

	public CellLabelProvider getCellRenderer(ColumnViewer viewer);

	public String getColumnName();

	public int getColumnWidth();

	public ColumnFilter getFilter();

	public int getIndex();

	public Object getProperty(NarrativeEntry entry);

	public boolean isColumnWidthExpand();

	public boolean isVisible();

	public void setProperty(NarrativeEntry entry, Object obj);

	public void setVisible(boolean isVisible);

}