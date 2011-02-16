package org.mwc.asset.scenariocontroller2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Vector;

import ASSET.GUI.CommandLine.CommandLine;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.GUI.CommandLine.MultiScenarioCore;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.Control.StandaloneObserverListHandler;
import ASSET.Util.XML.Control.Observers.ScenarioControllerHandler;

public class MultiScenarioPresenter extends CoreControllerPresenter
{
	/**
	 * package up an operation with a progress monitor
	 * 
	 * @author ian
	 * 
	 */
	public static interface JobWithProgress
	{
		void run(ASSETProgressMonitor montor);
	}

	/**
	 * objects that handle a series of runs
	 * 
	 * @author ian
	 * 
	 */
	public static interface ManageMultiListener
	{
		/**
		 * trigger scenario generation
		 * 
		 */
		void doGenerate();

		/**
		 * trigger stepping through the scenarios
		 * 
		 */
		void doRunAll();
	}

	/**
	 * display (view) for scenario controller
	 * 
	 * @author ian
	 * 
	 */
	public static interface MultiScenarioDisplay extends ScenarioDisplay
	{
		/**
		 * someone is listening to the run/generate buttons
		 * 
		 * @param listener
		 */
		void addMultiScenarioHandler(ManageMultiListener listener);

		/**
		 * new controller loaded, ditch generated scenarios
		 * 
		 */
		void clearScenarios();

		/**
		 * start this monster job running
		 * 
		 * @param theJob
		 */
		void runThisJob(JobWithProgress theJob);

		/**
		 * set the enabled state of the generate button
		 * 
		 * @param b
		 */
		void setGenerateState(boolean b);

		/**
		 * set the enabled state of the run button
		 * 
		 * @param b
		 */
		void setRunState(boolean b);

		/**
		 * update the list of scenarios
		 * 
		 * @param _myModel
		 */
		void setScenarios(MultiScenarioCore _myModel);
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

	/**
	 * our data model
	 * 
	 */
	private MultiScenarioCore _myModel;

	/**
	 * our view
	 * 
	 */
	MultiScenarioDisplay _myDisplay;

	public MultiScenarioPresenter(MultiScenarioDisplay display,
			MultiScenarioCore model)
	{
		super(display);

		// store the presenter
		_myDisplay = display;

		// generate our model
		_myModel = model;

		// ok, sort out the file drop handler
		_myDisplay.addFileDropListener(new FilesDroppedListener()
		{

			public void filesDropped(String[] files)
			{
				handleTheseFiles(files);
			}
		});

		_myDisplay.addMultiScenarioHandler(new ManageMultiListener()
		{
			public void doGenerate()
			{
				generateScenarios();
			}

			public void doRunAll()
			{
				runScenarios();
			}
		});

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
			_myDisplay.clearScenarios();
		}
		catch (Exception e)
		{
			e.printStackTrace();
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

	protected void generateScenarios()
	{
		// disable the genny button, until it's done.
		_myDisplay.setGenerateState(false);

		JobWithProgress theJob = new JobWithProgress()
		{

			public void run(ASSETProgressMonitor montor)
			{

				// and let it create some files
				_myModel.prepareFiles(_controlFileName, _scenarioFileName, System.out,
						System.err, System.in, montor, _scenarioController.outputDirectory);

				// and sort out the observers
				_myModel.prepareControllers(_scenarioController, montor, null);

				// ok, now give the scenarios to the multi scenario table (in the UI
				// thread
				_myDisplay.setScenarios(_myModel);

				// and set the button states
				_myDisplay.setGenerateState(false);
				_myDisplay.setRunState(true);
			}
		};

		_myDisplay.runThisJob(theJob);

	}

	@Override
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

	@Override
	public void reloadDataFiles()
	{
		// let the parent do it's stuff
		super.reloadDataFiles();

		// and clear the list of scenarios
		_myDisplay.clearScenarios();

	}

	protected void runScenarios()
	{
		System.out.println("doing run");

		Thread doRun = new Thread()
		{

			@Override
			public void run()
			{
				// tell them to go for it
				_myModel.nowRun(System.out, System.err, System.in, null);

				// ok, better refresh the workspace
				_myDisplay.refreshWorkspace();
			}
		};
		doRun.start();
	}

}
