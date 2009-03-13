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
 * -----------------
 * XYSeriesDemo.java
 * -----------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYSeriesDemo.java,v 1.1.1.1 2003/07/17 10:06:37 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 * 11-Jun-2002 : Inserted value out of order to see that it works (DG);
 * 11-Oct-2002 : Fixed issues reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import com.jrefinery.data.XYSeries;
import com.jrefinery.data.XYSeriesCollection;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A simple demo for XYSeries.
 *
 * @author DG
 */
public class XYSeriesDemo extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public XYSeriesDemo(String title) {

        super(title);
        XYSeries series = new XYSeries("Random Data");
        series.add(1.0, 500.2);
        series.add(5.0, 694.1);
        series.add(4.0, 100.0);
        series.add(12.5, 734.4);
        series.add(17.3, 453.2);
        series.add(21.2, 500.2);
        series.add(21.9, null);
        series.add(25.6, 734.4);
        series.add(30.0, 453.2);
        XYSeriesCollection data = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createLineXYChart("XY Series Demo",
                                                          "X", "Y", data, true);

        chart.getPlot().setSeriesPaint(new Paint[] { Color.blue });
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

        XYSeriesDemo demo = new XYSeriesDemo("XY Series Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
