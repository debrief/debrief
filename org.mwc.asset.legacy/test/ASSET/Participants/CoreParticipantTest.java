package ASSET.Participants;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Participants.CoreParticipant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.Participants.CoreParticipant.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.SensorType;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SSMovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class CoreParticipantTest {
	protected class DecidedListener implements ParticipantDecidedListener {
		@Override
		public void newDecision(final String state, final ASSET.Participants.DemandedStatus dem_stat) {
			dem_state = state;
			dem_status = dem_stat;
		}

		@Override
		public void restart(final ScenarioType scenario) {

		}
	}

	public class DetectedListener implements ParticipantDetectedListener {
		@Override
		public void newDetections(final ASSET.Models.Detection.DetectionList list) {
			newDetectionList = list;
		}

		@Override
		public void restart(final ScenarioType scenario) {

		}
	}

	protected class MovedListener implements ParticipantMovedListener {
		@Override
		public void moved(final ASSET.Participants.Status newStatus) {
			newStat = newStatus;
		}

		@Override
		public void restart(final ScenarioType scenario) {

		}
	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	ASSET.Participants.Status newStat = null;

	String dem_state = null;

	ASSET.Participants.DemandedStatus dem_status = null;

	ASSET.Models.Detection.DetectionList newDetectionList = null;


	@Test
	public void testParticipantStepping() {
		// setup the world model
		MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

		final ParticipantType part = new CoreParticipant(12);
		part.setMovementModel(new ASSET.Models.Movement.CoreMovement());
		part.setMovementChars(new SSMovementCharacteristics("here",
				new WorldAcceleration(1, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(1, WorldAcceleration.M_sec_sec), 0, new WorldSpeed(20, WorldSpeed.M_sec),
				new WorldSpeed(1, WorldSpeed.M_sec), new WorldDistance(200, WorldDistance.METRES),
				new WorldSpeed(1, WorldSpeed.M_sec), new WorldSpeed(1, WorldSpeed.M_sec),
				new WorldDistance(100, WorldDistance.METRES), new WorldDistance(1, WorldDistance.METRES)));

		// initialise the participant
		final Status newStat1 = new Status(part.getId(), 0);
		newStat1.setLocation(new MWC.GenericData.WorldLocation(0.0, 0.0, 0));
		newStat1.setCourse(45);
		newStat1.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		part.setStatus(newStat1);

		// create the dummy decision model
		final ASSET.Models.Decision.Movement.Transit transit = new ASSET.Models.Decision.Movement.Transit();
		transit.setLoop(false);
		final MWC.GenericData.WorldPath path = new MWC.GenericData.WorldPath();
		path.addPoint(new MWC.GenericData.WorldLocation(1, 1, 0));
		path.addPoint(new MWC.GenericData.WorldLocation(1.1, 0.9, 0));
		transit.setDestinations(path);
		part.setDecisionModel(transit);

		// setup the listeners
		final MovedListener mover = new MovedListener();
		part.addParticipantMovedListener(mover);

		final DecidedListener decider = new DecidedListener();
		part.addParticipantDecidedListener(decider);

		// give him some sensors

		// make first step
		final ASSET.Scenario.CoreScenario theScenario = new ASSET.Scenario.CoreScenario();
		part.doDecision(0, 10000, theScenario);
		part.doMovement(0, 10000, theScenario);
		part.doDetection(0, 10000, theScenario);

		assertEquals(10000, this.newStat.getTime(), "moved forward correct time");
		assertEquals(transit.getActivity(), this.dem_state, "new decision activity is correct");
		assertEquals(45d, ((SimpleDemandedStatus) this.dem_status).getCourse(), 0.001, "new dem state is correct");
		assertEquals(null, this.newDetectionList, "no detections made (no sensors)");

		// make second step
		part.doDecision(10000, 20000, theScenario);
		part.doMovement(10000, 20000, theScenario);
		part.doDetection(10000, 20000, theScenario);
		assertEquals(20000, this.newStat.getTime(), "moved forward correct time");
		assertEquals(transit.getActivity(), this.dem_state, "new decision activity is correct");
		assertEquals(45d, ((SimpleDemandedStatus) this.dem_status).getCourse(), 0.001, "new dem state is correct");
		assertEquals(null, this.newDetectionList, "no detections made (no sensors)");

	}
}
