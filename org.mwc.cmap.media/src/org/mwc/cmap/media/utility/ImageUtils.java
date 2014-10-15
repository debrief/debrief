/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.media.utility;

import org.eclipse.swt.graphics.Point;

public class ImageUtils {
	
	public static Point getScaledSize(int originalWidth, int originalHeight, int maxWidth, int maxHeight) {
		return getScaledSize(originalWidth, originalHeight, maxWidth, maxHeight, null);
	}
	
	public static Point getScaledSize(int originalWidth, int originalHeight, int maxWidth, int maxHeight, Point point) {
		int scaledWidth, scaledHeight;
		double widthCoef = (double) maxWidth / originalWidth;
		double heightCoef = (double) maxHeight / originalHeight;
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
