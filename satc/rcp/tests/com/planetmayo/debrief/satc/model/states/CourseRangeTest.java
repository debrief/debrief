package com.planetmayo.debrief.satc.model.states;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

import junit.framework.TestCase;

public class CourseRangeTest extends TestCase
{
	public void testCreate()
	{
		final double minS = 23.4;
		final double maxS = 34.5;
		CourseRange spdR = new CourseRange(Math.toRadians(minS), Math.toRadians(maxS));
		assertEquals("correct lower value", minS,Math.toDegrees( spdR.getMin()), 0.001);
		assertEquals("correct upper value", maxS,Math.toDegrees( spdR.getMax()), 0.001);
	}

	public void testConstrain() throws IncompatibleStateException
	{
		CourseRange sOne = new CourseRange(Math.toRadians(10d), Math.toRadians(20d));
		CourseRange sTwo = new CourseRange(Math.toRadians(12d), Math.toRadians(40d));
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", 12d,Math.toDegrees( sOne.getMin()), 0.001);
		assertEquals("correct upper", 20d,Math.toDegrees( sOne.getMax()), 0.001);

		CourseRange sThree = new CourseRange(Math.toRadians(4d), Math.toRadians(16d));
		sOne.constrainTo(sThree);
		assertEquals("correct lower", 12d, Math.toDegrees(sOne.getMin()), 0.001);
		assertEquals("correct upper", 16d, Math.toDegrees(sOne.getMax()), 0.001);
	}

	public void testConstrainThroughZeroA() throws IncompatibleStateException
	{
		CourseRange sOne = new CourseRange(Math.toRadians(350d), Math.toRadians(20d));
		CourseRange sTwo = new CourseRange(Math.toRadians(320d), Math.toRadians(40d));
		sOne.constrainTo(sTwo);
		assertEquals("correct lower",350d,Math.toDegrees( sOne.getMin()), 0.001);
		assertEquals("correct upper", 20d, Math.toDegrees(sOne.getMax()), 0.001);

		CourseRange sThree = new CourseRange(Math.toRadians(4d), Math.toRadians(16d));
		sOne.constrainTo(sThree);
		assertEquals("correct lower", 4d,Math.toDegrees( sOne.getMin()), 0.001);
		assertEquals("correct upper", 16d,Math.toDegrees( sOne.getMax()), 0.001);
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
	
	public void testCrossZero() throws IncompatibleStateException
	{
		CourseRange sOne = new CourseRange(Math.toRadians(220), Math.toRadians(315));
		CourseRange sTwo = new CourseRange(Math.toRadians(225), Math.toRadians(5));
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", 225d, Math.toDegrees( sOne.getMin()));
		assertEquals("correct upper", 315d, Math.toDegrees( sOne.getMax()));

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
		assertEquals("correct lower", 350d, Math.toDegrees(sOne.getMin()), 0.001);
		assertEquals("correct upper", 358d, Math.toDegrees(sOne.getMax()), 0.001);

		CourseRange sThree = new CourseRange(Math.toRadians(352d), Math.toRadians(16d));
		sOne.constrainTo(sThree);
		assertEquals("correct lower", 352d, Math.toDegrees(sOne.getMin()), 0.001);
		assertEquals("correct upper", 358d, Math.toDegrees(sOne.getMax()), 0.001);
	}
}