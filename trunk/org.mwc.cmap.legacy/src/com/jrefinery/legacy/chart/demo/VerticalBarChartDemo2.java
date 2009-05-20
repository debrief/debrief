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
 * --------------------------
 * VerticalBarChartDemo2.java
 * --------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: VerticalBarChartDemo2.java,v 1.1.1.1 2003/07/17 10:06:37 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 22-Aug-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.legacy.chart.demo;

import java.awt.Paint;
import java.awt.Color;
import com.jrefinery.legacy.chart.CategoryPlot;
import com.jrefinery.legacy.chart.ChartFactory;
import com.jrefinery.legacy.chart.ChartPanel;
import com.jrefinery.legacy.chart.HorizontalCategoryAxis;
import com.jrefinery.legacy.chart.JFreeChart;
import com.jrefinery.legacy.chart.NumberAxis;
import com.jrefinery.legacy.chart.ValueAxis;
import com.jrefinery.legacy.chart.VerticalBarRenderer;
import com.jrefinery.legacy.data.DefaultCategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A bar chart with just one series.  In this special case, it is possible to use multiple colors
 * for the items within the series.
 *
 * @author DG
 */
public class VerticalBarChartDemo2 extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    @SuppressWarnings("deprecation")
		public VerticalBarChartDemo2(String title) {

        super(title);

        // create a dataset...
        double[][] data = new double[][] {
            { 4.0, 3.0, 2.0, 3.0, 6.0, 3.0, 4.0, 3.0 }
        };

        DefaultCategoryDataset dataset = new DefaultCategoryDataset(data);

        // set the category names...
        String[] categories = new String[] { "Category 1", "Category 2",
                                             "Category 3", "Category 4",
                                             "Category 5", "Category 6",
                                             "Category 7", "Category 8"  };
        dataset.setCategories(categories);

        // create the chart...
        JFreeChart chart = ChartFactory.createVerticalBarChart(
                                                     "Vertical Bar Chart",  // chart title
                                                     "Category",            // domain axis label
                                                     "Value",               // range axis label
                                                     dataset,               // data
                                                     false                  // include legend
                                                 );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.lightGray);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setLabelsVisible(true);
        VerticalBarRenderer renderer = (VerticalBarRenderer) plot.getRenderer();

        // set the color for each category (this only works for single series datasets)...
        renderer.setCategoriesPaint(new Paint[] { Color.red, Color.blue, Color.green,
                                                  Color.yellow, Color.orange, Color.cyan,
                                                  Color.magenta, Color.blue });

        // change the category labels to vertical...
        HorizontalCategoryAxis domainAxis = (HorizontalCategoryAxis) plot.getDomainAxis();
        domainAxis.setSkipCategoryLabelsToFit(true);

        // change the margin at the top of the range axis...
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.10);

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

        VerticalBarChartDemo2 demo = new VerticalBarChartDemo2("Vertical Bar Chart Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
