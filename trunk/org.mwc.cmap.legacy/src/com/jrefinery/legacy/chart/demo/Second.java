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
 * -----------
 * Second.java
 * -----------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: Second.java,v 1.1.1.1 2003/07/17 10:06:36 Ian.Mayo Exp $
 *
 * Changes (since 24-Apr-2002)
 * ---------------------------
 * 24-Apr-2002 : Added standard header (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart.demo;

import com.jrefinery.legacy.chart.ChartFactory;
import com.jrefinery.legacy.chart.ChartFrame;
import com.jrefinery.legacy.chart.JFreeChart;
import com.jrefinery.legacy.chart.StandardXYItemRenderer;
import com.jrefinery.legacy.chart.ValueAxis;
import com.jrefinery.legacy.chart.XYItemRenderer;
import com.jrefinery.legacy.chart.XYPlot;
import com.jrefinery.legacy.data.XYSeries;
import com.jrefinery.legacy.data.XYSeriesCollection;

/**
 * A simple demo.
 *
 * @author DG
 */
public class Second {

    /**
     * Starting point for the demo.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        // create some data...
        XYSeries series1 = new XYSeries("Advisory Range");
        series1.add(new Integer(1200), new Integer(1));
        series1.add(new Integer(1500), new Integer(1));

        XYSeries series2 = new XYSeries("Normal Range");
        series2.add(new Integer(2000), new Integer(4));
        series2.add(new Integer(2300), new Integer(4));

        XYSeries series3 = new XYSeries("Recommended");
        series3.add(new Integer(2100), new Integer(2));

        XYSeries series4 = new XYSeries("Current");
        series4.add(new Integer(2400), new Integer(3));

        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(series1);
        data.addSeries(series2);
        data.addSeries(series3);
        data.addSeries(series4);

        // create a chart...
        JFreeChart chart = ChartFactory.createLineXYChart("My Chart", "Calories", "Y", data, true);

        XYItemRenderer renderer
            = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES, null);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRenderer(renderer);
        ValueAxis axis = plot.getRangeAxis();
        axis.setTickLabelsVisible(false);
        axis.setRange(0.0, 5.0);

        // create and display a frame...
        ChartFrame frame = new ChartFrame("Test", chart);
        frame.pack();
        frame.setVisible(true);

    }

}
