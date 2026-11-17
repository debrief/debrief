/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.sensorfusion.views;

import java.awt.Color;
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
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class DataSupport {

	public static class SensorSeries extends TacticalSeries {
		/**
			 *
			 */
		private static final long serialVersionUID = 1L;

		final protected SensorWrapper _subject;

		public SensorSeries(final String name, final SensorWrapper subject) {
			super(name);
			_subject = subject;
		}

		@Override
		public Color getColor() {
			return _subject.getColor();
		}

		public SensorWrapper getSensor() {
			return _subject;
		}

		@Override
		public boolean getVisible() {
			if (_subject != null)
				return _subject.getVisible();
			else
				return true;
		}

	}

	abstract public static class TacticalSeries extends TimeSeries {
		/**
			 *
			 */
		private static final long serialVersionUID = 1L;

		public TacticalSeries(final String name) {
			super(name);
		}

		abstract public Color getColor();

		abstract public boolean getVisible();

	}

	public static class TrackSeries extends TacticalSeries {

		/**
			 *
			 */
		private static final long serialVersionUID = 1L;

		private final WatchableList _myTrack;

		public TrackSeries(final String name, final WatchableList subject) {
			super(name);
			_myTrack = subject;
		}

		@Override
		public Color getColor() {
			return _myTrack.getColor();
		}

		@Override
		public boolean getVisible() {
			if (_myTrack != null)
				return _myTrack.getVisible();
			else
				return true;
		}

	}

	private static final int THRESHOLD_FOR_IRRELEVANT_DATA = 45;

	private static double _previousVal;

	private static ColouredDataItem create(final HiResDate hiResDate, final double thisVal, final Color color) {
		double val = thisVal;
		if (val < 0)
			val += 360;

		// aaah, but is it a jump?
		boolean connectToPrevious = true;
		if (!Double.isNaN(_previousVal)) {
			final double delta = Math.abs(val - _previousVal);
			if (delta > 100) {
				connectToPrevious = false;
			}
		}
		_previousVal = val;
		final ColouredDataItem cd = new ColouredDataItem(new FixedMillisecond(hiResDate.getDate().getTime()), val,
				color, connectToPrevious, null, true, true);

		return cd;
	}

	/**
	 * Creates a chart.
	 *
	 * @param dataset a dataset.
	 *
	 * @return A chart.
	 */
	public static JFreeChart createChart(final XYDataset dataset) {

		final JFreeChart chart = ChartFactory.createTimeSeriesChart("Bearing Management", // title
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
		if (r instanceof XYLineAndShapeRenderer) {
			final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}

		final DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new GMTDateFormat("HH:mm.ss"));

		return chart;

	}

	public static long delay() {
		final long delay = (int) (Math.random() * 210) * 60000 + (int) (Math.random() * 5 * 1000);
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

	public static long duration() {
		final long delay = (int) (Math.random() * 90) * 60000 + (int) (Math.random() * 500 * 1000);
		return delay;
	}

	public static void sensorDataFor(final TrackWrapper primary, final TimeSeriesCollection newData,
			final HashMap<SensorWrapper, SensorSeries> index) {

		index.clear();

		final Enumeration<Editable> sensors = primary.getSensors().elements();
		while (sensors.hasMoreElements()) {
			final SensorWrapper sensor = (SensorWrapper) sensors.nextElement();
			final SensorSeries series = new SensorSeries(sensor.getName(), sensor);
			final Enumeration<Editable> cuts = sensor.elements();
			_previousVal = Double.NaN;

			index.put(sensor, series);
			while (cuts.hasMoreElements()) {
				final SensorContactWrapper scw = (SensorContactWrapper) cuts.nextElement();
				final double thisVal = scw.getBearing();

				// wrap this in a try/catch - in case there are multiple entries
				try {
					series.add(create(scw.getTime(), thisVal, scw.getColor()));
				} catch (final Exception e) {
					// no probs, just ignore the new value
				}
			}

			if (!series.isEmpty())
				newData.addSeries(series);
		}
	}

	public static long stepInterval() {
		final long[] intervals = { 1000, 5000, 60000, 300000 };
		final int index = (int) (Math.random() * 4);
		return intervals[index];
	}

	public static void tracksFor(final TrackWrapper primary, final WatchableList[] secondaries,
			final TimeSeriesCollection newData) {
		if (secondaries != null) {
			for (int i = 0; i < secondaries.length; i++) {
				final WatchableList thisS = secondaries[i];
				final TrackSeries thisT = new TrackSeries(thisS.getName(), thisS);
				final Enumeration<Editable> priPts = primary.getPositionIterator();
				_previousVal = Double.NaN;
				while (priPts.hasMoreElements()) {
					final FixWrapper thisP = (FixWrapper) priPts.nextElement();
					final Watchable[] nearest = thisS.getNearestTo(thisP.getDTG());
					if (nearest != null)
						if (nearest.length > 0) {
							final Watchable thisLoc = nearest[0];
							final WorldVector offset = thisLoc.getLocation().subtract(thisP.getLocation());
							final double thisVal = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());

							thisT.add(create(thisP.getTime(), thisVal, thisP.getColor()));
						}
				}
				if (!thisT.isEmpty())
					newData.addSeries(thisT);
			}
		}

	}

	/**
	 * Deletes all blocks of sensor data that are more than 45 degrees from an
	 * secondary track.
	 *
	 * @return
	 */
	public static ArrayList<SensorWrapper> trimToSensorNearSubjectTracks(final TrackWrapper primary,
			final WatchableList[] secondaries) {
		final ArrayList<SensorWrapper> toRemove = new ArrayList<SensorWrapper>();
		final ArrayList<SensorWrapper> toKeep = new ArrayList<SensorWrapper>();

		if (primary == null || secondaries == null)
			return toRemove;

		final Enumeration<Editable> sensors = primary.getSensors().elements();
		while (sensors.hasMoreElements()) {
			SensorWrapper sensor = (SensorWrapper) sensors.nextElement();

			// ok, remember this one
			toRemove.add(sensor);

			final Enumeration<Editable> contacts = sensor.elements();
			// loop though the individual sensor contact objects
			while (sensor != null && (contacts.hasMoreElements())) {
				final SensorContactWrapper contact = (SensorContactWrapper) contacts.nextElement();
				// check this sensor contact has a bearing
				if (!contact.getHasBearing()) {
					// hey, no bearing - we aren't sufficiently able to decide if
					// it can be ditched.
					toKeep.add(sensor);
					sensor = null;
				} else {
					final HiResDate contactTime = contact.getDTG();
					// loop through each secondary track
					for (int i = 0; i < secondaries.length; i++) {
						final WatchableList thisS = secondaries[i];

						final Watchable[] secondaryFixes = thisS.getNearestTo(contactTime);
						final Watchable[] primaryFixes = primary.getNearestTo(contactTime);
						double bearing = 0;
						if (secondaryFixes != null && secondaryFixes.length > 0) {
							if (primaryFixes != null && primaryFixes.length > 0) {
								final WorldLocation wl1 = secondaryFixes[0].getLocation();
								final WorldLocation wl2 = primaryFixes[0].getLocation();
								bearing = wl1.bearingFrom(wl2);
							}
						}

						final double bearingDelta = contact.getBearing() - Math.toDegrees(bearing);
						if (Math.abs(bearingDelta) < THRESHOLD_FOR_IRRELEVANT_DATA) {
							// ok, this sensor is relevant
							toKeep.add(sensor);

							// now clear the sensor, as a marker to move on to the next
							// sensor
							sensor = null;
						}
					}

				}
			} // end loop through sensor contacts
		}

		// ok, we've built up a list of sensors to keep. We now need to get rid of
		// the other ones
		final Iterator<SensorWrapper> keepers = toKeep.iterator();
		while (keepers.hasNext()) {
			final SensorWrapper keepMe = keepers.next();
			toRemove.remove(keepMe);
		}

		return toRemove;
	}

	/**
	 * Deletes any sensor data that is outside the start/finish period of the
	 * primary track.
	 *
	 * @param primary
	 * @param index
	 * @return
	 */
	public static ArrayList<SensorWrapper> trimToTrackPeriod(final TrackWrapper primary) {
		final ArrayList<SensorWrapper> toDelete = new ArrayList<SensorWrapper>();
		final HiResDate startPeriod = primary.getStartDTG();
		final HiResDate finishPeriod = primary.getEndDTG();
		final Enumeration<Editable> numer = primary.getSensors().elements();
		while (numer.hasMoreElements()) {
			final SensorWrapper thisS = (SensorWrapper) numer.nextElement();
			final HiResDate startDTG = thisS.getStartDTG();
			final HiResDate endDTG = thisS.getEndDTG();
			if (startPeriod.greaterThan(endDTG)) {
				// this sensor is before the track even starts
				toDelete.add(thisS);
			}
			if (finishPeriod.lessThan(startDTG)) {
				// this sensor is after the track ends
				toDelete.add(thisS);
			}
		}

		return toDelete;
	}

}
