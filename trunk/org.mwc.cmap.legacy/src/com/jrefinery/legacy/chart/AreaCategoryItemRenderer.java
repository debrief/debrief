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
 * -----------------------------
 * AreaCategoryItemRenderer.java
 * -----------------------------
 * (C) Copyright 2002, by Jon Iles and Contributors.
 *
 * Original Author:  Jon Iles;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: AreaCategoryItemRenderer.java,v 1.1.1.1 2003/07/17 10:06:19 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 21-May-2002 : Version 1, contributed by John Iles (DG);
 * 29-May-2002 : Now extends AbstractCategoryItemRenderer (DG);
 * 11-Jun-2002 : Updated Javadoc comments (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Oct-2002 : Added constructors and basic entity support (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.legacy.chart.entity.CategoryItemEntity;
import com.jrefinery.legacy.chart.entity.EntityCollection;
import com.jrefinery.legacy.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.legacy.chart.urls.CategoryURLGenerator;
import com.jrefinery.legacy.data.CategoryDataset;
import com.jrefinery.legacy.data.Range;

/**
 * A category item renderer that draws area charts.  You can use this renderer
 * with the VerticalCategoryPlot class.
 *
 * @author JI
 */
public class AreaCategoryItemRenderer extends AbstractCategoryItemRenderer {

    /**
     * Creates a new renderer.
     */
    public AreaCategoryItemRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer.
     *
     * @param toolTipGenerator  the tool tip generator (null permitted).
     * @param urlGenerator  the URL generator (null permitted).
     */
    public AreaCategoryItemRenderer(CategoryToolTipGenerator toolTipGenerator,
                                    CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Draws a line (or some other marker) to indicate a certain value on the
     * range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker.
     * @param axisDataArea  the area defined by the axes.
     * @param dataClipRegion  the data clip region.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D axisDataArea,
                                Shape dataClipRegion) {

        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(axisDataArea.getMinX(), y,
                                        axisDataArea.getMaxX(), y);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(line);

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
     * @param previousCategory  the previous category (will be null when
     *                          the first category is drawn).
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
            double x1 = plot.getCategoryCoordinate(categoryIndex, dataArea);
            double y1 = axis.translateValueToJava2D(value.doubleValue(), dataArea);

            g2.setPaint(plot.getSeriesPaint(series));
            g2.setStroke(plot.getSeriesStroke(series));

            if (previousCategory != null) {
                Number previousValue = data.getValue(series, previousCategory);
                if (previousValue != null) {
                    double x0 = plot.getCategoryCoordinate(categoryIndex - 1, dataArea);
                    double y0 = axis.translateValueToJava2D(previousValue.doubleValue(), dataArea);
                    double zeroInJava2D = axis.translateValueToJava2D(0.0, dataArea);

                    Polygon p = new Polygon ();
                    p.addPoint((int) x0, (int) y0);
                    p.addPoint((int) x1, (int) y1);
                    p.addPoint((int) x1, (int) zeroInJava2D);
                    p.addPoint((int) x0, (int) zeroInJava2D);

                    g2.setPaint(plot.getSeriesPaint(series));
                    g2.setStroke(plot.getSeriesStroke(series));
                    g2.fill(p);
                }
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                Shape shape = new Rectangle2D.Double(x1 - 3.0, y1 - 3.0, 6.0, 6.0);
                if (entities != null && shape != null) {
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, series, category);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(data, series, category);
                    }
                    CategoryItemEntity entity
                        = new CategoryItemEntity(shape, tip, url, series, category, categoryIndex);
                    entities.addEntity(entity);
                }
            }
        }

    }

}
