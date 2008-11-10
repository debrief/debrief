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
 * ----------------------------------
 * MovingAveragePlotFitAlgorithm.java
 * ----------------------------------
 * (C) Copyright 2001, 2002, by Matthew Wright and Contributors.
 *
 * Original Author:  Matthew Wright;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: MovingAveragePlotFitAlgorithm.java,v 1.1.1.1 2003/07/17 10:06:31 Ian.Mayo Exp $
 *
 * Changes (from 15-Oct-2001)
 * --------------------------
 * 15-Oct-2001 : Data source classes in new package com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 08-Nov-2001 : Removed redundant import statements, tidied up Javadoc comments (DG);
 *
 */

package com.jrefinery.chart.data;

import java.util.Vector;
import com.jrefinery.data.Statistics;
import com.jrefinery.data.XYDataset;

/**
 * Calculates a moving average for an XYDataset.
 *
 * @author MW
 */
public class MovingAveragePlotFitAlgorithm implements PlotFitAlgorithm {

    /** The underlying dataset. */
    private XYDataset dataset;

    /** The moving average period. */
    private int period = 5;

    /** ?? */
    private Vector plots;

    /**
     * @return the name that you want to see in the legend.
     */
    public String getName() {
        return "Moving Average";
    }

    /**
     * Sets the period for this moving average algorithm.
     *
     * @param period  the number of points to include in the average.
     */
    public void setPeriod(int period) {
        this.period = period;
    }

    /**
     * @param ds  the underlying XYDataset.
     */
    public void setXYDataset(XYDataset ds) {

        this.dataset = ds;

        /*
         * build the x and y data arrays to be passed to the
         * statistics class to get a linear fit and store them
         * for each dataset in the datasets Vector
         */
        Vector datasets = new Vector();
        for (int i = 0; i < ds.getSeriesCount(); i++) {
            int seriessize = ds.getItemCount(i);
            Number[] xData = new Number[seriessize];
            Number[] yData = new Number[seriessize];
            for (int j = 0; j < seriessize; j++) {
                xData[j] = ds.getXValue(i, j);
                yData[j] = ds.getYValue(i, j);
            }
            Vector pair = new Vector();
            pair.addElement(xData);
            pair.addElement(yData);
            datasets.addElement(pair);
        }
        plots = new Vector();
        for (int j = 0; j < datasets.size(); j++) {
            Vector pair = (Vector) datasets.elementAt(j);
            Number[] xData = (Number[]) pair.elementAt(0);
            Number[] yData = (Number[]) pair.elementAt(1);
            plots.addElement(new ArrayHolder(Statistics.getMovingAverage(xData, yData, period)));
        }

    }

    /**
     * Returns the y-value for any x-value.
     * @param x     The x-value.
     * @param series    The series.
     * @return      The y-value
     */
    public Number getY(int series, Number x) {

        /*
         * for a moving average, this returns a number if there is a match
         * for that y and series, otherwise, it returns a null reference
         */
        double[][] mavg = ((ArrayHolder) plots.elementAt(series)).getArray();
        for (int j = 0; j < mavg.length; j++) {

            /* if the x matches up, we have a moving average point for this x */
            if (mavg[j][0] == x.doubleValue()) {
                return new Double(mavg[j][1]);
            }
        }
        /* if we don't return null */
        return null;
    }

}

/**
 * A utility class to hold the moving average arrays in a Vector.
 *
 * @author MW
 */
class ArrayHolder {

    /** The array. */
    private double[][] array;

    /**
     * Creates a new array holder.
     *
     * @param array  the array.
     */
    ArrayHolder(double[][] array) {
        this.array = array;
    }

    /**
     * Returns the array.
     *
     * @return the array.
     */
    public double[][] getArray() {
        return array;
    }

}
