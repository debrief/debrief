/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model.manager.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

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

public class SolversManagerImpl implements ISolversManager {
	private final Set<ISolversManagerListener> listeners = Collections
			.newSetFromMap(new WeakHashMap<ISolversManagerListener, Boolean>());

	private final Set<ISolver> available = Collections.newSetFromMap(new WeakHashMap<ISolver, Boolean>());

	private ISolver activeSolver;

	private final IVehicleTypesManager vehicleTypesManager;

	public SolversManagerImpl(final IVehicleTypesManager vehicleTypesManager) {
		this.vehicleTypesManager = vehicleTypesManager;
	}

	@Override
	public void addSolversManagerListener(final ISolversManagerListener listener) {
		listeners.add(listener);
	}

	@Override
	public ISolver createSolver(final String name) {
		final IContributions contributions = new Contributions();
		final ProblemSpace space = new ProblemSpace();
		final IBoundsManager boundsManager = new BoundsManager(contributions, space);
		final IJobsManager jobsManager = new RCPJobsManager();
		final ISolutionGenerator solutionGenerator = new SwitchableSolutionGenerator(contributions, jobsManager,
				new SafeProblemSpace(space));

		final ISolver solver = new Solver(name, contributions, space, boundsManager, solutionGenerator, jobsManager);
		solver.setVehicleType(vehicleTypesManager.getAllTypes().get(0));
		available.add(solver);
		for (final ISolversManagerListener listener : listeners) {
			listener.solverCreated(solver);
		}
		return solver;
	}

	@Override
	public void deactivateSolverIfActive(final ISolver solver) {
		if (activeSolver == solver) {
			setActiveSolver(null);
		}
	}

	@Override
	public ISolver getActiveSolver() {
		return activeSolver;
	}

	@Override
	public List<ISolver> getAvailableSolvers() {
		return new ArrayList<ISolver>(available);
	}

	@Override
	public void removeSolverManagerListener(final ISolversManagerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setActiveSolver(final ISolver solver) {
		activeSolver = solver;
		for (final ISolversManagerListener listener : listeners) {
			listener.activeSolverChanged(activeSolver);
		}
	}
}
