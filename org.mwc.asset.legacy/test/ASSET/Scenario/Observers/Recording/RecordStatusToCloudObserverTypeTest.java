/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package ASSET.Scenario.Observers.Recording;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Scenario.Observers.Recording.RecordStatusToCloudObserverType.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ASSET.Scenario.Observers.Recording.RecordStatusToCloudObserverType.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.UserControl;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Sensor.Initial.OpticSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.CoreObserver;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.Track;
import MWC.Utilities.ReaderWriter.json.GNDDocHandler;
import MWC.Utilities.ReaderWriter.json.GNDStore;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public class RecordStatusToCloudObserverTypeTest {
	String _buildDate;
	String _headerDetails;
	boolean _detectionDetailsWritten;
	boolean _positionDetailsWritten;
	boolean _decisionDetailsWritten;

	// TODO FIX-TEST
	public void NtestToCloud() {
		final RecordStatusToCloudObserverType observer = new RecordStatusToCloudObserverType(true, true, true,
				new TargetType(Category.Type.SUBMARINE), "trial", true, "http://gnd.iriscouch.com", "tracks");
		assertNotNull(observer, "observer wasn't created");

		observer.setActive(true);

		// and the scenario
		final CoreScenario cs = new CoreScenario();
		cs.setName("RecordTrial");
		cs.setTime(new Date().getTime());
		cs.setScenarioStepTime(new Duration(5, Duration.MINUTES));

		// add a participant
		final SSN ssn = new SSN(12);
		ssn.setName("Subject98");
		ssn.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));
		ssn.setDecisionModel(new ASSET.Models.Decision.Tactical.Wait(new Duration(12, Duration.HOURS), "do wait"));
		final OpticSensor sampleSensor = new OpticSensor(12) {
			private static final long serialVersionUID = 1L;

			// what is the detection strength for this target?
			@Override
			protected DetectionEvent detectThis(final EnvironmentType environment, final ParticipantType host,
					final ParticipantType target1, final long time, final ScenarioType scenario) {
				final DetectionEvent de = new DetectionEvent(12l, 12, null, this, null, null, null, null, null,
						null, null, null, ssn);
				return de;
			}
		};
		ssn.addSensor(sampleSensor);
		final HeloMovementCharacteristics moveChars = (HeloMovementCharacteristics) HeloMovementCharacteristics
				.getSampleChars();
		moveChars.setTurnRate(0.02);
		moveChars.setDecelRate(new WorldAcceleration(0.05, WorldAcceleration.Kts_sec));
		moveChars.setAccelRate(new WorldAcceleration(0.05, WorldAcceleration.Kts_sec));
		moveChars.setMinSpeed(new WorldSpeed(4, WorldSpeed.Kts));
		ssn.setMovementChars(moveChars);
		final Status theStat = new Status(12, 12);
		theStat.setTime(new Date().getTime());
		theStat.setLocation(new WorldLocation(18, -23, 0));
		theStat.setSpeed(new WorldSpeed(32, WorldSpeed.Kts));
		theStat.setCourse(335);
		ssn.setStatus(theStat);

		final UserControl userControl = new UserControl(330d, new WorldSpeed(18, WorldSpeed.Kts),
				new WorldDistance(0, WorldDistance.METRES));
		ssn.setDecisionModel(userControl);

		cs.addParticipant(12, ssn);

		// initialise results instances
		_buildDate = null;
		_headerDetails = null;
		_detectionDetailsWritten = false;
		_positionDetailsWritten = false;
		_decisionDetailsWritten = false;

		// and do the setup
		observer.setup(cs);

		// do a step
		cs.step();

		// do lots more steps
		for (int i = 0; i < 50; i++) {
			// do a step
			cs.step();
		}

		userControl.setCourse(313);
		userControl.setSpeed(new WorldSpeed(3, WorldSpeed.Kts));

		// do lots more steps
		for (int i = 0; i < 50; i++) {
			// do a step
			cs.step();
		}

		userControl.setCourse(166);
		userControl.setSpeed(new WorldSpeed(19, WorldSpeed.Kts));

		// do lots more steps
		for (int i = 0; i < 50; i++) {
			// do a step
			cs.step();
		}

		userControl.setCourse(44);
		userControl.setSpeed(new WorldSpeed(9, WorldSpeed.Kts));

		for (int i = 0; i < 200; i++) {
			// do a step
			cs.step();
		}

		userControl.setCourse(200);
		userControl.setSpeed(new WorldSpeed(19, WorldSpeed.Kts));

		for (int i = 0; i < 300; i++) {
			// do a step
			cs.step();
		}

		// and the close
		observer.tearDown(cs);
	}

	@Test
	public void testDummy() {

	}
}
