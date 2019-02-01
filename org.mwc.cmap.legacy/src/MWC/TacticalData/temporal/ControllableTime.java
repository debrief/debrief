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
package MWC.TacticalData.temporal;

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
