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
 * -----------------------
 * DrawableLegendItem.java
 * -----------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DrawableLegendItem.java,v 1.1.1.1 2003/07/17 10:06:22 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 07-Feb-2002 : Version 1 (DG);
 * 23-Sep-2002 : Renamed LegendItem --> DrawableLegendItem (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Represents a single item within a legend.
 *
 * @author DG
 */
public class DrawableLegendItem {

    /** The legend item (encapsulates information about the label, color and shape). */
    private LegendItem item;

    /** The x-coordinate for the item's location. */
    private double x;

    /** The y-coordinate for the item's location. */
    private double y;

    /** The width of the item. */
    private double width;

    /** The height of the item. */
    private double height;

    /** A shape used to indicate color on the legend. */
    private Shape marker;

    /** The label position within the item. */
    private Point2D labelPosition;

    /**
     * Create a legend item.
     *
     * @param item  the legend item for display.
     */
    public DrawableLegendItem(LegendItem item) {
        this.item = item;
    }

    /**
     * Returns the legend item.
     *
     * @return the legend item.
     */
    public LegendItem getItem() {
        return this.item;
    }

    /**
     * Get the x-coordinate for the item's location.
     *
     * @return the x-coordinate for the item's location.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Set the x-coordinate for the item's location.
     *
     * @param x  the x-coordinate.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Get the y-coordinate for the item's location.
     *
     * @return the y-coordinate for the item's location.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Set the y-coordinate for the item's location.
     *
     * @param y  the y-coordinate.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the width of this item.
     *
     * @return the width.
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Get the height of this item.
     *
     * @return the height.
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Get the marker.
     *
     * @return the shape used to indicate color on the legend for this item.
     */
    public Shape getMarker() {
        return this.marker;
    }

    /**
     * Set the marker.
     *
     * @param marker  a shape used to indicate color on the legend for this item.
     */
    public void setMarker(Shape marker) {
        this.marker = marker;
    }

    /**
     * Returns the label position.
     *
     * @return the label position.
     */
    public Point2D getLabelPosition() {
        return this.labelPosition;
    }

    /**
     * Sets the label position.
     *
     * @param position  the label position.
     */
    public void setLabelPosition(Point2D position) {
        this.labelPosition = position;
    }

    /**
     * Set the bounds of this item.
     *
     * @param x  x-coordinate for the item's location.
     * @param y  y-coordinate for the item's location.
     * @param width  the width of this item.
     * @param height  the height of this item.
     */
    public void setBounds(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Draw the item. Currently it does nothing.
     *
     * @param g2  the graphics device.
     * @param xOffset  offset for the x-coordinate.
     * @param yOffset  offset for the y-coordinate.
     */
    public void draw(Graphics2D g2, double xOffset, double yOffset) {
        // set up a translation on g2

        // restore original g2
    }

}
