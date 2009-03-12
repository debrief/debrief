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
 * -------------------
 * EmptyXYDataset.java
 * -------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: EmptyXYDataset.java,v 1.1.1.1 2003/07/17 10:06:32 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 22-Nov-2001 : Version 1 (DG);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.XYDataset;
import com.jrefinery.data.AbstractSeriesDataset;

/**
 * An empty dataset for testing purposes.
 *
 * @author DG
 */
public class EmptyXYDataset extends AbstractSeriesDataset implements XYDataset {

    /**
     * Default constructor.
     */
    public EmptyXYDataset() {
    }

    /**
     * Returns the x-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return  the x-value (always null for this class).
     */
    public Number getXValue(int series, int item) {
        return null;
    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return  the y-value (always null for this class).
     */
    public Number getYValue(int series, int item) {
        return null;
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the series count (always zero for this class).
     */
    public int getSeriesCount() {
        return 0;
    }

    /**
     * Returns the name of the series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of the series (always null in this class).
     */
    public String getSeriesName(int series) {
        return null;
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the item count (always zero in this class).
     */
    public int getItemCount(int series) {
        return 0;
    }

}
