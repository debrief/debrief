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
 * -----------------
 * MeterDataset.java
 * -----------------
 * (C) Copyright 2002, by Hari and Contributors.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: MeterDataset.java,v 1.1.1.1 2003/07/17 10:06:54 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 02-Apr-2002 : Version 1, based on code contributed by Hari (DG);
 * 16-Apr-2002 : Updated version from Hari (DG);
 * 23-Aug-2002 : Updated Javadoc comments (DG);
 *
 */

package com.jrefinery.legacy.data;

/**
 * A dataset containing a single value within an overall range.  In addition, the dataset defines
 * three subranges:  the 'normal' range, the 'warning' range and the 'critical' range.
 * <P>
 * This dataset can be used to supply data to meters and gauges.  MeterPlot and ThermometerPlot
 * are the current implementations.
 *
 * @author Hari
 */
public interface MeterDataset extends Dataset {

    /** A constant representing the 'normal' level. */
    public static final int NORMAL_DATA = 0;

    /** A constant representing the 'warning' level. */
    public static final int WARNING_DATA = 1;

    /** A constant representing the 'critical' level. */
    public static final int CRITICAL_DATA = 2;

    /** A constant representing the full data range. */
    public static final int FULL_DATA = 3;

    /**
     * Returns the value.
     *
     * @return The value.
     */
    public Number getValue();

    /**
     * Returns the lower value in the overall range.
     *
     * @return The lower value.
     */
    public Number getMinimumValue();

    /**
     * Returns the upper value in the overall range.
     *
     * @return The upper value.
     */
    public Number getMaximumValue();

    /**
     * Returns the lower value in the normal range.
     *
     * @return The lower value.
     */
    public Number getMinimumNormalValue();

    /**
     * Returns the upper value in the normal range.
     *
     * @return The upper value.
     */
    public Number getMaximumNormalValue();

    /**
     * Returns the lower value in the warning range.
     *
     * @return The lower value.
     */
    public Number getMinimumWarningValue();

    /**
     * Returns the upper value in the warning range.
     *
     * @return The upper value.
     */
    public Number getMaximumWarningValue();

    /**
     * Returns the lower value in the critical range.
     *
     * @return The lower value.
     */
    public Number getMinimumCriticalValue();

    /**
     * Returns the upper value in the critical range.
     *
     * @return The upper value.
     */
    public Number getMaximumCriticalValue();

    /**
     * Returns true if the value is valid, and false otherwise.
     *
     * @return boolean
     */
    public boolean isValueValid();

    /**
     * Returns a string representing the units on the dial.
     *
     * @return the units.
     */
    public String getUnits();

    /**
     * Returns the border type for the data.
     *
     * @return The border type.
     */
    public int getBorderType();

}
