package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

import com.planetmayo.debrief.satc.model.VehicleType;
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

public class LocationAnalysisContribution extends
		BaseAnalysisContribution<LocationRange>
{
	private static final long serialVersionUID = 1L;

	public LocationAnalysisContribution()
	{
		super();
		setName("Location Analysis");

	}

	@Override
	protected void applyThis(BoundedState state, LocationRange thisState)
			throws IncompatibleStateException
	{
		state.constrainTo(thisState);
	}

	@Override
	protected LocationRange getRangeFor(BoundedState lastStateWithRange)
	{
		return lastStateWithRange.getLocation();
	}

	@Override
	protected LocationRange duplicateThis(LocationRange thisRange)
	{
		return new LocationRange(thisRange);
	}

	@Override
	protected void furtherConstrain(LocationRange currentLegState,
			LocationRange thisRange) throws IncompatibleStateException
	{
		currentLegState.constrainTo(thisRange);
	}

	protected void applyAnalysisConstraints(final ProblemSpace space,
			SwitchableIterator switcher) throws IncompatibleStateException
	{
		// remember the previous state
		BoundedState lastStateWithState = null;

		// get the vehicle type
		final VehicleType vType = space.getVehicleType();

		// ok, loop through the states, setting range limits for any unbounded
		// ranges
		Collection<BoundedState> theStates = space.states();
		ArrayList<BoundedState> al = new ArrayList<BoundedState>(theStates);
		ListIterator<BoundedState> iter = switcher.getIterator(al);
		while (switcher.canStep(iter))
		{
			BoundedState currentState = switcher.next(iter);

			// ok, we're not in a leg. relax it.
			try
			{
				lastStateWithState = applyRelaxedRangeBounds(lastStateWithState,
						currentState, vType);
			}
			catch (IncompatibleStateException e)
			{
				e.setFailingState(currentState);
				throw e;
			}
		}
	}

	@Override
	protected LocationRange calcRelaxedRange(BoundedState state,
			VehicleType vType, long diff)
	{

		LinearRing res = null;

		// ok, generate the achievable bounds for the state
		CourseRange course = state.getCourse();
		SpeedRange speed = state.getSpeed();

		// now - if we're running backwards, we need to reverse the course
		if (diff < 0)
		{
			// aah, do we have a course?
			if (course != null)
				course = course.generateInverse();
		}

		// ok, put the time back to +ve
		diff = Math.abs(diff);

		double maxRange = getMaxRangeDegs(speed, diff);
		LinearRing courseR = getCourseRing(course, maxRange);
		Polygon speedP = getSpeedRing(speed, diff);
		
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

					throw new RuntimeException("We should not have encountered a non-linestring here");
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

		// ///////////////////////////////
		//
		// double maxDecel = vType.getMaxDecelRate();
		// double maxAccel = vType.getMaxAccelRate();
		//
		// double diffSeconds = millis / 1000.0d;
		//
		// double minSpeed = lastStateWithRange.getSpeed().getMin() - maxDecel
		// * diffSeconds;
		// double maxSpeed = lastStateWithRange.getSpeed().getMax() + maxAccel
		// * diffSeconds;
		// if (minSpeed < 0)
		// {
		// minSpeed = 0;
		// }
		// return new SpeedRange(minSpeed, maxSpeed);
	}

	@Override
	protected void relaxConstraint(BoundedState currentState,
			LocationRange newRange)
	{
		currentState.setLocation(newRange);
	}

	// @Override
	// public void actUpon(ProblemSpace space) throws IncompatibleStateException
	// {
	// // remember the previous state
	// BoundedState _lastState = null;
	//
	// // ok, loop through the states
	// @SuppressWarnings("unused")
	// int ctr = 0;
	// Iterator<BoundedState> iter = space.states().iterator();
	// while (iter.hasNext())
	// {
	// ctr++;
	// BoundedState thisS = iter.next();
	//
	// // do we have a previous constraint to work from?
	// if (_lastState != null)
	// {
	//
	// // ok. sort out the constraints from the last state
	// long timeDiff = thisS.getTime().getTime() - _lastState.getTime().getTime();
	//
	// // we may be going forwards or backwards, use the abs time diff
	// timeDiff = Math.abs(timeDiff);
	//
	// // ok - where could we have travelled to?
	// LocationRange newConstraint = getRangeFor(_lastState, timeDiff);
	//
	// if (newConstraint != null)
	// {
	// // and constraint it
	// thisS.constrainTo(newConstraint);
	// }
	// }
	//
	// // ok, remember, and move on
	// _lastState = thisS;
	// }
	// }

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
			if ((maxC == minC) ||(maxC - minC == 2 * Math.PI))
			{
				return res;
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
		Geometry tmpGeom = pt.buffer(maxR).getBoundary();
		LinearRing outer = (LinearRing) tmpGeom;

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

			// just check we don't have a zero min speed
			if (minR > 0)
			{
				inner = new LinearRing[]
				{ (LinearRing) pt.buffer(minR).getBoundary() };
			}
			else
				inner = null;
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
