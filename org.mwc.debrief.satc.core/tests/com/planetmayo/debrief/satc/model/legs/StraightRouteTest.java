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
package com.planetmayo.debrief.satc.model.legs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.DateUtils;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class StraightRouteTest extends ModelTestBase
{
	
	@Test
	public void testRouteSegmentation1()
	{
		Date startD = DateUtils.date(2012, 5, 5, 12, 0, 0);
		Date endD = DateUtils.date(2012, 5, 5, 17, 0, 0);
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		StraightRoute testR = new StraightRoute("1", startP, startD, endP, endD);

		assertNull("no states, yet", testR.getStates());

		// ok, generate some times
		ArrayList<BoundedState> theTimes = new ArrayList<BoundedState>();
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 11, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 14, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 15, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 17, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 18, 0, 0)));

		testR.generateSegments(theTimes);

		ArrayList<State> states = testR.getStates();
		assertNotNull("have some states", states);
		assertEquals("correct num states", 4, states.size());
	}

	@Test
	public void testRouteSegmentation2()
	{
		Date startD = DateUtils.date(2012, 5, 5, 12, 0, 0);
		Date endD = DateUtils.date(2012, 5, 5, 12, 0, 0);
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(1, 0));
		StraightRoute testR = new StraightRoute("1", startP, startD, endP, endD);

		assertNull("no states, yet", testR.getStates());

		// ok, generate some times
		ArrayList<BoundedState> theTimes = new ArrayList<BoundedState>();
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 11, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 14, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 15, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 17, 0, 0)));
		theTimes.add(new BoundedState(DateUtils.date(2012, 5, 5, 18, 0, 0)));

		testR.generateSegments(theTimes);

		ArrayList<State> states = testR.getStates();
		assertNotNull("have some states", states);
		assertEquals("correct num states", 1, states.size());
	}
	
		
	@Test
	public void testRouteCourseAndSpeed()
	{
		Date startD = DateUtils.date(2012, 5, 5, 12, 0, 0);
		Date endD = DateUtils.date(2012, 5, 5, 17, 0, 0);
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(0, 0));
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(0, 1));
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

		assertEquals("correct course", Math.toRadians(135), testR.getCourse(), EPS);
		assertEquals("correct speed", GeoSupport.kts2MSec(16.97), testR.getSpeed(),
				0.01);

		startP = GeoSupport.getFactory().createPoint(new Coordinate(1, -1));
		endP = GeoSupport.getFactory().createPoint(new Coordinate(1, -1));
		testR = new StraightRoute("1", startP, startD, endP, endD);

		assertEquals("correct course", Math.toRadians(0), testR.getCourse(), EPS);
		assertEquals("correct speed", 0, testR.getSpeed(), 0.01);
	}
}
