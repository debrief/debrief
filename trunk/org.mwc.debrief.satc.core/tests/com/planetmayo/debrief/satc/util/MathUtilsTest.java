package com.planetmayo.debrief.satc.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathUtilsTest
{

	@Test
	public void testAngleDiff()
	{
		testTheseDegs(45, 90, 45, false);
		testTheseDegs(90, 45, 45, false);
		testTheseDegs(45, 90, 45, true);
		testTheseDegs(90, 45, 45, true);
		testTheseDegs(-45, 90, 135, false);
		testTheseDegs(90, -45, 135, false);
		testTheseDegs(-45, 90, 135, true);
		testTheseDegs(90, -45, 135, true);
		testTheseDegs(-45, -90, 45, false);
		testTheseDegs(-90, -45, 45, false);
		testTheseDegs(-45, -90, 45, true);
		testTheseDegs(-90, -45, 45, true);
		testTheseDegs(-45, -190, 135, false);
		testTheseDegs(-90, -145, 45, false);
		testTheseDegs(-145, -90, 45, true);
		testTheseDegs(-90, 145, 135, true);
		testTheseDegs(405, 90, 45, false);
		testTheseDegs(90, 405, 45, false);
		testTheseDegs(405, 90, 45, true);
		testTheseDegs(90, 405, 45, true);
		testTheseDegs(-405, 90, 135, false);
		testTheseDegs(90, -405, 135, false);
		testTheseDegs(-405, 90, 135, true);
		testTheseDegs(90, -405, 135, true);
	}

	private void testTheseDegs(double ang1D, double ang2D, double answerD, boolean normalized)
	{
		double ang1R = Math.toRadians(ang1D);
		double ang2R = Math.toRadians(ang2D);
		double answerR = Math.toRadians(answerD);
		assertEquals(answerR, MathUtils.angleDiff(ang1R, ang2R, normalized), 0001);
	}
	
}
