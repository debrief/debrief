package com.planetmayo.debrief.satc.model.states;

import junit.framework.TestCase;

/** class representing a set of speed bounds
 * 
 * @author ian
 *
 */
public class SpeedRange
{
	private double _min;
	private double _max;
	
	public double getMin()
	{
		return _min;
	}

	public void setMin(double minSpeed)
	{
		_min = minSpeed;
	}

	public double getMax()
	{
		return _max;
	}

	public void setMax(double maxSpeed)
	{
		_max = maxSpeed;
	}

	public SpeedRange(double minSpd, double maxSpd)
	{
		_min = minSpd;
		_max = maxSpd;
	}

	public void constrainTo(SpeedRange sTwo)
	{
		_min = Math.max(getMin(), sTwo.getMin());
		_max = Math.min(getMax(), sTwo.getMax());
	}
	
	
	public static class TestSpeed extends TestCase
	{
		public void testCreate()
		{
			final double minS = 23.4;
			final double maxS = 34.5;
			SpeedRange spdR = new SpeedRange(minS, maxS);
			assertEquals("correct lower value", minS, spdR.getMin());
			assertEquals("correct upper value", maxS, spdR.getMax());
		}
		
		public void testConstrain()
		{
			SpeedRange sOne = new SpeedRange(10d, 20d);
			SpeedRange sTwo = new SpeedRange(12d, 40d);
			sOne.constrainTo(sTwo);
			assertEquals("correct lower", 12d, sOne.getMin());
			assertEquals("correct upper", 20d, sOne.getMax());
			
			SpeedRange sThree = new SpeedRange(4d, 16d);
			sOne.constrainTo(sThree);
			assertEquals("correct lower", 12d, sOne.getMin());
			assertEquals("correct upper", 16d, sOne.getMax());
		}
	}

}
