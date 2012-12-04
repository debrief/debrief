package com.planetmayo.debrief.satc.model.states;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

import junit.framework.TestCase;

public class CourseRangeTest extends TestCase
{
	public void testCreate()
	{
		final double minS = Math.toRadians(23.4);
		final double maxS = Math.toRadians(34.5);
		CourseRange spdR = new CourseRange(minS, maxS);
		assertEquals("correct lower value", minS, spdR.getMin());
		assertEquals("correct upper value", maxS, spdR.getMax());
	}

	public void testConstrain() throws IncompatibleStateException
	{
		CourseRange sOne = new CourseRange(Math.toRadians(10d), Math.toRadians(20d));
		CourseRange sTwo = new CourseRange(Math.toRadians(12d), Math.toRadians(40d));
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", Math.toRadians(12d), sOne.getMin());
		assertEquals("correct upper", Math.toRadians(20d), sOne.getMax());

		CourseRange sThree = new CourseRange(Math.toRadians(4d), Math.toRadians(16d));
		sOne.constrainTo(sThree);
		assertEquals("correct lower", Math.toRadians(12d), sOne.getMin());
		assertEquals("correct upper", Math.toRadians(16d), sOne.getMax());
	}

	public void testConstrainThroughZeroA() throws IncompatibleStateException
	{
		CourseRange sOne = new CourseRange(Math.toRadians(350d), Math.toRadians(20d));
		CourseRange sTwo = new CourseRange(Math.toRadians(320d), Math.toRadians(40d));
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", Math.toRadians(350d), sOne.getMin());
		assertEquals("correct upper", Math.toRadians(20d), sOne.getMax());

		CourseRange sThree = new CourseRange(Math.toRadians(4d), Math.toRadians(16d));
		sOne.constrainTo(sThree);
		assertEquals("correct lower", Math.toRadians(4d), sOne.getMin());
		assertEquals("correct upper", Math.toRadians(16d), sOne.getMax());
	}

	public void testIncompatibleStatesA() throws IncompatibleStateException
	{
		CourseRange sOne = new CourseRange(Math.toRadians(350d), Math.toRadians(20d));
		CourseRange sTwo = new CourseRange(Math.toRadians(320d), Math.toRadians(330d));
		IncompatibleStateException e = null;
		try
		{
			sOne.constrainTo(sTwo);
		}
		catch (IncompatibleStateException ie)
		{
			e = ie;
		}
		assertNotNull("incompatible exception thrown", e);
	}
	public void testIncompatibleStatesB()
	{
		CourseRange sOne = new CourseRange(Math.toRadians(10d), Math.toRadians(20d));
		CourseRange sTwo = new CourseRange(Math.toRadians(320d), Math.toRadians(330d));
		IncompatibleStateException e = null;
		try
		{
			sOne.constrainTo(sTwo);
		}
		catch (IncompatibleStateException ie)
		{
			e = ie;
		}
		assertNotNull("incompatible exception thrown", e);
		assertEquals("correct existing range", sOne, e.getExistingRange());
		assertEquals("correct new range", sTwo, e.getNewRange());
	}

	public void testConstrainThroughZeroB() throws IncompatibleStateException
	{
		CourseRange sOne = new CourseRange(Math.toRadians(350d), Math.toRadians(20d));
		CourseRange sTwo = new CourseRange(Math.toRadians(320d), Math.toRadians(358d));
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", Math.toRadians(350d), sOne.getMin());
		assertEquals("correct upper", Math.toRadians(358d), sOne.getMax());

		CourseRange sThree = new CourseRange(Math.toRadians(352d), Math.toRadians(16d));
		sOne.constrainTo(sThree);
		assertEquals("correct lower", Math.toRadians(352d), sOne.getMin());
		assertEquals("correct upper", Math.toRadians(358d), sOne.getMax());
	}
}