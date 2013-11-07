package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.geotools.referencing.GeodeticCalculator;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
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

	public AlteringRoute(String name, Point startP, Date startTime, Point endP,
			Date endTime)
	{
		super(startP, endP, startTime, endTime, name, LegType.ALTERING);
		
		GeodeticCalculator calculator = new GeodeticCalculator();
		calculator.setStartingGeographicPoint(startP.getX(), startP.getY());
		calculator.setDestinationGeographicPoint(endP.getX(), endP.getY());
		_directDistance = calculator.getOrthodromicDistance();
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
			GeodeticCalculator calculator = new GeodeticCalculator();
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
		Point p0 = before.getStartPoint(),
					p1 = before.getEndPoint(),
					p2 = after.getStartPoint(),
					p3 = after.getEndPoint();
		
		double coefBefore = before.getElapsedTime() / getElapsedTime();
		double coefAfter = after.getElapsedTime() / getElapsedTime();
		
		double c1x = p1.getX() + (p1.getX() - p0.getX()) / (3 * coefBefore);
		double c1y = p1.getY() + (p1.getY() - p0.getY()) / (3 * coefBefore);
		double c2x = p2.getX() + (p2.getX() - p3.getX()) / (3 * coefAfter);
		double c2y = p2.getY() + (p2.getY() - p3.getY()) / (3 * coefAfter);		
		
		_controlPoints = new Point[] { GeoSupport.createPoint(c1x, c1y), GeoSupport.createPoint(c2x, c2y) };		
		_routeType = AlteringRouteType.CUBIC_BEZIER;
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
}
