package com.planetmayo.debrief.satc.model.states;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public class ProblemSpace
{
	private TreeSet<BoundedState> _boundedStates;

	public ProblemSpace()
	{
		_boundedStates = new TreeSet<BoundedState>();
	}

	/**
	 * add a new bounded state
	 * 
	 * @param newState
	 */
	public void add(BoundedState newState) throws IncompatibleStateException
	{

		// check if this has a date - if it doesn't we'll give it our start/end
		// times
		if (newState.getTime() == null)
		{
			if (size() == 0)
				throw new RuntimeException(
						"we can't accept a null time state, since we don't know our period yet");

			// ok, we'll just apply this state to our start and end times
			_boundedStates.first().constrainTo(newState);
			_boundedStates.last().constrainTo(newState);
		}
		else
			_boundedStates.add(newState);
	}

	protected Date getFinishDate()
	{
		Date res = null;
		if (size() > 0)
		{
			res = _boundedStates.last().getTime();
		}

		return res;
	}

	protected Date getStartDate()
	{
		Date res = null;
		if (size() > 0)
		{
			res = _boundedStates.first().getTime();
		}

		return res;
	}

	/**
	 * iterator through the set of bounded states
	 * 
	 * @return
	 */
	public Iterator<BoundedState> states()
	{
		return _boundedStates.iterator();
	}

	public int size()
	{
		return _boundedStates.size();
	}

	/**
	 * return the bounded state at this time (or null)
	 * 
	 * @param theTime the time we're searching for
	 * @return
	 */
	public BoundedState getBoundedStateAt(Date theTime)
	{
		BoundedState res = null;
		Iterator<BoundedState> iter = _boundedStates.iterator();
		while (iter.hasNext())
		{
			BoundedState boundedState = (BoundedState) iter.next();
			if (boundedState.getTime().equals(theTime))
			{
				res = boundedState;
				break;
			}
		}

		return res;
	}

}
