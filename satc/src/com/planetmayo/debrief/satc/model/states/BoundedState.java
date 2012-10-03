package com.planetmayo.debrief.satc.model.states;

import java.util.Date;

public class BoundedState implements Comparable<BoundedState>
{
	private final Date _time;
	private SpeedRange _speed;

	public BoundedState(Date time)
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
		_speed.constrainTo(range);
	}

	@Override
	public int compareTo(BoundedState o)
	{
		return getTime().compareTo(o.getTime());
	}

}
