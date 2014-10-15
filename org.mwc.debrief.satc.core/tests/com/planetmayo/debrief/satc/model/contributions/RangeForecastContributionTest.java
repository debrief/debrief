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

import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class RangeForecastContributionTest extends TestCase
{
	@SuppressWarnings("deprecation")
	public void testPolygons() throws IncompatibleStateException
	{
		RangeForecastContribution sc = new RangeForecastContribution();
		sc.setMinRange(300.0);
		sc.setMaxRange(5000.0);

		ProblemSpace ps = new ProblemSpace();
		BoundedState newState = new BoundedState(new Date(2012, 3, 3));
		Coordinate[] coordinates = new Coordinate[]
		{ new Coordinate(0, 0), new Coordinate(10, 0), new Coordinate(10, 10), new Coordinate(0, 10),new Coordinate(0, 0) };
		LinearRing g1 = new GeometryFactory().createLinearRing(coordinates);
		Polygon poly = new GeometryFactory().createPolygon(g1, null);
		newState.constrainTo(new LocationRange(poly));
		ps.add(newState);

		// should be empty
		assertEquals("no states yet", 1, ps.size());

		sc.actUpon(ps);

		// should be two states
		assertEquals("still have one state", 1, ps.size());
		final BoundedState theState = ps.states().iterator().next();
		assertEquals("correct limits", 5, theState.getLocation().getGeometry().getNumPoints());
	}

	@SuppressWarnings("deprecation")
	public void testNullDateDoubleStates() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(12d);
		sc.setMaxSpeed(22d);

		ProblemSpace ps = new ProblemSpace();
		ps.add(new BoundedState(new Date(2012, 3, 3)));
		ps.add(new BoundedState(new Date(2012, 3, 12)));

		// should be empty
		assertEquals("no states yet", 2, ps.size());

		sc.actUpon(ps);

		// should be two states
		assertEquals("still have two state", 2, ps.size());
		Iterator<BoundedState> iter = ps.states().iterator();
		final BoundedState first = iter.next();
		final BoundedState second = iter.next();
		assertEquals("correct limits", 12d, first.getSpeed().getMin());
		assertEquals("correct limits", 22d, first.getSpeed().getMax());
		assertEquals("correct limits", 12d, second.getSpeed().getMin());
		assertEquals("correct limits", 22d, second.getSpeed().getMax());
	}

	/*@SuppressWarnings("deprecation")
	public void testWithDates() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(12d);
		sc.setMaxSpeed(22d);
		sc.setStartDate(new Date(2012, 4, 12));
		sc.setFinishDate(new Date(2012, 4, 16));
		sc.setActive(false);
		
		BoundsManager bm = new BoundsManager();
		bm.addContribution(sc);
		
		// should be empty (not run yet)
		assertEquals("no states yet", 0, bm.getSpace().size());

		bm.run();

		// should still have zero states (not activated yet)
		assertEquals("now have two states", 0, bm.getSpace().size());
		
		// aah, try activating the contribution
		sc.setActive(true);

		// and run again
		bm.run();
		
		// should be two states
		assertEquals("now have two states", 2, bm.getSpace().size());
		
		Iterator<BoundedState> iter = bm.getSpace().states().iterator();
		BoundedState first = iter.next();
		BoundedState second = iter.next();

		assertEquals("correct limits", 12d, first.getSpeed().getMin());
		assertEquals("correct limits", 22d, first.getSpeed().getMax());
		assertEquals("correct limits", 12d, second.getSpeed().getMin());
		assertEquals("correct limits", 22d, second.getSpeed().getMax());

	}

	@SuppressWarnings("deprecation")
	public void testWithDateNotAlreadyPresent() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(12d);
		sc.setMaxSpeed(22d);

		sc.setStartDate(new Date(2012, 4, 12));
		sc.setFinishDate(new Date(2012, 4, 16));

		BoundsManager bm = new BoundsManager();
		bm.addContribution(sc);

		ProblemSpace ps = bm.getSpace();
		BoundedState newState = new BoundedState(new Date(2012, 4, 14));
		newState.constrainTo(new SpeedRange(15d, 25d));
		ps.add(newState);

		// should be empty
		assertEquals("just one state", 1, ps.size());

		Iterator<BoundedState> iter = ps.states().iterator();
		BoundedState first = iter.next();
		assertEquals("correct limits before new constraints", 15d, first.getSpeed()
				.getMin());
		assertEquals("correct limits before new constraints", 25d, first.getSpeed()
				.getMax());

		bm.run();
//		sc.actUpon(ps);

		// should be three states
		assertEquals("now have three states", 3, ps.size());

		iter = ps.states().iterator();
		first = iter.next();
		BoundedState second = iter.next();
		BoundedState third = iter.next();

		assertEquals("correct limits", 12d, first.getSpeed().getMin());
		assertEquals("correct limits", 22d, first.getSpeed().getMax());
		assertEquals("correct limits (using existing constraint)", 15d, second
				.getSpeed().getMin());
		assertEquals("correct limits", 22d, second.getSpeed().getMax());
		assertEquals("correct limits", 12d, third.getSpeed().getMin());
		assertEquals("correct limits", 22d, third.getSpeed().getMax());

	}

	@SuppressWarnings("deprecation")
	public void testWithDateAlreadyPresent() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(12d);
		sc.setMaxSpeed(22d);

		sc.setStartDate(new Date(2012, 4, 12));
		sc.setFinishDate(new Date(2012, 4, 16));

		BoundsManager bm = new BoundsManager();
		bm.addContribution(sc);
		ProblemSpace ps = bm.getSpace();
		ps.add(new BoundedState(new Date(2012, 4, 16)));

		// should be empty
		assertEquals("just one state", 1, ps.size());

		bm.run();

		// should be two states
		assertEquals("now have two states", 2, ps.size());

		Iterator<BoundedState> iter = ps.states().iterator();
		BoundedState first = iter.next();
		BoundedState second = iter.next();

		assertEquals("correct limits", 12d, first.getSpeed().getMin());
		assertEquals("correct limits", 22d, first.getSpeed().getMax());
		assertEquals("correct limits", 12d, second.getSpeed().getMin());
		assertEquals("correct limits", 22d, second.getSpeed().getMax());

	}*/

}