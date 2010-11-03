package org.mwc.debrief.sensorfusion.views;

import java.awt.Color;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class DataSupport
{

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *          a dataset.
	 * 
	 * @return A chart.
	 */
	public static JFreeChart createChart(XYDataset dataset)
	{

		JFreeChart chart = ChartFactory.createTimeSeriesChart("Bearing Fusion", // title
				"Time", // x-axis label
				"Bearing", // y-axis label
				dataset, // data
				false, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairVisible(false);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer)
		{
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm.ss"));

		return chart;

	}

	public static class TacticalSeries extends TimeSeries
	{
		protected Object _subject;
		private boolean _amVisible;

		public TacticalSeries(String name, Object subject)
		{
			super(name);
			_subject = subject;
			if(Math.random() > 0.5)
				_amVisible = true;
			else
				_amVisible = false;
		}
		
		public boolean getVisible()
		{
			return _amVisible;
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	public static class TrackSeries extends TacticalSeries
	{

		public TrackSeries(String name, String subject)
		{
			super(name, subject);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	public static class SensorSeries extends TacticalSeries
	{

		public SensorSeries(String name, String subject)
		{
			super(name, subject);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	
	private static long stepInterval()
	{
		long[] intervals = {1000, 5000, 60000, 300000};
		int index = (int)(Math.random() * 4);
		return intervals[index];
	}
	
	private static long delay()
	{
		long delay = (int)(Math.random() * 210) * 60000 + (int)(Math.random() * 5 * 1000);
		return delay;
	}

	private static long duration()
	{
		long delay = (int)(Math.random() * 90) * 60000 + (int)(Math.random() * 500 * 1000);
		return delay;
	}
	/**
	 * Creates a dataset, consisting of two series of monthly data.
	 * 
	 * @return The dataset.
	 */
	public static TimeSeriesCollection createDataset()
	{
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		final long _start = (long)( Math.random() * 1000000);
		final long _end  =  (long)( _start + 12000000 + Math.random() * 100000);
		
		int ctr = 0;

		int MAX_TRACKS = 4;
		for (int i = 0; i < MAX_TRACKS; i++)
		{
			final long _step = 60000;
			final long _thisStart = _start;
			final long _thisEnd = _end;
			long _this = _thisStart;
			
			TimeSeries s1 = new TrackSeries("track:" + i, "track");
			double theVal = Math.random() * 360;
			while(_this < _thisEnd)
			{
				theVal = theVal - 1 + (Math.random() * 2);
				s1.add(new FixedMillisecond (_this), theVal);
				ctr++;
				_this += _step;
			}
			dataset.addSeries(s1);
		}

		int MAX_SENSORS = 450;
		for (int i = 0; i < MAX_SENSORS; i++)
		{
			final long _step = stepInterval();
			final long _thisStart = _start + delay();
			 long _thisEnd = _thisStart + duration();
			_thisEnd = Math.min(_thisEnd, _end);
			long _this = _thisStart;
			
			TimeSeries s1 = new SensorSeries("sensor:" + i, "sensor");
			double theVal = Math.random() * 360;
			while(_this < _thisEnd)
			{
				theVal = theVal - 1 + (Math.random() * 2);
				s1.add(new FixedMillisecond (_this), theVal);
				ctr++;
				_this += _step;
			}
			dataset.addSeries(s1);
		}

		System.out.println(ctr + " points created");

		return dataset;
	}

}
