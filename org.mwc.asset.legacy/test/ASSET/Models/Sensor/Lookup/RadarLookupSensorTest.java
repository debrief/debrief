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

import static ASSET.Models.Sensor.Lookup.RadarLookupSensor.*;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Vector;
import ASSET.NetworkParticipant;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
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
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

////////////////////////////////////////////////////////////
// test the optic sensor
////////////////////////////////////////////////////////////
public final class RadarLookupSensorTest extends ASSET.Util.EditableTesting {
	protected class MyEnvironment extends CoreEnvironment {
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
	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	public static RadarLookupSensor getTestRadarSensor(final double rp_m, final double ri_m, final double cr_m,
			final double ir_m, final double max_m, final double k) {
		return new RadarLookupSensor(21, 0.05, 10, 1.05, 0.8, new Duration(20, Duration.SECONDS), 0.2,
				new Duration(30, Duration.SECONDS), k);
	}

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
		return getTestRadarSensor(12, 12, 12, 12, 12, 12);
	}

	@Test
	public void testAspectCalcs() {
		WorldLocation loc1 = SupportTesting.createLocation(0, 0);
		final WorldLocation loc2 = SupportTesting.createLocation(1000, 1000);

		double course = 0;
		double res = calcAspect(loc1, loc2, course);
		assertEquals(135, Math.abs(res), 0.001, "do the first one");

		course = 45;
		res = calcAspect(loc1, loc2, course);
		assertEquals(180, Math.abs(res), 0.001, "do the second one");

		course = 90;
		res = calcAspect(loc1, loc2, course);
		assertEquals(135, Math.abs(res), 0.001, "do the second one");

		course = 135;
		res = calcAspect(loc1, loc2, course);
		assertEquals(90, Math.abs(res), 0.001, "do the second one");

		course = 180;
		res = calcAspect(loc1, loc2, course);
		assertEquals(45, Math.abs(res), 0.001, "do the second one");

		course = 225;
		res = calcAspect(loc1, loc2, course);
		assertEquals(0, Math.abs(res), 0.001, "do the second one");

		// move ownship to the east of the target
		course = 0;
		loc1 = SupportTesting.createLocation(2000, 0);
		res = calcAspect(loc1, loc2, course);
		assertEquals(135, Math.abs(res), 0.001, "do the second one");

		course = 90;
		res = calcAspect(loc1, loc2, course);
		assertEquals(45, Math.abs(res), 0.001, "do the second one");

		course = 180;
		res = calcAspect(loc1, loc2, course);
		assertEquals(45, Math.abs(res), 0.001, "do the second one");

		// and move to the north
		course = 0;
		loc1 = SupportTesting.createLocation(2000, 2000);
		res = calcAspect(loc1, loc2, course);
		assertEquals(45, Math.abs(res), 0.001, "do the second one");

		course = 90;
		res = calcAspect(loc1, loc2, course);
		assertEquals(45, Math.abs(res), 0.001, "do the second one");

		course = 180;
		res = calcAspect(loc1, loc2, course);
		assertEquals(135, Math.abs(res), 0.001, "do the second one");

		// and wierd ones
		course = 0;
		loc1 = SupportTesting.createLocation(2000, 1000);
		res = calcAspect(loc1, loc2, course);
		assertEquals(90, Math.abs(res), 0.001, "do the second one");
		assertEquals(BEAM, determineAspectOf(loc1, loc2, course), 0, "and the aspect");

		loc1 = SupportTesting.createLocation(0, 1000);
		res = calcAspect(loc1, loc2, course);
		assertEquals(90, Math.abs(res), 0.001, "do the second one");

		loc1 = SupportTesting.createLocation(1000, 0);
		res = calcAspect(loc1, loc2, course);
		assertEquals(180, Math.abs(res), 0.001, "do the second one");
		assertEquals(ASTERN, determineAspectOf(loc1, loc2, course), 0, "and the aspect");

		loc1 = SupportTesting.createLocation(1000, 2000);
		res = calcAspect(loc1, loc2, course);
		assertEquals(0, Math.abs(res), 0.001, "do the second one");
		assertEquals(DEAD_AHEAD, determineAspectOf(loc1, loc2, course), 0, "and the aspect");

		loc1 = SupportTesting.createLocation(1200, 1400);
		res = calcAspect(loc1, loc2, course);
		assertEquals(26.565, Math.abs(res), 0.01, "do the second one");
		assertEquals(BOW, determineAspectOf(loc1, loc2, course), 0, "and the aspect");

		loc1 = SupportTesting.createLocation(800, 930);
		res = calcAspect(loc1, loc2, course);
		assertEquals(109.29, Math.abs(res), 0.01, "do the second one");
		assertEquals(BEAM, determineAspectOf(loc1, loc2, course), 0, "and the aspect");

		loc1 = SupportTesting.createLocation(400, 600);
		res = calcAspect(loc1, loc2, course);
		assertEquals(123.69, Math.abs(res), 0.01, "do the second one");
		assertEquals(QUARTER, determineAspectOf(loc1, loc2, course), 0, "and the aspect");

		loc1 = SupportTesting.createLocation(800, 400);
		res = calcAspect(loc1, loc2, course);
		assertEquals(161.56, Math.abs(res), 0.01, "do the second one");
		assertEquals(ASTERN, determineAspectOf(loc1, loc2, course), 0, "and the aspect");

		loc1 = SupportTesting.createLocation(1400, 400);
		res = calcAspect(loc1, loc2, course);
		assertEquals(146.30, Math.abs(res), 0.01, "do the second one");
		assertEquals(QUARTER, determineAspectOf(loc1, loc2, course), 0, "and the aspect");

	}

	@Test
	public final void testFirst() {
		final RadarLookupSensor ts = getTestRadarSensor(4000, 3100, 2500, 1900, 4700, 600);
		final Status statA = new Status(12, 0);
		statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final Helo alpha = new Helo(12);
		alpha.setName("Alpha");
		alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		final WorldLocation originA = SupportTesting.createLocation(0, 0);
		statA.setLocation(originA);
		statA.getLocation().setDepth(-400);
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
		statB.setLocation(SupportTesting.createLocation(800, 600));
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(0, res.size(), "null detections returned");

		// and closer still (to gain contact)
		statB.setLocation(SupportTesting.createLocation(200, 200));
		time += 1000;
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(1, res.size(), "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(), "check detected state");

		// check that the parameters have stayed the same
		statB.setLocation(SupportTesting.createLocation(200, 200));
		time += 1000;
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(1, res.size(), "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(), "check detected state");

		// stay close, move forward enough elapsed time so that we can move to
		// classified
		time += 21000;
		ts.detects(env, res, alpha, scenario, time);
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(), "check detected state");
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
			e.printStackTrace(); // To change body of catch statement use Options | File Templates.
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
		final SensorType firstSensor = theSensors.getSensor(555);
		assertNotNull(firstSensor, "found first sensor");
		assertTrue(firstSensor instanceof RadarLookupSensor);
		final RadarLookupSensor opticS = (RadarLookupSensor) firstSensor;

		// check the values
		assertEquals(0.04, opticS.VDR, 0.0, "VDR correct");
		assertEquals("The radar", opticS.getName(), "name correct");
		assertEquals(1.2, opticS.MRF, 0.0, "MRF correct");
		assertEquals(0.0, opticS.CRF, 0.0, "CRF correct");
		assertEquals(0.0, opticS.IRF, 0.0, "IRF correct");

		assertEquals(11000, opticS.getTimeBetweenDetectionOpportunities(), 0, "tbdo correct");
		assertEquals(0, opticS.CTP.getMillis(), 0, "ctp correct");
		assertEquals(0, opticS.ITP.getMillis(), 0, "itp correct");

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

		final DebriefReplayObserver dro = new DebriefReplayObserver("test_reports", "radar_lookup.rep", true,
				"test observer", true);
		dro.setup(scen);

		// hey, let's run through it a little
		for (int i = 0; i < 900; i++) {
			scen.step();
		}

		dro.tearDown(scen);

		assertTrue(detectedOther, "yes, we made a detection");
		assertTrue(targetLost, "yes, we also lost contact");
		assertEquals(2, maxClassification, 0, "managed to classify");

	}
}
