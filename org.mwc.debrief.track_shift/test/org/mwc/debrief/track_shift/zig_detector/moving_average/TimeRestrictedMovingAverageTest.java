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

package org.mwc.debrief.track_shift.zig_detector.moving_average;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mwc.debrief.track_shift.zig_detector.moving_average.TimeRestrictedMovingAverage.*;
import org.mwc.debrief.track_shift.zig_detector.moving_average.TimeRestrictedMovingAverage.*;
import java.util.List;
import java.util.Vector;
import org.junit.jupiter.api.Test;

public class TimeRestrictedMovingAverageTest {
	@Test
	public void testAverage() {
		final TimeRestrictedMovingAverage avg = new TimeRestrictedMovingAverage(1000L, 10);

		avg.add(100, 10);
		assertEquals(10d, avg.getAverage(), 0.0001, "correct average");

		avg.add(200, 20);
		assertEquals(15d, avg.getAverage(), 0.0001, "correct average");

		avg.add(500, 30);
		assertEquals(20d, avg.getAverage(), 0.0001, "correct average");

		// check we're storing all items
		assertEquals(3, avg.size(), "holding all items");

		assertEquals(400, avg.period(), "correctly calculating period");

		avg.add(800, 60);
		assertEquals(30d, avg.getAverage(), 0.0001, "correct average");

		avg.add(1000, 10);
		assertEquals(26d, avg.getAverage(), 0.0001, "correct average");

		// check we're storing all items
		assertEquals(5, avg.size(), "holding all items");

		assertEquals(900, avg.period(), "correctly calculating period");

		avg.add(1100, 20);
		assertEquals(6, avg.size(), "holding all items");
		assertEquals(25d, avg.getAverage(), 0.0001, "correct average");

		assertEquals(1000, avg.period(), "correctly calculating period");

		avg.add(1200, 40);
		assertEquals(6, avg.size(), "holding all items");
		assertEquals(34d, avg.getAverage(), 0.0001, "correct average");

		assertEquals(1000, avg.period(), "correctly calculating period");
	}

	@Test
	public void testReverse() {
		final TimeRestrictedMovingAverage avg = new TimeRestrictedMovingAverage(300L, 10);

		avg.add(1000, 10);
		assertEquals(10d, avg.getAverage(), 0.0001, "correct average");

		avg.add(900, 20);
		assertEquals(15d, avg.getAverage(), 0.0001, "correct average");

		avg.add(800, 30);
		assertEquals(20d, avg.getAverage(), 0.0001, "correct average");
		assertFalse(avg.isPopulated());

		avg.add(700, 40);
		assertFalse(avg.isPopulated());
		assertEquals(25d, avg.getAverage(), 0.0001, "correct average");

		avg.add(600, 50);
		assertEquals(35d, avg.getAverage(), 0.0001, "correct average");
		assertFalse(avg.isPopulated());
	}
}
