/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package ASSET.Models.Sensor.Lookup;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Sensor.Lookup.OpticLookupSensor.*;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Vector;
import ASSET.NetworkParticipant;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Decision.Tactical.PatternSearch_Ladder;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.CoreEnvironment;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

// //////////////////////////////////////////////////////////
// constructor
// //////////////////////////////////////////////////////////

// //////////////////////////////////////////////////////////
// test the optic sensor
// //////////////////////////////////////////////////////////
public final class OpticLookupSensorTest extends ASSET.Util.EditableTesting {
	static public class MyEnvironment extends CoreEnvironment {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		protected int _lightLevel = 1;

		protected int _seaState = 4;

		protected int _atten = EnvironmentType.VERY_CLEAR;

		/**
		 * get the atmospheric attenuation
		 *
		 * @param time     current time
		 * @param location place to get data for
		 * @return one of the atmospheric attenuation factors
		 */
		@Override
		public int getAtmosphericAttentuationFor(final long time, final WorldLocation location) {
			return _atten;
		}

		/**
		 * get the light level at this location
		 *
		 * @param time     the time we're talking about
		 * @param location the location we're talking about
		 * @return the current light level
		 */
		@Override
		public int getLightLevelFor(final long time, final WorldLocation location) {
			return _lightLevel;
		}

		/**
		 * get the sea state
		 *
		 * @param time     current time
		 * @param location place to get data for
		 * @return sea state, from 0 to 10
		 */
		@Override
		public int getSeaStateFor(final long time, final WorldLocation location) {
			return _seaState;
		}

		/**
		 * over-ride the sea state
		 *
		 * @param seaState the new sea-state to use
		 */
		public void setSeaState(final int seaState) {
			this._seaState = seaState;
		}
	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	protected boolean detectedOther = false;

	protected boolean targetLost = false;

	protected int maxClassification = 0;


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return getTestOpticSensor();
	}

	@Test
	public void testDefaultValues() {
		// set the sensor
		final OpticLookupSensor sensor = getTestOpticSensor();

		// and set the environment
		final OpticEnvironment env = sensor.getDefaultLookups();

		// now check that a real value gets return
		Double res = env._sea_states.find(1, Category.Type.CARRIER);
		assertNotNull(res, "didn't find result which we should have done");
		assertEquals(1d, res.doubleValue(), 0d, "didn't find correct lookup value");

		// and for the vis table
		res = env._visibility.find(Category.Type.CARRIER);
		assertNotNull(res, "didn't find result which we should have done");
		assertEquals(0.2d, res.doubleValue(), 0d, "didn't find correct lookup value");

		// now move on to datums which we don't have
		res = env._sea_states.find(1, Category.Type.DESTROYER);
		assertNotNull(res, "didn't find result which we should have done");
		assertEquals(env._sea_states.getUnknownResult(), res, "didn't find correct lookup value");

		// and for the vis table
		res = env._visibility.find(Category.Type.DESTROYER);
		assertNotNull(res, "didn't find result which we should have done");
		assertEquals(env._visibility.getUnknownResult(), res, "didn't find correct lookup value");

		// now try to remove the default value, and check that exception gets
		// thrown
		env._visibility.setUnknownResult(null);
		env._sea_states.setUnknownResult(null);

		boolean visThrown = false;

		try {

			res = env._sea_states.find(1, Category.Type.DESTROYER);
			assertNull(res, "didn't find result which we should have done");

			// and for the vis table
			res = env._visibility.find(Category.Type.DESTROYER);
			assertNull(res, "didn't find result which we should have done");
		} catch (final RuntimeException re) {
			visThrown = true;
		}

		res = env._sea_states.find(1, Category.Type.DESTROYER);

		try {
			res = env._visibility.find(Category.Type.DESTROYER);
			fail("should have thrown exception");
		} catch (final RuntimeException re) {
			visThrown = true;
		}

		assertTrue(visThrown, "should have fired missing item for missing type in visibility test");

	}

	@Test
	public final void testFirst() {
		final OpticLookupSensor ts = getTestOpticSensor();
		final Status statA = new Status(12, 0);
		statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final Helo alpha = new Helo(12);
		alpha.setName("Alpha");
		alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		final WorldLocation originA = SupportTesting.createLocation(0, 0);
		statA.setLocation(originA);
		statA.getLocation().setDepth(-900);
		alpha.setStatus(statA);
		final Surface bravo = new Surface(11);
		final Status statB = new Status(12, 0);
		statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		bravo.setName("Bravo");
		bravo.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
		statB.setLocation(originB);
		statB.getLocation().setDepth(-20);
		bravo.setStatus(statB);

		alpha.addSensor(ts);
		final CoreScenario scenario = new CoreScenario();
		scenario.addParticipant(alpha.getId(), alpha);
		scenario.addParticipant(bravo.getId(), bravo);

		scenario.setScenarioStepTime(1000);

		// create our dummy environment object
		final MyEnvironment env = new MyEnvironment();

		// ok, start off with us miles apart - and check we can't see each other.
		long time = 1000;
		final DetectionList res = new DetectionList();
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(0, res.size(), "null detections returned");

		// ok. move the targets a little closer (but still out of range)
		statB.setLocation(SupportTesting.createLocation(800, 700));
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(0, res.size(), "null detections returned");

		// and closer still (to gain contact)
		statB.setLocation(SupportTesting.createLocation(500, 500));
		time += 1000;
		ts.detects(env, res, alpha, scenario, time);
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(), "check detected state");

		// check that the parameters have stayed the same
		statB.setLocation(SupportTesting.createLocation(500, 500));
		time += 1000;
		ts.detects(env, res, alpha, scenario, time);
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(), "check detected state");

		// stay close, move forward enough elapsed time so that we can move to
		// classified
		time += 21000;
		ts.detects(env, res, alpha, scenario, time);
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(), "check detected state");

		// change one of the parameters
		env._atten = EnvironmentType.FOG;

		// check that the parameters have changed
		time += 1000;
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(0, res.size(), "null detections returned");
	}

	@Test
	public void testRead() {
		String fileName = "lookup_test_scenario.xml";
		fileName = "../org.mwc.asset.legacy/src/ASSET/Models/Sensor/Lookup/" + fileName;

		final CoreScenario scen = new CoreScenario();
		try {
			final FileInputStream is = new FileInputStream(fileName);
			final InputStream bi = new BufferedInputStream(is);
			ASSET.Util.XML.ASSETReaderWriter.importThis(scen, fileName, bi);
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// ok, try to check that the data got loaded.
		final Integer[] participants = scen.getListOfParticipants();

		final NetworkParticipant partA = scen.getThisParticipant(participants[0].intValue());
		final NetworkParticipant partB = scen.getThisParticipant(participants[1].intValue());
		NetworkParticipant part;
		if (partA instanceof Helo)
			part = partA;
		else
			part = partB;

		assertTrue(part instanceof Helo, "we've loaded and found the helo");
		final Helo helo = (Helo) part;
		final SensorList theSensors = helo.getSensorFit();
		assertEquals(3, theSensors.getNumSensors(), "has sensors loaded");
		final SensorType firstSensor = theSensors.getSensor(444);
		assertNotNull(firstSensor, "found first sensor");
		assertTrue(firstSensor instanceof OpticLookupSensor);
		final OpticLookupSensor opticS = (OpticLookupSensor) firstSensor;

		// check the values
		assertEquals(0.05, opticS.VDR, 0.0, "VDR correct");
		assertEquals("Eyesight", opticS.getName(), "name correct");
		assertEquals(1.05, opticS.MRF, 0.0, "MRF correct");
		assertEquals(0.8, opticS.CRF, 0.0, "CRF correct");
		assertEquals(0.2, opticS.IRF, 0.0, "IRF correct");

		assertEquals(10000, opticS.getTimeBetweenDetectionOpportunities(), 0, "tbdo correct");
		assertEquals(20000, opticS.CTP.getMillis(), 0, "ctp correct");
		assertEquals(30000, opticS.ITP.getMillis(), 0, "itp correct");

		// also stick in our tests for the ladder search
		final Waterfall wat = (Waterfall) helo.getDecisionModel();
		final Sequence seq = (Sequence) wat.getModels().elementAt(1);
		assertEquals(3, seq.getModels().size(), "all loaded");
		assertTrue(seq.getModels().elementAt(2) instanceof PatternSearch_Ladder, "ladder loaded");

		helo.addParticipantDetectedListener(new ParticipantDetectedListener() {
			@Override
			public void newDetections(final DetectionList detections) {
				if (detections.size() > 0) {
					detectedOther = true;

					final DetectionEvent det = detections.getDetection(0);

					maxClassification = Math.max(maxClassification, det.getDetectionState());
				} else {
					if (detectedOther)
						targetLost = true;
				}
			}

			@Override
			public void restart(final ScenarioType scenario) {
			}
		});

		// DebriefReplayObserver dro = new DebriefReplayObserver("c:/temp",
		// "optic_lookup.rep", true, "test observer", true);
		// dro.setup(scen);

		// hey, let's run through it a little
		for (int i = 0; i < 1800; i++) {
			scen.step();
		}

		// dro.tearDown(scen);

		assertTrue(detectedOther, "yes, we made a detection");
		assertTrue(targetLost, "yes, we also lost contact");
		assertEquals(2, maxClassification, 0, "managed to identify");

	}
}
