/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2015, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.Wrappers.Track;

import MWC.GenericData.HiResDate;

/** interface for class that is able to provide the an additional
 * value for specified time stams to help with styling, plus an accessor
 * to find out if that styling should be applied
 * 
 * @author ian
 *
 */
public interface ITimeVariableProvider
{
	
	/** what is the value at the specified time
	 * 
	 * @param dtg
	 * @return
	 */
	long getValueAt(HiResDate dtg);

	/** whether the user wants the errors to be drawn to scale on the spatial plot
	 * 
	 * @return
	 */
	boolean applyStyling();
}
