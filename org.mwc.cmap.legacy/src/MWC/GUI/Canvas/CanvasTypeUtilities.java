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

package MWC.GUI.Canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import MWC.GUI.CanvasType;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class CanvasTypeUtilities {

	/**
	 * plot a label 1/2 way along a line, aligned with the line
	 *
	 * @param dest              the canvas we're drawing on
	 * @param textLabel         the text to write
	 * @param font              the font to use
	 * @param color             the color to use
	 * @param firstLoc          the start of the line
	 * @param lastLoc           the end of the line
	 * @param course            the direction of the line (used to orient the text)
	 * @param clippingThreshold how many times longer than the label the line has to
	 *                          be for it to be plotted
	 */
	public static void drawLabelOnLine(final CanvasType dest, final String textLabel, final Font font,
			final Color color, final WorldLocation firstLoc, final WorldLocation lastLoc,
			final double clippingThreshold, final boolean above) {
		final Point startPoint = dest.toScreen(firstLoc);
		final Point lastPoint = dest.toScreen(lastLoc);

		// handle unable to gen screen coords (if off visible area)
		if (startPoint == null)
			return;

		final double width = startPoint.distance(lastPoint);

		final double stringWidth = dest.getStringWidth(font, textLabel);
//		double distance = (width-stringWidth)/2;
		if (width > stringWidth * clippingThreshold) {
			// calculate the course
			double course = Math.toDegrees(lastLoc.subtract(firstLoc).getBearing());

			final double direction = Math.toRadians(course - 90);

			// sort out the offset to use
			final double dis = -stringWidth / 2;
			final int deltaX = (int) (dis * Math.cos(direction));
			final int deltaY = (int) (dis * Math.sin(direction));
			dest.setColor(color);
			dest.setFont(font);

			// put the course in the correct domain
			if (course < 0)
				course += 360;
			final WorldLocation _centre = new WorldArea(firstLoc, lastLoc).getCentre();
			final Point centrePoint = dest.toScreen(_centre);

			dest.drawText(textLabel, centrePoint.x + deltaX, centrePoint.y + deltaY, (float) course, above);
			// dest.drawRect(centrePoint.x, centrePoint.y, (int) stringWidth,
			// dest.getStringHeight(font));
			// dest.drawRect(centrePoint.x, centrePoint.y, 2, 2);
		}
	}

}
