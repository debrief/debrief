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

/**
 * ASSET from PlanetMayo Ltd User: Ian.Mayo Date: 19-Aug-2003 Time: 14:50:21
 * Log: $log: $
 */
public class DirectedOnTopWaypoint extends WaypointVisitor {

	private static interface CaseSolver {
		/**
		 * what's the exit angle of the first turn
		 *
		 * @param hyp
		 * @param rad1
		 * @param rad2
		 * @param hDelta
		 * @param kDelta
		 * @return
		 */
		public double alpha1(double hyp, double rad1, double rad2, double hDelta, double kDelta);

		/**
		 * what's the entrance angle of the second turn
		 *
		 * @param alpha1
		 * @return
		 */
		public double alpha2(double alpha1);

		/**
		 * calculate the centre of the first turn
		 *
		 * @param point
		 * @param radius
		 * @param courseRads
		 * @return
		 */
		public Point2D firstOrigin(Point2D point, double radius, double courseRads);

		/**
		 * populate the final results object (including setting the direction of the
		 * turns
		 *
		 * @param initialTurnDegs the initial turn we have to make
		 * @param zLen            how far to travel in a straight line
		 * @param finalTurnDegs   the final turn we have to make
		 * @param isPossible      whether the turn is possible
		 * @param totalTime       the total time for this combination
		 * @return
		 */
		TurnType populateResults(double initialTurnDegs, double zLen, double finalTurnDegs, boolean isPossible,
				double totalTime);

		/**
		 * calculate the centre of the second turn
		 *
		 * @param point
		 * @param radius
		 * @param courseRads
		 * @return
		 */
		public Point2D secondOrigin(Point2D point, double radius, double courseRads);

		/**
		 * what's the total angle travelled for the first turn?
		 *
		 * @param alpha1
		 * @param p1
		 * @return
		 */
		double theta1(double alpha1, double p1);

		/**
		 * what's the total angle travelled for the second turn?
		 *
		 * @param alpha2
		 * @param p2
		 * @return
		 */
		double theta2(double alpha2, double p2);

		/**
		 * how far do we travel in a straight line?
		 *
		 * @param hDelta
		 * @param alpha1
		 * @param rad1
		 * @param rad2
		 * @return
		 */
		public double zLength(double hDelta, double alpha1, double rad1, double rad2);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public class DirectedOnTopTest extends SupportTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public DirectedOnTopTest(final String val) {
			super(val);
		}

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
			assertEquals("yes, it's posslble", true, winner._isPossible);

			// check it's case 2
			// assertEquals("correct theta 1", 203.9, winner._initialTurn.doubleValue(),
			// 0.1);
			// assertEquals("correct theta 2", 66.1, winner._finalTurn.doubleValue(), 0.1);
			// assertEquals("correct straight dist", 31.48,
			// winner._straightCourseTime.doubleValue(),
			// 0.1);
		}

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
			assertEquals("correct clipped time returned", 20000, time);

			// ok, try again, but when we are closer to the target location
			time = WaypointVisitor.calcClippedHeightChangeTimeFor(curSpeed, heightDelta, chars, 20000, 200);
			assertEquals("correct clipped time returned", 13500, time);

		}

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
			assertEquals("correct clipped time returned", 3000, time);
			time = WaypointVisitor.calcTrimmedAccelTime(demSpeed, curSpeed, chars.getAccelRate(), 3000, 30);
			assertEquals("correct clipped time returned", 2000, time);
			time = WaypointVisitor.calcTrimmedAccelTime(curSpeed, demSpeed, chars.getDecelRate(), 3000, 40);
			assertEquals("correct clipped time returned", 2000, time);

		}

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
			assertTrue("we reached the last point", transit.isFinished());

			// also check that we're on the correct height
			assertEquals("on correct final height", helo.getStatus().getLocation().getDepth(), -30, 0.0001);

			// and that we're at the correct speed
			assertEquals("at correct speed", helo.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec), 12, 0.001);

			assertEquals("at correct lat", -0.0271, helo.getStatus().getLocation().getLat(), 0.001);
			assertEquals("at correct long", 0.14571, helo.getStatus().getLocation().getLong(), 0.0001);

		}

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

	////////////////////////////////////////////////////////////
	// embedded class to store the various turn combinations
	////////////////////////////////////////////////////////////
	private static class TurnType {
		/**
		 * exception defined to pass message back that the points are too close - and
		 * that we need to continue on course a little more.
		 */
		public static class PointsTooCloseException extends RuntimeException {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;
		}

		/**
		 * the initial turn (relative) we need to make (degs), set to null on
		 * completion. -ve means turn to port
		 */
		protected Double _initialTurn = null;

		/**
		 * the final turn (relative) we need to make (degs), set to null on completion.
		 * -ve means turn to port
		 */
		protected Double _finalTurn = null;

		/**
		 * the distance we travel in a straight line (m)
		 */
		protected Double _zLen = null;

		/**
		 * whether this turn is possible or not
		 */
		protected boolean _isPossible = true;

		/**
		 * whether we fail because the points are too close (the turning circles
		 * overlap)
		 */
		protected boolean _failTooClose = false;

		/**
		 * whether we fail because there isn't room for the acceleration
		 */
		protected boolean _failCantAccelerate = false;

		/**
		 * how long this particular manoeuvre takes (secs)
		 */
		protected double _thisLength;

		public TurnType(final boolean isPossible) {
			this._isPossible = isPossible;
		}

		public TurnType(final double initialTurn, final double zLen, final double finalTurn, final boolean isPossible,
				final double thisLength) {
			this._initialTurn = new Double(initialTurn);
			this._zLen = new Double(zLen);
			this._finalTurn = new Double(finalTurn);
			this._isPossible = isPossible;
			this._thisLength = thisLength;
		}

	}

	static final String _myType = "DirectedOnTop";

	public static TurnType calcCombination(final Point2D startPoint, final Point2D endPoint, final CaseSolver thisCase,
			final double course1Rads, final double course2Rads, double rad1, final double rad2, final double speed1,
			final double speed2, final MovementCharacteristics moves) {
		TurnType res = null;

		// 2. ok, first do the origins
		final Point2D firstOrigin = thisCase.firstOrigin(startPoint, rad1, course1Rads);

		// and the second origin
		final Point2D secondOrigin = thisCase.secondOrigin(endPoint, rad2, course2Rads);

		// 2a. now the h delta
		final double hDelta = secondOrigin.getX() - firstOrigin.getX();

		// and the k delta
		final double kDelta = secondOrigin.getY() - firstOrigin.getY();

		// 3. do we need to overcome the div/0 error?
		if ((rad1 == rad2) && (hDelta == kDelta)) {
			rad1 += 1e-10;
		}

		// 4. check for overlapping circles
		final double h2 = Math.pow(hDelta, 2);
		final double k2 = Math.pow(kDelta, 2);
		final double hyp = Math.sqrt(h2 + k2);
		final double totalRad = rad1 + rad2;

		if (hyp < totalRad) {
			res = new TurnType(false);
			res._failTooClose = true;
			return res;
		}

		// 5. Calc start & end turn points
		double p1 = Math.atan2(startPoint.getY() - firstOrigin.getY(), startPoint.getX() - firstOrigin.getX());
		double p2 = Math.atan2(endPoint.getY() - secondOrigin.getY(), endPoint.getX() - secondOrigin.getX());

		// and trim away
		if (p1 < 0)
			p1 += Math.PI * 2;
		if (p2 < 0)
			p2 += Math.PI * 2;

		// 6. Now get the tan points
		double alpha1 = thisCase.alpha1(hyp, rad1, rad2, hDelta, kDelta);
		double alpha2 = thisCase.alpha2(alpha1);

		// and trim them
		if (alpha1 < 0)
			alpha1 += Math.PI * 2;
		if (alpha2 < 0)
			alpha2 += Math.PI * 2;

		// 7. now the leg length
		final double zLen = thisCase.zLength(hDelta, alpha1, rad1, rad2);

		// 8. and the accel rates
		double accelRate;
		if (speed2 > speed1)
			accelRate = moves.getAccelRate().getValueIn(WorldAcceleration.M_sec_sec);
		else
			accelRate = moves.getDecelRate().getValueIn(WorldAcceleration.M_sec_sec);

		final double accelTime = (speed2 - speed1) / accelRate;

		final double len = speed1 * accelTime + accelRate * Math.pow(accelTime, 2) / 2;

		// is it possible?
		if (len > zLen) {
			res = new TurnType(false);
			res._failCantAccelerate = true;
			return res;

		}

		// 9. calc the straight time
		final double straightTime = (zLen - len) / speed2 + accelTime;

		// 10. and the turn angles
		double initialTurn = thisCase.theta1(alpha1, p1);
		double finalTurn = thisCase.theta2(alpha2, p2);

		// and do some trimming
		if (initialTurn < 0)
			initialTurn += Math.PI * 2;
		if (finalTurn < 0)
			finalTurn += Math.PI * 2;

		// 11. now the overall time
		final double firstTurnRate = moves.calculateTurnRate(speed1);
		final double secondTurnRate = moves.calculateTurnRate(speed2);

		final double time1 = initialTurn / (firstTurnRate / 180 * Math.PI);
		final double time3 = finalTurn / (secondTurnRate / 180 * Math.PI);

		final double totalTime = time1 + straightTime + time3;

		final double initialTurnDegs = MWC.Algorithms.Conversions.Rads2Degs(initialTurn);
		final double finalTurnDegs = MWC.Algorithms.Conversions.Rads2Degs(finalTurn);

		res = thisCase.populateResults(initialTurnDegs, zLen, finalTurnDegs, true, totalTime);

		return res;
	}

	public static TurnType getTurn(final Status startStat, final Status endStat,
			final ASSET.Models.Movement.MovementCharacteristics moves) throws TurnType.PointsTooCloseException {

		TurnType res = null;

		// where's the center of the area?
		final WorldArea coverage = new WorldArea(startStat.getLocation(), endStat.getLocation());
		final WorldLocation centre = coverage.getCentre();

		// and create the corners
		WorldVector thisOffset = startStat.getLocation().subtract(centre);
		double dx = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.sin(thisOffset.getBearing());
		double dy = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.cos(thisOffset.getBearing());
		final Point2D startPoint = new Point2D.Double(dx, dy);
		thisOffset = endStat.getLocation().subtract(centre);
		dx = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.sin(thisOffset.getBearing());
		dy = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.cos(thisOffset.getBearing());
		final Point2D endPoint = new Point2D.Double(dx, dy);

		double course1Rads = Math.toRadians(startStat.getCourse());
		if (course1Rads < 0)
			course1Rads += Math.PI * 2;
		double course2Rads = Math.toRadians(endStat.getCourse());
		if (course2Rads < 0)
			course2Rads += Math.PI * 2;
		final double speed1 = startStat.getSpeed().getValueIn(WorldSpeed.M_sec);
		double speed2;

		// do we have a demanded speed?
		if (endStat.getSpeed() != null) {
			// yes, ensure we head towards it.
			speed2 = endStat.getSpeed().getValueIn(WorldSpeed.M_sec);
		} else {
			// no, just continue at current speed
			speed2 = speed1;
		}

		final double spd1_rate = getTurnRateFor(moves, speed1);
		final double spd2_rate = getTurnRateFor(moves, speed2);

		final double rad1 = speed1 / spd1_rate;
		final double rad2 = speed2 / spd2_rate;

		////////////////////////////////////////////////////////////
		// HARD-CODE inputs whilst we prove alg
		////////////////////////////////////////////////////////////
		// startPoint = new Point2D.Double(29, 10);
		// endPoint = new Point2D.Double(30, -26);

		////////////////////////////////////////////////////////////
		// CASE 1
		////////////////////////////////////////////////////////////
		final CaseSolver firstCase = new CaseSolver() {
			@Override
			public double alpha1(final double hyp, final double rad1a, final double rad2a, final double hDelta,
					final double kDelta) {
				final double rad = rad1a + rad2a;
				return Math.asin(rad / hyp) - Math.atan2(hDelta, kDelta);
			}

			@Override
			public double alpha2(final double alpha1) {
				return alpha1 + Math.PI;
			}

			@Override
			public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() - radius * Math.cos(courseRads);
				final double yVal = point.getY() + radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public TurnType populateResults(final double initialTurnDegs, final double zLen, final double finalTurnDegs,
					final boolean isPossible, final double totalTime) {
				return new TurnType(-initialTurnDegs, zLen, finalTurnDegs, isPossible, totalTime);
			}

			@Override
			public Point2D secondOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() + radius * Math.cos(courseRads);
				final double yVal = point.getY() - radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public double theta1(final double alpha1, final double p1) {
				return alpha1 - p1;
			}

			@Override
			public double theta2(final double alpha2, final double p2) {
				return alpha2 - p2;
			}

			@Override
			public double zLength(final double hDelta, final double alpha1, final double rad1a, final double rad2a) {
				final double rad = rad1a + rad2a;
				return Math.abs((-hDelta + rad * Math.cos(alpha1)) / Math.sin(alpha1));
			}
		};

		////////////////////////////////////////////////////////////
		// CASE 2
		////////////////////////////////////////////////////////////
		final CaseSolver secondCase = new CaseSolver() {
			@Override
			public double alpha1(final double hyp, final double rad1a, final double rad2a, final double hDelta,
					final double kDelta) {
				return Math.asin((rad1a - rad2a) / hyp) - Math.atan2(hDelta, kDelta);
			}

			@Override
			public double alpha2(final double alpha1) {
				return alpha1;
			}

			@Override
			public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() - radius * Math.cos(courseRads);
				final double yVal = point.getY() + radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public TurnType populateResults(final double initialTurnDegs, final double zLen, final double finalTurnDegs,
					final boolean isPossible, final double totalTime) {
				return new TurnType(-initialTurnDegs, zLen, -finalTurnDegs, isPossible, totalTime);
			}

			@Override
			public Point2D secondOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() - radius * Math.cos(courseRads);
				final double yVal = point.getY() + radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public double theta1(final double alpha1, final double p1) {
				return alpha1 - p1;
			}

			@Override
			public double theta2(final double alpha2, final double p2) {
				return p2 - alpha2;
			}

			@Override
			public double zLength(final double hDelta, final double alpha1, final double rad1a, final double rad2a) {
				return Math.abs((-hDelta + (rad1a - rad2a) * Math.cos(alpha1)) / Math.sin(alpha1));
			}

		};

		////////////////////////////////////////////////////////////
		// CASE 3
		////////////////////////////////////////////////////////////
		final CaseSolver thirdCase = new CaseSolver() {
			@Override
			public double alpha1(final double hyp, final double rad1a, final double rad2a, final double hDelta,
					final double kDelta) {
				final double rad = rad1a + rad2a;
				return -Math.asin(rad / hyp) - Math.atan2(-hDelta, -kDelta);
			}

			@Override
			public double alpha2(final double alpha1) {
				return alpha1 + Math.PI;
			}

			@Override
			public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() + radius * Math.cos(courseRads);
				final double yVal = point.getY() - radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public TurnType populateResults(final double initialTurnDegs, final double zLen, final double finalTurnDegs,
					final boolean isPossible, final double totalTime) {
				return new TurnType(initialTurnDegs, zLen, -finalTurnDegs, isPossible, totalTime);
			}

			@Override
			public Point2D secondOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() - radius * Math.cos(courseRads);
				final double yVal = point.getY() + radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public double theta1(final double alpha1, final double p1) {
				return p1 - alpha1;
			}

			@Override
			public double theta2(final double alpha2, final double p2) {
				return p2 - alpha2;
			}

			@Override
			public double zLength(final double hDelta, final double alpha1, final double rad1a, final double rad2a) {
				final double rad = rad1a + rad2a;
				return Math.abs((-hDelta + rad * Math.cos(alpha1)) / Math.sin(alpha1));
			}

		};

		////////////////////////////////////////////////////////////
		// CASE 4
		////////////////////////////////////////////////////////////
		final CaseSolver fourthCase = new CaseSolver() {
			@Override
			public double alpha1(final double hyp, final double rad1a, final double rad2a, final double hDelta,
					final double kDelta) {
				return Math.asin((rad2a - rad1a) / hyp) - Math.atan2(-hDelta, -kDelta);
			}

			@Override
			public double alpha2(final double alpha1) {
				return alpha1;
			}

			@Override
			public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() + radius * Math.cos(courseRads);
				final double yVal = point.getY() - radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public TurnType populateResults(final double initialTurnDegs, final double zLen, final double finalTurnDegs,
					final boolean isPossible, final double totalTime) {
				return new TurnType(initialTurnDegs, zLen, finalTurnDegs, isPossible, totalTime);
			}

			@Override
			public Point2D secondOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() + radius * Math.cos(courseRads);
				final double yVal = point.getY() - radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public double theta1(final double alpha1, final double p1) {
				return p1 - alpha1;
			}

			@Override
			public double theta2(final double alpha2, final double p2) {
				return alpha2 - p2;
			}

			@Override
			public double zLength(final double hDelta, final double alpha1, final double rad1a, final double rad2a) {
				return Math.abs((-hDelta + (rad1a - rad2a) * Math.cos(alpha1)) / Math.sin(alpha1));
			}
		};

		final TurnType firstTurn = calcCombination(startPoint, endPoint, firstCase, course1Rads, course2Rads, rad1,
				rad2, speed1, speed2, moves);

		final TurnType secondTurn = calcCombination(startPoint, endPoint, secondCase, course1Rads, course2Rads, rad1,
				rad2, speed1, speed2, moves);

		final TurnType thirdTurn = calcCombination(startPoint, endPoint, thirdCase, course1Rads, course2Rads, rad1,
				rad2, speed1, speed2, moves);

		final TurnType fourthTurn = calcCombination(startPoint, endPoint, fourthCase, course1Rads, course2Rads, rad1,
				rad2, speed1, speed2, moves);

		if (firstTurn._isPossible) {
			res = firstTurn;
		}

		if (secondTurn._isPossible) {
			if (res == null) {
				res = secondTurn;
			} else {
				if (secondTurn._thisLength < res._thisLength) {
					res = secondTurn;
				}
			}
		}

		if (thirdTurn._isPossible) {
			if (res == null) {
				res = thirdTurn;
			} else {
				if (thirdTurn._thisLength < res._thisLength) {
					res = thirdTurn;
				}
			}
		}

		if (fourthTurn._isPossible) {
			if (res == null) {
				res = fourthTurn;
			} else {
				if (fourthTurn._thisLength < res._thisLength) {
					res = fourthTurn;
				}
			}
		}

		// so, did we fail because the points are too close?
		if (res == null) {
			// so, did we fail because the points are too close?
			if (firstTurn._failTooClose || secondTurn._failTooClose || thirdTurn._failTooClose
					|| fourthTurn._failTooClose) {
				// bugger. the points are too close. Pass this back by throwing exception
				System.out.println("CAN'T DO, POINTS TOO CLOSE");
			}

			if (firstTurn._failCantAccelerate || secondTurn._failCantAccelerate || thirdTurn._failCantAccelerate
					|| fourthTurn._failCantAccelerate) {
				// bugger. the points are too close. Pass this back by throwing exception
				System.out.println("CAN'T DO, NO ROOM FOR ACCELERATION");
			}

		}

		return res;

	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	private TurnType _requiredTurn;

	/**
	 * note that our parent behaviour has been interrupted. If we have a calculated
	 * the time/distance to the next waypoint these calculations may become invalid
	 * - we need to clear them in order that they be re-calculated next time we
	 * actually get called.
	 */
	@Override
	public void forgetCalculatedRoute() {
		//
		_requiredTurn = null;
	}

	@Override
	public String getType() {
		return _myType;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: DirectedOnTopWaypoint.java,v $
	 * Revision 1.1  2006/08/08 14:21:46  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:55  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.19  2004/08/31 09:36:41  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 *
	 * Revision 1.18  2004/08/25 11:20:56  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.17  2004/08/16 09:16:16  Ian.Mayo
	 * Respect changed processing of tester recording to file (it needed a valid scenario object)
	 * <p/>
	 * Revision 1.16  2004/08/09 15:50:39  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.15  2004/08/06 12:27:51  Ian.Mayo
	 * Make interruptable, handle missing demanded speed
	 * <p/>
	 * Revision 1.14  2004/08/06 11:14:38  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.13  2004/05/24 15:09:02  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:53  ian
	 * no message
	 * <p/>
	 * Revision 1.12  2004/02/16 13:41:02  Ian.Mayo
	 * Minor improvements
	 * <p/>
	 * Revision 1.10  2003/12/08 13:17:03  Ian.Mayo
	 * Implement OnTop alg
	 * <p/>
	 * Revision 1.9  2003/11/21 14:59:00  Ian.Mayo
	 * part way through better handling of speed changes
	 * <p/>
	 * Revision 1.8  2003/11/21 13:37:38  Ian.Mayo
	 * Correctly handle height change in route
	 * <p/>
	 * Revision 1.7  2003/11/21 08:48:29  Ian.Mayo
	 * more testing, minor corrections
	 * <p/>
	 * Revision 1.6  2003/11/19 15:49:58  Ian.Mayo
	 * tidying up test comments
	 * <p/>
	 * Revision 1.5  2003/11/19 10:10:40  Ian.Mayo
	 * Initial implementation
	 * <p/>
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	private Status processThisTurn(Status current, final long endTime, final HighLevelDemandedStatus highLevelDemStat,
			final MovementCharacteristics moves, final TurnAlgorithm turner) {

		// ok - is the initial turn complete?
		if (_requiredTurn._initialTurn != null) {

			// get the turn required
			final double demandedTurn = _requiredTurn._initialTurn.doubleValue();

			// remember the old course - so we can update the change required
			final double oldCourse = current.getCourse();

			// no, get turning
			current = doThisPartOfTheTurn(demandedTurn, moves, current, endTime, turner,
					-current.getLocation().getDepth());

			// are we now on the correct course
			// so, still got some way to go...
			final double amountTurned = current.getCourse() - oldCourse;
			double amountRemaining = demandedTurn - amountTurned;

			// trim the amount remaining to a realistic figure, silly.
			if (amountRemaining < -360)
				amountRemaining += 360;

			if (amountRemaining > 360)
				amountRemaining -= 360;

			if (Math.abs(amountRemaining) < 0.4) {
				_requiredTurn._initialTurn = null;
			} else {
				_requiredTurn._initialTurn = new Double(amountRemaining);
			}

		}

		// do we have time left
		if (current.getTime() < endTime) {
			// ok, is the straight turn complete?
			if (_requiredTurn._zLen != null) {

				final Double distance = _requiredTurn._zLen;

				// remember where we are
				final WorldLocation origin = new WorldLocation(current.getLocation());

				// ok, move forward as far on the straight course as we can
				current = processStraightCourse(distance, current, endTime, highLevelDemStat, turner, moves);

				// ok, see how far we have to go...
				final WorldVector travelled = current.getLocation().subtract(origin);
				final double rngDegs = travelled.getRange();
				final double rngM = MWC.Algorithms.Conversions.Degs2m(rngDegs);

				// hmm, how far is left?
				final double remainingDistance = distance.doubleValue() - rngM;

				// ok - are we practically there?
				if (remainingDistance < 0.1) {
					// hey - we're done!
					_requiredTurn._zLen = null;
				} else {
					_requiredTurn._zLen = new Double(remainingDistance);
				}
			}
		}

		// do we have any time left?
		if (current.getTime() < endTime) {
			// ok, is the final turn complete?
			if (_requiredTurn._finalTurn != null) {

				// remember the old course - so we can update the change required
				final double oldCourse = current.getCourse();

				// no, sort out the final turn
				final double turnRequired = _requiredTurn._finalTurn.doubleValue();
				current = doThisPartOfTheTurn(turnRequired, moves, current, endTime, turner,
						-current.getLocation().getDepth());

				// are we now on the correct course
				// so, still got some way to go...
				double amountTurned = current.getCourse() - oldCourse;

				// trim the amount turned to reflect the true amount
				if (amountTurned > 270)
					amountTurned -= 360;

				if (amountTurned < -270)
					amountTurned += 360;

				final double amountRemaining = _requiredTurn._finalTurn.doubleValue() - amountTurned;

				if (Math.abs(amountRemaining) < 0.4) {
					_requiredTurn._finalTurn = null;
					_requiredTurn = null;

					// were we interrupted?
					if (_interruptedLocation != null) {
						// ok. get back on track
						_interruptedLocation = null;
					} else {
						// move on to the next point
						highLevelDemStat.nextWaypointVisited();
					}
				} else {
					_requiredTurn._finalTurn = new Double(amountRemaining);
				}

			}
		}

		return current;
	}

	/**
	 * produce a simple demanded status from the complex path
	 *
	 * @param highLevelDemStatus
	 * @param current
	 * @param newTime
	 * @param moves
	 * @return the new status
	 */
	@Override
	public Status step(final HighLevelDemandedStatus highLevelDemStatus, Status current, final long newTime,
			final MovementCharacteristics moves, final TurnAlgorithm turner) {

		// ok, loop through the next time step
		while (current.getTime() < newTime) {

			// do we have course to next waypoint?
			if (_requiredTurn == null) {
				// hmm, better sort out how to get to the next waypoint
				final Status endStat = new Status(current);

				// what's the next waypoint?
				// -- and what course do we need to be on after that?
				WorldLocation nextWaypoint = null;
				WorldLocation nextWaypointButOne = null;

				// have we been interrupted?
				if (_interruptedLocation != null) {
					// yes, that's where we've got to head
					nextWaypoint = _interruptedLocation;
					nextWaypointButOne = highLevelDemStatus.getCurrentTarget();
				} else {
					// no, just head on down our track
					nextWaypoint = highLevelDemStatus.getCurrentTarget();
					nextWaypointButOne = highLevelDemStatus.getNextTarget();
				}

				// does it exist?
				if (nextWaypoint == null) {
					// hey - this should have been checked
					MWC.Utilities.Errors.Trace.trace("reached end of waypoint in DirectedOnTop - shouldn't have!",
							true);
					return current;
				}

				// and do we know it?
				if (nextWaypointButOne == null) {
					// hey, we're heading for the last point. Hooray

					// create our own little high level demanded status, just to take us to the
					// final location
					final WorldPath path = new WorldPath();
					path.addPoint(nextWaypoint);

					final HighLevelDemandedStatus ds = new HighLevelDemandedStatus(12, current.getTime(), 0, path,
							WaypointVisitor.createVisitor(OnTopWaypoint._myType), highLevelDemStatus.getSpeed());

					final CoreMovement mover = new CoreMovement();

					current = mover.step(newTime, current, ds, moves);

					// hey, have we make it there?
					if (ds.getCurrentTarget() == null) {
						// hey, we're all done!

						// were we interrupted?
						if (_interruptedLocation != null) {
							// ok. get back on track
							_interruptedLocation = null;
						} else {
							// move on to the next point
							highLevelDemStatus.nextWaypointVisited();
						}
					}
				} else {
					// ok, what's the course to the next waypoint but one?
					final WorldVector toNext = nextWaypointButOne.subtract(nextWaypoint);
					final double nextCourse = MWC.Algorithms.Conversions.Rads2Degs(toNext.getBearing());
					endStat.setCourse(nextCourse);
					endStat.setLocation(nextWaypoint);
					endStat.setSpeed(highLevelDemStatus.getSpeed());

					// ok, work out the required manoeuvre
					_requiredTurn = getTurn(current, endStat, moves);

					// todo: modify this processing so that we repeatedly call getTurn until we get
					// an
					// achievable manoeuvre -
					// particularly where we start with an unobtainable height change. This part of
					// the
					// algorithm is in the documentation, but not
					// yet implemented

				}
			}

			// hey, we must have a required turn by now..
			if (_requiredTurn != null) {
				// ok, continue on this course
				current = processThisTurn(current, newTime, highLevelDemStatus, moves, turner);
			} else {
				// do we have any time remaining?
				if (current.getTime() < newTime) {
					// do we have a waypoint to head to?
					// bugger. the points are too close.
					// to overcome this, continue in steady state for the remainder of this time
					// step - then
					// redo the test at the start of the next step
					final SimpleDemandedStatus sds = new SimpleDemandedStatus(1, current);

					// hey, why not update the speed and height - so that at least we're on our
					// way...
					final WorldSpeed spd = highLevelDemStatus.getSpeed();
					if (spd != null)
						sds.setSpeed(spd);

					// and the height
					sds.setHeight(-highLevelDemStatus.getCurrentTarget().getDepth());

					current = turner.doTurn(current, sds, moves, newTime);
				}

			}

		}

		return current;
	}
}
