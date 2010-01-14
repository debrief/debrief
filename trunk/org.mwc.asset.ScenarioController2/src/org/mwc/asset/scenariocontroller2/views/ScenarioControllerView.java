package org.mwc.asset.scenariocontroller2.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.asset.SimulationController.table.SimulationTable;
import org.mwc.asset.core.ASSETPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.SteppableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine;
import ASSET.GUI.CommandLine.MultiScenarioCore;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.ASSETReaderWriter.ResultsContainer;
import ASSET.Util.XML.Control.StandaloneObserverListHandler;
import ASSET.Util.XML.Control.Observers.ScenarioControllerHandler;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class ScenarioControllerView extends ViewPart implements
		ISelectionProvider, TimeManager.LiveScenario
{

	private static final String CONTROL_FILE_INDEX = "CONTROL_FILE";
	private static final String SCENARIO_FILE_INDEX = "SCENARIO_FILE";
	/**
	 * remember the files we've loaded
	 * 
	 */
	private String _scenarioFileName = null;
	private String _controlFileName = null;

	/**
	 * ui bits
	 * 
	 */
	private Action _viewInPlotter;
	private Action _actionReloadDatafiles;
	private DropTarget target;
	private UISkeleton _myUI;

	/**
	 * tactical data
	 * 
	 */
	private CoreScenario _myScenario;
	private Vector<ScenarioObserver> _myObservers = new Vector<ScenarioObserver>(
			0, 1);

	/**
	 * watchable parts
	 * 
	 */
	private TimeManager _myTimeProvider;

	Vector<ISelectionChangedListener> _selectionListeners;

	/**
	 * wrap the scenario so it can be shown in the layer manager
	 * 
	 */
	private ScenarioWrapper _scenarioWrapper;
	private Vector<ScenarioObserver> _theObservers;
	private SteppableTime _steppableTime;
	private MultiScenarioCore _myMultiScenario;
	private String[] _myPendingFilenames;
	private TimeControlPreferences _myTimeControlProps;

	/**
	 * support for anybody that wants to know how we're getting on
	 * 
	 */
	private PropertyChangeSupport _scenStopSupport;
	private SimulationTable _simTable;
	private ISelection _currentSelection;
	private ResultsContainer _multiRunResultsStore;

	/**
	 * The constructor.
	 */
	public ScenarioControllerView()
	{
		_myScenario = new CoreScenario();
		_scenarioWrapper = new ScenarioWrapper(this);

		_myTimeProvider = new TimeManager();

		// listen to the scenario
		_myScenario.addScenarioSteppedListener(new ScenarioSteppedListener()
		{
			public void restart(ScenarioType scenario)
			{
				scenarioRestarted();
			}

			public void step(ScenarioType scenario, long newTime)
			{
				scenarioStepped(newTime);
			}
		});

		_myScenario.addScenarioRunningListener(new ScenarioRunningListener()
		{
			public void finished(long elapsedTime, String reason)
			{
				// communicate what's happened to the time controller, if there is one.
				if (_scenStopSupport != null)
					_scenStopSupport.firePropertyChange(
							TimeManager.LiveScenario.FINISHED, null, this);

				// update our own status indicator(s)
				setScenarioStatus(_myScenario, reason);

				// tell the observers that it's all over
				tearDownObservers(_myScenario);

				// it's stopped running, refresh the workspace
				IProject theProj = getAProject();
				try
				{
					theProj.refreshLocal(2, null);
				}
				catch (CoreException e)
				{
					ASSETPlugin.logError(Status.ERROR,
							"Had trouble refreshing project folder", e);
					e.printStackTrace();
				}

			}

			public void newScenarioStepTime(int val)
			{
			}

			public void newStepTime(int val)
			{
			}

			public void paused()
			{
			}

			public void started()
			{
			}

			public void restart(ScenarioType scenario)
			{
			}
		});

		/**
		 * and support for the time controller moving us forward
		 * 
		 */
		_steppableTime = new SteppableTime()
		{
			public void run(Object origin, boolean fireUpdate)
			{
				_myScenario.start();
			}

			public void step(Object origin, boolean fireUpdate)
			{
				_myScenario.step();
			}

			public void stop(Object origin, boolean fireUpdate)
			{
				_myScenario.pause();
			}

			public void restart(Object origin, boolean fireUpdate)
			{
				_myScenario.restart();
			}
		};

	}

	protected void scenarioRestarted()
	{
		setScenarioStatus(_myScenario, "Restarted");
		_myTimeProvider.setTime(this, new HiResDate(_myScenario.getTime()), true);
	}

	protected void scenarioStepped(long newTime)
	{
		// update anybody listening to the time
		_myTimeProvider.setTime(this, new HiResDate(newTime), true);

		// and update the displayed time
		setScenarioStatus(_myScenario, FormatRNDateTime.toString(newTime));
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		// create our UI
		_myUI = new UISkeleton(parent, SWT.FILL);
		// _myUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		_myUI.getMultiTableHolder().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		_myUI.getScenarioTabs().addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (_myUI.getScenarioTabs().getSelectionIndex() == 0)
				{
					EditableWrapper ew = new EditableWrapper(_scenarioWrapper);
					StructuredSelection sel = new StructuredSelection(ew);
					setSelection(sel);
				}
				else
				{
					setSelection(null);
				}

			}
		});

		_simTable = new SimulationTable(_myUI.getMultiTableHolder(), this);
		_simTable.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		// multiUI = new SimControllerUI(_myUI.getMultiTableHolder());
		// multiUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
		// 1));
		// multiUI.pack();

		// let us accept dropped files
		configureFileDropSupport(_myUI);

		// fille in the menu bar(s)
		makeActions();
		contributeToActionBars();

		// declare fact that we can provide selections (and let our scenario table
		// know we do it aswell)
		getSite().setSelectionProvider(this);
		_simTable.setSelectionProvider(this);

		// now listen to the UI buttons
		_myUI.getDoGenerateButton().addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// ok, do the gen
				doMultiGenerateOperation();
			}
		});

		_myUI.getRunBtn().addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				doMultiRunOperation();
			}
		});

		// if we have any pending filenames, get them dropped
		if (_myPendingFilenames != null)
			filesDropped(_myPendingFilenames);

		// listen out for the single scenario being run
		_myUI.getSingleRunBtn().addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				// ok, run the scenario
				_myScenario.start();
			}
		});

	}

	protected void doMultiRunOperation()
	{
		System.out.println("doing run");

		Thread doRun = new Thread()
		{

			@Override
			public void run()
			{
				_myMultiScenario.nowRun(System.out, System.err, System.in);
			}
		};
		doRun.start();
	}

	protected static class WrappedProgressMonitor implements ASSETProgressMonitor
	{
		final IProgressMonitor monitor;

		public WrappedProgressMonitor(IProgressMonitor val)
		{
			monitor = val;

		}

		@Override
		public void beginTask(String name, int totalWork)
		{
			monitor.beginTask(name, totalWork);
		}

		@Override
		public void worked(int work)
		{
			monitor.worked(work);
		}
	}

	protected void doMultiGenerateOperation()
	{
		// disable the genny button, until it's done.
		_myUI.getDoGenerateButton().setEnabled(false);

		Job job = new Job("Prepare multiple scenarios")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{

					ASSETProgressMonitor pMon = new WrappedProgressMonitor(monitor);

					if (_myMultiScenario == null)
					{
						// create a new, fresh multi scenario generator
						_myMultiScenario = new MultiScenarioCore();
					}

					// and let it create some files
					_myMultiScenario.prepareFiles(_controlFileName, _scenarioFileName,
							System.out, System.err, System.in, pMon, _multiRunResultsStore.outputDirectory);

					// and sort out the observers
					_myMultiScenario.prepareControllers(_multiRunResultsStore, pMon);

					// ok, now give the scenarios to the multi scenario table (in the UI
					// thread
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							_simTable.setInput(_myMultiScenario);
							// and make the run button live
							_myUI.getRunBtn().setEnabled(true);
							_myUI.getDoGenerateButton().setEnabled(true);
						}
					});
				}
				catch (Exception e)
				{
					CorePlugin.logError(Status.ERROR, "Failed in scenario generation", e);
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}
		};

		job.addJobChangeListener(new JobChangeAdapter()
		{
			public void done(IJobChangeEvent event)
			{
				if (event.getResult().isOK())
					System.out.println("Job completed successfully");
				else
					System.err.println("Job did not complete successfully");
			}
		});
		job.setUser(true);
		job.schedule(); // start as soon as possible
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == Layers.class)
		{
			res = _scenarioWrapper;
		}
		else if (adapter == ScenarioType.class)
		{
			res = _myScenario;
		}
		else if (adapter == TimeProvider.class)
		{
			res = _myTimeProvider;
		}
		else if (adapter == TimeControlPreferences.class)
		{
			if (_myTimeControlProps == null)
				_myTimeControlProps = new TimeControlProperties();

			res = _myTimeControlProps;
		}
		else if (adapter == TimeManager.LiveScenario.class)
		{
			return this;
		}
		else if (adapter == SteppableTime.class)
		{
			return _steppableTime;
		}

		if (res == null)
		{
			res = super.getAdapter(adapter);
		}
		return res;
	}

	public Vector<ScenarioObserver> getObservers()
	{
		return _theObservers;
	}

	public ScenarioType getScenario()
	{
		return _myScenario;
	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport(Control _pusher)
	{
		int dropOperation = DND.DROP_COPY;
		Transfer[] dropTypes =
		{ FileTransfer.getInstance() };

		target = new DropTarget(_pusher, dropOperation);
		target.setTransfer(dropTypes);
		target.addDropListener(new DropTargetListener()
		{
			public void dragEnter(DropTargetEvent event)
			{
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					if (event.detail != DND.DROP_COPY)
					{
						event.detail = DND.DROP_COPY;
					}
				}
			}

			public void dragLeave(DropTargetEvent event)
			{
			}

			public void dragOperationChanged(DropTargetEvent event)
			{
			}

			public void dragOver(DropTargetEvent event)
			{
			}

			public void dropAccept(DropTargetEvent event)
			{
			}

			public void drop(DropTargetEvent event)
			{
				String[] fileNames = null;
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
				{
					fileNames = (String[]) event.data;
				}
				if (fileNames != null)
				{
					filesDropped(fileNames);
				}
			}

		});

	}

	public void filesDropped(String[] fileNames)
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
						_myUI.getScenarioVal().setText(new File(thisName).getName());

						IWorkbench wb = PlatformUI.getWorkbench();
						IProgressService ps = wb.getProgressService();
						try
						{
							ps.busyCursorWhile(new IRunnableWithProgress()
							{
								public void run(IProgressMonitor pm)
								{
									scenarioAssigned(thisName);
								}
							});
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}

					}
					else if (firstNode.equals("ScenarioController"))
					{
						// remember it
						_controlFileName = thisName;

						// show it
						_myUI.getControlVal().setText(new File(thisName).getName());

						IWorkbench wb = PlatformUI.getWorkbench();
						IProgressService ps = wb.getProgressService();
						try
						{
							ps.busyCursorWhile(new IRunnableWithProgress()
							{
								public void run(IProgressMonitor pm)
								{
									controllerAssigned(thisName);
								}
							});
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}

		// lastly, select me - so our listeners get informed.
		// - note, we do it in a runnable because things can get a little recursive
		// if we're trying to show a view whilst its still being defined.
		Runnable doIt = new Runnable()
		{
			@Override
			public void run()
			{
				activateMe();
			}
		};
		Display.getCurrent().asyncExec(doIt);
	}

	private void scenarioAssigned(String thisName)
	{
		// now load the data
		try
		{
			// ditch any existing participants
			_myScenario.emptyParticipants();

			// now load the new ones
			String scenarioStr = thisName;
			File theFile = new File(scenarioStr);
			// final SampleDataPlugin thePlugin = SampleDataPlugin.getDefault();
			InputStream theStream = new FileInputStream(theFile);//
			// thePlugin.getResource(thePath);
			ASSETReaderWriter.importThis(_myScenario, _scenarioWrapper, scenarioStr,
					theStream);

			fireScenarioChanged();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ASSETPlugin.logError(Status.ERROR, "Failed to load sample data", e);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			ASSETPlugin.logError(Status.ERROR, "The sample-data plugin isn't loaded",
					e);
		}

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

				_multiRunResultsStore = ASSETReaderWriter.importThisControlFile(
						controlFile, new java.io.FileInputStream(controlFile));

				_theObservers = _multiRunResultsStore.observerList;

				// since we have a results container - we have enough information to set
				// the output files
				File tgtDir = _multiRunResultsStore.outputDirectory;

				// if the tgt dir is a relative reference, make it relative to
				// our first project, not the user's login directory
				if (isRelativePath(tgtDir))
				{
					// prepend the target directory with the root of the current project
					IProject someProject = getAProject();
					if (someProject != null)
					{
						// get the file-system path to this folder
						IPath filePath = someProject.getLocation();

						// ok, now stick the output folder in this parent
						tgtDir = new File(filePath.toOSString() + File.separator
								+ tgtDir.getPath());
						
						// and store the new location
						_multiRunResultsStore.outputDirectory = tgtDir;
					}
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

			// what's the index of the multi-run scenario tab?
			final int tgtIndex = (isMulti) ? 1 : 0;

			// ui update, put it in an async operation
			// updating the text items has to be done in the UI thread. make it
			// so
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					// show the correct tab
					_myUI.getScenarioTabs().setSelection(tgtIndex);

					if (isMulti)
					{
						// ok, enable the relevant elements
						updateMultiTab();

						// set the selection object to nothing
						setSelection(null);
					}
					else
					{
						// and enable the relevant elements
						updateSingleTab();

						// ok, now wrap it as an editable
						EditableWrapper ew = new EditableWrapper(_scenarioWrapper);

						// and as a selection
						StructuredSelection strSel = new StructuredSelection(ew);
						setSelection(strSel);
					}

					// and tell everybody
					fireControllerChanged();
				}
			});

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
		if (_myObservers != null)
		{
			// are we in a multi-core run
			if (_myMultiScenario == null)
			{
				// tell them all we're closing
				if (_myScenario != null)
				{
					for (Iterator<ScenarioObserver> iterator = _myObservers.iterator(); iterator
							.hasNext();)
					{
						ScenarioObserver thisO = iterator.next();
						thisO.tearDown(_myScenario);
					}
				}
			}
			// and ditch the list
			_myObservers.removeAllElements();
		}
	}

	private void updateSingleTab()
	{
		if (_myScenario != null)
			_myUI.getSingleRunBtn().setEnabled(true);

	}

	private void updateMultiTab()
	{
		// and disable the single run button
		_myUI.getSingleRunBtn().setEnabled(false);
		_myUI.getSingleScenarioStatus().setText("Pending");

		// ok, disable the run button,
		_myUI.getRunBtn().setEnabled(false);

		// and now enable the genny button
		_myUI.getDoGenerateButton().setEnabled(true);
	}

	private IProject getAProject()
	{
		IProject res = null;
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		if (projects != null)
		{
			if (projects.length > 0)
				res = projects[0];
		}
		return res;
	}

	private static boolean isRelativePath(File tgtDir)
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

	private void loadThisObserverList(String controlFile,
			Vector<ScenarioObserver> theObservers) throws FileNotFoundException
	{
		// add these observers to our scenario
		for (int i = 0; i < theObservers.size(); i++)
		{
			// get the next observer
			ScenarioObserver observer = theObservers.elementAt(i);

			// and add it to our list
			_myObservers.add(observer);

		}

		setupObservers(_myScenario);

	}

	/**
	 * right - store ourselves into the supplied memento object
	 * 
	 * @param memento
	 */
	public void saveState(IMemento memento)
	{
		// let our parent go for it first
		super.saveState(memento);

		if (_scenarioFileName != null)
			memento.putString(SCENARIO_FILE_INDEX, _scenarioFileName);
		if (_controlFileName != null)
			memento.putString(CONTROL_FILE_INDEX, _controlFileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite,
	 * org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		// let the parent do its bits
		super.init(site, memento);

		Vector<String> pendingFilenames = new Vector<String>();

		// are we showing the units column?
		if (memento != null)
		{
			String scenFile = memento.getString(SCENARIO_FILE_INDEX);
			if (scenFile != null)
			{
				pendingFilenames.add(scenFile);
			}
			String contFile = memento.getString(CONTROL_FILE_INDEX);
			if (contFile != null)
			{
				pendingFilenames.add(contFile);
			}
		}

		// did we receive any?
		if (pendingFilenames.size() > 0)
			_myPendingFilenames = pendingFilenames.toArray(new String[]
			{});

	}

	private void setupObservers(ScenarioType theScenario)
	{
		// and actually setup the observers
		for (Iterator<ScenarioObserver> iterator = _myObservers.iterator(); iterator
				.hasNext();)
		{
			ScenarioObserver thisO = iterator.next();
			thisO.setup(theScenario);
		}
	}

	private void tearDownObservers(ScenarioType theScenario)
	{
		// and actually setup the observers
		for (Iterator<ScenarioObserver> iterator = _myObservers.iterator(); iterator
				.hasNext();)
		{
			ScenarioObserver thisO = iterator.next();
			thisO.tearDown(theScenario);
		}
	}

	/**
	 * a scenario has been loaded, tell our listeners
	 * 
	 */
	private void fireScenarioChanged()
	{
		_scenarioWrapper.fireNewScenario();

		// ok, change the time aswell
		long time = _myScenario.getTime();
		if (time != -1)
		{
			_myTimeProvider.setTime(this, new HiResDate(time), true);
		}

		// also, re-initialise the observers
		setupObservers(_myScenario);

		// and show the loaded status in the ui
		setScenarioStatus(_myScenario, "Loaded");
	}

	/**
	 * update the status in the UI
	 * 
	 * @param scen
	 *          the scenario we're updating
	 * @param text
	 *          the text to display
	 */
	private void setScenarioStatus(final ScenarioType scen, final String text)
	{
		// ui update, put it in an async operation
		// updating the text items has to be done in the UI thread. make it
		// so
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (scen == _myScenario)
				{
					_myUI.getSingleScenarioStatus().setText(text);
				}
			}
		});

	}

	/**
	 * a scenario controller has been loaded, tell our listeners
	 * 
	 */
	private void fireControllerChanged()
	{
		_scenarioWrapper.fireNewController();
	}

	private String getFirstNodeName(String SourceXMLFilePath)
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

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_viewInPlotter);
		manager.add(new Separator());
		manager.add(_actionReloadDatafiles);
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_viewInPlotter);
		manager.add(_actionReloadDatafiles);
	}

	private void makeActions()
	{
		_viewInPlotter = new Action()
		{
			public void run()
			{
				openPlotter();
			}
		};
		_viewInPlotter.setText("View in LPD");
		_viewInPlotter.setToolTipText("View 2D overview of scenario");
		_viewInPlotter.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/overview.gif"));

		_actionReloadDatafiles = new Action()
		{
			public void run()
			{
				reloadDataFiles();
			}
		};
		_actionReloadDatafiles.setText("Reload");
		_actionReloadDatafiles.setToolTipText("Reload data files");
		ImageDescriptor desc = CorePlugin.getImageDescriptor("icons/repaint.gif");
		_actionReloadDatafiles.setImageDescriptor(desc);
	}

	protected void openPlotter()
	{
		IWorkbenchPage page = this.getViewSite().getPage();
		IEditorInput ie = new IEditorInput()
		{
			public boolean exists()
			{
				return true;
			}

			public ImageDescriptor getImageDescriptor()
			{
				return ImageDescriptor.getMissingImageDescriptor();
			}

			public String getName()
			{
				return "Pending";
			}

			public IPersistableElement getPersistable()
			{
				return null;
			}

			public String getToolTipText()
			{
				return "Pending plot";
			}

			@SuppressWarnings("unchecked")
			public Object getAdapter(Class adapter)
			{
				return null;
			}
		};
		try
		{
			// first, open the editor
			page.openEditor(ie, "org.mwc.asset.ASSETPlotEditor");

			// now fire ourselves as active
			activateMe();
		}
		catch (PartInitException e)
		{
			ASSETPlugin.logError(Status.ERROR, "trouble opening ScenarioPlotter", e);
			e.printStackTrace();
		}
	}

	private void activateMe()
	{
		try
		{
			// just check we're alive - just in case we've been called before
			// the init is complete
			IWorkbenchPartSite site = getSite();
			IWorkbenchWindow window = site.getWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				// try to find our view first
				IViewPart theView = page.findView(site.getId());
				if (theView != null)
				{
					// cool, show it then
					page.showView(site.getId());
				}
			}
		}
		catch (PartInitException e)
		{
			// demote this error, it happens quite frequently when we're still opening
			ASSETPlugin
					.logError(
							Status.WARNING,
							"failed to activate scenario controller - possible because trying to activing during init",
							null);
		}
	}

	protected void reloadDataFiles()
	{
		// ok, force the data-files to be reloaded
		if (_scenarioFileName != null)
			filesDropped(new String[]
			{ _scenarioFileName });
		if (_controlFileName != null)
			filesDropped(new String[]
			{ _controlFileName });

		// and clear the scenario table
		_simTable.setInput(null);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		// viewer.getControl().setFocus();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	public ISelection getSelection()
	{
		return _currentSelection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection)
	{
		_currentSelection = selection;

		// and tell everybody about it
		if (_selectionListeners != null)
		{
			SelectionChangedEvent event = new SelectionChangedEvent(this,
					_currentSelection);
			for (ISelectionChangedListener thisL : _selectionListeners)
			{
				thisL.selectionChanged(event);
			}
		}
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
					ScenarioControllerView.isRelativePath(new File("c:\\test.rep")));
			super.assertEquals("failed to root designator", false,
					ScenarioControllerView.isRelativePath(new File("\\test.rep")));
			super.assertEquals("failed to root designator", false,
					ScenarioControllerView.isRelativePath(new File("\\\\test.rep")));
			super.assertEquals("failed to root designator", false,
					ScenarioControllerView.isRelativePath(new File("//test.rep")));
			super.assertEquals("failed to root designator", false,
					ScenarioControllerView.isRelativePath(new File("////test.rep")));
			super.assertEquals("failed to recognise absolute ref", true,
					ScenarioControllerView.isRelativePath(new File("test.rep")));
			super.assertEquals("failed to recognise relative ref", true,
					ScenarioControllerView.isRelativePath(new File("./test.rep")));
			super.assertEquals("failed to recognise parent ref", true,
					ScenarioControllerView.isRelativePath(new File("../test.rep")));
		}
	}

	@Override
	public void addStoppedListener(PropertyChangeListener listener)
	{
		if (_scenStopSupport == null)
			_scenStopSupport = new PropertyChangeSupport(listener);
		_scenStopSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removeStoppedListener(PropertyChangeListener listener)
	{
		if (_scenStopSupport != null)
			_scenStopSupport.removePropertyChangeListener(listener);
	}
}