package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class LocationAnalysisTest extends ModelTestBase
{

	public void testOverap()
	{

	}

	@Test
	public void testAnalysis() throws IncompatibleStateException
	{
		ProblemSpace space = new ProblemSpace();

		// ok, create the state
		Date oldDate = new Date(100000);
		Date newDate = new Date(160000);
		BoundedState oldState = new BoundedState(oldDate);
		BoundedState newStateConstraint = new BoundedState(newDate);
		BoundedState newStateNoConstraint = new BoundedState(newDate);

		Coordinate[] coords = new Coordinate[]
		{ createCoord(0.01, 0.02), createCoord(0.04, 0.05),
				createCoord(0.05, 0.02), createCoord(0.03, 0.01),
				createCoord(0.01, 0.02) };
		LinearRing outer = GeoSupport.getFactory().createLinearRing(coords);
		Polygon area = GeoSupport.getFactory().createPolygon(outer, null);
		// and the location
		LocationRange lr = new LocationRange(area);
		oldState.constrainTo(lr);

		// get ready to analyse
		LocationAnalysisContribution lac = new LocationAnalysisContribution();

		// give it a course
		CourseRange cRange = new CourseRange(Math.toRadians(20), Math.toRadians(60));
		oldState.constrainTo(cRange);

		// and a speed
		SpeedRange sRange = new SpeedRange(GeoSupport.kts2MSec(6),
				GeoSupport.kts2MSec(9));
		oldState.constrainTo(sRange);

		// ok - time for the new state
		coords = new Coordinate[]
		{ createCoord(0.01, 0.02), createCoord(0.04, 0.05),
				createCoord(0.05, 0.02), createCoord(0.03, 0.01),
				createCoord(0.01, 0.02) };
		outer = GeoSupport.getFactory().createLinearRing(coords);
		area = GeoSupport.getFactory().createPolygon(outer, null);
		// and the location
		lr = new LocationRange(area);
		newStateConstraint.constrainTo(lr);

		// get ready to analyse
		lac = new LocationAnalysisContribution();

		// give it a course
		cRange = new CourseRange(Math.toRadians(20), Math.toRadians(60));
		newStateConstraint.constrainTo(cRange);

		// and a speed
		sRange = new SpeedRange(GeoSupport.kts2MSec(6), GeoSupport.kts2MSec(9));
		newStateConstraint.constrainTo(sRange);

		space.add(oldState);
		space.add(newStateConstraint);

		lac.actUpon(space);
	}

	@Test
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
		CourseRange cRange = new CourseRange(Math.toRadians(20), Math.toRadians(60));
		bs.constrainTo(cRange);

		// and a speed
		SpeedRange sRange = new SpeedRange(GeoSupport.kts2MSec(6),
				GeoSupport.kts2MSec(9));
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
		LocationRange newB = lac.getRangeFor(bs, newDate.getTime() - bs.getTime().getTime());
		GeoSupport.writeGeometry("location region", newB.getGeometry());

		// did it work?
		// assertNotNull("Should have created location", newB);
	}

	private Coordinate createCoord(double x, double y)
	{
		double scale = 10d;
		return new Coordinate(x / scale, y / scale);
	}
}
