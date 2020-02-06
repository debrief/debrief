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

package org.mwc.cmap.media.utility;

import org.eclipse.swt.graphics.Point;

public class ImageUtils {

	public static Point getScaledSize(final int originalWidth, final int originalHeight, final int maxWidth,
			final int maxHeight) {
		return getScaledSize(originalWidth, originalHeight, maxWidth, maxHeight, null);
	}

	public static Point getScaledSize(final int originalWidth, final int originalHeight, final int maxWidth,
			final int maxHeight, Point point) {
		int scaledWidth, scaledHeight;
		final double widthCoef = (double) maxWidth / originalWidth;
		final double heightCoef = (double) maxHeight / originalHeight;
		if (widthCoef < heightCoef) {
			scaledWidth = maxWidth;
			scaledHeight = (int) (originalHeight * widthCoef);
		} else {
			scaledHeight = maxHeight;
			scaledWidth = (int) (originalWidth * heightCoef);
		}
		if (point != null) {
			point.x = scaledWidth;
			point.y = scaledHeight;
		} else {
			point = new Point(scaledWidth, scaledHeight);
		}
		return point;
	}
}
