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

/**
 * ASSET from PlanetMayo Ltd User: Ian.Mayo Date: 19-Aug-2003 Time: 14:50:21
 * Log: $log: $
 */
public class OnTopWaypoint extends WaypointVisitor {
	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	/**
	 * interface for set of algorithms solving single turn permutation
	 */
	private static interface CaseSolver {
		/**
		 * what's the exit angle of the first turn
		 *
		 * @param hyp
		 * @param rad1
		 * @param hDelta
		 * @param kDelta
		 * @return
		 */
		public double alpha1(double hyp, double rad1, double hDelta, double kDelta);

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
		 * @param isPossible      whether the turn is possible
		 * @param totalTime       the total time for this combination
		 * @return
		 */
		OnTopSolution populateResults(double initialTurnDegs, double zLen, boolean isPossible, double totalTime);

		/**
		 * what's the total angle travelled for the first turn?
		 *
		 * @param alpha1
		 * @param p1
		 * @return
		 */
		double theta1(double alpha1, double p1);

		/**
		 * how far do we travel in a straight line?
		 *
		 * @param hDelta
		 * @param alpha1
		 * @param rad1
		 * @return
		 */
		public double zLength(double hDelta, double alpha1, double rad1);
	}

	protected static class OnTopSolution {
		/**
		 * we can't fit in the turn (too close)
		 */
		private static final String NO_ROOM_FOR_ACCELERATION = "NO ROOM FOR ACCELERATION";

		/**
		 * we can't fit in the acceleration (straight not long enough)
		 */
		private static final String NO_ROOM_FOR_TURN = "NO ROOM FOR TURN";

		/**
		 * the course change we need to make (-ve means turn to port)
		 */
		protected Double demandedCourseChange = null;

		/**
		 * the distance to travel once we're on course
		 */
		protected Double distanceToRun = null;

		/**
		 * whether this solution is possible
		 */
		protected boolean isPossible = false;

		/**
		 * and the total time taken (secs)
		 */
		protected double timeTaken;

		/**
		 * reason for failure
		 */
		protected String failureReason = null;

		/**
		 * quick constructor for when the f*cker isn't possible anyway
		 *
		 * @param possible
		 * @param reason   - why we failed
		 */
		public OnTopSolution(final boolean possible, final String reason) {
			isPossible = possible;
			failureReason = reason;
		}

		/**
		 * constructor = setup data
		 *
		 * @param initialCourse
		 * @param zLen
		 * @param possible
		 * @param totalTime
		 */
		public OnTopSolution(final double initialCourse, final double zLen, final boolean possible,
				final double totalTime) {
			demandedCourseChange = new Double(initialCourse);
			distanceToRun = new Double(zLen);
			isPossible = possible;
			timeTaken = totalTime;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public class OnTopWaypointTest extends SupportTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public OnTopWaypointTest(final String val) {
			super(val);
		}

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
			assertEquals("on correct course", 46, helo.getStatus().getCourse(), 1);

			// check we're at the correct finish point
			final WorldLocation endPoint = new WorldLocation(0.0776899319, 0.089914902, 0);
			double rng = helo.getStatus().getLocation().subtract(endPoint).getRange();
			rng = MWC.Algorithms.Conversions.Degs2m(rng);
			assertEquals("at correct end point:" + helo.getStatus().getLocation().getLat() + ","
					+ helo.getStatus().getLocation().getLong(), 0, rng, 1);

			// also output the series of locations to replay file
			super.outputTheseToRep("on_top_points.rep", destinations);

		}

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
				@Override
				public double alpha1(final double hyp, final double rad1, final double hDelta, final double kDelta) {
					return Math.asin(rad1 / hyp) + Math.atan2(hDelta, -kDelta);
				}

				@Override
				public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
					final double xVal = point.getX() - radius * Math.cos(courseRads);
					final double yVal = point.getY() + radius * Math.sin(courseRads);
					return new Point2D.Double(xVal, yVal);
				}

				@Override
				public OnTopSolution populateResults(final double initialTurnDegs, final double zLen,
						final boolean isPossible, final double totalTime) {
					return new OnTopSolution(-initialTurnDegs, zLen, isPossible, totalTime);
				}

				@Override
				public double theta1(final double alpha1, final double p1) {
					return alpha1 - p1;
				}

				@Override
				public double zLength(final double hDelta, final double alpha1, final double rad1) {
					return Math.abs((hDelta + rad1 * Math.cos(alpha1)) / Math.sin(alpha1));
				}
			};

			////////////////////////////////////////////////////////////
			// CASE 2
			////////////////////////////////////////////////////////////
			final CaseSolver secondCase = new CaseSolver() {
				@Override
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

				@Override
				public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
					final double xVal = point.getX() + radius * Math.cos(courseRads);
					final double yVal = point.getY() - radius * Math.sin(courseRads);
					return new Point2D.Double(xVal, yVal);
				}

				@Override
				public OnTopSolution populateResults(final double initialTurnDegs, final double zLen,
						final boolean isPossible, final double totalTime) {
					return new OnTopSolution(initialTurnDegs, zLen, isPossible, totalTime);
				}

				@Override
				public double theta1(final double alpha1, final double p1) {
					return p1 - alpha1;
				}

				@Override
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

			assertNotNull("found a solution", testOne);
			assertEquals("correct demanded turn", -277.1807, testOne.demandedCourseChange.doubleValue(), 0.01);
			assertEquals("correct distance", 39.686, testOne.distanceToRun.doubleValue(), 0.01);

			final OnTopSolution testTwo = OnTopWaypoint.calcCombination(p1, p2, secondCase, c1r, r1, spd1, spd2, moves);

			assertNotNull("found a solution", testTwo);
			assertEquals("correct demanded turn", 99.59, testTwo.demandedCourseChange.doubleValue(), 0.01);
			assertEquals("correct distance", 29.58, testTwo.distanceToRun.doubleValue(), 0.01);

		}

	}

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////

	static final String _myType = "OnTop";

	private static final double TINY_VALUE = 1e-14;

	public static OnTopSolution calcCombination(final Point2D startPoint, final Point2D endPoint,
			final CaseSolver thisCase, final double course1Rads, final double rad1, final double speed1,
			final double speed2, final MovementCharacteristics moves) {
		OnTopSolution res = null;

		// 2. ok, first do the origins
		final Point2D firstOrigin = thisCase.firstOrigin(startPoint, rad1, course1Rads);

		// 2a. now the h delta
		final double hDelta = firstOrigin.getX() - endPoint.getX();

		// and the k delta
		final double kDelta = firstOrigin.getY() - endPoint.getY();

		// 3. do we need to overcome the div/0 error?
		// NOPE

		// 4. check for overlapping circles
		final double h2 = Math.pow(hDelta, 2);
		final double k2 = Math.pow(kDelta, 2);
		final double hyp = Math.sqrt(h2 + k2);

		if (hyp < rad1) {
			res = new OnTopSolution(false, OnTopSolution.NO_ROOM_FOR_TURN);
			return res;
		}

		// 5. Calc start & end turn points
		double p1 = Math.atan2(startPoint.getY() - firstOrigin.getY(), startPoint.getX() - firstOrigin.getX());

		// and trim away
		if (p1 < 0)
			p1 += Math.PI * 2;

		// 6. Now get the tan points
		double alpha1 = thisCase.alpha1(hyp, rad1, hDelta, kDelta);

		// and trim them
		if (alpha1 < 0)
			alpha1 += Math.PI * 2;

		// 7. now the leg length
		// pad out the alpha if it's zero
		if (alpha1 == 0)
			alpha1 += TINY_VALUE;

		double initialTurn = thisCase.theta1(alpha1, p1);

		double zLen;
		if (Math.abs(initialTurn) <= TINY_VALUE) {
			final double xDelta = endPoint.getX() - startPoint.getX();
			final double yDelta = endPoint.getY() - startPoint.getY();
			zLen = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));
		} else {

			zLen = thisCase.zLength(hDelta, alpha1, rad1);
		}

		// 8. and the accel rates
		double accelRate;
		if (speed2 > 1)
			accelRate = moves.getAccelRate().getValueIn(WorldAcceleration.M_sec_sec);
		else
			accelRate = moves.getDecelRate().getValueIn(WorldAcceleration.M_sec_sec);

		final double accelTime = (speed2 - speed1) / accelRate;

		final double len = speed1 * accelTime + accelRate * Math.pow(accelTime, 2) / 2;

		// is it possible?
		if (len > zLen) {
			res = new OnTopSolution(false, OnTopSolution.NO_ROOM_FOR_ACCELERATION);
			return res;

		}

		// 9. calc the straight time
		final double straightTime = (zLen - len) / speed2 + accelTime;

		// 10. and the turn angles
		// and do some trimming
		if (initialTurn < 0)
			initialTurn += Math.PI * 2;

		// 11. now the overall time
		final double firstTurnRate = moves.calculateTurnRate(speed1);

		final double time1 = initialTurn / (firstTurnRate / 180 * Math.PI);

		final double totalTime = time1 + straightTime;

		final double initialTurnDegs = MWC.Algorithms.Conversions.Rads2Degs(initialTurn);

		res = thisCase.populateResults(initialTurnDegs, zLen, true, totalTime);

		return res;
	}

	public static ParticipantType createTestHelo() {
		final ParticipantType res = new Helo(122);
		res.setName("test_helo");
		res.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		return res;
	}

	public static WorldLocation offsetLocation(final WorldLocation host, final double bearing_degs, final double y_m) {
		final WorldVector vector = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearing_degs),
				MWC.Algorithms.Conversions.m2Degs(y_m), 0);
		return host.add(vector);
	}

	/**
	 * the calculated distance to run and course change necessary to get to the next
	 * waypoint
	 */
	protected OnTopSolution _currentSolution = null;

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * note that our parent behaviour has been interrupted. If we have a calculated
	 * the time/distance to the next waypoint these calculations may become invalid
	 * - we need to clear them in order that they be re-calculated next time we
	 * actually get called.
	 */
	@Override
	public void forgetCalculatedRoute() {
		//
		_currentSolution = null;
	}

	////////////////////////////////////////////////////////////
	// class to hold the demanded course to head to
	////////////////////////////////////////////////////////////

	/**
	 * calculate a viable solution to get OnTop of the target location
	 *
	 * @param nextW   the location we're heading for
	 * @param current our current status
	 * @param moves   our movement characteristics
	 * @param newTime the finish time
	 * @return the solution to this manoeuvre
	 */
	protected OnTopSolution getSolution(final WorldLocation nextW, final Status current,
			final MovementCharacteristics moves, final long newTime, final WorldSpeed demSpeed) {
		OnTopSolution res = null;

		// ok. sort out the results data
		// double r =
		// moves.getTurningCircleDiameter(current.getSpeed().getValueIn(WorldSpeed.M_sec));

		// now the initial position

		// where's the center of the area?
		final WorldArea coverage = new WorldArea(current.getLocation(), nextW);
		final WorldLocation centre = coverage.getCentre();

		// and create the corners
		WorldVector thisOffset = current.getLocation().subtract(centre);
		double dx = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.sin(thisOffset.getBearing());
		double dy = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.cos(thisOffset.getBearing());
		final Point2D startPoint = new Point2D.Double(dx, dy);

		thisOffset = nextW.subtract(centre);
		dx = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.sin(thisOffset.getBearing());
		dy = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.cos(thisOffset.getBearing());
		final Point2D endPoint = new Point2D.Double(dx, dy);

		double course1Rads = Math.toRadians(current.getCourse());
		if (course1Rads < 0)
			course1Rads += Math.PI * 2;

		final double speed1 = current.getSpeed().getValueIn(WorldSpeed.M_sec);
		double speed2;
		if (demSpeed != null)
			speed2 = demSpeed.getValueIn(WorldSpeed.M_sec);
		else
			speed2 = current.getSpeed().getValueIn(WorldSpeed.M_sec);

		final double spd1_rate = getTurnRateFor(moves, speed1);

		final double rad1 = speed1 / spd1_rate;

		// ok. now do the permutations

		////////////////////////////////////////////////////////////
		// CASE 1
		////////////////////////////////////////////////////////////
		final CaseSolver firstCase = new CaseSolver() {
			@Override
			public double alpha1(final double hyp, final double rad1a, final double hDelta, final double kDelta) {
				return Math.asin(rad1a / hyp) + Math.atan2(hDelta, -kDelta);
			}

			@Override
			public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() - radius * Math.cos(courseRads);
				final double yVal = point.getY() + radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public OnTopSolution populateResults(final double initialTurnDegs, final double zLen,
					final boolean isPossible, final double totalTime) {
				return new OnTopSolution(-initialTurnDegs, zLen, isPossible, totalTime);
			}

			@Override
			public double theta1(final double alpha1, final double p1) {
				return alpha1 - p1;
			}

			@Override
			public double zLength(final double hDelta, final double alpha1, final double rad1a) {
				return Math.abs((hDelta + rad1a * Math.cos(alpha1)) / Math.sin(alpha1));
			}
		};

		////////////////////////////////////////////////////////////
		// CASE 2
		////////////////////////////////////////////////////////////
		final CaseSolver secondCase = new CaseSolver() {
			@Override
			public double alpha1(final double hyp, final double rad1a, final double hDelta, final double kDelta) {
				return Math.asin(-rad1a / hyp) - Math.atan2(hDelta, kDelta);
			}

			// public Point2D secondOrigin(Point2D point,
			// double radius,
			// double courseRads)
			// {
			// double xVal = point.getX() - radius * Math.cos(courseRads);
			// double yVal = point.getY() + radius * Math.sin(courseRads);
			// return new Point2D.Double(xVal, yVal);
			// }

			@Override
			public Point2D firstOrigin(final Point2D point, final double radius, final double courseRads) {
				final double xVal = point.getX() + radius * Math.cos(courseRads);
				final double yVal = point.getY() - radius * Math.sin(courseRads);
				return new Point2D.Double(xVal, yVal);
			}

			@Override
			public OnTopSolution populateResults(final double initialTurnDegs, final double zLen,
					final boolean isPossible, final double totalTime) {
				return new OnTopSolution(initialTurnDegs, zLen, isPossible, totalTime);
			}

			@Override
			public double theta1(final double alpha1, final double p1) {
				return p1 - alpha1;
			}

			@Override
			public double zLength(final double hDelta, final double alpha1, final double rad1a) {
				return Math.abs((-hDelta - rad1a * Math.cos(alpha1)) / Math.sin(alpha1));
			}

		};

		// ok - get going
		final OnTopSolution firstSol = calcCombination(startPoint, endPoint, firstCase, course1Rads, rad1, speed1,
				speed2, moves);

		final OnTopSolution secondSol = calcCombination(startPoint, endPoint, secondCase, course1Rads, rad1, speed1,
				speed2, moves);

		if (firstSol.isPossible)
			res = firstSol;

		if (secondSol.isPossible) {
			if (secondSol.timeTaken < firstSol.timeTaken)
				res = secondSol;
		}

		return res;
	}

	@Override
	public String getType() {
		return _myType;
	}

	// private static WorldLocation offset(WorldLocation origin,
	// double rng_m,
	// double brg_degs)
	// {
	// WorldLocation res = origin.add(new
	// WorldVector(MWC.Algorithms.Conversions.Degs2Rads(brg_degs),
	// MWC.Algorithms.Conversions.m2Degs(rng_m), 0));
	// return res;
	// }

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: OnTopWaypoint.java,v $
	 * Revision 1.1  2006/08/08 14:21:50  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:58  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.32  2005/04/15 14:11:59  Ian.Mayo
	 * Update tests to reflect new scenario step cycle
	 *
	 * Revision 1.31  2004/08/31 09:36:48  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.30  2004/08/25 11:21:01  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.29  2004/08/20 13:32:44  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.28  2004/08/16 09:16:19  Ian.Mayo
	 * Respect changed processing of tester recording to file (it needed a valid scenario object)
	 * <p/>
	 * Revision 1.27  2004/08/09 15:50:42  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.26  2004/08/06 12:56:25  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.25  2004/08/06 11:14:39  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.24  2004/08/06 10:21:24  Ian.Mayo
	 * Manage investigate height
	 * <p/>
	 * Revision 1.23  2004/05/24 15:09:08  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:53  ian
	 * no message
	 * <p/>
	 * Revision 1.22  2004/02/16 13:38:55  Ian.Mayo
	 * General improvements
	 * <p/>
	 * Revision 1.20  2003/12/10 16:16:14  Ian.Mayo
	 * Minor tidying
	 * <p/>
	 * Revision 1.19  2003/12/08 13:17:05  Ian.Mayo
	 * Implement OnTop alg
	 * <p/>
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
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

		// ok, loop through the available time
		while (current.getTime() < newTime) {
			// do we know where we're going?
			if (_currentSolution == null) {

				WorldLocation nextW = null;

				// have we been interrupted?
				if (super._interruptedLocation != null) {
					// yes, head back to it before we resume route
					nextW = _interruptedLocation;
				} else {
					// no, just continue through the points
					nextW = highLevelDemStatus.getCurrentTarget();

				}

				// ok. do calc.
				if (nextW != null) {
					// nope - we've no idea where we're going
					_currentSolution = getSolution(nextW, current, moves, newTime, highLevelDemStatus.getSpeed());
				}

				// ok, we continue in steady state if we don't have a next waypoint, or if we
				// can't
				// calculate a solution
				// to the next waypoint

				if ((nextW == null) || (_currentSolution == null)) {
					// hey, we're done!

					// check if we're dead close and can't make it - in which case we mark it as
					// reached
					if (_currentSolution == null && (nextW != null)) {
						double rangeError = current.getLocation().subtract(nextW).getRange();
						rangeError = MWC.Algorithms.Conversions.Degs2m(rangeError);
						if (rangeError < 10) {
							// are we resuming after interruption?
							if (_interruptedLocation != null) {
								// yes we were. We're ok now, get back on proper route
								_interruptedLocation = null;
							} else {
								// and mark that this point is reached
								highLevelDemStatus.nextWaypointVisited();
							}
						}
					}

					// Carry on in steady state to complete the time step
					final SimpleDemandedStatus sds = new SimpleDemandedStatus(newTime, current);

					// carry on doing the speed update if we haven't already
					if (highLevelDemStatus.getSpeed() != null)
						sds.setSpeed(highLevelDemStatus.getSpeed());

					// and do the turn
					current = turner.doTurn(current, sds, moves, newTime);
				}

			} else {
				// yup - keep heading towards the next waypoint
				if (_currentSolution.demandedCourseChange != null) {
					// get the turn required
					final double demandedTurn = _currentSolution.demandedCourseChange.doubleValue();

					// remember the old course - so we can update the change required
					final double oldCourse = current.getCourse();

					// no, get turning
					current = doThisPartOfTheTurn(demandedTurn, moves, current, newTime, turner,
							-current.getLocation().getDepth());

					// are we now on the correct course
					// so, still got some way to go...
					final double amountTurned = current.getCourse() - oldCourse;
					double amountRemaining = demandedTurn - amountTurned;

					// trim the amount remaining to a realistic figure, silly.
					if (amountRemaining <= -360)
						amountRemaining += 360;

					if (amountRemaining >= 360)
						amountRemaining -= 360;

					// ok, update the amount of course change remaining
					if (Math.abs(amountRemaining) < 0.4) {
						_currentSolution.demandedCourseChange = null;
					} else {
						_currentSolution.demandedCourseChange = new Double(amountRemaining);
					}
				} else {
					if (_currentSolution.distanceToRun != null) {
						// ok carry on travelling along the straight section

						final Double distance = _currentSolution.distanceToRun;

						// remember where we are
						final WorldLocation origin = new WorldLocation(current.getLocation());

						// ok, move forward as far on the straight course as we can
						current = processStraightCourse(distance, current, newTime, highLevelDemStatus, turner, moves);

						// ok, see how far we have to go...
						final WorldVector travelled = current.getLocation().subtract(origin);
						final double rngDegs = travelled.getRange();
						final double rngM = MWC.Algorithms.Conversions.Degs2m(rngDegs);

						// hmm, how far is left?
						final double remainingDistance = distance.doubleValue() - rngM;

						// ok - are we practically there?
						if (remainingDistance < 0.1) {
							// hey - we're done!
							_currentSolution.distanceToRun = null;
							_currentSolution = null;

							// and mark that this point is reached

							// are we returning after interruption?
							if (_interruptedLocation != null) {
								// yes. get back onto our proper course
								_interruptedLocation = null;
							} else {
								// no, just continue onto the next waypoint.
								highLevelDemStatus.nextWaypointVisited();
							}
						} else {
							_currentSolution.distanceToRun = new Double(remainingDistance);
						}
					} else {
						// hey, all done! Clear the solution
						_currentSolution = null;
					}
				}
			}

		}

		return current;
	}
}
