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

package ASSET.Models.Movement;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Models.Movement.DirectedOnTopWaypoint.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.Models.Movement.DirectedOnTopWaypoint.*;
import java.awt.geom.Point2D;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.Movement.TransitWaypoint;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class DirectedOnTopWaypointTest extends SupportTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testCaseOne() {
		WorldLocation.setModel(new CompletelyFlatEarth());

		// ok, run through the first test case.
		final WorldLocation loc1 = SupportTesting.createLocation(29, 10);
		final WorldLocation loc2 = SupportTesting.createLocation(30, -26);
		final Status startStat = new Status(12, 10);
		startStat.setLocation(loc1);
		startStat.setCourse(0);
		startStat.setSpeed(new WorldSpeed(3 * Math.PI / 20, WorldSpeed.M_sec));

		final Status endStat = new Status(13, 10);
		endStat.setLocation(loc2);
		endStat.setCourse(90);
		endStat.setSpeed(new WorldSpeed(Math.PI / 5, WorldSpeed.M_sec));

		// and now the movement chars
		final MovementCharacteristics moves = new ASSET.Models.Movement.HeloMovementCharacteristics("merlin",
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec), 0,
				new WorldSpeed(200, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldDistance(3000, WorldDistance.YARDS), new WorldDistance(30, WorldDistance.YARDS), 3,
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(60, WorldSpeed.Kts));

		final TurnType winner = getTurn(startStat, endStat, moves);

		// and the result
		assertEquals(true, winner._isPossible, "yes, it's posslble");

		// check it's case 2
		// assertEquals("correct theta 1", 203.9, winner._initialTurn.doubleValue(),
		// 0.1);
		// assertEquals("correct theta 2", 66.1, winner._finalTurn.doubleValue(), 0.1);
		// assertEquals("correct straight dist", 31.48,
		// winner._straightCourseTime.doubleValue(),
		// 0.1);
	}

	@Test
	public void testClipHeightChangeTime() {
		final String myName = "Merlin Trial";
		final double accelRate = 5;
		final double decelRate = 10;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 20;
		final double maxHeight = 0;
		final double minHeight = -400;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final WorldSpeed curSpeed = new WorldSpeed(10, WorldSpeed.M_sec);
		final double heightDelta = 10;
		long time = WaypointVisitor.calcClippedHeightChangeTimeFor(curSpeed, heightDelta, chars, 20000, 500);
		assertEquals(20000, time, "correct clipped time returned");

		// ok, try again, but when we are closer to the target location
		time = WaypointVisitor.calcClippedHeightChangeTimeFor(curSpeed, heightDelta, chars, 20000, 200);
		assertEquals(13500, time, "correct clipped time returned");

	}

	@Test
	public void testClippedAccelTime() {

		final String myName = "Merlin Trial";
		final double accelRate = 5;
		final double decelRate = 10;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 20;
		final double maxHeight = 0;
		final double minHeight = -400;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final WorldSpeed curSpeed = new WorldSpeed(10, WorldSpeed.M_sec);
		final WorldSpeed demSpeed = new WorldSpeed(30, WorldSpeed.M_sec);
		long time = WaypointVisitor.calcTrimmedAccelTime(demSpeed, curSpeed, chars.getAccelRate(), 3000, 300);
		assertEquals(3000, time, "correct clipped time returned");
		time = WaypointVisitor.calcTrimmedAccelTime(demSpeed, curSpeed, chars.getAccelRate(), 3000, 30);
		assertEquals(2000, time, "correct clipped time returned");
		time = WaypointVisitor.calcTrimmedAccelTime(curSpeed, demSpeed, chars.getDecelRate(), 3000, 40);
		assertEquals(2000, time, "correct clipped time returned");

	}

	@Test
	public void testStraightCourseBit() {
		WorldLocation.setModel(new CompletelyFlatEarth());

		// setup ownship
		final Status startStat = new Status(12, 10);
		final WorldLocation origin = SupportTesting.createLocation(0, 0);
		origin.setDepth(-300);
		startStat.setLocation(origin);
		startStat.setCourse(0);
		startStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		final Helo helo = new Helo(12);
		helo.setName("Merlin_Test");

		// and now the movement chars
		final MovementCharacteristics moves = new ASSET.Models.Movement.HeloMovementCharacteristics("merlin",
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec), 0,
				new WorldSpeed(200, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldDistance(3000, WorldDistance.YARDS), new WorldDistance(30, WorldDistance.YARDS), 3,
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(60, WorldSpeed.Kts));
		helo.setMovementChars(moves);
		helo.setStatus(startStat);
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

		// and create the behaviour
		final WorldLocation loc1 = SupportTesting.createLocation(1000, 1000);
		loc1.setDepth(-300);
		final WorldLocation loc2 = SupportTesting.createLocation(1610, 350);
		loc2.setDepth(-900);
		final WorldLocation loc3 = SupportTesting.createLocation(3000, 1000);
		loc3.setDepth(-300);
		final WorldLocation loc4 = SupportTesting.createLocation(4310, 1326);
		loc4.setDepth(-300);
		final WorldLocation loc5 = SupportTesting.createLocation(3000, 4000);
		loc5.setDepth(-100);
		final WorldLocation loc6 = SupportTesting.createLocation(3090, 5010);
		loc6.setDepth(-500);
		final WorldLocation loc7 = SupportTesting.createLocation(2000, 2100);
		loc7.setDepth(-400);
		final WorldLocation loc8 = SupportTesting.createLocation(3310, 1626);
		loc8.setDepth(-30);

		final WorldPath destinations = new WorldPath();
		destinations.addPoint(loc1);
		destinations.addPoint(loc2);
		destinations.addPoint(loc3);
		destinations.addPoint(loc4);
		destinations.addPoint(loc5);
		destinations.addPoint(loc6);
		destinations.addPoint(loc7);
		destinations.addPoint(loc8);

		final ASSET.Models.Decision.Movement.TransitWaypoint transit = new TransitWaypoint(destinations,
				new WorldSpeed(12, WorldSpeed.M_sec), false,
				WaypointVisitor.createVisitor(DirectedOnTopWaypoint._myType));

		helo.setDecisionModel(transit);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);
		cs.addParticipant(12, helo);

		////////////////////////////////////////////////////////////
		// add in our various listeners
		////////////////////////////////////////////////////////////

		final boolean doRecording = true;

		super.startListeningTo(helo, "directed_on_top_Straight", doRecording, doRecording, doRecording, cs);

		final long stepLimit = 2400;
		for (int i = 0; i < stepLimit; i++) {
			cs.step();
		}

		// todo: extend these tests

		super.endRecording(cs);

		// also output the series of locations to replay file
		super.outputTheseToRep("directed_points_straight.rep", destinations);

		// so, check that we're now on the last point
		assertTrue(transit.isFinished(), "we reached the last point");

		// also check that we're on the correct height
		assertEquals(helo.getStatus().getLocation().getDepth(), -30, 0.0001, "on correct final height");

		// and that we're at the correct speed
		assertEquals(helo.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec), 12, 0.001, "at correct speed");

		assertEquals(-0.0271, helo.getStatus().getLocation().getLat(), 0.001, "at correct lat");
		assertEquals(0.14571, helo.getStatus().getLocation().getLong(), 0.0001, "at correct long");

	}

	@Test
	public void testTurnManagement() {
		WorldLocation.setModel(new CompletelyFlatEarth());

		// setup ownship
		final Status startStat = new Status(12, 10);
		final WorldLocation origin = SupportTesting.createLocation(0, 0);
		origin.setDepth(-300);
		startStat.setLocation(origin);
		startStat.setCourse(0);
		startStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		final Helo helo = new Helo(12);
		helo.setName("Merlin_Test");

		// and now the movement chars
		final MovementCharacteristics moves = new ASSET.Models.Movement.HeloMovementCharacteristics("merlin",
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec), 0,
				new WorldSpeed(200, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldDistance(3000, WorldDistance.YARDS), new WorldDistance(30, WorldDistance.YARDS), 3,
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(60, WorldSpeed.Kts));
		helo.setMovementChars(moves);
		helo.setStatus(startStat);
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

		// and create the behaviour
		final WorldLocation loc1 = SupportTesting.createLocation(2099, 1099);
		loc1.setDepth(-300);
		final WorldLocation loc2 = SupportTesting.createLocation(2330, -2694);
		loc2.setDepth(-300);
		final WorldLocation loc3 = SupportTesting.createLocation(3120, -123);
		loc3.setDepth(-300);
		final WorldLocation loc4 = SupportTesting.createLocation(4310, 1326);
		loc4.setDepth(-300);
		final WorldLocation loc5 = SupportTesting.createLocation(1310, 2326);
		loc5.setDepth(-300);
		final WorldLocation loc6 = SupportTesting.createLocation(2310, 326);
		loc6.setDepth(-300);
		final WorldLocation loc7 = SupportTesting.createLocation(3310, 2326);
		loc7.setDepth(-300);
		final WorldLocation loc8 = SupportTesting.createLocation(4310, 3326);
		loc8.setDepth(-300);
		final WorldPath destinations = new WorldPath();
		destinations.addPoint(loc1);
		destinations.addPoint(loc2);
		destinations.addPoint(loc3);
		destinations.addPoint(loc4);
		destinations.addPoint(loc5);
		destinations.addPoint(loc6);
		destinations.addPoint(loc7);
		destinations.addPoint(loc8);

		final ASSET.Models.Decision.Movement.TransitWaypoint transit = new TransitWaypoint(destinations,
				new WorldSpeed(12, WorldSpeed.M_sec), false,
				WaypointVisitor.createVisitor(DirectedOnTopWaypoint._myType));

		helo.setDecisionModel(transit);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(3000);
		cs.addParticipant(12, helo);

		////////////////////////////////////////////////////////////
		// add in our various listeners
		////////////////////////////////////////////////////////////
		super.startListeningTo(helo, "directed_on_top", true, true, false, cs);

		final long stepLimit = 800;
		for (int i = 0; i < stepLimit; i++) {
			cs.step();
		}

		super.endRecording(cs);

		// also output the series of locations to replay file
		super.outputTheseToRep("directed_points.rep", destinations);

	}

	@Test
	public void testTurnManagement2() {
		WorldLocation.setModel(new CompletelyFlatEarth());

		// setup ownship
		final Status startStat = new Status(12, 10);
		final WorldLocation origin = SupportTesting.createLocation(0, 0);
		origin.setDepth(-300);
		startStat.setLocation(origin);
		startStat.setCourse(0);
		startStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		final Helo helo = new Helo(12);
		helo.setName("Merlin_Test");

		// and now the movement chars
		final MovementCharacteristics moves = new ASSET.Models.Movement.HeloMovementCharacteristics("merlin",
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec), 0,
				new WorldSpeed(200, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldDistance(3000, WorldDistance.YARDS), new WorldDistance(30, WorldDistance.YARDS), 3,
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(60, WorldSpeed.Kts));
		helo.setMovementChars(moves);
		helo.setStatus(startStat);
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

		// and create the behaviour
		final WorldLocation loc1 = SupportTesting.createLocation(2000, 1600);
		loc1.setDepth(-300);
		final WorldLocation loc2 = SupportTesting.createLocation(3000, 1000);
		loc2.setDepth(-300);
		final WorldLocation loc3 = SupportTesting.createLocation(3000, 3000);
		loc3.setDepth(-100);
		final WorldLocation loc4 = SupportTesting.createLocation(4000, 3500);
		loc4.setDepth(-300);
		final WorldLocation loc5 = SupportTesting.createLocation(3000, 2900);
		loc5.setDepth(-300);
		final WorldLocation loc6 = SupportTesting.createLocation(3000, 900);
		loc6.setDepth(-300);
		final WorldPath destinations = new WorldPath();
		destinations.addPoint(loc1);
		destinations.addPoint(loc2);
		destinations.addPoint(loc3);
		destinations.addPoint(loc4);
		destinations.addPoint(loc5);
		destinations.addPoint(loc6);

		final ASSET.Models.Decision.Movement.TransitWaypoint transit = new TransitWaypoint(destinations,
				new WorldSpeed(12, WorldSpeed.M_sec), false,
				WaypointVisitor.createVisitor(DirectedOnTopWaypoint._myType));

		final WorldLocation wanderCentre = new WorldLocation(SupportTesting.createLocation(4000, 500));
		wanderCentre.setDepth(-300);
		final Wander wandering = new Wander(wanderCentre, new WorldDistance(3000, WorldDistance.METRES));
		final Sequence theSequence = new Sequence();
		theSequence.insertAtFoot(transit);
		theSequence.insertAtFoot(wandering);

		helo.setDecisionModel(theSequence);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);

		cs.addParticipant(12, helo);

		////////////////////////////////////////////////////////////
		// add in our various listeners
		////////////////////////////////////////////////////////////
		super.startListeningTo(helo, "directed_on_top", true, true, true, cs);

		final long timeLimit = 12 * 100000;
		final long startTime = cs.getTime();
		long elapsed = cs.getTime() - startTime;
		while (elapsed < timeLimit) {
			cs.step();
			elapsed = cs.getTime() - startTime;
		}

		super.endRecording(cs);

		// also output the series of locations to replay file
		super.outputTheseToRep("directed_points.rep", destinations);

	}

}
