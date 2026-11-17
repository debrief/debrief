package ASSET.Models.Decision.Tactical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.SSKRecharge.*;
import ASSET.Models.Decision.Tactical.SSKRecharge.*;
import ASSET.ParticipantType;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * ************************************************* testing
 * *************************************************
 */
public class SSKRechargeTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final SSKRecharge recharge = new SSKRecharge();
		return recharge;
	}

	/**
	 * test we decide to snort correctly
	 */
	@Test
	public void testSnorting() {
		// setup our scenario
		final SSKRecharge recharge = new SSKRecharge();
		recharge.setMinLevel(20);
		recharge.setSafeLevel(80);
		recharge.setSnortSpeed(new WorldSpeed(6, WorldSpeed.M_sec));
		recharge.setTargetToEvade(null);

		// check when we've no target to avoid
		final Status theStat = new Status(12, 0);
		theStat.setCourse(12);
		theStat.setFuelLevel(100);
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		theStat.setLocation(new WorldLocation(12, 12, 33));

		final Category friendly = new Category(Category.Force.RED, Category.Environment.SUBSURFACE,
				Category.Type.SUBMARINE);

		// see what the plan is
		DemandedStatus de = recharge.decide(theStat, null, null, null, null, 12);

		// check we don't need to recharge
		assertEquals(de, null, "Check we don't need to recharge with full box");

		// check we need to recharge at limit
		theStat.setFuelLevel(20);
		de = recharge.decide(theStat, null, null, null, null, 12);
		assertNotNull(de, "Check we recharge at limit");

		// check we need to recharge under limit
		theStat.setFuelLevel(18);
		de = recharge.decide(theStat, null, null, null, null, 12);
		assertNotNull(de, "Check we recharge under limit");

		// check we recharge with friendly contacts
		final DetectionList dl = new DetectionList();
		final ASSET.Models.Sensor.Initial.OpticSensor optic = new ASSET.Models.Sensor.Initial.OpticSensor(12);
		final ParticipantType cp = new CoreParticipant(12);
		final DetectionEvent dEvent = new DetectionEvent(0, cp.getId(), null, optic, null, null, null, null, null,
				friendly, null, null, cp);
		dl.add(dEvent);
		// set the avoid category to blue vessels
		recharge.setTargetToEvade(new TargetType(Category.Force.BLUE));

		// see what happens now
		de = recharge.decide(theStat, null, null, dl, null, 12);
		assertNotNull(de, "Check don't avoid friendly");

		// see about avoiding hostile vessels
		// change it so we want to avoid friendly vessels
		recharge.setTargetToEvade(new TargetType(Category.Force.RED));
		de = recharge.decide(theStat, null, null, dl, null, 12);
		assertNull(de, "Check don't snort with hostile");

		theStat.setFuelLevel(80);
		recharge.setTargetToEvade(new TargetType(Category.Force.BLUE));
		de = recharge.decide(theStat, null, null, dl, null, 12);
		assertNull(de, "Check don't snort when not needed");
	}

}
