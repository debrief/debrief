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

package ASSET.Scenario;

import static ASSET.Scenario.CoreScenario.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.Scenario.CoreScenario.*;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Status;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Util.RandomGenerator;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////
// objects
// ////////////////////////////////////

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class CoreScenarioTest {
	protected class createdListener implements ParticipantsChangedListener {
		/**
		 * the indicated participant has been added to the scenario
		 */
		@Override
		public void newParticipant(final int index) {
			createdCounter++;
		}

		/**
		 * the indicated participant has been removed from the scenario
		 */
		@Override
		public void participantRemoved(final int index) {
			destroyedCounter++;
		}

		@Override
		public void restart(final ScenarioType scenario) {

		}
	}

	protected class startStopListener implements ScenarioRunningListener {
		@Override
		public void finished(final long elapsedTime, final String reason) {
			lastStartState = new Boolean(false);
		}

		@Override
		public void newScenarioStepTime(final int val) {
			newStepTime = val;
		}

		@Override
		public void newStepTime(final int val) {
			newStepTime = val;
		}

		/**
		 * the scenario has stopped running on auto
		 */
		@Override
		public void paused() {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		@Override
		public void restart(final ScenarioType scenario) {

		}

		@Override
		public void started() {
			lastStartState = new Boolean(true);
		}

	}

	protected class stepListener implements ScenarioSteppedListener {
		@Override
		public void restart(final ScenarioType scenario) {

		}

		@Override
		public void step(final ScenarioType scenario, final long newTime) {
			lastTime = newTime;
			stepCounter++;
		}

	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";
	protected int stepCounter = 0;
	protected long lastTime = 0;
	protected Boolean lastStartState = null;

	protected int createdCounter = 0;

	protected int destroyedCounter = 0;

	int newStepTime;


	@Test
	public void testMonteCarloCreation() {
		final CoreScenario scen = new CoreScenario();

		final SSN ssn_A = new SSN(12);
		final SSN ssn_B = new SSN(13);
		final SSN ssn_C = new SSN(14);
		final SSN ssn_D = new SSN(15);

		final Status theStat = new Status(12, 0);
		final WorldLocation theLoc = new WorldLocation(1, 1, 1);
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		theStat.setLocation(theLoc);

		ssn_A.setStatus(theStat);
		ssn_B.setStatus(theStat);
		ssn_C.setStatus(theStat);
		ssn_D.setStatus(theStat);

		// check empty, etc
		assertEquals(0, scen._completeParticipantList.size(), "first list done");
		assertEquals(0, scen._myInvisibleParticipants.size(), "second list done");
		assertEquals(0, scen._myVisibleParticipants.size(), "third list done");

		scen.addParticipant(ssn_A.getId(), ssn_A);

		assertEquals(1, scen._completeParticipantList.size(), "first list done");
		assertEquals(0, scen._myInvisibleParticipants.size(), "second list done");
		assertEquals(1, scen._myVisibleParticipants.size(), "third list done");

		scen.addParticipant(ssn_B.getId(), ssn_B);

		assertEquals(2, scen._completeParticipantList.size(), "first list done");
		assertEquals(0, scen._myInvisibleParticipants.size(), "second list done");
		assertEquals(2, scen._myVisibleParticipants.size(), "third list done");

		scen.addMonteCarloParticipant(ssn_C.getId(), ssn_C);

		assertEquals(3, scen._completeParticipantList.size(), "first list done");
		assertEquals(1, scen._myInvisibleParticipants.size(), "second list done");
		assertEquals(2, scen._myVisibleParticipants.size(), "third list done");

		scen.addMonteCarloParticipant(ssn_D.getId(), ssn_D);

		assertEquals(4, scen._completeParticipantList.size(), "first list done");
		assertEquals(2, scen._myInvisibleParticipants.size(), "second list done");
		assertEquals(2, scen._myVisibleParticipants.size(), "third list done");

		// //////////////////////////////////////////////////////////
		// now try deleting them
		// //////////////////////////////////////////////////////////
		scen.removeParticipant(13);
		assertEquals(3, scen._completeParticipantList.size(), "first list done");
		assertEquals(2, scen._myInvisibleParticipants.size(), "second list done");
		assertEquals(1, scen._myVisibleParticipants.size(), "third list done");

		scen.removeParticipant(15);
		assertEquals(2, scen._completeParticipantList.size(), "first list done");
		assertEquals(1, scen._myInvisibleParticipants.size(), "second list done");
		assertEquals(1, scen._myVisibleParticipants.size(), "third list done");

	}

	@Test
	public void testMonteCarloRunning() {
		final SSN ssn_A = new SSN(12);
		final SSN ssn_B = new SSN(13);
		final SSN ssn_C = new SSN(14);
		final SSN ssn_D = new SSN(15);

		final Status theStat = new Status(12, 0);
		final WorldLocation theLoc = new WorldLocation(1, 1, 1);
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		theStat.setLocation(theLoc);

		ssn_A.setStatus(theStat);
		ssn_B.setStatus(theStat);
		ssn_C.setStatus(theStat);
		ssn_D.setStatus(theStat);

		final CoreScenario scen = new CoreScenario();

		scen.addParticipant(ssn_A.getId(), ssn_A);
		scen.addParticipant(ssn_B.getId(), ssn_B);
		scen.addMonteCarloParticipant(ssn_C.getId(), ssn_C);
		scen.addMonteCarloParticipant(ssn_D.getId(), ssn_D);

		final Vector<Integer> aDetections = new Vector<Integer>(0, 1);
		final Vector<Integer> dDetections = new Vector<Integer>(0, 1);

		ssn_A.getRadiatedChars().add(EnvironmentType.BROADBAND_PASSIVE,
				new ASSET.Models.Mediums.BroadbandRadNoise(99));
		ssn_B.getRadiatedChars().add(EnvironmentType.BROADBAND_PASSIVE,
				new ASSET.Models.Mediums.BroadbandRadNoise(99));
		ssn_C.getRadiatedChars().add(EnvironmentType.BROADBAND_PASSIVE,
				new ASSET.Models.Mediums.BroadbandRadNoise(99));
		ssn_D.getRadiatedChars().add(EnvironmentType.BROADBAND_PASSIVE,
				new ASSET.Models.Mediums.BroadbandRadNoise(99));

		ssn_A.addSensor(new BroadbandSensor(12) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			// what is the detection strength for this target?
			@Override
			protected DetectionEvent detectThis(final EnvironmentType environment, final ParticipantType host,
					final ParticipantType target, final long time, final ScenarioType scenario) {
				aDetections.add(new Integer(target.getId()));
				return null;
			}
		});

		ssn_D.addSensor(new BroadbandSensor(19) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			// what is the detection strength for this target?
			@Override
			protected DetectionEvent detectThis(final EnvironmentType environment, final ParticipantType host,
					final ParticipantType target, final long time, final ScenarioType scenario) {
				dDetections.add(new Integer(target.getId()));
				return null;
			}
		});

		scen.setScenarioStepTime(1000);

		scen.step();

		// ok, check that the correct participants were informed about what's
		// going on
		assertEquals(aDetections.size(), 1, "we were only told about B");
		final Integer firstEle = aDetections.firstElement();
		assertEquals(firstEle.intValue(), 13, 0, "we were only told about B");

		// and for the other track
		assertEquals(dDetections.size(), 2, "we were only told about B");
		final Integer firstIndex = new Integer(12);
		final Integer secondIndex = new Integer(13);

		assertTrue(dDetections.contains(firstIndex), " we were informed about A");
		assertTrue(dDetections.contains(secondIndex), " we were informed about B");

	}

	@Test
	public void testScenarioParticipants() {

		// initialise our test counters
		createdCounter = 0;
		destroyedCounter = 0;

		// create server
		final ScenarioType srv = new CoreScenario();

		// add as listener
		final createdListener cl = new createdListener();
		srv.addParticipantsChangedListener(cl);

		// create new scenarios
		// final int s1 = srv.createNewParticipant("SUBMARINE");
		final int s2 = srv.createNewParticipant("FRIGATE");

		// now stop listening for these events
		srv.removeParticipantsChangedListener(cl);

		// add another, which we shouldn't hear about
		final int s3 = srv.createNewParticipant("DESTROYER");

		// check events got fired
		assertEquals(1, createdCounter, "count of created events (one ignored)");
		assertEquals(destroyedCounter, 0, "count of destroyed events");

		// get list of scenarios
		final Integer[] res = srv.getListOfParticipants();

		assertEquals(res.length, 2, "Wrong number of participants");

		// get specific scenarios
		final ParticipantType sc2 = srv.getThisParticipant(s2);
		assertTrue(sc2 != null, "participant returned");

		if (sc2 == null)
			return;

		// make edits
		sc2.setName("participant 2");
		final ParticipantType sc3 = srv.getThisParticipant(s3);
		sc3.setName("participant 3");

		// re-retrieve this scenario to check we're getting the correct one
		final NetworkParticipant sc2a = srv.getThisParticipant(s2);
		assertEquals(sc2a.getName(), "participant 2", "correct name");

		// check we can remove a participant
		// , but check the length first
		final int len = srv.getListOfParticipants().length;
		srv.removeParticipant(s2);

		// check the length
		assertEquals(len - 1, srv.getListOfParticipants().length, "check participant removed");

		// check invalid scenario indices
		NetworkParticipant dd = srv.getThisParticipant(1000);
		assertEquals(dd, null, "invalid index");
		dd = srv.getThisParticipant(0);
		assertEquals(dd, null, "invalid index");
	}

	@Test
	public void testScenarioTimes() {
		final ScenarioType cs = new CoreScenario();

		// listener
		final stepListener sl = new stepListener();
		final startStopListener ssl = new startStopListener();
		cs.addScenarioSteppedListener(sl);
		cs.addScenarioRunningListener(ssl);

		// step times
		cs.setStepTime(new Duration(5, Duration.SECONDS));
		assertEquals(5000, cs.getStepTime(), "New step time");
		assertEquals(5000, newStepTime, "New step time event");
		cs.setScenarioStepTime(new Duration(4, Duration.SECONDS));
		assertEquals(4000, cs.getScenarioStepTime(), "New scenario step time");
		assertEquals(4000, newStepTime, "New scenario step time event");

		cs.setStepTime(500);
		assertEquals(500, cs.getStepTime(), "New step time");
		assertEquals(500, newStepTime, "New step time event");
		cs.setScenarioStepTime(1000);
		assertEquals(1000, cs.getScenarioStepTime(), "New scenario step time");
		assertEquals(1000, newStepTime, "New scenario step time event");

		// current time
		cs.setTime(100);
		assertEquals(100, cs.getTime(), "set time");

		// stepping - firing zero time event first
		cs.step();
		assertEquals(100, cs.getTime(), "fired at zero time");

		// stepping
		cs.step();
		assertEquals(1100, cs.getTime(), "first step");

		cs.step();
		assertEquals(2100, cs.getTime(), "second step");
		assertEquals(2100, lastTime, "second step message");
		assertEquals(3, stepCounter, "step message count");

		// start
		// time step interval
		cs.setStepTime(1000);

		// reset the step counter
		stepCounter = 0;

		// check our listener is prepped
		assertNull(lastStartState, "Start-Stop listener is ready");
		System.out.println("about to start timed steps");

		long tStart = System.currentTimeMillis();
		cs.start();

		assertTrue((lastStartState.booleanValue() == true), "Start message sent");

		while (stepCounter < 3) {
			try {
				Thread.sleep(100);
			} catch (final java.lang.InterruptedException e) {
				e.printStackTrace();
			}
		}

		long tEnd = System.currentTimeMillis();
		System.out.println("finished timed steps");

		final CoreScenario coreS = (CoreScenario) cs;
		assertEquals(null, coreS._stopReason, "Stop not pending yet");

		cs.stop("testing");

		assertTrue((lastStartState.booleanValue() == true), "Stop message sent but not processed");
		assertNotNull(coreS._stopReason, "Stop message sent but not processed");

		// insert a step, so the stop can be processed
		cs.step();

		assertTrue((lastStartState.booleanValue() == false), "Stop message sent");

		long tDiff = tEnd - tStart;

		// check time elapsed
		assertTrue((tDiff >= 1000), "timer actually ran: time was:" + tDiff);
		assertTrue((tDiff < 2300), "timer didn't take too long" + tDiff);

		// running on auto!
		cs.setStepTime(0);
		cs.setScenarioStepTime(5);
		cs.setTime(0);

		tStart = System.currentTimeMillis();

		// reset the step counter
		stepCounter = 0;

		// check our listener is prepped
		lastStartState = null;
		assertNull(lastStartState, "Start-Stop listener is ready");

		// get going
		cs.start();
		assertTrue((lastStartState.booleanValue() == true), "Start message sent");

		while (stepCounter < 100) {
			try {
				Thread.sleep(1);
			} catch (final java.lang.InterruptedException e) {
				e.printStackTrace();
			}
		}
		cs.stop("testing");

		tEnd = System.currentTimeMillis();

		cs.step();

		assertTrue((lastStartState.booleanValue() == false), "Stop message sent");

		// how long?
		tDiff = tEnd - tStart;

		// check time elapsed

		// hack: suspect both of these test should allow zero error.
		// assertEquals("steps got performed", 140, stepCounter, 40);
		// assertEquals("scenario moved forward", 600, cs.getTime(), 100);

		// stop
		// - this has already been tested

		// reset
	}
}
