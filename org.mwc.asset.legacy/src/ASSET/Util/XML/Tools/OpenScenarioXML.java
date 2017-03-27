/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package ASSET.Util.XML.Tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ASSET.ServerType;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;

public class OpenScenarioXML extends MWC.GUI.Tools.Operations.Open
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * copy of the parent application for this tool
	 */
	private ServerType _myServer = null;

	/**
	 * the chart we are going to redraw if we are successful
	 * 
	 */
	private MWC.GUI.PlainChart _myChart = null;

	private final static String mySuffix = ".xml";

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	public OpenScenarioXML(final ToolParent theParent, final ServerType server,
			final MWC.GUI.PlainChart theChart)
	{
		super(theParent, "Open Scenario", new String []{"*" + mySuffix}, "Asset Scenario Files (*"
				+ mySuffix + ")");

		// store local data
		_myChart = theChart;
		_myServer = server;

		// see if we have an old directory to retrieve
		if (_lastDirectory == "")
		{
			final String val = getParent().getProperty("ASF_Directory");
			if (val != null)
				_lastDirectory = val;
		}

	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public Action doOpen(final String filename)
	{
		Action res = null;

		// data is collated, now create 'action' function
		res = new OpenScenarioAction(filename, _myServer, _myChart);

		// return the product
		return res;
	}

	// /////////////////////////////////////////////////////
	// store action information
	// /////////////////////////////////////////////////////
	static public class OpenScenarioAction implements Action
	{
		/**
		 * the filename we originally read the data from (note that by this point
		 * the data has already been read, and stored in the Layers object)
		 */
		private String _theFileName;

		/**
		 * the server we are going to import the data into
		 */
		private ServerType _myServer;
		/**
		 * the chart we are going to redraw if we are successful
		 * 
		 */
		MWC.GUI.PlainChart _myChart = null;

		/**
		 * constructor - produced AFTER we have read in the data, but before we have
		 * added it to the Application
		 */
		public OpenScenarioAction(final String theFileName,
				final ServerType server, final MWC.GUI.PlainChart theChart)
		{
			_myServer = server;
			_myChart = theChart;
			_theFileName = theFileName;
		}

		public boolean isRedoable()
		{
			return false;
		}

		public boolean isUndoable()
		{
			return false;
		}

		public String toString()
		{
			return "Open " + _theFileName;
		}

		public void undo()
		{
			// delete the plottables from the Application object
		}

		public void execute()
		{
			FileInputStream is = null;

			try
			{

				// create a new scenario
				final int index = _myServer.createNewScenario("blank");
				final ASSET.Scenario.CoreScenario newScen = (ASSET.Scenario.CoreScenario) _myServer
						.getThisScenario(index);

				// create new object input stream
				is = new FileInputStream(_theFileName);
				final InputStream bi = new BufferedInputStream(is);
				ASSET.Util.XML.ASSETReaderWriter.importThis(newScen, _theFileName, bi);

				// we also want to redraw the plot
				_myChart.update();

			}
			catch (IOException e)
			{
				// do nothing;
				MWC.Utilities.Errors.Trace.trace(e);
			}
			finally
			{
				// handle the file close - put it this late so that we are sure it gets
				// done
				try
				{
					if (is != null)
						is.close();
				}
				catch (java.io.IOException ex)
				{
					MWC.Utilities.Errors.Trace.trace(ex, "Closing ASSET file");
				}
			}

		}

	}

}
