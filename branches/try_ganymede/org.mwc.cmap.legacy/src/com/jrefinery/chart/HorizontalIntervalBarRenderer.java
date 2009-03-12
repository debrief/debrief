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
 * ----------------------------------
 * HorizontalIntervalBarRenderer.java
 * ----------------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalIntervalBarRenderer.java,v 1.1.1.1 2003/07/17 10:06:23 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 21-Mar-2002 : Version 1 (DG);
 * 29-May-2002 : Added constructors (DG);
 * 13-Jun-2002 : Added check to make sure marker is visible before drawing it (DG);
 * 18-Jun-2002 : Fixed bug in drawCategoryItem (occurs when there is just one category) (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 20-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Oct-2002 : Added chart entity support (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.IntervalCategoryDataset;
import com.jrefinery.data.Range;

/**
 * A renderer that draws horizontal bars representing a data range on a category plot.
 * <P>
 * One application of this renderer is the creation of Gantt charts.
 *
 * @author DG
 */
public class HorizontalIntervalBarRenderer extends BarRenderer implements CategoryItemRenderer {

    /**
     * Creates a bar renderer with no tool tip or URL generators.
     */
    public HorizontalIntervalBarRenderer() {
        this(null, null);
    }

    /**
     * Constructs a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public HorizontalIntervalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Constructs a new renderer with the specified tool tip generator.
     *
     * @param urlGenerator  the URL generator.
     */
    public HorizontalIntervalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Constructs a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    public HorizontalIntervalBarRenderer(CategoryToolTipGenerator toolTipGenerator,
                                         CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be plotted.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param data  the data.
     * @param info  collects chart rendering information for return to caller.
     *
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ValueAxis axis,
                           CategoryDataset data,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, axis, data, info);
        this.calculateCategoryAndItemSpans(g2, dataArea, plot, data, dataArea.getHeight());

    }

    /**
     * Returns true, since for this renderer there are gaps between the items
     * in one category.
     *
     * @return always <code>false</code>.
     */
    public boolean hasItemGaps() {
        return false;
    }

    /**
     * This renderer shows each series within a category as a separate bar (as
     * opposed to a stacked bar renderer).
     *
     * @param data  the data.
     *
     * @return the number of series in the data.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }

    /**
     * Draws a vertical line across the chart to represent the marker.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker line.
     * @param axisDataArea  the axis data area.
     * @param dataClipRegion  the data clip region.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double x = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(x, axisDataArea.getMinY(),
                                        x, axisDataArea.getMaxY());
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
     * @param previousCategory  the previous category (will be null when the first category is
     *                          drawn).
     */
    public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                 CategoryPlot plot, ValueAxis axis,
                                 CategoryDataset data, int series, Object category,
                                 int categoryIndex, Object previousCategory) {

        IntervalCategoryDataset intervalData = (IntervalCategoryDataset) data;

        // X0
        Number value0 = intervalData.getStartValue(series, category);
        double translatedValue0 = axis.translateValueToJava2D(value0.doubleValue(), dataArea);

        // X1
        Number value1 = intervalData.getEndValue(series, category);
        double translatedValue1 = axis.translateValueToJava2D(value1.doubleValue(), dataArea);

        if (translatedValue1 < translatedValue0) {
          double temp = translatedValue1;
          translatedValue1 = translatedValue0;
          translatedValue0 = temp;
        }

        // Y
        double rectY = dataArea.getY() + dataArea.getHeight() * plot.getIntroGapPercent();
        int categories = data.getCategoryCount();
        int seriesCount = data.getSeriesCount();
        if (categories > 1) {
            rectY = rectY
                  // bars in completed categories
                  + (categoryIndex * categorySpan / categories)
                  // gaps between completed categories
                  + (categoryIndex * categoryGapSpan / (categories - 1))
                  // bars+gaps completed in current category
                  + (series * itemSpan / (categories * seriesCount));
            if (seriesCount > 1) {
                rectY = rectY
                        + (series * itemGapSpan / (categories * (seriesCount - 1)));
            }
        }

        else {
            rectY = rectY
                    // bars+gaps completed in current category;
                    + (series * itemSpan / (categories * seriesCount));
            if (seriesCount > 1) {
                rectY = rectY
                        + (series * itemGapSpan / (categories * (seriesCount - 1)));
            }
        }

        // WIDTH
        double rectWidth = Math.abs(translatedValue1 - translatedValue0);

        // HEIGHT
        double rectHeight = itemWidth;

        // DRAW THE BAR...
        Rectangle2D bar = new Rectangle2D.Double(translatedValue0, rectY, rectWidth, rectHeight);

        Paint seriesPaint = plot.getSeriesPaint(series);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (itemWidth > BAR_OUTLINE_WIDTH_THRESHOLD) {
            g2.setStroke(plot.getSeriesStroke(series));
            g2.setPaint(plot.getSeriesOutlinePaint(series));
            g2.draw(bar);
        }

        // collect entity and tool tip information...
        if (getInfo() != null) {
            EntityCollection entities = getInfo().getEntityCollection();
            if (entities != null) {
                String tip = null;
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
