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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

class ColorDescriptor implements Disposable {
	private final RGB myRGB;
	private Color myColor;

	public ColorDescriptor(final int red, final int green, final int blue) {
		this(new RGB(red, green, blue));
	}

	public ColorDescriptor(final RGB rgb) {
		myRGB = rgb;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ColorDescriptor) {
			final ColorDescriptor that = (ColorDescriptor) obj;
			return that.myRGB == this.myRGB;
		}
		return false;
	}

	@Override
	public void freeResources() {
		if (myColor != null) {
			ColorManager.getInstance().releaseColor(myColor);
			myColor = null;
		}
	}

	public Color getColor() {
		if (myColor == null) {
			myColor = ColorManager.getInstance().getColor(myRGB);
		}
		return myColor;
	}

	public RGB getRGB() {
		return myRGB;
	}

	@Override
	public int hashCode() {
		return myRGB.hashCode();
	}

}
