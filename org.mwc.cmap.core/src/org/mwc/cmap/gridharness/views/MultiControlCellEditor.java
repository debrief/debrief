package org.mwc.cmap.gridharness.views;

import org.eclipse.swt.widgets.Control;

public interface MultiControlCellEditor {

	/**
	 * @return the last controld for this cell editor. 'Tab'-key, when pressed
	 * 	on this control should switch editing to the next editor cell.
	 */
	public Control getLastControl();
}
