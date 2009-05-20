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
 * DefaultWindDataset.java
 * -----------------------
 * (C) Copyright 2001, 2002, by Achilleus Mantzios and Contributors.
 *
 * Original Author:  Achilleus Mantzios;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: DefaultWindDataset.java,v 1.1.1.1 2003/07/17 10:06:52 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 06-Feb-2002 : Version 1, based on code contributed by Achilleus Mantzios (DG);
 *
 */

package com.jrefinery.legacy.data;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.Date;

/**
 * A default implementation of the WindDataset interface.
 *
 * @author AM
 */
public class DefaultWindDataset extends AbstractSeriesDataset implements WindDataset {

    /** The names of the series. */
    private List<String> seriesNames;

    /** Storage for the series data. */
    private List<List<WindDataItem>> allSeriesData;

    /**
     * Constructs a new, empty, WindDataset.
     */
    public DefaultWindDataset() {
        seriesNames = new java.util.ArrayList<String>();
        allSeriesData = new java.util.ArrayList<List<WindDataItem>>();
    }

    /**
     * Constructs a WindDataset based on the specified data.
     *
     * @param data  the wind dataset.
     */
    public DefaultWindDataset(Object[][][] data) {
        this(seriesNameListFromDataArray(data), data);
    }

    /**
     * Constructs a WindDataset based on the specified data.
     *
     * @param seriesNames    the names of the series.
     * @param data  the wind dataset.
     */
    public DefaultWindDataset(String[] seriesNames, Object[][][] data) {
        this(Arrays.asList(seriesNames), data);
    }

    /**
     * Constructs a WindDataset based on the specified data.
     *
     * @param seriesNames    the names of the series.
     * @param data  the wind dataset.
     */
    @SuppressWarnings("unchecked")
		public DefaultWindDataset(List<String> seriesNames, Object[][][] data) {

        this.seriesNames = seriesNames;

        int seriesCount = data.length;

        allSeriesData = new java.util.ArrayList<List<WindDataItem>>(seriesCount);

        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            List<WindDataItem> oneSeriesData = new java.util.ArrayList<WindDataItem>();
            int maxItemCount = data[seriesIndex].length;
            for (int itemIndex = 0; itemIndex < maxItemCount; itemIndex++) {
                Object xObject = data[seriesIndex][itemIndex][0];
                if (xObject != null) {
                    Number xNumber = null;
                    if (xObject instanceof Number) {
                        xNumber = (Number) xObject;
                    }
                    else {
                        if (xObject instanceof Date) {
                            Date xDate = (Date) xObject;
                            xNumber = new Long(xDate.getTime());
                        }
                        else {
                            xNumber = new Integer(0);
                        }
                    }
                    Number windDir = (Number) data[seriesIndex][itemIndex][1];
                    Number windForce = (Number) data[seriesIndex][itemIndex][2];
                    oneSeriesData.add(new WindDataItem(xNumber, windDir, windForce));
                }
            }
            Collections.sort(oneSeriesData);
            allSeriesData.add(seriesIndex, oneSeriesData);
        }

    }

    /**
     * Returns the number of series in the dataset.
     * @return The number of series in the dataset.
     */
    public int getSeriesCount() {
        return allSeriesData.size();
    }

    /**
     * Returns the number of items in a series.
     * @param series    The series (zero-based index).
     * @return The number of items in a series.
     */
    public int getItemCount(int series) {
        List<WindDataItem> oneSeriesData =  allSeriesData.get(series);
        return oneSeriesData.size();
    }

    /**
     * Returns the name of a series.
     * @param series    The series (zero-based index).
     * @return The name of the specified series.
     */
    public String getSeriesName(int series) {
        return seriesNames.get(series).toString();
    }

    /**
     * Returns the x-value for one item within a series.  This should represent
     * a point in time, encoded as milliseconds in the same way as
     * java.util.Date.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The x-value for the item within the series.
     */
    public Number getXValue(int series, int item) {
        List<WindDataItem> oneSeriesData =  allSeriesData.get(series);
        WindDataItem windItem = (WindDataItem) oneSeriesData.get(item);
        return windItem.x;
    }

    /**
     * Returns the y-value for one item within a series.  This maps to the
     * getWindForce(...) method and is implemented because WindDataset is an
     * extension of XYDataset.
     *
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The y-value for the item within the series.
     */
    public Number getYValue(int series, int item) {
        return getWindForce(series, item);
    }

    /**
     * Returns the wind direction for one item within a series.  This is a
     * number between 0 and 12, like the numbers on a clock face.
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The wind direction for the item within the series.
     */
    public Number getWindDirection(int series, int item) {
        List<WindDataItem> oneSeriesData =  allSeriesData.get(series);
        WindDataItem windItem = (WindDataItem) oneSeriesData.get(item);
        return windItem.windDir;
    }

    /**
     * Returns the wind force for one item within a series.  This is a number
     * between 0 and 12, as defined by the Beaufort scale.
     * @param series    The series (zero-based index).
     * @param item      The item (zero-based index).
     * @return The wind force for the item within the series.
     */
    public Number getWindForce(int series, int item) {
        List<WindDataItem> oneSeriesData = allSeriesData.get(series);
        WindDataItem windItem = (WindDataItem) oneSeriesData.get(item);
        return windItem.windForce;
    }

    /**
     * Utility method for automatically generating series names.
     * @param data  the wind dataset.
     *
     * @return an array of <i>Series N</i> with N = { 1 .. data.length }.
     */
    public static List<String> seriesNameListFromDataArray(Object[][] data) {

        int seriesCount = data.length;
        List<String> seriesNameList = new java.util.ArrayList<String>(seriesCount);
        for (int i = 0; i < seriesCount; i++) {
            seriesNameList.add("Series " + (i + 1));
        }
        return seriesNameList;

    }

}

/**
 * A wind data item.
 */
@SuppressWarnings("unchecked")
class WindDataItem implements Comparable {

    /** The x-value. */
    public Number x;

    /** The wind direction. */
    public Number windDir;

    /** The wind force. */
    public Number windForce;

    /**
     * Creates a new wind data item.
     *
     * @param x  the x-value.
     * @param windDir  the direction.
     * @param windForce  the force.
     */
    public WindDataItem(Number x, Number windDir, Number windForce) {
        this.x = x;
        this.windDir = windDir;
        this.windForce = windForce;
    }

    /**
     * Compares this item to another object.
     *
     * @param object  the other object.
     */
    public int compareTo(Object object) {
        if (object instanceof WindDataItem) {
            WindDataItem item = (WindDataItem)object;
            if (this.x.doubleValue() > item.x.doubleValue()) {
                return 1;
            }
            else if (this.x.equals(item.x)) {
                return 0;
            }
            else {
                return -1;
            }
        }
        else {
            throw new ClassCastException("WindDataItem.compareTo(error)");
        }
    }

}
