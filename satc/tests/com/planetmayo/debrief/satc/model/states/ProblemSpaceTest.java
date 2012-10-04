package com.planetmayo.debrief.satc.model.states;

import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public class ProblemSpaceTest extends TestCase
{
	@SuppressWarnings("deprecation")
	@Test
	public void testMissingDate() throws IncompatibleStateException
	{
		ProblemSpace ps = new ProblemSpace();
		BoundedState b1 = new BoundedState(new Date(2012, 4,4));
		BoundedState b2 = new BoundedState(new Date(2012, 5,4));
		BoundedState b3 = new BoundedState(new Date(2012, 2,4));
		
		ps.add(b1);
		ps.add(b2);
		ps.add(b3);
		
		Iterator<BoundedState> iter = ps.states();
		assertEquals("correct order", b3, iter.next());
		assertEquals("correct order", b1, iter.next());
		assertEquals("correct order", b2, iter.next());
		
	}
	
	@SuppressWarnings("deprecation")
	public void testAddSort() throws IncompatibleStateException
	{
		ProblemSpace ps = new ProblemSpace();
		BoundedState b1 = new BoundedState(new Date(2012, 4,4));
		BoundedState b2 = new BoundedState(new Date(2012, 5,4));
		BoundedState b3 = new BoundedState(new Date(2012, 2,4));
		
		ps.add(b1);
		ps.add(b2);
		ps.add(b3);
		
		Iterator<BoundedState> iter = ps.states();
		assertEquals("correct order", b3, iter.next());
		assertEquals("correct order", b1, iter.next());
		assertEquals("correct order", b2, iter.next());
	}
}