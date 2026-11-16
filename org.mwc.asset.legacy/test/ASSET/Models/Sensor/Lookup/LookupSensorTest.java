package ASSET.Models.Sensor.Lookup;

import static ASSET.Models.Sensor.Lookup.LookupSensor.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.Models.Sensor.Lookup.LookupSensor.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.StopOnElapsedObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.Test;

// //////////////////////////////////////////////////////////
// TESTING CODE
// //////////////////////////////////////////////////////////
// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class LookupSensorTest extends SupportTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private static TestSensor getTestSensor(final double rp_m, final double ri_m, final double cr_m,
			final double ir_m, final double max_m) {
		return new TestSensor(0.05, 1000, 1.05, 0.8, new Duration(20, Duration.SECONDS), 0.2,
				new Duration(30, Duration.SECONDS), rp_m, ri_m, cr_m, ir_m, max_m);
	}


	@Test
	public void testClearingOldDetections() {
		// create the sensors
		final OpticLookupSensor os = OpticLookupSensor.getTestOpticSensor();
		os.setTimeBetweenDetectionOpportunities(5000);
		final RadarLookupSensor rs = RadarLookupSensorTest.getTestRadarSensor(4000, 3100, 2500, 1900,
				4700, 600);
		rs.setTimeBetweenDetectionOpportunities(8000);

		// and the participants
		final Status statA = new Status(12, 0);
		statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final Helo alpha = new Helo(12);
		alpha.setName("Alpha");
		alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		final WorldLocation originA = SupportTesting.createLocation(0, 0);
		statA.setLocation(originA);
		statA.getLocation().setDepth(-900);
		alpha.setStatus(statA);
		final Surface bravo = new Surface(11);
		final Status statB = new Status(12, 0);
		statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		bravo.setName("Bravo");
		bravo.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		final WorldLocation originB = SupportTesting.createLocation(5000, 5000);
		statB.setLocation(originB);
		statB.getLocation().setDepth(-20);
		bravo.setStatus(statB);
		final Surface charlie = new Surface(22);
		final Status statC = new Status(12, 0);
		statC.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		charlie.setName("charlie");
		charlie.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
		final WorldLocation originC = SupportTesting.createLocation(5050, 5000);
		statC.setLocation(originC);
		statC.getLocation().setDepth(-20);
		charlie.setStatus(statC);

		final SensorList sl = new SensorList();
		sl.add(os);
		sl.add(rs);

		alpha.setSensorFit(sl);

		// lastly the scenario
		final CoreScenario scenario = new CoreScenario();
		scenario.addParticipant(alpha.getId(), alpha);
		scenario.addParticipant(bravo.getId(), bravo);
		scenario.addParticipant(charlie.getId(), charlie);

		scenario.setScenarioStepTime(1000);

		// create our dummy environment object
		final OpticLookupSensorTest.MyEnvironment env = new OpticLookupSensorTest.MyEnvironment();

		// ok. create the empty detectionList
		final DetectionList dets = new DetectionList();

		// make env sensor friendly
		env.setSeaState(1);

		long curTime = 1000;

		// do we get anything?
		sl.detects(env, dets, alpha, scenario, curTime += 1000);

		// check that list still empty
		assertEquals(dets.size(), 2, "Obtained first detections");

		// right, get the detections
		DetectionEvent d1 = dets.elementAt(0);
		DetectionEvent d2 = dets.elementAt(1);

		// keep moving forward,
		sl.detects(env, dets, alpha, scenario, curTime += 1000);

		// check that we still have the old detections
		assertFalse(dets.contains(d1), "last contact deleted");
		assertFalse(dets.contains(d2), "last contact deleted");

		// keep moving forward,
		sl.detects(env, dets, alpha, scenario, curTime += 1000);

		// check that we still have the old detections
		assertFalse(dets.contains(d1), "last contact deleted");
		assertFalse(dets.contains(d2), "last contact deleted");

		// keep moving forward,
		sl.detects(env, dets, alpha, scenario, curTime += 1000);

		// check that we still have the old detections
		assertFalse(dets.contains(d1), "last contact deleted");
		assertFalse(dets.contains(d2), "last contact deleted");

		// keep moving forward,
		sl.detects(env, dets, alpha, scenario, curTime += 1000);

		// check that we still have the old detections
		assertFalse(dets.contains(d1), "last contact deleted");
		assertFalse(dets.contains(d2), "last contact deleted");

		// keep moving forward,
		sl.detects(env, dets, alpha, scenario, curTime += 1000);

		// check that we have removed the old detections
		assertFalse(dets.contains(d1), "should have ditched last contact");
		assertFalse(dets.contains(d2), "should have ditched last contact");
		d1 = dets.elementAt(0);
		d2 = dets.elementAt(1);

		// check that we still have the old detections
		assertTrue(dets.contains(d1), "still have last contact");
		assertTrue(dets.contains(d2), "still have last contact");

		// check that we have new contacts
		assertEquals(2, dets.size(), "have new contacts");

		// change range so that we know we won't get detection
		alpha.getStatus().setLocation(SupportTesting.createLocation(90000, 90000));

		// keep moving forward,
		sl.detects(env, dets, alpha, scenario, curTime += 3000);

		// check that we have removed the old detections and that we still have
		// the old detections
		assertFalse(dets.contains(d1), "last contact deleted");
		assertFalse(dets.contains(d2), "last contact deleted");
		assertEquals(0, dets.size(), "should have kept contacts, havne't done next scan yet");

		// keep moving forward,
		curTime += 3000;
		sl.detects(env, dets, alpha, scenario, curTime += 3000);

		// right, passed time for next scan. check that old detections removed,
		// and no new ones added.
		assertFalse(dets.contains(d1), "still have last contact");
		assertFalse(dets.contains(d2), "still have last contact");
		assertEquals(0, dets.size(), "should have lost contacts");

	}

	@Test
	public final void testFirst() {
		final TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
		long time = 1000;
		final Status statA = new Status(12, 0);
		statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final Helo alpha = new Helo(12);
		alpha.setName("Alpha");
		final WorldLocation originA = SupportTesting.createLocation(0, 0);
		statA.setLocation(originA);
		alpha.setStatus(statA);
		final Surface bravo = new Surface(11);
		final Status statB = new Status(12, 0);
		statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		bravo.setName("Bravo");
		final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
		statB.setLocation(originB);
		bravo.setStatus(statB);

		alpha.addSensor(ts);
		final CoreScenario scenario = new CoreScenario();
		scenario.addParticipant(alpha.getId(), alpha);
		scenario.addParticipant(bravo.getId(), bravo);

		final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

		final Vector<Integer> newParams = new Vector<Integer>();
		newParams.add(new Integer(12));
		TestSensor.myParams = newParams;

		final DetectionList res = new DetectionList();
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(0, res.size(), "null detections returned");

		// ok. move the targets a little closer
		statB.setLocation(SupportTesting.createLocation(800, 800));

		// just do a check that no returns are produced if it's not time f
		// for another scan
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(0, res.size(), "zero detections returned (because it's not time yet)");

		time += 1000;
		ts.detects(env, res, alpha, scenario, time);

		// ok, now move forward and try again
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		// and move closer still!!
		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		// do another check for when we haven't moved forward enough
		time += 100;
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(0, res.size(), "old detections wiped");

		// now move forward in time though
		time += 1000;

		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		// time should be elapsed by now!!!
		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(500, 500));
		ts.detects(env, res, alpha, scenario, time);
		time += 9000;
		assertEquals(1, res.size(), "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(500, 500));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertEquals(1, res.size(), "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.IDENTIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		// INCREASE THE RANGE AGAIN!
		statB.setLocation(SupportTesting.createLocation(900, 900));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertEquals(1, res.size(), "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.IDENTIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		// AND AGAIN
		statB.setLocation(SupportTesting.createLocation(1200, 1200));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertEquals(1, res.size(), "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.IDENTIFIED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		// AND YET AGAIN (out of range!)
		statB.setLocation(SupportTesting.createLocation(5200, 5200));
		ts.detects(env, res, alpha, scenario, time);
		time += 5000;
		assertEquals(0, res.size(), "no detections returned (out of range)");

		// Let's go into range again!
		statB.setLocation(SupportTesting.createLocation(500, 500));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertEquals(1, res.size(), "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

	}

	@Test
	public void testForceProtection() {
		final Status start = new Status(12, 0);
		final WorldLocation origin = SupportTesting.createLocation(0, 0);
		origin.setDepth(-500);
		start.setLocation(origin);
		start.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));

	}

	@Test
	public void testMultipleContacts() {

		final TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
		long time = 1000;
		final Status statA = new Status(12, 0);
		statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final Helo test2alpha = new Helo(12);
		test2alpha.setName("Alpha");
		final WorldLocation originA = SupportTesting.createLocation(0, 0);
		statA.setLocation(originA);
		test2alpha.setStatus(statA);

		final Surface test2bravo = new Surface(11);
		final Status statB = new Status(12, 0);
		statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		test2bravo.setName("Bravo");
		final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
		statB.setLocation(originB);
		test2bravo.setStatus(statB);

		final Surface test2charlie = new Surface(21);
		final Status statC = new Status(12, 0);
		statC.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		test2charlie.setName("Charlie");
		final WorldLocation originC = SupportTesting.createLocation(4000, 4000);
		statC.setLocation(originC);
		test2charlie.setStatus(statC);

		test2alpha.addSensor(ts);
		final CoreScenario scenario = new CoreScenario();
		scenario.addParticipant(test2alpha.getId(), test2alpha);
		scenario.addParticipant(test2bravo.getId(), test2bravo);
		scenario.addParticipant(test2charlie.getId(), test2charlie);

		final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

		final Vector<Integer> newParams = new Vector<Integer>();
		newParams.add(new Integer(12));
		TestSensor.myParams = newParams;

		final DetectionList res = new DetectionList();
		ts.detects(env, res, test2alpha, scenario, time);
		assertEquals(0, res.size(), "zero detections returned (because it's not time yet)");
		time += 1000;

		// ok. move the targets a little closer
		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");

		// and move closer still!!
		statB.setLocation(SupportTesting.createLocation(800, 800));
		statC.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(2, res.size(), "two detections produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		statC.setLocation(SupportTesting.createLocation(5800, 5800));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		statC.setLocation(SupportTesting.createLocation(500, 500));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(2, res.size(), "two detections produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		statC.setLocation(SupportTesting.createLocation(500, 500));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(2, res.size(), "two detections produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		statC.setLocation(SupportTesting.createLocation(500, 500));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(2, res.size(), "two detections produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		statC.setLocation(SupportTesting.createLocation(500, 500));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(2, res.size(), "two detections produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(4800, 4800));
		statC.setLocation(SupportTesting.createLocation(500, 500));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "one detection produced");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(400, 400));
		statC.setLocation(SupportTesting.createLocation(200, 200));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(2, res.size(), "two detections produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");
		assertEquals(DetectionEvent.CLASSIFIED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(400, 400));
		statC.setLocation(SupportTesting.createLocation(200, 200));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(2, res.size(), "two detections produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");
		assertEquals(DetectionEvent.IDENTIFIED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(2400, 2400));
		statC.setLocation(SupportTesting.createLocation(2200, 2200));
		ts.detects(env, res, test2alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(2, res.size(), "two detections produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(test2bravo).getDetectionState(),
				"check detected state");
		assertEquals(DetectionEvent.IDENTIFIED, ts._pastContacts.get(test2charlie).getDetectionState(),
				"check detected state");

	}

	@Test
	public final void testPersistentData() {
		final TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
		long time = 1000;
		final Status statA = new Status(12, 0);
		statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final Helo alpha = new Helo(12);
		alpha.setName("Alpha");
		final WorldLocation originA = SupportTesting.createLocation(0, 0);
		statA.setLocation(originA);
		alpha.setStatus(statA);
		final Surface bravo = new Surface(11);
		final Status statB = new Status(12, 0);
		statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		bravo.setName("Bravo");
		final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
		statB.setLocation(originB);
		bravo.setStatus(statB);

		alpha.addSensor(ts);
		final CoreScenario scenario = new CoreScenario();
		scenario.addParticipant(alpha.getId(), alpha);
		scenario.addParticipant(bravo.getId(), bravo);

		final EnvironmentType env = new SimpleEnvironment(1, 1, 1);

		final Vector<Integer> newParams = new Vector<Integer>();
		newParams.add(new Integer(12));
		TestSensor.myParams = newParams;

		final DetectionList res = new DetectionList();
		ts.detects(env, res, alpha, scenario, time);
		assertEquals(0, res.size(), "zero detections returned (because it's not time yet)");
		time += 1000;

		// ok. move the targets a little closer
		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		// and move closer still!!
		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		statB.setLocation(SupportTesting.createLocation(800, 800));
		ts.detects(env, res, alpha, scenario, time);
		time += 10000;
		assertNotNull(res, "detections returned");
		assertEquals(1, res.size(), "only one detection produced");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

		// change the detection parameters
		newParams.add(new Integer(32));
		TestSensor.myParams = newParams;
		ts._ri_m = 3300;

		// find out what the current RI is.
		LastTargetContact tc = ts._pastContacts.get(bravo);
		final WorldDistance lastRI = tc.getRI();
		ts.detects(env, res, alpha, scenario, time);
		time += 1000;
		// check that the RI has changed
		tc = ts._pastContacts.get(bravo);
		final WorldDistance newRI = tc.getRI();

		final boolean isSame = newRI.getValueIn(WorldDistance.METRES) == lastRI.getValueIn(WorldDistance.METRES);
		assertTrue(!isSame, "RI has changed");
		assertNotNull(res, "detections returned");
		assertEquals(DetectionEvent.DETECTED, ts._pastContacts.get(bravo).getDetectionState(),
				"check detected state");

	}

	@Test
	public void testThresholds() {
		final int RP__M = 4000;
		final int ri_m = 3100;
		final int cr_m = 2500;
		final int ir_m = 1900;
		final int max_m = 4700;
		final TestSensor ts = getTestSensor(RP__M, ri_m, cr_m, ir_m, max_m);
		final Status statA = new Status(12, 0);
		statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		statA.setCourse(28);
		final Helo alpha = new Helo(12);
		alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
		alpha.setName("Alpha");
		final WorldLocation originA = SupportTesting.createLocation(0, 0);
		statA.setLocation(originA);
		alpha.setStatus(statA);
		final Surface bravo = new Surface(11);
		bravo.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE));
		final Status statB = new Status(12, 0);
		statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		statB.setCourse(225);
		bravo.setName("Bravo");
		final WorldLocation originB = SupportTesting.createLocation(6000, 6000);
		statB.setLocation(originB);
		bravo.setStatus(statB);

		alpha.setMovementChars(HeloMovementCharacteristics.getSampleChars());
		bravo.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

		alpha.addSensor(ts);
		final CoreScenario scenario = new CoreScenario();
		scenario.addParticipant(alpha.getId(), alpha);
		scenario.addParticipant(bravo.getId(), bravo);

		scenario.setStepTime(5000);
		scenario.setScenarioStepTime(1000);

		alpha.addParticipantDetectedListener(new ParticipantDetectedListener() {

			int lastState = DetectionEvent.UNDETECTED;

			public void checkDetectionThreshold(final int newState, final double range_m) {
				switch (newState) {
				case (DetectionEvent.DETECTED):
					assertEquals(range_m, ri_m, 30, "detection range");
					break;
				case (DetectionEvent.CLASSIFIED):
					assertEquals(range_m, cr_m, 30, "detection range");
					break;
				case (DetectionEvent.IDENTIFIED):
					assertEquals(range_m, ir_m, 30, "detection range");
					break;
				case (DetectionEvent.UNDETECTED):
					assertEquals(range_m, max_m, 30, "detection range");
					break;
				}

			}

			@Override
			public void newDetections(final DetectionList detections) {
				if (detections.size() > 0) {
					final DetectionEvent de = detections.getDetection(0);

					if (de.getDetectionState() != lastState) {
						lastState = de.getDetectionState();
						final WorldDistance wd = LookupSensor.calculateSlantRangeFor(
								alpha.getStatus().getLocation(), bravo.getStatus().getLocation());
						checkDetectionThreshold(de.getDetectionState(), wd.getValueIn(WorldDistance.METRES));
					}
				}
			}

			@Override
			public void restart(final ScenarioType scenario) {
			}

		});

		final ASSET.Scenario.Observers.Recording.DebriefReplayObserver dr = new DebriefReplayObserver(
				"test_reports", null, true, "record file", true);
		dr.setup(scenario);

		for (int i = 0; i < 4000; i++) {
			if (scenario.getTime() == 690000)
				System.out.println("here");

			scenario.step();
		}

		dr.tearDown(scenario);

	}

	@Test
	public void testTutorialScenario() {
		final CoreScenario cs = new CoreScenario();
		try {
			final String fName = "../org.mwc.asset.core.feature/root_installs/AssetData/Samples/legacy/lookup_tutorial_scenario.xml";
			final File tFile = new File(fName);
			assertTrue(tFile.exists(), "sceanrio not found");
			final java.io.FileInputStream fis = new java.io.FileInputStream(fName);
			ASSET.Util.XML.ASSETReaderWriter.importThis(cs, fName, fis);
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}

		// so, do we have our scenario?
		assertNotNull(cs, "scenario loaded");

		// hmm, have all the vessels loaded?
		assertEquals(4, cs.getListOfParticipants().length, 0, "all parts loaded");

		// run through for a few hours, recording to file
		final DebriefReplayObserver dro = new DebriefReplayObserver("./test_reports", "test_tutorial.rep", true,
				"test observer", true);
		dro.setup(cs);

		final ASSET.Scenario.Observers.StopOnElapsedObserver to = new StopOnElapsedObserver(0, 3, 0, 0,
				"Test time observer", true);
		to.setup(cs);

		while (!to.hasStopped()) {
			cs.step();
		}

		to.tearDown(cs);
		dro.tearDown(cs);

	}
}
