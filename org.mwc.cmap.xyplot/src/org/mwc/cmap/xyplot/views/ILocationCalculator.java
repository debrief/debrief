/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
