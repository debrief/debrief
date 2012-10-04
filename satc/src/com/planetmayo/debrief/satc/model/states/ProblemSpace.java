package com.planetmayo.debrief.satc.model.states;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

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
	public void add(BoundedState newState)
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
	public Iterator<BoundedState> iterator()
	{
		return _boundedStates.iterator();
	}

	public int size()
	{
		return _boundedStates.size();
	}
}
