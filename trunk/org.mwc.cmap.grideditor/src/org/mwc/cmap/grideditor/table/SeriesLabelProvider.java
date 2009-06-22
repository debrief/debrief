package org.mwc.cmap.grideditor.table;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class SeriesLabelProvider extends LabelProvider implements ITableLabelProvider {

	private final TableModel myTableModel;

	public SeriesLabelProvider(TableModel tableModel) {
		myTableModel = tableModel;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		TableModel.ColumnBase column = myTableModel.getColumnData(columnIndex);
		if (column == null) {
			//wow
			return "";
		}
		return column.getLabelProvider(element).getText(element);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

}
