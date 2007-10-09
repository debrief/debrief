package org.mwc.asset.scenariocontroller.views;

import java.beans.*;
import java.text.*;
import java.util.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.scenariocontroller.ScenarioControllerPlugin;
import org.mwc.asset.scenariocontroller.preferences.TimeControllerProperties;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.narrative.BaseNarrativeProvider;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;

public class ScenarioController extends ViewPart implements ISelectionProvider
{

	/**
	 * hey, it's our scenario
	 */
	private ScenarioType _theScenario;

	private Label _timeLabel;

	private Composite _holder;

	private Action _editProperties;

	protected StructuredSelection _propsAsSelection;

	private TimeControllerProperties _myStepperProperties;

	private PropertyChangeListener _myPropertyListener;

	private BaseNarrativeProvider _myNarrativeProvider;

	private PartMonitor _myPartMonitor;

	private ScenarioSteppedListener _mySteppedListener;

	/**
	 * The constructor.
	 */
	public ScenarioController()
	{
		// ok, initialise the time step
		_myStepperProperties = new TimeControllerProperties();

		_myPropertyListener = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				_theScenario.setScenarioStepTime(_myStepperProperties.getSmallStep());
				_theScenario.setStepTime(_myStepperProperties.getAutoInterval());
			}
		};
		// listen for property changes
		_myStepperProperties.addPropertyChangeListener(_myPropertyListener);

		// and the narrative.
		_myNarrativeProvider = new BaseNarrativeProvider();


	}

	public void dispose()
	{
		super.dispose();

		// do some tidying
		_myStepperProperties.removePropertyChangeListener(_myPropertyListener);
	}

	public void scenarioStepped(long newTime)
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

	private void listenToMyParts()
	{
		// try to add ourselves to listen out for page changes
		// getSite().getWorkbenchWindow().getPartService().addPartListener(this);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
		_myPartMonitor.addPartListener(ScenarioType.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						if (_theScenario != null)
						{
							stopListeningToCurrentScenario();
						}

						startListeningTo((ScenarioType) part);
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						if(part == _theScenario)
							stopListeningToCurrentScenario();
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
	}

	/** ok, the user has switched scenario, start listening to it
	 * 
	 * @param type
	 */
	protected void startListeningTo(ScenarioType type)
	{
		_theScenario = type;

		if (_mySteppedListener == null)
			_mySteppedListener = new ScenarioSteppedListener()
			{
				public void restart()
				{
					scenarioStepped(_theScenario.getTime());
				}

				public void step(long newTime)
				{
					scenarioStepped(newTime);
				}
			};
		type.addScenarioSteppedListener(_mySteppedListener);
		
		scenarioStepped(_theScenario.getTime());
		
		// aah, and is it currently running
		if(_theScenario.isRunning())
		{
			// ok, make the GUI reflect this
			showPlaying(_playBtn);
		}
		else
		{
			// well in that case, make the GUI reflect this
			showPaused(_playBtn);
		}
		
		// fire it once to get us started
		_myPropertyListener.propertyChange(new PropertyChangeEvent(this, "test", this, this));

		
	}

	protected void stopListeningToCurrentScenario()
	{
		if(_mySteppedListener != null)
			_theScenario.removeScenarioSteppedListener(_mySteppedListener);
		
		_theScenario = null;
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

		// show a list for what's happening

		// and the buttons
		makeActions();

		// and store them
		contributeToActionBars();

		listenToMyParts();
		
		// declare fact that we can provide selections
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

	private void makeActions()
	{
		final IWorkbenchPart myPart = this;
		final ISelectionProvider provider = this;
		_editProperties = new Action("Properties")
		{

			public void run()
			{
				super.run();
				// get the editable thingy
				if (_propsAsSelection == null)
					_propsAsSelection = new StructuredSelection(_myStepperProperties);

				CorePlugin.editThisInProperties(_selectionListeners, _propsAsSelection, provider, myPart);
				_propsAsSelection = null;
			}

		};
		_editProperties.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/properties.gif"));
	}

	private Vector _selectionListeners;

	/** the play button, which we update when switching to a new sceanrio
	 * 
	 */
	private Button _playBtn;

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
				if(_theScenario != null)
					_theScenario.step();
			}
		});
		stepBtn.setImage(ScenarioControllerPlugin.getImage("icons/media_forward.png"));
		stepBtn.setToolTipText("Step the scenario forward");

		_playBtn = new Button(_btnPanel, SWT.TOGGLE);
		_playBtn.addSelectionListener(new SelectionListener()
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
							boolean paused = _playBtn.getSelection();
							if (paused)
							{
								showPlaying(_playBtn);
							}
							else
							{
								showPaused(_playBtn);
							}
						}
					}
				});
			}
		});
		_playBtn.setImage(ScenarioControllerPlugin.getImage("icons/media_play.png"));
		_playBtn.setToolTipText("Play the scenario");

		// eBwd.setImage(TimeControllerPlugin.getImage("icons/control_start_blue.png"));

	}
//
//	protected void doStep()
//	{
//		_theScenario.step();
////		fireMessage("test", new HiResDate(), "some test message:"
////				+ (int) (Math.random() * 100));
//	}
//	
//
//	public IEditorInput getEditorInput()
//	{
//		final IEditorInput input = new IEditorInput()
//		{
//			public boolean exists()
//			{
//				return true;
//			}
//
//			public ImageDescriptor getImageDescriptor()
//			{
//				return null;
//			}
//
//			public String getName()
//			{
//				return "ASSET Scenario";
//			}
//
//			public IPersistableElement getPersistable()
//			{
//				return null;
//			}
//
//			public String getToolTipText()
//			{
//				return "ASSET Scenario - extended description";
//			}
//
//			public Object getAdapter(Class adapter)
//			{
//				Object res = null;
//				if (adapter == ScenarioType.class)
//				{
//					res = _theScenario;
//				}
//				return res;
//			}
//		};
//
//		return input;
//	}

	protected void openASSETEditor()
	{
		// try
		// {
		//			
		// // IWorkbench wb = PlatformUI.getWorkbench();
		// // IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		// // IWorkbenchPage page = win.getActivePage();
		// //
		// //
		// // page.openEditor(input, "org.mwc.asset.ASSETPlotEditor");
		//
		// Display.getCurrent().asyncExec(new Runnable()
		// {
		// public void run()
		// {
		// IWorkbenchPage page =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		// .getActivePage();
		// try
		// {
		// System.out.println("opening editor...");
		// IDE.openEditor(page, input, "org.mwc.asset.ASSETPlotEditor");
		// }
		// catch (PartInitException e)
		// {
		// System.out.println("Init exception:");
		// e.printStackTrace();
		// }
		// }
		// });
		//
		// }
		// catch (Exception e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	// final String MY_SCENARIO =
	// "d:/dev/eclipse2/org.mwc.asset.sample_data/data/force_prot_scenario_area.xml";

	// protected void getTheSampleScenarioLoaded()
	// {
	// // hey, have a go at loading a scenario
	// // final String MY_SCENARIO =
	// // "d:/dev/eclipse2/org.mwc.asset.sample_data/data/herd_scenario_1.xml";//
	// // "c:\\temp\\andy_tactic\\ssn_run1.xml";
	// // final String MY_CONTROL =
	// // "d:/dev/eclipse2/org.mwc.asset.sample_data/data/herd_control_1.xml";
	// // //"c:\\temp\\andy_tactic\\ssn_observers.xml";
	//
	// try
	// {
	// File theFile = new File(MY_SCENARIO);
	// // final SampleDataPlugin thePlugin = SampleDataPlugin.getDefault();
	// InputStream theStream = new FileInputStream(theFile);//
	// thePlugin.getResource(thePath);
	// loadThisScenario(theStream, MY_SCENARIO);
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// CorePlugin.logError(Status.ERROR, "Failed to load sample data", e);
	// }
	// catch (NullPointerException e)
	// {
	// e.printStackTrace();
	// CorePlugin.logError(Status.ERROR, "The sample-data plugin isn't loaded",
	// e);
	// }
	//
	// // next, go for the observers
	//
	// }

	// public void observerDropped(final Vector files)
	// {
	// final Iterator ii = files.iterator();
	// while (ii.hasNext())
	// {
	// final File file = (File) ii.next();
	// // read in this file
	//
	// try
	// {
	// Vector theObservers =
	// ASSETReaderWriter.importThisObserverList(file.getName(),
	// new java.io.FileInputStream(file));
	//
	// // check we have a layer for the observers
	// Layer observers = checkObserverHolder();
	//
	// // add these observers to our scenario
	// for (int i = 0; i < theObservers.size(); i++)
	// {
	// // get the next observer
	// ScenarioObserver observer = (ScenarioObserver) theObservers.elementAt(i);
	//
	// // setup the observer
	// observer.setup(_theScenario);
	//
	// // and add it to our list
	// observers.add(observer);
	// }
	//
	// _myObservers.addAll(theObservers);
	// }
	// catch (java.io.FileNotFoundException fe)
	// {
	// MWC.Utilities.Errors.Trace.trace(fe, "Reading in dragged participant
	// file");
	// }
	// }
	// }

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{

	}

	// /**
	// * ok, we've received some files. process them.
	// */
	//
	// protected void filesDropped(String[] fileNames)
	// {
	//
	// // ok, iterate through the files
	// for (int i = 0; i < fileNames.length; i++)
	// {
	// final String thisFilename = fileNames[i];
	// System.out.println("should be loading:" + thisFilename);
	// loadThisFile(thisFilename);
	//
	// }
	//
	// // // ok, we're probably done - fire the update
	// // this._myLayers.fireExtended();
	// //
	// // // and resize to make sure we're showing all the data
	// // this._myChart.rescale();
	// //
	// // // hmm, we may have loaded more track data - but we don't track
	// // // loading of individual tracks - just fire a "modified" flag
	// // _trackDataProvider.fireTracksChanged();
	//
	// }

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

	// /**
	// * @param input
	// * the file to insert
	// */
	// private void loadThisFile(String filePath)
	// {
	// try
	// {
	// FileInputStream ifs = new FileInputStream(filePath);
	// loadThisScenario(ifs, filePath);
	// }
	// catch (FileNotFoundException e)
	// {
	// e.printStackTrace();
	// }
	// }
//
//	public void clearTheScenario()
//	{
//		_theScenario.close();
//	}

	// public void loadThisScenario(InputStream is, String fileName)
	// {
	// ASSETReaderWriter.importThis(_theScenario, fileName, is);
	//
	// // ok, tell everybody we've got some new participants
	// fireMessage(SCENARIO_CONFIG, new HiResDate(), "Scenario loaded from:" +
	// fileName);
	//
	// // right, does it have a backdrop?
	// if (_theScenario.getBackdrop() != null)
	// {
	// _theLayers.removeThisLayer(_theLayers.findLayer(Layers.CHART_FEATURES));
	// _theLayers.addThisLayer(_theScenario.getBackdrop());
	// }
	//
	// // fire the layers change for new scenario data
	// _theLayers.fireExtended(null, _myScenarioLayer);
	// }

	// /**
	// * sort out the file-drop target
	// */
	// private void configureFileDropSupport(Button _pusher)
	// {
	// int dropOperation = DND.DROP_COPY;
	// Transfer[] dropTypes = { FileTransfer.getInstance() };
	//
	// target = new DropTarget(_pusher, dropOperation);
	// target.setTransfer(dropTypes);
	// target.addDropListener(new DropTargetListener()
	// {
	// public void dragEnter(DropTargetEvent event)
	// {
	// if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
	// {
	// if (event.detail != DND.DROP_COPY)
	// {
	// event.detail = DND.DROP_COPY;
	// }
	// }
	// }
	//
	// public void dragLeave(DropTargetEvent event)
	// {
	// }
	//
	// public void dragOperationChanged(DropTargetEvent event)
	// {
	// }
	//
	// public void dragOver(DropTargetEvent event)
	// {
	// }
	//
	// public void dropAccept(DropTargetEvent event)
	// {
	// }
	//
	// public void drop(DropTargetEvent event)
	// {
	// String[] fileNames = null;
	// if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
	// {
	// fileNames = (String[]) event.data;
	// }
	// if (fileNames != null)
	// {
	// filesDropped(fileNames);
	// }
	// }
	//
	// });
	//
	// }

	public Object getAdapter(Class adapter)
	{
		Object res = null;

		// did we find anything?
		if (res == null)
			res = super.getAdapter(adapter);

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
	
	/**
	 * @param playBtn
	 * @return
	 */
	private void showPaused(final Button playBtn)
	{
		ImageDescriptor thisD;
		_theScenario.pause();
		thisD = ScenarioControllerPlugin
				.getImageDescriptor("icons/media_play.png");
		playBtn.setToolTipText("auto-step");
		playBtn.setImage(thisD.createImage());
		playBtn.setSelection(false);
	}

	/**
	 * @param playBtn
	 * @return
	 */
	private void showPlaying(final Button playBtn)
	{
		ImageDescriptor thisD;
		thisD = ScenarioControllerPlugin
				.getImageDescriptor("icons/media_pause.png");
		playBtn.setToolTipText("Pause play");
		playBtn.setImage(thisD.createImage());
		playBtn.setSelection(true);
		_theScenario.start();
	}	
}