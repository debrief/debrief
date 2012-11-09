package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class LocationAnalysisTest extends TestCase
{

	public void testOverap()
	{

	}

	public void testBoundary() throws IncompatibleStateException
	{
		// ok, create the state
		Date oldDate = new Date(100000);
		Date newDate = new Date(160000);
		BoundedState bs = new BoundedState(oldDate);

		Coordinate[] coords = new Coordinate[]
		{ createCoord(0.01, 0.02), createCoord(0.04, 0.05),
				createCoord(0.05, 0.02), createCoord(0.03, 0.01),
				createCoord(0.01, 0.02) };
		LinearRing outer = GeoSupport.getFactory().createLinearRing(coords);
		Polygon area = GeoSupport.getFactory().createPolygon(outer, null);
		// and the location
		LocationRange lr = new LocationRange(area);
		bs.constrainTo(lr);

		// get ready to analyse
		LocationAnalysisContribution lac = new LocationAnalysisContribution();

		// give it a course
		CourseRange cRange = new CourseRange(20, 60);
		bs.constrainTo(cRange);

		// and a speed
		SpeedRange sRange = new SpeedRange(6, 9);
		bs.constrainTo(sRange);

		// try the speed
		Polygon speedRegion = lac.getSpeedRing(sRange,
				newDate.getTime() - oldDate.getTime());
		assertNotNull("course not generated", speedRegion);
		// GeoSupport.writeGeometry("Speed", speedRegion);

		// ok, try the course
		double maxRange = lac.getMaxRangeDegs(sRange,
				newDate.getTime() - oldDate.getTime());

		LinearRing courseRegion = lac.getCourseRing(cRange, maxRange);
		assertNotNull("course not generated", courseRegion);
		assertEquals("correct num of coords for arc", 5,
				courseRegion.getNumPoints());
		// GeoSupport.writeGeometry("course region", courseRegion);

		//
		LocationRange newB = lac.getRangeFor(bs, newDate);
	//	GeoSupport.writeGeometry("location region", newB.getPolygon());

		// did it work?
		// assertNotNull("Should have created location", newB);
	}

	private Coordinate createCoord(double x, double y)
	{
		double scale = 10d;
		return new Coordinate(x / scale, y / scale);
	}
}
