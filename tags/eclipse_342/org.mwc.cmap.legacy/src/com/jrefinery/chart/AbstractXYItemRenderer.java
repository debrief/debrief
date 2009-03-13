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
 * ---------------------------
 * AbstractXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: AbstractXYItemRenderer.java,v 1.1.1.1 2003/07/17 10:06:19 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 15-Mar-2002 : Version 1 (DG);
 * 09-Apr-2002 : Added a getToolTipGenerator() method reflecting the change in the XYItemRenderer
 *               interface (DG);
 * 05-Aug-2002 : Added a urlGenerator member variable to support HTML image maps (RA);
 * 20-Aug-2002 : Added property change events for the tooltip and URL generators (DG);
 * 22-Aug-2002 : Moved property change support into AbstractRenderer class (DG);
 * 23-Sep-2002 : Fixed errors reported by Checkstyle tool (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.Range;

/**
 * A base class that can be used to create new XYItemRenderer implementations.
 *
 * @author DG
 */
public abstract class AbstractXYItemRenderer extends AbstractRenderer implements XYItemRenderer {

    /** The plot that the renderer is assigned to. */
    private XYPlot plot;

    /** The tool tip generator. */
    private XYToolTipGenerator toolTipGenerator;

    /** The URL text generator. */
    private XYURLGenerator urlGenerator;

    /**
     * Default constructor.
     */
    protected AbstractXYItemRenderer() {
        this(null, null);
    }

    /**
     * Creates a renderer with the specified tooltip generator.
     * <P>
     * Storage is allocated for property change listeners.
     *
     * @param toolTipGenerator  the tooltip generator (null permitted).
     */
    protected AbstractXYItemRenderer(XYToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Creates a renderer with the specified tooltip generator.
     * <P>
     * Storage is allocated for property change listeners.
     *
     * @param urlGenerator  the URL generator (null permitted).
     */
    protected AbstractXYItemRenderer(XYURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Creates a renderer with the specified tooltip generator and URL generator.
     * <P>
     * Storage is allocated for property change listeners.
     *
     * @param toolTipGenerator  the tooltip generator (null permitted).
     * @param urlGenerator  the URL generator (null permitted).
     */
    protected AbstractXYItemRenderer(XYToolTipGenerator toolTipGenerator,
                                     XYURLGenerator urlGenerator) {

        this.toolTipGenerator = toolTipGenerator;
        this.urlGenerator = urlGenerator;

    }

    /**
     * Returns the plot that this renderer has been assigned to.
     *
     * @return the plot.
     */
    public XYPlot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot that this renderer has been assigned to.
     *
     * @param plot  the plot.
     */
    public void setPlot(XYPlot plot) {
        this.plot = plot;
    }
    /**
     * Initialises the renderer.
     * <P>
     * This method will be called before the first item is rendered, giving the
     * renderer an opportunity to initialise any state information it wants to maintain.
     * The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot1  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to the caller.
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           XYPlot plot1,
                           XYDataset data,
                           ChartRenderingInfo info) {

        setInfo(info);

    }

    /**
     * Returns the tool tip generator.
     *
     * @return the tool tip generator (possibly null).
     */
    public XYToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator.
     *
     * @param generator  the tool tip generator (null permitted).
     */
    public void setToolTipGenerator(XYToolTipGenerator generator) {

        Object oldValue = this.toolTipGenerator;
        this.toolTipGenerator = generator;
        firePropertyChanged("renderer.ToolTipGenerator", oldValue, generator);

    }

    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return the URL generator (possibly null).
     */
    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator  the URL generator (null permitted).
     */
    public void setURLGenerator(XYURLGenerator urlGenerator) {

        Object oldValue = this.urlGenerator;
        this.urlGenerator = urlGenerator;
        firePropertyChanged("renderer.URLGenerator", oldValue, urlGenerator);

    }

    /**
     * Returns a legend item for a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return a legend item for the series.
     */
    public LegendItem getLegendItem(int series) {

        if (this.plot == null) {
            return null;
        }

        XYDataset dataset = this.plot.getXYDataset();
        String label = dataset.getSeriesName(series);
        String description = label;
        Shape shape = null;
        Paint paint = plot.getSeriesPaint(series);
        Paint outlinePaint = plot.getSeriesOutlinePaint(series);
        Stroke stroke = plot.getSeriesStroke(series);
        Stroke outlineStroke = plot.getSeriesOutlineStroke(series);

        return new LegendItem(label, description,
                              shape, paint, outlinePaint, stroke, outlineStroke);

    }

    /**
     * Draws a vertical line on the chart to represent a 'range marker'.
     *
     * @param g2  the graphics device.
     * @param plot1  the plot.
     * @param domainAxis  the domain axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawDomainMarker(Graphics2D g2,
                                 XYPlot plot1,
                                 ValueAxis domainAxis,
                                 Marker marker,
                                 Rectangle2D dataArea) {

        double value = marker.getValue();
        Range range = domainAxis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double x = domainAxis.translateValueToJava2D(marker.getValue(), dataArea);
        Line2D line = new Line2D.Double(x, dataArea.getMinY(), x, dataArea.getMaxY());
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke != null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

    /**
     * Draws a horizontal line across the chart to represent a 'range marker'.
     *
     * @param g2  the graphics device.
     * @param plot1  the plot.
     * @param rangeAxis  the range axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                XYPlot plot1,
                                ValueAxis rangeAxis,
                                Marker marker,
                                Rectangle2D dataArea) {

        double value = marker.getValue();
        Range range = rangeAxis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double y = rangeAxis.translateValueToJava2D(marker.getValue(), dataArea);
        Line2D line = new Line2D.Double(dataArea.getMinX(), y, dataArea.getMaxX(), y);
        Paint paint = marker.getOutlinePaint();
        Stroke stroke = marker.getOutlineStroke();
        g2.setPaint(paint != null ? paint : Plot.DEFAULT_OUTLINE_PAINT);
        g2.setStroke(stroke == null ? stroke : Plot.DEFAULT_OUTLINE_STROKE);
        g2.draw(line);

    }

}
