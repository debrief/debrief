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
 * -------------------------
 * VerticalCategoryPlot.java
 * -------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Serge V. Grachov;
 *                   Jeremy Bowman;
 *
 * $Id: VerticalCategoryPlot.java,v 1.1.1.1 2003/07/17 10:06:28 Ian.Mayo Exp $
 *
 * Changes (from 21-Jun-2001):
 * ---------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved series paint and stroke attributes from JFreeChart.java to Plot.java (DG);
 *               Added new VerticalBarRenderer class (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 31-Oct-2001 : Debugging for gap settings (DG);
 *               Amendments by Serge V. Grachov to support 3D-effect bar plots (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 * 12-Dec-2001 : Removed redundant 'throws' clause from constructor (DG);
 * 13-Dec-2001 : Added tooltips, tidied up default values in constructor (DG);
 * 16-Jan-2002 : Renamed tooltips class (DG);
 * 06-Feb-2002 : Added optional background image and alpha-transparency to Plot and subclasses (DG);
 * 13-Feb-2002 : Renamed getCategoryAxis() --> getDomainAxis() (DG);
 * 15-Feb-2002 : Modified getMaximumVerticalDataValue() and getMinimumVerticalDataValue() to handle
 *               stacked plots (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 * 14-Mar-2002 : Renamed VerticalBarPlot.java --> VerticalCategoryPlot.java (DG);
 * 16-Apr-2002 : Added one line to set paint to gray before drawing baseline (DG);
 * 23-Apr-2002 : Added getVerticalRange() method (DG);
 * 29-Apr-2002 : Added getVerticalValueAxis() method (DG);
 * 11-May-2002 : Abstracted render() from draw() and added axis compatibility methods to simplify
 *               OverlaidVerticalCategoryPlot implementation (JB);
 * 30-May-2002 : Reorganised renderers to improve display of 3D bar charts (DG);
 * 06-Jun-2002 : Removed the tool tip generator which is now stored by the renderer (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 26-Jun-2002 : Added axis to initialise(...) method call (DG);
 * 19-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DatasetUtilities;
import com.jrefinery.data.Range;

/**
 * A general class for plotting vertical category charts (bars/lines/shapes),
 * using data from any class that implements the CategoryDataset interface.
 * <P>
 * The plot relies on a renderer to draw the individual data items, giving some
 * flexibility to change the visual representation of the data.
 *
 * @see Plot
 * @see CategoryItemRenderer
 *
 * @author DG
 */
public class VerticalCategoryPlot extends CategoryPlot implements VerticalValuePlot {

    /**
     * Constructs a new vertical category plot.
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the renderer for the data.
     *
     */
    public VerticalCategoryPlot(CategoryDataset data,
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
     * Constructs a new vertical category plot.
     *
     * @param data  the dataset.
     * @param domainAxis  the horizontal axis.
     * @param rangeAxis  the vertical axis.
     * @param renderer  the renderer for the data.
     * @param insets  the amount of space to leave blank around the edges of the plot.
     * @param backgroundPaint  an optional color for the plot's background.
     * @param backgroundImage  an optional image for the plot's background.
     * @param backgroundAlpha  alpha-transparency for the plot's background.
     * @param outlineStroke  the Stroke used to draw an outline around the plot.
     * @param outlinePaint  the color used to draw an outline around the plot.
     * @param foregroundAlpha  the alpha-transparency for the plot.
     * @param introGapPercent  the gap before the first bar in the plot, as a percentage of the
     *                         available drawing space.
     * @param trailGapPercent  the gap after the last bar in the plot, as a percentage of the
     *                         available drawing space.
     * @param categoryGapPercent  the percentage of drawing space allocated to the gap between
     *                            the last bar in one category and the first bar in the next
     *                            category.
     * @param itemGapPercent  the gap between bars within the same category.
     *
     */
    public VerticalCategoryPlot(CategoryDataset data,
                                CategoryAxis domainAxis,
                                ValueAxis rangeAxis,
                                CategoryItemRenderer renderer,
                                Insets insets,
                                Paint backgroundPaint,
                                Image backgroundImage,
                                float backgroundAlpha,
                                Stroke outlineStroke,
                                Paint outlinePaint,
                                float foregroundAlpha,
                                double introGapPercent,
                                double trailGapPercent,
                                double categoryGapPercent,
                                double itemGapPercent) {

        super(data,
              domainAxis, rangeAxis, renderer,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint,
              foregroundAlpha,
              introGapPercent, trailGapPercent, categoryGapPercent,
              itemGapPercent);

    }

    /**
     * A convenience method that returns the dataset for the plot, cast as a
     * CategoryDataset.
     *
     * @return the category dataset.
     *
     */
    public CategoryDataset getCategoryDataset() {

        return (CategoryDataset) dataset;

    }

    /**
     * Returns the vertical value axis.
     * <P>
     * This method supports the VerticalValuePlot interface.
     *
     * @return the vertical axis.
     *
     */
    public ValueAxis getVerticalValueAxis() {
        return this.rangeAxis;
    }

    /**
     * Returns the x-coordinate (in Java 2D User Space) of the center of the
     * specified category.
     *
     * @param category  the category (zero based index).
     * @param area  the region within which the plot will be drawn.
     *
     * @return the x-coordinate of the center of the specified category.
     */
    public double getCategoryCoordinate(int category, Rectangle2D area) {

        // calculate first part of result...
        double result = area.getX() + (area.getWidth() * introGapPercent);

        // then add some depending on how many categories...
        int categoryCount = getCategoryDataset().getCategoryCount();
        if (categoryCount > 1) {

            double categorySpan = area.getWidth()
                                  * (1 - introGapPercent - trailGapPercent - categoryGapsPercent);
            double categoryGapSpan = area.getWidth() * categoryGapsPercent;
            result = result + (category + 0.5) * (categorySpan / categoryCount)
                            + (category) * (categoryGapSpan / (categoryCount - 1));
        }
        else {
            result = result + (category + 0.5) * area.getWidth()
                            * (1 - introGapPercent - trailGapPercent);
        }

        return result;

    }

    /**
     * Checks the compatibility of a horizontal axis, returning true if the
     * axis is compatible with the plot, and false otherwise.
     *
     * @param axis  the horizontal axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
        if (axis instanceof CategoryAxis) {
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
        if (axis instanceof VerticalNumberAxis) {
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
     * @param axis the proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleDomainAxis(CategoryAxis axis) {
        if (axis == null) {
            return true;
        }
        else {
            return isCompatibleHorizontalAxis(axis);
        }
    }

    /**
     * Checks the compatibility of a range axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis  the proposed axis.
     *
     * @return true if the axis is compatible with the plot.
     */
    public boolean isCompatibleRangeAxis(ValueAxis axis) {
        if (axis == null) {
            return true;
        }
        else {
            return isCompatibleVerticalAxis(axis);
        }
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * At your option, you may supply an instance of ChartRenderingInfo.
     * If you do, it will be populated with information about the drawing,
     * including various plot dimensions and tooltip info.
     *
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param info  collects info as the chart is drawn.
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

            Rectangle2D axisArea = plotArea;
            if (renderer != null) {
                axisArea = this.renderer.getAxisArea(plotArea);
            }

            // estimate the area required for drawing the axes...
            double hAxisAreaHeight = 0;
            if (this.domainAxis != null) {
                HorizontalAxis hAxis = (HorizontalAxis) this.domainAxis;
                hAxisAreaHeight = hAxis.reserveHeight(g2, this, axisArea);
            }

            double vAxisWidth = 0;
            if (this.rangeAxis != null) {
                VerticalAxis vAxis = (VerticalAxis) this.rangeAxis;
                vAxisWidth = vAxis.reserveAxisArea(g2, this, axisArea, hAxisAreaHeight).getWidth();
            }

            // and thus the area available for plotting...
            Rectangle2D dataArea =
                new Rectangle2D.Double(plotArea.getX() + vAxisWidth,
                    plotArea.getY(),
                    plotArea.getWidth() - vAxisWidth,
                    plotArea.getHeight() - hAxisAreaHeight);

            Rectangle2D axisDataArea =
                new Rectangle2D.Double(axisArea.getX() + vAxisWidth,
                    axisArea.getY(),
                    axisArea.getWidth() - vAxisWidth,
                    axisArea.getHeight() - hAxisAreaHeight);

            if (info != null) {
                info.setDataArea(dataArea);
            }

            Shape dataClipRegion = dataArea;
            if (this.renderer != null) {
                dataClipRegion = this.renderer.getDataClipRegion(dataArea);

                // draw the background...
                renderer.drawPlotBackground(g2, this, axisDataArea, dataClipRegion);
            }

            getDomainAxis().draw(g2, axisArea, axisDataArea);
            getRangeAxis().draw(g2, axisArea, axisDataArea);

            // draw the range markers, if there are any...
            if ((this.rangeMarkers != null) && (this.renderer != null)) {
                Iterator<Marker> iterator = this.rangeMarkers.iterator();
                while (iterator.hasNext()) {
                    Marker marker = (Marker) iterator.next();
                    renderer.drawRangeMarker(g2, this, getRangeAxis(), marker,
                                             axisDataArea, dataClipRegion);
                }
            }
            render(g2, axisDataArea, info, dataClipRegion);

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
     *
     */
    @SuppressWarnings("unchecked")
		public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, Shape backgroundPlotArea) {

        CategoryDataset data = getCategoryDataset();
        if (data != null) {
            Shape savedClip = g2.getClip();
            g2.clip(backgroundPlotArea);

            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.foregroundAlpha));

            int seriesCount = data.getSeriesCount();
            this.renderer.initialise(g2, dataArea, this, getRangeAxis(), data, info);
            int categoryIndex = 0;
            Object previousCategory = null;
            Iterator iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {

                Object category = iterator.next();
                for (int series = 0; series < seriesCount; series++) {
                    renderer.drawCategoryItem(g2,
                                              dataArea, this,
                                              getRangeAxis(),
                                              data, series,
                                              category, categoryIndex,
                                              previousCategory);

                }
                categoryIndex++;
                previousCategory = category;

            }

            // draw horizontal crosshair if required...
            ValueAxis vva = this.getRangeAxis();
            if (vva.isCrosshairVisible()) {
                this.drawHorizontalLine(g2, dataArea, vva.getCrosshairValue(),
                                        vva.getCrosshairStroke(),
                                        vva.getCrosshairPaint());
            }

            g2.setClip(savedClip);
            g2.setComposite(originalComposite);

        }

    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return a description.
     */
    public String getPlotType() {
        return "Vertical Category Plot";
    }

    /**
     * Returns the range of data values that will be plotted against the range
     * axis.
     * <P>
     * If the dataset is null, this method returns null.
     *
     * @return the data range.
     */
    public Range getVerticalDataRange() {

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
     * Returns the minimum value in the range (since this is plotted against
     * the vertical axis by VerticalBarPlot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return the minimum value.
     */
    public Number getMinimumVerticalDataValue() {

        Number result = null;

        CategoryDataset data = getCategoryDataset();
        if (data != null) {
            if (this.renderer.isStacked()) {
                result = DatasetUtilities.getMinimumStackedRangeValue(data);
            }
            else {
                result = DatasetUtilities.getMinimumRangeValue(data);
            }
        }

        return result;

    }

    /**
     * Returns the maximum value in the range (since the range values are
     * plotted against the vertical axis by this plot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return the maximum value.
     */
    public Number getMaximumVerticalDataValue() {

        Number result = null;

        CategoryDataset data = getCategoryDataset();
        if (data != null) {
            if (this.renderer.isStacked()) {
                result = DatasetUtilities.getMaximumStackedRangeValue(data);
            }
            else {
                result = DatasetUtilities.getMaximumRangeValue(data);
            }
        }

        return result;

    }

    /**
     * Handles a 'click' on the plot by updating the anchor values.
     *
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     * @param info  the dimensions of the plot.
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // set the anchor value for the horizontal axis...
        ValueAxis vva = this.getRangeAxis();
        double vvalue = vva.translateJava2DtoValue((float) y, info.getDataArea());
        vva.setAnchorValue(vvalue);
        vva.setCrosshairValue(vvalue);

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param value  the vertical data value.
     * @param stroke  the line stroke.
     * @param paint  the line paint.
     */
    private void drawHorizontalLine(Graphics2D g2,
                                    Rectangle2D dataArea,
                                    double value, Stroke stroke, Paint paint) {

        double yy = this.getRangeAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

}
