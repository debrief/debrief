/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionDataType;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.legs.AlteringLeg;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightLegTests;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.manager.mock.MockVehicleTypesManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
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

	/*@Before
	public void prepareBoundsManager()
	{
		boundsManager = new BoundsManager();

		VehicleType type = new MockVehicleTypesManager().getAllTypes().get(0);
		boundsManager.setVehicleType(type);

		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getLongData());
		bearingMeasurementContribution.setAutoDetect(false);
		boundsManager.addContribution(bearingMeasurementContribution);

		courseForecastContribution = new CourseForecastContribution();
		courseForecastContribution.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		courseForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		courseForecastContribution.setMinCourse(Math.toRadians(110d));
		courseForecastContribution.setMaxCourse(Math.toRadians(300d));
		boundsManager.addContribution(courseForecastContribution);

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

	private ArrayList<CoreLeg> _scoredLegs = null;
	private boolean _started = false;
	
	@Test
	public void testLegScores()
	{
		boundsManager.run();

		// create a solution generator
		SolutionGenerator genny = new SolutionGenerator();
		
		// TODO: IAN, set the weight or bearing meas to zero, to cancel it out
		bearingMeasurementContribution.setWeight(0);
		
		// but, add our 'special' generator
		boundsManager.addContribution(new PredicableForecastContribution());
		
		// get listening
		genny.addReadyListener(new IGenerateSolutionsListener()
		{
			@Override
			public void solutionsReady(CompositeRoute[] routes)
			{
			}
			
			@Override
			public void legsScored(ArrayList<CoreLeg> theLegs)
			{
				_scoredLegs = theLegs;
			}

			@Override
			public void startingGeneration()
			{
				_started = true;
			}
		});
		
		assertNull("legs not there yet", _scoredLegs);
		assertFalse("not started yet", _started);
		
		genny.statesBounded(boundsManager);
		
		// check the calc got called
		assertNotNull(_scoredLegs);
		assertTrue("has started", _started);
		
		// work out the total
		double total = 0;
		total = calcLegScore(total);
		
		assertEquals("have some scores", 9091, total, 0.0001);
		
		// ok, check that we can turn BMC back on
		bearingMeasurementContribution.setWeight(1);
		
		// and run the calcs again
		genny.statesBounded(boundsManager);
		total = calcLegScore(total);
		
		assertEquals("have some more scores", 62441, (int)total);
	}

	private double calcLegScore(double total)
	{
		for (Iterator<CoreLeg> iterator = _scoredLegs.iterator(); iterator.hasNext();)
		{
			CoreLeg thisL = iterator.next();
			
			// and get the routes
			CoreRoute[][] routes = thisL.getRoutes();
			for(int i=0;i<routes.length;i++)
				for(int j=0;j<routes[0].length;j++)
				{
					CoreRoute thisR = routes[i][j];
					double thisScore = thisR.getScore();
					if(thisScore < 0)
					{
						System.out.println("here");
					}
					total += thisScore;
				}
		}
		return total;
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

	// private void writeThisLeg(CoreLeg leg)
	// {
	// Date first = leg.getFirst().getTime();
	// Date last = leg.getLast().getTime();
	// System.out.println("type:" + leg.getType() + " first:" + first + " last:"
	// + last);
	// }

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
	public void testMultiLegAchievable() throws ParseException
	{
		WKTReader reader = new WKTReader();
		SpeedRange sr = new SpeedRange(0, 65000);

		BoundedState s0 = new BoundedState(new Date(5000));
		Geometry geom = reader.read("		POLYGON ((1 1, 2 1, 2 3, 1 3, 1 1 ))");
		s0.setLocation(new LocationRange(geom));
		s0.setSpeed(sr);

		BoundedState s1 = new BoundedState(new Date(10000));
		geom = reader.read("	POLYGON ((3 1, 4 1, 4 2, 3 2, 3 1))");
		s1.setLocation(new LocationRange(geom));
		s1.setSpeed(sr);

		BoundedState s2 = new BoundedState(new Date(15000));
		geom = reader.read("		POLYGON ((5 1, 6 1, 6 3, 5 3, 5 1))");
		s2.setLocation(new LocationRange(geom));
		s2.setSpeed(sr);

		// check it got read int
		assertNotNull("got read in", geom);

		ArrayList<BoundedState> l1States = new ArrayList<BoundedState>();
		l1States.add(s0);
		l1States.add(s1);
		l1States.add(s2);

		// create the straight leg, sl1
		StraightLeg sl1 = new StraightLeg("sl1", l1States);
		// here is straight leg one: http://i.imgur.com/sA1kMFK.png

		ArrayList<Point> s1start = new ArrayList<Point>();
		s1start.add(GeoSupport.getFactory().createPoint(new Coordinate(1, 1)));
		s1start.add(GeoSupport.getFactory().createPoint(new Coordinate(2, 1)));
		s1start.add(GeoSupport.getFactory().createPoint(new Coordinate(2, 3)));
		s1start.add(GeoSupport.getFactory().createPoint(new Coordinate(1, 3)));

		ArrayList<Point> s1end = new ArrayList<Point>();
		s1end.add(GeoSupport.getFactory().createPoint(new Coordinate(5, 1)));
		s1end.add(GeoSupport.getFactory().createPoint(new Coordinate(6, 1)));
		s1end.add(GeoSupport.getFactory().createPoint(new Coordinate(6, 3)));
		sl1.calculatePerms(s1start, s1end);

		// check they're created
		assertEquals("got correct perms", sl1.getNumAchievable(), 12);

		// let them sort out what's achievable
		sl1.decideAchievableRoutes();

		// check they're created
		assertEquals("got correct perms", sl1.getNumAchievable(), 10);

		// check the right ones failed
		assertFalse("right one failed", sl1.getRoutes()[2][2].isPossible());
		assertFalse("right one failed", sl1.getRoutes()[3][2].isPossible());

		// handraulically force the other point-B (1,3) points to fail
		sl1.getRoutes()[3][1].setImpossible();
		// sl1.getRoutes()[3][0].setImpossible();

		// now for the second straight leg

		BoundedState s4 = new BoundedState(new Date(20000));
		geom = reader.read("		POLYGON ((8 2, 9 2, 9 4, 8 4, 8 2))");
		s4.setLocation(new LocationRange(geom));
		s4.setSpeed(sr);

		BoundedState s5 = new BoundedState(new Date(25000));
		geom = reader.read("	POLYGON ((10 0.5, 11 0.5, 11 1.5, 10 1.5, 10 0.5))");
		s5.setLocation(new LocationRange(geom));
		s5.setSpeed(sr);

		BoundedState s6 = new BoundedState(new Date(30000));
		geom = reader.read("		POLYGON ((12 0, 13  0, 13 1, 12 1, 12 0))");
		s6.setLocation(new LocationRange(geom));
		s6.setSpeed(sr);

		ArrayList<BoundedState> l2States = new ArrayList<BoundedState>();
		l2States.add(s4);
		l2States.add(s5);
		l2States.add(s6);

		// create the straight leg, sl1
		StraightLeg sl2 = new StraightLeg("sl2", l2States);
		// here is straight leg two: http://i.imgur.com/lbHZ2Tu.png

		ArrayList<Point> s2start = new ArrayList<Point>();
		s2start.add(GeoSupport.getFactory().createPoint(new Coordinate(8, 2)));
		s2start.add(GeoSupport.getFactory().createPoint(new Coordinate(9, 2)));
		s2start.add(GeoSupport.getFactory().createPoint(new Coordinate(9, 4)));
		s2start.add(GeoSupport.getFactory().createPoint(new Coordinate(8, 3)));

		ArrayList<Point> s2end = new ArrayList<Point>();
		s2end.add(GeoSupport.getFactory().createPoint(new Coordinate(12, 0)));
		s2end.add(GeoSupport.getFactory().createPoint(new Coordinate(13, 0)));
		s2end.add(GeoSupport.getFactory().createPoint(new Coordinate(13, 1)));
		sl2.calculatePerms(s2start, s2end);

		// check they're created
		assertEquals("got correct perms", sl2.getNumAchievable(), 12);

		sl2.decideAchievableRoutes();

		// check what is achievable
		assertEquals("got correct perms", sl2.getNumAchievable(), 8);

		// check the right ones failed
		assertFalse("right one failed", sl2.getRoutes()[2][2].isPossible());
		assertFalse("right one failed", sl2.getRoutes()[3][2].isPossible());

		// now for the altering leg
		ArrayList<BoundedState> a1States = new ArrayList<BoundedState>();
		a1States.add(s2);
		a1States.add(new BoundedState(new Date(17000)));
		a1States.add(s4);
		AlteringLeg a1 = new AlteringLeg("al1", a1States);

		ArrayList<Point> a1Start = new ArrayList<Point>();
		a1Start.add(GeoSupport.getFactory().createPoint(new Coordinate(5, 1)));
		a1Start.add(GeoSupport.getFactory().createPoint(new Coordinate(6, 1)));
		a1Start.add(GeoSupport.getFactory().createPoint(new Coordinate(6, 3)));

		ArrayList<Point> a1End = new ArrayList<Point>();
		a1End.add(GeoSupport.getFactory().createPoint(new Coordinate(8, 2)));
		a1End.add(GeoSupport.getFactory().createPoint(new Coordinate(9, 2)));
		a1End.add(GeoSupport.getFactory().createPoint(new Coordinate(9, 4)));
		a1End.add(GeoSupport.getFactory().createPoint(new Coordinate(8, 3)));
		a1.calculatePerms(a1Start, a1End);

		// check they're all achievable
		assertEquals("all achievable", 12, a1.getNumAchievable());

		a1.calculatePerms(a1Start, a1End);

		// now see which are achievable
		a1.decideAchievableRoutes();

		// now how have we got on?
		assertEquals("not altering legs achievable", 4, a1.getNumAchievable());

		// ok, put them into a legs object
		SolutionGenerator genny = new SolutionGenerator();
		ArrayList<CoreLeg> theLegs = new ArrayList<CoreLeg>();
		theLegs.add(sl1);
		theLegs.add(a1);
		theLegs.add(sl2);
		int[][] achievable = genny.calculateAchievableRoutesFor(theLegs);

		// ok, how many start routes are there
		assertEquals("correct num possible routes", 3,
				util_CountStartLocations(achievable));

		// StraightLegTests.util_writeMatrix("straight 1a", sl1.getRoutes());

		// ok, check they were all cancelled.
		assertEquals("all start relevant route location allowable", 9,
				sl1.getNumAchievable());

		// ok, now get it to tidy them
		genny.cancelUnachievable(theLegs, achievable);

		// StraightLegTests.util_writeMatrix("straight 1b", sl1.getRoutes());

		// ok, check they were all cancelled.
		assertEquals("start point routes cancelled", 8, sl1.getNumAchievable());

		// StraightLegTests.util_writeMatrix("straight 1", sl1.getRoutes());
		// StraightLegTests.util_writeMatrix("altering 1", a1.getRoutes());
		// StraightLegTests.util_writeMatrix("straight 2", sl2.getRoutes());
		// StraightLegTests.util_writeMatrix("achievable", achievable);

	}

	public static int util_CountStartLocations(int[][] routes)
	{
		int ctr = 0;
		for (int x = 0; x < routes.length; x++)
		{
			for (int y = 0; y < routes[0].length; y++)
			{
				if (routes[x][y] > 0)
				{
					ctr++;
					break;
				}
			}
		}
		return ctr;
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
		// boundsManager.run();

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
		sr = createRoute("sr", 1.5, 2.5, 5.5, 2.5, 5000, 15000, theStates);
		sr.generateSegments(theStates);

		sp.process(sr);

		// check it's not achievable
		assertFalse("can't do it", sr.isPossible());

		// try another route
		sr = createRoute("sr", 1.5, 2.0, 5.5, 2.5, 5000, 15000, theStates);

		sp.process(sr);

		// check it's not achievable
		assertFalse("can't do it", sr.isPossible());

		// try another route
		sr = createRoute("sr", 1.5, 2.4, 5.5, 1.5, 5000, 15000, theStates);

		sp.process(sr);

		// check it's not achievable
		assertTrue("can do it", sr.isPossible());

	}

	private static StraightRoute createRoute(String name, double x0, double y0,
			double x1, double y1, long t0, long t1, ArrayList<BoundedState> theStates)
	{
		Point p0 = GeoSupport.getFactory().createPoint(new Coordinate(x0, y0));
		Point p1 = GeoSupport.getFactory().createPoint(new Coordinate(x1, y1));

		StraightRoute sr = new StraightRoute(name, p0, new Date(t0), p1, new Date(
				t1));
		sr.generateSegments(theStates);

		return sr;

	}

	@Test
	public void testGeomPerformance() throws ParseException
	{
		final long perms = 100000;
		long tNow;
		WKTReader reader = new WKTReader();

		boolean running = true;

		// first, check our understanding of the covers method
		Geometry geomA = reader
				.read("		POLYGON ((0 0.5, 6.5 0.5, 6.5 2.5, 0 2.5, 0 0.5))");
		Geometry ptA = reader.read("	POINT (0.1 0.5)");
		assertTrue("is contained", geomA.covers(ptA));

		geomA = reader.read("		POLYGON ((0 0.5, 6.5 0.5, 6.5 2.5, 0 2.5, 0 0.5))");
		ptA = reader.read("	POINT (0.09 0.49)");
		assertFalse("is contained", geomA.covers(ptA));

		// ok, now carry on;
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

	private static int called = 0;

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

		// create a special contribution, that produces a predictable score
		
		SolutionGenerator genny = new SolutionGenerator();
		genny.addReadyListener(new IGenerateSolutionsListener()
		{
			public void solutionsReady(CompositeRoute[] routes)
			{
				called++;
			}


			@Override
			public void legsScored(ArrayList<CoreLeg> theLegs)
			{
				called++;
			}


			@Override
			public void startingGeneration()
			{
				_started = true;;
			}

		});

		// ok, get it to run
		assertEquals("not called yet",0, called);
		_started = false;

		// ok, get firing
		genny.statesBounded(boundsManager);

		// ok, get it to run
		assertTrue("has started", _started);
		assertEquals("now called ",2, called);
	}

	private static class PredicableForecastContribution extends BaseContribution
	{

		private static final long serialVersionUID = 1L;



		@Override
		public void actUpon(ProblemSpace space) throws IncompatibleStateException
		{
		}
		
		

		@Override
		protected double cumulativeScoreFor(CoreRoute route)
		{
			int code = route.getName().hashCode();
			double score = Math.abs(code % 10);
			if(score < 0)
				System.out.println("here 2");
			return score;
		}



		@Override
		public ContributionDataType getDataType()
		{
			return ContributionDataType.FORECAST;
		}
		
	}*/
}
