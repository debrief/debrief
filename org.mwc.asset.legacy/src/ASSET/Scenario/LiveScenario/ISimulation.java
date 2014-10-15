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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Scenario.LiveScenario;


public interface ISimulation
{
	/**
	 * series of state indicators
	 * 
	 */
	public final String WAITING = "Waiting";
	public final String RUNNING = "Running";
	public final String COMPLETE = "Complete";
	public final String TERMINATED = "Terminated";

	/**
	 * name of this simulation instance
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * instruct the simulation to start/resume moving forward
	 * 
	 */
	public void start();

	/**
	 * prematurely terminate the simulation
	 * 
	 */
	public void stop();
	
	/** don't finish the scenario, just pause it
	 * 
	 */
	public void pause();

	/**
	 * retrieve the current model time
	 * 
	 * @return
	 */
	public long getTime();

	/**
	 * move this scenario forward
	 * 
	 */
	public void step();

}
