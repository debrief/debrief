package com.planetmayo.debrief.satc.model.manager;

import com.planetmayo.debrief.satc.model.generator.ISolver;

public interface ISolversManagerListener
{
	
	void solverCreated(ISolver solver);
	
	void activeSolverChanged(ISolver activeSolver);
}
