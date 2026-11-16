package ASSET.Models.Decision.Tactical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.BearingTrail.*;
import ASSET.Models.Decision.Tactical.BearingTrail.*;
import ASSET.ParticipantType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Movement.Trail;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class BearingTrailTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final BearingTrail bt = new BearingTrail(new WorldDistance(1200, WorldDistance.YARDS));
		return bt;
	}

	@Test
	public void testBearingTrail() {

		final double rngToHim = 10000;
		final double crseToHim = 45;
		final double brgToHim = 110;

		// final MWC.GenericData.WorldLocation origin = new
		// MWC.GenericData.WorldLocation(0,0,0);
		// final MWC.GenericData.WorldLocation hisLoc = origin.add(new
		// MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(crseToHim),
		// MWC.Algorithms.Conversions.Yds2Degs(rngToHim),
		// 0));

		// setup a couple of targets
		final ASSET.Models.Vessels.Surface ssn = new ASSET.Models.Vessels.Surface(1);
		ssn.setName("ssn");
		ssn.setCategory(new ASSET.Participants.Category(Category.Force.RED, Category.Environment.SUBSURFACE,
				Category.Type.SUBMARINE));

		final ParticipantType target = new ASSET.Models.Vessels.Surface(1);
		target.setName("some target");

		final ASSET.Models.Sensor.Initial.BroadbandSensor bb = new ASSET.Models.Sensor.Initial.BroadbandSensor(12);
		final ASSET.Models.Detection.DetectionEvent de = new ASSET.Models.Detection.DetectionEvent(100l,
				ssn.getId(), new WorldLocation(0, 0, 0), bb, new WorldDistance(rngToHim, WorldDistance.YARDS),
				new WorldDistance(rngToHim, WorldDistance.YARDS), new Float(brgToHim), new Float(crseToHim),
				new Float(10), ssn.getCategory(), null, new Float(350), target);

		final ASSET.Models.Detection.DetectionList dl = new ASSET.Models.Detection.DetectionList();
		dl.add(de);

		final TargetType tt = new TargetType(ASSET.Participants.Category.Type.SUBMARINE);

		final BearingTrail bt = new BearingTrail(new WorldDistance(1200, WorldDistance.YARDS));
		bt.setTrailBearing(145.0);
		bt.setTrailRange(new WorldDistance(8000, WorldDistance.YARDS));
		bt.setTargetType(tt);

		final ASSET.Participants.Status status = new ASSET.Participants.Status(0, 0);
		status.setCourse(100);
		status.setLocation(new MWC.GenericData.WorldLocation(0, 0, 0));
		status.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		ASSET.Participants.DemandedStatus dec = bt.decide(status, null, null, dl, null, 100);

		// now try another trail location
		bt.setTrailBearing(335.0);
		bt.setTrailRange(new WorldDistance(8000, WorldDistance.YARDS));

		dec = bt.decide(status, null, null, dl, null, 100);

		// now try another trail location
		bt.setTrailBearing(225.0);
		bt.setTrailRange(new WorldDistance(11000, WorldDistance.YARDS));

		dec = bt.decide(status, null, null, dl, null, 100);

		assertNotNull(dec, "I'm alive");

		// @@@ testing this, produce results on paper first!
		// produce genuinely flat model, & allow us to assign it to WorldLocation
	}
}
