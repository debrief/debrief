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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.KTableSortComparator;
import de.kupzog.ktable.KTableSortedModel;
import de.kupzog.ktable.SWTX;

/**
 * Renderer that paints cells as fixed (respecting all the flags used in
 * <code>FixedCellRenderer</code>). Instead of painting text and images, it
 * paings checked/unchecked signs representing boolean values.
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 * @see de.kupzog.ktable.renderers.FixedCellRenderer
 */
public class FixedCheckableCellRenderer extends CheckableCellRenderer {

	// some default images:
	/**
	 * Small arrow pointing down. Can be used when displaying a sorting
	 * indicator.
	 */
	public static final Image IMAGE_ARROWDOWN = SWTX.loadImageResource(Display.getCurrent(), "/icons/arrow_down.gif");

	/** Small arrow pointing up. Can be used when displaying a sorting indicator */
	public static final Image IMAGE_ARROWUP = SWTX.loadImageResource(Display.getCurrent(), "/icons/arrow_up.gif");

	public static final Color COLOR_FIXEDBACKGROUND = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

	/**
	 * A constructor that lets the caller specify the style.
	 * 
	 * @param style
	 *            The style that should be used to paint.
	 *            <p> - Use SWT.FLAT for a flat look.<br> - Use SWT.PUSH for a
	 *            button-like look. (default)
	 *            <p>
	 *            The following additional indications can be activated: <br> -
	 *            INDICATION_FOCUS changes the background color if the fixed
	 *            cell has focus.<br> - INDICATION_FOCUS_ROW changes the
	 *            background color so that it machtes with normal cells in
	 *            rowselection mode.<br> - INDICATION_SORT shows the sort
	 *            direction when using a KTableSortedModel.<br> -
	 *            INDICATION_CLICKED shows a click feedback, if STYLE_PUSH is
	 *            specified.
	 */
	public FixedCheckableCellRenderer(int style) {
		super(style);
		m_Style |= STYLE_PUSH;
	}

	/**
	 * Paint a box with or without a checked symbol.
	 * 
	 * @see de.kupzog.ktable.KTableCellRenderer#drawCell(GC, Rectangle, int,
	 *      int, Object, boolean, boolean, boolean, KTableModel)
	 */
	public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, boolean focus, boolean fixed, boolean clicked, KTableModel model) {

		// set up the colors:
		Color bgColor = getBackground();
		Color bottomBorderColor = COLOR_LINE_DARKGRAY;
		Color rightBorderColor = COLOR_LINE_DARKGRAY;
		Color fgColor = getForeground();
		if (focus && (m_Style & INDICATION_FOCUS) != 0) {
			bgColor = COLOR_FIXEDHIGHLIGHT;
			bottomBorderColor = COLOR_TEXT;
			rightBorderColor = COLOR_TEXT;
		}
		if (focus && (m_Style & INDICATION_FOCUS_ROW) != 0) {
			bgColor = COLOR_BGROWFOCUS;
			bottomBorderColor = COLOR_BGROWFOCUS;
			rightBorderColor = COLOR_BGROWFOCUS;
			fgColor = COLOR_FGROWFOCUS;
		}

		// STYLE_FLAT:
		if ((m_Style & STYLE_FLAT) != 0) {
			rect = drawDefaultSolidCellLine(gc, rect, bottomBorderColor, rightBorderColor);

			// draw content:

			drawCellContent(gc, rect, col, content, model, bgColor, fgColor, clicked);

		} else { // STYLE_PUSH
			drawCellButton(gc, rect, "", clicked && (m_Style & INDICATION_CLICKED) != 0);

			// push style border is drawn, exclude:
			rect.x += 2;
			rect.y += 2;
			rect.width -= 5;
			rect.height -= 5;

			// draw content:
			drawCellContent(gc, rect, col, content, model, bgColor, fgColor, clicked);
		}
	}

	/**
	 * Check for sort indicator and delegate content drawing to
	 * drawCellContent()
	 */
	private void drawCellContent(GC gc, Rectangle rect, int col, Object content, KTableModel model, Color bgColor, Color fgColor, boolean clicked) {

		Image indicator = null;
		int x = 0, y = 0;

		if ((m_Style & INDICATION_SORT) != 0 && model instanceof KTableSortedModel && ((KTableSortedModel) model).getSortColumn() == col) {
			int sort = ((KTableSortedModel) model).getSortState();
			if (sort == KTableSortComparator.SORT_UP)
				indicator = IMAGE_ARROWDOWN;
			else if (sort == KTableSortComparator.SORT_DOWN)
				indicator = IMAGE_ARROWUP;

			if (indicator != null) {
				int contentLength = rect.x + 11 + gc.stringExtent(content.toString()).x;
				x = rect.x + rect.width - 8;
				if (contentLength < x)
					x = contentLength;
				else
					rect.width -= indicator.getBounds().width + 11;
				y = rect.y + rect.height / 2 - indicator.getBounds().height / 2;

				// do not draw if there is not enough space for the image:
				if (rect.width + 8 < indicator.getBounds().width) {
					rect.width += indicator.getBounds().width + 11;
					indicator = null;
				}
			}
		}

		drawCheckableImage(gc, rect, content, bgColor, clicked);

		// draw sort indicator:
		if (indicator != null) {
			gc.fillRectangle(x, y, indicator.getBounds().width, indicator.getBounds().height);
			gc.drawImage(indicator, x, y);
		}
	}

	/**
	 * Draws the cell as a button. It is visibly clickable and contains a button
	 * text. All line borders of the cell are overpainted - there will not be
	 * any border between buttons.
	 * 
	 * @param gc
	 *            The GC to use when painting.
	 * @param rect
	 *            The cell area as given by KTable. (contains 1px bottom & right
	 *            offset)
	 * @param text
	 *            The text to paint on the button.
	 * @param pressed
	 *            Wether the button should be painted as clicked/pressed or not.
	 */
	protected void drawCellButton(GC gc, Rectangle rect, String text, boolean pressed) {
		rect.height += 1;
		rect.width += 1;
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		if (pressed) {
			SWTX.drawButtonDown(gc, text, getAlignment(), null, getAlignment(), rect);
		} else {
			SWTX.drawButtonUp(gc, text, getAlignment(), null, getAlignment(), rect);
		}
	}

	/**
	 * @return returns the currently set background color. If none was set, the
	 *         default value is returned.
	 */
	public Color getBackground() {
		if (m_bgColor != null)
			return m_bgColor;
		return COLOR_FIXEDBACKGROUND;
	}
}
