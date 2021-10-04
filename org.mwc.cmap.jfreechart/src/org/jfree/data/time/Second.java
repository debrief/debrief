/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2021, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -----------
 * Second.java
 * -----------
 * (C) Copyright 2001-2021, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.jfree.chart.util.Args;

/**
 * Represents a second in a particular day.  This class is immutable, which is
 * a requirement for all {@link RegularTimePeriod} subclasses.
 */
public class Second extends RegularTimePeriod implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -6536564190712383466L;

    /** Useful constant for the first second in a minute. */
    public static final int FIRST_SECOND_IN_MINUTE = 0;

    /** Useful constant for the last second in a minute. */
    public static final int LAST_SECOND_IN_MINUTE = 59;

    /** The day. */
    private Day day;

    /** The hour of the day. */
    private byte hour;

    /** The minute. */
    private byte minute;

    /** The second. */
    private byte second;

    /**
     * The first millisecond.  We don't store the last millisecond, because it
     * is always firstMillisecond + 999L.
     */
    private long firstMillisecond;

    /**
     * Constructs a new Second, based on the system date/time.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     */
    public Second() {
        this(new Date());
    }

    /**
     * Constructs a new Second.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param second  the second (0 to 59).
     * @param minute  the minute ({@code null} not permitted).
     */
    public Second(int second, Minute minute) {
        Args.requireInRange(second, "second", 
                Second.FIRST_SECOND_IN_MINUTE, Second.LAST_SECOND_IN_MINUTE);
        Args.nullNotPermitted(minute, "minute");
        this.day = minute.getDay();
        this.hour = (byte) minute.getHourValue();
        this.minute = (byte) minute.getMinute();
        this.second = (byte) second;
        peg(getCalendarInstance());
    }

    /**
     * Creates a new second.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param second  the second (0-59).
     * @param minute  the minute (0-59).
     * @param hour  the hour (0-23).
     * @param day  the day (1-31).
     * @param month  the month (1-12).
     * @param year  the year (1900-9999).
     */
    public Second(int second, int minute, int hour,
                  int day, int month, int year) {
        this(second, new Minute(minute, hour, day, month, year));
    }

    /**
     * Constructs a new instance from the specified date/time.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param time  the time ({@code null} not permitted).
     *
     * @see #Second(Date, TimeZone, Locale)
     */
    public Second(Date time) {
        this(time, getCalendarInstance());
    }

    /**
     * Creates a new second based on the supplied time and time zone.
     *
     * @param time  the time ({@code null} not permitted).
     * @param zone  the time zone ({@code null} not permitted).
     * @param locale  the locale ({@code null} not permitted).
     */
    public Second(Date time, TimeZone zone, Locale locale) {
        this(time, Calendar.getInstance(zone, locale));
    }

    /**
     * Constructs a new instance, based on a particular date/time.
     * The time zone and locale are determined by the {@code calendar}
     * parameter.
     *
     * @param time the date/time ({@code null} not permitted).
     * @param calendar the calendar to use for calculations ({@code null} not permitted).
     */
    public Second(Date time, Calendar calendar) {
        calendar.setTime(time);
        this.second = (byte) calendar.get(Calendar.SECOND);
        this.minute = (byte) calendar.get(Calendar.MINUTE);
        this.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        this.day = new Day(time, calendar);
        peg(calendar);
    }

    /**
     * Returns the second within the minute.
     *
     * @return The second (0 - 59).
     */
    public int getSecond() {
        return this.second;
    }

    /**
     * Returns the minute.
     *
     * @return The minute (never {@code null}).
     */
    public Minute getMinute() {
        return new Minute(this.minute, new Hour(this.hour, this.day));
    }

    /**
     * Returns the first millisecond of the second.  This will be determined
     * relative to the time zone specified in the constructor, or in the
     * calendar instance passed in the most recent call to the
     * {@link #peg(Calendar)} method.
     *
     * @return The first millisecond of the second.
     *
     * @see #getLastMillisecond()
     */
    @Override
    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    /**
     * Returns the last millisecond of the second.  This will be
     * determined relative to the time zone specified in the constructor, or
     * in the calendar instance passed in the most recent call to the
     * {@link #peg(Calendar)} method.
     *
     * @return The last millisecond of the second.
     *
     * @see #getFirstMillisecond()
     */
    @Override
    public long getLastMillisecond() {
        return this.firstMillisecond + 999L;
    }

    /**
     * Recalculates the start date/time and end date/time for this time period
     * relative to the supplied calendar (which incorporates a time zone).
     *
     * @param calendar  the calendar ({@code null} not permitted).
     */
    @Override
    public void peg(Calendar calendar) {
        this.firstMillisecond = getFirstMillisecond(calendar);
    }

    /**
     * Returns the second preceding this one.
     * No matter what time zone and locale this instance was created with,
     * the returned instance will use the default calendar for time
     * calculations, obtained with {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @return The second preceding this one.
     */
    @Override
    public RegularTimePeriod previous() {
        Second result = null;
        if (this.second != FIRST_SECOND_IN_MINUTE) {
            result = new Second(this.second - 1, getMinute());
        }
        else {
            Minute previous = (Minute) getMinute().previous();
            if (previous != null) {
                result = new Second(LAST_SECOND_IN_MINUTE, previous);
            }
        }
        return result;
    }

    /**
     * Returns the second following this one.
     * No matter what time zone and locale this instance was created with,
     * the returned instance will use the default calendar for time
     * calculations, obtained with {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @return The second following this one.
     */
    @Override
    public RegularTimePeriod next() {
        Second result = null;
        if (this.second != LAST_SECOND_IN_MINUTE) {
            result = new Second(this.second + 1, getMinute());
        }
        else {
            Minute next = (Minute) getMinute().next();
            if (next != null) {
                result = new Second(FIRST_SECOND_IN_MINUTE, next);
            }
        }
        return result;
    }

    /**
     * Returns a serial index number for the minute.
     *
     * @return The serial index number.
     */
    @Override
    public long getSerialIndex() {
        long hourIndex = this.day.getSerialIndex() * 24L + this.hour;
        long minuteIndex = hourIndex * 60L + this.minute;
        return minuteIndex * 60L + this.second;
    }

    /**
     * Returns the first millisecond of the minute.
     *
     * @param calendar  the calendar/timezone ({@code null} not permitted).
     *
     * @return The first millisecond.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     */
    @Override
    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int d = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, d, this.hour, this.minute, this.second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Returns the last millisecond of the second.
     *
     * @param calendar  the calendar/timezone ({@code null} not permitted).
     *
     * @return The last millisecond.
     *
     * @throws NullPointerException if {@code calendar} is {@code null}.
     */
    @Override
    public long getLastMillisecond(Calendar calendar) {
        return getFirstMillisecond(calendar) + 999L;
    }

    /**
     * Tests the equality of this object against an arbitrary Object.
     * <P>
     * This method will return true ONLY if the object is a Second object
     * representing the same second as this instance.
     *
     * @param obj  the object to compare ({@code null} permitted).
     *
     * @return {@code true} if second and minute of this and the object
     *         are the same.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Second)) {
            return false;
        }
        Second that = (Second) obj;
        if (this.second != that.second) {
            return false;
        }
        if (this.minute != that.minute) {
            return false;
        }
        if (this.hour != that.hour) {
            return false;
        }
        if (!this.day.equals(that.day)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this object instance.  The approach described by
     * Joshua Bloch in "Effective Java" has been used here:
     * <p>
     * {@code http://developer.java.sun.com/developer/Books/effectivejava
     * /Chapter3.pdf}
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.second;
        result = 37 * result + this.minute;
        result = 37 * result + this.hour;
        result = 37 * result + this.day.hashCode();
        return result;
    }

    /**
     * Returns an integer indicating the order of this Second object relative
     * to the specified
     * object: negative == before, zero == same, positive == after.
     *
     * @param o1  the object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    @Override
    public int compareTo(Object o1) {
        int result;

        // CASE 1 : Comparing to another Second object
        // -------------------------------------------
        if (o1 instanceof Second) {
            Second s = (Second) o1;
            if (this.firstMillisecond < s.firstMillisecond) {
                return -1;
            }
            else if (this.firstMillisecond > s.firstMillisecond) {
                return 1;
            }
            else {
                return 0;
            }
        }

        // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        else if (o1 instanceof RegularTimePeriod) {
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
     * Creates a new instance by parsing a string.  The string is assumed to
     * be in the format "YYYY-MM-DD HH:MM:SS", perhaps with leading or trailing
     * whitespace.
     *
     * @param s  the string to parse.
     *
     * @return The second, or {@code null} if the string is not parseable.
     */
    public static Second parseSecond(String s) {
        Second result = null;
        s = s.trim();
        String daystr = s.substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day != null) {
            String hmsstr = s.substring(Math.min(daystr.length() + 1,
                    s.length()), s.length());
            hmsstr = hmsstr.trim();

            int l = hmsstr.length();
            String hourstr = hmsstr.substring(0, Math.min(2, l));
            String minstr = hmsstr.substring(Math.min(3, l), Math.min(5, l));
            String secstr = hmsstr.substring(Math.min(6, l), Math.min(8, l));
            int hour = Integer.parseInt(hourstr);

            if ((hour >= 0) && (hour <= 23)) {

                int minute = Integer.parseInt(minstr);
                if ((minute >= 0) && (minute <= 59)) {

                    Minute m = new Minute(minute, new Hour(hour, day));
                    int second = Integer.parseInt(secstr);
                    if ((second >= 0) && (second <= 59)) {
                        result = new Second(second, m);
                    }
                }
            }
        }
        return result;
    }

}
