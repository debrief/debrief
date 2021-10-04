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
 * --------------------------------------
 * DefaultStatisticalCategoryDataset.java
 * --------------------------------------
 * (C) Copyright 2002-2021, by Pascal Collet and Contributors.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 */

package org.jfree.data.statistics;

import java.util.List;
import org.jfree.chart.util.PublicCloneable;

import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;

/**
 * A convenience class that provides a default implementation of the
 * {@link StatisticalCategoryDataset} interface.
 */
public class DefaultStatisticalCategoryDataset extends AbstractDataset
        implements StatisticalCategoryDataset, RangeInfo, PublicCloneable {

    /** Storage for the data. */
    private KeyedObjects2D data;

    /** The minimum range value. */
    private double minimumRangeValue;

    /** The row index for the minimum range value. */
    private int minimumRangeValueRow;

    /** The column index for the minimum range value. */
    private int minimumRangeValueColumn;

    /** The minimum range value including the standard deviation. */
    private double minimumRangeValueIncStdDev;

    /**
     * The row index for the minimum range value (including the standard
     * deviation).
     */
    private int minimumRangeValueIncStdDevRow;

    /**
     * The column index for the minimum range value (including the standard
     * deviation).
     */
    private int minimumRangeValueIncStdDevColumn;

    /** The maximum range value. */
    private double maximumRangeValue;

    /** The row index for the maximum range value. */
    private int maximumRangeValueRow;

    /** The column index for the maximum range value. */
    private int maximumRangeValueColumn;

    /** The maximum range value including the standard deviation. */
    private double maximumRangeValueIncStdDev;

    /**
     * The row index for the maximum range value (including the standard
     * deviation).
     */
    private int maximumRangeValueIncStdDevRow;

    /**
     * The column index for the maximum range value (including the standard
     * deviation).
     */
    private int maximumRangeValueIncStdDevColumn;

    /**
     * Creates a new dataset.
     */
    public DefaultStatisticalCategoryDataset() {
        this.data = new KeyedObjects2D();
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
        this.minimumRangeValueIncStdDev = Double.NaN;
        this.minimumRangeValueIncStdDevRow = -1;
        this.minimumRangeValueIncStdDevColumn = -1;
        this.maximumRangeValueIncStdDev = Double.NaN;
        this.maximumRangeValueIncStdDevRow = -1;
        this.maximumRangeValueIncStdDevColumn = -1;
    }

    /**
     * Returns the mean value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The mean value (possibly {@code null}).
     */
    @Override
    public Number getMeanValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;
    }

    /**
     * Returns the value for an item (for this dataset, the mean value is
     * returned).
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return The value (possibly {@code null}).
     */
    @Override
    public Number getValue(int row, int column) {
        return getMeanValue(row, column);
    }

    /**
     * Returns the value for an item (for this dataset, the mean value is
     * returned).
     *
     * @param rowKey  the row key.
     * @param columnKey  the columnKey.
     *
     * @return The value (possibly {@code null}).
     */
    @Override
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        return getMeanValue(rowKey, columnKey);
    }

    /**
     * Returns the mean value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the columnKey.
     *
     * @return The mean value (possibly {@code null}).
     */
    @Override
    public Number getMeanValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getMean();
        }
        return result;
    }

    /**
     * Returns the standard deviation value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The standard deviation (possibly {@code null}).
     */
    @Override
    public Number getStdDevValue(int row, int column) {
        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                this.data.getObject(row, column);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;
    }

    /**
     * Returns the standard deviation value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the columnKey.
     *
     * @return The standard deviation (possibly {@code null}).
     */
    @Override
    public Number getStdDevValue(Comparable rowKey, Comparable columnKey) {
        Number result = null;
        MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                this.data.getObject(rowKey, columnKey);
        if (masd != null) {
            result = masd.getStandardDeviation();
        }
        return result;
    }

    /**
     * Returns the column index for a given key.
     *
     * @param key  the column key ({@code null} not permitted).
     *
     * @return The column index.
     */
    @Override
    public int getColumnIndex(Comparable key) {
        // defer null argument check
        return this.data.getColumnIndex(key);
    }

    /**
     * Returns a column key.
     *
     * @param column  the column index (zero-based).
     *
     * @return The column key.
     */
    @Override
    public Comparable getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    /**
     * Returns the column keys.
     *
     * @return The keys.
     */
    @Override
    public List getColumnKeys() {
        return this.data.getColumnKeys();
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key  the row key ({@code null} not permitted).
     *
     * @return The row index.
     */
    @Override
    public int getRowIndex(Comparable key) {
        // defer null argument check
        return this.data.getRowIndex(key);
    }

    /**
     * Returns a row key.
     *
     * @param row  the row index (zero-based).
     *
     * @return The row key.
     */
    @Override
    public Comparable getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    /**
     * Returns the row keys.
     *
     * @return The keys.
     */
    @Override
    public List getRowKeys() {
        return this.data.getRowKeys();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The row count.
     *
     * @see #getColumnCount()
     */
    @Override
    public int getRowCount() {
        return this.data.getRowCount();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The column count.
     *
     * @see #getRowCount()
     */
    @Override
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    /**
     * Adds a mean and standard deviation to the table.
     *
     * @param mean  the mean.
     * @param standardDeviation  the standard deviation.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void add(double mean, double standardDeviation,
                    Comparable rowKey, Comparable columnKey) {
        add(Double.valueOf(mean), Double.valueOf(standardDeviation), rowKey, columnKey);
    }

    /**
     * Adds a mean and standard deviation to the table.
     *
     * @param mean  the mean.
     * @param standardDeviation  the standard deviation.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     */
    public void add(Number mean, Number standardDeviation,
                    Comparable rowKey, Comparable columnKey) {
        MeanAndStandardDeviation item = new MeanAndStandardDeviation(
                mean, standardDeviation);
        this.data.addObject(item, rowKey, columnKey);

        double m = Double.NaN;
        double sd = Double.NaN;
        if (mean != null) {
            m = mean.doubleValue();
        }
        if (standardDeviation != null) {
            sd = standardDeviation.doubleValue();
        }

        // update cached range values
        int r = this.data.getColumnIndex(columnKey);
        int c = this.data.getRowIndex(rowKey);
        if ((r == this.maximumRangeValueRow && c
                == this.maximumRangeValueColumn) || (r
                == this.maximumRangeValueIncStdDevRow && c
                == this.maximumRangeValueIncStdDevColumn) || (r
                == this.minimumRangeValueRow && c
                == this.minimumRangeValueColumn) || (r
                == this.minimumRangeValueIncStdDevRow && c
                == this.minimumRangeValueIncStdDevColumn)) {

            // iterate over all data items and update mins and maxes
            updateBounds();
        }
        else {
            if (!Double.isNaN(m)) {
                if (Double.isNaN(this.maximumRangeValue)
                        || m > this.maximumRangeValue) {
                    this.maximumRangeValue = m;
                    this.maximumRangeValueRow = r;
                    this.maximumRangeValueColumn = c;
                }
            }

            if (!Double.isNaN(m + sd)) {
                if (Double.isNaN(this.maximumRangeValueIncStdDev)
                        || (m + sd) > this.maximumRangeValueIncStdDev) {
                    this.maximumRangeValueIncStdDev = m + sd;
                    this.maximumRangeValueIncStdDevRow = r;
                    this.maximumRangeValueIncStdDevColumn = c;
                }
            }

            if (!Double.isNaN(m)) {
                if (Double.isNaN(this.minimumRangeValue)
                        || m < this.minimumRangeValue) {
                    this.minimumRangeValue = m;
                    this.minimumRangeValueRow = r;
                    this.minimumRangeValueColumn = c;
                }
            }

            if (!Double.isNaN(m - sd)) {
                if (Double.isNaN(this.minimumRangeValueIncStdDev)
                        || (m - sd) < this.minimumRangeValueIncStdDev) {
                    this.minimumRangeValueIncStdDev = m - sd;
                    this.minimumRangeValueIncStdDevRow = r;
                    this.minimumRangeValueIncStdDevColumn = c;
                }
            }
        }
        fireDatasetChanged();
    }

    /**
     * Removes an item from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @see #add(double, double, Comparable, Comparable)
     */
    public void remove(Comparable rowKey, Comparable columnKey) {
        // defer null argument checks
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        this.data.removeObject(rowKey, columnKey);

        // if this cell held a maximum and/or minimum value, we'll need to
        // update the cached bounds...
        if ((r == this.maximumRangeValueRow && c
                == this.maximumRangeValueColumn) || (r
                == this.maximumRangeValueIncStdDevRow && c
                == this.maximumRangeValueIncStdDevColumn) || (r
                == this.minimumRangeValueRow && c
                == this.minimumRangeValueColumn) || (r
                == this.minimumRangeValueIncStdDevRow && c
                == this.minimumRangeValueIncStdDevColumn)) {

            // iterate over all data items and update mins and maxes
            updateBounds();
        }

        fireDatasetChanged();
    }


    /**
     * Removes a row from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowIndex  the row index.
     *
     * @see #removeColumn(int)
     */
    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowKey  the row key ({@code null} not permitted).
     *
     * @see #removeColumn(Comparable)
     */
    public void removeRow(Comparable rowKey) {
        this.data.removeRow(rowKey);
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param columnIndex  the column index.
     *
     * @see #removeRow(int)
     */
    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @see #removeRow(Comparable)
     */
    public void removeColumn(Comparable columnKey) {
        this.data.removeColumn(columnKey);
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Clears all data from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     */
    public void clear() {
        this.data.clear();
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Iterate over all the data items and update the cached bound values.
     */
    private void updateBounds() {
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValueIncStdDev = Double.NaN;
        this.maximumRangeValueIncStdDevRow = -1;
        this.maximumRangeValueIncStdDevColumn = -1;
        this.minimumRangeValueIncStdDev = Double.NaN;
        this.minimumRangeValueIncStdDevRow = -1;
        this.minimumRangeValueIncStdDevColumn = -1;

        int rowCount = this.data.getRowCount();
        int columnCount = this.data.getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                MeanAndStandardDeviation masd = (MeanAndStandardDeviation)
                        this.data.getObject(r, c);
                if (masd == null) {
                    continue;
                }
                double m = masd.getMeanValue();
                double sd = masd.getStandardDeviationValue();

                if (!Double.isNaN(m)) {

                    // update the max value
                    if (Double.isNaN(this.maximumRangeValue)) {
                        this.maximumRangeValue = m;
                        this.maximumRangeValueRow = r;
                        this.maximumRangeValueColumn = c;
                    }
                    else {
                        if (m > this.maximumRangeValue) {
                            this.maximumRangeValue = m;
                            this.maximumRangeValueRow = r;
                            this.maximumRangeValueColumn = c;
                        }
                    }

                    // update the min value
                    if (Double.isNaN(this.minimumRangeValue)) {
                        this.minimumRangeValue = m;
                        this.minimumRangeValueRow = r;
                        this.minimumRangeValueColumn = c;
                    }
                    else {
                        if (m < this.minimumRangeValue) {
                            this.minimumRangeValue = m;
                            this.minimumRangeValueRow = r;
                            this.minimumRangeValueColumn = c;
                        }
                    }

                    if (!Double.isNaN(sd)) {
                        // update the max value
                        if (Double.isNaN(this.maximumRangeValueIncStdDev)) {
                            this.maximumRangeValueIncStdDev = m + sd;
                            this.maximumRangeValueIncStdDevRow = r;
                            this.maximumRangeValueIncStdDevColumn = c;
                        }
                        else {
                            if (m + sd > this.maximumRangeValueIncStdDev) {
                                this.maximumRangeValueIncStdDev = m + sd;
                                this.maximumRangeValueIncStdDevRow = r;
                                this.maximumRangeValueIncStdDevColumn = c;
                            }
                        }

                        // update the min value
                        if (Double.isNaN(this.minimumRangeValueIncStdDev)) {
                            this.minimumRangeValueIncStdDev = m - sd;
                            this.minimumRangeValueIncStdDevRow = r;
                            this.minimumRangeValueIncStdDevColumn = c;
                        }
                        else {
                            if (m - sd < this.minimumRangeValueIncStdDev) {
                                this.minimumRangeValueIncStdDev = m - sd;
                                this.minimumRangeValueIncStdDevRow = r;
                                this.minimumRangeValueIncStdDevColumn = c;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the minimum y-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether or not the
     *                         y-interval is taken into account.
     *
     * @return The minimum value.
     *
     * @see #getRangeUpperBound(boolean)
     */
    @Override
    public double getRangeLowerBound(boolean includeInterval) {
        if (includeInterval && !Double.isNaN(this.minimumRangeValueIncStdDev)) {
            return this.minimumRangeValueIncStdDev;
        }
        else {
            return this.minimumRangeValue;
        }
    }

    /**
     * Returns the maximum y-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether or not the
     *                         y-interval is taken into account.
     *
     * @return The maximum value.
     *
     * @see #getRangeLowerBound(boolean)
     */
    @Override
    public double getRangeUpperBound(boolean includeInterval) {
        if (includeInterval && !Double.isNaN(this.maximumRangeValueIncStdDev)) {
            return this.maximumRangeValueIncStdDev;
        }
        else {
            return this.maximumRangeValue;
        }
    }

    /**
     * Returns the bounds of the values in this dataset's y-values.
     *
     * @param includeInterval  a flag that determines whether or not the
     *                         y-interval is taken into account.
     *
     * @return The range.
     */
    @Override
    public Range getRangeBounds(boolean includeInterval) {
        double lower = getRangeLowerBound(includeInterval);
        double upper = getRangeUpperBound(includeInterval);
        if (Double.isNaN(lower) && Double.isNaN(upper)) {
            return null;
        }
        return new Range(lower, upper);
    }

    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultStatisticalCategoryDataset)) {
            return false;
        }
        DefaultStatisticalCategoryDataset that
                = (DefaultStatisticalCategoryDataset) obj;
        if (!this.data.equals(that.data)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of this dataset.
     *
     * @return A clone of this dataset.
     *
     * @throws CloneNotSupportedException if cloning cannot be completed.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultStatisticalCategoryDataset clone
                = (DefaultStatisticalCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
        return clone;
    }
}
