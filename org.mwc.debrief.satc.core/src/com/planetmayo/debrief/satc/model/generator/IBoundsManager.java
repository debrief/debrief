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
package com.planetmayo.debrief.satc.model.generator;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;

/**
 * interface for how to constraint problem space
 * 
 * @author ian
 * 
 */
public interface IBoundsManager
{
	/**
	 * subscribe to progress events
	 */
	void addConstrainSpaceListener(IConstrainSpaceListener newListener);

	/**
	 * unsubscribe from progress events
	 */
	void removeConstrainSpaceListener(IConstrainSpaceListener newListener);

	/**
	 * store the vehicle type, restart the process if started
	 * 
	 * @param v
	 *          the new vehicle type
	 */
	void setVehicleType(VehicleType v);
	
	/**
	 * restart the set of contributions
	 * 
	 */
	void restart();

	/**
	 * run through the remaining contributions
	 * 
	 */
	void run();

	/**
	 * move to the next contribution
	 * 
	 */
	void step();

	/**
	 * @return contribution which was processed on current step
	 */
	BaseContribution getCurrentContribution();

	/**
	 * @return current step number
	 */
	int getCurrentStep();

	/**
	 * 
	 * @return is bounds manager already processed all contributions
	 */
	boolean isCompleted();
}
