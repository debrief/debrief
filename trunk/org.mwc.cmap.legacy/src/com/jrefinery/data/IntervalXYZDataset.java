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
 * IntervalXYZDataset.java
 * -----------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: IntervalXYZDataset.java,v 1.1.1.1 2003/07/17 10:06:53 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 31-Oct-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

/**
 * An extension of the XYZDataset interface that allows a range of data to be
 * defined for any of the X values, the Y values, and the Z values.
 *
 * @author DG
 */
public interface IntervalXYZDataset extends XYZDataset {

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the starting X value for the specified series and item.
     */
    public Number getStartXValue(int series, int item);

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the ending X value for the specified series and item.
     */
    public Number getEndXValue(int series, int item);

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the starting Y value for the specified series and item.
     */
    public Number getStartYValue(int series, int item);

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the ending Y value for the specified series and item.
     */
    public Number getEndYValue(int series, int item);

    /**
     * Returns the starting Z value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the starting Z value for the specified series and item.
     */
    public Number getStartZValue(int series, int item);

    /**
     * Returns the ending Z value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the ending Z value for the specified series and item.
     */
    public Number getEndZValue(int series, int item);

}
