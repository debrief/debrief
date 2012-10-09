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
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;

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
		Coordinate[] coords = new Coordinate[5];

		double minC = Angle.toRadians(course.getMin());
		double maxC = Angle.toRadians(course.getMax());
		double centreC = minC + (maxC - minC) / 2d;

		// start with the origin
		coords[0] = new Coordinate(0, 0);

		// now the start course
		coords[1] = new Coordinate(Math.sin(minC) * maxRng, Math.cos(minC) * maxRng);

		// give us a centre course
		coords[2] = new Coordinate(Math.sin(centreC) * (maxRng * 1.4),
				Math.cos(centreC) * (maxRng * 1.4));

		// now the end course
		coords[3] = new Coordinate(Math.sin(maxC) * maxRng, Math.cos(maxC) * maxRng);

		// back to the orgin
		coords[4] = new Coordinate(0, 0);

		return GeoSupport.getFactory().createLinearRing(coords);
	}

	public LocationRange getRangeFor(BoundedState state, Date newDate)
	{
		LinearRing res = null;

		// how long have we travelled?
		long diff = newDate.getTime() - state.getTime().getTime();

		// ok, generate the achievable bounds for the state
		CourseRange course = state.getCourse();
		LinearRing courseR = getCourseRing(course,
				getMaxRangeDegs(state.getSpeed(), diff));

		Polygon speedR = getSpeedRing(state.getSpeed(), diff);

		// now combine the two
		final LineString achievable;
		if (speedR != null)
		{
			if (courseR != null)
			{
				Geometry multiL = speedR.intersection(courseR);
				assert multiL.getNumGeometries() == 1;
				achievable = (LineString) multiL.getGeometryN(0);
			}
			else
			{
				achievable = (LineString) speedR.clone();
			}
		}
		else
		{
			if (courseR != null)
			{
				achievable = (LineString) courseR.clone();
			}
			else
			{
				// nope, we don't have any bounds
				achievable = null;
			}
		}

		// did we construct a bounds?
		if (achievable != null)
		{
			// ok, apply this to the pioints of the bounded state
			LocationRange loc = state.getLocation();

			if (loc != null)
			{
				// move around the outer points
				LineString ls = (LineString) loc.getPolygon().getExteriorRing();
				LinearRing ext = GeoSupport.getFactory().createLinearRing(
						ls.getCoordinates());
				Coordinate[] pts = ext.getCoordinates();

				Coordinate[] newPts = new Coordinate[pts.length];

				for (int i = 0; i < pts.length; i++)
				{
					Coordinate thisC = pts[i];

					// make a copy of the shape

					// add each of these coords to that shape
					AffineTransformation aff = new AffineTransformation();
					AffineTransformation trans = aff.translate(thisC.x, thisC.y);

					// actually do the move
					Geometry translated = trans.transform(achievable);

					// store the value
					newPts[i] = translated.getCoordinate();

				}

				// ok, create a shape from the new points
				LinearRing after = GeoSupport.getFactory().createLinearRing(newPts);
				// LinearRing after =
				// GeoSupport.getFactory().createLinearRing(translated.getCoordinates());

				// extend our bounds with this new geometry
				res = (LinearRing) after;
			}
		}

		// get the region
		final LocationRange answer;
		if (res != null)
		{
			Polygon tmpPoly = GeoSupport.getFactory().createPolygon(res, null);
			answer = new LocationRange(tmpPoly);
		}
		else
			answer = null;

		return answer;
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
		double maxR = getMaxRangeDegs(sRange, timeMillis);
		// ok, and the minimum
		double minR = sRange.getMinMS() * timeMillis / 1000d;

		// convert to degs
		minR = GeoSupport.m2deg(minR);

		LinearRing outer = (LinearRing) pt.buffer(maxR).getBoundary();
		LinearRing inner = (LinearRing) pt.buffer(minR).getBoundary();

		Polygon res = GeoSupport.getFactory().createPolygon(outer, new LinearRing[]
		{ inner });

		return res;
	}

	public double getMaxRangeDegs(SpeedRange sRange, long timeMillis)
	{
		final double res;
		if (sRange != null)
			res = GeoSupport.m2deg(sRange.getMaxMS() * timeMillis / 1000d);
		else
			res = RangeForecastContribution.MAX_SELECTABLE_RANGE_M;

		return res;
	}
}
