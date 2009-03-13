/**
 * 
 */
package org.mwc.cmap.core.DataTypes.Temporal;

import MWC.GenericData.HiResDate;

/**
 * Interface for objects for whom their time attribute may be 
 * externally controlled.
 * @author ian.mayo
 *
 */
public interface ControllableTime {
	
	/** set the new time
	 * 
	 * @param origin - an identifier for the object sending the update,
	 * provided largely so that triggering object can ignore time 
	 * changes it originally created
	 * @param newDate - the new DTG to use
	 * @param fireUpdate - whether to fire the update to any listeners.  This should normally be true, but may be false when originally loading the data
	 */
	public void setTime(Object origin, HiResDate newDate, boolean fireUpdate);
	
}
