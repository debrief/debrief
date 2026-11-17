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

import org.junit.jupiter.api.Test;

import static ASSET.Scenario.Observers.Recording.RecordStatusToFileObserverType.*;
import ASSET.Scenario.Observers.Recording.RecordStatusToFileObserverType.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Sensor.Initial.OpticSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public final class RecordStatusToFileObserverTypeTest extends ContinuousRecordToFileObserverTest {
	String _buildDate;
	String _headerDetails;
	boolean _detectionDetailsWritten;
	boolean _positionDetailsWritten;
	boolean _decisionDetailsWritten;


	public void doThisTest(final boolean testPos, final boolean testDecs, final boolean testDets,
			final TargetType target) {
		final RecordStatusToFileObserverType observer = getRecordObserver(true, testDets, testDecs, testPos,
				target);
		assertNotNull(observer, "observer wasn't created");

		// and the scenario
		final CoreScenario cs = new CoreScenario();
		cs.setName("testing scenario output");

		// add a participant
		final SSN ssn = new SSN(12);
		ssn.setName("SSN");
		ssn.setCategory(
				new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));
		ssn.setDecisionModel(new ASSET.Models.Decision.Tactical.Wait(new Duration(12, Duration.HOURS), "do wait"));
		final OpticSensor sampleSensor = new OpticSensor(12) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			// what is the detection strength for this target?
			@Override
			protected DetectionEvent detectThis(final EnvironmentType environment, final ParticipantType host,
					final ParticipantType target1, final long time, final ScenarioType scenario) {
				final DetectionEvent de = new DetectionEvent(12l, 12, null, this, null, null, null, null, null,
						null, null, null, ssn);
				return de;
			}
			// public void detects(EnvironmentType environment, DetectionList
			// existingDetections,
			// ParticipantType ownship, ScenarioType scenario,
			// long time)
			// {
			// DetectionEvent de = new DetectionEvent(12l, 12, null, this, null, null, null,
			// null, null,
			// null, null, null, ssn);
			// existingDetections.add(de);
			// }
		};
		ssn.addSensor(sampleSensor);
		ssn.setMovementChars(HeloMovementCharacteristics.getSampleChars());
		final Status theStat = new Status(12, 12);
		theStat.setLocation(new WorldLocation(12, 12, 12));
		theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		ssn.setStatus(theStat);

		cs.addParticipant(12, ssn);

		// initialise results instances
		_buildDate = null;
		_headerDetails = null;
		_detectionDetailsWritten = false;
		_positionDetailsWritten = false;
		_decisionDetailsWritten = false;

		// and do the setup
		observer.setup(cs);

		assertNull(_buildDate, "build date called - we should defer it until the first step");
		assertNull(_headerDetails, "headerDetails called - we should defer it until the first step");

		// do a step
		cs.step();

		assertNotNull(_buildDate, "build date wasn't called");
		assertNotNull(_headerDetails, "headerDetails weren't called");

		// see if the relevant bits were called
		assertEquals(testPos, _positionDetailsWritten, "positions called");
		assertEquals(testDets, _detectionDetailsWritten, "detections called");
		assertEquals(testDecs, _decisionDetailsWritten, "decisions called");

		// and the close
		observer.tearDown(cs);

		// and close the file
		assertNull(observer._os, "stream wasn't closed");

	}

	//////////////////////////////////////////////////
	// utility method to create an observer - over-ridden in instantiated classes
	//////////////////////////////////////////////////
	protected RecordStatusToFileObserverType getRecordObserver(final boolean isActive, final boolean dets,
			final boolean decisions, final boolean positions, final TargetType subject) {
		return new RecordStatusToFileObserverType(super.dir_name, super.file_name, dets, decisions, positions,
				subject, "rec status", isActive) {
			@Override
			protected EditorType createEditor() {
				return null; // To change body of implemented methods use File | Settings | File
								// Templates.
			}

			@Override
			protected String getMySuffix() {
				return "rso"; // To change body of implemented methods use File | Settings | File
								// Templates.
			}

			@Override
			protected String newName(final String name) {
				return "new_rec_status"; // To change body of implemented methods use File | Settings |
											// File Templates.
			}

			@Override
			protected void writeBuildDate(final String details) throws IOException {
				_buildDate = details;
			}

			@Override
			protected void writeFileHeaderDetails(final String title, final long currentDTG) throws IOException {
				_headerDetails = title;
			}

			@Override
			protected void writeTheseDetectionDetails(final ParticipantType pt, final DetectionList detections,
					final long dtg) {
				_detectionDetailsWritten = true;
			}

			@Override
			protected void writeThesePositionDetails(final WorldLocation loc, final Status stat,
					final ParticipantType pt, final long newTime) {
				_positionDetailsWritten = true;
			}

			@Override
			protected void writeThisDecisionDetail(final NetworkParticipant pt, final String activity,
					final long dtg) {
				_decisionDetailsWritten = true;
			}

		};
	}

	@Test
	public void testCombinations() {
		doThisTest(true, true, true, null);
		doThisTest(true, false, false, null);
		doThisTest(false, true, false, null);
		doThisTest(false, false, false, null);
	}
}
