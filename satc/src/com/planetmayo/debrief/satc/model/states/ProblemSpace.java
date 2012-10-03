package com.planetmayo.debrief.satc.model.states;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import junit.framework.TestCase;

public class ProblemSpace
{
	private TreeSet<BoundedState> _boundedStates;

	public ProblemSpace()
	{
		_boundedStates = new TreeSet<BoundedState>();
	}

	/** iterator through the set of bounded states
	 * 
	 * @return
	 */
	public Iterator<BoundedState> iterator()
	{
		return _boundedStates.iterator();
	}

	/**
	 * add a new bounded state
	 * 
	 * @param startState
	 */
	public void add(BoundedState startState)
	{
		_boundedStates.add(startState);
	}
	
	public static class SpaceTest extends TestCase
	{
		@SuppressWarnings("deprecation")
		public void testAddSort()
		{
			ProblemSpace ps = new ProblemSpace();
			BoundedState b1 = new BoundedState(new Date(2012, 4,4));
			BoundedState b2 = new BoundedState(new Date(2012, 5,4));
			BoundedState b3 = new BoundedState(new Date(2012, 2,4));
			
			ps.add(b1);
			ps.add(b2);
			ps.add(b3);
			
			Iterator<BoundedState> iter = ps.iterator();
			assertEquals("correct order", b3, iter.next());
			assertEquals("correct order", b1, iter.next());
			assertEquals("correct order", b2, iter.next());
		}
	}
}
