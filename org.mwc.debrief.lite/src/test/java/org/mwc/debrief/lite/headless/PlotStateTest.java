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

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.util.PlotState;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.CircleShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import junit.framework.TestCase;

/**
 * How the loaded data sets Lite's time controls (inactive, time-enabled or
 * active) and whether it asks to save before discarding the plot.
 */
public class PlotStateTest extends TestCase {

	private static final long T0 = 1767225600000L;

	private static ShapeWrapper circle(final HiResDate start, final HiResDate end) {
		final ShapeWrapper shape = new ShapeWrapper("circle", new CircleShape(new WorldLocation(0, 0, 0), 1000),
				java.awt.Color.RED, start);
		shape.setEndDTG(end);
		return shape;
	}

	private Layers _layers;

	private void assertState(final String expectedState, final boolean expectedDirty) {
		final PlotState state = PlotState.of(_layers);
		assertEquals("state", expectedState, state.getState());
		assertEquals("dirty", expectedDirty, state.isDirty());
	}

	@Override
	protected void setUp() {
		_layers = new Layers();
	}

	public void testEmptyPlot() {
		assertState(DebriefLiteApp.INACTIVE_STATE, false);
	}

	public void testNarratives() {
		final NarrativeWrapper narrative = new NarrativeWrapper("Narratives");
		_layers.addThisLayer(narrative);
		assertState("an empty narrative changes nothing", DebriefLiteApp.INACTIVE_STATE, false);

		narrative.add(new NarrativeEntry("OWNSHIP", new HiResDate(T0), "contact gained"));
		assertState(DebriefLiteApp.TIME_ENABLED_STATE, true);
	}

	public void testTimedShapes() {
		final BaseLayer shapes = new BaseLayer();
		shapes.setName("Shapes");
		_layers.addThisLayer(shapes);

		shapes.add(circle(null, null));
		assertState("an untimed shape changes nothing", DebriefLiteApp.INACTIVE_STATE, false);

		shapes.add(circle(new HiResDate(T0), null));
		assertState("a start time alone is not a period", DebriefLiteApp.INACTIVE_STATE, false);

		shapes.add(circle(new HiResDate(T0), new HiResDate(T0 + 60_000)));
		assertState("a timed shape enables the time controls", DebriefLiteApp.TIME_ENABLED_STATE, false);
	}

	public void testTracks() {
		_layers.addThisLayer(
				HeadlessFixture.stationaryTrack("OWNSHIP", new WorldLocation(0, 0, 0), 90, 10, T0, 3));
		assertState(DebriefLiteApp.ACTIVE_STATE, true);
	}

	public void testTracksOutrankNarratives() {
		final NarrativeWrapper narrative = new NarrativeWrapper("Narratives");
		narrative.add(new NarrativeEntry("OWNSHIP", new HiResDate(T0), "contact gained"));
		_layers.addThisLayer(narrative);
		_layers.addThisLayer(
				HeadlessFixture.stationaryTrack("OWNSHIP", new WorldLocation(0, 0, 0), 90, 10, T0, 3));
		assertState(DebriefLiteApp.ACTIVE_STATE, true);
	}

	private void assertState(final String message, final String expectedState, final boolean expectedDirty) {
		final PlotState state = PlotState.of(_layers);
		assertEquals(message + ": state", expectedState, state.getState());
		assertEquals(message + ": dirty", expectedDirty, state.isDirty());
	}
}
