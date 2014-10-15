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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.ObjectUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

@SuppressWarnings("deprecation")
public class BearingMeasurementContributionTest extends ModelTestBase
{
	public static final String THE_PATH = "tests/com/planetmayo/debrief/satc/model/contributions/data/bearing_measurement_data.txt";
	public static final String THE_SHORT_PATH = "tests/com/planetmayo/debrief/satc/model/contributions/data/short_bearing_measurement_data.txt";

	private BearingMeasurementContribution bmc;

	@Test
	public void testLoadFrom() throws Exception
	{
		bmc = new BearingMeasurementContribution();
		assertFalse("should be empty", bmc.hasData());

		bmc.loadFrom(TestSupport.getShortData());
		assertTrue("should not be empty", bmc.hasData());
		assertEquals("correct start date", new Date(110, 00, 12, 12, 13, 29),
				bmc.getStartDate());
		assertEquals("correct finish date", new Date(110, 00, 12, 12, 24, 29),
				bmc.getFinishDate());
		assertEquals(Math.toRadians(3d), bmc.getBearingError(), EPS);
	}

	@Test
	public void testActUpon() throws Exception
	{
		testLoadFrom();
		ProblemSpace ps = new ProblemSpace();

		bmc.actUpon(ps);
		for (BoundedState state : ps.states())
		{
			Geometry geo = state.getLocation().getGeometry();
			Coordinate[] coords = geo.getCoordinates();
			for (int i = 0; i <= 4; i++)
			{
				Coordinate coordinate = coords[i];
				assertNotNull("we should have a coordinate", coordinate);
			}
		}
		assertEquals("read in all lines", 5, ps.size());
	}

	@Test
	public void testErrorCalc()
	{
		bmc = new BearingMeasurementContribution();
		bmc.loadFrom(TestSupport.getLongData());

		double score;

		// ok, create a well-performing route to use
		Point startP = GeoSupport.getFactory().createPoint(new Coordinate(-30.005, 0.010));
		Date startT = ObjectUtils.safeParseDate(new SimpleDateFormat("yyMMdd HHmmss"),
				"100112 121300");
		Point endP = GeoSupport.getFactory().createPoint(new Coordinate(-30.075, 0.010));
		Date endT = ObjectUtils.safeParseDate(new SimpleDateFormat("yyMMdd HHmmss"),
				"100112 122836");
		StraightRoute goodRoute = new StraightRoute("rName", startP, startT, endP,
				endT);

		// and a performing route to use
		startP = GeoSupport.getFactory().createPoint(new Coordinate(-30.003, -0.05));
		startT = ObjectUtils.safeParseDate(new SimpleDateFormat("yyMMdd HHmmss"),
				"100112 121300");
		endP = GeoSupport.getFactory().createPoint(new Coordinate(-30.075, 0.010));
		endT = ObjectUtils.safeParseDate(new SimpleDateFormat("yyMMdd HHmmss"), "100112 122836");
		StraightRoute badRoute = new StraightRoute("rName", startP, startT, endP,
				endT);

		// we'll need some states, so the route can correctly segment itself
		ArrayList<BoundedState> states = new ArrayList<BoundedState>();

		// inject some early states for which there isn't a measurement
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 121300")));
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 121301")));

		// now for our real states
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 121459")));
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 121629")));
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 121814")));

		// inject a state for which there isn't a measurement
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 122300")));

		// and carry on
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 122329")));
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 122829")));

		// inject some late states for which there isn't a measurement
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 122832")));
		states.add(new BoundedState(parseDate("yyMMdd HHmmss", "100112 122836")));

		// test when we shouldn't run
		bmc.setActive(false);
		bmc.setWeight(0);

		score = bmc.calculateErrorScoreFor(null);
		assertEquals("should not be able to calc", 0, score, 0.0001);

		// ok, make it active
		bmc.setActive(true);

		score = bmc.calculateErrorScoreFor(null);
		assertEquals("still should not be able to calc", 0, score, 0.0001);

		// ok, let it start
		bmc.setWeight(1);
		score = bmc.calculateErrorScoreFor(null);
		assertEquals("still should not be able to calc", 0, score, 0.0001);

		score = bmc.calculateErrorScoreFor(goodRoute);
		assertEquals("still should not be able to calc", 0, score, 0.0001);

		// ok, flesh out the routes
		goodRoute.generateSegments(states);
		badRoute.generateSegments(states);

		score = bmc.calculateErrorScoreFor(goodRoute);
// NOTE: BMC no longer produces an error score.  The bearing isn't a forecast, it's an absolute measurement.
//		assertTrue("still should not be able to calc", 0 != score);
//		System.out.println("good score:" + score);
//		
//		score = bmc.calculateErrorScoreFor(badRoute);
//		assertTrue("still should not be able to calc", 0 != score);
//		System.out.println("bad score:" + score);
	}
}