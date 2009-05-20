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
 * ---------------------
 * FixedMillisecond.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: FixedMillisecond.java,v 1.1.1.1 2003/07/17 10:06:52 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 19-Mar-2002 : Version 1, based on original Millisecond implementation (DG);
 * 24-Jun-2002 : Removed unnecessary imports (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.data;

import java.util.Date;
import java.util.Calendar;

/**
 * Wrapper for a java.util.Date object that allows it to be used as a TimePeriod.
 * <P>
 * This class is immutable, which is a requirement for all TimePeriod subclasses.
 *
 * @author DG
 */
public class FixedMillisecond extends TimePeriod {

    /** The millisecond. */
    private Date time;

    /**
     * Constructs a millisecond based on the current system time.
     */
    public FixedMillisecond() {
        this(new Date());
    }

    /**
     * Constructs a millisecond.
     *
     * @param millisecond  the millisecond (same encoding as java.util.Date).
     */
    public FixedMillisecond(long millisecond) {
        this(new Date(millisecond));
    }

    /**
     * Constructs a millisecond.
     *
     * @param time  the time.
     */
    public FixedMillisecond(Date time) {
        this.time = time;
    }

    /**
     * Returns the date/time.
     *
     * @return the date/time.
     */
    public Date getTime() {
        return this.time;
    }

    /**
     * Returns the millisecond preceding this one.
     *
     * @return the millisecond preceding this one.
     */
    public TimePeriod previous() {

        TimePeriod result = null;

        long t = this.time.getTime();
        if (t != Long.MIN_VALUE) {
            result = new FixedMillisecond(t - 1);
        }

        return result;

    }

    /**
     * Returns the millisecond following this one.
     *
     * @return the millisecond following this one.
     */
    public TimePeriod next() {

        TimePeriod result = null;

        long t = this.time.getTime();
        if (t != Long.MAX_VALUE) {
            result = new FixedMillisecond(t + 1);
        }

        return result;

    }

    /**
     * Returns an integer indicating the order of this Millisecond object
     * relative to the specified
     * object: negative == before, zero == same, positive == after.
     *
     * @param o1    the object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;
        long difference;

        // CASE 1 : Comparing to another Second object
        // -------------------------------------------
        if (o1 instanceof FixedMillisecond) {
            FixedMillisecond t1 = (FixedMillisecond) o1;
            difference = this.time.getTime() - t1.time.getTime();
            if (difference > 0) {
                result = 1;
            }
            else {
                if (difference < 0) {
                   result = -1;
                }
                else {
                    result = 0;
                }
            }
        }

        // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        else if (o1 instanceof TimePeriod) {
            // more difficult case - evaluate later...
            result = 0;
        }

        // CASE 3 : Comparing to a non-TimePeriod object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;

    }

    /**
     * Returns the first millisecond of the time period.
     *
     * @return the first millisecond of the time period.
     */
    public long getStart() {
        return this.time.getTime();
    }


    /**
     * Returns the first millisecond of the time period.
     *
     * @param calendar  the calendar.
     *
     * @return the first millisecond of the time period.
     */
    public long getStart(Calendar calendar) {
        return this.time.getTime();
    }

    /**
     * Returns the last millisecond of the time period.
     *
     * @return the last millisecond of the time period.
     */
    public long getEnd() {
        return this.time.getTime();
    }

    /**
     * Returns the last millisecond of the time period.
     *
     * @param calendar  the calendar.
     *
     * @return the last millisecond of the time period.
     */
    public long getEnd(Calendar calendar) {
        return this.time.getTime();
    }

    /**
     * Returns the millisecond closest to the middle of the time period.
     *
     * @return the millisecond closest to the middle of the time period.
     */
    public long getMiddle() {
        return this.time.getTime();
    }

    /**
     * Returns the millisecond closest to the middle of the time period.
     *
     * @param calendar  the calendar.
     *
     * @return the millisecond closest to the middle of the time period.
     */
    public long getMiddle(Calendar calendar) {
        return this.time.getTime();
    }

    /**
     * Returns a serial index number for the millisecond.
     *
     * @return The serial index number.
     */
    public long getSerialIndex() {
        return this.time.getTime();
    }

}
