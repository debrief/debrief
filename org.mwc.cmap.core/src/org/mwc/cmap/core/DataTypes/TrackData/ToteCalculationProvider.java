/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.DataTypes.TrackData;

import Debrief.Tools.Tote.toteCalculation;

/**
 * @author ian.mayo
 *
 */
public interface ToteCalculationProvider
{
	/** find out the current set of calculations (typically
	 *  performed as the provider is opening/activated)
	 * 
	 * @return
	 */
	public toteCalculation[] getCalculations();
	
	/** assign the current set of calculations (typically performed
	 * as the provider is closing/deactivated)
	 * @param calcs
	 */
	public void setCalculations(toteCalculation[] calcs);
}
