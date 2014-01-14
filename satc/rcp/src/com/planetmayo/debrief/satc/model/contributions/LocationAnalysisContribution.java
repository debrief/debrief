package com.planetmayo.debrief.satc.model.contributions;

import java.awt.geom.Point2D;
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
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
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
		if (state.getLocation() == null)
		{
			return null;
		}
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

		Coordinate centerPoint = state.getLocation().getGeometry().getCoordinates()[0];
		LinearRing courseR = getCourseRing(centerPoint, course, speed, diff);
		LinearRing speedR = getSpeedRing(centerPoint, speed, diff);
		
		// now combine the two
		final LineString achievable;
		if (speedR != null && courseR != null)
		{
			// convert the course ring into a solid area
			Polygon courseP = GeoSupport.getFactory().createPolygon(courseR);
			Polygon speedP = GeoSupport.getFactory().createPolygon(speedR);

			// now sort out the intersection between course and speed
			Geometry trimmed = courseP.intersection(speedP);
			Geometry geom = trimmed.convexHull();

			if ((geom instanceof MultiPoint) || (geom instanceof Polygon))
			{
				geom = GeoSupport.getFactory().createLineString(geom.getCoordinates());
			}
			if (geom instanceof LineString)
			{
				achievable = (LineString) geom;
			}
			else
			{
				System.err
						.println("LocationAnalysisContribution: we were expecting a line-string, but it hasn't arrived!");

				throw new RuntimeException(
						"We should not have encountered a non-linestring here");
			}
		}
		else
		{
			achievable = courseR == null ? speedR : courseR;
		}

		// did we construct a bounds?
		if (achievable != null)
		{
			// ok, apply this to the pioints of the bounded state
			LocationRange loc = state.getLocation();

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

					// add each of these coords to that shape
					AffineTransformation trans = new AffineTransformation();
					trans.setToTranslation(thisC.x - centerPoint.x, thisC.y - centerPoint.y);
					LinearRing translated = GeoSupport.getFactory().createLinearRing( 
							trans.transform(achievable).getCoordinates()
					);

					if (res == null)
					{
						res = (LinearRing) translated;
					}
					else
					{
						// now we need to combine the two geometries

						// start off by wrapping them in polygons
						Polygon poly1 = GeoSupport.getFactory().createPolygon(res);
						Polygon poly2 = GeoSupport.getFactory().createPolygon(translated);

						// now generate the multi-polygon
						Geometry combined = GeoSupport.getFactory().createMultiPolygon(
								new Polygon[]
								{ poly1, poly2 });

						// and the convex hull for it
						Geometry outer = combined.convexHull();
						res = (LinearRing) outer.getBoundary();
					}
				}
			}
		}

		// get the region
		if (res == null)
		{
			return null;
		}
		return new LocationRange(GeoSupport.getFactory().createPolygon(res, null));
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

	public LinearRing getCourseRing(Coordinate center, CourseRange course, SpeedRange speed, long timeMillis)
	{
		LinearRing res = null;
		
		double maxRange = getMaxRange(speed, timeMillis);
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
				coords[0] = new Coordinate(center.x, center.y);
				
				GeodeticCalculator calculator = GeoSupport.createCalculator();
				calculator.setStartingGeographicPoint(center.x, center.y);
				
				// now the start course
				calculator.setDirection(Math.toDegrees(MathUtils.normalizeAngle2(minC)), maxRange);				
				coords[1] = convert(calculator.getDestinationGeographicPoint());

				// give us a centre course
				calculator.setDirection(Math.toDegrees(MathUtils.normalizeAngle2(centreC)), maxRange * 1.4);				
				coords[2] = convert(calculator.getDestinationGeographicPoint());

				// now the end course
				calculator.setDirection(Math.toDegrees(MathUtils.normalizeAngle2(maxC)), maxRange);						
				coords[3] = convert(calculator.getDestinationGeographicPoint());

				// back to the orgin
				coords[4] = new Coordinate(center.x, center.y);

				res = GeoSupport.getFactory().createLinearRing(coords);
			}
		}

		return res;
	}
	

	/**
	 * calculate the speed boundary, according to the min/max speeds.
	 * 
	 * @param speed
	 *          the speed constraints
	 * @param timeMillis
	 *          the time since the last state
	 * @return
	 */
	public LinearRing getSpeedRing(Coordinate centerPoint, SpeedRange speed, long timeMillis)	
	{
		Point center = GeoSupport.getFactory().createPoint(centerPoint);
		return GeoSupport.geoRing(center, getMaxRange(speed, timeMillis));
	}	

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}

	private double getMaxRange(SpeedRange speed, long timeMillis)
	{
		if (speed == null)
		{
			return RangeForecastContribution.MAX_SELECTABLE_RANGE_M;
		}
		return speed.getMax() * timeMillis / 1000.; 
	}
	
	private Coordinate convert(Point2D point)
	{
		return new Coordinate(point.getX(), point.getY());
	}

}
