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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;
import org.mwc.debrief.track_shift.zig_detector.ownship.LegOfData;
import org.mwc.debrief.track_shift.zig_detector.ownship.alternate.SCAlgorithms.SpanPair;

import MWC.Algorithms.Conversions;

public class AlternateLegWrapper implements IOwnshipLegDetector {

	static public final class TestUnwrap extends junit.framework.TestCase {

		public void testCollateUnwrapsBothWays() {
			final AlternateLegWrapper wrapper = new AlternateLegWrapper();
			final long[] times = new long[] { 0, 1000, 2000, 3000, 4000 };
			final double[] speeds = new double[] { 5, 5, 5, 5, 5 };

			// steering either side of north, starting east of it
			final List<Tote> res = wrapper.collateData(times, speeds, new double[] { 5, 355, 5, 350, 2 });
			final double[] expected = new double[] { 5, -5, 5, -10, 2 };
			for (int i = 0; i < expected.length; i++) {
				assertEquals("unwrapped heading " + i, expected[i], res.get(i).dheading, 0.0001);
			}

			// and starting west of it
			final List<Tote> res2 = wrapper.collateData(times, speeds, new double[] { 355, 5, 355, 10, 358 });
			final double[] expected2 = new double[] { 355, 365, 355, 370, 358 };
			for (int i = 0; i < expected2.length; i++) {
				assertEquals("unwrapped heading " + i, expected2[i], res2.get(i).dheading, 0.0001);
			}
		}

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
			assertEquals("single steady leg", 1, legs.size());
			assertEquals("leg includes first fix", times[0], (long) legs.get(0).getStart());
		}
	}

	// -------------------------------------------------------------------------
	static private void printIntervals(final List<SCAlgorithms.SpanPair> intervals, final List<Tote> totes)
	/* throws IOException */ {
		final Iterator<SCAlgorithms.SpanPair> iter = intervals.iterator();
		while (iter.hasNext()) {
			final SCAlgorithms.SpanPair item = iter.next();

			// times in hhmm.ss format
			final double dstart_time = totes.get(item.first).dabsolute_time;
			final double dend_time = 0.01 * totes.get(item.second - 1).dabsolute_time;
			final String sstart_time = String.format("%07.2f", dstart_time);
			final String send_time = String.format("%07.2f", dend_time);

			System.out.println(String.format("%5d", item.first) + " " + String.format("%5d", item.second) + "  ("
					+ sstart_time + " - " + send_time + ")");
		}
	}

	private List<Tote> collateData(final long[] times, final double[] rawSpeeds, final double[] rawCourses) {
		final List<Tote> res = new ArrayList<Tote>();
		final int len = times.length;
		Double previousHeading = null;
		for (int i = 0; i < len; i++) {
			final Tote it = new Tote();
			final long time = times[i];
			it.dabsolute_time = time / 1000d;
			it.dspeed = rawSpeeds[i];
			double heading = rawCourses[i];

			if (previousHeading != null) {
				// unwrap the course, so it is continuous with the previous
				// (unwrapped) value, taking the shortest turn
				heading = previousHeading + Conversions.degsDifference(previousHeading, heading);
			}
			it.dheading = heading;

			previousHeading = it.dheading;

			res.add(it);
		}
		return res;
	}

	@Override
	public List<LegOfData> identifyOwnshipLegs(final long[] times, final double[] rawSpeeds, final double[] rawCourses,
			final int minsOfAverage, final Precision precision) {
		// collate the input data
		final List<Tote> totes = collateData(times, rawSpeeds, rawCourses);

		final double mintime = 300.0; // 5min = 300sec

		// Finding out steady-course periods
		final List<SCAlgorithms.SpanPair> course0_intervals = SCAlgorithms.extractSteadyHeadings(totes, mintime);
		System.out.println("\nSteady course intervals:");
		printIntervals(course0_intervals, totes);

		// Finding out steady-speed periods
		final List<SCAlgorithms.SpanPair> speed0_intervals = SCAlgorithms.extractSteadySpeeds(totes, mintime);
		System.out.println("\nSteady speed intervals:");
		printIntervals(speed0_intervals, totes);

		// Combine them
		final List<SCAlgorithms.SpanPair> steady_CourseAndSpeed_intervals = SCAlgorithms
				.intersectLists(course0_intervals, speed0_intervals);
		System.out.println("\nSteady course-speed combined intervals:");
		printIntervals(steady_CourseAndSpeed_intervals, totes);

		// wrap the results
		final List<LegOfData> res = wrapResults(steady_CourseAndSpeed_intervals, totes);
		return res;
	}

	private List<LegOfData> wrapResults(final List<SpanPair> steady_CourseAndSpeed_intervals, final List<Tote> totes) {
		final List<LegOfData> res = new ArrayList<LegOfData>();

		for (final SpanPair leg : steady_CourseAndSpeed_intervals) {
			final long legStart = (long) (totes.get(leg.first).dabsolute_time * 1000);
			final long legEnd = (long) (totes.get(leg.second - 1).dabsolute_time * 1000);
			final LegOfData newL = new LegOfData("Leg:" + res.size() + 1, legStart, legEnd);
			res.add(newL);
		}

		return res;
	}
}
