/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.  All rights reserved.
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
 * --------------------
 * BasicTimeSeries.java
 * --------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: BasicTimeSeries.java,v 1.2 2006/02/09 15:21:20 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 14-Nov-2001 : Added listener mechanism (DG);
 * 15-Nov-2001 : Updated argument checking and exceptions in add(...) method (DG);
 * 29-Nov-2001 : Added properties to describe the domain and range (DG);
 * 07-Dec-2001 : Renamed TimeSeries --> BasicTimeSeries (DG);
 * 01-Mar-2002 : Updated import statements (DG);
 * 28-Mar-2002 : Added a method add(TimePeriod, double) (DG);
 * 27-Aug-2002 : Changed return type of delete method to void (DG);
 * 04-Oct-2002 : Added itemCount and historyCount attributes, fixed errors reported by
 *               Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

/**
 * Represents a sequence of zero or more data pairs in the form (period, value).
 *
 * @author DG
 */
public class BasicTimeSeries extends Series implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/** Default value for the domain description. */
    protected static final String DEFAULT_DOMAIN_DESCRIPTION = "Time";

    /** Default value for the range description. */
    protected static final String DEFAULT_RANGE_DESCRIPTION = "Value";

    /** A description of the domain. */
    private String domain;

    /** A description of the range. */
    private String range;

    /** The type of period for the data. */
    private Class<?> timePeriodClass;

    /** The list of data pairs in the series. */
    @SuppressWarnings("unchecked")
		private List data;

    /** The maximum number of items for the series. */
    private int maximumItemCount;

    /** The history count. */
    private int historyCount;

    /**
     * Creates a new (empty) time series.
     * <P>
     * By default, a daily time series is created.  Use one of the other
     * constructors if you require a different time period.
     *
     * @param name  the name of the series.
     */
    public BasicTimeSeries(String name) {

        this(name,
             DEFAULT_DOMAIN_DESCRIPTION,
             DEFAULT_RANGE_DESCRIPTION,
             Day.class);

    }

    /**
     * Creates a new  (empty) time series.
     *
     * @param name  the series name.
     * @param timePeriodClass  the type of time period.
     */
    public BasicTimeSeries(String name, Class<?> timePeriodClass) {

        this(name,
             DEFAULT_DOMAIN_DESCRIPTION,
             DEFAULT_RANGE_DESCRIPTION,
             timePeriodClass);

    }

    /**
     * Creates a new time series that contains no data.
     * <P>
     * Descriptions can be specified for the domain and range.  One situation
     * where this is helpful is when generating a chart for the time series -
     * axis labels can be taken from the domain and range description.
     *
     * @param name  the name of the series.
     * @param domain  the domain description.
     * @param range  the range description.
     * @param timePeriodClass  the type of time period.
     */
    public BasicTimeSeries(String name, String domain, String range, Class<?> timePeriodClass) {

        super(name);
        this.domain = domain;
        this.range = range;
        this.timePeriodClass = timePeriodClass;
        data = new java.util.ArrayList<TimeSeriesDataPair>();
        this.maximumItemCount = Integer.MAX_VALUE;
        this.historyCount = 0;

    }

    /**
     * Returns the domain description.
     *
     * @return the domain description.
     */
    public String getDomainDescription() {
        return this.domain;
    }

    /**
     * Sets the domain description.
     * <P>
     * A property change event is fired, and an undoable edit is posted.
     *
     * @param description  the new description.
     */
    public void setDomainDescription(String description) {

        String old = this.domain;
        this.domain = description;
        firePropertyChange("Domain", old, description);

    }

    /**
     * Returns the range description.
     *
     * @return the range description.
     */
    public String getRangeDescription() {
        return this.range;
    }

    /**
     * Sets the range description.
     * <P>
     * Registered listeners are notified of the change.
     *
     * @param description  the new description.
     */
    public void setRangeDescription(String description) {

        String old = this.range;
        this.range = description;
        firePropertyChange("Range", old, description);

    }

    /**
     * Returns the number of items in the series.
     *
     * @return the item count.
     */
    public int getItemCount() {
        return data.size();
    }

    /**
     * Returns the maximum number of items that will be retained in the series.
     * <P>
     * The default value is <code>Integer.MAX_VALUE</code>).
     *
     * @return the maximum item count.
     */
    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    /**
     * Sets the maximum number of items that will be retained in the series.
     * <P>
     * If you add a new item to the series such that the number of items will exceed the
     * maximum item count, then the FIRST element in the series is automatically removed,
     * ensuring that the maximum item count is not exceeded.
     *
     * @param maximum  the maximum.
     */
    public void setMaximumItemCount(int maximum) {
        this.maximumItemCount = maximum;
    }

    /**
     * Returns the history count for the series.
     *
     * @return the history count.
     */
    public int getHistoryCount() {
        return this.historyCount;
    }

    /**
     * Sets the number of time units in the 'history' for the series.
     * <P>
     * This provides one mechanism for automatically dropping old data from the time series.
     * For example, if a series contains daily data, you might set the history count to 30.
     * Then, when you add a new data item, all data items more than 30 days older than the latest
     * value are automatically dropped from the series.
     *
     * @param periods  the number of time periods.
     */
    public void setHistoryCount(int periods) {
        this.historyCount = periods;
    }

    /**
     * Returns the time period class for this series.
     * <p>
     * Only one time period class can be used within a single series (enforced).
     * If you add a data item with a Year for the time period, then all
     * subsequent data items must also have a Year for the time period.
     *
     * @return the time period class for this series (null if the series is empty).
     */
    public Class<?> getTimePeriodClass() {

        return this.timePeriodClass;

    }

    /**
     * Returns one data pair for the series.
     *
     * @param index  the index within the series (zero-based).
     *
     * @return one data pair for the series.
     */
    public TimeSeriesDataPair getDataPair(int index) {

        return (TimeSeriesDataPair) data.get(index);

    }

    /**
     * Returns the data pair for a specific period.
     *
     * @param period  the period of interest.
     *
     * @return the data pair matching the specified period (or null if there is no match).
     *
     */
    @SuppressWarnings("unchecked")
		public TimeSeriesDataPair getDataPair(TimePeriod period) {

        // check arguments...
        if (period == null) {
            throw new IllegalArgumentException(
                "TimeSeries.getDataPair(...): null time period not allowed.");
        }

        // fetch the value...
        TimeSeriesDataPair dummy = new TimeSeriesDataPair(period, new Integer(0));
        int index = Collections.binarySearch(data, dummy);
        if (index >= 0) {
            return (TimeSeriesDataPair) data.get(index);
        }
        else {
            return null;
        }

    }

    /**
     * Returns the time period at the specified index.
     *
     * @param index  the index of the data pair.
     *
     * @return the time period at the specified index.
     */
    public TimePeriod getTimePeriod(int index) {
        return getDataPair(index).getPeriod();
    }

    /**
     * Returns a time period that would be the next in sequence on the end of
     * the time series.
     *
     * @return the next time period.
     */
    public TimePeriod getNextTimePeriod() {

        TimePeriod last = this.getTimePeriod(this.getItemCount() - 1);
        return last.next();

    }

    /**
     * Returns a collection of all the time periods in the time series.
     *
     * @return a collection of all the time periods.
     */
    public Collection<TimePeriod> getTimePeriods() {

        Collection<TimePeriod> result = new java.util.ArrayList<TimePeriod>();

        for (int i = 0; i < this.getItemCount(); i++) {
            result.add(getTimePeriod(i));
        }

        return result;

    }

    /**
     * Returns a collection of time periods in the specified series, but not in
     * this series, and therefore unique to the specified series.
     *
     * @param series  the series to check against this one.
     *
     * @return the series minus 'this'.
     */
    public Collection<TimePeriod> getTimePeriodsUniqueToOtherSeries(BasicTimeSeries series) {

        Collection<TimePeriod> result = new java.util.ArrayList<TimePeriod>();

        for (int i = 0; i < series.getItemCount(); i++) {
            TimePeriod period = series.getTimePeriod(i);
            int index = getIndex(period);
            if (index < 0) {
                result.add(period);
            }

        }

        return result;

    }

    /**
     * Returns the index of the specified time period.
     *
     * @param period  time period to lookup.
     *
     * @return the index of the specified time period.
     */
    @SuppressWarnings("unchecked")
		public int getIndex(TimePeriod period) {

        if (period != null) {
            // fetch the value...
            TimeSeriesDataPair dummy = new TimeSeriesDataPair(period, new Integer(0));
            int index = Collections.binarySearch(data, dummy);
            return index;
        }
        else {
            return -1;
        }

    }

    /**
     * Returns the value at the specified index.
     *
     * @param index  index of a value.
     *
     * @return the value at the specified index.
     */
    public Number getValue(int index) {
        return getDataPair(index).getValue();
    }

    /**
     * Returns the value for a time period.
     *
     * @param period  time period to lookup.
     *
     * @return the value or <code>null</code> if the time period is not in the series.
     */
    public Number getValue(TimePeriod period) {

        int index = this.getIndex(period);
        if (index >= 0) {
            return getValue(index);
        }
        else {
            return null;
        }

    }

    /**
     * Adds a data item to the series.
     *
     * @param pair  the (timeperiod, value) pair.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    @SuppressWarnings({ "unchecked" })
		public void add(TimeSeriesDataPair pair) throws SeriesException {

        // check arguments...
        if (pair == null) {
            throw new IllegalArgumentException("TimeSeries.add(...): null item not allowed.");
        }

        if (!pair.getPeriod().getClass().equals(timePeriodClass)) {
            String message = "BasicTimeSeries.add(): you are trying to add data where the time ";
            message = message + "period class is " + pair.getPeriod().getClass().getName() + ", ";
            message = message + "but the BasicTimeSeries is expecting an instance of "
                              + timePeriodClass.getName() + ".";
            throw new SeriesException(message);
        }


        // make the change (if it's not a duplicate time period)...
        int index = Collections.binarySearch(data, pair);
        if (index < 0) {
            this.data.add(-index - 1, pair);

            // check if this addition will exceed the maximum item count...
            if (getItemCount() > this.maximumItemCount) {
                this.data.remove(0);
            }

            // check if there are any values earlier than specified by the history count...
            if ((getItemCount() > 1) && (this.historyCount > 0)) {
                long latest = getTimePeriod(getItemCount() - 1).getSerialIndex();
                while ((latest - getTimePeriod(0).getSerialIndex()) >= historyCount) {
                    this.data.remove(0);
                }
            }
            fireSeriesChanged();
        }
        else {
            throw new SeriesException("TimeSeries.add(...): time period already exists.");
        }

    }

    /**
     * Adds a new data item to the series.
     *
     * @param period  the time period.
     * @param value  the value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(TimePeriod period, double value) throws SeriesException {

        TimeSeriesDataPair pair = new TimeSeriesDataPair(period, value);
        add(pair);

    }

    /**
     * Adds a new data item to the series.
     *
     * @param period  the time period.
     * @param value  the value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    public void add(TimePeriod period, Number value) throws SeriesException {

        TimeSeriesDataPair pair = new TimeSeriesDataPair(period, value);
        add(pair);

    }

    /**
     * Updates (changes) the value for a time period.  Ignores the update if
     * the period does not exist.
     *
     * @param period  the period to update.
     * @param value  the new value.
     *
     * @throws SeriesException if there is a problem adding the data.
     */
    @SuppressWarnings("unchecked")
		public void update(TimePeriod period, Number value) throws SeriesException {

        TimeSeriesDataPair temp = new TimeSeriesDataPair(period, value);
        int index = Collections.binarySearch(data, temp);
        if (index >= 0) {
            TimeSeriesDataPair pair = (TimeSeriesDataPair) data.get(index);
            pair.setValue(value);
            fireSeriesChanged();
        }
        else {
            throw new SeriesException("TimeSeries.update(TimePeriod, Number): "
                                      + "period does not exist.");
        }

    }

    /**
     * Updates (changes) the value of a data pair.
     *
     * @param index  the index of the data pair to update.
     * @param value  the new value.
     */
    public void update(int index, Number value) {

        TimeSeriesDataPair pair = getDataPair(index);
        pair.setValue(value);
        fireSeriesChanged();

    }

    /**
     * Adds or updates data from one series to another.  Returns another series
     * containing the values that were overwritten.
     *
     * @param series  the series to merge with this.
     *
     * @return series containing the values that were overwritten.
     */
    public BasicTimeSeries addAndOrUpdate(BasicTimeSeries series) {

        BasicTimeSeries overwritten = new BasicTimeSeries("Overwritten values from: " + getName());

        for (int i = 0; i < series.getItemCount(); i++) {
            TimeSeriesDataPair pair = series.getDataPair(i);
            TimeSeriesDataPair oldPair = addOrUpdate(pair.getPeriod(), pair.getValue());
            if (oldPair != null) {
                try {
                    overwritten.add(oldPair);
                }
                catch (SeriesException e) {  // should not get here...
                    System.err.println("TimeSeries.addAndOrUpdate(series): "
                                       + "unable to add data to overwritten series.");
                }
            }
        }

        return overwritten;

    }

    /**
     * Adds or updates the times series.
     *
     * @param period  the time period to add/update.
     * @param value  the new value.
     *
     * @return A copy of the overwritten data pair (or null).
     */
    @SuppressWarnings("unchecked")
		public TimeSeriesDataPair addOrUpdate(TimePeriod period, Number value) {

        TimeSeriesDataPair overwritten = null;

        TimeSeriesDataPair key = new TimeSeriesDataPair(period, value);
        int index = Collections.binarySearch(data, key);
        if (index >= 0) {
            TimeSeriesDataPair existing = (TimeSeriesDataPair) data.get(index);
            overwritten = (TimeSeriesDataPair) existing.clone();
            existing.setValue(value);
            fireSeriesChanged();
        }
        else {
            data.add(-index - 1, new TimeSeriesDataPair(period, value));
        }

        return overwritten;

    }

    /**
     * Deletes data for the given time period.
     *
     * @param period  period to delete.
     */
    public void delete(TimePeriod period) {
        int index = this.getIndex(period);
        data.remove(index);
    }

    /**
     * Deletes data from start until end index (end inclusive).
     *
     * @param start  the index of the first period to delete.
     * @param end  the index of the last period to delete.
     */
    public void delete(int start, int end) {

        for (int i = 0; i <= (end - start); i++) {
            this.data.remove(start);
        }
        fireSeriesChanged();

    }

    /**
     * Returns a clone of the time series.
     * <P>
     * Notes:
     * <ul>
     *   <li>
     *     no need to clone the domain and range descriptions, since String object is immutable;
     *   </li>
     *   <li>
     *     we pass over to the more general method clone(start, end).
     *   </li>
     * </ul>
     *
     * @return a clone of the time series.
     */
    public Object clone() {

        Object clone = createCopy(0, this.getItemCount() - 1);
        return clone;

    }

    /**
     * Creates a new timeseries by copying a subset of the data in this time
     * series.
     *
     * @param start  the index of the first time period to copy.
     * @param end  the index of the last time period to copy.
     *
     * @return a series containing a copy of this times series from start until end.
     */
    @SuppressWarnings("unchecked")
		public BasicTimeSeries createCopy(int start, int end) {

        BasicTimeSeries copy = (BasicTimeSeries) super.clone();

        copy.data = new java.util.ArrayList();
        if (data.size() > 0) {
            for (int index = start; index <= end; index++) {
                TimeSeriesDataPair pair = (TimeSeriesDataPair) this.data.get(index);
                TimeSeriesDataPair clone = (TimeSeriesDataPair) pair.clone();
                try {
                    copy.add(clone);
                }
                catch (SeriesException e) {
                    System.err.println("TimeSeries.createCopy(): unable to add cloned data pair.");
                }
            }
        }

        return copy;

    }

    /**
     * Creates a new timeseries by copying a subset of the data in this time series.
     *
     * @param start  the first time period to copy.
     * @param end  the last time period to copy.
     *
     * @return a time series containing a copy of this time series from start until end.
     */
    public BasicTimeSeries createCopy(TimePeriod start, TimePeriod end) {

        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        BasicTimeSeries copy = createCopy(startIndex, endIndex);
        return copy;

    }

}
