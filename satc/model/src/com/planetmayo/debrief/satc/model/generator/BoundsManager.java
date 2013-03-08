package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionDataType;
import com.planetmayo.debrief.satc.model.generator.ISteppingListener.IConstrainSpaceListener;
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
	 * people interested in contributions
	 * 
	 */
	final private ArrayList<IContributionsChangedListener> _contributionListeners = new ArrayList<IContributionsChangedListener>();

	/**
	 * people interested in us stepping
	 * 
	 */
	final private ArrayList<IConstrainSpaceListener> _steppingListeners = new ArrayList<IConstrainSpaceListener>();

	/**
	 * the problem space we consider
	 */
	final private ProblemSpace _space = new ProblemSpace();
	
	/** the solution generator module
	 * 
	 */
	final private SolutionGenerator _genny = new SolutionGenerator();

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
	{ BaseContribution.ACTIVE, BaseContribution.START_DATE,
			BaseContribution.FINISH_DATE, BaseContribution.HARD_CONSTRAINTS };

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
	 * current step number and current step contribution
	 * 
	 */
	private int _currentStep = 0;
	private BaseContribution _currentContribution = null;

	/**
	 * whether we auto=run after each contribution chagne
	 * 
	 */
	private boolean _liveRunning = true;

	/**
	 * Contribution listeners stuff
	 * 
	 */
	@Override
	public void addContributionsListener(IContributionsChangedListener newListener)
	{
		_contributionListeners.add(newListener);
	}

	@Override
	public void removeContributionsListener(
			IContributionsChangedListener newListener)
	{
		_contributionListeners.remove(newListener);
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

	/**
	 * Stepping listeners stuff
	 * 
	 */
	@Override
	public void addBoundStatesListener(IConstrainSpaceListener newListener)
	{
		_steppingListeners.add(newListener);
	}

	@Override
	public void removeSteppingListener(IConstrainSpaceListener newListener)
	{
		_steppingListeners.remove(newListener);
	}

	protected void fireStepped(int thisStep, int totalSteps)
	{
		for (IConstrainSpaceListener listener : _steppingListeners)
		{
			listener.stepped(this, thisStep, totalSteps);
		}
	}

	protected void fireComplete()
	{
		for (IConstrainSpaceListener listener : _steppingListeners)
		{
			listener.statesBounded(this);
		}
	}

	protected void fireRestarted()
	{
		for (IConstrainSpaceListener listener : _steppingListeners)
		{
			listener.restarted(this);
		}
	}

	protected void fireError(IncompatibleStateException ex)
	{
		for (IConstrainSpaceListener listener : _steppingListeners)
		{
			listener.error(this, ex);
		}
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

		// start listening to it
		for (String property : _interestingProperties)
		{
			contribution.addPropertyChangeListener(property, _contribListener);
		}
		fireContributionAdded(contribution);
	}

	/**
	 * ditch all of the contributions
	 * 
	 */
	@Override
	public void clear()
	{
		this.restart();
		for (BaseContribution contribution : new ArrayList<BaseContribution>(
				_contribs))
		{
			this.removeContribution(contribution);
		}
	}

	@Override
	public Collection<BaseContribution> getContributions()
	{
		return Collections.unmodifiableSet(_contribs);
	}

	@Override
	public int getCurrentStep()
	{
		return _currentStep;
	}

	@Override
	public BaseContribution getCurrentContribution()
	{
		return _currentContribution;
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
	 * indicate whether we do 'run' after each contribution change
	 * 
	 * @return
	 */
	public boolean isLiveEnabled()
	{
		return _liveRunning;
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
	}

	@Override
	public boolean isCompleted()
	{
		return _currentStep >= _contribs.size();
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
	public ProblemSpace getSpace()
	{
		return _space;
	}

}
