package ASSET.Models.Decision.Movement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Movement.Trail.*;
import ASSET.Models.Decision.Movement.Trail.*;
import ASSET.NetworkParticipant;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class TrailTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final Trail followBlue = new Trail(new WorldDistance(2000, WorldDistance.YARDS));
		return followBlue;
	}

	@Test
	public void testGettingDemanded() {
		// //////////////////////////////////////////////////////////////
		// handle missing bearing

		// //////////////////////////////////////////////////////////////
		// handle missing range

	}

	@Test
	public void testTheTrail() {
		// setup the scenario
		final Trail followBlue = new Trail(new WorldDistance(2000, WorldDistance.YARDS));

		// setup the target
		final TargetType tgtType = new TargetType();
		tgtType.addTargetType(ASSET.Participants.Category.Type.SUBMARINE);
		followBlue.setTargetType(tgtType);

		// setup the status
		final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(0, 0, 0);
		final ASSET.Participants.Status stat = new ASSET.Participants.Status(0, 1000);
		stat.setLocation(origin);
		stat.setCourse(0);
		stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

		// create the host
		final NetworkParticipant host = new ASSET.Models.Vessels.Surface(12);
		final CoreSensor sensor = new ASSET.Models.Sensor.Initial.BroadbandSensor(22);

		// and the detection list
		ASSET.Models.Detection.DetectionList list = new ASSET.Models.Detection.DetectionList();

		// /////////////////////////////////////////////////////////////
		// what do we do when there are no detections?

		// null detections
		DemandedStatus dem = followBlue.decide(stat, null, null, null, null, 2000);
		assertEquals(null, dem, "return null when no detections");

		// zero detections
		dem = followBlue.decide(stat, null, null, list, null, 2000);
		assertEquals(null, dem, "return null when zero detections");

		// //////////////////////////////////////////////////////////////
		// handle detections, but not of valid target
		final ASSET.Participants.Category tgt = new ASSET.Participants.Category();
		tgt.setForce(ASSET.Participants.Category.Force.RED);
		tgt.setType(ASSET.Participants.Category.Type.CARRIER);
		tgt.setEnvironment(ASSET.Participants.Category.Environment.SURFACE);

		final ASSET.Models.Detection.DetectionEvent det1 = new DetectionEvent(2000, host.getId(), null, sensor,
				null, null, null, null, null, tgt, null, null, new CoreParticipant(12));

		list.add(det1);

		dem = followBlue.decide(stat, null, null, list, null, 2000);
		assertEquals(null, dem, "return null when invalid detections");

		// //////////////////////////////////////////////////////////////
		// handle detections including valid target

		final ASSET.Participants.Category tgt2 = new ASSET.Participants.Category();
		tgt2.setForce(ASSET.Participants.Category.Force.RED);
		tgt2.setType(ASSET.Participants.Category.Type.SUBMARINE);
		tgt2.setEnvironment(ASSET.Participants.Category.Environment.SUBSURFACE);
		final ASSET.Models.Detection.DetectionEvent det2 = new ASSET.Models.Detection.DetectionEvent(2000,
				host.getId(), stat.getLocation(), sensor, new WorldDistance(2000, WorldDistance.YARDS),
				new WorldDistance(2000, WorldDistance.YARDS), new Float(33), new Float(44), null, tgt2, null, null,
				new CoreParticipant(12));

		list.add(det2);

		dem = followBlue.decide(stat, null, null, list, null, 2000);
		SimpleDemandedStatus sds = (SimpleDemandedStatus) dem;
		assertTrue((dem != null), "returned demanded status");
		assertEquals(33, sds.getCourse(), 0.001, "returned valid course to target");

		// //////////////////////////////////////////////////////////////
		// handle loss of valid target
		list = new ASSET.Models.Detection.DetectionList();
		list.add(det1);

		dem = followBlue.decide(stat, null, null, list, null, 2100);
		assertNotNull(dem, "return null when lost contact");

		dem = followBlue.decide(stat, null, null, list, null, 2000 + followBlue._endStayOnBearing + 500);
		assertEquals(null, dem, "return null when time expired");

		// //////////////////////////////////////////////////////////////
		// regain target
		list.add(det2);

		dem = followBlue.decide(stat, null, null, list, null, 2000);
		sds = (SimpleDemandedStatus) dem;
		assertTrue((dem != null), "returned demanded status");
		assertEquals(33, sds.getCourse(), 0.001, "returned valid course to target");

	}
}
