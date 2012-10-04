package com.planetmayo.debrief.satc.model.states;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

import junit.framework.TestCase;

public class CourseRangeTest extends TestCase
{
	public void testCreate()
	{
		final double minS = 23.4;
		final double maxS = 34.5;
		CourseRange spdR = new CourseRange(minS, maxS);
		assertEquals("correct lower value", minS, spdR.getMin());
		assertEquals("correct upper value", maxS, spdR.getMax());
	}

	public void testConstrain()
	{
		CourseRange sOne = new CourseRange(10d, 20d);
		CourseRange sTwo = new CourseRange(12d, 40d);
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", 12d, sOne.getMin());
		assertEquals("correct upper", 20d, sOne.getMax());

		CourseRange sThree = new CourseRange(4d, 16d);
		sOne.constrainTo(sThree);
		assertEquals("correct lower", 12d, sOne.getMin());
		assertEquals("correct upper", 16d, sOne.getMax());
	}

	public void testConstrainThroughZeroA()
	{
		CourseRange sOne = new CourseRange(350d, 20d);
		CourseRange sTwo = new CourseRange(320d, 40d);
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", 350d, sOne.getMin());
		assertEquals("correct upper", 20d, sOne.getMax());

		CourseRange sThree = new CourseRange(4d, 16d);
		sOne.constrainTo(sThree);
		assertEquals("correct lower", 4d, sOne.getMin());
		assertEquals("correct upper", 16d, sOne.getMax());
	}

	public void testIncompatibleStatesA()
	{
		CourseRange sOne = new CourseRange(350d, 20d);
		CourseRange sTwo = new CourseRange(320d, 330d);
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
		CourseRange sOne = new CourseRange(10d, 20d);
		CourseRange sTwo = new CourseRange(320d, 330d);
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

	public void testConstrainThroughZeroB()
	{
		CourseRange sOne = new CourseRange(350d, 20d);
		CourseRange sTwo = new CourseRange(320d, 358d);
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", 350d, sOne.getMin());
		assertEquals("correct upper", 358d, sOne.getMax());

		CourseRange sThree = new CourseRange(352d, 16d);
		sOne.constrainTo(sThree);
		assertEquals("correct lower", 352d, sOne.getMin());
		assertEquals("correct upper", 358d, sOne.getMax());
	}
}