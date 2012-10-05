package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
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
		sc.setMinRange(300);
		sc.setMaxRange(5000);

		ProblemSpace ps = new ProblemSpace();
		BoundedState newState = new BoundedState(new Date(2012, 3, 3));
		Coordinate[] coordinates = new Coordinate[]
		{ new Coordinate(0, 0), new Coordinate(10, 0), new Coordinate(10, 0), new Coordinate(0, 10),new Coordinate(0, 0) };
		LinearRing g1 = new GeometryFactory().createLinearRing(coordinates);
		Polygon poly = new GeometryFactory().createPolygon(g1, null);
		newState.constrainTo(new LocationRange(poly));
		ps.add(newState);

		// should be empty
		assertEquals("no states yet", 1, ps.size());

		sc.actUpon(ps);

		// should be two states
		assertEquals("still have one state", 1, ps.size());
		final BoundedState theState = ps.states().next();
		assertEquals("correct limits", 12d, theState.getSpeed().getMin());
		assertEquals("correct limits", 22d, theState.getSpeed().getMax());
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
		Iterator<BoundedState> iter = ps.states();
		final BoundedState first = iter.next();
		final BoundedState second = iter.next();
		assertEquals("correct limits", 12d, first.getSpeed().getMin());
		assertEquals("correct limits", 22d, first.getSpeed().getMax());
		assertEquals("correct limits", 12d, second.getSpeed().getMin());
		assertEquals("correct limits", 22d, second.getSpeed().getMax());
	}

	@SuppressWarnings("deprecation")
	public void testWithDates() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(12d);
		sc.setMaxSpeed(22d);
		sc.setStartDate(new Date(2012, 4, 12));
		sc.setFinishDate(new Date(2012, 4, 16));

		ProblemSpace ps = new ProblemSpace();

		// should be empty
		assertEquals("no states yet", 0, ps.size());

		sc.actUpon(ps);

		// should be two states
		assertEquals("now have two states", 2, ps.size());

		Iterator<BoundedState> iter = ps.states();
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

		ProblemSpace ps = new ProblemSpace();
		BoundedState newState = new BoundedState(new Date(2012, 4, 14));
		newState.constrainTo(new SpeedRange(15d, 25d));
		ps.add(newState);

		// should be empty
		assertEquals("just one state", 1, ps.size());

		Iterator<BoundedState> iter = ps.states();
		BoundedState first = iter.next();
		assertEquals("correct limits before new constraints", 15d, first.getSpeed()
				.getMin());
		assertEquals("correct limits before new constraints", 25d, first.getSpeed()
				.getMax());

		sc.actUpon(ps);

		// should be three states
		assertEquals("now have three states", 3, ps.size());

		iter = ps.states();
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

		ProblemSpace ps = new ProblemSpace();
		ps.add(new BoundedState(new Date(2012, 4, 16)));

		// should be empty
		assertEquals("just one state", 1, ps.size());

		sc.actUpon(ps);

		// should be two states
		assertEquals("now have two states", 2, ps.size());

		Iterator<BoundedState> iter = ps.states();
		BoundedState first = iter.next();
		BoundedState second = iter.next();

		assertEquals("correct limits", 12d, first.getSpeed().getMin());
		assertEquals("correct limits", 22d, first.getSpeed().getMax());
		assertEquals("correct limits", 12d, second.getSpeed().getMin());
		assertEquals("correct limits", 22d, second.getSpeed().getMax());

	}

}