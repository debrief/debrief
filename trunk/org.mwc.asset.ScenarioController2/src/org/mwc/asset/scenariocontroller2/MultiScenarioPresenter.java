package org.mwc.asset.scenariocontroller2;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mwc.asset.scenariocontroller2.views.MultiScenarioView.UIDisplay;
import org.mwc.asset.scenariocontroller2.views.ScenarioWrapper;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;

import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.GUI.CommandLine.MultiScenarioCore;
import ASSET.GUI.CommandLine.MultiScenarioCore.InstanceWrapper;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.ScenarioHandler;
import ASSET.Util.XML.Control.StandaloneObserverListHandler;
import ASSET.Util.XML.Control.Observers.ScenarioControllerHandler;
import MWC.GUI.Layer;

public class MultiScenarioPresenter extends CoreControllerPresenter
{

	private static final String PLAY_LABEL = "Play";

	private static final String PAUSE_LABEL = "Pause";

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
		 * listen out for scenarios being selected from the list
		 * 
		 */
		public void addSelectionChangedListener(ISelectionChangedListener listener);

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
		 * get the detailed display components
		 * 
		 * @return
		 */
		UIDisplay getUI();

		/**
		 * update the list of scenarios
		 * 
		 * @param _myModel
		 */
		void setScenarios(MultiScenarioCore _myModel);

		public void selectFirstRow();
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

	/**
	 * the currently selected scenario
	 * 
	 */
	private ScenarioWrapper _currentScen;

	/**
	 * listener to let us watch the selected scenario
	 * 
	 */
	private ScenarioSteppedListener _stepListener;

	private SimpleDateFormat _format = new SimpleDateFormat("HH:mm:ss");

	public MultiScenarioPresenter(MultiScenarioDisplay display,
			MultiScenarioCore model)
	{
		super(display);

		// store the presenter
		_myDisplay = display;

		// generate our model
		_myModel = model;

		// listen out for scenarios being selected in the table
		_myDisplay.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				Object firstEle = sel.getFirstElement();
				if (firstEle instanceof EditableWrapper)
				{
					EditableWrapper ed = (EditableWrapper) sel.getFirstElement();
					ScenarioWrapper wrapped = (ScenarioWrapper) ed.getEditable();
					selectThis(wrapped);
				}
			}
		});

		// ok, sort out the file drop handler
		_myDisplay.addFileDropListener(new FilesDroppedListener()
		{

			public void filesDropped(String[] files)
			{
				handleTheseFiles(files);
			}
		});

		// and the button listeners
		_myDisplay.getUI().addGenerateListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doGenerate();
			}
		});

		_myDisplay.getUI().addRunAllListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doRunAll();
			}
		});

		_myDisplay.getUI().addInitListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doInit();
			}
		});
		_myDisplay.getUI().addStepListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doStep();
			}
		});
		_myDisplay.getUI().addPlayListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doPlay();
			}
		});

		_stepListener = new ScenarioSteppedListener()
		{

			@Override
			public void step(ScenarioType scenario, long newTime)
			{
				newTime(newTime);
			}

			@Override
			public void restart(ScenarioType scenario)
			{
			}
		};

	}

	protected void doPlay()
	{
		_currentScen.getScenario().start();
	}

	protected void doStep()
	{
		_currentScen.getScenario().step();
	}

	protected void doInit()
	{
		if (_currentScen == null)
		{
			CorePlugin.logError(Status.ERROR,
					"Should not be able to play, no scenario selected", null);
			return;
		}

		// get the wrapped scenario
		InstanceWrapper instance = _myModel.getWrapperFor(_currentScen
				.getScenario());

		if (instance == null)
		{
			CorePlugin.logError(Status.ERROR,
					"Should not be able to play, no scenario not found", null);
			return;
		}

		// set the listeners
		instance.initialise(_myModel.getObservers());

		_myDisplay.getUI().setInitEnabled(false);
		_myDisplay.getUI().setStepEnabled(true);
		_myDisplay.getUI().setPlayEnabled(true);
	}

	/**
	 * display an updated time
	 * 
	 * @param newTime
	 */
	protected void newTime(final long newTime)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				String timeStr = _format.format(new Date(newTime));
				_myDisplay.getUI().setTime(timeStr);
			}
		});
	}

	protected void selectThis(final ScenarioWrapper wrap)
	{
		// is this the currently selected scenario
		if (_currentScen != wrap)
		{
			if (_currentScen != null)
				_currentScen.getScenario().removeScenarioSteppedListener(_stepListener);
		}

		// ok, remember the new one
		_currentScen = wrap;

		_currentScen.getScenario().addScenarioSteppedListener(_stepListener);

		Display.getDefault().syncExec(new Runnable()
		{

			@Override
			public void run()
			{
				ScenarioType scen = wrap.getScenario();

				// ok start off with the time
				newTime(scen.getTime());

				// now look at the state
				String playLabel;
				if (scen.isRunning())
					playLabel = PAUSE_LABEL;
				else
					playLabel = PLAY_LABEL;
				_myDisplay.getUI().setPlayLabel(playLabel);

				_myDisplay.getUI().setInitEnabled(true);
				_myDisplay.getUI().setPlayEnabled(false);
				_myDisplay.getUI().setStepEnabled(false);

			}
		});
	}

	protected void controllerAssigned(String controlFile)
	{

		// start off by ditching the existing list
		_myDisplay.clearScenarios();

		// hmm, check what type of control file it is
		String controlType = getFirstNodeName(controlFile);

		if (controlType == StandaloneObserverListHandler.type)
		{
		}
		else if (controlType == ScenarioControllerHandler.type)
		{
			try
			{
				_scenarioController = ASSETReaderWriter.importThisControlFile(
						controlFile, new java.io.FileInputStream(controlFile));
			}
			catch (FileNotFoundException e)
			{
				CorePlugin.logError(Status.ERROR, "failed whilst loading control file",
						e);
			}

			Vector<ScenarioObserver> theObservers = _scenarioController.observerList;

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

			Enumeration<ScenarioObserver> numer = theObservers.elements();
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
	}

	protected void doGenerate()
	{
		// disable the genny button, until it's done.
		_myDisplay.getUI().setGenerateEnabled(false);

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

				// also clear the timer
				_myDisplay.getUI().setTime("--:--:--");

				// and set the button states
				_myDisplay.getUI().setRunAllEnabled(true);

				// lastly, select the first itme
				_myDisplay.selectFirstRow();

			}
		};

		runThisJob(theJob);

	}

	/**
	 * factor out how we actually run the job, so we can test it more easily
	 * 
	 * @param theJob
	 */
	protected void runThisJob(JobWithProgress theJob)
	{
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
					// get the last component of the filename
					String fName = new File(thisName).getName();

					if (firstNode.equals(ScenarioHandler.type))
					{
						// remember it
						_scenarioFileName = thisName;

						// set the filename
						_myDisplay.setScenarioName(fName);

					}
					else if (firstNode.equals(ScenarioControllerHandler.type))
					{
						// remember it
						_controlFileName = thisName;

						// show it
						_myDisplay.setControlName(fName);

						// now sort out the controller data
						controllerAssigned(_controlFileName);
					}
				}
			}
		}

		// right, make sure the correct buttons are enabled
		enableRelevantButtons();

		// lastly, make our view the current selection
		_myDisplay.activate();
	}

	/**
	 * make the relevant buttons enabled
	 * 
	 */
	private void enableRelevantButtons()
	{
		if ((_controlFileName == null) || (_scenarioFileName == null))
			return;

		try
		{
			// check if it's multi scenario..
			boolean isMulti = CommandLine.checkIfGenerationRequired(_controlFileName);

			// get the UI ready
			if (isMulti)
			{
				_myDisplay.getUI().setGenerateEnabled(true);
				_myDisplay.getUI().setRunAllEnabled(false);
			}
			else
			{
				// not multi run, disable the group buttons
				_myDisplay.getUI().setGenerateEnabled(false);
				_myDisplay.getUI().setRunAllEnabled(false);

				// there's only one scenario - go ahead with the generation
				doGenerate();
			}

		}
		catch (FileNotFoundException e)
		{
			CorePlugin.logError(Status.ERROR, "whilst enabling model controls", e);
		}

	}

	@Override
	public void reloadDataFiles()
	{
		// ok, clear the file paths, so we only do generate once they're both ready
		String safeControl = _controlFileName;
		String safeScenario = _scenarioFileName;

		// ok, now we've got a safe copy, clear the stored values (so we only do
		// init once they're both read in)
		_scenarioFileName = null;
		_controlFileName = null;
		_currentScen = null;
		_scenarioController = null;

		// and clear the list of scenarios
		_myDisplay.clearScenarios();

		// let the parent do it's stuff
		handleTheseFiles(new String[]
		{ safeControl, safeScenario });
	}

	protected void doRunAll()
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

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	public static class TestMe extends TestCase
	{

		static public final String TEST_ALL_TEST_TYPE = "UNIT";
		protected static String _controlfile;

		final String controlPath = "src/org/mwc/asset/scenariocontroller2/tests/trial1.xml";
		final String control_multiP_Path = "src/org/mwc/asset/scenariocontroller2/tests/trial_multi_part.xml";
		final String control_multiS_Path = "src/org/mwc/asset/scenariocontroller2/tests/trial_multi_scen.xml";
		final String scenarioPath = "src/org/mwc/asset/scenariocontroller2/tests/trial1.asset";

		public TestMe(final String val)
		{
			super(val);
		}

		@SuppressWarnings("synthetic-access")
		public final void testRelativePathMethod()
		{
			MultiScenarioDisplay mockDisplay = mock(MultiScenarioDisplay.class);
			MultiScenarioPresenter pres = new MultiScenarioPresenter(mockDisplay,
					null);

			super.assertEquals("failed to recognise drive", false,
					pres.isRelativePath(new File("c:\\test.rep")));
			super.assertEquals("failed to root designator", false,
					pres.isRelativePath(new File("\\test.rep")));
			super.assertEquals("failed to root designator", false,
					pres.isRelativePath(new File("\\\\test.rep")));
			super.assertEquals("failed to root designator", false,
					pres.isRelativePath(new File("//test.rep")));
			super.assertEquals("failed to root designator", false,
					pres.isRelativePath(new File("////test.rep")));
			super.assertEquals("failed to recognise absolute ref", true,
					pres.isRelativePath(new File("test.rep")));
			super.assertEquals("failed to recognise relative ref", true,
					pres.isRelativePath(new File("./test.rep")));
			super.assertEquals("failed to recognise parent ref", true,
					pres.isRelativePath(new File("../test.rep")));
		}

		@Test
		public final void testHandleScenarioFile()
		{
			MultiScenarioDisplay display = mock(MultiScenarioDisplay.class);
			MultiScenarioCore model = mock(MultiScenarioCore.class);
			MultiScenarioPresenter pres = new TestPresenter(display, model);
			UIDisplay ui = mock(UIDisplay.class);
			when(display.getUI()).thenReturn(ui);

			// ok, try for the scenario first
			String[] files =
			{ scenarioPath };

			assertTrue("scenario file exists", new File(scenarioPath).exists());

			pres.handleTheseFiles(files);

			// ok, check the display got set
			verify(display).setScenarioName("trial1.asset");
			verify(display).activate();

			// aah, and check the control wasn't set
			verify(display, never()).setControlName(anyString());
		}

		public void testTrackingSelection()
		{
			// TODO: test that we remove ourselves from unselected scenarios

		}

		public void testRealDataSinglePart()
		{

			// check we can see the files
			assertTrue("can't find scenario", new File(scenarioPath).exists());
			assertTrue("can't find control", new File(controlPath).exists());

			MultiScenarioDisplay display = mock(MultiScenarioDisplay.class);
			final MultiScenarioCore model = new MultiScenarioCore();
			final MultiScenarioPresenter pres = new MultiScenarioPresenter(display,
					model)
			{

				@Override
				protected void runThisJob(JobWithProgress theJob)
				{
					ASSETProgressMonitor monitor = new ASSETProgressMonitor()
					{
						public void beginTask(String name, int totalWork)
						{
						}

						public void worked(int work)
						{
						}
					};
					theJob.run(monitor);
				}
			};

			// just add support for a couple of methods that we need to work
			when(display.getProjectPathFor(new File("results"))).thenReturn(
					new File("results"));
			UIDisplay ui = mock(UIDisplay.class);
			when(display.getUI()).thenReturn(ui);

			doAnswer(new Answer<Object>()
			{
				public Object answer(InvocationOnMock invocation)
				{

					ScenarioType theScenario = (ScenarioType) model.getSimulations()
							.firstElement();

					// better wrap it
					ScenarioLayer sl = new ScenarioLayer();
					sl.setScenario(theScenario);

					ScenarioWrapper sw = new ScenarioWrapper(pres, sl);

					// tell it about any backdrop data
					Layer theBackdrop = theScenario.getBackdrop();
					if (theBackdrop != null)
						sw.addThisLayer(theBackdrop);

					// also tell it about any observers
					sw.fireNewController();

					pres.selectThis(sw);
					return null;
				}
			}).when(display).selectFirstRow();

			pres.handleTheseFiles(new String[]
			{ scenarioPath, controlPath });

			// check scenarios cleared
			verify(display).clearScenarios();

			// check the list of scenarios got set
			verify(display).setScenarios((MultiScenarioCore) anyObject());

			// check the UI is correctly enabled
			verify(ui, times(2)).setGenerateEnabled(false);
			verify(ui).setRunAllEnabled(false);
			verify(ui).setInitEnabled(true);
			verify(ui).setStepEnabled(false);
			verify(ui).setPlayEnabled(false);
		}

		public void testRealDataMultiPart()
		{

			// check we can see the files
			assertTrue("can't find scenario", new File(scenarioPath).exists());
			assertTrue("can't find control", new File(control_multiP_Path).exists());

			MultiScenarioDisplay display = mock(MultiScenarioDisplay.class);
			MultiScenarioCore model = new MultiScenarioCore();
			UIDisplay ui = mock(UIDisplay.class);
			MultiScenarioPresenter pres = new MultiScenarioPresenter(display, model);

			// just add support for a couple of methods that we need to work
			when(display.getProjectPathFor(new File("results"))).thenReturn(
					new File("results"));
			when(display.getUI()).thenReturn(ui);

			pres.handleTheseFiles(new String[]
			{ scenarioPath, control_multiP_Path });

			// ok, check they loaded
			verify(display).setScenarioName(anyString());
			verify(display).setControlName(anyString());

			// and that the UI looks right
			verify(ui).setGenerateEnabled(true);
			verify(ui).setRunAllEnabled(false);

			// and check the controller bits are done
			assertNotNull("controller loaded", pres._scenarioController);
			verify(display).activate();

			// ok, go for the generate
			pres.doGenerate();

		}

		@Test
		public final void testHandleControlFile()
		{
			MultiScenarioDisplay display = mock(MultiScenarioDisplay.class);
			MultiScenarioCore model = mock(MultiScenarioCore.class);
			MultiScenarioPresenter pres = new TestPresenter(display, model);

			// ok, try for the scenario first
			String[] files =
			{ controlPath };

			pres.handleTheseFiles(files);

			// ok, check the display got set
			verify(display).setControlName("trial1.xml");
			verify(display).activate();

			// and the scenario wasn't set
			verify(display, never()).setScenarioName(anyString());
		}

		@Test
		public final void testHandleBothFiles()
		{
			MultiScenarioDisplay display = mock(MultiScenarioDisplay.class);
			MultiScenarioCore model = mock(MultiScenarioCore.class);
			MultiScenarioPresenter pres = new TestPresenter(display, model);
			UIDisplay ui = mock(UIDisplay.class);
			when(display.getUI()).thenReturn(ui);

			// ok, try for the scenario first
			String[] files =
			{ controlPath, scenarioPath };

			pres.handleTheseFiles(files);

			// ok, check the display got set
			verify(display).setControlName("trial1.xml");
			verify(display).setScenarioName("trial1.asset");
			verify(display).activate();
		}

		protected static class TestPresenter extends MultiScenarioPresenter
		{
			public TestPresenter(MultiScenarioDisplay display, MultiScenarioCore model)
			{
				super(display, model);
			}

			@Override
			protected void controllerAssigned(String controlFile)
			{
				_controlfile = controlFile;
			}

		}

		public void testReloadDatafiles()
		{
			MultiScenarioDisplay display = mock(MultiScenarioDisplay.class);
			MultiScenarioCore model = mock(MultiScenarioCore.class);
			MultiScenarioPresenter pres = new TestPresenter(display, model);
			UIDisplay ui = mock(UIDisplay.class);
			when(display.getUI()).thenReturn(ui);

			pres._controlFileName = controlPath;
			pres._scenarioFileName = scenarioPath;

			// ok, try for the scenario first
			pres.reloadDataFiles();

			// ok, check the display got set
			verify(display).setControlName("trial1.xml");
			verify(display).setScenarioName("trial1.asset");
			verify(display).activate();
			verify(display).clearScenarios();
		}

	}

	public Vector<ScenarioObserver> getObservers()
	{
		return _myModel.getObservers();
	}

}
