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
 * --------------------
 * XYStepChartDemo.java
 * --------------------
 * (C) Copyright 2002, by Roger Studner and Contributors.
 *
 * Original Author:  Roger Studner;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: XYStepChartDemo.java,v 1.1.1.1 2003/07/17 10:06:37 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1, contributed by Roger Studner (DG);
 * 11-Oct-2002 : Moved create method to ChartFactory class, and fixed issues reported by
 *               Checkstyle (DG);
 *
 */
package com.jrefinery.chart.demo;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartFrame;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.data.XYDataset;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demonstration of the XYStepRenderer class.
 *
 * @author RS
 */
public class XYStepChartDemo {

    /** A frame for displaying the chart. */
    private ChartFrame frame = null;

    /**
     * Displays a sample chart in its own frame.
     */
    private void displayChart() {

        if (frame == null) {

            // create a default chart based on some sample data...
            String title = "LCACs in use at given time";
            String xAxisLabel = "Time";
            String yAxisLabel = "Number of Transports";

            XYDataset data = DemoDatasetFactory.createStepXYDataset();

            JFreeChart chart = ChartFactory.createXYStepChart(title,
                                                              xAxisLabel, yAxisLabel,
                                                              data,
                                                              true  // legend
                                                              );

            // then customise it a little...
            chart.setBackgroundPaint(new Color(216, 216, 216));
            XYPlot plot = chart.getXYPlot();
            Stroke[] s = new BasicStroke[] { new BasicStroke((float) 2.0),
                                             new BasicStroke((float) 2.0) };
            plot.setSeriesStroke(s);
            // and present it in a frame...
            frame = new ChartFrame("Plan Comparison", chart);
            frame.pack();
            RefineryUtilities.positionFrameRandomly(frame);
            frame.show();

        }
        else {
            frame.show();
            frame.requestFocus();
        }

    }

    /**
     * The starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        XYStepChartDemo demo = new XYStepChartDemo();
        demo.displayChart();

    }

}
