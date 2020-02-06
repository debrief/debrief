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
package Debrief.Wrappers.Track;

import MWC.GUI.CanvasType;

/** interface for classes that wish to use a residual error to assist with their plotting
 * 
 * @author ian
 *
 */
public interface CanBePlottedWithTimeVariable
{

	/** use the specified error provider to customise how the item gets painted
	 * 
	 * @param dest
	 * @param errorProvider
	 */
	void paint(CanvasType dest, ITimeVariableProvider errorProvider);

}
