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
 * XYSeriesCollection.java
 * -----------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Aaron Metzger;
 *
 * $Id: XYSeriesCollection.java,v 1.1.1.1 2003/07/17 10:06:57 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 03-Apr-2002 : Added change listener code (DG);
 * 29-Apr-2002 : Added removeSeries, removeAllSeries methods (ARM);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

import java.util.List;

/**
 * Represents a collection of time series that can be used as a dataset.
 *
 * @author DG
 */
public class XYSeriesCollection extends AbstractSeriesDataset implements XYDataset {

    /** The series that are included in the collection. */
    private List data;

    /**
     * Constructs an empty dataset.
     */
    public XYSeriesCollection() {

        this.data = new java.util.ArrayList();

    }

    /**
     * Constructs a dataset and populates it with a single time series.
     *
     * @param series  the time series.
     */
    public XYSeriesCollection(XYSeries series) {

        this.data = new java.util.ArrayList();
        if (series != null) {
            data.add(series);
            series.addChangeListener(this);
        }

    }

    /**
     * Adds a series to the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series  the series.
     */
    public void addSeries(XYSeries series) {

        // check arguments...
        if (series == null) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.addSeries(...): cannot add null series.");
        }

        // add the series...
        data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();

    }

    /**
     * Returns the number of series in the collection.
     *
     * @return the number of series in the collection.
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The series.
     */
    public XYSeries getSeries(int series) {

        // check arguments...
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.getSeries(...): index outside valid range.");
        }

        // fetch the series...
        XYSeries ts = (XYSeries) data.get(series);
        return ts;

    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of a series.
     */
    public String getSeriesName(int series) {

        // check arguments...delegated

        // fetch the result...
        return getSeries(series).getName();

    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the number of items in the specified series.
     */
    public int getItemCount(int series) {

        // check arguments...delegated

        // fetch the result...
        return getSeries(series).getItemCount();

    }

    /**
     * Returns the x-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {

        XYSeries ts = (XYSeries) data.get(series);
        XYDataPair dp = ts.getDataPair(item);
        return dp.getX();

    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param index  the index of the item of interest (zero-based).
     *
     * @return the y-value for the specified series and item.
     */
    public Number getYValue(int series, int index) {

        XYSeries ts = (XYSeries) data.get(series);
        XYDataPair dp = ts.getDataPair(index);
        return dp.getY();

    }

    /**
     * Removes all the series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     */
    public void removeAllSeries() {


        // Unregister the collection as a change listener to each series in the collection.
        for (int i = 0; i < data.size(); i++) {
          XYSeries series = (XYSeries) data.get(i);
          series.removeChangeListener(this);
        }

        // Remove all the series from the collection and notify listeners.
        data.clear();
        this.fireDatasetChanged();

    }

    /**
     * Removes a series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series  the series.
     */
    public void removeSeries(XYSeries series) {

        // check arguments...
        if (series == null) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.removeSeries(...): cannot remove null series.");
        }

        // remove the series...
        if (data.contains(series)) {
            series.removeChangeListener(this);
            data.remove(series);
            fireDatasetChanged();
        }

    }

    /**
     * Removes a series from the collection.
     * <P>
     * Notifies all registered listeners that the dataset has changed.
     *
     * @param series  the series(zero based index).
     */
    public void removeSeries(int series) {

        // check arguments...
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "XYSeriesCollection.getSeries(...): index outside valid range.");
        }

        // fetch the series, remove the change listener, then remove the series.
        XYSeries ts = (XYSeries) data.get(series);
        ts.removeChangeListener(this);
        data.remove(series);
        fireDatasetChanged();

    }

}
