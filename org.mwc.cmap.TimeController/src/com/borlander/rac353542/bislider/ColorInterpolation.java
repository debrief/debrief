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

package com.borlander.rac353542.bislider;

import org.eclipse.swt.graphics.RGB;

/**
 * Represents the abstract algorithm of color interpolation, that is computing
 * the Color for some intermediate value of some range. At any moment of time
 * each interpolation works in terms of some "context" consisting of range of
 * double inputs and the predefined color values at the range boundaries..
 */
public abstract class ColorInterpolation {

	/**
	 * Predefined implementation which interpolates colors in such way that the
	 * central value is always has predefined color. By default, this central color
	 * is selected to be black, however, any other value is also allowed.
	 */
	public static class INTERPOLATE_CENTRAL extends ColorInterpolation {

		private static final RGB DEFAULT_CENTER_VALUE = new RGB(0, 0, 0);
		private final ColorInterpolation myLeftInterpolation;
		private final ColorInterpolation myRightInterpolation;
		private final RGB myColorAtCenter;

		public INTERPOLATE_CENTRAL() {
			this(DEFAULT_CENTER_VALUE);
		}

		public INTERPOLATE_CENTRAL(final RGB colorAtCenter) {
			myColorAtCenter = colorAtCenter;
			myLeftInterpolation = new INTERPOLATE_RGB();
			myRightInterpolation = new INTERPOLATE_RGB();
		}

		@Override
		protected RGB computeRGB(final double rate) {
			return rate <= 0.5 ? myLeftInterpolation.interpolateRGB(rate)
					: myRightInterpolation.interpolateRGB(1 - rate);
		}

		@Override
		public void setContext(final RGB minRGB, final RGB maxRGB, final double minValue, final double maxValue) {
			super.setContext(minRGB, maxRGB, minValue, maxValue);
			myLeftInterpolation.setContext(minRGB, myColorAtCenter, 0, 0.5);
			myRightInterpolation.setContext(maxRGB, myColorAtCenter, 0, 0.5);
		}
	}

	/**
	 * Predefined implementation which interpolates colors using separate linear
	 * interpolation for hue, saturation and brightness bands.
	 */
	public static class INTERPOLATE_HSB extends ColorInterpolation {

		@Override
		protected RGB computeRGB(final double rate) {
			final RGB min = getMinRGB();
			final RGB max = getMaxRGB();
			final float hsbMin[] = AWTColorUtil.RGBtoHSB(min.red, min.green, min.blue, null);
			final float hsbMax[] = AWTColorUtil.RGBtoHSB(max.red, max.green, max.blue, null);
			final double h = (hsbMax[0] - hsbMin[0]) * rate + hsbMin[0];
			final double s = (hsbMax[1] - hsbMin[1]) * rate + hsbMin[1];
			final double b = (hsbMax[2] - hsbMin[2]) * rate + hsbMin[2];
			return AWTColorUtil.HSBtoRGBObject(h, s, b);
		}
	}

	/**
	 * Predefined implementation which interpolates colors using separate linear
	 * interpolation for red, green and blue bands.
	 */
	public static class INTERPOLATE_RGB extends ColorInterpolation {

		@Override
		protected RGB computeRGB(final double rate) {
			final RGB min = getMinRGB();
			final RGB max = getMaxRGB();
			return createRGB( //
					min.red + rate * (max.red - min.red), //
					min.green + rate * (max.green - min.green), //
					min.blue + rate * (max.blue - min.blue));
		}
	}

	private double myMaxValue;
	private double myMinValue;

	private RGB myMinRGB;

	private RGB myMaxRGB;

	private boolean myIsTrivial;

	/**
	 * Actual computing method.
	 *
	 * @param rate the normalized double value (from 0.0 to 1.0) representing the
	 *             position of input in context range.
	 */
	protected abstract RGB computeRGB(double rate);

	protected final RGB createRGB(final double red, final double green, final double blue) {
		return new RGB((int) red, (int) green, (int) blue);
	}

	protected final RGB getMaxRGB() {
		return myMaxRGB;
	}

	protected final RGB getMinRGB() {
		return myMinRGB;
	}

	protected double getRate(final double value) {
		if (value <= myMinValue) {
			return 0;
		}
		if (value > myMaxValue) {
			return 1;
		}
		return (value - myMinValue) / (myMaxValue - myMinValue);
	}

	/**
	 * Default implementation normalizes the input value into [0.0 1.0] range and
	 * delegates actual computing to <code>computeRGB</code>.
	 *
	 * @param value the value which should be interpolated
	 *
	 * @return the color for specified value computed by some class-sepcififc
	 *         algorithm in terms of the previously set interpolation context.
	 */
	public RGB interpolateRGB(final double value) {
		if (myIsTrivial) {
			return myMinRGB;
		}
		final double rate = getRate(value);
		return computeRGB(rate);
	}

	public boolean isSameInterpolationMode(final ColorInterpolation other) {
		return other != null && this.getClass().equals(other.getClass());
	}

	/**
	 * Sets the context of interpolation. It is guaranteed that any subsequent
	 * requests for <code>interpolateRGB()</code> will have parameter that
	 * <code>minValue &lt;= parameter &lt;= maxValue</code>.
	 */
	public void setContext(final RGB minRGB, final RGB maxRGB, double minValue, double maxValue) {
		if (minValue == maxValue) {
			throw new IllegalArgumentException("I can not accept zero range");
		}
		if (minValue > maxValue) {
			final double temp = minValue;
			minValue = maxValue;
			maxValue = temp;
		}
		myMinRGB = minRGB;
		myMaxRGB = maxRGB;
		myMinValue = minValue;
		myMaxValue = maxValue;
		myIsTrivial = myMinRGB.equals(myMaxRGB);
	}
}
