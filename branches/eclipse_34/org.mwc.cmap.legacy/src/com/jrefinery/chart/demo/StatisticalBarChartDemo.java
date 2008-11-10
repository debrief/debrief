/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * StatisticalBarChartDemo.java
 * ----------------------------
 * (C) Copyright 2002, by Pascal Collet and Contributors.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: StatisticalBarChartDemo.java,v 1.1.1.1 2003/07/17 10:06:36 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1, contributed by Pascal Collet (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFrame;
import com.jrefinery.chart.VerticalCategoryPlot;
import com.jrefinery.chart.CategoryAxis;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.ValueAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.CategoryItemRenderer;
import com.jrefinery.chart.VerticalStatisticalBarRenderer;
import com.jrefinery.data.DefaultStatisticalCategoryDataset;

/**
 * Demonstration of the statistical bar graph.
 *
 * @author PC
 */
public class StatisticalBarChartDemo {

    /** The data colors. */
    private static Color[] dataColors = null;

    /** The label font. */
    private static Font labelFont = null;

    /** The title font. */
    private static Font titleFont = null;

    /** The chart. */
    private JFreeChart chart = null;

    static {
        dataColors = new Color[1];
        dataColors[0] = new Color(51, 102, 153);
        labelFont = new Font("Helvetica", Font.PLAIN, 10);
        titleFont = new Font("Helvetica", Font.BOLD, 14);
    }

    /**
     * Creates a data array.
     *
     * @return a data array.
     */
    private Number[][] createStdDevData() {
        // data[series][category]
        Integer[][] theData = new Integer[3][10];

        // first serie (placebo)
        theData[0][0] = new Integer(10);
        theData[0][1] = new Integer(10);
        theData[0][2] = new Integer(10);
        theData[0][3] = new Integer(10);
        theData[0][4] = new Integer(10);
        theData[0][5] = new Integer(10);
        theData[0][6] = new Integer(10);
        theData[0][7] = new Integer(10);
        theData[0][8] = new Integer(10);
        theData[0][9] = new Integer(10);

        // second serie (compound 1)
        theData[1][0] = new Integer(20);
        theData[1][1] = new Integer(20);
        theData[1][2] = new Integer(20);
        theData[1][3] = new Integer(20);
        theData[1][4] = new Integer(20);
        theData[1][5] = new Integer(20);
        theData[1][6] = new Integer(20);
        theData[1][7] = new Integer(20);
        theData[1][8] = new Integer(20);
        theData[1][9] = new Integer(20);

        // third serie (compound 2)
        theData[2][0] = new Integer(30);
        theData[2][1] = new Integer(30);
        theData[2][2] = new Integer(30);
        theData[2][3] = new Integer(30);
        theData[2][4] = new Integer(30);
        theData[2][5] = new Integer(30);
        theData[2][6] = new Integer(30);
        theData[2][7] = new Integer(30);
        theData[2][8] = new Integer(30);
        theData[2][9] = new Integer(30);

        return theData;
    }

    /**
     * Creates a data array.
     *
     * @return a data array.
     */
    private Number[][] createMeanData() {
        // data[series][category]
        Integer[][] theData = new Integer[3][10];

        // first serie (placebo)
        theData[0][0] = new Integer(100);
        theData[0][1] = new Integer(100);
        theData[0][2] = new Integer(100);
        theData[0][3] = new Integer(100);
        theData[0][4] = new Integer(100);
        theData[0][5] = new Integer(100);
        theData[0][6] = new Integer(100);
        theData[0][7] = new Integer(100);
        theData[0][8] = new Integer(100);
        theData[0][9] = new Integer(100);

        // second serie (compound 1)
        theData[1][0] = new Integer(200);
        theData[1][1] = new Integer(200);
        theData[1][2] = new Integer(200);
        theData[1][3] = new Integer(200);
        theData[1][4] = new Integer(200);
        theData[1][5] = new Integer(200);
        theData[1][6] = new Integer(200);
        theData[1][7] = new Integer(200);
        theData[1][8] = new Integer(200);
        theData[1][9] = new Integer(200);

        // third serie (compound 2)
        theData[2][0] = new Integer(300);
        theData[2][1] = new Integer(300);
        theData[2][2] = new Integer(300);
        theData[2][3] = new Integer(300);
        theData[2][4] = new Integer(300);
        theData[2][5] = new Integer(300);
        theData[2][6] = new Integer(300);
        theData[2][7] = new Integer(300);
        theData[2][8] = new Integer(300);
        theData[2][9] = new Integer(300);

        return theData;
    }

    /**
     * Creates a new demo.
     */
    public StatisticalBarChartDemo() {

        Number[][] mean = createMeanData();
        Number[][] stdDev = createStdDevData();
        DefaultStatisticalCategoryDataset dataset
            = new DefaultStatisticalCategoryDataset(mean, stdDev);
        dataset.setSeriesNames(new String[] {"serie 0", "serie 1", "serie 2"});
        dataset.setCategories(new String[] {"cat 0", "cat 1", "cat 2", "cat 3",
                                                   "cat 4", "cat 5", "cat 6",
                                                   "cat 7", "cat 8", "cat 9"});



        CategoryAxis xAxis = new HorizontalCategoryAxis("x axis");
        ValueAxis yAxis = new VerticalNumberAxis("y axis");

        // define the plot
        CategoryItemRenderer renderer = new VerticalStatisticalBarRenderer();
        VerticalCategoryPlot plot = new VerticalCategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setItemGapsPercent(0.0d); // no gap between the item of the same category
        plot.setIntroGapPercent(0.01d); // percentage of space before first bar
        plot.setTrailGapPercent(0.01d); // percentage of space after last bar
        plot.setCategoryGapsPercent(0.05d); // percentage of space between categories

        plot.setBackgroundPaint(Color.lightGray);
        plot.setOutlinePaint(Color.white);
        plot.setSeriesPaint(dataColors);

        chart = new JFreeChart("Statistical Demo", titleFont, plot, false);
        chart.setBackgroundPaint(Color.white);
    }

    /**
     * Returns the chart.
     *
     * @return the chart.
     */
    public JFreeChart getChart() {
        return chart;
    }

    /**
     * For testing from the command line.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        StatisticalBarChartDemo sample = new StatisticalBarChartDemo();
        JFreeChart chart = sample.getChart();
        ChartFrame frame = new ChartFrame("Statistical Bar Chart Demo", chart);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);

    }
}
