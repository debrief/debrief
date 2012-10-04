package com.planetmayo.debrief.satc.model.states;


/** class representing a set of speed bounds
 * 
 * @author ian
 *
 */
public class SpeedRange extends BaseRange
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

	/** copy constructor
	 * 
	 * @param range
	 */
	public SpeedRange(SpeedRange range)
	{
		this(range.getMin(), range.getMax());
	}

	public void constrainTo(SpeedRange sTwo)
	{
		_min = Math.max(getMin(), sTwo.getMin());
		_max = Math.min(getMax(), sTwo.getMax());
	}

}
