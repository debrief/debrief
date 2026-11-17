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

import static org.junit.jupiter.api.Assertions.*;


import org.mwc.debrief.lite.LiteApplication;
import org.mwc.debrief.lite.LiteSession;
import org.mwc.debrief.lite.LiteTote;
import org.mwc.debrief.lite.gui.LiteStepControl;

import Debrief.Tools.Tote.Calculations.bearingCalc;
import Debrief.Tools.Tote.Calculations.courseCalc;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Tools.Tote.Calculations.speedCalc;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Stepping through time with Lite's step control, and the tote's range,
 * bearing, course and speed against hand-calculated values.
 */
public class TimeAndToteTest {

	/** 1 Jan 2026 00:00 UTC */
	private static final long T0 = 1767225600000L;
	private static final long MINUTE_MICROS = 60_000_000L;

	/** the tote reads the primary and secondary at this time */
	private static Watchable at(final WatchableList list, final HiResDate time) {
		final Watchable[] found = list.getNearestTo(time);
		assertTrue(found != null && found.length > 0, list.getName() + " has a point at " + time);
		return found[0];
	}

	private LiteStepControl _stepper;
	private LiteTote _tote;
	private Layers _layers;
	private TrackWrapper _ownship;
	private TrackWrapper _contact;

	@BeforeEach
	public void setUp() {
		final LiteApplication app = HeadlessFixture.newApp();
		final LiteSession session = HeadlessFixture.newSession(app);
		_layers = session.getData();
		_stepper = HeadlessFixture.stepper(session);

		// ownship on the equator, heading east at 10 kts
		_ownship = HeadlessFixture.stationaryTrack("OWNSHIP", new WorldLocation(0, 0, 0), 90, 10, T0, 11);
		// contact exactly 1 nm due north of ownship, heading south at 5 kts
		_contact = HeadlessFixture.stationaryTrack("CONTACT",
				new WorldLocation(MWC.Algorithms.Conversions.Nm2Degs(1), 0, 0), 180, 5, T0, 11);
		_layers.addThisLayer(_ownship);
		_layers.addThisLayer(_contact);

		// as Lite does after loading data
		_tote = new LiteTote(_layers, _stepper);
		_tote.assignWatchables(true);

		_stepper.setStartTime(new HiResDate(T0));
		_stepper.setEndTime(new HiResDate(T0 + 10 * 60_000L));
		_stepper.setStepSmall(MINUTE_MICROS);
		_stepper.setStepLarge(5 * MINUTE_MICROS);
		_stepper.changeTime(new HiResDate(T0));
	}

	@Test
	public void testStepping() {
		assertEquals(T0 * 1000, _stepper.getCurrentTime().getMicros(), "starts at the period start");

		_stepper.doStep(true, false);
		assertEquals(T0 * 1000 + MINUTE_MICROS, _stepper.getCurrentTime().getMicros(), "small step forward: +1 min");

		_stepper.doStep(true, true);
		assertEquals(T0 * 1000 + 6 * MINUTE_MICROS, _stepper.getCurrentTime().getMicros(),
				"large step forward: +5 min");

		_stepper.doStep(false, false);
		assertEquals(T0 * 1000 + 5 * MINUTE_MICROS, _stepper.getCurrentTime().getMicros(), "small step back: -1 min");

		_stepper.doStep(true, true);
		_stepper.doStep(true, true);
		assertEquals(T0 * 1000 + 10 * MINUTE_MICROS, _stepper.getCurrentTime().getMicros(),
				"stepping past the end stops at the end");

		_stepper.doStep(false, true);
		_stepper.doStep(false, true);
		_stepper.doStep(false, true);
		assertEquals(T0 * 1000, _stepper.getCurrentTime().getMicros(), "stepping before the start stops at the start");
	}

	@Test
	public void testToteFollowsTheStepper() {
		_stepper.doStep(true, true);
		assertEquals(_stepper.getCurrentTime(), _tote.getCurrentTime(), "the tote hears the new time");
	}

	@Test
	public void testToteMembers() {
		assertSame(_ownship, _tote.getPrimary(), "first track is the primary");
		assertEquals(1, _tote.getSecondary().size(), "one secondary");
		assertSame(_contact, _tote.getSecondary().get(0), "the other track is the secondary");

		// Lite drops a tote member when its layer is removed
		_layers.removeThisLayer(_contact);
		assertTrue(_tote.getSecondary().isEmpty(), "secondary dropped with its layer");
		_layers.removeThisLayer(_ownship);
		assertNull(_tote.getPrimary(), "primary dropped with its layer");
	}

	@Test
	public void testToteValues() {
		_stepper.doStep(true, false);
		final HiResDate now = _stepper.getCurrentTime();
		final Watchable primary = at(_tote.getPrimary(), now);
		final Watchable secondary = at(_tote.getSecondary().get(0), now);

		// contact is 1 nm due north of ownship
		// Lite shows range in yards by default: 1 nm = 1852 m = 1852 / 0.9144 yds
		final rangeCalc range = new rangeCalc();
		assertEquals("yd", range.getUnits(), "range units");
		assertEquals(1852 / 0.9144, range.calculate(primary, secondary, now), 1e-3, "range (yds)");
		assertEquals(0, new bearingCalc().calculate(primary, secondary, now), 1e-6, "bearing to contact (degs)");

		// course and speed are the primary's own
		assertEquals(90, new courseCalc().calculate(primary, secondary, now), 1e-6, "ownship course (degs)");
		assertEquals(10, new speedCalc().calculate(primary, secondary, now), 1e-6, "ownship speed (kts)");

		// and from the contact's point of view: ownship bears 180, 1 nm
		assertEquals(180, new bearingCalc().calculate(secondary, primary, now), 1e-6,
				"bearing back to ownship (degs)");
		assertEquals(180, new courseCalc().calculate(secondary, primary, now), 1e-6, "contact course (degs)");
		assertEquals(5, new speedCalc().calculate(secondary, primary, now), 1e-6, "contact speed (kts)");
	}
}
