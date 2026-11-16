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

package org.mwc.debrief.lite.headless;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSliderUI;

import org.mwc.debrief.lite.custom.JRibbonSlider;

import org.junit.jupiter.api.Test;

/**
 * The ribbon sliders draw their own, more visible, thumb: it must sit where the
 * look and feel puts the real thumb, at every value.
 */
public class SliderThumbTest {

	private static JSlider slider(final int value) throws Exception {
		final JSlider slider = new JSlider(0, 1000, value);
		slider.setSize(400, 20);
		// the UI works out its track when the resize event is handled
		SwingUtilities.invokeAndWait(() -> {
			slider.getUI().getPreferredSize(slider);
		});
		SwingUtilities.invokeAndWait(() -> {
		});
		return slider;
	}

	/** where the look and feel itself maps x to the slider's value */
	private static void assertOnValue(final JSlider slider) {
		final int x = JRibbonSlider.thumbCentreX(slider);
		assertTrue(x > 0 && x < slider.getWidth(), "thumb inside the slider, got " + x);
		final BasicSliderUI ui = (BasicSliderUI) slider.getUI();
		assertEquals(slider.getValue(), ui.valueForXPosition(x), 3, "value under the drawn thumb");
	}

	@Test
	public void testThumbFollowsTheValue() throws Exception {
		final JSlider start = slider(0);
		final JSlider middle = slider(500);
		final JSlider end = slider(1000);
		assertOnValue(start);
		assertOnValue(middle);
		assertOnValue(end);

		final int left = JRibbonSlider.thumbCentreX(start);
		final int right = JRibbonSlider.thumbCentreX(end);
		// at the start the thumb is at the track's left end, not the slider's edge,
		// which is where every x before the track would put it
		assertTrue(left > 0, "start thumb is on the track, got " + left);
		assertTrue(left < JRibbonSlider.thumbCentreX(middle), "start is left of middle");
		assertTrue(JRibbonSlider.thumbCentreX(middle) < right, "middle is left of end");
		assertEquals((left + right) / 2.0, JRibbonSlider.thumbCentreX(middle), 1, "middle is half way");
	}

	@Test
	public void testTrackSpansTheRange() throws Exception {
		final JSlider slider = slider(300);
		final int[] ends = JRibbonSlider.trackEnds(slider);
		assertNotNull(ends);
		assertTrue(0 < ends[0] && ends[0] < ends[1]
				&& ends[1] < slider.getWidth(), "track runs left to right, got " + ends[0] + ".." + ends[1]);
		final BasicSliderUI ui = (BasicSliderUI) slider.getUI();
		assertEquals(slider.getMinimum(), ui.valueForXPosition(ends[0]), "left end is the minimum");
		assertEquals(slider.getMaximum(), ui.valueForXPosition(ends[1]), "right end is the maximum");
		final int x = JRibbonSlider.thumbCentreX(slider);
		assertTrue(ends[0] <= x && x <= ends[1], "thumb is on the track");
	}

	@Test
	public void testNothingDrawnWithoutARange() throws Exception {
		final JSlider empty = new JSlider(5, 5, 5);
		empty.setSize(400, 20);
		assertEquals(-1, JRibbonSlider.thumbCentreX(empty));
		assertNull(JRibbonSlider.trackEnds(empty));
		final JSlider vertical = new JSlider(JSlider.VERTICAL, 0, 10, 5);
		vertical.setSize(20, 400);
		assertEquals(-1, JRibbonSlider.thumbCentreX(vertical));
	}
}
