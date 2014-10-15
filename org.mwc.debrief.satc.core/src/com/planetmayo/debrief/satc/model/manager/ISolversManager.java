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
package com.planetmayo.debrief.satc.model.manager;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.ISolver;

public interface ISolversManager
{
	
	void addSolversManagerListener(ISolversManagerListener listener);
	
	void removeSolverManagerListener(ISolversManagerListener listener);
	
	ISolver createSolver(String name);
	
	List<ISolver> getAvailableSolvers();
	
	/** 
	 * @return active solver or null if no solver is active
	 */
	ISolver getActiveSolver();
	
	
	/**
	 * Deactivates current active solver and activates specified one if it isn't null
	 *  
	 * @param solver
	 */
	void setActiveSolver(ISolver solver);
	
	/**
	 * if current active solver is solver specified in parameter the method deactivates it
	 * otherwise leaves active solver unchanged  
	 * @param solver
	 */
	void deactivateSolverIfActive(ISolver solver);
}
