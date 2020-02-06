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
 * Record the final state (reason) recorded at the end of a scenario
 * <p/>
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 11-Aug-2004 Time: 16:14:50 To
 * change this template use File | Settings | File Templates.
 */

public class FinalStateObserver extends EndOfRunBatchObserver implements ScenarioRunningListener {
	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	/**
	 * the reason returned when the scenario finished
	 */
	private String _finalState;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	/**
	 * @param directoryName the directory to write the results to
	 * @param fileName      the filename to write the results to
	 * @param observerName  the name of this observer
	 * @param isActive      whether this observer is active
	 */
	public FinalStateObserver(final String directoryName, final String fileName, final String observerName,
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
		_finalState = reason;

		// are we recording to batch?
		if (_batcher != null) {
			_batcher.submitResult(_myScenario.getName(), _myScenario.getCaseId(), _finalState);
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
		setBatchCollationProcessing(fileName, collationMethod, perCaseProcessing, isActive, "n/a");
	}

	/**
	 * the scenario has started running on auto
	 */
	@Override
	public void started() {
		_finalState = null;
	}

	/**
	 * ok, run complete. output my results
	 *
	 * @param myWriter the writer to use
	 * @throws IOException in case there's a problem
	 */
	@Override
	protected void writeMyResults(final FileWriter myWriter) throws IOException {
		myWriter.write("Final reason:" + _finalState);
	}
}
