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

package ASSET.Scenario.Observers.Summary;

import java.io.FileWriter;
import java.io.IOException;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioRunningListener;

/**
 * Measure the elapsed time in a scenario
 * <p/>
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 11-Aug-2004 Time: 16:14:50 To
 * change this template use File | Settings | File Templates.
 */
public class ElapsedTimeObserver extends EndOfRunBatchObserver implements ScenarioRunningListener {
	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	/**
	 * the start time for the scenario
	 */
	private long _startTime;

	/**
	 * the elapsed time within the scenario
	 */
	private long _elapsedTime;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	/**
	 * @param directoryName the directory to write the results to
	 * @param fileName      the filename to write the results to
	 * @param observerName  the name of this observer
	 * @param isActive      whether this observer is active
	 */
	public ElapsedTimeObserver(final String directoryName, final String fileName, final String observerName,
			final boolean isActive) {
		super(directoryName, fileName, observerName, isActive);
	}

	//////////////////////////////////////////////////
	// setup management
	//////////////////////////////////////////////////

	/**
	 * add any applicable listeners
	 */
	@Override
	protected void addListeners(final ScenarioType scenario) {
		_myScenario.addScenarioRunningListener(this);
	}

	//////////////////////////////////////////////////
	// batch collation methods
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////

	/**
	 * the scenario has stopped running on auto
	 */
	@Override
	public void finished(final long elapsedTime, final String reason) {
		// right, not record the end time
		_elapsedTime = _myScenario.getTime() - _startTime;

		// are we recording to batch?
		if (_batcher != null) {
			_batcher.submitResult(_myScenario.getName(), _myScenario.getCaseId(), (_elapsedTime / 1000d));
		}
	}

	//////////////////////////////////////////////////
	// scenario running support
	//////////////////////////////////////////////////
	/**
	 * the scenario step time has changed
	 */
	@Override
	public void newScenarioStepTime(final int val) {
		// ignore
	}

	/**
	 * the GUI step time has changed
	 */
	@Override
	public void newStepTime(final int val) {
		// ignore
	}

	/**
	 * the scenario has stopped running on auto
	 */
	@Override
	public void paused() {
		// let's not worry about this little thing
	}

	/**
	 * remove any listeners
	 */
	@Override
	protected void removeListeners(final ScenarioType scenario) {
		_myScenario.removeScenarioRunningListener(this);
	}

	/**
	 * configure the batch processing
	 *
	 * @param fileName          the filename to write to
	 * @param collationMethod   how to collate the data
	 * @param perCaseProcessing whether to collate the stats on a per-case basis
	 * @param isActive          whether this collator is active
	 */
	@Override
	public void setBatchCollationProcessing(final String fileName, final String collationMethod,
			final boolean perCaseProcessing, final boolean isActive) {
		setBatchCollationProcessing(fileName, collationMethod, perCaseProcessing, isActive, "secs");
	}

	/**
	 * the scenario has started running on auto
	 */
	@Override
	public void started() {
		_startTime = _myScenario.getTime();
	}

	/**
	 * ok, run complete. output my results
	 *
	 * @param myWriter the writer to use
	 * @throws IOException in case there's a problem
	 */
	@Override
	protected void writeMyResults(final FileWriter myWriter) throws IOException {
		myWriter.write("Elapsed time (secs):" + (_elapsedTime / 1000d));
	}
}
