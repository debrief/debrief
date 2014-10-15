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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

public class AlteringRoute extends CoreRoute
{

	private volatile double _directDistance;	
	
	private volatile AlteringRouteType _routeType = AlteringRouteType.UNDEFINED;
	/**
	 * array of control points for bezier curve: one point in 
	 * case of quad curve and two points in case of cubic curve   
	 */	
	private volatile Point _controlPoints[] = null;
	
	private volatile double _maxSpeed = -1;
	private volatile double _minSpeed = -1;
	private volatile int _extremumsCount = -1;

	public AlteringRoute(String name, Point startP, Date startTime, Point endP,
			Date endTime)
	{
		super(startP, endP, startTime, endTime, name, LegType.ALTERING);
		
		//GeodeticCalculator calculator = new GeodeticCalculator();
		//calculator.setStartingGeographicPoint(startP.getX(), startP.getY());
		//calculator.setDestinationGeographicPoint(endP.getX(), endP.getY());
		//_directDistance = calculator.getOrthodromicDistance();
	}

	/**
	 * break the line down into a series of points, at the indicated times
	 * 
	 */
	@Override	
	public void generateSegments(final Collection<BoundedState> states)
	{
		if (_controlPoints == null) 
		{
			return;
		}
		final double elapsedInSeconds = getElapsedTime();
		for (BoundedState state : states)
		{
			Date currentDate = state.getTime();
			if (! currentDate.before(_startTime) && ! currentDate.after(_endTime)) 
			{
				double delta = (currentDate.getTime() - _startTime.getTime()) / 1000.;
				double proportion = delta / elapsedInSeconds;
				
				// create the state object without course and speed for now
				Point currentPoint = MathUtils.calculateBezier(proportion, _startP, _endP, _controlPoints);
				State newS = new State(currentDate, currentPoint, getCourse(currentDate), getSpeed(currentDate));
				if (_myStates == null)
					_myStates = new ArrayList<State>();

				// and remember it
				_myStates.add(newS);				
			}
		}
	}

	public AlteringRouteType getAlteringRouteType()
	{
		return _routeType;
	}
	
	@Override
	public double getSpeed(Date time)
	{
		long timeMs = time.getTime();
		if (timeMs >= _startTime.getTime() && timeMs <= _endTime.getTime()) 
		{
			double delta = (timeMs - _startTime.getTime()) / 1000.;
			double elapsed = getElapsedTime();
			double t = delta / elapsed;
			Point currentPoint = MathUtils.calculateBezier(t, _startP, _endP, _controlPoints);
			Point speedVector = MathUtils.calculateBezierDerivative(t, _startP, _endP, _controlPoints);
			GeodeticCalculator calculator = GeoSupport.createCalculator();
			calculator.setStartingGeographicPoint(currentPoint.getX(), currentPoint.getY());
			calculator.setDestinationGeographicPoint(currentPoint.getX() + speedVector.getX(), currentPoint.getY() + speedVector.getY());
			return calculator.getOrthodromicDistance() / elapsed;
		}
		return -1;
	}

	@Override
	public double getCourse(Date time)
	{
		long timeMs = time.getTime();
		if (timeMs >= _startTime.getTime() && timeMs <= _endTime.getTime()) 
		{
			double delta = (timeMs - _startTime.getTime()) / 1000.;
			double elapsed = getElapsedTime();
			Point vector = MathUtils.calculateBezierDerivative(delta / elapsed, _startP, _endP, _controlPoints);
			return GeoSupport.convertToCompassAngle(MathUtils.calcAngle(vector)); 
		}
		return -1;
	}

	/**
   * constructs altering route as bezier curve:
	 * 
	 * @param before
	 * @param after
	 */
	public void constructRoute(StraightRoute before, StraightRoute after) 
	{
		final Point bStart = before.getStartPoint();
		final Point bEnd = before.getEndPoint();
		final Point aStart = after.getStartPoint();
		final Point aEnd = after.getEndPoint();
		
		double coefBefore = before.getElapsedTime() / getElapsedTime();
		double coefAfter = after.getElapsedTime() / getElapsedTime();
		
		double c1x = bEnd.getX() + (bEnd.getX() - bStart.getX()) / (3 * coefBefore);
		double c1y = bEnd.getY() + (bEnd.getY() - bStart.getY()) / (3 * coefBefore);
		double c2x = aStart.getX() + (aStart.getX() - aEnd.getX()) / (3 * coefAfter);
		double c2y = aStart.getY() + (aStart.getY() - aEnd.getY()) / (3 * coefAfter);		
		
		_controlPoints = new Point[] { GeoSupport.createPoint(c1x, c1y), GeoSupport.createPoint(c2x, c2y) };		
		_routeType = AlteringRouteType.CUBIC_BEZIER;
	}
	
	public double getMaxSpeed() 
	{
		if (_maxSpeed != -1) 
		{
			return _maxSpeed;
		}
		calculateExtremumSpeeds();
		return _maxSpeed;
	}
	
	public double getMinSpeed() 
	{
		if (_minSpeed != -1) 
		{
			return _minSpeed;
		}
		calculateExtremumSpeeds();
		return _minSpeed;		
	}	
	
	public int getExtremumsCount()
	{
		if (_extremumsCount != -1)
		{
			return _extremumsCount;
		}
		calculateExtremumSpeeds();
		return _extremumsCount;
	}

	/**
	 * get the straight line between the endsd
	 * 
	 * @return
	 */
	public double getDirectDistance()
	{
		return _directDistance;
	}
	
	public Point[] getBezierControlPoints() 
	{
		return _controlPoints;
	}
	
	private void calculateExtremumSpeeds()
	{
		double xCoeff, yCoeff; 
		GeodeticCalculator calculator = GeoSupport.createCalculator();
		calculator.setStartingGeographicPoint(_startP.getX(), _startP.getY());
		calculator.setDestinationGeographicPoint(_startP.getX() + 1, _startP.getY());
		xCoeff = calculator.getOrthodromicDistance();
		xCoeff *= xCoeff;
		
		calculator.setDestinationGeographicPoint(_startP.getX(), _startP.getY() + 1);
		yCoeff = calculator.getOrthodromicDistance();
		yCoeff *= yCoeff;
		
		long timeDelta = _endTime.getTime() - _startTime.getTime();
		double p0x = _startP.getX(), p1x = _controlPoints[0].getX(), p2x = _controlPoints[1].getX(), p3x = _endP.getX();
		double p0y = _startP.getY(), p1y = _controlPoints[0].getY(), p2y = _controlPoints[1].getY(), p3y = _endP.getY();

		double c1 = p3x - 3 * p2x + 3 * p1x - p0x;
		double c2 = p2x - 2 * p1x + p0x;
		double c3 = p1x - p0x;
		double c4 = p3y - 3 * p2y + 3 * p1y - p0y;
		double c5 = p2y - 2 * p1y + p0y;
		double c6 = p1y - p0y;

		double[] coefs = new double[4];
		coefs[3] = xCoeff * (2 * c1 * c1) + yCoeff * (2 * c4 * c4);
		coefs[2] = xCoeff * (6 * c1 * c2) + yCoeff * (6 * c4 * c5);
		coefs[1] = xCoeff * (4 * c2 * c2  + 2 * c1 * c3) + yCoeff * (4 * c5 * c5 + 2 * c4 * c6);
		coefs[0] = xCoeff * (2 * c2 * c3) + yCoeff * (2 * c5 * c6);
		
		double res[] = new double[3];		
		int roots = CubicCurve2D.solveCubic(coefs, res);
		res = Arrays.copyOf(res, roots + 2);
		res[roots] = 0;
		res[roots + 1] = 1;
		
		_minSpeed = Double.MAX_VALUE;
		_maxSpeed = -Double.MAX_VALUE;
		_extremumsCount = -2;
		for (double root : res) 
		{
			if (root >= 0 && root <= 1)
			{
				long time = _startTime.getTime() + (long) (root * timeDelta);
				double speed = getSpeed(new Date(time));
				_minSpeed = Math.min(_minSpeed, speed);
				_maxSpeed = Math.max(_maxSpeed, speed);
				_extremumsCount++;
			}
		}
	}
}
