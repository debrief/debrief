package org.mwc.asset.scenariocontroller2.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.asset.core.ASSETPlugin;
import org.mwc.asset.sample_data.SampleDataPlugin;
import org.mwc.cmap.core.DataTypes.Temporal.SteppableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.ASSETReaderWriter.ResultsContainer;
import ASSET.Util.XML.Control.StandaloneObserverListHandler;
import ASSET.Util.XML.Control.Observers.ScenarioControllerHandler;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class ScenarioController extends ViewPart implements ISelectionProvider
{

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

	/**
	 * The constructor.
	 */
	public ScenarioController()
	{
		_myScenario = new CoreScenario();
		_scenarioWrapper = new ScenarioWrapper(this);

		_myTimeProvider = new TimeManager();

		// listen to the scenario
		_myScenario.addScenarioSteppedListener(new ScenarioSteppedListener()
		{
			public void restart()
			{
				scenarioRestarted();
			}

			public void step(long newTime)
			{
				scenarioStepped(newTime);
			}
		});

		_myScenario.addScenarioRunningListener(new ScenarioRunningListener()
		{
			public void finished(long elapsedTime, String reason)
			{
				// update our own status indicator(s)
				setScenarioStatus(_myScenario, reason);

				// it's stopped running, tell the time controller, just in case it
				// wants to respond
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

			public void restart()
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

			@Override
			public void stop(Object origin, boolean fireUpdate)
			{
				_myScenario.pause();
			}

			@Override
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

		// let us accept dropped files
		configureFileDropSupport(_myUI);

		// fille in the menu bar(s)
		makeActions();
		contributeToActionBars();

		// declare fact that we can provide selections
		getSite().setSelectionProvider(this);
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
			res = new TimeControlProperties();
		}
		else if (adapter == TimeManager.LiveScenario.class)
		{
			return new TimeManager.LiveScenario()
			{
			};
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

	protected void filesDropped(String[] fileNames)
	{
		// ok, loop through the files
		for (int i = 0; i < fileNames.length; i++)
		{
			final String thisName = fileNames[i];

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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
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
			ASSETReaderWriter.importThis(_myScenario, scenarioStr, theStream);

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
				ResultsContainer results = ASSETReaderWriter.importThisControlFile(
						controlFile, new java.io.FileInputStream(controlFile));

				_theObservers = results.observerList;
			}

			if (_theObservers != null)
			{
				loadThisObserverList(controlFile, _theObservers);
			}

			// check if it's multi scenario..
			boolean isMulti = CommandLine.checkIfGenerationRequired(controlFile);
			final int tgtIndex = (isMulti) ? 1 : 0;

			// ui update, put it in an async operation
			// updating the text items has to be done in the UI thread. make it
			// so
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					_myUI.getScenarioTabs().setSelection(tgtIndex);

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

	private void loadThisObserverList(String controlFile,
			Vector<ScenarioObserver> theObservers) throws FileNotFoundException
	{
		// add these observers to our scenario
		for (int i = 0; i < theObservers.size(); i++)
		{
			// get the next observer
			ScenarioObserver observer = (ScenarioObserver) theObservers.elementAt(i);

			// setup the observer
			observer.setup(_myScenario);

			// and add it to our list
			_myObservers.add(observer);

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

		_actionReloadDatafiles = new Action()
		{
			public void run()
			{
				reloadDataFiles();
			}
		};
		_actionReloadDatafiles.setText("Reload");
		_actionReloadDatafiles.setToolTipText("Reload data files");
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
			page.openEditor(ie, "org.mwc.asset.ASSETPlotEditor");
		}
		catch (PartInitException e)
		{
			e.printStackTrace();
		}
	}

	protected void reloadDataFiles()
	{
		// get ourselves a scenario file, if we don't have one.
		if (_scenarioFileName == null)
		{
			URL scenURL = null;
			try
			{
				SampleDataPlugin data = new SampleDataPlugin();
				scenURL = data.getFileURL("/data/lookup_tutorial_scenario.xml");
			}
			catch (IOException e)
			{
				// aah well, never mind
				ASSETPlugin.logError(Status.ERROR, "Failed to load demo scenario data",
						e);
				e.printStackTrace();
			}
			// did it work?
			if (scenURL != null)
				_scenarioFileName = scenURL.getFile();
		}

		// now for the control file
		if (_controlFileName == null)
		{
			URL contURL = null;
			try
			{
				SampleDataPlugin data = new SampleDataPlugin();
				contURL = data.getFileURL("/data/lookup_test_control.xml");
			}
			catch (IOException e)
			{
				// aah well, never mind
				ASSETPlugin.logError(Status.ERROR, "Failed to load demo control data",
						e);
				e.printStackTrace();
			}
			// did it work?
			if (contURL != null)
				_controlFileName = contURL.getFile();
		}

		// ok, force the data-files to be reloaded
		if (_scenarioFileName != null)
			filesDropped(new String[]
			{ _scenarioFileName });
		if (_controlFileName != null)
			filesDropped(new String[]
			{ _controlFileName });
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
		return null;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection)
	{
		// ignore...
	}

}