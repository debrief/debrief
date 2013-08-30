package com.planetmayo.debrief.satc.model.manager;

import java.util.List;

import com.planetmayo.debrief.satc.model.generator.ISolver;

public interface ISolversManager
{
	
	ISolver createDefaultSolver();
	
	List<ISolver> getAvailableSolvers();
	
	ISolver getActiveSolver();
	
	void setActiveSolver(ISolver solver);
}
