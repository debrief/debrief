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

package org.mwc.debrief.lite.headless;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.mwc.debrief.lite.LiteApplication;
import org.mwc.debrief.lite.LiteSession;
import org.mwc.debrief.lite.gui.LiteStepControl;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Tools.PlainTool.BoundsProvider;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

/**
 * Shared set-up for Debrief Lite's headless tests: the same application
 * (ToolParent) and session classes that Lite runs with, but no window.
 */
final class HeadlessFixture {

	/** where the sample data lives, relative to the Lite plugin (the test's working dir) */
	static final File SAMPLE_DATA = new File("../org.mwc.cmap.combined.feature/root_installs/sample_data");

	/**
	 * Lite's application object, set to import REP tracks over-the-ground with
	 * every fix kept (no resampling), so a test can count fixes against the file
	 */
	static LiteApplication newApp() {
		final LiteApplication app = new LiteApplication(ImportReplay.IMPORT_AS_OTG, 0L);
		ImportReplay.initialise(app);
		// as Lite's startup does: errors go to the application's log, not a dialog
		MWC.Utilities.Errors.Trace.initialise(app);
		return app;
	}

	/**
	 * a new, empty Lite session, made the application's current one, with a step
	 * control wired up as Lite's startup does (plot files restore its settings)
	 */
	static LiteSession newSession(final LiteApplication app) {
		final LiteSession session = new LiteSession(new java.awt.datatransfer.Clipboard("test"), new Layers());
		app.setSession(session);
		final LiteStepControl stepper = new LiteStepControl(app, session);
		stepper.setUndoBuffer(session.getUndoBuffer());
		stepper.setLayers(session.getData());
		stepper.setTimeLabel(new LiteStepControl.TimeLabel() {
			@Override
			public void setFontSize(final int newSize) {
			}

			@Override
			public void setRange(final long start, final long end) {
			}

			@Override
			public void setValue(final long time) {
			}

			@Override
			public void setValue(final String text) {
			}
		});
		stepper.setSliderControls(new LiteStepControl.SliderControls() {
			private HiResDate _start;
			private HiResDate _end;

			@Override
			public HiResDate getToolboxEndTime() {
				return _end;
			}

			@Override
			public HiResDate getToolboxStartTime() {
				return _start;
			}

			@Override
			public void setEnabled(final boolean enabled) {
			}

			@Override
			public void setToolboxEndTime(final HiResDate val) {
				_end = val;
			}

			@Override
			public void setToolboxStartTime(final HiResDate val) {
				_start = val;
			}
		});
		session.setStepper(stepper);
		return session;
	}

	/** the step control {@link #newSession} gave the session */
	static LiteStepControl stepper(final LiteSession session) {
		return (LiteStepControl) session.getStepControl();
	}

	/** a fixed visible area, standing in for the map's viewport */
	static BoundsProvider boundsOf(final WorldArea area) {
		return new BoundsProvider() {
			@Override
			public WorldArea getBounds() {
				return area;
			}

			@Override
			public WorldArea getViewport() {
				return area;
			}
		};
	}

	/**
	 * a track with fixes every minute, all at the given position, course (degs)
	 * and speed (knots)
	 */
	static TrackWrapper stationaryTrack(final String name, final WorldLocation loc, final double courseDegs,
			final double speedKts, final long startMillis, final int numFixes) {
		final TrackWrapper track = new TrackWrapper();
		track.setName(name);
		for (int i = 0; i < numFixes; i++) {
			final Fix fix = new Fix(new HiResDate(startMillis + i * 60_000L), new WorldLocation(loc),
					MWC.Algorithms.Conversions.Degs2Rads(courseDegs),
					MWC.Algorithms.Conversions.Kts2Yps(speedKts));
			track.addFix(new FixWrapper(fix));
		}
		return track;
	}

	/** the tracks among the layers */
	static List<TrackWrapper> tracks(final Layers layers) {
		final List<TrackWrapper> res = new ArrayList<>();
		final Enumeration<Editable> iter = layers.elements();
		while (iter.hasMoreElements()) {
			final Editable next = iter.nextElement();
			if (next instanceof TrackWrapper) {
				res.add((TrackWrapper) next);
			}
		}
		return res;
	}

	/** the fixes of a track, in time order */
	static List<FixWrapper> fixes(final TrackWrapper track) {
		final List<FixWrapper> res = new ArrayList<>();
		final Enumeration<Editable> iter = track.getPositionIterator();
		while (iter.hasMoreElements()) {
			res.add((FixWrapper) iter.nextElement());
		}
		return res;
	}

	/** the names of all top-level layers */
	static List<String> layerNames(final Layers layers) {
		final List<String> res = new ArrayList<>();
		final Enumeration<Editable> iter = layers.elements();
		while (iter.hasMoreElements()) {
			res.add(((Layer) iter.nextElement()).getName());
		}
		return res;
	}

	private HeadlessFixture() {
	}
}
