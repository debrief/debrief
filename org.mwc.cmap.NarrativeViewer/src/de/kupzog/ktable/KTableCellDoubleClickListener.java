/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package de.kupzog.ktable;

/**
 * @author Friederich Kupzog
 */
public interface KTableCellDoubleClickListener {

	/**
	 * Is called if a non-fixed cell is double clicked.
	 * 
	 * @see KTable for an explanation of the term "fixed cells".
	 * @param col
	 *            the column of the cell
	 * @param row
	 *            the row of the cell
	 * @param statemask
	 *            the modifier keys that where pressed when the selection
	 *            happened.
	 */
	public void cellDoubleClicked(int col, int row, int statemask);

	/**
	 * Is called if a fixed cell is double clicked .
	 * 
	 * @see KTable for an explanation of the term "fixed cells".
	 * @param col
	 *            the column of the cell
	 * @param row
	 *            the row of the cell
	 * @param statemask
	 *            the modifier keys that where pressed when the selection
	 *            happened.
	 */
	public void fixedCellDoubleClicked(int col, int row, int statemask);

}
