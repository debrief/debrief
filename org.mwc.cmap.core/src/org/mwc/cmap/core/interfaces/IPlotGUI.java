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
package org.mwc.cmap.core.interfaces;

import java.awt.Color;

public interface IPlotGUI
{
	/** find out the background color
	 * 
	 * @return the current background color of the plot
	 */
	public Color getBackgroundColor();
	
	/** set the background color
	 * 
	 * @param theColor the background color
	 */
	public void setBackgroundColor(Color theColor);
}
