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

package Debrief.Tools.Operations;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;

public final class OpenPlot extends MWC.GUI.Tools.Operations.Open {
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////
	// store action information
	///////////////////////////////////////////////////////
	static public final class OpenPlotAction implements Action {
		/**
		 * the filename we originally read the data from (note that by this point the
		 * data has already been read, and stored in the Layers object)
		 */
		private final String _theFileName;
		/**
		 * the application we are going to import the data into
		 */
		private final Application _theApplication;

		/**
		 * constructor - produced AFTER we have read in the data, but before we have
		 * added it to the Application
		 */
		public OpenPlotAction(final String theFileName, final Application theApplication) {
			_theApplication = theApplication;
			_theFileName = theFileName;
		}

		@Override
		public final void execute() {
			_theApplication.setCursor(java.awt.Cursor.WAIT_CURSOR);

			FileInputStream is = null;

			try {

				// create new object input stream
				is = new FileInputStream(_theFileName);
				final InputStream bi = new BufferedInputStream(is);
				final ObjectInputStream ois = new ObjectInputStream(bi);

				// read in the session
				final Session st = (Session) ois.readObject();

				ois.close();

				// setup the GUI
				st.initialiseForm(_theApplication);

				// add this session to our application
				_theApplication.newSession(st);

				// also inform the Session of it's filename
				st.setFileName(_theFileName);

				// put the filename into the MRU
				Debrief.GUI.Frames.Application.addToMru(_theFileName);

			} catch (final java.io.InvalidClassException e) {
				// do nothing;
				MWC.GUI.Dialogs.DialogFactory.showMessage("Open Plot",
						"Incompatible File Versions:  " + e.getMessage());
			} catch (final ClassNotFoundException e) {
				MWC.Utilities.Errors.Trace.trace(e);
			} catch (final IOException e) {
				// do nothing;
				MWC.Utilities.Errors.Trace.trace(e);
			} finally {
				// do a closing "close" operation on the file - put it this late so that earlier
				// errors get caught
				try {
					if (is != null)
						is.close();
				} catch (final java.io.IOException ex) {
					MWC.Utilities.Errors.Trace.trace(ex, "Closing REPLAY file");
				}
			}

			_theApplication.restoreCursor();

		}

		@Override
		public final boolean isRedoable() {
			return false;
		}

		@Override
		public final boolean isUndoable() {
			return false;
		}

		@Override
		public final String toString() {
			return "Open " + _theFileName;
		}

		@Override
		public final void undo() {
			// delete the plottables from the Application object
		}

	}

	/**
		 *
		 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/**
	 * copy of the parent application for this tool
	 */
	private Application _myApplication = null;

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	public OpenPlot(final ToolParent theParent, final java.awt.Image theImage, final Application theApplication) {
		super(theParent, "Open Plot", new String[] { "*.dpl" }, "Debrief Plot Files (*.dpl)");

		// store local data

		// see if we have an old directory to retrieve
		if (_lastDirectory.equals("")) {
			final String val = getParent().getProperty("DPL_Directory");
			if (val != null)
				_lastDirectory = val;
		}

		_myApplication = theApplication;

	}

	@Override
	public final Action doOpen(final String filename) {
		Action res = null;

		// data is collated, now create 'action' function
		res = new OpenPlotAction(filename, _myApplication);

		// return the product
		return res;
	}

}
