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
 * @author Friederich Kupzog
 */
public interface KTableCellSelectionListener {

	/**
	 * Is called if a non-fixed cell is selected (gets the focus).
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
	public void cellSelected(int col, int row, int statemask);

	/**
	 * Is called if a fixed cell is selected (is clicked).
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
	public void fixedCellSelected(int col, int row, int statemask);

}
