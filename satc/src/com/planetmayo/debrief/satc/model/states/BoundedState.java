package com.planetmayo.debrief.satc.model.states;

import java.util.Date;

public class BoundedState implements Comparable<BoundedState>
{
	private Date _time;
	private SpeedRange _speed;

	public BoundedState(Date time)
	{
		_time = time;
	}

	public void setTime(Date time)
	{
		_time = time;
	}

	public Date getTime()
	{
		return _time;
	}

	public SpeedRange getSpeed()
	{
		return _speed;
	}

	/**
	 * apply the specified constraint to ourselves
	 * 
	 * @param range
	 */
	public void constrainTo(SpeedRange range)
	{
		// do we have any speed constraints?
		if (_speed == null)
		{
			// no, better create some
			_speed = new SpeedRange(range);
		}
		else
		{ // yes, further constrain to this set
			_speed.constrainTo(range);
		}
	}

	@Override
	public int compareTo(BoundedState o)
	{
		return getTime().compareTo(o.getTime());
	}

	/** apply all of the supplied state's constraints to ourselves
	 * 
	 * @param newState
	 */
	public void constrainTo(BoundedState newState)
	{
		this.constrainTo(newState._speed);
	}

}
