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
 * ---------------------------
 * HorizontalCategoryPlot.java
 * ---------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jeremy Bowman;
 *
 * $Id: HorizontalCategoryPlot.java,v 1.1.1.1 2003/07/17 10:06:23 Ian.Mayo Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke attributes from JFreeChart.java to Plot.java (DG);
 * 22-Oct-2001 : Changed draw(...) method with introduction of HorizontalBarRenderer class (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debugging for gap settings (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause in constructors (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Renamed the tooltips class (DG);
 * 22-Jan-2002 : Added DrawInfo class, incorporating tooltips and crosshairs (DG);
 * 06-Feb-2002 : Added optional background image and alpha-transparency to Plot and subclasses (DG);
 * 13-Feb-2002 : Renamed getCategoryAxis() --> getDomainAxis() (DG);
 * 15-Feb-2002 : Modified getMaximumVerticalDataValue() and getMinimumVerticalDataValue() to handle
 *               stacked plots (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 * 13-Mar-2002 : Renamed HorizontalBarPlot.java --> HorizontalCategoryPlot.java (DG);
 * 03-Apr-2002 : Added g2.setPaint(...) in draw(...) method (DG);
 * 23-Apr-2002 : Moved dataset from JFreeChart to Plot (DG);
 * 29-Apr-2002 : Added getHorizontalAxis() method (DG);
 * 13-May-2002 : Added methods (by Jeremy Bowman) to check axis compatibility (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 26-Jun-2002 : Added axis to initialise(...) method call (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.util.Iterator;

import com.jrefinery.legacy.data.CategoryDataset;
import com.jrefinery.legacy.data.DatasetUtilities;
import com.jrefinery.legacy.data.Range;

/**
 * A class that plots data from a CategoryDataset, with the values plotted
 * along the horizontal axis and the categories plotted along the vertical axis.
 * A plug-in class that implements the CategoryItemRenderer class is used to
 * draw individual data items.
 *
 * @see CategoryPlot
 * @see CategoryItemRenderer
 *
 * @author DG
 */
public class HorizontalCategoryPlot extends CategoryPlot implements HorizontalValuePlot {

    /**
     * Constructs a horizontal category plot.
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the renderer for the data.
     */
    public HorizontalCategoryPlot(CategoryDataset data,
                                  CategoryAxis domainAxis,
                                  ValueAxis rangeAxis,
                                  CategoryItemRenderer renderer) {

        this(data,
             domainAxis,
             rangeAxis,
             renderer,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             CategoryPlot.DEFAULT_INTRO_GAP_PERCENT,
             CategoryPlot.DEFAULT_TRAIL_GAP_PERCENT,
             CategoryPlot.DEFAULT_CATEGORY_GAPS_PERCENT,
             CategoryPlot.DEFAULT_ITEM_GAPS_PERCENT);

    }

    /**
     * Constructs a horizontal category plot.
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the renderer for the plot.
     * @param insets  the amount of space to leave blank around the edges of the plot.
     * @param backgroundPaint  an optional color for the plot's background.
     * @param backgroundImage  an optional image for the plot's background.
     * @param backgroundAlpha  alpha-transparency for the plot's background.
     * @param outlineStroke  the Stroke used to draw an outline around the plot.
     * @param outlinePaint  the color used to draw an outline around the plot.
     * @param alpha  the alpha-transparency for the plot.
     * @param introGapPercent  the gap before the first bar in the plot.
     * @param trailGapPercent  the gap after the last bar in the plot.
     * @param categoryGapPercent  the gap between the last bar in one
     *                            category and the first bar in the next category.
     * @param itemGapPercent  the gap between bars within the same category.
     */
    public HorizontalCategoryPlot(CategoryDataset data,
                                  CategoryAxis domainAxis, ValueAxis rangeAxis,
                                  CategoryItemRenderer renderer,
                                  Insets insets,
                                  Paint backgroundPaint,
                                  Image backgroundImage, float backgroundAlpha,
                                  Stroke outlineStroke, Paint outlinePaint,
                                  float alpha,
                                  double introGapPercent, double trailGapPercent,
                                  double categoryGapPercent, double itemGapPercent) {

        super(data,
              domainAxis, rangeAxis, renderer,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint,
              alpha,
              introGapPercent, trailGapPercent, categoryGapPercent,
              itemGapPercent
              );

    }

    /**
     * A convenience method that returns the dataset for the plot, cast as a CategoryDataset.
     *
     * @return The category dataset.
     */
    public CategoryDataset getCategoryDataset() {

        return (CategoryDataset) dataset;

    }

    /**
     * Returns the x-coordinate (in Java 2D User Space) of the center of the specified category.
     *
     * @param category  the category (zero-based index).
     * @param area  the region within which the plot will be drawn.
     *
     * @return the x-coordinate of the center of the specified category.
     */
    public double getCategoryCoordinate(int category, Rectangle2D area) {

        // calculate first part of result...
        double result = area.getY() + (area.getHeight() * introGapPercent);


        // then add some depending on how many categories...
        int categoryCount = getCategoryDataset().getCategoryCount();
        if (categoryCount > 1) {

            double categorySpan = area.getHeight()
                                  * (1 - introGapPercent - trailGapPercent - categoryGapsPercent);
            double categoryGapSpan = area.getHeight() * categoryGapsPercent;
            result = result
                     + (category + 0.5) * (categorySpan / categoryCount)
                     + (category) * (categoryGapSpan / (categoryCount - 1));

        }
        else {
            result = result + (category + 0.5) * area.getHeight()
                            * (1 - introGapPercent - trailGapPercent);
        }

        return result;

    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the
     * axis is compatible with the plot, and false otherwise.
     *
     * @param axis The proposed horizontal axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {

        if ((axis instanceof HorizontalAxis) && (axis instanceof ValueAxis)) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Checks the compatibility of a vertical axis, returning true if the axis
     * is compatible with the plot, and false otherwise.
     *
     * @param axis  the vertical axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {

        if (axis instanceof VerticalCategoryAxis) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Checks the compatibility of a domain axis, returning true if the axis
     * is compatible with the plot, and false otherwise.
     *
     * @param axis  the proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleDomainAxis(CategoryAxis axis) {

        if (axis == null) {
            return true;
        }
        else {
            return isCompatibleVerticalAxis(axis);
        }

    }

    /**
     * Checks the compatibility of a range axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis  the proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleRangeAxis(ValueAxis axis) {

        if (axis == null) {
            return true;
        }
        else {
            return isCompatibleHorizontalAxis(axis);
        }

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * At your option, you may supply an instance of ChartRenderingInfo.  If
     * you do, it will be populated with information about the drawing,
     * including various plot dimensions and tooltip info.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param info  a structure for passing back information about the
     *              chart drawing (ignored if null).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // set up the drawing info...
        if (info != null) {
            info.setPlotArea(plotArea);
        }

        // adjust the drawing area for the plot insets (if any)...
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        if ((plotArea.getWidth() >= MINIMUM_WIDTH_TO_DRAW)
            && (plotArea.getHeight() >= MINIMUM_HEIGHT_TO_DRAW)) {

            Rectangle2D axisArea = this.renderer.getAxisArea(plotArea);

            // estimate the area required for drawing the axes...
            VerticalAxis vAxis = (VerticalAxis) getDomainAxis();
            HorizontalAxis hAxis = (HorizontalAxis) getRangeAxis();
            double vAxisAreaWidth = vAxis.reserveWidth(g2, this, axisArea);
            Rectangle2D hAxisArea = hAxis.reserveAxisArea(g2, this, axisArea, vAxisAreaWidth);

            // and this the area available for plotting data...
            Rectangle2D dataArea
                = new Rectangle2D.Double(plotArea.getX() + vAxisAreaWidth,
                                         plotArea.getY(),
                                         plotArea.getWidth() - vAxisAreaWidth,
                                         plotArea.getHeight() - hAxisArea.getHeight());

            Rectangle2D axisDataArea
                = new Rectangle2D.Double(axisArea.getX() + vAxisAreaWidth,
                                         axisArea.getY(),
                                         axisArea.getWidth() - vAxisAreaWidth,
                                         axisArea.getHeight() - hAxisArea.getHeight());

            if (info != null) {
                info.setDataArea(dataArea);
            }

            Shape dataClipRegion = this.renderer.getDataClipRegion(dataArea);

            // draw the background and axes...
            if (renderer != null) {
                renderer.drawPlotBackground(g2, this, axisDataArea, dataClipRegion);
            }
            getDomainAxis().draw(g2, axisArea, axisDataArea);
            getRangeAxis().draw(g2, axisArea, axisDataArea);

            // draw the range markers...
            if ((this.rangeMarkers != null) && (renderer != null)) {
                Iterator<Marker> iterator = this.rangeMarkers.iterator();
                while (iterator.hasNext()) {
                    Marker marker = (Marker) iterator.next();
                    renderer.drawRangeMarker(g2, this, getRangeAxis(), marker,
                                             axisDataArea, dataClipRegion);
                }

                // now get the data and plot the data items...
                render(g2, axisDataArea, info, dataClipRegion);
            }

        }

    }

    /**
     * Draws a representation of the data within the dataArea region, using
     * the current renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     * @param backgroundPlotArea  the chart's background area.
     */
    @SuppressWarnings("unchecked")
		public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, Shape backgroundPlotArea) {

        CategoryDataset data = getCategoryDataset();
        if (data != null) {

            Shape savedClip = g2.getClip();
            g2.clip(dataArea);

            // set up the alpha-transparency...
            Composite originalComposite = g2.getComposite();
            Composite newComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                                this.foregroundAlpha);
            g2.setComposite(newComposite);

            // work out the span dimensions for the categories...
            int seriesCount = data.getSeriesCount();
            this.renderer.initialise(g2, dataArea, this, getRangeAxis(), data, info);

            // iterate through the categories...
            int categoryIndex = 0;
            Object previousCategory = null;
            Iterator iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {

                Object category = iterator.next();

                // loop through the series...
                for (int series = 0; series < seriesCount; series++) {

                    // draw the data item...
                    renderer.drawCategoryItem(g2, dataArea,
                                              this,
                                              this.getRangeAxis(),
                                              data, series,
                                              category, categoryIndex,
                                              previousCategory);

                }

                categoryIndex++;
                previousCategory = category;

            }

            // draw vertical crosshair if required...
            ValueAxis hva = this.getRangeAxis();
            if (hva.isCrosshairVisible()) {
                this.drawVerticalLine(g2, dataArea, hva.getCrosshairValue(),
                                      hva.getCrosshairStroke(),
                                      hva.getCrosshairPaint());
            }

            g2.setClip(savedClip);
            g2.setComposite(originalComposite);
        }

    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return plot type description.
     */
    public String getPlotType() {
        return "Horizontal Category Plot";
    }

    /**
     * Returns the data range for the horizontal axis.
     *
     * @return The range.
     */
    public Range getHorizontalDataRange() {

        Range result = null;

        CategoryDataset data = getCategoryDataset();
        if (data != null) {
            if ((this.renderer != null) && (this.renderer.isStacked())) {
                result = DatasetUtilities.getStackedRangeExtent(data);
            }
            else {
                result = DatasetUtilities.getRangeExtent(data);
            }
        }

        return result;

    }

    /**
     * Returns the horizontal axis.
     * <P>
     * This method supports the HorizontalValuePlot interface.
     *
     * @return the horizontal axis.
     */
    public ValueAxis getHorizontalValueAxis() {
        return this.rangeAxis;
    }

    /**
     * Handles a 'click' on the plot by updating the anchor values...
     *
     * @param x  x-coordinate of the click.
     * @param y  y-coordinate of the click.
     * @param info  an optional info collection object to return data back to the caller.
     *
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // set the anchor value for the horizontal axis...
        ValueAxis hva = getRangeAxis();
        double hvalue = hva.translateJava2DtoValue((float) x, info.getDataArea());
        hva.setAnchorValue(hvalue);
        hva.setCrosshairValue(hvalue);

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param value  the x-coordinate of the vertical crosshair line.
     * @param stroke  the stroke.
     * @param paint  the paint.
     */
    private void drawVerticalLine(Graphics2D g2,
                                  Rectangle2D dataArea, double value,
                                  Stroke stroke, Paint paint) {

        double xx = getRangeAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

}
