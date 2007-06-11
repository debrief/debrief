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
 * IntervalBarChartDemo.java
 * -------------------------
 * (C) Copyright 2002, by Jeremy Bowman and Contributors.
 *
 * Original Author:  Jeremy Bowman.
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: IntervalBarChartDemo.java,v 1.1.1.1 2003/07/17 10:06:33 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 29-Apr-2002 : Version 1, contributed by Jeremy Bowman.  Name changed to
 *               IntervalBarChartDemo, and the chart is displayed in a frame rather than
 *               saved to a file (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 10-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Locale;
import com.jrefinery.chart.ChartFrame;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.NumberTickUnit;
import com.jrefinery.chart.VerticalCategoryPlot;
import com.jrefinery.chart.VerticalIntervalBarRenderer;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.tooltips.IntervalCategoryToolTipGenerator;
import com.jrefinery.data.DefaultIntervalCategoryDataset;

/**
 * An interval bar chart.
 *
 * @author JB
 */
public class IntervalBarChartDemo {

    /** The categories. */
    private static final String[] CATEGORIES = { "1", "3", "5", "10", "20" };

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
     * Creates a new demo.
     */
    public IntervalBarChartDemo() {

        DefaultIntervalCategoryDataset data = null;
        double[][] lows = { { -.0315, .0159, .0306, .0453, .0557 } };
        double[][] highs = { { .1931, .1457, .1310, .1163, .1059 } };
        data = new DefaultIntervalCategoryDataset(lows, highs);
        data.setCategories(CATEGORIES);

        String title = "Strategie Sicherheit";
        String xTitle = "Zeitraum (in Jahren)";
        String yTitle = "Performance";
        HorizontalCategoryAxis xAxis = new HorizontalCategoryAxis(xTitle);
        xAxis.setLabelFont(titleFont);
        xAxis.setTickLabelFont(labelFont);
        xAxis.setTickMarksVisible(false);
        VerticalNumberAxis yAxis = new VerticalNumberAxis(yTitle);
        yAxis.setLabelFont(titleFont);
        yAxis.setTickLabelFont(labelFont);
        yAxis.setMinimumAxisValue(-0.2);
        yAxis.setMaximumAxisValue(0.40);
        DecimalFormat formatter = new DecimalFormat("0.##%");
        yAxis.setTickUnit(new NumberTickUnit(0.05, formatter));

        VerticalIntervalBarRenderer renderer = null;
        renderer = new VerticalIntervalBarRenderer(new IntervalCategoryToolTipGenerator(), null);
        VerticalCategoryPlot plot = null;
        plot = new VerticalCategoryPlot(data, xAxis, yAxis, renderer);
        plot.setLabelFont(labelFont);
        plot.setLabelFormatString("0.##%");
        // Uncomment the following line for vertical plot labels
        //plot.setVerticalLabels(true);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setOutlinePaint(Color.white);
        plot.setSeriesPaint(dataColors);
        chart = new JFreeChart(title, titleFont, plot, false);
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
     * Starting point for the demo.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.GERMANY);
        IntervalBarChartDemo sample = new IntervalBarChartDemo();
        JFreeChart chart = sample.getChart();
        ChartFrame frame = new ChartFrame("Interval Bar Chart Demo", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
