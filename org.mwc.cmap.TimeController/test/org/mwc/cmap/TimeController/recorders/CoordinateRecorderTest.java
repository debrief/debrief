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

package org.mwc.cmap.TimeController.recorders;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mwc.cmap.TimeController.recorders.CoordinateRecorder.*;
import org.mwc.cmap.TimeController.recorders.CoordinateRecorder.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.TimeController.wizards.ExportPPTDialog;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.preferences.PrefsPage;
import Debrief.GUI.Frames.Application;
import Debrief.GUI.Views.CoreCoordinateRecorder;
import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.temporal.TimeControlPreferences;
import org.junit.jupiter.api.Test;

public class CoordinateRecorderTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private static final String collingwood = "COLLINGWOOD";
	private static final String nelson = "NELSON";

	private static void checkTrackSize(
			final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track) {
		assertEquals(2, track.size(), "correct amount of tracks");
		assertTrue(track.containsKey(collingwood), "tracks contain collingwood");
		assertTrue(track.containsKey(nelson), "tracks contain nelson");
	}

	private static void doIteration(final CoordinateRecorder recorder,
			final MWC.GenericData.HiResDate currentTime_in, final int AMOUNT_OF_STEPS) {
		MWC.GenericData.HiResDate currentTime = currentTime_in;
		recorder.startStepping(currentTime);

		final long timeDelta = 60000; // 1 min.
		for (int i = 0; i < AMOUNT_OF_STEPS; i++) {
			recorder.newTime(currentTime);
			currentTime = new MWC.GenericData.HiResDate(currentTime.getMicros() / 1000L + timeDelta);
		}
	}

	private static java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> twoStepsCheckCollingwood(
			final CoordinateRecorder recorder, final MWC.GenericData.HiResDate currentTime) {
		final int AMOUNT_OF_STEPS = 2;
		doIteration(recorder, currentTime, AMOUNT_OF_STEPS);

		final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track = recorder.getTracks();
		checkTrackSize(track);
		assertEquals(0, track.get(collingwood).getStepsToSkip(),
				"correct number of skipped steps in collingwood's track");
		assertEquals(2, track.get(collingwood).getPoints().size(),
				"correct number of points in collingwood's segment");
		return track;
	}

	public CoordinateRecorder getRecorder() {
		final String testFilePath = "../org.mwc.cmap.combined.feature/root_installs/sample_data/offset_times.rep";

		// ok, now try to read it in
		final Layers _theLayers = new Layers();

		final ImportReplay importer = new ImportReplay();
		try {
			importer.importThis(testFilePath, new FileInputStream(testFilePath), _theLayers);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}

		assertTrue(_theLayers.size() > 0, "found data");

		final PlainProjection projection = new MWC.Algorithms.Projections.FlatProjection();
		projection.setScreenArea(new java.awt.Dimension(1443, 901));
		projection.setDataArea(new WorldArea(new WorldLocation(22.238965795584505, -21.928244631862952, 0),
				new WorldLocation(22.238965795584505, -21.43985414608609, 0)));

		final org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties timePreferences = new org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties();

		final CoordinateRecorder recorder = new CoordinateRecorder(_theLayers, projection, timePreferences);

		return recorder;
	}

	/**
	 * COLLINGWOOD ends before NELSON
	 */
	@Test
	public void testPrimaryEndsBeforeSecondary() {
		final CoordinateRecorder recorder = getRecorder();

		final MWC.GenericData.HiResDate currentTime = new MWC.GenericData.HiResDate(818764200000L);

		final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track = twoStepsCheckCollingwood(
				recorder, currentTime);

		assertEquals(0, track.get(nelson).getStepsToSkip(), "correct number of skipped steps in nelson's track");
		assertEquals(1, track.get(nelson).getPoints().size(), "correct number of points in track's segment");
		Application.logError2(ToolParent.INFO, "Recording Test Passed (Starting at the same time)", null);
	}

	/**
	 * COLLINGWOOD starts after NELSON
	 */
	@Test
	public void testPrimaryStartsFirst() {
		final CoordinateRecorder recorder = getRecorder();

		final MWC.GenericData.HiResDate currentTime = new MWC.GenericData.HiResDate(818748540000L);

		final int AMOUNT_OF_STEPS = 3;
		doIteration(recorder, currentTime, AMOUNT_OF_STEPS);

		final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track = recorder.getTracks();
		checkTrackSize(track);
		assertEquals(1, track.get(collingwood).getStepsToSkip(), "correct amount of steps skipped in collingwood");
		assertEquals(2, track.get(collingwood).getPoints().size(), "correct amount of segments for collingwood");
		assertEquals(0, track.get(nelson).getStepsToSkip(), "correct amount of steps skipped for nelson");
		assertEquals(3, track.get(nelson).getPoints().size(), "correct amount of tracks for nelson");
		Application.logError2(ToolParent.INFO, "Recording Test Passed (Primary Starting First)", null);
	}

	/**
	 * COLLINGWOOD starts with NELSON
	 */
	@Test
	public void testPrimaryStartsWithSecondary() {
		final CoordinateRecorder recorder = getRecorder();

		final MWC.GenericData.HiResDate currentTime = new MWC.GenericData.HiResDate(818748600000L);

		final java.util.Map<String, Debrief.ReaderWriter.powerPoint.model.Track> track = twoStepsCheckCollingwood(
				recorder, currentTime);
		assertEquals(0, track.get(nelson).getStepsToSkip(), "correct amount of steps skipped in nelson's track");
		assertEquals(2, track.get(nelson).getPoints().size(), "correct amount of points in collingwood's segment");
		Application.logError2(ToolParent.INFO, "Recording Test Passed (Starting at the same time)", null);
	}
}
