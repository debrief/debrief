/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package de.kupzog.ktable.renderers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Helper class that draws several kind of borders.
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public abstract class BorderPainter {

	public static Rectangle drawDoubleLineSeperatorBorder(GC gc, Rectangle rect, Color vColor, Color hColor, Color seperatorBGColor, boolean vSeperator, boolean hSeperator) {
		Rectangle newContentBounds = drawDefaultSolidCellLine(gc, rect, vColor, hColor);

		if (vSeperator) {
			gc.setForeground(vColor);
			gc.drawLine(rect.x + rect.width - 2, rect.y, rect.x + rect.width - 2, rect.y + rect.height);

			gc.setForeground(seperatorBGColor);
			gc.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height);

			newContentBounds.width -= 2;
		}
		if (hSeperator) {
			gc.setForeground(hColor);
			gc.drawLine(rect.x, rect.y + rect.height - 2, rect.x + rect.width, rect.y + rect.height - 2);
			gc.setForeground(seperatorBGColor);
			gc.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width, rect.y + rect.height - 1);
			newContentBounds.height -= 2;
		}

		return newContentBounds;
	}

	/**
	 * Simply draws a border line with width 1 on the bottom (<b>h</b>orizontal)
	 * and on the right side (<b>v</b>ertical). The other two sides belong to
	 * the neightbor cells and are painted by them.
	 * <p>
	 * The result is a table where a 1px line is between every two cells. This
	 * should be considered default behavior.
	 * 
	 * @param gc
	 *            The GC to use when painting.
	 * @param rect
	 *            The cell are to paint a border around.
	 * @param vBorderColor
	 *            The vertical line color for the line on the right.
	 * @param hBorderColor
	 *            The horizontal line color for the line on the bottom.
	 */
	public static final Rectangle drawDefaultSolidCellLine(GC gc, Rectangle rect, Color vBorderColor, Color hBorderColor) {
		gc.setForeground(hBorderColor);
		gc.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width, rect.y + rect.height);

		gc.setForeground(vBorderColor);
		gc.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height);

		// the 1px line is already included in the given rect (calculated in
		// KTable!)
		return rect;
	}

	/**
	 * Simply draws a solid border line. (<b>h</b>orizontal, <b>v</b>ertical).
	 * If all 4 line widths are 0, the default 1px border is painted. otherwise
	 * on every side the given width is added. Note that this is only the half
	 * side of the line, the other half is added by the neightbor cell at the
	 * appropriate side.
	 * <p>
	 * Setting lineweigts > 0 makes the cell content area returned shrink!
	 * 
	 * @param gc
	 *            The GC that should be used when drawing.
	 * @param rect
	 *            The cell area as given by KTable.
	 * @param topBorderColor
	 *            The border color for horizontal top lines.
	 * @param bottomBorderColor
	 *            The border color for horizontal bottom line.
	 * @param leftBorderColor
	 *            The border color for the left vertical line.
	 * @param rightBorderColor
	 *            The border color for vertical right line.
	 * @param leftWidth
	 *            additional width of the line on the left
	 * @param rightWidth
	 *            additional width of the line on the right
	 * @param topWidth
	 *            additional width of the line on the top
	 * @param bottomWidth
	 *            additional width of the line on the bottom.
	 * @return returns the remaining space in the cell that should be filled
	 *         with content.
	 */
	public static final Rectangle drawSolidCellLines(GC gc, Rectangle rect, Color topBorderColor, Color bottomBorderColor, Color leftBorderColor, Color rightBorderColor, int topWidth,
			int bottomWidth, int leftWidth, int rightWidth) {
		if (leftWidth < 0 || rightWidth < 0 || topWidth < 0 || bottomWidth < 0)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT, null, "Invalid border line width in KTable CellRenderer");

		// must paint at least a little bit since it is assumed that way in
		// KTable!
		rightWidth += 1;
		bottomWidth += 1;

		gc.setBackground(bottomBorderColor);
		gc.fillRectangle(rect.x, rect.y + rect.height - bottomWidth + 1, rect.width + 1, bottomWidth);
		gc.setBackground(topBorderColor);
		gc.fillRectangle(rect.x, rect.y, rect.width + 1, topWidth);

		gc.setBackground(rightBorderColor);
		gc.fillRectangle(rect.x + rect.width - rightWidth + 1, rect.y, rightWidth, rect.height + 1);
		gc.setBackground(leftBorderColor);
		gc.fillRectangle(rect.x, rect.y, leftWidth, rect.height + 1);

		// modify the new cell content area accordingly so that the lines are
		// not intersected:
		// note: this means by setting wider lines the cell shrinks!
		rect.x += leftWidth;
		rect.y += topWidth;
		rect.height = rect.height - bottomWidth - topWidth + 1;
		rect.width = rect.width - leftWidth - rightWidth + 1;
		return rect;
	}
}
