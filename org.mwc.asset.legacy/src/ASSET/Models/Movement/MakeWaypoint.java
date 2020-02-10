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
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 19-Aug-2003 Time: 14:50:21 To
 * change this template use Options | File Templates.
 */
public class MakeWaypoint extends OnTopWaypoint {

	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	/**
	 * embedded class containing the solution to this manoeuvering problem
	 */
	protected static class MakeWaypointSolution {

		/**
		 * remember how far we still have to travel
		 */
		protected Double remainingDist = null;

		/**
		 * remember what course (degs) we head towards to get to the waypoint
		 */
		protected Double demandedCourseChange = null;

		/**
		 * and the outgoing course (Degs)
		 */
		protected Double outgoingCourse = null;

		/**
		 * the new height to adopt on the straight portion
		 */
		protected Double heightToChangeToMake = null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public class MakeWaypointTest extends SupportTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public MakeWaypointTest(final String val) {
			super(val);
		}

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
			assertNotNull("yes, it's posslble", sol);
			assertEquals("correct distance to run", 48.98, sol.remainingDist.doubleValue(), 0.01);
			assertEquals("correct outgoing course", 180, sol.outgoingCourse.doubleValue(), 0.01);
			assertEquals("correct turn amount", 135, sol.demandedCourseChange.doubleValue(), 0.01);

			// hey, let's have another go - where the point is on the other side of us
			thisWaypoint = SupportTesting.createLocation(-50, 50);
			nextWaypoint = SupportTesting.createLocation(-50, 0);
			startStat.setCourse(315);

			final MakeWaypointSolution sol2 = mw.getMakeWaypointSolution(startStat, moves, thisWaypoint, nextWaypoint,
					1000, null);

			// and the result
			assertNotNull("yes, it's posslble", sol2);
			assertEquals("correct distance to run", 48.98, sol2.remainingDist.doubleValue(), 0.01);
			assertEquals("correct outgoing course", 180, sol2.outgoingCourse.doubleValue(), 0.01);
			assertEquals("correct turn amount", -135, sol2.demandedCourseChange.doubleValue(), 0.01);

			// hey, let's have another go - where the point is on the other side of us
			thisWaypoint = SupportTesting.createLocation(-50, 50);
			nextWaypoint = SupportTesting.createLocation(-150, 50);
			startStat.setCourse(270);

			final MakeWaypointSolution sol3 = mw.getMakeWaypointSolution(startStat, moves, thisWaypoint, nextWaypoint,
					1000, null);

			// and the result
			assertNotNull("yes, it's posslble", sol3);
			assertEquals("correct distance to run", 66.98, sol3.remainingDist.doubleValue(), 0.01);
			assertEquals("correct outgoing course", 270, sol3.outgoingCourse.doubleValue(), 0.01);
			assertEquals("correct turn amount", -45, sol3.demandedCourseChange.doubleValue(), 0.01);

		}

	}

	////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////

	static final String __myType = "MakeWaypoint";

	public static void main(final String[] args) {
		final MakeWaypointTest tt = new MakeWaypointTest("scrap");

		tt.testScenarioChangeHeight();

		SupportTesting.callTestMethods(tt);

	}

	private MakeWaypointSolution _mySolution = null;

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	protected MakeWaypointSolution getMakeWaypointSolution(final Status current, final MovementCharacteristics moves,
			final WorldLocation thisWaypoint, final WorldLocation nextWaypoint, final long newTime,
			final Double demandedHeight) {
		MakeWaypointSolution res = null;
		boolean possible = true;

		// ok, let's get solving!

		// where's the center of the area?
		final WorldArea coverage = new WorldArea(current.getLocation(), thisWaypoint);
		final WorldLocation centre = coverage.getCentre();

		// and create the corners
		WorldVector thisOffset = current.getLocation().subtract(centre);
		double dx = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.sin(thisOffset.getBearing());
		double dy = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.cos(thisOffset.getBearing());
		final Point2D startPoint = new Point2D.Double(dx, dy);

		thisOffset = thisWaypoint.subtract(centre);
		dx = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.sin(thisOffset.getBearing());
		dy = MWC.Algorithms.Conversions.Degs2m(thisOffset.getRange()) * Math.cos(thisOffset.getBearing());
		final Point2D endPoint = new Point2D.Double(dx, dy);

		double course1Rads = Math.toRadians(current.getCourse());
		if (course1Rads < 0)
			course1Rads += Math.PI * 2;

		final double speed1 = current.getSpeed().getValueIn(WorldSpeed.M_sec);

		final double spd1_rate = getTurnRateFor(moves, speed1);

		// what's the outgoing course?
		final WorldVector outGoing = nextWaypoint.subtract(thisWaypoint);
		double c2 = outGoing.getBearing();
		if (c2 < 0)
			c2 += Math.PI * 2;

		///////////////
		// STEP 1
		///////////////
		final double deltaX = endPoint.getX() - startPoint.getX();
		double deltaY = endPoint.getY() - startPoint.getY();
		final double hyp = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		final double theta = Math.atan2(deltaX, deltaY);
		double c1 = 0;
		if (deltaY > startPoint.getX())
			c1 = (Math.PI / 2 - theta);
		if (deltaY < startPoint.getX())
			c1 = (3 * Math.PI / 2 - theta);
		if (deltaY == startPoint.getX())
			c1 = (Math.PI);

		c1 = theta;

		///////////////
		// STEP 2
		///////////////
		// prevent div/0 error
		if (deltaY == 0)
			deltaY += 0.00000001;

		///////////////
		// STEP 3
		///////////////
		final double r1 = speed1 / spd1_rate;

		///////////////
		// STEP 4
		///////////////
		double theta2;
		if (c1 >= Math.PI) {
			theta2 = theta - Math.PI;
		} else {
			theta2 = theta + Math.PI;
		}

		///////////////
		// STEP 5
		///////////////
		double alpha = Math.abs(c2 - theta2);
		if (alpha > Math.PI)
			alpha = ((2 * Math.PI) - alpha);

		///////////////
		// STEP 6
		///////////////
		final double alpha2 = (c2 - c1);
		double turn = 0;
		if (alpha2 < -Math.PI) {
			turn = (2 * Math.PI) + alpha2;
		} else if (alpha2 > Math.PI) {
			turn = (-2 * Math.PI) + alpha2;
		} else {
			turn = alpha2;
		}

		// String dir = "";
		// if (turn > 0)
		// dir = "Right Turn";
		// else if (turn < 0)
		// dir = "Left Turn";
		// else
		// dir = "Straight";

		///////////////
		// STEP 6(b)
		///////////////
		final double phi = Math.atan(r1 / hyp);

		if ((alpha / 2) < phi) {
			possible = false;
		}

		///////////////
		// STEP 7
		///////////////
		final double z2 = r1 / Math.tan(alpha / 2);

		///////////////
		// STEP 9
		///////////////
		final double d = (hyp - z2);

		///////////////
		// STEP 10
		// - this additional step has been introduced to allow a height change during
		/////////////// the
		// straight portion of the manoeuvre.
		// The height change assumes instantaneous speed change, but ensures that the
		/////////////// speed as the end
		// of the straight section is the same as at the start
		///////////////

		// do we have a demanded height?
		// if (demandedHeight != null)
		// {
		// double timeRequired =
		// TurnAlgorithm.calcHeightChangeTime(demandedHeight.doubleValue() -
		// (-current.getLocation().getDepth()),
		// moves, newTime - current.getTime());
		// }

		// ok, store the data
		if (possible) {
			res = new MakeWaypointSolution();
			res.demandedCourseChange = new Double(MWC.Algorithms.Conversions.Rads2Degs(turn));
			res.remainingDist = new Double(d);
			res.outgoingCourse = new Double(MWC.Algorithms.Conversions.Rads2Degs(c2));
		}

		return res;
	}

	////////////////////////////////////////////////////////////
	// holder for the successful permutation
	////////////////////////////////////////////////////////////

	@Override
	public String getType() {
		return __myType;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: MakeWaypoint.java,v $
	 * Revision 1.1  2006/08/08 14:21:49  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:57  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.12  2004/08/31 09:36:45  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 *
	 * Revision 1.11  2004/08/16 09:16:18  Ian.Mayo
	 * Respect changed processing of tester recording to file (it needed a valid scenario object)
	 * <p/>
	 * Revision 1.10  2004/08/09 15:50:40  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.9  2004/05/24 15:09:06  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:53  ian
	 * no message
	 * <p/>
	 * Revision 1.8  2004/02/18 08:50:10  Ian.Mayo
	 * Tidy up, remove unnecessary variables
	 * <p/>
	 * Revision 1.6  2003/12/10 16:15:58  Ian.Mayo
	 * Still getting there
	 * <p/>
	 * Revision 1.5  2003/12/08 13:17:04  Ian.Mayo
	 * Implement OnTop alg
	 * <p/>
	 * Revision 1.4  2003/11/05 09:19:20  Ian.Mayo
	 * Include MWC Model support
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

		while (current.getTime() < newTime) {
			// do we have a solution?
			if (_mySolution == null) {

				// ok. do we know the next point?
				final WorldLocation thisW = highLevelDemStatus.getCurrentTarget();
				final WorldLocation nextW = highLevelDemStatus.getNextTarget();

				if (thisW != null) {
					// ok. do we have a point after that?
					if (nextW != null) {
						boolean onSpeed = false;
						// ok - do a speed change before we start doing any fancy manoeuvres
						// ok - on course. Calculate the solution
						final WorldSpeed demSpeed = highLevelDemStatus.getSpeed();
						if (demSpeed != null) {
							final double spd = demSpeed.getValueIn(WorldSpeed.M_sec);
							double spdDelta = Math.abs(spd - current.getSpeed().getValueIn(WorldSpeed.M_sec));
							if (spdDelta > TurnAlgorithm.SPEED_DELTA) {
								final SimpleDemandedStatus sds = new SimpleDemandedStatus(0, current);
								sds.setSpeed(demSpeed);
								current = turner.doTurn(current, sds, moves, newTime);
							}

							// double-check if we're there yet
							spdDelta = Math.abs(spd - current.getSpeed().getValueIn(WorldSpeed.M_sec));
							if (spdDelta < TurnAlgorithm.SPEED_DELTA) {
								onSpeed = true;
							}

						} else {
							// no dem speed. Hey, we must be there!
							onSpeed = true;
						}

						// only do the processing once we're on speed
						if (onSpeed) {

							// hey, we've got to head towards the target before we can decide on the turn.
							final WorldVector offset = thisW.subtract(current.getLocation());
							final double brg = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());
							double bearingError = brg - current.getCourse();
							if (bearingError < -180)
								bearingError += 360;
							if (bearingError > 180)
								bearingError -= 360;

							if (Math.abs(bearingError) < 1) {

								// sort out the demanded height, if there is one.
								Double demHeight = null;
								if (thisW.getDepth() != current.getLocation().getDepth()) {
									demHeight = new Double(thisW.getDepth());
								}

								_mySolution = getMakeWaypointSolution(current, moves, thisW, nextW, newTime, demHeight);

								// if(_mySolution != null)
								// {
								// System.out.println("dist:" + (int)_mySolution.remainingDist.doubleValue() +
								// " crse chnge:" + (int)_mySolution.demandedCourseChange.doubleValue() +
								// " out course:" + (int)_mySolution.outgoingCourse.doubleValue());
								// }
							}

							if (_mySolution == null) {
								// nope, still trying to head towards it
								current = super.step(highLevelDemStatus, current, newTime, moves, turner);
							}
						} // whether we're on speed yet or not.
					} else {
						// hmm. We've got the next point but not one after it. Carry on as if it's an
						// on-top
						current = super.step(highLevelDemStatus, current, newTime, moves, turner);
					}

				} else {
					// hey, we're all done. Carry on the rest of the step on steady course
					final SimpleDemandedStatus sds = new SimpleDemandedStatus(newTime, current);
					current = turner.doTurn(current, sds, moves, newTime);
				}
			} else {
				// ok. do we still have some straight run to perform?
				if (_mySolution.remainingDist != null) {
					// ok carry on travelling along the straight section

					final Double distance = _mySolution.remainingDist;

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
						_mySolution.remainingDist = null;
					} else {
						_mySolution.remainingDist = new Double(remainingDistance);
					}
				} else if (_mySolution.outgoingCourse != null) {

					// get the out course
					// double outCourse = _mySolution.outgoingCourse.doubleValue();

					// how far do we have to turn?
					final double demChange = _mySolution.demandedCourseChange.doubleValue();

					final double curCourse = current.getCourse();

					// and trim it
					// if (demChange < -180)
					// demChange += 360;
					// if (demChange > 180)
					// demChange -= 360;

					// and
					current = doThisPartOfTheTurn(demChange, moves, current, newTime, turner,
							-current.getLocation().getDepth());

					// how much is left
					final double amountTurned = current.getCourse() - curCourse;
					double amountRemaining = demChange - amountTurned;

					// trim back to our cycle
					if (amountRemaining <= -360)
						amountRemaining += 360;
					if (amountRemaining >= 360)
						amountRemaining -= 360;

					if (Math.abs(amountRemaining) < 0.4) {
						_mySolution.demandedCourseChange = null;
						_mySolution = null;

						// and mark that this point is reached
						highLevelDemStatus.nextWaypointVisited();
					} // yes, we've finished the turn!
					else {
						_mySolution.demandedCourseChange = new Double(amountRemaining);
					}
				} // whether we've finished the outgoing course yet
			} // whether we've finished the straight run
		} // looping through the time available

		return current;
	}

}
