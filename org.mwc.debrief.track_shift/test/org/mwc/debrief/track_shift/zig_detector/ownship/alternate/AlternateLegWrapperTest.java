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

package org.mwc.debrief.track_shift.zig_detector.ownship.alternate;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.debrief.track_shift.zig_detector.ownship.alternate.AlternateLegWrapper.*;
import org.mwc.debrief.track_shift.zig_detector.ownship.alternate.AlternateLegWrapper.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.mwc.debrief.track_shift.zig_detector.ownship.alternate.SCAlgorithms.SpanPair;
import MWC.Algorithms.Conversions;
import org.junit.jupiter.api.Test;

public final class AlternateLegWrapperTest {

	@Test
	public void testCollateUnwrapsBothWays() {
		final AlternateLegWrapper wrapper = new AlternateLegWrapper();
		final long[] times = new long[] { 0, 1000, 2000, 3000, 4000 };
		final double[] speeds = new double[] { 5, 5, 5, 5, 5 };

		// steering either side of north, starting east of it
		final List<Tote> res = wrapper.collateData(times, speeds, new double[] { 5, 355, 5, 350, 2 });
		final double[] expected = new double[] { 5, -5, 5, -10, 2 };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], res.get(i).dheading, 0.0001, "unwrapped heading " + i);
		}

		// and starting west of it
		final List<Tote> res2 = wrapper.collateData(times, speeds, new double[] { 355, 5, 355, 10, 358 });
		final double[] expected2 = new double[] { 355, 365, 355, 370, 358 };
		for (int i = 0; i < expected2.length; i++) {
			assertEquals(expected2[i], res2.get(i).dheading, 0.0001, "unwrapped heading " + i);
		}
	}

	@Test
	public void testSteadyLegThroughNorth() {
		final int len = 40;
		final long[] times = new long[len];
		final double[] speeds = new double[len];
		final double[] courses = new double[len];
		final double[] jitter = new double[] { 2, 358, 1, 359 };
		for (int i = 0; i < len; i++) {
			times[i] = 1000000L + i * 60000L;
			speeds[i] = 6;
			courses[i] = jitter[i % jitter.length];
		}
		final List<LegOfData> legs = new AlternateLegWrapper().identifyOwnshipLegs(times, speeds, courses, 5,
				Precision.LOW);
		assertEquals(1, legs.size(), "single steady leg");
		assertEquals(times[0], (long) legs.get(0).getStart(), "leg includes first fix");
	}
}
