/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.State;
import com.vividsolutions.jts.geom.Point;

public abstract class CoreRoute
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
	
	/** whether we're in a straight or altering leg
	 * 
	 */
	private LegType _legType;
	
	protected CoreRoute(Point startP, Point endP, Date startTime, Date endTime,
			String name, LegType legType)
	{
		_startP = startP;
		_endP = endP;
		_startTime = startTime;
		_endTime = endTime;
		_name = name;
		_legType = legType;
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

	public double getElapsedTime()
	{
		return (_endTime.getTime() - _startTime.getTime()) / 1000.0;
	}

	public Point getStartPoint()
	{
		return _startP;
	}

	public Point getEndPoint()
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
	
	public State getStateAt(Date time)
	{
		for (State state : _myStates)
		{
			if (state.getTime().equals(time))
			{
				return state;
			}
		}
		return null;
	}
	
	public LegType getType()
	{
		return _legType;
	}

	public void setScore(Double thisScore)
	{
		_myScore = thisScore;
	}

	public double getScore()
	{
		return _myScore;
	}
	
	public Date getStartTime()
	{
		return _startTime;
	}

	public Date getEndTime()
	{
		return _endTime;
	}

	public void generateSegments(final Collection<BoundedState> states) 
	{
		
	}
	
	public abstract double getSpeed(Date time);
	
	public abstract double getCourse(Date time);

}