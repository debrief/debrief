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

import static ASSET.Models.Movement.OnTopWaypoint.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ASSET.Models.Movement.OnTopWaypoint.*;
import java.awt.geom.Point2D;
import ASSET.ParticipantType;
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
public class OnTopWaypointTest extends SupportTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	// TODO FIX-TEST
	public void NtestOnTopRoute() {
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
		loc3.setDepth(-100);
		final WorldLocation loc4 = SupportTesting.createLocation(4310, 1326);
		loc4.setDepth(-300);
		final WorldLocation loc5 = SupportTesting.createLocation(1310, 2326);
		loc5.setDepth(-300);
		final WorldLocation loc6 = SupportTesting.createLocation(2310, 326);
		loc6.setDepth(-1300);
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
				new WorldSpeed(12, WorldSpeed.M_sec), false, WaypointVisitor.createVisitor(OnTopWaypoint._myType));

		helo.setDecisionModel(transit);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(3000);
		cs.addParticipant(12, helo);

		////////////////////////////////////////////////////////////
		// add in our various listeners
		////////////////////////////////////////////////////////////
		final boolean recordData = true;
		super.startListeningTo(helo, "on_top", recordData, recordData, recordData, cs);

		final long stepLimit = 800;
		for (int i = 0; i < stepLimit; i++) {
			cs.step();
		}

		super.endRecording(cs);

		// also output the series of locations to replay file
		super.outputTheseToRep("on_top_points.rep", destinations);

		// check we're on the correct final course
		assertEquals(46, helo.getStatus().getCourse(), 1, "on correct course");

		// check we're at the correct finish point
		final WorldLocation endPoint = new WorldLocation(0.0776899319, 0.089914902, 0);
		double rng = helo.getStatus().getLocation().subtract(endPoint).getRange();
		rng = MWC.Algorithms.Conversions.Degs2m(rng);
		assertEquals(0, rng, 1, "at correct end point:" + helo.getStatus().getLocation().getLat() + ","
				+ helo.getStatus().getLocation().getLong());

		// also output the series of locations to replay file
		super.outputTheseToRep("on_top_points.rep", destinations);

	}

	@Test
	public void testCases() {

		// and now the movement chars
		final MovementCharacteristics moves = new ASSET.Models.Movement.HeloMovementCharacteristics("merlin",
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec), 0,
				new WorldSpeed(200, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldDistance(3000, WorldDistance.YARDS), new WorldDistance(30, WorldDistance.YARDS), 3,
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(60, WorldSpeed.Kts));

		////////////////////////////////////////////////////////////
		// CASE 1
		////////////////////////////////////////////////////////////
		final CaseSolver firstCase = new CaseSolver() {
			public double alpha1(final double hyp, final double rad1, final double hDelta, final double kDelta) {
				return Math.asin(rad1 / hyp) + Math.atan2(hDelta, -kDelta);
			}

			public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() - radius * Math.cos(courseRads);
				final double yVal = point.getY() + radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			public OnTopSolution populateResults(final double initialTurnDegs, final double zLen,
					final boolean isPossible, final double totalTime) {
				return new OnTopSolution(-initialTurnDegs, zLen, isPossible, totalTime);
			}

			public double theta1(final double alpha1, final double p1) {
				return alpha1 - p1;
			}

			public double zLength(final double hDelta, final double alpha1, final double rad1) {
				return Math.abs((hDelta + rad1 * Math.cos(alpha1)) / Math.sin(alpha1));
			}
		};

		////////////////////////////////////////////////////////////
		// CASE 2
		////////////////////////////////////////////////////////////
		final CaseSolver secondCase = new CaseSolver() {
			public double alpha1(final double hyp, final double rad1, final double hDelta, final double kDelta) {
				return Math.asin(-rad1 / hyp) - Math.atan2(hDelta, kDelta);
			}

			// public Point2D secondOrigin(Point2D point,
			// double radius,
			// double courseRads)
			// {
			// double xVal = point.getX() - radius * Math.cos(courseRads);
			// double yVal = point.getY() + radius * Math.sin(courseRads);
			// return new Point2D.Double(xVal, yVal);
			// }

			public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() + radius * Math.cos(courseRads);
				final double yVal = point.getY() - radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			public OnTopSolution populateResults(final double initialTurnDegs, final double zLen,
					final boolean isPossible, final double totalTime) {
				return new OnTopSolution(initialTurnDegs, zLen, isPossible, totalTime);
			}

			public double theta1(final double alpha1, final double p1) {
				return p1 - alpha1;
			}

			public double zLength(final double hDelta, final double alpha1, final double rad1) {
				return Math.abs((-hDelta - rad1 * Math.cos(alpha1)) / Math.sin(alpha1));
			}

		};

		final Point2D p1 = new Point2D.Double(-15, 5);
		final Point2D p2 = new Point2D.Double(20, 5);
		final double c1r = 0;
		final double spd1 = Math.PI / 12;
		final double spd2 = spd1;
		// double rt = 3;
		final double r1 = 5;

		final OnTopSolution testOne = OnTopWaypoint.calcCombination(p1, p2, firstCase, c1r, r1, spd1, spd2, moves);

		assertNotNull(testOne, "found a solution");
		assertEquals(-277.1807, testOne.demandedCourseChange.doubleValue(), 0.01, "correct demanded turn");
		assertEquals(39.686, testOne.distanceToRun.doubleValue(), 0.01, "correct distance");

		final OnTopSolution testTwo = OnTopWaypoint.calcCombination(p1, p2, secondCase, c1r, r1, spd1, spd2, moves);

		assertNotNull(testTwo, "found a solution");
		assertEquals(99.59, testTwo.demandedCourseChange.doubleValue(), 0.01, "correct demanded turn");
		assertEquals(29.58, testTwo.distanceToRun.doubleValue(), 0.01, "correct distance");

	}

}
