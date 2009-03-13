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
 * TimePeriod.java
 * ---------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriod.java,v 1.1.1.1 2003/07/17 10:06:55 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to evaluate with reference
 *               to a particular time zone (DG);
 * 29-May-2002 : Implemented MonthConstants interface, so that these constants are conveniently
 *               available (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 *
 */

package com.jrefinery.data;

import java.util.Calendar;
import java.util.TimeZone;
import com.jrefinery.date.MonthConstants;

/**
 * An abstract class representing a unit of time.
 * <p>
 * Convenient methods are provided for calculating the next and previous time
 * periods.
 * <p>
 * Conversion methods are defined that return the first and last milliseconds
 * of the time period. The results from these methods are timezone-dependent.
 * <P>
 * Subclasses of TimePeriod are required to be immutable.
 *
 * @author DG
 */
@SuppressWarnings("unchecked")
public abstract class TimePeriod implements Comparable, MonthConstants {

    /**
     * Returns the time period preceding this one, or null if some lower limit
     * has been reached.
     *
     * @return the previous time period.
     */
    public abstract TimePeriod previous();

    /**
     * Returns the time period following this one, or null if some limit has
     * been reached.
     *
     * @return the next time period.
     */
    public abstract TimePeriod next();

    /**
     * Returns a serial index number for the time unit.
     *
     * @return the serial index number.
     */
    public abstract long getSerialIndex();

    //////////////////////////////////////////////////////////////////////////

    /** The default time zone. */
    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    /** A working calendar (recycle to avoid unnecessary object creation). */
    public static final Calendar WORKING_CALENDAR = Calendar.getInstance(DEFAULT_TIME_ZONE);

    /**
     * Returns the first millisecond of the time period, evaluated in the
     * default time zone.
     *
     * @return the first millisecond of the time period.
     */
    public long getStart() {
        return this.getStart(DEFAULT_TIME_ZONE);
    }

    /**
     * Returns the first millisecond of the time period, evaluated within a
     * specific time zone.
     *
     * @param zone  the time zone.
     *
     * @return the first millisecond of the time period.
     */
    public long getStart(TimeZone zone) {
        WORKING_CALENDAR.setTimeZone(zone);
        return this.getStart(WORKING_CALENDAR);
    }

    /**
     * Returns the first millisecond of the time period, evaluated using the
     * supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar.
     *
     * @return the first millisecond of the time period.
     */
    public abstract long getStart(Calendar calendar);

    /**
     * Returns the last millisecond of the time period, evaluated in the default
     * time zone.
     *
     * @return the last millisecond of the time period.
     */
    public long getEnd() {
        return this.getEnd(DEFAULT_TIME_ZONE);
    }

    /**
     * Returns the last millisecond of the time period, evaluated within a
     * specific time zone.
     *
     * @param zone  the time zone.
     *
     * @return the last millisecond of the time period.
     */
    public long getEnd(TimeZone zone) {

        WORKING_CALENDAR.setTimeZone(zone);
        return this.getEnd(WORKING_CALENDAR);

    }

    /**
     * Returns the last millisecond of the time period, evaluated using the
     * supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar.
     *
     * @return the last millisecond of the time period.
     */
    public abstract long getEnd(Calendar calendar);

    /**
     * Returns the millisecond closest to the middle of the time period,
     * evaluated in the default time zone.
     *
     * @return the millisecond closest to the middle of the time period.
     */
    public long getMiddle() {

        long result = (getStart() / 2) + (getEnd() / 2);
        return result;

    }

    /**
     * Returns the millisecond closest to the middle of the time period,
     * evaluated within a specific time zone.
     *
     * @param zone  the time zone.
     *
     * @return the millisecond closest to the middle of the time period.
     */
    public long getMiddle(TimeZone zone) {

        long result = (getStart(zone) / 2) + (getEnd(zone) / 2);
        return result;

    }

    /**
     * Returns the millisecond closest to the middle of the time period,
     * evaluated using the supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar.
     *
     * @return the millisecond closest to the middle of the time period.
     */
    public long getMiddle(Calendar calendar) {

        long result = (getStart(calendar) / 2) + (getEnd(calendar) / 2);
        return result;

    }

}
