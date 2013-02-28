package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Date;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.math.Vector2D;

public class AlteringRoute extends CoreRoute
{

	private double _directDistance;

	public AlteringRoute(String name, Point startP, Date startTime, Point endP,
			Date endTime)
	{
		super(startP, endP, startTime, endTime, name);
		
		// store the straight line distance
		Vector2D vector = new Vector2D(_startP.getCoordinate(),
				_endP.getCoordinate());

		// find the speed
		double lengthDeg = vector.length();
		_directDistance = GeoSupport.deg2m(lengthDeg);
	}

	/**
	 * break the line down into a series of points, at the indicated times
	 * 
	 */
	public void generateSegments(final ArrayList<BoundedState> states)
	{
		 // TODO sort out how to produce a curve through from start to end. actually, we can produce loads!
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
}
