package com.planetmayo.debrief.satc.model.states;


/**
 * class representing a set of speed bounds
 * 
 * @author ian
 * 
 */
public class SpeedRange extends  BaseRange<SpeedRange>
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

	@Override
	public String getConstraintSummary()
	{
		return "" + (int)_minMS + " - " + (int)_maxMS;
	}

}
