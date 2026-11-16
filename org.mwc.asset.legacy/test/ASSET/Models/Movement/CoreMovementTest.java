package ASSET.Models.Movement;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Models.Movement.CoreMovement.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ASSET.Models.Movement.CoreMovement.*;
import ASSET.ParticipantType;
import ASSET.Models.MovementType;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import org.junit.jupiter.api.Test;

/**
 * ******************************************************************* testing
 * code *******************************************************************
 */
//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class CoreMovementTest extends SupportTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testAcceleratingHighLevel() {
		final CoreMovement cm = new CoreMovement();

		WorldLocation.setModel(new CompletelyFlatEarth());

		final OnTopWaypoint myVisitor = new OnTopWaypoint();
		final WorldPath myPath = new WorldPath();
		myPath.addPoint(createLocation(000, 600));
		myPath.addPoint(createLocation(400, 600));
		myPath.addPoint(createLocation(600, 200));
		myPath.addPoint(createLocation(1000, 200));
		myPath.addPoint(createLocation(400, 300));
		myPath.addPoint(createLocation(600, 800));

		final HighLevelDemandedStatus hls = new HighLevelDemandedStatus(300, 200, 0, myPath, myVisitor,
				new WorldSpeed(60, WorldSpeed.M_sec));

		Status stat = new Status(0, 100);
		stat.setLocation(new WorldLocation(0, 0, 0));
		stat.setSpeed(new WorldSpeed(10.27, WorldSpeed.M_sec));
		stat.setCourse(0);

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

		final long newTime = 4000;

		final CoreScenario dummyScenario = new CoreScenario();

		final TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 400, 400, "tpo.png",
				new WorldDistance(100, WorldDistance.METRES), false, true, false, "test observer", true);
		tpo.setup(dummyScenario);
		final ParticipantType cp = new Helo(100);
		cp.setName("helo alpha");
		cp.setCategory(new Category(Category.Force.RED, Category.Environment.AIRBORNE, Category.Type.HELO));

		for (int i = 0; i < 500000; i += 3000) {
			stat = cm.step(newTime + i, stat, hls, chars);
			tpo.processTheseDetails(new WorldLocation(stat.getLocation()), stat, cp);
		}

		tpo.tearDown(dummyScenario);

	}

	@Test
	public void testHighLevel() {
		final CoreMovement cm = new CoreMovement();

		WorldLocation.setModel(new CompletelyFlatEarth());

		final OnTopWaypoint myVisitor = new OnTopWaypoint();
		final WorldPath myPath = new WorldPath();
		myPath.addPoint(createLocation(000, 600));
		myPath.addPoint(createLocation(400, 600));
		myPath.addPoint(createLocation(600, 200));
		myPath.addPoint(createLocation(1000, 200));
		myPath.addPoint(createLocation(400, 300));
		myPath.addPoint(createLocation(600, 800));
		final HighLevelDemandedStatus hls = new HighLevelDemandedStatus(300, 200, 0, myPath, myVisitor, null);

		Status stat = new Status(0, 100);
		stat.setLocation(new WorldLocation(0, 0, 0));
		stat.setSpeed(new WorldSpeed(10.27, WorldSpeed.M_sec));
		stat.setCourse(0);

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

		final long newTime = 4000;

		final CoreScenario dummyScenario = new CoreScenario();

		final TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 400, 400, "tpo.png",
				new WorldDistance(100, WorldDistance.METRES), false, true, false, "test observer", true);
		tpo.setup(dummyScenario);
		final ParticipantType cp = new Helo(100);
		cp.setName("helo alpha");
		cp.setCategory(new Category(Category.Force.RED, Category.Environment.AIRBORNE, Category.Type.HELO));

		for (int i = 0; i < 500000; i += 3000) {
			stat = cm.step(newTime + i, stat, hls, chars);
			tpo.processTheseDetails(new WorldLocation(stat.getLocation()), stat, cp);
		}

		tpo.tearDown(dummyScenario);

	}

	@Test
	public void testSimple() {
		final CoreMovement cm = new CoreMovement();
		final Status stat = new Status(0, 100);
		stat.setLocation(new WorldLocation(0, 0, 0));
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final SimpleDemandedStatus simpleStat = new SimpleDemandedStatus(200, stat);
		simpleStat.setCourse(120);
		simpleStat.setSpeed(3);

		final String myName = "Merlin Trial";
		final double accelRate = 10;
		final double decelRate = 25;
		final double fuel_usage_rate = 0;
		final double maxSpeed = 100;
		final double minSpeed = -5;
		final double defaultClimbRate = 15;
		final double defaultDiveRate = 15;
		final double maxHeight = 400;
		final double minHeight = 000;
		final double myTurnRate = 3;
		final double defaultClimbSpeed = 15;
		final double defaultDiveSpeed = 20;

		final ASSET.Models.Movement.MovementCharacteristics chars = ASSET.Models.Movement.HeloMovementCharacteristics
				.generateDebug(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate,
						defaultDiveRate, maxHeight, minHeight, myTurnRate, defaultClimbSpeed, defaultDiveSpeed);

		final Status res = cm.step(300, stat, simpleStat, chars);

		assertNotNull(res, "status received");
		assertEquals(300, res.getTime(), "time updated");
	}
}
