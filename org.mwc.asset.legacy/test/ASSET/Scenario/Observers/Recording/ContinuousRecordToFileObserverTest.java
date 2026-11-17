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

import static ASSET.Scenario.Observers.Recording.ContinuousRecordToFileObserver.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.Scenario.Observers.Recording.ContinuousRecordToFileObserver.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import ASSET.ScenarioType;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Util.SupportTesting;
import org.junit.jupiter.api.Test;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class ContinuousRecordToFileObserverTest extends SupportTesting {
	boolean _listenersRemoved = false;
	boolean _listenersAdded = false;
	boolean _closeProcessingPerformed = false;
	boolean _setupProcessingPerformed = false;
	boolean _headerInfoSent = false;
	// private boolean _newNameCalled;
	// private boolean _getSuffixCalled;

	protected String dir_name = "test_reports";

	protected String file_name = "cont_observer";


	//////////////////////////////////////////////////
	// utility method to create an observer - over-ridden in instantiated classes
	//////////////////////////////////////////////////
	protected ContinuousRecordToFileObserver getObserver(final boolean isActive) {
		return new ContinuousRecordToFileObserver(dir_name, file_name, "cont observer", isActive) {
			@Override
			protected void addListeners(final ScenarioType scenario) {
				_listenersAdded = true;
			}

			@Override
			protected EditorType createEditor() {
				return null; // To change body of implemented methods use File | Settings | File
								// Templates.
			}

			@Override
			protected String getMySuffix() {
				// _getSuffixCalled = true;
				return "tmp"; // To change body of implemented methods use File | Settings | File
								// Templates.
			}

			@Override
			protected String newName(final String name) {
				// _newNameCalled = true;
				return "new_name"; // To change body of implemented methods use File | Settings | File
									// Templates.
			}

			@Override
			protected void performCloseProcessing(final ScenarioType scenario) {
				super.performCloseProcessing(scenario); // To change body of overridden methods use File |
														// Settings | File Templates.

				_closeProcessingPerformed = true;
			}

			@Override
			protected void performSetupProcessing(final ScenarioType scenario) {
				super.performSetupProcessing(scenario); // To change body of overridden methods use File |
														// Settings | File Templates.

				_setupProcessingPerformed = true;
			}

			@Override
			protected void removeListeners(final ScenarioType scenario) {
				_listenersRemoved = true;
			}

			@Override
			protected void writeFileHeaderInformation(final FileWriter destination, final String fileName)
					throws IOException {

				_headerInfoSent = true;
			}
		};
	}

	@Test
	public final void testActiveRecorder() {

		final ContinuousRecordToFileObserver observer = getObserver(true);
		assertNotNull(observer, "observer wasn't created");

		// and the scenario
		final CoreScenario cs = new CoreScenario();
		cs.setName("testing scenario output");

		_listenersAdded = false;
		_listenersRemoved = false;
		_setupProcessingPerformed = false;
		_closeProcessingPerformed = false;
		_headerInfoSent = false;
		// _newNameCalled = false;
		// _getSuffixCalled = false;

		// and do the setup
		observer.setup(cs);

		// sort out the data file
		observer.createOutputFile();

		assertTrue(_listenersAdded, "listeners weren't added");
		assertNotNull(observer._os, "output file wasn't created");
		assertTrue(_setupProcessingPerformed, "pre-processing conducetd");
		assertTrue(_headerInfoSent, "header info wasn't sent");
		assertTrue(_headerInfoSent, "new name wasn't sent");
		assertTrue(_headerInfoSent, "get suffix wasn't sent");

		// and the close
		observer.tearDown(cs);

		assertTrue(_listenersRemoved, "listeners weren't removed");
		assertTrue(_closeProcessingPerformed, "post-processing conducetd");
		assertNull(observer._os, "output stream close");

	}

	@Test
	public final void testInActiveRecorder() {

		// File theOutputFile = null;

		final ContinuousRecordToFileObserver observer = getObserver(false);
		assertNotNull(observer, "observer wasn't created");

		// and the scenario
		final CoreScenario cs = new CoreScenario();
		cs.setName("testing scenario output");

		// initialise our results monitors
		_listenersAdded = false;
		_listenersRemoved = false;
		_setupProcessingPerformed = false;
		_closeProcessingPerformed = false;
		_headerInfoSent = false;
		// _newNameCalled = false;
		// _getSuffixCalled = false;

		// and do the setup
		observer.setup(cs);

		assertFalse(_listenersAdded, "listeners weren't added");
		assertNull(observer._os, "output file wasn't created");
		assertFalse(_setupProcessingPerformed, "pre-processing conducetd");
		assertFalse(_headerInfoSent, "header info wasn't sent");
		assertFalse(_headerInfoSent, "new name wasn't sent");
		assertFalse(_headerInfoSent, "get suffix wasn't sent");

		// and the close
		observer.tearDown(cs);

		assertFalse(_listenersRemoved, "listeners weren't removed");
		assertFalse(_closeProcessingPerformed, "post-processing conducetd");
		assertNull(observer._os, "output stream close");

	}
}
