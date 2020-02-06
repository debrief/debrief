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
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.core.runtime.IStatus;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.ContributionChangedAdapter;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class Contributions implements IContributions {
	private final Set<IContributionsChangedListener> contributionListeners;

	private final NavigableSet<BaseContribution> contributions;

	private final PropertyChangeSupport support;

	private final PropertyChangeListener _globalChangeListener;

	public Contributions() {
		contributions = new ConcurrentSkipListSet<BaseContribution>();
		contributionListeners = Collections.synchronizedSet(new HashSet<IContributionsChangedListener>());
		support = new PropertyChangeSupport(this);

		_globalChangeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				for (final IContributionsChangedListener listener : cloneListeners()) {
					listener.modified();
				}
			}
		};
	}

	@Override
	public void addContribution(final BaseContribution contribution) {
		if (contributions.contains(contribution)) {
			// register an error
			SATC_Activator.log(IStatus.ERROR, "Contribution names must be unique", null);
			SATC_Activator.showMessage("Add new contribution",
					"Sorry, contribution names must be unique. Do you need to delete target legs before generating them?");
			return;
		}
		contributions.add(contribution);
		final PropertyChangeListener[] listeners = support.getPropertyChangeListeners();
		for (final PropertyChangeListener listener : listeners) {
			if (listener instanceof PropertyChangeListenerProxy) {
				final PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
				contribution.addPropertyChangeListener(proxy.getPropertyName(), proxy.getListener());
			}
		}

		// add our generic listener
		contribution.addPropertyChangeListener(_globalChangeListener);

		fireContributionAdded(contribution);
	}

	@Override
	public void addContributionsChangedListener(final IContributionsChangedListener listener) {
		contributionListeners.add(listener);
	}

	@Override
	public void addPropertyListener(final String property, final PropertyChangeListener listener) {
		// thread safety, if threads invoke addPropertyListener and addContribution
		// concurrently
		// we should add a new listener to a new contribution
		final IContributionsChangedListener auxListener = new ContributionChangedAdapter() {

			@Override
			public void added(final BaseContribution contribution) {
				contribution.addPropertyChangeListener(property, listener);
			}
		};
		addContributionsChangedListener(auxListener);
		try {
			support.addPropertyChangeListener(property, listener);
			final Set<BaseContribution> copy = new HashSet<BaseContribution>(contributions);
			for (final BaseContribution contribution : copy) {
				contribution.addPropertyChangeListener(property, listener);
			}
		} finally {
			removeContributionsChangedListener(auxListener);
		}
	}

	@Override
	public void clear() {
		final Set<BaseContribution> set = new HashSet<BaseContribution>(contributions);
		for (final BaseContribution contribution : set) {
			removeContribution(contribution);
		}
	}

	private Set<IContributionsChangedListener> cloneListeners() {
		final Set<IContributionsChangedListener> currentListeners = new HashSet<IContributionsChangedListener>();
		synchronized (contributionListeners) {
			currentListeners.addAll(contributionListeners);
		}
		return currentListeners;
	}

	@Override
	public boolean contains(final BaseContribution contribution) {
		return contributions.contains(contribution);
	}

	protected void fireContributionAdded(final BaseContribution contribution) {
		for (final IContributionsChangedListener listener : cloneListeners()) {
			listener.added(contribution);
		}
	}

	protected void fireContributionRemoved(final BaseContribution contribution) {
		for (final IContributionsChangedListener listener : cloneListeners()) {
			listener.removed(contribution);
		}
	}

	@Override
	public SortedSet<BaseContribution> getContributions() {
		return Collections.unmodifiableSortedSet(contributions);
	}

	@Override
	public Iterator<BaseContribution> iterator() {
		return contributions.iterator();
	}

	@Override
	public BaseContribution nextContribution(final BaseContribution current) {
		if (current == null) {
			return contributions.first();
		}
		return contributions.higher(current);
	}

	@Override
	public void removeContribution(final BaseContribution contribution) {
		if (!contributions.contains(contribution)) {
			LogFactory.getLog().error("We're trying to delete " + contribution + " but its not one of ours!");

			return;
		}
		contributions.remove(contribution);
		final PropertyChangeListener[] listeners = support.getPropertyChangeListeners();
		for (final PropertyChangeListener listener : listeners) {
			if (listener instanceof PropertyChangeListenerProxy) {
				final PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
				contribution.removePropertyChangeListener(proxy.getPropertyName(), proxy.getListener());
			}
		}

		contribution.removePropertyChangeListener(_globalChangeListener);

		fireContributionRemoved(contribution);
	}

	@Override
	public void removeContributionsChangedListener(final IContributionsChangedListener listener) {
		contributionListeners.remove(listener);
	}

	@Override
	public void removePropertyListener(final String property, final PropertyChangeListener listener) {
		// thread safety, if threads invoke removePropertyListener and
		// removeContribution concurrently
		// we should remove the listener from the contribution
		final IContributionsChangedListener auxListener = new ContributionChangedAdapter() {

			@Override
			public void removed(final BaseContribution contribution) {
				contribution.removePropertyChangeListener(property, listener);
			}
		};
		addContributionsChangedListener(auxListener);
		try {
			support.removePropertyChangeListener(property, listener);
			final Set<BaseContribution> copy = new HashSet<BaseContribution>(contributions);
			for (final BaseContribution contribution : copy) {
				contribution.removePropertyChangeListener(property, listener);
			}
		} finally {
			removeContributionsChangedListener(auxListener);
		}
	}

	@Override
	public int size() {
		return contributions.size();
	}

}
