package MWC.GUI.Canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;

public class CanvasTypeUtilities
{

	/** plot a label 1/2 way along a line, aligned with the line
	 * 
	 * @param dest the canvas we're drawing on
	 * @param textLabel the text to write
	 * @param font the font to use
	 * @param color the color to use
	 * @param firstLoc the start of the line
	 * @param lastLoc the end of the line
	 * @param course the direction of the line (used to orient the text)
	 * @param clippingThreshold how many times longer than the label the line has to be for it to be plotted
	 */
	public static void drawLabelOnLine(final CanvasType dest, String textLabel, Font font,
			Color color, WorldLocation firstLoc, WorldLocation lastLoc, double clippingThreshold)
	{
		Point startPoint = dest.toScreen(firstLoc);
		Point lastPoint = dest.toScreen(lastLoc);
		double width = startPoint.distance(lastPoint);
		double stringWidth = dest.getStringWidth(font, textLabel);
		double distance = (width)/2;
		if (width > stringWidth * clippingThreshold)
		{
			// calculate the course 
			double course = Math.toDegrees(lastLoc.subtract(firstLoc).getBearing());

			final double direction = Math.toRadians(course-90);

			// sort out the offset to use
			int deltaX = (int) (distance * Math.cos(direction));
			int deltaY = (int) (distance * Math.sin(direction));
			dest.setColor(color);
			dest.setFont(font);
			
			// put the course in the correct domain
			if(course < 0)
				course += 360;
			
			float rotate = (float) (course - 90);
			if (course > 180) {
				rotate-=180;
			}
			dest.drawText(textLabel, startPoint.x + deltaX, startPoint.y + deltaY,
					rotate);
		}
	}

}
