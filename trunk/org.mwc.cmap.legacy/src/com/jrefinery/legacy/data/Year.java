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
 * ---------
 * Year.java
 * ---------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Year.java,v 1.1.1.1 2003/07/17 10:06:57 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 14-Nov-2001 : Override for toString() method (DG);
 * 19-Dec-2001 : Added a new constructor as suggested by Paul English (DG);
 * 29-Jan-2002 : Worked on parseYear(...) method (DG);
 * 14-Feb-2002 : Fixed bug in Year(Date) constructor (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to evaluate with reference
 *               to a particular time zone (DG);
 * 19-Mar-2002 : Changed API for TimePeriod classes (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.data;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.jrefinery.date.SerialDate;

/**
 * Represents a year in the range 1900 to 9999.
 * <P>
 * This class is immutable, which is a requirement for all TimePeriod subclasses.
 *
 * @author DG
 */
public class Year extends TimePeriod {

    /** The year. */
    private int year;

    /**
     * Constructs a new Year, based on the current system date/time.
     */
    public Year() {
        this(new Date());
    }

    /**
     * Constructs a time period representing a single year.
     *
     * @param year  the year.
     */
    public Year(int year) {

        // check arguments...
        if ((year < SerialDate.MINIMUM_YEAR_SUPPORTED)
            || (year > SerialDate.MAXIMUM_YEAR_SUPPORTED)) {

            throw new IllegalArgumentException(
                "Year constructor: year (" + year + ") outside valid range.");

        }

        // initialise...
        this.year = year;

    }

    /**
     * Constructs a new Year, based on a particular instant in time, using the
     * default time zone.
     *
     * @param time  the time.
     */
    public Year(Date time) {
        this(time, TimePeriod.DEFAULT_TIME_ZONE);
    }

    /**
     * Constructs a year, based on a particular instant in time and a time zone.
     *
     * @param time  the time.
     * @param zone  the time zone.
     */
    public Year(Date time, TimeZone zone) {

        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.year = calendar.get(Calendar.YEAR);

    }

    /**
     * Returns the year.
     *
     * @return the year.
     */
    public int getYear() {
        return this.year;
    }

    /**
     * Returns the year preceding this one.
     *
     * @return the year preceding this one (or null if the current year is 1900).
     */
    public TimePeriod previous() {

        if (year > SerialDate.MINIMUM_YEAR_SUPPORTED) {
            return new Year(year - 1);
        }
        else {
            return null;
        }

    }

    /**
     * Returns the year following this one.
     *
     * @return The year following this one (or null if the current year is 9999).
     */
    public TimePeriod next() {

        if (year < SerialDate.MAXIMUM_YEAR_SUPPORTED) {
            return new Year(year + 1);
        }
        else {
            return null;
        }

    }

    /**
     * Returns a serial index number for the year.
     * <P>
     * The implementation simply returns the year number (e.g. 2002).
     *
     * @return the serial index number.
     */
    public long getSerialIndex() {
        return year;
    }

    /**
     * Returns the first millisecond of the year, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar  the calendar.
     *
     * @return the first millisecond of the year.
     */
    public long getStart(Calendar calendar) {

        Day jan1 = new Day(1, SerialDate.JANUARY, year);
        return jan1.getStart(calendar);

    }

    /**
     * Returns the last millisecond of the year, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar  the calendar.
     *
     * @return the last millisecond of the year.
     */
    public long getEnd(Calendar calendar) {

        Day dec31 = new Day(31, SerialDate.DECEMBER, year);
        return dec31.getEnd(calendar);

    }

    /**
     * Tests the equality of this Year object to an arbitrary object.  Returns
     * true if the target is a Year instance representing the same year as this
     * object.  In all other cases, returns false.
     *
     * @param object  the object.
     *
     * @return <code>true</code> if the year of this and the object are the same.
     */
    public boolean equals(Object object) {

        if (object != null) {
            if (object instanceof Year) {
                Year target = (Year) object;
                return (year == target.getYear());
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }

    }

    /**
     * Returns an integer indicating the order of this Year object relative to
     * the specified object:
     *
     * negative == before, zero == same, positive == after.
     *
     * @param o1  the object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another Year object
        // -----------------------------------------
        if (o1 instanceof Year) {
            Year y = (Year) o1;
            result = this.year - y.getYear();
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
     * Returns a string representing the year (e.g. "2002").
     *
     * @return a string representing the year.
     */
    public String toString() {
        return Integer.toString(year);
    }

    /**
     * Parses the string argument as a year.
     * <P>
     * The string format is YYYY.
     *
     * @param s  a string representing the year.
     *
     * @return <code>null</code> if the string is not parseable, the year otherwise.
     *
     * @throws TimePeriodFormatException if there is a parsing error.
     */
    public static Year parseYear(String s) throws TimePeriodFormatException {

        // parse the string...
        int y;
        try {
            y = Integer.parseInt(s.trim());
        }
        catch (NumberFormatException e) {
            throw new TimePeriodFormatException("Year.parseYear(string): cannot parse string.");
        }

        // create the year...
        Year result = null;
        try {
            result = new Year(y);
        }
        catch (IllegalArgumentException e) {
            throw new TimePeriodFormatException(
                "Year.parseYear(string): year outside valid range.");
        }
        return result;

    }

}
