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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.asset.scenariocontroller2;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ASSET.Util.XML.ASSETReaderWriter.ResultsContainer;

public abstract class CoreControllerPresenter
{

	/**
	 * object that listens out for files being dropped
	 * 
	 * @author ian
	 * 
	 */
	public static interface FilesDroppedListener
	{
		void filesDropped(String[] files);
	}
	
	public static interface ScenarioDisplay
	{
		/**
		 * make this view the selected view. We've just loaded some data, so tell
		 * everybody we're alive
		 */
		void activate();

		/**
		 * specify handler for drop events
		 * 
		 * @param listener
		 */
		void addFileDropListener(FilesDroppedListener listener);

		/**
		 * this is a relative path, produce an absolute path to a relative location
		 * in the project directory
		 * 
		 * @param tgtDir
		 *          relative path
		 * @return absolute path
		 */
		File getProjectPathFor(File tgtDir);

		/**
		 * the project folder may have been updated, refresh what's shown
		 * 
		 */
		void refreshWorkspace();

		/**
		 * display the control file name
		 * 
		 * @param name
		 */
		void setControlName(String name);

		/**
		 * display the scenario name
		 * 
		 * @param name
		 */
		void setScenarioName(String name);
	}

	protected String getFirstNodeName(final String SourceXMLFilePath) throws Exception
	{
		/* Check whether file is XML or not */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		try
		{
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(SourceXMLFilePath);

			final NodeList nl = document.getElementsByTagName("*");
			return nl.item(0).getNodeName();
		}
		catch (final IOException ioe)
		{
			CorePlugin.logError(Status.ERROR, "Whilst getting first node in " + SourceXMLFilePath, ioe);
			return null;
		}

	}

	protected boolean isRelativePath(final File tgtDir)
	{
		boolean res = true;

		final String thePath = tgtDir.getPath();

		// use series of tests to check whether this is a relative path
		if (thePath.length() == 0)
			res = true;
		else
		{
			if (thePath.contains(":"))
				res = false;
			if (thePath.contains("\\\\"))
				res = false;
			if (thePath.charAt(0) == '\\')
				res = false;
			if (thePath.contains("//"))
				res = false;
			if (thePath.charAt(0) == '/')
				res = false;
		}

		return res;
	}

	/**
	 * filename for the scenario
	 * 
	 */
	protected String _scenarioFileName;

	/**
	 * filename for the controller
	 * 
	 */
	protected String _controlFileName;

	/**
	 * where we put our resutls
	 * 
	 */
	protected ResultsContainer _scenarioController;

	protected final ScenarioDisplay _coreDisplay;

	public CoreControllerPresenter(final ScenarioDisplay display)
	{
		_coreDisplay = display;

		// ok, sort out the file drop handler
		_coreDisplay.addFileDropListener(new FilesDroppedListener()
		{
			public void filesDropped(final String[] files)
			{
				handleTheseFiles(files);
			}
		});

	}

	public String getControlName()
	{
		return _controlFileName;
	}

	public String getScenarioName()
	{
		return _scenarioFileName;
	}

	protected abstract void handleTheseFiles(String[] strings);

	public void reloadDataFiles()
	{
		// ok, force the data-files to be reloaded
		if (_scenarioFileName != null)
			handleTheseFiles(new String[]
			{ _scenarioFileName });
		if (_controlFileName != null)
			handleTheseFiles(new String[]
			{ _controlFileName });
	}

}
