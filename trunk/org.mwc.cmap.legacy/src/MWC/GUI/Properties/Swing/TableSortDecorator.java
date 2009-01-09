package MWC.GUI.Properties.Swing;


import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TableSortDecorator extends TableModelDecorator 
								implements TableModelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int indexes[];

	public TableSortDecorator(TableModel m) {
		super(m);

		if(m == null || !(m instanceof AbstractTableModel))
			throw new IllegalArgumentException("bad model");

		m.addTableModelListener(this);
		allocate();
	}
	public Object getValueAt(int row, int column) {
		return super.getValueAt(indexes[row], column);
	}
	public void tableChanged(TableModelEvent e) {
		allocate();
	}
	public void sort(int column) {
		int rowCount = model.getRowCount();

		for(int i=0; i < rowCount; i++) {
			for(int j = i+1; j < rowCount; j++) {
				if(compare(indexes[i], indexes[j], column) < 0) {
					swap(i,j);
				}
			}
		}
		fireTableStructureChanged();
	}
    public void swap(int i, int j) {
		int tmp = indexes[i];
		indexes[i] = indexes[j];
		indexes[j] = tmp;
	}
	public int compare(int i, int j, int column) {
		Object io = model.getValueAt(i,column);
		Object jo = model.getValueAt(j,column);

		String iStr=null;
		String jStr=null;
		// if these are property descriptors, use the featureDescriptor as the
		// comparison string
		if(io instanceof java.beans.PropertyDescriptor)
		{
			java.beans.PropertyDescriptor iPd = (java.beans.PropertyDescriptor)io;
			iStr = iPd.getDisplayName();
			java.beans.PropertyDescriptor jPd = (java.beans.PropertyDescriptor)jo;
			jStr = jPd.getDisplayName();
		}
		else
		{
			iStr = io.toString();
			jStr = jo.toString();
		}
		
//		int c = jo.toString().compareTo(io.toString());
		int c = jStr.compareTo(iStr);
		return (c < 0) ? -1 : ((c > 0) ? 1 : 0);
	}
	private void allocate() {
		indexes = new int[model.getRowCount()];

		for(int i=0; i < indexes.length; ++i) {
			indexes[i] = i;			
		}
	}
}
