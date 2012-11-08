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
	private double _minMS;
	private double _maxMS;

	public SpeedRange(double minSpd, double maxSpd)
	{
		_minMS = minSpd;
		_maxMS = maxSpd;
	}

	/**
	 * copy constructor
	 * 
	 * @param range
	 */
	public SpeedRange(SpeedRange range)
	{
		this(range.getMinMS(), range.getMaxMS());
	}

	@Override
	public void constrainTo(SpeedRange sTwo) throws IncompatibleStateException
	{
		_minMS = Math.max(getMinMS(), sTwo.getMinMS());
		_maxMS = Math.min(getMaxMS(), sTwo.getMaxMS());
	}

	@Override
	public String getConstraintSummary()
	{
		// get the range, in knots
		int minSpdKts = (int) GeoSupport.MSec2kts(_minMS);
		int maxSpdKts = (int) GeoSupport.MSec2kts(_maxMS);

		return "" + minSpdKts + " - " + maxSpdKts + " kts";
	}

	public double getMaxMS()
	{
		return _maxMS;
	}

	public double getMinMS()
	{
		return _minMS;
	}

	public void setMaxMS(double maxSpeed)
	{
		_maxMS = maxSpeed;
	}

	public void setMinMS(double minSpeed)
	{
		_minMS = minSpeed;
	}

}
