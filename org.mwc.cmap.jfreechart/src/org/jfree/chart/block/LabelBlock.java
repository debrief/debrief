/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2021, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ---------------
 * LabelBlock.java
 * ---------------
 * (C) Copyright 2004-2021, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Pierre-Marie Le Biot;
 *
 */

package org.jfree.chart.block;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.text.TextBlock;
import org.jfree.chart.text.TextBlockAnchor;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.Size2D;
import org.jfree.chart.util.PaintUtils;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtils;

/**
 * A block containing a label.
 */
public class LabelBlock extends AbstractBlock
        implements Block, PublicCloneable {

    /** For serialization. */
    static final long serialVersionUID = 249626098864178017L;

    /**
     * The text for the label - retained in case the label needs
     * regenerating (for example, to change the font).
     */
    private String text;

    /** The label. */
    private TextBlock label;

    /** The font. */
    private Font font;

    /** The tool tip text (can be {@code null}). */
    private String toolTipText;

    /** The URL text (can be {@code null}). */
    private String urlText;

    /** The default color. */
    public static final Paint DEFAULT_PAINT = Color.BLACK;

    /** The paint. */
    private transient Paint paint;

    /**
     * The content alignment point.
     */
    private TextBlockAnchor contentAlignmentPoint;

    /**
     * The anchor point for the text.
     */
    private RectangleAnchor textAnchor;

    /**
     * Creates a new label block.
     *
     * @param label  the label ({@code null} not permitted).
     */
    public LabelBlock(String label) {
        this(label, new Font("SansSerif", Font.PLAIN, 10), DEFAULT_PAINT);
    }

    /**
     * Creates a new label block.
     *
     * @param text  the text for the label ({@code null} not permitted).
     * @param font  the font ({@code null} not permitted).
     */
    public LabelBlock(String text, Font font) {
        this(text, font, DEFAULT_PAINT);
    }

    /**
     * Creates a new label block.
     *
     * @param text  the text for the label ({@code null} not permitted).
     * @param font  the font ({@code null} not permitted).
     * @param paint the paint ({@code null} not permitted).
     */
    public LabelBlock(String text, Font font, Paint paint) {
        this.text = text;
        this.paint = paint;
        this.label = TextUtils.createTextBlock(text, font, this.paint);
        this.font = font;
        this.toolTipText = null;
        this.urlText = null;
        this.contentAlignmentPoint = TextBlockAnchor.CENTER;
        this.textAnchor = RectangleAnchor.CENTER;
    }

    /**
     * Returns the font.
     *
     * @return The font (never {@code null}).
     *
     * @see #setFont(Font)
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Sets the font and regenerates the label.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getFont()
     */
    public void setFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.font = font;
        this.label = TextUtils.createTextBlock(this.text, font, this.paint);
    }

    /**
     * Returns the paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setPaint(Paint)
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the paint and regenerates the label.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getPaint()
     */
    public void setPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.paint = paint;
        this.label = TextUtils.createTextBlock(this.text, this.font,
                this.paint);
    }

    /**
     * Returns the tool tip text.
     *
     * @return The tool tip text (possibly {@code null}).
     *
     * @see #setToolTipText(String)
     */
    public String getToolTipText() {
        return this.toolTipText;
    }

    /**
     * Sets the tool tip text.
     *
     * @param text  the text ({@code null} permitted).
     *
     * @see #getToolTipText()
     */
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    /**
     * Returns the URL text.
     *
     * @return The URL text (possibly {@code null}).
     *
     * @see #setURLText(String)
     */
    public String getURLText() {
        return this.urlText;
    }

    /**
     * Sets the URL text.
     *
     * @param text  the text ({@code null} permitted).
     *
     * @see #getURLText()
     */
    public void setURLText(String text) {
        this.urlText = text;
    }

    /**
     * Returns the content alignment point.
     *
     * @return The content alignment point (never {@code null}).
     */
    public TextBlockAnchor getContentAlignmentPoint() {
        return this.contentAlignmentPoint;
    }

    /**
     * Sets the content alignment point.
     *
     * @param anchor  the anchor used to determine the alignment point (never
     *         {@code null}).
     */
    public void setContentAlignmentPoint(TextBlockAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.contentAlignmentPoint = anchor;
    }

    /**
     * Returns the text anchor (never {@code null}).
     *
     * @return The text anchor.
     */
    public RectangleAnchor getTextAnchor() {
        return this.textAnchor;
    }

    /**
     * Sets the text anchor.
     *
     * @param anchor  the anchor ({@code null} not permitted).
     */
    public void setTextAnchor(RectangleAnchor anchor) {
        this.textAnchor = anchor;
    }

    /**
     * Arranges the contents of the block, within the given constraints, and
     * returns the block size.
     *
     * @param g2  the graphics device.
     * @param constraint  the constraint ({@code null} not permitted).
     *
     * @return The block size (in Java2D units, never {@code null}).
     */
    @Override
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        g2.setFont(this.font);
        Size2D s = this.label.calculateDimensions(g2);
        return new Size2D(calculateTotalWidth(s.getWidth()),
                calculateTotalHeight(s.getHeight()));
    }

    /**
     * Draws the block.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    /**
     * Draws the block within the specified area.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     * @param params  ignored ({@code null} permitted).
     *
     * @return Always {@code null}.
     */
    @Override
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        area = trimMargin(area);
        drawBorder(g2, area);
        area = trimBorder(area);
        area = trimPadding(area);

        // check if we need to collect chart entities from the container
        EntityBlockParams ebp = null;
        StandardEntityCollection sec = null;
        Shape entityArea = null;
        if (params instanceof EntityBlockParams) {
            ebp = (EntityBlockParams) params;
            if (ebp.getGenerateEntities()) {
                sec = new StandardEntityCollection();
                entityArea = (Shape) area.clone();
            }
        }
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        Point2D pt = this.textAnchor.getAnchorPoint(area);
        this.label.draw(g2, (float) pt.getX(), (float) pt.getY(),
                this.contentAlignmentPoint);
        BlockResult result = null;
        if (ebp != null && sec != null) {
            if (this.toolTipText != null || this.urlText != null) {
                ChartEntity entity = new ChartEntity(entityArea,
                        this.toolTipText, this.urlText);
                sec.add(entity);
                result = new BlockResult();
                result.setEntityCollection(sec);
            }
        }
        return result;
    }

    /**
     * Tests this {@code LabelBlock} for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabelBlock)) {
            return false;
        }
        LabelBlock that = (LabelBlock) obj;
        if (!this.text.equals(that.text)) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;
        }
        if (!PaintUtils.equal(this.paint, that.paint)) {
            return false;
        }
        if (!Objects.equals(this.toolTipText, that.toolTipText)) {
            return false;
        }
        if (!Objects.equals(this.urlText, that.urlText)) {
            return false;
        }
        if (!this.contentAlignmentPoint.equals(that.contentAlignmentPoint)) {
            return false;
        }
        if (!this.textAnchor.equals(that.textAnchor)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a clone of this {@code LabelBlock} instance.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writePaint(this.paint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtils.readPaint(stream);
    }

}
