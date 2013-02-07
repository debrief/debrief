package com.planetmayo.debrief.satc.model.generator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.Route;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;
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
		bearingMeasurementContribution = new BearingMeasurementContribution();
		bearingMeasurementContribution.loadFrom(TestSupport.getShortData());

		courseForecastContribution = new CourseForecastContribution();
		courseForecastContribution.setStartDate(new Date(110, 0, 12, 12, 15, 0));
		courseForecastContribution.setFinishDate(new Date(110, 0, 12, 12, 20, 0));
		courseForecastContribution.setMinCourse(50d);
		courseForecastContribution.setMaxCourse(100d);

		boundsManager = new BoundsManager();
		boundsManager.addContribution(bearingMeasurementContribution);
		boundsManager.addContribution(courseForecastContribution);
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
		Route testR = new Route("1", startP, startD, endP, endD);

		assertNull("no states, yet", testR.getStates());

		// ok, generate some times
		ArrayList<BoundedState> theTimes = new ArrayList<BoundedState>();
		theTimes.add(new BoundedState( new Date(2012, 5, 5, 11, 0, 0)));
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
		Route testR = new Route("1", startP, startD, endP, endD);

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
	public void testRouteCourseAndSpeed()
	{
		Date startD = new Date(2012, 5, 5, 12, 0, 0);
		Date endD = new Date(2012, 5, 5, 17, 0, 0);
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		Route testR = new Route("1", startP, startD, endP, endD);

		assertEquals("correct course", 0, testR.getCourse(), EPS);
		assertEquals("correct speed", GeoSupport.kts2MSec(12), testR.getSpeed(),
				0.01);

		startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		endP = GeoSupport.getFactory().createPoint(new Coordinate(1, 1));
		testR = new Route("1", startP, startD, endP, endD);

		assertEquals("correct course", Math.toRadians(45), testR.getCourse(), EPS);
		assertEquals("correct speed", GeoSupport.kts2MSec(16.97), testR.getSpeed(),
				0.01);

		startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		endP = GeoSupport.getFactory().createPoint(new Coordinate(1, -1));
		testR = new Route("1", startP, startD, endP, endD);

		assertEquals("correct course", Math.toRadians(-45), testR.getCourse(), EPS);
		assertEquals("correct speed", GeoSupport.kts2MSec(16.97), testR.getSpeed(),
				0.01);

		startP = GeoSupport.getFactory().createPoint(new Coordinate(1, -1));
		endP = GeoSupport.getFactory().createPoint(new Coordinate(1, -1));
		testR = new Route("1", startP, startD, endP, endD);

		assertEquals("correct course", Math.toRadians(0), testR.getCourse(), EPS);
		assertEquals("correct speed", 0, testR.getSpeed(), 0.01);

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

		Route[][] leg1 = new Route[startLen][endLen];

		Date tStart = new Date(2012, 1, 1, 11, 0, 0);
		Date tEnd = new Date(2012, 1, 1, 15, 0, 0);

		int ctr = 0;
		for (int i = 0; i < startLen; i++)
		{
			for (int j = 0; j < endLen; j++)
			{
				leg1[i][j] = new Route("1", startP.get(i), tStart, endP.get(j), tEnd);
				ctr++;
			}
		}

		// check we have the correct nubmer of points
		assertEquals("correct number of points", startLen * endLen, ctr);

	}

	@Test
	public void testLegCreation() throws ParseException,
			IncompatibleStateException
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
				wkt.read("POLYGON ((3.76 1.58, 4.55 2.39, 5.17 2.64, 4.38 1.53, 3.76 1.58))"));
		LocationRange locD = new LocationRange(
				wkt.read("POLYGON ((5 1, 5.5 2, 6 2, 6 1, 5 1))"));

		bA.constrainTo(locA);
		bB.constrainTo(locB);
		bC.constrainTo(locC);
		bD.constrainTo(locD);

		// apply speed bounds
		SpeedRange sr = new SpeedRange(3, 24);
		bA.constrainTo(sr);
		bD.constrainTo(sr);

		long tStart = System.currentTimeMillis();

		ArrayList<BoundedState> sList = new ArrayList<BoundedState>();
		sList.add(bA);
		sList.add(bB);
		sList.add(bC);
		sList.add(bD);

		StraightLeg sl = new StraightLeg("Straight_1", sList);

		System.out.println("elapsed:" + (System.currentTimeMillis() - tStart));

		assertNotNull("created leg", sl);

		// check we're still achievable
		assertEquals("all still achievable", 64, sl.getNumAchievable());

		// generate the routes
		// ok, check what's achievable
		sl.decideAchievableRoutes();

		// check some knocked off
		assertEquals("fewer achievable", 64, sl.getNumAchievable());
	}

	public static class StraightLeg
	{
		/*
		 * the route permutations through the leg This array will always be
		 * rectangular
		 */
		Route[][] myRoutes;

		/**
		 * how many points there are in the start polygon
		 * 
		 */
		private int _startLen;

		/**
		 * how many points there are in the end polygon
		 * 
		 */
		private int _endLen;

		private final String _name;

		private final ArrayList<BoundedState> _states;

		public StraightLeg(String name, ArrayList<BoundedState> states)
		{
			_states = states;
			_name = name;

			// how many cells per end-state?
			int gridNum = 10;

			// produce the grid of cells
			ArrayList<Point> startP = MakeGrid.ST_Tile(getFirst().getLocation()
					.getGeometry(), gridNum, 6);
			ArrayList<Point> endP = MakeGrid.ST_Tile(getLast().getLocation()
					.getGeometry(), gridNum, 6);

			// ok, now generate the array of routes
			_startLen = startP.size();
			_endLen = endP.size();

			// ok, create the array
			myRoutes = new Route[_startLen][_endLen];

			// now populate it
			int ctr = 1;
			for (int i = 0; i < _startLen; i++)
			{
				for (int j = 0; j < _endLen; j++)
				{
					String thisName = _name + "_" + ctr++;
					Route newRoute = new Route(thisName, startP.get(i), getFirst()
							.getTime(), endP.get(j), getLast().getTime());
					
					// tell the route to decimate itself
					newRoute.generateSegments(states);
					
					// and store the route
					myRoutes[i][j] = newRoute;
				}
			}
		}

		private BoundedState getFirst()
		{
			return _states.get(0);
		}

		private BoundedState getLast()
		{
			return _states.get(_states.size() - 1);
		}

		/**
		 * use a simple speed/time decision to decide if it's possible to navigate a
		 * route
		 */
		public void decideAchievableRoutes()
		{
			RouteOperator spd = new RouteOperator()
			{

				@Override
				public void process(Route theRoute)
				{
					// do we already know this isn't possible?
					if (!theRoute.isPossible())
						return;

					// bugger, we'll have to get on with our hard sums then

					// sort out the origin.
					State startState = theRoute.getStates().get(0);
					Coordinate startCoord = startState.getLocation().getCoordinate();
					
					// also sort out the end state
					State endState = theRoute.getStates().get(theRoute.getStates().size()-1);
					Point endPt = endState.getLocation();
					
					// remeber the start time
					long tZero = startState.getTime().getTime();

					// how long is the total run?
					long elapsed = theRoute.getElapsedTime();

					// loop through our states
					Iterator<BoundedState> iter = _states.iterator();
					while (iter.hasNext())
					{
						BoundedState thisB = iter.next();

						LocationRange thisL = thisB.getLocation();
						if (thisL != null)
						{
							// ok, what's the time difference
							long tDelta = (thisB.getTime().getTime() - tZero)/1000;

							// is this our first state
							if (tDelta > 0)
							{
								double scale = (1d*elapsed) / tDelta;
								
								// ok, project the shape forwards
								AffineTransformation st = AffineTransformation.scaleInstance(
										scale, scale, startCoord.x, startCoord.y);
								
								// ok, apply the transform to the location
								Geometry newGeom = st.transform(thisL.getGeometry());
								
								// see if the end point is in the new geometry
								if(newGeom.disjoint(endPt))
								{
									theRoute.setImpossible();
									break;
								}
								
							}

						}
					}
				}

			};

			applyToRoutes(spd);
		}
		
		private void tmpOutputGeometry(String name, Geometry geom)
		{
			if(geom instanceof Polygon)
			{
				Polygon poly = (Polygon) geom;
				Coordinate[] pts = poly.getCoordinates();
			}
			else if(geom instanceof Point)
			{
				Point pt = (Point) geom;
			}
		}

		/**
		 * apply the operator to all my routes
		 * 
		 * @param operator
		 */
		private void applyToRoutes(RouteOperator operator)
		{
			for (int i = 0; i < _startLen; i++)
			{
				for (int j = 0; j < _endLen; j++)
				{
					operator.process(myRoutes[i][j]);
				}
			}
		}

		/**
		 * find out how many achievable routes there are through the area
		 * 
		 * @return how many
		 */
		public int getNumAchievable()
		{

			CountPossible isPossible = new CountPossible();
			applyToRoutes(isPossible);

			return isPossible.getCount();
		}

		/**
		 * interface for a generic operation that acts on a route
		 * 
		 * @author Ian
		 * 
		 */
		public static interface RouteOperator
		{
			/**
			 * apply the processing to this route
			 * 
			 * @param theRoute
			 */
			public void process(Route theRoute);
		}

		public static class CountPossible implements RouteOperator
		{
			int res = 0;

			@Override
			public void process(Route theRoute)
			{
				if (theRoute.isPossible())
					res++;
			}

			public int getCount()
			{
				return res;
			}
		}

	}

}
