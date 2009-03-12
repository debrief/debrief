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
 * -------------
 * DateAxis.java
 * -------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jonathan Nash;
 *
 * $Id: DateAxis.java,v 1.1.1.1 2003/07/17 10:06:22 Ian.Mayo Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 27-Nov-2001 : Changed constructors from public to protected, updated Javadoc comments (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 26-Feb-2002 : Updated import statements (DG);
 * 22-Apr-2002 : Added a setRange() method (DG);
 * 25-Jun-2002 : Removed redundant local variable (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 21-Aug-2002 : The setTickUnit(...) method now turns off auto-tick unit selection (fix for
 *               bug id 528885) (DG);
 * 05-Sep-2002 : Updated the constructors to reflect changes in the Axis class (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 25-Sep-2002 : Added new setRange(...) methods, and deprecated setAxisRange(...) (DG);
 * 04-Oct-2002 : Changed auto tick selection to parallel number axis classes (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.data.Range;
import com.jrefinery.data.DateRange;

/**
 * The base class for axes that display java.util.Date values.
 * <P>
 * You will find it easier to understand how this axis works if you bear in mind that it really
 * displays/measures integer (or long) data, where the integers are milliseconds since midnight,
 * 1-Jan-1970.  When displaying tick labels, the millisecond values are converted back to dates
 * using a DateFormat instance.
 *
 * @see HorizontalDateAxis
 *
 * @author DG
 */
public abstract class DateAxis extends ValueAxis {

    /** The default date tick unit. */
    public static final DateTickUnit DEFAULT_DATE_TICK_UNIT
        = new DateTickUnit(DateTickUnit.DAY, 1, new SimpleDateFormat());

    /** The default anchor date. */
    public static final Date DEFAULT_ANCHOR_DATE = new Date();

    /** The default crosshair date. */
    public static final Date DEFAULT_CROSSHAIR_DATE = null;

    /** The current tick unit. */
    private DateTickUnit tickUnit;

    /**
     * The anchor date (needs to be synchronised with the anchorValue in the
     * ValueAxis superclass, as this form is maintained for convenience only).
     */
    private Date anchorDate;

    /**
     * The crosshair date (needs to be synchronised with the crosshairValue in
     * the ValueAxis superclass, as this form is maintained for convenience only).
     */
    private Date crosshairDate;

    /**
     * Constructs a date axis, using default values where necessary.
     *
     * @param label  the axis label (null permitted).
     */
    protected DateAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT,
             ValueAxis.DEFAULT_AUTO_RANGE,
             ValueAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE,
             new DateRange(),
             true, // auto tick unit selection
             DateAxis.createStandardDateTickUnits(),
             new DateTickUnit(DateTickUnit.DAY, 1, new SimpleDateFormat()),
             new SimpleDateFormat(),
             true, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             DateAxis.DEFAULT_ANCHOR_DATE,
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             DEFAULT_CROSSHAIR_DATE,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

    }

    /**
     * Constructs a date axis.
     *
     * @param label  the axis label (null permitted).
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param tickLabelsVisible  flag indicating whether or not tick labels are visible.
     * @param tickLabelFont  the font used to display tick labels.
     * @param tickLabelPaint  the paint used to draw tick labels.
     * @param tickLabelInsets  determines the amount of blank space around tick labels.
     * @param tickMarksVisible  flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     * @param autoRange  flag indicating whether the axis range is automatically adjusted to
     *                   fit the data.
     * @param autoRangeMinimumSize  the smallest range allowed when the axis range is calculated to
     *                              fit the data.
     * @param range  the axis range.
     * @param autoTickUnitSelection  a flag indicating whether the tick unit is automatically
     *                               selected.
     * @param standardTickUnits  the standard tick units.
     * @param tickUnit  the tick unit.
     * @param tickLabelFormatter  formatter to use for date formatting.
     * @param gridLinesVisible  flag indicating whether or not grid lines are visible.
     * @param gridStroke  the Stroke used to display grid lines (if visible).
     * @param gridPaint  the Paint used to display grid lines (if visible).
     * @param anchorDate  the anchor date.
     * @param crosshairVisible  a flag controlling whether the crosshair is visible for this axis.
     * @param crosshairDate  the crosshair date.
     * @param crosshairStroke  the crosshair stroke.
     * @param crosshairPaint  the crosshair paint.
     */
    protected DateAxis(String label,
                       Font labelFont, Paint labelPaint, Insets labelInsets,
                       boolean tickLabelsVisible,
                       Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
                       boolean tickMarksVisible,
                       Stroke tickMarkStroke, Paint tickMarkPaint,
                       boolean autoRange,
                       Number autoRangeMinimumSize,
                       Range range,
                       boolean autoTickUnitSelection,
                       TickUnits standardTickUnits,
                       DateTickUnit tickUnit,
                       SimpleDateFormat tickLabelFormatter,
                       boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                       Date anchorDate,
                       boolean crosshairVisible, Date crosshairDate, Stroke crosshairStroke,
                       Paint crosshairPaint) {

        super(label, labelFont, labelPaint, labelInsets,
              tickLabelsVisible, tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible, tickMarkStroke, tickMarkPaint,
              range,
              autoRange, autoRangeMinimumSize,
              autoTickUnitSelection,
              standardTickUnits,
              gridLinesVisible, gridStroke, gridPaint,
              0.0,
              crosshairVisible, 0.0,
              crosshairStroke, crosshairPaint);

        if (autoRangeMinimumSize.longValue() <= 0) {
            setAutoRangeMinimumSizeAttribute(new Long(4));
        }

        this.anchorDate = anchorDate;
        if (anchorDate != null) {
            setAnchorValueAttribute((double) anchorDate.getTime());
        }

        this.crosshairDate = crosshairDate;
        if (crosshairDate != null) {
            setCrosshairValueAttribute((double) crosshairDate.getTime());
        }

        this.tickUnit = tickUnit;

    }

    /**
     * Sets the upper and lower bounds for the axis.  Registered listeners are
     * notified of the change.
     * <P>
     * As a side-effect, the auto-range flag is set to false.
     *
     * @param range  the new range.
     */
    public void setRange(Range range) {

        // check arguments...
        if (range == null) {
            throw new IllegalArgumentException("DateAxis.setRange(...): null not permitted.");
        }

        // usually the range will be a DateRange, but if it isn't do a conversion...
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }

        setAutoRangeAttribute(false);
        setRangeAttribute(range);
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Sets the axis range.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    public void setRange(Date lower, Date upper) {

        // check arguments...
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("DateAxis.setRange(...): lower not before upper.");
        }

        // make the change...
        setRange(new DateRange(lower, upper));

    }

    /**
     * Sets the axis range.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    public void setRange(double lower, double upper) {

        // check arguments...
        if (lower >= upper) {
            throw new IllegalArgumentException("DateAxis.setRange(...): lower >= upper.");
        }

        // make the change...
        setRange(new DateRange(lower, upper));

    }

    /**
     * Returns the earliest date visible on the axis.
     *
     * @return the earliest date visible on the axis.
     */
    public Date getMinimumDate() {

        Date result = null;

        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getLowerDate();
        }
        else {
            result = new Date((long) range.getLowerBound());
        }

        return result;

    }

    /**
     * Sets the minimum date visible on the axis.
     *
     * @param minimumDate  the new minimum date.
     */
    public void setMinimumDate(Date minimumDate) {

        setRangeAttribute(new DateRange(minimumDate, getMaximumDate()));
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns the latest date visible on the axis.
     *
     * @return the latest date visible on the axis.
     */
    public Date getMaximumDate() {

        Date result = null;

        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getUpperDate();
        }
        else {
            result = new Date((long) range.getUpperBound());
        }

        return result;

    }

    /**
     * Sets the maximum date visible on the axis.
     *
     * @param maximumDate  the new maximum date.
     */
    public void setMaximumDate(Date maximumDate) {

        setRangeAttribute(new DateRange(getMinimumDate(), maximumDate));
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns the tick unit for the axis.
     *
     * @return  the tick unit for the axis.
     */
    public DateTickUnit getTickUnit() {
        return tickUnit;
    }

    /**
     * Sets the tick unit for the axis.
     *
     * @param unit  the new date unit.
     */
    public void setTickUnit(DateTickUnit unit) {

        setAutoTickUnitSelectionAttribute(false);
        this.tickUnit = unit;
        this.notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Sets the tick unit attribute without any other side effects.
     *
     * @param unit  the new tick unit.
     */
    protected void setTickUnitAttribute(DateTickUnit unit) {
        this.tickUnit = unit;
    }

    /**
     * Returns the anchor date for the axis.
     *
     * @return the anchor date for the axis (possibly null).
     */
    public Date getAnchorDate() {
        return this.anchorDate;
    }

    /**
     * Sets the anchor date for the axis.
     *
     * @param anchorDate  the new anchor date (null permitted).
     */
    public void setAnchorDate(Date anchorDate) {

        this.anchorDate = anchorDate;
        double millis = (double) anchorDate.getTime();
        super.setAnchorValue(millis);

    }

    /**
     * Sets the anchor value.
     * <p>
     * This method keeps the anchorDate and anchorValue in synch.
     *
     * @param value  the new value.
     */
    public void setAnchorValue(double value) {
        long millis = (long) value;
        this.anchorDate.setTime(millis);
        super.setAnchorValue(value);
    }

    /**
     * Returns the crosshair date for the axis.
     *
     * @return the crosshair date for the axis (possibly null).
     */
    public Date getCrosshairDate() {
        return this.crosshairDate;
    }

    /**
     * Sets the crosshair date for the axis.
     *
     * @param crosshairDate  the new crosshair date (null permitted).
     */
    public void setCrosshairDate(Date crosshairDate) {

        this.crosshairDate = crosshairDate;
        if (crosshairDate != null) {
            double millis = (double) crosshairDate.getTime();
            this.setCrosshairValue(millis);
        }
        else {
            this.setCrosshairVisible(false);
        }

    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @param unit      date unit to use.
     *
     * @return The value of the lowest visible tick on the axis.
     */
    public Date calculateLowestVisibleTickValue(DateTickUnit unit) {

        return nextStandardDate(getMinimumDate(), unit);

    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     *
     * @param unit  date unit to use.
     *
     * @return the value of the highest visible tick on the axis.
     */
    public Date calculateHighestVisibleTickValue(DateTickUnit unit) {

        return previousStandardDate(getMaximumDate(), unit);

    }

    /**
     * Returns the previous "standard" date, for a given date and tick unit.
     *
     * @param date  the reference date.
     * @param unit  the tick unit.
     *
     * @return the previous "standard" date.
     */
    protected Date previousStandardDate(Date date, DateTickUnit unit) {

        int seconds;
        int minutes;
        int hours;
        int days;
        int months;
        int years;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int count = unit.getCount();
        int current = calendar.get(unit.getCalendarField());
        int value = count * (current / count);

        switch (unit.getUnit()) {

            case (DateTickUnit.MILLISECOND) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);
                seconds = calendar.get(Calendar.SECOND);
                calendar.set(years, months, days, hours, minutes, seconds);
                calendar.set(Calendar.MILLISECOND, value);
                return calendar.getTime();
            }

            case (DateTickUnit.SECOND) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                minutes = calendar.get(Calendar.MINUTE);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, days, hours, minutes, value);
                return calendar.getTime();
            }

            case (DateTickUnit.MINUTE) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                hours = calendar.get(Calendar.HOUR_OF_DAY);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, days, hours, value, 0);
                return calendar.getTime();
            }

            case (DateTickUnit.HOUR) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                days = calendar.get(Calendar.DATE);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, days, value, 0, 0);
                return calendar.getTime();
            }

            case (DateTickUnit.DAY) : {
                years = calendar.get(Calendar.YEAR);
                months = calendar.get(Calendar.MONTH);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, months, value, 0, 0, 0);
                return calendar.getTime();
            }

            case (DateTickUnit.MONTH) : {
                years = calendar.get(Calendar.YEAR);
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(years, value, 1, 0, 0, 0);
                return calendar.getTime();
            }

            case(DateTickUnit.YEAR) : {
                calendar.clear(Calendar.MILLISECOND);
                calendar.set(value, 0, 1, 0, 0, 0);
                return calendar.getTime();
            }

            default: return null;

        }

    }

    /**
     * Returns the first "standard" date (based on the specified field and units).
     *
     * @param date  the reference date.
     * @param unit  the date tick unit.
     *
     * @return the next "standard" date.
     */
    protected Date nextStandardDate(Date date, DateTickUnit unit) {

        Date previous = previousStandardDate(date, unit);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getCount());
        return calendar.getTime();

    }

    /**
     * Returns a collection of standard date tick units.  This collection will be used by default,
     * but you are free to create your own collection if you want to (see the
     * setStandardTickUnits(...) method inherited from the ValueAxis class).
     *
     * @return a collection of standard date tick units.
     */
    public static TickUnits createStandardDateTickUnits() {

        TickUnits units = new TickUnits();

        // milliseconds
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 1,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 5,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 10,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 25,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 50,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 100,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 250,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));
        units.add(new DateTickUnit(DateTickUnit.MILLISECOND, 500,
                                   new SimpleDateFormat("HH:mm:ss.SSS")));

        // seconds
        units.add(new DateTickUnit(DateTickUnit.SECOND, 1, new SimpleDateFormat("HH:mm:ss")));
        units.add(new DateTickUnit(DateTickUnit.SECOND, 5, new SimpleDateFormat("HH:mm:ss")));
        units.add(new DateTickUnit(DateTickUnit.SECOND, 10, new SimpleDateFormat("HH:mm:ss")));
        units.add(new DateTickUnit(DateTickUnit.SECOND, 30, new SimpleDateFormat("HH:mm:ss")));

        // minutes
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 1, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 2, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 5, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 10, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 15, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 20, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.MINUTE, 30, new SimpleDateFormat("HH:mm")));

        // hours
        units.add(new DateTickUnit(DateTickUnit.HOUR, 1, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.HOUR, 2, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.HOUR, 4, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.HOUR, 6, new SimpleDateFormat("HH:mm")));
        units.add(new DateTickUnit(DateTickUnit.HOUR, 12, new SimpleDateFormat("d-MMM, HH:mm")));

        // days
        units.add(new DateTickUnit(DateTickUnit.DAY, 1, new SimpleDateFormat("d-MMM")));
        units.add(new DateTickUnit(DateTickUnit.DAY, 2, new SimpleDateFormat("d-MMM")));
        units.add(new DateTickUnit(DateTickUnit.DAY, 7, new SimpleDateFormat("d-MMM")));
        units.add(new DateTickUnit(DateTickUnit.DAY, 15, new SimpleDateFormat("d-MMM")));

        // months
        units.add(new DateTickUnit(DateTickUnit.MONTH, 1, new SimpleDateFormat("MMM-yyyy")));
        units.add(new DateTickUnit(DateTickUnit.MONTH, 2, new SimpleDateFormat("MMM-yyyy")));
        units.add(new DateTickUnit(DateTickUnit.MONTH, 6, new SimpleDateFormat("MMM-yyyy")));

        // years
        units.add(new DateTickUnit(DateTickUnit.YEAR, 1, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 2, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 5, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 10, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 25, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 50, new SimpleDateFormat("yyyy")));
        units.add(new DateTickUnit(DateTickUnit.YEAR, 100, new SimpleDateFormat("yyyy")));

        return units;

    }

    /**
     * Sets the axis range.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     *
     * @deprecated use setRange(double, double) method.
     */
    public void setAxisRange(double lower, double upper) {

        setRange(lower, upper);

    }

}
