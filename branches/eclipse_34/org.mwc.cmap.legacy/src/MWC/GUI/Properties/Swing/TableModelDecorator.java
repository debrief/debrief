package MWC.GUI.Properties.Swing;

import javax.swing.*; 
import javax.swing.event.*; 
import javax.swing.table.*;

public class TableModelDecorator extends AbstractTableModel { //implements TableModel {
	protected TableModel model;

	public TableModelDecorator(TableModel model) {
		if(model == null)
			throw new IllegalArgumentException(
								"null models are not allowed");
		this.model = model;	
	}
	public int getRowCount() {
		return model.getRowCount();	
	}
	public int getColumnCount() {
		return model.getColumnCount();	
	}
	public String getColumnName(int columnIndex) {
		return model.getColumnName(columnIndex);
	}
	public Class getColumnClass(int columnIndex) {
		return model.getColumnClass(columnIndex);
	}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return model.isCellEditable(rowIndex, columnIndex);
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model.getValueAt(rowIndex, columnIndex);
	}
	public void setValueAt(Object aValue, 
								int rowIndex, int columnIndex) {
		model.setValueAt(aValue, rowIndex, columnIndex);
	}
	public void addTableModelListener(TableModelListener l) {
		model.addTableModelListener(l);
	}
	public void removeTableModelListener(TableModelListener l) {
		model.removeTableModelListener(l);
	}
}
