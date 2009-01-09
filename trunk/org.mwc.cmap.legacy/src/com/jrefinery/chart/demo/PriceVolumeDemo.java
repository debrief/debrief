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
 * PriceVolumeDemo.java
 * --------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited).
 * Contributor(s):   -;
 *
 * $Id: PriceVolumeDemo.java,v 1.1.1.1 2003/07/17 10:06:35 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 28-Mar-2002 : Version 1 (DG);
 * 23-Apr-2002 : Modified to use new CombinedXYPlot class (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import java.awt.Color;
import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.data.Day;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.IntervalXYDataset;
import com.jrefinery.date.SerialDate;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.XYItemRenderer;
import com.jrefinery.chart.StandardXYItemRenderer;
import com.jrefinery.chart.VerticalXYBarRenderer;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.NumberAxis;
import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.CombinedXYPlot;
import com.jrefinery.chart.tooltips.TimeSeriesToolTipGenerator;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A demonstration application showing a time series chart overlaid with a vertical XY bar chart.
 *
 * @author DG
 */
public class PriceVolumeDemo extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public PriceVolumeDemo(String title) {

        super(title);
        JFreeChart chart = createCombinedChart();
        ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(panel);

    }

    /**
     * Creates a combined chart.
     *
     * @return a combined chart.
     */
    private JFreeChart createCombinedChart() {

        // create subplot 1...
        XYDataset priceData = this.createPriceDataset();
        XYItemRenderer renderer1 = new StandardXYItemRenderer();
        renderer1.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
        NumberAxis axis = new VerticalNumberAxis("Price");
        axis.setAutoRangeIncludesZero(false);
        XYPlot subplot1 = new XYPlot(priceData, null, axis, renderer1);

        // create subplot 2...
        IntervalXYDataset volumeData = this.createVolumeDataset();
        XYItemRenderer renderer2 = new VerticalXYBarRenderer(0.20);
        renderer2.setToolTipGenerator(new TimeSeriesToolTipGenerator("d-MMM-yyyy", "0.00"));
        XYPlot subplot2 = new XYPlot(volumeData, null,
                                     new VerticalNumberAxis("Volume"), renderer2);
        subplot2.setSeriesPaint(0, Color.blue);

        // make a combined plot...
        CombinedXYPlot plot = new CombinedXYPlot(new HorizontalDateAxis("Date"),
                                                 CombinedXYPlot.VERTICAL);
        plot.add(subplot1, 3);  // a weight of 3 (75%)
        plot.add(subplot2, 1);  // a weight of 1 (25%)

        // return a new chart containing the overlaid plot...
        return new JFreeChart("Price / Volume Example",
                              JFreeChart.DEFAULT_TITLE_FONT,
                              plot,
                              true);

    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private XYDataset createPriceDataset() {

        // create dataset 1...
        BasicTimeSeries series1 = new BasicTimeSeries("Price", Day.class);
        series1.add(new Day(1, SerialDate.MARCH, 2002), 12353.3);
        series1.add(new Day(4, SerialDate.MARCH, 2002), 13984.3);
        series1.add(new Day(5, SerialDate.MARCH, 2002), 12999.4);
        series1.add(new Day(6, SerialDate.MARCH, 2002), 14274.3);
        series1.add(new Day(7, SerialDate.MARCH, 2002), 15943.5);
        series1.add(new Day(8, SerialDate.MARCH, 2002), 14845.3);
        series1.add(new Day(11, SerialDate.MARCH, 2002), 17232.3);
        series1.add(new Day(12, SerialDate.MARCH, 2002), 14232.2);
        series1.add(new Day(13, SerialDate.MARCH, 2002), 13102.2);
        series1.add(new Day(14, SerialDate.MARCH, 2002), 14230.2);
        series1.add(new Day(15, SerialDate.MARCH, 2002), 11435.2);
        series1.add(new Day(18, SerialDate.MARCH, 2002), 14525.3);
        series1.add(new Day(19, SerialDate.MARCH, 2002), 13984.3);
        series1.add(new Day(20, SerialDate.MARCH, 2002), 12999.4);
        series1.add(new Day(21, SerialDate.MARCH, 2002), 14274.3);
        series1.add(new Day(22, SerialDate.MARCH, 2002), 15943.5);
        series1.add(new Day(25, SerialDate.MARCH, 2002), 16234.6);
        series1.add(new Day(26, SerialDate.MARCH, 2002), 17232.3);
        series1.add(new Day(27, SerialDate.MARCH, 2002), 14232.2);
        series1.add(new Day(28, SerialDate.MARCH, 2002), 13102.2);

        return new TimeSeriesCollection(series1);

    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private IntervalXYDataset createVolumeDataset() {

        // create dataset 2...
        BasicTimeSeries series2 = new BasicTimeSeries("Volume", Day.class);

        series2.add(new Day(1, SerialDate.MARCH, 2002), 500);
        series2.add(new Day(4, SerialDate.MARCH, 2002), 100);
        series2.add(new Day(5, SerialDate.MARCH, 2002), 350);
        series2.add(new Day(6, SerialDate.MARCH, 2002), 975);
        series2.add(new Day(7, SerialDate.MARCH, 2002), 675);
        series2.add(new Day(8, SerialDate.MARCH, 2002), 525);
        series2.add(new Day(11, SerialDate.MARCH, 2002), 675);
        series2.add(new Day(12, SerialDate.MARCH, 2002), 700);
        series2.add(new Day(13, SerialDate.MARCH, 2002), 250);
        series2.add(new Day(14, SerialDate.MARCH, 2002), 225);
        series2.add(new Day(15, SerialDate.MARCH, 2002), 425);
        series2.add(new Day(18, SerialDate.MARCH, 2002), 600);
        series2.add(new Day(19, SerialDate.MARCH, 2002), 300);
        series2.add(new Day(20, SerialDate.MARCH, 2002), 325);
        series2.add(new Day(21, SerialDate.MARCH, 2002), 925);
        series2.add(new Day(22, SerialDate.MARCH, 2002), 525);
        series2.add(new Day(25, SerialDate.MARCH, 2002), 775);
        series2.add(new Day(26, SerialDate.MARCH, 2002), 725);
        series2.add(new Day(27, SerialDate.MARCH, 2002), 125);
        series2.add(new Day(28, SerialDate.MARCH, 2002), 150);

        return new TimeSeriesCollection(series2);

    }

    /**
     * Starting point for the price/volume chart demo application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        PriceVolumeDemo demo = new PriceVolumeDemo("Price Volume Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
