package com.planetmayo.debrief.satc.model.states;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

import static org.junit.Assert.*;

public class CourseRangeTest
{
	private static final double EPS = 0.000001d;
	
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