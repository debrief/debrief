package com.planetmayo.debrief.satc.model.generator;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContributionTest;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.SteppingGenerator.SteppingListener;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.support.SupportServices;

public class TrackGeneratorTest extends TestCase {
	static protected IncompatibleStateException _ise;
	static int _ctr1 = 0;
	static int _ctr2 = 0;
	static int _ctr3 = 0;
	private RuntimeException _re;

	public void testAddOrder() {
		TrackGenerator tg = new TrackGenerator();

		// sort out the listener
		tg.addContributionsListener(new ContributionsChangedListener() {
			public void removed(BaseContribution contribution) {
				_ctr1--;
			}

			@Override
			public void added(BaseContribution contribution) {
				_ctr1++;
			}
		});

		// check the counter is zeroed
		assertEquals("counter should be zero", 0, _ctr1);

		tg.addContribution(new SpeedForecastContribution());
		BearingMeasurementContribution bmc = new BearingMeasurementContribution();
		tg.addContribution(bmc);
		tg.addContribution(new LocationForecastContribution());

		// check the counter is zeroed
		assertEquals("counter should have found some", 3, _ctr1);

		// hmm, but are they in the correct order?
		Iterator<BaseContribution> iter = tg.contributions().iterator();
		BaseContribution c1 = iter.next();
		BaseContribution c2 = iter.next();
		BaseContribution c3 = iter.next();

		assertEquals("measurement comes first",
				BearingMeasurementContribution.class, c1.getClass());
		assertEquals("location comes second",
				LocationForecastContribution.class, c2.getClass());
		assertEquals("speed comes third", SpeedForecastContribution.class,
				c3.getClass());

		// ok, now try to remove one
		tg.removeContribution(bmc);

		// check the counter is zeroed
		assertEquals("counter should have heard about removal", 2, _ctr1);

	}

	public void testRestartOnContribChange() throws IOException {
		// sort out our contributions
		BearingMeasurementContribution bearingM = new BearingMeasurementContribution();
		bearingM.loadFrom(SupportServices.INSTANCE.getIOService()
				.readLinesFrom(BearingMeasurementContributionTest.THE_PATH));

		CourseForecastContribution courseF = new CourseForecastContribution();
		courseF.setMinCourse(24);
		courseF.setMaxCourse(31);

		SpeedForecastContribution speedF = new SpeedForecastContribution();
		speedF.setMinSpeed(21d);
		speedF.setMaxSpeed(14d);

		// and the track generator
		TrackGenerator tg = new TrackGenerator();
		tg.addContribution(speedF);
		tg.addContribution(bearingM);
		tg.addContribution(courseF);

		// check they've all loaded
		assertEquals("have 3 contribs", 3, tg.contributions().size());

		// reset the change counter;
		_ctr1 = 0;
		_ctr2 = 0;
		_ctr3 = 0;

		// listen out for track genny changes
		tg.addSteppingListener(new SteppingListener() {

			@Override
			public void stepped(int thisStep, int totalSteps) {
				_ctr2++;
			}

			@Override
			public void restarted() {
				_ctr1++;
			}

			@Override
			public void complete() {
				_ctr3++;
			}
		});

		// ok, make some changes
		courseF.setMinCourse(12);

		// did we even see it?
		assertEquals("we saw change", 1, _ctr1);

		// chuck in a step
		tg.step();

		// did we even see it?
		assertEquals("we saw step", 1, _ctr2);

		// ok, lets get fancy
		courseF.setMaxCourse(44);
		courseF.setMinCourse(23);

		// did we even see it?
		assertEquals("we saw more changes", 3, _ctr1);

		_ctr2 = 0;
		_ctr3 = 0;
		_re = null;

		// try and run it
		tg.restart();
		tg.run();

		// did we even see it?
		assertEquals("we saw steps", 3, _ctr2);
		assertEquals("we saw complete", 1, _ctr3);

		// and a few steps?
		_ctr2 = 0;
		_ctr3 = 0;
		try {
			tg.step();
		} catch (RuntimeException re) {
			_re = re;
		}

		assertNotNull("should have thrown error when we step after end", _re);

		tg.restart();

		// chuck in 3 step s
		tg.step();
		tg.step();
		tg.step();

		// did we even see it?
		assertEquals("we saw steps", 3, _ctr2);
		assertEquals("we saw complete", 1, _ctr3);

	}

	public void testIncompatibleBounds() throws IOException {
		// sort out our contributions
		BearingMeasurementContribution bearingM = new BearingMeasurementContribution();
		bearingM.loadFrom(SupportServices.INSTANCE.getIOService()
				.readLinesFrom(BearingMeasurementContributionTest.THE_PATH));

		CourseForecastContribution courseF = new CourseForecastContribution();
		courseF.setMinCourse(24);
		courseF.setMaxCourse(31);

		SpeedForecastContribution speedF = new SpeedForecastContribution();
		speedF.setMinSpeed(21d);
		speedF.setMaxSpeed(14d);

		// and the track generator
		TrackGenerator tg = new TrackGenerator();
		tg.addContribution(speedF);
		tg.addContribution(bearingM);
		tg.addContribution(courseF);

		// reset the change counter;
		_ctr1 = 0;
		_ctr2 = 0;
		_ctr3 = 0;

		// listen out for track genny changes
		tg.addSteppingListener(new SteppingListener() {

			@Override
			public void stepped(int thisStep, int totalSteps) {

			}

			@Override
			public void restarted() {
				_ctr3++;
			}

			@Override
			public void complete() {
				// TODO Auto-generated method stub
			}
		});
		tg.addBoundedStateListener(new BoundedStatesListener() {

			@Override
			public void statesBounded(Collection<BoundedState> newStates) {
				_ctr1++;
			}

			@Override
			public void incompatibleStatesIdentified(BaseContribution contribution, 
					IncompatibleStateException e) {
				_ise = e;
			}

			@Override
			public void debugStatesBounded(Collection<BoundedState> newStates) {
				_ctr2++;
			}
		});

		// ok, make some changes
		courseF.setMinCourse(12);

		assertEquals("restart got fired", 1, _ctr3);

		// hey, chuck in a step
		tg.run();

		// did we even see it?
		assertEquals("we saw two states bounded events (one for reset)", 2,
				_ctr1);
		assertEquals("we saw debug steps", 3, _ctr2);

		_ctr1 = _ctr2 = _ctr3 = 0;

		// ok, lets get fancy
		courseF.setMaxCourse(44);
		courseF.setMinCourse(23);

		// hey, chuck in a step
		tg.step();

		// did we even see it?
		assertEquals("we saw debug step", 1, _ctr2);

		// try an incompatible change, see what gets chucked!
		assertNull("no exception yet", _ise);

		_ctr1 = _ctr2 = _ctr3 = 0;

		// trigger the trouble
		courseF.setMinCourse(100);

		// hey, chuck in a step
		tg.run();

		// hopefully something got triggered.
		assertEquals(
				"we saw debug steps before incompatible states got thrown", 2,
				_ctr2);
		assertNotNull("caught an exception", _ise);

	}

	public void testListeningB() {
		// TODO: create some contributions, add them to generator, make some
		// changes, check we're listening to the correct events
	}

	public void testRegeneration() {
		// TODO: create some contributions, include some constraints, check that
		// contraint restriction is happening
	}

}
