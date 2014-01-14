package com.planetmayo.debrief.satc.model.legs;

import java.util.Iterator;
import java.util.List;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;

public class StraightLeg extends CoreLeg
{
	
	public StraightLeg(String name)
	{
		this(name, null);
	}
	
	
	public StraightLeg(String name, List<BoundedState> states)
	{
		super(name, states);
	}

	/**
	 * create a straight leg.
	 * 
	 * @param name
	 *          what to call the leg
	 * @param states
	 *          the set of bounded states that comprise the leg
	 */
	
	void decideScaledPolygons(StraightRoute theRoute)
	{
		// do we already know this isn't possible?
		if (!theRoute.isPossible())
			return;

		// bugger, we'll have to get on with our hard sums then

		// sort out the origin.
		State startState = theRoute.getStates().get(0);
		Coordinate startCoord = startState.getLocation().getCoordinate();

		// also sort out the end state
		State endState = theRoute.getStates()
				.get(theRoute.getStates().size() - 1);
		Point endPt = endState.getLocation();

		// remeber the start time
		long tZero = startState.getTime().getTime();

		// how long is the total run?
		double elapsed = theRoute.getElapsedTime();

		// allow for multiple fidelity processing
		int ctr = 0;

		// how frequently shall we process the polygons?
		// calculating the scaled polygons is really expensive. this
		// give us most of the benefits, at a third of the speed (well,
		// with a freq of '3' it does)
		final int freq = 3;

		// loop through our states
		Iterator<BoundedState> iter = _states.iterator();
		while (iter.hasNext())
		{
			BoundedState thisB = iter.next();

			LocationRange thisL = thisB.getLocation();
			if (thisL != null)
			{
				// ok, what's the time difference
				long thisDelta = thisB.getTime().getTime() - tZero;

				// convert to secs
				long tDelta = thisDelta / 1000;

				// is this our first state
				if (tDelta > 0)
				{
					// ok, we've got a location - increment the counter
					ctr++;

					// is this one we're going to process?
					if (((ctr % freq) == 0) || ctr == _states.size() - 1)
					{
						double scale = elapsed / tDelta;

						// ok, project the shape forwards
						AffineTransformation st = AffineTransformation.scaleInstance(
								scale, scale, startCoord.x, startCoord.y);

						// ok, apply the transform to the location
						/*Geometry originalGeom = thisL.getGeometry();
						Geometry newGeom = st.transform(originalGeom);

						// see if the end point is in the new geometry
						if (endPt.coveredBy(newGeom))
						{
							// cool, this route works
						}
						else
						{
							// bugger, can't do this one
							theRoute.setImpossible();
							break;
						}*/
						if (! MathUtils.rayTracing(endPt, thisL.getGeometry(), st))
						{
							theRoute.setImpossible();
							break;							
						}
					}
				}
			}
		}
	}	

	/**
	 * use a simple speed/time decision to decide if it's possible to navigate a
	 * route
	 */
	public void decideAchievableRoute(CoreRoute r)
	{		
		if (!r.isPossible() || !(r instanceof StraightRoute))
			return;
		StraightRoute route = (StraightRoute) r;
		
		double distance = route.getDistance();
		double elapsed = route.getElapsedTime();
		double speed = distance / elapsed;

		SpeedRange speedR = _states.get(0).getSpeed();
		if (speedR != null && !speedR.allows(speed))
		{
			route.setImpossible();
			return;
		}

		double thisC = route.getCourse();
		CourseRange courseR = _states.get(0).getCourse();
		if (courseR != null && !courseR.allows(thisC))
		{
			route.setImpossible();
			return;
		}
		
		// examine the scaled polygons to see if this candidate route passes
		// through all of them.
		decideScaledPolygons(route);
	}
	
	@Override
	public CoreRoute createRoute(String name, Point start, Point end)
	{
		StraightRoute route = new StraightRoute(name, start, getFirst().getTime(), end, getLast().getTime());
		route.generateSegments(_states);
		return route;
	}

	@Override
	public LegType getType()
	{
		return LegType.STRAIGHT;
	}
}