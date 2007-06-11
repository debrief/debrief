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
 * StandardXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Andreas Schneider;
 *                   Norbert Kiesel (for TBD Networks);
 *
 * $Id: StandardXYItemRenderer.java,v 1.1.1.1 2003/07/17 10:06:27 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 19-Oct-2001 : Version 1, based on code by Mark Watson (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 21-Dec-2001 : Added working line instance to improve performance (DG);
 * 22-Jan-2002 : Added code to lock crosshairs to data points.  Based on code by Jonathan Nash (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that the renderer no longer needs to
 *               be immutable (DG);
 * 02-Apr-2002 : Modified to handle null values (DG);
 * 09-Apr-2002 : Modified draw method to return void.  Removed the translated zero from the
 *               drawItem method.  Override the initialise() method to calculate it (DG);
 * 13-May-2002 : Added code from Andreas Schneider to allow changing shapes/colors per item (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 05-Aug-2002 : Incorporated URLs for HTML image maps into chart entities (RA);
 * 08-Aug-2002 : Added discontinuous lines option contributed by Norbert Kiesel (DG);
 * 20-Aug-2002 : Added user definable default values to be returned by protected methods unless
 *               overridden by a subclass (DG);
 * 23-Sep-2002 : Updated for changes in the XYItemRenderer interface (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.XYDataset;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardXYToolTipGenerator;
import com.jrefinery.chart.urls.XYURLGenerator;

/**
 * Standard item renderer for an XYPlot.  This class can draw (a) shapes at
 * each point, or (b) lines between points, or (c) both shapes and lines.
 *
 * @author DG
 */
public class StandardXYItemRenderer extends AbstractXYItemRenderer
                                    implements XYItemRenderer {

    /** Cconstant for the type of rendering (shapes only). */
    public static final int SHAPES = 1;

    /** Constant for the type of rendering (lines only). */
    public static final int LINES = 2;

    /** Constant for the type of rendering (shapes and lines). */
    public static final int SHAPES_AND_LINES = SHAPES | LINES;

    /** Constant for the type of rendering (images only). */
    public static final int IMAGES = 4;

    /** Constant for the type of rendering (discontinuous lines). */
    public static final int DISCONTINUOUS = 8;

    /** Constant for the type of rendering (discontinuous lines). */
    public static final int DISCONTINUOUS_LINES = LINES | DISCONTINUOUS;

    /** A flag indicating whether or not shapes are drawn at each XY point. */
    private boolean plotShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean plotLines;

    /** A flag indicating whether or not images are drawn between XY points. */
    private boolean plotImages;

    /** A flag controlling whether or not discontinuous lines are used. */
    private boolean plotDiscontinuous;

    /** Threshold for deciding when to discontinue a line. */
    private double gapThreshold = 1.0;

    /** Scale factor for standard shapes. */
    private double defaultShapeScale;

    /** The default value returned by the getShapeFilled(...) method. */
    private boolean defaultShapeFilled;

    /** A working line (to save creating thousands of instances). */
    private Line2D line;

    /**
     * Constructs a new renderer.
     */
    public StandardXYItemRenderer() {

        this(LINES, new StandardXYToolTipGenerator());

    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES
     * or SHAPES_AND_LINES.
     *
     * @param  type the type.
     */
    public StandardXYItemRenderer(int type) {
        this(type, new StandardXYToolTipGenerator());
    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES
     * or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * @param toolTipGenerator  the tooltip generator.
     */
    public StandardXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator) {

        this(type, toolTipGenerator, null);

    }

    /**
     * Constructs a new renderer.
     * <p>
     * To specify the type of renderer, use one of the constants: SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type  the type of renderer.
     * @param toolTipGenerator  the tooltip generator.
     * @param urlGenerator  the URL generator.
     */
    public StandardXYItemRenderer(int type,
                                  XYToolTipGenerator toolTipGenerator,
                                  XYURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);
        if ((type & SHAPES) != 0) {
            this.plotShapes = true;
        }
        if ((type & LINES) != 0) {
            this.plotLines = true;
        }
        if ((type & IMAGES) != 0) {
            this.plotImages = true;
        }
        if ((type & DISCONTINUOUS) != 0) {
            this.plotDiscontinuous = true;
        }
        this.line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);
        this.defaultShapeScale = 6.0;

    }

    /**
     * Returns true if shapes are being plotted by the renderer.
     *
     * @return <code>true</code> if shapes are being plotted by the renderer.
     */
    public boolean getPlotShapes() {
        return this.plotShapes;
    }

    /**
     * Sets the flag that controls whether or not a shape is plotted at each data point.
     *
     * @param flag  the flag.
     */
    public void setPlotShapes(boolean flag) {
        Object oldValue = new Boolean(flag);
        if (this.plotShapes != flag) {
            this.plotShapes = flag;
            firePropertyChanged("renderer.PlotShapes", oldValue, new Boolean(flag));
        }
    }

    /**
     * Returns the default shape scale.
     * <P>
     * The renderer will call getShapeScale(...) to determine the scaling factor to use for shapes.
     * Unless overridden, the getShapeScale(...) method just returns the default shape scale.
     *
     * @return the default shape scale.
     */
    public double getDefaultShapeScale() {
        return this.defaultShapeScale;
    }

    /**
     * Sets the default shape scale.
     *
     * @param scale  the shape scale.
     */
    public void setDefaultShapeScale(double scale) {

        Object oldValue = new Double(this.defaultShapeScale);
        this.defaultShapeScale = scale;
        firePropertyChanged("renderer.DefaultShapeScale", oldValue, new Double(scale));

    }

    /**
     * Returns the default value returned by the isShapeFilled(...) method.
     * <P>
     * The renderer will call isShapeFilled(...) to determine whether or not to fill a shape.
     * Unless overridden, the isShapeFilled(...) method just returns the default shape filled flag.
     *
     * @return the flag.
     */
    public boolean getDefaultShapeFilled() {
        return this.defaultShapeFilled;
    }

    /**
     * Sets the default shape filled flag.
     *
     * @param flag  the flag.
     */
    public void setDefaultShapeFilled(boolean flag) {

        Object oldValue = new Boolean(this.defaultShapeFilled);
        this.defaultShapeFilled = flag;
        firePropertyChanged("renderer.DefaultShapeFilled", oldValue, new Boolean(flag));

    }

    /**
     * Returns true if lines are being plotted by the renderer.
     *
     * @return <code>true</code> if lines are being plotted by the renderer.
     */
    public boolean getPlotLines() {
        return this.plotLines;
    }

    /**
     * Sets the flag that controls whether or not a line is plotted between each data point.
     *
     * @param flag  the flag.
     */
    public void setPlotLines(boolean flag) {
        Object oldValue = new Boolean(flag);
        if (this.plotLines != flag) {
            this.plotLines = flag;
            firePropertyChanged("renderer.PlotLines", oldValue, new Boolean(flag));
        }
    }

    /**
     * Returns the gap threshold for discontinuous lines.
     *
     * @return the gap threshold.
     */
    public double getGapThreshold() {
        return this.gapThreshold;
    }

    /**
     * Sets the gap threshold for discontinuous lines.
     *
     * @param t  the threshold.
     */
    public void setGapThreshold(double t) {
        Object oldValue = new Double(this.gapThreshold);
        this.gapThreshold = t;
        firePropertyChanged("renderer.GapThreshold", oldValue, new Double(t));
    }

    /**
     * Returns true if images are being plotted by the renderer.
     *
     * @return <code>true</code> if images are being plotted by the renderer.
     */
    public boolean getPlotImages() {
        return this.plotImages;
    }

    /**
     * Sets the flag that controls whether or not an image is drawn at each data point.
     *
     * @param flag  the flag.
     */
    public void setPlotImages(boolean flag) {
        Object oldValue = new Boolean(flag);
        if (this.plotImages != flag) {
            this.plotImages = flag;
            firePropertyChanged("renderer.PlotImages", oldValue, new Boolean(flag));
        }
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param data  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairInfo  information about crosshairs on a plot.
     */
    public void drawItem(Graphics2D g2,
                         Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset data,
                         int series,
                         int item,
                         CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        Number x1n = data.getXValue(series, item);
        Number y1n = data.getYValue(series, item);
        if (y1n != null) {
            double x1 = x1n.doubleValue();
            double y1 = y1n.doubleValue();
            double transX1 = domainAxis.translateValueToJava2D(x1, dataArea);
            double transY1 = rangeAxis.translateValueToJava2D(y1, dataArea);

            Paint paint = getPaint(plot, series, item, transX1, transY1);
            if (paint != null) {
                g2.setPaint(paint);
            }

            if (this.plotLines) {

                if (item > 0) {
                    // get the previous data point...
                    Number x0n = data.getXValue(series, item - 1);
                    Number y0n = data.getYValue(series, item - 1);
                    if (y0n != null) {
                        double x0 = x0n.doubleValue();
                        double y0 = y0n.doubleValue();
                        boolean drawLine = true;
                        if (this.plotDiscontinuous) {
                            // only draw a line if the gap between the current and previous data
                            // point is within the threshold
                            int numX = data.getItemCount(series);
                            double minX = data.getXValue(series, 0).doubleValue();
                            double maxX = data.getXValue(series, numX - 1).doubleValue();
                            drawLine = (x1 - x0) <= ((maxX - minX) / numX * this.gapThreshold);
                        }
                        if (drawLine) {
                            double transX0 = domainAxis.translateValueToJava2D(x0, dataArea);
                            double transY0 = rangeAxis.translateValueToJava2D(y0, dataArea);

                            line.setLine(transX0, transY0, transX1, transY1);
                            if (line.intersects(dataArea)) {
                                g2.draw(line);
                            }
                        }
                    }
                }
            }

            if (this.plotShapes) {

                double scale = getShapeScale(plot, series, item, transX1, transY1);
                Shape shape = getShape(plot, series, item, transX1, transY1, scale);
                if (shape.intersects(dataArea)) {
                    if (isShapeFilled(plot, series, item, transX1, transY1)) {
                        g2.fill(shape);
                    }
                    else {
                        g2.draw(shape);
                    }
                }
                entityArea = shape;

            }

            if (this.plotImages) {
                // use shape scale with transform??
                double scale = getShapeScale(plot, series, item, transX1, transY1);
                Image image = getImage(plot, series, item, transX1, transY1);
                if (image != null) {
                    Point hotspot = getImageHotspot(plot, series, item, transX1, transY1, image);
                    g2.drawImage(image,
                                 (int) (transX1 - hotspot.getX()),
                                 (int) (transY1 - hotspot.getY()), (ImageObserver) null);
                }
                // tooltipArea = image; not sure how to handle this yet
            }

            // add an entity for the item...
            if (entities != null) {
                if (entityArea == null) {
                    entityArea = new Rectangle2D.Double(transX1 - 2, transY1 - 2, 4, 4);
                }
                String tip = "";
                if (getToolTipGenerator() != null) {
                    tip = getToolTipGenerator().generateToolTip(data, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(data, series, item);
                }
                XYItemEntity entity = new XYItemEntity(entityArea, tip, url, series, item);
                entities.addEntity(entity);
            }

            // do we need to update the crosshair values?
            if (domainAxis.isCrosshairLockedOnData()) {
                if (rangeAxis.isCrosshairLockedOnData()) {
                    // both axes
                    crosshairInfo.updateCrosshairPoint(x1, y1);
                }
                else {
                    // just the horizontal axis...
                    crosshairInfo.updateCrosshairX(x1);
                }
            }
            else {
                if (rangeAxis.isCrosshairLockedOnData()) {
                    // just the vertical axis...
                    crosshairInfo.updateCrosshairY(y1);
                }
            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PROTECTED METHODS
    // These provide the opportunity to subclass the standard renderer and create custom effects.
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the shape used to draw a single data item.
     *
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param series  the series index
     * @param item  the item index
     * @param x  the x value of the item
     * @param y  the y value of the item
     * @param scale  the scale used to draw the shape
     *
     * @return The shape used to draw the data item
     */
    protected Shape getShape(Plot plot, int series, int item, double x, double y, double scale) {
        return plot.getShape(series, item, x, y, scale);
    }

    /**
     * Returns the shape scale of a single data item.
     * <P>
     * Unless overridden, this method just returns the default shape scale.
     *
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param series  the series index
     * @param item  the item index
     * @param x  the x value of the item
     * @param y  the y value of the item
     *
     * @return The scale used to draw the shape used for the data item
     */
    protected double getShapeScale(Plot plot, int series, int item, double x, double y) {
        return this.defaultShapeScale;
    }

    /**
     * Is used to determine if a shape is filled when drawn or not
     *
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param series  the series index.
     * @param item  the item index.
     * @param x  the x value of the item.
     * @param y  the y value of the item.
     *
     * @return <code>true</code> if the shape used to draw the data item should be filled.
     */
    protected boolean isShapeFilled(Plot plot, int series, int item, double x, double y) {
        return this.defaultShapeFilled;
    }

    /**
     * Returns the paint used to draw a single data item.
     * <P>
     * If null is returned, the renderer defaults to the paint for the current series.
     *
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param series  the series index.
     * @param item  the item index.
     * @param x  the x value of the item.
     * @param y  the y value of the item.
     *
     * @return The paint.
     */
    protected Paint getPaint(Plot plot, int series, int item, double x, double y) {
        return null;
    }

    /**
     * Returns the image used to draw a single data item.
     *
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param series  the series index.
     * @param item  the item index.
     * @param x  the x value of the item.
     * @param y  the y value of the item.
     *
     * @return the image.
     */
    protected Image getImage(Plot plot, int series, int item, double x, double y) {
        // should this be added to the plot as well ?
        // return plot.getShape(series, item, x, y, scale);
        // or should this be left to the user - like this:
        return null;
    }

    /**
     * Returns the hotspot of the image used to draw a single data item.
     * The hotspot is the point relative to the top left of the image
     * that should indicate the data item. The default is the center of the
     * image.
     *
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param image  the image (can be used to get size information about the image)
     * @param series  the series index
     * @param item  the item index
     * @param x  the x value of the item
     * @param y  the y value of the item
     *
     * @return the hotspot used to draw the data item.
     */
    protected Point getImageHotspot(Plot plot, int series, int item,
                                    double x, double y, Image image) {

        int height = image.getHeight(null);
        int width = image.getWidth(null);
        return new Point(width / 2, height / 2);

    }

}