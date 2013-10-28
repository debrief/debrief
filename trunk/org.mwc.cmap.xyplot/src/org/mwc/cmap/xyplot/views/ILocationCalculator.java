package org.mwc.cmap.xyplot.views;

import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.Watchable;

public interface ILocationCalculator 
{

	/**
	 * Returns the distance between 
	 * perpendicular projection of the watchable to the line
	 * and the line end.
	 */
	public double getDistance(final LineShape line, final Watchable watchable);

}
