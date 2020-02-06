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

package com.planetmayo.debrief.satc.model.generator.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.mwc.debrief.track_shift.zig_detector.Precision;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.ModelObject;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.SteppingAdapter;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class Solver extends ModelObject implements ISolver {
	private class BoundsManagerToSolutionGeneratorBinding extends SteppingAdapter {

		@Override
		public void error(final IBoundsManager boundsManager, final IncompatibleStateException ex) {
			solutionGenerator.clear();
		}

		@Override
		public void restarted(final IBoundsManager boundsManager) {
			solutionGenerator.clear();
		}

		@Override
		public void statesBounded(final IBoundsManager boundsManager) {
		}
	}

	private class LiveRunningListener implements PropertyChangeListener, IContributionsChangedListener {
		@Override
		public void added(final BaseContribution contribution) {
			run();
		}

		@Override
		public void modified() {
		}

		@Override
		public void propertyChange(final PropertyChangeEvent arg0) {
			run();
		}

		@Override
		public void removed(final BaseContribution contribution) {
			run();
		}

		private void run() {
			synchronized (Solver.this) {
				if (isClear) {
					return;
				}
				try {
					boundsManager.restart();
					if (liveRunning) {
						boundsManager.run();
					}
				} catch (final Exception ex) {
					LogFactory.getLog().error("Exception: " + ex.getMessage(), ex);
				}
			}
		}
	}

	private static final long serialVersionUID = 1L;
	private String name;
	private final IContributions contributions;
	private final IBoundsManager boundsManager;

	private final ISolutionGenerator solutionGenerator;

	@SuppressWarnings("unused")
	private final IJobsManager jobsManager;

	private final ProblemSpace problemSpace;

	/**
	 * whether we auto=run after each contribution change
	 *
	 */
	private volatile boolean liveRunning = false;
	private volatile boolean isClear = false;

	private LiveRunningListener liveRunningListener;

	private BoundsManagerToSolutionGeneratorBinding boundsManagerListener;

	/**
	 * the set of contribution properties that we're interested in
	 *
	 */
	private final String[] propertiesToRestartBoundsManager = { BaseContribution.ACTIVE, BaseContribution.START_DATE,
			BaseContribution.FINISH_DATE, BaseContribution.HARD_CONSTRAINTS };

	public Solver(final String name, final IContributions contributions, final ProblemSpace problemSpace,
			final IBoundsManager boundsManager, final ISolutionGenerator solutionGenerator,
			final IJobsManager jobsManager) {
		super();
		this.name = name;
		this.contributions = contributions;
		this.boundsManager = boundsManager;
		this.solutionGenerator = solutionGenerator;
		this.jobsManager = jobsManager;
		this.problemSpace = problemSpace;

		attachListeners();
	}

	private void attachListeners() {
		liveRunningListener = new LiveRunningListener();
		contributions.addContributionsChangedListener(liveRunningListener);
		for (final String property : propertiesToRestartBoundsManager) {
			contributions.addPropertyListener(property, liveRunningListener);
		}
		boundsManagerListener = new BoundsManagerToSolutionGeneratorBinding();
		boundsManager.addConstrainSpaceListener(boundsManagerListener);
	}

	@Override
	public synchronized void cancel() {
		solutionGenerator.cancel();
	}

	@Override
	public synchronized void clear() {
		isClear = true;
		try {
			contributions.clear();
			boundsManager.restart();
			solutionGenerator.clear();
		} finally {
			isClear = false;
		}
	}

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 *
	 * @return yes/no
	 */
	@Override
	public boolean getAutoSuppress() {
		return solutionGenerator.getAutoSuppress();
	}

	@Override
	public IBoundsManager getBoundsManager() {
		return boundsManager;
	}

	@Override
	public IContributions getContributions() {
		return contributions;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Precision getPrecision() {
		return solutionGenerator.getPrecision();
	}

	@Override
	public SafeProblemSpace getProblemSpace() {
		return new SafeProblemSpace(problemSpace);
	}

	@Override
	public ISolutionGenerator getSolutionGenerator() {
		return solutionGenerator;
	}

	@Override
	public VehicleType getVehicleType() {
		return problemSpace.getVehicleType();
	}

	@Override
	public boolean isLiveRunning() {
		return liveRunning;
	}

	@Override
	public synchronized void load(final Reader reader) {
		try {
			boundsManager.removeConstrainSpaceListener(boundsManagerListener);
			contributions.removeContributionsChangedListener(liveRunningListener);

			clear();
			for (final BaseContribution contribution : reader.readContributions()) {
				contributions.addContribution(contribution);
			}
			solutionGenerator.setPrecision(reader.readPrecision());
			setVehicleType(reader.readVehicleType());
		} finally {
			boundsManager.addConstrainSpaceListener(boundsManagerListener);
			contributions.addContributionsChangedListener(liveRunningListener);
		}
	}

	@Override
	public synchronized void run(final boolean constraint, final boolean generate) {
		if (constraint) {
			SATC_Activator.log(IStatus.INFO, "SATC - about to restart bounds", null);
			boundsManager.restart();
			SATC_Activator.log(IStatus.INFO, "SATC - restarted, about to collate bounds", null);
			boundsManager.run();
			SATC_Activator.log(IStatus.INFO, "SATC - bounds collated", null);
		}
		if (generate && boundsManager.isCompleted()) {
			SATC_Activator.log(IStatus.INFO, "SATC - about to generate solutions", null);
			solutionGenerator.generateSolutions(true);
			SATC_Activator.log(IStatus.INFO, "SATC - preparation complete. SATC running", null);
		}
	}

	@Override
	public synchronized void save(final Writer writer) {
		final List<BaseContribution> contributionsList = new ArrayList<BaseContribution>(
				contributions.getContributions());
		writer.writeContributions(contributionsList);
		writer.writePrecision(solutionGenerator.getPrecision());
		writer.writeVehicleType(getVehicleType());
	}

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 *
	 * @param autoSuppress yes/no
	 */
	@Override
	public void setAutoSuppress(final boolean autoSuppress) {
		final Boolean old = solutionGenerator.getAutoSuppress();
		solutionGenerator.setAutoSuppress(autoSuppress);
		firePropertyChange(PRECISION, old, autoSuppress);
	}

	@Override
	public void setLiveRunning(final boolean checked) {
		final boolean old = liveRunning;
		liveRunning = checked;
		firePropertyChange(LIVE_RUNNING, old, checked);
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public void setPrecision(final Precision precision) {
		final Precision old = precision;
		solutionGenerator.setPrecision(precision);
		firePropertyChange(PRECISION, old, precision);
	}

	@Override
	public void setVehicleType(final VehicleType type) {
		final VehicleType old = type;
		boundsManager.setVehicleType(type);
		firePropertyChange(VEHICLE_TYPE, old, type);
	}
}
