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
 * -----------------
 * ShapeFactory.java
 * -----------------
 * (C) Copyright 2002, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   -;
 *
 * $Id: ShapeFactory.java,v 1.1.1.1 2003/07/17 10:06:26 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1 (JB);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Shape;

/**
 * Interface to be implemented by classes which provide shapes for indicating
 * data points on a Plot.
 *
 * @author Jeremy Bowman
 */
public interface ShapeFactory {

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
    public Shape getShape(int series, int item, double x, double y,
                          double scale);

    /**
     * Returns a Shape that can be used in plotting data.  Used in
     * CategoryPlots.
     *
     * @param series  the index of the series.
     * @param category  the category.
     * @param x  x-coordinate of the category.
     * @param y  y-coordinate of the category.
     * @param scale  the size.
     *
     * @return a Shape that can be used in plotting data.
     */
    public Shape getShape(int series, Object category, double x, double y,
                          double scale);

}
