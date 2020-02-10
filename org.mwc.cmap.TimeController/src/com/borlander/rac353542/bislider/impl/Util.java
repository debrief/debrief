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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

class Util {

	public static Rectangle cloneRectangle(final Rectangle rectangle) {
		return new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	/**
	 * Always create a new instance with SWT.BOLD flag "on".
	 */
	public static Font deriveBold(final Font font) {
		final FontData[] fontData = font.getFontData();
		if (fontData.length != 1) {
			// fallback, newer with Windows
			// create separate instance to simplify disposing
			return new Font(Display.getCurrent(), fontData);
		}
		final FontData theOnly = fontData[0];
//        return new Font(Display.getCurrent(), theOnly.getName(), theOnly.getHeight(), theOnly.getStyle() | SWT.BOLD);
		return new Font(Display.getCurrent(), theOnly.getName(), 8, theOnly.getStyle());
	}

	private Util() {
		// do not instantiate
	}
}
