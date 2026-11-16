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

public class StackedDotHelperSlicingTest {
	@Test
	public void testOSLegDetector() {
		final TimeSeries osC = new TimeSeries(new FixedMillisecond());
		long time = 0;
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 21d);
		osC.add(new FixedMillisecond(time++), 22d);
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 21d);
		osC.add(new FixedMillisecond(time++), 20d);

		assertFalse(containsIdenticalValues(osC, 3));

		// inject some more duplicates
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 20d);

		assertTrue(containsIdenticalValues(osC, 3));

		osC.clear();
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 21d);
		osC.add(new FixedMillisecond(time++), 21d);
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 21d);
		osC.add(new FixedMillisecond(time++), 21d);
		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 20d);

		assertFalse(containsIdenticalValues(osC, 3), "check we're verifying single runs of matches");

		osC.add(new FixedMillisecond(time++), 20d);
		osC.add(new FixedMillisecond(time++), 20d);

		assertTrue(containsIdenticalValues(osC, 3));
	}

	@Test
	public void testSetLeg() {
		final TrackWrapper host = new TrackWrapper();
		host.setName("Host Track");

		// create a sensor
		final SensorWrapper sensor = new SensorWrapper("Sensor");
		sensor.setHost(host);
		host.add(sensor);

		// add some cuts
		final ArrayList<SensorContactWrapper> contacts = new ArrayList<SensorContactWrapper>();
		for (int i = 0; i < 30; i++) {
			final HiResDate thisDTG = new HiResDate(10000 * i);
			final WorldLocation thisLocation = new WorldLocation(2 + 0.01 * i, 2 + 0.03 * i, 0);
			final SensorContactWrapper scw = new SensorContactWrapper(host.getName(), thisDTG,
					new WorldDistance(4, WorldDistance.MINUTES), 25d, thisLocation, Color.RED, "" + i, 0,
					sensor.getName());
			sensor.add(scw);
			contacts.add(scw);

			// also create a host track fix at this DTG
			final Fix theFix = new Fix(thisDTG, thisLocation, 12d, 3d);
			final FixWrapper newF = new FixWrapper(theFix);
			host.add(newF);
		}

		// produce the target leg
		final TrackWrapper target = new TrackWrapper();
		target.setName("Tgt Track");

		// add a TMA leg
		final Layers theLayers = new Layers();
		theLayers.addThisLayer(host);
		theLayers.addThisLayer(target);

		final SensorContactWrapper[] contactArr = contacts.toArray(new SensorContactWrapper[] {});
		final RelativeTMASegment newLeg = new RelativeTMASegment(contactArr, new WorldVector(1, 1, 0),
				new WorldSpeed(12, WorldSpeed.Kts), 12d, theLayers, Color.red);
		target.add(newLeg);

		final BaseStackedDotsView view = new BaseStackedDotsView(true, false) {

			@Override
			protected boolean allowDisplayOfTargetOverview() {
				return false;
			}

			@Override
			protected boolean allowDisplayOfZoneChart() {
				return false;
			}

			@Override
			protected String formatValue(final double value) {
				return "" + value;
			}

			@Override
			protected ZoneSlicer getOwnshipZoneSlicer(final ColorProvider blueProv) {
				return null;
			}

			@Override
			protected String getType() {
				return null;
			}

			@Override
			protected String getUnits() {
				return null;
			}

			@Override
			protected void makeActions() {
				// don't make actions, since they rely on Workbench running
			}

			@Override
			protected void updateData(final boolean updateDoublets) {
				// no, nothing to do.
			}
		};

		// try to set a zone on the track
		Zone trimmedPeriod = new Zone(150000, 220000, Color.RED);
		view.setLeg(host, target, trimmedPeriod);

		// ok, check the leg has changed
		assertEquals(150000, target.getStartDTG().getDate().getTime(), "leg start changed");
		assertEquals(220000, target.getEndDTG().getDate().getTime(), "leg start changed");

		// ok, also see if we can create a new leg
		trimmedPeriod = new Zone(250000, 320000, Color.RED);
		view.setLeg(host, target, trimmedPeriod);

	}
}
