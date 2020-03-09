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

public class TestCenteredMovingAvg {

	public static void main(final String[] args) {

		final double[] testData = { 1, 2, 3, 4, 5, 5, 4, 3, 2, 1 };

		final int[] windowSizes = { 3, 5 };

		for (final int windSize : windowSizes) {

			final CenteredMovingAverage cma = new CenteredMovingAverage(windSize);

			for (int n = 0; n < testData.length; n++) {
				final double avg = cma.average(n, testData);
				final String msg = String.format("The centered moving average with period %d and n %d is %f",
						cma.getPeriod(), n, avg);
				System.out.println(msg);
			}
			System.out.println();
		}
	}
}
