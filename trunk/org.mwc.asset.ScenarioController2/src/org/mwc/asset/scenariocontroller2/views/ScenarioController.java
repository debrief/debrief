package org.mwc.asset.scenariocontroller2.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.core.ASSETPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;

public class ScenarioController extends ViewPart
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
	private Vector<ScenarioObserver> _myObservers = new Vector<ScenarioObserver>(0,1);

	/**
	 * The constructor.
	 */
	public ScenarioController()
	{
		_myScenario = new CoreScenario();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		// create our UI
		_myUI = new UISkeleton(parent, SWT.FILL);

		// listen to the load button
		_myUI.getLoadBtn().addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				loadTheScenario(_myUI.getScenarioVal().getText());
				loadController(_myUI.getControlVal().getText());
			}

		});

		// let us accept dropped files
		configureFileDropSupport(_myUI);

		// fille in the menu bar(s)
		makeActions();
		contributeToActionBars();
	}

	private void initialiseDummyData()
	{
		_myUI.getScenarioVal().setText(DUMMY_SCENARIO_FILE);
		_myUI.getControlVal().setText(DUMMY_CONTROL_FILE);
		_myUI.getLoadBtn().setEnabled(true);
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
			String thisName = fileNames[i];

			// ok, examine this file
			String firstNode = getFirstNodeName(thisName);

			if (firstNode != null)
			{
				boolean scenarioFound = false;
				if (firstNode.equals("Scenario"))
				{
					_myUI.getScenarioVal().setText(thisName);
					scenarioFound = true;
				} else if (firstNode.equals("ScenarioController"))
				{
					_myUI.getControlVal().setText(thisName);
				}
				
				// decide whether to enable the Load button
				if(scenarioFound)
					_myUI.getLoadBtn().setEnabled(true);
			}
		}
	}

	private String getFirstNodeName(String filename)
	{
		String res = null;
		Document thisD = loadFileIntoDom(filename);

		Node thisN = thisD.getFirstChild();
		return res;
	}

	private Document loadFileIntoDom(String filename)
	{
		Document res = null;

		DocumentBuilder db;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			res = db.parse(filename);
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return res;
	}

	private void selectTab(boolean isSingle)
	{
		if (isSingle)
			_myUI.getScenarioTabs().setSelection(0);
		else
			_myUI.getScenarioTabs().setSelection(1);

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

	protected void loadTheScenario(String theScenario)
	{
		try
		{
			String scenarioStr = theScenario;
			File theFile = new File(scenarioStr);
			// final SampleDataPlugin thePlugin = SampleDataPlugin.getDefault();
			InputStream theStream = new FileInputStream(theFile);//
			// thePlugin.getResource(thePath);
			ASSETReaderWriter.importThis(_myScenario, scenarioStr, theStream);
		} catch (IOException e)
		{
			e.printStackTrace();
			ASSETPlugin.logError(Status.ERROR, "Failed to load sample data", e);
		} catch (NullPointerException e)
		{
			e.printStackTrace();
			ASSETPlugin.logError(Status.ERROR, "The sample-data plugin isn't loaded",
					e);
		}

		// next, go for the observers
	}

	public void loadController(String controlFile)
	{
			try
			{
				Vector<ScenarioObserver> theObservers = ASSETReaderWriter.importThisObserverList(controlFile, new java.io.FileInputStream(controlFile));

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
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

}