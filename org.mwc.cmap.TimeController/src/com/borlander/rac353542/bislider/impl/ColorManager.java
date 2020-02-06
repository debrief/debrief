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

package com.borlander.rac353542.bislider.impl;

import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

class ColorManager {
	private static class ColorWithCounter {
		private Color myColor;
		private int myCounter;
		private final int myRed;
		private final int myGreen;
		private final int myBlue;

		public ColorWithCounter(final int red, final int green, final int blue) {
			myRed = red;
			myGreen = green;
			myBlue = blue;
			myCounter = 0;
		}

		public ColorWithCounter(final RGB rgb) {
			this(rgb.red, rgb.green, rgb.blue);
		}

		public Color allocateColor() {
			if (myColor != null && myColor.isDisposed()) {
				myColor = null;
				myCounter = 0;
			}
			myCounter++;
			if (myColor == null) {
				myColor = new Color(Display.getCurrent(), myRed, myGreen, myBlue);
			}
			return myColor;
		}

		public void disposeColor() {
			myCounter--;
			if (myCounter <= 0) {
				if (myColor != null && !myColor.isDisposed()) {
					myColor.dispose();
				}
				myColor = null;
				myCounter = 0;
			}
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof ColorWithCounter) {
				final ColorWithCounter that = (ColorWithCounter) obj;
				return this.myRed == that.myRed && this.myGreen == that.myGreen && this.myBlue == that.myBlue;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return myRed << 16 + myGreen << 8 + myBlue;
		}

		@Override
		public String toString() {
			return "SafeColor: [" + myRed + ", " + myGreen + ", " + myBlue + "]";
		}

	}

	private static ColorManager ourInstance;

	public static ColorManager getInstance() {
		if (ourInstance == null) {
			ourInstance = new ColorManager();
		}
		return ourInstance;
	}

	private final HashMap<RGB, ColorWithCounter> myColors;

	private ColorManager() {
		myColors = new HashMap<RGB, ColorWithCounter>();
	}

	public Color getColor(final int red, final int green, final int blue) {
		return getColor(getKey(red, green, blue));
	}

	public Color getColor(final RGB rgb) {
		ColorWithCounter holder = myColors.get(rgb);
		if (holder == null) {
			holder = new ColorWithCounter(rgb);
			myColors.put(rgb, holder);
		}
		return holder.allocateColor();
	}

	private RGB getKey(final int red, final int green, final int blue) {
		return new RGB(red, green, blue);
	}

	public void releaseColor(final Color color) {
		if (color.isDisposed()) {
			// we can not ask disposed color for components.
			// but most probably application is about to close and we may skip disposing
			return;
		}
		final Object key = getKey(color.getRed(), color.getGreen(), color.getBlue());
		final ColorWithCounter holder = myColors.get(key);
		// intentionally do not check null
		holder.disposeColor();
	}

}
