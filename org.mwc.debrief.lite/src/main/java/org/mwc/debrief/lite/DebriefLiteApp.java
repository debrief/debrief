/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.mwc.cmap.geotools.gt2plot.GeoToolsLayer;
import org.mwc.cmap.geotools.gt2plot.ShapeFileLayer;
import org.mwc.cmap.geotools.gt2plot.WorldImageLayer;
import org.mwc.debrief.lite.gui.FitToWindow;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePane.Direction;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePaneWithTitle;
import org.mwc.debrief.lite.gui.custom.graph.GraphPanelView;
import org.mwc.debrief.lite.gui.custom.narratives.NarrativeConfigurationModel;
import org.mwc.debrief.lite.gui.custom.narratives.NarrativePanelToolbar;
import org.mwc.debrief.lite.gui.custom.narratives.NarrativePanelView;
import org.mwc.debrief.lite.loader.AntaresLoaderDebriefLite;
import org.mwc.debrief.lite.map.ContextMenu;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.GeoToolMapRenderer.MapRenderer;
import org.mwc.debrief.lite.map.LiteMapPane;
import org.mwc.debrief.lite.menu.DebriefRibbon;
import org.mwc.debrief.lite.menu.DebriefRibbonFile;
import org.mwc.debrief.lite.menu.DebriefRibbonInsert;
import org.mwc.debrief.lite.menu.DebriefRibbonTimeController;
import org.mwc.debrief.lite.menu.DebriefRibbonView;
import org.mwc.debrief.lite.menu.MenuUtils;
import org.mwc.debrief.lite.outline.OutlinePanelView;
import org.mwc.debrief.lite.util.DoSaveAs;
import org.mwc.debrief.lite.util.ResizableIconFactory;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Tote.Painters.PainterManager;
import Debrief.GUI.Tote.Painters.SnailPainter2;
import Debrief.GUI.Tote.Painters.TotePainter;
import Debrief.ReaderWriter.Antares.ImportAntares;
import Debrief.ReaderWriter.NMEA.ImportNMEA;
import Debrief.ReaderWriter.Nisida.ImportNisida;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.ReaderWriter.XML.SessionHandler;
import Debrief.ReaderWriter.XML.dummy.SATCHandler_Mock;
import Debrief.ReaderWriter.XML.dynamic.DynamicLayerHandler;
import Debrief.ReaderWriter.XML.dynamic.DynamicShapeLayerHandler;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.DataListenerAdaptor;
import MWC.GUI.Defaults;
import MWC.GUI.Defaults.PreferenceProvider;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.Layers.DataListener2;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.StepperListener;
import MWC.GUI.SupportedApps;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.Canvas.ExtendedCanvasAdapter;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.DragDrop.FileDropSupport.FileDropListener;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.temporal.PlotOperations;
import MWC.TacticalData.temporal.TimeManager;
import MWC.TacticalData.temporal.TimeProvider;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.ImportManager;
import MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller;
import MWC.Utilities.ReaderWriter.PlainImporter;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 * @author Unni Mana <unnivm@gmail.com>
 */

public class DebriefLiteApp implements FileDropListener {

	/**
	 * introduce a preferences helper, particularly to give default font sizes
	 *
	 */
	private static class LiteProvider implements PreferenceProvider {

		private Font _defaultFont;

		@Override
		public Font getDefaultFont() {
			if (_defaultFont == null) {
				// it's ok if we throw an exception here, just in case we had
				// a platform-specific font initialise string
				_defaultFont = new Font("Arial", Font.PLAIN, 18);
			}
			return _defaultFont;
		}

		@Override
		public String getPreference(final String name) {
			final String res;
			if (SensorContactWrapper.TRANSPARENCY.equals(name)) {
				res = "100";
			} else {
				return null;
			}
			return res;
		}
	}

	/**
	 * helper class
	 *
	 * @author ian
	 *
	 */
	private static class ToteSetter implements Runnable {
		public static interface RefreshStepper {
			void refresh(StepperListener listener);
		}

		final private PainterManager _manager;
		final private StepperListener _painter;

		final private RefreshStepper _refresher;

		public ToteSetter(final PainterManager manager, final StepperListener painter, final RefreshStepper refresher) {
			_manager = manager;
			_manager.addPainter(painter);
			_painter = painter;
			_refresher = refresher;
		}

		@Override
		public void run() {
			_manager.setCurrentListener(_painter);
			_refresher.refresh(_painter);
		}
	}

	public static final String TEST_MODE = "TEST_MODE";

	private static final String BACKGROUND_NAME = "Background coastline";

	private static DebriefLiteApp _instance;
	public static final String DEBRIEF_LITE_APP = "Debrief Lite";
	public static final String appName = DEBRIEF_LITE_APP;
	public static final String NOTES_ICON = "icons/16/note.png";

	public static String currentFileName = null;
	public static final String ACTIVE_STATE = "ACTIVE";
	public static final String TIME_ENABLED_STATE = "TIMED_STATE";
	public static final String INACTIVE_STATE = "INACTIVE";

	public static final String STATE = "STATE";

	public static String state = null;

	public static boolean collapsedState = false;
	private static ArrayList<PropertyChangeListener> stateListeners = new ArrayList<>();

	protected static boolean _plotDirty;

	private static String defaultTitle = appName + " (" + BuildDate.BUILD_DATE + ")";

	private final static LiteApplication app = new LiteApplication(ImportReplay.IMPORT_AS_OTG, 0L);

	private static final JLabel statusBar = new JLabel("[pending]");

	private static List<TrackWrapper> determineCandidateHosts() {
		final List<TrackWrapper> res = new ArrayList<TrackWrapper>();

		final Enumeration<Editable> iter = _instance._theLayers.elements();
		while (iter.hasMoreElements()) {
			final Editable editable = iter.nextElement();
			if (editable instanceof TrackWrapper) {
				res.add((TrackWrapper) editable);
			}
		}

		return res;
	}

	public static void disposeForTest() {
		System.out.println("Disposed:" + _instance);
		_instance.theFrame.dispose();
		currentFileName = null;
		state = null;
		collapsedState = false;
		stateListeners.clear();
		_instance = null;
	}

	public static ToolParent getDefault() {
		return app;
	}

	public static DebriefLiteApp getInstance() {
		return _instance;
	}

	public static void handleImportTIFFile(final File file) {
		final String layerName = file.getName();

		// ok - get loading going
		final ExternallyManagedDataLayer dl = new ExternallyManagedDataLayer(ChartBoundsWrapper.WORLDIMAGE_TYPE,
				layerName, file.getAbsolutePath());

		// note: our layers.addThisLayer() has extra processing to wrap
		// ExternallyManagedDataLayer instances
		_instance._theLayers.addThisLayer(dl);
	}

	public static boolean isDirty() {
		return _plotDirty;
	}

	public static void launchApp() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						new DebriefLiteApp();
					} catch (final NoninvertibleTransformException e) {
						Application.logError2(ToolParent.ERROR, "Error invsersing the Transform", e);
					}
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void loadBackdropdata(final Layers layers) {
		// ok, do the shapefile
		final String shape_path = "data/coastline/ne_10M_admin0_countries_89S.shp";
		System.out.println("ShapePath:" + new File(shape_path).getAbsolutePath());
		final ExternallyManagedDataLayer extFile = new ExternallyManagedDataLayer(ChartBoundsWrapper.SHAPEFILE_TYPE,
				BACKGROUND_NAME, shape_path);
		System.out.println("Adding layer:" + extFile.getName());
		layers.addThisLayer(extFile);
	}

	public static void main(final String[] args) {
		launchApp();
	}

	private static void notifyListenersStateChanged(final Object source, final String property, final String oldValue,
			final String newValue) {
		for (final PropertyChangeListener event : stateListeners) {
			event.propertyChange(new PropertyChangeEvent(source, property, oldValue, newValue));
		}
	}

	public static void openDsfFile(final File file) throws FileNotFoundException {
		final ImportReplay rep = new ImportReplay();
		rep.setLayers(_instance._theLayers);
		final FileInputStream fis = new FileInputStream(file);
		try {
			rep.importThis(file.getAbsolutePath(), fis);
		} finally {
			try {
				fis.close();
			} catch (final IOException e) {
				getDefault().logError(ToolParent.ERROR, "Failed to close DSF file", e);
			}
		}
		final Vector<SensorWrapper> sensors = rep.getPendingSensors();

		boolean isAllCorrect = true;
		final StringBuilder builder = new StringBuilder();
		// see if there are any sensors awaiting a host
		if (sensors.size() >= 1) {
			final Iterator<SensorWrapper> sIter = sensors.iterator();
			while (sIter.hasNext()) {
				final SensorWrapper sensor = sIter.next();
				if (sensor.getHost() == null) {
					// have we sorted out the hosts?
					final List<TrackWrapper> candidateHosts = determineCandidateHosts();

					if (candidateHosts.size() == 0) {
						JOptionPane.showMessageDialog(null, "Sensor data can only be loaded after tracks",
								"Loading sensor data", JOptionPane.ERROR_MESSAGE);
						return;
					}

					isAllCorrect = false;
					builder.append(",");
					builder.append(sensor.getName());
				}
			}
		}
		if (isAllCorrect) {
			rep.storePendingSensors();
			_instance._theLayers.fireExtended();
			JOptionPane.showMessageDialog(null, "Finished loading sensor data from the file", "Loading sensor data",
					JOptionPane.INFORMATION_MESSAGE);

		} else {
			JOptionPane.showMessageDialog(null, "Couldn't find Track(s) titled: " + builder.toString().substring(1),
					"Couldn't find Track", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	public static void openNMEAFile(final File file) {
		try {
			_instance.handleImportNMEAFile(file);
		} catch (final Exception e) {
			Trace.trace(e);
		}
	}
	
	
	public static void openNisidaFile(final File file) {
		try {
			_instance.handleImportNisidaFile(file);
		} catch (final Exception e) {
			Trace.trace(e);
		}
	}

	public static void openPlotFile(final File file) {

		try {
			_instance.handleImportDPF(file);
		} catch (final Exception e) {
			Trace.trace(e);
		}
	}

	public static void openRepFile(final File file) {
		try {
			_instance.handleImportRep(new File[] { file });
		} catch (final Exception e) {
			Trace.trace(e);
		}
	}

	public static void setDirty(final boolean b) {

		_plotDirty = b;
		if (currentFileName != null) {
			final String name = new File(currentFileName).getName();
			if (b) {
				setTitle(name + " *");
			} else {
				setTitle(name);
			}
		}

	}

	private static void setInitialArea(final JMapPane mapPane, final MathTransform transform) {
		// give it a default viewport - overlooking Europe
		final DirectPosition2D tl = new DirectPosition2D(-22, 59.75);
		final DirectPosition2D br = new DirectPosition2D(37, 36.5);

		try {
			// convert to map units
			transform.inverse().transform(tl, tl);
			transform.inverse().transform(br, br);
			final Envelope2D envelope = new Envelope2D(tl, br);
			mapPane.setDisplayArea(envelope);
		} catch (MismatchedDimensionException | TransformException e) {
			Application.logError2(ToolParent.ERROR, "Failure in setting initial viewport coverage", e);
		}

		mapPane.getMapContent().getViewport().setFixedBoundsOnResize(true);
	}

	/**
	 * State of the application. Inactive will disable all the button.
	 *
	 * @param newState
	 */
	public static void setState(final String newState) {
		final String oldState = state;
		state = newState;

		// if (newState != null && !newState.equals(oldState) && _instance != null)
		if (newState != null && _instance != null) {
			notifyListenersStateChanged(_instance, STATE, oldState, newState);
		}
	}

	public static void setTitle(final String title) {
		if (title.startsWith(defaultTitle)) {
			_instance.theFrame.setTitle(title);
		} else {
			_instance.theFrame.setTitle(defaultTitle + " - " + title);
		}
	}

	/**
	 * @param filename autofilled
	 */
	private static String suffixOf(final String filename) {
		String theSuffix = null;
		final int pos = filename.lastIndexOf(".");
		theSuffix = filename.substring(pos, filename.length());
		return theSuffix.toUpperCase();
	}

	public static void updateStatusMessage(final String string) {

		statusBar.setText(string);
	}

	private final HashMap<String, String> generalPersistence = new HashMap<>();

	protected DataListener2 _listenForMods;

	private OutlinePanelView layerManager;

	private GraphPanelView graphPanelView;

	private final JXCollapsiblePaneWithTitle outlinePanel = new JXCollapsiblePaneWithTitle(Direction.LEFT, "Outline",
			400);
	private final JXCollapsiblePaneWithTitle graphPanel = new JXCollapsiblePaneWithTitle(Direction.DOWN, "Graph", 150);

	private final JXCollapsiblePaneWithTitle narrativePanel = new JXCollapsiblePaneWithTitle(Direction.RIGHT,
			"Narratives", 350);

	private final List<JXCollapsiblePaneWithTitle> openPanels = new ArrayList<JXCollapsiblePaneWithTitle>();
	private final JRibbonFrame theFrame;
	private final Layers _theLayers = new Layers() {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void addThisLayer(final Layer theLayer) {

			// ok, if this is an externally managed layer (and we're doing
			// GT-plotting, we will wrap it, and actually add the wrapped layer
			final Layer wrappedLayer;
			if (theLayer instanceof ExternallyManagedDataLayer) {
				final ExternallyManagedDataLayer dl = (ExternallyManagedDataLayer) theLayer;
				if (dl.getDataType().equals(ChartBoundsWrapper.WORLDIMAGE_TYPE)) {
					final GeoToolsLayer gt = new WorldImageLayer(dl.getName(), dl.getFilename());

					gt.setVisible(dl.getVisible());
					projection.addGeoToolsLayer(gt);
					wrappedLayer = gt;
				} else if (dl.getDataType().equals(ChartBoundsWrapper.SHAPEFILE_TYPE)) {
					// just see if it's a raster extent layer (special processing)
					if (dl.getName().equals(WorldImageLayer.RASTER_FILE)) {
						// special processing - wrap it.
						wrappedLayer = WorldImageLayer.RasterExtentHelper.loadRasters(dl.getFilename(), this);
					} else {

						// special handling. D-Lite includes background chart, by default. But, when
						// a plot gets saved, it will save this background chart. We wish to avoid
						// loading more and more background charts
						final String layerName = dl.getName();
						if (BACKGROUND_NAME.equals(layerName)) {
							// hmm, see of we've already got background data
							final List<org.geotools.map.Layer> layers = mapPane.getMapContent().layers();
							for (final org.geotools.map.Layer layer : layers) {
								if (layer.getTitle().equals(BACKGROUND_NAME)) {
									// ok, skip it - we've already got it loaded
									return;
								}
							}
						}

						// ok, it's a normal shapefile: load it.
						final GeoToolsLayer gt = new ShapeFileLayer(layerName, dl.getFilename());
						gt.setVisible(dl.getVisible());
						projection.addGeoToolsLayer(gt);
						wrappedLayer = gt;
					}
				} else {
					wrappedLayer = null;
				}

				if (wrappedLayer != null)
					super.addThisLayer(wrappedLayer);
			} else {
				super.addThisLayer(theLayer);
			}

		}

		@Override
		public void removeThisLayer(final Layer theLayer) {
			if (theLayer instanceof GeoToolsLayer) {
				// get the content
				/*
				 * final GtProjection gp = (GtProjection) _myChart.getCanvas().getProjection();
				 */
				final GeoToolsLayer gt = (GeoToolsLayer) theLayer;

				// just check it can be deleted
				if (mapPane.getMapContent().layers().size() > 1) {
					gt.clearMap();

					// and remove from the actual list
					super.removeThisLayer(theLayer);
				} else {
					Application.logError2(ToolParent.WARNING,
							"We need to keep one backround layer, to enable correct projection", null);
				}
			} else {
				// and remove from the actual list
				super.removeThisLayer(theLayer);
			}
		}
	};

	private final GeoToolMapProjection projection;

	private final LiteSession session;

	private final LiteMapPane mapPane;

	private final PlotOperations _myOperations = new PlotOperations() {
		// just provide with our complete set of layers
		@Override
		public Object[] getTargets() {
			// ok, return our top level layers as objects
			final Vector<Layer> res = new Vector<Layer>(0, 1);
			for (int i = 0; i < _theLayers.size(); i++) {
				res.add(_theLayers.elementAt(i));
			}
			return res.toArray();
		}

		/**
		 * override performing the operation, since we'll do a screen update on
		 * completion
		 */
		@Override
		public Vector<Layer> performOperation(final AnOperation operationName) {
			// make the actual change
			final Vector<Layer> res = super.performOperation(operationName);

			if (res != null && !res.isEmpty()) {
				for (final Iterator<Layer> iter = res.iterator(); iter.hasNext();) {
					final Layer thisL = iter.next();
					// and update the screen
					_theLayers.fireReformatted(thisL);
				}
			}
			return res;
		}
	};

	private final TimeManager timeManager = new TimeManager();

	private final GeoToolMapRenderer geoMapRenderer;

	private final PainterManager painterManager;

	private final LiteTote theTote;

	private final LiteStepControl _stepControl;

	private final Layer safeChartFeatures;
	private HiResDate _pendingNewTime;
	private HiResDate _pendingOldTime;
	private final ToteSetter _normalSetter;
	private final ToteSetter _snailSetter;

	private boolean _plotUpdating = false;

	// Maybe this should go inside GeoToolMapRenderer
	public final float initialAlpha = 0.7f;

	private DebriefLiteApp() throws NoninvertibleTransformException {
		_instance = this;

		// set the substance look and feel
		System.setProperty(SupportedApps.APP_NAME_SYSTEM_PROPERTY, SupportedApps.DEBRIEF_LITE_APP);

		// don't try to load jai lib
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");

		JFrame.setDefaultLookAndFeelDecorated(true);
		if (SubstanceCortex.GlobalScope.getCurrentSkin() == null) {
			SubstanceCortex.GlobalScope.setSkin(new BusinessBlueSteelSkin());
		}
		final DisplaySplash splashScreen = new DisplaySplash(5);
		final Thread t = new Thread(splashScreen);
		t.start();
		try {
			t.join();
		} catch (final InterruptedException e) {
			// ignore
		}

		// configure the default fonts, etc
		Defaults.setProvider(new LiteProvider());

		// for legacy integration we need to provide a tool-parent
		Trace.initialise(app);

		theFrame = new JRibbonFrame(defaultTitle);

		final MapContent mapComponent = new MapContent();
		projection = new GeoToolMapProjection(mapComponent, _theLayers);
		geoMapRenderer = new GeoToolMapRenderer(initialAlpha, mapComponent, projection.getDataTransform().inverse());

		final FileDropSupport dropSupport = new FileDropSupport();
		dropSupport.setFileDropListener(this, " .REP, .XML, .DSF, .DTF, .DPF, .LOG, .TIF, .TXT");

		// provide some file helpers
		ImportReplay.initialise(app);
		ImportManager.addImporter(new ImportReplay());

		// sort out time control
		final Clipboard _theClipboard = new Clipboard("Debrief");
		session = new LiteSession(_theClipboard, _theLayers);
		_stepControl = new LiteStepControl(app, session);
		session.setStepper(_stepControl);
		app.setSession(session);
		app.setFrame(theFrame);

		_stepControl.setUndoBuffer(session.getUndoBuffer());
		_stepControl.setLayers(session.getData());

		// take a safe copy of the chart features layer
		safeChartFeatures = _theLayers.findLayer(Layers.CHART_FEATURES);

		ImportManager.addImporter(new DebriefXMLReaderWriter(app));

		mapPane = (LiteMapPane) geoMapRenderer.getMap();
		createContextMenu(mapPane, geoMapRenderer.getTransform());

		dropSupport.addComponent(mapPane);

		setInitialArea(mapPane, geoMapRenderer.getTransform());

		// ok, ready to load map content
		initializeMapContent();
		final CanvasAdaptor theCanvas = new CanvasAdaptor(projection, mapPane.getGraphics());

		timeManager.addListener(_stepControl, TimeProvider.PERIOD_CHANGED_PROPERTY_NAME);
		timeManager.addListener(_stepControl, TimeProvider.TIME_CHANGED_PROPERTY_NAME);
		timeManager.addListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				timeUpdate(theCanvas, evt);
			}
		}, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

		final DataListener dListener = new DataListener() {
			@Override
			public void dataExtended(final Layers theData) {
				mapPane.repaint();
			}

			@Override
			public void dataModified(final Layers theData, final Layer changedLayer) {
				mapPane.repaint();
			}

			@Override
			public void dataReformatted(final Layers theData, final Layer changedLayer) {
				mapPane.repaint();
			}
		};

		_theLayers.addDataReformattedListener(dListener);
		_theLayers.addDataExtendedListener(dListener);
		_theLayers.addDataModifiedListener(dListener);

		painterManager = new PainterManager(_stepControl);
		final PlainChart theChart = new LiteChart(_theLayers, theCanvas, mapPane, projection);
		theTote = new LiteTote(_theLayers, _stepControl);
		final TotePainter tp = new TotePainter(theChart, _theLayers, theTote, false);
		tp.setColor(Color.white);
		final TotePainter sp = new SnailPainter2(theChart, _theLayers, theTote);

		final ToteSetter.RefreshStepper refresher = new ToteSetter.RefreshStepper() {

			@Override
			public void refresh(final StepperListener listener) {
				if (LiteMapPane.isMapViewportAcceptable(mapPane)) {
					// and the time marker
					final Graphics graphics = mapPane.getGraphics();

					final CanvasAdaptor adapter = new CanvasAdaptor(projection, graphics, Color.blue);

					listener.newTime(null, timeManager.getTime(), adapter);
				}
			}
		};

		_normalSetter = new ToteSetter(painterManager, tp, refresher);
		_snailSetter = new ToteSetter(painterManager, sp, refresher);
		_normalSetter.run();

		final Runnable collapseAction = new Runnable() {
			@Override
			public void run() {
				doExpandCollapse();
			}
		};

		final String path = "ReadMe.pdf";

		final ChangeListener alphaListener = new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				final JSlider source = (JSlider) e.getSource();
				final int alpha = source.getValue();
				mapPane.setTransparency(alpha / 100f);
				mapPane.repaint();
			}
		};

		// create the components
		initForm();
		final MathTransform screenTransform = geoMapRenderer.getTransform();
		createAppPanels(geoMapRenderer, session.getUndoBuffer(), dropSupport, mapPane, _stepControl, timeManager,
				_myOperations, _normalSetter, _snailSetter, statusBar, screenTransform, collapseAction, alphaListener,
				initialAlpha, path);
		_listenForMods = new DataListenerAdaptor() {
			@Override
			public void dataExtended(final Layers theData, final Plottable newItem, final HasEditables parent) {
				update(theData, newItem, parent);
			}
		};

		// tell the Session handler about the optional dynamic layer handlers
		SessionHandler.addAdditionalHandler(new DynamicLayerHandler());
		SessionHandler.addAdditionalHandler(new DynamicShapeLayerHandler());
		SessionHandler.addAdditionalHandler(new SATCHandler_Mock());

		_theLayers.addDataExtendedListener(_listenForMods);
		_theLayers.addDataModifiedListener(_listenForMods);
		_theLayers.addDataReformattedListener(_listenForMods);

		// lastly give us some backdrop data
		loadBackdropdata(_theLayers);
		theFrame.setApplicationIcon(ResizableIconFactory.factory(
				ImageWrapperResizableIcon.getIcon(MenuUtils.createImage("icons/d_lite.png"), MenuUtils.ICON_SIZE_32)));
		theFrame.setIconImage(new ImageIcon(ClassLoader.getSystemResource("icons/d_lite.png")).getImage());
		theFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		theFrame.setVisible(true);
		theFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());

		setState(INACTIVE_STATE);
	}

	private void addGraphView() {
		graphPanelView = new GraphPanelView(_stepControl);
		graphPanel.setCollapsed(true);
		graphPanel.add(graphPanelView, BorderLayout.CENTER);
	}

	private void addNarrativeView() {
		final NarrativeConfigurationModel model = new NarrativeConfigurationModel(timeManager);
		final NarrativePanelToolbar toolbar = new NarrativePanelToolbar(_stepControl, model);
		final NarrativePanelView narrativePanelView = new NarrativePanelView(toolbar);
		narrativePanel.setCollapsed(true);
		narrativePanel.add(narrativePanelView, BorderLayout.CENTER);
	}

	private void addOutlineView(final ToolParent toolParent, final UndoBuffer undoBuffer) {
		layerManager = new OutlinePanelView(undoBuffer, session.getClipboard());
		layerManager.setObject(_theLayers);
		layerManager.setParent(toolParent);
		outlinePanel.add(layerManager, BorderLayout.CENTER);
	}

	public void addStateListener(final PropertyChangeListener newListener) {
		stateListeners.add(newListener);
	}

	private void createAppPanels(final GeoToolMapRenderer geoMapRenderer, final UndoBuffer undoBuffer,
			final FileDropSupport dropSupport, final Component mapPane, final LiteStepControl stepControl,
			final TimeManager timeManager, final PlotOperations operation, final ToteSetter normalT,
			final ToteSetter snailT, final JLabel statusBar, final MathTransform transform,
			final Runnable collapseAction, final ChangeListener alphaListener, final float alpha, final String path) {
		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.setName("Center Panel");

		mapPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				// TODO . This must be changed once we update geotools.
				// Reverted ec7262026be4cbe07c7c521687703ddfa1acfb97
				// I _think_ it is causing the behavior described here.
				// here https://github.com/debrief/debrief/issues/4051#issuecomment-511193288
				mapPane.setVisible(false);
				mapPane.setVisible(true);
				// mapPane.repaint();
				DebriefLiteApp.getInstance().updateProjectionArea();
			}
		});

		centerPanel.add(mapPane, BorderLayout.CENTER);
		centerPanel.add(graphPanel, BorderLayout.PAGE_END);

		theFrame.add(centerPanel, BorderLayout.CENTER);

		theFrame.add(outlinePanel, BorderLayout.WEST);

		theFrame.add(narrativePanel, BorderLayout.EAST);

		addOutlineView(app, undoBuffer);
		addGraphView();
		addNarrativeView();

		theFrame.add(statusBar, BorderLayout.SOUTH);
		final Runnable resetAction = new Runnable() {
			@Override
			public void run() {
				resetPlot();
			}
		};
		final Runnable exitAction = new Runnable() {
			@Override
			public void run() {
				exit();
			}
		};
		new DebriefRibbon(theFrame.getRibbon(), _theLayers, app, geoMapRenderer, stepControl, timeManager, operation,
				session, resetAction, normalT, snailT, new Runnable() {

					@Override
					public void run() {
						mapPane.repaint();
					}
				}, statusBar, exitAction, projection, transform, collapseAction, alphaListener, alpha, path);
	}

	/**
	 * Add the context menu to the LiteMapPane
	 *
	 * @param _myMapPane MapPane to add the menu
	 * @param transform  Transform to use.
	 */
	private void createContextMenu(final LiteMapPane _myMapPane, final MathTransform transform) {
		final WorldLocation clickPosition = new WorldLocation(0, 0, 0);
		final ContextMenu menu = new ContextMenu(clickPosition, _theLayers);
		_myMapPane.addMouseListener(new MapMouseAdapter() {

			@Override
			public void onMouseExited(final MapMouseEvent ev) {
				final DirectPosition2D curPos = ev.getWorldPos();

				if (ev.getWorldPos().getCoordinateReferenceSystem() != DefaultGeographicCRS.WGS84) {
					try {
						transform.transform(curPos, curPos);
					} catch (MismatchedDimensionException | TransformException e) {
						Application.logError2(ToolParent.ERROR, "Failure in projection transform", e);
					}
				}

				clickPosition.setValues(new WorldLocation(curPos.getY(), curPos.getX(), 0));
			}
		});
		_myMapPane.setContextMenu(menu);
	}

	protected void doExpandCollapse() {
		if (collapsedState) {
			for (final JXCollapsiblePaneWithTitle panel : openPanels) {
				panel.setCollapsed(false);
			}
		} else {
			// clear the list
			openPanels.clear();

			// get list of panels
			final List<JXCollapsiblePaneWithTitle> items = new ArrayList<JXCollapsiblePaneWithTitle>();
			items.add(outlinePanel);
			items.add(graphPanel);
			items.add(narrativePanel);

			for (final JXCollapsiblePaneWithTitle panel : items) {
				// is it currently open?
				if (!panel.isCollapsed()) {
					// remember it was open
					openPanels.add(panel);

					// and collapse it
					panel.setCollapsed(true);
				}
			}
		}
		collapsedState = !collapsedState;
	}

	protected void doPaint(final Graphics gc) {
		if (LiteMapPane.isMapViewportAcceptable(mapPane)) {
			final CanvasAdaptor dest;
			if (gc instanceof Graphics2D) {
				dest = new ExtendedCanvasAdapter(projection, gc, Color.red);
			} else {
				final String s = "Lite rendering is expecting a Graphics2D object";
				app.logError(ToolParent.ERROR, s, null);
				throw new IllegalArgumentException(s);
			}

			// ok, are we in snail mode?
			final String current = painterManager.getCurrentPainterObject().toString();
			if (current.equals(TotePainter.NORMAL_NAME)) {
				// ok, we need to draw in the layers
				dest.setLineWidth(2f);
				dest.startDraw(gc);
				_theLayers.paint(dest);
			}

			// and the time marker
			redoTimePainter(true, dest, _pendingOldTime, _pendingNewTime);

			_pendingNewTime = null;
			_pendingOldTime = null;

			dest.endDraw(gc);
		}
	}

	private void enableFileCloseButton(final boolean b) {
		DebriefRibbonFile.closeButton.getContentModel().setActionEnabled(b);

	}

	public void exit() {
		if (DebriefLiteApp.isDirty()) {
			final int res = JOptionPane.showConfirmDialog(theFrame, "Save before exiting Debrief Lite?", "Warning",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (res == JOptionPane.OK_OPTION) {
				final String currentFileName = DebriefLiteApp.currentFileName;
				String outputFileName = null;
				if (currentFileName != null) {
					final File currentFile = new File(currentFileName);
					final File directory = currentFile.getParentFile();
					if (currentFileName.endsWith(".dpf")) {
						DebriefRibbonFile.saveChanges(currentFileName, session, theFrame);
						exitApp();
					} else {
						final String initialName = currentFile.getName().substring(0,
								currentFile.getName().lastIndexOf("."));
						outputFileName = DoSaveAs.showSaveDialog(directory, initialName);
					}
				} else {
					final File directory;
					final String lastFileLocation = DebriefLiteApp.getDefault()
							.getProperty(DoSaveAs.LAST_FILE_LOCATION);
					if (lastFileLocation != null) {
						directory = new File(lastFileLocation);
					} else {
						directory = null;
					}
					outputFileName = DoSaveAs.showSaveDialog(directory, "DebriefPlot");
				}
				if (outputFileName != null) {
					DebriefRibbonFile.saveChanges(outputFileName, session, theFrame);
					exitApp();
				}

			} else if (res == JOptionPane.NO_OPTION) {
				exitApp();
			}
		} else {
			exitApp();
		}

	}

	private void exitApp() {
		session.close();
		theFrame.dispose();
		if (!("TRUE".equalsIgnoreCase(System.getProperty("TEST_MODE"))))
			System.exit(0);
	}

	@Override
	public void FilesReceived(final Vector<File> files) {
		setCursor(Cursor.WAIT_CURSOR);
		File file = null;
		try {
			final Enumeration<File> iter = files.elements();

			while (iter.hasMoreElements()) {
				file = iter.nextElement();

				final String suff = suffixOf(file.getName());
				if (suff.equalsIgnoreCase(".DPL")) {
					DialogFactory.showMessage("Open File", "Sorry DPL file format no longer supported");
				} else {
					if (suff.equalsIgnoreCase(".DSF")) {
						openDsfFile(file);
					} else if ((suff.equalsIgnoreCase(".REP")) || (suff.equalsIgnoreCase(".DTF"))) {
						// fake wrap it
						final File[] fList = new File[] { file };
						handleImportRep(fList);
					} else if (suff.equalsIgnoreCase(".XML") || suff.equalsIgnoreCase(".DPF")) {
						handleImportDPF(file);
					} else if (suff.equalsIgnoreCase(".LOG")) {
						handleImportNMEAFile(file);
						// layerManager.resetTree();
					} else if (suff.equalsIgnoreCase(".TIF")) {
						handleImportTIFFile(file);
					} else if (suff.equalsIgnoreCase(".TXT")) {
						// Maybe it is an antares file
						handleImportAntaresFile(file);
					} else {
						// try nisida format - we can't rely on filename
						final FileInputStream fis = new FileInputStream(file);
						if (ImportNisida.canLoadThisFile(fis)) {
							fis.close();
							handleImportNisidaFile(file);
						} else {
							Trace.trace("This file type not handled:" + suff);
							DialogFactory.showMessage("Open Debrief file", "This file type is not handled:" + suff);
						}
					}
				}
			}
		} catch (final Exception e) {
			Trace.trace(e);
			MWC.GUI.Dialogs.DialogFactory.showMessage("Open Debrief file", "Error Opening the file: " + e.getMessage());
		}

		restoreCursor();
	}

	public static void handleImportAntaresFile(File file) {
		final ImportAntares antaresImporter = new ImportAntares();
		if (antaresImporter.canImportThisFile(file.getAbsolutePath())) {
			AntaresLoaderDebriefLite.handleImportAntares(file, antaresImporter, _instance._theLayers,
					getInstance().getApplicationFrame(), getInstance().getGeneralPersistence(), new Runnable() {

						@Override
						public void run() {
							getInstance().finishTimeRelatedImport(getInstance());
						}
					});
		}
		
	}

	public JRibbonFrame getApplicationFrame() {
		return theFrame;

	}

	public HashMap<String, String> getGeneralPersistence() {
		return generalPersistence;
	}

	public OutlinePanelView getLayerManager() {
		return layerManager;
	}

	public GeoToolMapProjection getProjection() {
		return projection;
	}

	public WorldArea getProjectionArea() {
		final DirectPosition2D upperCorner = (DirectPosition2D) mapPane.getDisplayArea().getUpperCorner();
		final DirectPosition2D bottomCorner = (DirectPosition2D) mapPane.getDisplayArea().getLowerCorner();

		if (bottomCorner.getCoordinateReferenceSystem() != DefaultGeographicCRS.WGS84
				&& upperCorner.getCoordinateReferenceSystem() != DefaultGeographicCRS.WGS84) {
			try {
				geoMapRenderer.getTransform().transform(upperCorner, upperCorner);
				geoMapRenderer.getTransform().transform(bottomCorner, bottomCorner);
			} catch (MismatchedDimensionException | TransformException e) {
				Application.logError2(ToolParent.ERROR, "Failure in projection transform", e);
			}
		}

		final WorldLocation topLocation = new WorldLocation(upperCorner.getY(), upperCorner.getX(), 0);
		final WorldLocation bottomLocation = new WorldLocation(bottomCorner.getY(), bottomCorner.getX(), 0);
		final WorldArea newArea = new WorldArea(topLocation, bottomLocation);
		newArea.normalise();

		return newArea;
	}

	public TimeManager getTimeManager() {
		return _instance.timeManager;
	}

	private void handleImportDPF(final File file) {
		boolean success = true;

		final DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(app);
		try {
			final FileInputStream is = new FileInputStream(file);
			try {
				reader.importThis(file.getName(), is, session);
			} finally {
				try {
					is.close();
				} catch (final IOException e) {
					getDefault().logError(ToolParent.ERROR, "Failed to close DPF file", e);
				}
			}
			// update the time panel
			final TimePeriod period = _theLayers.getTimePeriod();
			_myOperations.setPeriod(period);
			timeManager.setPeriod(this, period);
			if (period != null) {
				timeManager.setTime(this, period.getStartDTG(), true);
			}
			if (_stepControl.getDateFormat() != null) {
				DebriefRibbonTimeController.assignThisTimeFormat(_stepControl.getDateFormat(), true, true);
			}
			_theLayers.fireModified(null);

			// also tell the layers they've been reformatted
			_theLayers.fireReformatted(null);

			// and the spatial bounds
			new FitToWindow(_theLayers, mapPane, projection, null).actionPerformed(null);
		} catch (final FileNotFoundException e) {
			app.logError(ToolParent.ERROR, "Failed to read DPF File", e);
			MWC.GUI.Dialogs.DialogFactory.showMessage("Open Debrief file", "Failed to read DPF File" + e.getMessage());
			success = false;
		} catch (final PlainImporter.ImportException ie) {
			DialogFactory.showMessage("Error in opening file", ie.getMessage());
			success = false;
		}

		if (success) {
			resetFileName(file);
		}
	}

	public void handleImportNisidaFile(final File file) throws FileNotFoundException {
		final FileInputStream fis = new FileInputStream(file); 
		try {
			ImportNisida.importThis(fis, _theLayers);
			final TimePeriod period = _theLayers.getTimePeriod();
			_myOperations.setPeriod(period);
			timeManager.setPeriod(getInstance(), period);
			if (period != null) {
				timeManager.setTime(getInstance(), period.getStartDTG(), true);
			}

			_theLayers.fireModified(null);

			// also tell the layers they've been reformatted
			_theLayers.fireReformatted(null);
			enableFileCloseButton(true);
		}finally {
			if(fis!=null) {
				try {
					fis.close();
				} catch (final IOException e) {
					getDefault().logError(ToolParent.ERROR, "Failed to close NMEA file", e);
				}
			}
		}

		
	}

	private void handleImportNMEAFile(final File file) throws FileNotFoundException {
		// show the dialog first, then import the file

		final ImportNMEA importer = new ImportNMEA(_theLayers);

		final FileInputStream fis = new FileInputStream(file);
		try {
			importer.importThis(file.getName(), fis, 60000, 60000, false);
		} finally {
			try {
				fis.close();
			} catch (final IOException e) {
				getDefault().logError(ToolParent.ERROR, "Failed to close NMEA file", e);
			}
		}

		final TimePeriod period = _theLayers.getTimePeriod();
		_myOperations.setPeriod(period);
		timeManager.setPeriod(this, period);
		if (period != null) {
			timeManager.setTime(this, period.getStartDTG(), true);
		}

		_theLayers.fireModified(null);

		// also tell the layers they've been reformatted
		_theLayers.fireReformatted(null);
		enableFileCloseButton(true);
	}

	private void finishTimeRelatedImport(final Object source) {
		setCursor(Cursor.WAIT_CURSOR);
		layerManager.createAndInitializeTree();
		mapPane.repaint();
		restoreCursor();
		// update the time panel
		final TimePeriod period = _theLayers.getTimePeriod();
		if (period != null) {
			_myOperations.setPeriod(period);
			timeManager.setPeriod(source, period);
			timeManager.setTime(source, period.getStartDTG(), true);
		}
		timeManager.firePeriodPropertyChange();

		theTote.assignWatchables(true);

		// and the spatial bounds
		final FitToWindow fitMe = new FitToWindow(_theLayers, mapPane, projection, null);
		fitMe.actionPerformed(null);
	}

	private void handleImportRep(final File[] fList) {

		final DebriefLiteApp source = this;
		boolean success = true;
		if (fList.length == 1) {
			if (fList[0].getName().endsWith("dsf")) {
				try {
					openDsfFile(fList[0]);
				} catch (final FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				final BaseImportCaller caller = new BaseImportCaller(fList, _theLayers) {
					// handle completion of the full import process
					@Override
					public void allFilesFinished(final File[] fNames, final Layers newData) {
						finishImport(source);
					}

					// handle the completion of each file
					@Override
					public void fileFinished(final File fName, final Layers newData) {

					}

					private void finishImport(final DebriefLiteApp source) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								finishTimeRelatedImport(source);
							}
						});
					}
				};
				try {
					// ok, start loading
					caller.start();
				} catch (final PlainImporter.ImportException ie) {
					success = false;
				}

				if (success) {
					resetFileName(fList[0]);
				}
			}
		}
	}

	/**
	 * fill in the UI details
	 */
	private void initForm() {
		theFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final java.awt.event.WindowEvent e) {
				exit();
			}

		});
		// try to give the application an icon
		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		/*
		 * theFrame.getRibbon().setApplicationMenu(new RibbonAppMenuProvider()
		 * .createApplicationMenu(theFrame));
		 */
		// It cannot be smaller than this size to have the ribbon complete!
		final int sizeWidth = Math.max((int) (dim.width * 0.8), 870);
		final int sizeHeight = (int) (dim.height * 0.8);
		theFrame.setSize(sizeWidth, sizeHeight);
		final Dimension sz = theFrame.getSize();
		theFrame.setLocation((dim.width - sz.width) / 2, (dim.height - sz.height) / 2);

		// do any final re-arranging
		theFrame.doLayout();
	}

	private void initializeMapContent() {
		geoMapRenderer.addRenderer(new MapRenderer() {
			@Override
			public void paint(final Graphics gc) {
				doPaint(gc);
			}
		});
	}

	private void paintDynamicLayers(final CanvasType dest) {
		final HiResDate tNow = timeManager.getTime();
		// do we have time?
		if (tNow != null) {
			final long timeVal = tNow.getDate().getTime();
			final Enumeration<Editable> lIter = _theLayers.elements();
			while (lIter.hasMoreElements()) {
				final Editable next = lIter.nextElement();
				if (next instanceof DynamicPlottable) {
					final DynamicPlottable dp = (DynamicPlottable) next;
					dp.paint(dest, timeVal);
				}
			}
		}
	}

	private void redoTimePainter(final boolean bigPaint, final CanvasAdaptor dest, final HiResDate oldDTG,
			final HiResDate newDTG) {
		if (LiteMapPane.isMapViewportAcceptable(mapPane)) {
			final StepperListener current = painterManager.getCurrentPainterObject();
			final boolean isNormal = current.toString().equals(TotePainter.NORMAL_NAME);

			final Color backColor = Color.white;

			// and the time marker
			final Graphics graphics = mapPane.getGraphics();

			if (graphics instanceof Graphics2D) {
				final Graphics2D g2 = (Graphics2D) graphics;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}

			if (bigPaint) {
				final CanvasType.PaintListener thisPainter = (CanvasType.PaintListener) painterManager
						.getCurrentPainterObject();

				// it must be ok
				final CanvasAdaptor adapter = new CanvasAdaptor(projection, dest.getGraphicsTemp(), backColor);
				thisPainter.paintMe(adapter);

				// also render dynamic layers
				paintDynamicLayers(adapter);
			} else {
				if (!isNormal) {
					@SuppressWarnings("unused")
					final SnailPainter2 snail = (SnailPainter2) current;
					// note: we were over-writing the vector stretch
					// value, but I can't see why. Comment it out.
					// snail.setVectorStretch(1d);
				}

				final CanvasAdaptor adapter = new CanvasAdaptor(projection, graphics, backColor);
				painterManager.newTime(oldDTG, newDTG, adapter);

				// also render dynamic layers
				paintDynamicLayers(adapter);
			}
		}
	}

	private void resetClipboard() {
		final Clipboard theClipboard = session.getClipboard();
		theClipboard.setContents(new Transferable() {
			@Override
			public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException {
				throw new UnsupportedFlavorException(flavor);
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[0];
			}

			@Override
			public boolean isDataFlavorSupported(final DataFlavor flavor) {
				return false;
			}
		}, layerManager);
	}

	private void resetFileName(final File file) {
		if (DebriefLiteApp.currentFileName == null) {
			DebriefLiteApp.currentFileName = file.getAbsolutePath();
			DebriefLiteApp.setTitle(file.getName());
			// setState(ACTIVE_STATE);
			DebriefLiteApp.getInstance().enableFileCloseButton(true);
		}
	}

	public void resetPlot() {
		// remove the data from the GeoMap
		final MapContent content = mapPane.getMapContent();
		final List<org.geotools.map.Layer> layers = content.layers();
		for (final org.geotools.map.Layer layer : layers) {
			content.removeLayer(layer);
		}

		// clear the data
		_theLayers.clear();
		layerManager.resetTree();

		// special behaviour. The chart creator objects take a point to the
		// target layer on creation. So, we need to keep the same chart features layer
		// for the running session.
		if (safeChartFeatures != null) {
			final BaseLayer bl = (BaseLayer) safeChartFeatures;
			bl.removeAllElements();
		}
		_theLayers.addThisLayer(safeChartFeatures);

		// continue with reset processing
		_plotDirty = false;
		setState(INACTIVE_STATE);
		DebriefRibbonFile.closeButton.getContentModel().setActionEnabled(false);
		currentFileName = null;
		setTitle(defaultTitle);

		setInitialArea(mapPane, geoMapRenderer.getTransform());

		// also clear the tote
		theTote.clear();

		timeManager.setPeriod(this, null);
		timeManager.setTime(this, null, false);

		// and the time format dropdown
		DebriefRibbonView.resetToggleMenuStates();
		DebriefRibbonTimeController.resetToggleMenuStates();
		theFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());
		// stop the timer
		if (_stepControl.isPlaying()) {
			_stepControl.startStepping(false);
		}

		// send a reset to the step control
		_stepControl.reset();

		// reset the map
		final ResetAction resetMap = new ResetAction(mapPane);
		resetMap.actionPerformed(null);
		DebriefRibbonInsert.resetLayers(_theLayers);
		// put some backdrop data back in
		loadBackdropdata(_theLayers);
		resetUndoBuffer();

		graphPanelView.reset();
		graphPanel.setCollapsed(true);
	}

	private void resetUndoBuffer() {
		session.getUndoBuffer().resetBuffer();
		resetClipboard();

	}

	public final void restoreCursor() {
		theFrame.getContentPane().setCursor(null);
	}

	public final void setCursor(final int theCursor) {
		theFrame.getContentPane().setCursor(new Cursor(theCursor));
	}

	public void setStatus(final String message) {
		statusBar.setText(message);
	}

	protected void timeUpdate(final CanvasAdaptor theCanvas, final PropertyChangeEvent evt) {
		// are we already plotting?
		if (!_plotUpdating) {
			// no - capture the fact that we are
			_plotUpdating = true;

			// trigger screen update to happen on UI thread
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						// ok, redraw the whole map
						mapPane.repaint();
						_pendingNewTime = (HiResDate) evt.getNewValue();
						_pendingOldTime = (HiResDate) evt.getOldValue();
						redoTimePainter(false, theCanvas, (HiResDate) evt.getOldValue(), (HiResDate) evt.getNewValue());
					} finally {
						_plotUpdating = false;
					}
				}
			});
		}
	}

	protected void update(final Layers theData, final Plottable newItem, final HasEditables theLayer) {
		getLayerManager().updateData((Layer) theLayer, newItem);
	}

	public void updateProjectionArea() {
		final DirectPosition2D upperCorner = (DirectPosition2D) mapPane.getDisplayArea().getUpperCorner();
		final DirectPosition2D bottomCorner = (DirectPosition2D) mapPane.getDisplayArea().getLowerCorner();

		if (bottomCorner.getCoordinateReferenceSystem() != DefaultGeographicCRS.WGS84
				&& upperCorner.getCoordinateReferenceSystem() != DefaultGeographicCRS.WGS84) {
			try {
				geoMapRenderer.getTransform().transform(upperCorner, upperCorner);
				geoMapRenderer.getTransform().transform(bottomCorner, bottomCorner);
			} catch (MismatchedDimensionException | TransformException e) {
				Application.logError2(ToolParent.ERROR, "Failure in projection transform", e);
			}
		}

		final WorldLocation topLocation = new WorldLocation(upperCorner.getY(), upperCorner.getX(), 0);
		final WorldLocation bottomLocation = new WorldLocation(bottomCorner.getY(), bottomCorner.getX(), 0);
		final WorldArea newArea = new WorldArea(topLocation, bottomLocation);
		newArea.normalise();

		projection.setDataArea(newArea);
	}


}
