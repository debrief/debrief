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
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;

public class LocationAnalysisContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public LocationAnalysisContribution()
	{
		super();
		setName("Location Analysis");

	}

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// remember the previous state
		BoundedState _lastState = null;

		// ok, loop through the states
		@SuppressWarnings("unused")
		int ctr = 0;
		Iterator<BoundedState> iter = space.states().iterator();
		while (iter.hasNext())
		{
			ctr++;
			BoundedState thisS = iter.next();

			// does it have a location?
			LocationRange loc = thisS.getLocation();
			if (loc != null)
			{
				// ok, do we have a previous state
				if (_lastState != null)
				{

					// ok. sort out the constraints from the last state
					LocationRange newConstraint = getRangeFor(_lastState, thisS.getTime());

					// and constraint it
					loc.constrainTo(newConstraint);

					// ok, display what is being shown
					// GeoSupport.writeGeometry("loc_" + ctr, newConstraint.getPolygon());
				}

				// ok, remember, and move on
				_lastState = thisS;
			}

		}
	}

	public LinearRing getCourseRing(CourseRange course, double maxRng)
	{
		LinearRing res = null;

		if (course != null)
		{
			// double the max range = to be sure we cover the possible curved arc

			// ok, produce the arcs
			Coordinate[] coords = new Coordinate[5];

			double minC = course.getMin();
			double maxC = course.getMax();

			// SPECIAL CASE: if the course is 0..360, then we just create a circle
			if (maxC - minC == 2 * Math.PI)
			{
				Point pt = GeoSupport.getFactory().createPoint(new Coordinate(0d, 0d));

				// and create an outer ring to represent it
				res = (LinearRing) pt.buffer(maxRng).getBoundary();

			}
			else
			{

				// minor idiot check. if the two courses are the same, the geometry
				// falls
				// over. so, if they are the same, trim one slightly
				if (minC == maxC)
					maxC += 0.0000001;

				double centreC = minC + (maxC - minC) / 2d;

				// start with the origin
				coords[0] = new Coordinate(0, 0);

				// now the start course
				coords[1] = new Coordinate(Math.sin(minC) * maxRng, Math.cos(minC)
						* maxRng);

				// give us a centre course
				coords[2] = new Coordinate(Math.sin(centreC) * (maxRng * 1.4),
						Math.cos(centreC) * (maxRng * 1.4));

				// now the end course
				coords[3] = new Coordinate(Math.sin(maxC) * maxRng, Math.cos(maxC)
						* maxRng);

				// back to the orgin
				coords[4] = new Coordinate(0, 0);

				res = GeoSupport.getFactory().createLinearRing(coords);
			}
		}

		return res;
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}

	public double getMaxRangeDegs(SpeedRange sRange, long timeMillis)
	{
		final double res;
		if (sRange != null)
			res = GeoSupport.m2deg(sRange.getMax() * timeMillis / 1000d);
		else
			res = GeoSupport.m2deg(RangeForecastContribution.MAX_SELECTABLE_RANGE_M);

		return res;
	}

	/**
	 * at the newDate, work out a relaxed location bounds from the previous state
	 * 
	 * @param state
	 *          the previous known state
	 * @param newDate
	 *          the point in time that we're projecting forwards to
	 * @return
	 */
	public LocationRange getRangeFor(BoundedState state, Date newDate)
	{
		LinearRing res = null;

		// how long have we travelled?
		long diff = newDate.getTime() - state.getTime().getTime();

		// ok, generate the achievable bounds for the state
		CourseRange course = state.getCourse();
		double maxRange = getMaxRangeDegs(state.getSpeed(), diff);
		LinearRing courseR = getCourseRing(course, maxRange);
		Polygon speedP = getSpeedRing(state.getSpeed(), diff);

		// now combine the two
		final LineString achievable;
		if (speedP != null)
		{
			if (courseR != null)
			{
				// convert the course ring into a solid area
				Polygon courseP = GeoSupport.getFactory().createPolygon(courseR, null);

				// now sort out the intersection between course and speed
				Geometry trimmed = courseP.intersection(speedP);

				// GeoSupport.writeGeometry("trimmed", trimmed);

				Geometry geom = trimmed.convexHull();
				if (!(geom instanceof LineString))
				{
					// is it a multi-point?
					if (geom instanceof MultiPoint)
					{
						MultiPoint mp = (MultiPoint) geom;
						// get a line string from the coordinates
						geom = GeoSupport.getFactory()
								.createLineString(mp.getCoordinates());
					}
					else if (geom instanceof Polygon)
					{
						// the geoms are all currently appearing as polygons, maybe this is
						// to do with the
						// Jan 2013 JTS update.
						geom = GeoSupport.getFactory().createLineString(
								geom.getCoordinates());
					}
				}
				if (geom instanceof LineString)
					achievable = (LineString) geom;
				else
				{
					System.err
							.println("LocationAnalysisContribution: we were expecting a line-string, but it hasn't arrived!");
					// TODO: get rid of this
					GeoSupport.writeGeometry("not a line-string", geom);
					achievable = null;
				}
			}
			else
			{
				// TODO: speedP is actually two circles = we probably need to process
				// both circles
				achievable = (LineString) speedP.getExteriorRing().clone();
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

		// GeoSupport.writeGeometry("Achievable_" + newDate, achievable);

		// did we construct a bounds?
		if (achievable != null)
		{
			// ok, apply this to the pioints of the bounded state
			LocationRange loc = state.getLocation();

			if (loc != null)
			{
				// move around the outer points
				Geometry geometry = loc.getGeometry();
				if (geometry instanceof Polygon)
				{
					LineString ls = ((Polygon) geometry).getExteriorRing();
					LinearRing ext = GeoSupport.getFactory().createLinearRing(
							ls.getCoordinates());
					Coordinate[] pts = ext.getCoordinates();

					for (int i = 0; i < pts.length; i++)
					{
						Coordinate thisC = pts[i];

						// make a copy of the shape

						// add each of these coords to that shape
						AffineTransformation trans = new AffineTransformation();
						trans.setToTranslation(thisC.x, thisC.y);

						// actually do the move
						Coordinate[] oldCoords = achievable.getCoordinates();
						Coordinate[] newCoords = new Coordinate[oldCoords.length];
						for (int j = 0; j < oldCoords.length; j++)
						{
							Coordinate tmpC = oldCoords[j];
							Coordinate newC = new Coordinate(0, 0);
							newC = trans.transform(tmpC, newC);
							newCoords[j] = newC;
						}

						// and put it back into a linestring
						Geometry translated = GeoSupport.getFactory().createLinearRing(
								newCoords);

						// GeoSupport.writeGeometry("shape:" + i, translated);

						if (res == null)
						{
							res = (LinearRing) translated;
						}
						else
						{
							// now we need to combine the two geometries
							Geometry geom = res.union(translated);
							Geometry geom2 = geom.convexHull();
							res = (LinearRing) geom2.getBoundary();
						}
					}

				}
			}
		}

		// get the region
		final LocationRange answer;
		if (res != null)
		{
			Polygon tmpPoly2 = GeoSupport.getFactory().createPolygon(res, null);

			answer = new LocationRange(tmpPoly2);
		}
		else
			answer = null;

		return answer;
	}

	/**
	 * calculate the speed boundary, according to the min/max speeds.
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

		// and create an outer ring to represent it
		LinearRing outer = (LinearRing) pt.buffer(maxR).getBoundary();

		// ok, and the minimum
		final LinearRing[] inner;

		// do we have a speed val?
		if (sRange != null)
		{
			// yes, get calculating
			double minR = sRange.getMin() * timeMillis / 1000d;
			// convert to degs
			minR = GeoSupport.m2deg(minR);

			// aaah, just double check the two ranges aren't equal - it causes trouble
			if (minR == maxR)
			{
				minR = maxR - GeoSupport.m2deg(10);
			}

			inner = new LinearRing[]
			{ (LinearRing) pt.buffer(minR).getBoundary() };
		}
		else
		{
			// with a zero min speed, we don't have any holes
			inner = null;
		}

		// and now a polygon to represent them both
		Polygon res = GeoSupport.getFactory().createPolygon(outer, inner);

		return res;
	}
}
