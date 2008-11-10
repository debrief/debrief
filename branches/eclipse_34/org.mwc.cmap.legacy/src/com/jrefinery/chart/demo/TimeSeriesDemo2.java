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
 * TimeSeriesDemo2.java
 * --------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo2.java,v 1.1.1.1 2003/07/17 10:06:37 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.Marker;
import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.Quarter;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demo showing a time series (quarterly data) with a null value.  The null value causes a gap
 * in the line connecting the data points.
 *
 * @author DG
 */
public class TimeSeriesDemo2 extends ApplicationFrame {

    /**
     * A demonstration application showing a quarterly time series containing a null value.
     *
     * @param title  the frame title.
     */
    public TimeSeriesDemo2(String title) {

        super(title);

        BasicTimeSeries series = new BasicTimeSeries("Quarterly Data", Quarter.class);
        series.add(new Quarter(1, 2001), 500.2);
        series.add(new Quarter(2, 2001), 694.1);
        series.add(new Quarter(3, 2001), 734.4);
        series.add(new Quarter(4, 2001), 453.2);
        series.add(new Quarter(1, 2002), 500.2);
        series.add(new Quarter(2, 2002), null);
        series.add(new Quarter(3, 2002), 734.4);
        series.add(new Quarter(4, 2002), 453.2);
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Time Series Demo 2",
                                                              "Time", "Value",
                                                              dataset, true);
        chart.getXYPlot().addRangeMarker(new Marker(550));
        Quarter q = new Quarter(2, 2002);
        chart.getXYPlot().addDomainMarker(new Marker((double) q.getMiddle()));
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        TimeSeriesDemo2 demo = new TimeSeriesDemo2("Time Series Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
