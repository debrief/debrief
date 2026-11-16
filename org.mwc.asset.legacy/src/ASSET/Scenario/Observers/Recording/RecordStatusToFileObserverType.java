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
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import MWC.GenericData.WorldLocation;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 28-Oct-2003 Time: 14:17:34 To
 * change this template use Options | File Templates.
 */
abstract public class RecordStatusToFileObserverType extends ContinuousRecordToFileObserver
		implements ASSET.Scenario.ScenarioSteppedListener {

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
