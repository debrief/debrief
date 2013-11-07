package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.geotools.referencing.GeodeticCalculator;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
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
		
		GeodeticCalculator calculator = new GeodeticCalculator();
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
		// what is our time period
		final long elapsed = _endTime.getTime() - _startTime.getTime();

		// find the x & y deltas
		final double startX = _startP.getX();
		final double startY = _startP.getY();
		final double xDelta = _endP.getX() - startX;
		final double yDelta = _endP.getY() - startY;

		// move to our start time
		Iterator<BoundedState> iter = states.iterator();
		while (iter.hasNext())
		{
			// get the next date
			BoundedState thisBoundedState = iter.next();
			Date thisDate = thisBoundedState.getTime();

			// is this after our start time?
			if (!thisDate.before(_startTime))
			{
				if (!thisDate.after(_endTime))
				{
					// ok, consider how far along the route we are
					long delta = thisDate.getTime() - _startTime.getTime();
					double proportion = (delta * 1.0) / elapsed;

					// ok, work out where it is
					Point p = GeoSupport.getFactory().createPoint(
							new Coordinate(startX + proportion * xDelta, startY + proportion
									* yDelta));

					// create the state object
					State newS = new State(thisDate, p, _course, _speed);
					
					// set the color, if we have them
					if(thisBoundedState.getColor() != null)
						newS.setColor(thisBoundedState.getColor());
					

					if (_myStates == null)
						_myStates = new ArrayList<State>();

					// and remember it
					_myStates.add(newS);

				}
				else
				{
					// ok, we're done - drop out
					break;
				}
			}
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