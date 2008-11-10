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
 * -----------------------
 * EventFrequencyDemo.java
 * -----------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: EventFrequencyDemo.java,v 1.1.1.1 2003/07/17 10:06:32 Ian.Mayo Exp $
 *
 * Changes (from 10-Oct-2002)
 * --------------------------
 * 10-Oct-2002 : Added standard header and Javadocs (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import java.awt.Paint;
import java.text.DateFormat;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.HorizontalShapeRenderer;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.tooltips.StandardCategoryToolTipGenerator;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.Day;
import com.jrefinery.date.SerialDate;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demo application showing how to display category data against a date axis.
 *
 * @author DG
 */
public class EventFrequencyDemo extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public EventFrequencyDemo(String title) {

        super(title);

        // create a dataset...
        Number[][] data = new Number[3][4];
        DefaultCategoryDataset dataset = new DefaultCategoryDataset(data);

        // set the series names...
        String[] seriesNames = new String[] { "Add", "Change", "Delete" };
        dataset.setSeriesNames(seriesNames);

        // set the category names...
        String[] categories = new String[] { "Requirement 1", "Requirement 2",
                                             "Requirement 3", "Requirement 4" };
        dataset.setCategories(categories);

        // initialise the data...
        Day d1 = new Day(12, SerialDate.JUNE, 2002);
        Day d2 = new Day(14, SerialDate.JUNE, 2002);
        Day d3 = new Day(15, SerialDate.JUNE, 2002);
        Day d4 = new Day(10, SerialDate.JULY, 2002);
        Day d5 = new Day(20, SerialDate.JULY, 2002);
        Day d6 = new Day(22, SerialDate.AUGUST, 2002);

        dataset.setValue(0, "Requirement 1", new Long(d1.getMiddle()));
        dataset.setValue(0, "Requirement 2", new Long(d1.getMiddle()));
        dataset.setValue(0, "Requirement 3", new Long(d2.getMiddle()));
        dataset.setValue(1, "Requirement 1", new Long(d3.getMiddle()));
        dataset.setValue(1, "Requirement 3", new Long(d4.getMiddle()));
        dataset.setValue(2, "Requirement 2", new Long(d5.getMiddle()));
        dataset.setValue(0, "Requirement 4", new Long(d6.getMiddle()));

        // create the chart...
        JFreeChart chart
            = ChartFactory.createHorizontalBarChart("Event Frequency Demo",  // chart title
                                                    "Category",              // domain axis label
                                                    "Value",                 // range axis label
                                                    dataset,                 // data
                                                    true,                    // include legend
                                                    true,                    // tooltips
                                                    false                    // URLs
                                                    );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.yellow);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeAxis(new HorizontalDateAxis("Date"));
        CategoryToolTipGenerator tooltips
            = new StandardCategoryToolTipGenerator(DateFormat.getDateInstance());
        plot.setRenderer(new HorizontalShapeRenderer(HorizontalShapeRenderer.SHAPES,
                                                     HorizontalShapeRenderer.TOP,
                                                     tooltips,
                                                     null));

        // set the color for each series...
        plot.setSeriesPaint(new Paint[] { Color.green, Color.orange, Color.red });

        // OPTIONAL CUSTOMISATION COMPLETED.

        // add the chart to a panel...
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        this.setContentPane(chartPanel);

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        EventFrequencyDemo demo = new EventFrequencyDemo("Event Frequency Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
