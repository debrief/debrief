package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.SupportServices;

/**
 * the top level manager object that handles the generation of bounded
 * constraints
 * 
 * @author ian
 * 
 */
public class BoundsManager implements ISteppingGenerator
{
	public static final String STATES_BOUNDED = "states_bounded";

	/**
	 * bounded state listeners
	 * 
	 */
	final private ArrayList<IBoundedStatesListener> _boundedListeners = new ArrayList<IBoundedStatesListener>();

	/**
	 * people interested in contributions
	 * 
	 */
	final private ArrayList<IContributionsChangedListener> _contributionListeners = new ArrayList<IContributionsChangedListener>();

	/**
	 * people interested in us stepping
	 * 
	 */
	final private ArrayList<ISteppingListener> _steppingListeners = new ArrayList<ISteppingListener>();

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
	// TODO:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	private final String[] _interestingProperties =
	{ BaseContribution.ACTIVE, 
			BaseContribution.START_DATE, BaseContribution.FINISH_DATE, BaseContribution.HARD_CONSTRAINTS };

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
			if (_liveRunning)
				run();
		}
	};

	/**
	 * how far we've got to through the contributions
	 * 
	 */
	private int _currentStep = 0;

	/**
	 * whether we auto=run after each contribution chagne
	 * 
	 */
	private boolean _liveRunning;

	public void addBoundedStateListener(IBoundedStatesListener newListener)
	{
		_boundedListeners.add(newListener);
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
			final Iterator<IContributionsChangedListener> iter = _contributionListeners
					.iterator();
			while (iter.hasNext())
			{
				final IContributionsChangedListener listener = iter.next();
				listener.added(contribution);
			}
		}
	}

	public void addContributionsListener(IContributionsChangedListener newListener)
	{
		_contributionListeners.add(newListener);
	}

	public void addSteppingListener(ISteppingListener newListener)
	{
		_steppingListeners.add(newListener);
	}

	private void broadcastBoundedStates()
	{
		// now share the good news
		Iterator<IBoundedStatesListener> iter2 = _boundedListeners.iterator();
		while (iter2.hasNext())
		{
			IBoundedStatesListener boundedStatesListener = iter2.next();
			boundedStatesListener.statesBounded(_space.states());
		}
	}

	private void broadcastBoundedStatesDebug()
	{
		// now share the good news
		Iterator<IBoundedStatesListener> iter2 = _boundedListeners.iterator();
		while (iter2.hasNext())
		{
			IBoundedStatesListener boundedStatesListener = iter2.next();
			boundedStatesListener.debugStatesBounded(_space.states());
		}
	}

	/**
	 * ditch all of the contributions
	 * 
	 */
	public void clear()
	{
		// ditch the bounded states first
		this.restart();

		// clear out the contributions
		// take a copy, since we're going to be modifying the list
		BaseContribution[] safeList = _contribs.toArray(new BaseContribution[]
		{});

		for (int i = 0; i < safeList.length; i++)
		{
			BaseContribution contrib = safeList[i];
			this.removeContribution(contrib);
		}
	}

	public Collection<BaseContribution> getContributions()
	{
		return _contribs;
	}

	/**
	 * indicate whether we do 'run' after each contribution change
	 * 
	 * @return
	 */
	public boolean isLiveEnabled()
	{
		return _liveRunning;
	}

	protected void performSingleStep(final BaseContribution theContrib,
			final int stepIndex)
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
			Iterator<ISteppingListener> iter3 = _steppingListeners.iterator();
			while (iter3.hasNext())
			{
				ISteppingListener stepper = iter3.next();

				stepper.stepped(stepIndex, _contribs.size());
			}

		}
		catch (IncompatibleStateException e)
		{
			SupportServices.INSTANCE.getLog().error(
					"Failed applying bounds:" + theContrib.getName(), e);

			// ooh dear, suppose we should tell everybody
			Iterator<IBoundedStatesListener> iter2 = _boundedListeners.iterator();
			while (iter2.hasNext())
			{
				IBoundedStatesListener boundedStatesListener = iter2.next();
				boundedStatesListener.incompatibleStatesIdentified(theContrib, e);
			}

			// clear the bounded states = they're invalid
			_space.clear();
			// TODO handle the incompatible state problem, see ticket 5:
			// https://bitbucket.org/ianmayo/deb_satc/issue/5/consider-how-to-propagate-incompatible
		}
		catch (Exception re)
		{
			SupportServices.INSTANCE.getLog().error(
					"unknown error:" + theContrib.getName(), re);
			// TODO handle the incompatible state problem, see ticket 5:
			// https://bitbucket.org/ianmayo/deb_satc/issue/5/consider-how-to-propagate-incompatible
		}

	}

	public void removeBoundedStateListener(IBoundedStatesListener newListener)
	{
		_boundedListeners.remove(newListener);
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
			final Iterator<IContributionsChangedListener> iter = _contributionListeners
					.iterator();
			while (iter.hasNext())
			{
				final IContributionsChangedListener listener = iter.next();
				listener.removed(contribution);
			}
		}
	}

	public void removeContributionsListener(
			IContributionsChangedListener newListener)
	{
		_contributionListeners.remove(newListener);
	}

	public void removeSteppingStateListener(ISteppingListener newListener)
	{
		_steppingListeners.remove(newListener);

	}

	@Override
	public void restart()
	{
		// clear the states
		_space.clear();

		// ok, just clear the counter.
		_currentStep = 0;

		// and tell everybody we've restared
		Iterator<ISteppingListener> iter3 = _steppingListeners.iterator();
		while (iter3.hasNext())
		{
			ISteppingListener stepper = iter3.next();
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

	/**
	 * specify whether we should do a 'run' after each contribution change
	 * 
	 * @param checked
	 */
	public void setLiveRunning(boolean checked)
	{
		_liveRunning = checked;
	}

	@Override
	public void step()
	{
		if (_currentStep >= _contribs.size())
			return;

		// ok, get the next contribution
		Object[] theArr = _contribs.toArray();
		BaseContribution thisC = (BaseContribution) theArr[_currentStep];

		// ok, go for it.
		performSingleStep(thisC, _currentStep);

		// now increment the counter
		_currentStep++;

		// are we now complete?
		if (_currentStep == _contribs.size())
		{
			// and tell any step listeners
			Iterator<ISteppingListener> iter3 = _steppingListeners.iterator();

			while (iter3.hasNext())
			{
				ISteppingListener stepper = iter3.next();
				stepper.complete();
			}

			// tell any listeners that the final bounds have been updated
			broadcastBoundedStates();
		}
	}

	/**
	 * store the vehicle type
	 * 
	 * @param v
	 *          the new vehicle type
	 */
	public void setVehicleType(VehicleType v)
	{
		// store the new value
		_space.setVehicleType(v);

		// and get ourselves to re-run, to reflect this change
		PropertyChangeEvent pce = new PropertyChangeEvent(this,
				ProblemSpace.VEHICLE_TYPE, null, v);
		_contribListener.propertyChange(pce);
	}

}
