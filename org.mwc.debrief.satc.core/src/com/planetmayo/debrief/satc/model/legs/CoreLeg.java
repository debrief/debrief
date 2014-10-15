/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.vividsolutions.jts.geom.Point;

public abstract class CoreLeg
{
	
	/**
	 * how many points there are in the start polygon
	 * 
	 */
	protected int _startLen;

	/**
	 * how many points there are in the end polygon
	 * 
	 */
	protected int _endLen;

	/**
	 * a name for the leg
	 * 
	 */
	protected final String _name;

	/**
	 * the set of bounded states
	 * 
	 */
	protected final List<BoundedState> _states;
	
	protected List<Point> startPoints;
	
	protected List<Point> endPoints;
	
	protected int currentGridPrecision;

	protected CoreLeg(String name)
	{
		_states = new ArrayList<BoundedState>();
		_name = name;
	}
	
	protected CoreLeg(String name, List<BoundedState> states)
	{
		this(name);
		if (states != null)
		{
			add(states);
		}
	}	

	/**
	 * add this bounded state
	 * 
	 * @param thisS
	 */
	final public void add(BoundedState state)
	{
		_states.add(state);
	}
	
	final public void add(List<BoundedState> states)
	{
		_states.addAll(states);
	}	

	final public BoundedState getFirst()
	{
		return _states.get(0);
	}

	final public BoundedState getLast()
	{
		return _states.get(_states.size() - 1);
	}

	final public String getName()
	{
		return _name;
	}

	final public List<BoundedState> getStates()
	{
		return _states;
	}

	public List<Point> getStartPoints()
	{
		if (startPoints == null)
		{
			return null;
		}
		return Collections.unmodifiableList(startPoints);
	}

	public List<Point> getEndPoints()
	{
		if (endPoints == null)
		{
			return null;
		}
		return Collections.unmodifiableList(endPoints);
	}
	
	public void addStartPoints(List<Point> points)
	{
		if (startPoints != null)
		{
			startPoints.addAll(points);
		}
	}
	
	public void addEndPoints(List<Point> points)
	{
		if (endPoints != null)
		{
			endPoints.addAll(points);
		}
	}
	

	/**
	 * produce the set of constituent routes for this leg
	 * 
	 * @param precision
	 *          how many grid cells to dissect the area into
	 */
	public void generatePoints(int numPoints)
	{
		currentGridPrecision = numPoints;
		
		// produce the grid of cells
		LocationRange firstLoc = getFirst().getLocation();
		LocationRange lastLoc = getLast().getLocation();

		if ((firstLoc == null) || (lastLoc == null))
			throw new IllegalArgumentException(
					"The end states must have location bounds");

		// ok, get gridding
		startPoints = MakeGrid.ST_Tile(firstLoc.getGeometry(),
				numPoints, 6);
		endPoints = MakeGrid.ST_Tile(lastLoc.getGeometry(), numPoints, 6);
		
		// just check we've been able to create some points
		if(startPoints.size() == 0) 
				throw new RuntimeException("Unable to generate any start points for leg:" + this.getName());
		if(endPoints.size() == 0)
			throw new RuntimeException("Unable to generate any end points for leg:" + this.getName());
	}
	
	public int getCurrentGridPrecision() 
	{
		return currentGridPrecision;
	}

	/**
	 * find out if this is straight or altering
	 * 
	 * @return
	 */
	abstract public LegType getType();

	/**
	 * determine which legs are achievable
	 * 
	 */
	abstract public void decideAchievableRoute(CoreRoute route);
	
	abstract public CoreRoute createRoute(Point start, Point end, String name);
}