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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ASSET.ScenarioType;
import ASSET.Scenario.Observers.EndOfRunRecordToFileObserver;

/**
 * Abstract class containing parent methods used to support a batch observer
 * which writes it's output at the end of a run, as well as during a run.
 * <p/>
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 18-Aug-2004 Time: 15:56:07 To
 * change this template use File | Settings | File Templates.
 */
public abstract class EndOfRunBatchObserver extends EndOfRunRecordToFileObserver implements BatchCollator {
	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	/**
	 * our batch collator
	 */
	protected BatchCollatorHelper _batcher = null;
	/**
	 * whether to override (cancel) writing per-scenario results to file
	 */
	private boolean _onlyBatch = false;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	public EndOfRunBatchObserver(final String directoryName, final String fileName, final String observerName,
			final boolean isActive) {
		super(directoryName, fileName, observerName, isActive);
	}

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////

	/**
	 * whether to actually write the end-of-run results to file
	 */
	@Override
	protected boolean doEndOfRunWrite() {
		return !_onlyBatch;
	}

	//////////////////////////////////////////////////
	// inter-scenario observer methods
	//////////////////////////////////////////////////
	@Override
	public void finish() {
		if (isActive()) {
			if (_batcher != null) {
				// ok, get the batch thingy to do it's stuff
				_batcher.writeOutput(getHeaderInfo());
			}
		}
	}

	/**
	 * accessor to retrieve batch processing settings
	 */
	@Override
	public BatchCollatorHelper getBatchHelper() {
		return _batcher;
	}

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 *
	 * @return whether to override batch processing
	 */
	@Override
	public boolean getBatchOnly() {
		return _onlyBatch;
	}

	/**
	 * determine the normal suffix for this file type
	 */
	@Override
	protected String getMySuffix() {
		return "csv"; // To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void initialise(final File outputDirectory) {
		if (isActive()) {
			// set the output directory for the batch collator
			if (_batcher != null)
				_batcher.setDirectory(outputDirectory);
		}
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
	}

	/**
	 * run is complete, and we've output our data. clear the data structures
	 */
	@Override
	protected void resetData() {
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
	abstract public void setBatchCollationProcessing(String fileName, String collationMethod, boolean perCaseProcessing,
			boolean isActive);

	/**
	 * configure the batch processing
	 *
	 * @param fileName          the filename to write to
	 * @param collationMethod   how to collate the data
	 * @param perCaseProcessing whether to collate the stats on a per-case basis
	 * @param isActive          whether this collator is active
	 */
	protected void setBatchCollationProcessing(String fileName, final String collationMethod,
			final boolean perCaseProcessing, final boolean isActive, final String units) {
		_batcher = new BatchCollatorHelper(getName(), perCaseProcessing, collationMethod, isActive, units);

		// do we have a filename?
		if (fileName == null)
			fileName = getName() + "." + getMySuffix();

		_batcher.setFileName(fileName);
	}

	/**
	 * whether to override (cancel) writing per-scenario results to file
	 *
	 * @param override
	 */
	@Override
	public void setBatchOnly(final boolean override) {
		_onlyBatch = override;
	}

	/**
	 * output the results of just this run
	 *
	 * @param myWriter the stream to write to
	 * @throws IOException if there are any problems
	 */
	@Override
	protected abstract void writeMyResults(FileWriter myWriter) throws IOException;

}
