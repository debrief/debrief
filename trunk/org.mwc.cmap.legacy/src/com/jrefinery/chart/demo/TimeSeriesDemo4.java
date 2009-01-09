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
 * TimeSeriesDemo4.java
 * --------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesDemo4.java,v 1.1.1.1 2003/07/17 10:06:37 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary import (DG);
 * 28-Aug-2002 : Centered frame on screen (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Insets;
import java.awt.Color;
import java.awt.BasicStroke;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.StandardXYItemRenderer;
import com.jrefinery.chart.XYItemRenderer;
import com.jrefinery.chart.Marker;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.Hour;
import com.jrefinery.data.Day;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * An example of a time series chart using hourly data and including a null value.  The plot
 * has an image set for the background, and a blue range marker is added to the plot.
 *
 * @author DG
 */
public class TimeSeriesDemo4 extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * A demonstration application showing a quarterly time series containing a null value.
     *
     * @param title  the frame title.
     */
    public TimeSeriesDemo4(String title) {

        super(title);
        BasicTimeSeries series = new BasicTimeSeries("Random Data", Hour.class);
        Day today = new Day();
        series.add(new Hour(1, today), 500.2);
        series.add(new Hour(2, today), 694.1);
        series.add(new Hour(3, today), 734.4);
        series.add(new Hour(4, today), 453.2);
        series.add(new Hour(7, today), 500.2);
        series.add(new Hour(8, today), null);
        series.add(new Hour(12, today), 734.4);
        series.add(new Hour(16, today), 453.2);
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        // create a title with Unicode characters (currency symbols in this case)...
        String chartTitle = "\u20A2\u20A2\u20A2\u20A3\u20A4\u20A5\u20A6\u20A7\u20A8\u20A9\u20AA";
        JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle,
                                                              "Time", "Value",
                                                              dataset, true);

        XYPlot plot = chart.getXYPlot();
        plot.setInsets(new Insets(0, 0, 0, 20));
        plot.addRangeMarker(new Marker(700, Color.blue, new BasicStroke(1.0f), Color.blue, 0.8f));
        plot.setBackgroundPaint(null);
        plot.setBackgroundImage(JFreeChart.INFO.getLogo());
        XYItemRenderer renderer = plot.getRenderer();
        if (renderer instanceof StandardXYItemRenderer) {
            StandardXYItemRenderer r = (StandardXYItemRenderer) renderer;
            r.setPlotShapes(true);
            r.setDefaultShapeFilled(true);
            r.setDefaultShapeScale(10.0);
        }
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

        TimeSeriesDemo4 demo = new TimeSeriesDemo4("Time Series Demo 4");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
