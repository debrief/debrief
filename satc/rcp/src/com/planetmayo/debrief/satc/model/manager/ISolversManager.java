package com.planetmayo.debrief.satc.model.manager;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.ISolver;

public interface ISolversManager
{
	
	void addSolversManagerListener(ISolversManagerListener listener);
	
	void removeSolverManagerListener(ISolversManagerListener listener);
	
	ISolver createSolver(String name);
	
	List<ISolver> getAvailableSolvers();
	
	ISolver getActiveSolver();
	
	void setActiveSolver(ISolver solver);
}
