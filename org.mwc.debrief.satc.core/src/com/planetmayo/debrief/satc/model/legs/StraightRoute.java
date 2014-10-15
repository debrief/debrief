/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.vividsolutions.jts.geom.Point;

/**
 * a straight line path between two points, with a given speed
 * 
 * @author Ian
 * 
 */
public class StraightRoute extends CoreRoute
{
	/**
	 * the course followed for this route
	 */
	private double _course;

	/**
	 * the speed achieved along this route
	 * 
	 */
	private double _speed;

	/**
	 * the length of this route (m)
	 * 
	 */
	private double _length;

	/**
	 * @param startP
	 * @param startTime
	 * @param endP
	 * @param endTime
	 */
	public StraightRoute(String name, Point startP, Date startTime, Point endP,
			Date endTime)
	{
		super(startP, endP, startTime, endTime, name, LegType.STRAIGHT);
		
		GeodeticCalculator calculator = GeoSupport.createCalculator();
		calculator.setStartingGeographicPoint(startP.getX(), startP.getY());
		calculator.setDestinationGeographicPoint(endP.getX(), endP.getY());
		// find the course (converting it to our compass-oriented coordinate system
		if (! startP.equals(endP)) 
		{
			_course = Math.toRadians(calculator.getAzimuth());
		} 
		else 
		{
			_course = 0;
		}
  	// find the speed
		_length = calculator.getOrthodromicDistance();
		_speed = _length / getElapsedTime();

	}

	public double getCourse()
	{
		return _course;
	}

	public double getSpeed()
	{
		return _speed;
	}
	
	@Override
	public double getSpeed(Date time)
	{
		long timeMs = time.getTime();
		if (timeMs >= _startTime.getTime() && timeMs <= _endTime.getTime()) 
		{		
			return _speed;
		}
		return -1;
	}

	@Override
	public double getCourse(Date time)
	{
		long timeMs = time.getTime();
		if (timeMs >= _startTime.getTime() && timeMs <= _endTime.getTime()) 
		{		
			return _course;
		}
		return -1;			
	}

	/**
	 * break the line down into a series of points, at the indicated times
	 * 
	 */
	@Override
	public void generateSegments(final Collection<BoundedState> states)
	{
		final long elapsed = _endTime.getTime() - _startTime.getTime();
		if (_myStates == null) 
		{
			_myStates = new ArrayList<State>();
		}
		
		for (BoundedState boundedState : states) 
		{
			Date currentDate = boundedState.getTime();
			double delta = currentDate.getTime() - _startTime.getTime();
			
			Point p = MathUtils.calculateBezier(delta / elapsed, _startP, _endP, null);
			State state = new State(currentDate, p, _course, _speed);
			if(boundedState.getColor() != null)
			{
				state.setColor(boundedState.getColor());
			}
			_myStates.add(state);
		}
	}

	/**
	 * get the length of this route
	 * 
	 * @return
	 */
	public double getDistance()
	{
		return _length;
	}
}