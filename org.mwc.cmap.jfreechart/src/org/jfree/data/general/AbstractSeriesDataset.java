/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2020, by Object Refinery Limited and Contributors.
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
 * --------------------------
 * AbstractSeriesDataset.java
 * --------------------------
 * (C) Copyright 2001-2020, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 */

package org.jfree.data.general;

import java.io.Serializable;

/**
 * An abstract implementation of the {@link SeriesDataset} interface,
 * containing a mechanism for registering change listeners.
 */
public abstract class AbstractSeriesDataset extends AbstractDataset
        implements SeriesDataset, SeriesChangeListener, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -6074996219705033171L;

    /**
     * Creates a new dataset.
     */
    protected AbstractSeriesDataset() {
        super();
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    @Override
    public abstract int getSeriesCount();

    /**
     * Returns the key for a series.
     * <p>
     * If {@code series} is not within the specified range, the
     * implementing method should throw an {@link IndexOutOfBoundsException}
     * (preferred) or an {@link IllegalArgumentException}.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     *
     * @return The series key.
     */
    @Override
    public abstract Comparable getSeriesKey(int series);

    /**
     * Returns the index of the named series, or -1.
     *
     * @param seriesKey  the series key ({@code null} permitted).
     *
     * @return The index.
     */
    @Override
    public int indexOf(Comparable seriesKey) {
        int seriesCount = getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
           if (getSeriesKey(s).equals(seriesKey)) {
               return s;
           }
        }
        return -1;
    }

    /**
     * Called when a series belonging to the dataset changes.
     *
     * @param event  information about the change.
     */
    @Override
    public void seriesChanged(SeriesChangeEvent event) {
        fireDatasetChanged();
    }

}
