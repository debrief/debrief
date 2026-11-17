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

package org.mwc.debrief.track_shift.zig_detector.ownship;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.track_shift.zig_detector.ownship.PeakTrackingOwnshipLegDetector.*;
import org.mwc.debrief.track_shift.zig_detector.ownship.PeakTrackingOwnshipLegDetector.*;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.mwc.debrief.track_shift.TrackShiftActivator;
import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.junit.jupiter.api.Test;

public class PeakTrackingOwnshipLegDetectorTest {
	private TimeSeries getOSValues() {
		final TimeSeries tmpSeries = new TimeSeries("Set piece");
		tmpSeries.add(new FixedMillisecond(946697490000L), 60.20000000000001);
		tmpSeries.add(new FixedMillisecond(946697520000L), 64.4);
		tmpSeries.add(new FixedMillisecond(946697550000L), 97.2);
		tmpSeries.add(new FixedMillisecond(946697580000L), 106.1);
		tmpSeries.add(new FixedMillisecond(946697610000L), 105.2);
		tmpSeries.add(new FixedMillisecond(946697640000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946697670000L), 104.9);
		tmpSeries.add(new FixedMillisecond(946697700000L), 105.2);
		tmpSeries.add(new FixedMillisecond(946697730000L), 105.4);
		tmpSeries.add(new FixedMillisecond(946697760000L), 105.2);
		tmpSeries.add(new FixedMillisecond(946697790000L), 105.2);
		tmpSeries.add(new FixedMillisecond(946697820000L), 105.0);
		tmpSeries.add(new FixedMillisecond(946697850000L), 105.3);
		tmpSeries.add(new FixedMillisecond(946697880000L), 105.3);
		tmpSeries.add(new FixedMillisecond(946697910000L), 105.2);
		tmpSeries.add(new FixedMillisecond(946697940000L), 105.3);
		tmpSeries.add(new FixedMillisecond(946697970000L), 105.2);
		tmpSeries.add(new FixedMillisecond(946698000000L), 105.4);
		tmpSeries.add(new FixedMillisecond(946698030000L), 105.4);
		tmpSeries.add(new FixedMillisecond(946698060000L), 104.7);
		tmpSeries.add(new FixedMillisecond(946698090000L), 105.5);
		tmpSeries.add(new FixedMillisecond(946698120000L), 105.4);
		tmpSeries.add(new FixedMillisecond(946698150000L), 104.9);
		tmpSeries.add(new FixedMillisecond(946698180000L), 105.4);
		tmpSeries.add(new FixedMillisecond(946698210000L), 105.4);
		tmpSeries.add(new FixedMillisecond(946698240000L), 104.8);
		tmpSeries.add(new FixedMillisecond(946698270000L), 105.7);
		tmpSeries.add(new FixedMillisecond(946698300000L), 105.2);
		tmpSeries.add(new FixedMillisecond(946698330000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946698360000L), 105.3);
		tmpSeries.add(new FixedMillisecond(946698390000L), 105.5);
		tmpSeries.add(new FixedMillisecond(946698420000L), 104.8);
		tmpSeries.add(new FixedMillisecond(946698450000L), 105.3);
		tmpSeries.add(new FixedMillisecond(946698480000L), 105.3);
		tmpSeries.add(new FixedMillisecond(946698510000L), 105.2);
		tmpSeries.add(new FixedMillisecond(946698540000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946698570000L), 105.5);
		tmpSeries.add(new FixedMillisecond(946698600000L), 105.6);
		tmpSeries.add(new FixedMillisecond(946698630000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946698660000L), 105.4);
		tmpSeries.add(new FixedMillisecond(946698690000L), 105.0);
		tmpSeries.add(new FixedMillisecond(946698720000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946698750000L), 105.4);
		tmpSeries.add(new FixedMillisecond(946698780000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946698810000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946698840000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946698870000L), 105.3);
		tmpSeries.add(new FixedMillisecond(946698900000L), 105.1);
		tmpSeries.add(new FixedMillisecond(946698930000L), 105.0);
		tmpSeries.add(new FixedMillisecond(946698960000L), 105.3);
		tmpSeries.add(new FixedMillisecond(946698990000L), 105.0);
		tmpSeries.add(new FixedMillisecond(946699020000L), 107.6);
		tmpSeries.add(new FixedMillisecond(946699050000L), 128.9);
		tmpSeries.add(new FixedMillisecond(946699080000L), 133.6);
		tmpSeries.add(new FixedMillisecond(946699110000L), 135.6);
		tmpSeries.add(new FixedMillisecond(946699140000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946699170000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946699200000L), 135.6);
		tmpSeries.add(new FixedMillisecond(946699230000L), 134.8);
		tmpSeries.add(new FixedMillisecond(946699260000L), 135.6);
		tmpSeries.add(new FixedMillisecond(946699290000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946699320000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946699350000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946699380000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946699410000L), 135.0);
		tmpSeries.add(new FixedMillisecond(946699440000L), 135.1);
		tmpSeries.add(new FixedMillisecond(946699470000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946699500000L), 135.0);
		tmpSeries.add(new FixedMillisecond(946699530000L), 135.0);
		tmpSeries.add(new FixedMillisecond(946699560000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946699590000L), 134.9);
		tmpSeries.add(new FixedMillisecond(946699620000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946699650000L), 135.7);
		tmpSeries.add(new FixedMillisecond(946699680000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946699710000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946699740000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946699770000L), 135.5);
		tmpSeries.add(new FixedMillisecond(946699800000L), 135.1);
		tmpSeries.add(new FixedMillisecond(946699830000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946699860000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946699890000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946699920000L), 135.7);
		tmpSeries.add(new FixedMillisecond(946699950000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946699980000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946700010000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946700040000L), 135.0);
		tmpSeries.add(new FixedMillisecond(946700070000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946700100000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946700130000L), 135.5);
		tmpSeries.add(new FixedMillisecond(946700160000L), 135.0);
		tmpSeries.add(new FixedMillisecond(946700190000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946700220000L), 135.6);
		tmpSeries.add(new FixedMillisecond(946700250000L), 134.9);
		tmpSeries.add(new FixedMillisecond(946700280000L), 135.1);
		tmpSeries.add(new FixedMillisecond(946700310000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946700340000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946700370000L), 135.6);
		tmpSeries.add(new FixedMillisecond(946700400000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946700430000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946700460000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946700490000L), 135.1);
		tmpSeries.add(new FixedMillisecond(946700520000L), 135.1);
		tmpSeries.add(new FixedMillisecond(946700550000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946700580000L), 135.1);
		tmpSeries.add(new FixedMillisecond(946700610000L), 134.8);
		tmpSeries.add(new FixedMillisecond(946700640000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946700670000L), 135.5);
		tmpSeries.add(new FixedMillisecond(946700700000L), 135.3);
		tmpSeries.add(new FixedMillisecond(946700730000L), 135.5);
		tmpSeries.add(new FixedMillisecond(946700760000L), 135.4);
		tmpSeries.add(new FixedMillisecond(946700790000L), 135.1);
		tmpSeries.add(new FixedMillisecond(946700820000L), 135.5);
		tmpSeries.add(new FixedMillisecond(946700850000L), 135.2);
		tmpSeries.add(new FixedMillisecond(946700880000L), 135.1);
		tmpSeries.add(new FixedMillisecond(946700910000L), 135.0);
		tmpSeries.add(new FixedMillisecond(946700940000L), 136.0);
		tmpSeries.add(new FixedMillisecond(946700970000L), 136.7);
		return tmpSeries;
	}

	@Test
	public void testContinuous() {
		final double[] test = new double[] { 160, 170, 175, 180, -175, -200, -175, 180, 170, 150, 130, 110, 70, 20,
				5, 355, 335 };
		final double[] res = PeakTrackingOwnshipLegDetector.makeContinuous(test);
		assertEquals(185d, res[4], "correct side");
		assertEquals(-5d, res[15], "correct side");
		System.out.println(res);
	}

	@Test
	public void testIsHalfLeg() {
		final double[] legIds1 = new double[] { 0.5, 0.5, 0.5, 1d, 1d, 1d, 1.5, 1.5 };
		final double[] legIds2 = new double[] { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 };
		final double[] courseDeltas1 = new double[] { 6, 6, 6, 1, 1, 1, 5, 5, 5 };
		final double[] courseDeltas2 = new double[] { 1, 1, 1, 5, 5, 5, 1, 1, 1 };

		System.out.println("started");

		assertFalse(PeakTrackingOwnshipLegDetector.isHalfLeg(legIds1, courseDeltas1), "right answer");
		assertTrue(PeakTrackingOwnshipLegDetector.isHalfLeg(legIds1, courseDeltas2), "right answer");
		assertTrue(PeakTrackingOwnshipLegDetector.isHalfLeg(legIds2, courseDeltas2), "right answer");
	}

	@Test
	public void testSetPiece() {
		final TimeSeries tmpSeries = getOSValues();

		final PeakTrackingOwnshipLegDetector detector = new PeakTrackingOwnshipLegDetector();
		final int len = tmpSeries.getItemCount();
		final long[] times = new long[len];
		final double[] speeds = new double[len];
		final double[] courses = new double[len];

		// long start = tmpSeries.getDataItem(0).getPeriod().getFirstMillisecond();

		for (int i = 0; i < len; i++) {
			times[i] = tmpSeries.getDataItem(i).getPeriod().getFirstMillisecond();
			speeds[i] = 0d;
			courses[i] = (Double) tmpSeries.getDataItem(i).getValue();
		}

		final List<LegOfData> legs = detector.identifyOwnshipLegs(times, speeds, courses, 5, Precision.LOW);

		assertEquals(2, legs.size(), "got right num legs:");
		assertEquals(946697580000L, legs.get(0).tStart, "got leg 1 start time right");
		assertEquals(946699020000L, legs.get(0).tEnd, "got leg 1 end time right");
		assertEquals(946699110000L, legs.get(1).tStart, "got leg 2 start time right");
		assertEquals(946700970000L, legs.get(1).tEnd, "got leg 2 end time right");
	}
}
