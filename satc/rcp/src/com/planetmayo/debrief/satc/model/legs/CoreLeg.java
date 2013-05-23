package com.planetmayo.debrief.satc.model.legs;

import java.util.Collections;
import java.util.List;


import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.vividsolutions.jts.geom.Point;

public abstract class CoreLeg
{
	
	/** the maximum number of points we allow in a leg start/end state before we try to generate solutions
	 * 
	 */
	private static final int MAX_PTS = 5000;
	
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

	protected CoreLeg(String name, List<BoundedState> states)
	{
		_states = states;
		_name = name;
	}

	/**
	 * add this bounded state
	 * 
	 * @param thisS
	 */
	final public void add(BoundedState thisS)
	{
		_states.add(thisS);
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
	
	/**
	 * produce the set of constituent routes for this leg
	 * 
	 * @param precision
	 *          how many grid cells to dissect the area into
	 */
	public void generatePoints(Precision precision) 
	{
		generatePoints(precision, MAX_PTS);
	}

	/**
	 * produce the set of constituent routes for this leg
	 * 
	 * @param precision
	 *          how many grid cells to dissect the area into
	 * @param maxPoints 
	 *          if we have more than maxPoints points for each area throw the exception   
	 */
	public void generatePoints(Precision precision, int maxPoints)
	{
		// produce the grid of cells
		LocationRange firstLoc = getFirst().getLocation();
		LocationRange lastLoc = getLast().getLocation();

		if ((firstLoc == null) || (lastLoc == null))
			throw new IllegalArgumentException(
					"The end states must have location bounds");

		final double delta;
		switch (precision)
			{
			case LOW:
				delta = GeoSupport.m2deg(600);
				break;
			case MEDIUM:
				delta = GeoSupport.m2deg(300);
				break;
			case HIGH:
				delta = GeoSupport.m2deg(100);
				break;

			default:
				throw new RuntimeException(
						"We've failed to implement case for a precision type");
			}
		;

		// right, what's the area of the start?
		double startArea = firstLoc.getGeometry().getArea();
		double endArea = lastLoc.getGeometry().getArea();

		final int numStart = (int) (startArea / (delta * delta));
		final int numEnd = (int) (endArea / (delta * delta));

		// just check neither of our domains are too large (a typical symptom of a contribution with an invalid time state)
		if(numStart > maxPoints)
			throw new RuntimeException("Too many start points (" + numStart + ") for leg:" + this.getName());
		if(numEnd > maxPoints)
			throw new RuntimeException("Too many end points (" + numEnd + ") for leg:" + this.getName());

		startPoints = MakeGrid.ST_Tile(firstLoc.getGeometry(),
				numStart, 6);
		endPoints = MakeGrid.ST_Tile(lastLoc.getGeometry(), numEnd, 6);
		
		// just check we've been able to create some points
		if(startPoints.size() == 0) 
				throw new RuntimeException("Unable to generate any start points for leg:" + this.getName());
		if(endPoints.size() == 0)
			throw new RuntimeException("Unable to generate any end points for leg:" + this.getName());
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
	
	abstract public CoreRoute createRoute(String name, Point start, Point end);
}