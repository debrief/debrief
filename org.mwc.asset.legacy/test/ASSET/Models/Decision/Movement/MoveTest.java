package ASSET.Models.Decision.Movement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Movement.Move.*;
import ASSET.Models.Decision.Movement.Move.*;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * ********************************************************************** test
 * this public class
 * **********************************************************************
 */

public class MoveTest extends ASSET.Util.EditableTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		final MWC.GUI.Editable ed = new Move();
		return ed;
	}

	@Test
	public void testTheMove() {
		// ok, create some locations
		final WorldLocation locA = new WorldLocation(0, 0, 12);
		final WorldLocation locC = new WorldLocation(0, 3, 12);

		final Move move = new Move();
		move.setCourse(new Double(60));
		move.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		move.setDistance(new WorldDistance(3, WorldDistance.DEGS));

		// check the initial values are correct
		assertEquals(move.getCourse().doubleValue(), 60, 0.01, "the course is set");
		assertEquals(move.getSpeed().getValueIn(WorldSpeed.M_sec), 12, 0.01, "the speed is set");
		assertEquals(move.getDistance().getValueIn(WorldDistance.DEGS), 3, 0.01, "the distance is set");

		// change course to head east (to ease our testing)
		move.setCourse(new Double(90));

		final Status theStat = new Status(12, 12000);
		theStat.setLocation(locA);
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

		DemandedStatus res = move.decide(theStat, null, null, null, null, 1000);

		// did it work?
		assertEquals(((SimpleDemandedStatus) res).getCourse(), 90, 0.01, "course set correctly");
		assertEquals(((SimpleDemandedStatus) res).getSpeed(), 12, 0.01, "speed set correctly");
		assertEquals(((SimpleDemandedStatus) res).getHeight(), -12, 0.01, "depth not set");

		assertEquals(move._theDestination.subtract(locC).getRange(), 0, 0.001, "target location set correctly");

		// fiddle with the depth
		move.setHeight(new WorldDistance(-55, WorldDistance.METRES));

		res = move.decide(theStat, null, null, null, null, 1000);

		// did it work?
		assertEquals(((SimpleDemandedStatus) res).getCourse(), 90, 0.01, "course set correctly");
		assertEquals(((SimpleDemandedStatus) res).getSpeed(), 12, 0.01, "speed set correctly");
		assertEquals(((SimpleDemandedStatus) res).getHeight(), -55, 0.01, "depth set");

	}
}
