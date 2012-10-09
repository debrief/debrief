package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;

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

	public LinearRing getCourseRing(CourseRange course)
	{
		return null;
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

	public LinearRing getSpeedRing(SpeedRange sRange, long timeMillis)
	{
		Point pt = GeoSupport.getFactory().createPoint(new Coordinate(0d,0d));
		
		// ok, what's the maximum value?
	//	double maxS = sRange.getMax()
		
		return null;
	}

}
