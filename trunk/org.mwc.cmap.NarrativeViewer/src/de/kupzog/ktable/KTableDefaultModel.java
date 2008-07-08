/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html

 Authors: 
 Lorenz Maierhofer, lorenz.maierhofer@logicmindguide.com

 */
package de.kupzog.ktable;

import java.util.Hashtable;

import org.eclipse.swt.graphics.Point;

/**
 * Default implementation that handles column widths and row height.
 * <p>
 * Provides a wrapper framework that allows subclasses to transparently change
 * the location of cells in the displayed table, e.g. for sorting.
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public abstract class KTableDefaultModel implements KTableModel {

	protected Hashtable m_ColWidths = new Hashtable();
	private Hashtable m_RowHeights = new Hashtable();

	// ///////////////////////////////////////////////////////
	// HANDLE HEIGHT AND WIDTH OF CELLS
	// ///////////////////////////////////////////////////////

	/**
	 * This method initializes the provided baseimplementation of the model
	 * properly.
	 * <p>
	 * This is not done in the constructor because there might be cases where
	 * some base data is set in the constructor and must be present for properly
	 * working model getter methods.
	 * <p>
	 * <b>MUST BE CALLED BY ANY SUBCLASS!</b>
	 */
	public void initialize() {
	}

	/**
	 * @param col
	 *            The column index.
	 * @return return the current column width.
	 */
	public int getColumnWidth(int col) {
		Integer width = (Integer) m_ColWidths.get(new Integer(col));
		if (width == null) {
			int initialW = getInitialColumnWidth(col);
			if (initialW < 0)
				return 0;
			return initialW;
		}
		return width.intValue();
	}

	/**
	 * Returns the initial column width for the column index given. Note that if
	 * resize is enabled, this value might not be the real width of a column.
	 * The value returned by <code>getColumnWidth()</code> corresponds to the
	 * real width used when painting the table!
	 * 
	 * @param column
	 *            The column index
	 * @return returns the initial width of the column.
	 */
	public abstract int getInitialColumnWidth(int column);

	/**
	 * @return Returns the current row height.
	 */
	public int getRowHeight(int row) {
		Integer height = (Integer) m_RowHeights.get(new Integer(row));
		if (height == null) {
			if (row == 0) {
				int h = getInitialFirstRowHeight();
				if (h > 2)
					return h;
			}
			int initialH = getInitialRowHeight(row);
			if (initialH < 2)
				return 2;
			return initialH;
		}
		if (height.intValue() < 2)
			return 2;
		return height.intValue();
	}

	/**
	 * @param row
	 *            The row index.
	 * @return Returns the initial row height that should be used on normal
	 *         cells. If resize is enabled, the value returned by
	 *         <code>getRowHeight(int)</code> might not always be this value!
	 */
	public abstract int getInitialRowHeight(int row);

	/**
	 * @return Returns the height of the first row. Adapts to the value returned
	 *         by the method getInitialFirstRowHeight() as long as no value is
	 *         set via setFirstRowHeight(int).
	 * @see #setFirstRowHeight(int)
	 * @see #getInitialFirstRowHeight()
	 * @deprecated Use getRowHeight(0) instead.
	 */
	public int getFirstRowHeight() {
		return getRowHeight(0);
	}

	/**
	 * Implement to specify the height of the first row. This value might be
	 * overwritten when setFirstRowHeight() is called.
	 * 
	 * @return Should return the height of the first row in the table.
	 * @see #setFirstRowHeight(int);
	 * @deprecated Implement getInitialRowHeight(0) instead!
	 */
	public int getInitialFirstRowHeight() {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kupzog.ktable.KTableModel#setColumnWidth(int, int)
	 */
	public void setColumnWidth(int col, int value) {
		if (value < 0)
			value = 0;
		m_ColWidths.put(new Integer(col), new Integer(value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kupzog.ktable.KTableModel#setRowHeight(int, int)
	 */
	public void setRowHeight(int row, int value) {
		if (value < 2)
			value = 2;
		m_RowHeights.put(new Integer(row), new Integer(value));
	}

	/**
	 * Sets the row height for the first row.
	 * 
	 * @deprecated Use setRowHeight(0, value) instead.
	 */
	public void setFirstRowHeight(int value) {
		setRowHeight(0, value);
	}

	// ///////////////////////////////////////////////////////
	// SET UP WRAPPERS FOR CELL INDEX DEPENDEND CALLS
	// ///////////////////////////////////////////////////////

	/**
	 * Delegates the real content retrieval to the method
	 * <code>doGetContentAt(int, int)</code>.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getContentAt(int, int)
	 */
	public Object getContentAt(int col, int row) {
		return doGetContentAt(col, row);
	}

	/**
	 * Returns the content at the given cell.
	 * 
	 * @param col
	 *            The column index.
	 * @param row
	 *            The row index.
	 * @return Returns the content of the cell thats string form is shown in the
	 *         table cell.
	 */
	public abstract Object doGetContentAt(int col, int row);

	/**
	 * Returns the tooltip for the given cell.
	 * <p>
	 * Simply calls the method <code>doGetTooltipAt()</code>.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getTooltipAt(int, int)
	 */
	public String getTooltipAt(int col, int row) {
		return doGetTooltipAt(col, row);
	}

	/**
	 * Returns the tooltip text for the given cell. Implement this method rather
	 * than <code>getTooltipAt()</code>.
	 * 
	 * @param col
	 *            The column index.
	 * @param row
	 *            The row index.
	 * @return Returns the tooltip text for the cell. Default: None.
	 */
	public String doGetTooltipAt(int col, int row) {
		return null;
	}

	/**
	 * Delegates the real editor retrieval to the method
	 * <code>doGetCellEditor(int, int)</code>.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getCellEditor(int, int)
	 */
	public KTableCellEditor getCellEditor(int col, int row) {
		return doGetCellEditor(col, row);
	}

	/**
	 * Returns the celleditor for the given table cell.
	 * 
	 * @param col
	 *            The column index.
	 * @param row
	 *            The row index.
	 * @return Returns the cell editor to use, or <code>null</code> if none.
	 */
	public abstract KTableCellEditor doGetCellEditor(int col, int row);

	/**
	 * Calls the method <code>doSetContentAt(int, int, Object)</code> to
	 * actually set the content of a table cell to the model.
	 * 
	 * @see de.kupzog.ktable.KTableModel#setContentAt(int, int,
	 *      java.lang.Object)
	 */
	public void setContentAt(int col, int row, Object value) {
		doSetContentAt(col, row, value);
	}

	/**
	 * Called to change the cell value in the model.
	 * 
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index
	 * @param value
	 *            The new value to set in the model.
	 */
	public abstract void doSetContentAt(int col, int row, Object value);

	/**
	 * Calls the method <code>doGetCellRenderer(int, int)</code> to retrieve
	 * the cell renderer for a cell.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getCellRenderer(int, int)
	 */
	public KTableCellRenderer getCellRenderer(int col, int row) {
		return doGetCellRenderer(col, row);
	}

	/**
	 * Called to retrieve the cell renderer for a given cell.
	 * 
	 * @param col
	 *            the column index
	 * @param row
	 *            The row index
	 * @return Returns a cellrenderer that renders the cell in the KTable.
	 */
	public abstract KTableCellRenderer doGetCellRenderer(int col, int row);

	/**
	 * Returns wether a given cell is fixed.
	 * 
	 * @param col
	 *            The column index
	 * @param row
	 *            the row index
	 * @return returns true if the cell is fixed, false otherwise.
	 */
	public boolean isFixedCell(int col, int row) {
		return col < getFixedColumnCount() || row < getFixedRowCount();
	}

	/**
	 * Returns wether a given cell is a header cell (in the range of
	 * fixedHeader)
	 * 
	 * @param col
	 *            the column index
	 * @param row
	 *            the row index
	 * @return returns true if the cell is fixed AND a header cell, false
	 *         otherwise.
	 */
	public boolean isHeaderCell(int col, int row) {
		return col < getFixedHeaderColumnCount() || row < getFixedHeaderRowCount();
	}

	/**
	 * @return Returns the total number of fixed rows in the table. This is
	 *         nothing else than the sum of header rows and selectable fixed
	 *         rows.
	 * @see de.kupzog.ktable.KTableModel#getFixedHeaderRowCount()
	 * @see de.kupzog.ktable.KTableModel#getFixedSelectableRowCount()
	 */
	public int getFixedRowCount() {
		return getFixedHeaderRowCount() + getFixedSelectableRowCount();
	}

	/**
	 * @return Returns the total number of fixed columns in the table. This is
	 *         nothing else than the sum of header columns and selectable fixed
	 *         columns.
	 * @see de.kupzog.ktable.KTableModel#getFixedHeaderColumnCount()
	 * @see de.kupzog.ktable.KTableModel#getFixedSelectableColumnCount()
	 */
	public int getFixedColumnCount() {
		return getFixedHeaderColumnCount() + getFixedSelectableColumnCount();
	}

	/**
	 * @return The number of rows in the table, including the fixed rows. Calls
	 *         the client method that should be implemented instead of this
	 *         method.
	 * @see #doGetRowCount()
	 */
	public int getRowCount() {
		return doGetRowCount();
	}

	/**
	 * This function tells the KTable how many rows have to be displayed.
	 * <p>
	 * KTable counts header rows as normal rows, so the number of header rows
	 * has to be added to the number of data rows. The function must at least
	 * return the number of fixed (header + selectable) rows.
	 * 
	 * @return The number of rows in the table, including the fixed rows.
	 */
	public abstract int doGetRowCount();

	/**
	 * @return The number of columns in the table, including the fixed columns.
	 *         Calls the client method that should be implemented instead of
	 *         this method.
	 * @see #doGetColumnCount()
	 */
	public int getColumnCount() {
		return doGetColumnCount();
	}

	/**
	 * This function tells the KTable how many columns have to be displayed.
	 * <p>
	 * It must at least return the number of fixed and fixed selectable Columns.
	 * So the easiest way is to return the number of normal columns and add the
	 * value of <code>getFixedColumnCount()</code>.
	 * 
	 * @return Returns the number of columns in the table, including all fixed
	 *         columns.
	 */
	public abstract int doGetColumnCount();

	/**
	 * Calls doBelongsToCell to get the cell span. Never overwrite this method,
	 * but implement doBelongsToCell().
	 * 
	 * @see de.kupzog.ktable.KTableModel#belongsToCell(int, int)
	 */
	public Point belongsToCell(int col, int row) {
		return doBelongsToCell(col, row);
	}

	/**
	 * Return (0,0) if cell should stay in its own area, or return the cell that
	 * should overlap this cell.<br>
	 * Overwrite this rather than belongsToCell()!
	 * <p>
	 * Defaults to no spanning.
	 * 
	 * @see de.kupzog.ktable.KTableModel#belongsToCell(int, int).
	 */
	public Point doBelongsToCell(int col, int row) {
		return new Point(col, row);
	}

	/**
	 * Maps the given row index that references a visible row, to one that is
	 * internally used in the tablemodel.<br>
	 * This allows different tablemodels (such as the sorted table model) to
	 * rearrange rows flexibly. This visual rearrangement leads to changed shown
	 * row indices, but using this method, the model-internal row indices stay
	 * the same.
	 * 
	 * @param shownRow
	 *            The row index as displayed by the KTable.
	 * @return Returns the row index as used in the model. This stays unchanged
	 *         even if the visual arrangement is changed (e.g. when sorting the
	 *         talbe).
	 */
	public int mapRowIndexToModel(int shownRow) {
		return shownRow;
	}

	/**
	 * Maps the given row index from a model-internal to one that references
	 * visualized table rows. This is usually used to do something with the
	 * KTable from within the tablemodel.
	 * 
	 * @param modelRow
	 *            The row index as used in the model.
	 * @return Returns the row index as needed/used by the KTable to display the
	 *         data.
	 */
	public int mapRowIndexToTable(int modelRow) {
		return modelRow;
	}

}
