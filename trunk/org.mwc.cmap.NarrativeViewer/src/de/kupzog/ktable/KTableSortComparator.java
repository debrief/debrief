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

import java.util.Comparator;

import org.eclipse.swt.graphics.Point;

/**
 * Implementations of this class are used when sorting a tablemodel.
 * 
 * @see de.kupzog.ktable.KTableSortedModel
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public abstract class KTableSortComparator implements Comparator {

	public static final int SORT_NONE = -1;
	public static final int SORT_UP = 1;
	public static final int SORT_DOWN = 2;

	private int m_ColIndex = -1;
	private KTableSortedModel m_Model;
	private int m_Direction = SORT_NONE;

	/**
	 * Creates a new comparator on the given KTableSortedModel.
	 * 
	 * @param model
	 *            The mode to compare on.
	 */
	public KTableSortComparator(KTableSortedModel model, int columnIndex, int direction) {
		setModel(model);
		setSortDirection(direction);
		setColumnToCompare(columnIndex);
	}

	/**
	 * Compares two cells.
	 * <p>
	 * The given objects are of type Integer and represent the row numbers to
	 * use.
	 * 
	 * @throws ClassCastException
	 *             if the arguments' types prevent them from being compared by
	 *             this Comparator.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public final int compare(Object o1, Object o2) {
		if (!(o1 instanceof Integer) || !(o2 instanceof Integer))
			throw new ClassCastException("KTableSortComparator was used in a way not allowed!");

		int row1 = ((Integer) o1).intValue();
		int row2 = ((Integer) o2).intValue();

		if (m_Direction == SORT_NONE) {
			if (row1 > row2)
				return 1;
			if (row1 < row2)
				return -1;
			return 0;
		}

		// translate to content cells if cell spanning is active:
		Point loc1 = m_Model.getValidCell(m_ColIndex, row1);
		if (loc1 == null)
			loc1 = new Point(m_ColIndex, row1);
		Point loc2 = m_Model.getValidCell(m_ColIndex, row2);
		if (loc2 == null)
			loc2 = new Point(m_ColIndex, row2);

		Object content1 = m_Model.doGetContentAt(loc1.x, loc1.y);
		Object content2 = m_Model.doGetContentAt(loc2.x, loc2.y);

		if (m_Direction == SORT_DOWN)
			return -doCompare(content1, content2, row1, row2);
		else
			return doCompare(content1, content2, row1, row2);
	}

	/**
	 * Implement this method to do the actual compare between the two cell
	 * contents.
	 * 
	 * @param o1
	 *            The cell content of the first cell
	 * @param o2
	 *            The cell content of the second cell
	 * @param row1
	 *            The row index where o1 was found in the model.
	 * @param row2
	 *            The row index where o2 was found in the model.
	 * @return Returns an int smaller, equal or larger than 0 if o1 is smaller,
	 *         equal or larger than o2.
	 */
	public abstract int doCompare(Object o1, Object o2, int row1, int row2);

	/**
	 * Sets the column index this comparator operates on.
	 * 
	 * @param column
	 *            the column index to use.
	 */
	public void setColumnToCompare(int column) {
		// if (row>=m_Model.getFixedRowCount())
		// throw new IllegalArgumentException("An invalid column index was
		// given!");

		m_ColIndex = column;
	}

	/**
	 * @return Returns the sorting direction, either SORT_NONE, SORT_UP or
	 *         SORT_DOWN.
	 */
	public int getSortDirection() {
		return m_Direction;
	}

	/**
	 * Sets the sorting direction.
	 * 
	 * @param direction
	 *            The sort direction, either SORT_NONE, SORT_UP or SORT_DOWN.
	 */
	public void setSortDirection(int direction) {
		if (direction != SORT_UP && direction != SORT_DOWN && direction != SORT_NONE)
			throw new IllegalArgumentException("Undefined sorting direction: " + direction);

		m_Direction = direction;
	}

	/**
	 * @return Returns the column index that serves as a base for the sorting.
	 */
	public int getColumnToSortOn() {
		return m_ColIndex;
	}

	/**
	 * @return Returns the model this comparator compares on.
	 */
	public KTableSortedModel getModel() {
		return m_Model;
	}

	/**
	 * Sets the tablemodel to work on.
	 * 
	 * @param model
	 *            The <class>KTableSortedModel</class> to work with.
	 */
	public void setModel(KTableSortedModel model) {
		m_Model = model;
	}
}
