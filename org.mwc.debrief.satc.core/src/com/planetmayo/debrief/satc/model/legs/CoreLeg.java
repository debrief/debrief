/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.vividsolutions.jts.geom.Point;

public abstract class CoreLeg {

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

	protected CoreLeg(final String name) {
		_states = new ArrayList<BoundedState>();
		_name = name;
	}

	protected CoreLeg(final String name, final List<BoundedState> states) {
		this(name);
		if (states != null) {
			add(states);
		}
	}

	/**
	 * add this bounded state
	 *
	 * @param thisS
	 */
	final public void add(final BoundedState state) {
		_states.add(state);
	}

	final public void add(final List<BoundedState> states) {
		_states.addAll(states);
	}

	public void addEndPoints(final List<Point> points) {
		if (endPoints != null) {
			endPoints.addAll(points);
		}
	}

	public void addStartPoints(final List<Point> points) {
		if (startPoints != null) {
			startPoints.addAll(points);
		}
	}

	abstract public CoreRoute createRoute(Point start, Point end, String name);

	/**
	 * determine which legs are achievable
	 *
	 */
	abstract public void decideAchievableRoute(CoreRoute route);

	/**
	 * produce the set of constituent routes for this leg
	 *
	 * @param precision how many grid cells to dissect the area into
	 */
	public void generatePoints(final int numPoints) {
		currentGridPrecision = numPoints;

		// produce the grid of cells
		final LocationRange firstLoc = getFirst().getLocation();
		final LocationRange lastLoc = getLast().getLocation();

		if ((firstLoc == null) || (lastLoc == null))
			throw new IllegalArgumentException("The end states must have location bounds");

		// ok, get gridding
		startPoints = MakeGrid.ST_Tile(firstLoc.getGeometry(), numPoints, 6);
		endPoints = MakeGrid.ST_Tile(lastLoc.getGeometry(), numPoints, 6);

		// just check we've been able to create some points
		if (startPoints.size() == 0)
			throw new RuntimeException("Unable to generate any start points for leg:" + this.getName());
		if (endPoints.size() == 0)
			throw new RuntimeException("Unable to generate any end points for leg:" + this.getName());
	}

	public int getCurrentGridPrecision() {
		return currentGridPrecision;
	}

	public List<Point> getEndPoints() {
		if (endPoints == null) {
			return null;
		}
		return Collections.unmodifiableList(endPoints);
	}

	final public BoundedState getFirst() {
		return _states.get(0);
	}

	final public BoundedState getLast() {
		return _states.get(_states.size() - 1);
	}

	final public String getName() {
		return _name;
	}

	public List<Point> getStartPoints() {
		if (startPoints == null) {
			return null;
		}
		return Collections.unmodifiableList(startPoints);
	}

	final public List<BoundedState> getStates() {
		return _states;
	}

	/**
	 * find out if this is straight or altering
	 *
	 * @return
	 */
	abstract public LegType getType();
}