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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionDataType;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

/**
 * the top level manager object that handles the generation of bounded
 * constraints
 *
 * @author ian
 *
 */
public class BoundsManager implements IBoundsManager {
	public static final String STATES_BOUNDED = "states_bounded";

	/**
	 * the contribution that we're currently processing
	 *
	 */
	private volatile BaseContribution _currentContribution = null;

	/**
	 * current step number and current step contribution
	 *
	 */
	private volatile int _currentStep = 0;

	/**
	 * the problem space we consider
	 */
	private final ProblemSpace _space;

	/**
	 * contributions
	 */
	private final IContributions _contributions;

	/**
	 * people interested in us stepping
	 *
	 */
	private final Set<IConstrainSpaceListener> _steppingListeners;

	/**
	 * process should be stopped due to restart request
	 */
	private volatile boolean stopped;

	public BoundsManager(final IContributions contributions, final ProblemSpace space) {
		_steppingListeners = Collections.synchronizedSet(new HashSet<IConstrainSpaceListener>());
		this._contributions = contributions;
		this._space = space;
	}

	/**
	 * Stepping listeners stuff
	 *
	 */
	@Override
	public void addConstrainSpaceListener(final IConstrainSpaceListener newListener) {
		_steppingListeners.add(newListener);
	}

	private Set<IConstrainSpaceListener> cloneListeners() {
		final Set<IConstrainSpaceListener> listeners = new HashSet<IConstrainSpaceListener>();
		synchronized (_steppingListeners) {
			listeners.addAll(_steppingListeners);
		}
		return listeners;
	}

	protected void createInitialBoundedStates() {
		for (final BaseContribution contribution : _contributions) {
			// is this contribution active?
			if (contribution.isActive()) {
				if (contribution.getDataType() == ContributionDataType.FORECAST) {
					final Date startDate = contribution.getStartDate();
					final Date finishDate = contribution.getFinishDate();
					if (startDate != null && _space.getBoundedStateAt(startDate) == null) {
						try {
							_space.add(new BoundedState(startDate));
						} catch (final IncompatibleStateException ex) {
							// can't be thrown here in any correct situation
							throw new RuntimeException(ex);
						}
					}
					if (finishDate != null && _space.getBoundedStateAt(finishDate) == null) {
						try {
							_space.add(new BoundedState(finishDate));
						} catch (final IncompatibleStateException ex) {
							// can't be thrown here in any correct situation
							throw new RuntimeException(ex);
						}
					}
				}
			}
		}
	}

	protected void fireComplete() {
		for (final IConstrainSpaceListener listener : cloneListeners()) {
			listener.statesBounded(this);
		}
	}

	protected void fireError(final IncompatibleStateException ex) {
		for (final IConstrainSpaceListener listener : cloneListeners()) {
			listener.error(this, ex);
		}
	}

	protected void fireRestarted() {
		for (final IConstrainSpaceListener listener : cloneListeners()) {
			listener.restarted(this);
		}
	}

	protected void fireStepped(final int thisStep, final int totalSteps) {
		for (final IConstrainSpaceListener listener : cloneListeners()) {
			listener.stepped(this, thisStep, totalSteps);
		}
	}

	@Override
	public BaseContribution getCurrentContribution() {
		return _currentContribution;
	}

	@Override
	public int getCurrentStep() {
		return _currentStep;
	}

	@Override
	public boolean isCompleted() {
		return _currentStep >= _contributions.size();
	}

	protected void performSingleStep(final BaseContribution theContrib, final int stepIndex) {
		try {
			if (theContrib.isActive()) {
				theContrib.actUpon(_space);
			}
			fireStepped(stepIndex, _contributions.size());
		} catch (final IncompatibleStateException e) {
			LogFactory.getLog().error("Failed applying bounds:" + theContrib.getName() + " Error:" + e.getMessage());
			fireError(e);
		} catch (final Exception re) {
			LogFactory.getLog().error("unknown error:" + theContrib.getName(), re);
			throw new RuntimeException(re);
		}
	}

	@Override
	public void removeConstrainSpaceListener(final IConstrainSpaceListener newListener) {
		_steppingListeners.remove(newListener);
	}

	@Override
	public void restart() {
		stopped = true;
		synchronized (this) {
			stopped = false;
			if (_currentStep == 0) {
				return;
			}
			// clear the states
			_space.clear();

			// ok, just clear the counter.
			_currentStep = 0;
			_currentContribution = null;

			// and tell them about the new bounded states
			fireRestarted();
		}
	}

	@Override
	public synchronized void run() {
		// ok, keep stepping until we're done
		while (_currentStep < _contributions.size()) {
			step();
			if (stopped) {
				break;
			}
		}
	}

	/**
	 * store the vehicle type
	 *
	 * @param v the new vehicle type
	 */
	@Override
	public void setVehicleType(final VehicleType v) {
		// store the new value
		_space.setVehicleType(v);

		// and get ourselves to re-run, to reflect this change
		restart();
	}

	@Override
	public synchronized void step() {
		if (_currentStep == 0) {
			createInitialBoundedStates();
		}
		if (isCompleted()) {
			// ok, we're done
			return;
		}

		// get next contribution
		_currentContribution = _contributions.nextContribution(_currentContribution);
		if (_currentContribution != null) {
			// ok, go for it.
			performSingleStep(_currentContribution, _currentStep);

			// now increment the counter and contribution
			_currentStep++;

			// are we now complete?
			if (_currentStep == _contributions.size()) {
				// tell any listeners that the final bounds have been updated
				fireComplete();
			}
		}
	}
}
