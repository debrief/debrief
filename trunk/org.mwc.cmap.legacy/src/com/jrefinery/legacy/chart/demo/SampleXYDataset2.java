/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, Simba Management Limited and Contributors.
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
 * SampleXYDataset2.java
 * ---------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: SampleXYDataset2.java,v 1.1.1.1 2003/07/17 10:06:35 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 22-Oct-2001 : Version 1 (DG);
 *               Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Nov-2001 : Updated source header (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart.demo;

import com.jrefinery.legacy.data.AbstractSeriesDataset;
import com.jrefinery.legacy.data.DomainInfo;
import com.jrefinery.legacy.data.Range;
import com.jrefinery.legacy.data.RangeInfo;
import com.jrefinery.legacy.data.XYDataset;

/**
 * Random data for a scatter plot demo.
 * <P>
 * Note that the aim of this class is to create a self-contained data source for demo purposes -
 * it is NOT intended to show how you should go about writing your own datasets.
 *
 * @author DG
 */
public class SampleXYDataset2 extends AbstractSeriesDataset implements XYDataset,
                                                                       DomainInfo, RangeInfo {

    /** The series count. */
    private static final int SERIES_COUNT = 4;

    /** The item count. */
    private static final int ITEM_COUNT = 100;

    /** The range. */
    private static final double RANGE = 200;

    /** The x values. */
    private Double[][] xValues = new Double[SERIES_COUNT][ITEM_COUNT];

    /** The y values. */
    private Double[][] yValues = new Double[SERIES_COUNT][ITEM_COUNT];

    /** The minimum domain value. */
    private Number domainMin;

    /** The maximum domain value. */
    private Number domainMax;

    /** The minimum range value. */
    private Number rangeMin;

    /** The maximum range value. */
    private Number rangeMax;

    /** The range of the domain. */
    private Range domainRange;

    /** The range. */
    private Range range;

    /**
     * Default constructor.
     */
    public SampleXYDataset2() {

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (int series = 0; series < SERIES_COUNT; series++) {
            for (int item = 0; item < ITEM_COUNT; item++) {

                double x = (Math.random() - 0.5) * RANGE;
                xValues[series][item] = new Double(x);
                if (x < minX) {
                    minX = x;
                }
                if (x > maxX) {
                    maxX = x;
                }

                double y = (Math.random() + 0.5) * 6 * x + x;
                yValues[series][item] = new Double(y);
                if (y < minY) {
                    minY = y;
                }
                if (y > maxY) {
                    maxY = y;
                }

            }
        }

        this.domainMin = new Double(minX);
        this.domainMax = new Double(maxX);
        this.domainRange = new Range(minX, maxX);

        this.rangeMin = new Double(minY);
        this.rangeMax = new Double(maxY);
        this.range = new Range(minY, maxY);

    }

    /**
     * Returns the x-value for the specified series and item.  Series are numbered 0, 1, ...
     *
     * @param series  the index (zero-based) of the series.
     * @param item  the index (zero-based) of the required item.
     *
     * @return the x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {
        return xValues[series][item];
    }

    /**
     * Returns the y-value for the specified series and item.  Series are numbered 0, 1, ...
     *
     * @param series  the index (zero-based) of the series.
     * @param item  the index (zero-based) of the required item.
     *
     * @return  the y-value for the specified series and item.
     */
    public Number getYValue(int series, int item) {
        return yValues[series][item];
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return SERIES_COUNT;
    }

    /**
     * Returns the name of the series.
     *
     * @param series  the index (zero-based) of the series.
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
        return "Sample " + series;
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the index (zero-based) of the series.
     *
     * @return the number of items in the specified series.
     */
    public int getItemCount(int series) {
        return ITEM_COUNT;
    }

    /**
     * Returns the minimum domain value.
     *
     * @return the minimum domain value.
     */
    public Number getMinimumDomainValue() {
        return this.domainMin;
    }

    /**
     * Returns the maximum domain value.
     *
     * @return the maximum domain value.
     */
    public Number getMaximumDomainValue() {
        return this.domainMax;
    }

    /**
     * Returns the range of values in the domain.
     *
     * @return the range.
     */
    public Range getDomainRange() {
        return this.domainRange;
    }

    /**
     * Returns the minimum range value.
     *
     * @return the minimum range value.
     */
    public Number getMinimumRangeValue() {
        return this.rangeMin;
    }

    /**
     * Returns the maximum range value.
     *
     * @return the maximum range value.
     */
    public Number getMaximumRangeValue() {
        return this.rangeMax;
    }

    /**
     * Returns the range of values in the range (y-values).
     *
     * @return the range.
     */
    public Range getValueRange() {
        return this.range;
    }


}
