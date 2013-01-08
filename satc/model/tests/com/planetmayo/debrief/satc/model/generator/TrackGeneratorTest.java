package com.planetmayo.debrief.satc.model.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class TrackGeneratorTest extends ModelTestBase {
	static protected IncompatibleStateException _ise;
	static int _ctr1 = 0;
	static int _ctr2 = 0;
	static int _ctr3 = 0;
	private RuntimeException _re;

	@Test
	public void testAddOrder() {
		BoundsManager tg = new BoundsManager();

		// sort out the listener
		tg.addContributionsListener(new IContributionsChangedListener() {
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
		Iterator<BaseContribution> iter = tg.getContributions().iterator();
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

	@Test
	public void testRestartOnContribChange() throws IOException {
		// sort out our contributions
		BearingMeasurementContribution bearingM = new BearingMeasurementContribution();
		bearingM.loadFrom(TestSupport.getLongData());

		CourseForecastContribution courseF = new CourseForecastContribution();
		courseF.setMinCourse(Math.toRadians(24));
		courseF.setMaxCourse(Math.toRadians(31));

		SpeedForecastContribution speedF = new SpeedForecastContribution();
		speedF.setMinSpeed(GeoSupport.kts2MSec(21d));
		speedF.setMaxSpeed(GeoSupport.kts2MSec(14d));

		// and the track generator
		BoundsManager tg = new BoundsManager();
		tg.addContribution(speedF);
		tg.addContribution(bearingM);
		tg.addContribution(courseF);

		// check they've all loaded
		assertEquals("have 3 contribs", 3, tg.getContributions().size());

		// reset the change counter;
		_ctr1 = 0;
		_ctr2 = 0;
		_ctr3 = 0;

		// listen out for track genny changes
		tg.addSteppingListener(new ISteppingListener() {

			@Override
			public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps) {
				_ctr2++;
			}

			@Override
			public void restarted(IBoundsManager boundsManager) {
				_ctr1++;
			}

			@Override
			public void complete(IBoundsManager boundsManager) {
				_ctr3++;
			}

			@Override
			public void error(IBoundsManager boundsManager,
					IncompatibleStateException ex)
			{
				_ise = ex;
			}
		});

		// ok, make some changes
		courseF.setMinCourse(Math.toRadians(12));

		// did we even see it?
		assertEquals("we saw change", 1, _ctr1);

		// chuck in a step
		tg.step();

		// did we even see it?
		assertEquals("we saw step", 1, _ctr2);

		// ok, lets get fancy
		courseF.setMaxCourse(Math.toRadians(44));
		courseF.setMinCourse(Math.toRadians(23));

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

	@Test
	public void testIncompatibleBounds() throws IOException {
		// sort out our contributions
		BearingMeasurementContribution bearingM = new BearingMeasurementContribution();
		bearingM.loadFrom(TestSupport.getLongData());

		CourseForecastContribution courseF = new CourseForecastContribution();
		courseF.setMinCourse(Math.toRadians(24));
		courseF.setMaxCourse(Math.toRadians(31));

		SpeedForecastContribution speedF = new SpeedForecastContribution();
		speedF.setMinSpeed(GeoSupport.kts2MSec(21d));
		speedF.setMaxSpeed(GeoSupport.kts2MSec(14d));

		// and the track generator
		BoundsManager tg = new BoundsManager();
		tg.addContribution(speedF);
		tg.addContribution(bearingM);
		tg.addContribution(courseF);

		// reset the change counter;
		_ctr1 = 0;
		_ctr2 = 0;
		_ctr3 = 0;

		// listen out for track genny changes
		tg.addSteppingListener(new ISteppingListener() {

			@Override
			public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps) {
				_ctr2++;
			}

			@Override
			public void restarted(IBoundsManager boundsManager) {
				_ctr3++;
			}

			@Override
			public void complete(IBoundsManager boundsManager) {
				_ctr1++;
			}

			@Override
			public void error(IBoundsManager boundsManager,
					IncompatibleStateException ex)
			{
				_ise = ex;
			}
		});
		// ok, make some changes
		courseF.setMinCourse(Math.toRadians(12));

		assertEquals("restart got fired", 1, _ctr3);

		// hey, chuck in a step
		tg.run();

		// did we even see it?
		assertEquals("it's completed succesfully", 1,	_ctr1);
		assertEquals("3 steps", 3, _ctr2);

		_ctr1 = _ctr2 = _ctr3 = 0;

		// ok, lets get fancy
		courseF.setMaxCourse(Math.toRadians(44));
		courseF.setMinCourse(Math.toRadians(23));

		// hey, chuck in a step
		tg.step();

		// did we even see it?
		assertEquals("we saw debug step", 1, _ctr2);

		// try an incompatible change, see what gets chucked!
		assertNull("no exception yet", _ise);

		_ctr1 = _ctr2 = _ctr3 = 0;

		// trigger the trouble
		courseF.setMinCourse(Math.toRadians(100));

		// hey, chuck in a step
		tg.run();

		// hopefully something got triggered.
		assertEquals(
				"we saw 2 steps before incompatible states got thrown", 2,
				_ctr2);
		assertNotNull("caught an exception", _ise);

	}

	@Test
	public void testListeningB() {
		// TODO: create some contributions, add them to generator, make some
		// changes, check we're listening to the correct events
	}

	@Test
	public void testRegeneration() {
		// TODO: create some contributions, include some constraints, check that
		// contraint restriction is happening
	}

}
