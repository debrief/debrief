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
import org.eclipse.swt.graphics.Rectangle;

import de.kupzog.ktable.KTableModel;

/**
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public class BarDiagramCellRenderer extends DefaultCellRenderer {

	/**
	 * @param style
	 *            The style bits to use. Currently supported are:<br> -
	 *            INDICATION_FOCUS<br> - INDICATION_FOCUS_ROW<br> -
	 *            INDICATION_GRADIENT
	 */
	public BarDiagramCellRenderer(int style) {
		super(style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kupzog.ktable.KTableCellRenderer#getOptimalWidth(org.eclipse.swt.graphics.GC,
	 *      int, int, java.lang.Object, boolean)
	 */
	public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed, KTableModel model) {
		return 20;
	}

	/**
	 * @param content
	 *            The content is expected to be a Float value between 0 and 1
	 *            that represents the fraction of the cell width that should be
	 *            used for the bar.
	 * @see de.kupzog.ktable.KTableCellRenderer#drawCell(GC, Rectangle, int,
	 *      int, Object, boolean, boolean, boolean, KTableModel)
	 */
	public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, boolean focus, boolean fixed, boolean clicked, KTableModel model) {

		if (focus && (m_Style & INDICATION_FOCUS) != 0) {
			rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY, COLOR_LINE_LIGHTGRAY);
			drawBar(gc, rect, content, COLOR_BGFOCUS, getForeground());
			gc.drawFocus(rect.x, rect.y, rect.width, rect.height);

		} else if (focus && (m_Style & INDICATION_FOCUS_ROW) != 0) {
			rect = drawDefaultSolidCellLine(gc, rect, COLOR_BGROWFOCUS, COLOR_BGROWFOCUS);
			Color defaultBg = COLOR_BACKGROUND;
			setDefaultBackground(COLOR_BGROWFOCUS);
			drawBar(gc, rect, content, getBackground(), getForeground());
			setDefaultBackground(defaultBg);

		} else {
			rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY, COLOR_LINE_LIGHTGRAY);
			drawBar(gc, rect, content, getBackground(), getForeground());
		}
	}

	/**
	 * @param gc
	 * @param rect
	 * @param m_fraction
	 * @param background
	 */
	protected void drawGradientBar(GC gc, Rectangle rect, float m_fraction, Color background, Color foreground) {
		int barWidth = Math.round(rect.width * m_fraction);
		gc.setForeground(background);
		gc.setBackground(foreground);
		gc.fillGradientRectangle(rect.x, rect.y, barWidth, rect.height, false);
		gc.setBackground(COLOR_BACKGROUND);
		gc.fillRectangle(rect.x + barWidth, rect.y, rect.width - barWidth, rect.height);
	}

	/**
	 * @param gc
	 * @param rect
	 * @param background
	 * @param foreground
	 * @param m_fraction
	 */
	protected void drawNormalBar(GC gc, Rectangle rect, Color background, Color foreground, float m_fraction) {
		int barWidth = Math.round(rect.width * m_fraction);
		gc.setBackground(foreground);
		gc.fillRectangle(rect.x, rect.y, barWidth, rect.height);
		gc.setBackground(background);
		gc.fillRectangle(rect.x + barWidth, rect.y, rect.width - barWidth, rect.height);
	}

	/**
	 * @param gc
	 * @param rect
	 * @param m_fraction
	 * @param background
	 */
	protected void drawBar(GC gc, Rectangle rect, Object content, Color background, Color foreground) {
		float m_fraction;
		if (content instanceof Float)
			m_fraction = ((Float) content).floatValue();
		else if (content instanceof Double)
			m_fraction = ((Double) content).floatValue();
		else if (content instanceof IPercentage)
			m_fraction = ((IPercentage) content).getPercentage();
		else
			m_fraction = 0;

		if (m_fraction > 1)
			m_fraction = 1;
		if (m_fraction < 0)
			m_fraction = 0;

		if ((m_Style & INDICATION_GRADIENT) != 0)
			drawGradientBar(gc, rect, m_fraction, background, foreground);
		else
			drawNormalBar(gc, rect, background, foreground, m_fraction);
	}
}
