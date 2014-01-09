package org.mwc.debrief.sensorfusion.views;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

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
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

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
	public static JFreeChart createChart(final XYDataset dataset)
	{

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Bearing Management", // title
				"Time", // x-axis label
				"Bearing", // y-axis label
				dataset, // data
				false, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		final XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeCrosshairVisible(false);

		plot.setOrientation(PlotOrientation.HORIZONTAL);

		final XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer)
		{
			final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}

		final DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm.ss"));

		return chart;

	}

	abstract public static class TacticalSeries extends TimeSeries
	{
		public TacticalSeries(final String name)
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

		private final WatchableList _myTrack;

		public TrackSeries(final String name, final WatchableList subject)
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

		public SensorSeries(final String name, final SensorWrapper subject)
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
		final long[] intervals =
		{ 1000, 5000, 60000, 300000 };
		final int index = (int) (Math.random() * 4);
		return intervals[index];
	}

	public static long delay()
	{
		final long delay = (int) (Math.random() * 210) * 60000
				+ (int) (Math.random() * 5 * 1000);
		return delay;
	}

	public static long duration()
	{
		final long delay = (int) (Math.random() * 90) * 60000
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

	public static void tracksFor(final TrackWrapper primary,
			final WatchableList[] secondaries, final TimeSeriesCollection newData)
	{
		if (secondaries != null)
		{
			for (int i = 0; i < secondaries.length; i++)
			{
				final WatchableList thisS = secondaries[i];
				final TrackSeries thisT = new TrackSeries(thisS.getName(), thisS);
				final Enumeration<Editable> priPts = primary.getPositions();
				_previousVal = Double.NaN;
				while (priPts.hasMoreElements())
				{
					final FixWrapper thisP = (FixWrapper) priPts.nextElement();
					final Watchable[] nearest = thisS.getNearestTo(thisP.getDTG());
					if (nearest != null)
						if (nearest.length > 0)
						{
							final Watchable thisLoc = nearest[0];
							final WorldVector offset = thisLoc.getLocation().subtract(
									thisP.getLocation());
							final double thisVal = MWC.Algorithms.Conversions
									.Rads2Degs(offset.getBearing());

							thisT.add(create(thisP.getTime(), thisVal, thisP.getColor()));
						}
				}
				if (!thisT.isEmpty())
					newData.addSeries(thisT);
			}
		}

	}

	public static void sensorDataFor(final TrackWrapper primary,
			final TimeSeriesCollection newData,
			final HashMap<SensorWrapper, SensorSeries> index)
	{

		index.clear();

		final Enumeration<Editable> sensors = primary.getSensors().elements();
		while (sensors.hasMoreElements())
		{
			final SensorWrapper sensor = (SensorWrapper) sensors.nextElement();
			final SensorSeries series = new SensorSeries(sensor.getName(), sensor);
			final Enumeration<Editable> cuts = sensor.elements();
			_previousVal = Double.NaN;

			index.put(sensor, series);
			while (cuts.hasMoreElements())
			{
				final SensorContactWrapper scw = (SensorContactWrapper) cuts
						.nextElement();
				final double thisVal = scw.getBearing();

				// wrap this in a try/catch - in case there are multiple entries
				try
				{
					series.add(create(scw.getTime(), thisVal, scw.getColor()));
				}
				catch (final Exception e)
				{
					// no probs, just ignore the new value
				}
			}

			if (!series.isEmpty())
				newData.addSeries(series);
		}
	}

	/**
	 * Deletes any sensor data that is outside the start/finish period of the
	 * primary track.
	 * 
	 * @param primary
	 * @param index
	 */
	public static void trimToTrackPeriod(final TrackWrapper primary,
			final HashMap<SensorWrapper, SensorSeries> index)
	{
		final HiResDate startPeriod = primary.getStartDTG();
		final HiResDate finishPeriod = primary.getEndDTG();
		final Iterator<SensorWrapper> sensors = index.keySet().iterator();
		while (sensors.hasNext())
		{
			final SensorWrapper thisS = sensors.next();
			final HiResDate startDTG = thisS.getStartDTG();
			final HiResDate endDTG = thisS.getEndDTG();
			if (startPeriod.compareTo(startDTG) > 0)
			{
				thisS.setVisible(false);
				System.out.println(thisS.getName());
				continue;
			}
			if (finishPeriod.compareTo(endDTG) < 0)
			{
				thisS.setVisible(false);
			}
		}
	}

	/**
	 * Deletes all blocks of sensor data that are more than 45 degrees from an
	 * secondary track.
	 * 
	 * @return
	 */
	public static ArrayList<SensorWrapper> trimToSensorNearSubjectTracks(
			final TrackWrapper primary, final WatchableList[] secondaries)
	{
		ArrayList<SensorWrapper> toRemove = new ArrayList<SensorWrapper>();

		if (primary == null || secondaries == null)
			return toRemove;

		Enumeration<Editable> sensors = primary.getSensors().elements();
		while (sensors.hasMoreElements())
		{
			SensorWrapper sensor = (SensorWrapper) sensors.nextElement();
		//	if (sensor.getVisible())
			{
				final Enumeration<Editable> contacts = sensor.elements();
				// loop though the individual sensor contact objects
				while (sensor != null && (contacts.hasMoreElements()))
				{
					final SensorContactWrapper contact = (SensorContactWrapper) contacts
							.nextElement();
					// check this sensor contact has a bearing
					if (contact.getHasBearing())
					{
						final HiResDate contactTime = contact.getDTG();
						// loop through each secondary track
						for (int i = 0; i < secondaries.length; i++)
						{
							final WatchableList thisS = secondaries[i];

							final Watchable[] secondaryFixes = thisS
									.getNearestTo(contactTime);
							final Watchable[] primaryFixes = primary
									.getNearestTo(contactTime);
							double bearing = 0;
							if (secondaryFixes != null && secondaryFixes.length > 0)
							{
								if (primaryFixes != null && primaryFixes.length > 0)
								{
									WorldLocation wl1 = secondaryFixes[0].getLocation();
									WorldLocation wl2 = primaryFixes[0].getLocation();
									bearing = wl1.bearingFrom(wl2);
								}
							}

							double bearingDelta = contact.getBearing()
									- Math.toDegrees(bearing);
							if (Math.abs(bearingDelta) > 45)
							{
								// ok, remember we want to forget it
								toRemove.add(sensor);

								// now clear the sensor, as a marker to move on to the next
								// sensor
								sensor = null;
							}
						}
					}
				}
			} // end loop through sensor contacts
		}
		return toRemove;
	}

	private static ColouredDataItem create(final HiResDate hiResDate,
			final double thisVal, final Color color)
	{
		double val = thisVal;
		if (val < 0)
			val += 360;

		// aaah, but is it a jump?
		boolean connectToPrevious = true;
		if (_previousVal != Double.NaN)
		{
			final double delta = Math.abs(val - _previousVal);
			if (delta > 100)
			{
				connectToPrevious = false;
			}
		}
		_previousVal = val;
		final ColouredDataItem cd = new ColouredDataItem(new FixedMillisecond(
				hiResDate.getDate().getTime()), val, color, connectToPrevious, null);

		return cd;
	}

	static public final class DataSupportTest extends junit.framework.TestCase
	{
		private HashMap<SensorWrapper, SensorSeries> _trackIndex;
		private TimeSeriesCollection _timeData;
		private TrackWrapper _primary;
		private SensorWrapper _sw1;
		private SensorWrapper _sw2;

		public void setUp()
		{
			_trackIndex = new HashMap<SensorWrapper, SensorSeries>();
			_timeData = new TimeSeriesCollection();
			_primary = getDummyPrimary();
		}

		private TrackWrapper getDummyPrimary()
		{
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
					loc_1.add(new WorldVector(33, new WorldDistance(100,
							WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
					loc_1.add(new WorldVector(33, new WorldDistance(200,
							WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
					loc_1.add(new WorldVector(33, new WorldDistance(300,
							WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
					loc_1.add(new WorldVector(33, new WorldDistance(400,
							WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
					loc_1.add(new WorldVector(33, new WorldDistance(500,
							WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);
			// also give it some sensor data
			_sw1 = new SensorWrapper("title one");
			_sw1.setVisible(true);
			final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa",
					new HiResDate(150, 0), null, new Double(15), null, null, null, 0,
					null);
			final SensorContactWrapper scwa2 = new SensorContactWrapper("bbb",
					new HiResDate(180, 0), null, new Double(15), null, null, null, 0,
					null);
			final SensorContactWrapper scwa3 = new SensorContactWrapper("ccc",
					new HiResDate(250, 0), null, new Double(150), null, null, null, 0,
					null);
			_sw1.add(scwa1);
			_sw1.add(scwa2);
			_sw1.add(scwa3);
			tw.add(_sw1);
			_sw2 = new SensorWrapper("title two");
			_sw2.setVisible(true);
			final SensorContactWrapper scw1 = new SensorContactWrapper("ddd",
					new HiResDate(260, 0), null, null, null, null, null, 0, null);
			final SensorContactWrapper scw2 = new SensorContactWrapper("eee",
					new HiResDate(280, 0), null, null, null, null, null, 0, null);
			// this time is greater than the primary track finish period
			final SensorContactWrapper scw3 = new SensorContactWrapper("fff",
					new HiResDate(650, 0), null, null, null, null, null, 0, null);
			_sw2.add(scw1);
			_sw2.add(scw2);
			_sw2.add(scw3);
			tw.add(_sw2);

			return tw;
		}

		private TrackWrapper getDummySecondary()
		{
			final TrackWrapper tw = new TrackWrapper();

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
					loc_1.add(new WorldVector(33, new WorldDistance(100,
							WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
					loc_1.add(new WorldVector(33, new WorldDistance(200,
							WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
					loc_1.add(new WorldVector(33 + 200, new WorldDistance(300,
							WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");

			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);

			return tw;
		}

		private TrackWrapper[] getDummyTracks()
		{
			final TrackWrapper[] tracks = new TrackWrapper[1];
			final TrackWrapper track = getDummySecondary();
			tracks[0] = track;
			return tracks;
		}

		public void testTrimToTrackPeriod()
		{
			final TrackWrapper[] secondaries = new TrackWrapper[0];

			DataSupport.tracksFor(_primary, secondaries, _timeData);

			DataSupport.sensorDataFor(_primary, _timeData, _trackIndex);

			assertTrue(_sw1.getVisible());
			assertTrue(_sw2.getVisible());
			DataSupport.trimToTrackPeriod(_primary, _trackIndex);
			assertTrue(_sw1.getVisible());
			assertFalse(_sw2.getVisible());
		}

		public void testTrimToSensorNearSubjectTracks()
		{
			final TrackWrapper[] secondaries = getDummyTracks();

			DataSupport.tracksFor(_primary, secondaries, _timeData);

			DataSupport.sensorDataFor(_primary, _timeData, _trackIndex);

			assertTrue(_sw1.getVisible());
			assertTrue(_sw2.getVisible());
			ArrayList<SensorWrapper> res = DataSupport.trimToSensorNearSubjectTracks(
					_primary, secondaries);
			assertEquals("contains sensors", 1, res.size());
			assertEquals("correct first sensor", _primary.getSensors().first(), res.get(0));
		}
	}

}
