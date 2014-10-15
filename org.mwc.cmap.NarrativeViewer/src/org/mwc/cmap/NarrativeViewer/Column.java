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