package ASSET.Models.Decision.Tactical;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static ASSET.Models.Decision.Tactical.SternArcClearance.*;
import ASSET.Models.Decision.Tactical.SternArcClearance.*;
import java.beans.PropertyEditorSupport;
import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.ThreeDimMovementCharacteristics;
import ASSET.Models.Movement.TurnAlgorithm;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////
// support testing
//////////////////////////////////////////////////
public class SternArcClearanceTest extends ASSET.Util.EditableTesting {
	/**
	 * get an object which we can test
	 *
	 * @return Editable object which we can check the properties for
	 */
	@Override
	public Editable getEditable() {
		return new SternArcClearance();
	}

	@Test
	public void testDoubleTurn() {
		final SternArcClearance sac = new SternArcClearance(null, false, SternArcClearance.DOUBLE_LEG, null);
		Status theStat = new Status(12, 1000);
		theStat.setLocation(new WorldLocation(0, 0, 0));
		theStat.setCourse(10);
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final MovementCharacteristics theChars = ThreeDimMovementCharacteristics.getSampleChars(400);
		final DemandedStatus theDemStat = new SimpleDemandedStatus(12, theStat);
		final DetectionList theDets = null;
		final ScenarioActivityMonitor theMonitor = null;
		long time = 3000;
		SimpleDemandedStatus res = (SimpleDemandedStatus) sac.decide(theStat, theChars, theDemStat, theDets,
				theMonitor, time += 5000);
		assertNotNull(res, "Didn't return demanded status");

		// check we;re moving onto the correct course
		assertEquals(10 + SternArcClearance.DEFAULT_TURN, res.getCourse(), 0.01, "haven't adopted correct course");

		final TurnAlgorithm alg = new TurnAlgorithm();

		while (res.getCourse() == 10 + SternArcClearance.DEFAULT_TURN) {
			theStat = alg.doTurn(theStat, res, theChars, time);
			res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
		}

		assertEquals(10, res.getCourse(), 0.01, "not back on original course");

		while (res.getCourse() == 10) {
			theStat = alg.doTurn(theStat, res, theChars, time);
			res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
		}

		assertEquals(250, res.getCourse(), 0.01, "not on second leg");

		while (res.getCourse() == 250) {
			theStat = alg.doTurn(theStat, res, theChars, time);
			res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
		}

		assertEquals(10, res.getCourse(), 0.01, "not back on course");

		while (res != null) {
			theStat = alg.doTurn(theStat, res, theChars, time);
			res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
		}

		assertNull(res, "should have finished");
	}

	@Test
	public void testSingleShot() {
		final SternArcClearance sac = new SternArcClearance(null, false, SternArcClearance.SINGLE_LEG, null);
		Status theStat = new Status(12, 1000);
		theStat.setLocation(new WorldLocation(0, 0, 0));
		theStat.setCourse(10);
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		final MovementCharacteristics theChars = ThreeDimMovementCharacteristics.getSampleChars(400);
		final DemandedStatus theDemStat = new SimpleDemandedStatus(12, theStat);
		final DetectionList theDets = null;
		final ScenarioActivityMonitor theMonitor = null;
		long time = 3000;
		SimpleDemandedStatus res = (SimpleDemandedStatus) sac.decide(theStat, theChars, theDemStat, theDets,
				theMonitor, time += 5000);
		assertNotNull(res, "Didn't return demanded status");

		// check we;re moving onto the correct course
		assertEquals(10 + SternArcClearance.DEFAULT_TURN, res.getCourse(), 0.01, "haven't adopted correct course");

		final TurnAlgorithm alg = new TurnAlgorithm();

		while (res.getCourse() == 10 + SternArcClearance.DEFAULT_TURN) {
			theStat = alg.doTurn(theStat, res, theChars, time);
			res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
		}

		// check we're heading fwd again
		assertEquals(10, res.getCourse(), 0.01, "not back on straight course");

		while (res != null) {
			theStat = alg.doTurn(theStat, res, theChars, time);
			res = (SimpleDemandedStatus) sac.decide(theStat, theChars, res, theDets, theMonitor, time += 5000);
		}

		assertNull(res, "should have finished");
	}
}
