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

import java.util.Iterator;
import java.util.List;

public class Steady_course {

	// =========================================================================
	public static void main(final String[] args) {

		try {
			final SCFileReader myreader = new SCFileReader();
			final List<Tote> totes = myreader.process("OtherOwnship_Trimmed.txt");

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

			// Trace the results
			// System.out.println("There are " + (any list from above).size() + " steady
			// course intervals.");
			// for(int ii=0; ii<(any list from above).size(); ii++) {
			// int index_start = (any list from above).get(ii).first;
			// int index_end = (any list from above).get(ii).second;
			// int numelements = index_end - index_start;

			// double[] timesreg = SCStatistics.getRelativeTimes(totes, index_start,
			// index_end);
			// double delapsedtime = timesreg[timesreg.length - 1] - timesreg[0];

			// System.out.println("index: " + index_start + " - " + index_end +
			// " numelements = " + numelements +
			// " elapsed time = " + delapsedtime);
			// }
		}

		catch (final Exception e) {
			System.out.println(e);
		}
	}

	// -------------------------------------------------------------------------
	static void printIntervals(final List<SCAlgorithms.SpanPair> intervals,
			final List<Tote> totes) /* throws IOException */ {
		final Iterator<SCAlgorithms.SpanPair> iter = intervals.iterator();
		while (iter.hasNext()) {
			final SCAlgorithms.SpanPair item = iter.next();

			// times in hhmm.ss format
			final double dstart_time = 0.01 * Double.parseDouble(totes.get(item.first).stime);
			final double dend_time = 0.01 * Double.parseDouble(totes.get(item.second - 1).stime);
			final String sstart_time = String.format("%07.2f", dstart_time);
			final String send_time = String.format("%07.2f", dend_time);

			System.out.println(String.format("%5d", item.first) + " " + String.format("%5d", item.second) + "  ("
					+ sstart_time + " - " + send_time + ")");
		}
	}
}
