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
 * DateTickUnit.java
 * -----------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DateTickUnit.java,v 1.1.1.1 2003/07/17 10:06:22 Ian.Mayo Exp $
 *
 * Changes (from 03-Sep-2002)
 * --------------------------
 * 03-Sep-2002 : Incomplete code, not used yet (DG);
 *
 */

package com.jrefinery.chart;

import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;

/**
 * Represents a tick unit for a date axis.
 *
 * @author DG
 */
public class DateTickUnit extends TickUnit {

    /** A constant for years. */
    public static final int YEAR = 0;

    /** A constant for months. */
    public static final int MONTH = 1;

    /** A constant for days. */
    public static final int DAY = 2;

    /** A constant for hours. */
    public static final int HOUR = 3;

    /** A constant for minutes. */
    public static final int MINUTE = 4;

    /** A constant for seconds. */
    public static final int SECOND = 5;

    /** A constant for milliseconds. */
    public static final int MILLISECOND = 6;

    /** The unit. */
    private int unit;

    /** The unit count. */
    private int count;

    /** The date formatter. */
    private DateFormat formatter;

    /**
     * Creates a new date tick unit.  The dates will be formatted using a SHORT format for the
     * default locale.
     *
     * @param unit  the unit.
     * @param count  the unit count.
     */
    public DateTickUnit(int unit, int count) {
        this(unit, count, DateFormat.getDateInstance(DateFormat.SHORT));
    }

    /**
     * Creates a new date tick unit.
     * <P>
     * You can specify the units using one of the constants YEAR, MONTH, DAY, HOUR, MINUTE,
     * SECOND or MILLISECOND.  In addition, you can specify a unit count, and a date format.
     *
     * @param unit  the unit.
     * @param count  the unit count.
     * @param formatter  the date formatter.
     */
    public DateTickUnit(int unit, int count, DateFormat formatter) {

        super(DateTickUnit.getMillisecondCount(unit, count));
        this.unit = unit;
        this.count = count;
        this.formatter = formatter;

    }

    /**
     * Returns the date unit.  This will be one of the constants YEAR, MONTH, DAY, HOUR, MINUTE,
     * SECOND or MILLISECOND (defined by this class).
     *
     * @return the date unit.
     */
    public int getUnit() {
        return this.unit;
    }

    /**
     * Returns the unit count.
     *
     * @return the unit count.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Formats a value.
     *
     * @param milliseconds  date in milliseconds since 01-01-1970.
     *
     * @return the formatted date.
     */
    public String valueToString(double milliseconds) {
        return formatter.format(new Date((long) milliseconds));
    }

    /**
     * Formats a date.
     *
     * @param date  the date.
     *
     * @return the formatted date.
     */
    public String dateToString(Date date) {
        return formatter.format(date);
    }


    /**
     * Calculates a new date by adding this unit to the base date.
     *
     * @param base  the base date.
     *
     * @return a new date one unit after the base date.
     */
    public Date addToDate(Date base) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(getCalendarField(this.unit), this.count);
        return calendar.getTime();

    }

    /**
     * Returns a field code that can be used with the Calendar class.
     *
     * @return the field code.
     */
    public int getCalendarField() {
        return getCalendarField(this.unit);
    }

    /**
     * Returns a field code (that can be used with the Calendar class) for a given 'unit' code.
     * The 'unit' is one of:  YEAR, MONTH, DAY, HOUR, MINUTE, SECOND and MILLISECOND.
     *
     * @param unit  the unit.
     *
     * @return the field code.
     */
    private int getCalendarField(int unit) {

        switch (unit) {
            case (YEAR) : return Calendar.YEAR;
            case (MONTH) : return Calendar.MONTH;
            case (DAY) : return Calendar.DATE;
            case (HOUR) : return Calendar.HOUR_OF_DAY;
            case (MINUTE) : return Calendar.MINUTE;
            case (SECOND) : return Calendar.SECOND;
            case (MILLISECOND) : return Calendar.MILLISECOND;
            default: return Calendar.MILLISECOND;
        }

    }

    /**
     * Returns the (approximate) number of milliseconds for the given unit and unit count.
     * <P>
     * This value is an approximation some of the time (e.g. months are assumed to have 31 days)
     * but this shouldn't matter.
     *
     * @param unit  the unit.
     * @param count  the unit count.
     *
     * @return the number of milliseconds.
     */
    private static long getMillisecondCount(int unit, int count) {

        switch (unit) {
            case (YEAR) : return (365L * 24L * 60L * 60L * 1000L) * count;
            case (MONTH) : return (31L * 24L * 60L * 60L * 1000L) * count;
            case (DAY) : return (24L * 60L * 60L * 1000L) * count;
            case (HOUR) : return (60L * 60L * 1000L) * count;
            case (MINUTE) : return (60L * 1000L) * count;
            case (SECOND) : return 1000L * count;
            case (MILLISECOND) : return (long) count;
            default: return (long) count;
        }

    }

}