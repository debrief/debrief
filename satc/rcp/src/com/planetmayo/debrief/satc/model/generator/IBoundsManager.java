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
