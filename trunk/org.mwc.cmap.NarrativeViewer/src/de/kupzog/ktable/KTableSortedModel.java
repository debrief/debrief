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

import java.util.Collections;
import java.util.Vector;

import org.eclipse.swt.graphics.Point;

/**
 * Provides a transparently sorted tablemodel: For model calls, the row indices
 * are mapped according to the sorting.
 * <p>
 * The approach taken here has one drawback: the KTable does not know anything
 * about the sorting. Thus, when setting selections, listening to selections or
 * dealing with the KTable directly, you have to manually map the row indices
 * using mapRowIndexToTable(int).
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public abstract class KTableSortedModel extends KTableDefaultModel {

	private int m_Ordered = KTableSortComparator.SORT_NONE;
	private int m_SortColumn = -1;
	private KTableSortComparator m_currentSortComparator = null;

	private Vector<Integer> rowMapping;

	public void resetRowMapping() {
		int numberOfElems = getRowCount() - getFixedHeaderRowCount();
		rowMapping = new Vector<Integer>(numberOfElems);
	}

	/*
	 * @see de.kupzog.ktable.KTableDefaultModel#initialize()
	 */
	public void initialize() {
		super.initialize();
		int numberOfElems = getRowCount() - getFixedHeaderRowCount();
		rowMapping = new Vector<Integer>(numberOfElems);

		// SORT_NONE is default, so direclty map the rows 1:1
		int fixedRowCount = getFixedHeaderRowCount() + getFixedSelectableRowCount();
		for (int i = 0; i < numberOfElems; i++)
			rowMapping.add(i, new Integer(i + fixedRowCount));
	}

	/**
	 * @return Returns the current sort state of the sorted model. Can be:
	 *         <p>
	 *         <ul>
	 *         <li>SORT_NONE: Unsorted (default)
	 *         <li>SORT_UP: Sorted with largest value up
	 *         <li>SORT_DOWN: Sorted with largest value down.
	 *         </ul>
	 */
	public final int getSortState() {
		return m_Ordered;
	}

	/**
	 * Sorts the model elements so that the retrieval methods by index (e.g. of
	 * type <code>method(int col, int row)</code>) return the content ordered
	 * in the given direction.
	 * <p>
	 * Note: To make the table reflect this sorting, it must be
	 * refreshed/redrawn!
	 * <p>
	 * Note: Often it is desired that there is some visual sign of how the
	 * sorting is.
	 * 
	 * @param comparator
	 *            The KTableSortComparator that knows how to sort the rows!
	 */
	@SuppressWarnings("unchecked")
	public void sort(KTableSortComparator comparator) {
		Collections.sort(rowMapping, comparator);

		m_Ordered = comparator.getSortDirection();

		if (m_Ordered == KTableSortComparator.SORT_NONE)
			setSortColumn(-1);
		else
			setSortColumn(comparator.getColumnToSortOn());
		m_currentSortComparator = comparator;
	}

	/**
	 * @return Returns the column that is used for sorting, or -1 if no sorting
	 *         is present or sorting is SORT_NONE.
	 */
	public final int getSortColumn() {
		return m_SortColumn;
	}

	/**
	 * Sets the sort column for this model. Note that this should equal the real
	 * one defined by the comparator given to sort().
	 */
	protected final void setSortColumn(int column) {
		m_SortColumn = column;
	}

	/**
	 * Maps the given row index as it is requested by the KTable to the real
	 * model index as it is seen from within the model.
	 * 
	 * @param row
	 *            The row index used by the KTable.
	 * @return Returns the row index as processed by the model.
	 */
	public int mapRowIndexToModel(int row) {
		// we only map non-fixed cells:
		if (row < getFixedHeaderRowCount() + getFixedSelectableRowCount())
			return row;

		// if new elements were added, update the size of the mapping vector.
		if (row - getFixedRowCount() >= rowMapping.size()) {
			int fixedRowCount = getFixedHeaderRowCount() + getFixedSelectableRowCount();
			for (int i = rowMapping.size(); i < getRowCount() - fixedRowCount; i++)
				rowMapping.add(i, new Integer(i + fixedRowCount));
		}
		int bodyRow = row - getFixedRowCount();
		if (bodyRow < 0 || bodyRow >= rowMapping.size())
			return row;
		int mappedRow = ((Integer) rowMapping.get(bodyRow)).intValue();
		if (mappedRow >= getRowCount() || mappedRow < 0) {
			resetRowMapping();
			if (m_currentSortComparator != null)
				sort(m_currentSortComparator);
			return mapRowIndexToModel(row);
			// throw new IllegalArgumentException("The model has changed, making
			// the sort mapping invalid.\nPerform a resort after model
			// changes!");
		}
		return mappedRow;
	}

	/**
	 * Maps the given row index as seen from a model implementor to the row
	 * index needed by the KTable.
	 * 
	 * @param row
	 *            The row index as used in the model.
	 * @return Returns the row index as needed by the KTable.
	 */
	public int mapRowIndexToTable(int row) {
		// we only map non-fixed cells:
		if (row < getFixedHeaderRowCount() + getFixedSelectableRowCount())
			return row;

		for (int i = 0; i < rowMapping.size(); i++) {
			Integer im = (Integer) rowMapping.get(i);
			if (im.intValue() == row)
				return i + getFixedRowCount();
		}
		return row;
	}

	/**
	 * Maps between the KTable row index and the model row index!
	 * <p>
	 * Accesses the sorted model accordingly. Delegates the real content
	 * retrieval to the method <code>doGetContentAt(int, int)</code>.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getContentAt(int, int)
	 */
	public Object getContentAt(int col, int row) {
		int nrow = mapRowIndexToModel(row);
		// now have to check if the mapped cell is spanned, and if it is, take
		// the
		// cell that is responsible for the content of the big cell:
		Point valid = getValidCell(col, nrow);
		return doGetContentAt(valid.x, valid.y);
	}

	/**
	 * Returns the tooltip for the given cell.
	 * <p>
	 * Calls the method <code>doGetTooltipAt()</code> to retrieve content.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getTooltipAt(int, int)
	 */
	public String getTooltipAt(int col, int row) {
		row = mapRowIndexToModel(row);
		Point valid = getValidCell(col, row);
		return doGetTooltipAt(valid.x, valid.y);
	}

	/**
	 * Maps between the KTable row index and the model row index!
	 * <p>
	 * Delegates the real editor retrieval to the method
	 * <code>doGetCellEditor(int, int)</code>.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getCellEditor(int, int)
	 */
	public KTableCellEditor getCellEditor(int col, int row) {
		row = mapRowIndexToModel(row);
		// now have to check if the mapped cell is spanned, and if it is, take
		// the
		// cell that is responsible for the content of the big cell:
		Point valid = getValidCell(col, row);
		return doGetCellEditor(valid.x, valid.y);
	}

	/**
	 * Maps between the KTable row index and the model row index!
	 * <p>
	 * Calls the method <code>doSetContentAt(int, int, Object)</code> to
	 * actually set the content of a table cell to the model.
	 * 
	 * @see de.kupzog.ktable.KTableModel#setContentAt(int, int,
	 *      java.lang.Object)
	 */
	public void setContentAt(int col, int row, Object value) {
		row = mapRowIndexToModel(row);
		// now have to check if the mapped cell is spanned, and if it is, take
		// the
		// cell that is responsible for the content of the big cell:
		Point valid = getValidCell(col, row);
		doSetContentAt(valid.x, valid.y, value);
	}

	/**
	 * Maps between the KTable row index and the model row index!
	 * <p>
	 * Calls the method <code>doGetCellRenderer(int, int)</code> to retrieve
	 * the cell renderer for a cell.
	 * 
	 * @see de.kupzog.ktable.KTableModel#getCellRenderer(int, int)
	 */
	public KTableCellRenderer getCellRenderer(int col, int row) {
		row = mapRowIndexToModel(row);
		Point valid = getValidCell(col, row);
		return doGetCellRenderer(valid.x, valid.y);
	}

	/**
	 * Sorting disables all cell spanning. The behavior is: spanned cells are
	 * spittet, but all cells originally spanned get the content of the spanned
	 * cell.
	 * 
	 * @see de.kupzog.ktable.KTableModel#belongsToCell(int, int)
	 */
	public Point belongsToCell(int col, int row) {
		if (getSortState() == KTableSortComparator.SORT_NONE)
			return doBelongsToCell(col, row);
		// if sorting is active, all cells get rendered seperately!
		return new Point(col, row);
	}

	/**
	 * Retrieves the valid cell for the given cell. A valid cell in this context
	 * is the cell responsible for the content when the area of several cells is
	 * spanned.
	 * 
	 * @param colToCheck
	 *            The column index.
	 * @param rowToCheck
	 *            The row index.
	 * @return Returns the given cell, or if the cell is part of a spanned cell
	 *         and not responsible for the content, the content 'supercell'.
	 * @throws IllegalArgumentException
	 *             if the <code>doBeongsTo()</code> method returns a row or
	 *             col index that is larger than that given. This is a
	 *             restriction for valid cell spanning.
	 */
	protected Point getValidCell(int colToCheck, int rowToCheck) {
		// well, there is no supercell with negative indices, so don't check:
		Point found = new Point(colToCheck, rowToCheck);
		if (colToCheck == 0 || rowToCheck == 0)
			return found;

		Point lastFound = null;
		while (!found.equals(lastFound)) {
			lastFound = found;
			found = doBelongsToCell(found.x, found.y);
			if (found != null && (found.x > lastFound.x || found.y > lastFound.y))
				throw new IllegalArgumentException("When spanning over several cells, " + "supercells that determine the content of the large cell must " + "always be in the left upper corner!");
			if (found == null)
				return lastFound;
		}
		return found;
	}
}
