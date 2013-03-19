package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionDataType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.SupportServices;

/**
 * the top level manager object that handles the generation of bounded
 * constraints
 * 
 * @author ian
 * 
 */
public class BoundsManager implements IBoundsManager
{
	public static final String STATES_BOUNDED = "states_bounded";

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
	 * property listener = so we know about estimates changing
	 * 
	 */
	private final PropertyChangeListener _estimateListener = new PropertyChangeListener()
	{
		@Override
		public void propertyChange(PropertyChangeEvent arg0)
		{
			for (Iterator<PropertyChangeListener> iterator = _estimateChangedListeners
					.iterator(); iterator.hasNext();)
			{
				PropertyChangeListener thisL = iterator.next();
				thisL.propertyChange(arg0);
			}
		}
	};

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
	 * people interested in contributions
	 * 
	 */
	final private ArrayList<IContributionsChangedListener> _contributionListeners = new ArrayList<IContributionsChangedListener>();

	/**
	 * the contribution that we're currently processing
	 * 
	 */
	private BaseContribution _currentContribution = null;

	/**
	 * current step number and current step contribution
	 * 
	 */
	private int _currentStep = 0;

	/**
	 * the set of contribution properties that we're interested in
	 * 
	 */
	private final String[] _interestingProperties =
	{ BaseContribution.ACTIVE, BaseContribution.START_DATE,
			BaseContribution.FINISH_DATE, BaseContribution.HARD_CONSTRAINTS };

	/**
	 * whether we auto=run after each contribution chagne
	 * 
	 */
	private boolean _liveRunning = true;
	/**
	 * the problem space we consider
	 */
	final private ProblemSpace _space = new ProblemSpace();

	/**
	 * people interested in us stepping
	 * 
	 */
	final private ArrayList<IConstrainSpaceListener> _steppingListeners = new ArrayList<IConstrainSpaceListener>();

	/**
	 * keep track of folks interested in estimate changes
	 * 
	 */
	private Collection<PropertyChangeListener> _estimateChangedListeners = new ArrayList<PropertyChangeListener>();

	private IConstrainSpaceListener _mysolGenny;

	private boolean _generateSolutions = false;

	/**
	 * Stepping listeners stuff
	 * 
	 */
	@Override
	public void addBoundStatesListener(IConstrainSpaceListener newListener)
	{
		_steppingListeners.add(newListener);
	}

	/**
	 * store this new contribution
	 * 
	 * @param contribution
	 */
	@Override
	public void addContribution(BaseContribution contribution)
	{
		// remember it
		_contribs.add(contribution);

		// start listening to our interesting properties
		for (String property : _interestingProperties)
		{
			contribution.addPropertyChangeListener(property, _contribListener);
		}

		contribution.addPropertyChangeListener(BaseContribution.ESTIMATE,
				_estimateListener);

		fireContributionAdded(contribution);
	}

	public void addEstimateChangedListener(PropertyChangeListener listener)
	{
		_estimateChangedListeners.add(listener);
	}

	/**
	 * Contribution listeners stuff
	 * 
	 */
	@Override
	public void addContributionsListener(IContributionsChangedListener newListener)
	{
		_contributionListeners.add(newListener);
	}

	/**
	 * ditch all of the contributions
	 * 
	 */
	@Override
	public void clear()
	{
		// ditch the contributions
		for (BaseContribution contribution : new ArrayList<BaseContribution>(
				_contribs))
		{
			this.removeContribution(contribution);
		}

		// clear the probelm space
		_space.clear();

		// tell everyone we've restarted
		this.restart();

	}

	protected void createInitialBoundedStates()
	{
		for (BaseContribution contribution : _contribs)
		{
			// is this contribution active?
			if (contribution.isActive())
			{
				if (contribution.getDataType() == ContributionDataType.FORECAST)
				{
					Date startDate = contribution.getStartDate();
					Date finishDate = contribution.getFinishDate();
					if (startDate != null && _space.getBoundedStateAt(startDate) == null)
					{
						try
						{
							_space.add(new BoundedState(startDate));
						}
						catch (IncompatibleStateException ex)
						{
							// can't be thrown here in any correct situation
							throw new RuntimeException(ex);
						}
					}
					if (finishDate != null
							&& _space.getBoundedStateAt(finishDate) == null)
					{
						try
						{
							_space.add(new BoundedState(finishDate));
						}
						catch (IncompatibleStateException ex)
						{
							// can't be thrown here in any correct situation
							throw new RuntimeException(ex);
						}
					}
				}
			}
		}
	}

	protected void fireComplete()
	{
		for (IConstrainSpaceListener listener : _steppingListeners)
		{
			listener.statesBounded(this);
		}
	}

	protected void fireContributionAdded(BaseContribution contribution)
	{
		for (IContributionsChangedListener listener : _contributionListeners)
		{
			listener.added(contribution);
		}
	}

	protected void fireContributionRemoved(BaseContribution contribution)
	{
		for (IContributionsChangedListener listener : _contributionListeners)
		{
			listener.removed(contribution);
		}
	}

	protected void fireError(IncompatibleStateException ex)
	{
		for (IConstrainSpaceListener listener : _steppingListeners)
		{
			listener.error(this, ex);
		}
	}

	protected void fireRestarted()
	{
		for (IConstrainSpaceListener listener : _steppingListeners)
		{
			listener.restarted(this);
		}
	}

	protected void fireStepped(int thisStep, int totalSteps)
	{
		for (IConstrainSpaceListener listener : _steppingListeners)
		{
			listener.stepped(this, thisStep, totalSteps);
		}
	}

	@Override
	public Collection<BaseContribution> getContributions()
	{
		return Collections.unmodifiableSet(_contribs);
	}

	@Override
	public BaseContribution getCurrentContribution()
	{
		return _currentContribution;
	}

	@Override
	public int getCurrentStep()
	{
		return _currentStep;
	}

	@Override
	public ProblemSpace getSpace()
	{
		return _space;
	}

	@Override
	public boolean isCompleted()
	{
		return _currentStep >= _contribs.size();
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
			}
			fireStepped(stepIndex, _contribs.size());
		}
		catch (IncompatibleStateException e)
		{
			SupportServices.INSTANCE.getLog().error(
					"Failed applying bounds:" + theContrib.getName());
			fireError(e);
		}
		catch (Exception re)
		{
			SupportServices.INSTANCE.getLog().error(
					"unknown error:" + theContrib.getName(), re);
			throw new RuntimeException(re);
		}
	}

	/**
	 * remove this contribution
	 * 
	 * @param contribution
	 */
	@Override
	public void removeContribution(BaseContribution contribution)
	{
		if (!_contribs.contains(contribution))
		{
			return;
		}
		// remember it
		_contribs.remove(contribution);

		// stop listening to it
		for (int i = 0; i < _interestingProperties.length; i++)
		{
			String thisProp = _interestingProperties[i];
			contribution.removePropertyChangeListener(thisProp, _contribListener);
		}
		fireContributionRemoved(contribution);
	}

	@Override
	public void removeContributionsListener(
			IContributionsChangedListener newListener)
	{
		_contributionListeners.remove(newListener);
	}

	@Override
	public void removeSteppingListener(IConstrainSpaceListener newListener)
	{
		_steppingListeners.remove(newListener);
	}

	@Override
	public void restart()
	{
		if (_currentStep == 0)
		{
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

	/**
	 * store the vehicle type
	 * 
	 * @param v
	 *          the new vehicle type
	 */
	@Override
	public void setVehicleType(VehicleType v)
	{
		// store the new value
		_space.setVehicleType(v);

		// and get ourselves to re-run, to reflect this change
		PropertyChangeEvent pce = new PropertyChangeEvent(this,
				ProblemSpace.VEHICLE_TYPE, null, v);
		_contribListener.propertyChange(pce);
	}

	@Override
	public void step()
	{
		if (_currentStep == 0)
		{
			createInitialBoundedStates();
		}
		if (isCompleted())
		{
			// ok, we're done
			return;
		}

		// get next contribution
		_currentContribution = SupportServices.INSTANCE.getUtilsService()
				.higherElement(_contribs, _currentContribution);
		// ok, go for it.
		performSingleStep(_currentContribution, _currentStep);

		// now increment the counter and contribution
		_currentStep++;

		// are we now complete?
		if (_currentStep == _contribs.size())
		{
			// tell any listeners that the final bounds have been updated
			fireComplete();
		}

		// do we have a solution generator?
		if (_generateSolutions && (_mysolGenny != null))
			_mysolGenny.statesBounded(this);
	}

	/**
	 * assign the solution generator
	 * 
	 * @param solutionG
	 */
	public void setGenerator(IConstrainSpaceListener solutionG)
	{
		_mysolGenny = solutionG;
	}

	@Override
	public void setGenerateSolutions(boolean doIt)
	{
		_generateSolutions = doIt;
		
		if (_generateSolutions && (_mysolGenny != null))
			_mysolGenny.statesBounded(this);

	}

}
