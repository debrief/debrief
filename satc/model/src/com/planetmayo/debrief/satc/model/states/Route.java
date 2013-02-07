package com.planetmayo.debrief.satc.model.states;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.math.Vector2D;

public class Route
{
	/**
	 * whether this route is possible. Routes start off as possible, and we mark
	 * them as impossible through successive tests
	 * 
	 */
	private boolean _isPossible = true;

	/**
	 * the sum of errors representing the performance of this route
	 * 
	 */
	private double _errorSum;

	/**
	 * the start point
	 * 
	 */
	private final Point _startP;

	/**
	 * the end point
	 * 
	 */
	private final Point _endP;

	/**
	 * the start time
	 * 
	 */
	private final Date _startTime;
	/**
	 * the end time
	 * 
	 */
	private final Date _endTime;

	/**
	 * the series of points that this route represents
	 * 
	 */
	private ArrayList<State> _myStates = null;

	/**
	 * the course followed for this route
	 */
	private double _course;

	/**
	 * the speed achieved along this route
	 * 
	 */
	private double _speed;

	/** the length of this route (m)
	 * 
	 */
	private double _length;

	/** the identifier for this route
	 * 
	 */
	private final String _name;

	/**
	 * @param startP
	 * @param startTime
	 * @param endP
	 * @param endTime
	 */
	public Route(String name, Point startP, Date startTime, Point endP, Date endTime)
	{
		_startP = startP;
		_endP = endP;
		_startTime = startTime;
		_endTime = endTime;
		_name = name;

		Vector2D vector = new Vector2D(_startP.getCoordinate(),
				_endP.getCoordinate());

		// find the course
		_course = vector.angle();

		// what is our time period
		final long elapsed = _endTime.getTime() - _startTime.getTime();

		// find the speed
		double lengthDeg = vector.length();
		_length = GeoSupport.deg2m(lengthDeg);
		_speed = _length / (elapsed /1000);

	}

	public double getCourse()
	{
		return _course;
	}

	public double getSpeed()
	{
		return _speed;
	}

	/**
	 * break the line down into a series of points, at the indicated times
	 * 
	 */
	public void generateSegments(final ArrayList<BoundedState> states)
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
			Date thisDate = iter.next().getTime();

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

	public double getErrorSum()
	{
		return _errorSum;
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

	/**
	 * get the length of this route
	 * 
	 * @return
	 */
	public double getDistance()
	{
		return _length;
	}

	public long getElapsedTime()
	{
		return (_endTime.getTime() - _startTime.getTime())/1000;
	}
}