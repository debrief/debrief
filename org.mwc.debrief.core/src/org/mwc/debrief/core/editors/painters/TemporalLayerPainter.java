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
package org.mwc.debrief.core.editors.painters;

import MWC.GUI.*;
import MWC.GenericData.HiResDate;

/** interface for any classes which want to be able to paint layers possibly in a special way, such as 
 * our snail/normal painters
 * 
 * @author ian.mayo
 *
 */
public interface TemporalLayerPainter extends Editable
{
	/** ok, get painting
	 * 
	 * @param theLayer
	 * @param dest
	 * @param dtg
	 */
	public void paintThisLayer(Layer theLayer, CanvasType dest, HiResDate dtg);
	
	/** retrieve it's name
	 * 
	 * @return the name of this painter
	 */
	public String getName();
}