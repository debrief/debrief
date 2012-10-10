package com.planetmayo.debrief.satc.model.states;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

import junit.framework.TestCase;

public class SpeedRangeTest extends TestCase
{
	public void testCreate()
	{
		final double minS = 23.4;
		final double maxS = 34.5;
		SpeedRange spdR = new SpeedRange(minS, maxS);
		assertEquals("correct lower value", minS, spdR.getMinMS());
		assertEquals("correct upper value", maxS, spdR.getMaxMS());
	}

	public void testConstrain() throws IncompatibleStateException
	{
		SpeedRange sOne = new SpeedRange(10d, 20d);
		SpeedRange sTwo = new SpeedRange(12d, 40d);
		sOne.constrainTo(sTwo);
		assertEquals("correct lower", 12d, sOne.getMinMS());
		assertEquals("correct upper", 20d, sOne.getMaxMS());

		SpeedRange sThree = new SpeedRange(4d, 16d);
		sOne.constrainTo(sThree);
		assertEquals("correct lower", 12d, sOne.getMinMS());
		assertEquals("correct upper", 16d, sOne.getMaxMS());
	}
}