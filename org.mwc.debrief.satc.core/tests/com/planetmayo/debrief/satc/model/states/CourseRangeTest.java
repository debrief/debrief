/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.states;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

import static org.junit.Assert.*;

public class CourseRangeTest extends ModelTestBase
{
	
	@Test
	public void testCreate()
	{
		final double minCourse = Math.toRadians(23.4);
		final double maxCourse = Math.toRadians(34.5);
		CourseRange range = new CourseRange(minCourse, maxCourse);
		assertEquals("correct lower value", minCourse, range.getMin(), EPS);
		assertEquals("correct upper value", maxCourse, range.getMax(), EPS);
	}
	
	@Test
	public void testCloneCreate() {
		final double minCourse = Math.toRadians(23.4);
		final double maxCourse = Math.toRadians(34.5);
		CourseRange range1 = new CourseRange(minCourse, maxCourse);
		CourseRange range2 = new CourseRange(range1);
		assertEquals("correct lower value", minCourse, range2.getMin(), EPS);
		assertEquals("correct upper value", maxCourse, range2.getMax(), EPS);		
	}

	@Test
	public void testConstrain() throws IncompatibleStateException
	{
		CourseRange range1 = new CourseRange(Math.toRadians(10d), Math.toRadians(20d));
		CourseRange range2 = new CourseRange(Math.toRadians(12d), Math.toRadians(40d));
		range1.constrainTo(range2);
		assertEquals("correct lower", Math.toRadians(12d), range1.getMin(), EPS);
		assertEquals("correct upper", Math.toRadians(20d), range1.getMax(), EPS);

		CourseRange range3 = new CourseRange(Math.toRadians(4d), Math.toRadians(16d));
		range1.constrainTo(range3);
		assertEquals("correct lower", Math.toRadians(12d), range1.getMin(), EPS);
		assertEquals("correct upper", Math.toRadians(16d), range1.getMax(), EPS);
	}

	@Test
	public void testConstrainThroughZeroA() throws IncompatibleStateException
	{
		CourseRange range1 = new CourseRange(Math.toRadians(350d), Math.toRadians(20d));
		CourseRange range2 = new CourseRange(Math.toRadians(320d), Math.toRadians(40d));
		range1.constrainTo(range2);
		assertEquals("correct lower", Math.toRadians(350d), range1.getMin(), EPS);
		assertEquals("correct upper", Math.toRadians(20d), range1.getMax(), EPS);

		CourseRange range3 = new CourseRange(Math.toRadians(4d), Math.toRadians(16d));
		range1.constrainTo(range3);
		assertEquals("correct lower", Math.toRadians(4d), range1.getMin(), EPS);
		assertEquals("correct upper", Math.toRadians(16d), range1.getMax(), EPS);
	}
	
	@Test	
	public void testConstrainThroughZeroB() throws IncompatibleStateException
	{
		CourseRange range1 = new CourseRange(Math.toRadians(350d), Math.toRadians(20d));
		CourseRange range2 = new CourseRange(Math.toRadians(320d), Math.toRadians(358d));
		range1.constrainTo(range2);
		assertEquals("correct lower", Math.toRadians(350d), range1.getMin(), EPS);
		assertEquals("correct upper", Math.toRadians(358d), range1.getMax(), EPS);

		CourseRange range3 = new CourseRange(Math.toRadians(352d), Math.toRadians(16d));
		range1.constrainTo(range3);
		assertEquals("correct lower", Math.toRadians(352d), range1.getMin(), EPS);
		assertEquals("correct upper", Math.toRadians(358d), range1.getMax(), EPS);
	}	

	@Test(expected = IncompatibleStateException.class)
	public void testIncompatibleStatesA() throws IncompatibleStateException
	{
		CourseRange range1 = new CourseRange(Math.toRadians(350d), Math.toRadians(20d));
		CourseRange range2 = new CourseRange(Math.toRadians(320d), Math.toRadians(330d));
		range1.constrainTo(range2);
	}

	@Test
	public void testAllows() 
	{
		CourseRange cr = new CourseRange(1.2, 1.9);
		assertTrue("correct res", cr.allows(1.3));
		assertFalse("not allowed", cr.allows(1.0));
		assertFalse("not allowed", cr.allows(1.95));
		assertTrue("correct res", cr.allows(1.3 - Math.PI * 2));
		assertTrue("correct res", cr.allows(1.3 + Math.PI * 2));
		assertTrue("correct res", cr.allows(1.3 - Math.PI * 4));
		assertTrue("correct res", cr.allows(1.3 + Math.PI * 4));
		
		cr = new CourseRange(6.2, 0.3);
		assertTrue("correct res", cr.allows(0.2));
		assertFalse("not allowed", cr.allows(1.0));
		assertFalse("not allowed", cr.allows(6.1));
		assertTrue("correct res", cr.allows(0.3 - Math.PI * 2));
		assertTrue("correct res", cr.allows(0.3 + Math.PI * 2));
		assertTrue("correct res", cr.allows(0.2 - Math.PI * 4));
		assertTrue("correct res", cr.allows(0.2 + Math.PI * 4));
	}

	@Test(expected = IncompatibleStateException.class)	
	public void testIncompatibleStatesB()  throws IncompatibleStateException
	{
		CourseRange range1 = new CourseRange(Math.toRadians(10d), Math.toRadians(20d));
		CourseRange range2 = new CourseRange(Math.toRadians(320d), Math.toRadians(330d));
		try 
		{
			range1.constrainTo(range2);
		}
		catch (IncompatibleStateException ex)
		{
			assertEquals("correct existing range", range1, ex.getExistingRange());
			assertEquals("correct new range", range2, ex.getNewRange());			
			throw ex;
		}
	}
}