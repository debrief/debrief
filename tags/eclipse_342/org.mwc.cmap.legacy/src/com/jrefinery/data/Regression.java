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
 * ---------------
 * Regression.java
 * ---------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Regression.java,v 1.1.1.1 2003/07/17 10:06:55 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 30-Sep-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

/**
 * A utility class for fitting regression curves to data.
 *
 * @author DG.
 */
public class Regression {

    /**
     * Returns the parameters 'a' and 'b' for an equation y = a + bx, fitted to the data using
     * ordinary least squares regression.
     * <p>
     * The result is returned as a double[], where result[0] --> a, and result[1] --> b.
     *
     * @param data  the data.
     *
     * @return  the parameters.
     */
    public static double[] getOLSRegression(double[][] data) {

        int n = data.length;
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }

        double sumX = 0;
        double sumY = 0;
        double sumXX = 0;
        double sumXY = 0;
        for (int i = 0; i < n; i++) {
            double x = data[i][0];
            double y = data[i][1];
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
        }
        double sxx = sumXX - (sumX * sumX) / n;
        double sxy = sumXY - (sumX * sumY) / n;
        double xbar = sumX / n;
        double ybar = sumY / n;

        double[] result = new double[2];
        result[1] = sxy / sxx;
        result[0] = ybar - result[1] * xbar;

        return result;

    }

    /**
     * Returns the parameters 'a' and 'b' for an equation y = a + bx, fitted to the data using
     * ordinary least squares regression.
     * <p>
     * The result is returned as a double[], where result[0] --> a, and result[1] --> b.
     *
     * @param data  the data.
     * @param series  the series (zero-based index).
     *
     * @return  the parameters.
     */
    public static double[] getOLSRegression(XYDataset data, int series) {

        int n = data.getItemCount(series);
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }

        double sumX = 0;
        double sumY = 0;
        double sumXX = 0;
        double sumXY = 0;
        for (int i = 0; i < n; i++) {
            double x = data.getXValue(series, i).doubleValue();
            double y = data.getYValue(series, i).doubleValue();
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
        }
        double sxx = sumXX - (sumX * sumX) / n;
        double sxy = sumXY - (sumX * sumY) / n;
        double xbar = sumX / n;
        double ybar = sumY / n;

        double[] result = new double[2];
        result[1] = sxy / sxx;
        result[0] = ybar - result[1] * xbar;

        return result;

    }


    /**
     * Returns the parameters 'a' and 'b' for an equation y = ax^b, fitted to the data using
     * a power regression equation.
     * <p>
     * The result is returned as an array, where double[0] --> a, and double[1] --> b.
     *
     * @param data  the data.
     *
     * @return  the parameters.
     */
    public static double[] getPowerRegression(double[][] data) {

        int n = data.length;
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }

        double sumX = 0;
        double sumY = 0;
        double sumXX = 0;
        double sumXY = 0;
        for (int i = 0; i < n; i++) {
            double x = Math.log(data[i][0]);
            double y = Math.log(data[i][1]);
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
        }
        double sxx = sumXX - (sumX * sumX) / n;
        double sxy = sumXY - (sumX * sumY) / n;
        double xbar = sumX / n;
        double ybar = sumY / n;

        double[] result = new double[2];
        result[1] = sxy / sxx;
        result[0] = Math.pow(Math.exp(1.0), ybar - result[1] * xbar);

        return result;

    }

    /**
     * Returns the parameters 'a' and 'b' for an equation y = ax^b, fitted to the data using
     * a power regression equation.
     * <p>
     * The result is returned as an array, where double[0] --> a, and double[1] --> b.
     *
     * @param data  the data.
     * @param series  the series to fit the regression line against.
     *
     * @return  the parameters.
     */
    public static double[] getPowerRegression(XYDataset data, int series) {

        int n = data.getItemCount(series);
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }

        double sumX = 0;
        double sumY = 0;
        double sumXX = 0;
        double sumXY = 0;
        for (int i = 0; i < n; i++) {
            double x = Math.log(data.getXValue(series, i).doubleValue());
            double y = Math.log(data.getYValue(series, i).doubleValue());
            sumX += x;
            sumY += y;
            double xx = x * x;
            sumXX += xx;
            double xy = x * y;
            sumXY += xy;
        }
        double sxx = sumXX - (sumX * sumX) / n;
        double sxy = sumXY - (sumX * sumY) / n;
        double xbar = sumX / n;
        double ybar = sumY / n;

        double[] result = new double[2];
        result[1] = sxy / sxx;
        result[0] = Math.pow(Math.exp(1.0), ybar - result[1] * xbar);

        return result;

    }

}
