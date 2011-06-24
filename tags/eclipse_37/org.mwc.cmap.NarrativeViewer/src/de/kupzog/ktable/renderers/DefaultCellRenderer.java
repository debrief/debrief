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
package de.kupzog.ktable.renderers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.SWTX;

/**
 * Class that provides additional facilities commonly used when writing custom
 * renderers.
 * 
 * @author Lorenz Maierhofer <lorenz.maierhofer@logicmindguide.com>
 */
public class DefaultCellRenderer implements KTableCellRenderer {

	// default colors:
	public Color COLOR_TEXT = Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	public Color COLOR_BACKGROUND = Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	public static Color COLOR_LINE_LIGHTGRAY = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	public static Color COLOR_LINE_DARKGRAY = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	public static Color COLOR_BGFOCUS = SWTX.getColor(SWTX.COLOR_BGFOCUS);
	public static Color COLOR_COMMENTSIGN = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
	public static Color COLOR_FIXEDHIGHLIGHT = SWTX.getColor(SWTX.COLOR_FIXEDHIGHLIGHT);
	public static Color COLOR_BGROWFOCUS = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION);
	public static Color COLOR_FGROWFOCUS = Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);

	/**
	 * Makes a button-like cell.
	 * <p>
	 * Used in: <br> - FixedCellRenderer
	 */
	public static final int STYLE_PUSH = 0;
	/**
	 * Makes a flat looking cell.
	 * <p>
	 * Used in: <br> - FixedCellRenderer
	 */
	public static final int STYLE_FLAT = 1 << 2;
	/**
	 * Shows a sort indicator in the fixed cell if sorting is active for the
	 * column.
	 * <p>
	 * Has only an effect if the tablemodel is an instanceof KTableSortModel.
	 * <p>
	 * Used in: <br> - FixedCellRenderer
	 */
	public static final int INDICATION_SORT = 1 << 3;

	/**
	 * Color fixed cells with focus in a different color.
	 * <p>
	 * Has only an effect on fixed cells when
	 * <code>KTable.setHighlightSelectionInHeader(true)</code> was set.
	 * <p>
	 * Used in: <br> - CheckableCellRenderer<br> - FixedCellRenderer<br> -
	 * TextCellRenderer<br>
	 */
	public static final int INDICATION_FOCUS = 1 << 4;

	/**
	 * Color fixed cells with focus in a different color.
	 * <p>
	 * Has only an effect when
	 * <code>KTable.setHighlightSelectionInHeader(true)</code> was set.
	 * <p>
	 * Used in: <br> - CheckableCellRenderer<br> - FixedCellRenderer<br> -
	 * TextCellRenderer<br>
	 */
	public static final int INDICATION_FOCUS_ROW = 1 << 5;

	/**
	 * Make the header column show a button-pressed behavior when clicked.
	 * <p>
	 * Used in: <br> - FixedCellRenderer (Has only an effect when STYLE_PUSH is
	 * set.)<br> - CheckableCellRenderer<br>
	 */
	public static final int INDICATION_CLICKED = 1 << 6;

	/**
	 * Draws a little triangle in the upper right corner of the cell as an
	 * indication that additional information is available.
	 * <p>
	 * Used in: <br> - CheckableCellRenderer<br> - TextCellRenderer<br>
	 */
	public static final int INDICATION_COMMENT = 1 << 7;

	/**
	 * Draws a gradient in the figure representing the cell content. Used by:
	 * <br> - BarDiagramCellRenderer<br>
	 */
	public static final int INDICATION_GRADIENT = 1 << 8;

	/**
	 * The display to use when painting.
	 */
	protected final Display m_Display = Display.getCurrent();

	/**
	 * Holds the default instance for this renderer.
	 */
	protected int m_Style = 0;

	protected int m_alignment = SWTX.ALIGN_HORIZONTAL_LEFT | SWTX.ALIGN_VERTICAL_CENTER;

	/**
	 * Holds the currently set bg and fg colors.
	 */
	protected Color m_bgColor = null, m_fgColor = null;
	protected Font m_font = null;
	private Font m_GCfont, m_TMPfont = null;

	// the default cell renderer for fixed cells:
	protected static final FixedCellRenderer m_FixedRenderer = new FixedCellRenderer(STYLE_FLAT);
	protected static final TextCellRenderer m_TextRenderer = new TextCellRenderer(INDICATION_FOCUS);

	/**
	 * The constructor that sets the style bits given. The default cellrenderer
	 * ignores all style bits. See subclasses for their honored style bits.
	 */
	public DefaultCellRenderer(int style) {
		m_Style |= style;
	}

	/**
	 * Overwrites the style bits with the given one.
	 * 
	 * @see getStyle() for accessing the style bits.
	 * @param style
	 *            The styles to AND with the current style bits.
	 */
	public void setStyle(int style) {
		m_Style = style;
	}

	/**
	 * @return Returns the currently set style.
	 */
	public int getStyle() {
		return m_Style;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kupzog.ktable.KTableCellRenderer#getOptimalWidth(org.eclipse.swt.graphics.GC,
	 *      int, int, java.lang.Object, boolean)
	 */
	public int getOptimalWidth(GC gc, int col, int row, Object content, boolean fixed, KTableModel model) {
		applyFont(gc);
		int result = SWTX.getCachedStringExtent(gc, content.toString()).x + 8;
		resetFont(gc);
		return result;
	}

	/**
	 * A default implementation that paints cells in a way that is more or less
	 * Excel-like. Only the cell with focus looks very different.
	 * 
	 * @see de.kupzog.ktable.KTableCellRenderer#drawCell(GC, Rectangle, int,
	 *      int, Object, boolean, boolean, boolean, KTableModel)
	 */
	public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, boolean focus, boolean fixed, boolean clicked, KTableModel model) {
		if (fixed) {
			m_FixedRenderer.drawCell(gc, rect, col, row, content, focus, fixed, clicked, model);
		} else {
			m_TextRenderer.drawCell(gc, rect, col, row, content, focus, fixed, clicked, model);
		}
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
		// clear background and paint content:
		gc.setBackground(backColor);
		gc.setForeground(textColor);
		gc.fillRectangle(rect);
		SWTX.drawTextImage(gc, text, getAlignment(), img, getAlignment(), rect.x + 3, rect.y + 2, rect.width - 6, rect.height - 4);
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
	protected void drawVerticalCellContent(GC gc, Rectangle rect, String text, Image img, Color textColor, Color backColor) {
		if (rect.height <= 0)
			rect.height = 1;
		if (rect.width <= 0)
			rect.width = 1;
		Image vImg = new Image(Display.getCurrent(), rect.height, rect.width);
		GC gcImg = new GC(vImg);
		applyFont(gcImg);
		// clear background and paint content:
		gcImg.setBackground(backColor);
		gc.setBackground(backColor);
		gcImg.setForeground(textColor);
		gc.setForeground(textColor);
		gcImg.fillRectangle(vImg.getBounds());

		int alignment = mirrorAlignment();
		SWTX.drawTextImage(gcImg, text, alignment, img, alignment, 3, 3, rect.height - 6, rect.width - 6);
		gcImg.dispose();
		Image mirrorImg = mirrorImage(vImg);
		gc.drawImage(mirrorImg, rect.x, rect.y);
		vImg.dispose();
		mirrorImg.dispose();
	}

	private int mirrorAlignment() {
		int align = getAlignment();
		int result = 0;
		if ((align & SWTX.ALIGN_HORIZONTAL_MASK) == SWTX.ALIGN_HORIZONTAL_CENTER)
			result = SWTX.ALIGN_VERTICAL_CENTER;
		else if ((align & SWTX.ALIGN_HORIZONTAL_MASK) == SWTX.ALIGN_HORIZONTAL_RIGHT)
			result = SWTX.ALIGN_VERTICAL_TOP;
		else
			result = SWTX.ALIGN_VERTICAL_BOTTOM;

		if ((align & SWTX.ALIGN_VERTICAL_MASK) == SWTX.ALIGN_VERTICAL_CENTER)
			result |= SWTX.ALIGN_HORIZONTAL_CENTER;
		else if ((align & SWTX.ALIGN_VERTICAL_MASK) == SWTX.ALIGN_VERTICAL_TOP)
			result |= SWTX.ALIGN_HORIZONTAL_RIGHT;
		else
			result |= SWTX.ALIGN_HORIZONTAL_LEFT;

		result |= (SWTX.WRAP_MASK & align);

		return result;
	}

	/**
	 * Mirrors the given image. Note that the returned image must be disposed
	 * after rendering!
	 * 
	 * @param source
	 *            The source image. Gets disposed in this method.
	 * @return Returns a new image with mirrored content. The caller is
	 *         responsible for disposing this image!
	 */
	private Image mirrorImage(Image source) {
		Rectangle bounds = source.getBounds();

		ImageData sourceData = source.getImageData();
		ImageData resultData = new ImageData(sourceData.height, sourceData.width, sourceData.depth, sourceData.palette);
		for (int x = 0; x < bounds.width; x++)
			for (int y = 0; y < bounds.height; y++)
				resultData.setPixel(y, resultData.height - x - 1, sourceData.getPixel(x, y));
		source.dispose();
		return new Image(Display.getCurrent(), resultData);
	}

	/**
	 * Draws the default border by invoking the relevant method in
	 * BorderPainter.
	 * <p>
	 * Overwrite this method if you desire another border style.
	 * 
	 * @param gc
	 *            The GC to use.
	 * @param rect
	 *            The cell bounds. Note that this method returns the new cell
	 *            bounds that exlude the border area.
	 * @param vBorderColor
	 *            The vertical border color.
	 * @param hBorderColor
	 *            The horizontal border color.
	 * @return Returns the new bounds of the cell that should be filled with
	 *         content.
	 */
	protected Rectangle drawDefaultSolidCellLine(GC gc, Rectangle rect, Color vBorderColor, Color hBorderColor) {
		return BorderPainter.drawDefaultSolidCellLine(gc, rect, vBorderColor, hBorderColor);
	}

	/**
	 * Paints a sign that a comment is present in the right upper corner!
	 * 
	 * @param gc
	 *            The GC to use when painting.
	 * @param rect
	 *            The cell area where content should be added.
	 */
	protected final void drawCommentSign(GC gc, Rectangle rect) {
		gc.setBackground(COLOR_COMMENTSIGN);
		gc.fillPolygon(new int[] { rect.x + rect.width - 4, rect.y + 1, rect.x + rect.width - 1, rect.y + 1, rect.x + rect.width - 1, rect.y + 4 });
	}

	/**
	 * Sets the alignment of the cell content.
	 * 
	 * @param style
	 *            The OR-ed alignment constants for vertical and horizontal
	 *            alignment as defined in SWTX.
	 * @see SWTX#ALIGN_HORIZONTAL_CENTER
	 * @see SWTX#ALIGN_HORIZONTAL_LEFT
	 * @see SWTX#ALIGN_HORIZONTAL_RIGHT
	 * @see SWTX#ALIGN_VERTICAL_CENTER
	 * @see SWTX#ALIGN_VERTICAL_TOP
	 * @see SWTX#ALIGN_VERTICAL_BOTTOM
	 */
	public void setAlignment(int style) {
		m_alignment = style;
	}

	/**
	 * @return Returns the alignment for the cell content. 2 or-ed constants,
	 *         one for horizontal, one for vertical alignment.
	 * @see SWTX#ALIGN_HORIZONTAL_CENTER
	 * @see SWTX#ALIGN_HORIZONTAL_LEFT
	 * @see SWTX#ALIGN_HORIZONTAL_RIGHT
	 * @see SWTX#ALIGN_VERTICAL_CENTER
	 * @see SWTX#ALIGN_VERTICAL_TOP
	 * @see SWTX#ALIGN_VERTICAL_BOTTOM
	 */
	public int getAlignment() {
		return m_alignment;
	}

	/**
	 * Set the foreground color used to paint text et al.
	 * 
	 * @param fgcolor
	 *            The color or <code>null</code> to reset to default (black).
	 *            Note that also the default color can be set using
	 *            <code>setDefaultForeground(Color)</code>
	 * @see #setDefaultForeground(Color)
	 */
	public void setForeground(Color fgcolor) {
		m_fgColor = fgcolor;
	}

	/**
	 * Changes the default foreground color that will be used when no other
	 * foreground color is set. (for example when
	 * <code>setForeground(null)</code> is called)
	 * 
	 * @param fgcolor
	 *            The foreground color to use.
	 * @see #setForeground(Color)
	 */
	public void setDefaultForeground(Color fgcolor) {
		COLOR_TEXT = fgcolor;
	}

	/**
	 * Set the background color that should be used when painting the cell
	 * background.
	 * <p>
	 * If the <code>null</code> value is given, the default color will be
	 * used. The default color is settable using
	 * <code>setDefaultBacktround(Color)</code>
	 * 
	 * @param bgcolor
	 *            The color or <code>null</code> to reset to default.
	 * @see #setDefaultBackground(Color)
	 */
	public void setBackground(Color bgcolor) {
		m_bgColor = bgcolor;
	}

	/**
	 * Changes the default background color that will be used when no background
	 * color is set via setBackground().
	 * 
	 * @param bgcolor
	 *            The color for the background.
	 * @see #setBackground(Color)
	 */
	public void setDefaultBackground(Color bgcolor) {
		COLOR_BACKGROUND = bgcolor;
	}

	/**
	 * @return returns the currently set foreground color. If none was set, the
	 *         default value is returned.
	 */
	public Color getForeground() {
		if (m_fgColor != null)
			return m_fgColor;
		return COLOR_TEXT;
	}

	/**
	 * @return returns the currently set background color. If none was set, the
	 *         default value is returned.
	 */
	public Color getBackground() {
		if (m_bgColor != null)
			return m_bgColor;
		return COLOR_BACKGROUND;
	}

	/**
	 * Sets the font the renderer will use for drawing its content.
	 * 
	 * @param font
	 *            The font to use. Be aware that you must dispose fonts you have
	 *            created.
	 */
	public void setFont(Font font) {
		m_font = font;
	}

	/**
	 * @return Returns the font the renderer will use to draw the content.
	 */
	public Font getFont() {
		return m_font;
	}

	/**
	 * Applies the font style of the renderer to the gc that will draw the
	 * content.
	 * <p>
	 * <b>To be called by implementors</b>
	 * 
	 * @param gc
	 *            The gc that will draw the renderers content.
	 */
	protected void applyFont(GC gc) {
		m_GCfont = gc.getFont();
		if (m_font == null)
			m_font = Display.getCurrent().getSystemFont();
		if ((m_Style & SWT.BOLD) != 0 || (m_Style & SWT.ITALIC) != 0) {
			FontData[] fd = m_font.getFontData();
			int style = SWT.NONE;
			if ((m_Style & SWT.BOLD) != 0)
				style |= SWT.BOLD;
			if ((m_Style & SWT.ITALIC) != 0)
				style |= SWT.ITALIC;

			for (int i = 0; i < fd.length; i++)
				fd[i].setStyle(style);
			m_TMPfont = new Font(Display.getCurrent(), fd);
			gc.setFont(m_TMPfont);
		} else
			gc.setFont(m_font);
	}

	/**
	 * Resets the given GC's font parameters to the original state.
	 * 
	 * @param gc
	 *            The gc to draw with.
	 */
	protected void resetFont(GC gc) {
		if (m_TMPfont != null) {
			m_TMPfont.dispose();
			m_TMPfont = null;
		}

		gc.setFont(m_GCfont);
	}
}
