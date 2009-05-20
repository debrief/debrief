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
 * ------------------------
 * HorizontalBar3DDemo.java
 * ------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalBar3DDemo.java,v 1.1.1.1 2003/07/17 10:06:33 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 29-May-2002 : Version 1 (DG);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart.demo;

import com.jrefinery.legacy.chart.ChartFactory;
import com.jrefinery.legacy.chart.ChartPanel;
import com.jrefinery.legacy.chart.JFreeChart;
import com.jrefinery.legacy.data.CategoryDataset;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a horizontal 3D bar chart using data
 * from a CategoryDataset.
 *
 * @author DG
 */
public class HorizontalBar3DDemo extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public HorizontalBar3DDemo(String title) {

        super(title);
        CategoryDataset dataset = DemoDatasetFactory.createCategoryDataset();

        // create the chart...
        JFreeChart chart = ChartFactory.createHorizontalBarChart3D(
                                                  "Horizontal Bar 3D Demo",  // chart title
                                                  "Category",                // domain axis label
                                                  "Value",                   // range axis label
                                                  dataset,                   // data
                                                  true,                      // include legend
                                                  true,                      // tooltips
                                                  false                      // urls
                                              );

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

        HorizontalBar3DDemo demo = new HorizontalBar3DDemo("Horizontal 3D Bar Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
