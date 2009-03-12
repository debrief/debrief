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
 * -------------------------
 * TimeSeriesCollection.java
 * -------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesCollection.java,v 1.2 2006/02/09 15:21:40 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 18-Oct-2001 : Added implementation of IntervalXYDataSource so that bar plots (using numerical
 *               axes) can be plotted from time series data (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 15-Nov-2001 : Added getSeries(...) method (DG);
 *               Changed name from TimeSeriesDataset to TimeSeriesCollection (DG);
 * 07-Dec-2001 : TimeSeries --> BasicTimeSeries (DG);
 * 01-Mar-2002 : Added a time zone offset attribute, to enable fast calculation of the time period
 *               start and end values (DG);
 * 29-Mar-2002 : The collection now registers itself with all the time series objects as a
 *               SeriesChangeListener.  Removed redundant calculateZoneOffset method (DG);
 * 06-Jun-2002 : Added a setting to control whether the x-value supplied in the getXValue(...)
 *               method comes from the START, MIDDLE, or END of the time period.  This is a
 *               workaround for JFreeChart, where the current date axis always labels the start
 *               of a time period (DG);
 * 24-Jun-2002 : Removed unnecessary import (DG);
 * 24-Aug-2002 : Implemented DomainInfo interface, and added the DomainIsPointsInTime flag (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Added remove methods (DG);
 *
 */

package com.jrefinery.data;

import java.io.Serializable;
import java.util.List;
import java.util.Iterator;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * A collection of time series objects.
 * <P>
 * This class implements the IntervalXYDataset interface.  One consequence of
 * this is that this class can be used quite easily to supply data to JFreeChart.
 *
 * @author DG
 */
public class TimeSeriesCollection extends AbstractSeriesDataset
                                  implements IntervalXYDataset, DomainInfo, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** Useful constant for controlling the x-value returned for a time period. */
    public static final int START = 0;

    /** Useful constant for controlling the x-value returned for a time period. */
    public static final int MIDDLE = 1;

    /** Useful constant for controlling the x-value returned for a time period. */
    public static final int END = 2;

    /** Storage for the time series. */
    private List<BasicTimeSeries> data;

    /** A working calendar (to recycle) */
    private Calendar workingCalendar;

    /** The position within a time period to return as the x-value (START, MIDDLE or END). */
    private int position;

    /**
     * A flag that indicates that the domain is 'points in time'.  If this flag is true, only
     * the x-value is used to determine the range of values in the domain, the start and end
     * x-values are ignored.
     */
    private boolean domainIsPointsInTime;

    /**
     * Constructs an empty dataset, tied to the default timezone.
     */
    public TimeSeriesCollection() {
        this(null, TimeZone.getDefault());
    }

    /**
     * Constructs an empty dataset, tied to a specific timezone.
     *
     * @param zone the timezone.
     */
    public TimeSeriesCollection(TimeZone zone) {
        this(null, zone);
    }

    /**
     * Constructs a dataset containing a single series (more can be added),
     * tied to the default timezone.
     *
     * @param series the series.
     */
    public TimeSeriesCollection(BasicTimeSeries series) {
        this(series, TimeZone.getDefault());
    }

    /**
     * Constructs a dataset containing a single series (more can be added),
     * tied to a specific timezone.
     *
     * @param series the series.
     * @param zone the timezone.
     */
    public TimeSeriesCollection(BasicTimeSeries series, TimeZone zone) {

        this.data = new java.util.ArrayList<BasicTimeSeries>();
        if (series != null) {
            data.add(series);
            series.addChangeListener(this);
        }
        this.workingCalendar = Calendar.getInstance(zone);
        this.position = START;
        this.domainIsPointsInTime = true;

    }

    /**
     * Returns the position of the x-value returned for a time period (START,
     * MIDDLE, or END).
     *
     * @return the position.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Sets the position - this controls the x-value that is returned for a
     * particular time period.
     * <P>
     * Use the constants START, MIDDLE and END.
     *
     * @param position the position.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Returns a flag that controls whether the domain is treated as 'points in time'.
     * <P>
     * This flag is used when determining the max and min values for the domain.  If true, then
     * only the x-values are considered for the max and min values.  If false, then the start and
     * end x-values will also be taken into consideration
     *
     * @return the flag.
     */
    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    /**
     * Sets a flag that controls whether the domain is treated as 'points in time', or time
     * periods.
     *
     * @param flag The new value of the flag.
     */
    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns a series.
     *
     * @param series The index of the series (zero-based).
     *
     * @return the series.
     */
    public BasicTimeSeries getSeries(int series) {

        // check arguments...
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "TimeSeriesDataset.getSeries(...): index outside valid range.");
        }

        // fetch the series...
        BasicTimeSeries ts = (BasicTimeSeries) data.get(series);
        return ts;

    }

    /**
     * Returns the name of a series.
     * <P>
     * This method is provided for convenience.
     *
     * @param series The index of the series (zero-based).
     *
     * @return the name of a series.
     */
    public String getSeriesName(int series) {

        // check arguments...delegated
        // fetch the series name...
        return this.getSeries(series).getName();

    }

    /**
     * Adds a series to the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series the time series.
     */
    public void addSeries(BasicTimeSeries series) {

        // check argument...
        if (series == null) {
            throw new IllegalArgumentException(
                "TimeSeriesDataset.addSeries(...): cannot add null series.");
        }

        // add the series...
        data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();

    }

    /**
     * Removes the specified series from the collection.
     *
     * @param series  the series to remove.
     */
    public void removeSeries(BasicTimeSeries series) {

        // check argument...
        if (series == null) {
            throw new IllegalArgumentException(
                "TimeSeriesDataset.addSeries(...): cannot add null series.");
        }

        // remove the series...
        data.remove(series);
        series.removeChangeListener(this);
        fireDatasetChanged();

    }

    /**
     * Removes a series from the collection.
     *
     * @param index  the series index (zero-based).
     */
    public void removeSeries(int index) {

        BasicTimeSeries series = getSeries(index);
        if (series != null) {
            removeSeries(series);
        }

    }

    /**
     * Returns the number of items in the specified series.
     * <P>
     * This method is provided for convenience.
     *
     * @param series The index of the series of interest (zero-based).
     *
     * @return the number of items in the specified series.
     */
    public int getItemCount(int series) {

        return this.getSeries(series).getItemCount();

    }

    /**
     * Returns the x-value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {

        BasicTimeSeries ts = (BasicTimeSeries) data.get(series);
        TimeSeriesDataPair dp = ts.getDataPair(item);
        TimePeriod period = dp.getPeriod();

        return new Long(getX(period));

    }

    /**
     * Returns the x-value for a time period.
     *
     * @param period  the time period.
     *
     * @return the x-value.
     */
    private long getX(TimePeriod period) {

        long result = 0L;
        switch (position) {
            case (START) : result = period.getStart(workingCalendar); break;
            case (MIDDLE) : result = period.getMiddle(workingCalendar); break;
            case (END) : result = period.getEnd(workingCalendar); break;
            default: result = period.getMiddle(workingCalendar);
        }
        return result;

    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the starting X value for the specified series and item.
     */
    public Number getStartXValue(int series, int item) {

        BasicTimeSeries ts = (BasicTimeSeries) data.get(series);
        TimeSeriesDataPair dp = ts.getDataPair(item);
        return new Long(dp.getPeriod().getStart(workingCalendar));

    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item  The item (zero-based index).
     *
     * @return the ending X value for the specified series and item.
     */
    public Number getEndXValue(int series, int item) {

        BasicTimeSeries ts = (BasicTimeSeries) data.get(series);
        TimeSeriesDataPair dp = ts.getDataPair(item);
        return new Long(dp.getPeriod().getEnd(workingCalendar));

    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the y-value for the specified series and item.
     */
    public Number getYValue(int series, int item) {

        BasicTimeSeries ts = (BasicTimeSeries) data.get(series);
        TimeSeriesDataPair dp = (TimeSeriesDataPair) ts.getDataPair(item);
        return dp.getValue();

    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the starting Y value for the specified series and item.
     */
    public Number getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item The item (zero-based index).
     *
     * @return the ending Y value for the specified series and item.
     */
    public Number getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the minimum value in the dataset (or null if all the values in
     * the domain are null).
     *
     * @return the minimum value.
     */
    public Number getMinimumDomainValue() {

        Range r = getDomainRange();
        return new Double(r.getLowerBound());

    }

    /**
     * Returns the maximum value in the dataset (or null if all the values in
     * the domain are null).
     *
     * @return the maximum value.
     */
    public Number getMaximumDomainValue() {

        Range r = getDomainRange();
        return new Double(r.getUpperBound());

    }

    /**
     * Returns the range of the values in the series domain.
     *
     * @return the range.
     */
    public Range getDomainRange() {

        Range result = null;
        Range temp = null;
        Iterator<BasicTimeSeries> iterator = data.iterator();
        while (iterator.hasNext()) {
            BasicTimeSeries series = (BasicTimeSeries) iterator.next();
            int count = series.getItemCount();
            if (count > 0) {
                TimePeriod start = series.getTimePeriod(0);
                TimePeriod end = series.getTimePeriod(count - 1);
                if (this.domainIsPointsInTime) {
                    temp = new Range(getX(start), getX(end));
                }
                else {
                    temp = new Range(start.getStart(workingCalendar),
                                     end.getEnd(workingCalendar));
                }
                result = Range.combine(result, temp);
            }
        }

        return result;

    }

}
