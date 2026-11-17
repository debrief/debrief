package ASSET.Models.Decision.Movement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Movement.TransitWaypoint.*;
import ASSET.Models.Decision.Movement.TransitWaypoint.*;
import ASSET.ParticipantType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Movement.HighLevelDemandedStatus;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.OnTopWaypoint;
import ASSET.Models.Movement.WaypointVisitor;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Scenario.Observers.Recording.CSVTrackObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

/**
 * ********************************************************************** test
 * this public class
 * **********************************************************************
 */

public class TransitWaypointTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final MWC.GUI.Editable ed = new TransitWaypoint();
		return ed;
	}

	@Test
	public void testAcceleratingForwardLoopRoute() {

		final WorldPath wp = new WorldPath();
		wp.addPoint(createLocation(000, 6000));
		wp.addPoint(createLocation(4000, 6000));
		wp.addPoint(createLocation(6000, 2000));
		wp.addPoint(createLocation(10000, 2000));
		wp.addPoint(createLocation(4000, 3000));
		wp.addPoint(createLocation(6000, 8000));

		final OnTopWaypoint otw = new OnTopWaypoint();

		final TransitWaypoint t2 = new TransitWaypoint(wp, new WorldSpeed(62, WorldSpeed.M_sec), true, otw);
		t2.setReverse(false);

		final String myName = "Merlin Trial";
		final double accelRate = 0.6;
		final double decelRate = 0.5;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxHeight = 400;
		final double minHeight = 0;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final Status current = new Status(1, 100);
		current.setLocation(new WorldLocation(0.00, 0.00, 0));
		current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
		current.setCourse(0);

		final TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 400, 400,
				"testAcceleratingForwardLoopRoute.png", new WorldDistance(100, WorldDistance.METRES), false, true,
				false, "test observer", true);
		final ParticipantType cp = OnTopWaypoint.createTestHelo();
		cp.setDecisionModel(t2);
		cp.setMovementChars(chars);
		cp.setStatus(current);

		final ASSET.Scenario.Observers.Recording.DebriefReplayObserver dr = new DebriefReplayObserver(
				"./test_reports", "testAcceleratingForwardLoopRoute.rep", false, "test observer", true);
		final ASSET.Scenario.Observers.Recording.CSVTrackObserver csv = new CSVTrackObserver("./test_reports",
				"testAcceleratingForwardLoopRoute.csv", false, "test observer", true);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(cp.getId(), cp);
		cs.setScenarioStepTime(1000);

		tpo.setup(cs);
		dr.setup(cs);

		csv.setup(cs);

		for (int newTime = 0; newTime < 1200000; newTime += 1000) {
			cs.step();
		}

		tpo.tearDown(cs);
		dr.tearDown(cs);
		csv.tearDown(cs);

		// ok, now output the points
		super.outputTheseToRep("testAcceleratingForwardLoopRoute_points.rep", wp);

		// ok - check where we are
		final WorldLocation finalLocation = cp.getStatus().getLocation();
		outputLocation(finalLocation);
		final WorldLocation targetLocation = createLocation(6518, 8322);
		final double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
		assertEquals(0, range_offset, 10, "near target - range error:" + range_offset + " pos:" + toXYString(finalLocation));

	}

	@Test
	public void testForwardLoopRoute() {

		final WorldPath wp = new WorldPath();
		wp.addPoint(createLocation(000, 6000));
		wp.addPoint(createLocation(4000, 6000));
		wp.addPoint(createLocation(6000, 2000));
		wp.addPoint(createLocation(10000, 2000));
		wp.addPoint(createLocation(4000, 3000));
		wp.addPoint(createLocation(6000, 8000));

		final OnTopWaypoint otw = new OnTopWaypoint();

		final TransitWaypoint t2 = new TransitWaypoint(wp, null, true, otw);
		t2.setReverse(false);

		final String myName = "Merlin Trial";
		final double accelRate = 10;
		final double decelRate = 25;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxHeight = 400;
		final double minHeight = 0;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final Status current = new Status(1, 100);
		current.setLocation(new WorldLocation(0.00, 0.00, 0));
		current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
		current.setCourse(0);

		final TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 400, 400, "transit2b.png",
				new WorldDistance(100, WorldDistance.METRES), false, true, false, "test observer", true);
		final ParticipantType cp = OnTopWaypoint.createTestHelo();
		cp.setDecisionModel(t2);
		cp.setMovementChars(chars);
		cp.setStatus(current);

		final ASSET.Scenario.Observers.Recording.DebriefReplayObserver dr = new DebriefReplayObserver(
				"./test_reports", null, false, "test observer", true);

		final CoreScenario cs = new CoreScenario();
		cs.addParticipant(cp.getId(), cp);
		cs.setScenarioStepTime(1000);

		tpo.setup(cs);
		dr.setup(cs);

		for (int newTime = 0; newTime < 11000000; newTime += 1000) {
			cs.step();
		}

		super.outputTheseToRep("testForwardLoopRoute_pts.rep", wp);

		tpo.tearDown(cs);
		dr.tearDown(cs);

		// ok - check where we are
		final WorldLocation finalLocation = cp.getStatus().getLocation();
		outputLocation(finalLocation);
		final WorldLocation targetLocation = createLocation(6948, 1915);
		final double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
		assertEquals(0, range_offset, 10d, "near target");

	}

	@Test
	public void testNonLoopRoute() {

		final WorldPath wp = new WorldPath();
		wp.addPoint(createLocation(000, 600));
		wp.addPoint(createLocation(400, 600));
		wp.addPoint(createLocation(600, 200));
		wp.addPoint(createLocation(1000, 200));
		wp.addPoint(createLocation(400, 300));
		wp.addPoint(createLocation(600, 800));

		final OnTopWaypoint otw = new OnTopWaypoint();

		final TransitWaypoint t2 = new TransitWaypoint(wp, null, false, otw);

		final String myName = "Merlin Trial";
		final double accelRate = 10;
		final double decelRate = 25;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxHeight = 0;
		final double minHeight = -400;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final Status current = new Status(1, 100);
		current.setLocation(new WorldLocation(0.00, 0.00, 0));
		current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
		current.setCourse(0);

		final Helo helo = new Helo(12);
		helo.setName("Merlin_Test");
		helo.setMovementChars(chars);
		helo.setStatus(current);
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		helo.setDecisionModel(t2);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);
		cs.addParticipant(12, helo);

		super.startListeningTo(helo, "testNonLoopRoute", true, true, true, cs);

		for (long newTime = 0; newTime < 600000; newTime += 1000) {
			cs.step();
			newTime = cs.getTime();
		}

		super.endRecording(cs);

		super.outputTheseToRep("testNonLoopRoute_pts.rep", wp);

		// ok - check where we are
		final WorldLocation finalLocation = helo.getStatus().getLocation();
		final WorldLocation targetLocation = createLocation(2319, 1515);
		final double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
		outputLocation(finalLocation);
		assertTrue(range_offset <= 11, "near target - error: " + (int) range_offset);

	}

	@Test
	public void testReverseLoopRoute() {

		final WorldPath wp = new WorldPath();
		wp.addPoint(createLocation(000, 6000));
		wp.addPoint(createLocation(4000, 6000));
		wp.addPoint(createLocation(6000, 2000));
		wp.addPoint(createLocation(10000, 2000));
		wp.addPoint(createLocation(4000, 3000));
		wp.addPoint(createLocation(6000, 8000));

		final OnTopWaypoint otw = new OnTopWaypoint();

		final TransitWaypoint t2 = new TransitWaypoint(wp, null, true, otw);

		final String myName = "Merlin Trial";
		final double accelRate = 10;
		final double decelRate = 25;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxDepth = 400;
		final double minDepth = 0;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxDepth, minDepth, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final Status current = new Status(1, 100);
		current.setLocation(new WorldLocation(0.00, 0.00, 0));
		current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
		current.setCourse(0);

		final Helo helo = new Helo(12);
		helo.setName("Merlin_Test");
		helo.setMovementChars(chars);
		helo.setStatus(current);
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		helo.setDecisionModel(t2);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);
		cs.addParticipant(12, helo);

		super.startListeningTo(helo, "testReverseLoopRoute", true, true, true, cs);

		for (int newTime = 0; newTime < 11000000; newTime += 1000) {
			cs.step();
		}

		super.endRecording(cs);
		super.outputTheseToRep("testReverseLoopRoute_pts.rep", wp);

		// ok - check where we are
		final WorldLocation finalLocation = helo.getStatus().getLocation();
		final WorldLocation targetLocation = createLocation(4207, 5860);
		final double range_offset = MWC.Algorithms.Conversions.Degs2m(finalLocation.rangeFrom(targetLocation));
		outputLocation(finalLocation);

		// todo find out why we're looping around again at point 6'
		assertTrue(range_offset < 12, "near target - range:" + range_offset);

	}

	@Test
	public void testShortLoopRoute() {

		final WorldPath wp = new WorldPath();
		wp.addPoint(createLocation(10000, 2000));
		wp.addPoint(createLocation(4000, 3000));
		wp.addPoint(createLocation(6000, 8000));

		final OnTopWaypoint otw = new OnTopWaypoint();

		final TransitWaypoint t2 = new TransitWaypoint(wp, null, true, otw);

		final String myName = "Merlin Trial";
		final double accelRate = 10;
		final double decelRate = 25;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxDepth = 400;
		final double minDepth = 0;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxDepth, minDepth, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final Status current = new Status(1, 100);
		current.setLocation(new WorldLocation(0.00, 0.00, 0));
		current.setSpeed(new WorldSpeed(10.172, WorldSpeed.M_sec));
		current.setCourse(0);

		final Helo helo = new Helo(12);
		helo.setName("Merlin_Test");
		helo.setMovementChars(chars);
		helo.setStatus(current);
		helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		helo.setDecisionModel(t2);

		final CoreScenario cs = new CoreScenario();
		cs.setScenarioStepTime(1000);
		cs.addParticipant(12, helo);

		super.startListeningTo(helo, "testShortLoopRoute", true, true, true, cs);

		for (int newTime = 0; newTime < 2600000; newTime += 1000) {
			cs.step();
			if (newTime == 2202000)
				System.out.println("here");
		}

		super.endRecording(cs);
		super.outputTheseToRep("testShortLoopRoute_pts.rep", wp);
	}

}
