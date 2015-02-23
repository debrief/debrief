/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
/**
 * 
 */
package org.mwc.debrief.core.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllablePeriod;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.ui_support.wizards.SimplePageListWizard;
import org.mwc.cmap.core.wizards.EnterBooleanPage;
import org.mwc.cmap.core.wizards.EnterRangePage;
import org.mwc.cmap.core.wizards.EnterStringPage;
import org.mwc.cmap.core.wizards.SelectColorPage;
import org.mwc.cmap.gt2plot.proj.GtProjection;
import org.mwc.cmap.plotViewer.actions.Pan;
import org.mwc.cmap.plotViewer.actions.Pan.PanMode;
import org.mwc.cmap.plotViewer.actions.RangeBearing;
import org.mwc.cmap.plotViewer.actions.RangeBearing.RangeBearingMode;
import org.mwc.cmap.plotViewer.actions.ZoomIn;
import org.mwc.cmap.plotViewer.actions.ZoomIn.ZoomInMode;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.ContextOperations.RainbowShadeSonarCuts.ShadeCutsOperation;
import org.mwc.debrief.core.ContextOperations.RainbowShadeSonarCuts.ShadeOperation;
import org.mwc.debrief.core.actions.DragComponent;
import org.mwc.debrief.core.actions.DragComponent.DragComponentMode;
import org.mwc.debrief.core.actions.DragFeature;
import org.mwc.debrief.core.actions.DragFeature.DragFeatureMode;
import org.mwc.debrief.core.actions.DragSegment;
import org.mwc.debrief.core.actions.DragSegment.DragSegmentMode;
import org.mwc.debrief.core.actions.RadioHandler;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.mwc.debrief.core.interfaces.IPlotLoader.BaseLoader;
import org.mwc.debrief.core.interfaces.IPlotLoader.DeferredPlotLoader;
import org.mwc.debrief.core.loaders.LoaderManager;
import org.mwc.debrief.core.loaders.ReplayLoader;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;
import org.mwc.debrief.core.operations.ExportDopplerShift;
import org.mwc.debrief.core.operations.ExportTimeDataToClipboard;
import org.mwc.debrief.core.operations.ExportToFlatFile;
import org.mwc.debrief.core.operations.ExportToFlatFile2;
import org.mwc.debrief.core.operations.PlotOperations;
import org.osgi.framework.Bundle;

import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.PlainProjection.RelativeProjectionParent;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.IRollingNarrativeProvider;

/**
 * @author ian.mayo
 */
@SuppressWarnings("deprecation")
public class PlotEditor extends org.mwc.cmap.plotViewer.editors.CorePlotEditor
{
	// Extension point tag and attributes in plugin.xml
	private static final String EXTENSION_POINT_ID = "DebriefPlotLoader";

	private static final String EXTENSION_TAG = "loader";

	private static final String EXTENSION_TAG_LABEL_ATTRIB = "name";

	private static final String EXTENSION_TAG_EXTENSIONS_ATTRIB = "extensions";

	private static final String EXTENSION_TAG_ICON_ATTRIB = "icon";

	// private static final String EXTENSION_TAG_CLASS_ATTRIB = "class";

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.debrief.core";

	/**
	 * helper object which loads plugin file-loaders
	 */
	LoaderManager _loader;

	/**
	 * we keep the reference to our track-type adapter
	 */
	TrackDataProvider _trackDataProvider;

	/**
	 * something to look after our layer painters
	 */
	LayerPainterManager _layerPainterManager;

	/**
	 * and how we view the time
	 * 
	 */
	protected TimeControlPreferences _timePreferences;

	private PlotOperations _myOperations;

	/**
	 * support tool that provides a relative plot
	 */
	private RelativeProjectionParent _myRelativeWrapper;

	/**
	 * handle narrative management
	 */
	protected IRollingNarrativeProvider _theNarrativeProvider;

	/**
	 * an object to look after all of the time bits
	 */
	private TimeManager _timeManager;

	private org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore _timeControllerOperations;

	private PlotOutlinePage _outlinePage;

	private TraverseListener dragModeListener = new TraverseListener()
	{

		@Override
		public void keyTraversed(TraverseEvent e)
		{
			if (getChart() == null)
			{
				return;
			}
			Control control = getChart().getCanvasControl();
			if (control == null || control.isDisposed() || !control.isVisible()) {
				return;
			}
			if (e.detail == SWT.TRAVERSE_TAB_NEXT)
			{
				PlotMouseDragger dragMode = getChart().getDragMode();
				if (dragMode != null)
				{
					try
					{
						String currentState = null;
						ExecutionEvent executionEvent = new ExecutionEvent();
						if (dragMode instanceof DragSegmentMode)
						{
							new DragComponent().execute(executionEvent);
							currentState = RadioHandler.DRAG_COMPONENT;
						}
						else if (dragMode instanceof DragComponentMode)
						{
							new DragFeature().execute(executionEvent);
							currentState = RadioHandler.DRAG_FEATURE;
						}
						else if (dragMode instanceof DragFeatureMode)
						{
							new RangeBearing().execute(executionEvent);
							currentState = RadioHandler.RANGE_BEARING;
						}
						else if (dragMode instanceof RangeBearingMode)
						{
							new Pan().execute(executionEvent);
							currentState = RadioHandler.PAN;
						}
						else if (dragMode instanceof PanMode)
						{
							new ZoomIn().execute(executionEvent);
							currentState = RadioHandler.ZOOM_IN;
						}
						else if (dragMode instanceof ZoomInMode)
						{
							new DragSegment().execute(executionEvent);
							currentState = RadioHandler.DRAG_SEGMENT;
						}
						if (currentState != null)
						{
							ICommandService service = (ICommandService) getSite().getService(ICommandService.class);
							Command command = service.getCommand(RadioHandler.ID);
							HandlerUtil.updateRadioState(command, currentState);
						}
					}
					catch (Exception e1)
					{
						CorePlugin.logError(Status.WARNING, "Cannot change drag mode:", e1);
					}
				}
			}
		}
	};

	/**
	 * constructor - quite simple really.
	 */
	public PlotEditor()
	{
		super();

		// create the track manager to manage the primary & secondary tracks
		_trackDataProvider = new TrackManager(_myLayers);

		// and listen out form modifications, because we want to mark ourselves
		// as
		// dirty once they've updated
		_trackDataProvider.addTrackDataListener(new TrackDataListener()
		{
			public void tracksUpdated(final WatchableList primary,
					final WatchableList[] secondaries)
			{
				fireDirty();
			}
		});

		// sort out the time controlleroperations
		_timeControllerOperations = new org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore();
		_timeControllerOperations.add(new ExportTimeDataToClipboard());
		_timeControllerOperations.add(new ExportToFlatFile());
		_timeControllerOperations.add(new ExportToFlatFile2());
		_timeControllerOperations.add(new ExportDopplerShift());

		_layerPainterManager = new LayerPainterManager(_trackDataProvider);
		_layerPainterManager.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent arg0)
			{
				// ok, trigger repaint of plot
				if (getChart() != null)
					getChart().update();
			}
		});

		// create the time manager. cool
		_timeManager = new TimeManager();
		_timeManager.addListener(_timeListener,
				TimeProvider.TIME_CHANGED_PROPERTY_NAME);

		// and how time is managed
		_timePreferences = new TimeControlProperties();

		// listen out for when our input changes, since we will change the
		// editor
		// window title
		this.addPropertyListener(new IPropertyListener()
		{

			@SuppressWarnings("synthetic-access")
			public void propertyChanged(final Object source, final int propId)
			{
				if (propId == PROP_INPUT)
				{
					final Object input = getEditorInput();
					if (input instanceof IFileEditorInput)
					{
						final IFileEditorInput inp = (IFileEditorInput) getEditorInput();
						setPartName(inp.getName());
					}
					else
					{
						if (input instanceof FileStoreEditorInput)
						{
							final FileStoreEditorInput fsi = (FileStoreEditorInput) input;
							final String theName = fsi.getName();
							setPartName(theName);
						}
						else
						{
							CorePlugin.logError(Status.ERROR,
									"data source for PlotEditor not of expected type:" + input,
									null);
							System.err.println("Not expected file type:" + input);
						}
					}
				}
			}
		});

		_myOperations = new PlotOperations()
		{
			// just provide with our complete set of layers
			@SuppressWarnings("synthetic-access")
			public Object[] getTargets()
			{
				// ok, return our top level layers as objects
				final Vector<Layer> res = new Vector<Layer>(0, 1);
				for (int i = 0; i < _myLayers.size(); i++)
				{
					res.add(_myLayers.elementAt(i));
				}
				return res.toArray();
			}

			/**
			 * override performing the operation, since we'll do a screen update on
			 * completion
			 */
			@SuppressWarnings("synthetic-access")
			public Vector<Layer> performOperation(final AnOperation operationName)
			{
				// make the actual change
				final Vector<Layer> res = super.performOperation(operationName);

				if (res != null)
				{
					if (res.size() != 0)
					{
						for (final Iterator<Layer> iter = res.iterator(); iter.hasNext();)
						{
							final Layer thisL = (Layer) iter.next();
							// and update the screen
							_myLayers.fireReformatted(thisL);

						}
					}
				}

				return res;

			}
		};

		// do we have some time preferences?
		if (_timePreferences != null)
		{
			final HiResDate startDTG = _timePreferences.getSliderStartTime();
			final HiResDate endDTG = _timePreferences.getSliderEndTime();
			// and were there any times in it?
			if ((startDTG != null) && (endDTG != null))
			{
				// yup, store the time data.
				_myOperations
						.setPeriod(new TimePeriod.BaseTimePeriod(startDTG, endDTG));
			}
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();

		// stop listening to the time manager
		_timeManager.removeListener(_timeListener,
				TimeProvider.TIME_CHANGED_PROPERTY_NAME);

		_timeManager = null;

		if (_layerPainterManager != null)
		{
			_layerPainterManager.close();
			_layerPainterManager = null;
		}

		if (_outlinePage != null)
		{
			_outlinePage.dispose();
			_outlinePage = null;
		}
	}

	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException
	{
		setSite(site);
		setInputWithNotify(input);

		// ok - declare and load the supplemental plugins which can load
		// datafiles
		initialiseFileLoaders();

		// and start the load
		loadThisFile(input);

		// lastly, set the title (if we have one)
		this.setPartName(input.getName());
	}

	/**
	 * @param input
	 *          the file to insert
	 */
	private void loadThisFile(final IEditorInput input)
	{
		InputStream is = null;
		if (!input.exists())
		{
			CorePlugin.logError(Status.ERROR,
					"File cannot be found:" + input.getName(), null);
			return;
		}
		try
		{
			final IPersistableElement persist = input.getPersistable();
			if (input instanceof IFileEditorInput)
			{
				final IFileEditorInput ife = (IFileEditorInput) input;
				final IFile iff = ife.getFile();
				iff.refreshLocal(IResource.DEPTH_ONE, null);
				is = iff.getContents();
			}
			else if (persist instanceof IFileEditorInput)
			{
				final IFileEditorInput iff = (IFileEditorInput) persist;
				is = iff.getFile().getContents();
			}
			else if (input instanceof FileStoreEditorInput)
			{
				final FileStoreEditorInput _input = (FileStoreEditorInput) input;
				final URI _uri = _input.getURI();
				final Path _p = new Path(_uri.getPath());
				final IFileStore _ifs = EFS.getLocalFileSystem().getStore(_p);
				is = _ifs.openInputStream(EFS.NONE, null);
			}

			if (is != null)
				loadThisStream(is, input.getName());
			else
			{
				CorePlugin.logError(Status.INFO, "Failed to load file from:" + input,
						null);
			}

		}
		catch (final CoreException e)
		{
			CorePlugin.logError(Status.ERROR,
					"Resource out of sync:" + input.getName() + " REFRESH the workspace",
					null);
			MessageDialog
					.openError(
							Display.getDefault().getActiveShell(),
							"File out of sync",
							"This file has been edited or removed:"
									+ input.getName()
									+ "\nPlease right-click on your navigator project and press Refresh");
		}
	}

	/**
	 * @param input
	 *          the file to insert
	 */
	private void loadThisFile(final String filePath)
	{
		try
		{
			final FileInputStream ifs = new FileInputStream(filePath);
			loadThisStream(ifs, filePath);
		}
		catch (final FileNotFoundException e)
		{
			CorePlugin.logError(Status.ERROR,
					"Problem loading data file:" + filePath, e);
		}
	}

	private void loadThisStream(final InputStream is, final String fileName)
	{
		// right, see if any of them will do our edit
		final IPlotLoader[] loaders = _loader.findLoadersFor(fileName);
		// did we find any?
		if (loaders.length > 0)
		{
			// cool, give them a go...
			try
			{
				for (int i = 0; i < loaders.length; i++)
				{
					final IPlotLoader thisLoader = loaders[i];

					// get it to load. Just in case it's an asychronous load
					// operation, we
					// rely on it calling us back (loadingComplete)
					thisLoader.loadFile(this, is, fileName);

					// special handling - popup a dialog to allow sensor name/color to be
					// set if there's just one sensor
					if (thisLoader instanceof DeferredPlotLoader)
					{
						final DeferredPlotLoader ld = (DeferredPlotLoader) thisLoader;
						final BaseLoader loader = ld.getLoader();
						if (loader != null)
						{
							if (loader instanceof ReplayLoader)
							{
								final ReplayLoader rl = (ReplayLoader) loader;
								final ImportReplay ir = rl.getReplayLoader();
								final Vector<SensorWrapper> sensors = ir
										.getLoadedSensors();
								if (sensors.size() == 1)
								{
									final SensorWrapper thisS = sensors.firstElement();
									nameThisSensor(thisS);
								}
							}
						}
					}
				}
			}
			catch (final RuntimeException e)
			{
				CorePlugin.logError(Status.ERROR, "Problem loading data file:"
						+ fileName, e);
			}
		}
	}

	private void nameThisSensor(final SensorWrapper thisS)
	{
		// create the wizard to color/name this
		final SimplePageListWizard wizard = new SimplePageListWizard();

		// right, just have a quick look and see if the sensor has range data -
		// because
		// if it doesn't we'll let the user set a default
		final Enumeration<Editable> cuts = thisS.elements();
		boolean needsRange = false;
		if (cuts.hasMoreElements())
		{
			final Editable firstCut = cuts.nextElement();
			final SensorContactWrapper scw = (SensorContactWrapper) firstCut;
			// do we have bearing?
			if (scw.getHasBearing())
			{
				// yes. now are we waiting for a range?
				if (scw.getRange() == null)
				{
					needsRange = true;
				}
			}
		}
		final String imagePath = "images/NameSensor.jpg";

		final EnterStringPage getName = new EnterStringPage(null, thisS.getName(),
				"Import Sensor data", "Please provide the name for this sensor",
				"a one-word title for this block of sensor contacts (e.g. S2046)",
				imagePath, null, false);
		final SelectColorPage getColor = new SelectColorPage(null,
				thisS.getColor(), "Import Sensor data", "Now format the new sensor",
				"The default color for the cuts for this new sensor", imagePath, null);
		final EnterBooleanPage getVis = new EnterBooleanPage(null, false,
				"Import Sensor data",
				"Please specify if this sensor should be displayed once loaded",
				"yes/no", imagePath, null);
		final WorldDistance defRange = new WorldDistance(5000, WorldDistance.YARDS);
		final EnterRangePage getRange = new EnterRangePage(
				null,
				"Import Sensor data",
				"Please provide a default range for the sensor cuts \n(or enter 0.0 to leave them as infinite length)",
				"Default range", defRange, imagePath, null);
		final EnterBooleanPage applyRainbowInRainbowColors = new EnterBooleanPage(null, false,
				"Apply Rainbow Shades in rainbow colors",
				"Should Debrief apply Rainbow Shades to these sensor cuts?",
				"yes/no", "images/ShadeRainbow.png", null);

		wizard.addWizard(getName);
		wizard.addWizard(getColor);
		if (needsRange)
			wizard.addWizard(getRange);
		wizard.addWizard(getVis);
		wizard.addWizard(applyRainbowInRainbowColors);
		final WizardDialog dialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), wizard);
		dialog.create();
		dialog.setBlockOnOpen(true);
		dialog.open();
		// did it work?
		if (dialog.getReturnCode() == WizardDialog.OK)
		{
			// ok, use the name
			thisS.setName(getName.getString());
			thisS.setColor(getColor.getColor());
			thisS.setVisible(getVis.getBoolean());

			// are we doing range?
			if (needsRange)
			{
				final WorldDistance theRange = getRange.getRange();

				// did a range get entered?
				if ((theRange != null) && (theRange.getValue() != 0))
				{
					final Enumeration<Editable> iter = thisS.elements();
					while (iter.hasMoreElements())
					{
						final SensorContactWrapper cut = (SensorContactWrapper) iter
								.nextElement();
						cut.setRange(new WorldDistance(theRange));
					}
				}
			}
			if (applyRainbowInRainbowColors.getBoolean())
			{

				SensorWrapper theSensor = null;

				// are they items we're interested in?
				HiResDate startDTG = new HiResDate(Long.MAX_VALUE / 1000, 0);
				HiResDate endDTG = new HiResDate(0);
				Enumeration<Editable> elements = thisS.elements();
				ArrayList<Editable> sensors = new ArrayList<Editable>();
				ArrayList<SensorContactWrapper> list = new ArrayList<SensorContactWrapper>();
				while (elements.hasMoreElements())
				{
					sensors.add(elements.nextElement());
				}
				for (Editable thisE:sensors)
				{
					if (thisE instanceof SensorWrapper)
					{
						// just check that there's only one item selected
						if (sensors.size() == 1)
						{
							theSensor = (SensorWrapper) thisE;
						}
					}
					else if (thisE instanceof SensorContactWrapper)
					{
						list.add((SensorContactWrapper) thisE);
						if (startDTG.compareTo(((SensorContactWrapper) thisE).getDTG()) > 0)
						{
							startDTG = ((SensorContactWrapper) thisE).getDTG();
						}
						if (endDTG.compareTo(((SensorContactWrapper) thisE).getDTG()) < 0)
						{
							endDTG = ((SensorContactWrapper) thisE).getDTG();
						}
					}
				}

				// ok, do we have a single sensor?
				if (theSensor != null)
				{
					startDTG = theSensor.getStartDTG();
					endDTG = theSensor.getEndDTG();
					Collection<Editable> editables = theSensor.getItemsBetween(theSensor.getStartDTG(), theSensor.getEndDTG());
					for (Editable editable : editables)
					{
						if (editable instanceof SensorContactWrapper)
						{
							list.add((SensorContactWrapper) editable);
						}
					}
				}
				final HiResDate start = startDTG;
				final HiResDate end = endDTG;
				// create this operation
				final String title1 = "Shade in rainbow colors";
				Layer parentLayer = null;
				
				Layers parentLayers = _myLayers;
				if (parentLayers != null)
				{
					if (parentLayers.size() == 1)
					{
						parentLayer = parentLayers.elementAt(0);
					}
				}

				final IUndoableOperation theAction =
						new ShadeCutsOperation(title1, parentLayers, parentLayer, list.toArray(new SensorContactWrapper[0]),
								start, end, ShadeOperation.RAINBOW_SHADE);
				CorePlugin.run(theAction);
			}
		}
	}

	/**
	 * 
	 */
	private void initialiseFileLoaders()
	{
		// hey - sort out our plot readers
		_loader = new LoaderManager(EXTENSION_POINT_ID, EXTENSION_TAG, PLUGIN_ID)
		{

			public INamedItem createInstance(
					final IConfigurationElement configElement, final String label)
			{
				// get the attributes
				final String theLabel = configElement
						.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);
				final String icon = configElement
						.getAttribute(EXTENSION_TAG_ICON_ATTRIB);
				final String fileTypes = configElement
						.getAttribute(EXTENSION_TAG_EXTENSIONS_ATTRIB);

				// create the instance
				final INamedItem res = new IPlotLoader.DeferredPlotLoader(
						configElement, theLabel, icon, fileTypes);

				// and return it.
				return res;
			}

		};
	}

	private static TimePeriod getPeriodFor(final Layers theData)
	{
		TimePeriod res = null;

		for (final Enumeration<Editable> iter = theData.elements(); iter
				.hasMoreElements();)
		{
			final Layer thisLayer = (Layer) iter.nextElement();

			// and through this layer
			if (thisLayer instanceof TrackWrapper)
			{
				final TrackWrapper thisT = (TrackWrapper) thisLayer;
				res = extend(res, thisT.getStartDTG());
				res = extend(res, thisT.getEndDTG());
			}
			else if (thisLayer instanceof BaseLayer)
			{
				final Enumeration<Editable> elements = thisLayer.elements();
				while (elements.hasMoreElements())
				{
					final Plottable nextP = (Plottable) elements.nextElement();
					if (nextP instanceof Watchable)
					{
						final Watchable wrapped = (Watchable) nextP;
						final HiResDate dtg = wrapped.getTime();
						if (dtg != null)
						{
							res = extend(res, dtg);

							// also see if it this data type an end time
							if (wrapped instanceof WatchableList)
							{
								// ok, make sure we also handle the end time
								final WatchableList wl = (WatchableList) wrapped;
								final HiResDate endD = wl.getEndDTG();
								if (endD != null)
									res = extend(res, endD);
							}
						}
					}
				}
			}
		}

		return res;
	}

	private static TimePeriod extend(final TimePeriod period, final HiResDate date)
	{
		TimePeriod result = period;
		// have we received a date?
		if (date != null)
		{
			if (result == null)
			{
				result = new TimePeriod.BaseTimePeriod(date, date);
			}
			else
				result.extend(date);
		}

		return result;
	}

	/**
	 * method called when a helper object has completed a plot-load operation
	 * 
	 * @param source
	 */
	public void loadingComplete(final Object source)
	{

		// ok, stop listening for dirty calls - since there will be so many and
		// we
		// don't want
		// to start off with a dirty plot
		startIgnoringDirtyCalls();

		DebriefPlugin.logError(Status.INFO, "File load received", null);

		// and update the time management bits
		final TimePeriod timePeriod = getPeriodFor(_myLayers);

		if (timePeriod != null)
		{
			_timeManager.setPeriod(this, timePeriod);

			// also give it a current DTG (if it doesn't have one)
			if (_timeManager.getTime() == null)
				_timeManager.setTime(this, timePeriod.getStartDTG(), false);
		}

		// done - now we can process dirty calls again
		stopIgnoringDirtyCalls();

	}

	protected void filesDropped(final String[] fileNames)
	{
		super.filesDropped(fileNames);

		// ok, iterate through the files
		for (int i = 0; i < fileNames.length; i++)
		{
			final String thisFilename = fileNames[i];
			loadThisFile(thisFilename);
		}

		// ok, we're probably done - fire the update
		this._myLayers.fireExtended();

		// and resize to make sure we're showing all the data
		this._myChart.rescale();

		// hmm, we may have loaded more track data - but we don't track
		// loading of individual tracks - just fire a "modified" flag
		_trackDataProvider.fireTracksChanged();

	}

	private static boolean _updatingPlot = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.plotViewer.editors.CorePlotEditor#timeChanged()
	 */
	protected void timeChanged(final HiResDate newDTG)
	{
		super.timeChanged(newDTG);

		// just check we're ready for plotting.
		if (getChart() == null)
			return;

		if (_updatingPlot)
		{
			// skip the update - we're already at it
		}
		else
		{
			_updatingPlot = true;

			try
			{
				// note, we've learn't to use the default display instead of the
				// current one, we were get a null returned since this thread may not
				// have a display
				final Display myDis = Display.getDefault();
				if (myDis != null)
				{
					myDis.asyncExec(new Runnable()
					{
						public void run()
						{
							if (getChart() == null || getChart().getCanvas() == null)
							{
								return;
							}
							// ok - update our painter
							getChart().getCanvas().updateMe();
						}
					});
				}

			}
			finally
			{
				_updatingPlot = false;
			}
		}

	}

	/**
	 * Creates the outline page used with this editor.
	 * 
	 * @return the created plot outline page
	 */
	protected PlotOutlinePage createOutlinePage()
	{
		PlotOutlinePage page = new PlotOutlinePage(this, _myLayers);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mwc.cmap.plotViewer.editors.CorePlotEditor#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter)
	{
		Object res = null;

		if (adapter == Layers.class)
		{
			if (_myLayers != null)
				res = _myLayers;
		}
		else if (adapter == TrackManager.class)
		{
			res = _trackDataProvider;
		}
		else if (adapter == TrackDataProvider.class)
		{
			res = _trackDataProvider;
		}
		else if (adapter == PlainProjection.class)
		{
			res = super.getChart().getCanvas().getProjection();
		}
		else if (adapter == TimeControllerOperationStore.class)
		{
			res = getTimeControllerOperations();
		}
		else if (adapter == LayerPainterManager.class)
		{
			res = _layerPainterManager;
		}
		else if (adapter == ControllablePeriod.class)
		{
			res = _myOperations;
		}
		else if (adapter == TimeControlPreferences.class)
		{
			res = _timePreferences;
		}
		else if (adapter == ControllableTime.class)
		{
			res = _timeManager;
		}
		else if (adapter == TimeProvider.class)
		{
			res = _timeManager;
		}
		else if (adapter == IGotoMarker.class)
		{
			return new IGotoMarker()
			{
				public void gotoMarker(final IMarker marker)
				{
					final String lineNum = marker.getAttribute(IMarker.LINE_NUMBER, "na");
					if (lineNum != "na")
					{
						// right, convert to DTG
						final HiResDate tNow = new HiResDate(0, Long.parseLong(lineNum));
						_timeManager.setTime(this, tNow, true);
					}
				}

			};
		}

		else if (adapter == IRollingNarrativeProvider.class)
		{
			// so, do we have any narrative data?
			final Layer narr = _myLayers.findLayer(ImportReplay.NARRATIVE_LAYER);

			if (narr != null)
			{
				// did we find it?
				// cool, cast to object
				final NarrativeWrapper wrapper = (NarrativeWrapper) narr;

				res = wrapper;
			}
			else
			{
				// create an empty narrative warpper
				res = new NarrativeWrapper("Empty");
			}
		}
		else if (adapter == RelativeProjectionParent.class)
		{
			if (_myRelativeWrapper == null)
			{
				_myRelativeWrapper = new RelativeProjectionParent()
				{

					public double getHeading()
					{
						double res1 = 0.0;
						final Watchable thePos = getFirstPosition(_trackDataProvider,
								_timeManager);

						if (thePos != null)
						{
							// yup, get the centre point
							res1 = thePos.getCourse();
						}

						return res1;
					}

					public WorldLocation getLocation()
					{
						MWC.GenericData.WorldLocation res1 = null;
						final Watchable thePos = getFirstPosition(_trackDataProvider,
								_timeManager);

						if (thePos != null)
						{
							// yup, get the centre point
							res1 = thePos.getBounds().getCentre();
						}
						return res1;
					}

					private Watchable getFirstPosition(final TrackDataProvider provider,
							final TimeManager manager)
					{
						Watchable res = null;

						// do we have a primary?
						final WatchableList priTrack = provider.getPrimaryTrack();
						if (priTrack == null)
						{
							CorePlugin.logError(Status.ERROR,
									"Can't do relative projection without primary track", null);
						}
						else
						{
							final Watchable[] list = priTrack.getNearestTo(manager.getTime());
							if (list != null)
								if (list.length > 0)
									res = list[0];
						}

						return res;
					}
				};
			}
			res = _myRelativeWrapper;
		}
		else if (IContentOutlinePage.class.equals(adapter))
		{
			// lazy instantiation of outline page
			if (_outlinePage == null)
				_outlinePage = createOutlinePage();

			res = _outlinePage;
		}

		// did we find anything?
		if (res == null)
		{
			// nope, see if the parent can find anything
			res = super.getAdapter(adapter);
		}

		// ok, done
		return res;
	}

	private org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore getTimeControllerOperations()
	{
		return _timeControllerOperations;
	}

	/**
	 * @param parent
	 */
	protected SWTChart createTheChart(final Composite parent)
	{
		final SWTChart res = new SWTChart(_myLayers, parent, _myGeoHandler)
		{

			@Override
			public SWTCanvas createCanvas(final Composite parent1,
					final GtProjection projection)
			{
				return new CustomisedSWTCanvas(parent1, _myGeoHandler)
				{

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void parentFireSelectionChanged(final ISelection selected)
					{
						chartFireSelectionChanged(selected);
					}

					public void doSupplementalRightClickProcessing(
							final MenuManager menuManager, final Plottable selected,
							final Layer theParentLayer)
					{
						if (selected instanceof CreateEditorForParent)
						{
							// get the parent track
							final CreateEditorForParent editor = (CreateEditorForParent) selected;
							final Editable parent11 = editor.getParent();
							RightClickSupport.getDropdownListFor(menuManager, new Editable[]
							{ parent11 }, new Layer[]
							{ theParentLayer }, new Layer[]
							{ theParentLayer }, getLayers(), true);
						}
					}
				};
			}

			public void chartFireSelectionChanged(final ISelection sel)
			{
				fireSelectionChanged(sel);
			}

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @param thisLayer
			 * @param dest
			 */
			protected void paintThisLayer(final Layer thisLayer, final CanvasType dest)
			{
				try
				{
					// get the current time
					final HiResDate tNow = _timeManager.getTime();

					// do we know the time?
					// if (tNow != null)
					if (true)
					{
						// yes. cool, get plotting
						_layerPainterManager.getCurrentPainter().paintThisLayer(thisLayer,
								dest, tNow);

						// ok, now sort out the highlight

						// right, what are the watchables
						final Vector<Plottable> watchables = SnailPainter
								.getWatchables(thisLayer);

						// cycle through them
						final Enumeration<Plottable> watches = watchables.elements();
						while (watches.hasMoreElements())
						{
							final WatchableList list = (WatchableList) watches.nextElement();
							// is the primary an instance of layer (with it's
							// own line
							// thickness?)
							if (list instanceof Layer)
							{
								final Layer ly = (Layer) list;
								final int thickness = ly.getLineThickness();
								dest.setLineWidth(thickness);
							}

							// ok, clear the nearest items
							if (tNow != null)
							{
								final Watchable[] wList = list.getNearestTo(tNow);
								for (int i = 0; i < wList.length; i++)
								{
									final Watchable watch = wList[i];
									// if (wList.length > 0)
									// watch = wList[0];

									if (watch != null)
									{
										// aah, is this the primary?
										final boolean isPrimary = (list == _trackDataProvider
												.getPrimaryTrack());

										// plot it
										_layerPainterManager.getCurrentHighlighter().highlightIt(
												dest.getProjection(), dest, list, watch, isPrimary);
									}

								}
							} // whether we have a current time...
						}
					}
				}
				catch (final Exception e)
				{
					CorePlugin
							.logError(Status.ERROR, "Whilst repainting:" + thisLayer, e);
				}
			}

		};
		final Control control = res.getCanvasControl();
		if (control != null && !control.isDisposed()) {
			control.addTraverseListener(dragModeListener);
			control.addDisposeListener(new DisposeListener()
			{
				
				@Override
				public void widgetDisposed(DisposeEvent e)
				{
					control.removeTraverseListener(dragModeListener);
					control.removeDisposeListener(this);
				}
			});
		}
		return res;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(final IProgressMonitor monitor)
	{
		final IEditorInput input = getEditorInput();
		String ext = null;

		// do we have an input
		if (input.exists())
		{
			// get the file suffix
			if (input instanceof IFileEditorInput)
			{
				IFile file = null;
				file = ((IFileEditorInput) getEditorInput()).getFile();
				final IPath path = file.getFullPath();
				ext = path.getFileExtension();
			}
			else if (input instanceof FileStoreEditorInput)
			{
				final FileStoreEditorInput fi = (FileStoreEditorInput) input;
				final URI uri = fi.getURI();
				final Path path = new Path(uri.getPath());
				ext = path.getFileExtension();
			}

			// right, have a look at it.
			if ( (ext == null) || (!ext.equalsIgnoreCase("xml") && !ext.equalsIgnoreCase("dpf")) )
			{
				String msg = "Debrief stores data in a structured (xml) text format,";
				msg += "\nwhich is different to the format you've used to load the data.";
				msg += "\nThus you must specify an existing (or new) folder to "
						+ "store the plot,\nand provide new filename.";
				msg += "\nNote: it's important that you give the file a .dpf file suffix";
				final MessageDialog md = new MessageDialog(getEditorSite().getShell(),
						"Save as", null, msg, MessageDialog.WARNING, new String[]
						{ "Ok" }, 0);
				md.open();

				// not, we have to do a save-as
				doSaveAs("Can't store this file-type, select a target folder, and remember to save as Debrief plot-file (*.dpf)");
			}
			else
			{

				OutputStream tmpOS = null;
				// the workspace has a listenser that will close/rename the current plot
				// editor if it's parent file
				// has been deleted/renamed. We need to cancel that processing whilst we
				// do a file-save,
				// since the file-save includes a name-change.
				ResourcesPlugin.getWorkspace().removeResourceChangeListener(
						resourceChangeListener);
				try
				{
					// NEW STRATEGY. Save to tmp first, then overwrite existing on
					// success.

					// 1. create the temp file
					final File tmpFile = File.createTempFile("DebNG_tmp", ".dpf");
					tmpFile.createNewFile();
					tmpFile.deleteOnExit();

					// 1a. record the name of the tmp file in the log
					final String filePath = tmpFile.getAbsolutePath();
					CorePlugin.logError(Status.INFO, "Created temp save file at:"
							+ filePath, null);

					// 2. open the file as a stream
					tmpOS = new FileOutputStream(tmpFile);

					// 3. save to this stream
					doSaveTo(tmpOS, monitor);

					tmpOS.close();
					tmpOS = null;

					// sort out the file size
					CorePlugin.logError(Status.INFO,
							"Saved file size is:" + tmpFile.length() / 1024 + " Kb", null);

					// 4. Check there's something in the temp file
					if (tmpFile.exists())
						if (tmpFile.length() == 0)
						{
							// save failed throw exception (to be collected shortly
							// afterwards)
							throw new RuntimeException("Stored file is of zero size");
						}
						else
						{

							// save worked. cool.

							// 5. overwrite the existing file with the saved file
							// - note we will only reach this point if the save succeeded.
							// sort out where we're saving to
							if (input instanceof IFileEditorInput)
							{
								CorePlugin.logError(Status.INFO,
										"Performing IFileEditorInput save", null);

								final IFile file = ((IFileEditorInput) getEditorInput())
										.getFile();

								// get the current path (since we're going to be moving the temp
								// one to it
								final IPath thePath = file.getLocation();

								// create a backup path
								final IPath bakPath = file.getFullPath()
										.addFileExtension("bak");

								// delete any existing backup file
								final File existingBackupFile = new File(file.getLocation()
										.addFileExtension("bak").toOSString());
								if (existingBackupFile.exists())
								{
									CorePlugin.logError(Status.INFO,
											"Existing back file still there, having to delete"
													+ existingBackupFile.getAbsolutePath(), null);
									existingBackupFile.delete();
								}

								// now rename the existing file as the backup
								file.move(bakPath, true, monitor);

								// move the temp file to be our real working file
								final File destFile = thePath.toFile().getAbsoluteFile();
								if (!tmpFile.renameTo(destFile))
								{
									FileUtils.moveFile(tmpFile, destFile);
								}

								// finally, delete the backup file
								if (existingBackupFile.exists())
								{
									CorePlugin.logError(Status.INFO,
											"Save operation completed successfully, deleting backup file"
													+ existingBackupFile.getAbsolutePath(), null);
									existingBackupFile.delete();
								}

								// throw in a refresh - since we've done the save outside
								// Eclipse
								file.getParent()
										.refreshLocal(IResource.DEPTH_INFINITE, monitor);

							}
							else if (input instanceof FileStoreEditorInput)
							{

								CorePlugin.logError(Status.INFO,
										"Performing FileStoreEditorInput save", null);

								// get the data-file
								final FileStoreEditorInput fi = (FileStoreEditorInput) input;
								final URI _uri = fi.getURI();
								final Path _p = new Path(_uri.getPath());

								// create pointers to the existing file, and the backup file
								final IFileStore existingFile = EFS.getLocalFileSystem()
										.getStore(_p);
								final IFileStore backupFile = EFS.getLocalFileSystem()
										.getStore(_p.addFileExtension("bak"));

								// delete any existing backup file
								final IFileInfo backupStatus = backupFile.fetchInfo();
								if (backupStatus.exists())
								{
									CorePlugin.logError(Status.INFO,
											"Existing back file still there, having to delete"
													+ backupFile.toURI().getRawPath(), null);
									backupFile.delete(EFS.NONE, monitor);
								}

								// now rename the existing file as the backup
								existingFile.move(backupFile, EFS.OVERWRITE, monitor);

								// and rename the temp file as the working file
								tmpFile.renameTo(existingFile.toLocalFile(EFS.NONE, monitor));

								if (backupStatus.exists())
								{
									CorePlugin.logError(Status.INFO,
											"Save operation successful, deleting backup file"
													+ backupFile.toURI().getRawPath(), null);
									backupFile.delete(EFS.NONE, monitor);
								}

							}
						}
				}
				catch (final CoreException e)
				{
					CorePlugin.logError(Status.ERROR,
							"Failed whilst saving external file", e);
				}
				catch (final FileNotFoundException e)
				{
					CorePlugin.logError(Status.ERROR,
							"Failed to find local file to save to", e);
				}
				catch (final Exception e)
				{
					CorePlugin.logError(Status.ERROR, "Unknown file-save error occurred",
							e);
				}
				finally
				{
					try
					{
						if (tmpOS != null)
							tmpOS.close();
					}
					catch (final IOException e)
					{
						CorePlugin.logError(Status.ERROR, "Whilst performing save", e);
					}
					ResourcesPlugin.getWorkspace().addResourceChangeListener(
							resourceChangeListener,
							IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
									| IResourceChangeEvent.POST_CHANGE);
				}
			}
		}
	}

	/**
	 * save our plot to the indicated location
	 * 
	 * @param destination
	 *          where to save plot to
	 * @param monitor
	 *          somebody/something to be informed about progress
	 */
	private void doSaveTo(final OutputStream os, final IProgressMonitor monitor)
	{
		if (os != null)
		{

			final IProduct prod = Platform.getProduct();
			final Bundle bund = prod.getDefiningBundle();
			final String version = "" + new Date(bund.getLastModified());

			try
			{
				// ok, now write to the file
				DebriefEclipseXMLReaderWriter.exportThis(this, os, version);

				// ok, lastly indicate that the save worked (if it did!)
				_plotIsDirty = false;
				firePropertyChange(PROP_DIRTY);
			}
			catch (final Exception e)
			{
				DebriefPlugin.logError(Status.ERROR, "Error exporting plot file", e);
			}

		}
		else
		{
			DebriefPlugin.logError(Status.ERROR,
					"Unable to identify source file for plot", null);
		}

	}

	public void doSaveAs()
	{
		doSaveAs("Save as");
	}

	/**
	 * utility function to extact the root part of this filename
	 * 
	 * @param fileName
	 *          full file path
	 * @return root of file name (before the . marker)
	 */
	private String fileNamePartOf(final String fileName)
	{
		if (fileName == null)
		{
			throw new IllegalArgumentException("file name == null");
		}

		// ok, extract the parent portion
		final File wholeFile = new File(fileName);
		final String parentSection = wholeFile.getParent();
		final int parentLen = parentSection.length() + 1;
		final int fileLen = fileName.length();
		final String fileSection = fileName.substring(parentLen, fileLen);

		final int pos = fileSection.lastIndexOf('.');
		if (pos > 0 && pos < fileSection.length() - 1)
		{
			return fileSection.substring(0, pos);
		}
		return "";
	}

	public void doSaveAs(final String message)
	{
		// do we have a project?
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject[] projects = workspace.getRoot().getProjects();
		if ((projects == null) || (projects.length == 0))
		{
			String msg = "Debrief plots are stored in 'Projects'";
			msg += "\nBut you do not yet have one defined.";
			msg += "\nPlease follow the 'Generating a project for your data' cheat";
			msg += "\nsheet, accessed from Help/Cheat Sheets then Debrief/Getting started.";
			msg += "\nOnce you have created your project, please start the Save process again.";
			msg += "\nNote: the cheat sheet will open automatically when you close this dialog.";
			final MessageDialog md = new MessageDialog(getEditorSite().getShell(),
					"Save as", null, msg, MessageDialog.WARNING, new String[]
					{ "Ok" }, 0);
			md.open();

			// try to open the cheat sheet
			final String CHEAT_ID = "org.mwc.debrief.help.started.generate_project";

			Display.getCurrent().asyncExec(new Runnable()
			{
				public void run()
				{
					final OpenCheatSheetAction action = new OpenCheatSheetAction(CHEAT_ID);
					action.run();
				}
			});

			// ok, drop out - we can't do a save anyway
			return;
		}

		// get the workspace

		final SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
		dialog.setTitle("Save Plot As");
		if (getEditorInput() instanceof FileEditorInput)
		{
			// this has been loaded from the navigator
			final IFile oldFile = ((FileEditorInput) getEditorInput()).getFile();
			// dialog.setOriginalFile(oldFile);

			final IPath oldPath = oldFile.getFullPath();
			final IPath newStart = oldPath.removeFileExtension();
			final IPath newPath = newStart.addFileExtension("dpf");
			final File asFile = newPath.toFile();
			final String newName = asFile.getName();
			dialog.setOriginalName(newName);
		}
		else if (getEditorInput() instanceof FileStoreEditorInput)
		{
			// this has been dragged from an explorer window
			final FileStoreEditorInput fi = (FileStoreEditorInput) getEditorInput();
			final URI uri = fi.getURI();
			final File thisFile = new File(uri.getPath());
			String newPath = fileNamePartOf(thisFile.getAbsolutePath());
			newPath += ".dpf";
			dialog.setOriginalName(newPath);
		}

		dialog.create();
		if (message != null)
			dialog.setMessage(message, IMessageProvider.WARNING);
		else
			dialog.setMessage("Save file to another location.");
		dialog.open();
		final IPath path = dialog.getResult();

		if (path == null)
		{
			return;
		}
		else
		{
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (!file.exists())
				try
				{
					System.out.println("creating:" + file.getName());
					file.create(new ByteArrayInputStream(new byte[]
					{}), false, null);
				}
				catch (final CoreException e)
				{
					DebriefPlugin.logError(IStatus.ERROR,
							"Failed trying to create new file for save-as", e);
					return;
				}

			OutputStream os = null;
			try
			{
				os = new FileOutputStream(file.getLocation().toFile(), false);
				// ok, write to the file
				doSaveTo(os, new NullProgressMonitor());

				// also make this new file our input
				final IFileEditorInput newInput = new FileEditorInput(file);
				setInputWithNotify(newInput);

				// lastly, trigger a navigator refresh
				final IFile iff = newInput.getFile();
				iff.refreshLocal(IResource.DEPTH_ONE, null);
				// refresh navigator
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IViewPart view = page
						.findView("org.eclipse.ui.views.ResourceNavigator");
				if (view instanceof ResourceNavigator)
				{
					((ResourceNavigator) view).getViewer().refresh(iff);
				}
			}
			catch (final FileNotFoundException e)
			{
				CorePlugin
						.logError(Status.ERROR, "Failed whilst performing Save As", e);
			}
			catch (final CoreException e)
			{
				CorePlugin.logError(Status.ERROR,
						"Refresh failed after saving new file", e);
			}
			finally
			{
				// and close it
				try
				{
					if (os != null)
						os.close();
				}
				catch (final IOException e)
				{
					CorePlugin.logError(Status.ERROR, "Whilst performaing save-as", e);
				}

			}

		}

		_plotIsDirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * 
	 */
	@Override
	protected void layersExtended()
	{
		// inform our parent
		super.layersExtended();

		// we should also recalculate the time period we cover
		final TimePeriod timePeriod = getPeriodFor(_myLayers);

		// and share the good news.
		_timeManager.setPeriod(this, timePeriod);

		// and tell the track data manager that something's happened. One of
		// it's
		// tracks may have been
		// deleted!
		_trackDataProvider.fireTracksChanged();
	}

	public boolean isSaveAsAllowed()
	{
		return true;
	}

	public void outlinePageClosed()
	{
		_outlinePage = null;
	}
}
