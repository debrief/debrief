package org.mwc.debrief.sensorfusion.views;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldVector;

public class DataSupport
{

	private static double _previousVal;

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

		JFreeChart chart = ChartFactory.createTimeSeriesChart("Bearing Management", // title
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
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairVisible(false);
		
		plot.setOrientation(PlotOrientation.HORIZONTAL);

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

	abstract public static class TacticalSeries extends TimeSeries
	{
		public TacticalSeries(String name)
		{
			super(name);
		}

		abstract public boolean getVisible();

		abstract public Color getColor();

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	public static class TrackSeries extends TacticalSeries
	{

		private WatchableList _myTrack;

		public TrackSeries(String name, WatchableList subject)
		{
			super(name);
			_myTrack = subject;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean getVisible()
		{
			if (_myTrack != null)
				return _myTrack.getVisible();
			else
				return true;
		}

		@Override
		public Color getColor()
		{
			return _myTrack.getColor();
		}

	}

	public static class SensorSeries extends TacticalSeries
	{
		final protected SensorWrapper _subject;

		public SensorSeries(String name, SensorWrapper subject)
		{
			super(name);
			_subject = subject;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean getVisible()
		{
			if (_subject != null)
				return _subject.getVisible();
			else
				return true;
		}

		@Override
		public Color getColor()
		{
			return _subject.getColor();
		}

		public SensorWrapper getSensor()
		{
			return _subject;
		}

	}

	public static long stepInterval()
	{
		long[] intervals =
		{ 1000, 5000, 60000, 300000 };
		int index = (int) (Math.random() * 4);
		return intervals[index];
	}

	public static long delay()
	{
		long delay = (int) (Math.random() * 210) * 60000
				+ (int) (Math.random() * 5 * 1000);
		return delay;
	}

	public static long duration()
	{
		long delay = (int) (Math.random() * 90) * 60000
				+ (int) (Math.random() * 500 * 1000);
		return delay;
	}

	//
	// /**
	// * Creates a dataset, consisting of two series of monthly data.
	// *
	// * @return The dataset.
	// */
	// public static TimeSeriesCollection createDataset()
	// {
	// TimeSeriesCollection dataset = new TimeSeriesCollection();
	//
	// final long _start = (long) (Math.random() * 1000000);
	// final long _end = (long) (_start + 12000000 + Math.random() * 100000);
	//
	// int ctr = 0;
	//
	// int MAX_TRACKS = 1;
	// for (int i = 0; i < MAX_TRACKS; i++)
	// {
	// final long _step = 60000;
	// final long _thisStart = _start;
	// final long _thisEnd = _end;
	// long _this = _thisStart;
	//
	// TimeSeries s1 = new TrackSeries("track:" + i, null);
	// double theVal = Math.random() * 360;
	// while (_this < _thisEnd)
	// {
	// theVal = theVal - 1 + (Math.random() * 2);
	// s1.add(new FixedMillisecond(_this), theVal);
	// ctr++;
	// _this += _step;
	// }
	// dataset.addSeries(s1);
	// }
	//
	// int MAX_SENSORS = 5;
	// for (int i = 0; i < MAX_SENSORS; i++)
	// {
	// final long _step = stepInterval();
	// final long _thisStart = _start + delay();
	// long _thisEnd = _thisStart + duration();
	// _thisEnd = Math.min(_thisEnd, _end);
	// long _this = _thisStart;
	//
	// TimeSeries s1 = new SensorSeries("sensor:" + i, null);
	// double theVal = Math.random() * 360;
	// while (_this < _thisEnd)
	// {
	// theVal = theVal - 1 + (Math.random() * 2);
	// s1.add(new FixedMillisecond(_this), theVal);
	// ctr++;
	// _this += _step;
	// }
	// dataset.addSeries(s1);
	// }
	//
	// System.out.println(ctr + " points created");
	//
	// return dataset;
	// }

	public static void tracksFor(TrackWrapper primary,
			WatchableList[] secondaries, TimeSeriesCollection newData)
	{
		if (secondaries != null)
		{
			for (int i = 0; i < secondaries.length; i++)
			{
				WatchableList thisS = secondaries[i];
				TrackSeries thisT = new TrackSeries(thisS.getName(), thisS);
				Enumeration<Editable> priPts = primary.getPositions();
				_previousVal = Double.NaN;
				while (priPts.hasMoreElements())
				{
					FixWrapper thisP = (FixWrapper) priPts.nextElement();
					Watchable[] nearest = thisS.getNearestTo(thisP.getDTG());
					if (nearest != null)
						if (nearest.length > 0)
						{
							Watchable thisLoc = nearest[0];
							WorldVector offset = thisLoc.getLocation().subtract(
									thisP.getLocation());
							double thisVal = MWC.Algorithms.Conversions.Rads2Degs(offset
									.getBearing());

							thisT.add(create(thisP.getTime(), thisVal, thisP.getColor()));
						}
				}
				if (!thisT.isEmpty())
					newData.addSeries(thisT);
			}
		}

	}

	public static void sensorDataFor(TrackWrapper primary,
			TimeSeriesCollection newData, HashMap<SensorWrapper, SensorSeries> index)
	{

		index.clear();

		Enumeration<Editable> sensors = primary.getSensors().elements();
		while (sensors.hasMoreElements())
		{
			SensorWrapper sensor = (SensorWrapper) sensors.nextElement();
			SensorSeries series = new SensorSeries(sensor.getName(), sensor);
			Enumeration<Editable> cuts = sensor.elements();
			_previousVal = Double.NaN;

			index.put(sensor, series);
			while (cuts.hasMoreElements())
			{
				SensorContactWrapper scw = (SensorContactWrapper) cuts.nextElement();
				double thisVal = scw.getBearing();

				// wrap this in a try/catch - in case there are multiple entries
				try
				{
					series.add(create(scw.getTime(), thisVal, scw.getColor()));
				}
				catch (Exception e)
				{
					// no probs, just ignore the new value
				}
			}

			if (!series.isEmpty())
				newData.addSeries(series);
		}
	}

	private static ColouredDataItem create(HiResDate hiResDate, double thisVal,
			Color color)
	{

		if (thisVal < 0)
			thisVal += 360;

		// aaah, but is it a jump?
		boolean connectToPrevious = true;
		if (_previousVal != Double.NaN)
		{
			double delta = Math.abs(thisVal - _previousVal);
			if (delta > 100)
			{
				connectToPrevious = false;
			}
		}
		_previousVal = thisVal;
		ColouredDataItem cd = new ColouredDataItem(new FixedMillisecond(hiResDate
				.getDate().getTime()), thisVal, color, connectToPrevious, null);

		return cd;
	}

}
