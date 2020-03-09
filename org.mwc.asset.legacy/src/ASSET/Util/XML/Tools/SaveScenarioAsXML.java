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

package ASSET.Util.XML.Tools;

import ASSET.ScenarioType;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;

public class SaveScenarioAsXML extends MWC.GUI.Tools.Operations.Save {
	///////////////////////////////////////////////////////
	// store action information
	///////////////////////////////////////////////////////
	protected class SaveScenarioAction implements Action {
		/**
		 * store the name of the ScenarioType we have saved
		 */
		final String _theScenarioName;

		public SaveScenarioAction(final String theName) {
			_theScenarioName = theName;
		}

		@Override
		public void execute() {
		}

		@Override
		public boolean isRedoable() {
			return false;
		}

		@Override
		public boolean isUndoable() {
			return false;
		}

		@Override
		public String toString() {
			return "Save " + _theScenarioName;
		}

		@Override
		public void undo() {
			// delete the plottables from the Application object
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final static String mySuffix = "xml";

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	private ScenarioType _theScenario = null;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public SaveScenarioAsXML(final ToolParent theParent, final ASSET.ScenarioType theScenario) {
		this(theParent, theScenario, "Save Scenario As...", "images/saveas.gif");
	}

	SaveScenarioAsXML(final ToolParent theParent, final ASSET.ScenarioType theScenario, final String theTitle,
			final String theImage) {
		super(theParent, theTitle, "*." + mySuffix, theImage);

		// store the ScenarioType parameter
		_theScenario = theScenario;

		// see if we have an old directory to retrieve
		if (_lastDirectory == "") {
			final String val = getParent().getProperty("ASF_Directory");
			if (val != null)
				_lastDirectory = val;
		}
	}

	@Override
	public void close() {
		super.close();

		_theScenario = null;
	}

	/////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////
	@Override
	protected Action doSave(String filename) {
		final Action res = null;

		// now save ScenarioType to this file
		// check if the file ends in XML
		final int idx = filename.toLowerCase().indexOf("." + mySuffix);
		// final int CLASS_EXTENSION_LENGTH = 4;
		if (idx == -1) {
			filename += "." + mySuffix;
		}

		// open the file

		throw new UnsupportedOperationException();
		// final OutputStream os = new FileOutputStream(filename);
		// // pass all of this to the XML exporter
		// ASSET.Util.XML.ASSETReaderWriter.exportThis(_theScenario, null, os);
		// os.close();

		// res = new SaveScenarioAction("the scenario"); /** create names for scenarios
		// */

		// return res;
	}

	protected ScenarioType getScenarioType() {
		return _theScenario;
	}
}
