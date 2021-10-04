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
 * -------------------------
 * CategoryToPieDataset.java
 * -------------------------
 * (C) Copyright 2003-2021, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Christian W. Zuckschwerdt;
 *
 */

package org.jfree.data.category;

import java.util.Collections;
import java.util.List;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.TableOrder;

import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.PieDataset;

/**
 * A {@link PieDataset} implementation that obtains its data from one row or
 * column of a {@link CategoryDataset}.
 */
public class CategoryToPieDataset extends AbstractDataset
        implements PieDataset, DatasetChangeListener {

    /** For serialization. */
    static final long serialVersionUID = 5516396319762189617L;

    /** The source. */
    private CategoryDataset source;

    /** The extract type. */
    private TableOrder extract;

    /** The row or column index. */
    private int index;

    /**
     * An adaptor class that converts any {@link CategoryDataset} into a
     * {@link PieDataset}, by taking the values from a single row or column.
     * <p>
     * If {@code source} is {@code null}, the created dataset will
     * be empty.
     *
     * @param source  the source dataset ({@code null} permitted).
     * @param extract  extract data from rows or columns? ({@code null}
     *                 not permitted).
     * @param index  the row or column index.
     */
    public CategoryToPieDataset(CategoryDataset source, TableOrder extract,
            int index) {
        Args.nullNotPermitted(extract, "extract");
        this.source = source;
        if (this.source != null) {
            this.source.addChangeListener(this);
        }
        this.extract = extract;
        this.index = index;
    }

    /**
     * Returns the underlying dataset.
     *
     * @return The underlying dataset (possibly {@code null}).
     */
    public CategoryDataset getUnderlyingDataset() {
        return this.source;
    }

    /**
     * Returns the extract type, which determines whether data is read from
     * one row or one column of the underlying dataset.
     *
     * @return The extract type.
     */
    public TableOrder getExtractType() {
        return this.extract;
    }

    /**
     * Returns the index of the row or column from which to extract the data.
     *
     * @return The extract index.
     */
    public int getExtractIndex() {
        return this.index;
    }

    /**
     * Returns the number of items (values) in the collection.  If the
     * underlying dataset is {@code null}, this method returns zero.
     *
     * @return The item count.
     */
    @Override
    public int getItemCount() {
        int result = 0;
        if (this.source != null) {
            if (this.extract == TableOrder.BY_ROW) {
                result = this.source.getColumnCount();
            }
            else if (this.extract == TableOrder.BY_COLUMN) {
                result = this.source.getRowCount();
            }
        }
        return result;
    }

    /**
     * Returns a value from the dataset.
     *
     * @param item  the item index (zero-based).
     *
     * @return The value (possibly {@code null}).
     *
     * @throws IndexOutOfBoundsException if {@code item} is not in the
     *     range {@code 0} to {@code getItemCount() -1}.
     */
    @Override
    public Number getValue(int item) {
        Number result = null;
        if (item < 0 || item >= getItemCount()) {
            // this will include the case where the underlying dataset is null
            throw new IndexOutOfBoundsException(
                    "The 'item' index is out of bounds.");
        }
        if (this.extract == TableOrder.BY_ROW) {
            result = this.source.getValue(this.index, item);
        }
        else if (this.extract == TableOrder.BY_COLUMN) {
            result = this.source.getValue(item, this.index);
        }
        return result;
    }

    /**
     * Returns the key at the specified index.
     *
     * @param index  the item index (in the range {@code 0} to
     *     {@code getItemCount() -1}).
     *
     * @return The key.
     *
     * @throws IndexOutOfBoundsException if {@code index} is not in the
     *     specified range.
     */
    @Override
    public Comparable getKey(int index) {
        Comparable result = null;
        if (index < 0 || index >= getItemCount()) {
            // this includes the case where the underlying dataset is null
            throw new IndexOutOfBoundsException("Invalid 'index': " + index);
        }
        if (this.extract == TableOrder.BY_ROW) {
            result = this.source.getColumnKey(index);
        }
        else if (this.extract == TableOrder.BY_COLUMN) {
            result = this.source.getRowKey(index);
        }
        return result;
    }

    /**
     * Returns the index for a given key, or {@code -1} if there is no
     * such key.
     *
     * @param key  the key.
     *
     * @return The index for the key, or {@code -1}.
     */
    @Override
    public int getIndex(Comparable key) {
        int result = -1;
        if (this.source != null) {
            if (this.extract == TableOrder.BY_ROW) {
                result = this.source.getColumnIndex(key);
            }
            else if (this.extract == TableOrder.BY_COLUMN) {
                result = this.source.getRowIndex(key);
            }
        }
        return result;
    }

    /**
     * Returns the keys for the dataset.
     * <p>
     * If the underlying dataset is {@code null}, this method returns an
     * empty list.
     *
     * @return The keys.
     */
    @Override
    public List getKeys() {
        List result = Collections.EMPTY_LIST;
        if (this.source != null) {
            if (this.extract == TableOrder.BY_ROW) {
                result = this.source.getColumnKeys();
            }
            else if (this.extract == TableOrder.BY_COLUMN) {
                result = this.source.getRowKeys();
            }
        }
        return result;
    }

    /**
     * Returns the value for a given key.  If the key is not recognised, the
     * method should return {@code null} (but note that {@code null}
     * can be associated with a valid key also).
     *
     * @param key  the key.
     *
     * @return The value (possibly {@code null}).
     */
    @Override
    public Number getValue(Comparable key) {
        Number result = null;
        int keyIndex = getIndex(key);
        if (keyIndex != -1) {
            if (this.extract == TableOrder.BY_ROW) {
                result = this.source.getValue(this.index, keyIndex);
            }
            else if (this.extract == TableOrder.BY_COLUMN) {
                result = this.source.getValue(keyIndex, this.index);
            }
        }
        return result;
    }

    /**
     * Sends a {@link DatasetChangeEvent} to all registered listeners, with
     * this (not the underlying) dataset as the source.
     *
     * @param event  the event (ignored, a new event with this dataset as the
     *     source is sent to the listeners).
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        fireDatasetChanged();
    }

    /**
     * Tests this dataset for equality with an arbitrary object, returning
     * {@code true} if {@code obj} is a dataset containing the same
     * keys and values in the same order as this dataset.
     *
     * @param obj  the object to test ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PieDataset)) {
            return false;
        }
        PieDataset that = (PieDataset) obj;
        int count = getItemCount();
        if (that.getItemCount() != count) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            Comparable k1 = getKey(i);
            Comparable k2 = that.getKey(i);
            if (!k1.equals(k2)) {
                return false;
            }

            Number v1 = getValue(i);
            Number v2 = that.getValue(i);
            if (v1 == null) {
                if (v2 != null) {
                    return false;
                }
            }
            else {
                if (!v1.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }

}
