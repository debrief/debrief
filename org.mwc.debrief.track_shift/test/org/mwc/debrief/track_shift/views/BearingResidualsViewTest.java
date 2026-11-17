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
import static org.mwc.debrief.track_shift.views.BearingResidualsView.*;
import org.mwc.debrief.track_shift.views.BearingResidualsView.*;
import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.debrief.track_shift.TrackShiftActivator;
import org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver;
import org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver.LegsAndZigs;
import org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver.ResolvedLeg;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts;
import org.mwc.debrief.track_shift.ambiguity.preferences.PreferenceConstants;
import org.mwc.debrief.track_shift.controls.ZoneChart;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.controls.ZoneChart.ZoneSlicer;
import org.mwc.debrief.track_shift.controls.ZoneUndoRedoProvider;
import org.mwc.debrief.track_shift.views.StackedDotHelper.SetBackgroundShade;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.ITimeVariableProvider;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import org.junit.jupiter.api.Test;

public class BearingResidualsViewTest {
	private static TrackWrapper getData(final String name) throws FileNotFoundException {
		// get our sample data-file
		final ImportReplay importer = new ImportReplay();
		final Layers theLayers = new Layers();
		final String fName = "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/" + name;
		final File inFile = new File(fName);
		assertTrue(inFile.exists(), "input file exists");
		final FileInputStream is = new FileInputStream(fName);
		importer.importThis(fName, is, theLayers);

		// sort out the sensors
		importer.storePendingSensors();
		assertEquals(3, theLayers.size(), "has some layers");

		// get the sensor track
		final TrackWrapper track = (TrackWrapper) theLayers.findLayer("SENSOR");
		return track;
	}

	@Test
	public void testDeleteAmbiguous() throws FileNotFoundException {
		final TrackWrapper track = new TrackWrapper();
		track.setName("track");

		final SensorWrapper s1 = new SensorWrapper("s1");

		final SensorContactWrapper sc1 = new SensorContactWrapper(track.getName(), new HiResDate(100000), null,
				100d, 120d, null, null, Color.RED, "one", LineStylePropertyEditor.DOT_DASH, "sensor");
		final SensorContactWrapper sc2 = new SensorContactWrapper(track.getName(), new HiResDate(200000), null,
				101d, 121d, null, null, Color.RED, "one", LineStylePropertyEditor.DOT_DASH, "sensor");
		final SensorContactWrapper sc3 = new SensorContactWrapper(track.getName(), new HiResDate(300000), null,
				102d, 122d, null, null, Color.RED, "one", LineStylePropertyEditor.DOT_DASH, "sensor");
		final SensorContactWrapper sc4 = new SensorContactWrapper(track.getName(), new HiResDate(400000), null,
				103d, 123d, null, null, Color.RED, "one", LineStylePropertyEditor.DOT_DASH, "sensor");
		final SensorContactWrapper sc5 = new SensorContactWrapper(track.getName(), new HiResDate(500000), null,
				104d, 124d, null, null, Color.RED, "one", LineStylePropertyEditor.DOT_DASH, "sensor");

		s1.add(sc1);
		s1.add(sc2);
		s1.add(sc3);
		s1.add(sc4);
		s1.add(sc5);

		track.add(s1);
		;

		final TimePeriod period = new TimePeriod.BaseTimePeriod(new HiResDate(250000), new HiResDate(500000));
		LegOfCuts resolved = findResolvedAmbiguousCuts(period, track);
		assertEquals(0, resolved.size(), "found none, sensor not visible");

		s1.setVisible(true);

		resolved = findResolvedAmbiguousCuts(period, track);
		assertEquals(0, resolved.size(), "found none, they're not resolved ones");

		// ok, now resolve some
		sc3.setHasAmbiguousBearing(false);
		sc4.setHasAmbiguousBearing(false);

		resolved = findResolvedAmbiguousCuts(period, track);
		assertEquals(2, resolved.size(), "found correct ones");

		sc5.setHasAmbiguousBearing(false);

		resolved = findResolvedAmbiguousCuts(period, track);
		assertEquals(3, resolved.size(), "found correct ones");

	}

	@Test
	public void testDitchUsingAmbiguity() throws FileNotFoundException {
		final TrackWrapper track = getData("Ambig_tracks2.rep");
		assertNotNull(track, "found track");

		// has sensors
		assertEquals(1, track.getSensors().size(), "has sensor");

		// make the sensor visible
		final SensorWrapper sensor = (SensorWrapper) track.getSensors().elements().nextElement();
		sensor.setVisible(true);

		final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor.getEndDTG());

		// try to get zones using ambiguity delta
		final LegsAndZigs res = AmbiguityResolver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, null, null,
				null, null, timePeriod, null);
		final List<LegOfCuts> legs = res.getLegs();
		final LegOfCuts zigs = res.getZigs();

		assertNotNull(legs, "found zones");
		assertEquals(12, legs.size(), "found correct number of zones");

		assertNotNull(zigs, "found zigs");
		assertEquals(22, zigs.size(), "found correct number of zig cuts");

		// ok, ditch those cuts
		final int fullSensorLen = sensor.size();
		Map<SensorWrapper, LegOfCuts> deleted = DeleteCutsOperation.deleteTheseCuts(zigs);
		assertEquals(99, sensor.size(), "fewer cuts");

		// ok, and undo them
		BearingResidualsView.restoreCuts(deleted);
		assertEquals(fullSensorLen, sensor.size(), "fewer cuts");

		// and do it again, so we've got fewer cuts
		deleted = DeleteCutsOperation.deleteTheseCuts(zigs);

		final List<ResolvedLeg> resolvedLegs = AmbiguityResolver.resolve(legs);
		assertNotNull(resolvedLegs);
		assertEquals(12, legs.size(), "right num legs");

		assertEquals(251.33d, resolvedLegs.get(0).leg.get(0).getBearing(), 1d, "correct leg");
		assertEquals(253d, resolvedLegs.get(1).leg.get(0).getBearing(), 1d, "correct leg");
		assertEquals(251d, resolvedLegs.get(2).leg.get(0).getBearing(), 1d, "correct leg");
		assertEquals(254d, resolvedLegs.get(3).leg.get(0).getBearing(), 1d, "correct leg");
		assertEquals(258d, resolvedLegs.get(4).leg.get(0).getBearing(), 1d, "correct leg");
		assertEquals(269d, resolvedLegs.get(5).leg.get(0).getBearing(), 1d, "correct leg");

		// ok, and cancel the leg resolving
		BearingResidualsView.undoResolveBearings(resolvedLegs);

		// and re-check they're ambiguous
		assertEquals(true, resolvedLegs.get(0).leg.get(0).getHasAmbiguousBearing(), "is unresloved");
	}

	/**
	 * note: we're testing this zone chart functionality in here, so we can
	 * introduce it into the automated test suite.
	 */
	@Test
	public void testPanCalculation() {
		TimePeriod period = ZoneChart.calculatePanData(false, 1000, 2000, 1100, 1300);
		assertEquals(1300, period.getStartDTG().getDate().getTime(), "corrent new times");
		assertEquals(1500, period.getEndDTG().getDate().getTime(), "corrent new times");
		period = ZoneChart.calculatePanData(false, 1000, 2000, 1700, 1900);
		assertEquals(1800, period.getStartDTG().getDate().getTime(), "corrent new times");
		assertEquals(2000, period.getEndDTG().getDate().getTime(), "corrent new times");
		period = ZoneChart.calculatePanData(true, 1000, 2000, 1700, 1900);
		assertEquals(1500, period.getStartDTG().getDate().getTime(), "corrent new times");
		assertEquals(1700, period.getEndDTG().getDate().getTime(), "corrent new times");
		period = ZoneChart.calculatePanData(true, 1000, 2000, 1100, 1300);
		assertEquals(1000, period.getStartDTG().getDate().getTime(), "corrent new times");
		assertEquals(1200, period.getEndDTG().getDate().getTime(), "corrent new times");
	}
}
