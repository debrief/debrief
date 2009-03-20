package org.mwc.asset.scenariocontroller2.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.mwc.asset.core.ASSETPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ASSET.ScenarioType;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.Layers;

public class ScenarioController extends ViewPart implements ISelectionProvider 
{

	private static final String DUMMY_CONTROL_FILE = "C:\\dev\\cmap\\org.mwc.asset.sample_data\\data\\force_prot_control.xml";
	private static final String DUMMY_SCENARIO_FILE = "C:\\dev\\cmap\\org.mwc.asset.sample_data\\data\\force_prot_scenario.xml";
	/**
	 * ui bits
	 * 
	 */
	private Action action1;
	private Action action2;
	private DropTarget target;
	private UISkeleton _myUI;

	/**
	 * tactical data
	 * 
	 */
	private CoreScenario _myScenario;
	private Vector<ScenarioObserver> _myObservers = new Vector<ScenarioObserver>(
			0, 1);

	Vector<ISelectionChangedListener> _selectionListeners;
	
	/** wrap the scenario so it can be shown in the layer manager
	 * 
	 */
	private ScenarioWrapper _scenarioWrapper;

	/**
	 * The constructor.
	 */
	public ScenarioController()
	{
		_myScenario = new CoreScenario();
		_scenarioWrapper = new ScenarioWrapper(this);
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
		else if(adapter == ScenarioType.class)
		{
			res = _myScenario;
		}

		if (res == null)
		{
			res = super.getAdapter(adapter);
		}
		return res;
	}	

	private void initialiseDummyData()
	{
		_myUI.getScenarioVal().setText(DUMMY_SCENARIO_FILE);
		_myUI.getControlVal().setText(DUMMY_CONTROL_FILE);
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
					// set the filename
					_myUI.getScenarioVal().setText(thisName);

					IWorkbench wb = PlatformUI.getWorkbench();
				   IProgressService ps = wb.getProgressService();
				   try
					{
						ps.busyCursorWhile(new IRunnableWithProgress() {
						    public void run(IProgressMonitor pm) {
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
					_myUI.getControlVal().setText(thisName);
					IWorkbench wb = PlatformUI.getWorkbench();
				   IProgressService ps = wb.getProgressService();
				   try
					{
						ps.busyCursorWhile(new IRunnableWithProgress() {
						    public void run(IProgressMonitor pm) {
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
			Vector<ScenarioObserver> theObservers = ASSETReaderWriter
					.importThisObserverList(controlFile, new java.io.FileInputStream(
							controlFile));
	
			// add these observers to our scenario
			for (int i = 0; i < theObservers.size(); i++)
			{
				// get the next observer
				ScenarioObserver observer = (ScenarioObserver) theObservers
						.elementAt(i);
	
				// setup the observer
				observer.setup(_myScenario);
	
				// and add it to our list
				_myObservers.add(observer);
				
				// and tell everybody
				fireControllerChanged();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * a scenario has been loaded, tell our listeners
	 * 
	 */
	private void fireScenarioChanged()
	{
		_scenarioWrapper.fireNewScenario();
	}

	/**
	 * a scenario controller has been loaded, tell our listeners
	 * 
	 */
	private void fireControllerChanged()
	{
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
			System.out.println("First Node : " + nl.item(0).getNodeName());
			return nl.item(0).getNodeName();

			// for (int i=0; i<nl.getLength(); i++)
			// {
			// n = nl.item(i);
			// System.out.println(n.getNodeName() + " " );
			// }
		}
		catch (IOException ioe)
		{
			// ioe.printStackTrace();
			return null;
			// return "Not Valid XML File";
		}
		catch (Exception sxe)
		{
//			Exception x = sxe;
			return null;
			// x.printStackTrace();
			//return "Not Valid XML File";
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
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions()
	{
		action1 = new Action()
		{
			public void run()
			{
				initialiseDummyData();
			}
		};
		action1.setText("Load dummy");

		action2 = new Action()
		{
			public void run()
			{
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
				"Scenario controller", message);
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