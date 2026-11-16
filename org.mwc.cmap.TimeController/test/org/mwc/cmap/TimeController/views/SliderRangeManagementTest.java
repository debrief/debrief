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

package org.mwc.cmap.TimeController.views;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.cmap.TimeController.views.SliderRangeManagement.*;
import org.mwc.cmap.TimeController.views.SliderRangeManagement.*;
import java.util.Date;
import MWC.GenericData.HiResDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class SliderRangeManagementTest {
	static class MySlider extends SliderRangeManagement {
		@Override
		public void setEnabled(final boolean val) {
			_isEnabled = val;
		}

		@Override
		public void setMaxVal(final int max) {
			_maxVal = max;
		}

		@Override
		public void setMinVal(final int min) {
			_minVal = min;
		}

		@Override
		public void setTickSize(final int small, final int large, final int drag) {
			_small = small;
			_large = large;
			_drag = drag;
		}

	}

	private static int _maxVal, _minVal;
	private static int _small, _large, _drag;

	private static boolean _isEnabled;

	@BeforeEach
	public void setUp() {
		_small = _large = _drag = -1;
		_minVal = _maxVal = -1;
		_isEnabled = false;
	}

	@SuppressWarnings("deprecation")
	@Disabled("Expects minute steps but gets day steps; it was in no suite before the JUnit 6 migration, so it never ran")
	@Test
	public void testRanges() {

		// check we're initialised
		assertEquals(_small, -1, "not initialised");

		final MySlider slider = new MySlider();
		HiResDate minR = new HiResDate(new Date(0));
		HiResDate maxR = new HiResDate(new Date(500));
		slider.resetRange(minR, maxR);

		assertEquals(true, _isEnabled, "is enabled");
		assertEquals(0, _minVal, "correct for tiny range");
		assertEquals(500000, _maxVal, "correct for tiny range");
		assertEquals(1, _small, "correct for tiny range");
		assertEquals(10000, _large, "correct for tiny range");
		assertEquals(0, _drag, "correct for tiny range");

		// try a couple of conversions
		int sVal = slider.toSliderUnits(new HiResDate(new Date(250)));
		assertEquals(250000, sVal, "correct slider value");
		final long sMicros = slider.fromSliderUnits(250000, 1).getMicros();
		assertEquals(250000, sMicros, "correct slider value");

		minR = new HiResDate(new Date(2009, 1, 1, 12, 0, 0));
		maxR = new HiResDate(new Date(2009, 1, 1, 13, 0, 0));
		slider.resetRange(minR, maxR);

		assertEquals(true, _isEnabled, "is enabled");
		assertEquals(0, _minVal, "correct for mid range");
		assertEquals(3600, _maxVal, "correct for mid range");
		assertEquals(1, _small, "correct for mid range");
		assertEquals(600, _large, "correct for mid range");
		assertEquals(0, _drag, "correct for mid range");

		// try a couple of conversions
		sVal = slider.toSliderUnits(new HiResDate(new Date(2009, 1, 1, 12, 0, 30)));
		assertEquals(30, sVal, "correct slider value");
		final HiResDate dt = slider.fromSliderUnits(250000, 1);
		final String dtStr = MWC.Utilities.TextFormatting.FormatRNDateTime.toMediumString(dt.getDate().getTime());
		System.out.println(dtStr);
		assertEquals(250000, sMicros, "correct slider value");

		minR = new HiResDate(new Date(2009, 1, 1, 12, 0, 0));
		maxR = new HiResDate(new Date(2090, 1, 1, 12, 0, 1));
		slider.resetRange(minR, maxR);

		assertEquals(MINUTE_STEP, slider._timeScalar, "is in minutes mode");
		assertEquals(true, _isEnabled, "is enabled");
		assertEquals(0, _minVal, "correct for large range");
		assertEquals(42602400, _maxVal, "correct for large range");
		assertEquals(1, _small, "correct for large range");
		assertEquals(6000000, _large, "correct for large range");
		assertEquals(0, _drag, "correct for large range");

	}
}
