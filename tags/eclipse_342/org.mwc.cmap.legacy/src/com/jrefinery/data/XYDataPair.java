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
 * ---------------
 * XYDataPair.java
 * ---------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYDataPair.java,v 1.1.1.1 2003/07/17 10:06:56 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 24-Jun-2002 : Removed unnecessary import (DG);
 * 27-Aug-2002 : Implemented cloneable (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

/**
 * Represents one (x, y) data item for an xy-series.
 *
 * @author DG
 */
@SuppressWarnings("unchecked")
public class XYDataPair implements Cloneable, Comparable {

    /** The x-value. */
    private Number x;

    /** The y-value. */
    private Number y;

    /**
     * Constructs a new data pair.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     */
    public XYDataPair(Number x, Number y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new data pair.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     */
    public XYDataPair(double x, double y) {
        this(new Double(x), new Double(y));
    }

    /**
     * Returns the x-value.
     *
     * @return the x-value.
     */
    public Number getX() {
        return this.x;
    }

    /**
     * Returns the y-value.
     *
     * @return the y-value.
     */
    public Number getY() {
        return this.y;
    }

    /**
     * Sets the y-value for this data pair.
     * <P>
     * Note that there is no corresponding method to change the x-value.
     *
     * @param y  the new y-value.
     */
    public void setY(Number y) {
        this.y = y;
    }

    /**
     * Returns an integer indicating the order of this data pair object relative to another object.
     * <P>
     * For the order we consider only the x-value:
     * negative == "less-than", zero == "equal", positive == "greater-than".
     *
     * @param o1  the object being compared to.
     *
     * @return  an integer indicating the order of this data pair object
     *      relative to another object.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another TimeSeriesDataPair object
        // -------------------------------------------------------
        if (o1 instanceof XYDataPair) {
            XYDataPair datapair = (XYDataPair) o1;
            double compare = this.x.doubleValue() - datapair.getX().doubleValue();
            if (compare > 0) {
                result = 1;
            }
            else {
                if (compare < 0) {
                    result = -1;
                }
                else {
                    result = 0;
                }
            }
        }

        // CASE 2 : Comparing to a general object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;

    }

    /**
     * Returns a clone of this XYDataPair.
     *
     * @return a clone of the data pair.
     */
    public Object clone() {

        Object clone = null;

        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) { // won't get here...
            System.err.println("XYDataPair.clone(): operation not supported.");
        }

        return clone;

    }

}
