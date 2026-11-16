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

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.sensorfusion.views.DataSupport.*;
import org.mwc.debrief.sensorfusion.views.DataSupport.*;
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
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class DataSupportTest {
	private HashMap<SensorWrapper, SensorSeries> _trackIndex;
	private TimeSeriesCollection _timeData;
	private TrackWrapper _primary;
	private SensorWrapper _sw1;
	private SensorWrapper _sw2a;
	private SensorWrapper _sw2b;

	private TrackWrapper getDummyPrimary() {
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300, WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400, WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500, WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);
		// also give it some sensor data
		_sw1 = new SensorWrapper("title one");
		_sw1.setVisible(true);
		final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa", new HiResDate(150, 0), null,
				new Double(15), null, null, null, 0, null);
		final SensorContactWrapper scwa2 = new SensorContactWrapper("bbb", new HiResDate(180, 0), null,
				new Double(15), null, null, null, 0, null);
		final SensorContactWrapper scwa3 = new SensorContactWrapper("ccc", new HiResDate(250, 0), null,
				new Double(150), null, null, null, 0, null);
		_sw1.add(scwa1);
		_sw1.add(scwa2);
		_sw1.add(scwa3);
		_sw2a = new SensorWrapper("title two");
		_sw2a.setVisible(true);
		final SensorContactWrapper scw1 = new SensorContactWrapper("ddd", new HiResDate(260, 0), null, null, null,
				null, null, 0, null);
		final SensorContactWrapper scw2 = new SensorContactWrapper("eee", new HiResDate(280, 0), null, null, null,
				null, null, 0, null);
		// this time is greater than the primary track finish period
		final SensorContactWrapper scw3 = new SensorContactWrapper("fff", new HiResDate(650, 0), null, null, null,
				null, null, 0, null);
		_sw2a.add(scw1);
		_sw2a.add(scw2);
		_sw2a.add(scw3);
		_sw2b = new SensorWrapper("title three");
		_sw2b.setVisible(true);
		final SensorContactWrapper scw1b = new SensorContactWrapper("ddd", new HiResDate(760, 0), null,
				new Double(150), null, null, null, 0, null);
		final SensorContactWrapper scw2b = new SensorContactWrapper("eee", new HiResDate(880, 0), null,
				new Double(150), null, null, null, 0, null);
		// this time is greater than the primary track finish period
		final SensorContactWrapper scw3b = new SensorContactWrapper("fff", new HiResDate(950, 0), null,
				new Double(150), null, null, null, 0, null);
		_sw2b.add(scw1b);
		_sw2b.add(scw2b);
		_sw2b.add(scw3b);
		tw.add(_sw1);
		tw.add(_sw2a);
		tw.add(_sw2b);

		return tw;
	}

	private TrackWrapper getDummySecondary() {
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33 + 200, new WorldDistance(300, WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");

		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);

		return tw;
	}

	private TrackWrapper[] getDummyTracks() {
		final TrackWrapper[] tracks = new TrackWrapper[1];
		final TrackWrapper track = getDummySecondary();
		tracks[0] = track;
		return tracks;
	}

	@BeforeEach
	public void setUp() {
		_trackIndex = new HashMap<SensorWrapper, SensorSeries>();
		_timeData = new TimeSeriesCollection();
		_primary = getDummyPrimary();
	}

	@Test
	public void testTrimToSensorNearSubjectTracks() {
		final TrackWrapper[] secondaries = getDummyTracks();

		DataSupport.tracksFor(_primary, secondaries, _timeData);

		DataSupport.sensorDataFor(_primary, _timeData, _trackIndex);

		assertTrue(_sw1.getVisible());
		assertTrue(_sw2a.getVisible());
		final ArrayList<SensorWrapper> res = DataSupport.trimToSensorNearSubjectTracks(_primary, secondaries);
		assertEquals(1, res.size(), "contains single sensor");
	}

	@Test
	public void testTrimToTrackPeriod() {
		final TrackWrapper[] secondaries = new TrackWrapper[0];

		DataSupport.tracksFor(_primary, secondaries, _timeData);

		DataSupport.sensorDataFor(_primary, _timeData, _trackIndex);

		assertTrue(_sw1.getVisible());
		assertTrue(_sw2a.getVisible());
		assertEquals(3, _primary.getSensors().size());
		final ArrayList<SensorWrapper> res = DataSupport.trimToTrackPeriod(_primary);
		assertNotNull(res);
		assertEquals(1, res.size());
	}
}
