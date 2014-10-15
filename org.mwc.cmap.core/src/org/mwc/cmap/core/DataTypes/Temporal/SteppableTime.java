/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.DataTypes.Temporal;



/**
 * Interface for objects for whom their time attribute may be 
 * externally controlled.
 * @author ian.mayo
 *
 */
public interface SteppableTime {
	
	/** step forward
	 * 
	 * @param origin - an identifier for the object sending the update,
	 * provided largely so that triggering object can ignore time 
	 * changes it originally created
	 * @param fireUpdate - whether to fire the update to any listeners.  This should normally be true, but may be false when originally loading the data
	 */
	public void step(Object origin, boolean fireUpdate);

	/** restart the scenario
	 * 
	 * @param origin - an identifier for the object sending the update,
	 * provided largely so that triggering object can ignore time 
	 * changes it originally created
	 * @param fireUpdate - whether to fire the update to any listeners.  This should normally be true, but may be false when originally loading the data
	 */
	public void restart(Object origin, boolean fireUpdate);

	/** run forward
	 * 
	 * @param origin - an identifier for the object sending the update,
	 * provided largely so that triggering object can ignore time 
	 * changes it originally created
	 * @param fireUpdate - whether to fire the update to any listeners.  This should normally be true, but may be false when originally loading the data
	 */
	public void run(Object origin, boolean fireUpdate);

	/** stop running forward
	 * 
	 * @param origin - an identifier for the object sending the update,
	 * provided largely so that triggering object can ignore time 
	 * changes it originally created
	 * @param fireUpdate - whether to fire the update to any listeners.  This should normally be true, but may be false when originally loading the data
	 */
	public void stop(Object origin, boolean fireUpdate);

	/** pause running forward. but don't finish the scenario
	 * 
	 * @param origin - an identifier for the object sending the update,
	 * provided largely so that triggering object can ignore time 
	 * changes it originally created
	 * @param fireUpdate - whether to fire the update to any listeners.  This should normally be true, but may be false when originally loading the data
	 */
	public void pause(Object origin, boolean fireUpdate);

}
