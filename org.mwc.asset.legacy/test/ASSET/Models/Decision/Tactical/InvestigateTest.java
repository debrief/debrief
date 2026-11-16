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

package ASSET.Models.Decision.Tactical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.Investigate.*;
import ASSET.Models.Decision.Tactical.Investigate.*;
import java.util.HashMap;
import java.util.Vector;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Decision.Movement.RectangleWander;
import ASSET.Models.Decision.Tactical.Investigate.InvestigatePatternFactory.InvestigatePattern;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public final class InvestigateTest extends ASSET.Util.EditableTesting {
	boolean hasStopped = false;


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final TargetType theTarget = new TargetType(Category.Force.RED);
		final Investigate investigate = new Investigate("investigating red targets", theTarget,
				DetectionEvent.IDENTIFIED, null);
		return investigate;
	}

	// TODO FIX-TEST
	public void NtestCollaborative() {
		final TargetType theWatch1 = new TargetType(Category.Type.MPA);
		final TargetType theTarget1 = new TargetType(Category.Force.RED);
		final Investigate investigate1 = new Investigate("investigating red targets near blue", theTarget1,
				theWatch1, DetectionEvent.IDENTIFIED, null);
		investigate1.setCollaborativeSearch(true);

		final TargetType theWatch2 = new TargetType(Category.Type.MPA);
		final TargetType theTarget2 = new TargetType(Category.Force.RED);
		final Investigate investigate2 = new Investigate("other investigating red targets near blue", theTarget2,
				theWatch2, DetectionEvent.IDENTIFIED, null);
		investigate2.setCollaborativeSearch(true);

		final int TGT1_ID = 567;
		final int TGT2_ID = 568;
		final int BLUE_ID1 = 34;
		final int BLUE_ID2 = 46;
		final int NOT_TGT_ID = 123;

		final Status blueStat1 = new Status(BLUE_ID1, 0);
		blueStat1.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
		blueStat1.setLocation(new WorldLocation(4, 4, 2));
		final ParticipantType blueP1 = new CoreParticipant(BLUE_ID1);
		blueP1.setStatus(blueStat1);
		final Category blueCat1 = new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
				Category.Type.MPA);
		blueP1.setCategory(blueCat1);

		final Status blueStat2 = new Status(BLUE_ID2, 0);
		blueStat2.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
		blueStat2.setLocation(new WorldLocation(4, 4, 2));
		final ParticipantType blueP2 = new CoreParticipant(BLUE_ID2);
		blueP2.setStatus(blueStat2);
		final Category blueCat2 = new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
				Category.Type.MPA);
		blueP2.setCategory(blueCat2);

		final Status tgt1Stat = new Status(222, 0);
		tgt1Stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		tgt1Stat.setLocation(new WorldLocation(2, 2.5, 2));
		tgt1Stat.setCourse(22);

		final Status tgt2Stat = new Status(222, 0);
		tgt2Stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		tgt2Stat.setLocation(new WorldLocation(4, 2.5, 2));
		tgt2Stat.setCourse(22);

		final MovementCharacteristics theChars = HeloMovementCharacteristics.getSampleChars();
		final SimpleDemandedStatus theDemStat = new SimpleDemandedStatus(12, 12000);
		DetectionList theDetections = null;

		final LookupSensor duffSensor = new RadarLookupSensor(12, 0, 0, 0, 0, null, 0, null, 0);

		final Surface target1 = new Surface(TGT1_ID, null, null, "fisher");
		target1.setStatus(tgt1Stat);

		final Surface target2 = new Surface(TGT2_ID, null, null, "fisher");
		target2.setStatus(tgt2Stat);

		final ScenarioActivityMonitor theMonitor = new ScenarioActivityMonitor() {
			@Override
			public void createParticipant(final ParticipantType newPart) {
			}

			@Override
			public void detonationAt(final int id, final WorldLocation loc, final double power) {
			}

			@Override
			public Integer[] getListOfParticipants() {
				return new Integer[] { BLUE_ID1, TGT1_ID, TGT2_ID };
			}

			@Override
			public ParticipantType getThisParticipant(final int id)

			{
				ParticipantType res = null;
				switch (id) {
				case BLUE_ID1:
					res = blueP1;
					break;
				case TGT1_ID:
					res = target1;
					break;
				case TGT2_ID:
					res = target2;
					break;
				}
				return res;
			}
		};

		DemandedStatus res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		SimpleDemandedStatus simple = (SimpleDemandedStatus) res;
		assertNull(res, "null dem stat when null detections");

		// ok. now use zero length detections
		theDetections = new DetectionList();

		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when empty detections");
		res = investigate2.decide(blueStat2, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when empty detections");

		// and put in an invalid detection
		final Category tgtCategory = new Category(Category.Force.BLUE, Category.Environment.SURFACE,
				Category.Type.FISHING_VESSEL);
		final double target_brg = 12;
		final DetectionEvent de = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
				null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target1,
				DetectionEvent.DETECTED);
		theDetections.add(de);
		final DetectionEvent de2 = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
				null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target2,
				DetectionEvent.DETECTED);
		theDetections.add(de2);

		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when invalid target");
		final InvestigateStore theStore1 = Investigate._invData.get(theMonitor, BLUE_ID1,
				investigate1.isCollaborativeSearch());
		assertNull(theStore1.getCurrentTarget(BLUE_ID1), "inv target still empty");

		res = investigate2.decide(blueStat2, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when invalid target");
		final InvestigateStore theStore2 = Investigate._invData.get(theMonitor, BLUE_ID2,
				investigate2.isCollaborativeSearch());
		assertNull(theStore2.getCurrentTarget(BLUE_ID2), "inv target still empty");

		// now try a valid target
		tgtCategory.setForce(Category.Force.RED);
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");
		assertNotNull(theStore1.getCurrentTarget(BLUE_ID1), "inv target not still empty");
		assertEquals(theStore1.getCurrentTarget(BLUE_ID1).intValue(), TGT2_ID, "inv target not still empty");

		// check that the other searcher spots the other target
		tgtCategory.setForce(Category.Force.RED);
		res = investigate2.decide(blueStat2, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");
		assertNotNull(theStore2.getCurrentTarget(BLUE_ID2), "inv target not still empty");
		assertEquals(theStore2.getCurrentTarget(BLUE_ID2).intValue(), TGT1_ID, "inv target not still empty");

		// now make it so there's no valid watch
		blueCat1.setType(Category.Type.MINISUB);
		theStore1.clearCurrentTarget(BLUE_ID1);
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "dem stat when valid target");
		assertNull(theStore1.getCurrentTarget(BLUE_ID1), "inv target not still empty");

		// and put back our watched item
		blueCat1.setType(Category.Type.MPA);
		theStore1.clearCurrentTarget(12);
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");
		assertNotNull(theStore1.getCurrentTarget(BLUE_ID1), "inv target not still empty");

		// ok. let's lose the target and see what happens
		theDetections.clear();
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when target lost");
		assertNotNull(theStore1.getCurrentTarget(BLUE_ID1), "remembered target");
		assertEquals(TGT2_ID, theStore1.getCurrentTarget(BLUE_ID1).intValue(), "got correct tgt id");

		// and offer another target
		theDetections.add(de);
		de.setTarget(NOT_TGT_ID);
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "no dem stat when valid target");

		// back to our target
		de.setTarget(TGT1_ID);
		theDetections.add(de2);
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		simple = (SimpleDemandedStatus) res;
		assertNotNull(res, "new dem stat when valid target");
		assertTrue(simple.getCourse() > 0, "on a good bearing");
		assertEquals(TGT2_ID, theStore1.getCurrentTarget(BLUE_ID1).intValue(), "got new tgt id");

		// check that we can tick off target when found
		de2.setDetectionState(DetectionEvent.CLASSIFIED);
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");
		assertEquals(TGT2_ID, theStore1.getCurrentTarget(BLUE_ID1).intValue(), "got new tgt id");

		// check that we can tick off target when found
		de2.setDetectionState(DetectionEvent.IDENTIFIED);
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "no dem status");
		assertNull(theStore1.getCurrentTarget(BLUE_ID1), "ditched current target");
		assertEquals(1, theStore1.countDoneTargets(), "got something in found targets");

		// check that we can tick off target when found
		de.setDetectionState(DetectionEvent.IDENTIFIED);
		res = investigate2.decide(blueStat2, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "no dem status");
		assertNull(theStore2.getCurrentTarget(BLUE_ID2), "ditched current target");
		assertEquals(2, theStore2.countDoneTargets(), "got something in found targets");

		// check that we can tick off other target when found
		de2.setDetectionState(DetectionEvent.CLASSIFIED);
		res = investigate1.decide(blueStat1, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "no dem status when only existing target found");
		assertNull(theStore1.getCurrentTarget(BLUE_ID1), "ditched current target");
		assertEquals(2, theStore1.countDoneTargets(), "got something in found targets");

	}

	// TODO FIX-TEST
	public void NtestRunning() {
		// note, for test purposes we need to clear the list of stores, since
		// our debug accessor just fetches the first store
		Investigate._invData.clearStores();

		final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
		topLeft.setDepth(-1000);
		final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
		bottomRight.setDepth(1000);
		final WorldArea theArea = new WorldArea(topLeft, bottomRight);
		final RectangleWander heloWander = new RectangleWander(theArea, "rect wander");
		heloWander.setSpeed(new WorldSpeed(20, WorldSpeed.Kts));
		final RectangleWander fishWander = new RectangleWander(theArea, "rect wander 2");

		RandomGenerator.seed(12);

		final Waterfall searchPattern = new Waterfall();
		searchPattern.setName("Searching");

		final TargetType theTarget = new TargetType(Category.Force.RED);
		final Investigate investigate = new Investigate("investigating red targets", theTarget,
				DetectionEvent.IDENTIFIED, null);
		searchPattern.insertAtHead(investigate);
		searchPattern.insertAtFoot(heloWander);

		final Status heloStat = new Status(1, 0);
		heloStat.setLocation(SupportTesting.createLocation(2000, 4000));
		heloStat.getLocation().setDepth(-200);
		heloStat.setCourse(270);
		heloStat.setSpeed(new WorldSpeed(100, WorldSpeed.Kts));

		final Status fisherStat = new Status(1, 0);
		fisherStat.setLocation(SupportTesting.createLocation(4000, 2000));
		fisherStat.setCourse(155);
		fisherStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		final DemandedStatus dem = null;

		final SimpleDemandedStatus ds = (SimpleDemandedStatus) heloWander.decide(heloStat, null, dem, null, null,
				100);

		assertNotNull(ds, "dem returned");

		// ok. the radar first
		final ASSET.Models.Sensor.Lookup.RadarLookupSensor radar = new RadarLookupSensor(12, 0.04, 11000, 1.2, 0,
				new Duration(0, Duration.SECONDS), 0, new Duration(0, Duration.SECONDS), 9200);

		final ASSET.Models.Sensor.Lookup.OpticLookupSensor optic = new OpticLookupSensor(333, 0.05, 10000, 1.05,
				0.8, new Duration(20, Duration.SECONDS), 0.2, new Duration(30, Duration.SECONDS));

		final Helo helo = new Helo(23);
		helo.setName("Merlin");
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		helo.setStatus(heloStat);
		helo.setDecisionModel(searchPattern);
		helo.setMovementChars(HeloMovementCharacteristics.getSampleChars());
		helo.getSensorFit().add(radar);
		helo.getSensorFit().add(optic);

		final Surface fisher2 = new Surface(25);
		fisher2.setName("Fisher2");
		fisher2.setCategory(
				new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		fisher2.setStatus(fisherStat);
		fisher2.setDecisionModel(fishWander);
		fisher2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(5000);
		cs.addParticipant(helo.getId(), helo);
		cs.addParticipant(fisher2.getId(), fisher2);

		// ok. just do a couple of steps, to check how things pan out.
		cs.step();

		System.out.println("started at:" + cs.getTime());

		final DebriefReplayObserver dro = new DebriefReplayObserver("./test_reports/", "investigate_search.rep",
				false, true, true, null, "plotter", true, null);
		final TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 300, 300, "investigate_search.png",
				null, false, true, false, "tester", true);
		dro.setup(cs);
		tpo.setup(cs);
		//
		dro.outputThisArea(theArea);

		final InvestigateStore theStore = Investigate._invData.firstStore();

		// now run through to completion
		int counter = 0;
		while ((cs.getTime() < 12000000) && (theStore.getCurrentTarget(23) == null)) {
			cs.step();
			counter++;
		}

		// so, we should have found our tartget
		assertNotNull(theStore.getCurrentTarget(23), "found target");

		// ok. we've found it. check that we do transition to detected
		counter = 0;
		while ((counter++ < 100) && (theStore.getCurrentTarget(23) != null)) {
			cs.step();
		}

		dro.tearDown(cs);
		tpo.tearDown(cs);

		// so, we should have cleared our tartget
		assertNull(theStore.getCurrentTarget(23), "found target");
		assertEquals(1, theStore.countDoneTargets(), "remembered contact");

	}

	// TODO FIX-TEST
	public void NtestWithWatch() {
		final TargetType theWatch = new TargetType(Category.Type.MPA);
		final TargetType theTarget = new TargetType(Category.Force.RED);
		final Investigate investigate = new Investigate("investigating red targets near blue", theTarget, theWatch,
				DetectionEvent.IDENTIFIED, null);

		final int TGT1_ID = 567;
		final int TGT2_ID = 568;
		final int BLUE_ID = 34;
		final int NOT_TGT_ID = 123;

		final Status blueStat = new Status(BLUE_ID, 0);
		blueStat.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
		blueStat.setLocation(new WorldLocation(4, 4, 2));
		final ParticipantType blueP = new CoreParticipant(BLUE_ID);
		blueP.setStatus(blueStat);
		final Category blueCat = new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
				Category.Type.MPA);
		blueP.setCategory(blueCat);

		final Status myStat = new Status(12, 0);
		myStat.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
		myStat.setLocation(new WorldLocation(2, 2, 2));

		final Status tgt1Stat = new Status(222, 0);
		tgt1Stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		tgt1Stat.setLocation(new WorldLocation(2, 2.5, 2));
		tgt1Stat.setCourse(22);

		final Status tgt2Stat = new Status(222, 0);
		tgt2Stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		tgt2Stat.setLocation(new WorldLocation(4, 2.5, 2));
		tgt2Stat.setCourse(22);

		final MovementCharacteristics theChars = HeloMovementCharacteristics.getSampleChars();
		final SimpleDemandedStatus theDemStat = new SimpleDemandedStatus(12, 12000);
		DetectionList theDetections = null;

		final LookupSensor duffSensor = new RadarLookupSensor(12, 0, 0, 0, 0, null, 0, null, 0);

		final Surface target1 = new Surface(TGT1_ID, null, null, "fisher");
		target1.setStatus(tgt1Stat);

		final Surface target2 = new Surface(TGT2_ID, null, null, "fisher");
		target2.setStatus(tgt2Stat);

		final ScenarioActivityMonitor theMonitor = new ScenarioActivityMonitor() {
			@Override
			public void createParticipant(final ParticipantType newPart) {
			}

			@Override
			public void detonationAt(final int id, final WorldLocation loc, final double power) {
			}

			@Override
			public Integer[] getListOfParticipants() {
				return new Integer[] { BLUE_ID, TGT1_ID, TGT2_ID };
			}

			@Override
			public ParticipantType getThisParticipant(final int id)

			{
				ParticipantType res = null;
				switch (id) {
				case BLUE_ID:
					res = blueP;
					break;
				case TGT1_ID:
					res = target1;
					break;
				case TGT2_ID:
					res = target2;
					break;
				}
				return res;
			}
		};

		DemandedStatus res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		SimpleDemandedStatus simple = (SimpleDemandedStatus) res;
		assertNull(res, "null dem stat when null detections");

		// ok. now use zero length detections
		theDetections = new DetectionList();

		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when empty detections");

		// and put in an invalid detection
		final Category tgtCategory = new Category(Category.Force.BLUE, Category.Environment.SURFACE,
				Category.Type.FISHING_VESSEL);
		final double target_brg = 12;
		final DetectionEvent de = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
				null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target1,
				DetectionEvent.DETECTED);
		theDetections.add(de);
		final DetectionEvent de2 = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
				null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target2,
				DetectionEvent.DETECTED);
		theDetections.add(de2);

		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when invalid target");
		final InvestigateStore theStore = Investigate._invData.get(theMonitor, 12,
				investigate.isCollaborativeSearch());
		assertNull(theStore.getCurrentTarget(12), "inv target still empty");

		// now try a valid target
		tgtCategory.setForce(Category.Force.RED);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");
		assertNotNull(theStore.getCurrentTarget(12), "inv target not still empty");
		assertEquals(theStore.getCurrentTarget(12).intValue(), TGT2_ID, "inv target not still empty");

		// now make it so there's no valid watch
		blueCat.setType(Category.Type.MINISUB);
		theStore.clearCurrentTarget(12);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "dem stat when valid target");
		assertNull(theStore.getCurrentTarget(12), "inv target not still empty");

		// and put back our watched item
		blueCat.setType(Category.Type.MPA);
		theStore.clearCurrentTarget(12);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");
		assertNotNull(theStore.getCurrentTarget(12), "inv target not still empty");

		// ok. let's lose the target and see what happens
		theDetections.clear();
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when target lost");
		assertNotNull(theStore.getCurrentTarget(12), "remembered target");
		assertEquals(TGT2_ID, theStore.getCurrentTarget(12).intValue(), "got correct tgt id");

		// and offer another target
		theDetections.add(de);
		de.setTarget(NOT_TGT_ID);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "new dem stat when valid target");

		// back to our target
		de.setTarget(TGT1_ID);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		simple = (SimpleDemandedStatus) res;
		assertNotNull(res, "new dem stat when valid target");
		assertTrue(simple.getCourse() > 0, "on a good bearing");
		assertEquals(TGT1_ID, theStore.getCurrentTarget(12).intValue(), "got new tgt id");

		// check that we can tick off target when found
		de.setDetectionState(DetectionEvent.CLASSIFIED);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");
		assertEquals(TGT1_ID, theStore.getCurrentTarget(12).intValue(), "got new tgt id");

		// check that we can tick off target when found
		de.setDetectionState(DetectionEvent.IDENTIFIED);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "no dem status");
		assertNull(theStore.getCurrentTarget(12), "ditched current target");
		assertEquals(1, theStore.countDoneTargets(), "got something in found targets");

		// check that we can tick off target when found
		de.setDetectionState(DetectionEvent.CLASSIFIED);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "no dem status when only existing target found");
		assertNull(theStore.getCurrentTarget(12), "ditched current target");
		assertEquals(1, theStore.countDoneTargets(), "got something in found targets");

	}

	@Test
	public void testHashMap() {
		final HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(12, "twelve");
		assertTrue(map.containsKey(12));
		assertTrue(map.containsKey(11 + 1));
		final Integer newVal = 33 - 21;
		assertTrue(map.containsKey(newVal));
	}

	@Test
	public void testRunningMultipleTargets() {
		Investigate._invData.clearStores();

		// initialise the random genny
		ASSET.Util.RandomGenerator.seed(12);

		final WorldLocation topLeft = SupportTesting.createLocation(0, 0);
		topLeft.setDepth(-1000);
		final WorldLocation bottomRight = SupportTesting.createLocation(new WorldDistance(40, WorldDistance.NM),
				new WorldDistance(40, WorldDistance.NM));
		bottomRight.setDepth(1000);
		final WorldArea theArea = new WorldArea(topLeft, bottomRight);

		final PatternSearch_Core ladder = new PatternSearch_Ladder(90.0, new Integer(4), new WorldLocation(0, 0, 0),
				new WorldDistance(10, WorldDistance.NM), new WorldDistance(30, WorldDistance.NM),
				new WorldDistance(500, WorldDistance.FT), null, "Force Protection Ladder");

		final RectangleWander heloWander = new RectangleWander(theArea, "rect wander");
		heloWander.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));
		heloWander.setHeight(new WorldDistance(800, WorldDistance.METRES));
		final RectangleWander fishWander = new RectangleWander(theArea, "rect wander 2");

		final Waterfall searchPattern = new Waterfall();
		searchPattern.setName("Searching");

		final TargetType theTarget = new TargetType(Category.Force.RED);
		final WorldDistance detectionHeight = new WorldDistance(100, WorldDistance.FT);
		final Investigate investigate = new Investigate("investigating red targets", theTarget,
				DetectionEvent.IDENTIFIED, detectionHeight);

		searchPattern.insertAtHead(investigate);
		searchPattern.insertAtFoot(ladder);

		final Status heloStat = new Status(1, 10);
		heloStat.setLocation(SupportTesting.createLocation(2000, 4000));
		heloStat.getLocation().setDepth(-MWC.Algorithms.Conversions.ft2m(900));
		heloStat.setCourse(270);
		heloStat.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));

		// get the helo sensors ready
		// ok. the radar first
		final ASSET.Models.Sensor.Lookup.RadarLookupSensor radar = new RadarLookupSensor(12, 0.04, 5000, 1.2, 0,
				new Duration(0, Duration.SECONDS), 0, new Duration(0, Duration.SECONDS), 9200);

		final ASSET.Models.Sensor.Lookup.OpticLookupSensor optic = new OpticLookupSensor(333, 0.05, 5000, 1.05, 0.8,
				new Duration(20, Duration.SECONDS), 0.2, new Duration(30, Duration.SECONDS));

		final Helo helo = new Helo(93);
		helo.setName("Merlin");
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		helo.setStatus(heloStat);
		helo.setDecisionModel(searchPattern);
		helo.setMovementChars(HeloMovementCharacteristics.getSampleChars());
		helo.getSensorFit().add(radar);
		helo.getSensorFit().add(optic);

		// ////////////////////////////////////////////////
		// scenario
		// ////////////////////////////////////////////////

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(5000);
		cs.setSeed(new Integer(251));
		cs.addParticipant(helo.getId(), helo);

		// ////////////////////////////////////////////////
		// fishing vessels
		// ////////////////////////////////////////////////

		// create the fishing vessels
		for (int i = 0; i < 6; i++) {
			final int my_id = 10 + i;
			final Surface fisher2 = new Surface(my_id);
			fisher2.setName("Fisher_" + my_id);
			fisher2.setCategory(
					new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
			final Status fisherStat = new Status(21, 10);
			fisherStat.setLocation(SupportTesting.createLocation(theArea));
			fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
			fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
			fisher2.setStatus(fisherStat);
			fisher2.setDecisionModel(fishWander);
			fisher2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
			// if(i==5)
			cs.addParticipant(fisher2.getId(), fisher2);
		}

		// also add some duff targets
		Surface fisher3 = new Surface(123);
		fisher3.setName("Safe_fisher_1");
		fisher3.setCategory(
				new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		Status fisherStat = new Status(21, 10);
		fisherStat.setLocation(SupportTesting.createLocation(ASSET.Util.RandomGenerator.nextRandom() * 20000,
				ASSET.Util.RandomGenerator.nextRandom() * 20000));
		fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
		fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
		fisher3.setStatus(fisherStat);
		fisher3.setDecisionModel(fishWander);
		fisher3.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
		// cs.addParticipant(fisher3.getId(), fisher3);

		fisher3 = new Surface(124);
		fisher3.setName("Safe_fisher_2");
		fisher3.setCategory(
				new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		fisherStat = new Status(21, 10);
		fisherStat.setLocation(SupportTesting.createLocation(ASSET.Util.RandomGenerator.nextRandom() * 20000,
				ASSET.Util.RandomGenerator.nextRandom() * 20000));
		fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
		fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
		fisher3.setStatus(fisherStat);
		fisher3.setDecisionModel(fishWander);
		fisher3.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
		// cs.addParticipant(fisher3.getId(), fisher3);

		// ok. just do a couple of steps, to check how things pan out.
		cs.step();

		System.out.println("started at:" + cs.getTime());

		// and the other observers
		final DebriefReplayObserver dro = new DebriefReplayObserver("test_reports", "investigate_tracks.rep", false,
				false, true, null, "plotter", true, null);
		final DebriefReplayObserver dr2 = new DebriefReplayObserver("test_reports",
				"investigate_helo_decisions.rep", false, true, false, new TargetType(Category.Type.HELO),
				"decisions", true, null);
		final TrackPlotObserver tpo = new TrackPlotObserver("test_reports", 300, 400, "investigate_mult_search.png",
				null, false, true, false, "tester", true);

		dro.setup(cs);
		dr2.setup(cs);
		tpo.setup(cs);

		dro.outputThisArea(theArea);
		dro.outputTheseLocations(ladder.getRoute());

		// ok. we've found it. check that we do transition to detected
		int counter = 0;
		while ((counter++ < 1200)) {
			cs.step();
		}

		dro.tearDown(cs);
		dr2.tearDown(cs);
		tpo.tearDown(cs);

		// hack: suspect we shouldn't be allowing relaxed number of remembered
		// contacts.
		final InvestigateStore theStore = Investigate._invData.firstStore();
		assertEquals(3, theStore.countDoneTargets(), "remembered contacts");

		// check we've found one or more of correct targets
		assertTrue(theStore.hasBeenDone(15), "we haven't found hostile target");
		assertTrue(theStore.hasBeenDone(10), "we haven't found hostile target");
		// assertTrue("we haven't found hostile target",
		// investigate._targetsDone.contains(new Integer(11)));

		// also check that we haven't detected the friendly targets
		assertTrue(!theStore.hasBeenDone(123), "we've found friendly target");

		// so, we should have cleared our tartget
		// assertNull("still have link to target",
		// investigate._invData.get(theMonitor).getCurrentTarget(12));

	}

	@Test
	public void testStatic() {
		final TargetType theTarget = new TargetType(Category.Force.RED);
		final Investigate investigate = new Investigate("investigating red targets", theTarget,
				DetectionEvent.IDENTIFIED, null);

		final int TGT_ID = 567;
		final int NOT_TGT_ID = 123;

		final Status myStat = new Status(12, 0);
		myStat.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
		myStat.setLocation(new WorldLocation(2, 2, 2));

		final Status tgtStat = new Status(222, 0);
		tgtStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		tgtStat.setLocation(new WorldLocation(2, 2, 2));
		tgtStat.setCourse(22);

		final MovementCharacteristics theChars = HeloMovementCharacteristics.getSampleChars();
		final SimpleDemandedStatus theDemStat = new SimpleDemandedStatus(12, 12000);
		DetectionList theDetections = null;

		final LookupSensor duffSensor = new RadarLookupSensor(12, 0, 0, 0, 0, null, 0, null, 0);
		final Surface target = new Surface(TGT_ID, null, null, "fisher");
		target.setStatus(tgtStat);

		final ScenarioActivityMonitor theMonitor = new ScenarioActivityMonitor() {
			@Override
			public void createParticipant(final ParticipantType newPart) {
			}

			@Override
			public void detonationAt(final int id, final WorldLocation loc, final double power) {
			}

			@Override
			public Integer[] getListOfParticipants() {
				return null;
			}

			@Override
			public ParticipantType getThisParticipant(final int id) {
				return target;
			}
		};

		DemandedStatus res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		SimpleDemandedStatus simple = (SimpleDemandedStatus) res;
		assertNull(res, "null dem stat when null detections");

		// ok. now use zero length detections
		theDetections = new DetectionList();
		final WorldDistance dist1 = new WorldDistance(1, WorldDistance.MINUTES);

		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when empty detections");

		// and put in an invalid detection
		final Category tgtCategory = new Category(Category.Force.BLUE, Category.Environment.SURFACE,
				Category.Type.FISHING_VESSEL);
		final double target_brg = 12;
		final DetectionEvent de = new DetectionEvent(1200, 12, null, duffSensor, dist1, null, new Float(target_brg),
				null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target, DetectionEvent.DETECTED);
		theDetections.add(de);

		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when invalid target");
		final InvestigateStore theStore = Investigate._invData.get(theMonitor, 12,
				investigate.isCollaborativeSearch());
		assertNull(theStore.getCurrentTarget(12), "inv target still empty");

		// now try a valid target
		tgtCategory.setForce(Category.Force.RED);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");

		assertNotNull(theStore.getCurrentTarget(12), "inv target not still empty");

		// ok. let's lose the target and see what happens
		theDetections.clear();
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "null dem stat when target lost");
		assertNotNull(theStore.getCurrentTarget(12), "remembered target");
		assertEquals(TGT_ID, theStore.getCurrentTarget(12).intValue(), "got correct tgt id");

		// and offer another target
		theDetections.add(de);
		de.setTarget(NOT_TGT_ID);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "new dem stat when valid target");
		assertEquals(NOT_TGT_ID, theStore.getCurrentTarget(12).intValue(), "got new tgt id");

		// back to our target
		de.setTarget(TGT_ID);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		simple = (SimpleDemandedStatus) res;
		assertNotNull(res, "new dem stat when valid target");
		assertTrue(simple.getCourse() > 0, "on a good bearing");
		assertEquals(TGT_ID, theStore.getCurrentTarget(12).intValue(), "got new tgt id");

		// check that we can tick off target when found
		de.setDetectionState(DetectionEvent.CLASSIFIED);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNotNull(res, "dem stat when valid target");
		assertEquals(TGT_ID, theStore.getCurrentTarget(12).intValue(), "got new tgt id");

		// check that we can tick off target when found
		de.setDetectionState(DetectionEvent.IDENTIFIED);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "no dem status");
		assertNull(theStore.getCurrentTarget(12), "ditched current target");
		assertEquals(1, theStore.countDoneTargets(), "got something in found targets");

		// check that we can tick off target when found
		de.setDetectionState(DetectionEvent.CLASSIFIED);
		res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
		assertNull(res, "no dem status when only existing target found");
		assertNull(theStore.getCurrentTarget(12), "ditched current target");
		assertEquals(1, theStore.countDoneTargets(), "still got something in found targets");

	}

	// public void testReadFromCommandLine()
	// {
	// String test_path = "../src/java/ASSET_SRC/ASSET/Util/MonteCarlo/";
	// String scen_file = test_path + "test_variance_scenario_area.xml";
	// String var_file = test_path + "test_variance_area.xml";
	//
	// CommandLine.main(new String[]{" " + scen_file, var_file});
	// }
	//

}
