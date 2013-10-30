package MWC.GUI.Canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;

import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;

public class CanvasTypeUtilities
{

	public static void drawLabelOnLine(final CanvasType dest, String textLabel, Font font,
			Color color, WorldLocation firstLoc, WorldLocation lastLoc, double course)
	{
		Point startPoint = dest.toScreen(firstLoc);
		Point lastPoint = dest.toScreen(lastLoc);
		double width = startPoint.distance(lastPoint);
		double stringWidth = dest.getStringWidth(font, textLabel);
		double distance = (width)/2;
		final double direction = Math.toRadians(course-90);
		if (width > stringWidth * 2)
		{
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
