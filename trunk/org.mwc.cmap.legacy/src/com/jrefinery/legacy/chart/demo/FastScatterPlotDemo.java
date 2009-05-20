package com.jrefinery.legacy.chart.demo;

import com.jrefinery.legacy.chart.ChartPanel;
import com.jrefinery.legacy.chart.FastScatterPlot;
import com.jrefinery.legacy.chart.HorizontalNumberAxis;
import com.jrefinery.legacy.chart.JFreeChart;
import com.jrefinery.legacy.chart.ValueAxis;
import com.jrefinery.legacy.chart.VerticalNumberAxis;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.ui.RefineryUtilities;

public class FastScatterPlotDemo extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		private static final int COUNT = 1000000;

    private float[][] data = new float[2][COUNT];

    public FastScatterPlotDemo(String title) {
        super(title);
        populateData();

        ValueAxis domainAxis = new HorizontalNumberAxis("X");
        domainAxis.setRange(0, (double) COUNT);
        ValueAxis rangeAxis = new VerticalNumberAxis("Y");
        rangeAxis.setRange(0, 100 + 3 * (double) COUNT);
        FastScatterPlot plot = new FastScatterPlot(data, domainAxis, rangeAxis);
        JFreeChart chart = new JFreeChart("Fast Scatter Plot", JFreeChart.DEFAULT_TITLE_FONT,
                                          plot, false);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(panel);
    }

    private void populateData() {

        for (int i = 0; i < data[0].length; i++) {

            float x = (float) i;
            data[0][i] = x;
            data[1][i] = 100 + (2 * x) + (float) Math.random() * COUNT;
        }

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        FastScatterPlotDemo demo = new FastScatterPlotDemo("Fast Scatter Plot Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
