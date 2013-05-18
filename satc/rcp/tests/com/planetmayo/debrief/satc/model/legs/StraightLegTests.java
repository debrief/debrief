package com.planetmayo.debrief.satc.model.legs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.math.Vector2D;

@SuppressWarnings("deprecation")
public class StraightLegTests extends ModelTestBase
{
	private BearingMeasurementContribution bearingMeasurementContribution;
	private CourseForecastContribution courseForecastContribution;

	@Before
	public void prepareBoundsManager()
	{
		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getShortData());

		courseForecastContribution = new CourseForecastContribution();
		courseForecastContribution.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		courseForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		courseForecastContribution.setMinCourse(50d);
		courseForecastContribution.setMaxCourse(100d);
	}

	@Test
	public void testGridding() throws ParseException
	{

		WKTReader wkt = new WKTReader();
		Geometry geom = wkt
				.read("POLYGON ((0.0 3.0, 2.0 4.0, 4.0 4.0, 2.0 3.0, 0.0 3.0))");

		// how many points?
		final int num = 100;

		// ok, try the tesselate function
		long start = System.currentTimeMillis();
		ArrayList<Point> pts = MakeGrid.ST_Tile(geom, num, 6);
		System.out.println("elapsed:" + (System.currentTimeMillis() - start));
		assertNotNull("something returned", pts);
		assertEquals("correct num", 98, pts.size());
		Iterator<Point> iter = pts.iterator();
		while (iter.hasNext())
		{
			Point po = iter.next();
			// check the point is in the area
			assertEquals("point is in area", true, geom.contains(po));

			// send out for debug
			// System.out.println(po.getX() + "\t" + po.getY());
		}
	}

	@Test
	public void testRouteSegmentation1()
	{
		Date startD = new Date(2012, 5, 5, 12, 0, 0);
		Date endD = new Date(2012, 5, 5, 17, 0, 0);
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		StraightRoute testR = new StraightRoute("1", startP, startD, endP, endD);

		assertNull("no states, yet", testR.getStates());

		// ok, generate some times
		ArrayList<BoundedState> theTimes = new ArrayList<BoundedState>();
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 11, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 12, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 14, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 15, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 17, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 18, 0, 0)));

		testR.generateSegments(theTimes);

		ArrayList<State> states = testR.getStates();
		assertNotNull("have some states", states);
		assertEquals("correct num states", 4, states.size());
	}

	@Test
	public void testRouteSegmentation2()
	{
		Date startD = new Date(2012, 5, 5, 12, 0, 0);
		Date endD = new Date(2012, 5, 5, 12, 0, 0);
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		StraightRoute testR = new StraightRoute("1", startP, startD, endP, endD);

		assertNull("no states, yet", testR.getStates());

		// ok, generate some times
		ArrayList<BoundedState> theTimes = new ArrayList<BoundedState>();
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 11, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 12, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 14, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 15, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 17, 0, 0)));
		theTimes.add(new BoundedState(new Date(2012, 5, 5, 18, 0, 0)));

		testR.generateSegments(theTimes);

		ArrayList<State> states = testR.getStates();
		assertNotNull("have some states", states);
		assertEquals("correct num states", 1, states.size());
	}

	@Test
	public void testFreqAssumption()
	{
		int ctr = 0;
		int freq = 3;
		for (int i = 0; i < 8; i++)
		{
			if ((i % freq) == 0)
				ctr++;
		}
		assertEquals("ran correct num times", 3, ctr);

		ctr = 0;
		freq = 2;
		for (int i = 0; i < 8; i++)
		{
			if ((i % freq) == 0)
				ctr++;
		}
		assertEquals("ran correct num times", 4, ctr);

		ctr = 0;
		freq = 1;
		for (int i = 0; i < 8; i++)
		{
			if ((i % freq) == 0)
				ctr++;
		}
		assertEquals("ran correct num times", 8, ctr);

		Collection<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(2);
		list.add(2);
		list.add(2);
		list.add(2);
		list.add(2);
		list.add(2);

		assertEquals("correct length", 8, list.size());

		ctr = 0;
		freq = 3;
		for (int i = 0; i < 8; i++)
		{
			if (((i % freq) == 0) || (i == list.size() - 1))
			{
				ctr++;
			}
		}
		assertEquals("ran correct num times (including hte last instance", 4, ctr);

	}

	@Test
	public void testRouteCourseCalc()
	{
		// find the course
		assertEquals("correct course", 45f, Math.toDegrees( new Vector2D(new Coordinate(0, 0),
				new Coordinate(1, 1)).angle()), 0.0001);
		// find the course
		assertEquals("correct course", 0f, ( Math.toDegrees(Math.PI/2 -  new Vector2D(new Coordinate(0, 0),
				new Coordinate(0, 1)).angle())), 0.0001);
		assertEquals("correct course", 90f, Math.toDegrees(Math.PI/2 - new Vector2D(new Coordinate(0, 0),
				new Coordinate(1, 0)).angle()), 0.0001);
	}

	@Test
	public void testRouteCourseAndSpeed()
	{
		Date startD = new Date(2012, 5, 5, 12, 0, 0);
		Date endD = new Date(2012, 5, 5, 17, 0, 0);
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		StraightRoute testR = new StraightRoute("1", startP, startD, endP, endD);

		assertEquals("correct course", 0, testR.getCourse(), EPS);
		assertEquals("correct speed", GeoSupport.kts2MSec(12), testR.getSpeed(),
				0.01);

		startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		endP = GeoSupport.getFactory().createPoint(new Coordinate(1, 1));
		testR = new StraightRoute("1", startP, startD, endP, endD);

		assertEquals("correct course", Math.toRadians(45), testR.getCourse(), EPS);
		assertEquals("correct speed", GeoSupport.kts2MSec(16.97), testR.getSpeed(),
				0.01);

		startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		endP = GeoSupport.getFactory().createPoint(new Coordinate(1, -1));
		testR = new StraightRoute("1", startP, startD, endP, endD);

		assertEquals("correct course", Math.toRadians(-45), testR.getCourse(), EPS);
		assertEquals("correct speed", GeoSupport.kts2MSec(16.97), testR.getSpeed(),
				0.01);

		startP = GeoSupport.getFactory().createPoint(new Coordinate(1, -1));
		endP = GeoSupport.getFactory().createPoint(new Coordinate(1, -1));
		testR = new StraightRoute("1", startP, startD, endP, endD);

		assertEquals("correct course", Math.toRadians(0), testR.getCourse(), EPS);
		assertEquals("correct speed", 0, testR.getSpeed(), 0.01);

	}

	@Test
	public void testBearingCalc()
	{
		GeoPoint g1 = new GeoPoint(50, 4);
		assertEquals("correct bearing", Math.PI / 2, g1.bearingTo(GeoSupport
				.getFactory().createPoint(new Coordinate(5, 50))), 0.0001);
		assertEquals("correct bearing", Math.PI / 4, g1.bearingTo(GeoSupport
				.getFactory().createPoint(new Coordinate(5, 51))), 0.0001);
		assertEquals(
				"correct bearing",
				Math.PI + Math.PI / 4,
				g1.bearingTo(GeoSupport.getFactory().createPoint(new Coordinate(3, 49))),
				0.0001);
		assertEquals("correct bearing", -Math.PI / 2, g1.bearingTo(GeoSupport
				.getFactory().createPoint(new Coordinate(3, 50))), 0.0001);
	}

	@Test
	public void testErrorCalc()
	{
		Point startP = GeoSupport.getFactory().createPoint(
				new Coordinate(-30.003, 0));
		Date startT = parseDate("yyMMdd HHmmss", "100112 121329");
		Point endP = GeoSupport.getFactory().createPoint(
				new Coordinate(-30.03, 0.025));
		Date endT = parseDate("yyMMdd HHmmss", "100112 122429");
		@SuppressWarnings("unused")
		StraightRoute thisR = new StraightRoute("sr1", startP, startT, endP, endT);

		// TODO: IAN - HIGH produce a set of state objects, invite thisR to segment
		// itself
		@SuppressWarnings("unused")
		ArrayList<BoundedState> theStates = null;
		// thisR.generateSegments(theStates);

		// double val =
		// bearingMeasurementContribution.calculateErrorScoreFor(thisR);
		// assertEquals("calculated something", 1, val, 0.0001);

		// TODO: IAN test how bearing measurement works, especially how it find the
		// right measurement to
		// compare against.

	}

	@Test
	public void testMatrixStorage() throws ParseException
	{
		WKTReader wkt = new WKTReader();
		Geometry leg1Start = wkt.read("POLYGON ((0 3, 2 4, 4 4, 2 3, 0 3))");
		Geometry leg1End = wkt.read("POLYGON ((5 1, 5.5 2,6 2,6 1, 5 1))");

		ArrayList<Point> startP = MakeGrid.ST_Tile(leg1Start, 10, 6);
		ArrayList<Point> endP = MakeGrid.ST_Tile(leg1End, 10, 6);

		assertNotNull("produced start", startP);
		assertNotNull("produced end", endP);

		int startLen = startP.size();
		int endLen = endP.size();

		CoreRoute[][] leg1 = new CoreRoute[startLen][endLen];

		Date tStart = new Date(2012, 1, 1, 11, 0, 0);
		Date tEnd = new Date(2012, 1, 1, 15, 0, 0);

		int ctr = 0;
		for (int i = 0; i < startLen; i++)
		{
			for (int j = 0; j < endLen; j++)
			{
				leg1[i][j] = new StraightRoute("1", startP.get(i), tStart, endP.get(j),
						tEnd);
				ctr++;
			}
		}

		// check we have the correct nubmer of points
		assertEquals("correct number of points", startLen * endLen, ctr);

	}

/*	@Test
	public void testStraightLegCreation() throws ParseException,
			IncompatibleStateException
	{
		ArrayList<BoundedState> sList1 = createStates(3, 36, false);
		ArrayList<BoundedState> sList2 = createStates(3, 29, true);

		StraightLeg s1 = new StraightLeg("Straight_1", sList1);
		StraightLeg s2 = new StraightLeg("Straight_2", sList2);

		s1.generateRoutes(Precision.LOW);
		s2.generateRoutes(Precision.LOW);

		assertNotNull("created leg", s1);

		// check we're still achievable
		assertEquals("all still achievable", 504, s1.getNumAchievable());
		assertEquals("all still achievable", 504, s2.getNumAchievable());

		// generate the routes
		// ok, check what's achievable
		s1.decideAchievableRoutes();
		s2.decideAchievableRoutes();

		// check some knocked off
		assertEquals("fewer achievable", 165, s1.getNumAchievable());
		assertEquals("fewer achievable", 129, s2.getNumAchievable());

		// writeMatrix("s1",s1.getRoutes());
		// System.out.println("==========");
		// writeMatrix("s2", s2.getRoutes());

		// now multiply them together
		int[][] leg1Arr = s1.asMatrix();
		int[][] leg2Arr = s2.asMatrix();
		int[][] legRes = s2.multiply(leg1Arr, leg2Arr);

		// writeMatrix("l1", leg1Arr);
		// writeMatrix("l2", leg2Arr);
		// writeMatrix("l res", legRes);
		//
		// double check that the answer is of the correct size
		assertEquals("correct rows", s1.getRoutes().length, legRes.length);
		assertEquals("correct rows", s2.getRoutes()[0].length, legRes[0].length);

		// have a look at them
		// util_writePossibleRoutes(s1);

	}

	@Test
	public void testLegMultiplication()
	{
		int[][] m1 = new int[][]
		{
		{ 2, 3 },
		{ 1, 2 },
		{ 1, 1 } };
		int[][] m2 = new int[][]
		{
		{ 0, 2, 3 },
		{ 1, 2, 0 } };
		int[][] res = CoreLeg.multiply(m1, m2);
		util_writeMatrix("res", res);
	}

	@SuppressWarnings("unused")
	private void util_writePossibleRoutes(StraightLeg s1)
	{
		// have a look at the achievable routes
		RouteOperator writePossible = new RouteOperator()
		{
			public void process(StraightRoute theRoute)
			{
				if (theRoute.isPossible())
				{
					Coordinate coord = theRoute.first().getLocation().getCoordinate();
					System.out.println(coord.x + "\t" + coord.y);
					coord = theRoute.last().getLocation().getCoordinate();
					System.out.println(" " + coord.x + "\t" + coord.y);
				}
			}
		};
		System.out.println("= Possible: =");
		s1.applyToRoutes(writePossible);

		System.out.println("=====================");
		System.out.println("= Impossible: =");

		RouteOperator writeimPossible = new RouteOperator()
		{
			public void process(StraightRoute theRoute)
			{
				if (!theRoute.isPossible())
				{
					Coordinate coord = theRoute.first().getLocation().getCoordinate();
					System.out.println(coord.x + "\t" + coord.y);
					coord = theRoute.last().getLocation().getCoordinate();
					System.out.println(" " + coord.x + "\t" + coord.y);
				}
			}
		};
		s1.applyToRoutes(writeimPossible);
	}*/

	public static void util_writeMatrix(String name, CoreRoute[][] routes)
	{
		System.out.println("== " + name + " ==");
		for (int x = 0; x < routes.length; x++)
		{
			for (int y = 0; y < routes[0].length; y++)
			{
				CoreRoute thisR = routes[x][y];
				if (thisR.isPossible())
					System.out.print("1 ");
				else
					System.out.print("0 ");

			}
			System.out.println();
		}
	}

	public static void util_writeMatrix(String name, int[][] routes)
	{
		System.out.println("== " + name + " ==");
		for (int x = 0; x < routes.length; x++)
		{
			for (int y = 0; y < routes[0].length; y++)
			{
				System.out.print(routes[x][y] + " ");
			}
			System.out.println();
		}
	}

	private ArrayList<BoundedState> createStates(double minS, double maxS,
			boolean reverseOrder) throws ParseException, IncompatibleStateException
	{
		Date startA = new Date(2012, 5, 5, 12, 0, 0);
		Date startB = new Date(2012, 5, 5, 14, 0, 0);
		Date startC = new Date(2012, 5, 5, 15, 0, 0);
		Date startD = new Date(2012, 5, 5, 17, 0, 0);
		BoundedState bA = new BoundedState(startA);
		BoundedState bB = new BoundedState(startB);
		BoundedState bC = new BoundedState(startC);
		BoundedState bD = new BoundedState(startD);

		// apply location bounds
		WKTReader wkt = new WKTReader();
		LocationRange locA = new LocationRange(
				wkt.read("POLYGON ((0 3, 2 4, 4 4, 2 3, 0 3))"));
		LocationRange locB = new LocationRange(
				wkt.read("POLYGON ((2.63 2.56, 3.5 3.16, 4.11 3.42, 3.33 2.3, 2.63 2.56))"));
		LocationRange locC = new LocationRange(
				wkt.read("POLYGON ((3.32 1.99,3.93 2.71,4.64 2.87,3.81 1.78, 3.32 1.99))"));
		LocationRange locD = new LocationRange(
				wkt.read("POLYGON ((5 1, 5.5 2, 6 2, 6 1, 5 1))"));

		if (!reverseOrder)
		{
			bA.constrainTo(locA);
			bB.constrainTo(locB);
			bC.constrainTo(locC);
			bD.constrainTo(locD);
		}
		else
		{
			bA.constrainTo(locD);
			bB.constrainTo(locC);
			bC.constrainTo(locB);
			bD.constrainTo(locA);

		}

		// apply speed bounds
		SpeedRange sr = new SpeedRange(minS, maxS);
		bA.constrainTo(sr);
		bD.constrainTo(sr);

		ArrayList<BoundedState> sList = new ArrayList<BoundedState>();
		sList.add(bA);
		sList.add(bB);
		sList.add(bC);
		sList.add(bD);
		return sList;
	}

}
