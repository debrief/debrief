package org.mwc.asset.SimulationController.table;

import org.eclipse.swt.widgets.TableColumn;

public class ColumnSizeData {

	private final TableColumn myTableColumn;

	private int myWeight;

	private int myWidth;

	public ColumnSizeData(TableColumn tableColumn, int weight) {
		myTableColumn = tableColumn;
		myWeight = weight;
		myWidth = myTableColumn.getWidth();
	}

	public TableColumn getTableColumn() {
		return myTableColumn;
	}

	public boolean isWidthChanged() {
		return myTableColumn.getWidth() != myWidth;
	}

	public void setWidth(int width) {
		myTableColumn.setWidth(width);
		myWidth = width;
	}

	public void updateWeight() {
		myWeight = myTableColumn.getWidth();
	}

	public int getWeight() {
		return myWeight;
	}
}
