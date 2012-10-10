package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class SpeedContributionTest extends TestCase
{
	@SuppressWarnings("deprecation")
	public void testNullDateSingleState() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(GeoSupport.MSec2kts(12d));
		sc.setMaxSpeed(GeoSupport.MSec2kts(22d));

		ProblemSpace ps = new ProblemSpace();
		ps.add(new BoundedState(new Date(2012, 3, 3)));

		// should be empty
		assertEquals("no states yet", 1, ps.size());

		sc.actUpon(ps);

		// should be two states
		assertEquals("still have one state", 1, ps.size());
		final BoundedState theState = ps.states().iterator().next();
		assertEquals("correct limits", 12d, theState.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, theState.getSpeed().getMaxMS());
		
		assertEquals("correct description", "12 - 22", theState.getSpeed().getConstraintSummary());
	}

	@SuppressWarnings("deprecation")
	public void testNullDateDoubleStates() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(GeoSupport.MSec2kts(12d));
		sc.setMaxSpeed(GeoSupport.MSec2kts(22d));

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
		assertEquals("correct limits", 12d, first.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, first.getSpeed().getMaxMS());
		assertEquals("correct limits", 12d, second.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, second.getSpeed().getMaxMS());
	}

	@SuppressWarnings("deprecation")
	public void testWithDates() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(GeoSupport.MSec2kts(12d));
		sc.setMaxSpeed(GeoSupport.MSec2kts(22d));
		sc.setStartDate(new Date(2012, 4, 12));
		sc.setFinishDate(new Date(2012, 4, 16));

		ProblemSpace ps = new ProblemSpace();

		// should be empty
		assertEquals("no states yet", 0, ps.size());

		sc.actUpon(ps);

		// should be two states
		assertEquals("now have two states", 2, ps.size());

		Iterator<BoundedState> iter = ps.states().iterator();
		BoundedState first = iter.next();
		BoundedState second = iter.next();

		assertEquals("correct limits", 12d, first.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, first.getSpeed().getMaxMS());
		assertEquals("correct limits", 12d, second.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, second.getSpeed().getMaxMS());

	}
	
	@SuppressWarnings("deprecation")
	public void testWithDateNotAlreadyPresent() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(GeoSupport.MSec2kts(12d));
		sc.setMaxSpeed(GeoSupport.MSec2kts(22d));
		sc.setStartDate(new Date(2012, 4, 12));
		sc.setFinishDate(new Date(2012, 4, 16));

		ProblemSpace ps = new ProblemSpace();
		BoundedState newState = new BoundedState(new Date(2012, 4,14));
		newState.constrainTo(new SpeedRange(15d,25d));
		ps.add(newState);

		// should be empty
		assertEquals("just one state", 1, ps.size());

		Iterator<BoundedState> iter = ps.states().iterator();
		BoundedState first = iter.next();
		assertEquals("correct limits before new constraints", 15d, first.getSpeed().getMinMS());
		assertEquals("correct limits before new constraints", 25d, first.getSpeed().getMaxMS());

		sc.actUpon(ps);

		// should be three states
		assertEquals("now have three states", 3, ps.size());

		iter = ps.states().iterator();
		first = iter.next();
		BoundedState second = iter.next();
		BoundedState third = iter.next();

		assertEquals("correct limits", 12d, first.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, first.getSpeed().getMaxMS());
		assertEquals("correct limits (using existing constraint)", 15d, second.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, second.getSpeed().getMaxMS());
		assertEquals("correct limits", 12d, third.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, third.getSpeed().getMaxMS());

	}
	@SuppressWarnings("deprecation")
	public void testWithDateAlreadyPresent() throws IncompatibleStateException
	{
		SpeedForecastContribution sc = new SpeedForecastContribution();
		sc.setMinSpeed(GeoSupport.MSec2kts(12d));
		sc.setMaxSpeed(GeoSupport.MSec2kts(22d));
		sc.setStartDate(new Date(2012, 4, 12));
		sc.setFinishDate(new Date(2012, 4, 16));

		ProblemSpace ps = new ProblemSpace();
		ps.add(new BoundedState(new Date(2012, 4,16)));

		// should be empty
		assertEquals("just one state", 1, ps.size());

		sc.actUpon(ps);

		// should be two states
		assertEquals("now have two states", 2, ps.size());

		Iterator<BoundedState> iter = ps.states().iterator();
		BoundedState first = iter.next();
		BoundedState second = iter.next();

		assertEquals("correct limits", 12d, first.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, first.getSpeed().getMaxMS());
		assertEquals("correct limits", 12d, second.getSpeed().getMinMS());
		assertEquals("correct limits", 22d, second.getSpeed().getMaxMS());

	}

}