/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.lite.headless;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.mwc.debrief.lite.util.ScaledImageIcon;

import junit.framework.TestCase;

/**
 * Ribbon icons must cover the same share of their button whatever the screen
 * zoom: at 200% Windows UI zoom they were drawn half size.
 */
public class ScaledImageIconTest extends TestCase {

	private static BufferedImage solid(final int size) {
		final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = image.createGraphics();
		g.setColor(Color.RED);
		g.fillRect(0, 0, size, size);
		g.dispose();
		return image;
	}

	/** the screen pixels the icon covers, when painted at this zoom */
	private static Rectangle painted(final ScaledImageIcon icon, final double zoom) {
		final int size = (int) Math.ceil(icon.getIconWidth() * zoom) + 20;
		final BufferedImage screen = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = screen.createGraphics();
		g.translate(10, 10);
		g.scale(zoom, zoom);
		icon.paintIcon(null, g, 0, 0);
		g.dispose();

		Rectangle res = null;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				// ignore the faint fringe that interpolation leaves
				if ((screen.getRGB(x, y) >>> 24) > 128) {
					final Rectangle pixel = new Rectangle(x - 10, y - 10, 1, 1);
					res = res == null ? pixel : res.union(pixel);
				}
			}
		}
		return res;
	}

	private static void assertSquare(final String message, final int offset, final int size, final Rectangle actual) {
		assertNotNull(message + ": nothing painted", actual);
		assertEquals(message + " x", offset, actual.x, 1);
		assertEquals(message + " y", offset, actual.y, 1);
		assertEquals(message + " width", size, actual.width, 2);
		assertEquals(message + " height", size, actual.height, 2);
	}

	public void testSmallImageKeepsItsSizeWhenZoomed() {
		// a 24 pixel image in the ribbon's 32 unit icon: centred, at its own size
		final ScaledImageIcon icon = ScaledImageIcon.getIcon(solid(24), new Dimension(32, 32));
		assertEquals(new Dimension(24, 24), icon.getDrawnSize());
		assertSquare("100%", 4, 24, painted(icon, 1));
		assertSquare("150%", 6, 36, painted(icon, 1.5));
		assertSquare("200%", 8, 48, painted(icon, 2));
	}

	public void testLargeImageShrinksToFit() {
		final ScaledImageIcon icon = ScaledImageIcon.getIcon(solid(400), new Dimension(32, 32));
		assertEquals(new Dimension(32, 32), icon.getDrawnSize());
		assertSquare("100%", 0, 32, painted(icon, 1));
		assertSquare("200%", 0, 64, painted(icon, 2));
	}

	public void testFollowsTheIconSize() {
		final ScaledImageIcon icon = ScaledImageIcon.getIcon(solid(24), new Dimension(32, 32));
		icon.setDimension(new Dimension(16, 16));
		assertEquals(16, icon.getIconWidth());
		assertEquals(16, icon.getIconHeight());
		assertSquare("200%", 0, 32, painted(icon, 2));
	}

	public void testNoImageNoIcon() {
		assertNull(ScaledImageIcon.getIcon(null, new Dimension(16, 16)));
	}
}
