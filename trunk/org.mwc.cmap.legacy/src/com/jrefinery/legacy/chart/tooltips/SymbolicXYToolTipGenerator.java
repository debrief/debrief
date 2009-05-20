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
 * SymbolicXYToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2001, 2002, by Anthony Boulestreau and Contributors.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 *
 * Changes
 * -------
 * 29-Mar-2002 : Version 1, contributed by Anthony Boulestreau (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart.tooltips;

import com.jrefinery.legacy.data.TimePeriod;
import com.jrefinery.legacy.data.TimeSeriesCollection;
import com.jrefinery.legacy.data.XYDataset;
import com.jrefinery.legacy.data.XisSymbolic;
import com.jrefinery.legacy.data.YisSymbolic;

/**
 * A standard tooltip generator for plots that use data from an XYDataset.
 *
 * @author AB
 */
public class SymbolicXYToolTipGenerator implements XYToolTipGenerator {

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

        String x, y;
        if (data instanceof YisSymbolic) {
            y = ((YisSymbolic) data).getYSymbolicValue(series, item);
        }
        else {
            Number n = data.getYValue(series, item);
            y = Double.toString(round(n.doubleValue(), 2));
        }
        if (data instanceof XisSymbolic) {
            x = ((XisSymbolic) data).getXSymbolicValue(series, item);
        }
        else if (data instanceof TimeSeriesCollection) {
            TimePeriod p = ((TimeSeriesCollection) data).getSeries(series).getTimePeriod(item);
            x = p.toString();
        }
        else {
            Number n = data.getXValue(series, item);
            x = Double.toString(round(n.doubleValue(), 2));
        }
        return "X: " + x + ", Y: " + y;
    }

    /**
    * Round a double value. Not used by any class.
    *
    * @param value  the value.
    * @param nb  the exponent.
    *
    * @return  the rounded value.
    */
    private static double round(double value, int nb) {
        if (nb <= 0) {
            return Math.floor(value + 0.5d);
        }
        double p = Math.pow(10, nb);
        double tempval = Math.floor(value * p + 0.5d);
        return tempval / p;
    }

}
