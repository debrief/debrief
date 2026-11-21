/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.lite.gui.custom;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Map;

import javax.swing.JLabel;

/**
 * A label whose text can be drawn rotated, centred in the label: used for the
 * titles of the side panels, which read bottom to top. It replaces swingx's
 * {@code JXLabel.setTextRotation}, the only part of JXLabel Lite used. Only the
 * text is drawn when rotated (no icon).
 */
public class RotatedLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	/** the rotation of the text, in radians, clockwise */
	private double textRotation;

	public RotatedLabel(final String text, final int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public double getTextRotation() {
		return textRotation;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		if (textRotation == 0 || getText() == null) {
			super.paintComponent(g);
			return;
		}
		final Graphics2D g2 = (Graphics2D) g.create();
		try {
			if (isOpaque()) {
				g2.setColor(getBackground());
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
			// the same text anti-aliasing as the look and feel uses for labels
			final Object hints = Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
			if (hints instanceof Map) {
				g2.addRenderingHints((Map<?, ?>) hints);
			} else {
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
			final Insets insets = getInsets();
			final double cx = insets.left + (getWidth() - insets.left - insets.right) / 2.0;
			final double cy = insets.top + (getHeight() - insets.top - insets.bottom) / 2.0;
			g2.translate(cx, cy);
			g2.rotate(textRotation);
			g2.setFont(getFont());
			g2.setColor(isEnabled() ? getForeground() : getBackground().darker());
			final FontMetrics fm = g2.getFontMetrics();
			final int x = -fm.stringWidth(getText()) / 2;
			final int y = (fm.getAscent() - fm.getDescent()) / 2;
			g2.drawString(getText(), x, y);
		} finally {
			g2.dispose();
		}
	}

	/**
	 * @param textRotation the rotation of the text, in radians, clockwise (for
	 *                     example 3 * Math.PI / 2 to read bottom to top)
	 */
	public void setTextRotation(final double textRotation) {
		this.textRotation = textRotation;
		repaint();
	}
}
