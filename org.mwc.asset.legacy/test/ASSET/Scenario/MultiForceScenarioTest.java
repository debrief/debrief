package ASSET.Scenario;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Scenario.MultiForceScenario.*;
import ASSET.Scenario.MultiForceScenario.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class MultiForceScenarioTest {
	protected class createdListener implements ParticipantsChangedListener {
		/**
		 * the indicated participant has been added to the scenario
		 */
		@Override
		public void newParticipant(final int index) {
			createdCounter++;
		}

		/**
		 * the indicated participant has been removed from the scenario
		 */
		@Override
		public void participantRemoved(final int index) {
			destroyedCounter++;
		}

		/**
		 * the scenario has restarted
		 */
		@Override
		public void restart(final ScenarioType scenario) {
		}

	}

	protected class startStopListener implements ScenarioRunningListener {
		@Override
		public void finished(final long elapsedTime, final String reason) {
			lastStartState = new Boolean(false);
		}

		@Override
		public void newScenarioStepTime(final int val) {
			newStepTime = val;
		}

		@Override
		public void newStepTime(final int val) {
			newStepTime = val;
		}

		/**
		 * the scenario has stopped running on auto
		 */
		@Override
		public void paused() {
			// let's not worry about this little thing
		}

		/**
		 * the scenario has restarted
		 */
		@Override
		public void restart(final ScenarioType scenario) {
		}

		@Override
		public void started() {
			lastStartState = new Boolean(true);
		}

	}

	protected class stepListener implements ScenarioSteppedListener {
		/**
		 * the scenario has restarted
		 */
		@Override
		public void restart(final ScenarioType scenario) {
		}

		@Override
		public void step(final ScenarioType scenario, final long newTime) {
			lastTime = newTime;
			stepCounter++;
		}

	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";
	protected int stepCounter = 0;
	protected long lastTime = 0;
	protected Boolean lastStartState = null;

	protected int createdCounter = 0;

	protected int destroyedCounter = 0;

	int newStepTime;


	@Test
	public void testScenarioParticipants() {

		//
		// // create server
		// final MultiForceScenario srv = new MultiForceScenario();
		//
		// // add as listener
		// final createdListener cl = new createdListener();
		// srv.addParticipantsChangedListener(cl);
		//
		// SSMovementCharacteristics ssm = new SSMovementCharacteristics("blank",
		// 1, 1, 0.0, 20, 1, 300, 1, 1, 100, 1);
		// SurfaceMovementCharacteristics sur = new
		// SurfaceMovementCharacteristics("surf",
		// 1, 1, 0.0, 22, 1, 399);
		//
		// // create the red participants
		// final ASSET.Participants.Status stat = new ASSET.Participants.Status(2, 12);
		// final MWC.GenericData.WorldLocation origin = new
		// MWC.GenericData.WorldLocation(1, 1, 1);
		// stat.setLocation(origin);
		// stat.setCourse(0);
		// stat.setSpeed(new WorldSpeed(3, WorldSpeed.M_sec));
		// final ASSET.ParticipantType ssn = new ASSET.Models.Vessels.SSN(2, stat, null,
		// "SSN");
		// ssn.setMovementChars(ssm);
		// final ASSET.ParticipantType frig = new ASSET.Models.Vessels.Surface(3, stat,
		// null,
		// "Frigate");
		// frig.setMovementChars(sur);
		//
		// // create the blue participants
		// final ASSET.ParticipantType ssk1 = new ASSET.Models.Vessels.SSK(11, stat,
		// null, "SSK1");
		// ssk1.setMovementChars(ssm);
		// final ASSET.ParticipantType ssk2 = new ASSET.Models.Vessels.SSK(12, stat,
		// null, "SSK2");
		// ssk2.setMovementChars(ssm);
		// final ASSET.ParticipantType ssk3 = new ASSET.Models.Vessels.SSK(13, stat,
		// null, "SSK3");
		// ssk3.setMovementChars(ssm);
		//
		// // add the blues
		// srv.addBlueParticipant(ssn.getId(), ssn);
		// srv.addBlueParticipant(frig.getId(), frig);
		//
		// // give the ssn a sensor
		// final ASSET.Models.Sensor.CoreSensor sensor = new
		// ASSET.Models.Sensor.Initial.BroadbandSensor(22);
		// ssn.addSensor(sensor);
		//
		// // give the ssn a sensor
		// ssk1.addSensor(sensor);
		//
		// // add the blues
		// srv.addParticipant(ssk1.getId(), ssk1);
		// srv.addParticipant(ssk2.getId(), ssk2);
		// srv.addParticipant(ssk3.getId(), ssk3);
		//
		// // check the sizes
		// final Integer[] blues = srv.getListOfBlueParticipants();
		// assertEquals("blue participant size", 2, blues.length);
		//
		// final Integer[] reds = srv.getListOfRedParticipants();
		// assertEquals("red participant size", 3, reds.length);
		//
		// // try to remove a blue
		// srv.removeParticipant(ssk1.getId());
		// assertEquals("red participant size", 2,
		// srv.getListOfRedParticipants().length);
		//
		// final int lenBefore = srv.getListOfBlueParticipants().length;
		// srv.removeParticipant(ssn.getId());
		// assertEquals("blue participant size", lenBefore - 1,
		// srv.getListOfBlueParticipants().length);
		//
		//
		// // check stepping through
		// srv.step();

		// todo: reinstate tests
	}

}
