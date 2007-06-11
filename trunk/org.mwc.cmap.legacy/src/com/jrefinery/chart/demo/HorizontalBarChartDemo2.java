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
 * ----------------------------
 * HorizontalBarChartDemo2.java
 * ----------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalBarChartDemo2.java,v 1.1.1.1 2003/07/17 10:06:33 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 07-Aug-2002 : Version 1 (DG);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.CategoryAxis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.ui.RefineryUtilities;

/**
 * Another horizontal bar chart demo.  This time all the extras (titles, legend and axes) are
 * removed, to display just a single bar.
 *
 * @author DG
 */
public class HorizontalBarChartDemo2 extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public HorizontalBarChartDemo2(String title) {

        super(title);

        // create a dataset...
        double[][] data = new double[][] { { 83.0 } };

        DefaultCategoryDataset dataset = new DefaultCategoryDataset(data);

        // set the series names...
        dataset.setSeriesName(0, "First");

        // set the category names...
        String[] categories = new String[] { "Factor 1" };
        dataset.setCategories(categories);

        // create the chart...
        JFreeChart chart = ChartFactory.createHorizontalBarChart(
                                                     null,  // chart title
                                                     "Category",             // domain axis label
                                                     "Score (%)",            // range axis label
                                                     dataset,                // data
                                                     false                   // include legend
                                                 );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.yellow);  // not seen
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setInsets(null);
        plot.setSeriesPaint(0, Color.blue);
        plot.setIntroGapPercent(0.20);
        plot.setTrailGapPercent(0.20);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 100.0);
        rangeAxis.setVisible(false);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible(false);
        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
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

        HorizontalBarChartDemo2 demo = new HorizontalBarChartDemo2("Minimal Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
