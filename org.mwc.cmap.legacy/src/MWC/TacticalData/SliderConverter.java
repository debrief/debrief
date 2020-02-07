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
package MWC.TacticalData;

import junit.framework.TestCase;

/**
 * utility class to handle converting between slider range and time values
 *
 * @author ian
 *
 */
public class SliderConverter {
	public static class SliderConverterTest extends TestCase {
		public void testConverter() {
			final SliderConverter test = new SliderConverter();
			test.init(1240423198490L, 1240427422390L);

			final int originalStep = 21;
			final long originalTime = test.getTimeAt(originalStep);
			final long roundedTime = originalTime / 1000L * 1000L;
			final int newStep = test.getCurrentAt(roundedTime);
			assertEquals("Rounding slider converter", originalStep, newStep);
		}

		public void testOverflow() {
			final SliderConverter test = new SliderConverter();
			test.init(0L, 1240427422390L);

			final int position = 20006218;
			final long time = test.getTimeAt(position);
			assertTrue("Slider Converter Overflow in getTimeAt calculation", time > 0);

		}
	}

	private int range;
	private long origin;

	// have one second steps
	private final long step = 1000;

	public int getCurrentAt(final long now) {
		return (int) Math.round((double) (now - origin) / step);
	}

	public int getEnd() {
		return range;
	}

	public int getStart() {
		return 0;
	}

	public long getTimeAt(final int position) {
		return origin + (position * step);
	}

	public void init(final long start, final long end) {
		origin = start;
		range = (int) ((end - start) / step);
	}
}
