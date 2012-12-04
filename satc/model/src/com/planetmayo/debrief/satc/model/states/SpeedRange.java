package com.planetmayo.debrief.satc.model.states;

import com.planetmayo.debrief.satc.util.GeoSupport;

/**
 * class representing a set of speed bounds
 * 
 * @author ian
 * 
 */
public class SpeedRange extends BaseRange<SpeedRange>
{
	private double _minSpeed;
	private double _maxSpeed;

	public SpeedRange(double minSpd, double maxSpd)
	{
		_minSpeed = minSpd;
		_maxSpeed = maxSpd;
	}

	/**
	 * copy constructor
	 * 
	 * @param range
	 */
	public SpeedRange(SpeedRange range)
	{
		this(range.getMin(), range.getMax());
	}

	@Override
	public void constrainTo(SpeedRange sTwo) throws IncompatibleStateException
	{
		_minSpeed = Math.max(getMin(), sTwo.getMin());
		_maxSpeed = Math.min(getMax(), sTwo.getMax());
	}

	@Override
	public String getConstraintSummary()
	{
		// get the range, in knots
		int minSpdKts = (int) GeoSupport.MSec2kts(_minSpeed);
		int maxSpdKts = (int) GeoSupport.MSec2kts(_maxSpeed);

		return "" + minSpdKts + " - " + maxSpdKts + " kts";
	}

	public double getMax()
	{
		return _maxSpeed;
	}

	public double getMin()
	{
		return _minSpeed;
	}

	public void setMax(double maxSpeed)
	{
		_maxSpeed = maxSpeed;
	}

	public void setMin(double minSpeed)
	{
		_minSpeed = minSpeed;
	}

}
