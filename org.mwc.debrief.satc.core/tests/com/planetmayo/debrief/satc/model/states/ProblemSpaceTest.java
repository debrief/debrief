/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc.model.states;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.manager.mock.MockVehicleTypesManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class ProblemSpaceTest extends ModelTestBase
{
	
	private ProblemSpace space;	
	
	private ProblemSpace createTestSpace(VehicleType vehicleType) throws IncompatibleStateException
	{
		ProblemSpace space = new ProblemSpace();
		if (vehicleType != null) 
		{
			space.setVehicleType(vehicleType);
		}
		space.add(new BoundedState(new Date(2012, 4, 4)));
		space.add(new BoundedState(new Date(2012, 5, 4)));
		space.add(new BoundedState(new Date(2012, 2, 4)));
		space.add(new BoundedState(new Date(2012, 3, 4)));
		return space;		
	}
	
	@Before
	public void prepareCommonProblemSpace() throws Exception 
	{
		space = createTestSpace(null);
	}
	
	@Test
	public void testAdd() throws IncompatibleStateException 
	{
		assertEquals("correct size", 4, space.size());		
		for (BoundedState state : space.states()) 
		{
			assertNull(state.getSpeed());
			assertNull(state.getCourse());
			assertNull(state.getLocation());
		}		
	}
	
	@Test
	public void testCorrectSort() throws IncompatibleStateException
	{
		Iterator<BoundedState> iter = space.states().iterator();
		assertEquals("correct size", 4, space.size());
		assertEquals("correct order", new Date(2012, 2, 4), iter.next().getTime());
		assertEquals("correct order", new Date(2012, 3, 4), iter.next().getTime());
		assertEquals("correct order", new Date(2012, 4, 4), iter.next().getTime());
		assertEquals("correct order", new Date(2012, 5, 4), iter.next().getTime());
	}
	
	@Test
	public void testAddWithVehicleType() throws IncompatibleStateException 
	{
		VehicleType type = new MockVehicleTypesManager().getAllTypes().get(0);
		ProblemSpace space = createTestSpace(type);
		SpeedRange range = new SpeedRange(type.getMinSpeed(), type.getMaxSpeed());
		for (BoundedState state : space.states()) 
		{
			assertEquals(range, state.getSpeed());
			assertNull(state.getCourse());
			assertNull(state.getLocation());			
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDobleBoundedState() throws Exception 
	{
		space.add(new BoundedState(new Date(2012, 4, 4)));
	}
	
	@Test
	public void testGetBoundedStateAt() throws Exception 
	{
		Date date1 = new Date(2012, 3, 4);
		Date date2 = new Date(2012, 3, 5);
		assertNotNull(space.getBoundedStateAt(date1));
		assertNull(space.getBoundedStateAt(date2));
	}
	
	@Test
	public void testBoundaries() throws Exception 
	{
		assertEquals(new Date(2012, 2, 4), space.getStartDate());
		assertEquals(new Date(2012, 5, 4), space.getFinishDate());
		
		ProblemSpace cleanSpace = new ProblemSpace();
		assertNull(cleanSpace.getFinishDate());
		assertNull(cleanSpace.getStartDate());
	}
	
	@Test
	public void testClear() throws Exception 
	{
		space.clear();
		assertEquals(0, space.size());
	}
	
	@Test
	public void testGetBoundedStatesBetween() throws Exception 
	{
		Collection<BoundedState> states = space.getBoundedStatesBetween(
				new Date(2012, 1, 4), new Date(2012, 4, 4));

		Iterator<BoundedState> iter = states.iterator();
		assertEquals(3, states.size());
		assertEquals("correct order", new Date(2012, 2, 4), iter.next().getTime());
		assertEquals("correct order", new Date(2012, 3, 4), iter.next().getTime());
		assertEquals("correct order", new Date(2012, 4, 4), iter.next().getTime());	
		
		ProblemSpace cleanSpace = new ProblemSpace();
		states = cleanSpace.getBoundedStatesBetween(
				new Date(2012, 1, 4), new Date(2012, 4, 4));
		assertTrue(states.isEmpty());
	}
}