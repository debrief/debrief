/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 
 Author: Friederich Kupzog  
 fkmk@kupzog.de
 www.kupzog.de/fkmk
 */
package de.kupzog.ktable;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.kupzog.ktable.renderers.DefaultCellRenderer;

/**
 * @author Friederich Kupzog
 */
public interface KTableCellRenderer {

	public static KTableCellRenderer defaultRenderer = new DefaultCellRenderer(0);

	/**
	 * Returns the optimal width of the given cell (used by column resizing)
	 * 
	 * @param col
	 * @param row
	 * @param content
	 * @param fixed
	 * @return int
	 */
	public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed, KTableModel model);

	/**
	 * This method is called from KTable to draw a table cell.
	 * <p>
	 * Note that there are several helper methods that can do specified things
	 * for you.
	 * 
	 * @param gc
	 *            The gc to draw on
	 * @param rect
	 *            The coordinates and size of the cell (add 1 to width and hight
	 *            to include the borders)
	 * @param col
	 *            The column
	 * @param row
	 *            The row
	 * @param content
	 *            The content of the cell (as given by the table model)
	 * @param focus
	 *            True if the cell is selected
	 * @param header
	 *            True if the cell is an unscrollable header cell (not an
	 *            unscrollable body cell!)
	 * @param clicked
	 *            True if the cell is currently clicked (useful e.g. to paint a
	 *            pressed button) the case when fixed row and column elements
	 *            should be highlighted because a cell in that row and column
	 *            has focus.
	 * @param model
	 *            The KTableModel that holds the data for the cell. Note that
	 *            this is only included into the parameter list to allow more
	 *            flexible cell renderers. Models might provide additional
	 *            information that can be requested when rendering.
	 */
	public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, boolean focus, boolean header, boolean clicked, KTableModel model);

}