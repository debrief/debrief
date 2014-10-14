/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.table;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class SeriesLabelProvider extends LabelProvider implements ITableLabelProvider {

	private final TableModel myTableModel;

	public SeriesLabelProvider(final TableModel tableModel) {
		myTableModel = tableModel;
	}

	public String getColumnText(final Object element, final int columnIndex) {
		final TableModel.ColumnBase column = myTableModel.getColumnData(columnIndex);
		if (column == null) {
			//wow
			return "";
		}
		return column.getLabelProvider(element).getText(element);
	}

	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

}
