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

import static org.junit.jupiter.api.Assertions.*;

import static MWC.TacticalData.SliderConverter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import MWC.TacticalData.SliderConverter.*;
import org.junit.jupiter.api.Test;

public class SliderConverterTest {
	@Test
	public void testConverter() {
		final SliderConverter test = new SliderConverter();
		test.init(1240423198490L, 1240427422390L);

		final int originalStep = 21;
		final long originalTime = test.getTimeAt(originalStep);
		final long roundedTime = originalTime / 1000L * 1000L;
		final int newStep = test.getCurrentAt(roundedTime);
		assertEquals(originalStep, newStep, "Rounding slider converter");
	}

	@Test
	public void testOverflow() {
		final SliderConverter test = new SliderConverter();
		test.init(0L, 1240427422390L);

		final int position = 20006218;
		final long time = test.getTimeAt(position);
		assertTrue(time > 0, "Slider Converter Overflow in getTimeAt calculation");

	}
}
