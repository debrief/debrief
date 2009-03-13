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
 * ------------------------------------
 * StackedAreaCategoryItemRenderer.java
 * ------------------------------------
 * (C) Copyright 2002, by Dan Rivett (d.rivett@ukonline.co.uk).
 *
 * Original Author:  Dan Rivett (adapted from AreaCategoryItemRenderer);
 * Contributor(s):   Jon Iles, David Gilbert (for Simba Management Limited);
 *
 * $Id: StackedAreaCategoryItemRenderer.java,v 1.1.1.1 2003/07/17 10:06:26 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 20-Sep-2002 : Version 1, contributed by Dan Rivett;
 *
 */

package com.jrefinery.chart;

import com.jrefinery.data.CategoryDataset;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

/**
 * A category item renderer that draws stacked area charts.  You can use this renderer
 * with the VerticalCategoryPlot class.
 *
 * @author DR
 */
public class StackedAreaCategoryItemRenderer extends AreaCategoryItemRenderer {

    /**
     * Returns true to signify that this is a stacked chart.
     *
     * @see com.jrefinery.chart.CategoryItemRenderer#isStacked()
     */
    public boolean isStacked() {
        return true;
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param data  the data.
     * @param series  the series number (zero-based index).
     * @param category  the category.
     * @param categoryIndex  the category number (zero-based index).
     * @param previousCategory  the previous category (will be null when the first category is
     *                          drawn).
     */
    public void drawCategoryItem(Graphics2D g2,
                                 Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 ValueAxis axis,
                                 CategoryDataset data,
                                 int series,
                                 Object category,
                                 int categoryIndex,
                                 Object previousCategory) {

        // plot non-null values...
        Number value = data.getValue(series, category);
        if (value != null) {
            // leave the y values (y1, y0) untranslated as it is going to be be stacked
            // up later by previous series values, after this it will be translated.
            double x1 = plot.getCategoryCoordinate(categoryIndex, dataArea);
            double y1Untranslated = value.doubleValue();

            g2.setPaint(plot.getSeriesPaint(series));
            g2.setStroke(plot.getSeriesStroke(series));

            if (previousCategory != null) {

                Number previousValue = data.getValue(series, previousCategory);
                if (previousValue != null) {

                    double x0 = plot.getCategoryCoordinate(categoryIndex - 1, dataArea);
                    double y0Untranslated = previousValue.doubleValue();

                    // Get the previous height, but this will be different for both y0 and y1 as
                    // the previous series values could differ.
                    double previousHeightx0Untranslated
                        = getPreviousHeight(data, series, previousCategory);
                    double previousHeightx1Untranslated
                        = getPreviousHeight(data, series, category);

                    // Now stack the current y values on top of the previous values.
                    y0Untranslated += previousHeightx0Untranslated;
                    y1Untranslated += previousHeightx1Untranslated;

                    // Now translate the previous heights
                    double previousHeightx0
                        = axis.translateValueToJava2D(previousHeightx0Untranslated, dataArea);
                    double previousHeightx1
                        = axis.translateValueToJava2D(previousHeightx1Untranslated, dataArea);

                    // Now translate the current y values.
                    double y0 = axis.translateValueToJava2D(y0Untranslated, dataArea);
                    double y1 = axis.translateValueToJava2D(y1Untranslated, dataArea);

                    // create the Polygon of these stacked, translated values.
                    Polygon p = new Polygon();
                    p.addPoint((int) x0, (int) y0);
                    p.addPoint((int) x1, (int) y1);
                    p.addPoint((int) x1, (int) previousHeightx1);
                    p.addPoint((int) x0, (int) previousHeightx0);

                    g2.setPaint(plot.getSeriesPaint(series));
                    g2.setStroke(plot.getSeriesStroke(series));
                    g2.fill(p);
                }
            }
        }
    }

    /**
     * Calculates the stacked value of the all series up to, but not including <code>series</code>
     * for the specified category, <code>category</code>.  It returns 0.0 if <code>series</code>
     * is the first series, i.e. 0.
     *
     * @param data  the data.
     * @param series  the series.
     * @param category  the category.
     *
     * @return double returns a cumulative value for all series' values up to but excluding
     *                <code>series</code> for Object <code>category</code>.
     */
    protected double getPreviousHeight(CategoryDataset data, int series, Object category) {

        double result = 0.0;

        Number tmp;
        for (int i = 0; i < series; i++) {
            tmp = data.getValue(i, category);
            if (tmp != null) {
                result += tmp.doubleValue();
            }
        }

        return result;

    }

}
