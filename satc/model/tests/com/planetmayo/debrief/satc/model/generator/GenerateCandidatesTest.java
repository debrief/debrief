package com.planetmayo.debrief.satc.model.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.SolutionGenerator.ISolutionsReadyListener;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightLeg.ScaledPolygons;
import com.planetmayo.debrief.satc.model.legs.StraightLegTests;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.manager.mock.MockVehicleTypesManager;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@SuppressWarnings("deprecation")
public class GenerateCandidatesTest extends ModelTestBase
{

	private IBoundsManager boundsManager;
	private BearingMeasurementContribution bearingMeasurementContribution;
	private CourseForecastContribution courseForecastContribution;

	@Before
	public void prepareBoundsManager()
	{
		boundsManager = new BoundsManager();

		VehicleType type = new MockVehicleTypesManager().getAllTypes().get(0);
		boundsManager.setVehicleType(type);

		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getLongData());
		bearingMeasurementContribution.setAutoDetect(false);
		boundsManager.addContribution(bearingMeasurementContribution);

		// courseForecastContribution = new CourseForecastContribution();
		// courseForecastContribution.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		// courseForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20,
		// 0));
		// courseForecastContribution.setMinCourse(Math.toRadians(110d));
		// courseForecastContribution.setMaxCourse(Math.toRadians(300d));
		// boundsManager.addContribution(courseForecastContribution);

		StraightLegForecastContribution sl = new StraightLegForecastContribution();
		sl.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		sl.setFinishDate(new Date(110, 0, 12, 12, 18, 0));
		sl.setName("Straight leg 1");
		boundsManager.addContribution(sl);

		sl = new StraightLegForecastContribution();
		sl.setStartDate(new Date(110, 0, 12, 12, 25, 0));
		sl.setFinishDate(new Date(110, 0, 12, 12, 34, 0));
		sl.setName("Straight leg 2");
		boundsManager.addContribution(sl);

		sl = new StraightLegForecastContribution();
		sl.setStartDate(new Date(110, 0, 12, 12, 42, 0));
		sl.setFinishDate(new Date(110, 0, 12, 12, 56, 0));
		sl.setName("Straight leg 3");
		boundsManager.addContribution(sl);

		boundsManager.addContribution(new LocationAnalysisContribution());
		boundsManager.addContribution(new CourseAnalysisContribution());

	}

	@Test
	public void testExtractLegs()
	{
		boundsManager.run();

		// ok, let's look at the legs
		Collection<BoundedState> theStates = boundsManager.getSpace().states();
		ArrayList<CoreLeg> theLegs = new SolutionGenerator().getTheLegs(theStates);

		assertNotNull("got some legs", theLegs);
		assertEquals("got 7 (3 straight, 4 turns) legs", 7, theLegs.size());

		// check the legs overlap
		CoreLeg lastLeg = null;
		Iterator<CoreLeg> iter = theLegs.iterator();

		while (iter.hasNext())
		{
			CoreLeg coreLeg = (CoreLeg) iter.next();
			// writeThisLeg(coreLeg);
			if (lastLeg != null)
			{
				assertEquals("state gets re-used", lastLeg.getLast(),
						coreLeg.getFirst());
			}
			lastLeg = coreLeg;
			// writeThisLeg(coreLeg);
		}
	}

	private void writeThisLeg(CoreLeg leg)
	{
		Date first = leg.getFirst().getTime();
		Date last = leg.getLast().getTime();
		System.out.println("type:" + leg.getType() + " first:" + first + " last:"
				+ last);
	}

	@Test
	public void testJTSWithin() throws ParseException
	{
		WKTReader reader = new WKTReader();

		Geometry geom = reader.read("	POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))");
		Geometry pt = reader.read("	POINT (1 1)");
		assertTrue("First combi contained:", pt.within(geom));

		geom = reader.read("		POLYGON ((0 0.5, 6.5 0.5, 6.5 2.5, 0 2.5, 0 0.5))");
		pt = reader.read("	POINT (5.5 1.5)");
		assertTrue("Second combi contained:", pt.within(geom));
	}

	@Test
	public void testScaledPolygons() throws ParseException
	{
		WKTReader reader = new WKTReader();

		BoundedState s0 = new BoundedState(new Date(5000));
		Geometry geom = reader.read("		POLYGON ((1 1, 2 1, 2 3, 1 3, 1 1 ))");
		s0.setLocation(new LocationRange(geom));

		BoundedState s1 = new BoundedState(new Date(10000));
		geom = reader.read("	POLYGON ((3 1, 4 1, 4 2, 3 2, 3 1))");
		s1.setLocation(new LocationRange(geom));

		BoundedState s2 = new BoundedState(new Date(15000));
		geom = reader.read("		POLYGON ((5 1, 6 1, 6 3, 5 3, 5 1))");
		s2.setLocation(new LocationRange(geom));

		// check it got read int
		assertNotNull("got read in", geom);

		ArrayList<BoundedState> theStates = new ArrayList<BoundedState>();
		theStates.add(s0);
		theStates.add(s1);
		theStates.add(s2);
		boundsManager.run();

		// ok, now produce a route through the legs
		StraightRoute sr = new StraightRoute("sr",
				(Point) reader.read("POINT (1.5 1.5)"), new Date(5000),
				(Point) reader.read("POINT (5.5 1.5)"), new Date(15000));
		sr.generateSegments(theStates);

		// work on the first polygon
		ScaledPolygons sp = new StraightLeg.ScaledPolygons(theStates);

		sp.process(sr);

		// check it's achievable
		assertTrue("can do it", sr.isPossible());

		// try another route
		sr = new StraightRoute("sr", (Point) reader.read("POINT (2.5 1.5)"),
				new Date(5000), (Point) reader.read("POINT (5.5 1.5)"), new Date(15000));
		sr.generateSegments(theStates);

		sp.process(sr);

		// check it's not achievable
		assertFalse("can't do it", sr.isPossible());

		// try another route
		sr = new StraightRoute("sr", (Point) reader.read("POINT (1.5 1.5)"),
				new Date(5000), (Point) reader.read("POINT (5.5 2.5)"), new Date(15000));
		sr.generateSegments(theStates);

		sp.process(sr);

		// check it's not achievable
		assertFalse("can't do it", sr.isPossible());

		// try another route
		sr = new StraightRoute("sr", (Point) reader.read("POINT (1.5 2.4)"),
				new Date(5000), (Point) reader.read("POINT (5.5 1.5)"), new Date(15000));
		sr.generateSegments(theStates);

		sp.process(sr);

		// check it's not achievable
		assertTrue("can do it", sr.isPossible());

	}

	@Test
	public void testGeomPerformance() throws ParseException
	{
		final long perms = 100000;
		long tNow;
		WKTReader reader = new WKTReader();
		
		boolean running = true;

		Geometry geom = reader
				.read("		POLYGON ((0 0.5, 6.5 0.5, 6.5 2.5, 0 2.5, 0 0.5))");
		Geometry pt1 = reader.read("	POINT (5.5 1.5)");
		Geometry pt2 = reader.read("	POINT (5.5 1.5)");

		tNow = System.currentTimeMillis();
		for (int i = 0; i < perms; i++)
		{
			boolean res1 = pt1.within(geom);
			boolean res2 = pt2.within(geom);
			running = res1 || res2;
		}
		System.out.println("within:" + (System.currentTimeMillis() - tNow));

		tNow = System.currentTimeMillis();
		for (int i = 0; i < perms; i++)
		{
			boolean res1 = pt1.intersects(geom);
			boolean res2 = pt2.intersects(geom);
			running = res1 || res2;
		}
		System.out.println("intersects:" + (System.currentTimeMillis() - tNow));

		tNow = System.currentTimeMillis();
		for (int i = 0; i < perms; i++)
		{
			boolean res1 = pt1.disjoint(geom);
			boolean res2 = pt2.disjoint(geom);
			running = res1 || res2;
		}
		System.out.println("disjoint:" + (System.currentTimeMillis() - tNow));

		System.out.println(running);

	}

	//
	// @Test
	// public void testLookAtStraight3()
	// {
	// boundsManager.run();
	//
	// // ok, let's look at the legs
	// Collection<BoundedState> theStates = boundsManager.getSpace().states();
	// ArrayList<CoreLeg> theLegs= new SolutionGenerator().getTheLegs(theStates);
	//
	// CoreLeg sl3 = theLegs.get(5);
	// assertEquals("Straight leg 3", sl3.getName());
	//
	// // split into routes
	// sl3.generateRoutes(20);
	//
	// // and decide what's achievable
	// sl3.decideAchievableRoutes();
	//
	// // ok, have a look at it
	// StraightLegTests.util_writeMatrix("sl3", sl3.getRoutes());
	// }

	@Test
	public void testCalculateAchievable()
	{
		boundsManager.run();

		// ok, let's look at the legs
		Collection<BoundedState> theStates = boundsManager.getSpace().states();
		ArrayList<CoreLeg> theLegs = new SolutionGenerator().getTheLegs(theStates);

		SolutionGenerator genny = new SolutionGenerator();

		genny.generateRoutes(theLegs);
		genny.decideAchievable(theLegs);

		int[][] achievable = genny.calculateAchievableRoutesFor(theLegs);

		assertNotNull("produced results matrix", achievable);

		// TODO !! find out why straight leg 3 is impossible!

		StraightLegTests.util_writeMatrix("integrated", achievable);

	}

	private static boolean called = false;

	@Test
	public void testWholeCycle()
	{
		boundsManager.run();

		// ok, let's look at the legs
		Collection<BoundedState> theStates = boundsManager.getSpace().states();

		// ok, check the locations
		for (Iterator<BoundedState> iterator = theStates.iterator(); iterator
				.hasNext();)
		{
			BoundedState boundedState = (BoundedState) iterator.next();
			assertNotNull("course constraint should be present",
					boundedState.getCourse());
			assertNotNull("speed constraint should be present",
					boundedState.getSpeed());
			assertNotNull("location constraint should be present",
					boundedState.getLocation());
		}

		SolutionGenerator genny = new SolutionGenerator();
		genny.addReadyListener(new ISolutionsReadyListener()
		{
			public void solutionsReady(CompositeRoute[] routes)
			{
				called = true;
			}
		});

		// ok, get it to run
		assertFalse("not called yet", called);

		// ok, get firing
		genny.complete(boundsManager);

		// ok, get it to run
		assertTrue("now called yet", called);
	}

}
