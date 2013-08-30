package com.planetmayo.debrief.satc.model.manager.impl;

import java.util.List;

import org.geotools.util.WeakHashSet;

import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.impl.Solver;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;

public class SolversManagerImpl implements ISolversManager
{
	
	private WeakHashSet<ISolver> available = new WeakHashSet<ISolver>(ISolver.class);

	@Override
	public ISolver createDefaultSolver()
	{
		ISolver solver = new Solver(contributions, problemSpace, boundsManager, solutionGenerator, jobsManager);
		return null;
	}

	@Override
	public List<ISolver> getAvailableSolvers()
	{
		return null;
	}

	@Override
	public ISolver getActiveSolver()
	{
		return null;
	}

	@Override
	public void setActiveSolver(ISolver solver)
	{
		
	}
	
	

}
