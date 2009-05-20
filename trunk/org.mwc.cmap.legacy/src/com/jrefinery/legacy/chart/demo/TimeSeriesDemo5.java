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
 * --------------------
 * TimeSeriesDemo5.java
 * --------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo5.java,v 1.1.1.1 2003/07/17 10:06:37 Ian.Mayo Exp $
 *
 * Changes (from 24-Apr-2002)
 * --------------------------
 * 24-Apr-2002 : Added standard header (DG);
 * 10-Oct-2002 : Renamed JFreeChartDemo2 --> TimeSeriesDemo5 (DG);
 *
 */

package com.jrefinery.legacy.chart.demo;

import com.jrefinery.legacy.chart.ChartFactory;
import com.jrefinery.legacy.chart.ChartFrame;
import com.jrefinery.legacy.chart.JFreeChart;
import com.jrefinery.legacy.data.BasicTimeSeries;
import com.jrefinery.legacy.data.Day;
import com.jrefinery.legacy.data.SeriesException;
import com.jrefinery.legacy.data.TimeSeriesCollection;
import com.jrefinery.legacy.data.XYDataset;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A time series chart with 4000 data points, to get an idea of how JFreeChart performs with a
 * larger dataset.  You can see that it slows down significantly, so this needs to be worked on
 * (4000 points is not that many!).
 *
 * @author DG
 */
public class TimeSeriesDemo5 {

    /**
     * Starting point for the application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        BasicTimeSeries series = new BasicTimeSeries("Random Data");

        Day current = new Day(1, 1, 1990);
        double value = 100.0;

        for (int i = 0; i < 4000; i++) {
            try {
                value = value + Math.random() - 0.5;
                series.add(current, new Double(value));
                current = (Day) current.next();
            }
            catch (SeriesException e) {
                System.err.println("Error adding to series");
            }
        }

        XYDataset data = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart("Test", "Day", "Value", data, false);
        ChartFrame frame = new ChartFrame("Test", chart);
        frame.pack();
        RefineryUtilities.positionFrameRandomly(frame);
        frame.setVisible(true);

    }

}
