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

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JSlider;

import junit.framework.TestCase;

/**
 * An extension of JSlider to select a range of values using two thumb controls.
 * The thumb controls are used to select the lower and upper value of a range
 * with predetermined minimum and maximum values.
 *
 * <p>
 * Note that RangeSlider makes use of the default BoundedRangeModel, which
 * supports an inner range defined by a value and an extent. The upper value
 * returned by RangeSlider is simply the lower value plus the extent.
 * </p>
 *
 * Implementation taken from
 * https://ernienotes.wordpress.com/2010/12/27/creating-a-java-swing-range-slider/
 */
public class RangeSlider extends JSlider {

	public static class TestConversion extends TestCase {
		public void testConvert() {
			final Calendar date = new GregorianCalendar();
			final int millis = (int) (date.getTimeInMillis() / 1000L);
			final Calendar newD = toDate(millis);
			final int sliderVal = toInt(date);
			final Calendar newD2 = toDate(sliderVal);
			assertEquals("conversion works", millis, sliderVal);
			assertEquals("conversion works", newD, newD2);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static Calendar toDate(final int val) {
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(val * 1000L);
		return cal;
	}

	public static int toInt(final Calendar date) {
		return (int) (date.getTimeInMillis() / 1000L);
	}

	public RangeSlider() {
		initSlider();
	}

	/**
	 * Constructs a RangeSlider with using a Calendar, storing the values divided by
	 * 1000
	 *
	 * @param min
	 * @param max
	 */
	public RangeSlider(final Calendar min, final Calendar max) {
		super(toInt(min), toInt(max), toInt(min) + (toInt(max) - toInt(min)) / 2);
		initSlider();
	}

	/**
	 * Constructs a RangeSlider with using the time divided by 1000
	 *
	 * @param min
	 * @param max
	 */
	public RangeSlider(final int min, final int max) {
		super(min, max, min + (max - min) / 2);
		initSlider();
	}

	public Calendar getLowerDate() {
		return toDate(getValue());
	}

	public Calendar getUpperDate() {
		return toDate(getUpperValue());
	}

	/**
	 * Returns the upper value in the range.
	 */
	public int getUpperValue() {
		return getValue() + getExtent();
	}

	/**
	 * Returns the lower value in the range.
	 */
	@Override
	public int getValue() {
		return super.getValue();
	}

	/**
	 * Initializes the slider by setting default properties.
	 */
	private void initSlider() {
		setOrientation(HORIZONTAL);
		setValue(getMinimum());
		setUpperValue(getMaximum());
	}

	public void setLowerDate(final Calendar date) {
		setValue(toInt(date));
	}

	public void setMaximum(final Calendar date) {
		setMaximum(toInt(date));
	}

	public void setMinimum(final Calendar date) {
		setMinimum(toInt(date));
	}

	public void setUpperDate(final Calendar date) {
		setUpperValue(toInt(date));
	}

	/**
	 * Sets the upper value in the range.
	 */
	public void setUpperValue(final int value) {
		// Compute new extent.
		final int lowerValue = getValue();
		final int newExtent = Math.min(Math.max(0, value - lowerValue), getMaximum() - lowerValue);

		// Set extent to set upper value.
		setExtent(newExtent);
	}

	/**
	 * Sets the lower value in the range.
	 */
	@Override
	public void setValue(final int value) {
		final int oldValue = getValue();
		if (oldValue == value) {
			return;
		}

		// Compute new value and extent to maintain upper value.
		final int oldExtent = getExtent();
		final int newValue = Math.min(Math.max(getMinimum(), value), oldValue + oldExtent);
		final int newExtent = oldExtent + oldValue - newValue;

		// Set new value and extent, and fire a single change event.
		getModel().setRangeProperties(newValue, newExtent, getMinimum(), getMaximum(), getValueIsAdjusting());
	}

	/**
	 * Overrides the superclass method to install the UI delegate to draw two
	 * thumbs.
	 */
	@Override
	public void updateUI() {
		setUI(new RangeSliderUI(this));
		// Update UI for slider labels. This must be called after updating the
		// UI of the slider. Refer to JSlider.updateUI().
		updateLabelUIs();
	}
}