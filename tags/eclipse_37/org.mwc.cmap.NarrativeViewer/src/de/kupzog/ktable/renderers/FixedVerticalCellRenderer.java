/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package de.kupzog.ktable.renderers;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.SWTX;

/**
 * Draws a cell in fixed style (understanding all the flags used in
 * <code>FixedCellRenderer</code>. Draws the text vertically instead of
 * horizontally.
 * <p>
 * Accepted styles:
 * <ul>
 * <li><b>STYLE_FLAT</b> for a flat look.</li>
 * <li><b>STYLE_PUSH</b> for a button-like look.</li>
 * <li><b>INDICATION_SORT</b> if a sort indicator should be painted. Has only
 * an effect when the <code>KTableModel</code> used is an instance of
 * <code>KTableSortedModel</code>. <br>
 * Can be combined with <code>STYLE_FLAT</code> or <code>STYLE_PUSH</code>
 * by or-ing.</li>
 * <li><b>INDICATION_FOCUS</b> when a focus bit should be colored differently.
 * Combine by or-ing. Note that the focus is only set to fixed cells when <code>
 * KTable.setHighlightSelectionInHeader(true)</code>
 * is set.</li>
 * <li><b>INDICATION_FOCUS_ROW</b> when row-selection mode is on and the row
 * highlighting should be present in the fixed cell on the left. </li>
 * <li><b>INDICATION_CLICKED</b> shows a visible feedback when the user clicks
 * on the cell.<br>
 * Only applicable if STYLE_PUSH is used.</li>
 * <li><b>SWT.BOLD</b> Makes the renderer draw bold text.</li>
 * <li><b>SWT.ITALIC</b> Makes the renderer draw italic text</li>
 * </ul>
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public class FixedVerticalCellRenderer extends FixedCellRenderer {

	/**
	 * @param style
	 */
	public FixedVerticalCellRenderer(int style) {
		super(style);
		setAlignment(SWTX.ALIGN_HORIZONTAL_RIGHT | SWTX.ALIGN_VERTICAL_BOTTOM | SWTX.WRAP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kupzog.ktable.KTableCellRenderer#getOptimalWidth(org.eclipse.swt.graphics.GC,
	 *      int, int, java.lang.Object, boolean)
	 */
	public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed, KTableModel model) {
		String text = SWTX.wrapText(gc, content.toString(), model.getRowHeight(row) - 6);
		int w = SWTX.getCachedStringExtent(gc, text).y;
		w += 6;
		return w;
	}

	/**
	 * Draws the actual cell content (text & image).
	 * 
	 * @param gc
	 *            The GC to use when painting.
	 * @param rect
	 *            The cell area.
	 * @param text
	 *            The text to draw.
	 * @param textColor
	 *            The text color.
	 * @param backColor
	 *            The background color to use.
	 */
	protected void drawCellContent(GC gc, Rectangle rect, String text, Image img, Color textColor, Color backColor) {
		applyFont(gc);
		drawVerticalCellContent(gc, rect, text, img, textColor, backColor);
		resetFont(gc);
	}
}
