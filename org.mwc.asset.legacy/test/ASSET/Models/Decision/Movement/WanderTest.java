package ASSET.Models.Decision.Movement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Movement.Wander.*;
import ASSET.Models.Decision.Movement.Wander.*;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/***********************************************************************
 * member variables
 ***********************************************************************/

public class WanderTest extends ASSET.Util.EditableTesting {

	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final Wander newWander = new Wander(createLocation(0, 0), new WorldDistance(21, WorldDistance.KM));
		newWander.setName("testing wander");
		return newWander;
	}

	/**
	 * test we handle missing demanded speed
	 */
	@Test
	public final void testNoDemCourseSpeed() {
		final WorldLocation origin = SupportTesting.createLocation(0, 0);
		final Wander tw = new Wander(origin, new WorldDistance(21, WorldDistance.KM));

		final Status stat = new Status(1, 0);
		stat.setLocation(origin.add(new WorldVector(100, WorldDistance.METRES, 0)));
		stat.setCourse(22);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		final DemandedStatus dem = null;

		final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat, null, dem, null, null, 100);

		assertNotNull(ds, "dem returned");
		assertEquals(22, ds.getCourse(), 0, "still on old course");
		assertEquals(12, ds.getSpeed(), 0, "still on old speed");
	}

	@Test
	public final void testWithDemCourseSpeed() {
		final WorldLocation origin = SupportTesting.createLocation(0, 0);
		final Wander tw = new Wander(origin, new WorldDistance(21, WorldDistance.KM));
		tw.setSpeed(new WorldSpeed(44, WorldSpeed.M_sec));
		tw.setHeight(new WorldDistance(45, WorldDistance.METRES));

		final Status stat = new Status(1, 0);
		stat.setLocation(origin.add(new WorldVector(100, WorldDistance.METRES, 0)));
		stat.setCourse(22);
		stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

		final DemandedStatus dem = null;

		final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat, null, dem, null, null, 100);

		assertNotNull(ds, "dem returned");
		assertEquals(45, ds.getHeight(), 0, "new demanded Height");
		assertEquals(44, ds.getSpeed(), 0, "new demanded speed");
	}
}
