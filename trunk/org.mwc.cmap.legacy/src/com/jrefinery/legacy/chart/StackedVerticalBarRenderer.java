/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * -------------------------------
 * StackedVerticalBarRenderer.java
 * -------------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Thierry Saura;
 *
 * $Id: StackedVerticalBarRenderer.java,v 1.1.1.1 2003/07/17 10:06:27 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 19-Oct-2001 : Version 1 (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 22-Nov-2001 : Modified to allow for negative data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Fixed bug for single category datasets (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 * 14-Mar-2002 : Modified to implement the CategoryItemRenderer interface (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 11-Jun-2002 : Added check for (permitted) null info object, bug and fix reported by David
 *               Basten.  Also updated Javadocs. (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 26-Jun-2002 : Small change to entity (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 08-Aug-2002 : Added optional linking lines, contributed by Thierry Saura (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.legacy.chart.entity.CategoryItemEntity;
import com.jrefinery.legacy.chart.entity.EntityCollection;
import com.jrefinery.legacy.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.legacy.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.legacy.data.CategoryDataset;

/**
 * A bar renderer that draws stacked bars for a vertical bar plot.
 *
 * @author DG
 */
public class StackedVerticalBarRenderer extends VerticalBarRenderer {

    /** Linking lines flag. */
    private boolean linkingLines = false;

    /** Points set register. */
    private double[] pointsRegister = null;

    /**
     * Constructs a renderer with a standard tool tip generator.
     */
    public StackedVerticalBarRenderer() {
        this(new StandardCategoryToolTipGenerator());
    }

    /**
     * Constructs a renderer with a specific tool tip generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public StackedVerticalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        super(toolTipGenerator);
    }

    /**
     * Returns a flag to indicate whether or not there are lines between the items.
     *
     * @return boolean
     */
    public boolean hasLinkingLines() {
        return this.linkingLines;
    }

    /**
     * Sets or unsets the linking lines between items.
     *
     * @param status boolean linking lines if true.
     */
    public void setLinkingLines(boolean status) {
        this.linkingLines = status;
    }

    /**
     * Draws a stacked bar for a specific item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param data  the data.
     * @param series  the series number (zero-based index).
     * @param category  the category.
     * @param categoryIndex  the category number (zero-based index).
     * @param previousCategory  the previous category.
     */
    public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                 CategoryPlot plot, ValueAxis axis,
                                 CategoryDataset data, int series, Object category,
                                 int categoryIndex, Object previousCategory) {

        Paint seriesPaint = plot.getSeriesPaint(series);
        Paint seriesOutlinePaint = plot.getSeriesOutlinePaint(series);

        if (hasLinkingLines() && (pointsRegister == null)) {
            // need to init the points set...
            pointsRegister = new double[data.getCategoryCount() * data.getSeriesCount() * 2];
        }

        // BAR X
        double rectX = dataArea.getX()
                       // intro gap
                       + dataArea.getWidth() * plot.getIntroGapPercent()
                       // bars in completed categories
                       + categoryIndex * categorySpan / data.getCategoryCount();
        if (data.getCategoryCount() > 1) {
            // gaps between completed categories
            rectX = rectX
                  + categoryIndex * categoryGapSpan / (data.getCategoryCount() - 1);
        }

        // BAR Y
        double positiveBase = 0.0;
        double negativeBase = 0.0;

        for (int i = 0; i < series; i++) {
            Number v = data.getValue(i, category);
            if (v != null) {
                double d = v.doubleValue();
                if (d > 0) {
                    positiveBase = positiveBase + d;
                }
                else {
                    negativeBase = negativeBase + d;
                }
            }
        }

        boolean positiveValue = false;
        Number value = data.getValue(series, category);
        if (value != null) {
            double xx = value.doubleValue();
            double translatedBase;
            double translatedValue;
            double barY;
            if (xx > 0) {
                translatedBase = axis.translateValueToJava2D(positiveBase, dataArea);
                translatedValue = axis.translateValueToJava2D(positiveBase + xx, dataArea);
                barY = Math.min(translatedBase, translatedValue);
                positiveValue = true;
            }
            else {
                translatedBase = axis.translateValueToJava2D(negativeBase, dataArea);
                translatedValue = axis.translateValueToJava2D(negativeBase + xx, dataArea);
                barY = Math.min(translatedBase, translatedValue);
            }

            double rectWidth = itemWidth;
            double barHeight = Math.abs(translatedValue - translatedBase);

            Rectangle2D bar = new Rectangle2D.Double(rectX, barY, rectWidth, barHeight);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (rectWidth > 3) {
                g2.setStroke(plot.getSeriesStroke(series));
                g2.setPaint(seriesOutlinePaint);
                g2.draw(bar);
            }

            if (hasLinkingLines()) {
                // the same series of two categories are linked together
                if (categoryIndex == 0) {
                    // first category, no line drawn
                    pointsRegister[2 * series] = rectX + rectWidth;
                    if (positiveValue) {
                        pointsRegister[(2 * series) + 1] = barY;
                    }
                    else {
                        pointsRegister[(2 * series) + 1] = barY + barHeight;
                    }
                }
                else {
                    // other categories
                    int position = (categoryIndex * data.getSeriesCount() * 2) + (2 * series);
                    pointsRegister[position] = rectX + rectWidth;
                    if (positiveValue) {
                        // draw a line between two stacked bars
                        double lastX = pointsRegister[position - 2 * data.getSeriesCount()];
                        if (lastX > 0.0) {
                            Line2D line = new Line2D.Double(rectX, barY, lastX,
                                pointsRegister[position - 2 * data.getSeriesCount() + 1]);
                            g2.setPaint(Color.black);
                            g2.draw(line);
                        }
                        // register the base's extremity of the drawing bar
                        pointsRegister[position + 1] = barY;
                    }
                    else {
                        double lastX = pointsRegister[position - 2 * data.getSeriesCount()];
                        if (lastX > 0.0) {
                            Line2D line = new Line2D.Double(rectX, barY + barHeight, lastX,
                                pointsRegister[position - 2 * data.getSeriesCount() + 1]);
                            g2.setPaint(Color.black);
                            g2.draw(line);
                        }
                        pointsRegister[position + 1] = barY + barHeight;
                    }
                }
            }


            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null) {
                    String tip = "";
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, series, category);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(data, series, category);
                    }
                    CategoryItemEntity entity
                        = new CategoryItemEntity(bar, tip, url, series, category, categoryIndex);
                    entities.addEntity(entity);
                }
            }
        }

    }

    /**
     * Returns true, to indicate that this renderer stacks values.  This
     * affects the axis range required to display all values.
     *
     * @return Always true.
     */
    public boolean isStacked() {
        return true;
    }

    /**
     * Returns the number of "bar widths" per category.
     * <P>
     * For this style of rendering, there is only one bar per category.
     *
     * @param data  the dataset (ignored).
     *
     * @return Always <code>1</code>.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return 1;
    }

    /**
     * Returns a flag (always false for this renderer) to indicate whether or
     * not there are gaps between items in the plot.
     *
     * @return Always <code>false</code>.
     */
    public boolean hasItemGaps() {
        return false;
    }

}
