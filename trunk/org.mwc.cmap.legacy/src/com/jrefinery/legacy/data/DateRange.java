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
 * --------------
 * DateRange.java
 * --------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Bill Kelemen;
 *
 * $Id: DateRange.java,v 1.1.1.1 2003/07/17 10:06:51 Ian.Mayo Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 22-Apr-2002 : Version 1 based on code by Bill Kelemen (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.data;

import java.util.Date;

/**
 * An axis range specified in terms of two java.util.Date objects.
 *
 * @author DG/BK
 */
public class DateRange extends Range {

    /** The lower bound for the axis. */
    private Date lowerDate;

    /** The upper bound for the axis. */
    private Date upperDate;

    /**
     * Default constructor.
     */
    public DateRange() {

        this(new Date(0), new Date(1));

    }

    /**
     * Constructs a new DateAxisRange.
     *
     * @param lower  the lower bound.
     * @param upper  the upper bound.
     */
    public DateRange(Date lower, Date upper) {

        super(lower.getTime(), upper.getTime());
        this.lowerDate = lower;
        this.upperDate = upper;

    }

    /**
     * Constructs a new DateAxisRange.
     *
     * @param lower  the lower (oldest) date.
     * @param upper  the upper (youngest) date.
     */
    public DateRange(double lower, double upper) {

        super(lower, upper);
        this.lowerDate = new Date((long) lower);
        this.upperDate = new Date((long) upper);


    }

    /**
     * Constructs a new DateAxisRange based on another range.
     * <P>
     * The other range may not be a DateAxisRange.  If it is not, the upper
     * and lower bounds are evaluated as milliseconds since midnight
     * GMT, 1-Jan-1970.
     *
     * @param other  the other range.
     */
    public DateRange(Range other) {

        this(other.getLowerBound(), other.getUpperBound());

    }

    /**
     * Returns the lower bound for the axis.
     *
     * @return the lower bound for the axis.
     */
    public Date getLowerDate() {
        return this.lowerDate;
    }

    /**
     * Returns the upper bound for the axis.
     *
     * @return the upper bound for the axis.
     */
    public Date getUpperDate() {
        return this.upperDate;
    }

}
