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
 * -------------------
 * HighLowDataset.java
 * -------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Sylvain Vieujot;
 *
 * $Id: HighLowDataset.java,v 1.1.1.1 2003/07/17 10:06:53 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Updated header info (DG);
 * 16-Oct-2001 : Moved to package com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 05-Feb-2002 : Added getVolumeValue() method, as requested by Sylvain Vieujot (DG);
 *
 */

package com.jrefinery.data;

/**
 * An interface that defines data in the form of (x, high, low, open, close) tuples.
 * <P>
 * Example: JFreeChart used this interface to obtain data for high-low-open-close plots.
 *
 * @author DG
 *
 */
public interface HighLowDataset extends XYDataset {

    /**
     * Returns the high-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the high-value for the specified series and item.
     */
    public Number getHighValue(int series, int item);

    /**
     * Returns the low-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the low-value for the specified series and item.
     */
    public Number getLowValue(int series, int item);

    /**
     * Returns the open-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the open-value for the specified series and item.
     */
    public Number getOpenValue(int series, int item);

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value for the specified series and item.
     */
    public Number getCloseValue(int series, int item);

    /**
     * Returns the volume for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the volume for the specified series and item.
     */
    public Number getVolumeValue(int series, int item);

}
