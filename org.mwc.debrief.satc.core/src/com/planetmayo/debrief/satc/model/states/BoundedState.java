package com.planetmayo.debrief.satc.model.states;

import java.awt.Color;
import java.util.Date;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.vividsolutions.jts.geom.LineString;

public class BoundedState implements Comparable<BoundedState>
{
	/** allow different types of bounded state - used
	 * as a way of highlighting states that were generated
	 * to support assessing track in alterations.
	 *
	 */
	public static enum BoundedStateType
	{
		LEG, ALTERING;
	}
	
	private final Date _time;

	
	/** type of state
	 * 
	 */
	private final BoundedStateType _stateType;

	private SpeedRange _speed;

	private CourseRange _course;

	private LocationRange _location;

	private LineString _bearingLine;
	
	/**
	 * the leg that this state is a member of
	 * 
	 */
	private String _memberOf;

	/** the (optional) default color for this state
	 * 
	 */
	private Color _color;

	/** the measured bearing at this time (radians)
	 * 
	 */
	private Double _bearing = null;

	public BoundedState(Date time)
	{
		this(time, BoundedStateType.LEG);
	}
	
	public BoundedState(Date time, BoundedStateType type)
	{		
		_stateType = type;
		_time = time;
		if (time == null)
		{
			throw new IllegalArgumentException("time can't be null");
		}
	}

	@Override
	public int compareTo(BoundedState o)
	{
		return getTime().compareTo(o.getTime());
	}

	/**
	 * apply all of the supplied state's constraints to ourselves
	 * 
	 * @param newState
	 */
	public void constrainTo(BoundedState newState)
			throws IncompatibleStateException
	{
		if (newState._speed != null)
			this.constrainTo(newState._speed);
		if (newState._course != null)
			this.constrainTo(newState._course);
		if (newState._location != null)
			this.constrainTo(newState._location);
	}

	/**
	 * apply the specified constraint to ourselves
	 * 
	 * @param range
	 */
	public void constrainTo(CourseRange range) throws IncompatibleStateException
	{
		try 
		{
			// do we have any speed constraints?
			if (_course == null)
			{
				// no, better create some
				_course = new CourseRange(range);
			}
			else
			{
				// yes, further constrain to this set
				_course.constrainTo(range);
			}
		}
		catch (IncompatibleStateException ex) 
		{
			ex.setFailingState(this);
			throw ex;
		}
	}

	/**
	 * apply the specified constraint to ourselves
	 * 
	 * @param range
	 */
	public void constrainTo(LocationRange range)
			throws IncompatibleStateException
	{
		try 
		{
			// do we have any speed constraints?
			if (_location == null)
			{
				// no, better create some
				_location = new LocationRange(range);
			}
			else
			{ // yes, further constrain to this set
				_location.constrainTo(range);
			}
		}
		catch (IncompatibleStateException ex)
		{
			ex.setFailingState(this);
			throw ex;
		}
	}

	/**
	 * apply the specified constraint to ourselves
	 * 
	 * @param range
	 */
	public void constrainTo(SpeedRange range) throws IncompatibleStateException
	{
		try
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
		catch (IncompatibleStateException ex)
		{
			ex.setFailingState(this);
			throw ex;
		}
	}

	public void setMemberOf(String legName)
	{
		_memberOf = legName;
	}

	public String getMemberOf()
	{
		return _memberOf;
	}
	
	public LineString getBearingLine()
	{
		return _bearingLine;
	}

	public void setBearingLine(LineString _bearingLine)
	{
		this._bearingLine = _bearingLine;
	}

	public CourseRange getCourse()
	{
		return _course;
	}

	public LocationRange getLocation()
	{
		return _location;
	}

	public SpeedRange getSpeed()
	{
		return _speed;
	}

	public Date getTime()
	{
		return _time;
	}

	public void setLocation(LocationRange newRange)
	{
		_location = newRange;
	}

	public void setSpeed(SpeedRange newRange)
	{
		_speed = newRange;
	}

	public void setCourse(CourseRange newRange)
	{
		_course = newRange;
	}

	public void setColor(Color color)
	{
		_color = color;
	}
	
	public Color getColor()
	{
		return _color;
	}

	public double getBearing()
	{
		if(_bearing == null)
			throw new RuntimeException("SHOULD HAVE CHECKED IF BEARING PRESENT BEFORE ACCESSING");
		return _bearing;
	}
	
	public boolean hasBearing()
	{
		return (_bearing != null);
	}
	
	public void setBearingValue(Double bearing)
	{
		_bearing = bearing;
	}

	public BoundedStateType getStateType()
	{
		return _stateType;
	}

}
