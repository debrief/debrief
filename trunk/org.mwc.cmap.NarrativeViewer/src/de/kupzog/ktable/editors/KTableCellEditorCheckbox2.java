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
package de.kupzog.ktable.editors;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.SWTX;

/**
 * This class is an implementation of KTableCellEditorCheckbox that simply
 * inverts a boolean cell value.
 * <p>
 * In contrast to KTableCellEditorCheckbox, this class only allows its
 * activation on a mouse event when the user clicked on a special area inside
 * the cell. The area itself can be specified when calling the constructor.
 * 
 * @see de.kupzog.ktable.editors.KTableCellEditorCheckbox
 * @see de.kupzog.ktable.cellrenderers.CheckableCellRenderer
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public class KTableCellEditorCheckbox2 extends KTableCellEditorCheckbox {

	private Point m_Active;
	private int m_hAlign, m_vAlign;

	/**
	 * Creates a checkbox celleditor that is only sensible for mouse activation
	 * in the specified area.
	 * 
	 * @param activeArea
	 *            The size of the active area inside the cell. x means width, y
	 *            means height.
	 * @param hAlign
	 *            The horizontal alignment of the active area inside the cell.
	 *            Defined in the SWTX class. Possible values:
	 *            ALIGN_HORIZONTAL_CENTER, ALIGN_HORIZONTAL_LEFT,
	 *            ALIGN_HORIZONTAL_RIGHT
	 * @param vAlign
	 *            The vertical alignment of the active area inside the cell.
	 *            Defined in the SWTX class. Possible values:
	 *            ALIGN_VERTICAL_CENTER, ALIGN_VERTICAL_BOTTOM,
	 *            ALIGN_VERTICAL_TOP
	 * @throws ClassCastException
	 *             if an invalid input is given.
	 */
	public KTableCellEditorCheckbox2(Point activeArea, int hAlign, int vAlign) {
		if (activeArea == null || !isValidHAlignment(hAlign) || !isValidVAlignment(vAlign))
			throw new ClassCastException("Check the parameters given to KTableCellEditorCheckbox2!");

		m_Active = activeArea;
		m_hAlign = hAlign;
		m_vAlign = vAlign;
	}

	/**
	 * Checks wether the given horizontal alignment parameter is valid.
	 * 
	 * @param align
	 *            The alignment to check
	 * @return True if the alignment value is valid.
	 */
	private boolean isValidHAlignment(int align) {
		if (align == SWTX.ALIGN_HORIZONTAL_CENTER || align == SWTX.ALIGN_HORIZONTAL_LEFT || align == SWTX.ALIGN_HORIZONTAL_RIGHT)
			return true;
		return false;
	}

	/**
	 * Checks wether the given vertical alignment parameter is valid.
	 * 
	 * @param align
	 *            The alignment to check
	 * @return True if the alignment value is valid.
	 */
	private boolean isValidVAlignment(int align) {
		if (align == SWTX.ALIGN_VERTICAL_TOP || align == SWTX.ALIGN_VERTICAL_CENTER || align == SWTX.ALIGN_VERTICAL_BOTTOM)
			return true;
		return false;
	}

	/**
	 * Is called when an activation is triggered via a mouse click.
	 * <p>
	 * If false is returned, the editor does not get activated.
	 * <p>
	 * All coordinates must be relative to the KTable.
	 * 
	 * @param clickLocation
	 *            The point where the mouseclick occured.
	 * @return Returns true if the editor activation should happen.
	 */
	public boolean isApplicable(int eventType, KTable table, int col, int row, Point clickLocation, String keyInput, int stateMask) {
		if (eventType == SINGLECLICK) {
			// compute active location inside the cellBoundary:
			Rectangle active = new Rectangle(0, 0, m_Active.x, m_Active.y);
			Rectangle cellBoundary = table.getCellRect(col, row);
			if (cellBoundary.width < active.width)
				active.width = cellBoundary.width;
			if (cellBoundary.height < active.height)
				active.height = cellBoundary.height;

			if (m_hAlign == SWTX.ALIGN_HORIZONTAL_LEFT)
				active.x = cellBoundary.x;
			else if (m_hAlign == SWTX.ALIGN_HORIZONTAL_RIGHT)
				active.x = cellBoundary.x + cellBoundary.width - active.width;
			else
				// center
				active.x = cellBoundary.x + (cellBoundary.width - active.width) / 2;

			if (m_vAlign == SWTX.ALIGN_VERTICAL_TOP)
				active.y = cellBoundary.y;
			else if (m_vAlign == SWTX.ALIGN_VERTICAL_BOTTOM)
				active.y = cellBoundary.y + cellBoundary.height - active.height;
			else
				active.y = cellBoundary.y + (cellBoundary.height - active.height) / 2;

			// check if clickLocation is inside the specified active area:
			if (active.contains(clickLocation))
				return true;
			return false;
		} else
			return true;
	}
}
