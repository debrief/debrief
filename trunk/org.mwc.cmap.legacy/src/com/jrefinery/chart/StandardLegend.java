/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * -------------------
 * StandardLegend.java
 * -------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *
 * $Id: StandardLegend.java,v 1.1.1.1 2003/07/17 10:06:27 Ian.Mayo Exp $
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend placement;
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved some methods [getSeriesPaint(...) etc.] from JFreeChart to Plot (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 06-Feb-2002 : Bug fix for legends in small areas (DG);
 * 23-Apr-2002 : Legend item labels are now obtained from the plot, not the chart (DG);
 * 20-Jun-2002 : Added outline paint and stroke attributes for the key boxes (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Sep-2002 : Changed the name of LegendItem --> DrawableLegendItem (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Adjusted vertical text position in legend item (DG);
 * 17-Oct-2002 : Fixed bug where legend items are not using the font that has been set (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.event.LegendChangeEvent;

/**
 * A chart legend shows the names and visual representations of the series
 * that are plotted in a chart.
 *
 * @author DG
 */
public class StandardLegend extends Legend {

    /** Default font. */
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The pen/brush used to draw the outline of the legend. */
    private Stroke outlineStroke;

    /** The color used to draw the outline of the legend. */
    private Paint outlinePaint;

    /** The color used to draw the background of the legend. */
    private Paint backgroundPaint;

    /** The blank space inside the legend box. */
    private Spacer innerGap;

    /** The font used to display the legend item names. */
    private Font itemFont;

    /** The color used to display the legend item names. */
    private Paint itemPaint;

    /** The stroke used to outline key boxes. */
    private Stroke keyBoxOutlineStroke = new BasicStroke(0.5f);

    /** The paint used to outline key boxes. */
    private Paint keyBoxOutlinePaint = Color.lightGray;

    /**
     * Constructs a new legend with default settings.
     *
     * @param chart  the chart that the legend belongs to.
     */
    public StandardLegend(JFreeChart chart) {

        this(chart,
             3,
             new Spacer(Spacer.ABSOLUTE, 2, 2, 2, 2),
             Color.white, new BasicStroke(), Color.gray,
             DEFAULT_FONT, Color.black);

    }

    /**
     * Constructs a new legend.
     *
     * @param chart  the chart that the legend belongs to.
     * @param outerGap  the gap around the outside of the legend.
     * @param innerGap  the gap inside the legend.
     * @param backgroundPaint  the background color.
     * @param outlineStroke  the pen/brush used to draw the outline.
     * @param outlinePaint  the color used to draw the outline.
     * @param itemFont  the font used to draw the legend items.
     * @param itemPaint  the color used to draw the legend items.
     */
    public StandardLegend(JFreeChart chart,
                          int outerGap, Spacer innerGap,
                          Paint backgroundPaint,
                          Stroke outlineStroke, Paint outlinePaint,
                          Font itemFont, Paint itemPaint) {

        super(chart, outerGap);
        this.innerGap = innerGap;
        this.backgroundPaint = backgroundPaint;
        this.outlineStroke = outlineStroke;
        this.outlinePaint = outlinePaint;
        this.itemFont = itemFont;
        this.itemPaint = itemPaint;

    }

    /**
     * Returns the background color for the legend.
     *
     * @return the background color.
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background color of the legend.
     * <P>
     * Registered listeners are notified that the legend has changed.
     *
     * @param paint  the new background color.
     */
    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the outline pen/brush.
     *
     * @return the outline pen/brush.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline pen/brush.
     * <P>
     * Registered listeners are notified that the legend has changed.
     *
     * @param stroke  the new outline pen/brush.
     */
    public void setOutlineStroke(Stroke stroke) {
        this.outlineStroke = stroke;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the outline color.
     *
     * @return the outline color.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline color.
     * <P>
     * Registered listeners are notified that the legend has changed.
     *
     * @param paint  the new outline color.
     */
    public void setOutlinePaint(Paint paint) {
        this.outlinePaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the series label font.
     *
     * @return the series label font.
     */
    public Font getItemFont() {
        return this.itemFont;
    }

    /**
     * Sets the series label font.
     * <P>
     * Registered listeners are notified that the legend has changed.
     *
     * @param font  the new series label font.
     */
    public void setItemFont(Font font) {
        this.itemFont = font;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the series label color.
     *
     * @return the series label color.
     */
    public Paint getItemPaint() {
        return this.itemPaint;
    }

    /**
     * Sets the series label color.
     * <P>
     * Registered listeners are notified that the legend has changed.
     *
     * @param paint  the new series label color.
     */
    public void setItemPaint(Paint paint) {
        this.itemPaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the stroke used to outline key boxes.
     *
     * @return the stroke.
     */
    public Stroke getKeyBoxOutlineStroke() {
        return this.keyBoxOutlineStroke;
    }

    /**
     * Sets the stroke used to outline key boxes.
     * <P>
     * Registered listeners are notified of the change.
     *
     * @param stroke  the stroke.
     */
    public void setKeyBoxOutlineStroke(Stroke stroke) {
        this.keyBoxOutlineStroke = stroke;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the paint used to outline key boxes.
     *
     * @return the paint.
     */
    public Paint getKeyBoxOutlinePaint() {
        return this.keyBoxOutlinePaint;
    }

    /**
     * Sets the paint used to outline key boxes.
     * <P>
     * Registered listeners are notified of the change.
     *
     * @param paint  the paint.
     */
    public void setKeyBoxOutlinePaint(Paint paint) {
        this.keyBoxOutlinePaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Draws the legend on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param available  the area within which the legend, and afterwards the plot, should be
     *                   drawn.
     *
     * @return the area used by the legend.
     */
    public Rectangle2D draw(Graphics2D g2, Rectangle2D available) {

        return draw(g2, available, (getAnchor() & HORIZONTAL) != 0, (getAnchor() & INVERTED) != 0);

    }

    /**
     * Draws the legend.
     *
     * @param g2  the graphics device.
     * @param available  the area available for drawing the chart.
     * @param horizontal  a flag indicating whether the legend items are laid out horizontally.
     * @param inverted ???
     *
     * @return the remaining available drawing area.
     */
    protected Rectangle2D draw(Graphics2D g2, Rectangle2D available,
                               boolean horizontal, boolean inverted) {

        LegendItemCollection legendItems = getChart().getPlot().getLegendItems();

        if ((legendItems != null) && (legendItems.getItemCount() > 0)) {

            Rectangle2D legendArea = new Rectangle2D.Double();

            // the translation point for the origin of the drawing system
            Point2D translation = new Point2D.Double();

            // Create buffer for individual rectangles within the legend
            DrawableLegendItem[] items = new DrawableLegendItem[legendItems.getItemCount()];
            g2.setFont(itemFont);

            // Compute individual rectangles in the legend, translation point as well
            // as the bounding box for the legend.
            if (horizontal) {
                double xstart = available.getX() + getOuterGap();
                double xlimit = available.getX() + available.getWidth() - 2 * getOuterGap() - 1;
                double maxRowWidth = 0;
                double xoffset = 0;
                double rowHeight = 0;
                double totalHeight = 0;
                boolean startingNewRow = true;

                for (int i = 0; i < legendItems.getItemCount(); i++) {
                    items[i] = createDrawableLegendItem(g2, legendItems.get(i),
                                                        xoffset, totalHeight);
                    if ((!startingNewRow)
                        && (items[i].getX() + items[i].getWidth() + xstart > xlimit)) {

                        maxRowWidth = Math.max(maxRowWidth, xoffset);
                        xoffset = 0;
                        totalHeight += rowHeight;
                        i--;
                        startingNewRow = true;

                    }
                    else {
                        rowHeight = Math.max(rowHeight, items[i].getHeight());
                        xoffset += items[i].getWidth();
                        startingNewRow = false;
                    }
                }

                maxRowWidth = Math.max(maxRowWidth, xoffset);
                totalHeight += rowHeight;

                // Create the bounding box
                legendArea = new Rectangle2D.Double(0, 0, maxRowWidth, totalHeight);

                // The yloc point is the variable part of the translation point
                // for horizontal legends. xloc is constant.
                double yloc = (inverted)
                    ? available.getY() + available.getHeight() - totalHeight - getOuterGap()
                    : available.getY() + getOuterGap();
                double xloc = available.getX() + available.getWidth() / 2 - maxRowWidth / 2;

                // Create the translation point
                translation = new Point2D.Double(xloc, yloc);
            }
            else {  // vertical...
                double totalHeight = 0;
                double maxWidth = 0;
                g2.setFont(itemFont);
                for (int i = 0; i < items.length; i++) {
                    items[i] = createDrawableLegendItem(g2, legendItems.get(i),
                                                        0, totalHeight);
                    totalHeight += items[i].getHeight();
                    maxWidth = Math.max(maxWidth, items[i].getWidth());
                }

                // Create the bounding box
                legendArea = new Rectangle2D.Float(0, 0, (float) maxWidth, (float) totalHeight);

                // The xloc point is the variable part of the translation point
                // for vertical legends. yloc is constant.
                double xloc = (inverted)
                    ? available.getMaxX() - maxWidth - getOuterGap()
                    : available.getX() + getOuterGap();
                double yloc = available.getY() + (available.getHeight() / 2) - (totalHeight / 2);

                // Create the translation point
                translation = new Point2D.Double(xloc, yloc);
            }

            // Move the origin of the drawing to the appropriate location
            g2.translate(translation.getX(), translation.getY());

            // Draw the legend's bounding box
            g2.setPaint(backgroundPaint);
            g2.fill(legendArea);
            g2.setPaint(outlinePaint);
            g2.setStroke(outlineStroke);
            g2.draw(legendArea);

            // Draw individual series elements
            for (int i = 0; i < items.length; i++) {
                g2.setPaint(items[i].getItem().getPaint());
                Shape keyBox = items[i].getMarker();
                g2.fill(keyBox);
                if (getOutlineKeyBoxes()) {
                    g2.setPaint(this.keyBoxOutlinePaint);
                    g2.setStroke(this.keyBoxOutlineStroke);
                    g2.draw(keyBox);
                }
                g2.setPaint(this.itemPaint);
                g2.setFont(this.itemFont);
                g2.drawString(items[i].getItem().getLabel(),
                              (float) items[i].getLabelPosition().getX(),
                              (float) items[i].getLabelPosition().getY());
            }

            // translate the origin back to what it was prior to drawing the legend
            g2.translate(-translation.getX(), -translation.getY());

            if (horizontal) {
                // The remaining drawing area bounding box will have the same
                // x origin, width and height independent of the anchor's
                // location. The variable is the y coordinate. If the anchor is
                // SOUTH, the y coordinate is simply the original y coordinate
                // of the available area. If it is NORTH, we adjust original y
                // by the total height of the legend and the initial gap.
                double yy = available.getY();
                double yloc = (inverted) ? yy
                                         : yy + legendArea.getHeight() + getOuterGap();

                Rectangle2D.Double res = new Rectangle2D.Double(available.getX(), yloc, available.getWidth(),
                    available.getHeight() - legendArea.getHeight() - 2 * getOuterGap());

                // return the remaining available drawing area
                return res;
            }
            else {
                // The remaining drawing area bounding box will have the same
                // y  origin, width and height independent of the anchor's
                // location. The variable is the x coordinate. If the anchor is
                // EAST, the x coordinate is simply the original x coordinate
                // of the available area. If it is WEST, we adjust original x
                // by the total width of the legend and the initial gap.
                double xloc = (inverted) ? available.getX()
                                         : available.getX()
                                           + legendArea.getWidth() + 2 * getOuterGap();

                // return the remaining available drawing area
                return new Rectangle2D.Double(xloc, available.getY(),
                    available.getWidth() - legendArea.getWidth() - 2 * getOuterGap(),
                    available.getHeight());
            }
        }
        else {
            return available;
        }
    }

    /**
     * Returns a box that will be positioned next to the name of the specified
     * series within the legend area.  The box will be square and 65% of the
     * height of a line.
     *
     * @param series  the index of the series.
     * @param seriesCount  number of series.
     * @param textHeight  the height of one line of text.
     * @param innerLegendArea  the upper left corner of the inner legend.
     *
     * @return a box.
     */
    private Rectangle2D getLegendBox(int series, int seriesCount,
                                     float textHeight, Rectangle2D innerLegendArea) {

        int innerGap = 2;  // added to make this compile
        float boxHeightAndWidth = textHeight * 0.70f;
        float xx = (float) innerLegendArea.getX() + innerGap + 0.15f * textHeight;
        float yy = (float) innerLegendArea.getY() + innerGap + (series + 0.15f) * textHeight;
        return new Rectangle2D.Float(xx, yy, boxHeightAndWidth, boxHeightAndWidth);

    }

    /**
     * Returns a rectangle surrounding a individual entry in the legend.
     * <P>
     * The marker box for each entry will be positioned next to the name of the
     * specified series within the legend area.  The marker box will be square
     * and 70% of the height of current font.
     *
     * @param graphics  the graphics context (supplies font metrics etc.).
     * @param legendItem  the legend item.
     * @param x  the upper left x coordinate for the bounding box.
     * @param y  the upper left y coordinate for the bounding box.
     *
     * @return a DrawableLegendItem encapsulating all necessary info for drawing.
     */
    private DrawableLegendItem createDrawableLegendItem(Graphics2D graphics,
                                                        LegendItem legendItem,
                                                        double x, double y) {

        int innerGap = 2;
        FontMetrics fm = graphics.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(legendItem.getLabel(), graphics);
        float textAscent = lm.getAscent();
        float lineHeight = textAscent + lm.getDescent() + lm.getLeading();

        DrawableLegendItem item = new DrawableLegendItem(legendItem);

        float xloc = (float) (x + innerGap + 1.15f * lineHeight);
        float yloc = (float) (y + innerGap + 0.15f * lineHeight + textAscent);

        item.setLabelPosition(new Point2D.Float(xloc, yloc));

        float boxDim = lineHeight * 0.70f;
        xloc = (float) (x + innerGap + 0.15f * lineHeight);
        yloc = (float) (y + innerGap + 0.15f * lineHeight);

        item.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));

        float width = (float) (item.getLabelPosition().getX() - x
                               + fm.getStringBounds(legendItem.getLabel(), graphics).getWidth()
                               + 0.5 * textAscent);

        float height = (float) (2 * innerGap + lineHeight);
        item.setBounds(x, y, width, height);
        return item;

    }

}
