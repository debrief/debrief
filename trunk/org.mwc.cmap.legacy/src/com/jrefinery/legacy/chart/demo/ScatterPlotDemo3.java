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
 * ---------------------
 * ScatterPlotDemo3.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: ScatterPlotDemo3.java,v 1.1.1.1 2003/07/17 10:06:36 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 03-Sep-2002 : Version 1 (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 14-Oct-2002 : Renamed ScatterPlotDemo2 --> ScatterPlotDemo3 (DG);
 *
 */

package com.jrefinery.legacy.chart.demo;

import com.jrefinery.legacy.chart.ChartFactory;
import com.jrefinery.legacy.chart.ChartPanel;
import com.jrefinery.legacy.chart.JFreeChart;
import com.jrefinery.legacy.chart.NumberAxis;
import com.jrefinery.legacy.data.XYDataset;
import com.jrefinery.legacy.data.XYSeries;
import com.jrefinery.legacy.data.XYSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * Small demo to test bug report 599411.
 *
 * @author DG
 */
public class ScatterPlotDemo3 extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * A demonstration application showing a scatter plot.
     *
     * @param title  the frame title.
     */
    public ScatterPlotDemo3(String title) {

        super(title);
        XYSeries series = new XYSeries("Test Data");
        series.add(0.058333333333333334, 18.251567840576172);
        series.add(0.06666666666666667, 18.32216453552246);
        series.add(0.09166666666666666, 2.476291662324533E26);  // really large value
        series.add(0.1, 18.553701400756836);
        series.add(0.10833333333333334, 18.60835838317871);
        series.add(0.11666666666666667, 18.66070556640625);
        XYDataset data = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createScatterPlot("Scatter Plot Demo 2",
                                                          "X", "Y", data, true);
        NumberAxis domainAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(false);
        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        ScatterPlotDemo3 demo = new ScatterPlotDemo3("Scatter Plot Demo 3");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
