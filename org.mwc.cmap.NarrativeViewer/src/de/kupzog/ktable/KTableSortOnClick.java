/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package de.kupzog.ktable;

/**
 * This class provides the code that makes the table sort when the user clicks
 * on the table header.
 * 
 */
public class KTableSortOnClick extends KTableCellSelectionAdapter {

	KTable m_Table;
	KTableSortComparator m_SortComparator;

	public KTableSortOnClick(KTable table, KTableSortComparator comparator) {
		m_Table = table;
		m_SortComparator = comparator;
	}

	/**
	 * Implements sorting behavior when clicking on the fixed header row.
	 */
	public void fixedCellSelected(int col, int row, int statemask) {
		if (m_Table.getModel() instanceof KTableSortedModel) {
			KTableSortedModel model = (KTableSortedModel) m_Table.getModel();

			// implement the sorting when clicking on the header.
			if (row < model.getFixedHeaderRowCount() && col >= model.getFixedHeaderColumnCount()) {
				int type = KTableSortComparator.SORT_UP;
				if (model.getSortColumn() == col) {
					if (model.getSortState() == KTableSortComparator.SORT_UP) {
						type = KTableSortComparator.SORT_DOWN;
					} else if (model.getSortState() == KTableSortComparator.SORT_DOWN) {
						type = KTableSortComparator.SORT_NONE;
					}
				}

				// update the comparator properly:
				m_SortComparator.setColumnToCompare(col);
				m_SortComparator.setSortDirection(type);

				// perform the sorting
				model.sort(m_SortComparator);

				// needed to make the resorting visible!
				m_Table.redraw();
			}
		}
	}
}
