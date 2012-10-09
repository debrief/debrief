package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

/**
 * the top level manager object that handles the generation of bounded
 * constraints
 * 
 * @author ian
 * 
 */
public class TrackGenerator implements SteppingGenerator
{
	public static final String STATES_BOUNDED = "states_bounded";

	/**
	 * bounded state listeners
	 * 
	 */
	final private ArrayList<BoundedStatesListener> _boundedListeners = new ArrayList<BoundedStatesListener>();

	/**
	 * people interested in contributions
	 * 
	 */
	final private ArrayList<ContributionsChangedListener> _contributionListeners = new ArrayList<ContributionsChangedListener>();

	/**
	 * people interested in us stepping
	 * 
	 */
	final private ArrayList<SteppingListener> _steppingListeners = new ArrayList<SteppingListener>();

	/**
	 * the problem space we consider
	 */
	final private ProblemSpace _space = new ProblemSpace();

	/**
	 * the set of contributions we listen to. They are ordered, so that we have
	 * data-producing contributions before those that purely perform analysis.
	 * Because of BaseContribution.compareTo, the contribs will be in this order:
	 * 1. Measurement 2. Forecast 3. Analysis
	 * 
	 * This is because we need some data before we start analysing it.
	 * 
	 */
	private final TreeSet<BaseContribution> _contribs = new TreeSet<BaseContribution>();

	/**
	 * the set of contribution properties that we're interested in
	 * 
	 */
	private final String[] _interestingProperties =
	{ BaseContribution.ACTIVE, BaseContribution.HARD_CONSTRAINTS,
			BaseContribution.START_DATE, BaseContribution.FINISH_DATE };

	/**
	 * property listener = so we know about contibutions changing
	 * 
	 */
	private final PropertyChangeListener _contribListener = new PropertyChangeListener()
	{
		@Override
		public void propertyChange(PropertyChangeEvent arg0)
		{
			// let our custom method handle it
			restart();
			
			// aah, but are we auto-running?
			if(_liveRunning)
				run();
		}
	};

	/**
	 * how far we've got to through the contributions
	 * 
	 */
	private int _currentStep = 0;

	/** whether we auto=run after each contribution chagne
	 * 
	 */
	private boolean _liveRunning;

	protected void processThisStep(BaseContribution theContrib, int stepIndex)
	{
		try
		{

			if (theContrib.isActive())
			{
				theContrib.actUpon(_space);
				
				// tell everybody the bounded states have changed
				broadcastBoundedStatesDebug();
			}

			// and tell any step listeners
			Iterator<SteppingListener> iter3 = _steppingListeners.iterator();
			while (iter3.hasNext())
			{
				SteppingGenerator.SteppingListener stepper = (SteppingGenerator.SteppingListener) iter3
						.next();
				stepper.stepped(stepIndex, _contribs.size());
			}

		}
		catch (IncompatibleStateException e)
		{
			// ooh dear, suppose we should tell everybody
			Iterator<BoundedStatesListener> iter2 = _boundedListeners.iterator();
			while (iter2.hasNext())
			{
				BoundedStatesListener boundedStatesListener = (BoundedStatesListener) iter2
						.next();
				boundedStatesListener.incompatibleStatesIdentified(e);
			}
			// TODO handle the incompatible state problem, see ticket 5:
			// https://bitbucket.org/ianmayo/deb_satc/issue/5/consider-how-to-propagate-incompatible
		}

	}

	private void broadcastBoundedStatesDebug()
	{
		// now share the good news
		Iterator<BoundedStatesListener> iter2 = _boundedListeners.iterator();
		while (iter2.hasNext())
		{
			BoundedStatesListener boundedStatesListener = (BoundedStatesListener) iter2
					.next();
			boundedStatesListener.debugStatesBounded(_space.states());
		}
	}

	private void broadcastBoundedStates()
	{
		// now share the good news
		Iterator<BoundedStatesListener> iter2 = _boundedListeners.iterator();
		while (iter2.hasNext())
		{
			BoundedStatesListener boundedStatesListener = (BoundedStatesListener) iter2
					.next();
			boundedStatesListener.statesBounded(_space.states());
		}
	}

	/**
	 * store this new contribution
	 * 
	 * @param contribution
	 */
	public void addContribution(BaseContribution contribution)
	{
		// remember it
		_contribs.add(contribution);

		// start listening to it
		for (int i = 0; i < _interestingProperties.length; i++)
		{
			String thisProp = _interestingProperties[i];
			contribution.addPropertyChangeListener(thisProp, _contribListener);
		}

		// ok, and tell the world
		if (_contributionListeners != null)
		{
			final Iterator<ContributionsChangedListener> iter = _contributionListeners
					.iterator();
			while (iter.hasNext())
			{
				final ContributionsChangedListener listener = (ContributionsChangedListener) iter
						.next();
				listener.added(contribution);
			}
		}
	}

	/**
	 * remove this contribution
	 * 
	 * @param contribution
	 */
	public void removeContribution(BaseContribution contribution)
	{
		// remember it
		_contribs.remove(contribution);

		// stop listening to it
		for (int i = 0; i < _interestingProperties.length; i++)
		{
			String thisProp = _interestingProperties[i];
			contribution.removePropertyChangeListener(thisProp, _contribListener);
		}

		// ok, and tell the world
		if (_contributionListeners != null)
		{
			final Iterator<ContributionsChangedListener> iter = _contributionListeners
					.iterator();
			while (iter.hasNext())
			{
				final ContributionsChangedListener listener = (ContributionsChangedListener) iter
						.next();
				listener.removed(contribution);
			}
		}
	}

	public Collection<BaseContribution> contributions()
	{
		return _contribs;
	}

	public void addSteppingListener(SteppingListener newListener)
	{
		_steppingListeners.add(newListener);
	}

	public void removeSteppingStateListener(SteppingListener newListener)
	{
		_steppingListeners.remove(newListener);
	}

	public void addBoundedStateListener(BoundedStatesListener newListener)
	{
		_boundedListeners.add(newListener);
	}

	public void removeBoundedStateListener(BoundedStatesListener newListener)
	{
		_boundedListeners.remove(newListener);
	}

	public void addContributionsListener(ContributionsChangedListener newListener)
	{
		_contributionListeners.add(newListener);
	}

	public void removeContributionsListener(
			ContributionsChangedListener newListener)
	{
		_contributionListeners.remove(newListener);
	}

	@Override
	public void step()
	{
		if (_currentStep >= _contribs.size())
			throw new RuntimeException("duh, have to reset before we can step again");

		// ok, get the next contribution
		BaseContribution thisC = (BaseContribution) _contribs.toArray()[_currentStep];

		// ok, go for it.
		processThisStep(thisC, _currentStep);

		// now increment the counter
		_currentStep++;

		// are we now complete?
		if (_currentStep == _contribs.size())
		{
			// and tell any step listeners
			Iterator<SteppingListener> iter3 = _steppingListeners.iterator();
			while (iter3.hasNext())
			{
				SteppingGenerator.SteppingListener stepper = (SteppingGenerator.SteppingListener) iter3
						.next();
				stepper.complete();
			}

			// tell any listeners that the final bounds have been updated
			broadcastBoundedStates();
		}
	}

	@Override
	public void restart()
	{
		// clear the states
		_space.clear();

		// ok, just clear the counter.
		_currentStep = 0;

		// and tell everybody we've restared
		Iterator<SteppingListener> iter3 = _steppingListeners.iterator();
		while (iter3.hasNext())
		{
			SteppingGenerator.SteppingListener stepper = (SteppingGenerator.SteppingListener) iter3
					.next();
			stepper.restarted();
		}

		// and tell them about the new bounded states
		broadcastBoundedStates();
	}

	@Override
	public void run()
	{
		// ok, keep stepping until we're done
		while (_currentStep < _contribs.size())
		{
			step();
		}
	}

	/** ditch all of the contributions
	 * 
	 */
	public void clear()
	{
		// ditch the bounded states first
		this.restart();
		
		// clear out the contributions
		// take a copy, since we're going to be modifying the list
		BaseContribution[] safeList = _contribs.toArray(new BaseContribution[]{});
		
		for (int i = 0; i < safeList.length; i++)
		{
			BaseContribution contrib = safeList[i];
			this.removeContribution(contrib);
		}
	}

	/** specify whether we should do a 'run' after each contribution change
	 * 
	 * @param checked
	 */
	public void setLiveRunning(boolean checked)
	{
		_liveRunning = checked;
	}

	/** indicate whether we do 'run' after each contr change
	 * 
	 * @return
	 */
	public boolean isLiveEnabled()
	{
		return _liveRunning;
	}

}
