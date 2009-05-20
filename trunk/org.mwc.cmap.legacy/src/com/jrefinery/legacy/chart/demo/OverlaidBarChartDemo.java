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
 * -------------------------
 * OverlaidBarChartDemo.java
 * -------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: OverlaidBarChartDemo.java,v 1.1.1.1 2003/07/17 10:06:34 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 20-Sep-2002 : Version 1 (DG);
 * 11-Oct-2002 : Added tooltips, modified series colors, centered frame on screen (DG);
 *
 */

package com.jrefinery.legacy.chart.demo;

import java.awt.Color;
import com.jrefinery.legacy.chart.CategoryItemRenderer;
import com.jrefinery.legacy.chart.ChartPanel;
import com.jrefinery.legacy.chart.JFreeChart;
import com.jrefinery.legacy.chart.LineAndShapeRenderer;
import com.jrefinery.legacy.chart.OverlaidVerticalCategoryPlot;
import com.jrefinery.legacy.chart.VerticalBarRenderer;
import com.jrefinery.legacy.chart.VerticalCategoryPlot;
import com.jrefinery.legacy.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.legacy.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.legacy.data.DefaultCategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a vertical bar chart overlaid
 * with a line chart.
 *
 * @author DG
 */
public class OverlaidBarChartDemo extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Default constructor.
     *
     * @param  title the frame title.
     */
    public OverlaidBarChartDemo(String title) {

        super(title);

        // create the first dataset...
        double[][] data1 = new double[][] {
            { 1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0 },
            { 5.0, 7.0, 6.0, 8.0, 4.0, 4.0, 2.0, 1.0 }
        };
        DefaultCategoryDataset dataset1 = new DefaultCategoryDataset(data1);
        dataset1.setSeriesName(0, "First");
        dataset1.setSeriesName(1, "Second");
        String[] categories1 = new String[] { "Category 1", "Category 2", "Category 3",
                                              "Category 4", "Category 5", "Category 6",
                                              "Category 7", "Category 8"  };
        dataset1.setCategories(categories1);

        // create the first plot...
        CategoryToolTipGenerator tooltips = new StandardCategoryToolTipGenerator();
        CategoryItemRenderer renderer = new VerticalBarRenderer(tooltips, null);
        VerticalCategoryPlot plot1 = new VerticalCategoryPlot(dataset1, null, null, renderer);

        // create the second dataset...
        double[][] data2 = new double[][] {
            { 9.0, 7.0, 2.0, 6.0, 6.0, 9.0, 5.0, 4.0 }
        };
        DefaultCategoryDataset dataset2 = new DefaultCategoryDataset(data2);
        dataset2.setSeriesName(0, "Level");
        String[] categories2 = new String[] { "Category 1", "Category 2", "Category 3",
                                              "Category 4", "Category 5", "Category 6",
                                              "Category 7", "Category 8"  };
        dataset2.setCategories(categories2);

        // create the second plot...
        CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
        VerticalCategoryPlot plot2 = new VerticalCategoryPlot(dataset2, null, null, renderer2);
        plot2.setSeriesPaint(0, Color.yellow);

        // create the overlaid plot...
        OverlaidVerticalCategoryPlot plot = new OverlaidVerticalCategoryPlot("Category", "Value",
                                                                             categories1);
        plot.add(plot1);
        plot.add(plot2);
        JFreeChart chart = new JFreeChart(plot);

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

        OverlaidBarChartDemo demo = new OverlaidBarChartDemo("Overlaid Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
