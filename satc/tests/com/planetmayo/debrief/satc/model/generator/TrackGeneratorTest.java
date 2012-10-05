package com.planetmayo.debrief.satc.model.generator;

import java.io.File;
import java.util.Iterator;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContributionTest;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;

public class TrackGeneratorTest extends TestCase
{
	static protected IncompatibleStateException _e;
	static int _ctr = 0;
	
	public void testAddOrder()
	{
		TrackGenerator tg = new TrackGenerator();
		
		// sort out the listener
		tg.addContributionsListener(new ContributionsChangedListener()
		{
			public void removed(BaseContribution contribution)
			{
				_ctr--;
			}
			
			@Override
			public void added(BaseContribution contribution)
			{
				_ctr++;
			}
		});
		
		// check the counter is zeroed
		assertEquals("counter should be zero", 0, _ctr);

		tg.addContribution(new SpeedForecastContribution());
		BearingMeasurementContribution bmc = new BearingMeasurementContribution();
		tg.addContribution(bmc);
		tg.addContribution(new LocationForecastContribution());

		// check the counter is zeroed
		assertEquals("counter should have found some", 3, _ctr);
		
		// hmm, but are they in the correct order?
		Iterator<BaseContribution> iter = tg.contributions();
		BaseContribution c1 = iter.next();
		BaseContribution c2 = iter.next();
		BaseContribution c3 = iter.next();
		
		assertEquals("measurement comes first", BearingMeasurementContribution.class, c1.getClass());
		assertEquals("location comes second", LocationForecastContribution.class, c2.getClass());
		assertEquals("speed comes third", SpeedForecastContribution.class, c3.getClass());

		// ok, now try to remove one
		tg.removeContribution(bmc);
		
		// check the counter is zeroed
		assertEquals("counter should have heard about removal", 2, _ctr);
		
		

		
	}
	
	public void testListeningA()
	{
		// sort out our contributions
		BearingMeasurementContribution bearingM = new BearingMeasurementContribution();
		bearingM.loadFrom(new File(BearingMeasurementContributionTest.THE_PATH));
		
		CourseForecastContribution courseF = new CourseForecastContribution();
		courseF.setMinCourse(24);
		courseF.setMaxCourse(31);
		
		SpeedForecastContribution speedF = new SpeedForecastContribution();
		speedF.setMinSpeed(21);
		speedF.setMaxSpeed(14);

		// and the track generator
		TrackGenerator tg = new TrackGenerator();
		tg.addContribution(speedF);
		tg.addContribution(bearingM);
		tg.addContribution(courseF);
		
		// reset the change counter;
		ctr = 0;
		
		// listen out for track genny changes
		tg.addBoundedStateListener(new BoundedStatesListener()
		{
			
			@Override
			public void statesBounded(Iterator<BoundedState> newStates)
			{
				ctr++;
			}

			@Override
			public void incompatibleStatesIdentified(IncompatibleStateException e)
			{
				_e = e;
			}
		});
			
		// ok, make some changes
		courseF.setMinCourse(12);
		
		// did we even see it?
		assertEquals("we saw change",1, ctr);
		
		// ok, lets get fancy
		courseF.setMaxCourse(44);
		courseF.setMinCourse(23);
		
		// did we even see it?
		assertEquals("we saw more changes",3, ctr);
		
		// try an incompatible change, see what gets chucked!
		assertNull("no exception yet", _e);
		
		// trigger the trouble
		courseF.setMinCourse(100);
		
		assertNotNull("caught an exception", _e);
		
	}
	
	static int ctr = 0;
	
	public void testListeningB()
	{
		// TODO: create some contributions, add them to generator, make some changes, check we're listening to the correct events
	}
	
	public void testRegeneration()
	{
		// TODO: create some contributions, include some constraints, check that contraint restriction is happening
	}
	
}
