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
 * XYImageAnnotation.java
 * ----------------------
 * (C) Copyright 2003-2021, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Mike Harris;
 *                   Peter Kolb (patch 2809117);
 *
 */

package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;

/**
 * An annotation that allows an image to be placed at some location on
 * an {@link XYPlot}.
 *
 * TODO:  implement serialization properly (image is not serializable).
 */
public class XYImageAnnotation extends AbstractXYAnnotation
        implements Cloneable, PublicCloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -4364694501921559958L;

    /** The x-coordinate (in data space). */
    private double x;

    /** The y-coordinate (in data space). */
    private double y;

    /** The image. */
    private transient Image image;

    /** The image anchor point. */
    private RectangleAnchor anchor;

    /**
     * Creates a new annotation to be displayed at the specified (x, y)
     * location.
     *
     * @param x  the x-coordinate (in data space).
     * @param y  the y-coordinate (in data space).
     * @param image  the image ({@code null} not permitted).
     */
    public XYImageAnnotation(double x, double y, Image image) {
        this(x, y, image, RectangleAnchor.CENTER);
    }

    /**
     * Creates a new annotation to be displayed at the specified (x, y)
     * location.
     *
     * @param x  the x-coordinate (in data space).
     * @param y  the y-coordinate (in data space).
     * @param image  the image ({@code null} not permitted).
     * @param anchor  the image anchor ({@code null} not permitted).
     */
    public XYImageAnnotation(double x, double y, Image image,
            RectangleAnchor anchor) {
        super();
        Args.nullNotPermitted(image, "image");
        Args.nullNotPermitted(anchor, "anchor");
        this.x = x;
        this.y = y;
        this.image = image;
        this.anchor = anchor;
    }

    /**
     * Returns the x-coordinate (in data space) for the annotation.
     *
     * @return The x-coordinate.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate (in data space) for the annotation.
     *
     * @return The y-coordinate.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Returns the image for the annotation.
     *
     * @return The image.
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Returns the image anchor for the annotation.
     *
     * @return The image anchor.
     */
    public RectangleAnchor getImageAnchor() {
        return this.anchor;
    }

    /**
     * Draws the annotation.  This method is called by the drawing code in the
     * {@link XYPlot} class, you don't normally need to call this method
     * directly.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param rendererIndex  the renderer index.
     * @param info  if supplied, this info object will be populated with
     *              entity information.
     */
    @Override
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis,
                     int rendererIndex,
                     PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
        AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
        RectangleEdge domainEdge
            = Plot.resolveDomainAxisLocation(domainAxisLocation, orientation);
        RectangleEdge rangeEdge
            = Plot.resolveRangeAxisLocation(rangeAxisLocation, orientation);
        float j2DX
            = (float) domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
        float j2DY
            = (float) rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
        float xx = 0.0f;
        float yy = 0.0f;
        if (orientation == PlotOrientation.HORIZONTAL) {
            xx = j2DY;
            yy = j2DX;
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            xx = j2DX;
            yy = j2DY;
        }
        int w = this.image.getWidth(null);
        int h = this.image.getHeight(null);

        Rectangle2D imageRect = new Rectangle2D.Double(0, 0, w, h);
        Point2D anchorPoint = this.anchor.getAnchorPoint(imageRect);
        xx = xx - (float) anchorPoint.getX();
        yy = yy - (float) anchorPoint.getY();
        g2.drawImage(this.image, (int) xx, (int) yy, null);

        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, new Rectangle2D.Float(xx, yy, w, h), rendererIndex,
                    toolTip, url);
        }
    }

    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        // now try to reject equality...
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYImageAnnotation)) {
            return false;
        }
        XYImageAnnotation that = (XYImageAnnotation) obj;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (!Objects.equals(this.image, that.image)) {
            return false;
        }
        if (!this.anchor.equals(that.anchor)) {
            return false;
        }
        // seems to be the same...
        return true;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        return this.image.hashCode();
    }

    /**
     * Returns a clone of the annotation.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  if the annotation can't be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        //SerialUtils.writeImage(this.image, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        //this.image = SerialUtils.readImage(stream);
    }


}
