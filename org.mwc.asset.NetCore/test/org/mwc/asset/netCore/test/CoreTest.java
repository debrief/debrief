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

package org.mwc.asset.netCore.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.mwc.asset.netCore.common.Network.AHandler;
import org.mwc.asset.netCore.common.Network.LightParticipant;
import org.mwc.asset.netCore.common.Network.LightScenario;
import org.mwc.asset.netCore.core.AServer;
import org.mwc.asset.netCore.core.MClient;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;
import org.junit.jupiter.api.BeforeEach;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDecidedListener;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.MultiScenarioLister;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GenericData.WorldSpeed;

public class CoreTest {

	private static class CombinedListener
			implements ParticipantMovedListener, ParticipantDetectedListener, ParticipantDecidedListener {
		private final Vector<String> moves;
		private final Vector<String> detects;
		protected Status lastStat;

		public CombinedListener(final Vector<String> movements, final Vector<String> detections) {
			moves = movements;
			detects = detections;
		}

		@Override
		public void moved(final Status newStatus) {
			moves.add("move:" + newStatus.getTime());
			lastStat = newStatus;
			System.err.println(newStatus.getTime() + " at:" + newStatus.getLocation());
		}

		@Override
		public void newDecision(final String description, final DemandedStatus dem_status) {
			moves.add("dec:" + description);
		}

		@Override
		public void newDetections(final DetectionList detections) {
			detects.add("det:" + detections.size());
		}

		@Override
		public void restart(final ScenarioType scenario) {
		}

	}

	public class MySListener implements ScenarioSteppedListener {
		private final Vector<String> _items;

		public MySListener(final Vector<String> items) {
			_items = items;
		}

		@Override
		public void restart(final ScenarioType scenario) {
			_items.add("restart");
		}

		@Override
		public void step(final ScenarioType scenario, final long newTime) {
			_items.add("step to:" + newTime);
		}
	}

	protected static void showEvents(final Vector<String> events) {
		System.out.println("=================");
		final Iterator<String> iter = events.iterator();
		int ctr = 0;
		while (iter.hasNext()) {
			final String string = iter.next();
			System.out.println(++ctr + " - " + string);
		}
	}

	private Vector<String> _events;

	protected Vector<LightScenario> _myList;;

	public void ntestStartup() throws InterruptedException, IOException {
		// check events empty
		assertEquals(0, _events.size(), "events empty");
		final AServer server = new AServer();
		server.start();
		assertEquals(1, _events.size(), "events recorded");
		assertEquals("Server opened.", _events.elementAt(0), "correct start event");

		// and now the client
		final MClient client = new MClient();
		assertEquals(1, _events.size(), "still no more events");

		// now real connection
		client.connect(null);
		Thread.sleep(100);

		assertEquals(5, _events.size(), "events recorded");
		assertTrue(_events.elementAt(1).contains("Discovered server"), "correct client event");
		assertTrue(_events.elementAt(2).contains("Connecting"), "correct client event");
		assertTrue(_events.elementAt(3).contains("connected"), "correct client event");
		assertTrue(_events.elementAt(4).contains("connected"), "correct client event");

		// ok, give the server some data
		final MultiScenarioLister lister = new MultiScenarioLister() {

			@Override
			public Vector<ScenarioType> getScenarios() {
				return SampleScenarios.getScenarioList();
			}
		};
		server.setDataProvider(lister);

		assertNull(_myList, "scen list should be empty");
		final AHandler<Vector<LightScenario>> sHandler = new AHandler<Vector<LightScenario>>() {
			@Override
			public void onSuccess(final Vector<LightScenario> result) {
				_myList = result;
			}
		};
		// fire in request for scenarios
		System.err.println("about to request scenarios");
		client.getScenarioList(sHandler);
		Thread.sleep(300);

		showEvents(_events);

		assertNotNull(_myList, "scen list should have data");
		assertEquals(3, _myList.size(), "scen has correct number");
		final LightScenario scen = _myList.elementAt(0);
		assertEquals("aaa", scen.name, "scen has correct name");
		assertEquals(3, scen.listOfParticipants.size(), "has correct parts");
		final LightParticipant firstPart = scen.listOfParticipants.firstElement();
		assertEquals("p12", firstPart.name, "has correct first part");
		assertEquals(12, firstPart.id, "has correct first id");

		// check we have no lsitenser....
		assertEquals(0, server.getPartListeners().size(), "no listeners");

		// ok, check scenario listen/release
		assertEquals(0, server.getScenListeners().size(), "no scen lsiteners");
		final Vector<String> stepLog = new Vector<String>();
		final ScenarioSteppedListener sListener = new MySListener(stepLog);
		client.listenScen(scen.name, sListener);
		Thread.sleep(100);

		assertEquals(1, server.getScenListeners().size(), "our scen lsitener got registered");
		assertEquals(0, stepLog.size(), "step evnts empty");

		// ok, are we rx step events?
		server.step(scen.name);
		Thread.sleep(100);

		assertEquals(1, stepLog.size(), "step evnts populated");

		// ok, stop listening
		client.stopListenScen(scen.name);
		Thread.sleep(100);

		// ok, are we rx step events?
		server.step(scen.name);
		Thread.sleep(100);
		assertEquals(1, stepLog.size(), "no new step evnts populated");

		// ok, start listening again
		client.listenScen(scen.name, sListener);
		Thread.sleep(100);

		// ok, are we rx step events?
		server.step(scen.name);
		Thread.sleep(100);

		assertEquals(2, stepLog.size(), "rx new step event");

		// next, we want to listen to a participant
		final Vector<String> moveLog = new Vector<String>();
		final Vector<String> detectLog = new Vector<String>();
		final CombinedListener combi = new CombinedListener(moveLog, detectLog);
		client.listenPart(scen.name, firstPart.id, combi, combi, combi);
		Thread.sleep(600);

		assertEquals(1, server.getPartListeners().size(), "a listener");

		// ok, now try to release
		client.stopListenPart(scen.name, firstPart.id);
		Thread.sleep(600);

		// System.in.read();
		assertEquals(0, server.getPartListeners().size(), "no listener");

		// connect again
		// ok, now try to control the participant
		client.listenPart(scen.name, firstPart.id, combi, combi, combi);
		Thread.sleep(600);

		assertEquals(1, server.getPartListeners().size(), "a listener");

		// move scenario & see if movement occurs...
		client.step(scen.name);
		Thread.sleep(200);
		client.step(scen.name);
		Thread.sleep(200);
		client.step(scen.name);
		Thread.sleep(200);

		// check we've seen some mvoement
		assertEquals(3, moveLog.size(), "movement detected");
		assertTrue(moveLog.firstElement().startsWith("move"), "has movement");

		// and some dtections
		assertEquals(3, moveLog.size(), "detections performed");

		// check the course
		assertEquals(12d, combi.lastStat.getCourse(), 0.001, "has correct course");

		// ok, stop listening
		// ok, now try to release
		client.stopListenPart(scen.name, firstPart.id);
		Thread.sleep(600);

		assertEquals(0, server.getPartListeners().size(), "no listener");

		// do another step anyway
		server.step(scen.name);
		Thread.sleep(200);

		// check we've got the same movement
		assertEquals(3, moveLog.size(), "movement detected");

		// ok, reconnect, and try driving it...
		// ok, now try to control the participant
		client.listenPart(scen.name, firstPart.id, combi, combi, combi);
		Thread.sleep(600);
		assertEquals(1, server.getPartListeners().size(), "a listener");

		// pl. try driving
		assertEquals("DefaultWander", SampleScenarios.testParticipant.getDecisionModel().getName(), "original dec model");
		client.controlPart(scen.name, firstPart.id, 55d, 4d, 0d);

		// move forward & look for change in course/speed
		server.step(scen.name);
		Thread.sleep(200);
		server.step(scen.name);
		Thread.sleep(200);
		server.step(scen.name);
		Thread.sleep(200);

		// check we've seen some mvoement
		assertEquals(6, moveLog.size(), "movement detected");
		assertTrue(moveLog.firstElement().startsWith("move"), "has movement");

		// check the model is user control
		assertEquals(AServer.NETWORK_CONTROL, SampleScenarios.testParticipant.getDecisionModel().getName(), "user control dec model");

		// check the course
		assertEquals(55d, combi.lastStat.getCourse(), 0.001, "has correct course");
		assertEquals(4d, combi.lastStat.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "has correct speed");

		// ok, release control
		client.releasePart(scen.name, firstPart.id);
		Thread.sleep(200);
		assertEquals("DefaultWander", SampleScenarios.testParticipant.getDecisionModel().getName(), "original dec model restored");

		// check we stop receiving updates
		client.stopListenPart(scen.name, firstPart.id);
		Thread.sleep(50);
		server.step(scen.name);
		Thread.sleep(200);
		server.step(scen.name);
		Thread.sleep(200);
		assertEquals(6, moveLog.size(), "no more steps received");

		// ///////////////////////////
		// checking down more than one level
		// ///////////////////////////
		client.listenPart(scen.name, firstPart.id, combi, combi, combi);
		assertEquals("DefaultWander", SampleScenarios.testParticipant.getDecisionModel().getName(), "original dec model");
		client.controlPart(scen.name, firstPart.id, 55d, 4d, 0d);
		Thread.sleep(200);

		// check the model is under user control
		assertEquals(AServer.NETWORK_CONTROL, SampleScenarios.testParticipant.getDecisionModel().getName(), "user control dec model");

		// ok, now step down two levels
		client.stopListenPart(scen.name, firstPart.id);

		// check the behaviour got restored
		Thread.sleep(200);
		assertEquals("DefaultWander", SampleScenarios.testParticipant.getDecisionModel().getName(), "original dec model restored");

		// HOW ABOUT TWO LEVELS!
		client.listenPart(scen.name, firstPart.id, combi, combi, combi);
		assertEquals("DefaultWander", SampleScenarios.testParticipant.getDecisionModel().getName(), "original dec model");
		client.controlPart(scen.name, firstPart.id, 55d, 4d, 0d);
		Thread.sleep(200);

		// check the model is under user control
		assertEquals(AServer.NETWORK_CONTROL, SampleScenarios.testParticipant.getDecisionModel().getName(), "user control dec model");
		assertEquals(1, server.getPartListeners().size(), "only one listener");

		// ok, collapse it!
		client.stopListenScen(scen.name);

		Thread.sleep(200);
		assertEquals("DefaultWander", SampleScenarios.testParticipant.getDecisionModel().getName(), "original dec model restored");
		assertEquals(0, server.getPartListeners().size(), "zero listeners");

		showEvents(_events);
		System.out.println("pausing");
		// System.in.read();
		client.stop();
		server.stop();
	}

	public void ntestZConnect() throws InterruptedException, IOException {
		// check events empty
		assertEquals(0, _events.size(), "events empty");
		final AServer server = new AServer();
		server.start();
		assertEquals(1, _events.size(), "events recorded");
		assertEquals("Server opened.", _events.elementAt(0), "correct start event");

		// and now the client
		final MClient client = new MClient();
		assertEquals(1, _events.size(), "still no more events");

		// try with dodgy target
		boolean errorThrown = false;
		try {
			client.connect("128.3.3.3");
		} catch (final IOException e) {
			errorThrown = true;
		}
		assertTrue(errorThrown, "Exception thrown with dodgy target");
		assertEquals(2, _events.size(), "events recorded");
		System.err.println("event is:" + _events.elementAt(1));
		assertTrue(_events.elementAt(1).contains("Connecting: /128.3.3.3:54555/54778"), "correct client event");

		// now real connection
		client.connect(null);
		Thread.sleep(100);

		client.stop();
		server.stop();
	}

	@BeforeEach
	public void setUp() throws Exception {

		_events = new Vector<String>();
		final Logger logger = new Logger() {

			@Override
			public void log(final int level, final String category, final String message, final Throwable ex) {
				_events.add(message);
			}
		};
		Log.setLogger(logger);
	}

}
