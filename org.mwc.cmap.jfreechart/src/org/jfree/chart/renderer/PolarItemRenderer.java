/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2021, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ----------------------
 * PolarItemRenderer.java
 * ----------------------
 * (C) Copyright 2004-2021, by Solution Engineering, Inc. and Contributors.
 *
 * Original Author:  Daniel Bridenbecker, Solution Engineering, Inc.;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 */

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;

/**
 * The interface for a renderer that can be used by the {@link PolarPlot} 
 * class.
 */
public interface PolarItemRenderer {

    /**
     * Plots the data for a given series.
     *
     * @param g2  the drawing surface.
     * @param dataArea  the data area.
     * @param info  collects plot rendering info.
     * @param plot  the plot.
     * @param dataset  the dataset.
     * @param seriesIndex  the series index.
     */
    public void drawSeries(Graphics2D g2, Rectangle2D dataArea,
            PlotRenderingInfo info, PolarPlot plot, XYDataset dataset,
            int seriesIndex);

    /**
     * Draw the angular gridlines - the spokes.
     *
     * @param g2  the drawing surface.
     * @param plot  the plot.
     * @param ticks  the ticks.
     * @param dataArea  the data area.
     */
    public void drawAngularGridLines(Graphics2D g2, PolarPlot plot,
            List ticks, Rectangle2D dataArea);

    /**
     * Draw the radial gridlines - the rings.
     *
     * @param g2  the drawing surface.
     * @param plot  the plot.
     * @param radialAxis  the radial axis.
     * @param ticks  the ticks.
     * @param dataArea  the data area.
     */
    public void drawRadialGridLines(Graphics2D g2, PolarPlot plot,
            ValueAxis radialAxis, List ticks, Rectangle2D dataArea);

    /**
     * Return the legend for the given series.
     *
     * @param series  the series index.
     *
     * @return The legend item.
     */
    public LegendItem getLegendItem(int series);

    /**
     * Returns the plot that this renderer has been assigned to.
     *
     * @return The plot.
     */
    public PolarPlot getPlot();

    /**
     * Sets the plot that this renderer is assigned to.  This method will be
     * called by the plot class...you do not need to call it yourself.
     *
     * @param plot  the plot.
     */
    public void setPlot(PolarPlot plot);

    /**
     * Adds a change listener.
     *
     * @param listener  the listener.
     */
    public void addChangeListener(RendererChangeListener listener);

    /**
     * Removes a change listener.
     *
     * @param listener  the listener.
     */
    public void removeChangeListener(RendererChangeListener listener);


    //// TOOL TIP GENERATOR ///////////////////////////////////////////////////

    /**
     * Returns the tool tip generator for a data item.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The generator (possibly {@code null}).
     */
    public XYToolTipGenerator getToolTipGenerator(int row, int column);

    /**
     * Returns the tool tip generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setSeriesToolTipGenerator(int, XYToolTipGenerator)
     */
    public XYToolTipGenerator getSeriesToolTipGenerator(int series);

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    public void setSeriesToolTipGenerator(int series,
                                          XYToolTipGenerator generator);

    /**
     * Returns the base tool tip generator.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setBaseToolTipGenerator(XYToolTipGenerator)
     */
    public XYToolTipGenerator getBaseToolTipGenerator();

    /**
     * Sets the base tool tip generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getBaseToolTipGenerator()
     */
    public void setBaseToolTipGenerator(XYToolTipGenerator generator);


    //// URL GENERATOR ////////////////////////////////////////////////////////

    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return The URL generator (possibly null).
     */
    public XYURLGenerator getURLGenerator();

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator the URL generator (null permitted).
     */
    public void setURLGenerator(XYURLGenerator urlGenerator);

}
