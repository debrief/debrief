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
package org.mwc.debrief.lite.gui.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;

/** RangeSlider's conversions between a date and a slider value (whole seconds). */
public class RangeSliderTest {

	@Test
	public void testConvert() {
		final Calendar date = new GregorianCalendar();
		final int millis = (int) (date.getTimeInMillis() / 1000L);
		final Calendar newD = RangeSlider.toDate(millis);
		final int sliderVal = RangeSlider.toInt(date);
		final Calendar newD2 = RangeSlider.toDate(sliderVal);
		assertEquals(millis, sliderVal, "conversion works");
		assertEquals(newD, newD2, "conversion works");
	}
}
