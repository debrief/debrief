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

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

import static org.junit.Assert.*;

public class SpeedRangeTest extends ModelTestBase
{

	@Test
	public void testCreate()
	{
		final double minSpeed = 23.4;
		final double maxSpeed = 34.5;
		SpeedRange range = new SpeedRange(minSpeed, maxSpeed);
		assertEquals("correct lower value", minSpeed, range.getMin(), EPS);
		assertEquals("correct upper value", maxSpeed, range.getMax(), EPS);
	}
	
	@Test
	public void testCloneCreate()
	{
		final double minSpeed = 23.4;
		final double maxSpeed = 34.5;
		SpeedRange range1 = new SpeedRange(minSpeed, maxSpeed);
		SpeedRange range2 = new SpeedRange(range1);		
		assertEquals("correct lower value", minSpeed, range2.getMin(), EPS);
		assertEquals("correct upper value", maxSpeed, range2.getMax(), EPS);
	}	

	@Test
	public void testConstrain() throws IncompatibleStateException
	{
		SpeedRange range1 = new SpeedRange(10d, 20d);
		SpeedRange range2 = new SpeedRange(12d, 40d);
		range1.constrainTo(range2);
		assertEquals("correct lower", 12d, range1.getMin(), EPS);
		assertEquals("correct upper", 20d, range1.getMax(), EPS);

		SpeedRange range3 = new SpeedRange(4d, 16d);
		range1.constrainTo(range3);
		assertEquals("correct lower", 12d, range1.getMin(), EPS);
		assertEquals("correct upper", 16d, range1.getMax(), EPS);
	}
}