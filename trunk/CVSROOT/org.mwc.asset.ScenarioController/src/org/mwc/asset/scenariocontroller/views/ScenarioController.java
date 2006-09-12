package org.mwc.asset.scenariocontroller.views;

import java.beans.*;
import java.io.*;
import java.text.*;
import java.util.*;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.scenariocontroller.ScenarioControllerPlugin;
import org.mwc.asset.scenariocontroller.preferences.TimeControllerProperties;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.narrative.BaseNarrativeProvider;

import ASSET.ScenarioType;
import ASSET.GUI.Workbench.Plotters.ScenarioLayer;
import ASSET.Scenario.*;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.*;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GenericData.*;
import MWC.TacticalData.NarrativeEntry;

public class ScenarioController extends ViewPart implements ISelectionProvider
{
	/**
	 * the type of message we send when loading/managing the scenario
	 */
	public static final String SCENARIO_CONFIG = "SCENARIO_CONFIG";

	/**
	 * hey, it's our scenario
	 */
	private CoreScenario _theScenario;

	/**
	 * remember that we can load files
	 */
	protected DropTarget target;

	/**
	 * and that we have some layers...
	 */
	final private Layers _theLayers;

	/**
	 * the set of observers which monitor this scenario
	 */
	private Vector _myObservers = new Vector(0, 1);

	/**
	 * narrative utility support
	 */
	private BaseNarrativeProvider _myNarrativeProvider;

	/**
	 * the people listening to us
	 */
	private Vector _selectionListeners;

	/**
	 * represent the scenario as a layer
	 */
	private ScenarioLayer _myScenarioLayer;

	private Label _timeLabel;

	private Composite _holder;

	private Action _editProperties;

	protected StructuredSelection _propsAsSelection;

	private TimeControllerProperties _myStepperProperties;

	private PropertyChangeListener _myPropertyListener;

	/**
	 * The constructor.
	 */
	public ScenarioController()
	{
		_selectionListeners = new Vector(0, 1);

		_theScenario = new CoreScenario();

		_myNarrativeProvider = new BaseNarrativeProvider();
		_theLayers = new Layers();

		// add the chart plotter
		_myScenarioLayer = new ASSET.GUI.Workbench.Plotters.ScenarioLayer();
		_myScenarioLayer.setScenario(_theScenario);
		_theLayers.addThisLayer(_myScenarioLayer);

		BaseLayer decs = new BaseLayer();
		decs.setName(Layers.CHART_FEATURES);
		GridPainter grid = new GridPainter();
		grid.setDelta(new WorldDistanceWithUnits(5, WorldDistanceWithUnits.NM));
		decs.add(grid);

		_theLayers.addThisLayer(decs);

		// listen for scenario changes
		listenForScenarioChanges(_theScenario, _myScenarioLayer, _theLayers);

		// ok, initialise the time step
		_myStepperProperties = new TimeControllerProperties();

		_myPropertyListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				System.out.println("stepper changed!");
				_theScenario.setScenarioStepTime(_myStepperProperties.getSmallStep());
				_theScenario.setStepTime(_myStepperProperties.getAutoInterval());
			}
		};
		// listen for property changes
		_myStepperProperties.addPropertyChangeListener(_myPropertyListener);

		// fire it once to get us started
		_myPropertyListener.propertyChange(new PropertyChangeEvent(this, "test", this, this));

	}

	public void dispose()
	{
		super.dispose();

		// do some tidying
		_myStepperProperties.removePropertyChangeListener(_myPropertyListener);
	}

	private void listenForScenarioChanges(final CoreScenario scenario,
			final ScenarioLayer scenarioLayer, final Layers layers)
	{
		_theScenario.addScenarioSteppedListener(new ScenarioSteppedListener()
		{

			public void restart()
			{
			}

			public void step(long newTime)
			{
				HiResDate dtg = new HiResDate(newTime);
				fireMessage("Stepped", dtg, "Stepped");
				final String newVal = toStringHiRes(dtg, "yy/MM/dd HH:mm");

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (!_holder.isDisposed())
						{
							_timeLabel.setText(newVal);
						}
					}
				});
			}
		});
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		_holder = new Composite(parent, SWT.NONE);
		GridLayout grid = new GridLayout(1, false);
		_holder.setLayout(grid);

		// put the time at the top
		createTimeLabel();

		// put in the control buttons
		createControlButtons(_holder);

		// and now the useful buttons
		Composite bottomRow = new Composite(_holder, SWT.NONE);
		bottomRow.setLayout(new RowLayout());

		Button _pusher = createOurOtherButtons(bottomRow);
		// show a list for what's happening

		// and get ready for drop
		configureFileDropSupport(_pusher);

		// and the buttons
		makeActions();

		// and store them
		contributeToActionBars();

		// say that we're a selection provider
		getSite().setSelectionProvider(this);
	}

	/**
	 * 
	 */
	private void createTimeLabel()
	{
		_timeLabel = new Label(_holder, SWT.NONE);
		GridData labelGrid = new GridData(GridData.FILL_HORIZONTAL);
		_timeLabel.setLayoutData(labelGrid);
		_timeLabel.setAlignment(SWT.CENTER);
		_timeLabel.setText("--------------------------");
		// _timeLabel.setFont(new Font(Display.getDefault(), "OCR A Extended", 16,
		// SWT.NONE));
		_timeLabel.setFont(new Font(Display.getDefault(), "Arial", 16, SWT.NONE));
		_timeLabel.setForeground(new Color(Display.getDefault(), 33, 255, 22));
		_timeLabel.setBackground(new Color(Display.getDefault(), 0, 0, 0));
	}

	/**
	 * @param bottomRow
	 * @return
	 */
	private Button createOurOtherButtons(Composite bottomRow)
	{
		Button _pusher = new Button(bottomRow, SWT.NONE);
		_pusher.setText("Load scenario");
		_pusher.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				getTheSampleScenarioLoaded();
			}
		});
		Button pusher2 = new Button(bottomRow, SWT.NONE);
		pusher2.setText("Step");
		pusher2.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				doStep();
			}
		});
		final Button pusher3 = new Button(bottomRow, SWT.NONE);
		pusher3.setText("Open asset plot");
		pusher3.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				openASSETEditor();
			}
		});
		return _pusher;
	}

	private void makeActions()
	{
		final ISelectionProvider provider = this;
		_editProperties = new Action("Properties")
		{

			public void run()
			{
				super.run();
				// get the editable thingy
				if (_propsAsSelection == null)
					_propsAsSelection = new StructuredSelection(_myStepperProperties);

				CorePlugin.editThisInProperties(_selectionListeners, _propsAsSelection, provider);
				_propsAsSelection = null;
			}

		};
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager)
	{
		toolBarManager.add(_editProperties);
	}

	private void fillLocalPullDown(IMenuManager menuManager)
	{
		menuManager.add(_editProperties);
	}

	protected void createControlButtons(Composite parent)
	{
		// first create the button holder
		Composite _btnPanel = new Composite(parent, SWT.NONE);
		GridData btnGrid = new GridData(GridData.FILL_HORIZONTAL);
		_btnPanel.setLayoutData(btnGrid);
		FillLayout btnFiller = new FillLayout(SWT.HORIZONTAL);
		btnFiller.marginHeight = 0;
		_btnPanel.setLayout(btnFiller);

		Button restartBtn = new Button(_btnPanel, SWT.NONE);
		restartBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				_theScenario.restart();
			}
		});
		restartBtn.setImage(ScenarioControllerPlugin.getImage("icons/media_beginning.png"));
		restartBtn.setToolTipText("Restart the scenario");

		Button stepBtn = new Button(_btnPanel, SWT.NONE);
		stepBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				_theScenario.step();
			}
		});
		stepBtn.setImage(ScenarioControllerPlugin.getImage("icons/media_forward.png"));
		stepBtn.setToolTipText("Step the scenario forward");

		final Button playBtn = new Button(_btnPanel, SWT.TOGGLE);
		playBtn.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (!_holder.isDisposed())
						{
							boolean paused = playBtn.getSelection();
							ImageDescriptor thisD;
							if (paused)
							{
								thisD = ScenarioControllerPlugin
										.getImageDescriptor("icons/media_pause.png");
								playBtn.setToolTipText("Pause play");
								_theScenario.start();
							}
							else
							{
								thisD = ScenarioControllerPlugin
										.getImageDescriptor("icons/media_play.png");
								playBtn.setToolTipText("auto-step");
								_theScenario.pause();
							}
							playBtn.setImage(thisD.createImage());
						}
					}
				});
			}
		});
		playBtn.setImage(ScenarioControllerPlugin.getImage("icons/media_play.png"));
		playBtn.setToolTipText("Play the scenario");

		// eBwd.setImage(TimeControllerPlugin.getImage("icons/control_start_blue.png"));

	}

	protected void stepScenario()
	{
	}

	protected void restartScenario()
	{
	}

	protected void doStep()
	{
		_theScenario.step();
		fireMessage("test", new HiResDate(), "some test message:"
				+ (int) (Math.random() * 100));
	}

	protected void openASSETEditor()
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
					Object res = null;
					if (adapter == Layers.class)
					{
						res = _theLayers;
					}
					else if (adapter == ScenarioType.class)
					{
						res = _theScenario;
					}
					else if (adapter == ScenarioLayer.class)
					{
						res = _myScenarioLayer;
					}
					return res;
				}
			};
			// IWorkbench wb = PlatformUI.getWorkbench();
			// IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			// IWorkbenchPage page = win.getActivePage();
			//			
			//			
			// page.openEditor(input, "org.mwc.asset.ASSETPlotEditor");

			Display.getCurrent().asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
					try
					{
						System.out.println("opening editor...");
						IDE.openEditor(page, input, "org.mwc.asset.ASSETPlotEditor");
					}
					catch (PartInitException e)
					{
						System.out.println("Init exception:");
						e.printStackTrace();
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

	protected void getTheSampleScenarioLoaded()
	{
		// hey, have a go at loading a scenario
//		final String MY_SCENARIO = "d:/dev/eclipse2/org.mwc.asset.sample_data/data/herd_scenario_1.xml";// "c:\\temp\\andy_tactic\\ssn_run1.xml";
		final String MY_SCENARIO = "d:/dev/eclipse2/org.mwc.asset.sample_data/data/force_prot_scenario_area.xml";
	//	final String MY_CONTROL = "d:/dev/eclipse2/org.mwc.asset.sample_data/data/herd_control_1.xml"; //"c:\\temp\\andy_tactic\\ssn_observers.xml";

		 
		try
		{
			File theFile = new File(MY_SCENARIO);
			// final SampleDataPlugin thePlugin = SampleDataPlugin.getDefault();
			InputStream theStream = new FileInputStream(theFile);// thePlugin.getResource(thePath);
			loadThisScenario(theStream, MY_SCENARIO);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			CorePlugin.logError(Status.ERROR, "Failed to load sample data", e);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			CorePlugin.logError(Status.ERROR, "The sample-data plugin isn't loaded", e);
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

		// right, does it have a backdrop?
		if(_theScenario.getBackdrop() != null)
		{
			_theLayers.removeThisLayer(_theLayers.findLayer(Layers.CHART_FEATURES));
			_theLayers.addThisLayer(_theScenario.getBackdrop());
		}
		
		
		// fire the layers change for new scenario data
		_theLayers.fireExtended(null, _myScenarioLayer);
	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport(Button _pusher)
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

	public static String toStringHiRes(HiResDate time, String pattern)
			throws IllegalArgumentException
	{
		// so, have a look at the data
		long micros = time.getMicros();
		// long wholeSeconds = micros / 1000000;

		StringBuffer res = new StringBuffer();

		java.util.Date theTime = new java.util.Date(micros / 1000);

		SimpleDateFormat _myFormat = new SimpleDateFormat(pattern);
		_myFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		res.append(_myFormat.format(theTime));

		DecimalFormat microsFormat = new DecimalFormat("000000");
		DecimalFormat millisFormat = new DecimalFormat("000");

		// do we have micros?
		if (micros % 1000 > 0)
		{
			// yes
			res.append(".");
			res.append(microsFormat.format(micros % 1000000));
		}
		else
		{
			// do we have millis?
			if (micros % 1000000 > 0)
			{
				// yes, convert the value to millis

				long millis = micros = (micros % 1000000) / 1000;

				res.append(".");
				res.append(millisFormat.format(millis));
			}
			else
			{
				// just use the normal output
			}
		}

		return res.toString();
	}

	public ISelection getSelection()
	{
		// TODO Auto-generated method stub
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