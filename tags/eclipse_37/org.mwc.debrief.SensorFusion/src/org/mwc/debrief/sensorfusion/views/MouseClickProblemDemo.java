package org.mwc.debrief.sensorfusion.views;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MouseClickProblemDemo extends ApplicationFrame
{

	/**
	 * 
	 */	
	private static final long serialVersionUID = 1L;

	/**
	 * @param title
	 *          the frame title.
	 */
	public MouseClickProblemDemo(String title)
	{
		super(title);

		TimeSeries s1 = new TimeSeries("Series to click");
		s1.add(new Month(2, 2001), 181.8);
		s1.add(new Month(3, 2001), 167.3);
		s1.add(new Month(4, 2001), 153.8);
		s1.add(new Month(5, 2001), 167.6);
		s1.add(new Month(6, 2001), 158.8);
		s1.add(new Month(7, 2001), 148.3);
		s1.add(new Month(8, 2001), 153.9);
		s1.add(new Month(9, 2001), 142.7);
		s1.add(new Month(10, 2001), 123.2);

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(s1);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"[Alt]-click to switch orientation", // title
				"Time axis", // x-axis label
				"Value axis", // y-axis label
				dataset, // data
				false, // create legend?
				false, // generate tooltips?
				false // generate URLs?
				);
		
		ChartPanel chartPanel = new ChartPanel(chart);

		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		chartPanel.addChartMouseListener(new ChartMouseListener()
		{
			public void chartMouseMoved(ChartMouseEvent arg0)
			{
			}

			public void chartMouseClicked(ChartMouseEvent arg0)
			{
				System.out.println("clicked on:" + arg0.getEntity());
				
				if(arg0.getTrigger().isAltDown())
				{
					if(chart.getXYPlot().getOrientation() == PlotOrientation.HORIZONTAL)
						chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
					else
						chart.getXYPlot().setOrientation(PlotOrientation.HORIZONTAL);
				}
			}
		});
		setContentPane(chartPanel);
	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *          ignored.
	 */
	public static void main(String[] args)
	{
		MouseClickProblemDemo demo = new MouseClickProblemDemo("Time Series Demo 1");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

}
