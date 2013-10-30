package com.planetmayo.debrief.satc.model.states;

import java.awt.Color;
import java.util.Date;

import com.vividsolutions.jts.geom.Point;

/* the state of an object at a specific time
 * 
 */
public class State implements Comparable<State>
{
	private final Date _time;
	private final double _speed;
	private final double _course;
	private final Point _location;
	
	/** the (optional) color used to present this state
	 * 
	 */
	private Color _color;

	public State(final Date time, final Point location, final double course,
			final double speed)
	{
		if (time == null)
		{
			throw new IllegalArgumentException("time can't be null");
		}
		_time = time;
		_location = location;
		_course = course;
		_speed = speed;
	}

	@Override
	public int compareTo(State o)
	{
		return getTime().compareTo(o.getTime());
	}

	public double getCourse()
	{
		return _course;
	}

	public Point getLocation()
	{
		return _location;
	}

	public double getSpeed()
	{
		return _speed;
	}

	public Date getTime()
	{
		return _time;
	}

	public void setColor(Color color)
	{
		_color = color;
	}
	
	public Color getColor()
	{
		return _color;
	}

}
