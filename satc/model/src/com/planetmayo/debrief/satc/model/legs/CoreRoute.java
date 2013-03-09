package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Date;

import com.planetmayo.debrief.satc.model.states.State;
import com.vividsolutions.jts.geom.Point;

public class CoreRoute
{

	/**
	 * whether this route is possible. Routes start off as possible, and we mark
	 * them as impossible through successive tests
	 * 
	 */
	protected boolean _isPossible = true;
	/**
	 * the start point
	 * 
	 */
	protected final Point _startP;
	/**
	 * the end point
	 * 
	 */
	protected final Point _endP;
	/**
	 * the start time
	 * 
	 */
	protected final Date _startTime;
	/**
	 * the end time
	 * 
	 */
	protected final Date _endTime;
	/**
	 * the series of states that this route passes through
	 * 
	 */
	protected ArrayList<State> _myStates = null;
	/**
	 * the identifier for this route
	 * 
	 */
	protected final String _name;

	/** the performance score for this route
	 * 
	 */
	protected double _myScore;
	
	protected CoreRoute(Point startP, Point endP, Date startTime, Date endTime,
			String name)
	{
		_startP = startP;
		_endP = endP;
		_startTime = startTime;
		_endTime = endTime;
		_name = name;
	}

	public State first()
	{
		return _myStates.get(0);
	}

	public State last()
	{
		return _myStates.get(_myStates.size() - 1);
	}

	public String getName()
	{
		return _name;
	}

	public long getElapsedTime()
	{
		return (_endTime.getTime() - _startTime.getTime()) / 1000;
	}

	public Point getStartPoint()
	{
		return _startP;
	}

	public Point getEndPoing()
	{
		return _endP;
	}

	/**
	 * indicate that this route is not achievable
	 * 
	 */
	public void setImpossible()
	{
		_isPossible = false;
	}

	public boolean isPossible()
	{
		return _isPossible;
	}

	public ArrayList<State> getStates()
	{
		return _myStates;
	}

	public void setScore(Double thisScore)
	{
		_myScore = thisScore;
	}

	public double getScore()
	{
		return _myScore;
	}

}