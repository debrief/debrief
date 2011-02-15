package org.mwc.asset.scenariocontroller2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.Control.StandaloneObserverListHandler;
import ASSET.Util.XML.Control.Observers.ScenarioControllerHandler;
import MWC.GenericData.HiResDate;

public class SingleScenarioPresenter extends CoreControllerPresenter
{
	/**
	 * our data model
	 * 
	 */
	final private CoreScenario _myModel;
	
	/** our UI
	 * 
	 */
	final private SingleDisplay _myDisplay;

	private ScenarioSteppedListener _stepListener;
	
	/** display interface for a single-scenario UI
	 * 
	 */
	public static interface SingleDisplay extends ScenarioDisplay
	{

		/** indicate who handles the buttons
		 * 
		 * @param manageSingleListener
		 */
		void addScenarioHandler(ManageSingleListener manageSingleListener);

		/** set whether the control buttons are enabled
		 * 
		 */
		void setButtonState(boolean state);
		
		/** clear the status label
		 * 
		 */
		void clearState();

		/** display the supplied status
		 * 
		 * @param string
		 */
		void setStatus(String string);

		/** display the supplied time
		 * 
		 * @param hiResDate
		 * @param b
		 */
		void setTime(HiResDate hiResDate, boolean b);
		
	}

	
	/** someone interested in managing a single scenario
	 * 
	 * @author ian
	 *
	 */
	public static interface ManageSingleListener
	{
		/** step forward one step
		 * 
		 */
		void step();
		
		/** run to the end
		 * 
		 */
		void run();
		
		/** and restart the scenario
		 * 
		 */
		void restart();
	}


	public SingleScenarioPresenter(SingleDisplay display, CoreScenario model)
	{
		super(display);
		
		// store our bits
		_myModel = model;
		_myDisplay = display;

		// listen to the model state (so we can display it)
		
		_myDisplay.addScenarioHandler(new ManageSingleListener(){

			public void step()
			{
				_myModel.step();
			}

			public void run()
			{
				_myModel.start();
			}

			public void restart()
			{
				_myModel.restart();
			}});
		
		_stepListener = new ScenarioSteppedListener()
		{
			public void restart(ScenarioType scenario)
			{
				scenarioRestarted();
			}

			public void step(ScenarioType scenario, long newTime)
			{
				scenarioStepped(newTime);
			}
		};

	}

	protected void scenarioRestarted()
	{
		_myDisplay.setStatus("Restarted");
		_myDisplay.setTime(new HiResDate(_myModel.getTime()), true);
	}

	protected void scenarioStepped(long newTime)
	{
		_myDisplay.setStatus("Stepped");
		_myDisplay.setTime( new HiResDate(newTime), true);
	}

	protected void handleTheseFiles(String[] fileNames)
	{

		// ok, loop through the files
		for (int i = 0; i < fileNames.length; i++)
		{
			final String thisName = fileNames[i];

			if (thisName != null)
			{

				// ok, examine this file
				String firstNode = getFirstNodeName(thisName);

				if (firstNode != null)
				{
					if (firstNode.equals("Scenario"))
					{
						// remember it
						_scenarioFileName = thisName;

						// set the filename
						_myDisplay.setScenarioName(new File(thisName).getName());

					}
					else if (firstNode.equals("ScenarioController"))
					{
						// remember it
						_controlFileName = thisName;

						// show it
						_myDisplay.setControlName(new File(thisName).getName());

						// now sort out the controller data
						controllerAssigned(_controlFileName);
					}
				}
			}
		}

		// lastly, make our view the current selection
		_myDisplay.activate();
	}

	private void controllerAssigned(String controlFile)
	{
		// ok, forget any existing observers
		ditchObservers();

		try
		{
			// hmm, check what type of control file it is
			String controlType = getFirstNodeName(controlFile);

			if (controlType == StandaloneObserverListHandler.type)
			{
				_theObservers = ASSETReaderWriter.importThisObserverList(controlFile,
						new java.io.FileInputStream(controlFile));
			}
			else if (controlType == ScenarioControllerHandler.type)
			{

				_scenarioController = ASSETReaderWriter.importThisControlFile(
						controlFile, new java.io.FileInputStream(controlFile));

				_theObservers = _scenarioController.observerList;

				// since we have a results container - we have enough information to set
				// the output files
				File tgtDir = _scenarioController.outputDirectory;

				// if the tgt dir is a relative reference, make it relative to
				// our first project, not the user's login directory
				if (isRelativePath(tgtDir))
				{
					File outputDir = _myDisplay.getProjectPathFor(tgtDir);
					if (outputDir != null)
						_scenarioController.outputDirectory = outputDir;

				}

				Enumeration<ScenarioObserver> numer = _theObservers.elements();
				while (numer.hasMoreElements())
				{
					ScenarioObserver thisS = numer.nextElement();
					// does this worry about the output file?
					if (thisS instanceof RecordToFileObserverType)
					{
						// yup, better store it...
						RecordToFileObserverType rs = (RecordToFileObserverType) thisS;
						rs.setDirectory(tgtDir);
					}
				}

			}

			// check if it's multi scenario..
			final boolean isMulti = CommandLine
					.checkIfGenerationRequired(controlFile);

			// setup the listeners if we're just in a single scenario run
			if (!isMulti)
			{
				if (_theObservers != null)
				{
					loadThisObserverList(controlFile, _theObservers);
				}
			}

			// get the ui to update itself
			_myDisplay.clearState();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void loadThisObserverList(String controlFile,
			Vector<ScenarioObserver> theObservers) throws FileNotFoundException
	{
		// add these observers to our scenario
		for (int i = 0; i < theObservers.size(); i++)
		{
			// get the next observer
			ScenarioObserver observer = theObservers.elementAt(i);

			// and add it to our list
			theObservers.add(observer);
		}
	}

	/**
	 * tell the observers to stop listening to the subject scenario, and then
	 * ditch them
	 * 
	 */
	private void ditchObservers()
	{
		// and ditch any existing observers
		if (_theObservers != null)
		{
			// and ditch the list
			_theObservers.removeAllElements();
		}
	}
	
	

	@Override
	public void reloadDataFiles()
	{
		// let the parent do it's stuff
		super.reloadDataFiles();
		
		// and clear the list of scenarios
		_myDisplay.clearState();

	}



	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		@SuppressWarnings("synthetic-access")
		public final void testRelativePathMethod()
		{
			super.assertEquals("failed to recognise drive", false,
					isRelativePath(new File("c:\\test.rep")));
			super.assertEquals("failed to root designator", false,
					isRelativePath(new File("\\test.rep")));
			super.assertEquals("failed to root designator", false,
					isRelativePath(new File("\\\\test.rep")));
			super.assertEquals("failed to root designator", false,
					isRelativePath(new File("//test.rep")));
			super.assertEquals("failed to root designator", false,
					isRelativePath(new File("////test.rep")));
			super.assertEquals("failed to recognise absolute ref", true,
					isRelativePath(new File("test.rep")));
			super.assertEquals("failed to recognise relative ref", true,
					isRelativePath(new File("./test.rep")));
			super.assertEquals("failed to recognise parent ref", true,
					isRelativePath(new File("../test.rep")));
		}
	}

}
