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
 * -----------
 * XYPlot.java
 * -----------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Craig MacFarlane;
 *                   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *
 * $Id: XYPlot.java,v 1.1.1.1 2003/07/17 10:06:29 Ian.Mayo Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Removed the code for drawing the visual representation of each data point into
 *               a separate class StandardXYItemRenderer.  This will make it easier to add
 *               variations to the way the charts are drawn.  Based on code contributed by
 *               Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 20-Nov-2001 : Fixed clipping bug that shows up when chart is displayed inside JScrollPane (DG);
 * 12-Dec-2001 : Removed unnecessary 'throws' clauses from constructor (DG);
 * 13-Dec-2001 : Added skeleton code for tooltips.  Added new constructor. (DG);
 * 16-Jan-2002 : Renamed the tooltips class (DG);
 * 22-Jan-2002 : Added DrawInfo class, incorporating tooltips and crosshairs.  Crosshairs based
 *               on code by Jonathan Nash (DG);
 * 05-Feb-2002 : Added alpha-transparency setting based on code by Sylvain Vieujot (DG);
 * 26-Feb-2002 : Updated getMinimumXXX() and getMaximumXXX() methods to handle special case when
 *               chart is null (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 * 28-Mar-2002 : The plot now registers with the renderer as a property change listener.  Also
 *               added a new constructor (DG);
 * 09-Apr-2002 : Removed the transRangeZero from the renderer.drawItem(...) method.  Moved the
 *               tooltip generator into the renderer (DG);
 * 23-Apr-2002 : Fixed bug in methods for drawing horizontal and vertical lines (DG);
 * 13-May-2002 : Small change to the draw(...) method so that it works for OverlaidXYPlot also (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 20-Aug-2002 : Renamed getItemRenderer() --> getRenderer(), and
 *               setXYItemRenderer() --> setRenderer() (DG);
 * 28-Aug-2002 : Added mechanism for (optional) plot annotations (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Iterator;
import com.jrefinery.legacy.chart.annotations.Annotation;
import com.jrefinery.legacy.chart.annotations.XYAnnotation;
import com.jrefinery.legacy.chart.event.PlotChangeEvent;
import com.jrefinery.legacy.data.DatasetChangeEvent;
import com.jrefinery.legacy.data.DatasetUtilities;
import com.jrefinery.legacy.data.Range;
import com.jrefinery.legacy.data.SeriesDataset;
import com.jrefinery.legacy.data.XYDataset;

/**
 * A general class for plotting data in the form of (x, y) pairs.  XYPlot can
 * use data from any class that implements the XYDataset interface
 * (in the com.jrefinery.data package).
 * <P>
 * XYPlot makes use of a renderer to draw each point on the plot.  By using
 * different renderers, various chart types can be produced.  The ChartFactory
 * class contains static methods for creating pre-configured charts.
 *
 * @see ChartFactory
 * @see Plot
 * @see XYDataset
 *
 * @author DG
 */
public class XYPlot extends Plot implements HorizontalValuePlot,
                                            VerticalValuePlot,
                                            PropertyChangeListener {

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** Object responsible for drawing the visual representation of each point on the plot. */
    private XYItemRenderer renderer;

    /** The parent plot (used only when this plot is part of a combined plot).*/
    private XYPlot parent;

    /** The weight for this plot in a combined plot. */
    private int weight;

    /** A list of markers (optional) for the domain axis. */
    private List<Marker> domainMarkers;

    /** A list of markers (optional) for the range axis. */
    private List<Marker> rangeMarkers;

    /** A list of annotations (optional) for the plot. */
    private List<Annotation> annotations;

    /**
     * Constructs an XYPlot with the specified axes (other attributes take default values).
     *
     * @param data  The dataset.
     * @param domainAxis  The domain axis.
     * @param rangeAxis  The range axis.
     */
    public XYPlot(XYDataset data, ValueAxis domainAxis, ValueAxis rangeAxis) {

        this(data,
             domainAxis, rangeAxis,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             new StandardXYItemRenderer()
             );

    }

    /**
     * Constructs an XYPlot with the specified axes and renderer (other
     * attributes take default values).
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param renderer  the renderer
     */
    public XYPlot(XYDataset data,
                  ValueAxis domainAxis,
                  ValueAxis rangeAxis,
                  XYItemRenderer renderer) {

        this(data,
             domainAxis, rangeAxis,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             renderer
             );

    }

    /**
     * Constructs a new XY plot.
     *
     * @param data  the dataset.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param insets  amount of blank space around the plot area.
     * @param backgroundPaint  an optional color for the plot's background.
     * @param backgroundImage  an optional image for the plot's background.
     * @param backgroundAlpha  alpha-transparency for the plot's background.
     * @param outlineStroke  the Stroke used to draw an outline around the plot.
     * @param outlinePaint  the color used to draw the plot outline.
     * @param alpha  the alpha-transparency.
     * @param renderer  the renderer.
     */
    public XYPlot(XYDataset data,
                  ValueAxis domainAxis,
                  ValueAxis rangeAxis,
                  Insets insets,
                  Paint backgroundPaint,
                  Image backgroundImage,
                  float backgroundAlpha,
                  Stroke outlineStroke,
                  Paint outlinePaint,
                  float alpha,
                  XYItemRenderer renderer) {

        super(data,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, alpha
              );

        this.parent = null;
        this.weight = 1;  // only relevant when this is a subplot
        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
            this.renderer.addPropertyChangeListener(this);
        }
        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }

    }

    /**
     * Returns the parent plot (or null if this plot is not part of a combined plot).
     *
     * @return The parent plot.
     */
    public Plot getParent() {
        return this.parent;
    }

    /**
     * Sets the parent plot.
     *
     * @param parent The parent plot.
     */
    public void setParent(XYPlot parent) {
        this.parent = parent;
    }

    /**
     * Returns true if this plot is part of a combined plot structure.
     *
     * @return <code>true</code> if this plot is part of a combined plot structure.
     */
    public boolean isSubplot() {
        return (this.parent != null);
    }

    /**
     * Returns the number of series in the dataset for this plot.
     *
     * @return The series count.
     */
    public int getSeriesCount() {

        int result = 0;

        SeriesDataset data = this.getXYDataset();
        if (data != null) {
            result = data.getSeriesCount();
        }

        return result;

    }
//
//    /**
//     * Returns an array of labels to be displayed by the legend.
//     *
//     * @return  An array of legend item labels (or null).
//     */
//    public List getLegendItemLabels() {
//
//        List result = new java.util.ArrayList();
//
//        SeriesDataset data = this.getXYDataset();
//        if (data != null) {
//            int seriesCount = data.getSeriesCount();
//            for (int i = 0; i < seriesCount; i++) {
//                result.add(data.getSeriesName(i));
//            }
//        }
//
//        return result;
//
//    }

    /**
     * Returns the legend items for the plot.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        SeriesDataset data = this.getXYDataset();
        if (data != null) {
            int seriesCount = data.getSeriesCount();
            for (int i = 0; i < seriesCount; i++) {
                LegendItem item = this.renderer.getLegendItem(i);
                result.add(item);
            }
        }

        return result;

    }

    /**
     * Returns the weight for this plot when it is used as a subplot within a
     * combined plot.
     *
     * @return  The weight.
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Sets the weight for the plot.
     *
     * @param weight  The weight.
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Returns the item renderer.
     *
     * @return The item renderer (possibly null).
     */
    public XYItemRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the item renderer, and notifies all listeners of a change to the plot.
     * <P>
     * If the renderer is set to null, no chart will be drawn.
     *
     * @param renderer  The new renderer (null permitted).
     */
    public void setRenderer(XYItemRenderer renderer) {

        boolean changed = false;

        if (this.renderer != null) {
            if (!this.renderer.equals(renderer)) {
                this.renderer.removePropertyChangeListener(this);
                this.renderer = renderer;
                changed = true;
            }
        }
        else {
            if (renderer != null) {
                this.renderer = renderer;
                changed = true;
            }
        }

        if (changed) {
            this.renderer.setPlot(this);
            this.notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * A convenience method that returns the dataset for the plot, cast as an
     * XYDataset.
     *
     * @return The dataset for the plot, cast as an XYDataset.
     */
    public XYDataset getXYDataset() {
        return (XYDataset) dataset;
    }

    /**
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return The domain axis.
     */
    public ValueAxis getDomainAxis() {

        ValueAxis result = domainAxis;

        if ((result == null) && (this.parent != null)) {
            result = parent.getDomainAxis();
        }

        return result;

    }

    /**
     * Sets the domain axis for the plot (this must be compatible with the plot
     * type or an exception is thrown).
     *
     * @param axis The new axis.
     *
     * @throws AxisNotCompatibleException if the axis is not compatible.
     */
    public void setDomainAxis(ValueAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleDomainAxis(axis)) {

            if (axis != null) {

                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "Plot.setDomainAxis(...): "
                        + "plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.domainAxis != null) {
                this.domainAxis.removeChangeListener(this);
            }

            this.domainAxis = axis;
            notifyListeners(new PlotChangeEvent(this));

        }
        else {
            throw new AxisNotCompatibleException(
                "Plot.setDomainAxis(...): axis not compatible with plot.");
        }

    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if
     * there is a parent plot).
     *
     * @return The range axis.
     */
    public ValueAxis getRangeAxis() {

        ValueAxis result = rangeAxis;

        if ((result == null) && (this.parent != null)) {
            result = parent.getRangeAxis();
        }

        return result;

    }

    /**
     * Sets the range axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually
     * compatible.
     *
     * @param axis The new axis (null permitted).
     *
     * @throws AxisNotCompatibleException if the axis is not compatible.
     */
    public void setRangeAxis(ValueAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleRangeAxis(axis)) {

            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "Plot.setRangeAxis(...): plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.rangeAxis != null) {
                this.rangeAxis.removeChangeListener(this);
            }

            this.rangeAxis = axis;
            notifyListeners(new PlotChangeEvent(this));

        }
        else {
            throw new AxisNotCompatibleException(
                "Plot.setRangeAxis(...): axis not compatible with plot.");
        }

    }

    /**
     * Adds a marker for the domain axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker the marker.
     */
    public void addDomainMarker(Marker marker) {

        if (this.domainMarkers == null) {
            this.domainMarkers = new java.util.ArrayList<Marker>();
        }
        this.domainMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the domain markers.
     */
    public void clearDomainMarkers() {
        if (this.domainMarkers != null) {
            this.domainMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Adds a marker for the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker The marker.
     */
    public void addRangeMarker(Marker marker) {

        if (this.rangeMarkers == null) {
            this.rangeMarkers = new java.util.ArrayList<Marker>();
        }
        this.rangeMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the range markers.
     */
    public void clearRangeMarkers() {
        if (this.rangeMarkers != null) {
            this.rangeMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Adds an annotation to the plot.
     *
     * @param annotation  the annotation.
     */
    public void addAnnotation(Annotation annotation) {

        if (this.annotations == null) {
            this.annotations = new java.util.ArrayList<Annotation>();
        }
        this.annotations.add(annotation);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the annotations.
     */
    public void clearAnnotations() {
        if (this.annotations != null) {
            this.annotations.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Checks the compatibility of a domain axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis The proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleDomainAxis(ValueAxis axis) {

        if (axis == null) {
            return true;
        }
        if (axis instanceof HorizontalAxis) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Checks the compatibility of a range axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis The proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleRangeAxis(ValueAxis axis) {

        if (axis == null) {
            return true;
        }
        if (axis instanceof VerticalAxis) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Draws the XY plot on a Java 2D graphics device (such as the screen or
     * a printer).
     * <P>
     * XYPlot relies on an XYItemRenderer to draw each item in the plot.  This
     * allows the visual representation of the data to be changed easily.
     * <P>
     * The optional info argument collects information about the rendering of
     * the plot (dimensions, tooltip information etc).  Just pass in null if
     * you do not need this information.
     *
     * @param g2  The graphics device.
     * @param plotArea  The area within which the plot (including axis
     *                  labels) should be drawn.
     * @param info  Collects chart drawing information (null permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // set up info collection...
        if (info != null) {
            info.setPlotArea(plotArea);

        }

        // adjust the drawing area for plot insets (if any)...
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        // estimate the area required for drawing the axes...
        double hAxisAreaHeight = 0;

        if (this.domainAxis != null) {
            HorizontalAxis hAxis = (HorizontalAxis) this.domainAxis;
            hAxisAreaHeight = hAxis.reserveHeight(g2, this, plotArea);
        }

        double vAxisWidth = 0;
        if (this.rangeAxis != null) {
            VerticalAxis vAxis = (VerticalAxis) this.rangeAxis;
            vAxisWidth = vAxis.reserveAxisArea(g2, this, plotArea, hAxisAreaHeight).getWidth();
        }

        // ...and therefore what is left for the plot itself...
        Rectangle2D dataArea = new Rectangle2D.Double(plotArea.getX() + vAxisWidth,
                                                      plotArea.getY(),
                                                      plotArea.getWidth() - vAxisWidth,
                                                      plotArea.getHeight() - hAxisAreaHeight);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        CrosshairInfo crosshairInfo = new CrosshairInfo();

        crosshairInfo.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairInfo.setAnchorX(getDomainAxis().getAnchorValue());
        crosshairInfo.setAnchorY(getRangeAxis().getAnchorValue());

        // draw the plot background and axes...
        drawOutlineAndBackground(g2, dataArea);

        if (this.domainAxis != null) {
            this.domainAxis.draw(g2, plotArea, dataArea);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.draw(g2, plotArea, dataArea);
        }

        if (renderer != null) {
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();

            g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.foregroundAlpha));

            if (this.domainMarkers != null) {
                Iterator<Marker> iterator = this.domainMarkers.iterator();
                while (iterator.hasNext()) {
                    Marker marker = (Marker) iterator.next();
                    renderer.drawDomainMarker(g2, this, getDomainAxis(), marker, dataArea);
                }
            }

            if (this.rangeMarkers != null) {
                Iterator<Marker> iterator = this.rangeMarkers.iterator();
                while (iterator.hasNext()) {
                    Marker marker = (Marker) iterator.next();
                    renderer.drawRangeMarker(g2, this, getRangeAxis(), marker, dataArea);
                }
            }

            render(g2, dataArea, info, crosshairInfo);

            // draw the annotations...
            if (this.annotations != null) {
                Iterator<Annotation> iterator = this.annotations.iterator();
                while (iterator.hasNext()) {
                    Annotation annotation = (Annotation) iterator.next();
                    if (annotation instanceof XYAnnotation) {
                        XYAnnotation xya = (XYAnnotation) annotation;
                        // get the annotation to draw itself...
                        xya.draw(g2, dataArea, getDomainAxis(), getRangeAxis());
                    }
                }
            }

            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
        }

    }

    /**
     * Draws a representation of the data within the dataArea region, using the
     * current renderer.
     * <P>
     * The <code>info</code> and <code>crosshairInfo</code> arguments may be <code>null</code>.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     * @param crosshairInfo  an optional object for collecting crosshair info.
     */
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, CrosshairInfo crosshairInfo) {

        // now get the data and plot it (the visual representation will depend
        // on the renderer that has been set)...
        XYDataset data = this.getXYDataset();
        if (data != null) {

            renderer.initialise(g2, dataArea, this, data, info);

            ValueAxis domainAxis1 = getDomainAxis();
            ValueAxis rangeAxis1 = getRangeAxis();
            int seriesCount = data.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = data.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    renderer.drawItem(g2, dataArea, info, this,
                                      domainAxis1, rangeAxis1,
                                      data, series, item,
                                      crosshairInfo);

                }
            }

            // draw vertical crosshair if required...
            domainAxis1.setCrosshairValue(crosshairInfo.getCrosshairX(), false);
            if (domainAxis1.isCrosshairVisible()) {
                drawVerticalLine(g2, dataArea,
                                 domainAxis1.getCrosshairValue(),
                                 domainAxis1.getCrosshairStroke(),
                                 domainAxis1.getCrosshairPaint());
            }

            // draw horizontal crosshair if required...
            rangeAxis1.setCrosshairValue(crosshairInfo.getCrosshairY(), false);
            if (rangeAxis1.isCrosshairVisible()) {
                drawHorizontalLine(g2, dataArea,
                                   rangeAxis1.getCrosshairValue(),
                                   rangeAxis1.getCrosshairStroke(),
                                   rangeAxis1.getCrosshairPaint());
            }

        }

    }

     /**
      * Utility method for drawing a crosshair on the chart (if required).
      *
      * @param g2  The graphics device.
      * @param dataArea  The data area.
      * @param value  The coordinate, where to draw the line.
      * @param stroke  The stroke to use.
      * @param paint  The paint to use.
      */
    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea,
                                    double value, Stroke stroke, Paint paint) {

        double xx = this.getDomainAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(xx, dataArea.getMinY(),
                                        xx, dataArea.getMaxY());
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

    /**
     * Utility method for drawing a crosshair on the chart (if required).
     *
     * @param g2  The graphics device.
     * @param dataArea  The data area.
     * @param value  The coordinate, where to draw the line.
     * @param stroke  The stroke to use.
     * @param paint  The paint to use.
     */
    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea,
                                      double value, Stroke stroke, Paint paint) {

        double yy = this.getRangeAxis().translateValueToJava2D(value, dataArea);
        Line2D line = new Line2D.Double(dataArea.getMinX(), yy,
                                        dataArea.getMaxX(), yy);
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);

    }

    /**
     * Handles a 'click' on the plot by updating the anchor values...
     *
     * @param x  x-coordinate, where the click occured.
     * @param y  y-coordinate, where the click occured.
     * @param info  An object for collection dimension information.
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // set the anchor value for the horizontal axis...
        ValueAxis hva = getDomainAxis();
        if (hva != null) {
            double hvalue = hva.translateJava2DtoValue((float) x, info.getDataArea());

            hva.setAnchorValue(hvalue);
            hva.setCrosshairValue(hvalue);
        }

        // set the anchor value for the vertical axis...
        ValueAxis vva = this.getRangeAxis();
        if (vva != null) {
            double vvalue = vva.translateJava2DtoValue((float) y, info.getDataArea());
            vva.setAnchorValue(vvalue);
            vva.setCrosshairValue(vvalue);
        }

    }

    /**
     * Zooms the axis ranges by the specified percentage about the anchor point.
     *
     * @param percent  The amount of the zoom.
     */
    public void zoom(double percent) {

        if (percent > 0) {
            ValueAxis domainAxis1 = getDomainAxis();
            double range = domainAxis1.getMaximumAxisValue() - domainAxis1.getMinimumAxisValue();
            double scaledRange = range * percent;
            domainAxis1.setAnchoredRange(scaledRange);

            ValueAxis rangeAxis1 = getRangeAxis();
            range = rangeAxis1.getMaximumAxisValue()
                - rangeAxis1.getMinimumAxisValue();
            scaledRange = range * percent;
            rangeAxis1.setAnchoredRange(scaledRange);
        }
        else {
            this.getRangeAxis().setAutoRange(true);
            this.getDomainAxis().setAutoRange(true);
        }

    }

    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    public String getPlotType() {
        return "XY Plot";
    }

    /**
     * Returns the range for the horizontal axis.
     *
     * @return The range for the horizontal axis.
     */
    public Range getHorizontalDataRange() {

        Range result = null;

        if (dataset != null) {
            result = DatasetUtilities.getDomainExtent(dataset);
        }

        return result;

    }

    /**
     * Returns the range for the vertical axis.
     *
     * @return The range for the vertical axis.
     */
    public Range getVerticalDataRange() {

        Range result = null;

        if (dataset != null) {
            result = DatasetUtilities.getRangeExtent(dataset);
        }

        return result;

    }

    /**
     * Notifies all registered listeners of a property change.
     * <P>
     * One source of property change events is the plot's renderer.
     *
     * @param event  Information about the property change.
     */
    public void propertyChange(PropertyChangeEvent event) {

        this.notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The chart reacts by passing on a chart change event to all registered
     * listeners.
     *
     * @param event  Information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {

        if (this.domainAxis != null) {
            this.domainAxis.configure();
        }

        if (this.rangeAxis != null) {
            this.rangeAxis.configure();
        }

        if (this.parent != null) {
            parent.datasetChanged(event);
        }
        else {
            PlotChangeEvent e = new PlotChangeEvent(this);
            notifyListeners(e);
        }

    }

    /**
     * Returns the horizontal axis.
     *
     * @return The horizontal axis.
     */
    public HorizontalAxis getHorizontalAxis() {
        return (HorizontalAxis) getDomainAxis();
    }

    /**
     * Returns the horizontal axis.
     * <P>
     * This method is part of the HorizontalValuePlot interface.
     *
     * @return The horizontal axis.
     */
    public ValueAxis getHorizontalValueAxis() {
        return getDomainAxis();
    }

    /**
     * Returns the vertical axis.
     *
     * @return The vertical axis.
     */
    public VerticalAxis getVerticalAxis() {
        return (VerticalAxis) getRangeAxis();
    }

    /**
     * Returns the vertical axis.
     * <P>
     * This method is part of the VerticalValuePlot interface.
     *
     * @return The vertical axis.
     */
    public ValueAxis getVerticalValueAxis() {
        return getRangeAxis();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DEPRECATED
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @return the item renderer.
     *
     * @deprecated Use getRenderer().
     */
    public XYItemRenderer getItemRenderer() {
        return this.getRenderer();
    }

    /**
     * @param renderer the renderer.
     *
     * @deprecated Use setRenderer(...).
     */
    public void setXYItemRenderer(XYItemRenderer renderer) {
        this.setRenderer(renderer);
    }

    /**
     * Adds a horizontal line at the specified data value, using the default color red.
     *
     * @param value The data value.
     *
     * @deprecated Use addRangeMarker(...).
     */
    public void addHorizontalLine(Number value) {
        addRangeMarker(new Marker(value.doubleValue()));

    }

    /**
     * Adds a horizontal line at the specified data value, using the specified color.
     *
     * @param location  The location.
     * @param color  The line color.
     *
     * @deprecated Use addRangeMarker(...).
     */
    public void addHorizontalLine(Number location, Paint color) {
        addRangeMarker(new Marker(location.doubleValue(), color, null, color, 1.0f));
    }

    /**
     * Adds a vertical line at location with default color blue.
     *
     * @param location  The location.
     *
     * @deprecated Use addDomainMarker(...).
     */
    public void addVerticalLine(Number location) {
        addDomainMarker(new Marker(location.doubleValue()));
    }

    /**
     * Adds a vertical of the given color at location with the given color.
     *
     * @param location  The location.
     * @param color  The color to use.
     *
     * @deprecated Use addDomainMarker(...).
     */
    public void addVerticalLine(Number location, Paint color) {
        addDomainMarker(new Marker(location.doubleValue(), color, null, color, 1.0f));
    }

}