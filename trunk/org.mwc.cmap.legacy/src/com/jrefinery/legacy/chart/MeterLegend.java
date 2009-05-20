/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * ----------------
 * MeterLegend.java
 * ----------------
 * (C) Copyright 2000-2002, by Hari and Contributors.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: MeterLegend.java,v 1.1.1.1 2003/07/17 10:06:25 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 01-Apr-2002 : Version 1, contributed by Hari (DG);
 * 25-Jun-2002 : Updated imports and Javadoc comments (DG);
 * 18-Sep-2002 : Updated for changes to StandardLegend (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.legacy.chart.event.LegendChangeEvent;
import com.jrefinery.legacy.data.MeterDataset;

/**
 * A legend for meter plots.
 *
 * @author Hari
 */
public class MeterLegend extends StandardLegend {

    /** The legend text. */
    private String legendText;

    /** Show the normal range? */
    private boolean showNormal = true;

    /** Show the warning range? */
    private boolean showWarning = true;

    /** Show the critical range? */
    private boolean showCritical = true;


    /**
     * Constructs a new legend.
     *
     * @param chart  the chart.
     * @param legendText  the legend text.
     *
     */
    public MeterLegend(JFreeChart chart, String legendText) {

        this(chart, 3, new Spacer(Spacer.ABSOLUTE, 2, 2, 2, 2),
             Color.white, new BasicStroke(), Color.gray,
             DEFAULT_FONT, Color.black, legendText);

    }

    /**
     * Constructs a new legend.
     *
     * @param chart  the chart.
     * @param outerGap  the gap around the outside of the legend.
     * @param innerGap  the gaps inside the border of the legend.
     * @param backgroundPaint  the background color.
     * @param outlineStroke  the outline stroke.
     * @param outlinePaint  the outline paint.
     * @param itemFont  the font used for the legend items.
     * @param itemPaint  the color used for the legend items.
     * @param legendText  the text for the legend.
     *
     */
    public MeterLegend(JFreeChart chart,
                       int outerGap, Spacer innerGap,
                       Paint backgroundPaint,
                       Stroke outlineStroke, Paint outlinePaint,
                       Font itemFont, Paint itemPaint, String legendText) {

        super(chart, outerGap, innerGap, backgroundPaint,
              outlineStroke, outlinePaint, itemFont, itemPaint);

        this.legendText = legendText;

    }

    /**
     * Returns the legend text.
     *
     * @return the legend text.
     */
    public String getLegendText() {
        return this.legendText;
    }

    /**
     * Sets the legend text.
     *
     * @param text the new legend text.
     */
    public void setLegendText(String text) {
        this.legendText = text;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Draws the legend.
     *
     * @param g2  the graphics device.
     * @param available  the available area.
     *
     * @return the remaining available drawing area.
     */
    public Rectangle2D draw(Graphics2D g2, Rectangle2D available) {

        return draw(g2, available, (getAnchor() & HORIZONTAL) != 0, (getAnchor() & INVERTED) != 0);

    }

    /**
     * Updates the legend information.
     *
     * @param plot  the plot.
     * @param data  the dataset.
     * @param type  the type.
     * @param index  the index.
     * @param legendItems  the legend items.
     * @param legendItemColors  the colors.
     *
     * @return boolean.
     */
    private boolean updateInformation(MeterPlot plot, MeterDataset data, int type, int index,
                                      LegendItem[] legendItems, Paint[] legendItemColors) {

        boolean ret = false;
        String label = null;
        Number minValue = null;
        Number maxValue = null;
        Paint paint = null;

        switch(type) {
            case MeterDataset.NORMAL_DATA:
                minValue = data.getMinimumNormalValue();
                maxValue = data.getMaximumNormalValue();
                paint = plot.getNormalPaint();
                label = MeterPlot.NORMAL_TEXT;
                break;
            case MeterDataset.WARNING_DATA:
                minValue = data.getMinimumWarningValue();
                maxValue = data.getMaximumWarningValue();
                paint = plot.getWarningPaint();
                label = MeterPlot.WARNING_TEXT;
                break;
            case MeterDataset.CRITICAL_DATA:
                minValue = data.getMinimumCriticalValue();
                maxValue = data.getMaximumCriticalValue();
                paint = plot.getCriticalPaint();
                label = MeterPlot.CRITICAL_TEXT;
                break;
            case MeterDataset.FULL_DATA:
                minValue = data.getMinimumValue();
                maxValue = data.getMaximumValue();
                paint = MeterPlot.DEFAULT_BACKGROUND_PAINT;
                label = "Meter Graph";
                break;
            default:
                return false;
        }

        if (minValue != null && maxValue != null) {
            if (data.getBorderType() == type) {
                label += "  Range: "
                      + data.getMinimumValue().toString() + " to "
                      + minValue.toString()
                      + "  and  "
                      + maxValue.toString() + " to "
                      + data.getMaximumValue().toString();
            }
            else {
                label += "  Range: " + minValue.toString() + " to " + maxValue.toString();
            }
            legendItems[index] = new LegendItem(label, label, null, null, null, null, null);
            legendItemColors[index] = paint;
            ret = true;
        }
        return ret;
    }

    /**
     * Draws the legend.
     *
     * @param g2  the graphics device.
     * @param available  the available drawing area.
     * @param horizontal  if <code>true</code> draw a horizontal legend.
     * @param inverted  ???
     *
     * @return the remaining available drawing area.
     *
     */
    protected Rectangle2D draw(Graphics2D g2, Rectangle2D available,
                               boolean horizontal, boolean inverted) {

        int legendCount = 0;
        Plot plot = getChart().getPlot();
        if (!(plot instanceof MeterPlot)) {
            throw new IllegalArgumentException("Plot must be MeterPlot");
        }
        MeterPlot meterPlot = (MeterPlot) plot;
        MeterDataset data = meterPlot.getMeterDataset();

        legendCount = 1;  // Name of the Chart.
        legendCount++;    // Display Full Range
        if (showCritical && data.getMinimumCriticalValue() != null) {
            legendCount++;
        }
        if (showWarning && data.getMinimumWarningValue() != null) {
            legendCount++;
        }
        if (showNormal && data.getMinimumNormalValue() != null) {
            legendCount++;
        }

        LegendItem[] legendItems = new LegendItem[legendCount];
        Color[] legendItemColors = new Color[legendCount];

        int currentItem = 0;
        String label = this.legendText
            + (data.isValueValid() ? ("   Current Value: " + data.getValue().toString()) : "");
        legendItems[currentItem] = new LegendItem(label, label, null, null, null, null, null);
        legendItemColors[currentItem] = null;  // no color
        currentItem++;
        if (updateInformation(meterPlot, data, MeterDataset.FULL_DATA,
            currentItem, legendItems, legendItemColors)) {
            currentItem++;
        }
        if (showCritical && updateInformation(meterPlot, data,
            MeterDataset.CRITICAL_DATA, currentItem, legendItems, legendItemColors)) {
            currentItem++;
        }
        if (showWarning && updateInformation(meterPlot, data,
            MeterDataset.WARNING_DATA, currentItem, legendItems, legendItemColors)) {
            currentItem++;
        }
        if (showNormal && updateInformation(meterPlot, data,
            MeterDataset.NORMAL_DATA, currentItem, legendItems, legendItemColors)) {
            currentItem++;
        }

        if (legendItems != null) {

            Rectangle2D legendArea = new Rectangle2D.Double();

            // the translation point for the origin of the drawing system
            Point2D translation = new Point2D.Double();

            // Create buffer for individual rectangles within the legend
            DrawableLegendItem[] items = new DrawableLegendItem[legendItems.length];
            g2.setFont(getItemFont());

            // Compute individual rectangles in the legend, translation point
            // as well as the bounding box for the legend.
            if (horizontal) {
                double xstart = available.getX() + getOuterGap();
                double xlimit = available.getX() + available.getWidth() - 2 * getOuterGap() - 1;
                double maxRowWidth = 0;
                double xoffset = 0;
                double rowHeight = 0;
                double totalHeight = 0;
                boolean startingNewRow = true;

                for (int i = 0; i < legendItems.length; i++) {
                    items[i] = createLegendItem(g2, legendItems[i], xoffset, totalHeight);
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
                g2.setFont(getItemFont());
                for (int i = 0; i < items.length; i++) {
                    items[i] = createLegendItem(g2, legendItems[i], 0, totalHeight);
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
            g2.setPaint(getBackgroundPaint());
            g2.fill(legendArea);
            g2.setPaint(getOutlinePaint());
            g2.setStroke(getOutlineStroke());
            g2.draw(legendArea);

            // Draw individual series elements
            for (int i = 0; i < items.length; i++) {
                Color color = legendItemColors[i];
                if (color != null) {
                    g2.setPaint(color);
                    g2.fill(items[i].getMarker());
                }
                g2.setPaint(getItemPaint());
                g2.drawString(items[i].getItem().getLabel(),
                              (float) items[i].getLabelPosition().getX(),
                              (float) items[i].getLabelPosition().getY());
            }

            // translate the origin back to what it was prior to drawing the
            // legend
            g2.translate(-translation.getX(), -translation.getY());

            if (horizontal) {
                // The remaining drawing area bounding box will have the same
                // x origin, width and height independent of the anchor's
                // location. The variable is the y coordinate. If the anchor is
                // SOUTH, the y coordinate is simply the original y coordinate
                // of the available area. If it is NORTH, we adjust original y
                // by the total height of the legend and the initial gap.
                double yloc = (inverted)
                    ? available.getY()
                    : available.getY() + legendArea.getHeight() + getOuterGap();

                // return the remaining available drawing area
                return new Rectangle2D.Double(available.getX(), yloc,
                                              available.getWidth(),
                                              available.getHeight() - legendArea.getHeight()
                                              - 2 * getOuterGap());
            }
            else {
                // The remaining drawing area bounding box will have the same
                // y origin, width and height independent of the anchor's
                // location. The variable is the x coordinate. If the anchor is
                // EAST, the x coordinate is simply the original x coordinate
                // of the available area. If it is WEST, we adjust original x
                // by the total width of the legend and the initial gap.
                double xloc = (inverted) ? available.getX()
                    : available.getX() + legendArea.getWidth() + 2 * getOuterGap();

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
     * Creates a legend item
     *
     * @param graphics  the graphics device.
     * @param item  the legend item.
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     *
     * @return the legend item.
     */
    private DrawableLegendItem createLegendItem(Graphics graphics,
                                                LegendItem item, double x, double y) {

        int innerGap = 2;
        FontMetrics fm = graphics.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(item.getLabel(), graphics);
        float textHeight = lm.getHeight();

        DrawableLegendItem drawable = new DrawableLegendItem(item);

        float xloc = (float) (x + innerGap + 1.15f * textHeight);
        float yloc = (float) (y + innerGap + (textHeight - lm.getLeading() - lm.getDescent()));

        drawable.setLabelPosition(new Point2D.Float(xloc, yloc));

        float boxDim = textHeight * 0.70f;
        xloc = (float) (x + innerGap + 0.15f * textHeight);
        yloc = (float) (y + innerGap + 0.15f * textHeight);

        drawable.setMarker(new Rectangle2D.Float(xloc, yloc, boxDim, boxDim));

        float width = (float) (drawable.getLabelPosition().getX() - x
                               + fm.getStringBounds(item.getLabel(), graphics).getWidth()
                               + 0.5 * textHeight);

        float height = (float) (2 * innerGap + textHeight);
        drawable.setBounds(x, y, width, height);
        return drawable;

    }

}
