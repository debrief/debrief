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
package org.mwc.debrief.lite.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.pushingpixels.neon.api.icon.ResizableIcon;

/**
 * A ribbon icon drawn from a bitmap, that keeps its size when the screen is
 * zoomed.
 *
 * Radiance's ImageWrapperResizableIcon treats the bitmap's pixels as screen
 * pixels whenever the bitmap is no bigger than the icon, so at 200% Windows UI
 * zoom a 24 pixel image is drawn half size. Here the bitmap's pixels are user
 * space units: the image is drawn at its own size, shrunk if needed to fit the
 * icon, and the zoom is left to Java2D.
 */
public class ScaledImageIcon implements ResizableIcon {

	public static ScaledImageIcon getIcon(final Image image, final Dimension initialDim) {
		return image == null ? null : new ScaledImageIcon(image, initialDim);
	}

	private final Image image;
	private int width;
	private int height;

	/** the image, reduced to the number of screen pixels last painted */
	private BufferedImage cached;

	public ScaledImageIcon(final Image image, final Dimension initialDim) {
		this.image = image;
		this.width = initialDim.width;
		this.height = initialDim.height;
	}

	/**
	 * the size the image is drawn at, in user space: its own size, or less where
	 * the icon is smaller
	 */
	public Dimension getDrawnSize() {
		final int imageWidth = image.getWidth(null);
		final int imageHeight = image.getHeight(null);
		if (imageWidth <= 0 || imageHeight <= 0) {
			return new Dimension(0, 0);
		}
		final double fit = Math.min(1d,
				Math.min((double) width / imageWidth, (double) height / imageHeight));
		return new Dimension(Math.max(1, (int) Math.round(imageWidth * fit)),
				Math.max(1, (int) Math.round(imageHeight * fit)));
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
		final Dimension drawn = getDrawnSize();
		if (drawn.width == 0) {
			return;
		}
		final Graphics2D g2 = (Graphics2D) g.create();
		try {
			final double zoom = Math.max(Math.abs(g2.getTransform().getScaleX()),
					Math.abs(g2.getTransform().getScaleY()));
			final int pixelWidth = Math.max(1, (int) Math.ceil(drawn.width * zoom));
			final int pixelHeight = Math.max(1, (int) Math.ceil(drawn.height * zoom));
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.drawImage(reduced(pixelWidth, pixelHeight), x + (width - drawn.width) / 2,
					y + (height - drawn.height) / 2, drawn.width, drawn.height, null);
		} finally {
			g2.dispose();
		}
	}

	/**
	 * the image at the given number of pixels, where that is fewer than it has.
	 * Shrinking by more than half in one go drops detail, so it is done in steps.
	 */
	private synchronized Image reduced(final int pixelWidth, final int pixelHeight) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		if (w <= pixelWidth && h <= pixelHeight) {
			return image;
		}
		if (cached != null && cached.getWidth() == pixelWidth && cached.getHeight() == pixelHeight) {
			return cached;
		}
		Image current = image;
		do {
			w = Math.max(pixelWidth, w / 2);
			h = Math.max(pixelHeight, h / 2);
			final BufferedImage next = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D g2 = next.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(current, 0, 0, w, h, null);
			g2.dispose();
			current = next;
		} while (w > pixelWidth || h > pixelHeight);
		cached = (BufferedImage) current;
		return cached;
	}

	@Override
	public void setDimension(final Dimension newDimension) {
		width = newDimension.width;
		height = newDimension.height;
	}
}
