package com.planetmayo.debrief.satc.model.generator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.ModelObject;
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
public class TrackGenerator
{
	public static final String STATES_BOUNDED = "states_bounded";

	/** bounded state listeners
	 * 
	 */
	private ArrayList<BoundedStatesListener> _boundedListeners;
	
	/**
	 * the problem space we consider
	 */
	private final ProblemSpace _space = new ProblemSpace();

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
			propChange();
		}
	};

	/**
	 * something has changed - rerun the scenario constraint management
	 * 
	 * @throws IncompatibleStateException
	 * 
	 */
	protected void propChange()
	{
		try
		{
			// ok, re-run our constraint generation
			Iterator<BaseContribution> iter = _contribs.iterator();
			while (iter.hasNext())
			{
				BaseContribution bC = (BaseContribution) iter.next();
				bC.actUpon(_space);
			}

			Iterator<BoundedStatesListener> iter2 = _boundedListeners.iterator();
			while (iter2.hasNext())
			{
				BoundedStatesListener boundedStatesListener = (BoundedStatesListener) iter2
						.next();
				boundedStatesListener.statesBounded(_space.states());
			}
		}
		catch (IncompatibleStateException e)
		{
			
			Iterator<BoundedStatesListener> iter2 = _boundedListeners.iterator();
			while (iter2.hasNext())
			{
				BoundedStatesListener boundedStatesListener = (BoundedStatesListener) iter2
						.next();
				boundedStatesListener.incompatibleStatesIdentified(e);
			}
			// TODO handle the incompatible state problem, see ticket 5:
		  // 	https://bitbucket.org/ianmayo/deb_satc/issue/5/consider-how-to-propagate-incompatible
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
	}

	public Iterator<BaseContribution> contributions()
	{
		return _contribs.iterator();
	}
	
	public void addBoundedStateListener(BoundedStatesListener newListener)
	{
		if(_boundedListeners == null)
			_boundedListeners = new ArrayList<BoundedStatesListener>();
		
		_boundedListeners.add(newListener);
	}
	
	public void removeBoundedStateListener(BoundedStatesListener newListener)
	{
		_boundedListeners.remove(newListener);
	}
	
		
}
