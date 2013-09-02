package com.planetmayo.debrief.satc.model.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.geotools.util.WeakHashSet;

import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.impl.BoundsManager;
import com.planetmayo.debrief.satc.model.generator.impl.Contributions;
import com.planetmayo.debrief.satc.model.generator.impl.Solver;
import com.planetmayo.debrief.satc.model.generator.impl.SwitchableSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.jobs.RCPJobsManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManagerListener;
import com.planetmayo.debrief.satc.model.manager.IVehicleTypesManager;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SolversManagerImpl implements ISolversManager
{
	private final WeakHashSet<ISolversManagerListener> listeners = 
			new WeakHashSet<ISolversManagerListener>(ISolversManagerListener.class);
	
	private final WeakHashSet<ISolver> available = new WeakHashSet<ISolver>(ISolver.class);
	
	private ISolver activeSolver;

	private IVehicleTypesManager vehicleTypesManager;
	
	public SolversManagerImpl(IVehicleTypesManager vehicleTypesManager)
	{
		this.vehicleTypesManager = vehicleTypesManager;
	}
	
	@Override
	public void addSolversManagerListener(ISolversManagerListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeSolverManagerListener(ISolversManagerListener listener)
	{
		listeners.remove(listener);		
	}

	@Override
	public ISolver createSolver(String name)
	{
		IContributions contributions = new Contributions();
		ProblemSpace space = new ProblemSpace();
		IBoundsManager boundsManager = new BoundsManager(contributions, space);
		IJobsManager jobsManager = new RCPJobsManager();
		ISolutionGenerator solutionGenerator = new SwitchableSolutionGenerator(
				contributions, jobsManager, new SafeProblemSpace(space)
		);
		
		ISolver solver = new Solver(name, contributions, space, boundsManager, solutionGenerator, jobsManager);
		solver.setVehicleType(vehicleTypesManager.getAllTypes().get(0));
		available.add(solver);
		for (ISolversManagerListener listener : listeners) 
		{
			listener.solverCreated(solver);
		}
		return solver;
	}

	@Override
	public List<ISolver> getAvailableSolvers()
	{
		return new ArrayList<ISolver>(available);
	}

	@Override
	public ISolver getActiveSolver()
	{
		return activeSolver;
	}

	@Override
	public void setActiveSolver(ISolver solver)
	{
		activeSolver = solver;
		for (ISolversManagerListener listener : listeners) 
		{
			listener.activeSolverChanged(activeSolver);
		}
	}
}
