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

import static ASSET.Models.Movement.MakeWaypoint.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ASSET.Models.Movement.MakeWaypoint.*;
import java.awt.geom.Point2D;
import ASSET.Models.Decision.Movement.TransitWaypoint;
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
public class MakeWaypointTest extends SupportTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testScenario() {
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
		loc3.setDepth(-300);
		final WorldLocation loc4 = SupportTesting.createLocation(4000, 3500);
		loc4.setDepth(-300);
		final WorldLocation loc5 = SupportTesting.createLocation(3000, 900);
		loc5.setDepth(-300);
		final WorldLocation loc6 = SupportTesting.createLocation(3500, 2900);
		loc6.setDepth(-300);
		final WorldLocation loc7 = SupportTesting.createLocation(4000, 3000);
		loc7.setDepth(-300);
		final WorldPath destinations = new WorldPath();
		destinations.addPoint(loc1);
		destinations.addPoint(loc2);
		destinations.addPoint(loc3);
		destinations.addPoint(loc4);
		destinations.addPoint(loc5);
		destinations.addPoint(loc6);
		destinations.addPoint(loc7);

		final ASSET.Models.Decision.Movement.TransitWaypoint transit = new TransitWaypoint(destinations,
				new WorldSpeed(12, WorldSpeed.M_sec), false, WaypointVisitor.createVisitor(OnTopWaypoint._myType));

		helo.setDecisionModel(transit);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);

		cs.addParticipant(12, helo);

		////////////////////////////////////////////////////////////
		// add in our various listeners
		////////////////////////////////////////////////////////////
		super.startListeningTo(helo, "makeWaypoint", true, true, true, cs);

		final long timeLimit = 12 * 100000;
		final long startTime = cs.getTime();
		long elapsed = cs.getTime() - startTime;
		while (elapsed < timeLimit) {
			cs.step();
			elapsed = cs.getTime() - startTime;
		}

		super.endRecording(cs);

		// also output the series of locations to replay file
		super.outputTheseToRep("makeWaypoint_pts.rep", destinations);

	}

	@Test
	public void testScenarioChangeHeight() {
		WorldLocation.setModel(new CompletelyFlatEarth());

		// setup ownship
		final Status startStat = new Status(12, 10);
		final WorldLocation origin = SupportTesting.createLocation(0, 0);
		origin.setDepth(-300);
		startStat.setLocation(origin);
		startStat.setCourse(0);
		startStat.setSpeed(new WorldSpeed(52, WorldSpeed.M_sec));

		final Helo helo = new Helo(12);
		helo.setName("Merlin_Test");

		// and now the movement chars
		final MovementCharacteristics moves = new ASSET.Models.Movement.HeloMovementCharacteristics("merlin",
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec), 0,
				new WorldSpeed(200, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldDistance(3000, WorldDistance.YARDS), new WorldDistance(30, WorldDistance.YARDS), 3,
				new WorldSpeed(10, WorldSpeed.Kts), new WorldSpeed(60, WorldSpeed.Kts));
		helo.setMovementChars(moves);
		helo.setStatus(startStat);
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

		// and create the behaviour
		final WorldLocation loc1 = SupportTesting.createLocation(20000, 16000);
		loc1.setDepth(-300);
		final WorldLocation loc2 = SupportTesting.createLocation(30000, 10000);
		loc2.setDepth(-100);
		final WorldLocation loc3 = SupportTesting.createLocation(30000, 30000);
		loc3.setDepth(-300);
		final WorldLocation loc4 = SupportTesting.createLocation(40000, 35000);
		loc4.setDepth(-1200);
		final WorldLocation loc5 = SupportTesting.createLocation(30000, 9000);
		loc5.setDepth(-700);
		final WorldLocation loc6 = SupportTesting.createLocation(35000, 29000);
		loc6.setDepth(-300);
		final WorldPath destinations = new WorldPath();
		destinations.addPoint(loc1);
		destinations.addPoint(loc2);
		destinations.addPoint(loc3);
		destinations.addPoint(loc4);
		destinations.addPoint(loc5);
		destinations.addPoint(loc6);

		final ASSET.Models.Decision.Movement.TransitWaypoint transit = new TransitWaypoint(destinations,
				new WorldSpeed(52, WorldSpeed.M_sec), false, WaypointVisitor.createVisitor(OnTopWaypoint._myType));

		helo.setDecisionModel(transit);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);

		cs.addParticipant(52, helo);

		////////////////////////////////////////////////////////////
		// add in our various listeners
		////////////////////////////////////////////////////////////
		super.startListeningTo(helo, "makeWaypointHeight", true, true, true, cs);

		final long timeLimit = 18 * 100000;
		final long startTime = cs.getTime();
		long elapsed = cs.getTime() - startTime;
		while (elapsed < timeLimit) {
			cs.step();
			elapsed = cs.getTime() - startTime;
		}

		super.endRecording(cs);

		// also output the series of locations to replay file
		super.outputTheseToRep("makeWaypointHeight_pts.rep", destinations);

	}

	@Test
	public void testScenarioChangeSpd() {
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
		loc3.setDepth(-300);
		final WorldLocation loc4 = SupportTesting.createLocation(4000, 3500);
		loc4.setDepth(-300);
		final WorldLocation loc5 = SupportTesting.createLocation(3000, 900);
		loc5.setDepth(-300);
		final WorldLocation loc6 = SupportTesting.createLocation(3500, 2900);
		loc6.setDepth(-300);
		final WorldPath destinations = new WorldPath();
		destinations.addPoint(loc1);
		destinations.addPoint(loc2);
		destinations.addPoint(loc3);
		destinations.addPoint(loc4);
		destinations.addPoint(loc5);
		destinations.addPoint(loc6);

		final ASSET.Models.Decision.Movement.TransitWaypoint transit = new TransitWaypoint(destinations,
				new WorldSpeed(19, WorldSpeed.M_sec), false, WaypointVisitor.createVisitor(OnTopWaypoint._myType));

		helo.setDecisionModel(transit);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);

		cs.addParticipant(12, helo);

		////////////////////////////////////////////////////////////
		// add in our various listeners
		////////////////////////////////////////////////////////////
		super.startListeningTo(helo, "makeWaypointSpd", true, true, true, cs);

		final long timeLimit = 12 * 100000;
		final long startTime = cs.getTime();
		long elapsed = cs.getTime() - startTime;
		while (elapsed < timeLimit) {
			cs.step();
			elapsed = cs.getTime() - startTime;
		}

		super.endRecording(cs);

		// also output the series of locations to replay file
		super.outputTheseToRep("makeWaypointSpd_pts.rep", destinations);

	}

	@Test
	public void testScenarioMissingTurn() {
		WorldLocation.setModel(new CompletelyFlatEarth());

		// setup ownship
		final Status startStat = new Status(12, 10);
		final WorldLocation origin = SupportTesting.createLocation(2000, 2200);
		origin.setDepth(-300);
		startStat.setLocation(origin);
		startStat.setCourse(65);
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
		loc3.setDepth(-300);
		final WorldLocation loc4 = SupportTesting.createLocation(4000, 3500);
		loc4.setDepth(-300);
		final WorldLocation loc5 = SupportTesting.createLocation(3000, 900);
		loc5.setDepth(-300);
		final WorldLocation loc6 = SupportTesting.createLocation(3500, 2900);
		loc6.setDepth(-300);
		final WorldPath destinations = new WorldPath();
		destinations.addPoint(loc1);
		destinations.addPoint(loc4);
		destinations.addPoint(loc2);
		destinations.addPoint(loc5);
		destinations.addPoint(loc3);
		destinations.addPoint(loc6);

		final ASSET.Models.Decision.Movement.TransitWaypoint transit = new TransitWaypoint(destinations,
				new WorldSpeed(12, WorldSpeed.M_sec), false, WaypointVisitor.createVisitor(OnTopWaypoint._myType));

		helo.setDecisionModel(transit);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);

		cs.addParticipant(12, helo);

		////////////////////////////////////////////////////////////
		// add in our various listeners
		////////////////////////////////////////////////////////////
		super.startListeningTo(helo, "makeWaypointMissingPoint", true, true, true, cs);

		final long timeLimit = 13 * 100000;
		final long startTime = cs.getTime();
		long elapsed = cs.getTime() - startTime;
		while (elapsed < timeLimit) {
			cs.step();
			elapsed = cs.getTime() - startTime;
		}

		super.endRecording(cs);

		// also output the series of locations to replay file
		super.outputTheseToRep("makeWaypointMissingPoint_pts.rep", destinations);

	}

	@Test
	public void testSolution() {
		WorldLocation.setModel(new CompletelyFlatEarth());

		// ok, run through the first test case.
		final WorldLocation start = SupportTesting.createLocation(0, 0);
		WorldLocation thisWaypoint = SupportTesting.createLocation(50, 50);
		WorldLocation nextWaypoint = SupportTesting.createLocation(50, 0);

		final Status startStat = new Status(12, 10);
		startStat.setLocation(start);
		startStat.setCourse(45);
		startStat.setSpeed(new WorldSpeed(3 * Math.PI / 20, WorldSpeed.M_sec));

		// and now the movement chars
		final MovementCharacteristics moves = new ASSET.Models.Movement.HeloMovementCharacteristics("merlin",
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec), 0,
				new WorldSpeed(200, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldDistance(3000, WorldDistance.YARDS), new WorldDistance(30, WorldDistance.YARDS), 3,
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(60, WorldSpeed.Kts));

		final MakeWaypoint mw = new MakeWaypoint();
		final MakeWaypointSolution sol = mw.getMakeWaypointSolution(startStat, moves, thisWaypoint, nextWaypoint,
				1000, null);

		// and the result
		assertNotNull(sol, "yes, it's posslble");
		assertEquals(48.98, sol.remainingDist.doubleValue(), 0.01, "correct distance to run");
		assertEquals(180, sol.outgoingCourse.doubleValue(), 0.01, "correct outgoing course");
		assertEquals(135, sol.demandedCourseChange.doubleValue(), 0.01, "correct turn amount");

		// hey, let's have another go - where the point is on the other side of us
		thisWaypoint = SupportTesting.createLocation(-50, 50);
		nextWaypoint = SupportTesting.createLocation(-50, 0);
		startStat.setCourse(315);

		final MakeWaypointSolution sol2 = mw.getMakeWaypointSolution(startStat, moves, thisWaypoint, nextWaypoint,
				1000, null);

		// and the result
		assertNotNull(sol2, "yes, it's posslble");
		assertEquals(48.98, sol2.remainingDist.doubleValue(), 0.01, "correct distance to run");
		assertEquals(180, sol2.outgoingCourse.doubleValue(), 0.01, "correct outgoing course");
		assertEquals(-135, sol2.demandedCourseChange.doubleValue(), 0.01, "correct turn amount");

		// hey, let's have another go - where the point is on the other side of us
		thisWaypoint = SupportTesting.createLocation(-50, 50);
		nextWaypoint = SupportTesting.createLocation(-150, 50);
		startStat.setCourse(270);

		final MakeWaypointSolution sol3 = mw.getMakeWaypointSolution(startStat, moves, thisWaypoint, nextWaypoint,
				1000, null);

		// and the result
		assertNotNull(sol3, "yes, it's posslble");
		assertEquals(66.98, sol3.remainingDist.doubleValue(), 0.01, "correct distance to run");
		assertEquals(270, sol3.outgoingCourse.doubleValue(), 0.01, "correct outgoing course");
		assertEquals(-45, sol3.demandedCourseChange.doubleValue(), 0.01, "correct turn amount");

	}

}
