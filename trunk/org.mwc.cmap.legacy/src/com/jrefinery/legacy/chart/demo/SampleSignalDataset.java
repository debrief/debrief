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
 * -------------------------
 * SampleSIgnalDataset.java
 * ------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: SampleSignalDataset.java,v 1.1.1.1 2003/07/17 10:06:35 Ian.Mayo Exp $
 *
 * Changes (since 11-Oct-2002)
 * ---------------------------
 * 11-Oct-2002 : Added Javadocs;
 *
 */


package com.jrefinery.legacy.chart.demo;

import com.jrefinery.legacy.data.AbstractDataset;
import com.jrefinery.legacy.data.DatasetChangeListener;
import com.jrefinery.legacy.data.HighLowDataset;
import com.jrefinery.legacy.data.SignalsDataset;

/**
 * A sample signal dataset.
 *
 * @author ??
 */
public class SampleSignalDataset extends AbstractDataset implements SignalsDataset {

    /** The data. */
    private HighLowDataset data;

    /**
     * Default constructor.
     */
    public SampleSignalDataset() {
        this.data = DemoDatasetFactory.createSampleHighLowDataset();
    }

    /**
     * Returns the number of items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the number of items within the series.
     */
    public int getItemCount(int series) {
        return data.getItemCount(series);
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return data.getSeriesCount();
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
        return data.getSeriesName(series);
    }

    /**
     * Returns the x-value for an item within a series.
     * <P>
     * The implementation is responsible for ensuring that the x-values are
     * presented in ascending order.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the x-value.
     */
    public Number getXValue(int series, int item) {
        return data.getXValue(series, item);
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value.
     */
    public Number getYValue(int series, int item) {
        return data.getYValue(series, item);
    }

    /**
     * Returns the type.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the type.
     */
    public int getType(int series, int item) {
        return SignalsDataset.ENTER_LONG;
    }

    /**
     * Returns the level.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the level.
     */
    public double getLevel(int series, int item) {
        return this.getXValue(series, item).doubleValue();
    }

    /**
     * Registers an object to receive notification of changes to the dataset.
     *
     * @param listener the object to register.
     */
    public void addChangeListener(DatasetChangeListener listener) {
        data.addChangeListener(listener);
    }

    /**
     * Deregisters an object so that it no longer receives notification of changes to the dataset.
     *
     * @param listener the object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener) {
        data.removeChangeListener(listener);
    }

}
