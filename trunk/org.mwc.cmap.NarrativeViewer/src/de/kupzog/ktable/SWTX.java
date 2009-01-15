/*
 * Copyright (C) 2004 by Friederich Kupzog Elektronik & Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html

 Authors: 
 Friederich Kupzog,  fkmk@kupzog.de, www.kupzog.de/fkmk
 Lorenz Maierhofer
 */

package de.kupzog.ktable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * Provides some extensions for the KTable SWT custom table.
 * 
 * @author Friedrich Ku..
 */
public class SWTX {

	// STYLE BITS for KTABLE:
	/**
	 * 
	 */
	public static final int AUTO_SCROLL = 1 << 10 | SWT.V_SCROLL | SWT.H_SCROLL;
	/**
	 * Style bit for KTable that ensures the table always covers the whole
	 * space.<br>
	 * Uses the policy of widening the last column.
	 */
	public static final int FILL_WITH_LASTCOL = 1 << 17;
	/**
	 * Style bit for KTable that ensures the table always covers the whole
	 * space. <br>
	 * Uses the policy to add a dummy column at the end that occupies unused
	 * space.
	 */
	public static final int FILL_WITH_DUMMYCOL = 1 << 18;

	/**
	 * Style bit that makes KTable activate the celleditor whenever a key is
	 * pressed. (in contrast to the default behavior that only opens the editor
	 * on ENTER).
	 */
	public static final int EDIT_ON_KEY = 1 << 30;

	/**
	 * Style bit that makes KTable draw left and top header cells in a different
	 * style when the focused cell is in their row/column. This mimics the MS
	 * Excel behavior that helps find the currently selected cell(s).
	 */
	public static final int MARK_FOCUS_HEADERS = 1 << 31;

	public static final String COLOR_BGFOCUS = "bgfocus";
	public static final String COLOR_FIXEDHIGHLIGHT = "fixedhighlight";

	private static final ColorRegistry m_colorFactory = new ColorRegistry();
	static {
		m_colorFactory.put(COLOR_BGFOCUS, new RGB(223, 227, 237));// 202, 209,
		// 230));
		m_colorFactory.put(COLOR_FIXEDHIGHLIGHT, new RGB(182, 189, 210));
	}

	/**
	 * Returns a custom color.
	 * 
	 * @param colorKey
	 *            The key defined in SWTX.
	 * @return Returns the appropriate color.
	 */
	public static Color getColor(String colorKey) {
		return m_colorFactory.get(colorKey);
	}

	public static final int EVENT_SWTX_BASE = 1000;
	public static final int EVENT_TABLE_HEADER = EVENT_SWTX_BASE + 1;
	public static final int EVENT_TABLE_HEADER_CLICK = EVENT_SWTX_BASE + 2;
	public static final int EVENT_TABLE_HEADER_RESIZE = EVENT_SWTX_BASE + 3;
	//
	public static final int ALIGN_HORIZONTAL_MASK = 0x00F;
	public static final int ALIGN_HORIZONTAL_NONE = 0x000;
	public static final int ALIGN_HORIZONTAL_LEFT = 0x001;
	public static final int ALIGN_HORIZONTAL_LEFT_LEFT = ALIGN_HORIZONTAL_LEFT;
	public static final int ALIGN_HORIZONTAL_LEFT_RIGHT = 0x002;
	public static final int ALIGN_HORIZONTAL_LEFT_CENTER = 0x003;
	public static final int ALIGN_HORIZONTAL_RIGHT = 0x004;
	public static final int ALIGN_HORIZONTAL_RIGHT_RIGHT = ALIGN_HORIZONTAL_RIGHT;
	public static final int ALIGN_HORIZONTAL_RIGHT_LEFT = 0x005;
	public static final int ALIGN_HORIZONTAL_RIGHT_CENTER = 0x006;
	public static final int ALIGN_HORIZONTAL_CENTER = 0x007;

	public static final int ALIGN_VERTICAL_MASK = 0x0F0;
	public static final int ALIGN_VERTICAL_TOP = 0x010;
	public static final int ALIGN_VERTICAL_BOTTOM = 0x020;
	public static final int ALIGN_VERTICAL_CENTER = 0x030;

	public static final int WRAP_MASK = 0xF00;
	public static final int WRAP = 0x900;

	//
	private static GC m_LastGCFromExtend;
	private static Map<String, Point> m_StringExtentCache = new HashMap<String, Point>();

	public static synchronized Point getCachedStringExtent(GC gc, String text) {
		if (m_LastGCFromExtend != gc) {
			m_StringExtentCache.clear();
			m_LastGCFromExtend = gc;
		}
		Point p = (Point) m_StringExtentCache.get(text);
		if (p == null) {
			if (text == null)
				return new Point(0, 0);
			p = gc.textExtent(text);
			m_StringExtentCache.put(text, p);
		}
		return new Point(p.x, p.y);
	}

	public static int drawTextVerticalAlign(GC gc, String text, int textAlign, int x, int y, int w, int h) {
		if (text == null)
			text = "";

		if ((textAlign & WRAP_MASK) == WRAP) {
			text = wrapText(gc, text, w);
			text = cropWrappedTextForHeight(gc, text, h);
		}

		Rectangle oldClip = gc.getClipping();
		Rectangle newClip = new Rectangle(x, y, w, h);
		newClip.intersect(oldClip);
		gc.setClipping(newClip);

		Point textSize = getCachedStringExtent(gc, text);
		{
			boolean addPoint = false;

			if ((textAlign & WRAP_MASK) != WRAP) {
				int cutoffLength = w / gc.getFontMetrics().getAverageCharWidth();
				text = text.substring(0, Math.max(0, Math.min(cutoffLength, text.length())));
			}

			while ((text.length() > 0) && (textSize.x >= w)) {
				text = text.substring(0, Math.max(text.length() - 1, 0));
				textSize = getCachedStringExtent(gc, text + "...");
				addPoint = true;
			}
			if (addPoint)
				text = text + "...";
			textSize = getCachedStringExtent(gc, text);
			if (textSize.x >= w) {
				text = "";
				textSize = getCachedStringExtent(gc, text);
			}
		}
		//
		if ((textAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_TOP) {
			gc.drawText(text, x, y);
			// gc.fillRectangle(x, y + textSize.y, textSize.x, h - textSize.y);
		} else if ((textAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_BOTTOM) {
			gc.drawText(text, x, y + h - textSize.y);
			// gc.fillRectangle(x, y, textSize.x, h - textSize.y);
		} else if ((textAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_CENTER) {
			int yOffset = (h - textSize.y) / 2;
			gc.drawText(text, x, y + yOffset);
			// gc.fillRectangle(x, y, textSize.x, yOffset);
			// gc.fillRectangle(x, y + yOffset + textSize.y, textSize.x, h -
			// (yOffset + textSize.y));
		} else
			throw new SWTException("Unknown alignment for text: " + (textAlign & ALIGN_VERTICAL_MASK));

		gc.setClipping(oldClip);
		return textSize.x;
	}

	// TODO: What was the intention behind painting image doublebuffered?
	public static void drawTransparentImage(GC gc, Image image, int x, int y) {
		if (image == null)
			return;
		// Point imageSize = new Point(image.getBounds().width,
		// image.getBounds().height);
		// Image img = new Image(Display.getCurrent(), imageSize.x,
		// imageSize.y);
		// GC gc2 = new GC(img);
		// gc2.setBackground(gc.getBackground());
		// gc2.fillRectangle(0, 0, imageSize.x, imageSize.y);
		// gc2.drawImage(image, 0, 0);
		gc.drawImage(image, x, y); // img if doublebufferd
		// gc2.dispose();
		// img.dispose();
	}

	public static void drawImageVerticalAlign(GC gc, Image image, int imageAlign, int x, int y, int h) {
		if (image == null)
			return;
		Point imageSize = new Point(image.getBounds().width, image.getBounds().height);
		//
		if ((imageAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_TOP) {
			drawTransparentImage(gc, image, x, y);
			// gc.fillRectangle(x, y + imageSize.y, imageSize.x, h -
			// imageSize.y);
			return;
		}
		if ((imageAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_BOTTOM) {
			drawTransparentImage(gc, image, x, y + h - imageSize.y);
			// gc.fillRectangle(x, y, imageSize.x, h - imageSize.y);
			return;
		}
		if ((imageAlign & ALIGN_VERTICAL_MASK) == ALIGN_VERTICAL_CENTER) {
			int yOffset = (h - imageSize.y) / 2;
			drawTransparentImage(gc, image, x, y + yOffset);
			// gc.fillRectangle(x, y, imageSize.x, yOffset);
			// gc.fillRectangle(x, y + yOffset + imageSize.y, imageSize.x, h -
			// (yOffset + imageSize.y));
			return;
		}
		throw new SWTException("Unknown alignment for image: " + (imageAlign & ALIGN_VERTICAL_MASK));
	}

	public static void drawTextImage(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h) {
		Point textSize = getCachedStringExtent(gc, text);
		Point imageSize;
		if (image != null)
			imageSize = new Point(image.getBounds().width, image.getBounds().height);
		else
			imageSize = new Point(0, 0);

		/*
		 * Rectangle oldClipping = gc.getClipping(); gc.setClipping(x, y, w, h);
		 */
		try {
			if ((image == null) && ((textAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_CENTER)) {
				Point p = getCachedStringExtent(gc, text);
				int offset = (w - p.x) / 2;
				if (offset > 0) {
					drawTextVerticalAlign(gc, text, textAlign, x + offset, y, w, h);
					// gc.fillRectangle(x, y, offset, h);
					// gc.fillRectangle(x + offset + p.x, y, w - (offset + p.x),
					// h);
				} else {
					p.x = drawTextVerticalAlign(gc, text, textAlign, x, y, w, h);
					// gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
					// gc.fillRectangle(x+p.x, y, w-(x+p.x)+1, h);
					// offset = (w - p.x) / 2;
					// gc.fillRectangle(x, y, offset, h);
					// gc.fillRectangle(x + offset + p.x, y, w - (offset + p.x),
					// h);
				}
				return;
			}
			if (((text == null) || (text.length() == 0)) && ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_CENTER)) {
				int offset = (w - imageSize.x) / 2;
				// System.out.println("w: " + w + " imageSize" + imageSize + "
				// offset: " + offset);
				drawImageVerticalAlign(gc, image, imageAlign, x + offset, y, h);
				// gc.fillRectangle(x, y, offset, h);
				// gc.fillRectangle(x + offset + imageSize.x, y, w - (offset +
				// imageSize.x), h);
				return;
			}
			if ((textAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT) {
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_NONE) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, y, w, h);
					// gc.fillRectangle(x + textSize.x, y, w - textSize.x, h);
					return;
				}
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x + imageSize.x, y, w - imageSize.x, h);
					drawImageVerticalAlign(gc, image, imageAlign, x, y, h);
					// gc.fillRectangle(x + textSize.x + imageSize.x, y, w -
					// (textSize.x + imageSize.x), h);
					return;
				}
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, y, w - imageSize.x, h);
					drawImageVerticalAlign(gc, image, imageAlign, x + w - imageSize.x, y, h);
					// gc.fillRectangle(x + textSize.x, y, w - (textSize.x +
					// imageSize.x), h);
					return;
				}
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT_LEFT) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, y, w - imageSize.x, h);
					drawImageVerticalAlign(gc, image, imageAlign, x + textSize.x, y, h);
					// gc.fillRectangle(x + textSize.x + imageSize.x, y, w -
					// (textSize.x + imageSize.x), h);
					return;
				}
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT_CENTER) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, y, w - imageSize.x, h);
					int xOffset = (w - textSize.x - imageSize.x) / 2;
					drawImageVerticalAlign(gc, image, imageAlign, x + textSize.x + xOffset, y, h);
					// gc.fillRectangle(x + textSize.x, y, xOffset, h);
					// gc.fillRectangle(
					// x + textSize.x + xOffset + imageSize.x,
					// y,
					// w - (textSize.x + xOffset + imageSize.x),
					// h);
					return;
				}
				throw new SWTException("Unknown alignment for text: " + (imageAlign & ALIGN_HORIZONTAL_MASK));
			} // text align left
			if ((textAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT) {
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_NONE) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, -1000, w, h);
					drawTextVerticalAlign(gc, text, textAlign, x + w - textSize.x, y, w, h);
					// gc.fillRectangle(x, y, w - textSize.x, h);
					return;
				}
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, -1000, w - imageSize.x, h);
					drawTextVerticalAlign(gc, text, textAlign, x + w - textSize.x, y, w - imageSize.x, h);
					drawImageVerticalAlign(gc, image, imageAlign, x, y, h);
					// gc.fillRectangle(x + imageSize.x, y, w - (textSize.x +
					// imageSize.x), h);
					return;
				}
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT_RIGHT) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, -1000, w - imageSize.x, h);
					drawTextVerticalAlign(gc, text, textAlign, x + w - textSize.x, y, w - imageSize.x, h);
					drawImageVerticalAlign(gc, image, imageAlign, x + w - (textSize.x + imageSize.x), y, h);
					// gc.fillRectangle(x, y, w - (textSize.x + imageSize.x),
					// h);
					return;
				}
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_LEFT_CENTER) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, -1000, w - imageSize.x, h);
					drawTextVerticalAlign(gc, text, textAlign, x + w - textSize.x, y, w - imageSize.x, h);
					int xOffset = (w - textSize.x - imageSize.x) / 2;
					drawImageVerticalAlign(gc, image, imageAlign, x + xOffset, y, h);
					// // gc.fillRectangle(x, y, xOffset, h);
					// // gc.fillRectangle(x + xOffset + imageSize.x, y, w -
					// (xOffset + imageSize.x + textSize.x), h);
					return;
				}
				if ((imageAlign & ALIGN_HORIZONTAL_MASK) == ALIGN_HORIZONTAL_RIGHT) {
					textSize.x = drawTextVerticalAlign(gc, text, textAlign, x, -1000, w - imageSize.x, h);
					drawTextVerticalAlign(gc, text, textAlign, x + w - (textSize.x + imageSize.x), y, w - imageSize.x, h);
					drawImageVerticalAlign(gc, image, imageAlign, x + w - imageSize.x, y, h);
					// // gc.fillRectangle(x, y, w - (textSize.x + imageSize.x),
					// h);
					return;
				}
				throw new SWTException("Unknown alignment for text: " + (imageAlign & ALIGN_HORIZONTAL_MASK));
			} // text align right
			throw new SWTException("Unknown alignment for text: " + (textAlign & ALIGN_HORIZONTAL_MASK));
		} // trye
		finally {
			// gc.setClipping(oldClipping);
		}
	}

	public static void drawTextImage(GC gc, String text, int textAlign, Image image, int imageAlign, Rectangle r) {
		drawTextImage(gc, text, textAlign, image, imageAlign, r.x, r.y, r.width, r.height);
	}

	public static String cropWrappedTextForHeight(GC gc, String text, int height) {
		String[] lines = text.split("\n");
		int linesToTake = height / gc.getFontMetrics().getHeight();
		if (linesToTake < 1)
			linesToTake = 1;
		if (lines.length <= linesToTake)
			return text;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < linesToTake; i++) {
			buffer.append(lines[i]);
			buffer.append('\n');
		}
		return buffer.substring(0, Math.max(buffer.length() - 1, 0));
	}

	public static String wrapText(GC gc, String text, int width) {
		Point textSize = getCachedStringExtent(gc, text);
		if (textSize.x > width) {
			StringBuffer wrappedText = new StringBuffer();
			String[] lines = text.split("\n");
			int cutoffLength = width / gc.getFontMetrics().getAverageCharWidth();
			if (cutoffLength < 3)
				return text;
			for (int i = 0; i < lines.length; i++) {
				int breakOffset = 0;
				while (breakOffset < lines[i].length()) {
					String lPart = lines[i].substring(breakOffset, Math.min(breakOffset + cutoffLength, lines[i].length()));
					Point lineSize = getCachedStringExtent(gc, lPart);
					while ((lPart.length() > 0) && (lineSize.x >= width)) {
						lPart = lPart.substring(0, Math.max(lPart.length() - 1, 0));
						lineSize = getCachedStringExtent(gc, lPart);
					}
					wrappedText.append(lPart);
					breakOffset += lPart.length();
					wrappedText.append('\n');
				}
			}
			return wrappedText.substring(0, Math.max(wrappedText.length() - 1, 0));
		} else
			return text;

	}

	public static void drawButtonUp(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h, Color face, Color shadowHigh, Color shadowNormal, Color shadowDark,
			int leftMargin, int topMargin) {
		Color prevForeground = gc.getForeground();
		Color prevBackground = gc.getBackground();
		Rectangle clip = gc.getClipping();
		clip.height++;
		clip.width++;
		gc.setClipping(clip);

		try {
			gc.setBackground(face);
			gc.setForeground(shadowHigh);
			gc.drawLine(x, y, x, y + h - 1);
			gc.drawLine(x, y, x + w - 2, y);
			gc.setForeground(shadowDark);
			gc.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
			gc.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
			gc.setForeground(shadowNormal);
			gc.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 2);
			gc.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);

			gc.fillRectangle(x + 1, y + 1, w - 3, h - 3);
			gc.setForeground(prevForeground);
			drawTextImage(gc, text, textAlign, image, imageAlign, x + 1 + leftMargin, y + 1 + topMargin, w - 3 - leftMargin, h - 3 - topMargin);
		} finally {
			gc.setForeground(prevForeground);
			gc.setBackground(prevBackground);
		}
	}

	public static void drawButtonUp(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h, Color face) {
		Display display = Display.getCurrent();
		drawButtonUp(gc, text, textAlign, image, imageAlign, x, y, w, h, face, display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW), display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
				display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW), 2, 2);
	}

	public static void drawButtonUp(GC gc, String text, int textAlign, Image image, int imageAlign, Rectangle r, int leftMargin, int topMargin) {
		Display display = Display.getCurrent();
		drawButtonUp(gc, text, textAlign, image, imageAlign, r.x, r.y, r.width, r.height, display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND), display
				.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW), display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW), display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW), leftMargin, topMargin);
	}

	public static void drawButtonUp(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h) {
		Display display = Display.getCurrent();
		drawButtonUp(gc, text, textAlign, image, imageAlign, x, y, w, h, display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND), display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW), display
				.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW), display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW), 2, 2);
	}

	public static void drawButtonUp(GC gc, String text, int textAlign, Image image, int imageAlign, Rectangle r) {
		drawButtonUp(gc, text, textAlign, image, imageAlign, r.x, r.y, r.width, r.height);
	}

	public static void drawButtonDown(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h, Color face, Color shadowNormal, int leftMargin, int topMargin) {
		Color prevForeground = gc.getForeground();
		Color prevBackground = gc.getBackground();
		try {
			gc.setBackground(face);
			gc.setForeground(shadowNormal);
			Rectangle clip = gc.getClipping();
			clip.height++;
			clip.width++;
			gc.setClipping(clip);
			gc.drawRectangle(x, y, w - 1, h - 1);
			gc.fillRectangle(x + 1, y + 1, w - 2, h - 2);
			gc.setForeground(prevForeground);
			drawTextImage(gc, text, textAlign, image, imageAlign, x + 2 + leftMargin, y + 2 + topMargin, w - 3 - leftMargin, h - 3 - topMargin);
		} finally {
			gc.setForeground(prevForeground);
			gc.setBackground(prevBackground);
		}
	}

	public static void drawButtonDown(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h) {
		Display display = Display.getCurrent();
		drawButtonDown(gc, text, textAlign, image, imageAlign, x, y, w, h, display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND), display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW), 2, 2);
	}

	public static void drawButtonDown(GC gc, String text, int textAlign, Image image, int imageAlign, Rectangle r) {
		drawButtonDown(gc, text, textAlign, image, imageAlign, r.x, r.y, r.width, r.height);
	}

	public static void drawButtonDown(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h, Color face) {
		Display display = Display.getCurrent();
		drawButtonDown(gc, text, textAlign, image, imageAlign, x, y, w, h, face, display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW), 2, 2);
	}

	public static void drawButtonDeepDown(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h) {
		Display display = Display.getCurrent();
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.drawLine(x, y, x + w - 2, y);
		gc.drawLine(x, y, x, y + h - 2);
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
		gc.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
		gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		gc.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
		gc.drawLine(x + w - 2, y + h - 2, x + w - 2, y + 1);

		gc.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		gc.fillRectangle(x + 2, y + 2, w - 4, 1);
		gc.fillRectangle(x + 1, y + 2, 2, h - 4);

		gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		drawTextImage(gc, text, textAlign, image, imageAlign, x + 2 + 1, y + 2 + 1, w - 4, h - 3 - 1);
	}

	public static void drawButtonDeepDown(GC gc, String text, int textAlign, Image image, int imageAlign, Rectangle r) {
		drawButtonDeepDown(gc, text, textAlign, image, imageAlign, r.x, r.y, r.width, r.height);
	}

	public static void drawFlatButtonUp(GC gc, String text, int textAlign, Image image, int imageAlign, int x, int y, int w, int h, Color face, Color shadowLight, Color shadowNormal, int leftMargin,
			int topMargin) {
		Color prevForeground = gc.getForeground();
		Color prevBackground = gc.getBackground();
		try {
			gc.setForeground(shadowLight);
			gc.drawLine(x, y, x + w - 1, y);
			gc.drawLine(x, y, x, y + h);
			gc.setForeground(shadowNormal);
			gc.drawLine(x + w, y, x + w, y + h);
			gc.drawLine(x + 1, y + h, x + w, y + h);
			//
			gc.setBackground(face);
			gc.fillRectangle(x + 1, y + 1, leftMargin, h - 1);
			gc.fillRectangle(x + 1, y + 1, w - 1, topMargin);
			//
			gc.setBackground(face);
			gc.setForeground(prevForeground);
			drawTextImage(gc, text, textAlign, image, imageAlign, x + 1 + leftMargin, y + 1 + topMargin, w - 1 - leftMargin, h - 1 - topMargin);
		} finally {
			gc.setForeground(prevForeground);
			gc.setBackground(prevBackground);
		}
	}

	/**
	 * 
	 * @param gc
	 * @param image
	 * @param x
	 * @param y
	 * @param alpha
	 */
	public static void drawShadowImage(GC gc, Image image, int x, int y, int alpha) {
		Display display = Display.getCurrent();
		Point imageSize = new Point(image.getBounds().width, image.getBounds().height);
		//
		ImageData imgData = new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(255, 255, 255));
		imgData.alpha = alpha;
		Image img = new Image(display, imgData);
		GC imgGC = new GC(img);
		imgGC.drawImage(image, 0, 0);
		gc.drawImage(img, x, y);
		imgGC.dispose();
		img.dispose();
	}

	/**
	 * Loads an image from the root of this package.
	 * 
	 * @param d
	 *            The display to use when creating the image.
	 * @param name
	 *            The string name inclusive path to the image.
	 * @return returns the image or null.
	 */
	public static Image loadImageResource(Display d, String name) {
		try {
			Image ret = null;
			InputStream is = SWTX.class.getResourceAsStream(name);
			if (is != null) {
				ret = new Image(d, is);
				is.close();
			}
			return ret;
		} catch (Exception e1) {
			return null;
		}
	}
}