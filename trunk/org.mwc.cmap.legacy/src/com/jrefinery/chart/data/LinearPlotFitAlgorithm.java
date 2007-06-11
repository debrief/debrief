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
 * ---------------------------
 * LinearPlotFitAlgorithm.java
 * ---------------------------
 * (C) Copyright 2000-2002, by Matthew Wright and Contributors.
 *
 * Original Author:  Matthew Wright;
 * Contributor(s):   David Gilbert;
 *
 * $Id: LinearPlotFitAlgorithm.java,v 1.1.1.1 2003/07/17 10:06:31 Ian.Mayo Exp $
 *
 * Changes (from 08-Nov-2001)
 * --------------------------
 * 08-Nov-2001 : Added standard header, removed redundant import statements and tidied Javadoc
 *               comments (DG);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.data;

import java.util.Vector;
import com.jrefinery.data.Statistics;
import com.jrefinery.data.XYDataset;

/**
 * A linear plot fit algorithm contributed by Matthew Wright.
 *
 * @author MW
 */
public class LinearPlotFitAlgorithm implements PlotFitAlgorithm {

    /** Underlying dataset. */
    private XYDataset dataset;

    /** The data for the linear fit. */
    private double[][] linearFit;

    /**
     * @return  the name that you want to see in the legend.
     */
    public String getName() {
        return "Linear Fit";
    }

    /**
     * Sets the dataset.
     *
     * @param data  the dataset.
     */
    public void setXYDataset(XYDataset data) {

        this.dataset = data;

        // build the x and y data arrays to be passed to the
        // statistics class to get a linear fit and store them
        // for each dataset in the datasets Vector

        Vector datasets = new Vector();
        for (int i = 0; i < data.getSeriesCount(); i++) {
            int seriessize = data.getItemCount(i);
            Number[] xData = new Number[seriessize];
            Number[] yData = new Number[seriessize];
            for (int j = 0; j < seriessize; j++) {
                xData[j] = data.getXValue(i, j);
                yData[j] = data.getYValue(i, j);
            }
            Vector pair = new Vector();
            pair.addElement(xData);
            pair.addElement(yData);
            datasets.addElement(pair);
        }

        // put in the linear fit array
        linearFit = new double[datasets.size()][2];
        for (int i = 0; i < datasets.size(); i++) {
            Vector pair = (Vector) datasets.elementAt(i);
            linearFit[i] = Statistics.getLinearFit((Number[]) pair.elementAt(0),
                                                   (Number[]) pair.elementAt(1));
        }
    }

    /**
     * Returns a y-value for any given x-value.
     *
     * @param x  the x value.
     * @param series  the series.
     *
     * @return the y value.
     */
    public Number getY(int series, Number x) {

         // for a linear fit, this will return the y for the formula
         //  y = a + bx
         //  These are in the private variable linear_fit
         //  a = linear_fit[i][0]
         //  b = linear_fit[i][1]
        return new Double(linearFit[series][0] + linearFit[series][1] * x.doubleValue());

    }

}
