package org.mwc.cmap.xyplot.views;

import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.Watchable;

public interface ILocationCalculator 
{
	/**
	 * Returns the angle between the two lines:
	 * (line_start, line_end) and (line_end, watchable_location)
	 */
	public double getAngle(final LineShape line, final Watchable watchable);
	
	/**
	 * Returns the distance between 
	 * perpendicular projection of the watchable to the line
	 * and the line end.
	 */
	public double getDistance(final LineShape line, final Watchable watchable);

}
