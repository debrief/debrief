/* ===============
 * JFreeChart Demo
 * ===============
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
 * ----------------------------
 * SimpleIntervalXYDataset.java
 * ----------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: SimpleIntervalXYDataset.java,v 1.1.1.1 2003/07/17 10:06:36 Ian.Mayo Exp $
 *
 * Changes (since 11-Oct-2002)
 * ---------------------------
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.AbstractDataset;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.data.DatasetChangeListener;

/**
 * A quick and dirty sample dataset.
 *
 * @author DG
 */
public class SimpleIntervalXYDataset extends AbstractDataset implements IntervalXYDataset {

    /** The start values. */
    private Double[] xStart = new Double[3];
    /** The end values. */
    private Double[] xEnd = new Double[3];

    /** The y values. */
    private Double[] y = new Double[3];

    /**
     * Creates a new dataset.
     */
    public SimpleIntervalXYDataset() {

        xStart[0] = new Double(0.0);
        xStart[1] = new Double(2.0);
        xStart[2] = new Double(3.5);

        xEnd[0] = new Double(2.0);
        xEnd[1] = new Double(3.5);
        xEnd[2] = new Double(4.0);

        y[0] = new Double(3.0);
        y[1] = new Double(4.5);
        y[2] = new Double(2.5);
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the number of series in the dataset.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the name of a series.
     *
     * @param series the series (zero-based index).
     *
     * @return the series name.
     */
    public String getSeriesName(int series) {
        return "Series 1";
    }

    /**
     * Returns the number of items in a series.
     *
     * @param series the series (zero-based index).
     *
     * @return the number of items within a series.
     */
    public int getItemCount(int series) {
        return 3;
    }

    /**
     * Returns the x-value for an item within a series.
     * <P>
     * The implementation is responsible for ensuring that the x-values are presented in ascending
     * order.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return  the x-value for an item within a series.
     */
    public Number getXValue(int series, int item) {
        return xStart[item];
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value for an item within a series.
     */
    public Number getYValue(int series, int item) {
        return y[item];
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the start x value.
     */
    public Number getStartXValue(int series, int item) {
        return xStart[item];
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the end x value.
     */
    public Number getEndXValue(int series, int item) {
        return xEnd[item];
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the start y value.
     */
    public Number getStartYValue(int series, int item) {
        return y[item];
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the end y value.
     */
    public Number getEndYValue(int series, int item) {
        return y[item];
    }

    /**
     * Registers an object for notification of changes to the dataset.
     *
     * @param listener  the object to register.
     */
    public void addChangeListener(DatasetChangeListener listener) {
    }

    /**
     * Deregisters an object for notification of changes to the dataset.
     *
     * @param listener  the object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener) {
    }

}
