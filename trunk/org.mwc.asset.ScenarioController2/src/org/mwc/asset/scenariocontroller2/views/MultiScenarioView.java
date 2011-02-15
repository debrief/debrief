package org.mwc.asset.scenariocontroller2.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Vector;

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
import org.eclipse.jface.action.IToolBarManager;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.SimulationController.table.SimulationTable;
import org.mwc.asset.core.ASSETPlugin;
import org.mwc.asset.scenariocontroller2.ScenContPresenter;
import org.mwc.asset.scenariocontroller2.ScenContPresenter.FilesDroppedListener;
import org.mwc.asset.scenariocontroller2.ScenContPresenter.JobWithProgress;
import org.mwc.asset.scenariocontroller2.ScenContPresenter.ManageMultiListener;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.SteppableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager.LiveScenario;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;

import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.GUI.CommandLine.MultiScenarioCore;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;

public class MultiScenarioView extends ViewPart implements
		ISelectionProvider, TimeManager.LiveScenario, ScenContPresenter.Display
{

	/**
	 * support class to make SWT progress monitor look like our ASSET one
	 * 
	 * @author ian
	 * 
	 */
	protected static class WrappedProgressMonitor implements ASSETProgressMonitor
	{
		final IProgressMonitor monitor;

		public WrappedProgressMonitor(IProgressMonitor val)
		{
			monitor = val;

		}

		public void beginTask(String name, int totalWork)
		{
			monitor.beginTask(name, totalWork);
		}

		public void worked(int work)
		{
			monitor.worked(work);
		}
	}

	private static final String CONTROL_FILE_INDEX = "CONTROL_FILE";
	private static final String SCENARIO_FILE_INDEX = "SCENARIO_FILE";

	/**
	 * ui bits
	 * 
	 */
	private UISkeleton _myUI;

	/**
	 * watchable parts
	 * 
	 */
	Vector<ISelectionChangedListener> _selectionListeners;

	/**
	 * wrap the scenario so it can be shown in the layer manager
	 * 
	 */
	private WrappingSteppableTime _timeManager;
	private String[] _myPendingFilenames;
	private TimeControlPreferences _myTimeControlProps;

	/**
	 * support for anybody that wants to know how we're getting on
	 * 
	 */
	private PropertyChangeSupport _scenStopSupport;
	private SimulationTable _simTable;
	private ISelection _currentSelection;
	private Duration _myPendingStepSize;
	private FilesDroppedListener _filesDroppedListener;
	private ScenContPresenter _myPresenter;
	private ManageMultiListener _multiHandler;

	/**
	 * The constructor.
	 */
	public MultiScenarioView()
	{
		// sort out the presenter
		_myPresenter = new ScenContPresenter(this, new MultiScenarioCore());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		// create our UI
		_myUI = new UISkeleton(parent, SWT.FILL);

		_myUI.getMultiTableHolder().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// and the table of scenarios
		_simTable = new SimulationTable(_myUI.getMultiTableHolder(), _myPresenter);
		_simTable.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

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
		_myUI.getDoGenerateButton().addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				_multiHandler.doGenerate();
			}
		});

		_myUI.getRunBtn().addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				_multiHandler.doRun();
			}
		});

		// if we have any pending filenames, get them dropped
		if (_myPendingFilenames != null)
			_filesDroppedListener.filesDropped(_myPendingFilenames);
	}

	public void addMultiScenarioHandler(ManageMultiListener listener)
	{
		_multiHandler = listener;
	}

	public void refreshWorkspace()
	{
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

	public void setGenerateState(final boolean state)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				_myUI.getDoGenerateButton().setEnabled(state);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		Object res = null;

		if (adapter == ScenarioType.class)
		{
			// TODO: return the selected scenario
		}
		else if (adapter == TimeProvider.class)
		{
			res = _timeManager;
		}
		else if (adapter == TimeControlPreferences.class)
		{
			if (_myTimeControlProps == null)
			{
				_myTimeControlProps = new TimeControlProperties();
				_myTimeControlProps.setAutoInterval(_myPendingStepSize);
			}

			res = _myTimeControlProps;
		}
		else if (adapter == TimeManager.LiveScenario.class)
		{
			// return the current selected item
			StructuredSelection sel = (StructuredSelection) _currentSelection;
			if (sel != null)
			{
				EditableWrapper obj = (EditableWrapper) sel.getFirstElement();
				if (obj instanceof EditableWrapper)
				{
					Editable ed = obj.getEditable();
					if (ed instanceof LiveScenario)
						return ed;
				}
			}
		}
		else if (adapter == SteppableTime.class)
		{
			return _timeManager;
		}

		if (res == null)
		{
			res = super.getAdapter(adapter);
		}
		return res;
	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport(Control _pusher)
	{
		int dropOperation = DND.DROP_COPY;
		Transfer[] dropTypes =
		{ FileTransfer.getInstance() };

		DropTarget target = new DropTarget(_pusher, dropOperation);
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
					if (_filesDroppedListener != null)
						_filesDroppedListener.filesDropped(fileNames);
				}
			}

		});

	}

	public void clearScenarios()
	{
		// ui update, put it in an async operation
		// updating the text items has to be done in the UI thread. make it
		// so
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				// ok, disable the run button,
				_myUI.getRunBtn().setEnabled(false);

				// and now enable the genny button
				_myUI.getDoGenerateButton().setEnabled(true);
				
				// and clear the scenario table
				_simTable.setInput(null);
			}
		});

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

	public File getProjectPathFor(File tgtDir)
	{
		File res = null;

		// prepend the target directory with the root of the current project
		IProject someProject = getAProject();
		if (someProject != null)
		{
			// get the file-system path to this folder
			IPath filePath = someProject.getLocation();

			// ok, now stick the output folder in this parent
			tgtDir = new File(filePath.toOSString() + File.separator
					+ tgtDir.getPath());

			res = tgtDir;
		}

		return res;
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

		String _scenarioFileName = _myPresenter.getScenarioName();
		String _controlFileName = _myPresenter.getControlName();
		
		if (_scenarioFileName != null)
			memento.putString(SCENARIO_FILE_INDEX, _scenarioFileName);
		if (_controlFileName != null)
			memento.putString(CONTROL_FILE_INDEX, _controlFileName);

		if (_myTimeControlProps != null)
		{
			Duration stepSize = _myTimeControlProps.getAutoInterval();
			String stepSizeStr = "" + stepSize.getValueIn(Duration.MILLISECONDS);
			memento.putString("StepInterval", stepSizeStr);
		}
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

			// also, see if we have an auto-step size property
			String stepSizeStr = memento.getString("StepInterval");
			if (stepSizeStr != null)
			{
				// and store it.
				Double duration = Double.valueOf(stepSizeStr);
				_myPendingStepSize = new Duration(duration, Duration.MILLISECONDS);
			}

		}

		// did we receive any?
		if (pendingFilenames.size() > 0)
			_myPendingFilenames = pendingFilenames.toArray(new String[]
			{});

	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		Action viewInPlotter = new Action()
		{
			public void run()
			{
				openPlotter();
			}
		};
		viewInPlotter.setText("View in LPD");
		viewInPlotter.setToolTipText("View 2D overview of scenario");
		viewInPlotter.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/overview.gif"));

		Action actionReloadDatafiles = new Action()
		{
			public void run()
			{
				_myPresenter.reloadDataFiles();
			}
		};
		actionReloadDatafiles.setText("Reload");
		actionReloadDatafiles.setToolTipText("Reload data files");
		ImageDescriptor desc = CorePlugin.getImageDescriptor("icons/repaint.gif");
		actionReloadDatafiles.setImageDescriptor(desc);
		
		// and display them
		manager.add(viewInPlotter);
		manager.add(actionReloadDatafiles);
	}

	private void makeActions()
	{

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

			@SuppressWarnings("rawtypes")
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
			activate();
		}
		catch (PartInitException e)
		{
			ASSETPlugin.logError(Status.ERROR, "trouble opening ScenarioPlotter", e);
			e.printStackTrace();
		}
	}

	public void activate()
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
			// is there something there?
			if (selection != null)
			{
				SelectionChangedEvent event = new SelectionChangedEvent(this,
						_currentSelection);
				for (ISelectionChangedListener thisL : _selectionListeners)
				{
					thisL.selectionChanged(event);
				}
			}
		}
	}

	public void addStoppedListener(PropertyChangeListener listener)
	{
		if (_scenStopSupport == null)
			_scenStopSupport = new PropertyChangeSupport(listener);
		_scenStopSupport.addPropertyChangeListener(listener);
	}

	public void removeStoppedListener(PropertyChangeListener listener)
	{
		if (_scenStopSupport != null)
			_scenStopSupport.removePropertyChangeListener(listener);
	}

	public void addFileDropListener(FilesDroppedListener listener)
	{
		_filesDroppedListener = listener;
	}

	public void setScenarios(final MultiScenarioCore _myModel)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				_simTable.setInput(_myModel);
			}
		});
	}

	public void setRunState(final boolean state)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				_myUI.getRunBtn().setEnabled(state);
			}
		});
	}

	public void runThisJob(final JobWithProgress theJob)
	{
		Job swtJob = new Job("Prepare multiple scenarios")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					// provide a wrapped progress monitpr
					ASSETProgressMonitor pMon = new WrappedProgressMonitor(monitor);

					// and run the job
					theJob.run(pMon);
				}
				catch (Exception e)
				{
					CorePlugin.logError(Status.ERROR, "Failed in scenario generation", e);
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}
		};

		swtJob.addJobChangeListener(new JobChangeAdapter()
		{
			public void done(IJobChangeEvent event)
			{
				if (event.getResult().isOK())
					System.out.println("Job completed successfully");
				else
					System.err.println("Job did not complete successfully");
			}
		});
		swtJob.setUser(true);
		swtJob.schedule(); // start as soon as possible
	}

	public void setScenarioName(final String name)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				_myUI.getScenarioVal().setText(name);
			}
		});
	}

	public void setControlName(final String name)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				_myUI.getControlVal().setText(name);
			}
		});
	}

}