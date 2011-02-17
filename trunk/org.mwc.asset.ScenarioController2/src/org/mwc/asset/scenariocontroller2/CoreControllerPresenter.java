package org.mwc.asset.scenariocontroller2;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

	protected String getFirstNodeName(String SourceXMLFilePath)
	{
		/* Check whether file is XML or not */
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(SourceXMLFilePath);

			NodeList nl = document.getElementsByTagName("*");
			return nl.item(0).getNodeName();
		}
		catch (IOException ioe)
		{
			// ioe.printStackTrace();
			return null;
			// return "Not Valid XML File";
		}
		catch (Exception sxe)
		{
			// Exception x = sxe;
			return null;
			// x.printStackTrace();
			// return "Not Valid XML File";
		}

	}

	protected boolean isRelativePath(File tgtDir)
	{
		boolean res = true;

		String thePath = tgtDir.getPath();

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

	public CoreControllerPresenter(ScenarioDisplay display)
	{
		_coreDisplay = display;

		// ok, sort out the file drop handler
		_coreDisplay.addFileDropListener(new FilesDroppedListener()
		{
			public void filesDropped(String[] files)
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
