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

public interface IDotErrorProvider
{
	
	/** what is the residual at this point in time?
	 * 
	 * @param dtg
	 * @return
	 */
	Double getDotPlotErrorAt(HiResDate dtg);

	/** whether the user wants the errors to be drawn to scale on the spatial plot
	 * 
	 * @return
	 */
	boolean scaleErrors();
}
