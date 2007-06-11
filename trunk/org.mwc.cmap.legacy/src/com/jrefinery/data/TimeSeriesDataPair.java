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
 * -----------------------
 * TimeSeriesDataPair.java
 * -----------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDataPair.java,v 1.1.1.1 2003/07/17 10:06:56 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 15-Nov-2001 : Updated Javadoc comments (DG);
 * 29-Nov-2001 : Added cloning (DG);
 * 24-Jun-2002 : Removed unnecessary import (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

/**
 * Represents one data item in a time series.
 * <P>
 * The time period can be any of the following: Year, Quarter, Month, Week,
 * Day, Hour, Minute, Second or Millisecond.
 * <P>
 * The time period is an immutable property of the data pair.  Data pairs will
 * often be sorted within a list, and allowing the time period to be changed
 * could destroy the sort order.
 * <P>
 * Implements the Comparable interface so that standard Java sorting can be
 * used to keep the data pairs in order.
 *
 * @author DG
 */
public class TimeSeriesDataPair implements Cloneable, Comparable {

    /** The time period. */
    private TimePeriod period;

    /** The value associated with the time period. */
    private Number value;

    /**
     * Constructs a new data pair.
     *
     * @param period  the time period.
     * @param value  the value associated with the time period.
     */
    public TimeSeriesDataPair(TimePeriod period, Number value) {

        this.period = period;
        this.value = value;

    }

    /**
     * Constructs a new data pair.
     *
     * @param period  the time period.
     * @param value  the value associated with the time period.
     */
    public TimeSeriesDataPair(TimePeriod period, double value) {

        this(period, new Double(value));

    }

    /**
     * Clones the data pair.
     * <P>
     * Notes:
     * --> no need to clone the period or value since they are immutable classes.
     *
     * @return a clone of this data pair.
     */
    public Object clone() {

        Object clone = null;

        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) { // won't get here...
            System.err.println("TimeSeriesDataPair.clone(): operation not supported.");
        }

        return clone;

    }

    /**
     * Returns the time period.
     *
     * @return the time period.
     */
    public TimePeriod getPeriod() {
        return this.period;
    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * Sets the value for this data pair.
     *
     * @param value  the new value.
     */
    public void setValue(Number value) {
        this.value = value;
    }

    /**
     * Returns an integer indicating the order of this data pair object
     * relative to another object.
     * <P>
     * For the order we consider only the timing:
     * negative == before, zero == same, positive == after.
     *
     * @param o1  The object being compared to.
     *
     * @return  An integer indicating the order of the data pair object relative to another object.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another TimeSeriesDataPair object
        // -------------------------------------------------------
        if (o1 instanceof TimeSeriesDataPair) {
            TimeSeriesDataPair datapair = (TimeSeriesDataPair) o1;
            result = this.getPeriod().compareTo(datapair.getPeriod());
        }

        // CASE 2 : Comparing to a general object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;

    }

}
