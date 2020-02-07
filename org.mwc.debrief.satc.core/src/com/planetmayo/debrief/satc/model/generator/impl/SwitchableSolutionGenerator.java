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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mwc.debrief.track_shift.zig_detector.Precision;

import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.bf.BFSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.ga.GASolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.sa.SASolutionGenerator;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SwitchableSolutionGenerator implements ISolutionGenerator {
	private final IContributions contributions;
	private final IJobsManager jobsManager;
	private final SafeProblemSpace problemSpace;

	private final Set<IGenerateSolutionsListener> listeners;
	private ISolutionGenerator currentGenerator;

	public SwitchableSolutionGenerator(final IContributions contributions, final IJobsManager jobsManager,
			final SafeProblemSpace problemSpace) {
		this.contributions = contributions;
		this.jobsManager = jobsManager;
		this.problemSpace = problemSpace;
		this.listeners = Collections.synchronizedSet(new HashSet<IGenerateSolutionsListener>());
		switchToGA();
	}

	@Override
	public void addReadyListener(final IGenerateSolutionsListener listener) {
		listeners.add(listener);
		if (currentGenerator != null) {
			currentGenerator.addReadyListener(listener);
		}
	}

	@Override
	public void cancel() {
		currentGenerator.cancel();
	}

	@Override
	public void clear() {
		currentGenerator.clear();
	}

	@Override
	public void generateSolutions(final boolean fullRerun) {
		currentGenerator.generateSolutions(fullRerun);
	}

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 *
	 * @return yes/no
	 */
	@Override
	public boolean getAutoSuppress() {
		boolean res = false;
		if (currentGenerator != null)
			res = currentGenerator.getAutoSuppress();

		return res;
	}

	public ISolutionGenerator getCurrentGenerator() {
		return currentGenerator;
	}

	@Override
	public Precision getPrecision() {
		return currentGenerator.getPrecision();
	}

	@Override
	public SafeProblemSpace getProblemSpace() {
		return currentGenerator.getProblemSpace();
	}

	@Override
	public void removeReadyListener(final IGenerateSolutionsListener listener) {
		listeners.remove(listener);
		if (currentGenerator != null) {
			currentGenerator.removeReadyListener(listener);
		}
	}

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 *
	 * @param autoSuppress yes/no
	 */
	@Override
	public void setAutoSuppress(final boolean autoSuppress) {
		if (currentGenerator != null)
			currentGenerator.setAutoSuppress(autoSuppress);
	}

	@Override
	public void setPrecision(final Precision precision) {
		currentGenerator.setPrecision(precision);
	}

	public synchronized void switchGenerator(final ISolutionGenerator generator) {
		if (generator == null) {
			throw new IllegalArgumentException("generator can't be null");
		}
		final Precision precision = currentGenerator == null ? Precision.LOW : getPrecision();
		synchronized (listeners) {
			for (final IGenerateSolutionsListener listener : listeners) {
				if (currentGenerator != null) {
					currentGenerator.removeReadyListener(listener);
				}
				generator.addReadyListener(listener);
			}
		}
		generator.setPrecision(precision);
		currentGenerator = generator;
	}

	public void switchToBF() {
		switchGenerator(new BFSolutionGenerator(contributions, jobsManager, problemSpace));
	}

	public void switchToGA() {
		switchGenerator(new GASolutionGenerator(contributions, jobsManager, problemSpace));
	}

	public void switchToSA() {
		switchGenerator(new SASolutionGenerator(contributions, jobsManager, problemSpace));
	}
}
