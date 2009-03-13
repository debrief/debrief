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
 * TimeSeriesToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: TimeSeriesToolTipGenerator.java,v 1.1.1.1 2003/07/17 10:06:45 Ian.Mayo Exp $
 *
 * Changes (since 30-May-2002):
 * ----------------------------
 * 30-May-2002 : Added series name to tool tip (DG);
 * 29-Aug-2002 : Modified so that series name is not shown if null (RA);
 *
 */

package com.jrefinery.chart.tooltips;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Date;
import com.jrefinery.data.XYDataset;

/**
 * A standard tool tip generator for time series plots.
 *
 * @author DG
 */
public class TimeSeriesToolTipGenerator implements XYToolTipGenerator {

    /** A formatter for the time. */
    private DateFormat dateFormat;

    /** A formatter for the value. */
    private NumberFormat numberFormat;

    /**
     * Default constructor.
     */
    public TimeSeriesToolTipGenerator() {

        this(DateFormat.getInstance(), NumberFormat.getNumberInstance());

    }

    /**
     * Creates a tool tip generator with the specified date and number format strings.
     *
     * @param dateFormat  the date format.
     * @param valueFormat  the value format.
     */
    public TimeSeriesToolTipGenerator(String dateFormat, String valueFormat) {
        this(new SimpleDateFormat(dateFormat), new DecimalFormat(valueFormat));
    }

    /**
     * Constructs a new tooltip generator using the specified number formats.
     *
     * @param dateFormat  the date formatter.
     * @param numberFormat  the number formatter.
     */
    public TimeSeriesToolTipGenerator(DateFormat dateFormat, NumberFormat numberFormat) {
        this.dateFormat = dateFormat;
        this.numberFormat = numberFormat;
    }

    /**
     * Returns the date formatter.
     *
     * @return the date formatter.
     */
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    /**
     * Returns the number formatter.
     *
     * @return the number formatter.
     */
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    /**
     * Generates a tool tip text item for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series number (zero-based index).
     * @param item  the item number (zero-based index).
     *
     * @return the tool tip text.
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        String result = "";
        String seriesName = data.getSeriesName(series);
        if (seriesName != null) {
            result += seriesName + ": ";
        }
        long x = data.getXValue(series, item).longValue();
        result = result + "date = " + this.dateFormat.format(new Date(x));

        Number y = data.getYValue(series, item);
        if (y != null) {
            result = result + ", value = " + this.numberFormat.format(y);
        }
        else {
            result = result + ", value = null";
        }

        return result;
    }

}
