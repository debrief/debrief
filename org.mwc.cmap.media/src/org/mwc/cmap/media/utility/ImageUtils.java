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
