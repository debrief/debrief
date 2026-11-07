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
import junit.framework.TestCase;

/**
 * Stepping through time with Lite's step control, and the tote's range,
 * bearing, course and speed against hand-calculated values.
 */
public class TimeAndToteTest extends TestCase {

	/** 1 Jan 2026 00:00 UTC */
	private static final long T0 = 1767225600000L;
	private static final long MINUTE_MICROS = 60_000_000L;

	/** the tote reads the primary and secondary at this time */
	private static Watchable at(final WatchableList list, final HiResDate time) {
		final Watchable[] found = list.getNearestTo(time);
		assertTrue(list.getName() + " has a point at " + time, found != null && found.length > 0);
		return found[0];
	}

	private LiteStepControl _stepper;
	private LiteTote _tote;
	private Layers _layers;
	private TrackWrapper _ownship;
	private TrackWrapper _contact;

	@Override
	protected void setUp() {
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

	public void testStepping() {
		assertEquals("starts at the period start", T0 * 1000, _stepper.getCurrentTime().getMicros());

		_stepper.doStep(true, false);
		assertEquals("small step forward: +1 min", T0 * 1000 + MINUTE_MICROS, _stepper.getCurrentTime().getMicros());

		_stepper.doStep(true, true);
		assertEquals("large step forward: +5 min", T0 * 1000 + 6 * MINUTE_MICROS,
				_stepper.getCurrentTime().getMicros());

		_stepper.doStep(false, false);
		assertEquals("small step back: -1 min", T0 * 1000 + 5 * MINUTE_MICROS, _stepper.getCurrentTime().getMicros());

		_stepper.doStep(true, true);
		_stepper.doStep(true, true);
		assertEquals("stepping past the end stops at the end", T0 * 1000 + 10 * MINUTE_MICROS,
				_stepper.getCurrentTime().getMicros());

		_stepper.doStep(false, true);
		_stepper.doStep(false, true);
		_stepper.doStep(false, true);
		assertEquals("stepping before the start stops at the start", T0 * 1000, _stepper.getCurrentTime().getMicros());
	}

	public void testToteFollowsTheStepper() {
		_stepper.doStep(true, true);
		assertEquals("the tote hears the new time", _stepper.getCurrentTime(), _tote.getCurrentTime());
	}

	public void testToteMembers() {
		assertSame("first track is the primary", _ownship, _tote.getPrimary());
		assertEquals("one secondary", 1, _tote.getSecondary().size());
		assertSame("the other track is the secondary", _contact, _tote.getSecondary().get(0));

		// Lite drops a tote member when its layer is removed
		_layers.removeThisLayer(_contact);
		assertTrue("secondary dropped with its layer", _tote.getSecondary().isEmpty());
		_layers.removeThisLayer(_ownship);
		assertNull("primary dropped with its layer", _tote.getPrimary());
	}

	public void testToteValues() {
		_stepper.doStep(true, false);
		final HiResDate now = _stepper.getCurrentTime();
		final Watchable primary = at(_tote.getPrimary(), now);
		final Watchable secondary = at(_tote.getSecondary().get(0), now);

		// contact is 1 nm due north of ownship
		// Lite shows range in yards by default: 1 nm = 1852 m = 1852 / 0.9144 yds
		final rangeCalc range = new rangeCalc();
		assertEquals("range units", "yd", range.getUnits());
		assertEquals("range (yds)", 1852 / 0.9144, range.calculate(primary, secondary, now), 1e-3);
		assertEquals("bearing to contact (degs)", 0, new bearingCalc().calculate(primary, secondary, now), 1e-6);

		// course and speed are the primary's own
		assertEquals("ownship course (degs)", 90, new courseCalc().calculate(primary, secondary, now), 1e-6);
		assertEquals("ownship speed (kts)", 10, new speedCalc().calculate(primary, secondary, now), 1e-6);

		// and from the contact's point of view: ownship bears 180, 1 nm
		assertEquals("bearing back to ownship (degs)", 180, new bearingCalc().calculate(secondary, primary, now),
				1e-6);
		assertEquals("contact course (degs)", 180, new courseCalc().calculate(secondary, primary, now), 1e-6);
		assertEquals("contact speed (kts)", 5, new speedCalc().calculate(secondary, primary, now), 1e-6);
	}
}
