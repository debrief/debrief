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

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSliderUI;

import org.mwc.debrief.lite.custom.JRibbonSlider;

import junit.framework.TestCase;

/**
 * The ribbon sliders draw their own, more visible, thumb: it must sit where the
 * look and feel puts the real thumb, at every value.
 */
public class SliderThumbTest extends TestCase {

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
		assertTrue("thumb inside the slider, got " + x, x > 0 && x < slider.getWidth());
		final BasicSliderUI ui = (BasicSliderUI) slider.getUI();
		assertEquals("value under the drawn thumb", slider.getValue(), ui.valueForXPosition(x), 3);
	}

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
		assertTrue("start thumb is on the track, got " + left, left > 0);
		assertTrue("start is left of middle", left < JRibbonSlider.thumbCentreX(middle));
		assertTrue("middle is left of end", JRibbonSlider.thumbCentreX(middle) < right);
		assertEquals("middle is half way", (left + right) / 2.0, JRibbonSlider.thumbCentreX(middle), 1);
	}

	public void testNothingDrawnWithoutARange() throws Exception {
		final JSlider empty = new JSlider(5, 5, 5);
		empty.setSize(400, 20);
		assertEquals(-1, JRibbonSlider.thumbCentreX(empty));
		final JSlider vertical = new JSlider(JSlider.VERTICAL, 0, 10, 5);
		vertical.setSize(20, 400);
		assertEquals(-1, JRibbonSlider.thumbCentreX(vertical));
	}
}
