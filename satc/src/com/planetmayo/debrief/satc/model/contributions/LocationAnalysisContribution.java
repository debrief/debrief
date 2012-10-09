package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class LocationAnalysisContribution extends BaseContribution
{

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// remember the previous state
		BoundedState _lastState = null;

		// ok, loop through the states
		Iterator<BoundedState> iter = space.states().iterator();
		while (iter.hasNext())
		{
			BoundedState thisS = (BoundedState) iter.next();

			// does it have a location?
			LocationRange loc = thisS.getLocation();
			if (loc != null)
			{
				// ok, do we have a previous state
				if (_lastState != null)
				{

					// ok. sort out the constraints from the last state
					LocationRange newConstraint = getRangeFor(_lastState, thisS.getTime());

					// now apply those constraints to me
					loc.constrainTo(newConstraint);
				}

				// ok, remember, and move on
				_lastState = thisS;
			}

		}
	}

	public LinearRing getCourseRing(CourseRange course, double maxRng)
	{
		// double the max range = to be sure we cover the possible curved arc

		// ok, produce the arcs
		Coordinate[] coords = new Coordinate[4];

		double minC = Angle.toRadians(course.getMin());
		double maxC = Angle.toRadians(course.getMax());
		
		// start with the origin
		coords[0] = new Coordinate(0, 0);
		
		// now the start course
		coords[1] = new Coordinate(
				 Math.sin(minC) * maxRng,
				 Math.cos(minC) * maxRng);

		// now the end course
		coords[2] = new Coordinate(
				 Math.sin(maxC) * maxRng,
				 Math.cos(maxC) * maxRng);
		
		// back to the orgin
		coords[3] = new Coordinate(0, 0);

		return GeoSupport.getFactory().createLinearRing(coords);
	}

	public LocationRange getRangeFor(BoundedState state, Date newDate)
	{
		// how long have we travelled?
		long diff = newDate.getTime() - state.getTime().getTime();

		// ok, generate the box of achievable location for the state
		Geometry achievable = null;

		CourseRange course = state.getCourse();

		// get the region
		Geometry edge = state.getLocation().getPolygon().getBoundary();

		// loop around it

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}

	@Override
	public String getHardConstraints()
	{
		return "n/a";
	}

	/**
	 * calculate the speed boundary
	 * 
	 * @param sRange
	 *          the speed constraints
	 * @param timeMillis
	 *          the time since the last state
	 * @param outer
	 *          the outer speed boundary (max speed)
	 * @return
	 */
	public Polygon getSpeedRing(SpeedRange sRange, long timeMillis)
	{
		Point pt = GeoSupport.getFactory().createPoint(new Coordinate(0d, 0d));

		// ok, what's the maximum value?
		double maxR = getMaxRange(sRange, timeMillis);
		// ok, and the minimum
		double minR = sRange.getMinMS() * timeMillis / 1000d;

		// convert to degs
		maxR = GeoSupport.m2deg(maxR);
		minR = GeoSupport.m2deg(minR);

		LinearRing outer = (LinearRing) pt.buffer(maxR).getBoundary();
		LinearRing inner = (LinearRing) pt.buffer(minR).getBoundary();

		Polygon res = GeoSupport.getFactory().createPolygon(outer, new LinearRing[]
		{ inner });

		return res;
	}

	public double getMaxRange(SpeedRange sRange, long timeMillis)
	{
		final double res;
		if (sRange != null)
			res = sRange.getMaxMS() * timeMillis / 1000d;
		else
			res = RangeForecastContribution.MAX_SELECTABLE_RANGE_M;

		return res;
	}
}
