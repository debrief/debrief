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

import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.vividsolutions.jts.geom.Point;

public class AlteringRoute extends CoreRoute {

	private volatile double _directDistance;

	private volatile AlteringRouteType _routeType = AlteringRouteType.UNDEFINED;
	/**
	 * array of control points for bezier curve: one point in case of quad curve and
	 * two points in case of cubic curve
	 */
	private volatile Point _controlPoints[] = null;

	private volatile double _maxSpeed = -1;
	private volatile double _minSpeed = -1;
	private volatile int _extremumsCount = -1;

	public AlteringRoute(final String name, final Point startP, final Date startTime, final Point endP,
			final Date endTime) {
		super(startP, endP, startTime, endTime, name, LegType.ALTERING);

		// GeodeticCalculator calculator = new GeodeticCalculator();
		// calculator.setStartingGeographicPoint(startP.getX(), startP.getY());
		// calculator.setDestinationGeographicPoint(endP.getX(), endP.getY());
		// _directDistance = calculator.getOrthodromicDistance();
	}

	private void calculateExtremumSpeeds() {
		double xCoeff, yCoeff;
		final GeodeticCalculator calculator = GeoSupport.createCalculator();
		calculator.setStartingGeographicPoint(_startP.getX(), _startP.getY());
		calculator.setDestinationGeographicPoint(_startP.getX() + 1, _startP.getY());
		xCoeff = calculator.getOrthodromicDistance();
		xCoeff *= xCoeff;

		calculator.setDestinationGeographicPoint(_startP.getX(), _startP.getY() + 1);
		yCoeff = calculator.getOrthodromicDistance();
		yCoeff *= yCoeff;

		final long timeDelta = _endTime.getTime() - _startTime.getTime();
		final double p0x = _startP.getX(), p1x = _controlPoints[0].getX(), p2x = _controlPoints[1].getX(),
				p3x = _endP.getX();
		final double p0y = _startP.getY(), p1y = _controlPoints[0].getY(), p2y = _controlPoints[1].getY(),
				p3y = _endP.getY();

		final double c1 = p3x - 3 * p2x + 3 * p1x - p0x;
		final double c2 = p2x - 2 * p1x + p0x;
		final double c3 = p1x - p0x;
		final double c4 = p3y - 3 * p2y + 3 * p1y - p0y;
		final double c5 = p2y - 2 * p1y + p0y;
		final double c6 = p1y - p0y;

		final double[] coefs = new double[4];
		coefs[3] = xCoeff * (2 * c1 * c1) + yCoeff * (2 * c4 * c4);
		coefs[2] = xCoeff * (6 * c1 * c2) + yCoeff * (6 * c4 * c5);
		coefs[1] = xCoeff * (4 * c2 * c2 + 2 * c1 * c3) + yCoeff * (4 * c5 * c5 + 2 * c4 * c6);
		coefs[0] = xCoeff * (2 * c2 * c3) + yCoeff * (2 * c5 * c6);

		double res[] = new double[3];
		final int roots = CubicCurve2D.solveCubic(coefs, res);
		res = Arrays.copyOf(res, roots + 2);
		res[roots] = 0;
		res[roots + 1] = 1;

		_minSpeed = Double.MAX_VALUE;
		_maxSpeed = -Double.MAX_VALUE;
		_extremumsCount = -2;
		for (final double root : res) {
			if (root >= 0 && root <= 1) {
				final long time = _startTime.getTime() + (long) (root * timeDelta);
				final double speed = getSpeed(new Date(time));
				_minSpeed = Math.min(_minSpeed, speed);
				_maxSpeed = Math.max(_maxSpeed, speed);
				_extremumsCount++;
			}
		}
	}

	/**
	 * constructs altering route as bezier curve:
	 *
	 * @param before
	 * @param after
	 */
	public void constructRoute(final StraightRoute before, final StraightRoute after) {
		final Point bStart = before.getStartPoint();
		final Point bEnd = before.getEndPoint();
		final Point aStart = after.getStartPoint();
		final Point aEnd = after.getEndPoint();

		final double coefBefore = before.getElapsedTime() / getElapsedTime();
		final double coefAfter = after.getElapsedTime() / getElapsedTime();

		final double c1x = bEnd.getX() + (bEnd.getX() - bStart.getX()) / (3 * coefBefore);
		final double c1y = bEnd.getY() + (bEnd.getY() - bStart.getY()) / (3 * coefBefore);
		final double c2x = aStart.getX() + (aStart.getX() - aEnd.getX()) / (3 * coefAfter);
		final double c2y = aStart.getY() + (aStart.getY() - aEnd.getY()) / (3 * coefAfter);

		_controlPoints = new Point[] { GeoSupport.createPoint(c1x, c1y), GeoSupport.createPoint(c2x, c2y) };
		_routeType = AlteringRouteType.CUBIC_BEZIER;
	}

	/**
	 * break the line down into a series of points, at the indicated times
	 *
	 */
	@Override
	public void generateSegments(final Collection<BoundedState> states) {
		if (_controlPoints == null) {
			return;
		}
		final double elapsedInSeconds = getElapsedTime();
		for (final BoundedState state : states) {
			final Date currentDate = state.getTime();
			if (!currentDate.before(_startTime) && !currentDate.after(_endTime)) {
				final double delta = (currentDate.getTime() - _startTime.getTime()) / 1000.;
				final double proportion = delta / elapsedInSeconds;

				// create the state object without course and speed for now
				final Point currentPoint = MathUtils.calculateBezier(proportion, _startP, _endP, _controlPoints);
				final State newS = new State(currentDate, currentPoint, getCourse(currentDate), getSpeed(currentDate));
				if (_myStates == null)
					_myStates = new ArrayList<State>();

				// and remember it
				_myStates.add(newS);
			}
		}
	}

	public AlteringRouteType getAlteringRouteType() {
		return _routeType;
	}

	public Point[] getBezierControlPoints() {
		return _controlPoints;
	}

	@Override
	public double getCourse(final Date time) {
		final long timeMs = time.getTime();
		if (timeMs >= _startTime.getTime() && timeMs <= _endTime.getTime()) {
			final double delta = (timeMs - _startTime.getTime()) / 1000.;
			final double elapsed = getElapsedTime();
			final Point vector = MathUtils.calculateBezierDerivative(delta / elapsed, _startP, _endP, _controlPoints);
			return GeoSupport.convertToCompassAngle(MathUtils.calcAngle(vector));
		}
		return -1;
	}

	/**
	 * get the straight line between the endsd
	 *
	 * @return
	 */
	public double getDirectDistance() {
		return _directDistance;
	}

	public int getExtremumsCount() {
		if (_extremumsCount != -1) {
			return _extremumsCount;
		}
		calculateExtremumSpeeds();
		return _extremumsCount;
	}

	public double getMaxSpeed() {
		if (_maxSpeed != -1) {
			return _maxSpeed;
		}
		calculateExtremumSpeeds();
		return _maxSpeed;
	}

	public double getMinSpeed() {
		if (_minSpeed != -1) {
			return _minSpeed;
		}
		calculateExtremumSpeeds();
		return _minSpeed;
	}

	@Override
	public double getSpeed(final Date time) {
		final long timeMs = time.getTime();
		if (timeMs >= _startTime.getTime() && timeMs <= _endTime.getTime()) {
			final double delta = (timeMs - _startTime.getTime()) / 1000.;
			final double elapsed = getElapsedTime();
			final double t = delta / elapsed;
			final Point currentPoint = MathUtils.calculateBezier(t, _startP, _endP, _controlPoints);
			final Point speedVector = MathUtils.calculateBezierDerivative(t, _startP, _endP, _controlPoints);
			final GeodeticCalculator calculator = GeoSupport.createCalculator();
			calculator.setStartingGeographicPoint(currentPoint.getX(), currentPoint.getY());
			calculator.setDestinationGeographicPoint(currentPoint.getX() + speedVector.getX(),
					currentPoint.getY() + speedVector.getY());
			return calculator.getOrthodromicDistance() / elapsed;
		}
		return -1;
	}
}
