/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.legs;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.DateUtils;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

public class StraightLegTests extends ModelTestBase
{
	private StraightLeg leg;

	@Before
	public void prepareBoundsManager() throws Exception
	{
		WKTReader wkt = new WKTReader();

		List<BoundedState> states = new ArrayList<BoundedState>();
		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 0, 0)));
		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 10, 0)));
		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 20, 0)));
		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 30, 0)));

		// apply location bounds
		states.get(0).constrainTo(
				new LocationRange(wkt.read("POLYGON ((0 3, 2 4, 4 4, 2 3, 0 3))"))
		);
		states.get(1).constrainTo(
				new LocationRange(wkt.read("POLYGON ((2.63 2.56, 3.5 3.16, 4.11 3.42, 3.33 2.3, 2.63 2.56))"))
		);
		states.get(2).constrainTo(
				new LocationRange(wkt.read("POLYGON ((3.32 1.99,3.93 2.71,4.64 2.87,3.81 1.78, 3.32 1.99))"))
		);
		states.get(3).constrainTo(
				new LocationRange(wkt.read("POLYGON ((5 1, 5.5 2, 6 2, 6 1, 5 1))"))
		);
		// apply speed bounds
		SpeedRange sr = new SpeedRange(3, 29);
		states.get(0).constrainTo(sr);
		states.get(3).constrainTo(sr);
		
		leg = new StraightLeg("1");
		leg.add(states);
	}

	
	@Test
	public void testGetType() 
	{
		assertEquals(LegType.STRAIGHT, leg.getType());		
	}
	
	@Test
	public void testGeneratePoints()
	{
		assertEquals(DateUtils.date(2012, 5, 5, 12, 0, 0), leg.getFirst().getTime());
		assertEquals(DateUtils.date(2012, 5, 5, 12, 30, 0), leg.getLast().getTime());		
		assertNull(leg.getStartPoints());
		assertNull(leg.getEndPoints());
		
		leg.generatePoints(Precision.LOW.getNumPoints());
		assertNotNull(leg.getStartPoints());
		assertNotNull(leg.getEndPoints());
		
		Geometry startPolygon = leg.getFirst().getLocation().getGeometry();
		Geometry endPolygon = leg.getLast().getLocation().getGeometry();
		for (Point point : leg.getStartPoints()) 
		{
			assertTrue(point.coveredBy(startPolygon));
		}
		for (Point point : leg.getEndPoints()) 
		{
			assertTrue(point.coveredBy(endPolygon));
		}		
	}
	
	@Test(expected = RuntimeException.class)
	public void testGeneratePointsMaxPoints() 
	{
		leg.generatePoints(Precision.LOW.getNumPoints());
	}
	
	@Test
	public void testDecideAchievableRoute() 
	{
		GeometryFactory factory = new GeometryFactory();
		leg.generatePoints(Precision.LOW.getNumPoints());
		
		StraightRoute route = new StraightRoute("1", factory.createPoint(new Coordinate(0, 3)), leg.getFirst().getTime(), 
				factory.createPoint(new Coordinate(6, 2)), leg.getLast().getTime());
		leg.decideAchievableRoute(route);
		assertFalse(route.isPossible());
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
	public void testAaaaa() throws Exception
	{
		WKTReader wkt = new WKTReader();
		List<BoundedState> states = new ArrayList<BoundedState>();
		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 0, 0)));
		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 30, 0)));		
		states.get(0).constrainTo(
				new LocationRange(wkt.read("POLYGON ((0 0.3, 0.2 0.4, 0.4 0.4, 0.2 0.3, 0 0.3))"))
		);
		states.get(1).constrainTo(
				new LocationRange(wkt.read("POLYGON ((0 0.3, 0.2 0.4, 0.4 0.4, 0.2 0.3, 0 0.3))"))
		);
		leg = new StraightLeg("1");
		leg.add(states);
		leg.generatePoints(Precision.HIGH.getNumPoints());
		List<Point> highPoints = leg.getStartPoints();
		leg.generatePoints(Precision.MEDIUM.getNumPoints());
		List<Point> medPoints = leg.getStartPoints();
		Set<Point> highSet = new HashSet<Point>(highPoints);
		for (Point point : medPoints)
		{
			boolean res = false;
			for (Point highPoint : highPoints)
			{
				if (highPoint.isWithinDistance(point, 0.0001))
				{
					res = true;
				}
			}
			if (! res)
			{
				fail();
			}
		}
	}
}
