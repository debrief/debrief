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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.map.MouseWheelHandler;

import MWC.GenericData.HiResDate;
import junit.framework.TestCase;

/**
 * The mouse wheel over the map: stepping the time, and the area a ctrl-wheel
 * zoom shows.
 */
public class MouseWheelTest extends TestCase {

	/** 1 Jan 2026 00:00 UTC */
	private static final long T0 = 1767225600000L;
	private static final long MINUTE_MICROS = 60_000_000L;

	private LiteStepControl _stepper;
	private MouseWheelHandler _handler;

	@Override
	protected void setUp() {
		_stepper = HeadlessFixture.stepper(HeadlessFixture.newSession(HeadlessFixture.newApp()));
		_stepper.setStartTime(new HiResDate(T0));
		_stepper.setEndTime(new HiResDate(T0 + 10 * 60_000L));
		_stepper.setStepSmall(MINUTE_MICROS);
		_stepper.setStepLarge(5 * MINUTE_MICROS);
		_stepper.changeTime(new HiResDate(T0));
		_handler = new MouseWheelHandler(_stepper, null);
	}

	public void testWheelStepsTime() {
		_handler.stepTime(1);
		assertEquals("one notch towards the user: +1 small step", T0 * 1000 + MINUTE_MICROS,
				_stepper.getCurrentTime().getMicros());

		_handler.stepTime(3);
		assertEquals("three notches: +3 small steps", T0 * 1000 + 4 * MINUTE_MICROS,
				_stepper.getCurrentTime().getMicros());

		_handler.stepTime(-2);
		assertEquals("two notches away: -2 small steps", T0 * 1000 + 2 * MINUTE_MICROS,
				_stepper.getCurrentTime().getMicros());

		_handler.stepTime(-5);
		assertEquals("stops at the start", T0 * 1000, _stepper.getCurrentTime().getMicros());

		_handler.stepTime(20);
		assertEquals("stops at the end", T0 * 1000 + 10 * MINUTE_MICROS, _stepper.getCurrentTime().getMicros());
	}

	public void testZoomDirection() {
		assertTrue("rolling away zooms in", MouseWheelHandler.zoomFactor(-1) < 1);
		assertTrue("rolling towards zooms out", MouseWheelHandler.zoomFactor(1) > 1);
		assertEquals("in then out is back where we started", 1d,
				MouseWheelHandler.zoomFactor(-2) * MouseWheelHandler.zoomFactor(2), 1e-12);
	}

	public void testZoomKeepsTheCursorInPlace() {
		final Rectangle2D area = new Rectangle2D.Double(10, 20, 100, 50);
		// a quarter of the way across, four fifths of the way up
		final Point2D anchor = new Point2D.Double(35, 60);

		final Rectangle2D in = MouseWheelHandler.zoomedArea(area, anchor, 0.5);
		assertEquals("half the width", 50, in.getWidth(), 1e-9);
		assertEquals("half the height", 25, in.getHeight(), 1e-9);
		assertEquals("anchor still a quarter of the way across", 0.25, (anchor.getX() - in.getX()) / in.getWidth(),
				1e-9);
		assertEquals("anchor still four fifths of the way up", 0.8, (anchor.getY() - in.getY()) / in.getHeight(),
				1e-9);

		final Rectangle2D back = MouseWheelHandler.zoomedArea(in, anchor, 2);
		assertEquals(area.getX(), back.getX(), 1e-9);
		assertEquals(area.getY(), back.getY(), 1e-9);
		assertEquals(area.getWidth(), back.getWidth(), 1e-9);
		assertEquals(area.getHeight(), back.getHeight(), 1e-9);
	}
}
