/**
 * 
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
