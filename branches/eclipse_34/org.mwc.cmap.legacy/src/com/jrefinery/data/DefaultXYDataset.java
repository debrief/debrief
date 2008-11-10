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
 * ---------------------
 * DefaultXYDataset.java
 * ---------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultXYDataset.java,v 1.1.1.1 2003/07/17 10:06:52 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Moved to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Dec-2001 : Replaced XYDataItem class with XYDataPair (DG);
 * 15-Mar-2002 : Modified to use ResourceBundle for elements that require localisation (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

import java.util.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A convenience class that provides a default implementation of the XYDataset
 * interface.
 * The standard constructor accepts data in a two dimensional array where the
 * first dimension is the series, and the second dimension is the category.
 *
 * @author DG
 */
public class DefaultXYDataset extends AbstractSeriesDataset implements XYDataset {

    /** A list of series names. */
    private List seriesNames;

    /** A list of Lists containing the data for each series. */
    private List allSeriesData;

    /**
     * Constructs a new dataset, initially empty.
     */
    public DefaultXYDataset() {
        seriesNames = new java.util.ArrayList();
        allSeriesData = new java.util.ArrayList();
    }

    /**
     * Constructs a new dataset, and populates it with the given data.
     * <P>
     * The dimensions of the data array are [series][item][x=0, y=1].
     * The x-values should be Number or Date objects, the y-values should be
     * Number objects.  Any other types are interpreted as zero. The data will
     * be sorted so that the x-values are ascending.
     *
     * @param data  the dataset.
     */
    public DefaultXYDataset(Object[][][] data) {
        this(seriesNameListFromDataArray(data), data);
    }

    /**
     * Constructs a new dataset with the given data.
     *
     * @param seriesNames  the names of the series.
     * @param data  the dataset.
     */
    public DefaultXYDataset(String[] seriesNames, Object[][][] data) {
        this(Arrays.asList(seriesNames), data);
    }

    /**
     * Constructs a new dataset with the given data.
     *
     * @param seriesNames  the names of the series.
     * @param data  the dataset.
     */
    public DefaultXYDataset(List seriesNames, Object[][][] data) {

        this.seriesNames = seriesNames;

        int seriesCount = data.length;

        allSeriesData = new java.util.ArrayList(seriesCount);

        for (int series = 0; series < seriesCount; series++) {
            List oneSeriesData = new java.util.ArrayList();
            int maxItemCount = data[series].length;
            for (int itemIndex = 0; itemIndex < maxItemCount; itemIndex++) {
                Object xObject = data[series][itemIndex][0];
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
                    Number yNumber = (Number) data[series][itemIndex][1];
                    oneSeriesData.add(new XYDataPair(xNumber, yNumber));
                }
            }
            Collections.sort(oneSeriesData);
            allSeriesData.add(series, oneSeriesData);
        }

    }

    /**
     * Returns the number of series.
     *
     * @return the number of series.
     */
    public int getSeriesCount() {
        return allSeriesData.size();
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the index of the series (zero-based).
     *
     * @return the number of items in the specified series.
     */
    public int getItemCount(int series) {
        List oneSeriesData = (List) allSeriesData.get(series);
        return oneSeriesData.size();
    }

    /**
     * Returns the name of the specified series.
     *
     * @param series  the index of the required series (zero-based).
     *
     * @return the name of the specified series.
     */
    public String getSeriesName(int series) {
        return seriesNames.get(series).toString();
    }

    /**
     * Sets the names of the series in the data source.
     *
     * @param seriesNames  the names of the series in the data source.
     */
    public void setSeriesNames(String[] seriesNames) {
        this.seriesNames = Arrays.asList(seriesNames);
        fireDatasetChanged();
    }

    /**
     * Returns the x value for the specified series and index (zero-based
     * indices).
     *
     * @param series  the index of the series (zero-based).
     * @param item  the index of the item (zero-based).
     *
     * @return the x value for the specified series and index.
     */
    public Number getXValue(int series, int item) {
        List oneSeriesData = (List) allSeriesData.get(series);
        XYDataPair xy = (XYDataPair) oneSeriesData.get(item);
        return xy.getX();
    }

    /**
     * Returns the y value for the specified series and index (zero-based indices).
     *
     * @param series  the index of the series (zero-based).
     * @param item  the index of the item (zero-based).
     *
     * @return the y value for the specified series and index
     */
    public Number getYValue(int series, int item) {
        List oneSeriesData = (List) allSeriesData.get(series);
        XYDataPair xy = (XYDataPair) oneSeriesData.get(item);
        return xy.getY();
    }

    /**
     * Returns a List of String objects that can be used as series names.
     *
     * @param data  an array containing the data for the data source.
     *
     * @return a List of String objects that can be used as series names.
     */
    public static List seriesNameListFromDataArray(Object[][] data) {

        String baseName = "com.jrefinery.data.resources.DataPackageResources";
        ResourceBundle resources = ResourceBundle.getBundle(baseName);

        String prefix = resources.getString("series.default-prefix") + " ";

        int seriesCount = data.length;
        List seriesNameList = new java.util.ArrayList(seriesCount);
        for (int i = 0; i < seriesCount; i++) {
            seriesNameList.add(prefix + (i + 1));
        }
        return seriesNameList;

    }

}
