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

package org.mwc.debrief.track_shift.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.track_shift.views.StackedDotHelper.*;
import org.mwc.debrief.track_shift.views.StackedDotHelper.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.util.ShapeUtilities;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegmentFromCuts.TMAfromCuts;
import org.mwc.debrief.track_shift.TrackShiftActivator;
import org.mwc.debrief.track_shift.controls.ZoneChart;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;
import org.mwc.debrief.track_shift.zig_detector.ArtificalLegDetector;
import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.mwc.debrief.track_shift.zig_detector.ownship.PeakTrackingOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.ownship.alternate.AlternateLegWrapper;
import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ISecondaryTrack;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layers;
import MWC.GUI.LoggingService;
import MWC.GUI.PlainWrapper;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

public class StackedDotHelperUpdatesTest {
	private static class TrackDataHelper implements SwitchableTrackProvider {

		private TrackWrapper _primary;
		private final List<TrackWrapper> _secondaries = new ArrayList<TrackWrapper>();
		private boolean switchMe = false;

		public void addSecondary(final TrackWrapper tma) {
			_secondaries.add(tma);
		}

		@Override
		public WatchableList[] getPrimaryTracks() {
			if (switchMe) {
				final WatchableList[] res = new WatchableList[_secondaries.size()];
				int ctr = 0;
				final Iterator<TrackWrapper> sIter = _secondaries.iterator();
				while (sIter.hasNext()) {
					res[ctr++] = sIter.next();
				}
				return res;
			} else {
				return new WatchableList[] { _primary };
			}
		}

		@Override
		public WatchableList[] getSecondaryTracks() {
			if (switchMe) {
				return new WatchableList[] { _primary };
			} else {
				final WatchableList[] res = new WatchableList[_secondaries.size()];
				int ctr = 0;
				final Iterator<TrackWrapper> sIter = _secondaries.iterator();
				while (sIter.hasNext()) {
					res[ctr++] = sIter.next();
				}
				return res;
			}
		}

		@Override
		public boolean isPopulated() {
			return true;
		}

		public void setPrimary(final TrackWrapper ownship) {
			_primary = ownship;
		}

		public void setSwitch(final boolean doSwitch) {
			switchMe = doSwitch;
		}
	}

	private static SensorContactWrapper[] checkHullSensor(final BaseLayer sensors) {
		final SensorWrapper hullSensor = (SensorWrapper) sensors.find("hull sensor");
		assertNotNull(hullSensor, "found hull");
		final SensorContactWrapper[] hullItems = getAllCutsFrom(hullSensor);
		assertEquals(9, hullItems.length, "got all cuts");
		return hullItems;
	}

	private static SensorContactWrapper[] checkTailSensor(final BaseLayer sensors) {
		final SensorWrapper tailSensor = (SensorWrapper) sensors.find("tail sensor");
		assertNotNull(tailSensor, "found tail");

		// give it it's offset
		tailSensor.setSensorOffset(new ArrayLength(1000));

		final SensorContactWrapper[] tailItems = getAllCutsFrom(tailSensor);

		// note: we've commented out some
		assertEquals(8, tailItems.length, "got all cuts");
		return tailItems;
	}

	private static TrackWrapper checkTMA(final Layers layers, final String newName) {
		// get the TMA
		final TrackWrapper tma = (TrackWrapper) layers.findLayer(newName);
		assertNotNull(tma, "found it");

		// have a butchers
		assertEquals(1, tma.getSegments().size(), "has segments");
		final Collection<Editable> fixes = tma.getUnfilteredItems(new HiResDate(0),
				new HiResDate(new Date().getTime()));

		// note: only 8 fixes in leg, since two sensor cut was hidden
		assertEquals(8, fixes.size(), "has fixes");

		final FixWrapper firstFix = (FixWrapper) fixes.toArray(new Editable[] {})[0];
		@SuppressWarnings("deprecation")
		final String toTime = firstFix.getDateTimeGroup().getDate().toGMTString();
		assertEquals("12 Jan 2010 12:00:15 GMT", toTime, "valid first time");
		return tma;
	}

	private static void checkUpdatedData(final TimeSeriesCollection dotPlotData,
			final TimeSeriesCollection linePlotData) {
		// have a look at what's happened
		assertEquals(8, dotPlotData.getSeriesCount(), "has error data");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals("ERRORStail sensor_2", dotPlotData.getSeries(0).getKey(), "series correct name");
		assertEquals("ERRORS_amb_tail sensor_2", dotPlotData.getSeries(1).getKey(), "series correct name");
		assertEquals("ERRORShull sensor_2", dotPlotData.getSeries(2).getKey(), "series correct name");
		assertEquals("ERRORS_amb_hull sensor_2", dotPlotData.getSeries(3).getKey(), "series correct name");
		assertEquals("ERRORStail sensor", dotPlotData.getSeries(4).getKey(), "series correct name");
		assertEquals("ERRORS_amb_tail sensor", dotPlotData.getSeries(5).getKey(), "series correct name");
		assertEquals("ERRORShull sensor", dotPlotData.getSeries(6).getKey(), "series correct name");
		assertEquals("ERRORS_amb_hull sensor", dotPlotData.getSeries(7).getKey(), "series correct name");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals(8, dotPlotData.getSeries(0).getItemCount(), "series correct length");
		assertEquals(8, dotPlotData.getSeries(1).getItemCount(), "series correct length");
		assertEquals(9, dotPlotData.getSeries(2).getItemCount(), "series correct length");
		assertEquals(9, dotPlotData.getSeries(3).getItemCount(), "series correct length");

		// error plot. the data is ambiguous, so we've got 4 sets of errors (two
		// sensors, port & stbd)
		assertEquals(12, linePlotData.getSeriesCount(), "has error data");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals("M_tail sensor_2", linePlotData.getSeries(0).getKey(), "series correct name");
		assertEquals("M_hull sensor_2", linePlotData.getSeries(1).getKey(), "series correct name");
		assertEquals("M_tail sensor", linePlotData.getSeries(2).getKey(), "series correct name");
		assertEquals("M_hull sensor", linePlotData.getSeries(3).getKey(), "series correct name");
		assertEquals("M_tail sensor_2(A)", linePlotData.getSeries(4).getKey(), "series correct name");
		assertEquals("M_hull sensor_2(A)", linePlotData.getSeries(5).getKey(), "series correct name");
		assertEquals("M_tail sensor(A)", linePlotData.getSeries(6).getKey(), "series correct name");
		assertEquals("M_hull sensor(A)", linePlotData.getSeries(7).getKey(), "series correct name");
		assertEquals("Calculatedtail sensor_2", linePlotData.getSeries(8).getKey(), "series correct name");
		assertEquals("Calculatedhull sensor_2", linePlotData.getSeries(9).getKey(), "series correct name");
		assertEquals("Calculatedtail sensor", linePlotData.getSeries(10).getKey(), "series correct name");
		assertEquals("Calculatedhull sensor", linePlotData.getSeries(11).getKey(), "series correct name");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals(8, linePlotData.getSeries(0).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(1).getItemCount(), "series correct length");
		assertEquals(8, linePlotData.getSeries(2).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(3).getItemCount(), "series correct length");
		assertEquals(8, linePlotData.getSeries(4).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(5).getItemCount(), "series correct length");
	}

	private static void checkUpdates2(final TimeSeriesCollection dotPlotData,
			final TimeSeriesCollection linePlotData) {
		// have a look at what's happened

		// error plot. the data is ambiguous, so we've got 4 sets of errors (two
		// sensors, port & stbd)
		assertEquals(4, dotPlotData.getSeriesCount(), "has error data");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals("ERRORStail sensor", dotPlotData.getSeries(0).getKey(), "series correct name");
		assertEquals("ERRORS_amb_tail sensor", dotPlotData.getSeries(1).getKey(), "series correct name");
		assertEquals("ERRORShull sensor", dotPlotData.getSeries(2).getKey(), "series correct name");
		assertEquals("ERRORS_amb_hull sensor", dotPlotData.getSeries(3).getKey(), "series correct name");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals(8, dotPlotData.getSeries(0).getItemCount(), "series correct length");
		assertEquals(8, dotPlotData.getSeries(1).getItemCount(), "series correct length");
		assertEquals(9, dotPlotData.getSeries(2).getItemCount(), "series correct length");
		assertEquals(9, dotPlotData.getSeries(3).getItemCount(), "series correct length");

		// error plot. the data is ambiguous, so we've got 4 sets of errors (two
		// sensors, port & stbd)
		assertEquals(6, linePlotData.getSeriesCount(), "has error data");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals("M_tail sensor", linePlotData.getSeries(0).getKey(), "series correct name");
		assertEquals("M_hull sensor", linePlotData.getSeries(1).getKey(), "series correct name");
		assertEquals("M_tail sensor(A)", linePlotData.getSeries(2).getKey(), "series correct name");
		assertEquals("M_hull sensor(A)", linePlotData.getSeries(3).getKey(), "series correct name");
		assertEquals("Calculatedtail sensor", linePlotData.getSeries(4).getKey(), "series correct name");
		assertEquals("Calculatedhull sensor", linePlotData.getSeries(5).getKey(), "series correct name");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals(8, linePlotData.getSeries(0).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(1).getItemCount(), "series correct length");
		assertEquals(8, linePlotData.getSeries(2).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(3).getItemCount(), "series correct length");
		assertEquals(8, linePlotData.getSeries(4).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(5).getItemCount(), "series correct length");
	}

	private static SensorContactWrapper[] getAllCutsFrom(final SensorWrapper secondSensor) {
		final SensorContactWrapper[] res = new SensorContactWrapper[secondSensor.size()];
		final Enumeration<Editable> sIter = secondSensor.elements();
		int ctr = 0;
		while (sIter.hasMoreElements()) {
			res[ctr++] = (SensorContactWrapper) sIter.nextElement();
		}

		return res;
	}

	public Layers getData() throws ParseException, IOException {
		final Layers layers = new Layers();
		final ImportReplay importer = new ImportReplay();
		importer.setLayers(layers);

		importer.readLine("100112 120000 SUBJECT VC 60 06 00.00 N 000 15 00.00 E 320.00  9.00  0.00");
		importer.readLine("100112 120000 SENSOR FA 60 10 48.00 N 000 12 00.00 E 200.00  12.00  0.00");
		importer.readLine(
				";SENSOR2: 100112 120016 SENSOR @A NULL 162.64 237.36 150.910 NULL \"hull sensor\" SUBJECT held on hull sensor");
		importer.readLine(
				";SENSOR2: 100112 120015 SENSOR @A NULL 166.15 233.85 150.920 NULL \"tail sensor\" SUBJECT held on tail sensor");
		importer.readLine("100112 120020 SUBJECT VC 60 06 02.30 N 000 14 56.13 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120020 SENSOR FA 60 10 44.24 N 000 11 57.25 E 200.00  12.00  0.00 ");
		importer.readLine(
				";SENSOR2: 100112 120020 SENSOR @A NULL 162.39 237.61 150.909 NULL \"hull sensor\" SUBJECT held on hull sensor");
		importer.readLine(
				";SENSOR2: 100112 120020 SENSOR @A NULL 165.99 234.01 150.919 NULL \"tail sensor\" SUBJECT held on tail sensor");
		importer.readLine("100112 120040 SUBJECT VC 60 06 04.60 N 000 14 52.26 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120040 SENSOR FA 60 10 40.48 N 000 11 54.50 E 200.00  12.00  0.00 ");
		importer.readLine(
				";SENSOR2: 100112 120040 SENSOR @A NULL 162.13 237.87 150.908 NULL \"hull sensor\" SUBJECT held on hull sensor");
		importer.readLine(
				";SENSOR2: 100112 120040 SENSOR @A NULL 165.82 234.18 150.919 NULL \"tail sensor\" SUBJECT held on tail sensor");
		importer.readLine("100112 120100 SUBJECT VC 60 06 06.89 N 000 14 48.39 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120100 SENSOR FA 60 10 36.72 N 000 11 51.75 E 200.00  12.00  0.00 ");
		importer.readLine(
				";SENSOR2: 100112 120100 SENSOR @A NULL 161.87 238.13 150.907 NULL \"hull sensor\" SUBJECT held on hull sensor");
		// miss this tail measurement
		// importer.readLine(
		// ";SENSOR2: 100112 120100 SENSOR @A NULL 165.64 234.36 150.918 NULL \"tail
		// sensor\"
		// SUBJECT held on tail sensor");
		importer.readLine("100112 120120 SUBJECT VC 60 06 09.19 N 000 14 44.53 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120120 SENSOR FA 60 10 32.96 N 000 11 49.00 E 200.00  12.00  0.00 ");
		importer.readLine(
				";SENSOR2: 100112 120120 SENSOR @A NULL 161.59 238.41 150.906 NULL \"hull sensor\" SUBJECT held on hull sensor");
		// importer.readLine(
		// ";SENSOR2: 100112 120120 SENSOR @A NULL 165.46 234.54 150.918 NULL \"tail
		// sensor\"
		// SUBJECT held on tail sensor");
		importer.readLine("100112 120140 SUBJECT VC 60 06 11.49 N 000 14 40.66 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120140 SENSOR FA 60 10 29.21 N 000 11 46.25 E 200.00  12.00  0.00 ");
		// importer.readLine(
		// ";SENSOR2: 100112 120140 SENSOR @A NULL 161.29 238.71 150.905 NULL \"hull
		// sensor\"
		// SUBJECT held on hull sensor");
		importer.readLine(
				";SENSOR2: 100112 120140 SENSOR @A NULL 165.27 234.73 150.918 NULL \"tail sensor\" SUBJECT held on tail sensor");
		importer.readLine("100112 120200 SUBJECT VC 60 06 13.79 N 000 14 36.79 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120200 SENSOR FA 60 10 25.45 N 000 11 43.49 E 200.00  12.00  0.00 ");
		importer.readLine(
				";SENSOR2: 100112 120200 SENSOR @A NULL 160.99 239.01 150.904 NULL \"hull sensor\" SUBJECT held on hull sensor");
		importer.readLine(
				";SENSOR2: 100112 120200 SENSOR @A NULL 165.08 234.92 150.917 NULL \"tail sensor\" SUBJECT held on tail sensor");
		importer.readLine("100112 120220 SUBJECT VC 60 06 16.09 N 000 14 32.92 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120220 SENSOR FA 60 10 21.69 N 000 11 40.74 E 200.00  12.00  0.00 ");
		importer.readLine(
				";SENSOR2: 100112 120220 SENSOR @A NULL 160.67 239.33 150.902 NULL \"hull sensor\" SUBJECT held on hull sensor");
		importer.readLine(
				";SENSOR2: 100112 120220 SENSOR @A NULL 164.87 235.13 150.916 NULL \"tail sensor\" SUBJECT held on tail sensor");
		importer.readLine("100112 120240 SUBJECT VC 60 06 18.39 N 000 14 29.05 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120240 SENSOR FA 60 10 17.93 N 000 11 37.99 E 200.00  12.00  0.00 ");
		importer.readLine(
				";SENSOR2: 100112 120240 SENSOR @A NULL 160.33 239.67 150.901 NULL \"hull sensor\" SUBJECT held on hull sensor");
		importer.readLine(
				";SENSOR2: 100112 120240 SENSOR @A NULL 164.66 235.34 150.916 NULL \"tail sensor\" SUBJECT held on tail sensor");
		importer.readLine("100112 120300 SUBJECT VC 60 06 20.68 N 000 14 25.18 E 320.00  9.00  0.00 ");
		importer.readLine("100112 120300 SENSOR FA 60 10 14.17 N 000 11 35.24 E 200.00  12.00  0.00 ");
		importer.readLine(
				";SENSOR2: 100112 120300 SENSOR @A NULL 159.98 240.02 150.900 NULL \"hull sensor\" SUBJECT held on hull sensor");
		importer.readLine(
				";SENSOR2: 100112 120300 SENSOR @A NULL 164.44 235.56 150.915 NULL \"tail sensor\" SUBJECT held on tail sensor");

		importer.storePendingSensors();

		return layers;
	}

	public FixWrapper getShiftedFix(final FixWrapper fix) {
		final WorldLocation newLoc = fix.getLocation().add(new WorldVector(0.002, Math.PI / 2, 0.0d));
		final Fix theFix = new Fix(fix.getDateTimeGroup(), newLoc, fix.getCourse(), fix.getSpeed());
		final FixWrapper res = new FixWrapper(theFix);
		return res;
	}

	public TrackDataHelper getTrackData() throws ParseException, IOException {
		final Layers layers = getData();

		final TrackWrapper ownship = (TrackWrapper) layers.findLayer("SENSOR");
		assertNotNull(ownship, "found ownship");

		final BaseLayer sensors = ownship.getSensors();
		assertEquals(2, sensors.size(), "has all sensors");

		final SensorContactWrapper[] tailItems = checkTailSensor(sensors);

		final SensorContactWrapper[] hullItems = checkHullSensor(sensors);

		final SensorWrapper hullSensor = (SensorWrapper) sensors.find("hull sensor");
		assertNotNull(hullSensor, "found hull");

		// note: we've commented out some
		assertEquals(8, tailItems.length, "got all cuts");
		assertEquals(9, hullItems.length, "got all cuts");

		final String newName = "TMA_LEG";

		// ok, we also have to generate some target track
		final TMAfromCuts genny = new TMAfromCuts(tailItems, layers, new WorldVector(Math.PI / 2, 0.02, 0), 45,
				new WorldSpeed(12, WorldSpeed.Kts), Color.RED) {
			@Override
			public String getTrackNameFor(final TrackWrapper newTrack) {
				return newName;
			}

			@Override
			public boolean isRunning() {
				return false;
			}
		};

		// create the new TMA
		try {
			genny.execute(null, null);
		} catch (final Exception e) {
			fail("exception thrown while running command" + e.getMessage());
			e.printStackTrace();
		}

		final TrackWrapper tma = checkTMA(layers, newName);

		// and now the track data object
		final TrackDataHelper prov = new TrackDataHelper();
		prov.setPrimary(ownship);
		prov.addSecondary(tma);

		return prov;
	}

	public TrackDataHelper getTrackDataWithSingle() throws ParseException, IOException {
		final Layers layers = getData();
		final TrackWrapper ownship = (TrackWrapper) layers.findLayer("SENSOR");
		assertNotNull(ownship, "found ownship");

		final BaseLayer sensors = ownship.getSensors();
		assertEquals(2, sensors.size(), "has all sensors");

		// ok, create another sensor track
		final TrackWrapper singleP = new TrackWrapper();
		singleP.setName("Single");
		final FixWrapper firstLoc = getShiftedFix((FixWrapper) ownship.getPositionIterator().nextElement());
		singleP.add(firstLoc);

		final Enumeration<Editable> sIter = sensors.elements();
		while (sIter.hasMoreElements()) {
			final SensorWrapper thisS = (SensorWrapper) sIter.nextElement();
			final SensorWrapper newS = new SensorWrapper(thisS.getName() + "_2");
			singleP.add(newS);
			final Enumeration<Editable> cutITer = thisS.elements();
			while (cutITer.hasMoreElements()) {
				final SensorContactWrapper scw = (SensorContactWrapper) cutITer.nextElement();
				final SensorContactWrapper dup = new SensorContactWrapper(scw);
				newS.add(dup);
			}
		}

		checkHullSensor(sensors);

		// get the tail
		final SensorContactWrapper[] tailItems = checkTailSensor(sensors);

		final String newName = "TMA_LEG";

		// ok, we also have to generate some target track
		final TMAfromCuts genny = new TMAfromCuts(tailItems, layers, new WorldVector(Math.PI / 2, 0.02, 0), 45,
				new WorldSpeed(12, WorldSpeed.Kts), Color.RED) {
			public String getTrackNameFor(final TrackWrapper newTrack) {
				return newName;
			}

			@Override
			public boolean isRunning() {
				return false;
			}
		};

		// create the new TMA
		try {
			genny.execute(null, null);
		} catch (final Exception e) {
			fail("exception thrown while running command" + e.getMessage());
			e.printStackTrace();
		}

		// get the TMA
		final TrackWrapper tma = (TrackWrapper) layers.findLayer(newName);
		assertNotNull(tma, "found it");

		// have a butchers
		assertEquals(1, tma.getSegments().size(), "has segments");
		final Collection<Editable> fixes = tma.getUnfilteredItems(new HiResDate(0),
				new HiResDate(new Date().getTime()));

		// note: only 8 fixes in leg, since two sensor cut was hidden
		assertEquals(8, fixes.size(), "has fixes");

		final FixWrapper firstFix = (FixWrapper) fixes.toArray(new Editable[] {})[0];
		@SuppressWarnings("deprecation")
		final String toTime = firstFix.getDateTimeGroup().getDate().toGMTString();
		assertEquals("12 Jan 2010 12:00:15 GMT", toTime, "valid first time");

		// and now the track data object
		final TrackDataHelper prov = new TrackDataHelper();
		prov.setPrimary(tma);
		prov.addSecondary(ownship);
		prov.addSecondary(singleP);
		prov.setSwitch(false);

		return prov;
	}

	@Test
	public void testGetCourseData() throws ParseException, IOException {
		final Layers layers = getData();
		final TrackWrapper ownship = (TrackWrapper) layers.findLayer("SENSOR");
		assertNotNull(ownship, "found ownship");

		// sort out start/end times
		HiResDate startDTG = DebriefFormatDateTime.parseThis("100112", "120200");
		HiResDate endDTG = DebriefFormatDateTime.parseThis("100112", "120240");

		TimeSeries courseData = getStandardCourseData(ownship, false, startDTG, endDTG);
		assertNotNull(courseData, "found course data");
		assertEquals(3, courseData.getItemCount(), "has items");

		TimeSeriesDataItem firstItem = courseData.getDataItem(0);
		assertEquals(200d, firstItem.getValue(), "correct course");

		TimeSeriesDataItem lastItem = courseData.getDataItem(courseData.getItemCount() - 1);
		assertEquals(200d, lastItem.getValue(), "correct course");

		// sort out start/end times
		startDTG = DebriefFormatDateTime.parseThis("100112", "110000");
		endDTG = DebriefFormatDateTime.parseThis("100112", "120240");

		courseData = getStandardCourseData(ownship, false, startDTG, endDTG);
		assertNotNull(courseData, "found course data");
		assertEquals(9, courseData.getItemCount(), "has items");

		firstItem = courseData.getDataItem(0);
		assertEquals(200d, firstItem.getValue(), "correct course");

		lastItem = courseData.getDataItem(courseData.getItemCount() - 1);
		assertEquals(200d, lastItem.getValue(), "correct course");

		// switch the flip axes value
		courseData = getStandardCourseData(ownship, true, startDTG, endDTG);
		assertNotNull(courseData, "found course data");
		assertEquals(9, courseData.getItemCount(), "has items");

		firstItem = courseData.getDataItem(0);
		assertEquals(-160d, firstItem.getValue(), "correct course");

		lastItem = courseData.getDataItem(courseData.getItemCount() - 1);
		assertEquals(-160d, lastItem.getValue(), "correct course");

	}

	@Test
	public void testGetMultiPrimaryTrackData() throws FileNotFoundException {
		// get our sample data-file
		final ImportReplay importer = new ImportReplay();
		final Layers layers = new Layers();
		final String fName = "../org.mwc.cmap.combined.feature/root_installs/sample_data/MultiStatics/multistatics_buoyfield.rep";
		final File inFile = new File(fName);
		assertTrue(inFile.exists(), "input file exists");
		final FileInputStream is = new FileInputStream(fName);
		importer.importThis(fName, is, layers);

		// sort out the sensors
		importer.storePendingSensors();

		// get the sensor tracks
		final TrackWrapper rx_1 = (TrackWrapper) layers.findLayer("RX_1");
		final TrackWrapper rx_2 = (TrackWrapper) layers.findLayer("RX_2");

		final SensorWrapper rx_1_sensor = (SensorWrapper) rx_1.getSensors().first();
		final SensorWrapper rx_2_sensor = (SensorWrapper) rx_2.getSensors().first();

		assertNotNull(rx_1_sensor, "found sensor 1");
		assertNotNull(rx_2_sensor, "found sensor 2");

		// ok, we need to move one sensor

		// get the tail

		final SensorContactWrapper[] rx1_cuts = getAllCutsFrom(rx_1_sensor);
		final SensorContactWrapper[] rx2_cuts = getAllCutsFrom(rx_2_sensor);

		// note: we've commented out some
		assertEquals(42, rx1_cuts.length, "got all cuts");
		assertEquals(66, rx2_cuts.length, "got all cuts");

		final String newName = "TMA_LEG";

		// ok, we also have to generate some target track
		final TMAfromCuts genny = new TMAfromCuts(rx1_cuts, layers, new WorldVector(Math.PI / 2, 0.02, 0), 45,
				new WorldSpeed(12, WorldSpeed.Kts), Color.RED) {
			public String getTrackNameFor(final TrackWrapper newTrack) {
				return newName;
			}

			@Override
			public boolean isRunning() {
				return false;
			}
		};

		// create the new TMA
		try {
			genny.execute(null, null);
		} catch (final Exception e) {
			fail("exception thrown while running command" + e.getMessage());
			e.printStackTrace();
		}

		// get the TMA
		final TrackWrapper tma = (TrackWrapper) layers.findLayer(newName);
		assertNotNull(tma, "found it");

		// have a butchers
		assertEquals(1, tma.getSegments().size(), "has segments");
		final Collection<Editable> fixes = tma.getUnfilteredItems(new HiResDate(0),
				new HiResDate(new Date().getTime()));

		assertEquals(42, fixes.size(), "has fixes");

		final FixWrapper firstFix = (FixWrapper) fixes.toArray(new Editable[] {})[0];
		@SuppressWarnings("deprecation")
		final String toTime = firstFix.getDateTimeGroup().getDate().toGMTString();
		assertEquals("12 Dec 2014 12:03:40 GMT", toTime, "valid first time");

		// and now the track data object
		final TrackDataHelper prov = new TrackDataHelper();
		prov.addSecondary(rx_1);
		prov.addSecondary(rx_2);
		prov.setPrimary(tma);
		prov.setSwitch(true);

		// return prov;
	}

	@Test
	public void testGetSinglePointCourseData() throws ParseException, IOException {
		final TrackDataHelper data = getTrackDataWithSingle();
		data.setSwitch(true);

		final WatchableList primary = data.getPrimaryTracks()[1];
		final ISecondaryTrack secondary = (ISecondaryTrack) data.getSecondaryTracks()[0];
		final TimeSeries series = StackedDotHelper.getSinglePointCourseData((TrackWrapper) primary, secondary,
				false);
		assertNotNull(series, "has data");
		assertEquals(8, series.getItemCount(), "correct num points");

		final List<?> items = series.getItems();
		for (final Object t : items) {
			final TimeSeriesDataItem item = (TimeSeriesDataItem) t;
			assertEquals(200d, item.getValue(), "correct course");
		}

	}

	@Test
	public void testSwitchedUpdateBearings() throws ExecutionException, ParseException, IOException {
		final StackedDotHelper helper = new StackedDotHelper();
		final TimeSeriesCollection dotPlotData = new TimeSeriesCollection();
		final TimeSeriesCollection linePlotData = new TimeSeriesCollection();

		final TrackDataHelper switcher = getTrackDataWithSingle();

		boolean onlyVis = false;
		final boolean showCourse = true;
		final boolean flipAxes = false;

		final ErrorLogger logger = new LoggingService();
		final boolean updateDoublets = true;
		final TimeSeriesCollection targetCourseSeries = new TimeSeriesCollection();
		final TimeSeriesCollection targetSpeedSeries = new TimeSeriesCollection();
		final TimeSeriesCollection measuredValuesColl = new TimeSeriesCollection();
		final TimeSeriesCollection ambigValuesColl = new TimeSeriesCollection();
		final TimeSeries ownshipCourseSeries = new TimeSeries("OS Course");
		final TimeSeries targetBearingSeries = new TimeSeries("Tgt Bearing");
		final TimeSeries targetCalculatedSeries = new TimeSeries("target calc");
		final ResidualXYItemRenderer overviewSpeedRenderer = null;
		final WrappingResidualRenderer overviewCourseRenderer = null;
		final SetBackgroundShade backShader = new SetBackgroundShade() {
			@Override
			public void setShade(final Paint errorColor) {
				// just ignore it
			}
		};

		helper.initialise(switcher, true, onlyVis, logger, "Bearings", true, false);
		helper.updateBearingData(dotPlotData, linePlotData, switcher, onlyVis, showCourse, flipAxes, logger,
				updateDoublets, targetCourseSeries, targetSpeedSeries, measuredValuesColl, ambigValuesColl,
				ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries, overviewSpeedRenderer,
				overviewCourseRenderer, backShader);

		// should be zero, since we have too many secondaries
		assertEquals(0, dotPlotData.getSeriesCount(), "has error data");

		switcher.setSwitch(true);
		helper.updateBearingData(dotPlotData, linePlotData, switcher, onlyVis, showCourse, flipAxes, logger,
				updateDoublets, targetCourseSeries, targetSpeedSeries, measuredValuesColl, ambigValuesColl,
				ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries, overviewSpeedRenderer,
				overviewCourseRenderer, backShader);

		checkUpdatedData(dotPlotData, linePlotData);
		assertEquals(8, linePlotData.getSeries(6).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(7).getItemCount(), "series correct length");
		assertEquals(8, linePlotData.getSeries(8).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(9).getItemCount(), "series correct length");
		assertEquals(8, linePlotData.getSeries(10).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(11).getItemCount(), "series correct length");

		// ok, hide a sensor, and recalculate
		final TrackWrapper primary = (TrackWrapper) switcher.getPrimaryTracks()[0];
		final SensorWrapper firstSensor = (SensorWrapper) primary.getSensors().elements().nextElement();
		firstSensor.setVisible(true);

		onlyVis = false;

		helper.updateBearingData(dotPlotData, linePlotData, switcher, onlyVis, showCourse, flipAxes, logger,
				updateDoublets, targetCourseSeries, targetSpeedSeries, measuredValuesColl, ambigValuesColl,
				ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries, overviewSpeedRenderer,
				overviewCourseRenderer, backShader);
	}

	@Test
	public void testUpdateBearings() throws ExecutionException, ParseException, IOException {
		final StackedDotHelper helper = new StackedDotHelper();
		final TimeSeriesCollection dotPlotData = new TimeSeriesCollection();
		final TimeSeriesCollection linePlotData = new TimeSeriesCollection();

		final TrackDataHelper switcher = getTrackData();

		boolean onlyVis = false;
		final boolean showCourse = true;
		final boolean flipAxes = false;

		final ErrorLogger logger = new LoggingService();
		final boolean updateDoublets = true;
		final TimeSeriesCollection targetCourseSeries = new TimeSeriesCollection();
		final TimeSeriesCollection targetSpeedSeries = new TimeSeriesCollection();
		final TimeSeriesCollection measuredValuesColl = new TimeSeriesCollection();
		final TimeSeriesCollection ambigValuesColl = new TimeSeriesCollection();
		final TimeSeries ownshipCourseSeries = new TimeSeries("OS Course");
		final TimeSeries targetBearingSeries = new TimeSeries("Tgt Bearing");
		final TimeSeries targetCalculatedSeries = new TimeSeries("target calc");
		final ResidualXYItemRenderer overviewSpeedRenderer = null;
		final WrappingResidualRenderer overviewCourseRenderer = null;
		final SetBackgroundShade backShader = new SetBackgroundShade() {
			@Override
			public void setShade(final Paint errorColor) {
				// just ignore it
			}
		};

		helper.initialise(switcher, true, onlyVis, logger, "Bearings", true, false);
		helper.updateBearingData(dotPlotData, linePlotData, switcher, onlyVis, showCourse, flipAxes, logger,
				updateDoublets, targetCourseSeries, targetSpeedSeries, measuredValuesColl, ambigValuesColl,
				ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries, overviewSpeedRenderer,
				overviewCourseRenderer, backShader);

		checkUpdates2(dotPlotData, linePlotData);

		// ok, hide a sensor, and recalculate
		final TrackWrapper primary = (TrackWrapper) switcher.getPrimaryTracks()[0];
		final SensorWrapper firstSensor = (SensorWrapper) primary.getSensors().elements().nextElement();
		firstSensor.setVisible(true);

		onlyVis = false;

		helper.updateBearingData(dotPlotData, linePlotData, switcher, onlyVis, showCourse, flipAxes, logger,
				updateDoublets, targetCourseSeries, targetSpeedSeries, measuredValuesColl, ambigValuesColl,
				ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries, overviewSpeedRenderer,
				overviewCourseRenderer, backShader);

		// have a look at what's happened

		checkUpdates2(dotPlotData, linePlotData);

		onlyVis = true;

		helper.updateBearingData(dotPlotData, linePlotData, switcher, onlyVis, showCourse, flipAxes, logger,
				updateDoublets, targetCourseSeries, targetSpeedSeries, measuredValuesColl, ambigValuesColl,
				ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries, overviewSpeedRenderer,
				overviewCourseRenderer, backShader);

		// have a look at what's happened

		// error plot. the data is ambiguous, so we've got 4 sets of errors (two
		// sensors, port & stbd)
		assertEquals(2, dotPlotData.getSeriesCount(), "has error data");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals(9, dotPlotData.getSeries(0).getItemCount(), "series correct length");
		assertEquals(9, dotPlotData.getSeries(1).getItemCount(), "series correct length");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals("ERRORS", dotPlotData.getSeries(0).getKey(), "series correct name");
		assertEquals("ERRORS_amb_", dotPlotData.getSeries(1).getKey(), "series correct name");

		// error plot. the data is ambiguous, so we've got 4 sets of errors (two
		// sensors, port & stbd)
		assertEquals(3, linePlotData.getSeriesCount(), "has error data");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals(9, linePlotData.getSeries(0).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(1).getItemCount(), "series correct length");
		assertEquals(9, linePlotData.getSeries(2).getItemCount(), "series correct length");

		// note: even though TMA only has 9 fixes, we get 10 errors since we interpolate
		assertEquals("M_", linePlotData.getSeries(0).getKey(), "series correct name");
		assertEquals("M_(A)", linePlotData.getSeries(1).getKey(), "series correct name");
		assertEquals("Calculated", linePlotData.getSeries(2).getKey(), "series correct name");

		// and make the second sensor visible
		final Enumeration<Editable> sIter = primary.getSensors().elements();
		while (sIter.hasMoreElements()) {
			final SensorWrapper sensor = (SensorWrapper) sIter.nextElement();
			sensor.setVisible(true);
		}

		helper.updateBearingData(dotPlotData, linePlotData, switcher, onlyVis, showCourse, flipAxes, logger,
				updateDoublets, targetCourseSeries, targetSpeedSeries, measuredValuesColl, ambigValuesColl,
				ownshipCourseSeries, targetBearingSeries, targetCalculatedSeries, overviewSpeedRenderer,
				overviewCourseRenderer, backShader);

		checkUpdates2(dotPlotData, linePlotData);

	}
}
