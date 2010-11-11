/* --------------------
 * TimeSeriesDemo1.java
 * --------------------
 * (C) Copyright 2002-2009, by Object Refinery Limited.
 *
 */

package org.mwc.debrief.sensorfusion.views;

import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

/**
 * An example of a time series chart.  For the most part, default settings are
 * used, except that the renderer is modified to show filled shapes (as well as
 * lines) at each data point.
 * <p>
 * IMPORTANT NOTE:  THIS DEMO IS DOCUMENTED IN THE JFREECHART DEVELOPER GUIDE.
 * DO NOT MAKE CHANGES WITHOUT UPDATING THE GUIDE ALSO!!
 */
public class TimeSeriesDemo1 extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * A demonstration application showing how to create a simple time series
     * chart.  This example uses monthly data.
     *
     * @param title  the frame title.
     */
    public TimeSeriesDemo1(String title) {
        super(title);
				System.out.println("prepped");
        ChartPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        
        chartPanel.addChartMouseListener(new ChartMouseListener()
				{
					public void chartMouseMoved(ChartMouseEvent arg0)
					{
					}
					public void chartMouseClicked(ChartMouseEvent arg0)
					{
						System.out.println("clicked");
					}
				});
        setContentPane(chartPanel);
        
     
    }

    /**
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return A chart.
     */
    private static JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Legal & General Unit Trust Prices",  // title
            "Date",             // x-axis label
            "Price Per Unit",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );


        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(false);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(false);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        
        return chart;

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return the dataset.
     */
    private static XYDataset createDataset() {

        TimeSeries s1 = new TimeSeries("L&G European Index Trust");
        s1.add(new Month(2, 2001), 181.8);
        s1.add(new Month(3, 2001), 167.3);
        s1.add(new Month(4, 2001), 153.8);
        s1.add(new Month(5, 2001), 167.6);
        s1.add(new Month(6, 2001), 158.8);
        s1.add(new Month(7, 2001), 148.3);
        s1.add(new Month(8, 2001), 153.9);
        s1.add(new Month(9, 2001), 142.7);
        s1.add(new Month(10, 2001), 123.2);
        s1.add(new Month(11, 2001), 131.8);
        s1.add(new Month(12, 2001), 139.6);
        s1.add(new Month(1, 2002), 142.9);
        s1.add(new Month(2, 2002), 138.7);
        s1.add(new Month(3, 2002), 137.3);
        s1.add(new Month(4, 2002), 143.9);
        s1.add(new Month(5, 2002), 139.8);
        s1.add(new Month(6, 2002), 137.0);
        s1.add(new Month(7, 2002), 132.8);
        s1.add(new Month(8, 2002), 110.3);
        s1.add(new Month(9, 2002), 110.5);
        s1.add(new Month(10, 2002), 94.11);
        s1.add(new Month(11, 2002), 102.5);
        s1.add(new Month(12, 2002), 112.3);
        s1.add(new Month(1, 2003), 104.0);
        s1.add(new Month(2, 2003), 98.53);
        s1.add(new Month(3, 2003), 97.15);
        s1.add(new Month(4, 2003), 94.90);
        s1.add(new Month(5, 2003), 107.8);
        s1.add(new Month(6, 2003), 113.7);
        s1.add(new Month(7, 2003), 112.5);
        s1.add(new Month(8, 2003), 118.6);
        s1.add(new Month(9, 2003), 123.8);
        s1.add(new Month(10, 2003), 117.2);
        s1.add(new Month(11, 2003), 123.0);
        s1.add(new Month(12, 2003), 127.0);
        s1.add(new Month(1, 2004), 132.7);
        s1.add(new Month(2, 2004), 132.4);
        s1.add(new Month(3, 2004), 131.7);
        s1.add(new Month(4, 2004), 128.0);
        s1.add(new Month(5, 2004), 131.8);
        s1.add(new Month(6, 2004), 127.4);
        s1.add(new Month(7, 2004), 133.5);
        s1.add(new Month(8, 2004), 126.0);
        s1.add(new Month(9, 2004), 129.5);
        s1.add(new Month(10, 2004), 135.3);
        s1.add(new Month(11, 2004), 138.0);
        s1.add(new Month(12, 2004), 141.3);
        s1.add(new Month(1, 2005), 148.8);
        s1.add(new Month(2, 2005), 147.1);
        s1.add(new Month(3, 2005), 150.7);
        s1.add(new Month(4, 2005), 150.0);
        s1.add(new Month(5, 2005), 145.7);
        s1.add(new Month(6, 2005), 152.0);
        s1.add(new Month(7, 2005), 157.2);
        s1.add(new Month(8, 2005), 167.0);
        s1.add(new Month(9, 2005), 165.0);
        s1.add(new Month(10, 2005), 171.6);
        s1.add(new Month(11, 2005), 166.2);
        s1.add(new Month(12, 2005), 174.3);
        s1.add(new Month(1, 2006), 183.8);
        s1.add(new Month(2, 2006), 187.0);
        s1.add(new Month(3, 2006), 191.3);
        s1.add(new Month(4, 2006), 202.5);
        s1.add(new Month(5, 2006), 200.6);
        s1.add(new Month(6, 2006), 187.3);
        s1.add(new Month(7, 2006), 192.2);
        s1.add(new Month(8, 2006), 190.8);
        s1.add(new Month(9, 2006), 194.7);
        s1.add(new Month(10, 2006), 201.3);
        s1.add(new Month(11, 2006), 205.1);
        s1.add(new Month(12, 2006), 206.7);
        s1.add(new Month(1, 2007), 216.8);
        s1.add(new Month(2, 2007), 218.0);
        s1.add(new Month(3, 2007), 215.4);
        s1.add(new Month(4, 2007), 223.0);
        s1.add(new Month(5, 2007), 235.1);
        s1.add(new Month(6, 2007), 242.0);
        s1.add(new Month(7, 2007), 237.8);

        TimeSeries s2 = new TimeSeries("L&G UK Index Trust");
        s2.add(new Month(2, 2001), 129.6);
        s2.add(new Month(3, 2001), 123.2);
        s2.add(new Month(4, 2001), 117.2);
        s2.add(new Month(5, 2001), 124.1);
        s2.add(new Month(6, 2001), 122.6);
        s2.add(new Month(7, 2001), 119.2);
        s2.add(new Month(8, 2001), 116.5);
        s2.add(new Month(9, 2001), 112.7);
        s2.add(new Month(10, 2001), 101.5);
        s2.add(new Month(11, 2001), 106.1);
        s2.add(new Month(12, 2001), 110.3);
        s2.add(new Month(1, 2002), 111.7);
        s2.add(new Month(2, 2002), 111.0);
        s2.add(new Month(3, 2002), 109.6);
        s2.add(new Month(4, 2002), 113.2);
        s2.add(new Month(5, 2002), 111.6);
        s2.add(new Month(6, 2002), 108.8);
        s2.add(new Month(7, 2002), 101.6);
        s2.add(new Month(8, 2002), 90.95);
        s2.add(new Month(9, 2002), 91.02);
        s2.add(new Month(10, 2002), 82.37);
        s2.add(new Month(11, 2002), 86.32);
        s2.add(new Month(12, 2002), 91.00);
        s2.add(new Month(1, 2003), 86.00);
        s2.add(new Month(2, 2003), 80.04);
        s2.add(new Month(3, 2003), 80.40);
        s2.add(new Month(4, 2003), 80.28);
        s2.add(new Month(5, 2003), 86.42);
        s2.add(new Month(6, 2003), 91.40);
        s2.add(new Month(7, 2003), 90.52);
        s2.add(new Month(8, 2003), 93.11);
        s2.add(new Month(9, 2003), 96.80);
        s2.add(new Month(10, 2003), 94.78);
        s2.add(new Month(11, 2003), 99.56);
        s2.add(new Month(12, 2003), 100.8);
        s2.add(new Month(1, 2004), 103.4);
        s2.add(new Month(2, 2004), 102.1);
        s2.add(new Month(3, 2004), 105.3);
        s2.add(new Month(4, 2004), 103.7);
        s2.add(new Month(5, 2004), 105.2);
        s2.add(new Month(6, 2004), 103.7);
        s2.add(new Month(7, 2004), 105.7);
        s2.add(new Month(8, 2004), 103.6);
        s2.add(new Month(9, 2004), 106.1);
        s2.add(new Month(10, 2004), 109.3);
        s2.add(new Month(11, 2004), 110.3);
        s2.add(new Month(12, 2004), 112.6);
        s2.add(new Month(1, 2005), 116.0);
        s2.add(new Month(2, 2005), 117.3);
        s2.add(new Month(3, 2005), 120.1);
        s2.add(new Month(4, 2005), 119.3);
        s2.add(new Month(5, 2005), 116.2);
        s2.add(new Month(6, 2005), 120.8);
        s2.add(new Month(7, 2005), 125.2);
        s2.add(new Month(8, 2005), 127.7);
        s2.add(new Month(9, 2005), 130.8);
        s2.add(new Month(10, 2005), 131.0);
        s2.add(new Month(11, 2005), 135.3);
        s2.add(new Month(12, 2005), 141.2);
        s2.add(new Month(1, 2006), 144.7);
        s2.add(new Month(2, 2006), 146.4);
        s2.add(new Month(3, 2006), 151.9);
        s2.add(new Month(4, 2006), 153.5);
        s2.add(new Month(5, 2006), 144.5);
        s2.add(new Month(6, 2006), 150.1);
        s2.add(new Month(7, 2006), 148.7);
        s2.add(new Month(8, 2006), 150.1);
        s2.add(new Month(9, 2006), 151.6);
        s2.add(new Month(10, 2006), 153.4);
        s2.add(new Month(11, 2006), 158.3);
        s2.add(new Month(12, 2006), 157.6);
        s2.add(new Month(1, 2007), 163.9);
        s2.add(new Month(2, 2007), 163.8);
        s2.add(new Month(3, 2007), 162.0);
        s2.add(new Month(4, 2007), 167.1);
        s2.add(new Month(5, 2007), 170.0);
        s2.add(new Month(6, 2007), 175.7);
        s2.add(new Month(7, 2007), 171.9);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);

        return dataset;

    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static ChartPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        TimeSeriesDemo1 demo = new TimeSeriesDemo1("Time Series Demo 1");
        demo.pack();
    //    RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
