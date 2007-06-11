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
 * -----------------------------------
 * VerticalStatisticalBarRenderer.java
 * -----------------------------------
 * (C) Copyright 2002, by Pascal Collet.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: VerticalStatisticalBarRenderer.java,v 1.1.1.1 2003/07/17 10:06:29 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1, contributed by Pascal Collet (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.StatisticalCategoryDataset;

/**
 * A renderer that handles the drawing of bars for a vertical bar plot where
 * each bar has a mean value and a standard deviation vertical line.
 *
 * @author PC
 */
public class VerticalStatisticalBarRenderer extends BarRenderer implements CategoryItemRenderer {

    /**
     * Creates a new renderer with no tool tip or URL generator.
     */
    public VerticalStatisticalBarRenderer() {
        this(null, null);
    }

    /**
     * Creates a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tooltip generator
     */
    public VerticalStatisticalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Creates a new renderer with the specified URL generator.
     *
     * @param urlGenerator  the URL generator
     */
    public VerticalStatisticalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tooltip generator
     * @param urlGenerator  the URL generator
     */
    public VerticalStatisticalBarRenderer(CategoryToolTipGenerator toolTipGenerator,
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
     * @param axis  the axis.
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
        this.calculateCategoryAndItemSpans(g2, dataArea, plot, data, dataArea.getWidth());

    }


    /**
     * Draws a line (or some other marker) to indicate a certain value on the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker.
     * @param axisDataArea  the area defined by the axes.
     * @param dataClipRegion  the data clip region.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(axisDataArea.getMinX(), y,
                                        axisDataArea.getMaxX(), y);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(line);

    }

    /**
     * Draws the bar with its standard deviation line range for a single (series, category) data
     * item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
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


        // defensive check
        if (!(data instanceof StatisticalCategoryDataset)) {
            throw new IllegalArgumentException("VerticalStatisticalBarRenderer.drawCategoryItem()"
                + " : the data should be of type StatisticalCategoryDataSet only.");
        }
        StatisticalCategoryDataset statData = (StatisticalCategoryDataset) data;

        // BAR X
        double rectX = dataArea.getX() + dataArea.getWidth() * plot.getIntroGapPercent();

        int categories = data.getCategoryCount();
        int seriesCount = data.getSeriesCount();
        if (categories > 1) {
            rectX = rectX
                  // bars in completed categories
                  + categoryIndex * (categorySpan / categories)
                  // gaps between completed categories
                  + (categoryIndex * (categoryGapSpan / (categories - 1))
                  // bars+gaps completed in current category
                  + (series * itemSpan / (categories * seriesCount)));
            if (seriesCount > 1) {
                rectX = rectX + (series * itemGapSpan / (categories * (seriesCount - 1)));
            }
        }
        else {
            rectX = rectX
                    // bars+gaps completed in current category
                    + (series * itemSpan / (categories * seriesCount));
            if (seriesCount > 1) {
                rectX = rectX + (series * itemGapSpan / (categories * (seriesCount - 1)));
            }
        }

        // BAR Y
        Number meanValue = statData.getMeanValue(series, category);
        double translatedValue = axis.translateValueToJava2D(meanValue.doubleValue(), dataArea);
        double rectY = Math.min(this.zeroInJava2D, translatedValue);

        double rectWidth = itemWidth;
        double rectHeight = Math.abs(translatedValue - this.zeroInJava2D);

        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        Paint seriesPaint = plot.getSeriesPaint(series);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (itemWidth > 3) {
            g2.setStroke(plot.getSeriesStroke(series));
            g2.setPaint(plot.getSeriesOutlinePaint(series));
            g2.draw(bar);
        }

        // standard deviation lines
        double valueDelta = statData.getStdDevValue(series, category).doubleValue();
        double highVal = axis.translateValueToJava2D(meanValue.doubleValue() + valueDelta,
                                                     dataArea);
        double lowVal = axis.translateValueToJava2D(meanValue.doubleValue() - valueDelta, dataArea);

        Line2D line = null;
        line = new Line2D.Double(rectX + rectWidth / 2.0d, lowVal,
                                 rectX + rectWidth / 2.0d, highVal);
        g2.draw(line);
        line = new Line2D.Double(rectX + rectWidth / 2.0d - 5.0d, highVal,
                                 rectX + rectWidth / 2.0d + 5.0d, highVal);
        g2.draw(line);
        line = new Line2D.Double(rectX + rectWidth / 2.0d - 5.0d, lowVal,
                                 rectX + rectWidth / 2.0d + 5.0d, lowVal);
        g2.draw(line);
    }


    /**
     * Returns true, since for this renderer there are gaps between the items
     * in one category.
     *
     * @return the flag.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     * Returns the number of bar-widths displayed in each category.  For this
     * renderer, there is one bar per series, so we return the number of series.
     *
     * @param data  the dataset.
     *
     * @return the number of bar widths per category.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }


    /**
     * Returns the area that the axes (and data) must fit into.
     * <P>
     * Often this is the same as the plotArea, but sometimes a smaller region should be used
     * (for example, the 3D charts require the axes to use less space in order to leave room
     * for the 'depth' part of the chart).
     *
     * @param plotArea The plot area.
     *
     * @return the axis area.
     */
    public Rectangle2D getAxisArea(Rectangle2D plotArea) {
        return plotArea;
    }

}
