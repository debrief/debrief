/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package de.kupzog.ktable.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;

/**
 * An abstract base implementation for a cell editor that opens a dialog.
 * <p>
 * Implement the methods <code>getDialog()</code> and
 * <code>setupShellProperties()</code> as needed. The dialog is automatically
 * opened in blocking mode. The editor is closed when the dialog is closed by
 * the user.
 * 
 * @author Lorenz Maierhofer
 */
public abstract class TableCellEditorDialog extends KTableCellEditor {
	private Dialog m_Dialog;

	public void open(KTable table, int col, int row, Rectangle rect) {
		m_Table = table;
		m_Model = table.getModel();
		m_Rect = rect;
		m_Row = row;
		m_Col = col;
		if (m_Dialog == null) {
			m_Dialog = getDialog(table.getShell());
		}
		if (m_Dialog != null) {
			m_Dialog.create();
			m_Dialog.setBlockOnOpen(true);
			setupShellProperties(m_Dialog.getShell());
			m_Dialog.open();
		}
		close(false);
	}

	/**
	 * @return Returns the dialog that should be shown on editor activation.
	 */
	public abstract Dialog getDialog(Shell shell);

	/**
	 * Changes the properties of the dialog shell. One could be the bounds of
	 * the dialog...
	 * <p>
	 * Overwrite to change the properties.
	 * 
	 * @param dialogShell
	 *            The shell of the dialog.
	 */
	public abstract void setupShellProperties(Shell dialogShell);

	/**
	 * Called when the open-method returns.
	 */
	public void close(boolean save) {
		super.close(save);
		m_Dialog = null;
	}

	/**
	 * Sets the bounds of the dialog to the cell bounds. DEFAULT: Ignored. Set
	 * the required shell properties by overwriting the method
	 * <code>setupShellProperties(Shell)</code>.
	 */
	public void setBounds(Rectangle rect) {
		// ignored.
	}

	/**
	 * Ignored.
	 * 
	 * @see de.kupzog.ktable.KTableCellEditor#setContent(java.lang.String)
	 */
	public void setContent(Object content) {
	}

	/**
	 * Ignored, since it is no longer in use. We use a dialog instead of a
	 * control!
	 */
	protected Control createControl() {
		return null;
	}
}
