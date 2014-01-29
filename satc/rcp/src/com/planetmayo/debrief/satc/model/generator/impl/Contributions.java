package com.planetmayo.debrief.satc.model.generator.impl;

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

import org.eclipse.core.runtime.Status;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.ContributionChangedAdapter;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class Contributions implements IContributions
{
	private final Set<IContributionsChangedListener> contributionListeners;

	private final NavigableSet<BaseContribution> contributions;

	private final PropertyChangeSupport support;

	public Contributions()
	{
		contributions = new ConcurrentSkipListSet<BaseContribution>();
		contributionListeners = Collections
				.synchronizedSet(new HashSet<IContributionsChangedListener>());
		support = new PropertyChangeSupport(this);
	}

	@Override
	public Iterator<BaseContribution> iterator()
	{
		return contributions.iterator();
	}

	@Override
	public void addContribution(BaseContribution contribution)
	{
		if (contributions.contains(contribution))
		{
			// register an error
			SATC_Activator.log(Status.ERROR, "Contribution names must be unique",
					null);
			return;
		}
		contributions.add(contribution);
		PropertyChangeListener[] listeners = support.getPropertyChangeListeners();
		for (PropertyChangeListener listener : listeners)
		{
			if (listener instanceof PropertyChangeListenerProxy)
			{
				PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
				contribution.addPropertyChangeListener(proxy.getPropertyName(),
						(PropertyChangeListener) proxy.getListener());
			}
		}
		fireContributionAdded(contribution);
	}

	@Override
	public void removeContribution(BaseContribution contribution)
	{
		if (!contributions.contains(contribution))
		{
			LogFactory.getLog().error(
					"We're trying to delete " + contribution
							+ " but its not one of ours!");
			
			return;
		}
		contributions.remove(contribution);
		PropertyChangeListener[] listeners = support.getPropertyChangeListeners();
		for (PropertyChangeListener listener : listeners)
		{
			if (listener instanceof PropertyChangeListenerProxy)
			{
				PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
				contribution.removePropertyChangeListener(proxy.getPropertyName(),
						(PropertyChangeListener) proxy.getListener());
			}
		}
		fireContributionRemoved(contribution);
	}

	@Override
	public void addPropertyListener(final String property,
			final PropertyChangeListener listener)
	{
		// thread safety, if threads invoke addPropertyListener and addContribution
		// concurrently
		// we should add a new listener to a new contribution
		IContributionsChangedListener auxListener = new ContributionChangedAdapter()
		{

			@Override
			public void added(BaseContribution contribution)
			{
				contribution.addPropertyChangeListener(property, listener);
			}
		};
		addContributionsChangedListener(auxListener);
		try
		{
			support.addPropertyChangeListener(property, listener);
			Set<BaseContribution> copy = new HashSet<BaseContribution>(contributions);
			for (BaseContribution contribution : copy)
			{
				contribution.addPropertyChangeListener(property, listener);
			}
		}
		finally
		{
			removeContributionsChangedListener(auxListener);
		}
	}

	@Override
	public void removePropertyListener(final String property,
			final PropertyChangeListener listener)
	{
		// thread safety, if threads invoke removePropertyListener and
		// removeContribution concurrently
		// we should remove the listener from the contribution
		IContributionsChangedListener auxListener = new ContributionChangedAdapter()
		{

			@Override
			public void removed(BaseContribution contribution)
			{
				contribution.removePropertyChangeListener(property, listener);
			}
		};
		addContributionsChangedListener(auxListener);
		try
		{
			support.removePropertyChangeListener(property, listener);
			Set<BaseContribution> copy = new HashSet<BaseContribution>(contributions);
			for (BaseContribution contribution : copy)
			{
				contribution.removePropertyChangeListener(property, listener);
			}
		}
		finally
		{
			removeContributionsChangedListener(auxListener);
		}
	}

	@Override
	public void addContributionsChangedListener(
			IContributionsChangedListener listener)
	{
		contributionListeners.add(listener);
	}

	@Override
	public void removeContributionsChangedListener(
			IContributionsChangedListener listener)
	{
		contributionListeners.remove(listener);
	}

	@Override
	public SortedSet<BaseContribution> getContributions()
	{
		return Collections.unmodifiableSortedSet(contributions);
	}

	@Override
	public int size()
	{
		return contributions.size();
	}

	@Override
	public void clear()
	{
		Set<BaseContribution> set = new HashSet<BaseContribution>(contributions);
		for (BaseContribution contribution : set)
		{
			removeContribution(contribution);
		}
	}

	@Override
	public BaseContribution nextContribution(BaseContribution current)
	{
		if (current == null)
		{
			return contributions.first();
		}
		return contributions.higher(current);
	}

	private Set<IContributionsChangedListener> cloneListeners()
	{
		Set<IContributionsChangedListener> currentListeners = new HashSet<IContributionsChangedListener>();
		synchronized (contributionListeners)
		{
			currentListeners.addAll(contributionListeners);
		}
		return currentListeners;
	}

	protected void fireContributionAdded(BaseContribution contribution)
	{
		for (IContributionsChangedListener listener : cloneListeners())
		{
			listener.added(contribution);
		}
	}

	protected void fireContributionRemoved(BaseContribution contribution)
	{
		for (IContributionsChangedListener listener : cloneListeners())
		{
			listener.removed(contribution);
		}
	}

}
