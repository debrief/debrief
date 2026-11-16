package ASSET.Models.Sensor.Initial;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Sensor.Initial.NarrowbandSensor.*;
import ASSET.Models.Sensor.Initial.NarrowbandSensor.*;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.NarrowbandRadNoise;
import ASSET.Models.Vessels.SSN;
import ASSET.Models.Vessels.Surface;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class NarrowbandSensorTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new NarrowbandSensor(12);
	}

	@Test
	public void testSteadyTime() {
		final NarrowbandSensor ns = new NarrowbandSensor(22);
		ns.setName("ian test");
		ns.setSteadyTime(new Duration(2, Duration.MINUTES));

		// create valid locations
		final WorldLocation loca = SupportTesting.createLocation(100, 1000);
		final WorldLocation locb = SupportTesting.createLocation(200, 1000);

		long time = 0;

		final EnvironmentType env = new SimpleEnvironment(1, 1, 1);
		final DetectionList dets = new DetectionList();
		final SSN me = new SSN(12);
		final Status stat = new Status(12, 0);
		stat.setCourse(11);
		stat.setLocation(loca);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		me.setStatus(stat);

		final Surface him = new Surface(122);
		final Status statb = new Status(32, 0);
		statb.setCourse(13);
		statb.setLocation(locb);
		statb.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		him.setStatus(statb);
		him.getRadiatedChars().add(EnvironmentType.NARROWBAND, new NarrowbandRadNoise(222, 12));

		final CoreScenario theScenario = new CoreScenario();
		theScenario.addParticipant(him.getId(), him);

		// check dets is empty
		assertEquals(0, dets.size(), "dets starts empty");

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(1, dets.size(), "should have gained at least one detection");

		// ok, move time forward. check we get another
		time += 1000;

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(1, dets.size(), "should have gained at least one detection");

		// ok, move time forward. check we get another
		time += 1000;

		// and change our course
		me.getStatus().setCourse(22);

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(0, dets.size(), "should not have gained any more detections");
		assertEquals(122000, ns._timeArraySteady, 0, "correct steady time set");

		// ok try another cycle
		time += 1000;

		// and change our course
		me.getStatus().setCourse(23);

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(0, dets.size(), "should not have gained any more detections");
		assertEquals(123000, ns._timeArraySteady, 0, "correct steady extended time set");
		// ok try another cycle
		time += 10000;

		// and change our course
		me.getStatus().setCourse(24);

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(0, dets.size(), "should not have gained any more detections");
		assertEquals(133000, ns._timeArraySteady, 0, "correct steady extended time set");

		// AND BACK ON COURSE
		time += 10000;

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(0, dets.size(), "should not have gained any more detections");
		assertEquals(133000, ns._timeArraySteady, 0, "correct steady extended time set");

		// STAY ON COURSE
		time += 10000;

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(0, dets.size(), "should not have gained any more detections");
		assertEquals(133000, ns._timeArraySteady, 0, "correct steady extended time set");

		// MAKE TIME ELAPSED VALID
		time = 133000;

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(1, dets.size(), "should not have gained any more detections");
		assertEquals(133000, ns._timeArraySteady, 0, "correct steady extended time set");

		// MAKE TIME ELAPSED VALID
		time = 134000;

		ns.detects(env, dets, me, theScenario, time);

		assertEquals(1, dets.size(), "should not have gained any more detections");
		assertEquals(133000, ns._timeArraySteady, 0, "correct steady extended time set");

	}

}
