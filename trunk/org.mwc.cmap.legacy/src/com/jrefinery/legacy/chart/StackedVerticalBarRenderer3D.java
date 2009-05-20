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
 * ---------------------------------
 * StackedVerticalBarRenderer3D.java
 * ---------------------------------
 * (C) Copyright 2000-2002, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Richard Atkinson;
 *
 * $Id: StackedVerticalBarRenderer3D.java,v 1.1.1.1 2003/07/17 10:06:27 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : Version 1, contributed by Serge V. Grachov (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 19-Jun-2002 : Added check for null info in drawCategoryItem method (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 26-Jun-2002 : Small change to entity (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs for HTML image
 *               maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import com.jrefinery.legacy.chart.entity.CategoryItemEntity;
import com.jrefinery.legacy.chart.entity.EntityCollection;
import com.jrefinery.legacy.data.CategoryDataset;

/**
 * Renders vertical stacked bars with 3D-effect.
 *
 * @author SVG
 */
public class StackedVerticalBarRenderer3D extends VerticalBarRenderer3D {

    /**
     * Returns true, to indicate that this renderer stacks values.  This
     * affects the axis range required to display all values.
     *
     * @return <code>true</code>.
     */
    public boolean isStacked() {

        return true;

    }

    /**
     * This will be a method in the renderer that tells whether there is one
     * bar width per category or onebarwidth per series per category.
     *
     * @param data  data set (not used by this implementation).
     *
     * @return <code>1</code>.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return 1;
    }

    /**
     * Returns false, since the items in each category are stacked on top of
     * one another.
     *
     * @return <code>false</code>.
     */
    public boolean hasItemGaps() {
        return false;
    }

    /**
     * Draws a stacked bar (with 3D-effect) for a specific item.
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
     *
     */
    public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                 CategoryPlot plot, ValueAxis axis,
                                 CategoryDataset data, int series, Object category,
                                 int categoryIndex, Object previousCategory) {

        Paint seriesPaint = plot.getSeriesPaint(series);
        Paint seriesOutlinePaint = plot.getSeriesOutlinePaint(series);

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
            }
            else {
                translatedBase = axis.translateValueToJava2D(negativeBase, dataArea);
                translatedValue = axis.translateValueToJava2D(negativeBase + xx, dataArea);
                barY = Math.min(translatedBase, translatedValue);
            }

            // BAR WIDTH
            double rectWidth = itemWidth;

            // BAR HEIGHT
            double barHeight = Math.abs(translatedValue - translatedBase);

            Rectangle2D bar = new Rectangle2D.Double(rectX, barY, rectWidth, barHeight);
            g2.setPaint(seriesPaint);
            g2.fill(bar);

            GeneralPath barR3d = null;
            GeneralPath barT3d = null;
            double effect3d = 0.00;
            VerticalAxis vAxis = (VerticalAxis) plot.getRangeAxis();
            if (barHeight != 0 && vAxis instanceof VerticalNumberAxis3D) {
                effect3d = ((VerticalNumberAxis3D) vAxis).getEffect3d();
                barR3d = new GeneralPath();
                barR3d.moveTo((float) (rectX + rectWidth), (float) barY);
                barR3d.lineTo((float) (rectX + rectWidth), (float) (barY + barHeight));
                barR3d.lineTo((float) (rectX + rectWidth + effect3d),
                              (float) (barY + barHeight - effect3d));
                barR3d.lineTo((float) (rectX + rectWidth + effect3d),
                              (float) (barY - effect3d));
                if (seriesPaint instanceof Color) {
                    g2.setPaint(((Color) seriesPaint).darker());
                }
                g2.fill(barR3d);

                if (xx > 0) {
                    barT3d = new GeneralPath();
                    barT3d.moveTo((float) rectX, (float) barY);
                    barT3d.lineTo((float) (rectX + effect3d), (float) (barY - effect3d));
                    barT3d.lineTo((float) (rectX + rectWidth + effect3d),
                                  (float) (barY - effect3d));
                    barT3d.lineTo((float) (rectX + rectWidth), (float) (barY));
                    if (seriesPaint instanceof Color) {
                        g2.setPaint(((Color) seriesPaint));//.brighter());
                    }
                    g2.fill(barT3d);
                }
            }

            if (rectWidth > 3) {
                g2.setStroke(plot.getSeriesOutlineStroke(series));
                g2.setPaint(seriesOutlinePaint);
                g2.draw(bar);
                if (barR3d != null) {
                    g2.draw(barR3d);
                }
                if (barT3d != null) {
                    g2.draw(barT3d);
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

}
