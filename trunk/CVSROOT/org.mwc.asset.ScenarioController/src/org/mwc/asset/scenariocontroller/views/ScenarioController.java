package org.mwc.asset.scenariocontroller.views;

import java.io.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.narrative.BaseNarrativeProvider;

import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Scenario.*;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.*;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public class ScenarioController extends ViewPart
{
	/**
	 * the type of message we send when loading/managing the scenario
	 */
	public static final String SCENARIO_CONFIG = "SCENARIO_CONFIG";

	/**
	 * hey, it's our scenario
	 */
	private CoreScenario _theScenario;

	private Button _pusher;

	/**
	 * remember that we can load files
	 */
	protected DropTarget target;

	/**
	 * and that we have some layers...
	 */
	private Layers _theLayers;

	/**
	 * the set of observers which monitor this scenario
	 */
	private Vector _myObservers = new Vector(0, 1);

	/**
	 * narrative utility support
	 */
	private BaseNarrativeProvider _myNarrativeProvider;

	/**
	 * represent the scenario as a layer
	 */
	private ScenarioLayer _myScenarioLayer;

	/**
	 * The constructor.
	 */
	public ScenarioController()
	{
		_theScenario = new CoreScenario();

		_myNarrativeProvider = new BaseNarrativeProvider();
		_theLayers = new Layers();

		// add the chart plotter
		_myScenarioLayer = new ASSET.GUI.Workbench.Plotters.ScenarioLayer();
		_myScenarioLayer.setScenario(_theScenario);
		_theLayers.addThisLayer(_myScenarioLayer);

		// listen for scenario changes
		_theScenario.addScenarioSteppedListener(new ScenarioSteppedListener()
		{
			public void restart()
			{
			}

			public void step(long newTime)
			{
				fireMessage("Stepped", new HiResDate(newTime), "Stepped");
			}
		});

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_pusher = new Button(parent, SWT.NONE);
		_pusher.setText("Tester");
		_pusher.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				btnOnePressed();
			}
		});
		Button pusher2 = new Button(parent, SWT.NONE);
		pusher2.setText("Tester 2");
		pusher2.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				doStep();
			}
		});
		Button pusher3 = new Button(parent, SWT.NONE);
		pusher3.setText("Open asset plot");
		pusher3.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				openEditor();
			}
		});
		// show a list for what's happening

		// and get ready for drop
		configureFileDropSupport();
	}

	protected void doStep()
	{
		_theScenario.step();
		fireMessage("test", new HiResDate(), "some test message:"
				+ (int) (Math.random() * 100));
	}

	protected void openEditor()
	{
		try
		{
			final IEditorInput input = new IEditorInput()
			{
				public boolean exists()
				{
					return true;
				}

				public ImageDescriptor getImageDescriptor()
				{
					return null;
				}

				public String getName()
				{
					return "ASSET Scenario";
				}

				public IPersistableElement getPersistable()
				{
					return null;
				}

				public String getToolTipText()
				{
					return "ASSET Scenario - extended description";
				}

				public Object getAdapter(Class adapter)
				{
					return null;
				}
			};
//			IWorkbench wb = PlatformUI.getWorkbench();
//			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//			IWorkbenchPage page = win.getActivePage();			
//			
//			
//			page.openEditor(input, "org.mwc.asset.ASSETPlotEditor");
			
			Display.getCurrent().asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					try
					{
						IDE.openEditor(page, input, "org.mwc.debrief.core.editors.PlotEditor");
					}
					catch (PartInitException e)
					{
					}
				}
			});			
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * fire off the message to any listeners
	 * 
	 * @param type
	 *          the type of message we're sending out
	 * @param dtg
	 *          the DTG of the message
	 * @param message
	 */
	protected void fireMessage(String type, HiResDate dtg, String message)
	{
		NarrativeEntry newEntry = new NarrativeEntry("Controller", type, dtg, message);
		_myNarrativeProvider.fireEntry(newEntry);
	}

	protected void btnOnePressed()
	{
		// hey, have a go at loading a scenario
		final String MY_SCENARIO = "c:\\temp\\andy_tactic\\ssn_run1.xml";
		// final String MY_OBSERVERS = "c:\\temp\\andy_tactic\\ssn_observers.xml";

		try
		{
			loadThisScenario(new FileInputStream(MY_SCENARIO), MY_SCENARIO);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		// next, go for the observers

	}

	public void observerDropped(final Vector files)
	{
		final Iterator ii = files.iterator();
		while (ii.hasNext())
		{
			final File file = (File) ii.next();
			// read in this file

			try
			{
				Vector theObservers = ASSETReaderWriter.importThisObserverList(file.getName(),
						new java.io.FileInputStream(file));

				// check we have a layer for the observers
				Layer observers = checkObserverHolder();

				// add these observers to our scenario
				for (int i = 0; i < theObservers.size(); i++)
				{
					// get the next observer
					ScenarioObserver observer = (ScenarioObserver) theObservers.elementAt(i);

					// setup the observer
					observer.setup(_theScenario);

					// and add it to our list
					observers.add(observer);
				}

				_myObservers.addAll(theObservers);
			}
			catch (java.io.FileNotFoundException fe)
			{
				MWC.Utilities.Errors.Trace.trace(fe, "Reading in dragged participant file");
			}
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{

	}

	/**
	 * ok, we've received some files. process them.
	 */

	protected void filesDropped(String[] fileNames)
	{

		// ok, iterate through the files
		for (int i = 0; i < fileNames.length; i++)
		{
			final String thisFilename = fileNames[i];
			System.out.println("should be loading:" + thisFilename);
			loadThisFile(thisFilename);

		}

		// // ok, we're probably done - fire the update
		// this._myLayers.fireExtended();
		//		
		// // and resize to make sure we're showing all the data
		// this._myChart.rescale();
		//		
		// // hmm, we may have loaded more track data - but we don't track
		// // loading of individual tracks - just fire a "modified" flag
		// _trackDataProvider.fireTracksChanged();

	}

	// /**
	// * @param input
	// * the file to insert
	// */
	// private void loadThisFile(IEditorInput input)
	// {
	// try
	// {
	// IFileEditorInput ife = (IFileEditorInput) input;
	// InputStream is = ife.getFile().getContents();
	// loadThisScenario(is, input.getName());
	// }
	// catch (CoreException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	/**
	 * @param input
	 *          the file to insert
	 */
	private void loadThisFile(String filePath)
	{
		try
		{
			FileInputStream ifs = new FileInputStream(filePath);
			loadThisScenario(ifs, filePath);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private void loadThisScenario(InputStream is, String fileName)
	{
		ASSETReaderWriter.importThis(_theScenario, fileName, is);

		// ok, tell everybody we've got some new participants
		fireMessage(SCENARIO_CONFIG, new HiResDate(), "Scenario loaded from:" + fileName);

		// fire the layers change for new scenario data
		_theLayers.fireExtended(null, _myScenarioLayer);
	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport()
	{
		int dropOperation = DND.DROP_COPY;
		Transfer[] dropTypes = { FileTransfer.getInstance() };

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

	public Object getAdapter(Class adapter)
	{
		Object res = null;

		// provide the scenario
		if (adapter == ScenarioType.class)
		{
			res = _theScenario;
		}
		else if (adapter == MWC.TacticalData.IRollingNarrativeProvider.class)
		{
			res = _myNarrativeProvider;
		}
		else if (adapter == Layers.class)
		{
			res = _theLayers;
		}

		// did we find anything?
		if (res == null)
			res = super.getAdapter(adapter);

		return res;
	}

	/**
	 * check that we have a layer for our observers
	 * 
	 * @return the layer (or a fresh one)
	 */
	private Layer checkObserverHolder()
	{
		Layer res = null;

		// do we have a layer for observers?
		res = _theLayers.findLayer("Observers");

		if (res == null)
		{
			res = new BaseLayer();
			res.setName("Observers");
			_theLayers.addThisLayer(res);
		}

		return res;

	}
}