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

/**
 * Created by IntelliJ IDEA. User: Ian Date: 28-Oct-2003 Time: 14:17:34 To
 * change this template use Options | File Templates.
 */
abstract public class RecordStatusToFileObserverType extends ContinuousRecordToFileObserver
		implements ASSET.Scenario.ScenarioSteppedListener {
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public static final class recToFileTest extends ContinuousRecordToFileObserver.RecToFileTest {
		String _buildDate;
		String _headerDetails;
		boolean _detectionDetailsWritten;
		boolean _positionDetailsWritten;
		boolean _decisionDetailsWritten;

		public recToFileTest(final String val) {
			super(val);
		}

		public void doThisTest(final boolean testPos, final boolean testDecs, final boolean testDets,
				final TargetType target) {
			final RecordStatusToFileObserverType observer = getRecordObserver(true, testDets, testDecs, testPos,
					target);
			assertNotNull("observer wasn't created", observer);

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

			assertNull("build date called - we should defer it until the first step", _buildDate);
			assertNull("headerDetails called - we should defer it until the first step", _headerDetails);

			// do a step
			cs.step();

			assertNotNull("build date wasn't called", _buildDate);
			assertNotNull("headerDetails weren't called", _headerDetails);

			// see if the relevant bits were called
			assertEquals("positions called", testPos, _positionDetailsWritten);
			assertEquals("detections called", testDets, _detectionDetailsWritten);
			assertEquals("decisions called", testDecs, _decisionDetailsWritten);

			// and the close
			observer.tearDown(cs);

			// and close the file
			assertNull("stream wasn't closed", observer._os);

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

		public void testCombinations() {
			doThisTest(true, true, true, null);
			doThisTest(true, false, false, null);
			doThisTest(false, true, false, null);
			doThisTest(false, false, false, null);
		}
	}

	/**
	 * keep track of whether the analyst wants detections recorded
	 */
	protected boolean _recordDetections = false;

	/**
	 * keep track of whether the analyst wants decisions recorded
	 */
	private boolean _recordDecisions;

	/**
	 * keep track of whether the analyst wants positions recorded
	 */
	private boolean _recordPositions;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////

	/**
	 * keep track of what target the analyst wants recorded
	 */
	private TargetType _subjectToTrack;

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////

	/**
	 * an observer which wants to record it's data to file
	 *
	 * @param directoryName    the directory to output to
	 * @param fileName         the filename to output to
	 * @param recordDetections whether to record detections
	 * @param recordDecisions  whether to record decisions
	 * @param recordPositions  whether to record positions
	 * @param subjectToTrack   the type of target to track (or null for all targets)
	 * @param observerName     what to call this narrative observer
	 * @param isActive         whether this observer is active
	 */
	public RecordStatusToFileObserverType(final String directoryName, final String fileName,
			final boolean recordDetections, final boolean recordDecisions, final boolean recordPositions,
			final TargetType subjectToTrack, final String observerName, final boolean isActive) {
		super(directoryName, fileName, observerName, isActive);

		_recordDetections = recordDetections;
		_recordDecisions = recordDecisions;
		_recordPositions = recordPositions;
		_subjectToTrack = subjectToTrack;
	}

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		_myScenario.addScenarioSteppedListener(this);
	}

	public boolean getRecordDecisions() {
		return _recordDecisions;
	}

	public boolean getRecordDetections() {
		return _recordDetections;
	}

	public boolean getRecordPositions() {
		return _recordPositions;
	}

	public TargetType getSubjectToTrack() {
		return _subjectToTrack;
	}

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		_myScenario.removeScenarioSteppedListener(this);
	}

	public void setRecordDecisions(final boolean recordDecisions) {
		this._recordDecisions = recordDecisions;
	}

	public void setRecordDetections(final boolean recordDetections) {
		this._recordDetections = recordDetections;
	}

	//////////////////////////////////////////////////
	// member getter/setters
	//////////////////////////////////////////////////

	public void setRecordPositions(final boolean recordPositions) {
		this._recordPositions = recordPositions;
	}

	public void setSubjectToTrack(final TargetType subjectToTrack) {
		this._subjectToTrack = subjectToTrack;
	}

	/**
	 * the scenario has stepped forward
	 */
	@Override
	public void step(final ScenarioType scenario, final long newTime) {
		if (!isActive())
			return;

		// just check that/if we have an output file
		if (_os == null) {
			// ok, better sort out the output files.
			createOutputFile();
		}

		// get the positions of the participants
		final Integer[] lst = _myScenario.getListOfParticipants();
		for (int thisIndex = 0; thisIndex < lst.length; thisIndex++) {
			final Integer integer = lst[thisIndex];
			if (integer != null) {
				final ASSET.ParticipantType pt = _myScenario.getThisParticipant(integer.intValue());

				// is this a target of interest?
				if ((_subjectToTrack == null) || (_subjectToTrack.matches(pt.getCategory()))) {
					if (getRecordPositions()) {
						final ASSET.Participants.Status stat = pt.getStatus();
						final MWC.GenericData.WorldLocation loc = stat.getLocation();

						// ok, now output these details in our special format
						writeThesePositionDetails(loc, stat, pt, newTime);
					}

					if (getRecordDetections()) {
						// get the list of detections
						final DetectionList list = pt.getNewDetections();
						writeTheseDetectionDetails(pt, list, newTime);
					}

					if (getRecordDecisions()) {
						// get the current activity
						final String thisActivity = pt.getActivity();
						writeThisDecisionDetail(pt, thisActivity, newTime);
					}

				}

			}
		}

	}

	/**
	 * write the supplied build details to file
	 *
	 * @param details the build time/date
	 * @throws IOException if we have any of that file trouble
	 */
	protected abstract void writeBuildDate(String details) throws IOException;

	/**
	 * ok. ready to start writing. get on with it
	 *
	 * @param title      the title of this run
	 * @param currentDTG the current time (not model time)
	 * @throws IOException
	 */
	abstract protected void writeFileHeaderDetails(final String title, long currentDTG) throws IOException;

	/**
	 * the file has been opened, write the header details to it
	 */
	@Override
	protected void writeFileHeaderInformation(final FileWriter destination, final String fileName) throws IOException {
		// write out the build date
		writeBuildDate(getBuildDate());

		// ok, write the header details
		final long theDTG = new Date().getTime();
		writeFileHeaderDetails(fileName, theDTG);

	}

	/**
	 * write these detections to file
	 *
	 * @param pt         the participant we're on about
	 * @param detections the current set of detections
	 * @param dtg        the dtg at which the detections were observed
	 */
	abstract protected void writeTheseDetectionDetails(ParticipantType pt, DetectionList detections, long dtg);

	/**
	 * write this set of details to file
	 *
	 * @param loc  the current location
	 * @param stat the current status
	 * @param pt   the participant in question
	 */
	abstract protected void writeThesePositionDetails(WorldLocation loc, Status stat, ParticipantType pt, long newTime);

	/**
	 * write the current decision description to file
	 *
	 * @param pt       the participant we're looking at
	 * @param activity a description of the current activity
	 * @param dtg      the dtg at which the description was recorded
	 */
	abstract protected void writeThisDecisionDetail(NetworkParticipant pt, String activity, long dtg);
}
