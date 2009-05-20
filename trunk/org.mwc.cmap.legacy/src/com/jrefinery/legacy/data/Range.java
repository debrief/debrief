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
 * ----------
 * Range.java
 * ----------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Chuanhao Chiu;
 *
 * $Id: Range.java,v 1.1.1.1 2003/07/17 10:06:54 Ian.Mayo Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 22-Apr-2002 : Version 1, loosely based by code by Bill Kelemen (DG);
 * 30-Apr-2002 : Added getLength() and getCentralValue() methods.  Changed argument check in
 *               constructor (DG);
 * 13-Jun-2002 : Added contains(double) method (DG);
 * 22-Aug-2002 : Added fix to combine method where both ranges are null, thanks to Chuanhao Chiu
 *               for reporting and fixing this (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.data;

/**
 * Represents the visible range for an axis.
 *
 * @author DG
 */
public class Range {

    /** The lower bound for the visible range. */
    private double lower;

    /** The upper bound for the visible range. */
    private double upper;

    /**
     * Constructs a new axis range.
     *
     * @param lower  the lower bound.
     * @param upper  the upper bound.
     */
    public Range(double lower, double upper) {

//        if (lower > upper) {
//            throw new IllegalArgumentException("Range(double, double): require lower<=upper.");
//        }

        this.lower = Math.min(lower, upper);
        this.upper = Math.max(lower, upper);

    }

    /**
     * Returns the lower bound for the range.
     *
     * @return the lower bound.
     */
    public double getLowerBound() {
        return this.lower;
    }

    /**
     * Returns the upper bound for the range.
     *
     * @return the upper bound.
     */
    public double getUpperBound() {
        return this.upper;
    }

    /**
     * Returns the length of the range.
     *
     * @return the length.
     */
    public double getLength() {
        return upper - lower;
    }

    /**
     * Returns the central value for the range.
     *
     * @return the central value.
     */
    public double getCentralValue() {
        return lower / 2 + upper / 2;
    }

    /**
     * Returns true if the range contains the specified value.
     *
     * @param value  the value to lookup.
     *
     * @return <code>true</code> if the range contains the specified value.
     */
    public boolean contains(double value) {
        return (value >= lower && value <= upper);
    }

    /**
     * Creates a new range by combining two existing ranges.
     * <P>
     * Note that:
     * <ul>
     *   <li>either range can be null, in which case the other range is returned;</li>
     *   <li>if both ranges are null the return value is null.</li>
     * </ul>
     *
     * @param range1  the first range.
     * @param range2  the second range.
     *
     * @return a new range.
     */
    public static Range combine(Range range1, Range range2) {

        if (range1 == null) {
            return range2;
        }
        else {
            if (range2 == null) {
                return range1;
            }
            else {
                double l = Math.min(range1.getLowerBound(), range2.getLowerBound());
                double u = Math.max(range1.getUpperBound(), range2.getUpperBound());
                return new Range(l, u);
            }
        }
    }

}
