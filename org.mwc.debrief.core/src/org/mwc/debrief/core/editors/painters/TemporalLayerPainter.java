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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
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