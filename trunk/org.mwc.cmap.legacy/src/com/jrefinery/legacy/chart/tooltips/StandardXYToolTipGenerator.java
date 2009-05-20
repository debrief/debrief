/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
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
 * -------------------------------
 * StandardXYToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardXYToolTipGenerator.java,v 1.1.1.1 2003/07/17 10:06:45 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 02-Apr-2002 : Modified to handle null y-values (DG);
 * 09-Apr-2002 : Added formatting objects for the x and y values (DG);
 * 30-May-2002 : Added series name to standard tool tip (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart.tooltips;

import java.text.NumberFormat;

import com.jrefinery.legacy.data.XYDataset;

/**
 * A standard tool tip generator for plots that use data from an XYDataset.
 *
 * @author DG
 */
public class StandardXYToolTipGenerator implements XYToolTipGenerator {

    /** A formatter for the x value. */
    private NumberFormat xFormat;

    /** A formatter for the y value. */
    private NumberFormat yFormat;

    /**
     * Creates a tool tip generator using default number formatters.
     */
    public StandardXYToolTipGenerator() {

        this(NumberFormat.getNumberInstance(), NumberFormat.getNumberInstance());

    }

    /**
     * Cnstructs a tool tip generator using the specified number formatters.
     *
     * @param xFormat  the format object for the x values.
     * @param yFormat  the format object for the y values.
     */
    public StandardXYToolTipGenerator(NumberFormat xFormat, NumberFormat yFormat) {

        this.xFormat = xFormat;
        this.yFormat = yFormat;

    }

    /**
     * Returns the number formatter for the x-values.
     *
     * @return the number formatter for the x-values.
     */
    public NumberFormat getXFormat() {
        return this.xFormat;
    }

    /**
     * Returns the number formatter for the y-values.
     *
     * @return the number formatter for the y-values.
     */
    public NumberFormat getYFormat() {
        return this.yFormat;
    }

    /**
     * Generates a tool tip text item for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the tool tip text.
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        String result = data.getSeriesName(series) + ": ";
        Number x = data.getXValue(series, item);
        result = result + "x: " + xFormat.format(x);

        Number y = data.getYValue(series, item);
        if (y != null) {
            result = result + ", y: " + yFormat.format(y);
        }
        else {
            result = result + ", y: null";
        }

        return result;
    }

}
