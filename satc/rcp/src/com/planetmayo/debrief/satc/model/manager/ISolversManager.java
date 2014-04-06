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
