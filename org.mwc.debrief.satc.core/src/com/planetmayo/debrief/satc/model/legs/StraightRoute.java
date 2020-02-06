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
public class StraightRoute extends CoreRoute {
	/**
	 * the course followed for this route
	 */
	private double _course;

	/**
	 * the speed achieved along this route
	 *
	 */
	private final double _speed;

	/**
	 * the length of this route (m)
	 *
	 */
	private final double _length;

	/**
	 * @param startP
	 * @param startTime
	 * @param endP
	 * @param endTime
	 */
	public StraightRoute(final String name, final Point startP, final Date startTime, final Point endP,
			final Date endTime) {
		super(startP, endP, startTime, endTime, name, LegType.STRAIGHT);

		final GeodeticCalculator calculator = GeoSupport.createCalculator();
		calculator.setStartingGeographicPoint(startP.getX(), startP.getY());
		calculator.setDestinationGeographicPoint(endP.getX(), endP.getY());
		// find the course (converting it to our compass-oriented coordinate system
		if (!startP.equals(endP)) {
			_course = Math.toRadians(calculator.getAzimuth());
		} else {
			_course = 0;
		}
		// find the speed
		_length = calculator.getOrthodromicDistance();
		_speed = _length / getElapsedTime();

	}

	/**
	 * break the line down into a series of points, at the indicated times
	 *
	 */
	@Override
	public void generateSegments(final Collection<BoundedState> states) {
		final long elapsed = _endTime.getTime() - _startTime.getTime();
		if (_myStates == null) {
			_myStates = new ArrayList<State>();
		}

		for (final BoundedState boundedState : states) {
			final Date currentDate = boundedState.getTime();
			final double delta = currentDate.getTime() - _startTime.getTime();

			final Point p = MathUtils.calculateBezier(delta / elapsed, _startP, _endP, null);
			final State state = new State(currentDate, p, _course, _speed);
			if (boundedState.getColor() != null) {
				state.setColor(boundedState.getColor());
			}
			_myStates.add(state);
		}
	}

	public double getCourse() {
		return _course;
	}

	@Override
	public double getCourse(final Date time) {
		final long timeMs = time.getTime();
		if (timeMs >= _startTime.getTime() && timeMs <= _endTime.getTime()) {
			return _course;
		}
		return -1;
	}

	/**
	 * get the length of this route
	 *
	 * @return
	 */
	public double getDistance() {
		return _length;
	}

	public double getSpeed() {
		return _speed;
	}

	@Override
	public double getSpeed(final Date time) {
		final long timeMs = time.getTime();
		if (timeMs >= _startTime.getTime() && timeMs <= _endTime.getTime()) {
			return _speed;
		}
		return -1;
	}
}