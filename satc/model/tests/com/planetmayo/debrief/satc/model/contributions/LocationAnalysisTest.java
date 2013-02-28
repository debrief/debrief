package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.VehicleType;
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
	public void testRelaxationNoPrior() throws IncompatibleStateException
	{
		ProblemSpace space = new ProblemSpace();
		VehicleType vType = new VehicleType("UK Ferry", GeoSupport.kts2MSec(0),
				GeoSupport.kts2MSec(30000), Math.toRadians(0), Math.toRadians(1), 0.2,
				0.4, 0.2, 0.4);
		space.setVehicleType(vType);

		double metreLarge = GeoSupport.deg2m(0.2);

		// ok, create the state
		Date date1 = new Date(100000);
		Date date2 = new Date(110000);
		Date date3 = new Date(120000);
		BoundedState s1 = new BoundedState(date1);
		BoundedState s2 = new BoundedState(date2);
		BoundedState s3 = new BoundedState(date3);

		space.add(s1);
		space.add(s2);
		space.add(s3);

		Coordinate[] coords = new Coordinate[]
		{ createCoord(2d, 2d), createCoord(3d, 2d), createCoord(3d, 3d),
				createCoord(2d, 3d), createCoord(2d, 2d) };
		LinearRing outer = GeoSupport.getFactory().createLinearRing(coords);
		Polygon area = GeoSupport.getFactory().createPolygon(outer, null);
		// and the location
		LocationRange lr = new LocationRange(area);
		s2.constrainTo(lr);

		// give it a course
		CourseRange cRange = new CourseRange(Math.toRadians(45),
				Math.toRadians(135));
		s1.constrainTo(cRange);
		s2.constrainTo(cRange);
		s3.constrainTo(cRange);

		// and a speed
		SpeedRange sRange = new SpeedRange(0.000001, metreLarge);
		s1.constrainTo(sRange);
		s2.constrainTo(sRange);
		s3.constrainTo(sRange);

		// ok, now lets put in the new state
		LocationAnalysisContribution lac = new LocationAnalysisContribution();
		lac.actUpon(space);

		// check they have ranges
		assertNotNull("Loc range created", s1.getLocation());
		assertNotNull("Loc range created", s3.getLocation());
		//
		// System.out.println(s1.getLocation().getGeometry().toText());
		// System.out.println("  ");
		// System.out.println(s2.getLocation().getGeometry().toText());
		// System.out.println("  ");
		// System.out.println(s3.getLocation().getGeometry().toText());
		//
		//
	}

	@Test
	public void testRelaxationWithPrior() throws IncompatibleStateException
	{
		ProblemSpace space = new ProblemSpace();
		VehicleType vType = new VehicleType("UK Ferry", GeoSupport.kts2MSec(0),
				GeoSupport.kts2MSec(30000), Math.toRadians(0), Math.toRadians(1), 0.2,
				0.4, 0.2, 0.4);
		space.setVehicleType(vType);

		double metreLarge = GeoSupport.deg2m(0.2);

		// ok, create the state
		Date date1 = new Date(100000);
		Date date2 = new Date(110000);
		Date date3 = new Date(120000);
		BoundedState s1 = new BoundedState(date1);
		BoundedState s2 = new BoundedState(date2);
		BoundedState s3 = new BoundedState(date3);

		space.add(s1);
		space.add(s2);
		space.add(s3);

		Coordinate[] coords = new Coordinate[]
		{ createCoord(2d, 2d), createCoord(3d, 2d), createCoord(3d, 3d),
				createCoord(2d, 3d), createCoord(2d, 2d) };
		LinearRing outer = GeoSupport.getFactory().createLinearRing(coords);
		Polygon area1 = GeoSupport.getFactory().createPolygon(outer, null);
		// and the location
		LocationRange lr = new LocationRange(area1);
		s2.constrainTo(lr);

		
		System.out.println(area1.toText());
		System.out.println("  ");

		coords = new Coordinate[]
		{ createCoord(2d, 3d), createCoord(3d, 3d), createCoord(3d, 4d),
				createCoord(2d, 4d), createCoord(2d, 3d) };
		outer = GeoSupport.getFactory().createLinearRing(coords);
		Polygon area2 = GeoSupport.getFactory().createPolygon(outer, null);
		// and the location
		lr = new LocationRange(area2);
		s1.constrainTo(lr);

		// give it a course
		CourseRange cRange = new CourseRange(Math.toRadians(45),
				Math.toRadians(135));
		s1.constrainTo(cRange);
		s2.constrainTo(cRange);
		s3.constrainTo(cRange);

		// and a speed
		SpeedRange sRange = new SpeedRange(0.000001, metreLarge);
		s1.constrainTo(sRange);
		s2.constrainTo(sRange);
		s3.constrainTo(sRange);

		// ok, now lets put in the new state
		LocationAnalysisContribution lac = new LocationAnalysisContribution();
		lac.actUpon(space);

		// check they have ranges
		assertNotNull("Loc range created", s1.getLocation());
		assertNotNull("Loc range created", s3.getLocation());

//		System.out.println(area2.toText());
//		System.out.println("  ");
//		System.out.println(s1.getLocation().getGeometry().toText());
//		System.out.println("  ");
//		System.out.println(s2.getLocation().getGeometry().toText());
//		System.out.println("  ");
//		System.out.println(s3.getLocation().getGeometry().toText());

	}

	@Test
	public void testAnalysis() throws IncompatibleStateException
	{
		ProblemSpace space = new ProblemSpace();
		VehicleType vType = new VehicleType("UK Ferry", GeoSupport.kts2MSec(0),
				GeoSupport.kts2MSec(30000), Math.toRadians(0), Math.toRadians(1), 0.2,
				0.4, 0.2, 0.4);
		space.setVehicleType(vType);


		// ok, create the state
		Date oldDate = new Date(100000);
		Date newDate = new Date(160000);
		BoundedState oldState = new BoundedState(oldDate);
	//	BoundedState newStateConstraint = new BoundedState(newDate);
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
		// get ready to analyse
		lac = new LocationAnalysisContribution();

		// give it a course
		cRange = new CourseRange(Math.toRadians(20), Math.toRadians(60));
		newStateNoConstraint.constrainTo(cRange);

		// and a speed
		sRange = new SpeedRange(GeoSupport.kts2MSec(6), GeoSupport.kts2MSec(9));
		newStateNoConstraint.constrainTo(sRange);

		space.add(oldState);
		space.add(newStateNoConstraint);

		lac.actUpon(space);

		assertNotNull("now has loc bounds", newStateNoConstraint.getLocation());

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
		LocationRange newB = lac.getRangeFor(bs, newDate.getTime()
				- bs.getTime().getTime());
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
