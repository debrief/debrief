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
package org.mwc.cmap.core.interfaces;

import MWC.Algorithms.PlainProjection;
import MWC.GenericData.WorldArea;

/** interface for objects who have a 2d geographic view that can be
 * externally controlled
 * @author ian.mayo
 *
 */
public interface IControllableViewport
{

	
	/** control the current coverage of the view
	 * 
	 * @param target
	 */
	public void setViewport(WorldArea target);
	
	/** find out the current coverage of the view
	 * 
	 * @return
	 */
	public WorldArea getViewport();

	/** control the complete projection details
	 * 
	 * @param proj the new projection to use
	 */
	public void setProjection(PlainProjection proj);
	
	/** find out the full projection details
	 * 
	 */
	public PlainProjection getProjection();
	
	/** get it to redraw itself
	 * 
	 */
	public void update();
	
	/** and to fit to window
	 * 
	 */
	public void rescale();
}
