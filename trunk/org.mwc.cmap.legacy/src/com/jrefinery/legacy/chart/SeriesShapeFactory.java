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
 * SeriesShapeFactory.java
 * -----------------------
 * (C) Copyright 2002, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   -;
 *
 * $Id: SeriesShapeFactory.java,v 1.1.1.1 2003/07/17 10:06:26 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1 (JB);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Provider of shapes for indicating data points on a Plot.  This one
 * uses a distinct shape for each series, looping if it exhausts the
 * possibilities.
 *
 * @author Jeremy Bowman
 */
public class SeriesShapeFactory implements ShapeFactory {

    /** The number of distinct shapes available */
    private static final int SHAPE_COUNT = 11;

    /**
     * Returns a Shape that can be used in plotting data.  Used in XYPlots.
     *
     * @param series  the index of the series.
     * @param item  the index of the item.
     * @param x  x-coordinate of the item.
     * @param y  y-coordinate of the item.
     * @param scale  the size.
     *
     * @return a Shape that can be used in plotting data.
     */
    public Shape getShape(int series, int item, double x, double y, double scale) {

        return getShape(series, null, x, y, scale);

    }

    /**
     * Returns a Shape that can be used in plotting data. Used in CategoryPlots.
     *
     * @param series  the index of the series.
     * @param category  the category.
     * @param x  x-coordinate of the category.
     * @param y  y-coordinate of the category.
     * @param scale  the size.
     *
     * @return a Shape that can be used in plotting data.
     */
    public Shape getShape(int series, Object category, double x, double y, double scale) {

        double delta = 0.5 * scale;
        int index = series % SHAPE_COUNT;
        int[] xpoints = null;
        int[] ypoints = null;
        switch (index) {
        case 0:
            // Square
            return new Rectangle2D.Double(x - delta, y - delta, scale, scale);
        case 1:
            // Circle
            return new Ellipse2D.Double(x - delta, y - delta, scale, scale);
        case 2:
            // Up-pointing triangle
            xpoints = intArray(x, x + delta, x - delta);
            ypoints = intArray(y - delta, y + delta, y + delta);
            return new Polygon(xpoints, ypoints, 3);
        case 3:
            // Diamond
            xpoints = intArray(x, x + delta, x, x - delta);
            ypoints = intArray(y - delta, y, y + delta, y);
            return new Polygon(xpoints, ypoints, 4);
        case 4:
            // Horizontal rectangle
            return new Rectangle2D.Double(x - delta, y - delta / 2, scale, scale / 2);
        case 5:
            // Down-pointing triangle
            xpoints = intArray(x - delta, x + delta, x);
            ypoints = intArray(y - delta, y - delta, y + delta);
            return new Polygon(xpoints, ypoints, 3);
        case 6:
            // Horizontal ellipse
            return new Ellipse2D.Double(x - delta, y - delta / 2, scale, scale / 2);
        case 7:
            // Right-pointing triangle
            xpoints = intArray(x - delta, x + delta, x - delta);
            ypoints = intArray(y - delta, y, y + delta);
            return new Polygon(xpoints, ypoints, 3);
        case 8:
            // Vertical rectangle
            return new Rectangle2D.Double(x - delta / 2, y - delta, scale / 2, scale);
        case 9:
            // Left-pointing triangle
            xpoints = intArray(x - delta, x + delta, x + delta);
            ypoints = intArray(y, y - delta, y + delta);
            return new Polygon(xpoints, ypoints, 3);
        default:
            // Vertical ellipse
            return new Ellipse2D.Double(x - delta / 2, y - delta, scale / 2, scale);
        }

    }

    /**
     * Helper method to avoid lots of explicit casts in getShape().  Returns
     * an array containing the provided doubles cast to ints.
     *
     * @param a  x
     * @param b  y
     * @param c  z
     *
     * @return int[3] with converted params.
     */
    private static int[] intArray(double a, double b, double c) {
        return new int[] { (int) a, (int) b, (int) c };
    }

    /**
     * Helper method to avoid lots of explicit casts in getShape().  Returns
     * an array containing the provided doubles cast to ints.
     *
     * @param a  x
     * @param b  y
     * @param c  z
     * @param d  t
     *
     * @return int[3] with converted params.
     */
    private static int[] intArray(double a, double b, double c, double d) {
        return new int[] { (int) a, (int) b, (int) c, (int) d };
    }

}
