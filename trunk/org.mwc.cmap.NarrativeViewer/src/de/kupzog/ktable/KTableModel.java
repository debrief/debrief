/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html

 Authors: 
 Friederich Kupzog,  fkmk@kupzog.de, www.kupzog.de/fkmk
 Lorenz Maierhofer, lorenz.maierhofer@logicmindguide.com

 */

package de.kupzog.ktable;

import org.eclipse.swt.graphics.Point;

/**
 * The table model is the most important part of KTable. It provides<br> -
 * content information<br> - layout information<br> - rendering information<br>
 * to the KTable.
 * <p>
 * Generally speaking, all functions should return their results as quick as
 * possible, since they might be called a few times when laying out and drawing
 * the widget.
 * <p>
 * NOTE that there exists a default implementation in the class
 * <code>KTableDefaultModel</code> that handles the column width and row
 * height in the table. It also provides a framework for more advanced
 * tablemodels, for example <code>KTableSortedModel</code>. <br>
 * <b>Before implementing this interface, consider extending KTableDefaultModel</b>
 * 
 * @author Friedrich Kupzog
 * @author Lorenz Maierhofer
 */

public interface KTableModel {

	/**
	 * This method should return the content at the given position. The content
	 * is an Object, that means it can be everything.
	 * <p>
	 * The returned Object is handed over to the KTableCellRenderer. You can
	 * decide which renderer is used in getCellRenderer. Usually, the renderer
	 * expects the content being of a certain type.
	 */
	Object getContentAt(int col, int row);

	/**
	 * This method allows the model to set a tooltip for a given cell. It is
	 * shown when the mouse stops over a cell and typically gives advanced
	 * information or shows the complete content.
	 * <p>
	 * Return <code>null</code> or <code>""</code> if no tooltip should be
	 * displayed.
	 * 
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index
	 * @return Returns the text that should be displayed when the tooltip for
	 *         the cell is shown to the user.
	 */
	String getTooltipAt(int col, int row);

	/**
	 * A table cell will be "in place editable" if this method returns a valid
	 * cell editor for the given cell. For no edit functionalitity return null.
	 * 
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index
	 * @return Returns an instance of KTableCellEditor that will be responsible
	 *         of showing an editor control and writing back the changed value
	 *         by calling <code>setContentAt()</code>.
	 */
	KTableCellEditor getCellEditor(int col, int row);

	/**
	 * If <code>getCellEditor()</code> does return any editor instead of
	 * <code>null</code>, the table will use this method to set the changed
	 * cell values.
	 * 
	 * @param col
	 *            The column index.
	 * @param row
	 *            The row index.
	 */
	void setContentAt(int col, int row, Object value);

	/**
	 * Returns the cell renderer for the given cell.
	 * <p>
	 * For a first approach, KTableCellRenderer.defaultRenderer can be returned.
	 * For some default renderer behavior, look at the classses in the package
	 * <code>de.kupzog.ktable.cellrenderers
	 * </code>.
	 * <p>
	 * If this does not suite your needs, you can easily derive your own
	 * cellrenderer from <code>KTableCellRenderer</code>. If it is some
	 * general, not too specific renderer, we would be happy to include it as a
	 * default renderer!
	 * 
	 * @param col
	 *            The column index
	 * @param row
	 *            The row index
	 * @return Returns the cell renderer that is responsible for drawing the
	 *         cell.
	 */
	KTableCellRenderer getCellRenderer(int col, int row);

	/**
	 * Allows cells to merge with other cells.
	 * <p>
	 * Return the column and row index of the cell the given cell should be
	 * merged with. Note that cells can only merge with cells that have a
	 * row/col index smaller or equal than their own index.
	 * <p>
	 * The content of a spanned, large cell is determined by the left upper
	 * cell, a 'supercell'. Such supercells as well as cells that do not span
	 * always return their own indices. So if no cell spanning is desired,
	 * simply return the given cell location:<br>
	 * <code>return new Point(col, row);</code>
	 * <p>
	 * To visualize the expected return value: <code>
	 * Normal table:   Spanned table:
	 *  ___________     ___________
	 * |__|__|__|__|   |     |__|__|
	 * |__|__|__|__|   |_____|__|__|
	 * </code>
	 * In this case, the left upper cell (0,0) returns its own index and is
	 * responsible for the content of the whole spanned cell. The cells (0,1),
	 * (1,0) and (1,1) are overlapped and thus not visible. So they return (0,0)
	 * to signal that they belong to the cell (0,0). Note that in this case, the
	 * value of the cell (1,1) is never requested, since the large cell must
	 * always be a rectangle. Cells like <code>
	 *  ___________
	 * |   __|__|__| 
	 * |__|__|__|__|
	 * </code>
	 * are not possible.
	 * 
	 * @param col
	 *            the column index
	 * @param row
	 *            the row index
	 * @return Return the given cell, or a cell the given cell should be merged
	 *         with. Point.x corresponds to column index, Point.y corresponds to
	 *         row index.
	 */
	Point belongsToCell(int col, int row);

	/**
	 * This function tells the KTable how many rows have to be displayed.
	 * <p>
	 * KTable counts header rows as normal rows, so the number of header rows
	 * has to be added to the number of data rows. The function must at least
	 * return the number of fixed (header + selectable) rows.
	 * 
	 * @return The number of rows in the table, including the fixed rows.
	 */
	int getRowCount();

	/**
	 * This function tells the KTable how many rows form the "row header".
	 * <p>
	 * These rows are always displayed and not scrolled. Note that the total
	 * number of fixed columns is the sum of header and selectable fixed
	 * columns.
	 * 
	 * @return int The number of fixed rows.
	 */
	int getFixedHeaderRowCount();

	/**
	 * This functon tells the KTable how many rows form a fixed region that is
	 * not scrolled. The clickable or selectable fixed columns start after
	 * getFixedRowCount() rows. Note that the number of fixed and fixed
	 * selectable rows must be smaller or equal to getRowCount().
	 * 
	 * @return Returns the number of fixed, selectable rows.
	 */
	int getFixedSelectableRowCount();

	/**
	 * This function tells the KTable how many columns have to be displayed.
	 * <p>
	 * It must at least return the number of fixed and fixed selectable Columns.
	 * 
	 * @return Returns the number of columns in the table, including all fixed
	 *         columns.
	 */
	int getColumnCount();

	/**
	 * This function tells the KTable how many columns form the "column header".
	 * These columns are always displayed and not scrolled - that means they are
	 * fixed. Note that cells in that region cannot be selected! The total of
	 * all fixed cells is formed by selectable and header cells.
	 * 
	 * @return The number of fixed columns in the table (must be smaller or
	 *         equal to the total number of columns in the table.
	 */
	int getFixedHeaderColumnCount();

	/**
	 * This functon tells the KTable how many columns form a fixed region that
	 * is not scrolled. The clickable fixed columns start after
	 * getFixedColumnCount() columns. Note that the number of fixed and fixed
	 * clickable columns must be smaller or equal to getColumnCount().
	 * 
	 * @return Returns the number of fixed, selectable columns.
	 */
	int getFixedSelectableColumnCount();

	/**
	 * Each column can have its individual width. The model has to manage these
	 * widths and return the values with this function.
	 * 
	 * @param col
	 *            The index of the column
	 * @return The width in pixels for this column.
	 */
	int getColumnWidth(int col);

	/**
	 * This function should return true if the user should be allowed to resize
	 * the given column. (all rows have the same height except the first)
	 * 
	 * @param col
	 *            The column index
	 * @return Returns true if the column is resizable.
	 */
	boolean isColumnResizable(int col);

	/**
	 * Each column can have its individual width. The model has to manage these
	 * widths. If the user resizes a column, the model has to keep track of
	 * these changes. The model is informed about such a resize by this method.
	 * (view updates are managed by the table)
	 * 
	 * @param col
	 *            the column index
	 * @param width
	 *            The width in pixels to set for the given column.
	 */
	void setColumnWidth(int col, int width);

	/**
	 * All rows except the first row have the same height.
	 * 
	 * @param row
	 *            The row index for the row height.
	 * @return the current height of all except the first row.
	 */
	int getRowHeight(int row);

	/**
	 * This function should return true if the user should be allowed to resize
	 * the rows.
	 */
	boolean isRowResizable(int row);

	/**
	 * This function should return the minimum height of the rows. It is only
	 * needed if the rows are resizable.
	 * 
	 * @return Returns the minimum height for the rows.
	 */
	int getRowHeightMinimum();

	/**
	 * If the user resizes a row, the model has to keep track of these changes.
	 * The model is informed about such a resize by this method. (view updates
	 * are managed by the table)
	 * 
	 * @param row
	 *            The row index.
	 * @param value
	 *            The height of all except the first row.
	 */
	void setRowHeight(int row, int value);
}